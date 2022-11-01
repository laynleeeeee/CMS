package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.CAPDeliveryDao;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Retrieves and generates the list of CAP Delivery forms for approval.

 *
 */
@Service
public class CAPDeliveryPlugin extends MultiFormPlugin {
	@Autowired
	private CAPDeliveryDao deliveryDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<CAPDelivery> searchResult = deliveryDao.getCAPDeliveries(searchParam, param.getStatuses(), typeId, param.getPageSetting());
		boolean highlight = false;
		FormWorkflow workflow = null;
		FormWorkflowLog workflowLog = null;
		StringBuffer shortDesc = null;
		for (CAPDelivery delivery : searchResult.getData()) {
			shortDesc = new StringBuffer(delivery.getShortDescription())
				.append("<b> Date </b> "+DateUtil.formatDate(delivery.getDeliveryDate()))
				.append(" "+delivery.getCompany().getName())
				.append(" "+delivery.getArCustomer().getName())
				.append(" "+delivery.getSalesInvoiceNo());
			workflow = delivery.getFormWorkflow();
			workflowLog = workflow.getCurrentLogStatus();
			highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(delivery.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					workflowLog.getCreated(), workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResult.getTotalRecords());
	}
}
