package eulap.eb.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.EmployeeProfileDao;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for Total Employee Per Branch Report.

 *
 */
@Service
public class EmployeePerBranchReportService {
	@Autowired
	private EmployeeProfileDao employeeDao;

	/**
	 * Get employees per branch report data
	 * @param companyId the company Id
	 * @param divisionId the division Id
	 * @param strStatus the status
	 * @param asOfDate the as of Date
	 * @param isOrderByLastName true if order by last name, else false
	 * @return List of employees per branch
	 */
	public JRDataSource generateEmployeePerBranch(Integer companyId,
			Integer divisionId, String strStatus, Date asOfDate, Boolean isOrderByLastName) {
		SearchStatus status = SearchStatus.getInstanceOf(strStatus);
		EBJRServiceHandler<EmployeeProfile> handler = new JREmployeePerBranchHandler(
				companyId, divisionId, status, employeeDao, asOfDate, isOrderByLastName);
		return new EBDataSource<EmployeeProfile>(handler);
	}

	private static class JREmployeePerBranchHandler implements EBJRServiceHandler<EmployeeProfile> {
		private int companyId;
		private int divisionId;
		private SearchStatus status;
		private EmployeeProfileDao employeeDao;
		private Date asOfDate;
		private Boolean isOrderByLastName;

		private JREmployeePerBranchHandler (Integer companyId,
				Integer divisionId, SearchStatus status, EmployeeProfileDao employeeDao, Date asOfDate, Boolean isOrderByLastName){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.status = status;
			this.employeeDao = employeeDao;
			this.asOfDate = asOfDate;
			this.isOrderByLastName = isOrderByLastName;
		}

		@Override
		public void close() throws IOException {
			employeeDao = null;
		}

		@Override
		public Page<EmployeeProfile> nextPage(PageSetting pageSetting) {
			Page<EmployeeProfile> result = employeeDao.getEmployeePerBranch(companyId, divisionId, status, 
					asOfDate, isOrderByLastName, pageSetting);
			return new Page<EmployeeProfile>(pageSetting, result.getData(), result.getTotalRecords());
		}
	}
}
