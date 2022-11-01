package eulap.eb.web.oo;

import java.io.InvalidClassException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.OOServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * Controller class for object info. 

 *
 */
@Controller
@RequestMapping("/oinfo")
public class ObjectInfoController {
	@Autowired
	private OOServiceHandler ooServiceHandler;
	
	@RequestMapping (value="title", method = RequestMethod.GET)
	public @ResponseBody String getTitle (
			@RequestParam (value="objectId", required=true) Integer objectId,
			HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ObjectInfo info = ooServiceHandler.getObjectInfo(objectId, user);
		return info.getTitle();
	}
	
	@RequestMapping (value="shortDescription", method = RequestMethod.GET)
	public @ResponseBody String getShortDescription (
			@RequestParam (value="objectId", required=true) Integer objectId,
			HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ObjectInfo info = ooServiceHandler.getObjectInfo(objectId, user);
		return info.getShortDescription();
	}
	
	@RequestMapping (method = RequestMethod.GET)
	public @ResponseBody String getObjectID (
			@RequestParam (value="objectId", required=true) Integer objectId,
			HttpSession session) throws InvalidClassException, ClassNotFoundException {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		ObjectInfo info = ooServiceHandler.getObjectInfo(objectId, user);
		String [] exclude = {""};
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(exclude);
		JSONObject jsonObject = JSONObject.fromObject(info, jsonConfig);
		return jsonObject.toString();
	}
}
