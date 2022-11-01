package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.CurrencyUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of AR Receipts.

 *
 */
@Service
public class ArReceiptApprovalPlugin extends MultiFormPlugin {
	@Autowired
	private ArReceiptDao arReceiptDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int divisionId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ArReceipt> arReceipts = arReceiptDao.getAllArReceipts(searchParam, param.getStatuses(), divisionId, param.getPageSetting());
		StringBuffer shortDescription = null;
		for(ArReceipt arReceipt : arReceipts.getData()) {
			double rate = arReceipt.getCurrencyRateValue();
			shortDescription = new StringBuffer("<b>AC No. : </b>" + arReceipt.getSequenceNo());
			shortDescription.append("<b> Receipt No : " + arReceipt.getReceiptNumber() + "</b>" +
				" " + DateUtil.formatDate(arReceipt.getReceiptDate()) +
				" " + arReceipt.getArCustomer().getName() +
				" " + DateUtil.formatDate(arReceipt.getMaturityDate()) +
				" " + arReceipt.getCurrency().getName() +
				" " + NumberFormatUtil.format(CurrencyUtil.convertMonetaryValues(arReceipt.getAmount(), rate)));
			FormWorkflow workflow = arReceipt.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(arReceipt.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, arReceipts.getTotalRecords());
	}
}
