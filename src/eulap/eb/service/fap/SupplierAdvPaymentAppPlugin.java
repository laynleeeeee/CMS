package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link SupplierAdvancePayment}.

 */

@Service
public class SupplierAdvPaymentAppPlugin extends MultiFormPlugin {
	@Autowired
	private SupplierAdvPaymentDao advPaymentDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<SupplierAdvancePayment> supplierAdvPayments = advPaymentDao.getSupplierAdvPayments(typeId, param);
		for (SupplierAdvancePayment sap : supplierAdvPayments.getData()) {
			String shortDescription = "<b>SAP No. " + sap.getSequenceNumber() + "</b>"
					+ " " + DateUtil.formatDate(sap.getDate())
					+ " " + sap.getDivision().getName()
					+ " " + sap.getSupplier().getName()
					+ " " + NumberFormatUtil.format(sap.getAmount());
			FormWorkflow workflow = sap.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(sap.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(),
					log.getCreatedDate(), highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), ret, supplierAdvPayments.getTotalRecords());
	}

}
