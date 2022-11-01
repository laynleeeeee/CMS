package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.DeductionTypeDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.FormDeductionDao;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormDeduction;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 *  Approval Plugin for {@link FormDeduction}

 *
 */
@Service
public class FormDeductionApprovalPlugin extends MultiFormPlugin{

	@Autowired
	private FormDeductionDao formDeductionDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private DeductionTypeDao deductionTypeDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<FormDeduction> formDeductions = formDeductionDao.getFormDeductions(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		Division division = null;
		DeductionType deductionType = null;
		for(FormDeduction formDeduction : formDeductions.getData()){
			deductionType = deductionTypeDao.get(formDeduction.getDeductionTypeId());
			division = divisionDao.get(formDeduction.getDivisionId());
			String shortDesc = "<b> Sequence Number: " + formDeduction.getSequenceNumber() + "</b>" +
					" " + formDeduction.getCompany().getName() +
					" " + division.getName() +
					" " + DateUtil.formatDate(formDeduction.getFormDate()) +
					" " + deductionType.getName();
			FormWorkflow workflow = formDeduction.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(formDeduction.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), result, formDeductions.getTotalRecords());
	}

}
