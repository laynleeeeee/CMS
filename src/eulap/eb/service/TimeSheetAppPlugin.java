package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.TimeSheetDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.TimeSheet;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Approval plugin for {@link TimeSheet}

 *
 */
@Service
public class TimeSheetAppPlugin extends FormApprovalPlugin {
	@Autowired
	private TimeSheetDao timeSheetDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private CompanyService companyService;
	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<TimeSheet> timeSheets = timeSheetDao.getTimeSheets(searchParam, param.getStatuses(), param.getPageSetting());
		Division division = null;
		Company company = null;
		for(TimeSheet ts : timeSheets.getData()){
			company = companyService.getCompany(ts.getCompanyId());
			division = divisionDao.get(ts.getDivisionId());
			String shortDesc = "<b> Sequence Number: " + ts.getSequenceNumber() + "</b>" +
					" " + company.getName() + " " + division.getName() +
					" " + DateUtil.formatDate(ts.getDate());
			FormWorkflow workflow = ts.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(ts.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), res, timeSheets.getTotalRecords());
	}

}
