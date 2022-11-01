package eulap.eb.web;

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
import eulap.eb.domain.hibernate.ActionNotice;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AdminActionNoticeService;

/**
 * Controller class for Action Notice Settings

 *
 */

@Controller
@RequestMapping("/admin/actionNotice")
public class AdminActionNoticeCtrlr {

	private final Logger logger = Logger.getLogger(AdminActionNoticeCtrlr.class);

	@Autowired
	private AdminActionNoticeService actionNoticeService;

	private static final String ACTION_NOTICE_ATTRIB_NAME = "actionNotice";

	@InitBinder
	public void initBindier(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadMainForm(Model model){
		logger.info("Loading Action Notice");
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(ACTION_NOTICE_ATTRIB_NAME, actionNoticeService.getActionNotices("", -1, 1));
		return "ActionNotice.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, params = {"name", "status", "pageNumber"})
	public String searchActionNotice(@RequestParam String name,
			@RequestParam int status, @RequestParam int pageNumber, Model model){
		logger.info("Retrieving Action Notices");
		Page<ActionNotice> actionNotices = actionNoticeService.getActionNotices(name, status, pageNumber);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute(ACTION_NOTICE_ATTRIB_NAME, actionNotices);
		return "ActionNoticeTable.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/form")
	public String addForm(Model model){
		ActionNotice actionNotice = new ActionNotice();
		actionNotice.setActive(true);
		return showForm(actionNotice, model);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/form", params = {"actionNoticeId"})
	public String editForm(@RequestParam int actionNoticeId, Model model){
		ActionNotice actionNotice = actionNoticeService.getActionNotice(actionNoticeId);
		return showForm(actionNotice, model);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/form")
	public String saveForm(@ModelAttribute("actionNotice") ActionNotice actionNotice, BindingResult result,
			Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Saving Action Notice: " + actionNotice.getName());
		actionNoticeService.saveActionNotice(actionNotice, user, result);
		if(result.hasErrors()){
			return showForm(actionNotice, model);
		}
		return "successfullySaved";
	}

	/**
	 * Get the Action Notice form
	 * @param actionNotice The Action Notice Object.
	 * @param model The Model Object.
	 * @return The ActionNoticeForm.jsp file
	 */
	private String showForm(ActionNotice actionNotice, Model model){
		model.addAttribute(ACTION_NOTICE_ATTRIB_NAME, actionNotice);
		return "ActionNoticeForm.jsp";
	}
}
