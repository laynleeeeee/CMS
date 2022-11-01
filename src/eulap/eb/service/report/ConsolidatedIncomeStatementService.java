package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.AccountService;
import eulap.eb.service.is.ISAccountClass;
import eulap.eb.service.is.IncomeStatement;
import eulap.eb.service.is.IncomeStatementConfParser;
import eulap.eb.service.is.SubStatement;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;
import eulap.eb.web.dto.IncomeStatementDto;

/**
 * Service class that will handle business logic for income statement


 */

@Service
public class ConsolidatedIncomeStatementService extends ISBSByDivisionService {
	@Autowired
	private CompanyDao companyDao; 
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountService accountService;
	private static final int DEFAULT_COMPANY_ID = 1;
	protected static final int CONSOLIDATED_COMP_ID = Integer.MAX_VALUE;
	protected static final String CONSOLIDATED_COMP_NAME = "CONSOLIDATED";

	/**
	 * Process and set the income statement report DTO
	 * @param companyId The company id
	 * @param accountLevel The account level
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The income statement report DTO
	 */
	public List<IncomeStatementDto> getIncomeStatementData(String strCompanyIds, Date dateFrom,
			Date dateTo) throws ConfigurationException, CloneNotSupportedException {
		List<IncomeStatementDto> incomeStatementDtos = new ArrayList<IncomeStatementDto>();
		List<Integer> companyIds = StringFormatUtil.parseIds(strCompanyIds);
		List<ISBSAccountDto> accts =  new ArrayList<>();

		List<Company> companies = new ArrayList<Company>();
		for (Integer companyId : companyIds) {
			accts.addAll(super.getISBSAccounts(companyId, -1, -1, dateFrom, dateTo, false));
			companies.add(companyDao.get(companyId));
		}
		Company company = new Company();
		companyIds.add(CONSOLIDATED_COMP_ID);
		company.setId(CONSOLIDATED_COMP_ID);
		company.setName(CONSOLIDATED_COMP_NAME);
		companies.add(company);
		Collections.sort(companyIds, new Comparator<Integer>() {

			@Override
			public int compare(Integer com1, Integer com2) {
				return com1.compareTo(com2);
			}
		});
		Collections.sort(companies, new Comparator<Company>() {

			@Override
			public int compare(Company o1, Company o2) {
				return Integer.valueOf(o1.getId()).compareTo(o2.getId());
			}
		});
		IncomeStatementDto statementMain = new IncomeStatementDto(true);

		statementMain.setCompanies(companies);
		IncomeStatement is = IncomeStatementConfParser.parseIncomeStatement(DEFAULT_COMPANY_ID);
		List<SubStatement> subs = is.getSubStatements();
		ISAccountClass acctClass = subs.get(0).getAccountClasses().get(0);
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);

		// Revenue
		// The code should based on the configuration file or SP.
		List<ISBSTypeDto> revs = new ArrayList<>();
		revs.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, AccountDto.MAIN_LEVEL,
				"Revenue", 5, accts, companyIds, false));
		statementMain.setRevenues(revs);

		// Direct Cost
		List<ISBSTypeDto> dcs = new ArrayList<>();
		dcs.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 90, AccountDto.MAIN_LEVEL, "Cost of Sales", 6, accts, companyIds, false));
		statementMain.setDirectCost(dcs);

		// Gross Profit
		statementMain.setGrossProfit(computeTotalsByCompType(new ArrayList<>(statementMain.getRevenues()),
				new ArrayList<>(statementMain.getDirectCost()), false, companyIds));

		// Operating Expense
		List<ISBSTypeDto> opex = new ArrayList<>();
		opex.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 80, AccountDto.MAIN_LEVEL, "Operating Expense", 7, accts, companyIds, false));
		statementMain.setOperatingExpense(opex);

		// Other Income
		List<ISBSTypeDto> otherIncome = new ArrayList<>();
		otherIncome.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 70, AccountDto.MAIN_LEVEL, "Other Income", 10, accts, companyIds, false));
		statementMain.setOtherIncome(otherIncome);

		// Process Operating Income
		statementMain.setOperatingIncome(computeOperatingIncome(statementMain.getGrossProfit(), statementMain.getOperatingExpense(), 
				statementMain.getOtherExpense(), companyIds));

		// Process Net Income
		statementMain.setNetIncome(computeNetIncome(statementMain.getOperatingIncome(), statementMain.getOtherIncome(),
				statementMain.getOtherExpense(), companyIds));
		incomeStatementDtos.add(statementMain);
		return incomeStatementDtos;
	}

	private List<ISBSTotalDto> computeOperatingIncome(List<ISBSTotalDto> grossProfit,
			List<ISBSTypeDto> opexpenses, List<ISBSTypeDto> otexpenses, List<Integer> companyIds) {
		List<ISBSTotalDto> totalExpenses = groupTotals(new ArrayList<>(opexpenses));
		opexpenses = null; // Free up memory. No longer needed.
		List<ISBSTotalDto> grossOperatingIncome = computeCompanyTotals(grossProfit, totalExpenses, false, companyIds);
		List<ISBSTotalDto> totalOtherExpenses = groupTotals(new ArrayList<>(otexpenses));
		return computeCompanyTotals(grossOperatingIncome, totalOtherExpenses, false, companyIds);
	}

	private List<ISBSTotalDto> computeNetIncome(List<ISBSTotalDto> operatingIncome,
			List<ISBSTypeDto> otherIncome, List<ISBSTypeDto> otherExpense, List<Integer> companyIds) {
		List<ISBSTotalDto> totalOtherIncome = groupTotals(new ArrayList<>(otherIncome));
		otherIncome = null; // Free up memory. No longer needed.
		return computeCompanyTotals(operatingIncome, totalOtherIncome, true, companyIds);
	}

}
