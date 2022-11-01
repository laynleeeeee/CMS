package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Plugin for {@link APInvoice}

 *
 */
@Service
public class PettyCashReplenishmentPlugin extends MultiFormPlugin{
	@Autowired
	private APInvoiceDao apInvoiceDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<APInvoice> aps = apInvoiceDao.retrieveReplenishments(searchParam, param.getStatuses(), param.getPageSetting(), typeId);
		for(APInvoice ap : aps.getData()){
			String shortDesc = "<b> PCR No: " +ap.getSequenceNumber()+ "</b>" +
						" " + ap.getCompany().getName() +
						" " + ap.getDivision().getName() +
						" " + ap.getUserCustodian().getCustodianAccount().getCustodianName() +
						" " + DateUtil.formatDate(ap.getGlDate());
			FormWorkflow workflow = ap.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(ap.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), res, aps.getTotalRecords());
	}
}
