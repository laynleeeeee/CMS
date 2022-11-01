package eulap.eb.validator.inv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemBuyingAddOn;
import eulap.eb.domain.hibernate.ItemBuyingDiscount;
import eulap.eb.domain.hibernate.ItemBuyingPrice;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.service.ItemAddOnUtil;
import eulap.eb.service.ItemBuyingService;
import eulap.eb.service.ItemDiscountUtil;
import eulap.eb.service.ItemService;
import eulap.eb.service.ItemSrpService;
import eulap.eb.validator.ValidatorMessages;

/**
 * Validator for retail item

 *
 */
@Service
public class RetailItemValidator implements Validator {
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemSrpService itemSrpService;
	@Autowired
	private ItemBuyingService buyingService;
	@Autowired
	private CompanyDao companyDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return Item.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		validate(object, errors, "", false);
	}

	public void validate(Object object, Errors errors, String fieldPrepend, boolean isValidateSrp) {
		Item item = (Item) object;

		if (item.getStockCode() == null || item.getStockCode().isEmpty()) {
			errors.rejectValue(fieldPrepend+"stockCode", null, null, ValidatorMessages.getString("RetailItemValidator.0"));
		} else {
			if (item.getStockCode().length() > Item.MAX_STOCK_CODE)
				errors.rejectValue(fieldPrepend+"stockCode", null, null, String.format(ValidatorMessages.getString("RetailItemValidator.1"), Item.MAX_STOCK_CODE));
			if (!itemService.isUniqueStockcode(item.getStockCode().trim(), item.getId()))
				errors.rejectValue(fieldPrepend+"stockCode", null, null, ValidatorMessages.getString("RetailItemValidator.2"));
		}

		if (item.getDescription().trim() == null || item.getDescription().trim().isEmpty()) {
			errors.rejectValue(fieldPrepend+"description", null, null, ValidatorMessages.getString("RetailItemValidator.3"));
		} else {
			if (item.getDescription().length() > Item.MAX_DESCRIPTION)
				errors.rejectValue(fieldPrepend+"description", null, null, ValidatorMessages.getString("RetailItemValidator.4"));
			if (itemService.hasDuplicateDescription(item))
				errors.rejectValue(fieldPrepend+"description", null, null, ValidatorMessages.getString("RetailItemValidator.5"));
		}

		if (item.getUnitMeasurementId() == null ) {
			errors.rejectValue(fieldPrepend+"unitMeasurementId", null, null, ValidatorMessages.getString("RetailItemValidator.6"));
		}

		if (item.getItemCategoryId() == null ) {
			errors.rejectValue(fieldPrepend+"itemCategoryId", null, null, ValidatorMessages.getString("RetailItemValidator.8"));
		}

		//Validate the Selling price details of the item.
		if(isValidateSrp) {
			List<ItemSrp> itemSrps = item.getItemSrps();
			if(itemSrps.isEmpty()) {
				errors.rejectValue(fieldPrepend+"errorItemSrps", null, null, ValidatorMessages.getString("RetailItemValidator.34"));
			}
		}

		List<ItemSrp> itemSrps = item.getItemSrps();
		if(!itemSrps.isEmpty()) {
			if (itemSrpService.hasInvalidItemSrp(itemSrps))
				errors.rejectValue(fieldPrepend+"errorItemSrps", null, null, ValidatorMessages.getString("RetailItemValidator.7"));
			if (itemSrpService.hasZeroOrNegValues(itemSrps))
				errors.rejectValue(fieldPrepend+"errorItemSrps", null, null, ValidatorMessages.getString("RetailItemValidator.9"));
		}

		//Validate the Discount details of the item.
		List<ItemDiscount> itemDiscounts = item.getItemDiscounts();
		if(!itemDiscounts.isEmpty()) {
			ItemDiscountUtil<ItemDiscount> discountUtil = new ItemDiscountUtil<ItemDiscount>();
			if (discountUtil.hasInvalidCompany(itemDiscounts, companyDao)) {
				errors.rejectValue(fieldPrepend+"errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.10"));
			} else {
				List<Integer> companyIds = itemSrpService.getCompanyIds(item.getItemSrps());
				if (discountUtil.isCompanyNotInSrps(companyIds, item.getItemDiscounts()))
					errors.rejectValue(fieldPrepend+"errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.11"));
			}

			if (discountUtil.hasNoName(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.12"));
			} else if (discountUtil.hasDuplicateName(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.13"));
			}

			if (discountUtil.hasZeroNegValues(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.14"));
			}

			if (discountUtil.hasNameExceeded(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.15") +
						ItemDiscountUtil.DISCOUNT_MAX_NAME + ValidatorMessages.getString("RetailItemValidator.16"));
			}
		}

		//Validate the add ons of the item.
		List<ItemAddOn> itemAddOns = item.getItemAddOns();
		if (itemAddOns != null && !itemAddOns.isEmpty()) {
			ItemAddOnUtil<ItemAddOn> addOnUtil = new ItemAddOnUtil<ItemAddOn>();
			if (addOnUtil.hasNoName(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.17"));
			} else if (addOnUtil.hasDuplicateName(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.18"));
			}

			if (addOnUtil.hasNegValues(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.19"));
			}

			if (addOnUtil.hasNameExceeded(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.20") +
						ItemAddOnUtil.ADD_ON_MAX_NAME + ValidatorMessages.getString("RetailItemValidator.21"));
			}
		}

		//Validate buying prices
		//If no buying prices, other details of buying won't be validated also.
		List<ItemBuyingPrice> buyingPrices = item.getBuyingPrices();
		if(buyingPrices == null) {
			return;
		} else {
			String errorMessage = buyingService.validateBuyingPrices(buyingPrices);
			if(errorMessage != null) {
				errors.rejectValue(fieldPrepend+"buyingPrices", null, null, errorMessage);
			}

			//Validate buying discount details of item
			List<ItemBuyingDiscount> buyingDiscounts = item.getBuyingDiscounts();
			if(!buyingDiscounts.isEmpty()) {
				ItemDiscountUtil<ItemBuyingDiscount> discountUtil = new ItemDiscountUtil<ItemBuyingDiscount>();
				if (discountUtil.hasInvalidCompany(buyingDiscounts, companyDao)) {
					errors.rejectValue(fieldPrepend+"buyingDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.22"));
				} else {
					List<Integer> companyIds = buyingService.getCompanyIds(item.getBuyingPrices());
					if (discountUtil.isCompanyNotInSrps(companyIds, buyingDiscounts))
						errors.rejectValue(fieldPrepend+"buyingDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.23"));
				}

				if (discountUtil.hasNoName(buyingDiscounts)) {
					errors.rejectValue(fieldPrepend+"buyingDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.24"));
				} else if (discountUtil.hasDuplicateName(buyingDiscounts)) {
					errors.rejectValue(fieldPrepend+"buyingDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.25"));
				}

				if (discountUtil.hasZeroNegValues(buyingDiscounts)) {
					errors.rejectValue(fieldPrepend+"buyingDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.26"));
				}

				if (discountUtil.hasNameExceeded(buyingDiscounts)) {
					errors.rejectValue(fieldPrepend+"buyingDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.27") +
							ItemDiscountUtil.DISCOUNT_MAX_NAME + ValidatorMessages.getString("RetailItemValidator.28"));
				}
			}

			//Validate Add On details of item
			List<ItemBuyingAddOn> buyingAddOns = item.getBuyingAddOns();
			if(!buyingAddOns.isEmpty()) {
				ItemAddOnUtil<ItemBuyingAddOn> addOnUtil = new ItemAddOnUtil<ItemBuyingAddOn>();
				if (addOnUtil.hasNoName(buyingAddOns)) {
					errors.rejectValue(fieldPrepend+"buyingAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.29"));
				} else if (addOnUtil.hasDuplicateName(buyingAddOns)) {
					errors.rejectValue(fieldPrepend+"buyingAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.30"));
				}

				if (addOnUtil.hasNegValues(buyingAddOns)) {
					errors.rejectValue(fieldPrepend+"buyingAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.31"));
				}

				if (addOnUtil.hasNameExceeded(buyingAddOns)) {
					errors.rejectValue(fieldPrepend+"buyingAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.33") +
							ItemAddOnUtil.ADD_ON_MAX_NAME + ValidatorMessages.getString("RetailItemValidator.32"));
				}
			}
		}
	}
}
