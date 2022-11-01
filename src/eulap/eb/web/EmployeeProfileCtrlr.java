package eulap.eb.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.ImageUtil;
import eulap.common.util.Page;
import eulap.eb.domain.hibernate.CivilStatus;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeEducationalAttainment;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.EmployeeStatus;
import eulap.eb.domain.hibernate.EmployeeType;
import eulap.eb.domain.hibernate.Gender;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.DivisionService;
import eulap.eb.service.EmployeeProfileService;
import eulap.eb.service.EmployeeService;
import eulap.eb.service.PayrollTimePeriodService;
import eulap.eb.service.UserGroupAccessRightService;
import eulap.eb.web.dto.EmployeeFileDocumentDto;
import eulap.eb.web.dto.EmployeeScheduleDtrDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Controller class for {@link EmployeeProfile}

 */

@Controller
@RequestMapping("/employeeProfile")
public class EmployeeProfileCtrlr {
	@Autowired
	private EmployeeProfileService employeeProfileService;
	@Autowired
	private PayrollTimePeriodService pTimePeriodService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserGroupAccessRightService ugarService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMain (Model model) {
		return loadMainEmployeeProfile(model);
	}

	@RequestMapping(value="/form", method = RequestMethod.GET)
	public String showNewEmployeeProfileForm (Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(null, companyId);
		return loadForm(employeeProfile, model, session);
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveNewPatientRecord(@ModelAttribute("employeeProfile") EmployeeProfile employeeProfile,
			BindingResult result, Model model, HttpSession session) throws IOException {
		return saveEmployeeProfile(employeeProfile, result, model, session);
	}

	/**
	 * Load the Patient Profile main page
	 * @throws ConfigurationException 
	 */
	@RequestMapping(value="/{employeeId}", method = RequestMethod.GET)
	public String showPatientProfileMain (@PathVariable(value="employeeId") int employeeId,
			@RequestParam(value="reload", required=false) Integer reload,
			Model model, HttpSession session) throws ConfigurationException {
		model.addAttribute("employeeId", employeeId);
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		loadEmployeeDTRDetails(employeeId, employeeProfile.getEmployeeScheduleDtrDtos(), model);
		loadEmployeeDocuments(employeeId, EmployeeFileDocumentDto.FT_ALL, 1, model);
		model.addAttribute("employeeProfile", employeeProfile);
		model.addAttribute("reload", reload != null ? 1 : 0);
		return loadMainEmployeeProfile(model);
	}

	@RequestMapping(value="/{employeeId}", method=RequestMethod.POST)
	public String saveEditPatientForm(@ModelAttribute("employeeProfile") EmployeeProfile employeeProfile,
			BindingResult result, Model model, HttpSession session) throws IOException {
		return saveEmployeeProfile(employeeProfile, result, model, session);
	}

	@RequestMapping(value="/{employeeId}/form", method = RequestMethod.GET)
	public String showEditPatientProfileForm (@PathVariable(value="employeeId") int employeeId, 
			Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		return loadForm(employeeProfile, model, session);
	}

	@RequestMapping(value="/{employeeId}/form", method=RequestMethod.POST)
	public String saveStudent(@ModelAttribute("employeeProfile") EmployeeProfile employeeProfile,
			BindingResult result, Model model, HttpSession session) throws IOException {
		return saveEmployeeProfile(employeeProfile, result, model, session);
	}

	@RequestMapping(value="/ePBasicInfoForm", method = RequestMethod.GET)
	public String showEPBasicForm (@RequestParam(value="employeeId", required=false) Integer employeeId,
			Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		model.addAttribute("employeeProfile", employeeProfile);
		return "EPBasicInfoForm.jsp";
	}

	@RequestMapping(value="/ePFamilyForm", method = RequestMethod.GET)
	public String showEPFamilyForm (@RequestParam(value="employeeId", required=false) Integer employeeId,
			Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		model.addAttribute("employeeProfile", employeeProfile);
		return "EPFamilyForm.jsp";
	}

	@RequestMapping(value="/ePEductAttainmentForm", method = RequestMethod.GET)
	public String showEPEducAttainmentForm (@RequestParam(value="employeeId", required=false) Integer employeeId,
			Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		model.addAttribute("employeeProfile", employeeProfile);
		return "EPEducForm.jsp";
	}

	@RequestMapping(value="/ePLastForm", method = RequestMethod.GET)
	public String showLastForm (@RequestParam(value="employeeId", required=false) Integer employeeId,
			Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		model.addAttribute("employeeProfile", employeeProfile);
		return "EPLastForm.jsp";
	}

	private String saveEmployeeProfile(EmployeeProfile employeeProfile, 
			BindingResult result, Model model, HttpSession session) throws IOException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		int formPage = employeeProfile.getFormPage();
		if (formPage == EmployeeProfile.FORM_PAGE_BASIC_INFO) {
			employeeProfile.deserializeEmployeeChildren();
		} else if(formPage == EmployeeProfile.FORM_PAGE_FINAL) {
			employeeProfile.deserializeEmergencyContacts();
			employeeProfile.deserializeLicensesCertificates();
			employeeProfile.deserializeSeminarsAttended();
			employeeProfile.deserializeNationalCompetencies();
		}
		synchronized (this) {
			employeeProfileService.saveEmployeeProfile(employeeProfile, user, result);
			if (result.hasErrors()) {
				return loadForm(employeeProfile, model, session);
			}
		}
		model.addAttribute("success", true);
		model.addAttribute("employeeProfileId", employeeProfile.getId());
		model.addAttribute("employeeId", employeeProfile.getEmployeeId());
		model.addAttribute("createdBy", employeeProfile.getCreatedBy());
		model.addAttribute("ebObjectId", employeeProfile.getEbObjectId());
		return "EmployeeProfileFormSuccess.jsp";
	}

	private String loadForm(EmployeeProfile employeeProfile, Model model, HttpSession session) {
		employeeProfile.serializeEmployeeChildren();
		employeeProfile.serializeEmergencyContacts();
		employeeProfile.serializeLicensesCertificates();
		employeeProfile.serializeSeminarsAttended();
		employeeProfile.serializeNationalCompetencies();
		List<Gender> genders = null;
		List<CivilStatus> civilStatuses = null;
		Integer educationalAttaimentId = null;
		if (employeeProfile.getEmployeeId() == null) {
			genders = employeeProfileService.getAllActiveGender();
			civilStatuses = employeeProfileService.getAllActiveStatus();
		} else {
			genders = employeeProfileService.getGendersWithInactive(employeeProfile.getGenderId());
			civilStatuses = employeeProfileService.getCivStatusesWithInactive(employeeProfile.getCivilStatusId());
			EmployeeEducationalAttainment educAttainment =
					employeeProfile.getEmployeeEducationalAttainment();
			if(educAttainment != null) {
				educationalAttaimentId = educAttainment.getEmployeeEducationalAttainmentTypeId();
			}
		}
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(model, user, employeeProfile.getEmployee());

		model.addAttribute("genders", genders);
		model.addAttribute("civilStatuses", civilStatuses);
		model.addAttribute("educAttTypes", employeeProfileService.getEducationalAttainments(educationalAttaimentId));
		model.addAttribute("employeeProfile", employeeProfile);
		return "EmployeeProfileForm.jsp";
	}

	private void loadMonthAndYear(Model model) {
		model.addAttribute("months", TimePeriodMonth.getMonths());
		model.addAttribute("years", pTimePeriodService.initYears());
	}

	@RequestMapping(value="/{employeeId}/dtrDetail", method = RequestMethod.GET)
	public String showEmployeeDTRDetail (@PathVariable(value="employeeId") int employeeId,
			@RequestParam(value="year") int year, @RequestParam(value="month") int month,
			Model model, HttpSession session) {
		loadEmployeeDTRDetails(employeeId, employeeProfileService.initDTR(employeeId, year, month), model);
		return "EmployeeDTRDetail.jsp";
	}

	private String loadMainEmployeeProfile (Model model) {
		loadMonthAndYear(model);
		Date currentDate = new Date();
		model.addAttribute("currentYear",  DateUtil.getYear(currentDate));
		model.addAttribute("currentMonth",  DateUtil.getMonth(currentDate) + 1);
		return "EmployeeProfile.jsp";
	}

	private void loadEmployeeDTRDetails(int employeeId, List<EmployeeScheduleDtrDto> schedules, Model model) {
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("schedules", schedules);
	}

	@RequestMapping(value="/{employeeId}/document", method = RequestMethod.GET)
	public String showEmployeeFormDoc (@PathVariable(value="employeeId") int employeeId,
			@RequestParam(value="formTypeId") int formTypeId, 
			@RequestParam(value="pageNumber") int pageNumber, 
			Model model, HttpSession session) {
		loadEmployeeDocuments(employeeId, formTypeId, pageNumber, model);
		return "EmployeeFormDocTable.jsp";
	}

	private void loadEmployeeDocuments(int employeeId, int formTypeId, int pageNumber, Model model) {
		Page<EmployeeFileDocumentDto> fileDocuments = employeeProfileService.getEmployeeFilesAndDocs(employeeId, formTypeId, pageNumber);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("fileDocuments", fileDocuments);
	}

	@RequestMapping(value="/{employeeId}/print", method = RequestMethod.GET)
	public String printEmployeeProfile(@PathVariable(
			value="employeeId")int employeeId, Model model, HttpSession session) {
		Integer companyId = getFirstCompanyId(session);
		EmployeeProfile employeeProfile = employeeProfileService.initEmployeeProfile(employeeId, companyId);
		List<EmployeeProfile> employeeProfiles = new ArrayList<>();
		employeeProfiles.add(employeeProfile);
		JRDataSource dataSource = new JRBeanCollectionDataSource(employeeProfiles);

		ReferenceDocument referenceDocument = employeeProfile.getReferenceDocument();
		if (referenceDocument != null && referenceDocument.getFile() != null && !referenceDocument.getFile().trim().isEmpty()) {
			String photo = referenceDocument.getFile().split(",")[1];
			InputStream base64Img = ImageUtil.convBase64ImgToInputStream(photo);
			model.addAttribute("base64Img", base64Img);
		}

		model.addAttribute("companyLogo", employeeProfile.getEmployee().getCompany().getLogo());
		model.addAttribute("company", employeeProfile.getEmployee().getCompany().getName());
		model.addAttribute("employeeName", employeeProfile.getEmployee().getFullName().toUpperCase());
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", "pdf");
		return "EmployeeProfile.jasper";
	}

	private void loadSelections(Model model, User user, Employee employee) {
		Integer companyId = employee.getCompanyId() != null ? employee.getCompanyId() : 0;
		Integer divisionId = employee.getDivisionId() != null ? employee.getDivisionId() : 0;
		Integer employeeTypeId = employee.getEmployeeTypeId() != null ? employee.getEmployeeTypeId() : 0;
		Integer employeeStatusId = employee.getEmployeeStatusId() != null ? employee.getEmployeeStatusId() : 0;

		//Company
		Collection<Company> companies = companyService.getCompaniesWithInactives(user, companyId);
		model.addAttribute("companies", companies);

		//Division
		Collection<Division> divisions = divisionService.getActiveDivisions(divisionId);
		model.addAttribute("divisions", divisions);

		//Employee type
		List<EmployeeType> employeeTypes = employeeService.getEmployeeTypes(employeeTypeId);
		model.addAttribute("employeeTypes", employeeTypes);

		//Employee status
		List<EmployeeStatus> employeeStatuses = employeeService.getEmployeeStatuses(employeeStatusId);
		model.addAttribute("employeeStatuses", employeeStatuses);
	}

	private Integer getFirstCompanyId (HttpSession session) {
		Integer companyId = 0;
		Collection<Company> companies = companyService.getCompaniesWithInactives(CurrentSessionHandler.getLoggedInUser(session), 0);
		if (!companies.isEmpty()) {
			companyId = companies.iterator().next().getId();
		}
		companies = null;
		return companyId;
	}

	@RequestMapping(value="/hasAccessRight", method=RequestMethod.GET)
	public @ResponseBody String hasAccessRight(@RequestParam(value="userGroupId") int userGroupId) {
		boolean hasAccessRight = ugarService.hasAccessRight(userGroupId, EmployeeProfile.PRODUCT_KEY);
		return hasAccessRight ? "true" : "false";
	}

	@RequestMapping(value="/generateEmployeeNumber", method=RequestMethod.GET)
	public @ResponseBody String generateEmpId(@RequestParam(value="companyId") Integer companyId, HttpSession session) {
		String employeeNo = employeeProfileService.genEmployeeNumber(companyId);
		return employeeNo;
	}
}
