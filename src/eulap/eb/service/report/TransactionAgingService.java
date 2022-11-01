package eulap.eb.service.report;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.ArTransactionAgingDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for generating report for the transaction aging.

 */

@Service
public class TransactionAgingService {
	private final Logger logger = Logger.getLogger(TransactionAgingService.class);
	@Autowired
	private ArTransactionDao transactionDao;
	@Autowired
	private AccountDao accountDao;

	/**
	 * Generate Jasper Report for Transaction Aging.
	 * @param param The Transaction aging parameter.
	 * @return Generated Jasper Report for Transaction Aging.
	 */
	public JRDataSource generateTransactionAging(TransactionAgingParam param) {
		String classification = param.getClassification();
		Integer accountId = !classification.equals("-1") ? accountDao.getAccountByName(classification, true).getId() : -1;
		param.setAccountId(accountId);
		EBJRServiceHandler<ArTransactionAgingDto> handler = new JRTransactionAgingHandler(param, this);
		return new EBDataSource<ArTransactionAgingDto>(handler);
	}

	private static class JRTransactionAgingHandler implements EBJRServiceHandler<ArTransactionAgingDto> {
		private TransactionAgingParam param;
		private TransactionAgingService agingService;

		private JRTransactionAgingHandler (TransactionAgingParam param, TransactionAgingService agingService){
			this.param = param;
			this.agingService = agingService;
		}

		@Override
		public void close() throws IOException {
			agingService = null;
		}

		@Override
		public Page<ArTransactionAgingDto> nextPage(PageSetting pageSetting) {
			agingService.logger.info("Generating the Ar Transaction Aging Report.");
			Page<ArTransactionAgingDto> transactionAgingData = agingService.transactionDao.genareteTransactionAging(param , pageSetting);
			agingService.logger.info("Successfully processed "+transactionAgingData.getTotalRecords()+" data for Ar Transaction Aging.");
			return transactionAgingData;
		}
	}
}
