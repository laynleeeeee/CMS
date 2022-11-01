package eulap.eb.service;

import java.text.DateFormatSymbols;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.web.dto.ValueAddedTaxSummaryDto;

/**
 * A class that handles the business logic of
 * Quarterly Value-Added Tax Declaration

 */
@Service
public class QrtrlyValAddedTaxDeclrtnService {
	@Autowired
	private APInvoiceDao aPInvoiceDao;
	@Autowired
	private ArTransactionDao arTransactionDao;

	private static Logger LOGGER = Logger.getLogger(QrtrlyValAddedTaxDeclrtnService.class);

	/**
	 * Get the Petty Cash Voucher Register report.
	 * @param companyId The company ID
	 * @param divisionId The Division ID
	 * @param year The quarterly vat declaration year
	 * @param quarter The quarterly vat declaration quarter
	 * @param month The quarterly vat declaration month
	 * @return The petty cash voucher register report.
	 */
	public ValueAddedTaxSummaryDto getQrtlyVATSummaryDto(int companyId, int divisionId, int year, int quarter, int month) {
		ValueAddedTaxSummaryDto summaryDto = new ValueAddedTaxSummaryDto();
		// Since quarter is from array list its index is 0, add plus 1 to quarter
		int fnlQuarter = quarter + 1;
		LOGGER.info("Generating the Quarterly Value-Added Tax Declaration input summary.");
		summaryDto.setInputVATDeclarations(aPInvoiceDao.getQrtrlyValAddedTaxDeclrtnData(companyId, divisionId, year, fnlQuarter, month));
		LOGGER.info("Generating the Quarterly Value-Added Tax Declaration output summary.");
		summaryDto.setOutputVATDeclarations(arTransactionDao.getOutputVatDeclaration(companyId, divisionId, year, fnlQuarter, month));
		return summaryDto;
	}

	/**
	 * Get the name of the quarter given the quarter number.
	 * @param quarterNum The quarter number.
	 * @return The quarter name.
	 */
	public static String getQuarterFullName(int quarterNum) {
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		switch(quarterNum) {
		case 0:
			return months[0] + " - " + months[2];
		case 1:
			return months[3] + " - " + months[5];
		case 2:
			return months[6] + " - " + months[8];
		case 3:
			return months[9] + " - " + months[11];
		}
		throw new RuntimeException("Invalid quarter.");
	}

}
