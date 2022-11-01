package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.EmployeeLeaveCreditDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EmployeeLeaveCredit;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Approval Plugin for {@link EmployeeLeaveCredit}

 *
 */
@Service
public class EmployeeLeaveCreditAppPlugin extends FormApprovalPlugin{

	@Autowired
	private EmployeeLeaveCreditDao empLeaveCreditDao;
	@Autowired
	private CompanyDao companyDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<EmployeeLeaveCredit> empLeaveCredits = empLeaveCreditDao.getEmployeeLeaveCredits(searchParam, param.getStatuses(), param.getPageSetting());
		Company company = null;
		for(EmployeeLeaveCredit empLeaveCredit : empLeaveCredits.getData()){
			company = companyDao.get(empLeaveCredit.getCompanyId());
			String shortDesc = "<b> Sequence Number: " + empLeaveCredit.getSequenceNumber() + "</b>" +
					" " + company.getName() + " " + empLeaveCredit.getDivision().getName() +
					" " + DateUtil.formatDate(empLeaveCredit.getDate());
			FormWorkflow workflow = empLeaveCredit.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(empLeaveCredit.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), res, empLeaveCredits.getTotalRecords());
	}

}
