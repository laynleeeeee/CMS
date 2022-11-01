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
import eulap.eb.domain.hibernate.DocumentType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AdminDocumentTypeService;

/**
 * Controller class for Document Type Settings.

 *
 */
@Controller
@RequestMapping("/admin/documentType")
public class AdminDocumentTypeCtrlr {
	private final Logger logger = Logger.getLogger(AdminActionNoticeCtrlr.class);

	@Autowired
	private AdminDocumentTypeService documentTypeService;

	private static final String DOCUMENT_TYPE_ATTRIB_NAME = "documentType";

	@InitBinder
	public void initBindier(WebDataBinder binder) {
		DateUtil.registerDateAndTimeFormat(binder);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loadMainForm(Model model){
		logger.info("Loading Document Type Settings.");
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, 1);
		model.addAttribute(DOCUMENT_TYPE_ATTRIB_NAME, documentTypeService.getDocumentTypes("", -1, 1));
		return "DocumentType.jsp";
	}

	@RequestMapping(method=RequestMethod.GET, params = {"name", "status", "pageNumber"})
	public String searchDocumentTypes(@RequestParam String name, @RequestParam int status,
			@RequestParam int pageNumber, Model model){
		logger.info("Retrieving Document Types");
		Page<DocumentType> docTypes = documentTypeService.getDocumentTypes(name, status, pageNumber);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY, pageNumber);
		model.addAttribute(DOCUMENT_TYPE_ATTRIB_NAME, docTypes);
		return "DocumentTypeTable.jsp";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/form")
	public String addForm(Model model){
		DocumentType documentType = new DocumentType();
		documentType.setActive(true);
		return showForm(documentType, model);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/form", params = {"documentTypeId"})
	public String editForm(@RequestParam int documentTypeId, Model model){
		DocumentType documentType = documentTypeService.getDocumentType(documentTypeId);
		return showForm(documentType, model);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/form")
	public String saveForm(@ModelAttribute("documentType") DocumentType documentType, BindingResult result,
			Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		logger.info("Saving Document Type: " + documentType.getName());
		documentTypeService.saveDocumentType(documentType, user, result);
		if(result.hasErrors()){
			return showForm(documentType, model);
		}
		return "successfullySaved";
	}

	/**
	 * Get the Document Type for.
	 * @param documentType The Document Type Object.
	 * @param model The Model Object.
	 * @return The Document Type view form.
	 */
	private String showForm(DocumentType documentType, Model model){
		model.addAttribute(DOCUMENT_TYPE_ATTRIB_NAME, documentType);
		return "DocumentTypeForm.jsp";
	}
}
