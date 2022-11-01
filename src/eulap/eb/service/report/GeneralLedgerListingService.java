package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.GeneralLedgerListingDto;
import eulap.eb.web.dto.JournalEntriesRegisterDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that handles the business logic for generating {@link GeneralLedgerListingDetailsDto}

 */
@Service
public class GeneralLedgerListingService {
	@Autowired
	private AccountDao accountDao;

	/**
	 * Generate the datasource for General ledger listing report.
	 */
	public JRDataSource generateGLListingDatasource(Integer companyId, String strDateFrom, 
			String strDateTo, Integer accountId) {
		EBJRServiceHandler<GeneralLedgerListingDto> glListingHandler = new GeneralLdgerListingJRHandler (accountDao,
				companyId, strDateFrom, strDateTo, accountId);
		return new EBDataSource<GeneralLedgerListingDto>(glListingHandler);
	}

	private static class GeneralLdgerListingJRHandler implements EBJRServiceHandler<GeneralLedgerListingDto> {
		private static Logger logger = Logger.getLogger(GeneralLdgerListingJRHandler.class);
		private final AccountDao accountDao;
		private final Integer companyId;
		private final Date dateFrom;
		private final Date dateTo;
		private final Integer accountId;

		private GeneralLdgerListingJRHandler (AccountDao accountDao, Integer companyId, String strDateFrom, 
				String strDateTo, Integer accountId) {
			this.accountDao = accountDao;
			this.companyId = companyId;
			this.dateFrom = DateUtil.parseDate(strDateFrom);
			this.dateTo = DateUtil.parseDate(strDateTo);
			this.accountId = accountId;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public Page<GeneralLedgerListingDto> nextPage(PageSetting pageSetting) {
			List<Account> accounts = new ArrayList<>();
			List<GeneralLedgerListingDto> data = new ArrayList<>();
			if(accountId.equals(-1)){
				accounts = accountDao.getAccounts(companyId);
			} else {
				accounts.add(accountDao.get(accountId));
			}
			GeneralLedgerListingDto ledgerListingDto = null;
			for (Account account : accounts) {
				ledgerListingDto = new GeneralLedgerListingDto();
				Page<JournalEntriesRegisterDto> jeRegisterData = accountDao.getGeneralLedgerListing(companyId,
						dateFrom, dateTo, account.getId(), new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT));
				if(!jeRegisterData.getData().isEmpty()){
					ledgerListingDto.setEntriesRegisterDtos((List<JournalEntriesRegisterDto>) jeRegisterData.getData());
					ledgerListingDto.setAccountName(account.getAccountName());
					data.add(ledgerListingDto);
				}
			}
			Page<GeneralLedgerListingDto> result = new Page<GeneralLedgerListingDto>(pageSetting, data, accounts.size());
			return result;
		}
	}
}
