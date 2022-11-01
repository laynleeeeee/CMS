package eulap.eb.service.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ObjectTypeDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.BaseItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ObjectType;
import eulap.eb.service.ItemService;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.workflow.InventoryWorkflowService;
import eulap.eb.web.dto.ItemTransactionHistory;

/**
 * A class that handle the re-allocation of COGS(Cost Of Goods Sold) for antedate
 * and cancelled transaction.
 * 
 * This will get the future withdrawal transaction from the form date and will reallocate
 * the COGS of the transaction. 
 * 

 *
 */
public class ItemCOGSReallocator {
	private static final int PAGE_SIZE = 1000;
	private static Logger logger = Logger.getLogger(ItemCOGSReallocator.class);
	private ApplicationContext applicationContext;
	private ItemService itemService;
	private EBObjectDao ebObjectDao;
	private ObjectTypeDao objectTypeDao;
	private OOLinkHelper ooLinkHelper;
	private Map<Integer, InventoryWorkflowService> objectType2Service = new HashMap<>();

	public ItemCOGSReallocator (ApplicationContext applicationContext, OOLinkHelper ooLinkHelper) {
		this.applicationContext = applicationContext;
		try {
			itemService = getInstance(ItemService.class.getName());
			ebObjectDao = getInstance(EBObjectDao.class.getName());
			objectTypeDao = getInstance(ObjectTypeDao.class.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T getInstance (String clazzName) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(clazzName);
		Object object = applicationContext.getBean(clazz);
		return (T) object;
	}

	public void reAllocateItem (InventoryWorkflowService ws, Integer workflowId) throws ClassNotFoundException, CloneNotSupportedException {
		BaseFormWorkflow form = ws.getFormByWorkflow(workflowId);
		List<? extends Domain> items = ws.getItems(form);
		if (items != null) {
			for (Domain itemLine : items) {
				Date date = form.getGLDate();
				Integer companyId = form.getCompanyId();
				List<Integer> assocItems = ws.getItems(form, itemLine);
				List<Integer> warehouses = ws.getWarehouses(form, itemLine);
				for (Integer itemId : assocItems) {
					for (Integer warehouseId : warehouses) {
						// Allocator of the GOCS.
						reallocate(companyId, warehouseId, itemId, date);
					}
				}
			}
		}
	}

	private void reallocate (Integer companyId, Integer warehouseId, Integer itemId, Date date)
			throws ClassNotFoundException, CloneNotSupportedException {

		WeightedAverageAllocator<InventoryItem> allocator
			= new WeightedAverageAllocator<>(itemService, itemId, companyId, warehouseId, date);

		PageSetting ps = new PageSetting(1, PAGE_SIZE);
		// Get the history of the item in the future of this transaction.
		Page<ItemTransactionHistory> futureTransactions = 
				itemService.getFutureWTHistory(companyId, warehouseId, itemId, date, ps);
		int currentPage = ps.getPageNumber();
		logger.info("Recomputing "+futureTransactions.getTotalRecords()+" transactions for item id: "+itemId);
		List<Domain> toBeSaved = new ArrayList<>();
		//TODO: Handle the proper paging..
		//while(currentPage <= futureTransactions.getLastPage()) {
		logger.debug("Re-allocate item transactions on page: "+currentPage);
		if(currentPage > 1) {
			futureTransactions = itemService.getFutureWTHistory(companyId, warehouseId, itemId, date, new PageSetting(currentPage, PAGE_SIZE));
		}

		for (ItemTransactionHistory ith : futureTransactions.getData()) {
			EBObject ebObj = ebObjectDao.get(ith.getEbObjectId());
			Integer objectTypeId = ebObj.getObjectTypeId();
			InventoryWorkflowService service = objectType2Service.get(objectTypeId);
			if (service == null) {
				ObjectType ot = objectTypeDao.get(objectTypeId);
				service = getInstance(ot.getServiceClass());
				objectType2Service.put(ot.getId(), service);
			}
			InventoryItem itemTransaction = service.getItemTransaction(ebObj.getId());
			allocator.allocateCost(itemTransaction);
			toBeSaved.add(itemTransaction);
		}
	//		currentPage++;
	//	}
		ebObjectDao.batchSaveOrUpdate(toBeSaved);
	}
}
