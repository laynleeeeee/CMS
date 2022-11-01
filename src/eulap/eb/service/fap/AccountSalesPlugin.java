package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.AccountSalesDao;
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Retrieves and generates the the list of forms for approval.

 *
 */
@Service
public class AccountSalesPlugin extends FormApprovalPlugin{
	@Autowired
	private AccountSalesDao poDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> result = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<AccountSales> searchResult =
				poDao.getAllPOsByStatus(searchParam, param.getStatuses(), param.getPageSetting());
		for (AccountSales po : searchResult.getData()) {
			StringBuffer shortDesc = new StringBuffer("<b>SO No : "+po.getFormattedPONumber()+"</b>")
				.append(" DATE "+DateUtil.formatDate(po.getPoDate()))
				.append(" "+po.getArCustomer().getName());
			FormWorkflow workflow = po.getFormWorkflow();
			FormWorkflowLog workflowLog = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			result.add(FormApprovalDto.getInstanceBy(po.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(), workflowLog.getCreated(),
					workflowLog.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), result, searchResult.getTotalRecords());
	}
}
