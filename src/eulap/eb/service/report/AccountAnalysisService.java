package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.AccountAnalysisReport;

/**
 * Account analysis report service.

 *
 */
@Service
public class AccountAnalysisService {

	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;
	/**
	 * Get the account analysis report.
	 * @param companyId The company id.
	 * @param accountId The account id.
	 * @param divisionIdFrom The first division id of the range. 
	 * @param divisionIdTo The second division id of the range.
	 * @param strDateFrom The first date of the range.
	 * @param strDateTo The second date of the range.
	 * @param pageSetting The page setting.
	 * @return The account analysis report.
	 */
	public Page<AccountAnalysisReport> getAccountAnalysisReport (Integer companyId, Integer accountId, 
			Integer divisionIdFrom, Integer divisionIdTo, String strDateFrom, String strDateTo, String description, User user, PageSetting pageSetting) {
		Date dateFrom = DateUtil.parseDate(strDateFrom);
		Date dateTo = DateUtil.parseDate(strDateTo);
		Division fromDiv = divisionDao.get(divisionIdFrom);
		Division toDiv = divisionDao.get(divisionIdTo);
		Account account = accountDao.get(accountId);
		
		Page<AccountAnalysisReport> ret = accountDao.getAccountAnalysisReport(companyId, account, 
				fromDiv.getNumber(), toDiv.getNumber(), dateFrom, dateTo, description, pageSetting);
		return ret;
	}

	/**
	 * Get the account analysis report data
	 * @param companyId The company id
	 * @param accountId The account id
	 * @param divisionIdFrom The division start range id
	 * @param divisionIdTo The division end range id
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @param description The description
	 * @return The account analysis report data
	 */
	public JRDataSource getAccntAnalysisReport(Integer companyId, Integer accountId, Integer divisionIdFrom, Integer divisionIdTo,
			String dateFrom, String dateTo, String description) {
		Division fromDiv = divisionDao.get(divisionIdFrom);
		Division toDiv = divisionDao.get(divisionIdTo);
		Account account = accountDao.get(accountId);
		EBJRServiceHandler<AccountAnalysisReport> handler = new JRAccountAnalysisHandler(companyId, account,
				fromDiv.getNumber(), toDiv.getNumber(), dateFrom, dateTo, description, this);
		return new EBDataSource<AccountAnalysisReport>(handler);
	}

	private static class JRAccountAnalysisHandler implements EBJRServiceHandler<AccountAnalysisReport> {
		private final int companyId;
		private final String fromDivNumber;
		private final String toDivNumber;
		private final String strDateFrom;
		private final String strDateTo;
		private final String description;
		private Account account;
		private AccountAnalysisService analysisService;
		private double runningBalance = 0;
		private boolean isFirstPage = true;
		private Logger logger = Logger.getLogger(JRAccountAnalysisHandler.class);

		private JRAccountAnalysisHandler (Integer companyId, Account account, String fromDivNumber, String toDivNumber,
				String strDateFrom, String strDateTo, String description, AccountAnalysisService analysisService){
			this.companyId = companyId;
			this.strDateFrom = strDateFrom;
			this.strDateTo = strDateTo;
			this.account = account;
			this.fromDivNumber = fromDivNumber;
			this.toDivNumber = toDivNumber;
			this.description = description;
			this.analysisService = analysisService;
		}

		@Override
		public void close() throws IOException {
			analysisService = null;
		}

		@Override
		public Page<AccountAnalysisReport> nextPage(PageSetting pageSetting) {
			Date dateFrom = DateUtil.parseDate(strDateFrom);
			Date dateTo = DateUtil.parseDate(strDateTo);
			List<AccountAnalysisReport> currentPage = new ArrayList<>();
			if (isFirstPage) {
				Date asOfDate = DateUtil.deductDaysToDate(dateFrom, 1);
				logger.info("Generating the Beginning Balance as of "+DateUtil.formatDate(asOfDate)+" for Account Analysis Report.");
				AccountAnalysisReport beginningBal = analysisService.accountDao.
						getAccountBalance(companyId, account, fromDivNumber, toDivNumber, asOfDate, description);
				currentPage.add(beginningBal);
				runningBalance += beginningBal.getBalance();
				isFirstPage = false;
			}

			Page<AccountAnalysisReport> ret = analysisService.accountDao.getAccountAnalysisReport(companyId, account,
					fromDivNumber, toDivNumber, dateFrom, dateTo, description, pageSetting);
			logger.info("Processing data on page "+ret.getCurrentPage());
			for (AccountAnalysisReport aAr : ret.getData()) {
				runningBalance += aAr.getBalance();
				aAr.setBalance(runningBalance);
				currentPage.add(aAr);
			}

			return new Page<>(pageSetting, currentPage, ret.getTotalRecords());
		}
	}
}
