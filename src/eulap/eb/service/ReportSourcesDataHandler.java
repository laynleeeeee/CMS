package eulap.eb.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import eulap.eb.domain.view.hibernate.ARLineAnalysis;
import eulap.eb.domain.view.hibernate.ARReceiptRegister;
import eulap.eb.web.dto.PaymentStatus;

/**
 * Class handling of reports sources.

 *
 */
@Service
public class ReportSourcesDataHandler implements ReportSourcesHandler{
	private static Logger logger = Logger.getLogger(ReportSourcesDataHandler.class);

	@Override
	public List<String> getAllJESources() {
		logger.info("Get all sources for the Journal Entries Register.");
		List<String> sources = new ArrayList<String>();
		sources.add("AP INVOICE - CONFIDENTIAL - CENTRAL");
		sources.add("AP INVOICE - CONFIDENTIAL - NSB 3");
		sources.add("AP INVOICE - CONFIDENTIAL - NSB 4");
		sources.add("AP INVOICE - CONFIDENTIAL - NSB 5");
		sources.add("AP INVOICE - CONFIDENTIAL - NSB 8");
		sources.add("AP INVOICE - CONFIDENTIAL - NSB 8A");
		sources.add("AP INVOICE - IMPORTATION - CENTRAL");
		sources.add("AP INVOICE - IMPORTATION - NSB 3");
		sources.add("AP INVOICE - IMPORTATION - NSB 4");
		sources.add("AP INVOICE - IMPORTATION - NSB 5");
		sources.add("AP INVOICE - IMPORTATION - NSB 8");
		sources.add("AP INVOICE - IMPORTATION - NSB 8A");
		sources.add("AP INVOICE - NON PO - CENTRAL");
		sources.add("AP INVOICE - NON PO - NSB 3");
		sources.add("AP INVOICE - NON PO - NSB 4");
		sources.add("AP INVOICE - NON PO - NSB 5");
		sources.add("AP INVOICE - NON PO - NSB 8");
		sources.add("AP INVOICE - NON PO - NSB 8A");
		sources.add("AP LOAN - CENTRAL");
		sources.add("AP LOAN - NSB 3");
		sources.add("AP LOAN - NSB 4");
		sources.add("AP LOAN - NSB 5");
		sources.add("AP LOAN - NSB 8");
		sources.add("AP LOAN - NSB 8A");
		sources.add("AP INVOICE GOODS/SERVICES - CENTRAL");
		sources.add("AP INVOICE GOODS/SERVICES - NSB 3");
		sources.add("AP INVOICE GOODS/SERVICES - NSB 4");
		sources.add("AP INVOICE GOODS/SERVICES - NSB 5");
		sources.add("AP INVOICE GOODS/SERVICES - NSB 8");
		sources.add("AP INVOICE GOODS/SERVICES - NSB 8A");
		sources.add("AP PAYMENT - CENTRAL");
		sources.add("AP PAYMENT - NSB 3");
		sources.add("AP PAYMENT - NSB 4");
		sources.add("AP PAYMENT - NSB 5");
		sources.add("AP PAYMENT - NSB 8");
		sources.add("AP PAYMENT - NSB 8A");
		sources.add("AR TRANSACTION - CENTRAL");
		sources.add("AR TRANSACTION - NSB 3");
		sources.add("AR TRANSACTION - NSB 4");
		sources.add("AR TRANSACTION - NSB 5");
		sources.add("AR TRANSACTION - NSB 8");
		sources.add("AR TRANSACTION - NSB 8A");
		sources.add("AR INVOICE - CENTRAL");
		sources.add("AR INVOICE - NSB 3");
		sources.add("AR INVOICE - NSB 4");
		sources.add("AR INVOICE - NSB 5");
		sources.add("AR INVOICE - NSB 8");
		sources.add("AR INVOICE - NSB 8A");
		sources.add("ACCOUNT COLLECTION - CENTRAL");
		sources.add("ACCOUNT COLLECTION - NSB 3");
		sources.add("ACCOUNT COLLECTION - NSB 4");
		sources.add("ACCOUNT COLLECTION - NSB 5");
		sources.add("ACCOUNT COLLECTION - NSB 8");
		sources.add("ACCOUNT COLLECTION - NSB 8A");
		sources.add("CUSTOMER ADVANCE PAYMENT - CENTRAL");
		sources.add("CUSTOMER ADVANCE PAYMENT - NSB 3");
		sources.add("CUSTOMER ADVANCE PAYMENT - NSB 4");
		sources.add("CUSTOMER ADVANCE PAYMENT - NSB 5");
		sources.add("CUSTOMER ADVANCE PAYMENT - NSB 8");
		sources.add("CUSTOMER ADVANCE PAYMENT - NSB 8A");
		sources.add("DELIVERY RECEIPT - CENTRAL");
		sources.add("DELIVERY RECEIPT - NSB 3");
		sources.add("DELIVERY RECEIPT - NSB 4");
		sources.add("DELIVERY RECEIPT - NSB 5");
		sources.add("DELIVERY RECEIPT - NSB 8");
		sources.add("DELIVERY RECEIPT - NSB 8A");
		sources.add("GENERAL LEDGER - CENTRAL");
		sources.add("GENERAL LEDGER - NSB 3");
		sources.add("GENERAL LEDGER - NSB 4");
		sources.add("GENERAL LEDGER - NSB 5");
		sources.add("GENERAL LEDGER - NSB 8");
		sources.add("GENERAL LEDGER - NSB 8A");
		sources.add("LOAN PROCEEDS - CENTRAL");
		sources.add("LOAN PROCEEDS - NSB 3");
		sources.add("LOAN PROCEEDS - NSB 4");
		sources.add("LOAN PROCEEDS - NSB 5");
		sources.add("LOAN PROCEEDS - NSB 8");
		sources.add("LOAN PROCEEDS - NSB 8A");
		sources.add("OTHER RECEIPT - CENTRAL");
		sources.add("OTHER RECEIPT - NSB 3");
		sources.add("OTHER RECEIPT - NSB 4");
		sources.add("OTHER RECEIPT - NSB 5");
		sources.add("OTHER RECEIPT - NSB 8");
		sources.add("OTHER RECEIPT - NSB 8A");
		sources.add("PROJECT RETENTION - CENTRAL");
		sources.add("PROJECT RETENTION - NSB 3");
		sources.add("PROJECT RETENTION - NSB 4");
		sources.add("PROJECT RETENTION - NSB 5");
		sources.add("PROJECT RETENTION - NSB 8");
		sources.add("PROJECT RETENTION - NSB 8A");
		sources.add("RECEIVING REPORT - CENTRAL");
		sources.add("RECEIVING REPORT - NSB 3");
		sources.add("RECEIVING REPORT - NSB 4");
		sources.add("RECEIVING REPORT - NSB 5");
		sources.add("RECEIVING REPORT - NSB 8");
		sources.add("RECEIVING REPORT - NSB 8A");
		sources.add("RECLASS - CENTRAL");
		sources.add("RECLASS - NSB 3");
		sources.add("RECLASS - NSB 4");
		sources.add("RECLASS - NSB 5");
		sources.add("RECLASS - NSB 8");
		sources.add("RECLASS - NSB 8A");
		sources.add("RETURN TO SUPPLIER - CENTRAL");
		sources.add("RETURN TO SUPPLIER - NSB 3");
		sources.add("RETURN TO SUPPLIER - NSB 4");
		sources.add("RETURN TO SUPPLIER - NSB 5");
		sources.add("RETURN TO SUPPLIER - NSB 8");
		sources.add("RETURN TO SUPPLIER - NSB 8A");
		sources.add("STOCK ADJUSTMENT - IN - CENTRAL");
		sources.add("STOCK ADJUSTMENT - IN - NSB 3");
		sources.add("STOCK ADJUSTMENT - IN - NSB 4");
		sources.add("STOCK ADJUSTMENT - IN - NSB 5");
		sources.add("STOCK ADJUSTMENT - IN - NSB 8");
		sources.add("STOCK ADJUSTMENT - IN - NSB 8A");
		sources.add("STOCK ADJUSTMENT - OUT - CENTRAL");
		sources.add("STOCK ADJUSTMENT - OUT - NSB 3");
		sources.add("STOCK ADJUSTMENT - OUT - NSB 4");
		sources.add("STOCK ADJUSTMENT - OUT - NSB 5");
		sources.add("STOCK ADJUSTMENT - OUT - NSB 8");
		sources.add("STOCK ADJUSTMENT - OUT - NSB 8A");
		sources.add("SUPPLIER ADVANCE PAYMENT - CENTRAL");
		sources.add("SUPPLIER ADVANCE PAYMENT - NSB 3");
		sources.add("SUPPLIER ADVANCE PAYMENT - NSB 4");
		sources.add("SUPPLIER ADVANCE PAYMENT - NSB 5");
		sources.add("SUPPLIER ADVANCE PAYMENT - NSB 8");
		sources.add("SUPPLIER ADVANCE PAYMENT - NSB 8A");
		sources.add("PETTY CASH REPLENISHMENT - CENTRAL");
		sources.add("PETTY CASH REPLENISHMENT - NSB 3");
		sources.add("PETTY CASH REPLENISHMENT - NSB 4");
		sources.add("PETTY CASH REPLENISHMENT - NSB 5");
		sources.add("PETTY CASH REPLENISHMENT - NSB 8");
		sources.add("PETTY CASH REPLENISHMENT - NSB 8A");
		return sources;
	}

	@Override
	public List<PaymentStatus> getAllARLineAnalysisSources() {
		logger.info("Get all the sources for the AR Line Analysis.");
		List<PaymentStatus> sources = new ArrayList<PaymentStatus>();
		sources.add(PaymentStatus.getInstanceOf(ARLineAnalysis.OTHER_RECEIPT, ARLineAnalysis.SOURCE_OTHER_RECEIPT));
		sources.add(PaymentStatus.getInstanceOf(ARLineAnalysis.AR_TRANSACTION, ARLineAnalysis.SOURCE_AR_TRANSACTION));
		sources.add(PaymentStatus.getInstanceOf(ARLineAnalysis.AR_INVOICE, ARLineAnalysis.SOURCE_AR_INVOICE));
		logger.info("Generated "+sources.size()+" sources for AR Line Analysis Report.");
		return sources;
	}

	@Override
	public List<String> getAllArReceiptRegisterSources() {
		logger.info("Get all the sources for the AR Receipt Register.");
		List<String> sources = new ArrayList<String>();
		sources.add(ARReceiptRegister.SOURCE_ACCT_COLLECTION);
		sources.add(ARReceiptRegister.SOURCE_AR_MISC);
		logger.info("Generated "+sources.size()+" sources for AR Receipt Register.");
		return sources;
	}

	/**
	 * Get the Id of the source of the report. Sources are:
	 * <br> -1 = ALL
	 * <br>  1 = Account Collection
	 * <br>  2 = AR Miscellaneous
	 * <br>  3 = Cash Sales
	 * @param source The source.
	 * @return The id of the source.
	 */
	public int getArReceiptRegisterSourceId(String source) {
		logger.debug("Get the receipt register id of the source: "+source);
		if(source.equalsIgnoreCase("ALL")) {
			return ARReceiptRegister.ALL_OPTION;
		} else if(source.equalsIgnoreCase(ARReceiptRegister.SOURCE_ACCT_COLLECTION)) {
			return ARReceiptRegister.ACCT_COLLECTION;
		} else if(source.equalsIgnoreCase(ARReceiptRegister.SOURCE_AR_MISC)) {
			return ARReceiptRegister.AR_MISC;
		} else if(source.equalsIgnoreCase(ARReceiptRegister.SOURCE_CASH_SALES)) {
			return ARReceiptRegister.CASH_SALE;
		}
		return ARReceiptRegister.CUSTOMER_ADV_PAYMENT;
	}

}
