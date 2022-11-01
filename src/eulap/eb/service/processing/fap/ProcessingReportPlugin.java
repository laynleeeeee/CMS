package eulap.eb.service.processing.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ProcessingReportDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ProcessingReport;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of Processing Reports.

 *
 */
@Service
public class ProcessingReportPlugin extends MultiFormPlugin {
	@Autowired
	private ProcessingReportDao processingReportDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ProcessingReport> processingReports = 
				processingReportDao.getProcessingReports(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		StringBuffer shortDescription = null;
		for(ProcessingReport pr : processingReports.getData()) {
			shortDescription = new StringBuffer("<b>" + pr.processShortCut(pr.getProcessingReportTypeId()) + " No. : </b>");
			String compCode = pr.getCompany().getCompanyCode();
			if(compCode != null) {
				shortDescription.append(StringFormatUtil.getFormattedSeqNo(compCode, pr.getSequenceNo()));
			} else {
				shortDescription.append(pr.getSequenceNo());
			}
			shortDescription.append("<b> Ref. No. : " + pr.getRefNumber() + "</b>" +
				" " + DateUtil.formatDate(pr.getDate()));
			FormWorkflow workflow = pr.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(pr.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, processingReports.getTotalRecords());
	}

}
