package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.dao.Dao;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.dao.ItemDiscountTypeDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.SalesQuotationDao;
import eulap.eb.dao.SalesQuotationEquipmentLineDao;
import eulap.eb.dao.SalesQuotationItemDao;
import eulap.eb.dao.SalesQuotationLineDao;
import eulap.eb.dao.SalesQuotationTruckingLineDao;
import eulap.eb.dao.UnitMeasurementDao;
import eulap.eb.domain.hibernate.AROtherCharge;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArLineSetup;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.domain.hibernate.SalesQuotationEquipmentLine;
import eulap.eb.domain.hibernate.SalesQuotationItem;
import eulap.eb.domain.hibernate.SalesQuotationLine;
import eulap.eb.domain.hibernate.SalesQuotationTruckingLine;
import eulap.eb.domain.hibernate.UnitMeasurement;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * A service class that will handle business logic for {@link SalesQuotation}

 */

@Service
public class SalesQuotationService extends BaseWorkflowService {
	@Autowired
	private SalesQuotationDao salesQuotationDao;
	@Autowired
	private SalesQuotationItemDao salesQuotationItemDao;
	@Autowired
	private SalesQuotationLineDao salesQuotationLineDao;
	@Autowired
	private SalesQuotationTruckingLineDao quotationTruckingLineDao;
	@Autowired
	private SalesQuotationEquipmentLineDao equipmentLineDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private UnitMeasurementDao uomDao;
	@Autowired
	private ArLineSetupDao arLineSetupDao;
	@Autowired
	private ItemDiscountTypeDao itemDiscountTypeDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private ArCustomerAcctService arCustomerAcctService;
	@Autowired
	private CustomerTypeService customerTypeService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ArLineSetupService arLineSetupService;
	@Autowired
	private UnitMeasurementService uomService;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private SalesOrderDao salesOrderDao;

	/**
	 * Get sales quotation object
	 * @param pId The sales quotation object id
	 * @return The sales quotation object
	 */
	public SalesQuotation getSalesQuotation(int pId) {
		SalesQuotation salesQuotation = salesQuotationDao.get(pId);
		List<SalesQuotationItem> salesQuotationItems =
				salesQuotationItemDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), pId);
		for (SalesQuotationItem sqi : salesQuotationItems) {
			sqi.setStockCode(sqi.getItem().getStockCode());
			Integer itemDiscountTypeId = sqi.getItemDiscountTypeId();
			if (itemDiscountTypeId != null) {
				sqi.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				itemDiscountTypeId = null;
			}
		}
		salesQuotation.setSqItems(salesQuotationItems);
		List<SalesQuotationLine> saleQuotationLines =
				salesQuotationLineDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), pId);
		setOtherCharges(saleQuotationLines);
		salesQuotation.setSqLines(saleQuotationLines);

		List<SalesQuotationTruckingLine> quotationTruckingLines =
				quotationTruckingLineDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), pId);
		setOtherCharges(quotationTruckingLines);
		salesQuotation.setSqtLines(quotationTruckingLines);

		List<SalesQuotationEquipmentLine> quotationEquipmentLines =
				equipmentLineDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), pId);
		setOtherCharges(quotationEquipmentLines);
		salesQuotation.setSqeLines(quotationEquipmentLines);
		return salesQuotation;
	}

	private <T extends AROtherCharge> void setOtherCharges(List<T> otherCharges) {
		UnitMeasurement uom = null;
		ArLineSetup arLineSetup = null;
		for (AROtherCharge sql : otherCharges) {
			arLineSetup = arLineSetupDao.get(sql.getArLineSetupId());
			if (arLineSetup != null) {
				sql.setArLineSetupName(arLineSetup.getName());
			}
			if (sql.getUnitOfMeasurementId() != null) {
				uom = uomDao.get(sql.getUnitOfMeasurementId());
				sql.setUnitMeasurementName(uom.getName());
			}
			Integer itemDiscountTypeId = sql.getDiscountTypeId();
			if (itemDiscountTypeId != null) {
				sql.setItemDiscountType(itemDiscountTypeDao.get(itemDiscountTypeId));
				itemDiscountTypeId = null;
			}
		}
	}

	/**
	 * Validate the form
	 * @param salesQuotation The form object for validation
	 * @param errors The validation errors
	 */
	public void validateForm(SalesQuotation salesQuotation, Errors errors) {
		ValidatorUtil.validateCompany(salesQuotation.getCompanyId(), companyService, errors, "companyId");

		Date date = salesQuotation.getDate();
		if (date == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("SalesQuotationService.1"));
		} else if (!timePeriodService.isOpenDate(date)) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("SalesQuotationService.2"));
		}

		Integer arCustomerId = salesQuotation.getArCustomerId();
		if (arCustomerId == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesQuotationService.3"));
		} else if (!arCustomerService.getCustomer(arCustomerId).isActive()) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("SalesQuotationService.4"));
		}

		Integer arCustomerAcctId = salesQuotation.getArCustomerAcctId();
		if (arCustomerAcctId == null) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("SalesQuotationService.5"));
		} else if (!arCustomerAcctService.getAccount(arCustomerAcctId).isActive()) {
			errors.rejectValue("arCustomerAcctId", null, null, ValidatorMessages.getString("SalesQuotationService.6"));
		}

		Integer customerTypeId = salesQuotation.getCustomerTypeId();
		if (customerTypeId == null) {
			errors.rejectValue("customerTypeId", null, null, ValidatorMessages.getString("SalesQuotationService.7"));
		} else if (customerTypeService.isActiveCustomerType(customerTypeId)) {
			errors.rejectValue("customerTypeId", null, null, ValidatorMessages.getString("SalesQuotationService.8"));
		}

		String shipTo = salesQuotation.getShipTo();
		if (shipTo == null || shipTo.isEmpty()) {
			errors.rejectValue("shipTo", null, null, ValidatorMessages.getString("SalesQuotationService.9"));
		}

		String subject = salesQuotation.getSubject();
		if (subject == null || subject.isEmpty()) {
			errors.rejectValue("subject", null, null, ValidatorMessages.getString("SalesQuotationService.10"));
		}

		String generalConditions = salesQuotation.getGeneralConditions();
		if (generalConditions == null || generalConditions.isEmpty()) {
			errors.rejectValue("generalConditions", null, null, ValidatorMessages.getString("SalesQuotationService.11"));
		}

		Double amount = salesQuotation.getAmount();
		if (amount == null) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("SalesQuotationService.12"));
		} else if (amount <= 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("SalesQuotationService.13"));
		}

		boolean hasItems = false;
		List<SalesQuotationItem> salesQuotationItems = salesQuotation.getSqItems();
		if (salesQuotationItems != null && !salesQuotationItems.isEmpty()) {
			int row = 0;
			for (SalesQuotationItem sqi : salesQuotationItems) {
				row++;
				Integer itemId = sqi.getItemId();
				if (itemId == null) {
					errors.rejectValue("sqItems", null, null, 
							String.format(ValidatorMessages.getString("SalesQuotationService.15"), row));
				} else if (!itemService.getItem(itemId).isActive()) {
					errors.rejectValue("sqItems", null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.16"), sqi.getStockCode(), row));
				}
				double quantity = sqi.getQuantity() != null ? sqi.getQuantity() : 0;
				if (quantity <= 0) {
					errors.rejectValue("sqItems", null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.17"), row));
				}
				double grossPrice = sqi.getGrossAmount() != null ? sqi.getGrossAmount() : 0;
				if (grossPrice <= 0) {
					errors.rejectValue("sqItems", null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.18"), row));
				}
				if (sqi.getItemDiscountTypeId() != null) {
					double discount = sqi.getDiscountValue() != null ? sqi.getDiscountValue() : 0;
					if (discount <= 0) {
						errors.rejectValue("sqItems", null, null,
								String.format(ValidatorMessages.getString("SalesQuotationService.19"), row));
					}
				}
			}
		} else {
			hasItems = true;
		}

		boolean hasOtherCharges = false;
		List<SalesQuotationLine> salesQuotationLines = salesQuotation.getSqLines();
		if (salesQuotationLines != null && !salesQuotationLines.isEmpty()) {
			validateOtherCharges(salesQuotationLines, errors, "sqLines");
		} else {
			hasOtherCharges = true;
		}

		boolean hasTruckingCharges = false;
		List<SalesQuotationTruckingLine> quotationTruckingLines = salesQuotation.getSqtLines();
		if (quotationTruckingLines != null && !quotationTruckingLines.isEmpty()) {
			validateOtherCharges(quotationTruckingLines, errors, "sqtLines");
		} else {
			hasTruckingCharges = true;
		}

		boolean hasEquipmentCharges = false;
		List<SalesQuotationEquipmentLine> quotationEquipmentLines = salesQuotation.getSqeLines();
		if (quotationEquipmentLines != null && !quotationEquipmentLines.isEmpty()) {
			validateOtherCharges(quotationEquipmentLines, errors, "sqeLines");
		} else {
			hasEquipmentCharges = true;
		}

		if (hasItems && hasOtherCharges && hasTruckingCharges && hasEquipmentCharges) {
			errors.rejectValue("commonErrorMsg", null, null, ValidatorMessages.getString("SalesQuotationService.14"));
		}
	}

	private <T extends AROtherCharge> void validateOtherCharges(List<T> otherCharges, Errors errors, String field) {
		int row = 0;
		ArLineSetup lineSetup = null;
		for (AROtherCharge otherCharge : otherCharges) {
			row++;
			Integer arLineSetupId = otherCharge.getArLineSetupId();
			if (arLineSetupId == null) {
				if(!otherCharge.getArLineSetupName().isEmpty()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.23"), otherCharge.getArLineSetupName(), row));
				} else {
					errors.rejectValue(field, null, null, 
							String.format(ValidatorMessages.getString("SalesQuotationService.20"), row));
				}
			} else {
				lineSetup = arLineSetupService.getLineSetup(arLineSetupId);
				if (!lineSetup.getName().equals(otherCharge.getArLineSetupName())) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.23"), otherCharge.getArLineSetupName(), row));
				} else if (!lineSetup.isActive()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.21"), otherCharge.getArLineSetupName(), row));
				}
			}
			Integer uomId = otherCharge.getUnitOfMeasurementId();
			if (uomId != null) {
				if (!uomService.getUnitMeasurement(uomId).isActive()) {
					errors.rejectValue(field, null, null,
							String.format(ValidatorMessages.getString("SalesQuotationService.22"), row));
				}
			}
		}
	}

	/**
	 * Save the form
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		SalesQuotation salesQuotation = (SalesQuotation) form;
		int salesQuotationId = salesQuotation.getId();
		boolean isNew = salesQuotationId == 0;
		AuditUtil.addAudit(salesQuotation, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			salesQuotation.setSequenceNumber(salesQuotationDao.generateSequenceNo(salesQuotation.getCompanyId()));
		} else {
			List<SalesQuotationLine> saleQuotationLines =
					salesQuotationLineDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), salesQuotationId);
			deleteLines(saleQuotationLines, salesQuotationLineDao);
			List<SalesQuotationTruckingLine> quotationTruckingLines =
					quotationTruckingLineDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), salesQuotationId);
			deleteLines(quotationTruckingLines, quotationTruckingLineDao);
			List<SalesQuotationEquipmentLine> quotationEquipmentLines =
					equipmentLineDao.getAllByRefId(SalesQuotationItem.FIELD.salesQuotationId.name(), salesQuotationId);
			deleteLines(quotationEquipmentLines, equipmentLineDao);
		}
		salesQuotation.setShipTo(StringFormatUtil.removeExtraWhiteSpaces(salesQuotation.getShipTo()));
		salesQuotation.setSubject(StringFormatUtil.removeExtraWhiteSpaces(salesQuotation.getSubject()));
		salesQuotation.setGeneralConditions(StringFormatUtil.removeExtraWhiteSpaces(
				salesQuotation.getGeneralConditions()));
		salesQuotationDao.saveOrUpdate(salesQuotation);

		List<SalesQuotationItem> salesQuotationItems = salesQuotation.getSqItems();
		for (SalesQuotationItem sqi : salesQuotationItems) {
			sqi.setSalesQuotationId(salesQuotation.getId());
			salesQuotationItemDao.saveOrUpdate(sqi);
		}

		List<SalesQuotationLine> salesQuotationLines = salesQuotation.getSqLines();
		for (SalesQuotationLine sql : salesQuotationLines) {
			sql.setSalesQuotationId(salesQuotation.getId());
			salesQuotationLineDao.save(sql);
		}

		List<SalesQuotationTruckingLine> quotationTruckingLines = salesQuotation.getSqtLines();
		for (SalesQuotationTruckingLine sql : quotationTruckingLines) {
			sql.setSalesQuotationId(salesQuotation.getId());
			quotationTruckingLineDao.save(sql);
		}

		List<SalesQuotationEquipmentLine> equipmentLines = salesQuotation.getSqeLines();
		for (SalesQuotationEquipmentLine sql : equipmentLines) {
			sql.setSalesQuotationId(salesQuotation.getId());
			equipmentLineDao.save(sql);
		}
	}

	private <T extends AROtherCharge> void deleteLines(List<T> otherCharges, Dao<T> dao) {
		List<Integer> ids = new ArrayList<Integer>();
		for (T t : otherCharges) {
			ids.add(t.getId());
		}
		dao.delete(ids);
	}

	/**
	 * Get the latest encoded ship to for the selected customer
	 * @param arCustomerId The customer id
	 * @return The latest encoded ship to field
	 */
	public String getCustomerShipTo(Integer arCustomerId) {
		return salesQuotationDao.getCustomerShipTo(arCustomerId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return salesQuotationDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return salesQuotationDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";
		SalesQuotation salesQuotation = salesQuotationDao.getByEbObjectId(ebObjectId);
		int pId = salesQuotation.getId();
		FormProperty property = workflowHandler.getProperty(salesQuotation.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = salesQuotation.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Sales Quotation - " + salesQuotation.getSequenceNumber();
		shortDescription = new StringBuffer(title).append(" " + salesQuotation.getArCustomer().getName())
				.append(" " + DateUtil.formatDate(salesQuotation.getDate()));
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus, shortDescription.toString(),
				fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case SalesQuotation.OBJECT_TYPE:
				return salesQuotationDao.getByEbObjectId(ebObjectId);
			case SalesQuotationItem.OBJECT_TYPE:
				return salesQuotationItemDao.getByEbObjectId(ebObjectId);
			case SalesQuotationLine.OBJECT_TYPE:
				return salesQuotationLineDao.getByEbObjectId(ebObjectId);
			case SalesQuotationTruckingLine.OBJECT_TYPE:
				return quotationTruckingLineDao.getByEbObjectId(ebObjectId);
			case SalesQuotationEquipmentLine.OBJECT_TYPE:
				return equipmentLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/**
	 * Retrieve the page list of {@code SalesQuotation} for general search
	 * @param searchCriteria The search criteria
	 * @return The page list of {@code SalesQuotation} for general search
	 */
	public List<FormSearchResult> retrieveForms(String searchCriteria) {
		Page<SalesQuotation> salesQuotations = salesQuotationDao.retrieveSalesQuotations(searchCriteria, new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		ArCustomer arCustomer = null;
		for (SalesQuotation sq : salesQuotations.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String title = sq.getCompany().getCompanyCode() + " " + sq.getSequenceNumber();
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(sq.getDate())));
			arCustomer = arCustomerService.getCustomer(sq.getArCustomerId());
			properties.add(ResultProperty.getInstance("Customer", arCustomer.getName()));
			String status = formStatusDao.get(sq.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(sq.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Process sales quotation - general condition for printout view
	 * @param genCon The saved SQ general condition
	 * @return The processed list of sales quotation - general condition
	 */
	public List<SalesQuotation> getGeneralConditions(String generalCondition) {
		List<SalesQuotation> ret = new ArrayList<SalesQuotation>();
		SalesQuotation sq = null;
		String generalConditions[] = generalCondition.split(";");
		for (String gc : generalConditions) {
			sq = new SalesQuotation();
			if (gc != null && !gc.isEmpty()) {
				sq.setGeneralConditions(gc);
				ret.add(sq);
			}
		}
		return ret;
	}

	/**
	 * Get the paged list of sales quotation for sales order reference.
	 * @param companyId The company id.
	 * @param arCustomerId The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param sequenceNo The sequence number.
	 * @param dateFrom The date from filter.
	 * @param dateTo The date to filter.
	 * @param pageNumber The page number.
	 * @param user The user logged in.
	 * @return
	 */
	public Page<SalesQuotation> getSalesQuotations(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer sequenceNo, Date dateFrom, Date dateTo, int pageNumber, User user) {
		return salesQuotationDao.getSalesQuotations(companyId, arCustomerId,
				arCustomerAccountId, sequenceNo, dateFrom, dateTo, new PageSetting(pageNumber), user);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
			SalesQuotation sq = salesQuotationDao.getByWorkflowId(currentWorkflowLog.getFormWorkflowId());
			StringBuffer errorMessage = null;
			if(sq != null) {
				Integer sqId = sq.getId();
				if(salesQuotationDao.isUsedBySO(sqId)) {
					List<SalesOrder> sos = salesOrderDao.getSOsBySalesOrderId(sqId);
					if(!sos.isEmpty()) {
						errorMessage = new StringBuffer("Sales Quotation form has been used by the ff forms:");
						for(SalesOrder so : sos) {
							errorMessage.append("<br>SO-"+so.getSequenceNumber());
						}
					}
				}
				if (errorMessage != null) {
					bindingResult.reject("workflowMessage", errorMessage.toString());
					currentWorkflowLog.setWorkflowMessage(errorMessage.toString());
				}
			}
		}
	}

}
