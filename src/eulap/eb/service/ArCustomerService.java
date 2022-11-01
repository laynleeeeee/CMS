package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.BusinessClassificationDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.DivisionProjectDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ReceiptMethodDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.DivisionProject;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ReceiptMethod;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will handle the business logic for AR Customer.

 *
 */
@Service
public class ArCustomerService {
	@Autowired
	private ArCustomerDao customerDao;
	@Autowired
	private ReceiptMethodDao receiptMethodDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private DivisionProjectService divisionProjectService;
	@Autowired
	private DivisionProjectDao divisionProjectDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private BusinessClassificationDao businessClassificationDao;

	/**
	 * Save the AR Customer.
	 */
	public void saveArCustomer(User user, ArCustomer arCustomer) {
		boolean isNewRecord = arCustomer.getId() == 0 ? true : false;
		if (isNewRecord) {
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
			eb.setObjectTypeId(ArCustomer.OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			arCustomer.setEbObjectId(eb.getId());
		}
		AuditUtil.addAudit(arCustomer, new Audit(user.getId(), isNewRecord, new Date()));
		arCustomer.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		arCustomer.setName(arCustomer.getName().trim());
		arCustomer.setLastName(arCustomer.getLastName().trim());
		arCustomer.setFirstName(arCustomer.getFirstName().trim());
		arCustomer.setMiddleName(arCustomer.getMiddleName().trim());
		arCustomer.setStreetBrgy(arCustomer.getStreetBrgy().trim());
		arCustomer.setCityProvince(arCustomer.getCityProvince().trim());
		arCustomer.setContactPerson(arCustomer.getContactPerson().trim());
		arCustomer.setContactNumber(arCustomer.getContactNumber().trim());
		arCustomer.setEmailAddress(arCustomer.getEmailAddress().trim());
		customerDao.saveOrUpdate(arCustomer);

		if (arCustomer.isProject()) {
			divisionProjectService.saveProjectDivision(user, arCustomer);
		}

		if (!isNewRecord) {
			DivisionProject divisionProject = divisionProjectDao.getDivisionProject(arCustomer.getId());
			if (divisionProject != null) {
				Division division = divisionDao.get(divisionProject.getDivisionId());
				division.setActive(arCustomer.isActive());
				String projectName = StringFormatUtil.removeExtraWhiteSpaces(arCustomer.getName());
				division.setName(projectName);
				division.setDescription(projectName);
				divisionDao.saveOrUpdate(division);
			}
		}
	}

	/**
	 * Roll back the data that was saved with error.
	 * @param toBeDeleted The domain that will be deleted.
	 */
	public void rollback (Queue<Domain> toBeDeleted) { // Last in first out
		for (Domain d : toBeDeleted) {
			customerDao.delete(d);
		}
	}

	/**
	 * Evaluate the AR Customer name is unique.
	 * @return True if unique, false if there is already an entry of the evaluated name.
	 */
	public boolean isUniqueName(ArCustomer customer) {
		if(customer.getId() != 0) {
			ArCustomer existingCustomer = customerDao.get(customer.getId());
			if(existingCustomer.getName().trim().equalsIgnoreCase(customer.getName()))
				return true;
		}
		return customerDao.isUniqueName(customer.getName());
	}

	/**
	 * Get the AR Customer object using its Id.
	 * @param id The Id of the AR Customer.
	 * @return The AR Customer object.
	 */
	public ArCustomer getCustomer(int id) {
		return customerDao.get(id);
	}

	/**
	 * Get the AR Customers of the logged user.
	 * @param user The current logged user.
	 * @return The collection of customers.
	 */
	public Collection<ArCustomer> getArCustomers (User user, ArTransaction arTransaction){
		Collection<ArCustomer> dbCustomers = customerDao.getArCustomers(user.getServiceLeaseKeyId());
		Collection<ArCustomer> arCustomers = new ArrayList<ArCustomer>();
		if ( arTransaction != null && arTransaction.getArCustomer() != null){
			boolean hasSelectedArCustomer = false;
			for (ArCustomer arCustomer : dbCustomers) {
				if (arCustomer.getId() == arTransaction.getCustomerId()){
					hasSelectedArCustomer = true;
					break;
				}
				if (!hasSelectedArCustomer)
					arCustomers.add(arCustomer);
			}
		}
		return arCustomers;
	}

	/**
	 * Get the list of AR Customers based on the logged user.
	 * @param user The logged user.
	 * @return The list of AR Customers.
	 */
	public List<ArCustomer> getArCustomers (User user) {
		return (List<ArCustomer>) customerDao.getArCustomers(user.getServiceLeaseKeyId());
	}

	/**
	 * Get the list of AR Customers based on the name and logged user.
	 * @param name The name of the ar customer.
	 * @param isExact True if it must be exact, otherwise false.
	 * @param user The logged user.
	 * @return The list of AR Customers.
	 */
	public List<ArCustomer> getArCustomers (String name, Boolean isExact, User user) {
		List<ArCustomer> arCustomers = customerDao.getArCustomers(name, isExact, user.getServiceLeaseKeyId());
		if (arCustomers == null || arCustomers.isEmpty())
			return Collections.emptyList();
		return arCustomers;
	}

	/**
	 *  Get the list of AR Customers based on the company id and name
	 * @param companyId The selected company id.
	 * @param name The name of the ar customer.
	 * @param isExact True if it must be exact, otherwise false.
	 * @param divisionId The division id
	 * @return The list of AR Customers.
	 */
	public List<ArCustomer> getArCustomers (Integer companyId, String name, Boolean isExact, Integer divisionId) {
		List<ArCustomer> arCustomers = customerDao.getArCustomers(companyId, name, isExact, divisionId);
		if (arCustomers == null || arCustomers.isEmpty()) {
			return Collections.emptyList();
		}
		return arCustomers;
	}

	/**
	 * Search the Ar Customers.
	 * @param name The customer name.
	 * @param address The customer address.
	 * @param status The customer status.
	 * @param pageNumber The page number.
	 * @return The page result.
	 */
	public Page<ArCustomer> searchCustomers(Integer bussClassId, String name, String streetBrgy, String cityProvince,
			String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<ArCustomer> customers = customerDao.searchArCustomer(bussClassId, name.trim(), streetBrgy.trim(), cityProvince.trim(),
				searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		processCustomerTin((List<ArCustomer>) customers.getData());
		return customers;
	}

	private void processCustomerTin(List<ArCustomer> customers) {
		for(ArCustomer cust : customers) {
			if(cust.getTin()!=null&&!cust.getTin().isEmpty()) {
				if (cust.getTin().length()==13) {
					cust.setTin(StringFormatUtil.processTin(StringFormatUtil.parseBIRTIN(cust.getTin()))
							+"-"+StringFormatUtil.parseBranchCode(cust.getTin()));
				} else {
					cust.setTin(StringFormatUtil.processTin(cust.getTin()));
				}
			}
		}
	}

	private void processName(ArCustomer customer) {
		customer.setName(customer.getLastName() + ", " +customer.getFirstName()+ " " 
				+ (!customer.getMiddleName().isEmpty() ? customer.getMiddleName().charAt(0) : ""));
	}

	/**
	 * Get the list of AR Customers based on the name.
	 * @param name The name of the ar customer.
	 * @param activeOnly True to filter active only, False to get all.
	 * @param limit The maximum number of result per query.
	 * @return The list of AR Customers.
	 */
	public List<ArCustomer> getArCustomers(String name, boolean activeOnly, Integer limit) {
		return customerDao.getArCustomers(name, activeOnly, limit);
	}

	/**
	 *  Get the list of AR Customers based on the receipt method.
	 * @param receiptMethodId The selected receipt method id.
	 * @param name The name of the ar customer.
	 * @param isExact True if it must be exact, otherwise false.
	 * @return The list of AR Customers.
	 */
	public List<ArCustomer> getArCustomersByRM (int receiptMethodId, String name, Boolean isExact) {
		ReceiptMethod receiptMethod = receiptMethodDao.get(receiptMethodId);
		List<ArCustomer> arCustomers = new ArrayList<ArCustomer>();
		if (receiptMethod != null) {
			arCustomers = customerDao.getArCustomers(receiptMethod.getCompanyId(), name, isExact, null);
			if (arCustomers == null || arCustomers.isEmpty())
				return Collections.emptyList();
		}
		return arCustomers;
	}

	/**
	 * Get the ar customer object by name.
	 * @param customerName The name filter.
	 * @return The customer object.
	 */
	public ArCustomer getByName (String customerName) {
		return customerDao.getByName(customerName);
	}

	/**
	 * Check if the TIN is unique/
	 * @return True if tin is unique per customer.
	 */
	public boolean isUniqueTin(ArCustomer arCustomer) {
		return customerDao.isUniqueTin(arCustomer);
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
	 * Get ar customer full address
	 * @param arCustomer The AR customer
	 * @return The full address of the customer
	 */
	public String getArCustomerFullAddress(ArCustomer arCustomer) {
		String address = "";
		boolean hasStreet = false;
		boolean hasCity = false;
		if(arCustomer.getStreetBrgy() != null) {
			hasStreet = true;
		}
		if(arCustomer.getCityProvince() != null) {
			hasCity = true;
		}
		if(hasStreet && hasCity) {
			address = arCustomer.getStreetBrgy() +", "+arCustomer.getCityProvince();
		}else if(hasStreet && !hasCity) {
			address = arCustomer.getStreetBrgy();
		}else if(!hasStreet && hasCity) {
			address = arCustomer.getCityProvince();
		}
		return address;
	}
}
