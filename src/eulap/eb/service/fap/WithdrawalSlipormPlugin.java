package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.WithdrawalSlip;
import eulap.eb.service.CompanyService;
import eulap.eb.service.WithdrawalSlipService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieving of withdrawal slip forms.

 */
@Service
public class WithdrawalSlipormPlugin extends FormApprovalPlugin {
	private static Logger logger = Logger.getLogger(WithdrawalSlipormPlugin.class);
	@Autowired
	private WithdrawalSlipService withdrawalSlipService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private CompanyService companyService;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<WithdrawalSlip> withdrawalSlips = withdrawalSlipService.getWithdrawalSlip(searchParam, param.getStatuses(), param.getPageSetting());
		logger.info("Retrieved "+withdrawalSlips.getTotalRecords()+" Withdrawal slip forms.");
		StringBuffer shortDescription = null;
		Company company = null;
		for(WithdrawalSlip ws : withdrawalSlips.getData()) {
			company = companyService.getCompany(ws.getCompanyId());
			String label = "Sequence";
			shortDescription = new StringBuffer("<b> "+ label +" No. : "+ (company.getCompanyCode() != null ? company.getCompanyCode() :
				company.getName().charAt(0))+"-"+ws.getWsNumber() + "</b>")
			.append(" ").append(ws.getCompanyName())
			.append(" ").append(ws.getFleetName() != null ? ws.getFleetName() : ws.getCustomerName())
			.append(" DATE ").append(DateUtil.formatDate(ws.getDate()));
			FormWorkflow workflow = ws.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(ws.getId(), shortDescription.toString(), workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
		}
		company = null;
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, withdrawalSlips.getTotalRecords());
	}
}
