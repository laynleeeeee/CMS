package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link ArInvoice}.

 */

@Service
public class ArInvoiceAppPlugin extends MultiFormPlugin {
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ArInvoiceDao arInvoiceDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ArInvoice> arInvoices = arInvoiceDao.getArInvoices(typeId, param);
		for (ArInvoice arInvoice : arInvoices.getData()) {
			String shortDescription = "<b> ARI No. " + arInvoice.getSequenceNo() + "</b>"
					+ " " + DateUtil.formatDate(arInvoice.getDate())
					+ " " + companyDao.get(arInvoice.getCompanyId()).getName()
					+ " " + arCustomerDao.get(arInvoice.getArCustomerId()).getName();
			FormWorkflow workflow = arInvoice.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(arInvoice.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(),
					log.getCreatedDate(), highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, arInvoices.getTotalRecords());
	}
}
