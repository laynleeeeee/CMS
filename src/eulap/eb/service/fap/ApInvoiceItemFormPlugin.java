package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieval of {@link APInvoice} item/service forms

 */

@Service
public class ApInvoiceItemFormPlugin extends MultiFormPlugin {
	private static Logger logger = Logger.getLogger(ApInvoiceItemFormPlugin.class);
	@Autowired
	private APInvoiceDao apInvoiceDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		logger.info("Retrieving the AP Invoice forms for approval");
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		StringBuffer shortDescription = null;
		FormWorkflow workflow = null;
		FormWorkflowLog log = null;
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		Page<APInvoice> apInvoices = apInvoiceDao.getAllAPInvoice(searchParam, param.getStatuses(),
				param.getPageSetting(), typeId);
		for(APInvoice apInvoice : apInvoices.getData()) {
			String sequenceNo = getFormLabel(typeId, apInvoice.getSequenceNumber());
			shortDescription = new StringBuffer(sequenceNo)
				.append("<b> Invoice No : " + apInvoice.getInvoiceNumber() + "</b>")
				.append(" " + apInvoice.getSupplier().getName())
				.append(" " + apInvoice.getSupplierAccount().getName())
				.append(" " + DateUtil.formatDate(apInvoice.getGlDate()))
				.append("<b> DUE DATE : </b>" + DateUtil.formatDate(apInvoice.getDueDate()))
				.append(" " + apInvoice.getAmount());

			workflow = apInvoice.getFormWorkflow();
			log = workflow.getCurrentLogStatus();
			ret.add(getInstance(apInvoice, workflow, log, param, shortDescription));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, apInvoices.getTotalRecords());
	}

	private FormApprovalDto getInstance(APInvoice apInvoice, FormWorkflow workflow, FormWorkflowLog log,
			FormPluginParam param, StringBuffer shortDesc) {
		boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
		return FormApprovalDto.getInstanceBy(apInvoice.getId(), shortDesc.toString(),
				workflow.getCurrentFormStatus().getDescription(), log.getCreated(), log.getCreatedDate(), highlight);
	}

	private String getFormLabel(Integer typeId, Integer sequenceNo) {
		switch (typeId) {
		case InvoiceType.REGULAR_TYPE_ID:
		case InvoiceType.DEBIT_MEMO_TYPE_ID:
		case InvoiceType.CREDIT_MEMO_TYPE_ID:
			return "<b>API "+sequenceNo+"</b> ";
		case InvoiceType.RR_TYPE_ID:
			return "<b>RR "+sequenceNo+"</b> ";
		case InvoiceType.RTS_TYPE_ID:
			return "<b>RTS "+sequenceNo+"</b> ";
		case InvoiceType.INVOICE_ITEM_TYPE_ID:
			return "<b>APII "+sequenceNo+"</b> ";
		case InvoiceType.INVOICE_SERVICE_TYPE_ID:
			return "<b>APIS "+sequenceNo+"</b> ";
		}
		return "";
	}
}
