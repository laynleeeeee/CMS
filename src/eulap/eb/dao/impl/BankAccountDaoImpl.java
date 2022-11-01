package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.BankAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.BankAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.BankReconItem;

/**
 * Implementation class of {@link BankAccountDao}

 */
public class BankAccountDaoImpl extends BaseDao<BankAccount> implements BankAccountDao {

	@Override
	protected Class<BankAccount> getDomainClass() {
		return BankAccount.class;
	}

	@Override
	public boolean isUniqueBankAccount(BankAccount bankAccount, boolean isNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		String value = bankAccount.getName();
		if (isNumber) {
			value = bankAccount.getBankAccountNo();
		}
		dc.add(Restrictions.like(BankAccount.FIELD.name.name(),
				StringFormatUtil.removeExtraWhiteSpaces(value)));
		dc.add(Restrictions.eq(BankAccount.FIELD.bankId.name(), bankAccount.getBankId()));
		dc.add(Restrictions.eq(BankAccount.FIELD.companyId.name(), bankAccount.getCompanyId()));
		if (bankAccount.getId() > 0) {
			dc.add(Restrictions.ne(BankAccount.FIELD.id.name(), bankAccount.getId()));
		}
		return getAll(dc).size() < 1;
	}

	@Override
	public List<BankAccount> getBankAccounts(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.addOrder(Order.asc(BankAccount.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<BankAccount> getAllBankAccounts(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(BankAccount.FIELD.serviceLeaseKeyId.name(),serviceLeaseKeyId));
		dc.addOrder(Order.asc(BankAccount.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public BankAccount getBankAccountById(int bankAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(BankAccount.FIELD.id.name(), bankAccountId));
		return get(dc);
	}

	@Override
	public Page<BankAccount> searchBankAccount(String searchCriteria,
			int companyId, int status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (searchCriteria != null && !searchCriteria.trim().isEmpty())
			dc.add(Restrictions.like(BankAccount.FIELD.name.name(), "%" + searchCriteria.trim() + "%"));
		if (companyId != -1)
			dc.add(Restrictions.eq(BankAccount.FIELD.companyId.name(), companyId));
		if (status != -1)
			dc.add(Restrictions.eq(BankAccount.FIELD.active.name(), status == 1));
		dc.addOrder(Order.asc(BankAccount.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<BankAccount> getBankAccounts(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(BankAccount.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(BankAccount.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public List<BankReconItem> getBankReconItems(int bankAcctId, Date asOfDate, Integer divisionId) {
		List<BankReconItem> brData = new ArrayList<BankReconItem>();
		List<Object> data = executeSP("GET_BANK_RECON", bankAcctId, asOfDate, divisionId);
		//FORM_ID, DATE, CHECK_DATE, RECEIPT_NO, CHECK_NO, CUSTOMER, SUPPLIER, AMOUNT, STATUS, FIELD
		if(!data.isEmpty()) {
			for (Object object : data) {
				Object[] rowResult = (Object[]) object;
				brData.add(BankReconItem.getInstanceOf(
						(Integer) rowResult[0],
						(Date) rowResult[1],
						(Date) rowResult[2],
						(String) rowResult[3],
						(String) rowResult[4],
						(String) rowResult[5],
						(String) rowResult[6],
						(Double) rowResult[7],
						(String) rowResult[8],
						Integer.valueOf((String) rowResult[9])));
			}
		}
		return brData;
	}

	@Override
	public List<BankAccount> getAllBankAccountsByName(String name, Integer limit, User user, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if(name != null && !name.isEmpty()) {
			dc.add(Restrictions.like(BankAccount.FIELD.name.name(), "%"+name.trim()+"%"));
		}
		if(limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		if(companyId != null) {
			dc.add(Restrictions.eq(BankAccount.FIELD.companyId.name(), companyId));
		}
		if(divisionId != null) {
			//Account combination
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			dc.add(Subqueries.propertyIn(BankAccount.FIELD.cashInBankAcctId.name(), acDc));
		}
		dc.add(Restrictions.eq(BankAccount.FIELD.active.name(), true));
		dc.addOrder(Order.asc(BankAccount.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public BankAccount getBankAccountByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		if(name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.eq(BankAccount.FIELD.name.name(), name.trim()));
		}
		return get(dc);
	}

	@Override
	public List<BankAccount> getBankAccountsByUser(User user, boolean isActiveOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, user);
		if (isActiveOnly) {
			dc.add(Restrictions.eq(BankAccount.FIELD.active.name(), true));
		}
		return getAll(dc);
	}

	@Override
	public BankAccount getBankAccountByName(String name, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(BankAccount.FIELD.name.name(), name.trim()));
		// Account combination
		DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
		acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		if (companyId != null) {
			acDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null) {
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Subqueries.propertyIn(BankAccount.FIELD.cashInBankAcctId.name(), acDc));
		return get(dc);
	}
}