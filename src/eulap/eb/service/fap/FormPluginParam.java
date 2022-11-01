package eulap.eb.service.fap;

import java.util.List;

import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;

/**
 * Form plugin parameters

 *
 */
public class FormPluginParam {
	private final User user;
	private final String workflowName;
	private final String searchCriteria;
	private final List<Integer> statuses;
	private final PageSetting pageSetting;
	public FormPluginParam (User user, String workflowName, String searchCriteria, List<Integer> statuses, PageSetting pageSetting) {
		this.user = user;
		this.searchCriteria = searchCriteria;
		this.statuses = statuses;
		this.pageSetting = pageSetting;
		this.workflowName = workflowName;
	}

	
	public User getUser() {
		return user;
	}

	public String getSearchCriteria() {
		return searchCriteria;
	}

	public List<Integer> getStatuses() {
		return statuses;
	}

	public PageSetting getPageSetting() {
		return pageSetting;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}
	
	@Override
	public String toString() {
		return "FormPluginParam [user=" + user + ", searchCriteria="
				+ searchCriteria + ", statuses=" + statuses + "]";
	}
	
	
}
