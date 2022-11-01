package eulap.eb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CustodianAccountSupplierDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.CustodianAccountSupplier;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will handle business logic for {@link CustodianAccountSupplier}

 */

@Service
public class CustodianAccountSupplierService  {
	@Autowired
	private SupplierService supplierSerivce;
	@Autowired
	private SupplierAccountService saService;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private CustodianAccountSupplierDao casDao;
	@Autowired
	private AccountCombinationService acService;

	/**
	 * Save the {@link CustodianAccountSupplier}
	 * @param user The logged user.
	 * @param CustodianAccountSupplier The domain to be saved.
	 */
	public void saveCustodianAccountSupplier(User user, CustodianAccount custodianAccount) {
		Integer custodianAccountId = custodianAccount.getId();
		int userId = user.getId();
		Date currentDate = new Date();
		CustodianAccountSupplier cas = null;
		if (!hasExistingCAS(custodianAccountId)) {
			cas = new CustodianAccountSupplier();
			Supplier supplier = getExistingSupplier(user, custodianAccount);
			SupplierAccount supplierAccount = getExistingSupplierAccount(custodianAccount, supplier, user);
			cas.setCustodianAccountId(custodianAccountId);
			cas.setSupplierId(supplier.getId());
			cas.setSupplierAccountId(supplierAccount.getId());
			AuditUtil.addAudit(cas, new Audit(userId, true, currentDate));
			casDao.save(cas);
		} else {
			cas = getCAS(custodianAccountId, null, null);
			SupplierAccount supplierAccount = saService.getSupplierAccount(cas.getSupplierAccountId());
			supplierAccount.setName(custodianAccount.getCustodianAccountName());
			supplierAccountDao.update(supplierAccount);
		}
	}

	private Supplier getExistingSupplier(User user, CustodianAccount custodianAccount) {
		String name = custodianAccount.getCustodianName();
		Supplier supplier = supplierSerivce.getSupplier(name);
		if (supplier == null) {
			return createSupplier(custodianAccount, user);
		}
		return supplier;
	}

	private Supplier createSupplier(CustodianAccount custodianAccount, User user) {
		String supplierName = StringFormatUtil.removeExtraWhiteSpaces(custodianAccount.getCustodianName());
		Supplier supplier = new Supplier();
		supplier.setBussinessClassificationId(BusinessClassification.CORPORATE_TYPE);
		supplier.setName(supplierName);
		supplier.setActive(custodianAccount.isActive());
		supplier.setStreetBrgy("N/A");
		supplier.setCityProvince("N/A");
		supplier.setTin("N/A");
		supplier.setBusRegTypeId(Supplier.BUS_REG_NON_VAT);
		supplierSerivce.saveSupplier(user, supplier);
		createSupplierAccount(custodianAccount, supplier, user);
		return supplier;
	}

	private SupplierAccount getExistingSupplierAccount(CustodianAccount custodianAccount, Supplier supplier, User user) {
		String name = custodianAccount.getCustodianAccountName();
		SupplierAccount supplierAccount = saService.getSupplierAcctByNameAndCompany(custodianAccount.getCompanyId(), name);
		if(supplierAccount == null) {
			return createSupplierAccount(custodianAccount, supplier, user);
		}
		return supplierAccount;
	}

	private SupplierAccount createSupplierAccount(CustodianAccount custodianAccount, Supplier supplier, User user) {
		String name = custodianAccount.getCustodianAccountName();
		SupplierAccount supplierAccount = new SupplierAccount();
		AccountCombination ac = acService.getAccountCombination(custodianAccount.getCdAccountCombinationId());
		supplierAccount.setActive(supplier.isActive());
		supplierAccount.setSupplierId(supplier.getId());
		supplierAccount.setCompanyId(custodianAccount.getCompanyId());
		supplierAccount.setName(name);
		supplierAccount.setTermId(custodianAccount.getTermId());
		supplierAccount.setCreditAccountId(ac.getAccountId());
		supplierAccount.setCreditDivisionId(ac.getDivisionId());
		saService.saveSupplierAccount(supplierAccount, user);
		return supplierAccount;
	}

	private boolean hasExistingCAS(Integer custodianAccountId) {
		return casDao.hasExistingCustodianAccountSupplier(custodianAccountId);
	}

	/**
	 * Get the {@link CustodianAccountSupplier}
	 * @param The custodian account The custodian account id
	 * @param The supplier id
	 * @param The supplier account id
	 * @return The {@link CustodianAccountSupplier}
	 */
	public CustodianAccountSupplier getCAS(Integer custodianAccountId, Integer supplierId, Integer supplierAccountId) {
		return casDao.getCustodianAccountSupplier(custodianAccountId, supplierId, supplierAccountId);
	}
}
