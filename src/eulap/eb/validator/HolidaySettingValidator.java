package eulap.eb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.HolidaySetting;
import eulap.eb.service.CompanyService;
import eulap.eb.service.HolidaySettingService;

/**
 * Validator class for {@link HolidaySetting}

 *
 */
@Service
public class HolidaySettingValidator implements Validator{

	@Autowired
	private CompanyService companyService;

	@Autowired
	private HolidaySettingService holidaySettingService;

	@Override
	public boolean supports(Class<?> clazz) {
		return HolidaySetting.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		HolidaySetting holidaySetting = (HolidaySetting) obj;

		//Company
		if(holidaySetting.getCompanyId() == null){
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("HolidaySettingValidator.0"));
		} else {
			Company company = companyService.getCompany(holidaySetting.getCompanyId());
			if(!company.isActive()){
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("HolidaySettingValidator.1"));
			}
		}
		//Holiday Setting name
		if(holidaySetting.getName() == null || holidaySetting.getName().trim().isEmpty()){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("HolidaySettingValidator.2"));
		} else if (holidaySetting.getName().trim().length() > HolidaySetting.MAX_NAME){
			errors.rejectValue("name", null, null, ValidatorMessages.getString("HolidaySettingValidator.3") + HolidaySetting.MAX_NAME + ValidatorMessages.getString("HolidaySettingValidator.4"));
		} else if (!holidaySettingService.isUniqueName(holidaySetting)) {
			errors.rejectValue("name", null, null, ValidatorMessages.getString("HolidaySettingValidator.5"));
		}
		//Date
		if(holidaySetting.getDate() == null){
			errors.rejectValue("date", null, null, ValidatorMessages.getString("HolidaySettingValidator.6"));
		}
	}

}
