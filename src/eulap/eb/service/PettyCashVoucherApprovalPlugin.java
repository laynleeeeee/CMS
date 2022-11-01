package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.PettyCashVoucherDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Plugin for {@link PettyCashVoucher}

 *
 */
@Service
public class PettyCashVoucherApprovalPlugin extends MultiFormPlugin{
	@Autowired
	private PettyCashVoucherDao pcvDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<PettyCashVoucher> pcvs = pcvDao.getPettyCashVouchers(typeId, searchParam, param.getStatuses(), param.getPageSetting());
		for(PettyCashVoucher pcv : pcvs.getData()){
			String shortDesc = "<b> PCV No: " +pcv.getSequenceNo()+ "</b>" +
						" " + pcv.getCompany().getName() +
						" " + pcv.getDivision().getName() +
						" " + pcv.getUserCustodian().getCustodianAccount().getCustodianName() +
						" " + DateUtil.formatDate(pcv.getPcvDate()) +
						" " + pcv.getRequestor();
			FormWorkflow workflow = pcv.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(pcv.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), res, pcvs.getTotalRecords());
	}
}
