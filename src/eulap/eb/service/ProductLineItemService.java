package eulap.eb.service;

import java.util.List;

import org.springframework.stereotype.Service;

import eulap.eb.domain.hibernate.ProductLineItem;

/**
 * Class that handles the business logic {@link ProductLineItem}

 *
 */
@Service
public class ProductLineItemService {

	/**
	 * Validate product line items.
	 * @param productLineItems The list of product line items.
	 * @param mainProductId The product line id.
	 * @return Validation message.
	 */
	public String validateProductLineItems(List<ProductLineItem> productLineItems, Integer mainProductId) {
		int productLineId = 0;
		int count = 1;
		for (ProductLineItem productLineItem : productLineItems) {
			if(productLineItem.getItemId() == null){
				return "Invalid stock code in row "+ count +" .";
			}
			productLineId = productLineItem.getItemId();
			if(productLineId == mainProductId){
				return "Product line must not be equal to raw materials at row "+ count +" .";
			} else if (productLineItem.getQuantity() == null) {
				return "Quantity is required at line "+count+".";
			} else if (hasDuplicateRawMarterial(productLineId, productLineItems, count)) {
				return "Duplicate item for Stockcode: " + productLineItem.getItemStockCode() + ".";
			}
			count++;
		}
		return null;
	}

	private Boolean hasDuplicateRawMarterial(Integer productLineId, List<ProductLineItem> productLineItems, int index) {
		int count = 1;
		for (ProductLineItem productLineItem : productLineItems) {
			if(count != index && productLineId.equals(productLineItem.getItemId())){
				return true;
			}
			count++;
		}
		return false;
	}
}
