package eulap.eb.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.BusRegTypeDao;
import eulap.eb.dao.BusinessClassificationDao;
import eulap.eb.dao.CustodianAccountDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.BusinessRegistrationType;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.CustodianAccountSupplier;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;

/**
 * A class that handles the business logic of Suppliers

 *
 */
@Service
public class SupplierService {
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private BusRegTypeDao busRegTypeDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private BusinessClassificationDao businessClassificationDao;
	@Autowired
	private CustodianAccountSupplierService casService;
	@Autowired
	private CustodianAccountDao custodianAccountDao;

	/**
	 * Get supplier
	 * @param supplierId the unique id associated to the supplier
	 * @return The Supplier object.
	 */
	public Supplier getSupplier (int supplierId) {
		return supplierDao.get(supplierId);
	}

	/**
	 * Save the supplier
	 * @param user The current logged user.
	 * @param supplier The supplier to be saved. 
	 */
	public void saveSupplier (User user, Supplier supplier) {
		boolean isNewRecord = supplier.getId() == 0 ? true : false;
		AuditUtil.addAudit(supplier, new Audit(user.getId(), isNewRecord, new Date()));
		supplier.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		supplierDao.saveOrUpdate(supplier);

		if (!isNewRecord) {
			CustodianAccountSupplier cas = casService.getCAS(null, supplier.getId(), null);
			if (cas != null) {
				CustodianAccount cs = custodianAccountDao.get(cas.getCustodianAccountId());
				cs.setCustodianName(StringFormatUtil.removeExtraWhiteSpaces(supplier.getName()));
				custodianAccountDao.update(cs);
			}
		}
	}

	/**
	 * Process name of INDIVIDUAL_TYPE suplier
	 * @param supplier The supplier
	 */
	public void processName(Supplier supplier) {
		supplier.setName(supplier.getLastName() + ", " +supplier.getFirstName()+ " " 
				+ (!supplier.getMiddleName().isEmpty() ? supplier.getMiddleName() : ""));
	}

	/**
	 * Evaluates if the supplier's name is unique. 
	 * @param supplier The supplier that will be evaluated.
	 * @return true if the supplier has a unique name otherwise false.
	 */
	public boolean isUnique (Supplier supplier) {
		return supplierDao.isUniqueSupplier(supplier);
	}

	/**
	 * Get the suppliers of the logged user.
	 * @param user The logged in user
	 * @return The list of suppliers
	 */
	public List<Supplier> getSuppliers (User user) {
		return supplierDao.getSuppliers(user.getServiceLeaseKeyId());
	}

	/**
	 * Get the list of suppliers of the logged user and add the supplier of the supplier account if inactive.
	 * @param user The logged user.
	 * @param supplierAcctId The id of the supplier account.
	 * @return The list of suppliers.
	 */
	public List<Supplier> getSuppliers (User user, Integer supplierAcctId) {
		if(supplierAcctId != null) {
			// Get the Supplier Account.
			SupplierAccount supplierAccount = supplierAccountDao.get(supplierAcctId);
			if(supplierAccount != null) {
				// Check if the supplier is inactive
				if(!supplierAccount.getSupplier().isActive()) {
					// Add the supplier to the list if inactive
					List<Supplier> suppliers = getSuppliers(user);
					suppliers.add(supplierAccount.getSupplier());
					return suppliers;
				}
			}
		}
		return getSuppliers(user);
	}

	/**
	 * Get the suppliers of the logged user.
	 * @param user The logged in user
	 * @param invoice The invoice of the supplier
	 * @return The list of suppliers
	 */
	public List<Supplier> getSuppliers (User user, APInvoice invoice) {
		List<Supplier> suppliers = supplierDao.getSuppliers(user.getServiceLeaseKeyId());
		if (invoice != null && invoice.getSupplierId() != null) {
			boolean hasSelectedSupplier = false;
			for (Supplier supplier: suppliers) 
				if (supplier.getId() == invoice.getSupplierId()) {
					hasSelectedSupplier = true;
					break;
				}
			if (!hasSelectedSupplier)
				suppliers.add(supplierDao.get(invoice.getSupplierId()));
		}
		return suppliers;
	}

	/**
	 * Get the list of suppliers by name.
	 * @param name The name of supplier.
	 * @return The list of suppliers.
	 */
	public List<Supplier> getSuppliers (User user, String name) {
		return supplierDao.getSuppliers(user, user.getServiceLeaseKeyId(), name);
	}

	/**
	 * Get all the suppliers under the service lease key.
	 * @param serviceLeaseKeyId The service lease key Id.
	 * @return The list of suppliers (both active and inactive).
	 */
	public List<Supplier> getAllSuppliers (int serviceLeaseKeyId) {
		return supplierDao.getAllSuppliers(serviceLeaseKeyId);
	}

	/**
	 * Retrieve all suppliers (active and inactive) under the company selected.
	 * @param companyId The Id of the company selected.
	 * @param serviceLeaseKeyId The service lease Id of the logged user.
	 * @return List of suppliers under the company selected.
	 */
	public List<Supplier> getAllSuppliers(int companyId, int serviceLeaseKeyId) {
		return supplierDao.getSuppliersByCompany(companyId, serviceLeaseKeyId);
	}

	/**
	 * Get the list of {@link Supplier} based on the parameter.
	 * @param bussinessClassificationId The {@link BusinessClassification} id.
	 * @param name The supplier name.
	 * @param streetBrgy The supplier street barangay.
	 * @param cityProvince The supplier city province.
	 * @param status The supplier status.
	 * @param busRegTypeId The {@link BusinessRegistrationType} id.
	 * @param serviceLeaseKeyId The service lease key id.
	 * @param pageSetting The {@link PageSetting}.
	 * @return The list of {@link Supplier} in page format.
	 */
	public Page<Supplier> searchSuppliers(Integer bussinessClassificationId, String name, String streetBrgy, String cityProvince, String status, int busRegTypeId,
			int serviceLeaseKeyId, int pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<Supplier> suppliers = supplierDao.searchSuppliers(bussinessClassificationId, name, streetBrgy, cityProvince, searchStatus, busRegTypeId,
				serviceLeaseKeyId, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		processSupplierTin((List<Supplier>) suppliers.getData());
		return suppliers;
	}

	private void processSupplierTin(List<Supplier> suppliers) {
		for(Supplier sup : suppliers) {
			if(sup.getTin()!=null&&!sup.getTin().isEmpty()) {
				if (sup.getTin().length()==13) {
					sup.setTin(StringFormatUtil.processTin(StringFormatUtil.parseBIRTIN(sup.getTin()))
							+"-"+StringFormatUtil.parseBranchCode(sup.getTin()));
				} else {
					sup.setTin(StringFormatUtil.processTin(sup.getTin()));
				}
			}
		}
	}

	/**
	 * Load all suppliers under the same service lease key.
	 * @param serviceLeaseKeyId The service lease Id of the logged user.
	 * @param pageNumber The page number.
	 * @return All suppliers.
	 */
	public Page<Supplier> loadSuppliers(int serviceLeaseKeyId, int pageNumber) {
		Page<Supplier> suppliers = supplierDao.loadSuppliers(serviceLeaseKeyId, new PageSetting(pageNumber, 25));
		processSupplierTin((List<Supplier>) suppliers.getData());
		return suppliers;
	}

	/**
	 * Validate the TIN if unique.
	 * @param supplier The supplier object to be validated.
	 * @param user The logged user.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueTin(Supplier supplier, User user) {
		if(supplier.getId() == 0)
			return supplierDao.isUniqueTin(supplier.getTin(), user.getServiceLeaseKeyId());
		Supplier existingSupplier = supplierDao.get(supplier.getId());
		if(supplier.getTin().trim().equalsIgnoreCase(existingSupplier.getTin().trim())) {
			if(supplier.getId() == existingSupplier.getId())
				return true;
			return false;
		}
		return supplierDao.isUniqueTin(supplier.getTin(), user.getServiceLeaseKeyId());
	}

	/**
	 * Get all business registration type.
	 * @return List of business registration type.
	 */
	public List<BusinessRegistrationType> getAllBusRegType(){
		return busRegTypeDao.getBusRegType();
	}

	/**
	 * Get the supplier by name.
	 * @param name The name of supplier.
	 * @return The supplier
	 */
	public Supplier getSupplier (String name) {
		return supplierDao.getSupplier( name);
	}

	/**
	 * Get the list of supplier by bus reg type.
	 * @param name The supplier name.
	 * @param busRegTypeId The bus reg type id.
	 * @return The list of supplier.
	 */
	public List<Supplier> getSuppliersByBusRegType (User user, String name, Integer busRegTypeId){
		return supplierDao.getSuppliersByBusReg(user, name.trim(), busRegTypeId);
	}

	/**
	 * Get the supplier by bus reg type.
	 * @param name The supplier name.
	 * @param busRegTypeId The bus reg type id.
	 * @return The supplier.
	 */
	public Supplier getSupplierByBusRegType (String name, Integer busRegTypeId){
		return supplierDao.getSupplierByBusRegType(name.trim(), busRegTypeId);
	}

	/**
	 * Get the list of Suppliers based on the company id and name
	 * @param companyId The selected company id.
	 * @param name The name of the supplier
	 * @param isExact True if it must be exact, otherwise false.
	 * @return The list of Suppliers.
	 */
	public List<Supplier> getSuppliers(Integer companyId, Integer divisionId, String name,
			Boolean isExact, User user) {
		return getSuppliers(companyId, divisionId, name, isExact, user, 10);
	}

	/**
	 * Get the list of suppliers based on the given params
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param name The supplier name
	 * @param isExact True if exact name, otherwise false
	 * @param user The current user logged
	 * @param limit The number of supplier to be shown
	 * @return The of suppliers
	 */
	public List<Supplier> getSuppliers(Integer companyId, Integer divisionId, String name,
			Boolean isExact, User user, Integer limit) {
		List<Supplier> suppliers = supplierDao.getSuppliers(companyId, divisionId, name, isExact, user, limit, null);
		if (suppliers != null && !suppliers.isEmpty()) {
			return suppliers;
		}
		return Collections.emptyList();
	}

	/**
	 * Get the list of companies.
	 * @param supplierId The supplier id.
	 * @param companyName The company name.
	 * @param user The current user logged.
	 * @return The list of companies.
	 */
	public List<Company> getCompaniesBySupplier(Integer supplierId, String companyName, User user){
		return supplierDao.getCompaniesBySupplier(supplierId, companyName, user);
	}

	/**
	 * Get the list of {@link BusinessClassification}.
	 * @param busClassId The {@link BusinessClassification} id. 
	 * @return The list of {@link BusinessClassification}.
	 */
	public List<BusinessClassification> getBusinessClassifications(Integer classificationId) {
		return businessClassificationDao.getBusinessClassifications(classificationId);
	}

	/**
	 * Get supplier full address
	 * @param supplier The supplier
	 * @return The full address of the supplier
	 */
	public String processSupplierAddress(Supplier supplier) {
		String address = "";
		boolean hasStreet = false;
		boolean hasCity = false;
		if(supplier.getStreetBrgy() != null) {
			hasStreet = true;
		}
		if(supplier.getCityProvince() != null) {
			hasCity = true;
		}
		if(hasStreet && hasCity) {
			address = supplier.getStreetBrgy() +", "+supplier.getCityProvince();
		}else if(hasStreet && !hasCity) {
			address = supplier.getStreetBrgy();
		}else if(!hasStreet && hasCity) {
			address = supplier.getCityProvince();
		}
		return address;
	}

	/**
	 * Checks if unique tin
	 * @param supplier The supplier
	 * @return True or False
	 */
	public boolean isUniqueTin(Supplier supplier) {
		return supplierDao.isUniqueTin(supplier);
	}
}
