package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.service.AccountService;
import eulap.eb.service.is.ISAccountClass;
import eulap.eb.service.is.IncomeStatement;
import eulap.eb.service.is.IncomeStatementConfParser;
import eulap.eb.service.is.SubStatement;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.DivisionDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;
import eulap.eb.web.dto.ISByMonthDto;
import eulap.eb.web.dto.IncomeStatementByDivisionDto;
import eulap.eb.web.dto.IncomeStatementByMonthDto;
import eulap.eb.web.dto.IncomeStatementComparisonDataDto;
import eulap.eb.web.dto.IncomeStatementComparisonDto;
import eulap.eb.web.dto.IncomeStatementNSBDto;
import eulap.eb.web.dto.TimePeriodMonth;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Service class that will handle business logic for Income Statement by Division report.

 *
 */
@Service
public class IncomeStatementByDivService extends ISBSByDivisionService{
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;
	private static final int DIVISION_ID_CENTRAL = 1; 
	private static final int DIVISION_ID_NSB3 = 2;
	private static final int DIVISION_ID_NSB4 = 3;
	private static final int DIVISION_ID_NSB5 = 4;
	private static final int DIVISION_ID_NSB8 = 5;
	private static final int DIVISION_ID_NSB8A = 6;

	/**
	 * Get the NSB Income Statement Datasource.
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param dateFrom The date ranged
	 * @param dateTo 
	 * @param hasZeroBalance 
	 * @return The NSB Income Statemet Datasource
	 * @throws ConfigurationException
	 * @throws CloneNotSupportedException
	 */
	public JRDataSource getNSBIncomeStatementDatasource(Integer companyId, Integer divisionId, 
			Date dateFrom, Date dateTo, Integer hasZeroBalance) throws ConfigurationException, CloneNotSupportedException{
		Map<Integer,List<AccountDto>> parent2Children = new HashMap<>();
		IncomeStatementNSBDto incomeStatementNSBDto = new IncomeStatementNSBDto();
		boolean isAllDiv = divisionId.equals(DivisionDto.DIV_ALL);

		List<DivisionDto> divisions = getDivisions(companyId, divisionId, null, true, true);
		IncomeStatementByDivisionDto is = getIncomeStatement(companyId, divisionId, dateFrom, dateTo, divisions, incomeStatementNSBDto);
		List<IncomeStatementByDivisionDto> incomeStatementMains = new ArrayList<>();
		List<IncomeStatementNSBDto> statementNSBDtos = new ArrayList<>();
		incomeStatementMains.add(is);
		incomeStatementNSBDto.setConsolidatedByDivisionDtos(incomeStatementMains);

		// Get data for comparison report.
		List<IncomeStatementComparisonDto> comparisonDtos = getComparisonReportData(companyId, divisionId, dateFrom,
				dateTo, divisions, is);
		incomeStatementNSBDto.setComparisonDtos(comparisonDtos);

		List<ISBSAccountDto> accts =  new ArrayList<>();
		if (isAllDiv) {
			accts =  super.getISBSAccounts(companyId, -1, DivisionDto.DIV_ALL, dateFrom, dateTo, false, true, true);
		} else {
			for (DivisionDto d : divisions) {
				accts.addAll(super.getISBSAccounts(companyId, -1, d.getId(), dateFrom, dateTo, false, true, true));
			}
		}

		if(hasZeroBalance != -1) {
			accts.addAll(addZeroAccount(dateFrom, divisionId, isAllDiv));
		}

		// Product Cost
		List<TimePeriodMonth> months = getMonths(dateFrom, dateTo);
		IncomeStatementByMonthDto productCostByMonthDto = getISByMonthProductUnitCost(companyId, divisionId, dateFrom, dateTo,
				divisions, months, isAllDiv, hasZeroBalance, accts, parent2Children);
		List<IncomeStatementByMonthDto> productCostByMonths = new ArrayList<>();
		productCostByMonths.add(productCostByMonthDto);
		incomeStatementNSBDto.setProductCostByMonths(productCostByMonths);

		// Selling
		IncomeStatementByMonthDto sellingByMonthDto = getISByMonth(companyId, divisionId, dateFrom, dateTo,
				divisions, months, isAllDiv, hasZeroBalance, accts, "Selling Epenses", 21, parent2Children);
		List<IncomeStatementByMonthDto> sellingByMonths = new ArrayList<>();
		sellingByMonths.add(sellingByMonthDto);
		incomeStatementNSBDto.setSellingByMonths(sellingByMonths);


		// General and Administrator
		IncomeStatementByMonthDto genAndAdminByMonthDto = getISByMonth(companyId, divisionId, dateFrom, dateTo,
				divisions, months, isAllDiv, hasZeroBalance, accts, "General and Administrative", 22, parent2Children);
		List<IncomeStatementByMonthDto> genAndAdminByMonths = new ArrayList<>();
		genAndAdminByMonths.add(genAndAdminByMonthDto);
		incomeStatementNSBDto.setGenAndAdminByMonths(genAndAdminByMonths);

		incomeStatementNSBDto.setMonthCount(months.size());
		incomeStatementNSBDto.setDivisionCount(divisions.size());
		statementNSBDtos.add(incomeStatementNSBDto);
		return new JRBeanCollectionDataSource(statementNSBDtos);
	}

	private List<ISBSAccountDto> addZeroAccount(Date dateFrom, Integer divisionId, boolean isAllDiv) {
		List<Account> accounts = accountDao.getLastLevelAccounts();
		List<ISBSAccountDto> accts = new ArrayList<ISBSAccountDto>();
		ISBSAccountDto accountDto = null;
		int month = DateUtil.getMonth(dateFrom)+1;
		for (Account account : accounts) {
			accountDto = new ISBSAccountDto();
			accountDto.setAccountId(account.getId());
			accountDto.setAccountName(account.getAccountName());
			accountDto.setAccountTypeId(account.getAccountTypeId());
			accountDto.setParentAccountId(account.getNumber());
			accountDto.setAmount(0.00);
			accountDto.setDivisionId(!isAllDiv ? divisionId : 1);
			accountDto.setMonth(month);
			accountDto.setMonthName(DateUtil.convertToStringMonth(month));
			accountDto.setpAcctId(account.getParentAccountId());
			accountDto.setAcctNo(account.getNumber());
			accts.add(accountDto);
		}
		return accts;
	}

	private List<IncomeStatementComparisonDto> getComparisonReportData(Integer companyId, Integer divisionId,
			Date dateFrom, Date dateTo, List<DivisionDto> divisions, IncomeStatementByDivisionDto currIS) throws ConfigurationException, CloneNotSupportedException {
		IncomeStatementComparisonDto comparisonDto = new IncomeStatementComparisonDto();
		List<IncomeStatementComparisonDto> comparisonDtos = new ArrayList<IncomeStatementComparisonDto>();
		IncomeStatementNSBDto prevIncomeStatementNSBDto = new IncomeStatementNSBDto();
		IncomeStatementByDivisionDto prevIS =
				getIncomeStatement(companyId, divisionId, DateUtil.deductYearsToDate(dateFrom, 1),
						DateUtil.deductYearsToDate(dateTo, 1), divisions, prevIncomeStatementNSBDto);

		// Get total sales
		comparisonDto.setCurrentYSales(currIS.getSalesGrandTotal());
		comparisonDto.setPrevYSales(prevIS.getSalesGrandTotal());

		comparisonDto.setProductionCosts(getComparisonData("Production Costs", currIS.getProductionCostsTotal(), prevIS.getProductionCostsTotal()));
		comparisonDto.setGrossIncome(getComparisonData("GROSS INCOME", currIS.getGrossIncome(), prevIS.getGrossIncome()));
		comparisonDto.setSellingExpenses(getComparisonData("Selling expenses", currIS.getSellingExpensesTotal(), prevIS.getSellingExpensesTotal()));
		comparisonDto.setGenAndAdminExpenses(getComparisonData("General and Administrative Expenses", currIS.getGenAndAdminExpensesTotal(), prevIS.getGenAndAdminExpensesTotal()));
		comparisonDto.setOperatingExpenses(getComparisonData("TOTAL OPERATING EXPENSES", currIS.getOperatingExpenses(), prevIS.getOperatingExpenses()));
		comparisonDto.setOtheIncome(getComparisonData("OTHER INCOME (EXPENSES)", currIS.getOtherIncomeExpensesTotal(), prevIS.getOtherIncomeExpensesTotal()));
		comparisonDto.setNetIncomeBefore(getComparisonData("NET INCOME (LOSS) before interest expense & depreciation", currIS.getNetIncomeBefore(), prevIS.getNetIncomeBefore()));
		comparisonDto.setInterestExpenses(getComparisonData("Interest Expense", currIS.getInterestExpensesTotal(), prevIS.getInterestExpensesTotal()));
		comparisonDto.setDepreciation(getComparisonData("Depreciation", currIS.getDepreciationsTotal(), prevIS.getDepreciationsTotal()));
		comparisonDto.setNetIncomeAfter(getComparisonData("NET INCOME (LOSS) after interest expense & depreciation", currIS.getNetIncomeAfter(), prevIS.getNetIncomeAfter()));
		comparisonDtos.add(comparisonDto);
		return comparisonDtos;
	}

	private List<IncomeStatementComparisonDataDto> getComparisonData(String particulars, List<ISBSTotalDto> current, List<ISBSTotalDto> prev) {
		List<IncomeStatementComparisonDataDto> comparisonDataDtos = new ArrayList<>();
		IncomeStatementComparisonDataDto comparisonDataDto = new IncomeStatementComparisonDataDto();
		comparisonDataDto.setParticulars(particulars);
		comparisonDataDto.setCurrentYAmount(getISBSTotal(current));
		comparisonDataDto.setPrevYAmount(getISBSTotal(prev));
		comparisonDataDtos.add(comparisonDataDto);
		return comparisonDataDtos;
	}

	private double getISBSTotal(List<ISBSTotalDto> totalDtos) {
		double amount = 0;
		for (ISBSTotalDto isbsTotalDto : totalDtos) {
			if(isbsTotalDto.getDivisionId().equals(IncomeStatementNSBDto.TOTAL_DIV_ID)) {
				amount = isbsTotalDto.getAmount();
			}
		}
		return amount;
	}

	/**
	 * Process and set the income statement report DTO
	 * @param companyId The company id
	 * @param accountLevel The account level
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param incomeStatementNSBDto 
	 * @return The income statement report DTO
	 */
	public IncomeStatementByDivisionDto getIncomeStatement(Integer companyId, Integer divisionId, 
			Date dateFrom, Date dateTo, List<DivisionDto> divisions, IncomeStatementNSBDto incomeStatementNSBDto) throws ConfigurationException, CloneNotSupportedException {
		List<ISBSAccountDto> accts =  new ArrayList<>();
		boolean isAllDiv = divisionId.equals(DivisionDto.DIV_ALL);
		if (isAllDiv) {
			accts =  super.getISBSAccounts(companyId, -1, DivisionDto.DIV_ALL, dateFrom, dateTo, false, true);
		} else {
			for (DivisionDto d : divisions) {
				accts.addAll(super.getISBSAccounts(companyId, -1, d.getId(), dateFrom, dateTo, false, true));
			}
		}

		IncomeStatementByDivisionDto statementByDivisionDto = new IncomeStatementByDivisionDto(true);

		statementByDivisionDto.setDivisions(divisions);
		IncomeStatement is = IncomeStatementConfParser.parseIncomeStatement(companyId);
		List<SubStatement> subs = is.getSubStatements();
		ISAccountClass acctClass = subs.get(0).getAccountClasses().get(0);
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);

		// Sales
		//List<ISBSAccountDto> revenues = fsHelper
		// The code should based on the configuration file or SP.
		List<ISBSTypeDto> sales = new ArrayList<>();
		sales.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, 2, "SALES", 16, accts, divisions, false, isAllDiv, true));
		statementByDivisionDto.setSales(sales);
		statementByDivisionDto.setSaleTotal(computeTotalsByType(new ArrayList<>(sales),
				new ArrayList<>(), false, false));
		setTotalSales(statementByDivisionDto, incomeStatementNSBDto);

		// Production Cost
		List<ISBSTypeDto> directMaterials = new ArrayList<>();
		directMaterials.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 90, 2, "Production Cost", 17, accts, divisions, false, isAllDiv, true));// Direct Materials
		List<ISBSTypeDto> directLabors = new ArrayList<>();
		directLabors.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 90, 2, "Production Cost", 18, accts, divisions, false, isAllDiv, true));// Direct Labors
		statementByDivisionDto.setProductionCostsTotalDirect(computeTotalsByType(new ArrayList<>(directMaterials),
				new ArrayList<>(directLabors), false, true));

		List<ISBSTypeDto> mfgOrver = new ArrayList<>();
		mfgOrver.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 90, 2, "Production Cost", 19, accts, divisions, false, isAllDiv, true));// Mfg Overhead
		List<ISBSTypeDto> mfgOverRM = new ArrayList<>();
		mfgOverRM.add(fsHelper.getAccountBalances (acctClass.getName(), false, true , 90, 2, "Production Cost", 20, accts, divisions, false, isAllDiv, true));// Direct Materials

		// Compute Total Production costs
		statementByDivisionDto.setProductionCostsTotal(computeTotalsByType(new ArrayList<>(mfgOverRM),
				new ArrayList<>(mfgOrver), false, true));
		statementByDivisionDto.setProductionCostsTotal(computeTotals(statementByDivisionDto.getProductionCostsTotal(),
					statementByDivisionDto.getProductionCostsTotalDirect(), true));

		// Compute Total Gross
		statementByDivisionDto.setGrossIncome(computeTotals(statementByDivisionDto.getSaleTotal(),
				statementByDivisionDto.getProductionCostsTotal(), false));

		List<ISBSTypeDto> selling = new ArrayList<>();
		selling.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 100, 2, "Selling Expenses", 21, accts, divisions, false, isAllDiv, true)); // Personnel Costs - Selling

		// Compute Total selling
		statementByDivisionDto.setSellingExpensesTotal(computeTotalsByType(new ArrayList<>(selling),new ArrayList<>(), false, true));

		List<ISBSTypeDto> genAndAdmin = new ArrayList<>();
		genAndAdmin.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 100, 2, "General And Admin", 22, accts, divisions, false, isAllDiv, true));

		// Compute General and Admin
		statementByDivisionDto.setGenAndAdminExpensesTotal(computeTotalsByType(new ArrayList<>(genAndAdmin),new ArrayList<>(), false, true));

		// Compute operating expense
		statementByDivisionDto.setOperatingExpenses(computeTotals(statementByDivisionDto.getGenAndAdminExpensesTotal(),
				statementByDivisionDto.getSellingExpensesTotal(), true));

		List<ISBSTypeDto> otherIncomeExpenses = new ArrayList<>();
		otherIncomeExpenses.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 100, 2, "Other Income Expenses", 26, accts, divisions, false, isAllDiv, true));
		statementByDivisionDto.setOtherIncomeExpenses(otherIncomeExpenses);
		List<ISBSTypeDto> otherIncome = new ArrayList<>();
		otherIncome.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 100, 2, "Other Income", 25, accts, divisions, false, isAllDiv, true));
		statementByDivisionDto.setOtherIncome(otherIncome);
		statementByDivisionDto.setOtherIncomeExpensesTotal(computeTotalsByType(new ArrayList<>(otherIncome),
				new ArrayList<>(otherIncomeExpenses), false, false));

		// net income before interest and depreciation added 
		statementByDivisionDto.setNetIncomeBefore(computeTotals(statementByDivisionDto.getGrossIncome(), statementByDivisionDto.getOperatingExpenses(), false));
		statementByDivisionDto.setNetIncomeBefore(computeTotals(statementByDivisionDto.getNetIncomeBefore(), statementByDivisionDto.getOtherIncomeExpensesTotal(), true));

		List<ISBSTypeDto> interestExpenses = new ArrayList<>();
		interestExpenses.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 100, 2, "Interest Expense", 24, accts, divisions, false, isAllDiv, true));
		statementByDivisionDto.setInterestExpenses(interestExpenses);
		statementByDivisionDto.setInterestExpensesTotal(computeTotalsByType(new ArrayList<>(interestExpenses),
				new ArrayList<>(), false, false));

		List<ISBSTypeDto> depreciations = new ArrayList<>();
		depreciations.add(fsHelper.getAccountBalances(acctClass.getName(), false, true , 100, 2, "Depreciation", 23, accts, divisions, false, isAllDiv, true));
		statementByDivisionDto.setDepreciations(depreciations);
		statementByDivisionDto.setDepreciationsTotal(computeTotalsByType(new ArrayList<>(depreciations),
				new ArrayList<>(), false, false));

		// Net income after depreciation and interest expense.
		statementByDivisionDto.setNetIncomeAfter(computeTotals(statementByDivisionDto.getNetIncomeBefore(), statementByDivisionDto.getInterestExpensesTotal(), false));
		statementByDivisionDto.setNetIncomeAfter(computeTotals(statementByDivisionDto.getNetIncomeAfter(), statementByDivisionDto.getDepreciationsTotal(), false));

		return statementByDivisionDto;
	}

	private void setTotalSales(IncomeStatementByDivisionDto statementMain, IncomeStatementNSBDto incomeStatementNSBDto) {
		List<ISBSTotalDto> totalSales = statementMain.getSaleTotal();
		for (ISBSTotalDto isbsTotalDto : totalSales) {
			switch (isbsTotalDto.getDivisionId()) {
			case DIVISION_ID_CENTRAL:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesTotalCentral(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesTotalCentral(isbsTotalDto.getAmount());
				}
				break;
			case DIVISION_ID_NSB3:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesTotalNSB3(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesTotalNSB3(isbsTotalDto.getAmount());
				}
				break;
			case DIVISION_ID_NSB4:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesTotalNSB4(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesTotalNSB4(isbsTotalDto.getAmount());
				}
				break;
			case DIVISION_ID_NSB5:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesTotalNSB5(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesTotalNSB5(isbsTotalDto.getAmount());
				}
				break;
			case DIVISION_ID_NSB8:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesTotalNSB8(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesTotalNSB8(isbsTotalDto.getAmount());
				}
				break;
			case DIVISION_ID_NSB8A:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesTotalNSB8A(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesTotalNSB8A(isbsTotalDto.getAmount());
				}
				break;
			case IncomeStatementNSBDto.TOTAL_DIV_ID:
				if(incomeStatementNSBDto != null) {
					statementMain.setSalesGrandTotal(isbsTotalDto.getAmount());
					incomeStatementNSBDto.setSalesGrandTotal(isbsTotalDto.getAmount());
				}
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected List<AccountBalancesDto> getAccountBalances(int companyId, int accountId, int divisionId, Date dateFrom,
			Date dateTo) {
		return super.getAccountBalances(companyId, accountId, divisionId, dateFrom, dateTo);
	}

	/**
	 * Process and set the income statement report DTO
	 * @param companyId The company id
	 * @param accountLevel The account level
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param hasZeroBalance 
	 * @return The income statement report DTO
	 */
	public IncomeStatementByMonthDto getISByMonthProductUnitCost(Integer companyId, Integer divisionId, 
			Date dateFrom, Date dateTo, List<DivisionDto> divisions, List<TimePeriodMonth> months,
			boolean isAllDiv, Integer hasZeroBalance, List<ISBSAccountDto> accts, Map<Integer,List<AccountDto>> parent2Children) throws ConfigurationException, CloneNotSupportedException {
		IncomeStatementByMonthDto byMonthDto = new IncomeStatementByMonthDto();
		byMonthDto.setMonths(months);
		byMonthDto.setReportType(IncomeStatementByMonthDto.PRODUCTION_REPORT_TYPE_ID);

		IncomeStatement is = IncomeStatementConfParser.parseIncomeStatement(companyId);
		List<SubStatement> subs = is.getSubStatements();
		ISAccountClass acctClass = subs.get(0).getAccountClasses().get(0);
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);

		List<ISBSTypeDto> directMaterials = new ArrayList<>();
		directMaterials.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, 2, "Direct Materials", 17, accts, divisions, false, isAllDiv, true, months, true));
		byMonthDto.setDirectMaterials(removedParent(parent2Children, directMaterials));

		List<ISBSTypeDto> directLabors = new ArrayList<>();
		directLabors.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, 2, "Direct Labor", 18, accts, divisions, false, isAllDiv, true, months, true));
		byMonthDto.setDirectLabors(removedParent(parent2Children, directLabors));

		List<ISBSTypeDto> mfgOverhead = new ArrayList<>();
		mfgOverhead.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, 2, "Mfg Overhead - R&M", 20, accts, divisions, false, isAllDiv, true, months, true));
		byMonthDto.setMfgOverhead(removedParent(parent2Children, mfgOverhead));

		List<ISBSTypeDto> manufacturingOverhead = new ArrayList<>();
		manufacturingOverhead.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, 2, "Manufacturing Overhead", 19, accts, divisions, false, isAllDiv, true, months, true));
		byMonthDto.setManufacturingOverhead(markParent(parent2Children, manufacturingOverhead));
		List<ISBSTypeDto> payrollCosts = new ArrayList<>();
		payrollCosts.addAll(directMaterials);
		payrollCosts.addAll(directLabors);
		payrollCosts.addAll(mfgOverhead);
		payrollCosts.addAll(manufacturingOverhead);

		filterPayrollRelatedCost(payrollCosts, byMonthDto, months, isAllDiv, divisions, fsHelper);

		// Compute Production Cost
		byMonthDto.setProductionCostTotal(computeTotalsByType(new ArrayList<>(directMaterials),
				new ArrayList<>(directLabors), false, true, true));
		byMonthDto.setProductionCostTotal(computeTotals(byMonthDto.getProductionCostTotal(),
				computeTotalsByType(new ArrayList<>(manufacturingOverhead),new ArrayList<>(mfgOverhead), false, true, true), true, true));
		return byMonthDto;
	}

	/**
	 * Filter payroll related accounts for production cost.
	 */
	private void filterPayrollRelatedCost(List<ISBSTypeDto> isbsTypeDtos, IncomeStatementByMonthDto byMonthDto,
			List<TimePeriodMonth> months, boolean isAllDiv,  List<DivisionDto> divisions, FinancialStatementHelper fsHelper) {
		List<ISBSTypeDto> ot = new ArrayList<>();
		List<ISBSAccountDto> payrollRelatedCostAccts = new ArrayList<ISBSAccountDto>();
		List<ISBSAccountDto> overtimeAccounts = new ArrayList<ISBSAccountDto>();
		// Filter Payroll related.
		for (ISBSTypeDto isbsTypeDto : isbsTypeDtos) {
			for (ISBSAccountDto accountDto : isbsTypeDto.getAccounts()) {
				if(accountDto.getAccountName().toUpperCase().contains("INDIRECT LABOR")
						|| accountDto.getAccountName().toUpperCase().contains("DIRECT LABOR")
						|| accountDto.getAccountName().toUpperCase().contains("SSS ER")
						|| accountDto.getAccountName().toUpperCase().contains("PHIC ER")
						|| accountDto.getAccountName().toUpperCase().contains("HDMF ER")
						|| accountDto.getAccountName().toUpperCase().contains("DRIVERS SALARIES")) {
					payrollRelatedCostAccts.add(accountDto);
					if(accountDto.getAccountName().toUpperCase().contains("OT") || accountDto.getAccountName().toUpperCase().contains("OVERTIME")) {
						// FIlter OT
						overtimeAccounts.add(accountDto);
					}
				}
			}
		}

		List<ISBSTotalDto> totalPayrollDto = initializedPayroll(months);

		if(overtimeAccounts != null && !overtimeAccounts.isEmpty()) {
			// And compute OT.
			ISBSTypeDto otISBSTypeDto = new ISBSTypeDto();
			otISBSTypeDto.setLabel("OT");
			otISBSTypeDto.setName("OT");
			otISBSTypeDto.setSequenceOrder(1);
			otISBSTypeDto.setTotals(fsHelper.getISTotal(overtimeAccounts, divisions,
					months, true, isAllDiv));
			ot.add(otISBSTypeDto);
		}
		// Compute Payroll related accounts.
		byMonthDto.setPayrollRelatedCost(computeTotals(totalPayrollDto, fsHelper.getISTotal(payrollRelatedCostAccts, divisions,
				months, true, isAllDiv), true, true));
		// OT
		byMonthDto.setPayrollRelatedCostOT(computeTotals(totalPayrollDto, computeTotalsByType(new ArrayList<>(ot),
				new ArrayList<>(), false, true, true), true, true));
		// Payroll
		byMonthDto.setPayroll(computeTotals(byMonthDto.getPayrollRelatedCost(),
				byMonthDto.getPayrollRelatedCostOT(), false, true));
	}

	private List<ISBSTypeDto> removedParent(Map<Integer,List<AccountDto>> parent2Children, List<ISBSTypeDto> isbsTypeDtos) {
		List<ISBSTypeDto> isBsNoParent = new ArrayList<ISBSTypeDto>();
		for (ISBSTypeDto isbsTypeDto : isbsTypeDtos) {
			List<ISBSAccountDto> accountDtos = new ArrayList<ISBSAccountDto>();
			for (ISBSAccountDto isbsAccountDto : isbsTypeDto.getAccounts()) {
				if(isbsAccountDto.getpAcctId() == null || isbsAccountDto.getpAcctId() == 0) {
					List<AccountDto> children = parent2Children.get(isbsAccountDto.getAccountId());
					if (children == null) {
						children = accountDao.getAllChildren(isbsAccountDto.getAccountId());
						parent2Children.put(isbsAccountDto.getAccountId(), children);
					}
					if(children != null && !children.isEmpty()) {
						continue;
					}
				}
				accountDtos.add(isbsAccountDto);
			}
			isbsTypeDto.setAccounts(accountDtos);
			isBsNoParent.add(isbsTypeDto);
		}
		return isbsTypeDtos;
	}


	private List<ISBSTypeDto> markParent(Map<Integer,List<AccountDto>> parent2Children, List<ISBSTypeDto> isbsTypeDtos) {
		List<ISBSTypeDto> isBsNoParent = new ArrayList<ISBSTypeDto>();
		for (ISBSTypeDto isbsTypeDto : isbsTypeDtos) {
			List<ISBSAccountDto> accountDtos = new ArrayList<ISBSAccountDto>();
			for (ISBSAccountDto isbsAccountDto : isbsTypeDto.getAccounts()) {
				if (isbsAccountDto.getpAcctId() == null || isbsAccountDto.getpAcctId() == 0) {
					int accountId = isbsAccountDto.getAccountId();
					List<AccountDto> children = parent2Children.get(isbsAccountDto.getAccountId());
					if (children == null) {
						children = accountDao.getAllChildren(isbsAccountDto.getAccountId());
						parent2Children.put(isbsAccountDto.getAccountId(), children);
					}
					if ((children != null && !children.isEmpty()) || accountDao.isLastLevel(accountId)) {
						isbsAccountDto.setParent(true);
					}
				} else {
					Account parent = accountDao.get(isbsAccountDto.getpAcctId());
					isbsAccountDto.setAcctNo(parent.getNumber() + isbsAccountDto.getAcctNo());
				}
				accountDtos.add(isbsAccountDto);
			}
			isbsTypeDto.setAccounts(accountDtos);
			isBsNoParent.add(isbsTypeDto);
		}
		return isbsTypeDtos;
	}

	private void perParentAccount(ISByMonthDto isByMonthDto, List<DivisionDto> divisions, boolean isAllDiv, List<TimePeriodMonth> months, IncomeStatementByMonthDto byMonthDto, Map<Integer,List<AccountDto>> parent2Children) {
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);
		List<ISBSTypeDto> isbsTypeDtos = isByMonthDto.getTypeDtos();
		Map<Integer, ISBSTypeDto> isPerTypeHM = new HashMap<Integer, ISBSTypeDto>();
		Map<Integer, ISBSTypeDto> personnelHM = new HashMap<Integer, ISBSTypeDto>();
		ISBSTypeDto typeDto = null;
		for (ISBSTypeDto isbsTypeDto : isbsTypeDtos) {
			Integer key = 0;
			for (ISBSAccountDto isbsAccountDto : isbsTypeDto.getAccounts()) {
				key = isbsAccountDto.getpAcctId();
				if(key == null || key == 0) {
					key = isbsAccountDto.getAccountId();
					List<AccountDto> children = parent2Children.get(key);
					if (children == null) {
						children = accountDao.getAllChildren(isbsAccountDto.getAccountId());
						parent2Children.put(key, children);
					}
					if(children != null && !children.isEmpty()) {
						continue;
					}
				}
				typeDto = isPerTypeHM.get(key);
				List<ISBSAccountDto> accountDtos = new ArrayList<ISBSAccountDto>();
				if(typeDto != null) {
					accountDtos = typeDto.getAccounts();
					if(accountDtos == null) {
						accountDtos = new ArrayList<ISBSAccountDto>();
					}
					accountDtos.add(isbsAccountDto);
				} else {
					typeDto = new ISBSTypeDto();
					accountDtos.add(isbsAccountDto);
				}
				Account parentAccount = accountDao.get(key);
				typeDto.setSequenceOrder(isbsTypeDto.getSequenceOrder());
				typeDto.setLabel(isbsAccountDto.getParentAccountId());
				typeDto.setName(parentAccount != null ? parentAccount.getAccountName() :  isbsAccountDto.getAccountName());
				typeDto.setAccounts(accountDtos);
				if(key == 264 || key == 189) { // Personnel Cost
					personnelHM.put(key, typeDto);
				}
				isPerTypeHM.put(key, typeDto);
			}
		}
		List<ISBSTypeDto> typeDtos = new ArrayList<ISBSTypeDto>();
		for (ISBSTypeDto isbsTypeDto : isPerTypeHM.values()) {
			if(isbsTypeDto.getAccounts() == null){
				continue;
			}
			isbsTypeDto.setTotals(fsHelper.getISTotal(isbsTypeDto.getAccounts(), divisions,
					months, true, isAllDiv));
			typeDtos.add(isbsTypeDto);
		}
		Collections.sort(typeDtos, new Comparator<ISBSTypeDto>() {

			@Override
			public int compare(ISBSTypeDto o1, ISBSTypeDto o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		isByMonthDto.setTypeDtos(typeDtos);

		typeDtos = new ArrayList<ISBSTypeDto>();
		List<ISBSTypeDto> ot = new ArrayList<>();
		List<ISBSAccountDto> accountDtos = new ArrayList<ISBSAccountDto>();
		for (ISBSTypeDto isbsTypeDto : personnelHM.values()) {
			typeDtos.add(isbsTypeDto);
			for (ISBSAccountDto accountDto : isbsTypeDto.getAccounts()) {
				if(accountDto.getAccountName().contains("OT") || accountDto.getAccountName().toUpperCase().contains("OVERTIME")) {
					if(accountDtos == null) {
						accountDtos = new ArrayList<ISBSAccountDto>();
					}
					accountDtos.add(accountDto);
				}
			}
			
		}

		if(accountDtos != null && !accountDtos.isEmpty()) {
			ISBSTypeDto otISBSTypeDto = null;
			otISBSTypeDto = new ISBSTypeDto();
			otISBSTypeDto.setLabel("OT");
			otISBSTypeDto.setName("OT");
			otISBSTypeDto.setSequenceOrder(1);
			otISBSTypeDto.setTotals(fsHelper.getISTotal(accountDtos, divisions,
					months, true, isAllDiv));
			ot.add(otISBSTypeDto);
		}
		List<ISBSTotalDto> totalPayrollDto = initializedPayroll(months);
		// Compute Payroll related accounts.
		byMonthDto.setPayrollRelatedCost(computeTotals(totalPayrollDto, computeTotalsByType(new ArrayList<>(typeDtos),
				new ArrayList<>(), false, true, true), true, true));
		// OT
		byMonthDto.setPayrollRelatedCostOT(computeTotals(totalPayrollDto, computeTotalsByType(new ArrayList<>(ot),
				new ArrayList<>(), false, true, true), true, true));
		// Payroll
		byMonthDto.setPayroll(computeTotals(byMonthDto.getPayrollRelatedCost(),
				byMonthDto.getPayrollRelatedCostOT(), false, true));
		typeDtos = null; // Free memory
		ot = null; // Free memory
	}

	private List<ISBSTotalDto> initializedPayroll(List<TimePeriodMonth> months) {
		List<ISBSTotalDto> totalDtos = new ArrayList<ISBSTotalDto>();
		ISBSTotalDto totalDto = null;
		for (TimePeriodMonth month : months) {
			totalDto = new ISBSTotalDto();
			totalDto.setAmount(0.0);
			totalDto.setDivisionId(1);
			totalDto.setBudgetAmount(0.00);
			totalDto.setMonth(month.getMonth());
			totalDtos.add(totalDto);
		}
		return totalDtos;
	}

	/**
	 * Process and set the income statement report DTO for General & Admin Account type.
	 * @param companyId The company id
	 * @param accountLevel The account level
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @param hasZeroBalance 
	 * @param accts 
	 * @param accountTypeId 
	 * @return The income statement report DTO
	 */
	public IncomeStatementByMonthDto getISByMonth(Integer companyId, Integer divisionId, 
			Date dateFrom, Date dateTo, List<DivisionDto> divisions, List<TimePeriodMonth> months,
			boolean isAllDiv, Integer hasZeroBalance, List<ISBSAccountDto> accts, String name, int accountTypeId, Map<Integer,List<AccountDto>> parent2Children) throws ConfigurationException, CloneNotSupportedException {
		IncomeStatementByMonthDto byMonthDto = new IncomeStatementByMonthDto();
		byMonthDto.setMonths(months);

		IncomeStatement is = IncomeStatementConfParser.parseIncomeStatement(companyId);
		List<SubStatement> subs = is.getSubStatements();
		ISAccountClass acctClass = subs.get(0).getAccountClasses().get(0);
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);

		List<ISBSTypeDto> isbsTypeDtos = new ArrayList<>();
		List<ISByMonthDto> isByMonthDtos = new ArrayList<>();
		ISByMonthDto isByMonthDto = new ISByMonthDto();
		isbsTypeDtos.add(fsHelper.getAccountBalances (acctClass.getName(), true, true , 100, 2, name, accountTypeId, accts, divisions, false, isAllDiv, true, months, true));
		isByMonthDto.setTypeDtos(isbsTypeDtos);

		// Compute Total
		perParentAccount(isByMonthDto, divisions, isAllDiv, months, byMonthDto, parent2Children);
		isByMonthDtos.add(isByMonthDto);
		byMonthDto.setByMonthDtos(isByMonthDtos);
		// Grand Total
		byMonthDto.setIsbsTotalDtos(computeTotalsByType(new ArrayList<>(isbsTypeDtos),
				new ArrayList<>(), false, true, true));
		return byMonthDto;
	}

	private List<TimePeriodMonth> getMonths(Date dateFrom, Date dateTo){
		int startMonth = DateUtil.getMonth(dateFrom)+1;
		int endMonth = DateUtil.getMonth(dateTo)+1;
		List<TimePeriodMonth> periodMonths = TimePeriodMonth.getMonths();
		List<TimePeriodMonth> months = new ArrayList<>(); 
		for (TimePeriodMonth timePeriodMonth : periodMonths) {
			if(timePeriodMonth.getMonth() >= startMonth && timePeriodMonth.getMonth() <= endMonth) {
				months.add(timePeriodMonth);
			}
		}
		months.add(new TimePeriodMonth(IncomeStatementNSBDto.TOTAL_DIV_ID, IncomeStatementNSBDto.TOTAL_DIV_NAME));
		months.add(new TimePeriodMonth(IncomeStatementNSBDto.PERCENT_TO_SALES_ID, IncomeStatementNSBDto.PERCENT_TO_SALES));
		return months;
	}
}