package eulap.eb.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.DailyShiftScheduleDao;
import eulap.eb.dao.DailyShiftScheduleLineDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.DailyShiftSchedule;
import eulap.eb.domain.hibernate.DailyShiftScheduleLine;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.DSSLineDto;
import eulap.eb.web.dto.DailyShiftScheduleDto;
import eulap.eb.web.dto.MaxDayPerScheduleLine;

/**
 * Service class that will handle business logic for {@link DailyShiftSchedule}

 *
 */
@Service
public class DailyShiftScheduleService {
	private static final Logger logger = Logger.getLogger(DailyShiftScheduleService.class);
	@Autowired
	private DailyShiftScheduleDao dailyShiftScheduleDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private PayrollTimePeriodScheduleDao scheduleDao;
	@Autowired
	private DailyShiftScheduleLineDao scheduleLineDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;

	/**
	 * Get the daily shift schedule by id.
	 * @param pId The daily shift schedule id.
	 * @return The daily shift schedule object.
	 */
	public DailyShiftSchedule getDaiyShiftSchedule(Integer pId, boolean isFirstNameFirst) {
		DailyShiftSchedule dailyShiftSchedule = dailyShiftScheduleDao.get(pId);
		setEmployeeScheduleSheet(dailyShiftSchedule, isFirstNameFirst);
		return dailyShiftSchedule;
	}

	/**
	 * Set employee schedule sheet.
	 */
	private void setEmployeeScheduleSheet(DailyShiftSchedule dailyShiftSchedule, boolean isFirstNameFirst){
		List<DailyShiftScheduleLine> shiftScheduleLines = scheduleLineDao.getAllByRefId("dailyShiftScheduleId", dailyShiftSchedule.getId());
		List<DailyShiftScheduleDto> dailyShiftScheduleDtos = new ArrayList<>();
		DailyShiftScheduleDto shiftScheduleDto = new DailyShiftScheduleDto();
		Employee employee = null;
		List<DSSLineDto> dssLineDtos = null;
		DSSLineDto dssLineDto = null;
		Map<String, DailyShiftScheduleDto> map = new HashMap<>();
		EmployeeShift employeeShift = null;
		Integer employeeShiftId = null;
		Integer employeeId = null;
		Integer companyId = dailyShiftSchedule.getCompanyId();
		Integer payrollTimePeriodScheduleId = dailyShiftSchedule.getPayrollTimePeriodScheduleId();
		Integer divisionId = dailyShiftSchedule.getDivisionId();
		boolean isEdit = dailyShiftSchedule.getId() != 0;
		if (isEdit) {
			List<DailyShiftScheduleLine> newEmployeeScheds = addSchedForNewEmployees(companyId,
					divisionId, payrollTimePeriodScheduleId);
			if (!newEmployeeScheds.isEmpty()) {
				shiftScheduleLines.addAll(newEmployeeScheds);
			}
			shiftScheduleLines.addAll(appendNewScheds(companyId, divisionId,
					payrollTimePeriodScheduleId));
		}
		for (DailyShiftScheduleLine dailyShiftScheduleLine : shiftScheduleLines) {
			if (dailyShiftScheduleLine.getEmployeeShiftId() != null) {
				employeeShiftId = dailyShiftScheduleLine.getEmployeeShiftId();
				employeeShift = employeeShiftDao.get(employeeShiftId);
			}
			shiftScheduleDto = new DailyShiftScheduleDto();
			employeeId = dailyShiftScheduleLine.getEmployeeId();
			employee = employeeDao.get(employeeId);
			shiftScheduleDto.setEmployee(employee);
			shiftScheduleDto.setEmployeeId(employeeId);
			shiftScheduleDto.setEmployeeName(isFirstNameFirst ? employee.getEmployeeFullName() : employee.getFullName());
			dssLineDto = new DSSLineDto();
			dssLineDto.setDate(dailyShiftScheduleLine.getDate());
			dssLineDto.setOrigEmployeeShiftId(employeeShiftId);
			dssLineDto.setEmployeeShiftId(employeeShiftId);
			dssLineDto.setShiftName(employeeShift != null ? employeeShift.getName() : null);
			dssLineDto.setActive(employeeShift != null ? employeeShift.isActive() : false);
			String key = "e"+employeeId;
			if (map.containsKey(key)) {
				shiftScheduleDto = map.get(key);
				dssLineDtos = shiftScheduleDto.getDssLineDtos();
				dssLineDtos.add(dssLineDto);
				shiftScheduleDto.setDssLineDtos(dssLineDtos);
				map.put(key, shiftScheduleDto);
			} else {
				dssLineDtos = new ArrayList<>();
				dssLineDtos.add(dssLineDto);
				shiftScheduleDto.setDssLineDtos(dssLineDtos);
				map.put(key, shiftScheduleDto);
			}
			employeeShift = null;
			employeeShiftId = null;
		}
		if (!map.isEmpty()) {
			dailyShiftScheduleDtos = new ArrayList<>(map.values());
		}
		sortDailyShiftScheduleDTO(dailyShiftScheduleDtos, isFirstNameFirst);
		dailyShiftSchedule.setScheduleSheetDtos(dailyShiftScheduleDtos);
	}

	private List<DailyShiftScheduleLine> addSchedForNewEmployees(Integer companyId, Integer divisionId,
			Integer payrollTimePeriodScheduleId) {
		List<DailyShiftScheduleLine> ret = new ArrayList<>();
		List<Employee> employees = employeeDao.getEmployeeBySchedule(companyId, divisionId,
				payrollTimePeriodScheduleId, false);
		PayrollTimePeriodSchedule schedule = scheduleDao.get(payrollTimePeriodScheduleId);
		if (schedule != null) {
			Date currentDate = null;
			if (employees != null && !employees.isEmpty())  {
				List<EmployeeShift> shifts = employeeShiftDao.getAllActive();
				DailyShiftScheduleLine dssl = null;
				for (Employee e : employees) {
					currentDate = schedule.getDateFrom();
					while (!currentDate.after(schedule.getDateTo())) {
						dssl = new DailyShiftScheduleLine();
						dssl.setEmployeeId(e.getId());
						dssl.setEmployee(e);
						dssl.setDate(currentDate);
						dssl.setEmployeeShiftId(shifts.iterator().next().getId());
						ret.add(dssl);
						currentDate = DateUtil.addDaysToDate(currentDate, 1);
					}
				}
			}
		}
		return ret;
	}

	private List<DailyShiftScheduleLine> appendNewScheds(Integer companyId, Integer divisionId,
			Integer payrollTimePeriodScheduleId) {
		MaxDayPerScheduleLine ml = scheduleLineDao.getMaxDayPerLine(payrollTimePeriodScheduleId);
		List<Employee> employees = employeeDao.getEmployeeBySchedule(companyId, divisionId,
				payrollTimePeriodScheduleId, true);
		List<DailyShiftScheduleLine> scheds = new ArrayList<>();
		List<EmployeeShift> shifts = employeeShiftDao.getAllActive();
		DailyShiftScheduleLine dssl = null;
		for (Employee e : employees) {
			for (int i=1; i<= ml.getDayDiff(); i++) {
				dssl = new DailyShiftScheduleLine();
				dssl.setEmployeeId(e.getId());
				dssl.setEmployee(e);
				dssl.setDate(DateUtil.addDaysToDate(ml.getMaxDate(), i));
				dssl.setEmployeeShiftId(shifts.iterator().next().getId());
				scheds.add(dssl);
			}
		}
		return scheds;
	}

	/**
	 * Initialize daily shift schedule.
	 */
	public List<DailyShiftScheduleDto> initEmployeeScheduleSheet(
			Integer companyId, List<Date> dates, boolean isFirstNameFirst) {
		List<DailyShiftScheduleDto> dailyShiftScheduleDtos = new ArrayList<>();
		List<Employee> employees = employeeDao.getEmployees(companyId, null, null);
		List<DSSLineDto> dssLineDtos = new ArrayList<>();
		DSSLineDto dssLineDto =  null;
		for (Date date : dates) {
			dssLineDto = new DSSLineDto();
			dssLineDto.setDate(date);
			dssLineDtos.add(dssLineDto);
		}
		DailyShiftScheduleDto shiftScheduleDto = null;
		for (Employee employee : employees) {
			shiftScheduleDto = new DailyShiftScheduleDto();
			shiftScheduleDto.setEmployee(employee);
			shiftScheduleDto.setEmployeeId(employee.getId());
			shiftScheduleDto.setEmployeeName(isFirstNameFirst ?
					employee.getEmployeeFullName() : employee.getFullName());
			shiftScheduleDto.setDssLineDtos(dssLineDtos);
			dailyShiftScheduleDtos.add(shiftScheduleDto);
		}
		sortDailyShiftScheduleDTO(dailyShiftScheduleDtos, isFirstNameFirst);
		return dailyShiftScheduleDtos;
	}

	private void sortDailyShiftScheduleDTO(List<DailyShiftScheduleDto> dailyShiftScheduleDtos,
			boolean isFirstNameFirst){
		if (isFirstNameFirst) {
			Collections.sort(dailyShiftScheduleDtos, new Comparator<DailyShiftScheduleDto>() {
				@Override
				public int compare(DailyShiftScheduleDto o1, DailyShiftScheduleDto o2) {
					return o1.getEmployee().getEmployeeFullName().compareToIgnoreCase(o2.getEmployee().getEmployeeFullName());
				}
			});
		} else {
			Collections.sort(dailyShiftScheduleDtos, new Comparator<DailyShiftScheduleDto>() {

				@Override
				public int compare(DailyShiftScheduleDto o1, DailyShiftScheduleDto o2) {
					return o1.getEmployee().getFullName().compareToIgnoreCase(o2.getEmployee().getFullName());
				}
			});
		}
	}

	/**
	 * Save daily shift schedule.
	 */
	public void saveDailyShiftSchedule(User user, DailyShiftSchedule shiftSchedule) {
		logger.info("Saving Daily Shift Schedule.");
		boolean isNewRecord = shiftSchedule.getId() == 0;
		AuditUtil.addAudit(shiftSchedule, new Audit(user.getId(), isNewRecord, new Date()));
		dailyShiftScheduleDao.saveOrUpdate(shiftSchedule);

		List<DailyShiftScheduleDto> dailyShiftScheduleDtos = shiftSchedule.getScheduleSheetDtos();
		if (!isNewRecord){
			List<DailyShiftScheduleLine> shiftScheduleLines = scheduleLineDao.getAllByRefId("dailyShiftScheduleId", shiftSchedule.getId());
			for (DailyShiftScheduleLine dailyShiftScheduleLine : shiftScheduleLines) {
				scheduleLineDao.delete(dailyShiftScheduleLine);
			}
		}

		DailyShiftScheduleLine scheduleLine = new DailyShiftScheduleLine();
		for (DailyShiftScheduleDto dailyShiftScheduleDto : dailyShiftScheduleDtos) {
			logger.info("Saving daily shift line.");
			for (DSSLineDto dssLineDto : dailyShiftScheduleDto.getDssLineDtos()) {
				scheduleLine = new DailyShiftScheduleLine();
				scheduleLine.setEmployeeId(dailyShiftScheduleDto.getEmployeeId());
				scheduleLine.setEmployeeShiftId(dssLineDto.getEmployeeShiftId());
				scheduleLine.setDate(dssLineDto.getDate());
				scheduleLine.setDailyShiftScheduleId(shiftSchedule.getId());
				scheduleLineDao.save(scheduleLine);
			}
		}
	}

	/**
	 * Save the daily shift schedule form
	 * @param user The current user logged
	 * @param dailyShiftSchedule The daily shift schedule object
	 */
	public void saveDailyShiftScheduleForm(User user, DailyShiftSchedule dailyShiftSchedule) {
		boolean isNewRecord = dailyShiftSchedule.getId() == 0;
		if (isNewRecord) {
			EBObject eb = new EBObject();
			AuditUtil.addAudit(eb, new Audit(user.getId(), true, new Date()));
			eb.setObjectTypeId(DailyShiftSchedule.OBJECT_TYPE_ID);
			ebObjectDao.save(eb);
			dailyShiftSchedule.setEbObjectId(eb.getId());
		} else {
			List<ReferenceDocument> savedRDocs = referenceDocumentDao.getRDsByEbObject(dailyShiftSchedule.getEbObjectId());
			if (savedRDocs != null && !savedRDocs.isEmpty()) {
				List<Integer> ids = new ArrayList<>();
				for (ReferenceDocument rd : savedRDocs) {
					ids.add(rd.getId());
				}
				referenceDocumentDao.delete(ids);
				ids = null;
				savedRDocs = null;
			}
		}

		ReferenceDocument referenceDocument = dailyShiftSchedule.getReferenceDocument();
		if (referenceDocument != null) {
			EBObject ebObject = new EBObject();
			AuditUtil.addAudit(ebObject, new Audit(user.getId(), true, new Date()));
			ebObject.setObjectTypeId(ReferenceDocument.OBJECT_TYPE_ID);
			ebObjectDao.save(ebObject);
			referenceDocument.setEbObjectId(ebObject.getId());
			referenceDocumentDao.save(referenceDocument);
			objectToObjectDao.saveOrUpdate(ObjectToObject.getInstanceOf(dailyShiftSchedule.getEbObjectId(),
					referenceDocument.getEbObjectId(), ReferenceDocument.OR_TYPE_ID, user, new Date()));
		}

		saveDailyShiftSchedule(user, dailyShiftSchedule);
	}

	/**
	 * Search Daily shift schedule.
	 * @param companyId The company id.
	 * @param month The month.
	 * @param year The year.
	 * @param user The user.
	 * @param pageNumber The page number.
	 * @return The list of daily shift schedule.
	 */
	public Page<DailyShiftSchedule> searchDailyShiftSchedule(Integer companyId,
			Integer month, Integer year, User user, Integer pageNumber, boolean isAddOrder) {
		return dailyShiftScheduleDao.getDailyShiftScheduleLines(companyId, month, year,
				user, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD), isAddOrder);
	}

	/**
	 * Get the daily shift schedule lines
	 * @param dailyShiftScheduleId The daily shift schedule id.
	 * @return  The schedule lines.
	 */
	public List<DailyShiftScheduleLine> getScheduleLines (int dailyShiftScheduleId, boolean isFirstNameFirst) {
		return scheduleLineDao.getAllDailyShiftShedule(dailyShiftScheduleId, isFirstNameFirst);
	}

	/**
	 * Check if the daily schedule is unique.
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueSchedule(DailyShiftSchedule shiftSchedule) {
		return dailyShiftScheduleDao.isUniqueSchedule(shiftSchedule);
	}

	/**
	 * Initialize daily shift schedule.
	 */
	public List<DailyShiftScheduleDto> initEmployeeScheduleTemplate(Integer companyId, Integer divisionId, List<Date> dates) {
		List<DailyShiftScheduleDto> dailyShiftScheduleDtos = new ArrayList<>();
		List<Employee> employees = employeeDao.getEmployees(companyId, divisionId, null);
		DailyShiftScheduleDto shiftScheduleDto = null;
		for (Employee employee : employees) {
			for (Date date : dates) {
				shiftScheduleDto = new DailyShiftScheduleDto();
				shiftScheduleDto.setEmployee(employee);
				shiftScheduleDto.setEmployeeId(employee.getId());
				shiftScheduleDto.setShiftDate(date);
				dailyShiftScheduleDtos.add(shiftScheduleDto);
			}
		}
		sortDailyShiftScheduleDTO(dailyShiftScheduleDtos, true);
		return dailyShiftScheduleDtos;
	}

	/**
	 * Convert JSON table to daily shift schedule
	 * @param file The multipart file
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param dates The list of dates
	 * @return The parsed data for daily shift schedule
	 */
	public List<DailyShiftScheduleDto> convJson2DailyShiftSchedule(MultipartFile file, Integer companyId, List<Date> dates) throws Exception {
		byte [] byteArr = file.getBytes();
		InputStream in = new ByteArrayInputStream(byteArr);
		return parseData(in, companyId, dates);
	}

	/**
	 * Parse data for daily shift schedule
	 * @param in The input stream
	 * @param companyId The company id
	 * @param dates The list of dates
	 * @return The parsed data for daily shift schedule
	 */
	public List<DailyShiftScheduleDto> parseData(InputStream in, Integer companyId, List<Date> dates) throws Exception {
		StringBuffer shiftErrMsg = new StringBuffer("Unknown shift : <br/>");
		StringBuffer notShiftErrMsg = new StringBuffer("Shift is required : <br/>");
		boolean hasShiftError = false;
		boolean hasNoShiftError = false;
		CsvReader reader = null;
		List<DailyShiftScheduleDto> shiftScheduleDtos = new ArrayList<>();
		try {
			reader = new CsvReader(in, Charset.defaultCharset());
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			int cnt = 0;
			Employee employee = null;
			String employeeNo = "";
			DailyShiftScheduleDto shiftScheduleDto = null;
			List<DSSLineDto> dssLineDtos = new ArrayList<>();
			DSSLineDto dssLineDto = null;
			while (reader.readRecord()) {
				if (cnt > 0) { // Ignore the header.
					String arr[] = reader.getRawRecord().split(",");
					Integer size = arr.length-1;
					if (employeeNo.isEmpty()){
						employeeNo = arr[0].replace("`", "");
					}
					String strDate = arr[4];
					Date date = formatter.parse(strDate);
					if (!dates.contains(date)){
						continue;
					}
					String employeeName = arr[2] + ", " + arr[3];
					dssLineDto = new DSSLineDto();
					dssLineDto.setDate(date);
					String shiftName = null;
					if (size == 5){
						shiftName = arr[size].trim();
					}
					if (shiftName != null && !shiftName.isEmpty()){
						dssLineDto.setShiftName(shiftName);
						EmployeeShift employeeShift = employeeShiftDao.getByNameAndCompanyId(shiftName, companyId);
						if (employeeShift != null){
							dssLineDto.setEmployeeShiftId(employeeShift.getId());
							dssLineDto.setOrigEmployeeShiftId(employeeShift.getId());
						} else {
							hasShiftError = true;
							shiftErrMsg.append(shiftName + " ("+employeeName + " : " + strDate + ")"+ "<br/>" );
						}
					} else {
						hasNoShiftError = true;
						notShiftErrMsg.append(employeeName + " : " + strDate+ "<br/>" );
					}
					if (!employeeNo.equals(arr[0].replace("`", ""))){
						shiftScheduleDto = new DailyShiftScheduleDto();
						employee = employeeDao.getEmployeeByNo(employeeNo, companyId);
						employeeNo = arr[0].replace("`", "");
						if (employee != null){
							shiftScheduleDto.setEmployee(employee);
							shiftScheduleDto.setEmployeeId(employee.getId());
							shiftScheduleDto.setEmployeeName(employee.getFullName());
							shiftScheduleDto.setDssLineDtos(dssLineDtos);
							shiftScheduleDtos.add(shiftScheduleDto);
						}
						dssLineDtos = new ArrayList<>();
					}
					dssLineDtos.add(dssLineDto);
				}
				cnt++;
			}
			shiftScheduleDto = new DailyShiftScheduleDto();
			employee = employeeDao.getEmployeeByNo(employeeNo, companyId);
			if (employee != null){
				shiftScheduleDto.setEmployee(employee);
				shiftScheduleDto.setEmployeeId(employee.getId());
				shiftScheduleDto.setEmployeeName(employee.getFullName());
				shiftScheduleDto.setDssLineDtos(dssLineDtos);
				shiftScheduleDtos.add(shiftScheduleDto);
			}
		} catch (Exception e) {
			throw new Exception("Invalid file format!");
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		if (hasNoShiftError || hasShiftError){
			throw new Exception((hasShiftError ? shiftErrMsg.toString() : "") + "<br/>" + (hasNoShiftError ? notShiftErrMsg.toString() : ""));
		}
		return shiftScheduleDtos;
	}
}
