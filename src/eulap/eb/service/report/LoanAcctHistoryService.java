package eulap.eb.service.report;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.eb.dao.LoanProceedsDao;
import eulap.eb.web.dto.LoanAcctHistoryDto;

/**
 * Service class that will handle business logic for loan account history report generation

 */

@Service
public class LoanAcctHistoryService {
	@Autowired
	private LoanProceedsDao loanProceedsDao;

	/**
	 * Get the loan account history report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier account id
	 * @param dateFrom The date from
	 * @param dateTo The date to
	 * @return The loan account history report data
	 */
	public List<LoanAcctHistoryDto> getLoanAcctHistoryData(int companyId, int divisionId, int supplierId,
			int supplierAcctId, Date dateFrom, Date dateTo) {
		return loanProceedsDao.getLoanAcctHistoryData(companyId, divisionId, supplierId, supplierAcctId, dateFrom, dateTo);
	}

	/**
	 * Get the loan account history total amount values on date
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param supplierId The supplier id
	 * @param supplierAcctId The supplier id
	 * @return The loan account history total amount values on date
	 */
	public LoanAcctHistoryDto getTotalAmountValues(int companyId, int divisionId,
			int supplierId, int supplierAcctId) {
		Date beginningDate = DateUtil.parseDate("01/01/1900");
		List<LoanAcctHistoryDto> loanAcctHistoryDtos = getLoanAcctHistoryData(companyId,
				divisionId, supplierId, supplierAcctId, beginningDate, new Date());
		double totalLoanAmt = 0;
		double totalInterestAmt = 0;
		double totalPrincipalPaid = 0;
		for (LoanAcctHistoryDto dto : loanAcctHistoryDtos) {
			totalLoanAmt += dto.getLoanAmount();
			totalInterestAmt += dto.getInterest();
			totalPrincipalPaid += dto.getPrincipal();
		}
		LoanAcctHistoryDto loanAcctHistoryDto = new LoanAcctHistoryDto();
		loanAcctHistoryDto.setLoanAmount(totalLoanAmt);
		loanAcctHistoryDto.setInterest(totalInterestAmt);
		loanAcctHistoryDto.setPrincipal(totalPrincipalPaid);
		double outstandingBalance = totalLoanAmt - totalPrincipalPaid;
		loanAcctHistoryDto.setBalance(outstandingBalance);
		return loanAcctHistoryDto;
	}

	/**
	 * Get the loan balances summary report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param balanceOption The balance option
	 * @param asOfDate The as of date
	 * @return The loan balances summary report data
	 */
	public List<LoanAcctHistoryDto> getLoanBalancesSummaryData(int companyId, int divisionId, int balanceOption, Date asOfDate) {
		return loanProceedsDao.getLoanBalancesSummaryData(companyId, divisionId, balanceOption, asOfDate);
	}
}
