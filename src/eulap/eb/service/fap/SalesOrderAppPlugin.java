package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.service.CompanyService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link SalesOrder}.

 *
 */
@Service
public class SalesOrderAppPlugin extends MultiFormPlugin {
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private CompanyService companyService;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<SalesOrder> saleOrders = salesOrderDao.getSalesOrders(typeId, searchParam, param.getStatuses(), param.getPageSetting());
		Company company = null;
		for (SalesOrder so : saleOrders.getData()) {
			company = companyService.getCompany(so.getCompanyId());
			String shortDescription = "<b>SO No. " + so.getSequenceNumber() + "</b>"
					+ " " + company.getName() + " " + so.getArCustomer().getName() + " "
					+ (so.getDate() != null ? DateUtil.formatDate(so.getDate()) : "");
			FormWorkflow workflow = so.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(so.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(),
					log.getCreatedDate(), highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, saleOrders.getTotalRecords());
	}
}
