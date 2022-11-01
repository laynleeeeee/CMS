package eulap.eb.web.form.inv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eulap.common.util.DateUtil;
import eulap.common.util.ReportUtil;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.CompanyService;
import eulap.eb.service.PositionService;
import eulap.eb.service.UserService;
import eulap.eb.web.dto.DailyCashCollection;
import eulap.eb.web.dto.DailyCashCollectionDenomination;
import eulap.eb.web.dto.DailyCashCollectionMain;
import bp.web.ar.CurrentSessionHandler;

/**
 * Daily cash collection controller.

 *
 */
@Controller
@RequestMapping("/dailyCashCollection")
public class DailyCashCollectionController {
	private static Logger logger = Logger.getLogger(DailyCashCollectionController.class);
	private final static String REPORT_TITLE = "DAILY CASH COLLECTION";
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private CashSaleService cashSaleService;
	@Autowired
	private UserService userService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String generatateReport (Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		loadSelections(user,model);
		model.addAttribute("currentDate", DateUtil.formatDate(new Date()));
		return "DailyCashCollection.jsp";
	}

	private void loadSelections(User user, Model model) {
		// Get Companies
		List<Company> companies = companyService.getCompanies(user);
		model.addAttribute("companies", companies);
		// Get Positions
		List<Position> positions = positionService.getAllPositions();
		model.addAttribute("positions", positions);
	}

	@RequestMapping (value="/generate", method = RequestMethod.GET)
	public String generatePDF(
			@RequestParam (value="companyId", required=true) Integer companyId,
			@RequestParam (value="userId", required=true) Integer userId,
			@RequestParam (value="date", required=true) Date date,
			@RequestParam (value="orderType", defaultValue="REFERENCE_NO") String orderType,
			@RequestParam (value="status") Integer status,
			@RequestParam (value="formatType", required = true) String formatType, 
			Model model, HttpSession session) throws JRException, ConfigurationException {
		logger.info("Generating " + formatType + " version of daily sales.");
		List<DailyCashCollection> dailyCashCollection = 
				cashSaleService.getDailyCashCollections(companyId, userId, date, orderType, status);
		DailyCashCollectionDenomination collectionDomination = new DailyCashCollectionDenomination(dailyCashCollection);
		List<DailyCashCollectionDenomination> dominations = new ArrayList<>();
		dominations.add(collectionDomination);
		DailyCashCollectionMain collectionMain = new DailyCashCollectionMain(dominations);
		List<DailyCashCollectionMain> cashCollectionMains = new ArrayList<>();
		cashCollectionMains.add(collectionMain);
		JRDataSource dataSource = new JRBeanCollectionDataSource(cashCollectionMains, false);
		model.addAttribute("datasource", dataSource);
		model.addAttribute("format", formatType );

		User loggedUser = CurrentSessionHandler.getLoggedInUser(session);
		ReportUtil.getPrintDetails(model, loggedUser);
		Company company = null;
		User user = null;
		String strDate = "";
		if (companyId != null && userId != null && date != null) {
			company = companyService.getCompany(companyId);
			user = userService.getUser(userId);
			model.addAttribute("companyLogo", company.getLogo());
			model.addAttribute("companyName", company.getName());
			model.addAttribute("companyAddress", company.getAddress());
			model.addAttribute("reportTitle" , REPORT_TITLE);
			if (user != null) {
				String middleInitial = user.getMiddleName().isEmpty() ?
						"" : String.valueOf(user.getMiddleName().trim().charAt(0) + ".");
				model.addAttribute("cashierName", user.getLastName() + ", " + user.getFirstName() 
						+ " " + middleInitial);
			}
			strDate = DateUtil.formatDate(date);
			model.addAttribute("date", strDate);
		} else {
			logger.error("Company , user, and  date are required");
			throw new RuntimeException("Company , user, and  date are required");
		}
		logger.info("Sucessfully loaded the daily sales");
		return "DailyCashCollection.jasper";
	}
	
	
}
