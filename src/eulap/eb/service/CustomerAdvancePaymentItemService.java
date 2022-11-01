package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.CustomerAdvancePaymentItemDao;
import eulap.eb.dao.ItemDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.SalesOrderItemDao;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.SalesOrderItem;
import eulap.eb.service.oo.OOLinkHelper;

/**
 * Class that handles the business logic of {@link CustomerAdvancePaymentItem}

 *
 */
@Service
public class CustomerAdvancePaymentItemService {
	private static Logger logger = Logger.getLogger(CustomerAdvancePaymentItemService.class);
	@Autowired
	private CustomerAdvancePaymentItemDao capItemDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private ObjectToObjectDao o2oDao;
	@Autowired
	private SalesOrderItemDao soItemDao;

	/**
	 * Get the list of Customer Advance Payment Items for Individual Selection type.
	 * @param customerAdvancePaymentId The unique ID of the CAP.
	 * @return List of {@link CustomerAdvancePaymentItem}
	 */
	public List<CustomerAdvancePaymentItem> getCapIsItems(int customerAdvancePaymentId) {
		List<CustomerAdvancePaymentItem> savedCapItems =
			capItemDao.getAllByRefId(CustomerAdvancePaymentItem.FIELD.customerAdvancePaymentId.name(),
					customerAdvancePaymentId);
		List<CustomerAdvancePaymentItem> capItems = new ArrayList<CustomerAdvancePaymentItem>();
		EBObject ebObject = null;
		for (CustomerAdvancePaymentItem capi : savedCapItems) {
			double origSrp = capi.getItemSrp().getSrp();
			capi.setOrigSrp(origSrp);
			capi.setOrigQty(capi.getQuantity());
			capi.setStockCode(capi.getItem().getStockCode());
			ebObject = ooLinkHelper.getReferenceObject(capi.getEbObjectId(), 11);
			capi.setOrigRefObjectId(ebObject.getId());
			capi.setRefenceObjectId(ebObject.getId());
			capItems.add(capi);
		}
		return capItems;
	}

	/**
	 * Get the list of customer advance payment items by id. This will merge the
	 * splitted item line.
	 * @param capId The customer advance payment id.
	 * @param itemId The item id.
	 * @param warehouseId The ware house id.
	 * @return List of customer advance payment items.
	 */
	public List<CustomerAdvancePaymentItem> getCAPItems (Integer capId, Integer itemId,
			Integer warehouseId, boolean hasSOReference) {
		SaleItemUtil<CustomerAdvancePaymentItem> saleItemUtil = 
				new SaleItemUtil<CustomerAdvancePaymentItem>();
		List<CustomerAdvancePaymentItem> capItems = capItemDao.getCAPItems(capId, itemId, warehouseId);
		capItems = saleItemUtil.processSaleItemsForViewing(capItems);
		for (CustomerAdvancePaymentItem capItem : capItems) {
			capItem.setStockCode(capItem.getItem().getStockCode());
			if(hasSOReference) {
				EBObject refSQIObjId = o2oDao.getOtherReference(
						capItem.getEbObjectId(), CustomerAdvancePaymentItem.CAPI_SOI_RELATIONSHIP);
				if(refSQIObjId != null) {
					SalesOrderItem soi = soItemDao.getByEbObjectId(refSQIObjId.getId());
					capItem.setRefenceObjectId(soi.getEbObjectId());
				}
			}
		}
		return capItems;
	}

	/**
	 * Get the list of CAP items by cash sales id.
	 * @param capId The CAP id.
	 * @return The list of CAP items.
	 */
	public List<CustomerAdvancePaymentItem> getCAPItemsPrintOut(Integer capId) {
		logger.info("Generating printout data for customer advance payment item");
		SaleItemUtil<CustomerAdvancePaymentItem> saleItemUtil = new SaleItemUtil<CustomerAdvancePaymentItem>();
		List<CustomerAdvancePaymentItem> capItems = saleItemUtil.getSummarisedSaleItems(capItemDao.getCAPItems(capId, null, null));
		if (capItems != null && !capItems.isEmpty()) {
			for (CustomerAdvancePaymentItem capi : capItems) {
				itemBagQuantityService.setItemBagQty(capi, capi.getEbObjectId(), ItemBagQuantity.CAP_IS_BAG_QTY);
				double quantity = capi.getQuantity() != null ? capi.getQuantity() : 0;
				double srp = capi.getSrp() != null ? capi.getSrp() : 0;
				capi.setGrossAmount(quantity * srp);
				capi.setNetAmount(capi.getAmount());
			}
		}
		return capItems;
	}

}
