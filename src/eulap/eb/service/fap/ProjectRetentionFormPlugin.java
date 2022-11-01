package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ProjectRetentionDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ProjectRetention;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieval of {@link ProjectRetention} forms

 */

@Service
public class ProjectRetentionFormPlugin extends MultiFormPlugin {
	@Autowired
	private ProjectRetentionDao projectRetentionDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ProjectRetention> searchResult = projectRetentionDao.getAllPrByStatus(
				searchParam, param.getStatuses(), param.getPageSetting(), typeId);
		for(ProjectRetention pr : searchResult.getData()) {
			StringBuffer shortDesc = new StringBuffer("<b>PR No : "+pr.getSequenceNo()+"</b>")
					.append(" DATE "+DateUtil.formatDate(pr.getDate()));
			FormWorkflow workflow = pr.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(pr.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(), workflowLog.getCreated(),
					workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResult.getTotalRecords());
	}
}
