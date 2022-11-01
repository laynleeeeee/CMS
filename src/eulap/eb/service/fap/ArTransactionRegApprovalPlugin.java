package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.ArTransactionService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for apporval of AR Transactions.

 *
 */
@Service
public class ArTransactionRegApprovalPlugin extends MultiFormPlugin{
	@Autowired
	private ArTransactionService arTransactionService;
	@Autowired
	private ArTransactionDao arTransactionDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ArTransaction> transactions = arTransactionDao.getAllArTransaction(typeId, null, searchParam, param.getStatuses(), param.getPageSetting());
		for (ArTransaction transaction : transactions.getData()) {
			String shortDescription = "<b> " + arTransactionService.getTransactionPrefix(typeId, transaction) + "</b>" +
					" " + transaction.getArTransactionType().getName() +
					" " + DateUtil.formatDate(transaction.getTransactionDate()) +
					" " + transaction.getArCustomer().getName() +
					(transaction.getDueDate() != null ? " <b>DUE DATE:</b> " + DateUtil.formatDate(transaction.getDueDate()) : "") +
					" " + NumberFormatUtil.format(transaction.getAmount());
			FormWorkflow workflow = transaction.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(transaction.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, transactions.getTotalRecords());
	}
}
