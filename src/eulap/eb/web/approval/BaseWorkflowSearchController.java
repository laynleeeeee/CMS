package eulap.eb.web.approval;

import java.util.List;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormApprovalPlugin;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.fap.FormWorkflowPlugin;
import eulap.eb.web.dto.FormApprovalDto;

/**
 * Base class for form seacher controller.

 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class BaseWorkflowSearchController extends WorkflowFormSearchController {
	/**
	 * Get the formApproval Plugin
	 */
	abstract FormApprovalPlugin getFormPlugin ();
	
	@RequestMapping (method = RequestMethod.GET)
	public  @ResponseBody String getForms (@RequestParam (value="statuses", required=false) String strStatuses, 
			@RequestParam (value="criteria", required=false) String searchCriteria,
			@RequestParam (value="page", required=false) Integer pPage,
			@RequestParam (value="workflowName", required=true) String workflowName,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<Integer> statuses = parseStatuses(strStatuses);
		int pageNumber = 1;
		if (pPage != null)
			pageNumber = pPage;
		PageSetting pageSetting = new PageSetting(pageNumber);
		FormPluginParam param = new FormPluginParam(user, workflowName, searchCriteria, statuses, pageSetting);
		Page<FormApprovalDto> formApprovalDto = getFormPlugin().retrieveForms(param);
		JSONArray jsonArray = JSONArray.fromObject(formApprovalDto);
		return jsonArray.toString();
	}
	
	@Override
	FormWorkflowPlugin getWorkflowPlugin() {
		return getFormPlugin();
	}
}
