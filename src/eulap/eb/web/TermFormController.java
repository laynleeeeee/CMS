package eulap.eb.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.TermService;
import eulap.eb.validator.TermValidator;

/**
 * A Controller class for term form.

 */
@Controller
@RequestMapping("/admin/termForm")
public class TermFormController {
	private static final String TERM_ATTRIB_NAME = "term";
	@Autowired
	private TermService termService;
	@Autowired
	private TermValidator termValidator;

	@RequestMapping (method = RequestMethod.GET)
	public String showTermMainForm(Model model) {
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(TERM_ATTRIB_NAME, termService.getTerm("", null, -1, 1));
		return "TermMain.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, params = {"termName", "days", "status", "pageNumber"})
	public String searchTerm(@RequestParam String termName, @RequestParam Integer days,
			@RequestParam int status, @RequestParam int pageNumber, Model model){
		Page<Term> terms = termService.getTerm(termName, days, status, pageNumber);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute(TERM_ATTRIB_NAME, terms);
		return "TermTable.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form")
	public String addTermForm(Model model) {
		Term term = new Term();
		term.setActive(true);
		return showTermForm(term, model);
	}

	@RequestMapping (method = RequestMethod.GET, value = "/form", params = {"termId"})
	public String editBusForm(@RequestParam int termId, Model model) {
		Term term = termService.getTerm(termId);
		return showTermForm(term, model);
	}

	private String showTermForm(Term term, Model model){
		model.addAttribute(term);
		return "TermForm.jsp";
	}

	@RequestMapping (value = "/form", method = RequestMethod.POST)
	public String saveBus(@ModelAttribute("term") Term term, BindingResult result,
			Model model, HttpSession session){
		User user =  CurrentSessionHandler.getLoggedInUser(session);
		termValidator.validate(term, result);
		if (result.hasErrors())
			return showTermForm(term, model);
		termService.saveTerm(term, user);
		return "successfullySaved";
	}
}
