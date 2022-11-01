package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.BusinessClassificationDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.service.ArCustomerService;

/**
 * Validator class for {@link ArCustomer}

 *
 */
@Service
public class ArCustomerValidator implements Validator{
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private BusinessClassificationDao busClassDao;
	@Autowired
	private ArCustomerDao arCustomerDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return ArCustomer.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ArCustomer arCustomer = (ArCustomer) obj;
		Integer typeId = arCustomer.getBussinessClassificationId();

		if(typeId == null) {
			//Customer type is required.
			errors.rejectValue("bussinessClassificationId", null, null, ValidatorMessages.getString("ArCustomerValidator.12"));
		} else if(!busClassDao.get(typeId).isActive()) {
			errors.rejectValue("bussinessClassificationId", null, null, ValidatorMessages.getString("ArCustomerValidator.18"));
		}

		if(typeId == BusinessClassification.INDIVIDUAL_TYPE) {
			if(arCustomer.getFirstName() == null || arCustomer.getFirstName().trim().isEmpty()) {
				//First name is required.
				errors.rejectValue("firstName", null, null, ValidatorMessages.getString("ArCustomerValidator.13"));
			} else if(arCustomer.getFirstName().length() > ArCustomer.MAX_FIRST_NAME) {
				//First name should not exceed %d characters.
				errors.rejectValue("firstName", null, null,
						String.format(ValidatorMessages.getString("ArCustomerValidator.14"), ArCustomer.MAX_FIRST_NAME));
			}

			if(arCustomer.getLastName() == null || arCustomer.getLastName().trim().isEmpty()) {
				errors.rejectValue("lastName", null, null, ValidatorMessages.getString("ArCustomerValidator.15"));
			} else if(arCustomer.getLastName().length() > ArCustomer.MAX_LAST_NAME) {
				//Last name should not exceed %d characters.
				errors.rejectValue("lastName", null, null,
						String.format(ValidatorMessages.getString("ArCustomerValidator.16"), ArCustomer.MAX_LAST_NAME));
			}

			if((arCustomer.getMiddleName() != null && arCustomer.getMiddleName().length() > ArCustomer.MAX_MIDDLE_NAME)) {
				//Middle name should not exceed %d characters
				errors.rejectValue("middleName", null, null,
						String.format(ValidatorMessages.getString("ArCustomerValidator.17"), ArCustomer.MAX_MIDDLE_NAME));
			}

			if((arCustomer.getFirstName() != null && !arCustomer.getFirstName().trim().isEmpty())
					&& (arCustomer.getLastName() != null && !arCustomer.getLastName().trim().isEmpty())
					&& !arCustomerDao.isUniqueCustomer(arCustomer)) {
				errors.rejectValue("middleName", null, null, ValidatorMessages.getString("ArCustomerValidator.25"));
			}
		}

		//Name
		String nameLabel = typeId == BusinessClassification.INDIVIDUAL_TYPE ? "Trade name" : "Name";
		if (arCustomer.getName() == null || arCustomer.getName().trim().isEmpty()) {
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("ArCustomerValidator.0"), nameLabel));
		} else if (arCustomer.getName().length() > ArCustomer.MAX_NAME) {
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("ArCustomerValidator.1"), nameLabel, ArCustomer.MAX_NAME));
		} else if (!customerService.isUniqueName(arCustomer)) {
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("ArCustomerValidator.2"), nameLabel));
		}

		//Address
		if(arCustomer.getStreetBrgy() == null || arCustomer.getStreetBrgy().trim().isEmpty()) {
			//Street, barangay is required.
			errors.rejectValue("streetBrgy", null, null, ValidatorMessages.getString("ArCustomerValidator.19"));
		} else if(arCustomer.getStreetBrgy().length() > ArCustomer.MAX_ADDRESS) {
			//Street, barangay should not exceed %d characters.
			errors.rejectValue("streetBrgy", null, null,
					String.format(ValidatorMessages.getString("ArCustomerValidator.20"), ArCustomer.MAX_ADDRESS));
		}

		if(arCustomer.getCityProvince() == null || arCustomer.getCityProvince().trim().isEmpty()) {
			//City, province, and ZIP code is required.
			errors.rejectValue("cityProvince", null, null, ValidatorMessages.getString("ArCustomerValidator.21"));
		} else if(arCustomer.getCityProvince().length() > ArCustomer.MAX_ADDRESS) {
			//City, province, and ZIP code should not exceed %d characters.
			errors.rejectValue("cityProvince", null, null,
					String.format(ValidatorMessages.getString("ArCustomerValidator.22"), ArCustomer.MAX_ADDRESS));
		}

		//Maximum Allowable Amount
		if (arCustomer.getMaxAmount() != null) {
			if (arCustomer.getMaxAmount() >= 10000000000.00)
				errors.rejectValue("maxAmount", null, null, ValidatorMessages.getString("ArCustomerValidator.5"));
		}

		//Contact Number
		if (!arCustomer.getContactNumber().trim().isEmpty()) {
			if (arCustomer.getContactNumber().length() > 20)
				errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("ArCustomerValidator.6"));
		}

		if(!arCustomer.getContactPerson().trim().isEmpty()) {
			if(arCustomer.getContactPerson().length() > ArCustomer.MAX_CONTACT_PERSON) {
				errors.rejectValue("contactPerson", null, null,
						String.format(ValidatorMessages.getString("ArCustomerValidator.23"), ArCustomer.MAX_CONTACT_PERSON));
			}
		}
		//Email Address
		if (!arCustomer.getEmailAddress().trim().isEmpty())
			if (arCustomer.getEmailAddress().length() > ArCustomer.MAX_EMAIL_ADDRESS)
				errors.rejectValue("emailAddress", null, null, String.format(
						ValidatorMessages.getString("ArCustomerValidator.24"), ArCustomer.MAX_EMAIL_ADDRESS));

		//TIN
		if (!arCustomer.getTin().trim().isEmpty()) {
			if (!customerService.isUniqueTin(arCustomer)) {
				errors.rejectValue("tin", null, null, ValidatorMessages.getString("ArCustomerValidator.9"));
			} else if (arCustomer.getTin().length() != ArCustomer.MAX_TIN) {
				errors.rejectValue("tin", null, null,
						String.format(ValidatorMessages.getString("ArCustomerValidator.24"), ArCustomer.MAX_TIN));
			}
		}

//		Disabling this since this not required on this project
//		Integer customerTypeId = arCustomer.getCustomerTypeId();
//		if (customerTypeId == null) {
//			errors.rejectValue("customerTypeId", null, null, ValidatorMessages.getString("ArCustomerValidator.10"));
//		} else if (customerTypeService.isActiveCustomerType(customerTypeId)) {
//			errors.rejectValue("customerTypeId", null, null, ValidatorMessages.getString("ArCustomerValidator.11"));
//		}
	}
}
