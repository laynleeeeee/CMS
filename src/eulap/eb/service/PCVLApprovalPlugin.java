package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.PettyCashVoucherLiquidationDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Plugin for {@link PettyCashVoucherLiquidation}

 *
 */
@Service
public class PCVLApprovalPlugin extends MultiFormPlugin{
	@Autowired
	private PettyCashVoucherLiquidationDao pcvlDao;

	@Override
	public Page<FormApprovalDto> retrieveForms(int typeId, FormPluginParam param) {
		List<FormApprovalDto> res = new ArrayList<>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<PettyCashVoucherLiquidation> pcvls = pcvlDao.getPettyCashVoucherLiquidations(typeId, searchParam,
				param.getStatuses(), param.getPageSetting());
		for(PettyCashVoucherLiquidation pcvl : pcvls.getData()){
			String shortDesc = "<b> PCVL No: " +pcvl.getSequenceNo()+ "</b>" +
						" " + pcvl.getCompany().getName() +
						" " + pcvl.getDivision().getName() +
						" " + pcvl.getUserCustodian().getCustodianAccount().getCustodianName() +
						" " + DateUtil.formatDate(pcvl.getPcvlDate()) +
						" " + pcvl.getRequestor();
			FormWorkflow workflow = pcvl.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			res.add(FormApprovalDto.getInstanceBy(pcvl.getId(), shortDesc.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(),
					log.getCreatedDate(),
					highlight));
		}
		return new Page<FormApprovalDto>(param.getPageSetting(), res, pcvls.getTotalRecords());
	}
}
