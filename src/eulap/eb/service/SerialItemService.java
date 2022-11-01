package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ItemAddOnDao;
import eulap.eb.dao.ItemDiscountDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.SerialItemDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemAddOn;
import eulap.eb.domain.hibernate.ItemDiscountType;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;

/**
 * A class that handles the business logic of Serialized Item.

 */

@Service
public class SerialItemService {
	private static Logger logger = Logger.getLogger(SerialItemService.class);
	@Autowired
	private SerialItemDao serialItemDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private ItemDiscountDao discountDao;
	@Autowired
	private ItemAddOnDao addOnDao;

	/**
	 * Get the list of serialized item serial numbers
	 * @param serialNumber The serial number
	 * @param warehouseId The warehouse id
	 * @param itemId The item id
	 * @param isExact True if the serial number exists, otherwise false
	 * @param divisionId The division id
	 * @return The list of serialized item serial numbers
	 */
	public List<SerialItem> getSerializeItems(String serialNumber, Integer companyId, Integer warehouseId,
			String stockCode, boolean isExact, Integer referenceObjectId, Integer divisionId) {
		Item item = itemService.getRetailItem(stockCode, null, warehouseId, false, divisionId, false);
		List<SerialItem> serialItems = new ArrayList<SerialItem>();
		if (item != null) {
			serialItems = serialItemDao.getItemSerialNumbers(serialNumber, warehouseId, item.getId(),
					isExact, referenceObjectId);
			item = null;
		}
		return serialItems;
	}

	/**
	 * Get the list serialize items by reference object id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param refEbObjectId The reference object Id.
	 * @param warehouseId The warehouse id
	 * @param orTypeId The OR type id
	 * @return The list of serialize items.
	 */
	public List<SerialItem> getSerializeItemsByRefObjectId(Integer companyId, Integer divisionId,
			Integer refEbObjectId, Integer warehouseId, Integer orTypeId, boolean isCancelled, StockAdjustment sa) {
			Double rate = sa != null ? sa.getCurrencyRateValue() != null ? sa.getCurrencyRateValue() : null : null;
		List<SerialItem> sItems = serialItemDao.getByReferenctObjectId(refEbObjectId, orTypeId, isCancelled);
		Double itemSrp = null;
		Item item = null;
		Integer itemId = null;
		for (SerialItem serialItem : sItems) {
			itemId = serialItem.getItemId();
			item = serialItem.getItem();
			serialItem.setOrigItemId(itemId);
			ItemAddOn addOn = serialItem.getItemAddOn();
			if(serialItem.getItemSrp() != null){
				itemSrp = serialItem.getItemSrp().getSrp();
				serialItem.getItemSrp().getSrp();
				serialItem.setOrigSrp(itemSrp);
			} else if(companyId != null){
				ItemSrp srp = itemSrpDao.getLatestItemSrp(companyId, itemId, divisionId);
				if(srp != null) {
					itemSrp = srp.getSrp();
				}
				serialItem.setSrp(itemSrp);
			}
			if(addOn != null) {
				double computeAddOn = addOn.getItemAddOnTypeId().equals(ItemDiscountType.DISCOUNT_TYPE_AMOUNT)
						? addOn.getValue() : SaleItemUtil.computeAddOn(itemSrp, serialItem.getQuantity(), serialItem.getSrp());
						serialItem.setAddOn(NumberFormatUtil.roundOffTo2DecPlaces(computeAddOn));
			}
			serialItem.setStockCode(item.getStockCode());
			serialItem.setUom(item.getUnitMeasurement().getName());
			serialItem.setExistingStocks(itemService.getItemExistingStocks(serialItem.getItemId(),
					warehouseId != null ? warehouseId : serialItem.getWarehouseId()));
			if(rate != null) {
				serialItem.setUnitCost(CurrencyUtil.convertMonetaryValues(serialItem.getUnitCost(), rate));
				serialItem.setAmount(CurrencyUtil.convertMonetaryValues(serialItem.getAmount(), rate));
			}
			serialItem.setOrigUnitCost(serialItem.getUnitCost());
			if (warehouseId == null) {
				serialItem.setOrigWarehouseId(serialItem.getWarehouseId());
			}
		}
		item = null;
		itemSrp = null;
		itemId = null;
		if (isCancelled) {
			return filterSerialItems(sItems);
		} else {
			return sItems;
		}
	}
	/**
	 * Get the list serialize items by reference object id.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param refEbObjectId The reference object Id.
	 * @param warehouseId The warehouse id
	 * @param orTypeId The OR type id
	 * @return The list of serialize items.
	 */
	public List<SerialItem> getSerializeItemsByRefObjectId(Integer companyId, Integer divisionId,
			Integer refEbObjectId, Integer warehouseId, Integer orTypeId, boolean isCancelled){
		return getSerializeItemsByRefObjectId(companyId, divisionId, refEbObjectId, warehouseId, orTypeId, isCancelled, null);
	}
	/**
	 * Get the list serialize items by reference object id.
	 * @param refEbObjectId The reference object Id.
	 * @param warehouseId The warehouse id
	 * @param orTypeId The OR type id
	 * @return The list of serialize items.
	 */
	public List<SerialItem> getSerializeItemsByRefObjectId(Integer refEbObjectId, Integer warehouseId, Integer orTypeId, boolean isCancelled) {
		return getSerializeItemsByRefObjectId(null, null, refEbObjectId, warehouseId, orTypeId, isCancelled, null);
	}

	/**
	 * Get the list serialize items by reference object id.
	 * @param refEbObjectId The reference object Id.
	 * @param warehouseId The warehouse id
	 * @param orTypeId The OR type id
	 * @return The list of serialize items.
	 */
	public List<SerialItem> getSerializeItemsByRefObjectIdAndObject(Integer refEbObjectId, Integer warehouseId, Integer orTypeId, boolean isCancelled, StockAdjustment sa) {
		return getSerializeItemsByRefObjectId(null, null, refEbObjectId, warehouseId, orTypeId, isCancelled, sa);
	}
	/**
	 * Saving Serialized Items.
	 * @param serialItems The list of serialized item.
	 * @param refEbObjectId The reference object id.
	 * @param warehouseId The warehouse id.
	 * @param user The user current log.
	 */
	public void saveSerializedItems(List<SerialItem> serialItems, Integer refEbObjectId,
			Integer warehouseId, User user, Integer orTypeId, boolean isStockIn) {
		logger.info("Saving serialize item.");
		SerialItem si = null;
		for (SerialItem serialItem : serialItems) {
			AuditUtil.addAudit(serialItem, new Audit(user.getId(), true, new Date()));
			String serialNumber = StringFormatUtil.removeExtraWhiteSpaces(serialItem.getSerialNumber());
			// Get and set unit cost based on the selected serial number
			if (!isStockIn) {
				si = serialItemDao.getSerialItem(serialNumber, serialItem.getItemId());
				serialItem.setUnitCost(si.getUnitCost());
			}
			serialItem.setAmount(serialItem.getAmount() != null ? serialItem.getAmount() : null);
			serialItem.setAddOn(serialItem.getAddOn() != null ? serialItem.getAddOn() : null);

			if(serialItem.getEbObjectId() == null){
				EBObject eb = new EBObject();
				AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
				eb.setObjectTypeId(SerialItem.OBJECT_TYPE_ID);
				ebObjectDao.save(eb);
				serialItem.setEbObjectId(eb.getId());
			}

			serialItem.setSerialNumber(serialNumber);
			serialItem.setActive(true);
			serialItem.setWarehouseId(warehouseId != null ?  warehouseId : serialItem.getWarehouseId());
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(refEbObjectId,
					serialItem.getEbObjectId(), orTypeId, user, new Date()));
			serialItemDao.save(serialItem);
		}
		si = null;
	}

	/**
	 * Validated serialized items.
	 * @param nonSerializedField The field for validating if
	 * <br>has items in both serialized or non-serialized.
	 * @param serializedField The serialized Item error field.
	 * @param hasNoneNonSerializedItem
	 * @param isStockIn If item to validate is from stock in form.
	 * @param isRR If item to validate is from Receiving Report form.
	 * @param serialItems The list of serial items.
	 * @param errors The validation errors
	 */
	public void validateSerialItems(String nonSerializedField, String serializedField,
			boolean hasNoneNonSerializedItem, boolean isStockIn, boolean isRR, 
			List<SerialItem> serialItems, Errors errors) {
		validateSerialItems(nonSerializedField, serializedField, hasNoneNonSerializedItem, isStockIn, isRR,
				serialItems, errors, false);
	}

	/**
	 * Validated serialized items.
	 * @param nonSerializedField The field for validating if
	 * <br>has items in both serialized or non-serialized.
	 * @param serializedField The serialized Item error field.
	 * @param hasNoneNonSerializedItem
	 * @param isStockIn If item to validate is from stock in form.
	 * @param isRR If item to validate is from Receiving Report form.
	 * @param serialItems The list of serial items.
	 * @param errors
	 */
	public void validateSerialItems(String nonSerializedField, String serializedField,
			boolean hasNoneNonSerializedItem, boolean isStockIn, boolean isRR, 
			List<SerialItem> serialItems, Errors errors, boolean isReferenceOnly) {
		logger.info("Validating serial items.");
		Map<String, String> hasDuplicateHm = new HashMap<>();
		if (hasNoneNonSerializedItem && (serialItems == null || serialItems.isEmpty())) {
			errors.rejectValue(nonSerializedField, null, null, ValidatorMessages.getString("SerialItemService.0"));
		} else {
			int row = 0;
			String serialNumber = null;
			Integer itemId = null;
			Integer serialItemId = null;
			for (SerialItem serialItem : serialItems) {
				row++;
				boolean hasErrorSerialNumber = false;
				boolean hasExistingSerialNo = false;
				itemId = serialItem.getItemId();
				serialItemId = serialItem.getId();
				if (itemId != null) {
					serialNumber = serialItem.getSerialNumber() != null ? StringFormatUtil.removeExtraWhiteSpaces(serialItem.getSerialNumber()) : serialItem.getSerialNumber();
					if (serialNumber == null || serialNumber.isEmpty()) {
						errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.1")
								+ row + ValidatorMessages.getString("SerialItemService.2"));
						hasErrorSerialNumber = true;
					}
					if (isStockIn) {
//						if (!isRR && (serialItem.getUnitCost() == null || serialItem.getUnitCost().equals(0.0))){
//							errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.3")
//								+ row + ValidatorMessages.getString("SerialItemService.4"));
//						}
						if (!isRR && (serialItem.getUnitCost() == null)){
							serialItem.setUnitCost(0.0);
						}
						if (!isRR && (serialItem.getUnitCost() < 0)){
							errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.3")
								+ row + ValidatorMessages.getString("SerialItemService.20"));
						}
						if(!isRR && itemService.ifFinishedGoods(itemService.getItem(itemId))) {
							if(serialItem.getPoNumber() == null) {
								errors.rejectValue(serializedField, null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.22"), row));
							} else if (serialItem.getPoNumber().trim().length() > 20) {
								errors.rejectValue(serializedField, null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.23"), row));
							}
						}
//						if(!isRR && !itemService.ifFinishedGoods(itemService.getItem(itemId)) && serialItem.getPoNumber() != null) {
//							errors.rejectValue(serializedField, null, null, String.format(ValidatorMessages.getString("StockAdjustmentValidator.24"), row));
//						}
						if (!hasErrorSerialNumber && serialNumber.length() > SerialItem.MAX_SERIAL_NUMBER){
							errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.5")
								+ SerialItem.MAX_SERIAL_NUMBER + ValidatorMessages.getString("SerialItemService.6")
								+ row + ValidatorMessages.getString("SerialItemService.7"));
						} else if (!hasErrorSerialNumber && isExistingSerialNumber(serialNumber, serialItemId, itemId)) {
							errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.8")
								+ row + ValidatorMessages.getString("SerialItemService.9"));
							hasExistingSerialNo = true;
						}
					} else if (!hasErrorSerialNumber) {
						if (!isExistingSerialNumber(serialNumber, serialItemId, itemId)) {
							String errorMsg = String.format(ValidatorMessages.getString("SerialItemService.16"), row);
							errors.rejectValue(serializedField, null, null, errorMsg);
							hasExistingSerialNo = true;
						} else if(serialItem.getQuantity() > 0 && isUsedSerialNumber(serialNumber, serialItemId, itemId)
								&& !isReferenceOnly) {
							String errorMsg = String.format(ValidatorMessages.getString("SerialItemService.18"), row);
							errors.rejectValue(serializedField, null, null, errorMsg);
							hasExistingSerialNo = true;
						}
					}

					if(!hasErrorSerialNumber && !hasExistingSerialNo) {
						String itemKey = itemId+ "-" + serialNumber;
						if(hasDuplicateHm.get(itemKey) != null){
							String str = String.format(ValidatorMessages.getString("SerialItemService.17"),
									serialNumber);
							errors.rejectValue(serializedField, null, null, str);
						} else {
							hasDuplicateHm.put(itemKey, itemKey);
						}
					}
					if(!itemService.getItem(itemId).isActive()) {
						errors.rejectValue(serializedField, null, null, 
								String.format(ValidatorMessages.getString("SerialItemService.19"), row));
					}
				} else {
					if(serialItem.getStockCode() != null && !serialItem.getStockCode().trim().isEmpty()){
						errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.10")
								+ row + ValidatorMessages.getString("SerialItemService.11"));
					} else {
						errors.rejectValue(serializedField, null, null, ValidatorMessages.getString("SerialItemService.12")
								+ row + ValidatorMessages.getString("SerialItemService.13"));
					}
				}
			}
			itemId = null;
			serialItemId = null;
			serialNumber = null;
			row = 0;
		}
	}

	/**
	 * Set the status of the serial number
	 * @param parentObjId The parent EB object id
	 * @param warehouseId The warehouse id
	 * @param orTypeId The OR type id
	 */
	public void setSerialNumberToInactive(List<SerialItem> serialItems) {
		Date currentDate = new Date();
		for (SerialItem serialItem : serialItems) {
			serialItem.setUpdatedDate(currentDate);
			serialItem.setActive(false);
			serialItemDao.update(serialItem);
		}
	}

	/**
	 * Get the list of cancelled serial items by ref Object.
	 * @param refEbObjectId The reference object id.
	 * @return The list of serial items.
	 */
	public Page<SerialItem> getCancelledSerialItemByRef(Integer refEbObjectId, Integer warehouseId){
		Page<SerialItem> serialItems = serialItemDao.getLatestUpdateSerialItemsByRef(refEbObjectId);
		Item item = null;
		Double itemSrp = null;
		for (SerialItem serialItem : serialItems.getData()) {
			item = itemService.getItem(serialItem.getItemId());
			serialItem.setItem(item);
			serialItem.setStockCode(item.getStockCode());
			serialItem.setUom(item.getUnitMeasurement().getName());
			if(serialItem.getItemSrp() != null){
				itemSrp = serialItem.getItemSrp().getSrp();
				serialItem.getItemSrp().getSrp();
				serialItem.setOrigSrp(itemSrp);
				serialItem.setSrp(itemSrp);
			}
			if(serialItem.getItemAddOnId() != null) {
				serialItem.setItemAddOn(addOnDao.get(serialItem.getItemAddOnId()));
			}
			if(serialItem.getItemDiscountId() != null){
				serialItem.setItemDiscount(discountDao.get(serialItem.getItemDiscountId()));
			}
			serialItem.setExistingStocks(itemService.getItemExistingStocks(serialItem.getItemId(),
					warehouseId != null ? warehouseId : serialItem.getWarehouseId()));
		}
		item = null;
		return serialItems;
	}

	/**
	 * Check if the serial number was already exist
	 * @param The serial number
	 * @param serialItemId The serial item id
	 * @param itemId The item id
	 * @return True if the serial number was already exist, otherwise false
	 */
	public boolean isExistingSerialNumber(String serialNumber,
			Integer serialItemId, Integer itemId) {
		return serialItemDao.isExistingOrUsedSerialNumber(serialNumber, serialItemId, itemId, true);
	}

	/**
	 * Check if the serial number has already been used.
	 * @param The serial number
	 * @param serialItemId The serial item id
	 * @param itemId The item id
	 * @return True if the serial number was already exist, otherwise false
	 */
	public boolean isUsedSerialNumber(String serialNumber,
			Integer serialItemId, Integer itemId) {
		return serialItemDao.isExistingOrUsedSerialNumber(serialNumber, serialItemId, itemId, false);
	}

	/**
	 * Check the to be cancelled serial item if used in other forms
	 * @param serialItems The list of serial items
	 * @return The list of serial number exist
	 */
	public String validateToBeCanceledRefForm(List<SerialItem> serialItems) {
		StringBuilder errorMessage = null;
		List<SerialItem> toBeValidatedSerialItems = new ArrayList<>();
		if(serialItems != null && !serialItems.isEmpty()) {
			int count = 0;
			for (SerialItem serialItem : serialItems) {
				List<SerialItem> forms = serialItemDao.getFormByRefSerialNo(serialItem.getSerialNumber(),
						serialItem.getWarehouseId(), serialItem.getId());
				for (SerialItem si : forms) {
					toBeValidatedSerialItems.add(si);
				}
			}

			for (SerialItem si : toBeValidatedSerialItems) {
				if(count == 0) {
					errorMessage = new StringBuilder(ValidatorMessages.getString("SerialItemService.14"));
					count++;
				}
				errorMessage.append("<br> " + si.getSource()
					+ ValidatorMessages.getString("SerialItemService.15") + si.getSerialNumber());
			}
			serialItems = null;
		}
		return errorMessage != null ? errorMessage.toString() : null;
	}

	/**
	 * Get the list of serial items by reference object id
	 * @param refEbObjectId The reference object id
	 * @param orTypeId The OR type id
	 * @return The list of serial items
	 */
	public List<SerialItem> getSerializedItemByRefObjId(Integer refEbObjectId, Integer orTypeId) {
		return serialItemDao.getByReferenctObjectId(refEbObjectId, orTypeId, false);
	}

	/**
	 * Summarize the list of serialized items
	 * @param serialItems The list of serial items
	 * @return The summarized the list of serialized items
	 */
	public List<SerialItem> summarizeItems(List<SerialItem> serialItems) {
		List<SerialItem> summarizedItems = new ArrayList<SerialItem>();
		Map<Integer, SerialItem> serialItemHm = new HashMap<Integer, SerialItem>();
		SerialItem editedItem = null;
		for (SerialItem si : serialItems) {
			if (serialItemHm.containsKey(si.getItemId())) {
				editedItem = processEditedItem(si, serialItemHm.get(si.getItemId()));
				serialItemHm.put(si.getItemId(), editedItem);
			} else {
				serialItemHm.put(si.getItemId(), si);
			}
		}
		for (Map.Entry<Integer, SerialItem> si : serialItemHm.entrySet()) {
			summarizedItems.add(si.getValue());
		}
		serialItemHm = null;
		editedItem = null;
		return summarizedItems;
	}

	/**
	 * Filter duplicate cancelled {@link SerialItem}
	 * @param serialItems
	 * @return List of {@link SerialItem}
	 */
	public List<SerialItem> filterSerialItems(List<SerialItem> serialItems) {
		List<SerialItem> filteredItems = new ArrayList<SerialItem>();
		Map<String, SerialItem> serialItemHm = new HashMap<String, SerialItem>();
		for (SerialItem si : serialItems) {
			if (!serialItemHm.containsKey(si.getSerialNumber())) {
				serialItemHm.put(si.getSerialNumber(), si);
			}
		}
		for (Map.Entry<String, SerialItem> si : serialItemHm.entrySet()) {
			filteredItems.add(si.getValue());
		}
		serialItemHm = null;
		return filteredItems;
	}

	private SerialItem processEditedItem(SerialItem i, SerialItem editedItem) {
		editedItem.setQuantity(i.getQuantity() + editedItem.getQuantity());
		return editedItem;
	}

	/**
	 * Get the list of RR remaining items for references
	 * @param ebObjectId The EB object id
	 * @return The list of RR remaining items for references
	 */
	public List<SerialItem> getRrRemainingItems(Integer ebObjectId) {
		return serialItemDao.getRrRemainingItems(ebObjectId);
	}

	/**
	 * Get the list of invoice goods/services remaining items for references
	 * @param ebObjectId The EB object id
	 * @return The list of invoice goods/services remaining items for references
	 */
	public List<SerialItem> getInvGsRemainingItems(Integer ebObjectId) {
		return serialItemDao.getInvGoodsRemainingItems(ebObjectId);
	}

	/**
	 * Get the remaining PO serial item quantity
	 * @param ebObjectId The reference object id
	 * @param itemId The item id
	 * @return The remaining PO serial item quantity.
	 */
	public double getPoRemainingQuantity(Integer ebObjectId, Integer itemId) {
		return serialItemDao.getPoRemainingQuantity(ebObjectId, itemId);
	}
}
