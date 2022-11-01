package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.RTransferReceiptDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Retrieves and generates the list of forms for approval.

 *
 */
@Service
public class RTRFormPlugin extends MultiFormPlugin{
	private static Logger logger = Logger.getLogger(RTRFormPlugin.class);
	@Autowired
	private RTransferReceiptDao trDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		logger.debug("Searching TRs using the earch criteria: "+param.getSearchCriteria());
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<RTransferReceipt> searchResults = trDao.getAllTRsByStatus(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		logger.debug("Retrieved "+searchResults.getTotalRecords()+" Retail - TRs.");
		String drNumber = null;
		boolean highlight = false;
		FormWorkflow workflow = null;
		FormWorkflowLog workflowLog = null;
		StringBuffer shortDesc = null;
		String label = null;
		for (RTransferReceipt tr : searchResults.getData()) {
			if(tr.getDrNumber() != null)
				drNumber = tr.getDrNumber();
			label = typeId == 1 ? "TR" : "TR - IS";
			shortDesc = new StringBuffer("<b>"+label+ " No : "+tr.getFormattedTRNumber()+"</b>")
					.append(" DATE "+DateUtil.formatDate(tr.getTrDate()));
			if(drNumber != null)
				shortDesc.append(" "+drNumber);
			workflow = tr.getFormWorkflow();
			workflowLog = workflow.getCurrentLogStatus();
			highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(tr.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(), workflowLog.getCreated(),
					workflowLog.getCreatedDate(), highlight));
		}

		logger.info("=======>> Freeing up memory allocation.");
		drNumber = null;
		workflow = null;
		workflowLog = null;

		logger.info("Showing the list of Retail - TRs for approval.");
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResults.getTotalRecords());
	}
}
