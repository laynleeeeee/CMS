package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.WorkOrderDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.WorkOrder;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link WorkOrder}.

 */

@Service
public class WorkOrderAppPlugin extends FormApprovalPlugin {
	@Autowired
	private WorkOrderDao workOrderDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<WorkOrder> workOrders = workOrderDao.getWorkOrders(searchParam, param.getStatuses(), param.getPageSetting());
		for (WorkOrder atw : workOrders.getData()) {
			String shortDescription = "<b>WO No. " + atw.getSequenceNumber() + "</b>"
					+ " " + DateUtil.formatDate(atw.getDate())
					+ " " + atw.getCompany().getName() + " " + atw.getArCustomer().getName()
					+ " " + atw.getArCustomerAccount().getName();
			FormWorkflow workflow = atw.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(atw.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(),
					log.getCreatedDate(), highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, workOrders.getTotalRecords());
	}
}