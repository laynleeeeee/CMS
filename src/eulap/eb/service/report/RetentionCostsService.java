package eulap.eb.service.report;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ProjectRetentionDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.RetentionCostDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class that will handle business logic for retention costs report generation

 */

@Service
public class RetentionCostsService {
	@Autowired
	private ProjectRetentionDao projectRetentionDao;

	/**
	 * Get the retention costs report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param customerId The customer id.
	 * @param customerAcctId The customer account id.
	 * @param dateFrom The date from delivery date filter.
	 * @param dateTo The date to delivery date filter.
	 * @param asOfDate The as of date filter for all not fully paid retentions.
	 * @return The retention costs report data.
	 */
	public JRDataSource generateRetentionCostRprt(Integer companyId, Integer divisionId, Integer customerId,
			Integer customerAcctId, Date dateFrom, Date dateTo, Date asOfDate) {
		EBJRServiceHandler<RetentionCostDto> handler =
				new JRRetentionCostRprtHandler(companyId, divisionId, customerId, customerAcctId, dateFrom,
						dateTo, asOfDate, this);
		return new EBDataSource<RetentionCostDto> (handler);
	}

	private static class JRRetentionCostRprtHandler implements EBJRServiceHandler<RetentionCostDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer customerId;
		private Integer customerAcctId;
		private Date dateFrom;
		private Date dateTo;
		private Date asOfDate;
		private RetentionCostsService retentionCostService;

		private JRRetentionCostRprtHandler (Integer companyId, Integer divisionId, Integer customerId,
				Integer customerAcctId, Date dateFrom, Date dateTo, Date asOfDate, RetentionCostsService retentionCostService){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.customerId = customerId;
			this.customerAcctId = customerAcctId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.asOfDate = asOfDate;
			this.retentionCostService = retentionCostService;
		}

		@Override
		public void close() throws IOException {
			retentionCostService = null;
		}

		@Override
		public Page<RetentionCostDto> nextPage(PageSetting pageSetting) {
			return retentionCostService.projectRetentionDao.generateRetentionCostRprt(companyId,
						divisionId, customerId, customerAcctId, dateFrom, dateTo, asOfDate, pageSetting);
		}
	}

	/**
	 * Process the date jrxml parameter for the retention costs report.
	 * @param dateFrom The date from parameter.
	 * @param dateTo The date to parameter.
	 * @param asOfDate The as of date parameter.
	 * @return The processed date jrxml parameter for the retention costs report.
	 */
	public String processReportDateParam(Date dateFrom, Date dateTo, Date asOfDate) {
		if(dateFrom != null && dateTo != null) {
			return DateUtil.formatDate(dateFrom) + " To " + DateUtil.formatDate(dateTo);
		}
		return "As of " + DateUtil.formatDate(asOfDate);
	}
}
