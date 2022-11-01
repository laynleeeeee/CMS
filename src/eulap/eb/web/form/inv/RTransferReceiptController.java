package eulap.eb.web.form.inv;

import java.io.InvalidClassException;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.DateUtil;
import eulap.eb.domain.hibernate.RTransferReceipt;
import eulap.eb.domain.hibernate.TransferReceiptType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.CompanyService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.service.RTransferReceiptService;
import eulap.eb.service.TransferReceiptISService;
import eulap.eb.validator.inv.RTransferReceiptValidator;

/**
 * Controller class for Retail - Transfer Receipt.

 *
 */
@Controller
@RequestMapping("/retailTransferReceipt")
public class RTransferReceiptController {
	private static Logger logger = Logger.getLogger(RTransferReceiptController.class);
	@Autowired
	private RTransferReceiptService trService;
	@Autowired
	private RTransferReceiptValidator trValidator;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private TransferReceiptISService trISService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateUtil.regesterDateFormat(binder);
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.GET)
	public String showTRForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId", required=false) Integer pId, Model model, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Showing the Transfer Receipt form");
		RTransferReceipt transferReceipt = null;
		loadCompanies(model, user);
		if (pId != null) {
			switch (typeId) {
			case TransferReceiptType.RETAIL:
				transferReceipt = trService.getTrWithItems(pId);
				break;
			default:
				transferReceipt = trISService.getTrAndItems(pId);
				break;
			}
		} else {
			transferReceipt = new RTransferReceipt();
			transferReceipt.setTrDate(new Date());
		}

		transferReceipt.serializeItems();
		transferReceipt.setTransferReceiptTypeId(typeId);
		model.addAttribute("rTransferReceipt", transferReceipt);
		if(typeId == TransferReceiptType.INDIVIDUAL_SELECTION) {
			logger.debug("Successfully TR-IS form: "+pId);
			return "TransferReceiptISForm.jsp";
		}

		logger.info("Successfully loaded TR form.");
		return "RTransferReceiptForm.jsp";
	}

	private void loadCompanies(Model model, User user) {
		model.addAttribute("companies", companyService.getCompanies(user));
	}

	@RequestMapping(value="{typeId}/form", method=RequestMethod.POST)
	public String saveTransferReceipt(@PathVariable("typeId") int typeId, @ModelAttribute("rTransferReceipt") RTransferReceipt rTransferReceipt,
			BindingResult result, HttpSession session, Model model) throws CloneNotSupportedException, InvalidClassException, ClassNotFoundException {
		logger.info("Saving the Transfer Receipt form Type : " +typeId+".");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		rTransferReceipt.deserializeItems();
		synchronized (this) {
			trValidator.validate(rTransferReceipt, result);
			if(result.hasErrors()) {
				logger.debug("Form has error/s. Reloading the Transfer Receipt form");
				//Set the company
				if(rTransferReceipt.getCompanyId() != null)
					rTransferReceipt.setCompany(companyService.getCompany(rTransferReceipt.getCompanyId()));
				//Set the form workflow
				if(rTransferReceipt.getFormWorkflowId() != null)
					rTransferReceipt.setFormWorkflow(trService.getFormWorkflow(rTransferReceipt.getId()));
				loadCompanies(model, user);
				if(rTransferReceipt.getTransferReceiptTypeId() == TransferReceiptType.INDIVIDUAL_SELECTION){
					return "TransferReceiptISForm.jsp";
				}
				return "RTransferReceiptForm.jsp";
			}
			ebFormServiceHandler.saveForm(rTransferReceipt, user);
		}
		model.addAttribute("success", true);
		model.addAttribute("formNumber", rTransferReceipt.getTrNumber());
		model.addAttribute("formId", rTransferReceipt.getId());
		model.addAttribute("ebObjectId", rTransferReceipt.getEbObjectId());
		logger.info("Successfully saved the Retail - Transfer Receipt.");
		return "successfullySaved";
	}

	@RequestMapping(value="{typeId}/viewForm", method=RequestMethod.GET)
	public String showViewForm(@PathVariable("typeId") int typeId,
			@RequestParam(value="pId") Integer pId, Model model) {
		logger.info("Loading the view form of Transfer Receipt form");
		String viewForm = "";
		RTransferReceipt rTransferReceipt = null;
		if(typeId == TransferReceiptType.INDIVIDUAL_SELECTION) {
			rTransferReceipt = trISService.getTrIsWithItems(pId);
			viewForm = "RTransferReceiptISView.jsp";
		} else {
			rTransferReceipt = trService.getTrWithItems(pId);
			viewForm = "RTransferReceiptView.jsp";
		}
		model.addAttribute("rTransferReceipt", rTransferReceipt);
		return viewForm;
	}
}
