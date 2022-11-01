package eulap.eb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.eb.domain.hibernate.BaseItem;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.SaleItem;
import eulap.eb.domain.hibernate.Warehouse;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.oo.OOChild;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.AvblStocksAndBagsDto;
import eulap.eb.web.processing.dto.AvailableStock;

/**
 * Utility class for common validation of items.

 */
public class ValidationUtil <T extends BaseItem> {
	private static Logger logger = Logger.getLogger(ValidationUtil.class);

	/**
	 * Validate the quantity to be withdrawn vs the available stocks from individual selection.
	 * @param stockCode The stock code of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param refObjectId The reference object id of the source for the available stocks.
	 * @return Null if quantity is valid, otherwise the error message.
	 */
	public static String validateQtyAvailStocks(ItemService itemService, String stockCode, Integer warehouseId,
			Integer refObjectId, double quantity) {
		if(refObjectId == null) {
			logger.debug("Reference object id is null for stock code "
					+ stockCode +". Will not validate the available stocks.");
			return null;
		}
		logger.debug("Validating the quantity to be withdrawn vs the available stocks of stock code: "+stockCode);
		AvailableStock availableStock = itemService.getAvailableStockByReference(stockCode, warehouseId, refObjectId);
		if(availableStock == null) {
			logger.debug("No available stocks for reference object id: "+refObjectId);
			return "No stocks available for stock code "+stockCode+". Please select stocks from other warehouse.";
		}

		double availableStockQty = NumberFormatUtil.roundOffTo2DecPlaces(availableStock.getQuantity());
		if(availableStockQty < NumberFormatUtil.roundOffTo2DecPlaces(quantity)) {
			logger.debug("Quantity is greater than the available stocks for reference object id: "+refObjectId);
			return "Quantity should not be greater than the selected stocks.";
		}

		logger.debug("Valid quantity for stock code: "+stockCode+" using source: "+availableStock.getSource());
		return null;
	}

	/**
	 * Validate the returned item of the Individual Selection inventory method.
	 * @param returnedItem Class that extends {@link BaseItem}
	 * @param refObjectId The reference object id.
	 * @param remainingQty The remaining quantity to be returned.
	 * @return The error message, otherwise null.
	 */
	public static <T extends BaseItem> String validateReturnItems(T returnedItem, Integer referenceId,
			Integer refObjectId, double remainingQty, String stockCode) {

		if(referenceId != null) {
			//Validate the remaining quantity of the item.
			double qtyToBeReturned = returnedItem.getQuantity() == null ? 0 : returnedItem.getQuantity();
			double origQtyReturned = returnedItem.getOrigQty() != null ? Math.abs(returnedItem.getOrigQty()) : 0;
			qtyToBeReturned = Math.abs(qtyToBeReturned) - origQtyReturned;
			if(qtyToBeReturned > remainingQty) {
				return "Quantity of return items must not be greater than the original quantity.";
			}
		} else {
			//Check if return item/s has/have selected stocks
			if(refObjectId == null) {
				return "No available stocks for stock code "+stockCode;
			}
		}
		return null;
	}

	/**
	 * Validate the quantity vs the existing stocks of the retail item.
	 * @return Returns an error message if the existing stocks is greater than the quantity.
	 */
	public static <T extends BaseItem> String validateWithdrawnQty(ItemService itemService, WarehouseService whService,
			int itemId, boolean isWarehouseChanged, List<T> items, Date asOfDate, Integer warehouseId, int rowCount) {
		logger.debug("Validating the quantity to be withdraw for item id: "+itemId);
		double quantity = getTotalQtyPerItem(itemId, isWarehouseChanged, items);
		logger.debug("Total quantity to be withdrawn: "+quantity+" on warehouse "+warehouseId);
		return validateQuantity(itemService, whService, itemId, asOfDate, warehouseId, quantity, rowCount);
	}

	public static <T extends SaleItem> String validateSaleItems(ItemService itemService, WarehouseService whService,
			int itemId, boolean isWarehouseChanged, List<T> items, Date asOfDate, Integer warehouseId, int rowCount) {
		logger.debug("Validating the quantity to be withdraw for item id: "+itemId);
		double quantity = SaleItemUtil.getTotalQtyByItemAndWH(itemId, warehouseId, items);
		logger.debug("Total quantity to be withdrawn: "+quantity+" on warehouse "+warehouseId);
		return validateQuantity(itemService, whService, itemId, asOfDate, warehouseId, quantity, rowCount);
	}

	/**
	 * Get the total quantity to be withdrawn per item.
	 * @param itemId The id of the item.
	 * @param isWarehouseChanged Set to true if the warehouse is changed, otherwise false.
	 * @param items The list of items to be processed.
	 * @return The total quantity to be withdrawn of the selected item.
	 */
	private static <T extends BaseItem> double getTotalQtyPerItem(int itemId, boolean isWarehouseChanged, List<T> items) {
		double totalQty = 0;
		for (T i : items) {
			if(i.getItemId() != null) {
				if(i.getItemId() == itemId && i.getQuantity() != null) {
					if(isWarehouseChanged) {
						//If warehouse is changed, get the quantity to be withdrawn.
						totalQty += Math.abs(i.getQuantity());
					} else {
						//Otherwise, get the difference between the quantity to be withdrawn and the original quantity.
						//Form is likely edited, quantity is already allocated.
						totalQty += Math.abs(i.getQuantity()) - (i.getOrigQty() != null ? i.getOrigQty() : 0);
					}
				}
			}
		}
		return totalQty;
	}

	/**
	 * Get the total quantity to be withdrawn per item.
	 * @param itemId The id of the item.
	 * @param items The list of items to be processed.
	 * @return The total quantity to be withdrawn of the selected item.
	 */
	public static <T extends BaseItem> double getTotalQtyPerItem(int itemId, List<T> items) {
		return getTotalQtyPerItem(itemId, false, items);
	}

	/**
	 * Validate the quantity vs the existing stocks of the retail item.
	 * @return Returns an error message if the existing stocks is greater than the quantity.
	 */
	public static String validateQuantity(ItemService itemService, WarehouseService whService, Integer itemId,
			Date asOfDate, Integer warehouseId, double quantity, int rowCount) {
		asOfDate = DateUtil.setTimeOfDate(asOfDate, 23, 59, 59);
		double existingStocks = itemService.getItemExistingStocks(itemId, warehouseId, asOfDate);
		//Get the existing stocks as of the current date.
		Date currentDate = DateUtil.setTimeOfDate(new Date(), 23, 59, 59);
		double currentES = itemService.getItemExistingStocks(itemId, warehouseId, currentDate);
		return validateQuantity(whService, asOfDate, quantity, warehouseId, currentES, existingStocks, rowCount);
	}

	public static String validateQuantity(WarehouseService warehouseService, Date asOfDate, double quantity,
			Integer warehouseId, double currentES, double existingStocks, int rowCount) {
		//Check if the current existing stocks is lesser than the existing stocks of the selected date.
		//If true, the existing stocks to be validated will be the current existing stocks.
		if(currentES < existingStocks) {
			existingStocks = currentES;
			asOfDate = new Date();
		}

		if(quantity > existingStocks) {
			Warehouse wh = warehouseService.getWarehouse(warehouseId);
			if (wh != null) {
				return "Existing stocks of " + wh.getName() + " as of "
						+ DateUtil.formatDate(asOfDate)+" is "+existingStocks + " at row "+rowCount+".";
			}
		}
		return null;
	}

	/**
	 * Validates the selected stock code has a reference object id.
	 * @param stockCode the stock code of the item
	 * @param referenceId the reference object id of the source of the available stocks
	 * @return null if the item has no reference object id (no available stocks loaded on the form), otherwise the error message
	 */
	public static String validateRefId(String stockCode, Integer referenceId) {
		if(stockCode != null && referenceId == null) {
			return "No stocks available for stock code "+stockCode+". Please select stocks from other warehouse.";
		}
		return null;
	}

	/**
	 * Check if the quantity of the item will result in a negative existing stocks if form will be cancelled.
	 * @return The error message if cancelling the form will result in a negative existing stocks, otherwise null.
	 */
	public static String validateToBeCancelledItem(ItemService itemService, int itemId, int warehouseId,
			Date currentDate, double quantity) {
		// Existing stocks as of the form date.
		double currentES = itemService.getItemExistingStocks(itemId, warehouseId, currentDate);
		logger.info("existing stocks as of "+DateUtil.formatDate(currentDate)+" is "+currentES);
		// Existing stocks as of today.
		// Just in case the form date is not the same as the date today.
		double existingStocksAsOfToday = itemService.getItemExistingStocks(itemId, warehouseId);
		logger.info("existing stocks as of today is "+existingStocksAsOfToday);
		BigDecimal bdESAsOfFormDate = new BigDecimal(String.valueOf(currentES));
		BigDecimal bdESAsOfToday = new BigDecimal(String.valueOf(existingStocksAsOfToday));
		BigDecimal bdQuantity = new BigDecimal(String.valueOf(quantity));
		BigDecimal zero = new BigDecimal(0);
		// Deduct the quantity to the existing stocks to check if cancelling the form will result in a negative existing stocks.
		if(((bdESAsOfFormDate.subtract(bdQuantity).compareTo(zero) < 0)
				|| (bdESAsOfToday.subtract(bdQuantity).compareTo(zero) < 0))) {
			logger.warn("Cannot cancel the form. Item id "+itemId+" will have negative existing stocks in warehouse : "+warehouseId);
			return "Cancelling the form will result in a negative existing stocks for the item/s in the form.";
		}
		return null;
	}

	/**
	 * Check the to be canceled form if used as a reference form
	 * @param itemService The item service class
	 * @param items The list of reference items
	 * @return The validation message
	 */
	public static <T extends OOChild> String validateToBeCancelledRefForm(ItemService itemService, List<T> items) {
		StringBuilder errorMessage = null;
		List<AvailableStock> availableStocks =  new ArrayList<AvailableStock>();
		if(items != null && !items.isEmpty()) {
			int cnt = 0;
			for (T t : items) {
				List<AvailableStock> forms = itemService.getFormsByRefObjectId(t.getEbObjectId());
				if(!forms.isEmpty()) {
					for (AvailableStock as : forms) {
						availableStocks.add(as);
					}
				}
			}
			for (AvailableStock as : getSummarisedASSource(availableStocks)) {
				if(cnt == 0){
					errorMessage = new StringBuilder("Unable to cancel form, corresponding document was created. "
							+ "Reference form/s:");
					cnt++;
				}
				errorMessage.append("<br> " + as.getSource());
			}
			availableStocks = null;
		}
		return errorMessage != null ? errorMessage.toString() : null;
	}

	/**
	 * Get the summarized list available stocks source
	 * @param forms List of available stocks source
	 * @return The summarized list available stocks source
	 */
	private static List<AvailableStock> getSummarisedASSource (List<AvailableStock> forms) {
		logger.debug("Group sale items by item id.");
		List<AvailableStock> updatedItems = new ArrayList<AvailableStock>();
		Map<String, AvailableStock> discHM = new HashMap<String, AvailableStock>();
		for (AvailableStock i : forms) {
			if(!discHM.containsKey(i.getSource())) {
				discHM.put(i.getSource(), i);
			}
		}
		for (Map.Entry<String, AvailableStock> disc : discHM.entrySet()) {
			updatedItems.add(disc.getValue());
		}
		discHM = null;
		return updatedItems;
	}

	/**
	 * Validates the selected bags and stocks per form.
	 * @param itemBagQuantityService The {@link ItemBagQuantityService}
	 * @param stockCode The stock code of the item.
	 * @param companyId The company Id.
	 * @param itemId The Item Id.
	 * @param warehouseId The warehouse Id.
	 * @param sourceObjectId The ebObjectId of the selected IN form(eg. Stock Adjustment IN - IS)
	 * @param bagsToBeWithdrawn The bags to be withdrawn.
	 * @param stocksToWithDrawn The stocks to be withdrawn.
	 * @return The error validation message.
	 */
	public static String validateAvailableBagsAndStock(ItemBagQuantityService itemBagQuantityService, String stockCode,
			Integer companyId, Integer itemId, Integer warehouseId, Integer sourceObjectId, Double bagsToBeWithdrawn, Double stocksToWithDrawn, Integer itemObjectId) {
		List<AvblStocksAndBagsDto> avblStocksAndBags = itemBagQuantityService.getAvailableBags(companyId, itemId, warehouseId, sourceObjectId, itemObjectId);
		if(avblStocksAndBags == null || avblStocksAndBags.isEmpty()) {
			return String.format(ValidatorMessages.getString("ValidationUtil.0"), stockCode);
		} else if(avblStocksAndBags != null && !avblStocksAndBags.isEmpty()){
			// Expecting only one line
			AvblStocksAndBagsDto bagsDto = avblStocksAndBags.get(0);
			if(bagsToBeWithdrawn != null) {
				if(bagsDto.getTotalBags() == null && (bagsToBeWithdrawn != null && bagsToBeWithdrawn > 0)) {
					return String.format(ValidatorMessages.getString("ValidationUtil.4"), stockCode);
				}
				if(bagsToBeWithdrawn < 0) {
					return ValidatorMessages.getString("ValidationUtil.3");
				}
				if(bagsToBeWithdrawn > bagsDto.getTotalBags()) {
					return ValidatorMessages.getString("ValidationUtil.1");
				}
			}

			if(stocksToWithDrawn != null &&
					stocksToWithDrawn > bagsDto.getTotalStocks()) {
				return String.format(ValidatorMessages.getString("ValidationUtil.2"), stockCode);
			}
		}
		return null;
	}

	/**
	 * Validates if the form's current status is complete or cancelled.
	 * @param formworkflow The {@link FormWorkflow} object.
	 * @return The validation message.
	 */
	public static String validateFormStatus(FormStatusService formStatusService, FormWorkflow formworkflow) {
		if(formworkflow != null && (formworkflow.isComplete() || formworkflow.getCurrentStatusId() == FormStatus.CANCELLED_ID)) {
			return String.format(ValidatorMessages.getString("ValidationUtil.5"), formStatusService.getFormStatus(formworkflow.getCurrentStatusId()).getDescription());
		}
		return null;
	}

	/**
	 * Validates if the form's current status is cancelled.
	 * @param formworkflow The {@link FormWorkflow} object.
	 * @return The validation message.
	 */
	public static String validateFormStatusCancelled(FormStatusService formStatusService, FormWorkflow formworkflow) {
		if(formworkflow != null && formworkflow.getCurrentStatusId() == FormStatus.CANCELLED_ID) {
			return String.format(ValidatorMessages.getString("ValidationUtil.5"), formStatusService.getFormStatus(formworkflow.getCurrentStatusId()).getDescription());
		}
		return null;
	}
}
