package eulap.eb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eulap.common.util.FileUtil;
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.web.dto.CSItemDto;

/**
 * Class that handles the business logic of {@link CashSaleItem}

 *
 */
@Service
public class CashSaleItemService {
	private static Logger logger = Logger.getLogger(CashSaleService.class);
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemSrpService itemSrpService;
	@Autowired
	private UnitMeasurementDao unitMeasurementDao;

	/**
	 * Get all the cash sales items.
	 * @return The list of cash sales items.
	 */
	public List<CashSaleItem> getCashSaleItems() {
		return (List<CashSaleItem>) cashSaleItemDao.getAll();
	}

	/**
	 * Get all the cash sales item.
	 * @param cashSaleId The id of the cash sale.
	 * @return The list of cash sale items.
	 */
	public List<CashSaleItem> getAllCashSaleItems(Integer cashSaleId) {
		return getAllCashSaleItems(cashSaleId, true);
	}

	/**
	 * Get all of the cash sale item line. 
	 * @param cashSaleId the cash sale id.
	 */
	public List<CashSaleItem> getAllCashSaleItems(Integer cashSaleId, boolean isSummariseItems) {
		List<CashSaleItem> items = cashSaleItemDao.getCashSaleItems(cashSaleId, null, null);
		for (CashSaleItem item : items) {
			item.setOrigQty(item.getQuantity());
		}

		if(!isSummariseItems) {
			return items;
		}
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		return saleItemUtil.getSummarisedSaleItems(items);
	}
	/**
	 * Process the cash sale items.
	 * @param cashSaleItems The list of cash sale items.
	 * @return The process cash sale items.
	 */
	public List<CashSaleItem> processCashSaleItems(List<CashSaleItem> cashSaleItems) {
		List<CashSaleItem> ret = new ArrayList<CashSaleItem>();
		if (cashSaleItems != null && !cashSaleItems.isEmpty()) {
			for (CashSaleItem csItem : cashSaleItems) {
				String stockCode = csItem.getStockCode();
				Double quantity = csItem.getQuantity();
				boolean hasStockCode = stockCode != null && !stockCode.isEmpty();
				boolean hasQty = quantity != null && quantity != 0.0;
				if (hasStockCode || hasQty) 
					ret.add(csItem);
			}			
		}
		return ret;
	}

	/**
	 * Checks if the cash sale item has an invalid item.
	 * @param cashSaleItems The list of cash sale items.
	 * @return True if all stock codes are valid, otherwise false.
	 */
	public boolean hasInvalidItem (List<CashSaleItem> cashSaleItems) {
		for (CashSaleItem csItem : cashSaleItems) {
			if (csItem.getItemId() == null)
				return true;
		}
		return false;
	}

	/**
	 * Checks if the cash sale item has no warehouse.
	 * @param cashSaleItems The list of cash sale items.
	 * @return True if all cash sale items has warehouse, otherwise false.
	 */
	public boolean hasNoWarehouse (List<CashSaleItem> cashSaleItems) {
		for (CashSaleItem csItem : cashSaleItems) {
			if (csItem.getWarehouseId() == null)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the cash sale item has negative quantity.
	 * @param cashSaleItems The list of cash sale items.
	 * @return True if all cash sale items has no amount, otherwise false.
	 */
	public boolean hasNegQty (List<CashSaleItem> cashSaleItems) {
		for (CashSaleItem csItem : cashSaleItems) {
			if (csItem.getQuantity() != null) {
				if (csItem.getQuantity() <= 0.0)
					return true;
			}
		}
		return false;
	}

	public List<CashSaleItem> getCSItemsByWarehouse(int cashSaleId) {
		List<CashSaleItem> csItems = cashSaleItemDao.getCashSaleItems(cashSaleId, null, null);
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		return saleItemUtil.processSaleItemsForViewing(csItems);
	}

	/**
	 * Get the list of cash sales items per warehouse.
	 * @param cashSaleId The cash sales id.
	 * @param warehouseId The warehouse id.
	 * @return The list of cash sales items by warehouse.
	 */
	public List<CashSaleItem> getCSItemsByWarehouse(int cashSaleId, Integer warehouseId) {
		List<CashSaleItem> csItems = cashSaleItemDao.getCashSaleItems(cashSaleId, null, warehouseId);
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		return saleItemUtil.processSaleItemsForViewing(csItems);
	}

	/**
	 * Get the total cash sale amount of all cash sale items per 
	 * cash sale.
	 * @param cashSaleId The cash sale id.
	 * @return The cash sale amount.
	 */
	public double getTotalCSAmount (Integer cashSaleId) {
		return cashSaleItemDao.getTotalCSAmount(cashSaleId);
	}

	/**
	 * Remove duplicate cash sale items.
	 * @param cashSaleItems The cash sales items.
	 */
	public void removeDuplicateCSItems (int cashSaleId, List<CashSaleItem> cashSaleItems) {
		Set<Integer> toBeDeleted = new HashSet<Integer>();
		
		List<CSItemDto> csItemDtos = cashSaleItemDao.getCsItemDtos(cashSaleId);
		if (csItemDtos != null) {
			for (CSItemDto distinctItem : csItemDtos) {
				int cnt = 0;
				for (CashSaleItem csi : cashSaleItems) {
					boolean isSameItem = csi.getItemId().equals(distinctItem.getItemId());
					boolean isSameWarehouse = csi.getWarehouseId().equals(distinctItem.getWarehouseId());
					boolean isSameQuantity = csi.getQuantity().equals(distinctItem.getQuantity());
					boolean isSameUnitCost = csi.getUnitCost() == null && distinctItem.getUnitCost() == null;
					if (csi.getUnitCost() != null && distinctItem.getUnitCost() != null) {
						if (csi.getUnitCost().equals(distinctItem.getUnitCost()))
							isSameUnitCost = true;
					}
					if (isSameItem && isSameWarehouse && isSameQuantity && isSameUnitCost) {
						cnt++;
						if (cnt > 1) {
							toBeDeleted.add(csi.getId());
						}
					}
				}
			}
		}

		if (!toBeDeleted.isEmpty()) {
			cashSaleItemDao.delete(toBeDeleted);
			logger.info("Done deleting " + toBeDeleted.size() + " duplicates.");
		}
		toBeDeleted = null;
		cashSaleItems = null;
	}

	/**
	 * Parse and convert to cash sale items the file.
	 * @param cashSale Cash sale object.
	 * @param companyId The company id.
	 * @param warehouseId The warehouse id.
	 * @param mpf The MultipartFile.
	 * @return List of cash sale items.
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<CashSaleItem> initConsolidatedCSItems (CashSale cashSale, int companyId, int warehouseId, MultipartFile mpf) 
			throws IOException, ParseException {
		List<CashSaleItem> csItems = new ArrayList<>();
		InputStream in = FileUtil.convertMpf2InputStream(mpf);
		BufferedReader reader = null;
		CashSaleItem currentCsi = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			int cnt = 0;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) { // Ignore blank lines.
					continue;
				}
				if (line.contains("%")) {
					String stockCode = "";
					String strScArr[] = line.split("\\s+");
					if (strScArr.length > 0) {
						for (int i=0; i<strScArr.length; i++) {
							String tmpStr = strScArr[i].trim();
							if (tmpStr.isEmpty() || tmpStr.contains("%")) {
								continue;
							}
							stockCode += " " + strScArr[i];
						}
					}
					currentCsi = new CashSaleItem();
					currentCsi.setStockCode(stockCode);
					cnt++;
				} else {
					if (cnt == 1) { // Ignore the item description.
						cnt++;
					} else if (cnt == 2 && currentCsi != null) { // Get the quantity and amount.
						String strQtyAmountArr[] = line.split("\\s+");
						double quantity = Double.parseDouble(strQtyAmountArr[1]);
						if (quantity > 0) {
							double amount = convertToDouble(strQtyAmountArr[2]);
							currentCsi.setQuantity(quantity);
							currentCsi.setAmount(amount);
							handleCsItem(cashSale, companyId, warehouseId, currentCsi);
							csItems.add(currentCsi);
						}
						currentCsi = null;
						cnt = 0;
					}
				}
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
		return csItems;
	}

	private double convertToDouble (String strValue) throws ParseException {
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		Number number = format.parse(strValue);
		return number.doubleValue();
	}

	private void handleCsItem(CashSale cashSale, Integer companyId, Integer warehouseId, CashSaleItem csi) {
		String strItemMsg = cashSale.getStrItemMsg();
		String strSrpMsg = cashSale.getStrSrpMsg();
		Item item = itemDao.getItemByStockCode(csi.getStockCode(), null);
		if (item == null) {
			if (strItemMsg == null) {
				strItemMsg = "There is/are no item/s encoded in the database for stock code/s:";
			}
			strItemMsg += "<br>" + csi.getStockCode();
			cashSale.setStrItemMsg(strItemMsg);
		} else {
			int itemId = item.getId();
			csi.setItemId(itemId);
			ItemSrp itemSrp = itemSrpService.getLatestItemSrp(companyId, itemId, null);
			if (itemSrp == null) {
				if (strSrpMsg == null) {
					strSrpMsg = "There is/are no srp/s encoded in the database for stock code/s:";
				}
				strSrpMsg += "<br>" + csi.getStockCode();
				cashSale.setStrSrpMsg(strSrpMsg);
			}
			Double existingStocks = itemDao.getItemExistingStocks(itemId, warehouseId, new Date());
			item.setExistingStocks(existingStocks);
			csi.setExistingStocks(existingStocks);
			if (itemSrp != null) {
				csi.setItemSrpId(itemSrp.getId());
			}
			csi.setItemSrp(itemSrp);
			csi.setItem(item);
			double itemSrpAmt = csi.getItemSrp() == null ? 0 : csi.getItemSrp().getSrp();
			double actualSellingPrice = csi.getAmount() / csi.getQuantity();
			csi.setWarehouseId(warehouseId);
			csi.setSrp(itemSrpAmt);
			if (actualSellingPrice >= itemSrpAmt) { // AddOn
				csi.setAddOn(actualSellingPrice - itemSrpAmt);
			} else if (actualSellingPrice < itemSrpAmt) { // Discount
				csi.setDiscount(csi.getQuantity() * (itemSrpAmt - actualSellingPrice));
			}
		}
	}

	/**
	 * Get the cash sale items for Consolidated POS viewing.
	 * @param cashSaleId The cash sale id.
	 * @return The list of cash sale items.
	 */
	public List<CashSaleItem> getCPosCSItems (int cashSaleId) {
		List<CashSaleItem> cashSaleItems = 
				cashSaleItemDao.getAllByRefId("cashSaleId", cashSaleId);
		if (cashSaleItems != null && !cashSaleItems.isEmpty()) {
			for (CashSaleItem csi : cashSaleItems) {
				Item item = itemDao.get(csi.getItemId());
				UnitMeasurement um = unitMeasurementDao.get(item.getUnitMeasurementId());
				item.setUnitMeasurement(um);
				csi.setItem(item);
				Double existingStocks = itemDao.getItemExistingStocks(csi.getItemId(), csi.getWarehouseId(), new Date());
				csi.setExistingStocks(existingStocks);

				if (csi.getItemSrpId() != null) {
					ItemSrp itemSrp = itemSrpService.getItemSrp(csi.getItemSrpId());
					double addOn = csi.getSrp() - itemSrp.getSrp();
					csi.setSrp(itemSrp.getSrp());
					csi.setAddOn(addOn);
				}
			}
		}
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<CashSaleItem>();
		return saleItemUtil.processSaleItemsForViewing(cashSaleItems);
	}

	/**
	 * Get the list of Cash Sale Items by cashSaleId
	 * @param cashSaleId The cash sale id.
	 * @return The list of Cash Sale Items.
	 */
	public List<CashSaleItem> getCSItems(Integer cashSaleId){
		return cashSaleItemDao.getCashSaleItems(cashSaleId, null, null);
	}
}
