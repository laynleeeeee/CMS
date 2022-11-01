package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.GeneralLedgerDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.service.GlEntryService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Retrieves and generates the the list of forms for approval.

 */

@Service
public class GeneralJournalFormPlugin extends MultiFormPlugin {
	@Autowired
	private GeneralLedgerDao glDao;
	@Autowired
	private GlEntryService glEntryService;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<GeneralLedger> searchResult = glDao.getAllGLDivsByStatus(typeId, searchParam,
				param.getStatuses(), param.getPageSetting());
		for (GeneralLedger gl : searchResult.getData()) {
			String totalAmt = NumberFormatUtil.format(glEntryService.getTotalAmt(gl.getGlEntries()));
			StringBuffer shortDesc = new StringBuffer("<b>JV No : "+gl.getSequenceNo()+ "</b>")
				.append(" DATE "+DateUtil.formatDate(gl.getGlDate()))
				.append(" "+totalAmt)
				.append(" "+gl.getComment());
			FormWorkflow workflow = gl.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(gl.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(), workflowLog.getCreated(),
					workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResult.getTotalRecords());
	}
}
