package eulap.eb.service.fap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.eb.dao.PayrollDao;
import eulap.eb.dao.PayrollTimePeriodDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * A class that retrieves the approval for payroll.

 *
 */
@Service
public class PayrollApprovalPlugin extends FormApprovalPlugin{
	@Autowired
	private PayrollDao payrollDao;
	@Autowired
	private PayrollTimePeriodDao payrollTimePeriodDao;
	@Autowired
	private PayrollTimePeriodScheduleDao payrollTimePeriodScheduleDao;
	@Autowired
	private DivisionService divisionService;
	@Autowired CompanyService companyService;
	@Override
	public Page<FormApprovalDto> retrieveForms(FormPluginParam param) {
		List<FormApprovalDto> ret = new ArrayList<FormApprovalDto>();
		ApprovalSearchParam searchParam = ApprovalSearchParam.parseSearchCriteria(param.getSearchCriteria());
		searchParam.setUser(param.getUser());
		Page<Payroll> payrolls = payrollDao.getPayrolls(searchParam, param.getStatuses(), param.getPageSetting());
		for (Payroll p : payrolls.getData()) {
			Company company = companyService.getCompany(p.getCompanyId());
			Division division = divisionService.getDivision(p.getDivisionId());
			PayrollTimePeriod timePeriod = payrollTimePeriodDao.get(p.getPayrollTimePeriodId());
			PayrollTimePeriodSchedule schedule = payrollTimePeriodScheduleDao.get(p.getPayrollTimePeriodScheduleId());
			String shortDescription = "<b>P No. " + p.getSequenceNumber() + "</b>" +
					" " + company.getName()+" " + division.getName() +
					" " +  timePeriod.getName() + " " + schedule.getName() + 
					" (" + DateUtil.formatDate(schedule.getDateFrom()) + " - "
					+ DateUtil.formatDate(schedule.getDateTo()) + ")" ;
			FormWorkflow workflow = p.getFormWorkflow();
			FormWorkflowLog log = workflow.getCurrentLogStatus();
			boolean highlight = workflowServiceHandler.hasAccessRighToNextWF(param.getWorkflowName(), workflow, param.getUser());
			ret.add(FormApprovalDto.getInstanceBy(p.getId(), shortDescription.toString(),
					workflow.getCurrentFormStatus().getDescription(),
					log.getCreated(), 
					log.getCreatedDate(),
					highlight));
		}
		return  new Page<FormApprovalDto>(param.getPageSetting(), ret, payrolls.getTotalRecords()); 
	}
}
