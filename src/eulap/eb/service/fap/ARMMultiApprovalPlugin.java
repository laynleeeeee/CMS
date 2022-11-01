package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ArMiscellaneousDao;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.CurrencyUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of AR Miscellaneous.

 *
 */
@Service
public class ARMMultiApprovalPlugin  extends MultiFormPlugin{
	@Autowired
	private ArMiscellaneousDao arMiscellaneousDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ArMiscellaneous> arMiscellaneouses = 
				arMiscellaneousDao.getAllArMiscellaneous(typeId, searchParam, param.getStatuses(), param.getPageSetting());
		for(ArMiscellaneous arM : arMiscellaneouses.getData()) {
			String shortDescription = "<b> Receipt No : " + arM.getReceiptNumber() + "</b>" +
					" " + DateUtil.formatDate(arM.getReceiptDate()) +
					" " + arM.getArCustomer().getName() +
					" " + DateUtil.formatDate(arM.getMaturityDate()) +
					" " + NumberFormatUtil.format(CurrencyUtil.convertMonetaryValues(arM.getAmount(), arM.getCurrencyRateValue()));
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
