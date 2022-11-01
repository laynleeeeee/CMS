package eulap.eb.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.EmployeeProfileDao;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that will handle the business logif for Employee For Regularization Report.

 *
 */
@Service
public class EmployeeForRegularizationService {
	@Autowired
	private EmployeeProfileDao employeeDao;

	/**
	 * Get Employees for regularization
	 * @param companyId the company Id
	 * @param divisionId the division Id
	 * @param dateFrom the date From
	 * @param dateTo the date To
	 * @param isOrderByLastName true if order by last name, else false
	 * @return list of employees for regularization
	 */
	public JRDataSource generateEmployeeForRegtularization(Integer companyId,
			Integer divisionId, Date dateFrom, Date dateTo, Boolean isOrderByLastName) {
		EBJRServiceHandler<EmployeeProfile> handler = new JREmployeeForRegularizationHandler(companyId,
				divisionId, dateFrom, dateTo, isOrderByLastName, employeeDao);
		return new EBDataSource<EmployeeProfile>(handler);
	}

	private static class JREmployeeForRegularizationHandler implements EBJRServiceHandler<EmployeeProfile> {
		private int companyId;
		private int divisionId;
		private Date dateFrom;
		private Date dateTo;
		private Boolean isOrderByLastName;
		private EmployeeProfileDao employeeDao;

		private JREmployeeForRegularizationHandler (Integer companyId, Integer divisionId, Date dateFrom,
				Date dateTo, Boolean isOrderByLastName, EmployeeProfileDao employeeDao) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.isOrderByLastName = isOrderByLastName;
			this.employeeDao = employeeDao;
		}

		@Override
		public void close() throws IOException {
			employeeDao = null;
		}

		@Override
		public Page<EmployeeProfile> nextPage(PageSetting pageSetting) {
			Page<EmployeeProfile> result = employeeDao.getEmployeeForRegularization(companyId, divisionId, dateFrom, 
					dateTo, isOrderByLastName, pageSetting);
			return new Page<>(pageSetting, result.getData(), result.getTotalRecords());
		}
	}

	/**
	 * Get Employees for regularization by as of date.
	 * @param companyId the company Id
	 * @param divisionId the division Id
	 * @param dateFrom the date From
	 * @param dateTo the date To
	 * @param isOrderByLastName true if order by last name, else false
	 * @return list of employees for regularization
	 */
	public JRDataSource generateEmployeeForRegularizationAsOfDate(Integer companyId,
			Integer divisionId, Date asOfDate, Boolean isOrderByLastName) {
		EBJRServiceHandler<EmployeeProfile> handler = new JREmployeeForRegularizationAsOfDateHandler(companyId,
				divisionId, asOfDate, isOrderByLastName, employeeDao);
		return new EBDataSource<EmployeeProfile>(handler);
	}

	private static class JREmployeeForRegularizationAsOfDateHandler implements EBJRServiceHandler<EmployeeProfile> {
		private int companyId;
		private int divisionId;
		private Date asOfDate;
		private Boolean isOrderByLastName;
		private EmployeeProfileDao employeeDao;

		private JREmployeeForRegularizationAsOfDateHandler (Integer companyId, Integer divisionId, Date asOfDate,
				Boolean isOrderByLastName, EmployeeProfileDao employeeDao) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.asOfDate = asOfDate;
			this.isOrderByLastName = isOrderByLastName;
			this.employeeDao = employeeDao;
		}

		@Override
		public void close() throws IOException {
			employeeDao = null;
		}

		@Override
		public Page<EmployeeProfile> nextPage(PageSetting pageSetting) {
			Page<EmployeeProfile> result = employeeDao.getEmployeeForRegularizationAsOfDate(
					companyId, divisionId, asOfDate, isOrderByLastName, pageSetting);
			return new Page<>(pageSetting, result.getData(), result.getTotalRecords());
		}
	}
}
