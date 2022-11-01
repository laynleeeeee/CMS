package eulap.eb.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.PayrollEmployeeSalaryDao;
import eulap.eb.domain.hibernate.PayrollEmployeeSalary;
import eulap.eb.service.payroll.PayrollService;

/**
 * Class that will handle the business for Total Salary Per Branch Report.

 *
 */
@Service
public class TotalSalaryPerBranchService {
	@Autowired
	private PayrollEmployeeSalaryDao payrollEmployeeSalaryDao;
	@Autowired
	private PayrollService dssdPayrollService;

	/**
	 * Get the list of Payroll employee salary.
	 * @param companyId The branch or company id.
	 * @param divisionId The department or division id.
	 * @param statusId The employee civil status.
	 * @param dateFrom The date ranged stated.
	 * @param dateTo The range end.
	 * @param isOrderByLastName true if order by last name, else false
	 * @return The list of payroll employee salary.
	 */
	public List<PayrollEmployeeSalary> getSalaryPerBranches(Integer companyId,
			Integer divisionId, Integer statusId, Date dateFrom, Date dateTo, Boolean isOrderByLastName){
		Page<PayrollEmployeeSalary> result = payrollEmployeeSalaryDao.getTotalSalaryPerBranch(companyId, divisionId, statusId, dateFrom,
				dateTo, isOrderByLastName, new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT));
		for (PayrollEmployeeSalary pes : result.getData()) {
			dssdPayrollService.setGrossAndNetPay(pes);
		}
		List<PayrollEmployeeSalary> employeeSalaries = (List<PayrollEmployeeSalary>) result.getData();
		if(!isOrderByLastName) {
			Collections.sort(employeeSalaries, new Comparator<PayrollEmployeeSalary>() {
				@Override
				public int compare(PayrollEmployeeSalary p1, PayrollEmployeeSalary p2) {
					return p1.getEmployee().getEmployeeNo().compareTo(p2.getEmployee().getEmployeeNo());
				}
			});
		}
		return employeeSalaries;
	}
}
