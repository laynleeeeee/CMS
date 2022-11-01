package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CustomerTypeDao;
import eulap.eb.domain.hibernate.CustomerType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * Service class that will handle business logic for {@link CustomerType}

 */

@Service
public class CustomerTypeService {
	private static final Logger logger = Logger.getLogger(CustomerTypeService.class);
	@Autowired
	private CustomerTypeDao customerTypeDao;

	/**
	 * Get the customer type by id
	 * @param customerTypeId The customer type id
	 * @return The customer type object
	 */
	public CustomerType getCustomerType(Integer customerTypeId) {
		return customerTypeDao.get(customerTypeId);
	}

	/**
	 * Save the Customer Type.
	 */
	public CustomerType saveCustomerType(User user, CustomerType customerType) {
		logger.debug("Saving the customer type.");
		boolean isNewRecord = customerType.getId() == 0 ? true : false;
		AuditUtil.addAudit(customerType, new Audit(user.getId(), isNewRecord, new Date()));
		customerType.setName(customerType.getName().trim());
		customerType.setDescription(customerType.getDescription().trim());
		customerTypeDao.saveOrUpdate(customerType);
		logger.info("Successfully saved the customer type.");
		return customerType;
	}

	/**
	 * Search customer type.
	 * @param name The name of the customer type
	 * @param description The description of the customer type
	 * @param status The status of the customer type
	 * @param pageNumber The page number
	 * @return The page result.
	 */
	public Page<CustomerType> searchCustomerTypes(String name, String description,
			String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return customerTypeDao.searchCustomerTypes(name, description,
				searchStatus, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Check if the customer type has duplicate entry
	 * @param customerType The customer type
	 * @return True if the customer type has duplicated entry, otherwise false
	 */
	public boolean isUniqueName(CustomerType customerType) {
		return customerTypeDao.isUniqueCustomerType(customerType);
	}

	/**
	 * Get the list of customer types with inactive
	 * @param customerTypeId The customer type id
	 * @return The list of customer types with inactive
	 */
	public List<CustomerType> getCustomerTypesWithInactive(Integer customerTypeId) {
		return customerTypeDao.getCustomerTypesWithInactive(customerTypeId, null);
	}

	/**
	 * Get the list of customer types with inactive
	 * @param customerTypeId The customer type id
	 * @param customerId The customer id
	 * @return The list of customer types with inactive
	 */
	public List<CustomerType> getCustomerTypesWithInactive(Integer customerTypeId, Integer customerId) {
		return customerTypeDao.getCustomerTypesWithInactive(customerTypeId, customerId);
	}

	/**
	 * Check if the customer has an inactive customer type
	 * @param customerTypeId The customer type id
	 * @return True if the customer is inactive, otherwise false
	 */
	public boolean isActiveCustomerType(Integer customerTypeId) {
		logger.info("Checking the status of customer type id "+customerTypeId);
		CustomerType customerType = getCustomerType(customerTypeId);
		if(!customerType.isActive()) {
			logger.info("Customer type id "+customerTypeId+" is inactive.");
			return true;
		}
		logger.info("Customer type id "+customerTypeId+" is active.");
		return false;
	}

	/**
	 * Validate the customer type
	 * @param customerType The customer type object
	 * @param errors The validation error message
	 */
	public void validateForm(CustomerType customerType, Errors errors) {
		if(customerType.getName() == null || customerType.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("CustomerTypeService.0"));
		} else if(customerType.getName().length() > CustomerType.MAX_CHARACTER_NAME) {
			errors.rejectValue("name", null, null,
				String.format(ValidatorMessages.getString("CustomerTypeService.1"),
					CustomerType.MAX_CHARACTER_NAME));
		} else if(!isUniqueName(customerType)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("CustomerTypeService.2"));
		}

		if(customerType.getDescription() == null || customerType.getDescription().trim().isEmpty()){
			errors.rejectValue("description", null, null, ValidatorMessages.getString("CustomerTypeService.3"));
		} else if(customerType.getDescription().length() > CustomerType.MAX_CHARACTER_DESCRIPTION) {
			errors.rejectValue("description", null, null,
				String.format(ValidatorMessages.getString("CustomerTypeService.4"),
					CustomerType.MAX_CHARACTER_DESCRIPTION));
		}
	}
}
