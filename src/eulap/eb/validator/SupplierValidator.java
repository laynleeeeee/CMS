package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.BusinessClassificationDao;
import eulap.eb.domain.hibernate.BusinessClassification;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.SupplierService;

/**
 * A class that validates the supplier form.

 *
 */
@Service
public class SupplierValidator implements Validator{
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private BusinessClassificationDao busClassDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return Supplier.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//Do nothing
	}

	public void validate(Object target, User user, Errors errors) {
		// handle the validation
		Supplier supplier = (Supplier) target;
		//Name
		supplier = removeWhitespacesFromSupplier(supplier);
		Integer typeId = supplier.getBussinessClassificationId();
		if(typeId == null) {
			//Supplier type is required.
			errors.rejectValue("bussinessClassificationId", null, null, ValidatorMessages.getString("SupplierValidator.10"));
		} else if(!busClassDao.get(typeId).isActive()) {
			errors.rejectValue("bussinessClassificationId", null, null, ValidatorMessages.getString("SupplierValidator.11"));
		}

		if(typeId == BusinessClassification.INDIVIDUAL_TYPE) {
			if(supplier.getFirstName() == null || supplier.getFirstName().isEmpty()) {
				//First name is required.
				errors.rejectValue("firstName", null, null, ValidatorMessages.getString("SupplierValidator.12"));
			} else if(supplier.getFirstName().length() > Supplier.FN_LN_MN_MAX_CHAR) {
				//First name should not exceed %d characters.
				errors.rejectValue("firstName", null, null,
						String.format(ValidatorMessages.getString("SupplierValidator.13"), Supplier.FN_LN_MN_MAX_CHAR));
			}

			if(supplier.getLastName() == null || supplier.getLastName().isEmpty()) {
				//Last name is required.
				errors.rejectValue("lastName", null, null, ValidatorMessages.getString("SupplierValidator.14"));
			} else if(supplier.getLastName().length() > Supplier.FN_LN_MN_MAX_CHAR) {
				//Last name should not exceed %d characters.
				errors.rejectValue("lastName", null, null,
						String.format(ValidatorMessages.getString("SupplierValidator.15"), Supplier.FN_LN_MN_MAX_CHAR));
			}

			if((supplier.getMiddleName() == null || supplier.getMiddleName().isEmpty()
					&& supplier.getMiddleName().length() > Supplier.FN_LN_MN_MAX_CHAR)) {
				//Middle name should not exceed %d characters
				errors.rejectValue("middleName", null, null,
						String.format(ValidatorMessages.getString("SupplierValidator.16"), Supplier.FN_LN_MN_MAX_CHAR));
			}

			if (!supplierService.isUnique(supplier)) {
				errors.rejectValue("middleName", null, null, ValidatorMessages.getString("SupplierValidator.23"));
			}
		}
		//Name
		String nameLabel = typeId == BusinessClassification.INDIVIDUAL_TYPE ? "Trade name" : "Name";
		if (supplier.getName() == null || supplier.getName().isEmpty()) {
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("SupplierValidator.0"), nameLabel));
		} else if(supplier.getName().length() > Supplier.NAME_MAX_CHAR) {
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("SupplierValidator.1"), nameLabel));
		} else if (!supplierService.isUnique(supplier)) {
			errors.rejectValue("name", null, null, String.format(ValidatorMessages.getString("SupplierValidator.2"), nameLabel));
		}

		//Address
		if(supplier.getStreetBrgy() == null || supplier.getStreetBrgy().isEmpty()) {
			//Street, barangay is required.
			errors.rejectValue("streetBrgy", null, null, ValidatorMessages.getString("SupplierValidator.17"));
		} else if(supplier.getStreetBrgy().length() > Supplier.ADDRESS_MAX_CHAR) {
			//Street, barangay should not exceed %d characters.
			errors.rejectValue("streetBrgy", null, null,
					String.format(ValidatorMessages.getString("SupplierValidator.18"), Supplier.ADDRESS_MAX_CHAR));
		}

		if(supplier.getCityProvince() == null || supplier.getCityProvince().isEmpty()) {
			//City, province, and ZIP code is required.
			errors.rejectValue("cityProvince", null, null, ValidatorMessages.getString("SupplierValidator.19"));
		} else if(supplier.getCityProvince().length() > Supplier.ADDRESS_MAX_CHAR) {
			//City, province, and ZIP code should not exceed %d characters.
			errors.rejectValue("cityProvince", null, null,
					String.format(ValidatorMessages.getString("SupplierValidator.20"), Supplier.ADDRESS_MAX_CHAR));
		}

		//TIN
		if(!supplier.getTin().isEmpty()) {
			if (!supplierService.isUniqueTin(supplier)) {
				errors.rejectValue("tin", null, null, ValidatorMessages.getString("SupplierValidator.6"));
			} else if (supplier.getTin().length() != Supplier.MAX_TIN) {
				errors.rejectValue("tin", null, null,
						String.format(ValidatorMessages.getString("SupplierValidator.21"), Supplier.MAX_TIN));
			}
		}

		//Contact Person
		if(!supplier.getContactPerson().isEmpty()) {
			if(supplier.getContactPerson().length() > 50) {
				errors.rejectValue("contactPerson", null, null, ValidatorMessages.getString("SupplierValidator.7"));
			}
		}

		//Contact Number
		if(!supplier.getContactNumber().isEmpty()) {
			if(supplier.getContactNumber().length() > 20) {
				errors.rejectValue("contactNumber", null, null, ValidatorMessages.getString("SupplierValidator.8"));
			}
		}

		//Business Registration Type
		if(supplier.getBusRegTypeId() == null) {
			errors.rejectValue("busRegTypeId", null, null, ValidatorMessages.getString("SupplierValidator.9"));
		}
	}

	private Supplier removeWhitespacesFromSupplier(Supplier supplier) {
		//Remove white spaces before saving.
		supplier.setName(StringFormatUtil.removeExtraWhiteSpaces(supplier.getName()).trim());
		if(!supplier.getContactPerson().trim().isEmpty())
			supplier.setContactPerson(StringFormatUtil.removeExtraWhiteSpaces(supplier.getContactPerson().trim()));
		if(!supplier.getContactNumber().trim().isEmpty())
			supplier.setContactNumber(StringFormatUtil.removeExtraWhiteSpaces(supplier.getContactNumber().trim()));
		if(supplier.getTin() != null && !supplier.getTin().trim().isEmpty())
			supplier.setTin(supplier.getTin().trim());
		supplier.setLastName(StringFormatUtil.removeExtraWhiteSpaces(supplier.getLastName()).trim());
		supplier.setFirstName(StringFormatUtil.removeExtraWhiteSpaces(supplier.getFirstName()).trim());
		supplier.setMiddleName(StringFormatUtil.removeExtraWhiteSpaces(supplier.getMiddleName()).trim());
		supplier.setStreetBrgy(StringFormatUtil.removeExtraWhiteSpaces(supplier.getStreetBrgy()).trim());
		supplier.setCityProvince(StringFormatUtil.removeExtraWhiteSpaces(supplier.getCityProvince()).trim());
		return supplier;
	}
}
