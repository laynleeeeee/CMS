package eulap.eb.service.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.web.dto.ValueAddedTaxSummaryDto;

/**
 * Business logic for generating report for the Monthly Value Added Tax Declaration

 */

@Service
public class MonthlyValueAddedTaxDecService {
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private APInvoiceDao aPInvoiceDao;

	/**
	 * Get the report data for Monthly Value Added Tax Declaration
	 * @return The Monthly Value Added Tax Declaration report data
	 */
	public ValueAddedTaxSummaryDto getMonthlyValueAddedTax(Integer companyId, Integer divisionId, Integer year, Integer quarter,Integer month){
		ValueAddedTaxSummaryDto summaryDto = new ValueAddedTaxSummaryDto();
		summaryDto.setOutputVATDeclarations(arTransactionDao.getOutputVatDeclaration(companyId,
				divisionId, year, quarter, month));
		summaryDto.setInputVATDeclarations(aPInvoiceDao.getQrtrlyValAddedTaxDeclrtnData(companyId,
				divisionId, year, quarter, month));
		return summaryDto;
	}
}