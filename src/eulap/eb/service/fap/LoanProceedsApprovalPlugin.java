package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.LoanProceedsDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.service.SupplierService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of Loan Proceeds.

 *
 */
@Service
public class LoanProceedsApprovalPlugin extends MultiFormPlugin {
	@Autowired
	private LoanProceedsDao loanProceedsDao;
	@Autowired
	private SupplierService supplierService;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<LoanProceeds> loanProceeds = loanProceedsDao.getAllLoanProceeds(searchParam,
				param.getStatuses(), param.getPageSetting(), typeId);
		for(LoanProceeds proceeds : loanProceeds.getData()) {
			proceeds.setSupplier(supplierService.getSupplier(proceeds.getSupplierId()));
			String shortDescription = "<b>LP No : " + proceeds.getSequenceNumber() +
					" DATE : " + DateUtil.formatDate(proceeds.getDate()) +
					" GL DATE : " + DateUtil.formatDate(proceeds.getDate()) + "</b>" +
					" " + proceeds.getSupplier().getName() +
					" " + NumberFormatUtil.format(proceeds.getAmount());
			FormWorkflow workflow = proceeds.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(proceeds.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, loanProceeds.getTotalRecords());
	}
}
