package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.AuthorityToWithdrawDao;
import eulap.eb.domain.hibernate.AuthorityToWithdraw;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link AuthorityToWithdraw}.

 */

@Service
public class AuthorityToWithdrawAppPlugin extends FormApprovalPlugin {
	@Autowired
	private AuthorityToWithdrawDao atwDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<AuthorityToWithdraw> authorityToWithdraws = atwDao.getAuthorityToWithdraws(searchParam, param.getStatuses(), param.getPageSetting());
		for (AuthorityToWithdraw atw : authorityToWithdraws.getData()) {
			String shortDescription = "<b>ATW No. " + atw.getSequenceNumber() + "</b>"
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
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, authorityToWithdraws.getTotalRecords());
	}
}
