package eulap.eb.web.form.inv;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.service.CashSaleProcessingService;
import eulap.eb.service.EBFormServiceHandler;
import eulap.eb.validator.inv.CashSaleProcessingValidator;

/**
 * Controller for cash sale webservice.

 *
 */
@Controller
@RequestMapping("/webservice/cashSale")
public class CashSaleWebServiceCtrlr {
	private static Logger logger = Logger.getLogger(CashSaleWebServiceCtrlr.class);
	@Autowired
	private CashSaleProcessingValidator csProcessingValidator;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private CashSaleProcessingService processingService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(csProcessingValidator);
	}

	@RequestMapping(value="/post", method=RequestMethod.POST)
	public @ResponseBody String syncFloreantServerData(HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, Model model) 
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		logger.info("Posting to admin ...");
		List<CashSale> cashSales = processingService.convert2FCashsales(request, response);
		return processCashSales(cashSales, model, response);
	}

	@RequestMapping(value="/post/authenticate", method=RequestMethod.POST)
	public @ResponseBody String authenticateServerData(HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, Model model) 
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		logger.info("Authenticating the user.");
		Boolean isValidUser = processingService.isValidCredential(request, response);
		writeOutputStream(response, isValidUser);
		return "Done authenticating the user.";
	}

	@RequestMapping(value="/post/bitpos", method=RequestMethod.POST)
	public @ResponseBody String syncBitposServerData(HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, Model model) 
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		logger.info("Posting to admin ...");
		List<CashSale> cashSales = processingService.convert2BCashsales(request, response);
		return processCashSales(cashSales, model, response);
	}

	private String processCashSales (List<CashSale> cashSales, Model model, 
			HttpServletResponse response) throws ClassNotFoundException, IOException {
		DataBinder result = null;
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject("Received " + cashSales.size() + " transactions. \n"
					+ "Saving transactions...\n");
			oos.flush();
			if (cashSales != null && !cashSales.isEmpty()) {
				int cnt = 1;
				for (CashSale cs : cashSales) {
					String msge = + cnt++ + "." + "OR Number " + cs.getSalesInvoiceNo() + "\n";
					result = new DataBinder(cs);
					csProcessingValidator.validate(cs, result.getBindingResult());
					result.setValidator(csProcessingValidator);
					boolean hasError = false;
					if (result.getBindingResult().hasErrors()) {
						String errMsg = "";
						for (ObjectError oe : result.getBindingResult().getAllErrors()) {
							errMsg += oe.getDefaultMessage() + "\n";
						}
						msge += errMsg;
						hasError = true;
					}
					if (!hasError) {
						ebFormServiceHandler.saveForm(cs, cs.getUser());
						msge += "Successfully saved cash sale.\n";
					}
					msge += "-----------------------------------------------------------------------\n";
					oos.writeObject(msge);
					if (cnt % 5 == 0) {
						oos.flush();
					}
				}
			}
			oos.writeObject("completed");
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
		logger.info("Done saving as cash sales ...");
		return "Done updating admin database.";
	}

	private void writeOutputStream (HttpServletResponse response, Object object) throws IOException {
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(object);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
	}
}
