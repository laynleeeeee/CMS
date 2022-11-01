package eulap.eb.web.search;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fsp.RepackingSearcher;
import eulap.eb.web.dto.FormSearchResult;

/**
 * Controller class that will be the entry point for searching Repacking forms.

 *
 */
@Controller
@RequestMapping("/searchRepacking")
public class RepackingSearchCtrlr {
	@Autowired
	private RepackingSearcher rpSearcher;

	@RequestMapping(value="{typeId}", method=RequestMethod.GET)
	public @ResponseBody String searchRepackingForms(@PathVariable("typeId") int typeId,
			@RequestParam(value="criteria", defaultValue="")
			String criteria, HttpSession session) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormSearchResult> result = rpSearcher.search(typeId, user, criteria);
		JSONArray jsonArray = JSONArray.fromObject(result);
		return jsonArray.toString();
	}
}
