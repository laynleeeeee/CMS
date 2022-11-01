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
import eulap.eb.domain.hibernate.Position;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.PositionService;
import eulap.eb.validator.PositionValidator;

/**
 * Controller Class for User Position

 */
@Controller
@RequestMapping ("/admin/userPositions")
public class UserPositionController {
	private static final String USER_POSITION_NAME = "userPosition";
	@Autowired
	private PositionService posService;
	@Autowired
	private PositionValidator userPositionValidator;

	@RequestMapping (method = RequestMethod.GET)
	public String showMainForm(Model model){
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY,1);
		model.addAttribute("positions", posService.searchPosition("", -1, 1));
		return "showUserPositionMain";
	}

	@RequestMapping (value="/form", method = RequestMethod.GET)
	public String addPositionForm (Model model){
		Position position = new Position();
		position.setActive(true);
		return showForm(position,model);
	}

	@RequestMapping (value="/form", method = RequestMethod.GET, params = {"pId"})
	public String editPositionForm(@RequestParam Integer pId,
			Model model){
		Position position = posService.getPosition(pId);
		return showForm(position,model);
	}

	private String showForm(Position position, Model model){
		model.addAttribute(USER_POSITION_NAME, position);
		return "UserPositionForm.jsp";
	}

	@RequestMapping (method = RequestMethod.GET, params = {"name","status","pageNumber"})
	public String searchPosition(@RequestParam String name,
			@RequestParam Integer status, @RequestParam Integer pageNumber, Model model) {
		Page<Position> positions = posService.searchPosition(name, status, pageNumber);
		model.addAttribute(PageSetting.PAGE_NUMBER_ATTRIBUTE_KEY,pageNumber);
		model.addAttribute("positions", positions);
		return "UserPositionTable.jsp";
	}

	@RequestMapping (value="/form", method = RequestMethod.POST)
	public String savePosition (@ModelAttribute("userPosition")Position position, BindingResult result,
			Model model, HttpSession session){
		User user = CurrentSessionHandler.getLoggedInUser(session);
		userPositionValidator.validate(position, result);
		if(result.hasErrors()){
			return showForm(position, model);
		}
		posService.savePosition(position, user);
		return "successfullySaved";
	}
}
