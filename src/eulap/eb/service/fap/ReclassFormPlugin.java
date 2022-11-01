package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.RepackingDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Retrieves and generates the list of reclass forms for approval.

 */

@Service
public class ReclassFormPlugin extends MultiFormPlugin {
	private static Logger logger = Logger.getLogger(ReclassFormPlugin.class);
	@Autowired
	private RepackingDao rpDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		logger.info("Retrieving reclass forms for repacking type " + typeId);
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		//The 1 value in the parameter is REPACKING type.
		Page<Repacking> searchResults = rpDao.getAllRepackingByStatus(searchParam, param.getStatuses(), param.getPageSetting(), 1, typeId);
		logger.info("Retrieved "+searchResults.getTotalRecords()+" reclass forms.");
		boolean highlight = false;
		FormWorkflow workflow = null;
		FormWorkflowLog workflowLog = null;
		StringBuffer shortDesc = null;
		String formLabel = "";
		for (Repacking rp : searchResults.getData()) {
			formLabel =  "RC";
			shortDesc = new StringBuffer("<b>"+formLabel+" No. "+rp.getFormattedRNumber()+"</b>")
					.append("<b> DIVISION "+rp.getDivision().getName()+"</b>")
					.append(" DATE "+DateUtil.formatDate(rp.getrDate()))
					.append(" "+rp.getWarehouse().getName());
			workflow = rp.getFormWorkflow();
			workflowLog = workflow.getCurrentLogStatus();
			highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(rp.getId(), shortDesc.toString(), workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
			logger.debug("Added "+rp.getFormattedRNumber()+" to the list.");
		}

		logger.info("=======>> Freeing up memory allocation.");
		workflow = null;
		workflowLog = null;
		shortDesc = null;
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResults.getTotalRecords());
	}

}
