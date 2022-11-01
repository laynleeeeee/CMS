package eulap.eb.dao.view;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.view.hibernate.ARLineAnalysis;
import eulap.eb.service.report.ArLineAnalysisReportParam;

/**
 * Data Access Layer of {@link ARLineAnalysis}

 *
 */
public interface ARLineAnalysisDao {

	/**
	 * Get the data for AR Line Analysis Report.
	 * @param param The class that holds the search parameters for the report.
	 * @param pageSetting The page setting.
	 * @return The data for the report.
	 */
	Page<ARLineAnalysis> getARLineAnalysisData(ArLineAnalysisReportParam param, PageSetting pageSetting);
}
