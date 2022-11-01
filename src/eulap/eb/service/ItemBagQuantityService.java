package eulap.eb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.dao.CashSaleReturnItemDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.PrByProductDao;
import eulap.eb.dao.PrMainProductDao;
import eulap.eb.dao.ProcessingReportDao;
import eulap.eb.dao.RTransferReceiptDao;
import eulap.eb.dao.RTransferReceiptItemDao;
import eulap.eb.dao.RriBagDiscountDao;
import eulap.eb.dao.RriBagQuantityDao;
import eulap.eb.dao.StockAdjustmentDao;
import eulap.eb.dao.StockAdjustmentItemDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.PrByProduct;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.RTransferReceiptItem;
import eulap.eb.domain.hibernate.RriBagDiscount;
import eulap.eb.domain.hibernate.RriBagQuantity;
import eulap.eb.domain.hibernate.StockAdjustment;
import eulap.eb.domain.hibernate.StockAdjustmentItem;
import eulap.eb.service.inventory.MillingItem;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.web.dto.AvblStocksAndBagsDto;

/**
 * Business logic class for {@code ItemBagQuantity}

 *
 */
@Service
public class ItemBagQuantityService {
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private StockAdjustmentDao stockAdjustmentDao;
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private CashSaleReturnDao cashSaleReturnDao;
	@Autowired
	private RTransferReceiptDao rTransferReceiptDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ProcessingReportDao processingReportDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private RriBagQuantityDao rriBagQuantityDao;
	@Autowired
	private RriBagDiscountDao rriBagDiscountDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private StockAdjustmentItemDao stockAdjustmentItemDao;
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private CashSaleReturnItemDao cashSaleReturnItemDao;
	@Autowired
	private RTransferReceiptItemDao rTransferReceiptItemDao;
	@Autowired
	private PrMainProductDao prMainProductDao;
	@Autowired
	private PrByProductDao prByProductDao;

	/**
	 * Set the item's bags quantity.
	 * @param mi The milling item. 
	 * @param refObjectId The reference object id.
	 */
	public void setItemBagQty (MillingItem mi, int refObjectId, int orTypeId) {
		ItemBagQuantity ibq =  itemBagQuantityDao.getByRefId(refObjectId, orTypeId);
		if (ibq != null) {
			mi.setItemBagQuantity(ibq.getQuantity());
		}
	}

	/**
	 * Get the Remaining Bag QTY of Account Sale Item
	 * to be used for returns.
	 * @param arTransactionId The arTransactionId
	 * @param refAcctSaleItemId The reference accountSaleItemId.
	 * @return The remaining Bag QTY.
	 */
	public Double getASIRemainingBagQty(Integer arTransactionId, Integer refAccountSaleItemId) {
		return itemBagQuantityDao.getASIRemainingBagQty(arTransactionId, refAccountSaleItemId);
	}

	/**
	 * Get the remaining Bag Qty of CAP-IS.
	 * @param capId The Customer Advance Payment Id.
	 * @param itemId The item id.
	 * @param capdId The Cap Delivery Id.
	 * @return The remaining qty available for PIAD.
	 */
	public Double getCAPISRemainingItmBagQty(Integer capId, Integer itemId, Integer capdId) {
		return itemBagQuantityDao.getCapIsRemainingBagQty(capId, itemId, capdId);
	}

	/**
	 * Get the available item bag quantity
	 * @param refObjectId The reference object id
	 * @return The list of available item bag quantities
	 */
	public Double getTotalAvailableBags(Integer refObjectId) {
		List<ItemBagQuantity> itemBagQuantities = itemBagQuantityDao.getAvailableItemBagQty(refObjectId);
		double totalAvailableBags = 0;
		for (ItemBagQuantity itemBagQuantity : itemBagQuantities) {
			totalAvailableBags += itemBagQuantity.getQuantity();
		}
		return totalAvailableBags;
	}

	/**
	 * Get the list of available bags by item id, warehouse id, and reference object id.
	 * @param itemId The item id.
	 * @param warehouseId The warehouse id.
	 * @param refBagQtyObjId The referenced eb object id.
	 * @return The list of available bags.
	 */
	public List<AvblStocksAndBagsDto> getAvailableBags(Integer companyId, Integer itemId, Integer warehouseId, Integer refBagQtyObjId, Integer itemObjectId) {
		List<AvblStocksAndBagsDto> availableBagsAndStocks =
				(List<AvblStocksAndBagsDto>) itemBagQuantityDao.getAvailableBags(companyId, itemId, warehouseId, refBagQtyObjId, itemObjectId).getData();
		return availableBagsAndStocks;
	}

	public AvblStocksAndBagsDto getRefAvbStocksandBags(Integer companyId, Integer itemId, Integer warehouseId, Integer sourceObjectId, Integer itemObjectId) {
		return getAvailableBags(companyId, itemId, warehouseId, sourceObjectId, itemObjectId).get(0);
	}

	public String getSourceLabel(EBObject ebObject) {
		String sourceLabel = "";
		switch (ebObject.getObjectTypeId()) {
		case 1 :
			APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObject.getId());
			Integer typeId = apInvoice.getInvoiceTypeId();
			if(typeId.equals(InvoiceType.RR_RAW_MAT_TYPE_ID)) {
				sourceLabel = "RR-RM IS " + apInvoice.getSequenceNumber();
			} else if(typeId.equals(InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID)) {
				sourceLabel = "RR-RM P " + apInvoice.getSequenceNumber();
			}
			break;
		case 10:
			sourceLabel = "SAI-IS " + stockAdjustmentDao.getByEbObjectId(ebObject.getId()).getSaNumber();
			break;
		case 22:
			sourceLabel = "ASR-IS " + arTransactionDao.getByEbObjectId(ebObject.getId()).getSequenceNumber();
			break;
		case 19:
			sourceLabel = "CSR-IS " + cashSaleReturnDao.getByEbObjectId(ebObject.getId()).getCsrNumber();
			break;
		case 25:
			sourceLabel = "TR-IS " + rTransferReceiptDao.getByEbObjectId(ebObject.getId()).getTrNumber();
			break;
		case 4:
			sourceLabel = "PR-MR " + processingReportDao.getByEbObjectId(ebObject.getId()).getSequenceNo();
			break;
		case 27:
			sourceLabel = "PR-MO " + processingReportDao.getByEbObjectId(ebObject.getId()).getSequenceNo();
			break;
		case 28:
			sourceLabel = "PR-PI " + processingReportDao.getByEbObjectId(ebObject.getId()).getSequenceNo();
			break;
		case 29:
			sourceLabel = "PR-PO " + processingReportDao.getByEbObjectId(ebObject.getId()).getSequenceNo();
			break;
		}
		return sourceLabel;
	}

	/**
	 * Get the proportion of Qty to be withdrawn.
	 * (Total available stocks / Total available bags) * bags to withdraw.
	 * @param companyId The id of the company.
	 * @param sourceObjectId The Eb Object Id of the source of Available stocks.
	 * @param itemId The id of the item.
	 * @param warehouseId The id of the warehouse.
	 * @param bagsToWithdrawn The qty of bags to withdraw.
	 * @return The computed qty to withdraw.
	 */
	public Double getProportionQty(Integer companyId, Integer sourceObjectId, Integer itemId,
			Integer warehouseId, Double bagsToWithdrawn, Integer origRefObjectId) {
		Double proportionedQty = null;
		if(sourceObjectId != null) {
			AvblStocksAndBagsDto avlStocksAndBags = itemBagQuantityDao.getRefAvailableBags(companyId, sourceObjectId, itemId, warehouseId);
			proportionedQty = avlStocksAndBags != null ? NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(avlStocksAndBags.getTotalStocks(), avlStocksAndBags.getTotalBags())) , bagsToWithdrawn) : 0.0;
		} else {
			if(origRefObjectId != null) {
				EBObject lineObject = ebObjectDao.get(origRefObjectId);
				boolean isASR = lineObject.getObjectTypeId() == AccountSaleItem.ACCOUNT_SALE_ITEM_OBJECT_TYPE_ID;
				EBObject sourceObject = ooLinkHelper.getReferenceObject(lineObject.getId(), isASR ? 5 : 4);
				switch (sourceObject.getObjectTypeId()) {
					case 1:
						// RR
						if(apInvoiceDao.getByEbObjectId(sourceObject.getId()).getInvoiceTypeId() == InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID) {
							List<RriBagQuantity> rriBagQuantities = rriBagQuantityDao.getRriBagQuantitiesByRefObjectId(sourceObject.getId());
							double totalQty = 0.0;
							double totalBags = 0.0;
							if(!rriBagQuantities.isEmpty()) {
								for(RriBagQuantity rriBagQuantity : rriBagQuantities) {
									totalQty += rriBagQuantity.getNetWeight();
									totalBags += rriBagQuantity.getBagQuantity();
								}
							}
							List<RriBagDiscount> rriBagDiscounts = rriBagDiscountDao.getRriDiscountsByRefObjectId(sourceObject.getId());
							double totalBagDiscounts = 0.0;
							if(!rriBagDiscounts.isEmpty()) {
								for(RriBagDiscount rriBagDiscount : rriBagDiscounts) {
									totalBagDiscounts += (NumberFormatUtil.multiplyWFP(rriBagDiscount.getBagQuantity() , rriBagDiscount.getDiscountQuantity()));
								}
							}
							double netWeight = totalQty - totalBagDiscounts;
							proportionedQty = (NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(netWeight, totalBags)) , bagsToWithdrawn));
						}
						break;
					case 10:
						// SA
						StockAdjustment stockAdjustment = stockAdjustmentDao.getByEbObjectId(sourceObject.getId());
						if(stockAdjustment != null) {
							StockAdjustmentItem stockAdjustmentItem = stockAdjustmentItemDao.getSAItems(
									stockAdjustment.getId(), itemId).get(0); // One row expected.
							setItemBagQty(stockAdjustmentItem, stockAdjustmentItem.getEbObjectId(), ItemBagQuantity.SAI_BAG_QTY);
							proportionedQty = NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(stockAdjustmentItem.getQuantity(), stockAdjustmentItem.getItemBagQuantity())), bagsToWithdrawn);
						}
						break;
					case 22:
						// ASR
						ArTransaction arTransaction = arTransactionDao.getByEbObjectId(sourceObject.getId());
						if(arTransaction != null) {
							AccountSaleItem accountSaleItem = accountSaleItemDao.getAccountSaleItems(
									arTransaction.getId(), itemId, warehouseId).get(0); // One row expected.
							setItemBagQty(accountSaleItem, accountSaleItem.getEbObjectId(), ItemBagQuantity.ASR_IS_BAG_QTY);
							proportionedQty = NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(accountSaleItem.getQuantity(), accountSaleItem.getItemBagQuantity())), bagsToWithdrawn);
						}
						break;
					case 19:
						// CSR
						CashSaleReturn cashSaleReturn = cashSaleReturnDao.getByEbObjectId(sourceObject.getId());
						if(cashSaleReturn != null) {
							CashSaleReturnItem cashSaleReturnItem = cashSaleReturnItemDao.getCashSaleReturnItems(
									cashSaleReturn.getId(), itemId, warehouseId, true).get(0); // One row expected.
							setItemBagQty(cashSaleReturnItem, cashSaleReturnItem.getEbObjectId(), ItemBagQuantity.CSR_IS_BAG_QTY);
							proportionedQty = NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(cashSaleReturnItem.getQuantity() , cashSaleReturnItem.getItemBagQuantity())), bagsToWithdrawn);
						}
						break;
					case 25:
						// TR
						RTransferReceipt transferReceipt = rTransferReceiptDao.getByEbObjectId(sourceObject.getId());
						if(transferReceipt != null) {
							RTransferReceiptItem trItem = rTransferReceiptItemDao.getRTrItems(
									transferReceipt.getId(), itemId).get(0); // One row expected.
							setItemBagQty(trItem, trItem.getEbObjectId(), ItemBagQuantity.TR_IS_BAG_QTY);
							proportionedQty = NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(trItem.getQuantity(), trItem.getItemBagQuantity())), bagsToWithdrawn);
						}
						break;
					default:
						ProcessingReport processingReport = processingReportDao.getByEbObjectId(sourceObject.getId());
						if(processingReport != null) {
							PrMainProduct prMainProduct = prMainProductDao.getRefObjectId(processingReport.getEbObjectId(), itemId);
							if(prMainProduct != null) {
								setItemBagQty(prMainProduct, prMainProduct.getEbObjectId(), ItemBagQuantity.PR_MAIN_PRODUCT_BAG_QTY);
								proportionedQty = NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(prMainProduct.getQuantity() , prMainProduct.getItemBagQuantity())), bagsToWithdrawn);
							}

							PrByProduct prByProduct = prByProductDao.getByRefObjectId(processingReport.getEbObjectId(), itemId);
							if(prByProduct != null) {
								proportionedQty = NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(prByProduct.getQuantity(), prByProduct.getItemBagQuantity())), bagsToWithdrawn);
							}
						}
						break;
				}
			}
		}
		return proportionedQty != null ? proportionedQty : 0.0;
	}
}
