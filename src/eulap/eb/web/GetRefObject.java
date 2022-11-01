package eulap.eb.web;

import java.io.InvalidClassException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOLinkHelper;

/**
 * Get the the reference object's short description.

 *
 */
@Controller
@RequestMapping ("getRefObject")
public class GetRefObject {

	@Autowired
	private OOLinkHelper ooLinkHelper;

	@RequestMapping (value="/shortDesc", method = RequestMethod.GET)
	public @ResponseBody String getRefShortDesc (@RequestParam (value="ebObjectId", required = true) Integer ebObjectId,
			@RequestParam (value="orTypeId", required = true) Integer orTypeId, HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		String shortDesc  = ooLinkHelper.getRefShortName(ebObjectId, orTypeId, user);
		return shortDesc == null ? "" : shortDesc;
	}

	@RequestMapping (value="/id", method = RequestMethod.GET)
	public @ResponseBody String getRefObject (@RequestParam (value="ebObjectId", required = true) Integer ebObjectId,
			@RequestParam (value="orTypeId", required = true) Integer orTypeId) throws InvalidClassException, ClassNotFoundException {
		EBObject ebObject = ooLinkHelper.getReferenceObject(ebObjectId, orTypeId);
		if(ebObject == null) {
			return "";
		}
		return ebObject.getId()+"";
	}
}
