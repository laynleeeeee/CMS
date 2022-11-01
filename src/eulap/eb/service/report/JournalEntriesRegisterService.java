package eulap.eb.service.report;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.JournalEntriesRegisterDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that handles the business logic for generating {@link JournalEntriesRegisterDto}

 */
@Service
public class JournalEntriesRegisterService {
	@Autowired
	private AccountDao accountDao;

	/**
	 * Get the list of journal entries register.
	 * @param companyId The company id.
	 * @param strFromDate The start date.
	 * @param strToDate The end date.
	 * @param statusId The status id.
	 * @param pageSetting The page setting.
	 * @return
	 */
	public Page<JournalEntriesRegisterDto> getJournalEntriesRegister (Integer companyId, String strFromDate, 
			String strToDate, Integer statusId, PageSetting pageSetting) {
		Date fromDate = DateUtil.isvalidDateFormat(strFromDate) ? DateUtil.parseDate(strFromDate) : null;
		Date toDate = DateUtil.isvalidDateFormat(strToDate) ? DateUtil.parseDate(strToDate) : null;
		Page<JournalEntriesRegisterDto> ret = accountDao.getJournalEntriesRegister(companyId, 
				fromDate, toDate, statusId, "ALL", "",
				new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT));
		return ret;
	}

	/**
	 * Generate the datasource for Journal Entries Register report.
	 */
	public JRDataSource generateJERegisterDatasource(Integer companyId, String strDateFrom, 
			String strDateTo, Integer statusId, String source, String refNo) {
		EBJRServiceHandler<JournalEntriesRegisterDto> jeRegisterHandler = new JournalEntriesJRHandler(accountDao,
				companyId, strDateFrom, strDateTo, statusId, source, refNo);
		return new EBDataSource<JournalEntriesRegisterDto>(jeRegisterHandler);
	}

	private static class JournalEntriesJRHandler implements EBJRServiceHandler<JournalEntriesRegisterDto> {
		private static Logger logger = Logger.getLogger(JournalEntriesJRHandler.class);
		private final AccountDao accountDao;
		private final Integer companyId;
		private final Date dateFrom;
		private final Date dateTo;
		private final Integer statusId;
		private final String source;
		private final String refNo;
		private Page<JournalEntriesRegisterDto> currentPage;

		private JournalEntriesJRHandler (AccountDao accountDao, Integer companyId, String strDateFrom,
				String strDateTo, Integer statusId, String source, String refNo) {
			this.accountDao = accountDao;
			this.companyId = companyId;
			this.dateFrom = DateUtil.parseDate(strDateFrom);
			this.dateTo = DateUtil.parseDate(strDateTo);
			this.statusId = statusId;
			this.source = source;
			this.refNo = refNo;
		}

		@Override
		public void close() throws IOException {
			currentPage = null;
		}

		@Override
		public Page<JournalEntriesRegisterDto> nextPage(PageSetting pageSetting) {
			currentPage = accountDao.getJournalEntriesRegister(companyId, dateFrom, dateTo, statusId, source, refNo, pageSetting);
			logger.debug("Retrieved "+currentPage.getDataSize()+" transactions for the report.");
			return currentPage;
		}
	}
}
