package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.BankAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.CurrencyUtil;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of AP Payments.

 *
 */
@Service
public class APPaymentApprovalPlugin extends MultiFormPlugin{
	@Autowired
	private ApPaymentDao apPaymentDao;
	@Autowired
	private BankAccountDao bankAcctDao;
	@Autowired
	private SupplierDao supplierDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int divisionId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<ApPayment> payments = apPaymentDao.getAllApPaymentForms(divisionId, searchParam, param.getStatuses(), param.getPageSetting());
		for(ApPayment ap : payments.getData()) {
			double currencyRateValue = ap.getCurrencyRateValue();
			String voucherNo = ap.getVoucherNumber() != null  ? "<b> Voucher No : " + ap.getVoucherNumber() + "</b>" : "";
			String shortDescription = voucherNo + " " + DateUtil.formatDate(ap.getPaymentDate())
				+ " " + bankAcctDao.get(ap.getBankAccountId()).getName()
				+ " " + ap.getCheckNumber()
				+ " " + DateUtil.formatDate(ap.getCheckDate())
				+ " " + supplierDao.get(ap.getSupplierId()).getName()
				+ " " + ap.getCurrency().getName()
				+ " " + NumberFormatUtil.formatNumber(CurrencyUtil.convertMonetaryValues(ap.getAmount(), currencyRateValue), 2);
			FormWorkflow workflow = ap.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(ap.getId(), shortDescription.toString(), workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), log.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, payments.getTotalRecords());
	}
}
