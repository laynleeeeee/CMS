package eulap.eb.service.report;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.view.hibernate.ARLineAnalysis;

/**
 * The business logic class of AR Line Analysis Report.

 */
public interface ArLineAnalysisReportService {

	/**
	 * Generate the AR Line Analysis Report.
	 * @param user The current logged user.
	 * @param pageSetting The page setting.
	 * @param param The parameters that the user selected. 
	 * @return The list of Ar Line Analysis report dto.
	 */
	Page<ARLineAnalysis> generate(User user, PageSetting pageSetting, ArLineAnalysisReportParam param);
}
