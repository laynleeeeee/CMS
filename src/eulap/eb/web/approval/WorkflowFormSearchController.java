package eulap.eb.web.approval;

import java.util.ArrayList;
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
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormWorkflowPlugin;

/**
 * A base class for searching the different forms. 

 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class WorkflowFormSearchController {

	/**
	 * Get the plugin of this form searcher. 
	 */
	abstract FormWorkflowPlugin getWorkflowPlugin ();
	
	@RequestMapping (value="/statuses",method = RequestMethod.GET)
	public  @ResponseBody String getStatuses (@RequestParam (value="workflowName", required=true) String workflowName,
			HttpSession session, Model model) {
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormStatus> formStatuses = getWorkflowPlugin().getStatuses(workflowName, user);
		return JSONArray.fromObject(formStatuses).toString();
	}
	
	public List<Integer> parseStatuses (String strStatuses) {
		List<Integer> statuses = new ArrayList<Integer>();
		if (strStatuses != null)
			for (String status : strStatuses.split(";")){ 
				int statusId = Integer.valueOf(status);
				if (statusId < 1){
					statuses.clear();
					break;
				}
				statuses.add(Integer.valueOf(status));
			}
		return statuses;
	}
}
