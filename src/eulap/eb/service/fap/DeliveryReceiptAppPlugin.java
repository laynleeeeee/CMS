package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.DeliveryReceiptType;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval form for {@link DeliveryReceipt}.

 */

@Service
public class DeliveryReceiptAppPlugin extends MultiFormPlugin {
	@Autowired
	private DeliveryReceiptDao deliveryReceiptDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private CompanyDao companyDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<DeliveryReceipt> drs = deliveryReceiptDao.getDeliveryReceipts(typeId, param);
		for (DeliveryReceipt dr : drs.getData()) {
			String formCode = "";
			if (DeliveryReceiptType.DR_TYPE_ID == typeId) {
				formCode = "DR I";
			} else if(DeliveryReceiptType.WAYBILL_DR_TYPE_ID == typeId) {
				formCode = "WB";
			} else if(DeliveryReceiptType.EQ_UTIL_DR_TYPE_ID == typeId) {
				formCode = "EQ";
			} else if (DeliveryReceiptType.DR_SERVICE_TYPE_ID == typeId) {
				formCode = "DR S";
			}
			String shortDescription = "<b>"+ formCode +" No. " + dr.getSequenceNo() + "</b>"
					+ " " + DateUtil.formatDate(dr.getDate())
					+ " " + companyDao.get(dr.getCompanyId()).getName()
					+ " " + arCustomerDao.get(dr.getArCustomerId()).getName();
			FormWorkflow workflow = dr.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(),
					workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(dr.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(), log.getCreated(),
					log.getCreatedDate(), highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, drs.getTotalRecords());
	}
}
