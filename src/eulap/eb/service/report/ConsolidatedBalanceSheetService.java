package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.service.AccountService;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.BalanceSheetDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;

/**
 * 

 */

@Service
public class ConsolidatedBalanceSheetService extends ISBSByDivisionService {
	@Autowired
	private CompanyDao companyDao; 
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountService accountService;
	protected static final int CONSOLIDATED_COMP_ID = Integer.MAX_VALUE;
	protected static final String CONSOLIDATED_COMP_NAME = "CONSOLIDATED";

	/**
	 * 
	 * @param strCompanyIds
	 * @param parseDate
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<BalanceSheetDto> getBalanceSheetData(String strCompanyIds, Date asOfDate) throws CloneNotSupportedException {
		List<BalanceSheetDto> balanceSheetDtos = new ArrayList<BalanceSheetDto>();
		Calendar startCal = Calendar.getInstance();
		startCal.set(1990, Calendar.JANUARY, 01);
		Date startDate = startCal.getTime();
		List<Integer> companyIds = StringFormatUtil.parseIds(strCompanyIds);
		List<ISBSAccountDto> accts =  new ArrayList<>();
		List<Company> companies = new ArrayList<Company>();
		for (Integer companyId : companyIds) {
			accts.addAll(super.getISBSAccounts(companyId, -1, -1, startDate, asOfDate, false));
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
		BalanceSheetDto bsDto = new BalanceSheetDto(true);
		bsDto.setCompanies(companies);
		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);
		// Current Assets
		ISBSTypeDto currentAsset = fsHelper.getAccountBalances("Current Assets", true, true, 100,
				AccountDto.MAIN_LEVEL, "Current Assets", 1, accts, companyIds, false);
		List<ISBSTypeDto> currentAssets = new ArrayList<ISBSTypeDto>();
		currentAssets.add(currentAsset);
		bsDto.setCurrentAssets(currentAssets);

		// Non-Current Asset
		ISBSTypeDto nonCurrentAsset = fsHelper.getAccountBalances("Non-current Assets", true, true, 90,
				AccountDto.MAIN_LEVEL, "Non-current Assets", 2, accts, companyIds, false);
		List<ISBSTypeDto> nonCurrentAssets = new ArrayList<ISBSTypeDto>();
		nonCurrentAssets.add(nonCurrentAsset);
		bsDto.setNonCurrentAssets(nonCurrentAssets);

		// Current Liabilities
		ISBSTypeDto currentLiability = fsHelper.getAccountBalances("Current Liabilities", true, true, 80,
				AccountDto.MAIN_LEVEL, "Current Liabilities", 3, accts, companyIds, false);
		List<ISBSTypeDto> currentLiabilities = new ArrayList<ISBSTypeDto>();
		currentLiabilities.add(currentLiability);
		bsDto.setCurrentLiabilities(currentLiabilities);

		// Non Current Liabilities
		ISBSTypeDto nonCurrentLiability = fsHelper.getAccountBalances("Non-Current Liabilities", true,
				true, 60, AccountDto.MAIN_LEVEL, "Non-Current Liabilities", 4, accts, companyIds, false);
		List<ISBSTypeDto> nonCurrentLiabilities = new ArrayList<ISBSTypeDto>();
		nonCurrentLiabilities.add(nonCurrentLiability);
		bsDto.setNonCurrentLiabilities(nonCurrentLiabilities);

		// Equity
		ISBSTypeDto equity = fsHelper.getAccountBalances("Equity", true, true, 50,
				AccountDto.MAIN_LEVEL, "Equity", 8, accts, companyIds, false);

		List<ISBSTypeDto> equities = new ArrayList<ISBSTypeDto>();
		equities.add(equity);
		bsDto.setEquities(equities);

		// Process Total Assets
		bsDto.setAssetsTotal(computeTotalsByCompType(new ArrayList<>(bsDto.getCurrentAssets()),
				new ArrayList<>(bsDto.getNonCurrentAssets()), true, companyIds));

		// Process Total Liabilities
		bsDto.setLiabilitiesTotal(computeTotalsByCompType(new ArrayList<>(bsDto.getCurrentLiabilities()),
				new ArrayList<>(bsDto.getNonCurrentLiabilities()), true, companyIds));

		// Process Total Equity
		bsDto.setEquityLiabilitiesTotal(computeTotalLiabilities(bsDto.getEquities(),
				bsDto.getLiabilitiesTotal(), companyIds));

		balanceSheetDtos.add(bsDto);
		return balanceSheetDtos;
	}

	private List<ISBSTotalDto> computeTotalLiabilities(List<ISBSTypeDto> equity, List<ISBSTotalDto> totalLiabilities, List<Integer> companyIds) {
		List<ISBSTotalDto> totalEquity = groupTotals(new ArrayList<>(equity));
		equity = null; // Free up memory. No longer needed.
		return computeCompanyTotals(totalEquity, totalLiabilities, true, companyIds);
	}
}