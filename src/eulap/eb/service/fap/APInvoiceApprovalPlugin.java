package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.RReceivingReportDao;
import eulap.eb.dao.RReturnToSupplierDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.service.CurrencyUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of AP invoices.

 */

@Service
public class APInvoiceApprovalPlugin extends MultiFormPlugin{
	private static Logger logger = Logger.getLogger(APInvoiceApprovalPlugin.class);
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private RReceivingReportDao rrDao;
	@Autowired
	private RReturnToSupplierDao rtsDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		logger.info("Retrieving the AP Invoice forms to be approved.");
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		int totalRecords = 0;
		StringBuffer shortDescription = null;
		FormWorkflow workflow = null;
		FormWorkflowLog log = null;
		if (isInvoice(typeId) || isInvoiceGs(typeId) || isInvoiceConf(typeId) || isInvoiceImport(typeId) || isLoan(typeId)) {
			ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
			searchParam.setUser(param.getUser());
			Page<APInvoice> invoices = apInvoiceDao.getAllAPInvoice(searchParam,
					param.getStatuses(), param.getPageSetting(), typeId);
			totalRecords = invoices.getTotalRecords();
			for (APInvoice ap : invoices.getData()) {
				int invTypeId = ap.getInvoiceTypeId();
				if (isRts(invTypeId) || isRr(invTypeId)) {
					continue;
				}
				if (isInvoiceGs(invTypeId)) {
					shortDescription = new StringBuffer("<b>API-G/S No.: " + ap.getSequenceNumber() + "</b> ")
						.append("<b>SI/SOA No. " + ap.getInvoiceNumber() + "</b> ")
						.append("<b>BMS No. </b>"+ap.getBmsNumber()+" ");
				} else if (isInvoiceConf(invTypeId)) {
					shortDescription = new StringBuffer("<b>API-C No.: " + ap.getSequenceNumber() + "</b> ")
							.append("<b>SI/SOA Ref No. " + ap.getInvoiceNumber() + "</b> ");
				} else if (isInvoiceImport(invTypeId)) {
					shortDescription = new StringBuffer("<b>API-I No.: " + ap.getSequenceNumber() + "</b> ")
							.append("<b>SI/SOA Ref No. " + ap.getInvoiceNumber() + "</b> ");
				} else if (isLoan(typeId)) {
					shortDescription = new StringBuffer("<b>APL No.:</b> " + ap.getSequenceNumber() + " ")
							.append("<b>Date</b> " + DateUtil.formatDate(ap.getGlDate()) + " ")
							.append("<b>Lender</b> " + ap.getSupplier().getName() + " ")
							.append("<b>" + ap.getCurrency().getName() + "</b> ")
							.append("<b>" + NumberFormatUtil.format(CurrencyUtil.convertMonetaryValues(ap.getAmount(), ap.getCurrencyRateValue())) + "</b> ");
				} else {
					shortDescription = new StringBuffer("<b>API-NPO No.: " + ap.getSequenceNumber() + "</b> ")
							.append("<b>SI/SOA Ref No. " + ap.getInvoiceNumber() + "</b> ");
				}

				if(!isLoan(typeId)) {
					shortDescription.append(" " + ap.getSupplier().getName())
					.append(" " + ap.getSupplierAccount().getName())
					.append(" " + DateUtil.formatDate(ap.getGlDate()))
					.append(ap.getDueDate() != null ? "<b> DUE DATE : </b>" + DateUtil.formatDate(ap.getDueDate()) : "")
					.append(" " + NumberFormatUtil.format(ap.getAmount()));
				}

				workflow = ap.getFormWorkflow();
				log = workflow.getCurrentLogStatus();
				ret.add(getInstance(ap, workflow, log, param, shortDescription));
			}
		} else {
			APInvoice ap = null;
			if (isRr(typeId)) {
				Page<RReceivingReport> searchResult = rrDao.getReceivingReports(typeId, param);
				totalRecords = searchResult.getTotalRecords();
				for (RReceivingReport rr : searchResult.getData()) {
					ap = rr.getApInvoice();
					shortDescription = new StringBuffer("<b> RR No :"+ ap.getSequenceNumber()+ "</b>")
					.append(" ").append(rr.getApInvoice().getSupplier().getName())
					.append(" DATE : ").append(DateUtil.formatDate(ap.getGlDate()));
					workflow = ap.getFormWorkflow();
					log = workflow.getCurrentLogStatus();
					ret.add(getInstance(ap, workflow, log, param, shortDescription));
				}
			} else if (isRts(typeId)) {
				Page<RReturnToSupplier> searchResult = rtsDao.getReturnToSuppliers(typeId, param);
				totalRecords = searchResult.getTotalRecords();
				for (RReturnToSupplier rts : searchResult.getData()) {
					ap = rts.getApInvoice();
					shortDescription = new StringBuffer("<b> RTS No :"+ rts.getFormattedRTSNumber() + "</b>");
					if (typeId != InvoiceType.RTS_TYPE_ID && typeId != InvoiceType.RTS_EB_TYPE_ID) {
						shortDescription.append("<b>SI/SOA No. " + ap.getInvoiceNumber() + "</b> ")
						.append("<b>BMS No. </b>"+ap.getBmsNumber()+" ");
					} else {
						shortDescription.append(" "+rts.getWarehouse().getName());
					}
					shortDescription.append(" ").append(ap.getSupplier().getName())
					.append(" DATE : ").append(DateUtil.formatDate(ap.getGlDate()));
					workflow = ap.getFormWorkflow();
					log = workflow.getCurrentLogStatus();
					ret.add(getInstance(ap, workflow, log, param, shortDescription));
				}
			}
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, totalRecords);
	}

	private boolean isInvoice(int typeId) {
		return typeId == InvoiceType.API_NON_PO_CENTRAL
			|| typeId == InvoiceType.API_NON_PO_NSB3 
			|| typeId == InvoiceType.API_NON_PO_NSB4
			|| typeId == InvoiceType.API_NON_PO_NSB5 
			|| typeId == InvoiceType.API_NON_PO_NSB8
			|| typeId == InvoiceType.API_NON_PO_NSB8A;
	}

	private boolean isInvoiceConf(int typeId) {
		return typeId == InvoiceType.API_CONF_CENTRAL
			|| typeId == InvoiceType.API_CONF_NSB3 
			|| typeId == InvoiceType.API_CONF_NSB4
			|| typeId == InvoiceType.API_CONF_NSB5 
			|| typeId == InvoiceType.API_CONF_NSB8
			|| typeId == InvoiceType.API_CONF_NSB8A ;
	}

	private boolean isInvoiceImport(int typeId) {
		return typeId == InvoiceType.API_IMPORT_CENTRAL
			|| typeId == InvoiceType.API_IMPORT_NSB3 
			|| typeId == InvoiceType.API_IMPORT_NSB4
			|| typeId == InvoiceType.API_IMPORT_NSB5 
			|| typeId == InvoiceType.API_IMPORT_NSB8
			|| typeId == InvoiceType.API_IMPORT_NSB8A ;
	}

	private boolean isInvoiceGs(int typeId) {
		return typeId == InvoiceType.API_GS_CENTRAL || typeId == InvoiceType.API_GS_NSB3
				|| typeId == InvoiceType.API_GS_NSB4 || typeId == InvoiceType.API_GS_NSB5
				|| typeId == InvoiceType.API_GS_NSB8 || typeId == InvoiceType.API_GS_NSB8A;
	}

	private boolean isRr(int typeId) {
		return typeId == InvoiceType.RR_TYPE_ID || typeId == InvoiceType.RR_RAW_MAT_TYPE_ID
				|| typeId == InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID || typeId == InvoiceType.RR_CENTRAL_TYPE_ID
				|| typeId == InvoiceType.RR_NSB3_TYPE_ID || typeId == InvoiceType.RR_NSB4_TYPE_ID
				|| typeId == InvoiceType.RR_NSB5_TYPE_ID || typeId == InvoiceType.RR_NSB8_TYPE_ID
				|| typeId == InvoiceType.RR_NSB8A_TYPE_ID;
	}

	private boolean isRts(int typeId) {
		return typeId == InvoiceType.RTS_TYPE_ID || typeId == InvoiceType.RTS_EB_TYPE_ID
				|| typeId == InvoiceType.RTS_CENTRAL_TYPE_ID || typeId == InvoiceType.RTS_NSB3_TYPE_ID
				|| typeId == InvoiceType.RTS_NSB4_TYPE_ID || typeId == InvoiceType.RTS_NSB5_TYPE_ID
				|| typeId == InvoiceType.RTS_NSB8_TYPE_ID || typeId == InvoiceType.RTS_NSB8A_TYPE_ID;
	}

	private boolean isLoan(int typeId) {
		return typeId == InvoiceType.AP_LOAN_CENTRAL || typeId == InvoiceType.AP_LOAN_NSB3
				|| typeId == InvoiceType.AP_LOAN_NSB4 || typeId == InvoiceType.AP_LOAN_NSB5
				|| typeId == InvoiceType.AP_LOAN_NSB8 || typeId == InvoiceType.AP_LOAN_NSB8A;
	}

	private FormApprovalDto getInstance(APInvoice ap, FormWorkflow workflow, FormWorkflowLog log,
			FormPluginParam param, StringBuffer shortDesc) {
		boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
		return FormApprovalDto.getInstanceBy(ap.getId(), shortDesc.toString(),
				workflow.getCurrentFormStatus().getDescription(), log.getCreated(), log.getCreatedDate(), highlight);
	}
}
