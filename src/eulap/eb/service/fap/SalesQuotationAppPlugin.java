package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.SalesQuotationDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesQuotation;
import eulap.eb.service.CompanyService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link SalesQuotation}.

 */

@Service
public class SalesQuotationAppPlugin extends FormApprovalPlugin {
	@Autowired
	private SalesQuotationDao salesQuotationDao;
	@Autowired
	private CompanyService companyService;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<SalesQuotation> saleQuotations = salesQuotationDao.getSalesQuotations(searchParam, param.getStatuses(), param.getPageSetting());
		Company company = null;
		for (SalesQuotation sq : saleQuotations.getData()) {
			company = companyService.getCompany(sq.getCompanyId());
			String shortDescription = "<b>SQ No. " + sq.getSequenceNumber() + "</b>"
					+ " " + company.getName() + " " + sq.getArCustomer().getName()
					+ (sq.getDate() != null ? DateUtil.formatDate(sq.getDate()) : "");
			FormWorkflow workflow = sq.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(sq.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(),
					log.getCreatedDate(), highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, saleQuotations.getTotalRecords());
	}
}
