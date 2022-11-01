package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.CashSaleReturnDao;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.CashSaleReturnService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieving of cash sales return forms.

 */
@Service
public class CashSaleReturnFormPlugin extends MultiFormPlugin {
	private static Logger logger = Logger.getLogger(CashSaleReturnFormPlugin.class);
	@Autowired
	private CashSaleReturnDao cashSaleReturnDao;
	@Autowired
	private CashSaleReturnService cashSaleReturnService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		logger.info("Retrieving CSR forms of type: " + typeId);
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<CashSaleReturn> cashSaleReturns = cashSaleReturnDao.getCashSaleReturns(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		logger.info("Retrieved "+cashSaleReturns.getTotalRecords()+" CSR forms.");
		StringBuffer shortDescription = null;
		for(CashSaleReturn csr : cashSaleReturns.getData()) {
			String label = cashSaleReturnService.getCsrLabel(typeId);
			shortDescription = new StringBuffer("<b> " + label + " : "+ csr.getFormattedCSRNumber() + "</b>")
				.append(" ").append(csr.getArCustomer().getName())
				.append(" "+csr.getSalesInvoiceNo())
				.append(" DATE ").append(DateUtil.formatDate(csr.getDate()));
			FormWorkflow workflow = csr.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(csr.getId(), shortDescription.toString(), workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, cashSaleReturns.getTotalRecords());
	}
}
