package eulap.eb.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.PayrollTimePeriodDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.domain.hibernate.PayrollTimePeriod;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.User;

/**
 * Service class that will handle business logic for {@link PayrollTimePeriod}

 *
 */
@Service
public class PayrollTimePeriodService {
	private static final Logger logger = Logger.getLogger(PayrollTimePeriodService.class);
	@Autowired
	private PayrollTimePeriodDao pTimePeriodDao;
	@Autowired
	private PayrollTimePeriodScheduleDao pTimePeriodScheduleDao;

	/**
	 * Get the payroll time period by id
	 * @param pId The payroll time period by id
	 * @return The payroll time period object
	 */
	public PayrollTimePeriod getPayrollTimePeriod(Integer pId) {
		return pTimePeriodDao.get(pId);
	}

	/**
	 * Get the list of payroll time period schedule
	 * by payroll time period id
	 * @param payrollTimePeriodId The payroll time period id
	 * @return The list of payroll time period schedule
	 */
	public List<PayrollTimePeriodSchedule> getPayrollTimePeriodSchedules(Integer payrollTimePeriodId) {
		return pTimePeriodScheduleDao.getTimePeriodSchedules(payrollTimePeriodId);
	}

	/**
	 * Save the payroll time period form
	 * @param payrollTimePeriod The payroll time period object
	 * @param user The logged user
	 */
	public void savePayrollTimePeriodForm(PayrollTimePeriod payrollTimePeriod, User user) {
		Integer payrollTimePeriodId = payrollTimePeriod.getId();
		boolean isNew = payrollTimePeriodId == 0;
		AuditUtil.addAudit(payrollTimePeriod, new Audit(user.getId(), isNew, new Date()));
		payrollTimePeriod.setName(payrollTimePeriod.getName().trim());

		List<PayrollTimePeriodSchedule> savedPTPSched = null;
		if(!isNew) {
			PayrollTimePeriod savePtp = getPayrollTimePeriod(payrollTimePeriodId);
			DateUtil.setCreatedDate(payrollTimePeriod, savePtp.getCreatedDate());
			savedPTPSched = getPayrollTimePeriodSchedules(payrollTimePeriodId);
		}
		logger.debug("Saving the payroll time period: "+payrollTimePeriod);
		pTimePeriodDao.saveOrUpdate(payrollTimePeriod);

		//Save the Payroll time period schedule
		List<PayrollTimePeriodSchedule> timePeriodSchedules = payrollTimePeriod.getPayrollTimePeriodSchedules();
		if(timePeriodSchedules != null && !timePeriodSchedules.isEmpty()){
			List<PayrollTimePeriodSchedule> toBeDeleted = new ArrayList<>();
			if (savedPTPSched != null && !savedPTPSched.isEmpty()) {
				toBeDeleted.addAll(savedPTPSched);
				toBeDeleted.removeAll(timePeriodSchedules);
				savedPTPSched = null;
			}
			if (!toBeDeleted.isEmpty()) {
				for (PayrollTimePeriodSchedule tbd : toBeDeleted) {
					if (!isUsed(tbd)) {
						pTimePeriodScheduleDao.delete(tbd);
					}
				}
				toBeDeleted = null;
			}
			List<Domain> toBeSaved = new ArrayList<>();
			for (PayrollTimePeriodSchedule tps : timePeriodSchedules) {
				tps.setPayrollTimePeriodId(payrollTimePeriod.getId());
				tps.setName(tps.getName().trim());
				toBeSaved.add(tps);
			}
			pTimePeriodScheduleDao.batchSaveOrUpdate(toBeSaved);
		}
		logger.info("Successfully save the payroll time period form.");
	}

	/**
	 * Check if the time period schedule is already is used in the payroll form.
	 * @param schedule The schedule object to be checked.
	 * @return True if used, otherwise false.
	 */
	public boolean isUsed (PayrollTimePeriodSchedule schedule) {
		return pTimePeriodScheduleDao.isUsed(schedule);
	}

	/**
	 * Process the payroll time schedule by removing lines
	 * without the following fields: name, start date and end date
	 * @param tpScheds The list of time period schedule
	 * @return The processed list of payroll time schedule.
	 */
	public List<PayrollTimePeriodSchedule> processTimeScheds (List<PayrollTimePeriodSchedule> tpScheds) {
		List<PayrollTimePeriodSchedule> ret = new ArrayList<PayrollTimePeriodSchedule>();
		if (tpScheds != null && !tpScheds.isEmpty()) {
			for (PayrollTimePeriodSchedule ptps : tpScheds) {
				if ((ptps.getName() != null && !ptps.getName().trim().isEmpty())
						|| ptps.getDateFrom() != null || ptps.getDateTo() != null) {
					//Set the payroll time period schedule
					ret.add(ptps);
				}
			}
		}
		return ret;
	}

	/**
	 * Search for the payroll time periods
	 * @param name The name of the payroll time period
	 * @param pageNumber The page number
	 * @return The list of the payroll time periods
	 * in a paged format
	 */
	public Page<PayrollTimePeriod> searchPayrollTimePeriods(String name, Integer month, Integer year,
			String status, Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return pTimePeriodDao.getPayrollTimePeriods(name, month, year, searchStatus,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Initialize the list of years
	 * @return The list of years
	 */
	public List<Integer> initYears() {
		List<Integer> years = new ArrayList<>();
		int minYear = 2016;
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		int maxYear = (currentYear > minYear ? currentYear : minYear) + 5;
		for (int i=minYear; i<=maxYear; i++) {
			years.add(i);
		}
		return years;
	}

	/**
	 * Evaluate if the time period schedule is a valid period.
	 * @param payrollTimePeriodScheds The list of payroll time periods 
	 * @return True for valid period, otherwise false.
	 */
	public boolean isInvalidTimePeriod(List<PayrollTimePeriodSchedule> payrollTimePeriodScheds) {
		int index1 = 0;
		for (PayrollTimePeriodSchedule ptps : payrollTimePeriodScheds) {
			if(ptps.getDateFrom() != null && ptps.getDateTo() != null && 
					!pTimePeriodScheduleDao.isValidPayrollTimePeriod(ptps)) {
				return true;
			}
			int index2 = 0;
			for (PayrollTimePeriodSchedule ptps2 : payrollTimePeriodScheds) {
				if (index1 != index2) {
					if(ptps.getDateFrom() == null || ptps.getDateTo() == null
							|| ptps2.getDateFrom() == null || ptps2.getDateTo() == null){
						break;
					}
					if (ptps.getDateFrom().equals(ptps2.getDateTo()) 
						|| ptps.getDateFrom().before(ptps2.getDateTo())
						&& (ptps2.getDateFrom().equals(ptps.getDateTo())
						|| ptps2.getDateFrom().before(ptps.getDateTo()))) {
						return true;
					}
				}
				index2++;
			}
			index1++;
		}
		return false;
	}

	/**
	 * The payroll time period name
	 * @param payrollTimePeriod The payroll time period
	 * @return True if the payroll time period name is unique, otherwise false.
	 */
	public boolean isUniqueName(PayrollTimePeriod payrollTimePeriod) {
		return pTimePeriodDao.isUniqueName(payrollTimePeriod);
	}

	/**
	 * Check if there is no contributions.
	 * @param payrollTimePeriodScheds The list of payroll time periods.
	 * @return True if there is no contributions, otherwise false.
	 */
	public boolean hasNoContribuition (List<PayrollTimePeriodSchedule> payrollTimePeriodScheds) {
		for (PayrollTimePeriodSchedule ptps : payrollTimePeriodScheds) {
			if(ptps.isComputeContributions()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the list of payroll time period schedules by month and year.
	 * @param month The month filter.
	 * @param year The year filter.
	 * @return The list of payroll time period schedule
	 */
	public List<PayrollTimePeriodSchedule> getTimePeriodSchedules(Integer month, Integer year) {
		return pTimePeriodScheduleDao.getTimePeriodSchedules(month, year, false);
	}

	/**
	 * Get the {@link PayrollTimePeriodSchedule} by id.
	 * @param ptpSceduleId The payroll time period schedule id.
	 * @return The {@link PayrollTimePeriodSchedule} object.
	 */
	public PayrollTimePeriodSchedule getPayrollTimePeriodSchedule(int ptpSceduleId) {
		return pTimePeriodScheduleDao.get(ptpSceduleId);
	}

	/**
	 *  Evaluates if the Month and Year already exists.
	 * @param payrollTimePeriod The payroll time period.
	 * @return True if the payroll time period month and year is unique, else false.
	 */
	public boolean isUniqueMonthAndYearCombi(PayrollTimePeriod payrollTimePeriod){
		return pTimePeriodDao.isUniqueMonthAndYearCombi(payrollTimePeriod);
	}

	/**
	 * Evaluates if the payroll time period schedule name has duplicate entry.
	 * @param payrollTimePeriodSchedules The list of Payroll Time Period Schedule.
	 * @return True if has duplicate, otherwise false.
	 */
	public boolean hasDuplicatePayrollTimePeriodShedName(List<PayrollTimePeriodSchedule> payrollTimePeriodSchedules){
		if (payrollTimePeriodSchedules != null && !payrollTimePeriodSchedules.isEmpty()) {
			Map<String, PayrollTimePeriodSchedule> hmSchedules = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			for (PayrollTimePeriodSchedule ptps : payrollTimePeriodSchedules) {
				if(ptps.getName() != null) {
					if (hmSchedules.containsKey(ptps.getName().trim())) {
						return true;
					} else {
						hmSchedules.put(ptps.getName().trim(), ptps);
					}
				} else {
					continue;
				}
			}
		}
		return false;
	}

	public List<PayrollTimePeriodSchedule> getTimePeriodSchedules(Integer month, Integer year,
			Integer payrollTimePeriodScheduleId) {
		List<PayrollTimePeriodSchedule> payrollTimePeriodSchedules = pTimePeriodScheduleDao.getTimePeriodSchedules(month, year, true);
		if(payrollTimePeriodScheduleId != null){
			Collection<Integer> payrollTimePeriodIds = new ArrayList<Integer>();
			for (PayrollTimePeriodSchedule payrollTimePeriodSchedule : payrollTimePeriodSchedules) {
				if(payrollTimePeriodScheduleId.equals(payrollTimePeriodSchedule.getId())){
					payrollTimePeriodIds.add(payrollTimePeriodSchedule.getId());
				}
			}
			if(!payrollTimePeriodIds.contains(payrollTimePeriodScheduleId)) {
				payrollTimePeriodSchedules.add(pTimePeriodScheduleDao.get(payrollTimePeriodScheduleId));
			}
		}
		return payrollTimePeriodSchedules;
	}
}
