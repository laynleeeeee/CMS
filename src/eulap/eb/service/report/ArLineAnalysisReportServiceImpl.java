package eulap.eb.service.report;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.view.ARLineAnalysisDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.view.hibernate.ARLineAnalysis;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for generating report for AR line analysis report.

 */
@Service
public class ArLineAnalysisReportServiceImpl implements ArLineAnalysisReportService{
	private static Logger logger = Logger.getLogger(ArLineAnalysisReportServiceImpl.class);
	@Autowired
	private ARLineAnalysisDao arLineAnalysisDao;

	@Override
	public Page<ARLineAnalysis> generate(User user,
			PageSetting pageSetting, ArLineAnalysisReportParam param) {
		Page<ARLineAnalysis> reportData = arLineAnalysisDao.getARLineAnalysisData(param, pageSetting);
		return new Page<ARLineAnalysis>(pageSetting, reportData.getData(), reportData.getTotalRecords());
	}

	/**
	 * Generate Arline Analysis Report.
	 * @param param The arline analysis parameters.
	 * @return The arline analysis report.
	 */
	public JRDataSource generateReport(ArLineAnalysisReportParam param) {
		EBJRServiceHandler<ARLineAnalysis> handler = new JRArLineHandler(
				param, this);
		return new EBDataSource<ARLineAnalysis>(handler);
	}
	private static class JRArLineHandler implements EBJRServiceHandler<ARLineAnalysis> {
		private ArLineAnalysisReportParam param;
		private ArLineAnalysisReportServiceImpl serviceImpl;

		private JRArLineHandler (ArLineAnalysisReportParam param,
				ArLineAnalysisReportServiceImpl agingServiceImpl){
			this.param = param;
			this.serviceImpl = agingServiceImpl;
		}
		@Override
		public void close() throws IOException {
			serviceImpl = null;
		}
		@Override
		public Page<ARLineAnalysis> nextPage(PageSetting pageSetting) {
			Page<ARLineAnalysis> reportData = serviceImpl.arLineAnalysisDao.getARLineAnalysisData(param, pageSetting);
			return new Page<ARLineAnalysis>(pageSetting, reportData.getData(), reportData.getTotalRecords());
		}
	}
}
