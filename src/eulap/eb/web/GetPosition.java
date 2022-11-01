package eulap.eb.web;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eulap.eb.domain.hibernate.Position;
import eulap.eb.service.PositionService;

/**
 * Controller for getting Position object.

 *
 */
@Controller
@RequestMapping(value="getPosition")
public class GetPosition {
	@Autowired
	private PositionService posService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String loadPosition(@RequestParam(value="name", required=false)String name){
		Position position = posService.getPositionByName(name.trim());
		JSONObject jsonObject = JSONObject.fromObject(position);
		return jsonObject.toString();
	}
}
