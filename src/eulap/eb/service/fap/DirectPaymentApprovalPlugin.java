package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.DirectPaymentDao;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.service.SupplierService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approved and for approval of Direct Payments.

 *
 */
@Service
public class DirectPaymentApprovalPlugin extends FormApprovalPlugin {
	@Autowired
	private DirectPaymentDao directPaymentDao;
	@Autowired
	private SupplierService supplierService;

	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<DirectPayment> drPayments = directPaymentDao.getAllDirectPayments(searchParam,
				param.getStatuses(), param.getPageSetting());
		ApPayment ap = null;
		for(DirectPayment drPayment : drPayments.getData()) {
			ap = drPayment.getApPayment();
			ap.setSupplier(supplierService.getSupplier(ap.getSupplierId()));
			String voucherNoLabel = drPayment.getDirectPaymentTypeId().equals(DirectPayment.CASH_DP_TYPE_ID) ? "Cash " : "Check ";
			String shortDescription = "<b>" + voucherNoLabel + "Voucher No : " + ap.getVoucherNumber() + "</b>" +
					" " + DateUtil.formatDate(ap.getPaymentDate()) +
					" " + ap.getSupplier().getName() +
					" " + NumberFormatUtil.format(ap.getAmount());
			FormWorkflow workflow = ap.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(drPayment.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, drPayments.getTotalRecords());
	}
}
