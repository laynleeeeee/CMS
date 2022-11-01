package eulap.eb.web.form.inv;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.ProductLine;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ProductLineService;
import eulap.eb.validator.ProductLineValidator;

/**
 * Controller class for Product Line.

 *
 */
@Controller
@RequestMapping ("/admin/productLine")
public class ProductLineController {
	private final Logger logger = Logger.getLogger(ProductLineController.class);
	@Autowired
	private ProductLineService productLineService;
	@Autowired
	private ProductLineValidator productLineValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping (method = RequestMethod.GET)
	public String showProductList (Model model, HttpSession currentSession) {
		List<String> searchStatus = SearchStatus.getSearchStatus();
		loadProductList(model, "","",  "All", 1);
		model.addAttribute("status", searchStatus);
		return "ProductLine.jsp";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String searchProductline(@RequestParam(value="productLine", required=false) String productLine,
			@RequestParam(value="rawMaterial", required=false) String rawMaterial,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="pageNumber", required=false) int pageNumber,
			HttpSession session, Model model) {
		loadProductList(model, productLine, rawMaterial, status, pageNumber);
		return "ProductLineTable.jsp";
	}

	private void loadProductList(Model model, String productLine, String rawMaterial, String searchStatus, int pageNumber) {
	Page<ProductLine> productLines = productLineService.getProductList(productLine, rawMaterial, searchStatus, pageNumber);
		model.addAttribute("productLines", productLines);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String showProductlineFormForm(@RequestParam(value="pId", required=false) Integer productLineId,
			HttpSession session, Model model) {
		ProductLine productLine = new ProductLine();
		if(productLineId != null) {
			productLine = productLineService.getProductLine(productLineId);
		} else {
			productLine.setActive(true);
		}
		logger.info("Showing the user company form.");
		return loadProductLineForm(model, productLine);
	}

	private String loadProductLineForm(Model model, ProductLine productLine) {
		productLine.serializeProductLineItems();
		model.addAttribute("productLine", productLine);
		return "ProductLineForm.jsp";
	}

	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String saveUserCompany(@ModelAttribute("productLine") ProductLine productLine,
			BindingResult result, HttpSession session, Model model) {
		logger.info("Saving the Product Line form.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		productLine.deSerializeProductLineItems();
		productLineValidator.validate(productLine, result);
		if(result.hasErrors()) {
			logger.debug("Form has error/s. Reloading the User Company form.");
			return loadProductLineForm(model, productLine);
		}
		productLineService.saveProductLine(productLine, user);
		return "successfullySaved";
	}
}
