package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.service.CompanyService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieving of requisition forms.

 *
 */
@Service
public class PurchaseRequisitionPlugin extends MultiFormPlugin {
	private static Logger logger = Logger.getLogger(PurchaseRequisitionPlugin.class);
	@Autowired
	private RequisitionFormDao requisitionFormDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<RequisitionForm> requisitionForms = requisitionFormDao.getRequisitionForms(searchParam, typeId, param.getStatuses(), true, param.getPageSetting());
		logger.info("Retrieved "+requisitionForms.getTotalRecords()+" Purchase Requisition forms.");
		StringBuffer shortDescription = null;
		for(RequisitionForm rf : requisitionForms.getData()) {
			shortDescription = new StringBuffer("<b> "+ "Sequence No. : " + rf.getSequenceNumber() + "</b>")
			.append(" "+rf.getRequisitionType().getName())
			.append(" ").append(companyService.getCompany(rf.getCompanyId()).getName())
			.append(" DATE ").append(DateUtil.formatDate(rf.getDate()));
			FormWorkflow workflow = rf.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(rf.getId(), shortDescription.toString(), workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, requisitionForms.getTotalRecords());
	}
}
