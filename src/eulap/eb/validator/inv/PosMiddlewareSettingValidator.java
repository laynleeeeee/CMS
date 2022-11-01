package eulap.eb.validator.inv;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.service.ArCustomerAcctService;
import eulap.eb.service.ArCustomerService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.PosMiddlewareSettingService;
import eulap.eb.service.WarehouseService;
import eulap.eb.validator.ValidatorMessages;


/**
 * Validation class for {@link PosMiddlewareSetting}

 *
 */
@Service
public class PosMiddlewareSettingValidator implements Validator{
	private static Logger logger = Logger.getLogger(PosMiddlewareSettingValidator.class);
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private PosMiddlewareSettingService posMiddlewareSettingService;
	@Autowired
	private ArCustomerService customerService;
	@Autowired
	private ArCustomerAcctService custAcctService;

	@Override
	public boolean supports(Class<?> clazz) {
		return PosMiddlewareSetting.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		PosMiddlewareSetting posMiddlewareSetting = (PosMiddlewareSetting) obj;
		Integer companyId = posMiddlewareSetting.getCompanyId();
		logger.debug("Validating the company id:"+companyId);

		// Company
		if(companyId == null) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.0"));
		} else if(!companyService.getCompany(companyId).isActive()) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.1"));
		} else if (posMiddlewareSettingService.hasActive(posMiddlewareSetting)) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.2"));
		}

		// Warehouse
		if(posMiddlewareSetting.getWarehouseId() == null) {
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.3"));
		} else if (!warehouseService.getWarehouse(posMiddlewareSetting.getWarehouseId()).isActive()) {
			errors.rejectValue("warehouseId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.4"));
		}

		// Customer
		if(posMiddlewareSetting.getArCustomerId() == null 
				&& posMiddlewareSetting.getCustomerName() == null
				&& posMiddlewareSetting.getCustomerName().trim().isEmpty()) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.5"));
		} else if (posMiddlewareSetting.getArCustomerId() != null && 
				!customerService.getCustomer(posMiddlewareSetting.getArCustomerId()).isActive()) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.6"));
		} else if (posMiddlewareSetting.getCustomerName() != null
				&& !posMiddlewareSetting.getCustomerName().trim().isEmpty()) {
			if (customerService.getByName(posMiddlewareSetting.getCustomerName()) == null) {
				errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.7"));
			}
		}

		// Customer Account
		if(posMiddlewareSetting.getArCustomerAccountId() == null){
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.8"));
		} else if (!custAcctService.getAccount(
				posMiddlewareSetting.getArCustomerAccountId()).isActive()) {
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("PosMiddlewareSettingValidator.9"));
		}
	}
}
