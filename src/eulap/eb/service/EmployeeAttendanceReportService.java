package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeDtrDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.EmployeeAttendanceReportDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * A class that will handle the business logic for generating employee attendance report

 */

@Service
public class EmployeeAttendanceReportService {
	@Autowired
	private EmployeeDtrDao empDtrDao;
	@Autowired
	private EmployeeProfileService employeeProfileService;
	/**
	 * Get the employee attendance report report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param isOrderByLastName true if order by last name, else false
	 * @return
	 */
	public JRDataSource generateEmpAttendanceReport(Integer companyId, Integer divisionId,
			Date dateFrom, Date dateTo, Boolean isOrderByLastName) {
		EBJRServiceHandler<EmployeeAttendanceReportDto> handler = new JREmpAttendanceHandler(companyId,
				divisionId, dateFrom, dateTo, isOrderByLastName, this);
		return new EBDataSource<EmployeeAttendanceReportDto>(handler);
	}

	private static class JREmpAttendanceHandler implements EBJRServiceHandler<EmployeeAttendanceReportDto> {
		private final Integer companyId;
		private final Integer divisionId;
		private final Date dateFrom;
		private final Date dateTo;
		private final boolean isOrderByLastName;
		private EmployeeAttendanceReportService empAttendanceReportService;

		public JREmpAttendanceHandler (Integer companyId, Integer divisionId, Date dateFrom,
				Date dateTo, Boolean isOrderByLastName,EmployeeAttendanceReportService empAttendanceReportService) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.isOrderByLastName = isOrderByLastName;
			this.empAttendanceReportService = empAttendanceReportService;
		}

		@Override
		public void close() throws IOException {
			empAttendanceReportService = null;
		}

		@Override
		public Page<EmployeeAttendanceReportDto> nextPage(PageSetting pageSetting) {
			Page<EmployeeAttendanceReportDto> empAttendanceReport =
					empAttendanceReportService.empDtrDao.getEmployeeAttendanceReport(companyId, divisionId,
							dateFrom, dateTo, isOrderByLastName, pageSetting);
			if (empAttendanceReport != null) {
				for (EmployeeAttendanceReportDto dto : empAttendanceReport.getData()) {
					EmployeeShift employeeShift = empAttendanceReportService.employeeProfileService
							.getEeCurrentShift(dto.getEmployeeId(), dto.getTimestamp());
					if(employeeShift != null) {
						dto.setShiftName(employeeShift.getName());
					}
					employeeShift = null;
				}
			}
			return empAttendanceReport;
		}
	}
}
