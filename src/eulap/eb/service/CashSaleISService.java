package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.eb.dao.CashSaleArLineDao;
import eulap.eb.dao.CashSaleDao;
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Class that handles business logic of {@link CashSale}

 *
 */
@Service
public class CashSaleISService extends BaseWorkflowService{
	private static Logger logger = Logger.getLogger(CashSaleISService.class);
	@Autowired
	private CashSaleDao cashSaleDao;
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private CashSaleItemService csItemService;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private CashSaleArLineDao cashSaleArLineDao;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return cashSaleDao.get(id).getFormWorkflow();
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		CashSale cashSale = (CashSale) form;
		SaleItemUtil<CashSaleItem> saleItemUtil = new SaleItemUtil<>();
		List<CashSaleItem> processedCsItems =
				saleItemUtil.processDiscountAndAmount(cashSale.getCashSaleItems(), itemDiscountService);
		cashSale.setCashSaleItems(processedCsItems);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		CashSale cashSale = (CashSale) form;
		boolean isNew = cashSale.getId() == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(cashSale, new Audit(user.getId(), isNew, currentDate));
		if (isNew) {			
			int csNumber = cashSaleDao.generateCsNumber(cashSale.getCompanyId(), cashSale.getCashSaleTypeId());
			logger.info("Generated CS Number: "+csNumber+" for type: "+cashSale.getCashSaleTypeId());
			cashSale.setCsNumber(csNumber);
		} else {
			CashSale savedCS = cashSaleService.getCashSale(cashSale.getId());
			DateUtil.setCreatedDate(cashSale, savedCS.getCreatedDate());
			// Get the CS Items
			List<CashSaleItem> savedCSItems = cashSaleService.getCSItems(cashSale.getId());
			if (!savedCSItems.isEmpty()) {
				List<Integer> toBeDeletedCSItems = new ArrayList<Integer>();
				for (CashSaleItem csItem : savedCSItems) {
					ItemBagQuantity ibq =  itemBagQuantityDao.getByRefId(csItem.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
					if (ibq != null) {
						ibq.setActive(false);
						itemBagQuantityDao.update(ibq);
					}
					toBeDeletedCSItems.add(csItem.getId());
				}
				cashSaleItemDao.delete(toBeDeletedCSItems);
				toBeDeletedCSItems = null;
			}
		}

		cashSaleDao.saveOrUpdate(cashSale);
		List<Domain> toBeSavedIbqs = new ArrayList<>();
		List<CashSaleItem> csItems = cashSale.getCashSaleItems();
		List<Domain> o2os = new ArrayList<>();
		for (CashSaleItem csItem : csItems) {
			csItem.setCashSaleId(cashSale.getId());
			SaleItemUtil.setNullUnitCostToZero(csItem);
			cashSaleItemDao.save(csItem);
			if(csItem.getItemBagQuantity() != null) {
				int ibqObjectId = ebObjectService.saveAndGetEbObjectId(user.getId(), ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
				ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(csItem.getItemId(), ibqObjectId, csItem.getItemBagQuantity());
				ibq.setCreatedBy(user.getId());
				AuditUtil.addAudit(ibq, new Audit(user.getId(), isNew, currentDate));
				toBeSavedIbqs.add(ibq);
				o2os.add(ObjectToObject.getInstanceOf(csItem.getEbObjectId(),
						ibqObjectId, ItemBagQuantity.CS_IS_BAG_QTY, user, currentDate));
			}
			
		}
		if (!toBeSavedIbqs.isEmpty()) {
			itemBagQuantityDao.batchSave(toBeSavedIbqs);
		}
		if (!o2os.isEmpty()) {
			objectToObjectDao.batchSave(o2os);
		}

		cashSaleService.saveOtherCharges(cashSale.getCashSaleArLines(), cashSale.getId(), isNew);
	}

	/**
	 * Get the list of cashSale sale items by the id of the parent object.
	 * @param cashSaleId The id of the parent object.
	 * @return The list of cashSale sales items.
	 */
	public CashSale getCashSaleISWithItems(Integer cashSaleId) {
		CashSale cashSale = cashSaleService.getCashSale(cashSaleId);
		List<CashSaleItem> cashSaleItems = csItemService.getAllCashSaleItems(cashSaleId, false);
		for (CashSaleItem csi : cashSaleItems) {
			addCSIReference(csi);
		}
		cashSale.setCashSaleItems(cashSaleItems);
		cashSale.setCashSaleArLines(cashSaleService.getDetailedArLines(cashSaleId));
		return cashSale;
	}

	private void addCSIReference(CashSaleItem csi) {
		itemBagQuantityService.setItemBagQty(csi, csi.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
		EBObject otherReference = objectToObjectDao.getOtherReference(csi.getEbObjectId(), CashSaleItem.CSI_OR_TYPE_ID);
		if (otherReference == null) {
			throw new RuntimeException("There was an error occured previously when saving object to object reference for cash sale.");
		}
		csi.setReferenceObjectId(otherReference.getId());
		csi.setStockCode(csi.getItem().getStockCode());
	}

	/**
	 * Get the cash sale item with the needed reference objects.
	 * @param csItemId The id of the cash sale item object.
	 * @return The cash sale item.
	 */
	public CashSaleItem getCSItemWithReference(Integer csItemId) {
		CashSaleItem csi = cashSaleItemDao.get(csItemId);
		addCSIReference(csi);
		return csi;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		cashSaleService.validateFormUsedAsReference(currentWorkflowLog, bindingResult);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return cashSaleDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		CashSale cashSale = cashSaleDao.getByEbObjectId(ebObjectId);
		int pId = cashSale.getId();
		Integer typeId = cashSale.getCashSaleTypeId();
		String uri = typeId != CashSaleType.PROCESSING ? "/cashSale/"+typeId : "/csProcessing/";
		String popupLink = uri+"/form?pId=" + pId;

		FormProperty property = workflowHandler.getProperty(cashSale.getWorkflowName(), user);
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String formName = "Cash Sale - IS";
		String latestStatus = cashSale.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + cashSale.getCsNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + cashSale.getArCustomer().getName())
				.append(" " + cashSale.getArCustomerAccount().getName())
				.append("<b> RECEIPT DATE : </b>" + DateUtil.formatDate(cashSale.getReceiptDate()))
				.append("<b> MATURITY DATE : </b>" + DateUtil.formatDate(cashSale.getMaturityDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case CashSale.CS_IS_OBJECT_TYPE_ID:
			return cashSaleDao.getByEbObjectId(ebObjectId);
		case CashSaleItem.OBJECT_TYPE_ID:
			return cashSaleItemDao.getByEbObjectId(ebObjectId);
		case 17:
			return cashSaleArLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
