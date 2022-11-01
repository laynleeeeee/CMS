package eulap.eb.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.ProductLineItem;
import eulap.eb.service.ProductLineItemService;
import eulap.eb.service.ProductLineService;

/**
 * Product line validator.

 *
 */
@Service
public class ProductLineValidator implements Validator{
	@Autowired
	private ProductLineService productLineService;
	@Autowired
	private ProductLineItemService productLineItemService;
	@Override
	public boolean supports(Class<?> clazz) {
		return ProductLine.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		ProductLine productLine = (ProductLine) object;
		if (productLine.getMainItemId() == null) {
			errors.rejectValue("mainItemName", null, null, ValidatorMessages.getString("ProductLineValidator.0"));
		} else {
			if(productLineService.isDuplicateProductline(productLine)){
				errors.rejectValue("mainItemName", null, null, ValidatorMessages.getString("ProductLineValidator.1"));
			}
			List<ProductLineItem> productLineItems = productLine.getProductLineItems();
			if (productLineItems != null) {
				if(productLineItems.isEmpty()){
					errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ProductLineValidator.2"));
				} else {
					String errorMsg = productLineItemService.validateProductLineItems(productLineItems, productLine.getMainItemId());
					if(errorMsg != null){
						errors.rejectValue("errorMessage", null, null, errorMsg);
					}
				}
			} else {
				errors.rejectValue("errorMessage", null, null, ValidatorMessages.getString("ProductLineValidator.3"));
			}
		}

	}
}
