package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.AccountDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;
import eulap.eb.service.is.ISAccountClass;
import eulap.eb.service.is.IncomeStatement;
import eulap.eb.service.is.IncomeStatementConfParser;
import eulap.eb.service.is.SubStatement;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.DivisionDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;
import eulap.eb.web.dto.IncomeStatementDto;

/**
 * Service class that will handle business logic for Income Statement by Division report.


 *
 */
@Service
public class IncomeStatementByDivOptService extends ISBSByDivisionService{
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;

	/**
	 * Generate the income statement report.
	 * @param companyId The company filter.
	 * @param divisionId The division filter.
	 * @param subDivision The child division.
	 * @param accountLevel MAIN LEVEL, LEVEL 2, LEVEL 3, or LEVEL 4
	 * @param dateFrom The start of date range.
	 * @param dateTo The end of date range.
	 * @return Data for income statement report.
	 * @throws ConfigurationException
	 * @throws CloneNotSupportedException 
	 */
	public IncomeStatementDto genIncomStatement(Integer companyId, Integer divisionId, Integer subDivision, int accountLevel, Date dateFrom, Date dateTo, 
			boolean isActualVsBudget, User user, int isType) throws ConfigurationException, CloneNotSupportedException {
		return getIncomeStatement(companyId, accountLevel, divisionId, subDivision, dateFrom, dateTo, isActualVsBudget);
	}


	/**
	 * Process and set the income statement report DTO
	 * @param companyId The company id
	 * @param accountLevel The account level
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The income statement report DTO
	 */
	public IncomeStatementDto getIncomeStatement(Integer companyId, int accountLevel, 
			Integer divisionId, Integer subDivision,
			Date dateFrom, Date dateTo, boolean isActualVsBudget) throws ConfigurationException, CloneNotSupportedException {
		List<DivisionDto> divisions = getDivisions(companyId, divisionId, subDivision, false, true);
		List<ISBSAccountDto> accts =  new ArrayList<>();
		boolean isAllDiv = divisionId.equals(DivisionDto.DIV_ALL);
		if (isAllDiv) {
			accts =  super.getISBSAccounts(companyId, -1, DivisionDto.DIV_ALL, dateFrom, dateTo, isActualVsBudget, true);
		} else {
			for (DivisionDto d : divisions) {
				accts.addAll(super.getISBSAccounts(companyId, -1, d.getId(), dateFrom, dateTo, isActualVsBudget, true));
			}
		}

		// TODO: Get the account type id in the configuration.
		IncomeStatementDto statementMain = new IncomeStatementDto(true);

		statementMain.setDivisions(divisions);
		IncomeStatement is = IncomeStatementConfParser.parseIncomeStatement(companyId);
		List<SubStatement> subs = is.getSubStatements();
		ISAccountClass acctClass = subs.get(0).getAccountClasses().get(0);
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);

		// Revenue
		//List<ISBSAccountDto> revenues = fsHelper
		// TODO : Easy fixed
		// The code should based on the configuration file or SP.
		List<ISBSTypeDto> revs = new ArrayList<>();
		revs.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, accountLevel, "Revenue", 5, accts, divisions, isActualVsBudget, isAllDiv, false));
		statementMain.setRevenues(revs);

		// Direct Cost
		//List<ISBSAccountDto> directCosts = getAccountsByTypeId(accts, 6);
		List<ISBSTypeDto> dcs = new ArrayList<>();
		dcs.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 90, accountLevel, "Direct Cost", 6, accts, divisions, isActualVsBudget, isAllDiv, false));
		statementMain.setDirectCost(dcs);

		// Gross Profit
		statementMain.setGrossProfit(computeTotalsByType(new ArrayList<>(statementMain.getRevenues()),
				new ArrayList<>(statementMain.getDirectCost()), false, false));

		// Operating Expense
		//List<ISBSAccountDto> operatingExpenses = getAccountsByTypeId(accts, 7);
		List<ISBSTypeDto> opex = new ArrayList<>();
		opex.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 80, accountLevel, "Operating Expense", 7, accts, divisions, isActualVsBudget, isAllDiv, false));
		statementMain.setOperatingExpense(opex);

		// Other Income
		//List<ISBSAccountDto> otherIncomes = getAccountsByTypeId(accts, 10);
		List<ISBSTypeDto> otherIncome = new ArrayList<>();
		otherIncome.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 70, accountLevel, "Other Income", 10, accts, divisions, isActualVsBudget, isAllDiv, false));
		statementMain.setOtherIncome(otherIncome);

		// Other Expense
		//List<ISBSAccountDto> otherExpenses = getAccountsByTypeId(accts, 11);
		List<ISBSTypeDto> otherExpense = new ArrayList<>();
		otherExpense.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 60, accountLevel, "Other Expense", 11, accts, divisions, isActualVsBudget, isAllDiv, false));
		statementMain.setOtherExpense(otherExpense);

		// Process Operating Income
		statementMain.setOperatingIncome(computeOperatingIncome(statementMain.getGrossProfit(), statementMain.getOperatingExpense(), 
				statementMain.getOtherExpense(),  false));

		// Process Net Income
		statementMain.setNetIncome(computeNetIncome(statementMain.getOperatingIncome(), statementMain.getOtherIncome(),
				statementMain.getOtherExpense(), false));
		return statementMain;
	}

	private List<ISBSTotalDto> computeOperatingIncome(List<ISBSTotalDto> grossProfit, List<ISBSTypeDto> opexpenses, List<ISBSTypeDto> otexpenses, boolean isActualVsBudget) {
		List<ISBSTotalDto> totalExpenses = groupTotals(new ArrayList<>(opexpenses), isActualVsBudget, false);
		opexpenses = null; // Free up memory. No longer needed.
		List<ISBSTotalDto> grossOperatingIncome = computeTotals(grossProfit, totalExpenses, false);
		List<ISBSTotalDto> totalOtherExpenses = groupTotals(new ArrayList<>(otexpenses), isActualVsBudget, false);
		return computeTotals(grossOperatingIncome, totalOtherExpenses, false);
	}

	private List<ISBSTotalDto> computeNetIncome(List<ISBSTotalDto> operatingIncome, List<ISBSTypeDto> otherIncome, List<ISBSTypeDto> otherExpense, boolean isActualVsBudget) {
		List<ISBSTotalDto> totalOtherIncome = groupTotals(new ArrayList<>(otherIncome), isActualVsBudget, false);
		return  computeTotals(operatingIncome, totalOtherIncome, true);
	}

	@Override
	protected List<AccountBalancesDto> getAccountBalances(int companyId, int accountId, int divisionId, Date dateFrom,
			Date dateTo) {
		return super.getAccountBalances(companyId, accountId, divisionId, dateFrom, dateTo);
	}
}