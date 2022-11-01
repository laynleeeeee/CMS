package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.PersonnelActionNoticeDao;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Approval plugin for {@link PersonnelActionNotice}

 *
 */
@Service
public class PersonnelActionNoticeAppPlugin extends FormApprovalPlugin {
	@Autowired
	private PersonnelActionNoticeDao panDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private EmployeeDao emplDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<PersonnelActionNotice> actionNotices = panDao.getActionNotices(searchParam, param.getStatuses(), param.getPageSetting());
		Division division = null;
		for(PersonnelActionNotice personnelActionNotice : actionNotices.getData()){
			Employee empl = emplDao.get(personnelActionNotice.getEmployeeId());
			division = divisionDao.get(empl.getDivisionId());
			String shortDesc = "<b> Sequence Number: " + personnelActionNotice.getSequenceNo() + "</b>" +
				    " " + personnelActionNotice.getCompany().getName() +" " + division.getName() +
					" " + DateUtil.formatDate(personnelActionNotice.getDate());
			FormWorkflow workflow = personnelActionNotice.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(personnelActionNotice.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), res, actionNotices.getTotalRecords());
	}

}
