package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.EmployeeDocumentDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.EmployeeDocument;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Plugin for {@link EmployeeDocument}

 *
 */
@Service
public class EmployeeDocumentAppPlugin extends FormApprovalPlugin{
	@Autowired
	private EmployeeDocumentDao employeeDocumentDao;
	@Autowired
	private CompanyDao companyDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<EmployeeDocument> employeeDocuments = employeeDocumentDao.getEmployeeDocuments(searchParam, param.getStatuses(), param.getPageSetting());
		Company company = null;
		for(EmployeeDocument ed : employeeDocuments.getData()){
			company = companyDao.get(ed.getCompanyId());
			String shortDesc = "<b> Sequence Number: " +ed.getSequenceNo()+ "</b>" +
						" " + company.getName() +
						" " + ed.getEmployee().getDivision().getName() +
						" " + DateUtil.formatDate(ed.getDate()) +
						" " + ed.getEmployee().getFullName();
			FormWorkflow workflow = ed.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(ed.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), res, employeeDocuments.getTotalRecords());
	}

}
