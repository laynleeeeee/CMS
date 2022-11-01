package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.ImageUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CivilStatusDao;
import eulap.eb.dao.DailyShiftScheduleLineDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.EducationalAttainmentTypeDao;
import eulap.eb.dao.EeEmergencyContactDao;
import eulap.eb.dao.EeLicenseCertificateDao;
import eulap.eb.dao.EeNationalCompetencyDao;
import eulap.eb.dao.EeSeminarAttendedDao;
import eulap.eb.dao.EmployeeChildrenDao;
import eulap.eb.dao.EmployeeCivilQueryDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeDtrDao;
import eulap.eb.dao.EmployeeEducationalAttainmentDao;
import eulap.eb.dao.EmployeeEmploymentDao;
import eulap.eb.dao.EmployeeEmploymentQueryDao;
import eulap.eb.dao.EmployeeFamilyDao;
import eulap.eb.dao.EmployeeProfileDao;
import eulap.eb.dao.EmployeeRelativeDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.EmployeeSiblingDao;
import eulap.eb.dao.EmployeeStatusDao;
import eulap.eb.dao.EmployeeTypeDao;
import eulap.eb.dao.GenderDao;
import eulap.eb.dao.MonthlyShiftScheduleLineDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.CivilStatus;
import eulap.eb.domain.hibernate.DailyShiftScheduleLine;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.EducationalAttainmentType;
import eulap.eb.domain.hibernate.EeEmergencyContact;
import eulap.eb.domain.hibernate.EeLicenseCertificate;
import eulap.eb.domain.hibernate.EeNationalCompetency;
import eulap.eb.domain.hibernate.EeSeminarAttended;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeChildren;
import eulap.eb.domain.hibernate.EmployeeCivilQuery;
import eulap.eb.domain.hibernate.EmployeeEducationalAttainment;
import eulap.eb.domain.hibernate.EmployeeEmployment;
import eulap.eb.domain.hibernate.EmployeeEmploymentQuery;
import eulap.eb.domain.hibernate.EmployeeFamily;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.EmployeeRelative;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.EmployeeSibling;
import eulap.eb.domain.hibernate.EmployeeStatus;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.Gender;
import eulap.eb.domain.hibernate.MonthlyShiftScheduleLine;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.EmployeeFileDocumentDto;
import eulap.eb.web.dto.EmployeeLogDto;
import eulap.eb.web.dto.EmployeeScheduleDtrDto;
import eulap.eb.web.dto.WebEmployeeProfileDto;

/**
 * Business logic for {@link EmployeeProfile}

 *
 */
@Service
public class EmployeeProfileService {
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeProfileDao employeeProfileDao;
	@Autowired
	private EmployeeSiblingDao employeeSiblingDao;
	@Autowired
	private EmployeeChildrenDao employeeChildrenDao;
	@Autowired
	private EmployeeEmploymentDao employeeEmploymentDao;
	@Autowired
	private EmployeeRelativeDao employeeRelativeDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private EBObjectDao ebObjectDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private GenderDao genderDao;
	@Autowired
	private CivilStatusDao civilStatusDao;
	@Autowired
	private PayrollTimePeriodScheduleDao payrollTimePeriodScheduleDao;
	@Autowired
	private EmployeeDtrDao employeeDtrDao;
	@Autowired
	private EmployeeFamilyDao employeeFamilyDao;
	@Autowired
	private EmployeeEducationalAttainmentDao employeeEducationalAttainmentDao;
	@Autowired
	private EmployeeCivilQueryDao employeeCivilQueryDao;
	@Autowired
	private EmployeeEmploymentQueryDao employeeEmploymentQueryDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private EmployeeTypeDao employeeTypeDao;
	@Autowired
	private EmployeeStatusDao employeeStatusDao;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private PayrollTimePeriodScheduleDao scheduleDao;
	@Autowired
	private MonthlyShiftScheduleLineDao shiftScheduleLineDao;
	@Autowired
	private DailyShiftScheduleLineDao dailyShiftScheduleLineDao;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EducationalAttainmentTypeDao educationalAttainmentTypeDao;
	@Autowired
	private EeLicenseCertificateDao licenseCertificateDao;
	@Autowired
	private EeEmergencyContactDao eeEmergencyContactDao;
	@Autowired
	private EeSeminarAttendedDao seminarAttendedDao;
	@Autowired
	private EeNationalCompetencyDao eeNatCmptncyDao;

	private final static double MAX_ALLOWABLE_FILE_SIZE = 150000;

	/**
	 * Get the list of all active genders.
	 */
	public List<Gender> getAllActiveGender() {
		return genderDao.getAllActive();
	}

	/**
	 * Get the list of all active civil status.
	 */
	public List<CivilStatus> getAllActiveStatus() {
		return civilStatusDao.getAllActive();
	}

	/**
	 * Initialize employee profile.
	 * @param employeeId The employee filter.
	 * @return {@link EmployeeProfile}
	 */
	public EmployeeProfile initEmployeeProfile(Integer employeeId, Integer companyId) {
		EmployeeProfile employeeProfile = null;
		if (employeeId == null) {
			employeeProfile = new EmployeeProfile();
			if(companyId != null) {
				employeeProfile.setEmployeeNumber(employeeProfileDao.generateEmpNumber(companyId));
				employeeProfile.setStrEmployeeNumber(genEmployeeNumber(companyId));
			}
			employeeProfile.setHiredDate(new Date());
			employeeProfile.setEmploymentPeriodFrom(new Date());
			employeeProfile.setEmploymentPeriodTo(new Date());

			// Employee
			Employee employee =  new Employee();
			employee.setBirthDate(new Date());
			employee.setActive(true);
			employeeProfile.setEmployee(employee);

			// Family
			EmployeeFamily employeeFamily = new EmployeeFamily();
			employeeProfile.setEmployeeFamily(employeeFamily);

			// Educational Attainment
			EmployeeEducationalAttainment employeeEducationalAttainment = new EmployeeEducationalAttainment();
			employeeProfile.setEmployeeEducationalAttainment(employeeEducationalAttainment);

			// Civil Query
			EmployeeCivilQuery employeeCivilQuery = new EmployeeCivilQuery();
			employeeCivilQuery.setAdministrativeInvestigation(false);
			employeeCivilQuery.setCrimeConvicted(false);
			employeeCivilQuery.setUsedProhibitedDrug(false);
			employeeProfile.setEmployeeCivilQuery(employeeCivilQuery);

			// Employment Query
			EmployeeEmploymentQuery employeeEmploymentQuery = new EmployeeEmploymentQuery();
			employeeEmploymentQuery.setPrevEmployed(false);
			employeeEmploymentQuery.setDiagnosedWithDisease(false);
			employeeProfile.setEmployeeEmploymentQuery(employeeEmploymentQuery);
		} else {
			employeeProfile = employeeProfileDao.getByEmployee(employeeId);
			if (employeeProfile == null) {
				throw new RuntimeException("No employee found with ID: " + employeeId);
			}
			Integer ebObjectId = employeeProfile.getEbObjectId();
			Employee employee = employeeDao.get(employeeId);
			EmployeeShift shift = getCurrentShift(employeeId);
			if (shift == null) {
				shift = new EmployeeShift();
				shift.setName(ValidatorMessages.getString("EmployeeProfileService.0"));
			}
			employee.setEmployeeShift(shift);
			employeeProfile.setEmployee(employee);
			employeeProfile.setEmployeeFamily(employeeFamilyDao.getByEmployee(employeeId));
			employeeProfile.setEmployeeEducationalAttainment(employeeEducationalAttainmentDao.getByEmployee(employeeId));
			employeeProfile.setEmployeeCivilQuery(employeeCivilQueryDao.getByEmployee(employeeId));
			employeeProfile.setEmployeeEmploymentQuery(employeeEmploymentQueryDao.getByEmployee(employeeId));
			employeeProfile.setReferenceDocument(referenceDocumentDao.getRDByEbObject(ebObjectId, ReferenceDocument.OR_TYPE_ID));
			employeeProfile.setEmployeeSiblings(employeeSiblingDao.getAllByRefId("employeeId", employeeId));
			employeeProfile.setEmployeeChildren(employeeChildrenDao.getAllByRefId("employeeId", employeeId));
			employeeProfile.setEmployeeEmployments(employeeEmploymentDao.getAllByRefId("employeeId", employeeId));
			employeeProfile.setEmployeeRelatives(employeeRelativeDao.getAllByRefId("employeeId", employeeId));
			employeeProfile.setEmployeeScheduleDtrDtos(initDTR(employeeId, new Date()));
			employeeProfile.setLicenseCertificates(licenseCertificateDao.getLicCertByEbObjectId(ebObjectId));
			employeeProfile.setNationalCompetencies(eeNatCmptncyDao.getNatCompByEbObjectId(ebObjectId));
			employeeProfile.setSeminarAttendeds(seminarAttendedDao.getSeminarAttendedByEbObjectId(ebObjectId));
			employeeProfile.setEmergencyContacts(eeEmergencyContactDao.getEmerContactsByEbObjId(ebObjectId));
			employeeProfile.setStrEmployeeNumber(employeeDao.get(employeeId).getEmployeeNo());
		}
		employeeProfile.setFormPage(EmployeeProfile.FORM_PAGE_BASIC_INFO);
		return employeeProfile;
	}

	public EmployeeShift getCurrentShift(int employeeId, Date curDate) {
		int month = DateUtil.getMonth(curDate) + 1;
		int year = DateUtil.getYear(curDate);
		List<PayrollTimePeriodSchedule> schedules = scheduleDao.getTimePeriodSchedules(month, year, false);
		schedules.addAll(scheduleDao.getTimePeriodSchedules(month+1, year, false));
		PayrollTimePeriodSchedule currentSched = null;
		if (!schedules.isEmpty()) {
			for (PayrollTimePeriodSchedule s : schedules) {
				if (!curDate.before(s.getDateFrom()) && !curDate.after(s.getDateTo())) {
					currentSched = s;
					break;
				}
			}
		}
		if (currentSched != null) {
			MonthlyShiftScheduleLine line = 
					shiftScheduleLineDao.getByPeriodAndSchedule(currentSched.getPayrollTimePeriodId(), 
							currentSched.getId(), employeeId);
			if (line != null) {
				return employeeShiftDao.get(line.getEmployeeShiftId());
			}
		}
		return null;
	}

	/**
	 * Get employee shift schedule details
	 * @param employeeId The employee id
	 * @param date The date
	 * @return The employee shift schedule details
	 */
	public EmployeeShift getEeCurrentShift(Integer employeeId, Date currentDate) {
		DailyShiftScheduleLine dailyShiftScheduleLine = dailyShiftScheduleLineDao.getDailyShiftSchedLine(employeeId, currentDate);
		if(dailyShiftScheduleLine != null) {
			return employeeShiftDao.get(dailyShiftScheduleLine.getEmployeeShiftId());
		}
		return null;
	}

	private EmployeeShift getCurrentShift(int employeeId) {
		return getCurrentShift(employeeId, DateUtil.removeTimeFromDate(new Date()));
	}

	/**
	 * Save employee profile form
	 * @param employeeProfile The employee profile object
	 * @param user The current user logged
	 * @param result The validation error result
	 */
	public void saveEmployeeProfile(EmployeeProfile employeeProfile, User user, BindingResult result) throws IOException {
		boolean isNew = employeeProfile.getId() == 0;
		removeExtraWhiteSpaces(employeeProfile);
		validate(employeeProfile, result, isNew);
		if (!result.hasErrors()) {
			saveEmployeeProfileForm(employeeProfile, user, isNew);
		}
	}

	protected void saveEmployeeProfileForm(EmployeeProfile employeeProfile, User user, boolean isNew) throws IOException {
		int userId = user.getId();
		int formPage = employeeProfile.getFormPage();
		Integer parentEbObjectId = null;
		if (formPage == EmployeeProfile.FORM_PAGE_BASIC_INFO) {
			Employee employee = employeeProfile.getEmployee();
			employee.setAddress(employeeProfile.getPermanentAddress());
			employee.setCivilStatus(employeeProfile.getCivilStatusId());
			employee.setGender(employeeProfile.getGenderId());
			String employeeNo = employeeProfile.getStrEmployeeNumber();
			employee.setEmployeeNo(employeeNo);
			employeeProfile.setEmployeeNumber(Integer.parseInt(employeeNo));
			employee.setEmployeeShiftId(employeeProfile.getEmployeeShiftId());
			AuditUtil.addAudit(employee, new Audit(user.getId(), isNew, new Date()));
			employeeDao.saveOrUpdate(employee);

			ReferenceDocument referenceDocument = employeeProfile.getReferenceDocument();
			boolean hasSelectedPhoto = referenceDocument.getFileName() != null &&
					!referenceDocument.getFileName().trim().isEmpty();
			EBObject ebObject = null;
			if (isNew) {
				ebObject = new EBObject();
				ebObject.setObjectTypeId(EmployeeProfile.OBJECT_TYPE_ID);
				AuditUtil.addAudit(ebObject, new Audit(user.getId(), true, new Date()));
				ebObjectDao.save(ebObject);
				employeeProfile.setEbObjectId(ebObject.getId());
			}

			parentEbObjectId = employeeProfile.getEbObjectId();
			if (hasSelectedPhoto) {
				if (referenceDocument.getId() == 0) {
					referenceDocument.setEbObjectId(generateEbObjectId(ReferenceDocument.OBJECT_TYPE_ID, userId));
					if (referenceDocument.getFileSize() == null) {
						// For webcam photos.
						referenceDocument.setFileSize(MAX_ALLOWABLE_FILE_SIZE);
					}
					saveObjectToObject(parentEbObjectId, referenceDocument.getEbObjectId(), userId,
							ReferenceDocument.OR_TYPE_ID);
				}
				if (referenceDocument.getFileSize() == null) {
					// For webcam photos.
					referenceDocument.setFileSize(MAX_ALLOWABLE_FILE_SIZE);
				}
				if (referenceDocument.getFileSize() > MAX_ALLOWABLE_FILE_SIZE) {
					referenceDocument.setFile(ImageUtil.reduceImageSize(referenceDocument.getFile(), 250, 250));
				}
				referenceDocument.setFileName(referenceDocument.getFileName().trim());
				referenceDocument.setDescription(referenceDocument.getDescription().trim());
				referenceDocumentDao.saveOrUpdate(referenceDocument);
			}
			employeeProfile.setEmployeeId(employee.getId());
			AuditUtil.addAudit(employeeProfile, new Audit(user.getId(), isNew, new Date()));
			employeeProfileDao.saveOrUpdate(employeeProfile);
			// Save employee family
			EmployeeFamily employeeFamily = employeeProfile.getEmployeeFamily();
			employeeFamily.setEmployeeId(employeeProfile.getEmployeeId());
			employeeFamilyDao.saveOrUpdate(employeeFamily);
			// Save the employee siblings.
			saveEmployeeSiblings(employeeProfile, isNew);
			// Save the employee children.
			saveEmployeeChildren(employeeProfile, isNew);
			employeeProfile.setFormPage(EmployeeProfile.FORM_PAGE_EDUC_ATTAINMENT);
		} else if (formPage == EmployeeProfile.FORM_PAGE_EDUC_ATTAINMENT) {
			// Save the employee educational attainment.
			EmployeeEducationalAttainment employeeEducationalAttainment = employeeProfile.getEmployeeEducationalAttainment();
			employeeEducationalAttainment.setEmployeeId(employeeProfile.getEmployeeId());
			employeeEducationalAttainmentDao.saveOrUpdate(employeeEducationalAttainment);
			// Save the employee employment
			saveEmployeeEmployment(employeeProfile, isNew);
			// Save the employee civil query.
			EmployeeCivilQuery employeeCivilQuery = employeeProfile.getEmployeeCivilQuery();
			if(employeeCivilQuery != null) {
				employeeCivilQuery.setEmployeeId(employeeProfile.getEmployeeId());
				employeeCivilQueryDao.saveOrUpdate(employeeCivilQuery);
			}
			// Save the relative employment
			saveEmployeeRelative(employeeProfile, isNew);
			employeeProfile.setFormPage(EmployeeProfile.FORM_PAGE_FINAL);
		} else if (formPage == EmployeeProfile.FORM_PAGE_FINAL) {
			EmployeeEmploymentQuery employeeEmploymentQuery = employeeProfile.getEmployeeEmploymentQuery();
			parentEbObjectId = employeeProfileDao.getByEmployee(employeeProfile.getEmployeeId()).getEbObjectId(); 
			if(employeeEmploymentQuery != null) {
				employeeEmploymentQuery.setEmployeeId(employeeProfile.getEmployeeId());
				employeeEmploymentQueryDao.forceSaveOrUpdate(employeeEmploymentQuery);
			}
			//Save employee licenses and background
			List<EeLicenseCertificate> licenseCertificates = employeeProfile.getLicenseCertificates();
			if(licenseCertificates != null && !licenseCertificates.isEmpty()) {
				saveEeLicenseCertificate(licenseCertificates, EeLicenseCertificate.OBJECT_TYPE_ID, userId, parentEbObjectId,
						EeLicenseCertificate.EE_LIC_CERT_OR_TYPE);
			}

			//Save employee national competencies
			List<EeNationalCompetency> nationalCompetencies = employeeProfile.getNationalCompetencies();
			if(nationalCompetencies != null && !nationalCompetencies.isEmpty()) {
				saveEeNationalCompetencies(nationalCompetencies, EeNationalCompetency.OBJECT_TYPE_ID, userId, parentEbObjectId,
						EeNationalCompetency.EE_NAT_COMP_OR_TYPE);
			}

			//Save employee seminar attended
			List<EeSeminarAttended> seminarsAttend = employeeProfile.getSeminarAttendeds();
			if(seminarsAttend != null && !seminarsAttend.isEmpty()) {
				saveEeSeminarAttended(seminarsAttend, EeSeminarAttended.OBJECT_TYPE_ID, userId, parentEbObjectId,
						EeSeminarAttended.EE_SEMINAR_OR_TYPE);
			}

			// Save employee emergency contacts
			List<EeEmergencyContact> emerContacts = employeeProfile.getEmergencyContacts();
			if(emerContacts != null && !emerContacts.isEmpty()) {
				saveEeEmergencyContacts(emerContacts, EeEmergencyContact.OBJECT_TYPE_ID, userId, parentEbObjectId,
						EeEmergencyContact.EE_EMER_CONTACT_OR_TYPE);
			}
		}
	}

	/**
	 * Save employee licenses and certificates
	 * @param licenseCertificates The list of employee licenses and certificates
	 * @param objectTypeId The employee dependent object type id
	 * @param userId The current logged user id
	 * @param orTypeId The OR type id
	 * @param parentEbObjectId The parent EB object id
	 */
	private void saveEeLicenseCertificate(List<EeLicenseCertificate> licenseCertificates, Integer objectTypeId,
			Integer userId, Integer parentEbObjectId, Integer orTypeId) {
		List<EeLicenseCertificate> eeLicenseCertificates = licenseCertificateDao.getLicCertByEbObjectId(parentEbObjectId);
		if(eeLicenseCertificates != null && !eeLicenseCertificates.isEmpty()) {
			for (EeLicenseCertificate licenseCertificate : eeLicenseCertificates) {
				licenseCertificate.setActive(false);
				licenseCertificateDao.saveOrUpdate(licenseCertificate);
			}
		}
		if (licenseCertificates != null && !licenseCertificates.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EeLicenseCertificate eelc : licenseCertificates) {
				eelc.setEbObjectId(generateEbObjectId(objectTypeId, userId));
				eelc.setRating("");
				eelc.setActive(true);
				saveObjectToObject(parentEbObjectId, eelc.getEbObjectId(),
					userId, orTypeId);
				toBeSaved.add(eelc);
			}
			licenseCertificateDao.batchSave(toBeSaved);
		}
	}

	/**
	 * Save employee emergency contacts
	 * @param emerContacts The list of emergency contacts
	 * @param objectTypeId The employee dependent object type id
	 * @param userId The current logged user id
	 * @param orTypeId The OR type id
	 * @param parentEbObjectId The parent EB object id
	 */
	private void saveEeEmergencyContacts(List<EeEmergencyContact> emerContacts, Integer objectTypeId,
			Integer userId, Integer parentEbObjectId, Integer orTypeId) {
		List<EeEmergencyContact> eeEmergencyContacts = eeEmergencyContactDao.getEmerContactsByEbObjId(parentEbObjectId);
		if(eeEmergencyContacts != null && !eeEmergencyContacts.isEmpty()) {
			for (EeEmergencyContact employeeDependent : eeEmergencyContacts) {
				employeeDependent.setActive(false);
				eeEmergencyContactDao.saveOrUpdate(employeeDependent);
			}
		}
		if (emerContacts != null && !emerContacts.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EeEmergencyContact eec : emerContacts) {
				eec.setEbObjectId(generateEbObjectId(objectTypeId, userId));
				eec.setActive(true);
				saveObjectToObject(parentEbObjectId, eec.getEbObjectId(),
						userId, orTypeId);
				toBeSaved.add(eec);
			}
			eeEmergencyContactDao.batchSave(toBeSaved);
		}
	}

	/**
	 * Save employee national competencies.
	 * @param nationalCompetency The list of employee licenses and certificates
	 * @param objectTypeId The employee dependent object type id
	 * @param userId The current logged user id
	 * @param orTypeId The OR type id
	 * @param parentEbObjectId The parent EB object id
	 */
	private void saveEeNationalCompetencies(List<EeNationalCompetency> nationalCompetency, Integer objectTypeId,
			Integer userId, Integer parentEbObjectId, Integer orTypeId) {
		List<EeNationalCompetency> eeNationalCompetencies = eeNatCmptncyDao.getNatCompByEbObjectId(parentEbObjectId);
		if(eeNationalCompetencies != null && !eeNationalCompetencies.isEmpty()) {
			for (EeNationalCompetency eeNationalCompetency : eeNationalCompetencies) {
				eeNationalCompetency.setActive(false);
				eeNatCmptncyDao.saveOrUpdate(eeNationalCompetency);
			}
		}
		if (nationalCompetency != null && !nationalCompetency.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EeNationalCompetency eenc : nationalCompetency) {
				eenc.setEbObjectId(generateEbObjectId(objectTypeId, userId));
				eenc.setActive(true);
				saveObjectToObject(parentEbObjectId, eenc.getEbObjectId(),
					userId, orTypeId);
				toBeSaved.add(eenc);
			}
			eeNatCmptncyDao.batchSave(toBeSaved);
		}
	}

	/**
	 * Save employee seminars attended.
	 * @param gvchEmployeeProfile The GVCH employee profile
	 * @param objectTypeId The employee dependent object type id
	 * @param userId The current logged user id
	 * @param orTypeId The or type id
	 * @param parentEbObjectId The parent EB object id
	 */
	private void saveEeSeminarAttended(List<EeSeminarAttended> seminarsAttend, Integer objectTypeId,
			Integer userId, Integer parentEbObjectId, Integer orTypeId) {
		List<EeSeminarAttended> eeSeminarsAttended = seminarAttendedDao.getSeminarAttendedByEbObjectId(parentEbObjectId);
		if(eeSeminarsAttended != null && !eeSeminarsAttended.isEmpty()) {
			for (EeSeminarAttended eeSeminarAttended : eeSeminarsAttended) {
				eeSeminarAttended.setActive(false);
				seminarAttendedDao.saveOrUpdate(eeSeminarAttended);
			}
		}
		if (seminarsAttend != null && !seminarsAttend.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EeSeminarAttended esa : seminarsAttend) {
				esa.setEbObjectId(generateEbObjectId(objectTypeId, userId));
				esa.setCompanyAgency("");
				esa.setActive(true);
				saveObjectToObject(parentEbObjectId, esa.getEbObjectId(),
					userId, orTypeId);
				toBeSaved.add(esa);
			}
			seminarAttendedDao.batchSave(toBeSaved);
		}
	}

	protected void removeExtraWhiteSpaces(EmployeeProfile ep) {
		// Employee
		Employee employee = ep.getEmployee();
		if (employee.getFirstName() != null) {
			employee.setFirstName(StringFormatUtil.removeExtraWhiteSpaces(employee.getFirstName()));
		}
		if (employee.getMiddleName() != null) {
			employee.setMiddleName(StringFormatUtil.removeExtraWhiteSpaces(employee.getMiddleName()));
		}
		if (employee.getLastName() != null) {
			employee.setLastName(StringFormatUtil.removeExtraWhiteSpaces(employee.getLastName()));
		}
		if (employee.getContactNo() != null) {
			employee.setContactNo(StringFormatUtil.removeExtraWhiteSpaces(employee.getContactNo()));
		}
		if (employee.getAddress() != null) {
			employee.setAddress(StringFormatUtil.removeExtraWhiteSpaces(employee.getAddress()));
		}
		if (employee.getEmailAddress() != null) {
			employee.setEmailAddress(StringFormatUtil.removeExtraWhiteSpaces(employee.getEmailAddress()));
		}

		// Employee Profile
		if (ep.getBloodType() != null) {
			ep.setBloodType(StringFormatUtil.removeExtraWhiteSpaces(ep.getBloodType()));
		}
		if (ep.getTin() != null) {
			ep.setTin(StringFormatUtil.removeExtraWhiteSpaces(ep.getTin()));
		}
		if (ep.getPhilhealthNo() != null) {
			ep.setPhilhealthNo(StringFormatUtil.removeExtraWhiteSpaces(ep.getPhilhealthNo()));
		}
		if (ep.getSssNo() != null) {
			ep.setSssNo(StringFormatUtil.removeExtraWhiteSpaces(ep.getSssNo()));
		}
		if (ep.getHdmfNo() != null) {
			ep.setHdmfNo(StringFormatUtil.removeExtraWhiteSpaces(ep.getHdmfNo()));
		}
		if (ep.getPermanentAddress() != null) {
			ep.setPermanentAddress(StringFormatUtil.removeExtraWhiteSpaces(ep.getPermanentAddress()));
		}
		if (ep.getBirthPlace() != null) {
			ep.setBirthPlace(StringFormatUtil.removeExtraWhiteSpaces(ep.getBirthPlace()));
		}
		if (ep.getCitizenship() != null) {
			ep.setCitizenship(StringFormatUtil.removeExtraWhiteSpaces(ep.getCitizenship()));
		}
		if (ep.getHeight() != null) {
			ep.setHeight(StringFormatUtil.removeExtraWhiteSpaces(ep.getHeight()));
		}
		if (ep.getWeight() != null) {
			ep.setWeight(StringFormatUtil.removeExtraWhiteSpaces(ep.getWeight()));
		}
		if (ep.getReligion() != null) {
			ep.setReligion(StringFormatUtil.removeExtraWhiteSpaces(ep.getReligion()));
		}
		if (ep.getEyeColor() != null) {
			ep.setEyeColor(StringFormatUtil.removeExtraWhiteSpaces(ep.getEyeColor()));
		}
		if (ep.getHairColor() != null) {
			ep.setHairColor(StringFormatUtil.removeExtraWhiteSpaces(ep.getHairColor()));
		}
		if (ep.getTelephoneNumber() != null) {
			ep.setTelephoneNumber(StringFormatUtil.removeExtraWhiteSpaces(ep.getTelephoneNumber()));
		}
		if (ep.getLanguageDialect() != null) {
			ep.setLanguageDialect(StringFormatUtil.removeExtraWhiteSpaces(ep.getLanguageDialect()));
		}

		// Employee Employment
		List<EmployeeEmployment> empEmployements = ep.getEmployeeEmployments();
		if(empEmployements != null){
			for(EmployeeEmployment empEmployement : empEmployements){
				if(empEmployement.getCompanyName() != null){
					empEmployement.setCompanyName(StringFormatUtil.removeExtraWhiteSpaces(empEmployement.getCompanyName()));
				}
				if(empEmployement.getPosition() != null){
					empEmployement.setPosition(StringFormatUtil.removeExtraWhiteSpaces(empEmployement.getPosition()));
				}
				if(empEmployement.getSeparationReason() != null){
					empEmployement.setSeparationReason(StringFormatUtil.removeExtraWhiteSpaces(empEmployement.getSeparationReason()));
				}
			}
		}

		// Employee Relatives
		List<EmployeeRelative> empRelatives = ep.getEmployeeRelatives();
		if(empRelatives != null){
			for(EmployeeRelative empRelative : empRelatives){
				if(empRelative.getName() != null){
					empRelative.setName(StringFormatUtil.removeExtraWhiteSpaces(empRelative.getName()));
				}
				if(empRelative.getPosition() != null){
					empRelative.setPosition(StringFormatUtil.removeExtraWhiteSpaces(empRelative.getPosition()));
				}
				if(empRelative.getRelationship() != null){
					empRelative.setRelationship(StringFormatUtil.removeExtraWhiteSpaces(empRelative.getRelationship()));
				}
			}
		}
	}

	/**
	 * Validate the employee profile form
	 * @param ep The employee profile object
	 * @param errors The validation errors
	 * @param isNew True if the form is a new form, otherwise edit
	 */
	public void validate(EmployeeProfile ep, Errors errors, boolean isNew) {
		validateForm(ep, errors, isNew, true, "");
	}

	/**
	 * Validate the employee profile form
	 * @param ep The employee profile object
	 * @param errors The validation errors
	 * @param isNew True if the form is a new form, otherwise edit
	 * @param isBiometricRequired True if the biometric id is required, otherwise false
	 */
	public void validateForm(EmployeeProfile ep, Errors errors, boolean isNew, boolean isBiometricRequired, String fieldPrepend) {
		int formPage = ep.getFormPage();
		if (formPage == EmployeeProfile.FORM_PAGE_BASIC_INFO) {
			Employee employee = ep.getEmployee();
			// Employee Number
			if(!employeeProfileDao.isUniqueEmployeeNo(ep) ) {
				errors.rejectValue(fieldPrepend+"strEmployeeNumber", null, null, ValidatorMessages.getString("EmployeeProfileService.94"));
			}
			//Position
			if(employee.getPositionId() == null){
				errors.rejectValue(fieldPrepend+"employee.positionId", null, null, ValidatorMessages.getString("EmployeeProfileService.1")); 
			}

			//First name
			if(employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"employee.firstName", null, null, ValidatorMessages.getString("EmployeeProfileService.2")); 
			} else if(employee.getFirstName().trim().length() > Employee.NAME_MAX_CHAR) {
				errors.rejectValue(fieldPrepend+"employee.firstName", null, null,
						ValidatorMessages.getString("EmployeeProfileService.3")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeProfileService.4")); 
			}

			//Middle name
			if(employee.getFirstName() != null &&
					employee.getMiddleName().trim().length() > Employee.NAME_MAX_CHAR) {
				errors.rejectValue(fieldPrepend+"employee.middleName", null, null,
						ValidatorMessages.getString("EmployeeProfileService.5")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeProfileService.6")); 
			}

			//Last name
			if(employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"employee.lastName", null, null, ValidatorMessages.getString("EmployeeProfileService.7")); 
			} else if(employee.getLastName().trim().length() > Employee.NAME_MAX_CHAR) {
				errors.rejectValue(fieldPrepend+"employee.lastName", null, null,
						ValidatorMessages.getString("EmployeeProfileService.8")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeProfileService.9")); 
			}

			//Birthday
			if(employee.getBirthDate() == null) {
				errors.rejectValue(fieldPrepend+"employee.birthDate", null, null, ValidatorMessages.getString("EmployeeProfileService.10")); 
			}

			//Contact number
			if(employee.getContactNo() != null &&
					employee.getContactNo().trim().length() > Employee.CONTACT_NO_MAX_CHAR) {
				errors.rejectValue(fieldPrepend+"employee.contactNo", null, null,
						ValidatorMessages.getString("EmployeeProfileService.11")+Employee.CONTACT_NO_MAX_CHAR+ValidatorMessages.getString("EmployeeProfileService.12")); 
			}

			//Email address
			if(employee.getEmailAddress() != null &&
					employee.getEmailAddress().trim().length() > Employee.NAME_MAX_CHAR) {
				errors.rejectValue(fieldPrepend+"employee.emailAddress", null, null,
						ValidatorMessages.getString("EmployeeProfileService.16")+Employee.NAME_MAX_CHAR+ValidatorMessages.getString("EmployeeProfileService.17")); 
			}

			//Blood Type
			if (ep.getBloodType() != null && ep.getBloodType().isEmpty()) {
				if (ep.getBloodType().length() > EmployeeProfile.MAX_CHAR_BLOOD_TYPE) {
					evalExceededChar(fieldPrepend+"bloodType", ValidatorMessages.getString("EmployeeProfileService.19"),
							EmployeeProfile.MAX_CHAR_BLOOD_TYPE, errors);
				}
			}

			//Tin
			if (ep.getTin().length() > EmployeeProfile.MAX_CHAR_TIN) {
				evalExceededChar(fieldPrepend+"tin", ValidatorMessages.getString("EmployeeProfileService.20"), EmployeeProfile.MAX_CHAR_TIN, errors); 
			}

			//Philhealth No
			if (ep.getPhilhealthNo()!= null && ep.getPhilhealthNo().length() > EmployeeProfile.MAX_CHAR_PHILHEATH_NO) {
				evalExceededChar(fieldPrepend+"philhealthNo", ValidatorMessages.getString("EmployeeProfileService.21"), EmployeeProfile.MAX_CHAR_PHILHEATH_NO, errors); 
			}

			//SSS No
			if (ep.getSssNo()!= null && ep.getSssNo().length() > EmployeeProfile.MAX_CHAR_SSS_NO) {
				evalExceededChar(fieldPrepend+"sssNo", ValidatorMessages.getString("EmployeeProfileService.22"), EmployeeProfile.MAX_CHAR_SSS_NO, errors); 
			}

			//HDMF No
			if (ep.getHdmfNo()!= null && ep.getHdmfNo().length() > EmployeeProfile.MAX_CHAR_HDMF_NO) {
				evalExceededChar(fieldPrepend+"hdmfNo", ValidatorMessages.getString("EmployeeProfileService.23"), EmployeeProfile.MAX_CHAR_HDMF_NO, errors); 
			}

			//Birthplace
			if (ep.getBirthPlace()!= null && ep.getBirthPlace().length() > EmployeeProfile.MAX_CHAR_BIRTH_PLACE) {
				evalExceededChar(fieldPrepend+"birthPlace", ValidatorMessages.getString("EmployeeProfileService.24"), EmployeeProfile.MAX_CHAR_BIRTH_PLACE, errors); 
			}

			//Language/Dialect
			if (ep.getLanguageDialect()!= null && ep.getLanguageDialect().length() > EmployeeProfile.MAX_CHAR_LANGUAGE_DIALECT) {
				evalExceededChar(fieldPrepend+"languageDialect", ValidatorMessages.getString("EmployeeProfileService.33"), EmployeeProfile.MAX_CHAR_LANGUAGE_DIALECT, errors); 
			}

			// Biometric Id
			if (isBiometricRequired) {
				if(employee.getBiometricId() == null) {
					errors.rejectValue(fieldPrepend+"employee.biometricId", null, null, ValidatorMessages.getString("EmployeeValidator.2"));
				}
			}

			if (employee.getBiometricId() != null && !employeeService.isUniqueBiometricId(employee)) {
				errors.rejectValue(fieldPrepend+"employee.biometricId", null, null, ValidatorMessages.getString("EmployeeValidator.3"));
			}

			// Date Hired
			if(ep.getHiredDate() == null){
				errors.rejectValue(fieldPrepend+"hiredDate", null, null, ValidatorMessages.getString("EmployeeProfileService.36")); 
			}

			// Company
			ValidatorUtil.validateCompany(employee.getCompanyId(), companyService, errors, fieldPrepend+"employee.companyId");

			// Employee Number
			if(!employeeProfileDao.isUniqueEmployeeNo(ep) ) {
				int companyId = employee.getCompanyId();
				ep.setEmployeeNumber(employeeProfileDao.generateEmpNumber(companyId));
				ep.setStrEmployeeNumber(genEmployeeNumber(companyId));
			}

			// Department
			if(employee.getDivisionId() == null) {
				errors.rejectValue(fieldPrepend+"employee.divisionId", null, null, ValidatorMessages.getString("EmployeeProfileService.38")); 
			} else {
				Division division = divisionDao.get(employee.getDivisionId());
				if (division != null && !division.isActive()) {
					errors.rejectValue(fieldPrepend+"employee.divisionId", null, null, ValidatorMessages.getString("EmployeeProfileService.39")); 
				}
			}

			// Employee Type
			if(employee.getEmployeeTypeId() == null) {
				errors.rejectValue(fieldPrepend+"employee.employeeTypeId", null, null, ValidatorMessages.getString("EmployeeProfileService.40")); 
			} else {
				EmployeeType employeeType = employeeTypeDao.get(employee.getEmployeeTypeId());
				if (employeeType != null && !employeeType.isActive()) {
					errors.rejectValue(fieldPrepend+"employee.employeeTypeId", null, null, ValidatorMessages.getString("EmployeeProfileService.41")); 
				}
			}

			// Employee Status
			if(employee.getEmployeeStatusId() == null) {
				errors.rejectValue(fieldPrepend+"employee.employeeStatusId", null, null, ValidatorMessages.getString("EmployeeProfileService.42")); 
			} else {
				EmployeeStatus employeeStatus = employeeStatusDao.get(employee.getEmployeeStatusId());
				if (employeeStatus != null && !employeeStatus.isActive()) {
					errors.rejectValue(fieldPrepend+"employee.employeeStatusId", null, null, ValidatorMessages.getString("EmployeeProfileService.43")); 
				}
			}

			// Gender
			if(ep.getGenderId() == null) {
				errors.rejectValue(fieldPrepend+"genderId", null, null, ValidatorMessages.getString("EmployeeProfileService.46")); 
			} else {
				Gender gender = genderDao.get(ep.getGenderId());
				if (gender != null && !gender.isActive()) {
					errors.rejectValue(fieldPrepend+"genderId", null, null, ValidatorMessages.getString("EmployeeProfileService.47")); 
				}
			}

			// Civil Status
			if(ep.getCivilStatusId() == null) {
				errors.rejectValue(fieldPrepend+"civilStatusId", null, null, ValidatorMessages.getString("EmployeeProfileService.48")); 
			} else {
				CivilStatus civilStatus = civilStatusDao.get(ep.getCivilStatusId());
				if (civilStatus != null && !civilStatus.isActive()) {
					errors.rejectValue(fieldPrepend+"civilStatusId", null, null, ValidatorMessages.getString("EmployeeProfileService.49")); 
				}
			}

			// Permanent address
			if(ep.getPermanentAddress() == null || ep.getPermanentAddress().trim().isEmpty()){
				errors.rejectValue(fieldPrepend+"permanentAddress", null, null, ValidatorMessages.getString("EmployeeProfileService.93"));
			} else if(ep.getPermanentAddress().trim().length() > EmployeeProfile.MAX_PERMANENT_ADDRESS) {
				evalExceededChar(fieldPrepend+"permanentAddress", ValidatorMessages.getString("EmployeeProfileService.50"), EmployeeProfile.MAX_PERMANENT_ADDRESS, errors); 
			}

			// Employee Family
			EmployeeFamily employeeFamily = ep.getEmployeeFamily();
			if (employeeFamily.getFatherName() == null || employeeFamily.getFatherName().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"employeeFamily.fatherName", null, null, ValidatorMessages.getString("EmployeeProfileService.51")); 
			} else if (employeeFamily.getFatherName().length() > EmployeeFamily.MAX_FATHER_NAME){
				evalExceededChar(fieldPrepend+"employeeFamily.fatherName", ValidatorMessages.getString("EmployeeProfileService.52"), EmployeeFamily.MAX_FATHER_NAME, errors); 
			}

			if ((employeeFamily.getFatherOccupation() != null && !employeeFamily.getFatherOccupation().isEmpty())
					&& employeeFamily.getFatherOccupation().length() > EmployeeFamily.MAX_FATHER_OCCUPATION) {
				evalExceededChar(fieldPrepend+"employeeFamily.fatherOccupation", ValidatorMessages.getString("EmployeeProfileService.56"), EmployeeFamily.MAX_FATHER_OCCUPATION, errors); 
			}

			if (employeeFamily.getMotherName() == null || employeeFamily.getMotherName().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"employeeFamily.motherName", null, null, ValidatorMessages.getString("EmployeeProfileService.53")); 
			} else if (employeeFamily.getMotherName().length() > EmployeeFamily.MAX_MOTHER_NAME) {
				evalExceededChar(fieldPrepend+"employeeFamily.motherName", ValidatorMessages.getString("EmployeeProfileService.54"), EmployeeFamily.MAX_MOTHER_NAME, errors); 
			}

			if ((employeeFamily.getMotherOccupation() != null && !employeeFamily.getMotherOccupation().isEmpty())
					&& employeeFamily.getMotherOccupation().length() > EmployeeFamily.MAX_MOTHER_OCCUPATION) {
				evalExceededChar(fieldPrepend+"employeeFamily.motherOccupation", ValidatorMessages.getString("EmployeeProfileService.57"), EmployeeFamily.MAX_MOTHER_OCCUPATION, errors); 
			}

			if ((employeeFamily.getSpouseName() != null && !employeeFamily.getSpouseName().isEmpty())
					&& employeeFamily.getSpouseName().length() > EmployeeFamily.MAX_SPOUSE_NAME) {
				evalExceededChar(fieldPrepend+"employeeFamily.spouseName", ValidatorMessages.getString("EmployeeProfileService.55"), EmployeeFamily.MAX_SPOUSE_NAME, errors); 
			}

			if ((employeeFamily.getSpouseOccupation() != null && !employeeFamily.getSpouseOccupation().isEmpty())
					&& employeeFamily.getSpouseOccupation().length() > EmployeeFamily.MAX_SPOUSE_OCCUPATION) {
				evalExceededChar(fieldPrepend+"employeeFamily.spouseOccupation", ValidatorMessages.getString("EmployeeProfileService.58"), EmployeeFamily.MAX_SPOUSE_OCCUPATION, errors); 
			}

			// Employee Children
			List<EmployeeChildren> empChildrens = ep.getEmployeeChildren();
			if(empChildrens != null){
				for(EmployeeChildren children : empChildrens){
					if(children.getName() == null || children.getBirthDate() == null){
						errors.rejectValue(fieldPrepend+"employeeChildrenMsg", null, null, ValidatorMessages.getString("EmployeeProfileService.62")); 
					}

					if(children.getName() != null && children.getName().length() > EmployeeChildren.MAX_NAME){
						evalExceededChar(fieldPrepend+"employeeChildrenMsg", ValidatorMessages.getString("EmployeeProfileService.63"), EmployeeChildren.MAX_NAME, errors); 
					}
				}
			}
		} else if (formPage == EmployeeProfile.FORM_PAGE_EDUC_ATTAINMENT) {
			EmployeeEducationalAttainment eea = ep.getEmployeeEducationalAttainment();

			if (eea.getElementarySchool().length() > EmployeeEducationalAttainment.MAX_ELEMENTARY_SCHOOL) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.elementarySchool", ValidatorMessages.getString("EmployeeProfileService.64"), EmployeeEducationalAttainment.MAX_ELEMENTARY_SCHOOL, errors); 
			}

			if (eea.getHsSchool().length() > EmployeeEducationalAttainment.MAX_HS_SCHOOL) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.hsSchool", ValidatorMessages.getString("EmployeeProfileService.66"), EmployeeEducationalAttainment.MAX_HS_SCHOOL, errors); 
			}

			if (eea.getCollegeSchool().length() > EmployeeEducationalAttainment.MAX_COLLEGE_SCHOOL) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.collegeSchool", ValidatorMessages.getString("EmployeeProfileService.68"), EmployeeEducationalAttainment.MAX_COLLEGE_SCHOOL, errors); 
			}

			if (eea.getCollegeCourse().length() > EmployeeEducationalAttainment.MAX_COLLEGE_COURSE) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.collegeCourse", ValidatorMessages.getString("EmployeeProfileService.69"), EmployeeEducationalAttainment.MAX_COLLEGE_COURSE, errors); 
			}

			if (eea.getPostGradSchool().length() > EmployeeEducationalAttainment.MAX_POST_GRAD_SCHOOL) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.postGradSchool", ValidatorMessages.getString("EmployeeProfileService.70"), EmployeeEducationalAttainment.MAX_POST_GRAD_SCHOOL, errors); 
			}

			if (eea.getPostGradCourse().length() > EmployeeEducationalAttainment.MAX_POST_GRAD_COURSE) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.postGradCourse", ValidatorMessages.getString("EmployeeProfileService.71"), EmployeeEducationalAttainment.MAX_POST_GRAD_COURSE, errors); 
			}

			// Skill Talent
			if (eea.getEmployeeSkills()!= null && eea.getEmployeeSkills().length() > EmployeeProfile.MAX_CHAR_SKILL_TALENT) {
				evalExceededChar(fieldPrepend+"employeeEducationalAttainment.employeeSkills", ValidatorMessages.getString("EmployeeProfileService.32"), EmployeeProfile.MAX_CHAR_SKILL_TALENT, errors); 
			}

			// Employee Employment
			List<EmployeeEmployment> employment = ep.getEmployeeEmployments();
			if(employment != null) {
				for(EmployeeEmployment emp : employment){
					if((emp.getCompanyName() == null || emp.getCompanyName().isEmpty())
							|| (emp.getYear() == null)
							|| (emp.getPosition() == null || emp.getPosition().isEmpty())
							|| (emp.getSeparationReason() == null || emp.getSeparationReason().isEmpty())) {
						errors.rejectValue(fieldPrepend+"employeeEmploymentsMsg", null, null, ValidatorMessages.getString("EmployeeProfileService.72")); 
					}
					if(emp.getCompanyName() != null
							&& emp.getCompanyName().length() > EmployeeEmployment.MAX_COMPANY_NAME){
						evalExceededChar(fieldPrepend+"employeeEmploymentsMsg", ValidatorMessages.getString("EmployeeProfileService.73"), EmployeeEmployment.MAX_COMPANY_NAME, errors); 
					}
					if(emp.getPosition() != null
							&& emp.getPosition().length() > EmployeeEmployment.MAX_POSITION){
						evalExceededChar(fieldPrepend+"employeeEmploymentsMsg", ValidatorMessages.getString("EmployeeProfileService.74"), EmployeeEmployment.MAX_POSITION, errors); 
					}
					if(emp.getSeparationReason() != null
							&& emp.getSeparationReason().length() > EmployeeEmployment.MAX_SEPARATION_REASON){
						evalExceededChar(fieldPrepend+"employeeEmploymentsMsg", ValidatorMessages.getString("EmployeeProfileService.75"), EmployeeEmployment.MAX_SEPARATION_REASON, errors); 
					}
					if(emp.getYear() != null && String.valueOf(emp.getYear()).length() > EmployeeEmployment.MAX_YEAR){
						evalExceededChar(fieldPrepend+"employeeEmploymentsMsg", ValidatorMessages.getString("EmployeeProfileService.76"), EmployeeEmployment.MAX_YEAR, errors); 
					}
				}
			}
			// Employee Relatives
			List<EmployeeRelative> relatives = ep.getEmployeeRelatives();
			if(relatives != null){
				for(EmployeeRelative empRelative : relatives){
					if(empRelative.getName() != null
							&& empRelative.getName().length() > EmployeeRelative.MAX_NAME){
						evalExceededChar(fieldPrepend+"employeeRelativesMsg", ValidatorMessages.getString("EmployeeProfileService.77"), EmployeeRelative.MAX_NAME, errors); 
					}
					if(empRelative.getPosition() != null
							&& empRelative.getPosition().length() > EmployeeRelative.MAX_POSITION){
						evalExceededChar(fieldPrepend+"employeeRelativesMsg", ValidatorMessages.getString("EmployeeProfileService.78"), EmployeeRelative.MAX_POSITION, errors); 
					}
					if(empRelative.getRelationship() != null
							&& empRelative.getRelationship().length() > EmployeeRelative.MAX_RELATIONSHIP){
						evalExceededChar(fieldPrepend+"employeeRelativesMsg", ValidatorMessages.getString("EmployeeProfileService.79"), EmployeeRelative.MAX_RELATIONSHIP, errors); 
					}
				}
			}
			// Employee civil query
			EmployeeCivilQuery employeeCivilQuery = ep.getEmployeeCivilQuery();
			if(employeeCivilQuery != null) {
				if(employeeCivilQuery.isAdministrativeInvestigation()) {
					if(employeeCivilQuery.getAdminInvesDetail() == null || employeeCivilQuery.getAdminInvesDetail().trim().isEmpty()) {
						errors.rejectValue(fieldPrepend+"employeeCivilQuery.adminInvesDetail", null, null, ValidatorMessages.getString("EmployeeProfileService.80")); 
					} else if (employeeCivilQuery.getAdminInvesDetail().length() > EmployeeCivilQuery.MAX_AID) {
						evalExceededChar(fieldPrepend+"employeeCivilQuery.adminInvesDetail", ValidatorMessages.getString("EmployeeProfileService.81"), EmployeeCivilQuery.MAX_AID, errors); 
					}
				}

				if(employeeCivilQuery.isCrimeConvicted()) {
					if(employeeCivilQuery.getCrimeConvictedDetail() == null || employeeCivilQuery.getCrimeConvictedDetail().trim().isEmpty()) {
						errors.rejectValue(fieldPrepend+"employeeCivilQuery.crimeConvictedDetail", null, null, ValidatorMessages.getString("EmployeeProfileService.82")); 
					} else if (employeeCivilQuery.getCrimeConvictedDetail().length() > EmployeeCivilQuery.MAX_CCD) {
						evalExceededChar(fieldPrepend+"employeeCivilQuery.crimeConvictedDetail", ValidatorMessages.getString("EmployeeProfileService.83"), EmployeeCivilQuery.MAX_CCD, errors); 
					}
				}
			}
		} else if (formPage == EmployeeProfile.FORM_PAGE_FINAL) {
			EmployeeEmploymentQuery employeeEmploymentQuery = ep.getEmployeeEmploymentQuery();
			if(employeeEmploymentQuery != null) {
				if(employeeEmploymentQuery.isPrevEmployed()) {
					if(employeeEmploymentQuery.getSeparationReason() == null || employeeEmploymentQuery.getSeparationReason().trim().isEmpty()) {
						errors.rejectValue(fieldPrepend+"employeeEmploymentQuery.separationReason", null, null, ValidatorMessages.getString("EmployeeProfileService.84")); 
					} else if (employeeEmploymentQuery.getSeparationReason().length() > EmployeeEmploymentQuery.MAX_SEPARATION_REASON) {
						evalExceededChar(fieldPrepend+"employeeEmploymentQuery.separationReason", ValidatorMessages.getString("EmployeeProfileService.85"), EmployeeEmploymentQuery.MAX_SEPARATION_REASON, errors); 
					}
				}

				if(employeeEmploymentQuery.isDiagnosedWithDisease()) {
					if(employeeEmploymentQuery.getDiseaseDetail() == null || employeeEmploymentQuery.getDiseaseDetail().trim().isEmpty()) {
						errors.rejectValue(fieldPrepend+"employeeEmploymentQuery.diseaseDetail", null, null, ValidatorMessages.getString("EmployeeProfileService.86")); 
					} else if (employeeEmploymentQuery.getDiseaseDetail().length() > EmployeeEmploymentQuery.MAX_SEPARATION_REASON) {
						evalExceededChar(fieldPrepend+"employeeEmploymentQuery.diseaseDetail", ValidatorMessages.getString("EmployeeProfileService.87"), EmployeeEmploymentQuery.MAX_SEPARATION_REASON, errors); 
					}
				}

				if (employeeEmploymentQuery.getIdentifyingMark().length() > EmployeeEmploymentQuery.MAX_IDENTIFYING_MARK) {
					evalExceededChar(fieldPrepend+"employeeEmploymentQuery.identifyingMark", ValidatorMessages.getString("EmployeeProfileService.88"), EmployeeEmploymentQuery.MAX_IDENTIFYING_MARK, errors); 
				}
			}

			// License and ertificate.
			List<EeLicenseCertificate> eeLicenseCertificates = ep.getLicenseCertificates();
			validateEeLicense(eeLicenseCertificates, errors);

			// National Competency.
			List<EeNationalCompetency> eeNationalCompetencies = ep.getNationalCompetencies();
			validateNatCmptncy(eeNationalCompetencies, errors);

			//Seminar and training.
			List<EeSeminarAttended> seminarsAttended = ep.getSeminarAttendeds();
			validateEeSeminars(seminarsAttended, errors);

			//Emergency Contacts.
			List<EeEmergencyContact> emergencyContacts = ep.getEmergencyContacts();
			validateEeEmrgncyCntct(emergencyContacts, errors);
		}
	}

	private void validateEeLicense(List<EeLicenseCertificate> eeLicenseCertificates, Errors errors) {
		if(eeLicenseCertificates != null && !eeLicenseCertificates.isEmpty()) {
			for (EeLicenseCertificate licenseCertificate : eeLicenseCertificates) {
				if (licenseCertificate.getAccreditationType() == null || licenseCertificate.getAccreditationType().isEmpty()) {
					errors.rejectValue("licenseCertificates", null, null, ValidatorMessages.getString("RequiredField.0"));
				} else if (licenseCertificate.getAccreditationType() != null &&
						licenseCertificate.getAccreditationType().length() > EeLicenseCertificate.MAX_CHAR_TYPE) {
					evalExceededChar("licenseCertificates", ValidatorMessages.getString("EeLicenseCertificate.0"), EeLicenseCertificate.MAX_CHAR_TYPE, errors);
				}
			}
		}
	}

	private void validateNatCmptncy(List<EeNationalCompetency> eeNationalCompetencies, Errors errors) {
		if(eeNationalCompetencies != null && !eeNationalCompetencies.isEmpty()) {
			for (EeNationalCompetency nationalCompetency : eeNationalCompetencies) {
				if (nationalCompetency.getDescription() == null || nationalCompetency.getDescription().isEmpty()) {
					errors.rejectValue("nationalCompetencies", null, null, ValidatorMessages.getString("RequiredField.0"));
				} else if (nationalCompetency.getDescription() != null &&
						nationalCompetency.getDescription().length() > EeNationalCompetency.MAX_CHAR_TYPE) {
					evalExceededChar("nationalCompetencies", ValidatorMessages.getString("EeNationalCompetency.0"), EeNationalCompetency.MAX_CHAR_TYPE, errors);
				}
			}
		}
	}

	private void validateEeSeminars(List<EeSeminarAttended> seminarsAttended, Errors errors) {
		if(seminarsAttended != null && !seminarsAttended.isEmpty()) {
			for (EeSeminarAttended seminarAttended : seminarsAttended) {
				if((seminarAttended.getCourseTitle() == null || seminarAttended.getSeminarDate() == null)
						&& (seminarAttended.getCourseTitle() != null || seminarAttended.getSeminarDate() != null)) {
					errors.rejectValue("seminarAttendeds", null, null, ValidatorMessages.getString("RequiredField.0"));
				}

				if (seminarAttended.getCourseTitle() != null &&
						seminarAttended.getCourseTitle().length() > EeSeminarAttended.MAX_CHAR_TILE) {
					evalExceededChar("seminarAttendeds", ValidatorMessages.getString("EeSeminarAttended.0"), EeSeminarAttended.MAX_CHAR_TILE, errors);
				}

				if (seminarAttended.getSeminarDate() != null &&
						seminarAttended.getSeminarDate().length() > EeSeminarAttended.MAX_CHAR_DATE){
					evalExceededChar("seminarAttendeds", ValidatorMessages.getString("EeSeminarAttended.1"), EeSeminarAttended.MAX_CHAR_DATE, errors);
				}
			}
		}
	}

	private void validateEeEmrgncyCntct(List<EeEmergencyContact> emergencyContacts, Errors errors) {
		if(emergencyContacts != null && !emergencyContacts.isEmpty()) {
			for(EeEmergencyContact emergencyContact : emergencyContacts) {
				//Required fields.
				if(emergencyContact.getName() == null || emergencyContact.getAddress() == null
						|| emergencyContact.getContactNo() == null) {
					errors.rejectValue("emergencyContacts", null, null, ValidatorMessages.getString("RequiredField.0"));
				}
				//Name.
				if(emergencyContact.getName() != null && emergencyContact.getName().length() > EeEmergencyContact.MAX_CHAR_NAME) {
					evalExceededChar("emergencyContacts",
							ValidatorMessages.getString("EeEmergencyContact.0"), EeEmergencyContact.MAX_CHAR_NAME, errors);
				}
				//Address
				if(emergencyContact.getAddress() != null && emergencyContact.getAddress().length() > EeEmergencyContact.MAX_CHAR_ADDRESS) {
					evalExceededChar("emergencyContacts",
							ValidatorMessages.getString("EeEmergencyContact.1"), EeEmergencyContact.MAX_CHAR_ADDRESS, errors);
				}
				//Contact Number.
				if(emergencyContact.getContactNo() != null && emergencyContact.getContactNo().length() > EeEmergencyContact.MAX_CHAR_CONTACT_NO) {
					evalExceededChar("emergencyContacts",
							ValidatorMessages.getString("EeEmergencyContact.2"), EeEmergencyContact.MAX_CHAR_CONTACT_NO, errors);
				}
			}
		}
	}

	/**
	 * Save and generate the saved EB object id
	 * @param objectTypeId The parent object type Id
	 * @param userId The user id
	 * @return The generated EB object id
	 */
	private Integer generateEbObjectId(Integer objectTypeId, Integer userId) {
		EBObject ebObject = new EBObject();
		ebObject.setObjectTypeId(objectTypeId);
		AuditUtil.addAudit(ebObject, new Audit(userId, true, new Date()));
		ebObjectDao.save(ebObject);
		return ebObject.getId();
	}

	/**
	 * Save the object to object relationship
	 * @param fromObjectId The parent EB object
	 * @param toObjectId The child EB object
	 * @param userId The current logged user id
	 */
	private void saveObjectToObject(Integer fromObjectId, Integer toObjectId, Integer userId, Integer orTypeId) {
		ObjectToObject obj2Obj = new ObjectToObject();
		obj2Obj.setOrTypeId(orTypeId);
		obj2Obj.setFromObjectId(fromObjectId);
		obj2Obj.setToObjectId(toObjectId);
		obj2Obj.setCreatedBy(userId);
		obj2Obj.setCreatedDate(new Date());
		objectToObjectDao.save(obj2Obj);
	}

	private void evalExceededChar(String fieldName, String fieldLabel, 
			int maxChar, Errors errors) {
		errors.rejectValue(fieldName, null, null, fieldLabel + ValidatorMessages.getString("EmployeeProfileService.91") +
			maxChar + ValidatorMessages.getString("EmployeeProfileService.92"));
	}

	private void saveEmployeeSiblings (EmployeeProfile employeeProfile, boolean isNew) {
		int employeeId = employeeProfile.getEmployeeId();
		if (!isNew) {
			List<EmployeeSibling> savedEmployeeSiblings = employeeSiblingDao.getAllByRefId("employeeId", employeeId);
			List<Integer> toBeDeleted = new ArrayList<>();
			if (savedEmployeeSiblings != null && !savedEmployeeSiblings.isEmpty()) {
				for (EmployeeSibling es : savedEmployeeSiblings) {
					toBeDeleted.add(es.getId());
				}
				employeeSiblingDao.delete(toBeDeleted);
			}
		}

		List<EmployeeSibling> employeeSiblings = employeeProfile.getEmployeeSiblings();
		if (employeeSiblings != null && !employeeSiblings.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EmployeeSibling es : employeeSiblings) {
				if(es.getName() != null && es.getAge() != null && es.getOccupation() != null){
					es.setEmployeeId(employeeId);
					toBeSaved.add(es);
				}
			}
			employeeSiblingDao.batchSave(toBeSaved);
		}
	}

	private void saveEmployeeChildren (EmployeeProfile employeeProfile, boolean isNew) {
		int employeeId = employeeProfile.getEmployeeId();
		if (!isNew) {
			List<EmployeeChildren> savedEmployeeChildren = employeeChildrenDao.getAllByRefId("employeeId", employeeId);
			List<Integer> toBeDeleted = new ArrayList<>();
			if (savedEmployeeChildren != null && !savedEmployeeChildren.isEmpty()) {
				for (EmployeeChildren ec : savedEmployeeChildren) {
					toBeDeleted.add(ec.getId());
				}
				employeeChildrenDao.delete(toBeDeleted);
			}
		}

		List<EmployeeChildren> employeeChildren = employeeProfile.getEmployeeChildren();
		if (employeeChildren != null && !employeeChildren.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EmployeeChildren ec : employeeChildren) {
				ec.setEmployeeId(employeeId);
				toBeSaved.add(ec);
			}
			employeeChildrenDao.batchSave(toBeSaved);
		}
	}

	private void saveEmployeeEmployment (EmployeeProfile employeeProfile, boolean isNew) {
		int employeeId = employeeProfile.getEmployeeId();
		if (!isNew) {
			List<EmployeeEmployment> savedEmployeeEmployment = employeeEmploymentDao.getAllByRefId("employeeId", employeeId);
			List<Integer> toBeDeleted = new ArrayList<>();
			if (savedEmployeeEmployment != null && !savedEmployeeEmployment.isEmpty()) {
				for (EmployeeEmployment ee : savedEmployeeEmployment) {
					toBeDeleted.add(ee.getId());
				}
				employeeEmploymentDao.delete(toBeDeleted);
			}
		}

		List<EmployeeEmployment> employeeEmployments = employeeProfile.getEmployeeEmployments();
		if (employeeEmployments != null && !employeeEmployments.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EmployeeEmployment ee : employeeEmployments) {
				ee.setEmployeeId(employeeId);
				toBeSaved.add(ee);
			}
			employeeEmploymentDao.batchSave(toBeSaved);
		}
	}

	private void saveEmployeeRelative (EmployeeProfile employeeProfile, boolean isNew) {
		int employeeId = employeeProfile.getEmployeeId();
		if (!isNew) {
			List<EmployeeRelative> savedEmployeeRelatives = employeeRelativeDao.getAllByRefId("employeeId", employeeId);
			List<Integer> toBeDeleted = new ArrayList<>();
			if (savedEmployeeRelatives != null && !savedEmployeeRelatives.isEmpty()) {
				for (EmployeeRelative er : savedEmployeeRelatives) {
					toBeDeleted.add(er.getId());
				}
				employeeRelativeDao.delete(toBeDeleted);
			}
		}

		List<EmployeeRelative> employeeRelatives = employeeProfile.getEmployeeRelatives();
		if (employeeRelatives != null && !employeeRelatives.isEmpty()) {
			List<Domain> toBeSaved = new ArrayList<>();
			for (EmployeeRelative er : employeeRelatives) {
				er.setEmployeeId(employeeId);
				toBeSaved.add(er);
			}
			employeeRelativeDao.batchSave(toBeSaved);
		}
	}

	public  List<EmployeeScheduleDtrDto> initDTR (int employeeId, Date date) {
		return initDTR(employeeId, DateUtil.getYear(date), DateUtil.getMonth(date) + 1);
	}

	public List<EmployeeScheduleDtrDto> initDTR (int employeeId, int year, int month) {
		List<EmployeeScheduleDtrDto> employeeScheduleDtrDtos = null;
		List<PayrollTimePeriodSchedule> schedules = payrollTimePeriodScheduleDao.getTimePeriodSchedules(month, year, false);

		if (schedules != null && !schedules.isEmpty()) {
			 int maxRow = 0;
			 employeeScheduleDtrDtos = new ArrayList<>();
			 List<EmployeeLogDto> employeeLogDtos = null;
			 List<String> headerNames = null;
			 for (PayrollTimePeriodSchedule s : schedules) {
				 employeeLogDtos = new ArrayList<>();
				 headerNames = new ArrayList<>();
				 for (Date date = s.getDateFrom(); date.before(s.getDateTo()) || date.equals(s.getDateTo());) {
					 List<String> logTimes =  employeeDtrDao.getLogTimes(employeeId, date);
					 if (logTimes.size() > maxRow) {
						 maxRow = logTimes.size();
					 }
					 employeeLogDtos.add(new EmployeeLogDto(logTimes));
					 headerNames.add(DateUtil.getDay(date) + "");
					 date = DateUtil.addDaysToDate(date, 1);
				 }
				 EmployeeScheduleDtrDto schedDto = new EmployeeScheduleDtrDto(s.getName(), headerNames, employeeLogDtos, maxRow);
				 processLogTimes(schedDto);
				 processSchedDto(schedDto);
				 employeeScheduleDtrDtos.add(schedDto);
				 maxRow = 0;
			 }
		}
		return employeeScheduleDtrDtos;
	}

	private void  processLogTimes(EmployeeScheduleDtrDto schedDto) {
		List<EmployeeLogDto> logs = schedDto.getEmployeeLogDtos();
		int maxRow = schedDto.getMaxRow();
		for (EmployeeLogDto log : logs) {
			List<String> logTimes = log.getLogTimes();
			if (logTimes.size() < maxRow) {
				int diff = maxRow - logTimes.size();
				for (int i=0; i<diff; i++) {
					logTimes.add(new String(""));
				}
				log.setLogTimes(logTimes);
			}
		}
	}

	private void processSchedDto(EmployeeScheduleDtrDto schedDto) {
		int maxRow = schedDto.getMaxRow();
		List<EmployeeLogDto> logs = schedDto.getEmployeeLogDtos();
		List<EmployeeLogDto> newLogs = new ArrayList<>();
		EmployeeLogDto newLog = null;
		for (int i=0; i<maxRow; i++) {
			newLog = new EmployeeLogDto();
			newLog.setLogTimes(new ArrayList<String>());
			for (EmployeeLogDto l : logs) { 
				newLog.getLogTimes().add(l.getLogTimes().get(i));
			}
			newLogs.add(newLog);
		}
		schedDto.setEmployeeLogDtos(newLogs);
	}

	/**
	 * Get employee profile object by employee id.
	 * @param employeeId The employee id.
	 * @return The employee profile object.
	 */
	public EmployeeProfile getEmployeeProfile(Integer employeeId) {
		return employeeProfileDao.getByEmployee(employeeId);
	}

	/**
	 * Get all newly created employee after parameter employeeId.
	 * @param employeeId The base employee id.
	 * @return List of employees.
	 */
	public List<WebEmployeeProfileDto> getEmployees(Integer employeeId, Date updatedDate) {
		List<WebEmployeeProfileDto> employeeProfileDtos = new ArrayList<>();
		List<EmployeeProfile> employeeProfiles = new ArrayList<>();
		if(updatedDate == null) {
			employeeProfiles = employeeProfileDao.getNewEmployee(employeeId);
		} else {
			employeeProfiles = employeeProfileDao.getNewlyUpdatedEmployee(employeeId, updatedDate);
		}
		Employee employee = null;
		for (EmployeeProfile employeeProfile : employeeProfiles) {
			employee = employeeProfile.getEmployee();
			ReferenceDocument referenceDocument = 
					referenceDocumentDao.getRDByEbObject(employeeProfile.getEbObjectId());
			employeeProfileDtos.add(new WebEmployeeProfileDto().getInstance(employee.getEmployeeTypeId(), employee.getId(), employee.getEmployeeShiftId(),
					employee.getPositionId(), employee.getEmployeeNo(), employeeProfile.getRfid(), employee.getFirstName(), employee.getMiddleName(), employee.getLastName(),
					employee.getGender(), employee.getBirthDate(), employee.getCivilStatus(), employee.getContactNo(), employee.getAddress(),
					employee.getEmailAddress(), employee.isActive(), employee.getCreatedDate(), employee.getUpdatedDate(), employee.getCreatedBy(),
					employee.getUpdatedBy(), (referenceDocument != null ? referenceDocument.getFile() : null), employee.getCompanyId()));
		}
		return employeeProfileDtos;
	}

	/**
	 * Get the list of completed forms/documents by employee.
	 * @param employeeId the employee filter.
	 * @param formTypeId The type of form.
	 * @param pageNumber The page number.
	 * @return The list of employee forms/documents.
	 */
	public Page<EmployeeFileDocumentDto> getEmployeeFilesAndDocs (Integer employeeId, Integer formTypeId, int pageNumber) {
		return employeeProfileDao.getEmployeeFilesAndDocs(employeeId, formTypeId, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the list of genders with inactive.
	 * @param genderId The gender id.
	 * @return The list of genders
	 */
	public List<Gender> getGendersWithInactive(Integer genderId) {
		return genderDao.getAllWithInactive(genderId);
	}

	/**
	 * Get the list of civil statuses with inactive.
	 * @param civStatusId The civil status id.
	 * @return The list of civil statuses.
	 */
	public List<CivilStatus> getCivStatusesWithInactive(Integer civStatusId) {
		return civilStatusDao.getAllWithInactive(civStatusId);
	}

	/**
	 * Generate employee number.
	 * @param companyId The company id.
	 * @return The generated employee number.
	 */
	public String genEmployeeNumber(Integer companyId) {
		Integer employeeNumber = employeeProfileDao.generateEmpNumber(companyId);
		if (employeeNumber != null) {
			if (employeeNumber == 0) {
				employeeNumber = 1;
			}
			String empNumber = "";
			String strEmployeeNumber = employeeNumber.toString();
			int difference = EmployeeProfile.MAX_CHAR_EMPLOYEE_NUMBER - strEmployeeNumber.length();
			for (int i=0; i<difference; i++) {
				empNumber += "0";
			}
			return empNumber + strEmployeeNumber;
		}
		return "";
	}

	/**
	 * 
	 * @param educationalAttaimentId
	 * @return
	 */
	public List<EducationalAttainmentType> getEducationalAttainments(Integer educationalAttaimentId) {
		if(educationalAttaimentId == null) {
			return educationalAttainmentTypeDao.getAllActive();
		}
		return educationalAttainmentTypeDao.getAllWithInactive(educationalAttaimentId);
	}
}
