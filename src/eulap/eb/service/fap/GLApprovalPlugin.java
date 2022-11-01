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
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;


/**
 * A class that retrieves the approved and for approval GL entries.

 *
 */
@Service
public class GLApprovalPlugin extends FormApprovalPlugin{

	@Autowired
	private GeneralLedgerDao generalLedgerDao;
	
	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<GeneralLedger> generalLedger =
				generalLedgerDao.getAllGeneralLedger(searchParam, param.getStatuses(), param.getPageSetting());
		for (GeneralLedger gl : generalLedger.getData()) {
			double totalBalance = 0;
			for (GlEntry entry : gl.getGlEntries()) {
				if (entry.isDebit())
					totalBalance += entry.getAmount();
			}
			String shortDescription = "<b>JV No. " + gl.getSequenceNo() + "</b>" +
					"  " + DateUtil.formatDate(gl.getGlDate()) +
					"  Amount " + NumberFormatUtil.format(totalBalance) +
					"  " + gl.getComment();
			FormWorkflow workflow = gl.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(gl.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, generalLedger.getTotalRecords()); 
	}
}

