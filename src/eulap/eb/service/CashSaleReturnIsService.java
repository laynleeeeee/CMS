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
import eulap.eb.dao.CashSaleItemDao;
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.dao.CashSaleReturnItemDao;
import eulap.eb.dao.ItemBagQuantityDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.CashSaleItem;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ItemBagQuantity;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;


/**
 * Business logic for Cash Sales Return - Individual Selection module.

 *
 */
@Service
public class CashSaleReturnIsService extends BaseWorkflowService {
	private static Logger logger = Logger.getLogger(CashSaleReturnIsService.class);
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private CashSaleReturnDao cashSaleReturnDao;
	@Autowired
	private CashSaleReturnItemDao cashSaleReturnItemDao;
	@Autowired
	private CashSaleItemDao cashSaleItemDao;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired
	private ItemService itemService;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ItemBagQuantityDao itemBagQuantityDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private ItemBagQuantityService itemBagQuantityService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;

	/**
	 * Get the list of CSR Items and set the stock code of the selected item.
	 * @param cashSaleReturnId The unique id of the cash sale return.
	 * @return The list of {@link CashSaleReturnItem}
	 */
	public List<CashSaleReturnItem> getCsrItems(int cashSaleReturnId) {
		List<CashSaleReturnItem> csrItems = cashSaleReturnItemDao.getAllByRefId("cashSaleReturnId", cashSaleReturnId);
		CashSaleItem refCsItem = null;
		EBObject ebObject = null;
		double srp = 0;
		double addOn = 0;
		for (CashSaleReturnItem csri : csrItems) {
			csri.setStockCode(csri.getItem().getStockCode());
			csri.setOrigQty(csri.getQuantity());

			Integer csItemId = csri.getCashSaleItemId();
			if(csItemId != null) {
				refCsItem = cashSaleItemDao.get(csItemId);
				itemBagQuantityService.setItemBagQty(refCsItem, refCsItem.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
				csri.setOrigBagQty(refCsItem.getItemBagQuantity());
				csri.setOrigRefObjectId(refCsItem.getEbObjectId());
				csri.setReferenceObjectId(refCsItem.getEbObjectId());
				csri.setSalesRefId(csItemId);
				csri.setRefQuantity(refCsItem.getQuantity());
				srp = refCsItem.getSrp();
				addOn = refCsItem.getItemAddOn() != null ? refCsItem.getItemAddOn().getValue() : 0;
				itemBagQuantityService.setItemBagQty(csri, csri.getEbObjectId(), ItemBagQuantity.CS_IS_BAG_QTY);
			} else {
				ebObject = ooLinkHelper.getReferenceObject(csri.getEbObjectId(), 6);
				csri.setReferenceObjectId(ebObject.getId());
				csri.setOrigRefObjectId(ebObject.getId());
				srp = csri.getSrp();
				addOn = csri.getItemAddOn() != null ? csri.getItemAddOn().getValue() : 0;
				itemBagQuantityService.setItemBagQty(csri, csri.getEbObjectId(), ItemBagQuantity.CSR_IS_BAG_QTY);
			}
			csri.setOrigSrp(srp - addOn);
		}
		return csrItems;
	}

	/**
	 * Get the remaining quantity of the reference Cash sale item.
	 * @param cashSaleItemId The id of the cash sale item.
	 * @return The remaining quantity.
	 */
	public double getRemainingQty(int cashSaleItemId, boolean isCSAsReference) {
		return cashSaleReturnItemDao.getRemainingQty(cashSaleItemId, isCSAsReference);
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		CashSaleReturn csr = (CashSaleReturn) form;

		//Re-set values from the reference Cash Sale Item.
		CashSaleItem refCsItem = null;
		for (CashSaleReturnItem csri : csr.getCashSaleReturnItems()) {
			if(csri.getCashSaleItemId() != null) {
				refCsItem = cashSaleItemDao.get(csri.getCashSaleItemId());
				csri.setSrp(refCsItem.getSrp());
				csri.setUnitCost(refCsItem.getUnitCost());
				csri.setItemSrpId(refCsItem.getItemSrpId());
				csri.setItemAddOnId(refCsItem.getItemAddOnId());
				csri.setItemDiscountId(refCsItem.getItemDiscountId());
			}
		}

		SaleItemUtil<CashSaleReturnItem> saleItemUtil = new SaleItemUtil<>();
		List<CashSaleReturnItem> processedCsrItems =
				saleItemUtil.processDiscountAndAmount(csr.getCashSaleReturnItems(), itemDiscountService);
		csr.setCashSaleReturnItems(processedCsrItems);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		logger.info("Saving the Cash Sale Return for Individual Selection.");
		CashSaleReturn csr = (CashSaleReturn) form;
		boolean isNew = csr.getId() == 0;
		AuditUtil.addAudit(csr, new Audit(user.getId(), isNew, new Date()));
		if(isNew) {
			int csrNumber = cashSaleReturnDao.generateCsrNumber(csr.getCompanyId(), csr.getCashSaleTypeId());
			logger.debug("Generated CSR Number: "+csrNumber+" for type: "+csr.getCashSaleTypeId());
			csr.setCsrNumber(csrNumber);
		}
		cashSaleReturnService.processAndSaveCsr(csr, isNew);

		//Save the CSR Items
		Date currentDate = new Date();
		int userId = user.getId();
		List<Domain> toBeSavedIbqs = new ArrayList<>();
		List<Domain> oos = new ArrayList<>();
		logger.debug("Saving the list of CSR Items.");
		for (CashSaleReturnItem csri : csr.getCashSaleReturnItems()) {
			csri.setCashSaleReturnId(csr.getId());
			if (csri.getItemBagQuantity() != null) {
				int ibqObjectId = ebObjectService.saveAndGetEbObjectId(userId, ItemBagQuantity.OBJECT_TYPE_ID, currentDate);
				ItemBagQuantity ibq = ItemBagQuantity.getInstanceOf(csri.getItemId(), ibqObjectId, csri.getItemBagQuantity());
				ibq.setCreatedBy(userId);
				AuditUtil.addAudit(ibq, new Audit(userId, isNew, currentDate));
				toBeSavedIbqs.add(ibq);
				oos.add(ObjectToObject.getInstanceOf(csri.getEbObjectId(),
						ibqObjectId, ItemBagQuantity.CSR_IS_BAG_QTY, user, currentDate));
			}
			SaleItemUtil.setNullUnitCostToZero(csri);
			cashSaleReturnItemDao.save(csri);
		}
		if (!toBeSavedIbqs.isEmpty()) {
			itemBagQuantityDao.batchSave(toBeSavedIbqs);
		}
		if (!oos.isEmpty()) {
			objectToObjectDao.batchSave(oos);
		}
		logger.info("Successfully saved the CSR with sequence no.: "+csr.getFormattedCSRNumber());
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return cashSaleReturnDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return cashSaleReturnService.getFormWorkflow(id);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		CashSaleReturn csr = cashSaleReturnDao.getByWorkflowId(
				currentWorkflowLog.getFormWorkflowId());
		if(currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID
				&& csr.getFormWorkflow().isComplete()) {
			//Check returned if used as reference
			List<CashSaleReturnItem> csrItems =
					cashSaleReturnItemDao.getAllByRefId("cashSaleReturnId", csr.getId());
			List<CashSaleReturnItem> returnedItems = SaleItemUtil.filterSaleReturnItems(csrItems, true);
			String errorMessage = ValidationUtil.validateToBeCancelledRefForm(itemService, returnedItems);
			if(errorMessage != null) {
				bindingResult.reject("workflowMessage", errorMessage);
				currentWorkflowLog.setWorkflowMessage(errorMessage);
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return cashSaleReturnDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		CashSaleReturn csr = cashSaleReturnDao.getByEbObjectId(ebObjectId);
		int pId = csr.getId();
		Integer typeId = csr.getCashSaleTypeId();
		FormProperty property = workflowHandler.getProperty(csr.getWorkflowName(), user);
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String popupLink = "/cashSaleReturn/"+typeId+"/form?pId=" + pId;
		String formName = "Cash Sale Return - IS";
		String latestStatus = csr.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName + " - " + csr.getCsrNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + csr.getArCustomer().getName())
				.append(" " + csr.getArCustomerAccount().getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(csr.getDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case CashSaleReturn.CSR_IS_OBJECT_TYPE_ID:
			return cashSaleReturnDao.getByEbObjectId(ebObjectId);
		case CashSaleReturnItem.RETURN_OBJECT_TYPE_ID:
		case CashSaleReturnItem.EXCHANGE_OBJECT_TYPE_ID:
			return cashSaleReturnItemDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
