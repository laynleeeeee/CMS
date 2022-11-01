package eulap.eb.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.ItemCategoryDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ItemVatTypeDao;
import eulap.eb.dao.SerialItemSetupDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemDiscount;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ItemVatType;
import eulap.eb.domain.hibernate.SerialItemSetup;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.SerialItemSetupDto;

/**
 * A class that handles the business logic of {@link SerialItemSetup}.

 *
 */
@Service
public class SerialItemSetupService {
	private static final Logger logger = Logger.getLogger(SerialItemSetupService.class);
	@Autowired
	private SerialItemSetupDao serialItemSetupDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemSrpService itemSrpService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemCategoryDao itemCategoryDao;
	@Autowired
	private ItemVatTypeDao itemVatTypeDao;
	@Autowired
	private UnitMeasurementDao uomDao;

	/**
	 * Get the serial item setup
	 * @param itemId The retail item id
	 * @return The serial item setup object
	 */
	public SerialItemSetup getSerialItemSetup(Integer itemId) {
		return serialItemSetupDao.getSerialItemSetupByItemId(itemId);
	}

	/**
	 * Save the serial item setup.
	 * @param serialItemSetupDto The Serial Item Setup DTO
	 * @param userId The logged user id
	 */
	public void saveRetailItem(SerialItemSetupDto serialItemSetupDto, Integer userId) {
		logger.info("Saving item.");
		Item item = serialItemSetupDto.getItem();
		item.setStockCode(StringFormatUtil.removeExtraWhiteSpaces(item.getStockCode()));
		item.setDescription(StringFormatUtil.removeExtraWhiteSpaces(item.getDescription()));
		item.setManufacturerPartNo(StringFormatUtil.removeExtraWhiteSpaces(item.getManufacturerPartNo()));
		itemService.saveRItem(item, userId);

		logger.info("Saving serial item setup.");
		SerialItemSetup serialItemSetup = serialItemSetupDto.getSerialItemSetup();
		serialItemSetup.setItemId(item.getId());
		serialItemSetupDao.saveOrUpdate(serialItemSetup);
		logger.info("Successfully save retail item");
	}

	/**
	 * Validate the retail item form.
	 * @param serialItemSetupDto The serial item setup DTO
	 * @param errors The validation errors
	 */
	public void validateRetailItem(SerialItemSetupDto serialItemSetupDto, Errors errors) {
		validateRetailItem(serialItemSetupDto, errors, "");
	}

	/**
	 * Validate the retail item form with field prepend.
	 * @param serialItemSetupDto The serial item setup DTO
	 * @param errors The validation errors
	 * @param fieldPrepend The prefix for the field path.
	 */
	public void validateRetailItem(SerialItemSetupDto serialItemSetupDto, Errors errors, String fieldPrepend) {
		Item item = serialItemSetupDto.getItem();

		if (item.getStockCode() == null || item.getStockCode().isEmpty()) {
			errors.rejectValue(fieldPrepend+"item.stockCode", null, null, ValidatorMessages.getString("RetailItemValidator.0"));
		} else {
			if (item.getStockCode().length() > Item.MAX_STOCK_CODE)
				errors.rejectValue(fieldPrepend+"item.stockCode", null, null, String.format(ValidatorMessages.getString("RetailItemValidator.1"), Item.MAX_STOCK_CODE));
			if (!itemService.isUniqueStockcode(item.getStockCode().trim(), item.getId()))
				errors.rejectValue(fieldPrepend+"item.stockCode", null, null, ValidatorMessages.getString("RetailItemValidator.2"));
		}

		if (item.getItemVatTypeId() == null) {
			//VAT Type is required.
			errors.rejectValue(fieldPrepend+"item.vatTypeId", null, null, ValidatorMessages.getString("RetailItemValidator.41"));
		}

		if (item.getDescription().trim() == null || item.getDescription().trim().isEmpty()) {
			errors.rejectValue(fieldPrepend+"item.description", null, null, ValidatorMessages.getString("RetailItemValidator.3"));
		} else {
			if (item.getDescription().length() > Item.MAX_DESCRIPTION)
				errors.rejectValue(fieldPrepend+"item.description", null, null, ValidatorMessages.getString("RetailItemValidator.4"));
			if (itemService.hasDuplicateDescription(item))
				errors.rejectValue(fieldPrepend+"item.description", null, null, ValidatorMessages.getString("RetailItemValidator.5"));
		}

		if (item.getUnitMeasurementId() == null ) {
			errors.rejectValue(fieldPrepend+"item.unitMeasurementId", null, null, ValidatorMessages.getString("RetailItemValidator.6"));
		} else if(!uomDao.get(item.getUnitMeasurementId()).isActive()) {
			errors.rejectValue(fieldPrepend+"item.unitMeasurementId", null, null, ValidatorMessages.getString("RetailItemValidator.42"));
		}

		Integer itemCategoryId = item.getItemCategoryId();
		ItemCategory itemCategory = itemCategoryDao.get(itemCategoryId);
		if (itemCategoryId == null) {
			errors.rejectValue(fieldPrepend+"item.itemCategoryId", null, null, ValidatorMessages.getString("RetailItemValidator.8"));
		} else if (!itemCategory.isActive()) {
			errors.rejectValue(fieldPrepend+"item.itemCategoryId", null, null, ValidatorMessages.getString("RetailItemValidator.35"));
		}

		if((item.getManufacturerPartNo() != null || !item.getManufacturerPartNo().trim().isEmpty())
				&& item.getManufacturerPartNo().length() > Item.MAX_MANU_PART_NO) {
			errors.rejectValue(fieldPrepend+"item.manufacturerPartNo", null, null, String.format(ValidatorMessages.getString("RetailItemValidator.36"), Item.MAX_MANU_PART_NO));
		}

		if((item.getPurchaseDesc() != null && !item.getPurchaseDesc().trim().isEmpty())
				&& item.getPurchaseDesc().length() > Item.MAX_PURCHASE_DESCRIPTION) {
			errors.rejectValue(fieldPrepend+"item.purchaseDesc", null, null, String.format(ValidatorMessages.getString("RetailItemValidator.37"), Item.MAX_PURCHASE_DESCRIPTION));
		}

		if((item.getSalesDesc() != null && !item.getSalesDesc().trim().isEmpty())
				&& item.getSalesDesc().length() > Item.MAX_SALE_DESCRIPTION) {
			errors.rejectValue(fieldPrepend+"item.salesDesc", null, null, String.format(ValidatorMessages.getString("RetailItemValidator.38"), Item.MAX_SALE_DESCRIPTION));
		}

		//Validate the Selling price details of the item.
		List<ItemSrp> itemSrps = item.getItemSrps();
		if(!itemSrps.isEmpty()) {
			if (itemSrpService.hasInvalidItemSrp(itemSrps)) {
				errors.rejectValue(fieldPrepend+"item.errorItemSrps", null, null, ValidatorMessages.getString("RetailItemValidator.7"));
			} else if(itemSrpService.hasDuplicateSRPCombination(itemSrps)) {
				errors.rejectValue(fieldPrepend+"item.errorItemSrps", null, null, ValidatorMessages.getString("RetailItemValidator.39"));
			}
			String divisionErr = itemSrpService.hasInvalidDivisionSrp(itemSrps);
			if(divisionErr != null) {
				errors.rejectValue(fieldPrepend+"item.errorItemSrps", null, null, divisionErr);
			}
			if (itemSrpService.hasZeroOrNegValues(itemSrps)) {
				errors.rejectValue(fieldPrepend+"item.errorItemSrps", null, null, ValidatorMessages.getString("RetailItemValidator.9"));
			}
		}

		//Validate the Discount details of the item.
		List<ItemDiscount> itemDiscounts = item.getItemDiscounts();
		if(!itemDiscounts.isEmpty()) {
			ItemDiscountUtil<ItemDiscount> discountUtil = new ItemDiscountUtil<ItemDiscount>();
			if (discountUtil.hasInvalidCompany(itemDiscounts, companyDao)) {
				errors.rejectValue(fieldPrepend+"item.errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.10"));
			} else {
				List<Integer> companyIds = itemSrpService.getCompanyIds(item.getItemSrps());
				if (discountUtil.isCompanyNotInSrps(companyIds, item.getItemDiscounts()))
					errors.rejectValue(fieldPrepend+"item.errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.11"));
			}

			if (discountUtil.hasNoName(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"item.errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.12"));
			} else if (discountUtil.hasDuplicateName(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"item.errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.13"));
			}

			if (discountUtil.hasZeroNegValues(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"item.errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.14"));
			}

			if (discountUtil.hasNameExceeded(itemDiscounts)) {
				errors.rejectValue(fieldPrepend+"item.errorItemDiscounts", null, null, ValidatorMessages.getString("RetailItemValidator.15") +
						ItemDiscountUtil.DISCOUNT_MAX_NAME + ValidatorMessages.getString("RetailItemValidator.16"));
			}
		}

		//Validate the add ons of the item.
		List<ItemAddOn> itemAddOns = item.getItemAddOns();
		if (itemAddOns != null && !itemAddOns.isEmpty()) {
			ItemAddOnUtil<ItemAddOn> addOnUtil = new ItemAddOnUtil<ItemAddOn>();
			if (addOnUtil.hasNoName(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"item.errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.17"));
			} else if (addOnUtil.hasDuplicateName(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"item.errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.18"));
			}

			if (addOnUtil.hasNegValues(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"item.errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.19"));
			}

			if (addOnUtil.hasNameExceeded(itemAddOns)) {
				errors.rejectValue(fieldPrepend+"item.errorItemAddOns", null, null, ValidatorMessages.getString("RetailItemValidator.20") +
						ItemAddOnUtil.ADD_ON_MAX_NAME + ValidatorMessages.getString("RetailItemValidator.21"));
			}
		}
	}

	/**
	 * Get the serialized and non serialized items
	 * @param companyId The company id
	 * @param stockCode The stock code
	 * @param isSerialized True if the serialized, otherwise false
	 * @param isActive True if item is active, otherwise false
	 * @param divisionId The division id
	 * @return The list of items
	 */
	public List<SerialItemSetup> getRetailItems(Integer companyId, Integer warehouseId, Integer itemCategoryId, String stockCode,
			boolean isSerialized, boolean isActive, Integer divisionId) {
		return serialItemSetupDao.getRetailItems(companyId, itemCategoryId, stockCode, isSerialized, isActive, divisionId);
	}

	/**
	 * Get the serialized or non serialized item.
	 * @param stockCode The stock code.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
	 * @param isSerialized True if the serialized, otherwise false
	 * @param isActiveOnly True if item is active only, otherwise false
	 * @param divisionId The division id
	 * @return The serial item setup.
	 */
	public SerialItemSetup getRetailItem(String stockCode, Integer companyId, Integer warehouseId, boolean isSerialized,
			boolean isActiveOnly, Integer divisionId) {
		SerialItemSetup serialItemSetup = serialItemSetupDao.getRetailItem(stockCode, companyId,
				warehouseId, isSerialized, isActiveOnly, divisionId);
		if (serialItemSetup != null) {
			Item item = serialItemSetup.getItem();
			item.setExistingStocks(itemDao.getItemExistingStocks(item.getId(), warehouseId == null ? -1 : warehouseId, new Date()));
			serialItemSetup.setItem(item);
		}
		return serialItemSetup;
	}

	/**
	 * Get all item categories with selected inactive category.
	 * @param itemCategoryId The item category id.
	 * @return A list of item categories with selected inactive category.
	 */
	public List<ItemCategory> getAllWithInactive(Integer itemCategoryId) {
		List<ItemCategory> itemCategories = itemCategoryDao.getAllActive();
		ItemCategory itemCategory = itemCategoryId != null ? itemCategoryDao.get(itemCategoryId) : null;
		if(itemCategory != null && !itemCategory.isActive()) {
			itemCategories.add(itemCategory);
		}
		return itemCategories;
	}

	/**
	 * Get the list of item vat types.
	 * @param itemVatTypeId the item vat type id.
	 * @return The list of item vat types.
	 */
	public List<ItemVatType> getItemVatTypes(Integer itemVatTypeId) {
		return itemVatTypeDao.getItemVatTypes(itemVatTypeId);
	}

	/**
	 * Generates barcode for item. The first four digits will be based on the item category selected.
	 * @param itemCategoryId The item category id.
	 * @return The generated barcode.
	 */
	public String generateBarcode(Integer itemCategoryId) {
		String barcode = "";
		Integer repeat = 4 - itemCategoryId.toString().length();
		barcode = String.join("", Collections.nCopies(repeat, "0")) + itemCategoryId;
		barcode += RandomStringUtils.randomNumeric(8);

		if (itemDao.getByBarcode(barcode) != null) {
			return generateBarcode(itemCategoryId);
		}
		return barcode;
	}
}
