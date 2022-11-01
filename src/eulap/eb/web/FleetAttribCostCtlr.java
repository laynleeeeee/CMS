package eulap.eb.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FleetType;
import eulap.eb.service.FleetAttribCostService;
import eulap.eb.service.FleetProfileService;
import eulap.eb.service.FleetTypeService;
import eulap.eb.web.dto.FleetAttribCostJrDto;
import eulap.eb.web.dto.FleetItemConsumedDto;
import eulap.eb.web.dto.FleetJobOrderDto;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Fleet attributable cost controller.

 *
 */
@Controller
@RequestMapping("/fleetAttributableCost")
public class FleetAttribCostCtlr {
	@Autowired
	private FleetAttribCostService fleetAttribCostService;
	@Autowired
	private FleetProfileService fleetProfileService;
	@Autowired
	private FleetTypeService FleetTypeService;

	private static final String REPORT_TITLE = "ATTRIBUTABLE COST";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMain (@RequestParam (value="refObjectId", required = false) Integer refObjectId, 
			Model model, HttpSession session) {
		FleetProfile fp = fleetProfileService.getFleetProfileByEbObject(refObjectId, true);
		Integer divisionId = fp != null ? fp.getDivisionId() : null;
		if (divisionId != null) {
			model.addAttribute("fpDivisionId", divisionId);
		}
		model.addAttribute("fp", fp);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, PageSetting.START_PAGE);
		getJobOrders(divisionId, null, null, PageSetting.START_PAGE, model);
		getItemsConsumed(divisionId, null, null, null, null, PageSetting.START_PAGE, model);
		return "FleetAttributableCost.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, value="/jobOrders")
	public String showJobOrders (@RequestParam (value="divisionId", required = false) Integer divisionId,
			@RequestParam (value="dateFrom", required = false) Date dateFrom,
			@RequestParam (value="dateTo", required = false) Date dateTo,
			@RequestParam (value="pageNumber", required = false) int pageNumber,
			Model model, HttpSession session) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		getJobOrders(divisionId, dateFrom, dateTo, pageNumber, model);
		return "FleetACJobOrder.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, value="/itemsConsumed")
	public String showItemsConsumed (@RequestParam (value="divisionId", required = false) Integer divisionId, 
			@RequestParam (value="dateFrom", required = false) Date dateFrom,
			@RequestParam (value="dateTo", required = false) Date dateTo,
			@RequestParam (value="stockCode", required = false) String stockCode,
			@RequestParam (value="itemDescription", required = false) String itemDescription,
			@RequestParam (value="pageNumber", required = false) int pageNumber,
			Model model, HttpSession session) {
		getItemsConsumed(divisionId, dateFrom, dateTo, stockCode, itemDescription, pageNumber, model);
		return "FleetACItemsConsumed.jsp";
	}

	private void getJobOrders(Integer divisionId, Date dateFrom, Date dateTo, int pageNumber, Model model) {
		Page<FleetJobOrderDto> jobOrders = fleetAttribCostService.getFleetJobOrders(divisionId,
				dateFrom, dateTo, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		model.addAttribute("jobOrders", jobOrders);
	}

	private void getItemsConsumed(Integer divisionId, Date dateFrom, Date dateTo, String stockCode, String description, int pageNumber, Model model) {
		Page<FleetItemConsumedDto> itemsConsumed = fleetAttribCostService.getFleetItemsConsumed(divisionId, dateFrom, dateTo, stockCode, description, 
				false, null, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		model.addAttribute("icPageNumber", itemsConsumed.getPageSetting().getPageNumber());
		model.addAttribute("itemsConsumed", itemsConsumed);
	}

	@RequestMapping (value="/print", method=RequestMethod.GET)
	public String printAttrCost(@RequestParam (value="fleetProfileId") Integer fleetProfileId,
			@RequestParam (value="divisionId", required = false) Integer divisionId, 
			@RequestParam (value="dateFrom", required = false) Date dateFrom,
			@RequestParam (value="dateTo", required = false) Date dateTo,
			@RequestParam (value="stockCode", required = false) String stockCode,
			@RequestParam (value="description", required = false) String description,
			Model model, HttpSession session) {
		FleetProfile fleetProfile = fleetProfileService.getFleetProfile(fleetProfileId);
		Page<FleetJobOrderDto> jobOrders = fleetAttribCostService.getFleetJobOrders(
				divisionId, dateFrom, dateTo, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
		Page<FleetItemConsumedDto> itemsConsumed = fleetAttribCostService.getFleetItemsConsumed(
				divisionId, dateFrom, dateTo, stockCode, description, false, null, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
		List<FleetAttribCostJrDto> attribCostJrDtos = new ArrayList<>();
		FleetAttribCostJrDto attribCostJrDto = fleetAttribCostService.getFleetAttribCostJrDto(
				fleetProfile, jobOrders, itemsConsumed); // TODO Add items consumed list after when items consumed method is updated.
		attribCostJrDtos.add(attribCostJrDto);

		FleetType fleetType = FleetTypeService.getFleetTypeByReferenceObjectId(fleetProfile.getEbObjectId());
		model.addAttribute("fleetTypeId", fleetType.getId());

		Company company = fleetAttribCostService.getCompanyByRefEbObjId(fleetProfile.getEbObjectId());
		model.addAttribute("companyName", company.getName());
		if(company.getLogo() != null) {
			model.addAttribute("companyLogo", company.getLogo());
		}

		JRDataSource dataSource = new JRBeanCollectionDataSource(attribCostJrDtos);
		model.addAttribute("datasource", dataSource);

		model.addAttribute("reportTitle", REPORT_TITLE);
		model.addAttribute("codeVessel", fleetProfile.getCodeVesselName());
		model.addAttribute("make", fleetProfile.getMake());
		model.addAttribute("officialNo", fleetProfile.getOfficialNo());
		model.addAttribute("chassisNo", fleetProfile.getChassisNo());
		model.addAttribute("callSign", fleetProfile.getCallSign());
		model.addAttribute("engineNo", fleetProfile.getEngineNo());
		model.addAttribute("format", "pdf");
		return "FleetAttributableCost.jasper";
	}
}
