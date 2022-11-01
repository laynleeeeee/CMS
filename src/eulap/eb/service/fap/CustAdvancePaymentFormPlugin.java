package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Service class that will handle the retrieving of customer advance payment forms for approval

 */
@Service
public class CustAdvancePaymentFormPlugin extends MultiFormPlugin {
	@Autowired
	private CustomerAdvancePaymentDao capDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<CustomerAdvancePayment> caps = capDao.getCapsForApproval(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		for(CustomerAdvancePayment cap : caps.getData()) {
			StringBuffer shortDescription = new StringBuffer("<b> CAP "
					+ (cap.getCustomerAdvancePaymentTypeId().equals(CustomerAdvancePaymentType.INDIV_SELECTION) ? "- IS " : "")
					+ "No. : "+ cap.getCapNumber() + "</b>")
			.append(" ").append(cap.getArCustomer().getName())
			.append(" DATE ").append(DateUtil.formatDate(cap.getReceiptDate()));
			FormWorkflow workflow = cap.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(cap.getId(), shortDescription.toString(), workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, caps.getTotalRecords());
	}

}
