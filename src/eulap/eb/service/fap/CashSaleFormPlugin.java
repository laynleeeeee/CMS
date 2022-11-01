package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieving of cash sale forms.

 */
@Service
public class CashSaleFormPlugin extends MultiFormPlugin {
	private static Logger logger = Logger.getLogger(CashSaleFormPlugin.class);
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<CashSale> cashSales = cashSaleService.getCashSales(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		logger.info("Retrieved "+cashSales.getTotalRecords()+" Cash Sales forms.");
		StringBuffer shortDescription = null;
		for(CashSale cs : cashSales.getData()) {
			String label = null;
			if(typeId <= CashSaleType.INDIV_SELECTION) {
				label = cashSaleService.getCsLabel(typeId);
			} else {
				label = "Sequence";
			}
			shortDescription = new StringBuffer("<b> "+ label +" No. : "+ cs.getFormattedCSNumber() + "</b>")
			.append(" "+cs.getArReceiptType().getName())
			.append(" ").append(cs.getArCustomer().getName())
			.append(" "+ (cs.getSalesInvoiceNo() != null ? cs.getSalesInvoiceNo() : ""))
			.append(" DATE ").append(DateUtil.formatDate(cs.getReceiptDate()));
			FormWorkflow workflow = cs.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(cs.getId(), shortDescription.toString(), workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, cashSales.getTotalRecords());
	}
}
