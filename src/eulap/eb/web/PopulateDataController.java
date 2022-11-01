package eulap.eb.web;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.PopulateDateService;

/**
 * A controller class that will handle the population of the item
 * and transactions

 *
 */
@Controller
@RequestMapping ("/test/populate")
public class PopulateDataController {
	@Autowired
	private PopulateDateService service;
	
	@RequestMapping(method = RequestMethod.GET, value="/items")
	public @ResponseBody String populateItems (@RequestParam (required = true, value="targetPopulation") int targetPopulation,
			HttpServletResponse response, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		service.populuteItem(targetPopulation, user);
		return "Successfully populated " + targetPopulation + " items";
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/gl")
	public @ResponseBody String populateGeneralLedger (@RequestParam (required = true, value="targetPopulation") int targetPopulation,
			HttpServletResponse response, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		service.populateGeneralLedgers(targetPopulation, user);
		return "Successfully populated " + targetPopulation + " items";
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/apinvoice")
	public @ResponseBody String populateAPInvoice (@RequestParam (required = true, value="targetPopulation") int targetPopulation,
			HttpServletResponse response, HttpSession session) throws InvalidClassException, ClassNotFoundException{
		User user = CurrentSessionHandler.getLoggedInUser(session);
		service.populateAPInvoice(targetPopulation, user);
		return "Successfully populated " + targetPopulation + " items";
	}
}
