package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArReceiptTransactionDao;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.StatementOfAccountDto;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Business logic for generating report for statement of account.

 */
@Service
public class StatementOfAccountService {
	@Autowired
	private ArCustomerAcctDao customerAcctDao;
	@Autowired
	private ArReceiptTransactionDao receiptTransactionDao;
	@Autowired
	private CustomerAdvancePaymentDao capDao;

	/**
	 * Generate the datasource of the SOA report.
	 */
	public JRDataSource generateSoaReport(StatementOfAccountParam param) {
		EBJRServiceHandler<StatementOfAccountDto> handler = new JRStatementOfAcctHandler(param, this);
		return new EBDataSource<StatementOfAccountDto> (handler);
	}

	private static class JRStatementOfAcctHandler implements EBJRServiceHandler<StatementOfAccountDto> {
		private Logger logger = Logger.getLogger(JRStatementOfAcctHandler.class);
		private StatementOfAccountParam param;
		private StatementOfAccountService service;

		private JRStatementOfAcctHandler (StatementOfAccountParam param, StatementOfAccountService service){
			this.param = param;
			this.service = service;
		}

		@Override
		public void close() throws IOException {
			service = null;
		}

		@Override
		public Page<StatementOfAccountDto> nextPage(PageSetting pageSetting) {
			List<StatementOfAccountDto> statementOfAccts = new ArrayList<StatementOfAccountDto>();
			Page<StatementOfAccountDto> soaData = service.customerAcctDao.getSoaData(param, pageSetting);
			if (!soaData.getData().isEmpty()) {
				Date dueDate = null;
				double balance = 0;
				String drReferenceIds = null;
				double collectedPayment = 0;
				double advancePayment = 0;
				for (StatementOfAccountDto dto : soaData.getData()) {
					advancePayment = 0;
					collectedPayment = dto.getCollectionAmount();
					drReferenceIds = dto.getDrReferenceIds();
					// if DR reference id is not null then the source form is AR invoice
					if (drReferenceIds != null && !drReferenceIds.isEmpty()) {
						dto.setTransactionNumber(service.capDao.getSalesOrderRefNumber(drReferenceIds));
						advancePayment = service.customerAcctDao.getPaidAdvances(drReferenceIds);
						dto.setCollectedAdvanceAmt(advancePayment);
						if (collectedPayment > 0) {
							if (advancePayment > collectedPayment) {
								collectedPayment = advancePayment - collectedPayment;
							} else {
								collectedPayment = collectedPayment - advancePayment;
							}
						}
					}
					dto.setCollectionAmount(collectedPayment);
					balance = dto.getTransactionAmount() - collectedPayment - advancePayment;
					if (NumberFormatUtil.roundOffTo2DecPlaces(balance) != 0) { // Only unpaid and partially paid will be included
						logger.debug(balance +"is the balance for transaction " + dto.getTransactionNumber() );
						dueDate =  DateUtil.addDaysToDate(dto.getTransactionDate(), dto.getTermDays());
						logger.debug(dueDate + " is the due date for this SOA");
						dto.setDueDate(DateUtil.formatDate(dueDate));
						dto.setBalance(balance);
						statementOfAccts.add(dto);
					}
				}
			}
			return new Page<StatementOfAccountDto>(pageSetting, statementOfAccts, soaData.getTotalRecords());
		}
	}

	/**
	 * Generate SOA report data with Account Collection Reference number.
	 */
	public JRDataSource generateSoaReportWithAcRef(StatementOfAccountParam param) {
		EBJRServiceHandler<StatementOfAccountDto> handler = new JRSoaHandler(param, customerAcctDao, receiptTransactionDao);
		return new EBDataSource<StatementOfAccountDto> (handler);
	}

	private static class JRSoaHandler implements EBJRServiceHandler<StatementOfAccountDto> {
		private StatementOfAccountParam param;
		private ArCustomerAcctDao customerAcctDao;
		private Logger logger = Logger.getLogger(JRStatementOfAcctHandler.class);

		private JRSoaHandler (StatementOfAccountParam param, ArCustomerAcctDao customerAcctDao,
				ArReceiptTransactionDao receiptTransactionDao){
			this.param = param;
			this.customerAcctDao = customerAcctDao;
		}

		@Override
		public void close() throws IOException {
			customerAcctDao = null;
		}

		@Override
		public Page<StatementOfAccountDto> nextPage(PageSetting pageSetting) {
			Page<StatementOfAccountDto> soaData = customerAcctDao.getSoaDataWithAcReference(param, pageSetting);
			List<StatementOfAccountDto> statementOfAccts = new ArrayList<StatementOfAccountDto>();

			if(!soaData.getData().isEmpty()) {
				double balance = 0;
				Date dueDate = null;
				for(StatementOfAccountDto transaction : soaData.getData()) {
					balance = transaction.getTransactionAmount() - transaction.getCollectionAmount();
					if(NumberFormatUtil.roundOffTo2DecPlaces(balance) != 0) {//Only unpaid and partially paid will be included
						logger.debug(balance +"is the balance for transaction " + transaction.getTransactionNumber() );
						dueDate =  DateUtil.addDaysToDate(transaction.getTransactionDate(), transaction.getTermDays());
						logger.debug(dueDate + " is the due date for this SOA");
						transaction.setDueDate(DateUtil.formatDate(dueDate));
						transaction.setBalance(balance);
						statementOfAccts.add(transaction);
					}
				}
			}
			return new Page<StatementOfAccountDto>(pageSetting, statementOfAccts, soaData.getTotalRecords());
		}
	}

	/**
	 * Get the statement of account (billing statement) report data
	 * @param param The statement of account (billing statement) report parameter object
	 * @return The statement of account (billing statement) report data
	 */
	public JRDataSource generateBillingStatementReport(StatementOfAccountParam param) {
		EBJRServiceHandler<StatementOfAccountDto> handler = new JRBillingStatementHandler(param, this);
		return new EBDataSource<StatementOfAccountDto> (handler);
	}

	private static class JRBillingStatementHandler implements EBJRServiceHandler<StatementOfAccountDto> {
		private StatementOfAccountParam param;
		private StatementOfAccountService service;

		private JRBillingStatementHandler (StatementOfAccountParam param, StatementOfAccountService service){
			this.param = param;
			this.service = service;
		}

		@Override
		public void close() throws IOException {
			service = null;
		}

		@Override
		public Page<StatementOfAccountDto> nextPage(PageSetting pageSetting) {
			return service.customerAcctDao.getBillingStatement(param, pageSetting);
		}
	}
}
