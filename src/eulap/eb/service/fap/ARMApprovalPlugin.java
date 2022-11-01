package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ArMiscellaneousDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of AR Miscellaneous.

 *
 */
@Service
public class ARMApprovalPlugin  extends FormApprovalPlugin{
	@Autowired
	private ArMiscellaneousDao arMiscellaneousDao;
	@Autowired
	private UserDao userDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ArMiscellaneous> arMiscellaneouses = 
				arMiscellaneousDao.getAllArMiscellaneous(searchParam, param.getStatuses(), param.getPageSetting());
		for(ArMiscellaneous arM : arMiscellaneouses.getData()) {
			String shortDescription = "<b> Receipt No : " + arM.getReceiptNumber() + "</b>" +
					" " + DateUtil.formatDate(arM.getReceiptDate()) +
					" " + arM.getArCustomer().getName() +
					" " + DateUtil.formatDate(arM.getMaturityDate()) +
					" " + arM.getAmount();
			FormWorkflow workflow = arM.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(arM.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, arMiscellaneouses.getTotalRecords());
	}
}
