package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeRequestDao;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Approval Plugin for {@link EmployeeRequest}

 *
 */
@Service
public class EmployeeRequestAppPlugin extends MultiFormPlugin {
	@Autowired
	private EmployeeRequestDao employeeRequestDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private EmployeeDao emplDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<EmployeeRequest> requestForLeaves = employeeRequestDao.getRequests(typeId, searchParam, param.getStatuses(), param.getPageSetting());
		Division division = null;
		for(EmployeeRequest rfl : requestForLeaves.getData()){
			Employee empl = emplDao.get(rfl.getEmployeeId());
			division = divisionDao.get(empl.getDivisionId());
			String shortDesc = "<b> Sequence Number: " + rfl.getSequenceNo() + "</b>" +
					" " + rfl.getCompany().getName()+ " " + division.getName() +
					" " + DateUtil.formatDate(rfl.getDate());
			FormWorkflow workflow = rfl.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow,
					param.getUser());
			res.add(FormApprovalDto.getInstanceBy(rfl.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(), log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), res, requestForLeaves.getTotalRecords());
	}

}
