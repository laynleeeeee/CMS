package eulap.eb.validator.inv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemVolumeConversion;
import eulap.eb.service.CompanyService;
import eulap.eb.service.ItemService;
import eulap.eb.service.ItemVolumeConversionService;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for {@link ItemVolumeConversion}

 * 
 */
@Service
public class ItemVolumeConversionValidator implements Validator{
	@Autowired
	private ItemVolumeConversionService volumeConversionService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ItemService itemService;

	@Override
	public boolean supports(Class<?> clazz) {
		return ItemVolumeConversion.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ItemVolumeConversion volumeConversion = (ItemVolumeConversion) obj;
		if(volumeConversion.getItemId() == 0){
			errors.rejectValue("stockCode", null, null, "Item is required.");
		} else if (volumeConversionService.hasDuplicateItem(volumeConversion)){
			errors.rejectValue("stockCode", null, null, "Item must be unique per company.");
		} else {
			Item rItem = itemService.getRetailItem(volumeConversion.getItemId(), volumeConversion.getCompanyId());
			if(rItem == null) {
				errors.rejectValue("stockCode", null, null, "Invalid stock code.");
			}
		}

		//Check if company is inactive.
		ValidatorUtil.checkInactiveCompany(companyService, volumeConversion.getCompanyId(),
				"companyId", errors, null);

		if(volumeConversion.getQuantity() == 0){
			errors.rejectValue("quantity", null, null, "Quantity is required.");
		} else if (volumeConversion.getQuantity() < 0){
			errors.rejectValue("quantity", null, null, "Negative value is not allowed.");
		}
		if(volumeConversion.getVolumeConversion() == 0){
			errors.rejectValue("volumeConversion", null, null, "Volume Conversion is required.");
		}
		else if (volumeConversion.getVolumeConversion() < 0){
			errors.rejectValue("volumeConversion", null, null, "Negative value is not allowed.");
		}
	}

}
