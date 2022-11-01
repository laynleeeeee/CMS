package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.AccountService;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.BalanceSheetDto;
import eulap.eb.web.dto.DivisionDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;

/**
 * Service class that will handle business logic for Balance Sheet by Division report.


 *
 */
@Service
public class BalanceSheetByDivisionService extends ISBSByDivisionService{
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;

	public BalanceSheetDto generateBS (Integer companyId, Integer divisionId, Integer subDivisionId, int accountLevel, Date asOfDate, 
			User user) throws CloneNotSupportedException {
		List<DivisionDto> divisions = getDivisions(companyId, divisionId, subDivisionId, false, false);
		Calendar startCal = Calendar.getInstance();
		startCal.set(1990, Calendar.JANUARY, 01);
		List<ISBSAccountDto> accts = new ArrayList<ISBSAccountDto>();
		// All division
		boolean isAllDiv = divisionId == DivisionDto.DIV_ALL;
		if (isAllDiv) {
			accts =  getISBSAccounts(companyId, -1, divisionId, startCal.getTime(), asOfDate, false);
		} else if (subDivisionId != DivisionDto.DIV_ALL) {
			accts =  getISBSAccounts(companyId, -1, subDivisionId, startCal.getTime(), asOfDate, false);
		} else { // Selected division and selected "ALL"  sub division
			List<DivisionDto> subDivisions = divisionDao.getAllChildren(divisionId, true); //TODO: investigate this method.S
			if (!subDivisions.isEmpty()) {
				for (DivisionDto subDivision : subDivisions) {
					accts.addAll(getISBSAccounts(companyId, -1, subDivision.getId(), startCal.getTime(), asOfDate, false));
				}
			} else {
				accts =  getISBSAccounts(companyId, -1, divisionId, startCal.getTime(), asOfDate, false);
			}
		}

		BalanceSheetDto bsMain = new BalanceSheetDto(true);
		bsMain.setDivisions(divisions);

		FinancialStatementHelper fsHelper = new FinancialStatementHelper(accountDao, divisionDao, accountService);

		// Current Asset
		ISBSTypeDto currentAsset = fsHelper.getAccountBalances("Current Assets", true, 
				true, 100, accountLevel, "Current Assets", 1, accts, divisions, false, isAllDiv, false);
		List<ISBSTypeDto> currentAssets = new ArrayList<ISBSTypeDto>();
		currentAssets.add(currentAsset);
		bsMain.setCurrentAssets(currentAssets);

		// Non-Current Asset
//		ISBSTypeDto nonCurrentAsset = fsHelper.getAccountBalances("Non-current Assets", true,
//				true, 90, accountLevel, "Non-current Assets", 2, accts, divisions, false, isAllDiv, false);
		ISBSTypeDto propertPlantEq = fsHelper.getAccountBalances("Property, Plant and Equipment", true,
				true, 90, accountLevel, "Property, Plant and Equipment", 10, accts, divisions, false, isAllDiv, false);
		ISBSTypeDto otherAsset = fsHelper.getAccountBalances("Other Assets", true,
				true, 90, accountLevel, "Other Assets", 11, accts, divisions, false, isAllDiv, false);

		List<ISBSTypeDto> nonCurrentAssets = new ArrayList<ISBSTypeDto>();
//		nonCurrentAssets.add(nonCurrentAsset);
		nonCurrentAssets.add(propertPlantEq);
		nonCurrentAssets.add(otherAsset);
		bsMain.setNonCurrentAssets(nonCurrentAssets);

		// Current Liabilities
		ISBSTypeDto currentLiability = fsHelper.getAccountBalances("Current Liabilities", true, 
				true, 80, accountLevel, "Current Liabilities", 3, accts, divisions, false, isAllDiv, false);
		List<ISBSTypeDto> currentLiabilities = new ArrayList<ISBSTypeDto>();
		currentLiabilities.add(currentLiability);
		bsMain.setCurrentLiabilities(currentLiabilities);

		// Non Current Liabilities
//		ISBSTypeDto nonCurrentLiability = fsHelper.getAccountBalances("Non-Current Liabilities", true,
//				true, 60, accountLevel, "Non-Current Liabilities", 4, accts, divisions, false, isAllDiv, false);
		ISBSTypeDto longTermLiab = fsHelper.getAccountBalances("Long-term Liabilities", true,
				true, 60, accountLevel, "Long-term Liabilities", 12, accts, divisions, false, isAllDiv, false);
		ISBSTypeDto otherLiab = fsHelper.getAccountBalances("Other Liabilities", true,
				true, 60, accountLevel, "Other Liabilities", 13, accts, divisions, false, isAllDiv, false);

		List<ISBSTypeDto> nonCurrentLiabilities = new ArrayList<ISBSTypeDto>();
//		nonCurrentLiabilities.add(nonCurrentLiability);
		nonCurrentLiabilities.add(longTermLiab);
		nonCurrentLiabilities.add(otherLiab);
		bsMain.setNonCurrentLiabilities(nonCurrentLiabilities);

		// Equity
//		ISBSTypeDto equity = fsHelper.getAccountBalances("Equity", true,
//				true, 50, accountLevel, "Equity", 8, accts, divisions, false, isAllDiv, false);
		ISBSTypeDto nsbCapital = fsHelper.getAccountBalances("NSB, Capital",
				true, true, 50, accountLevel, "NSB, Capital", 15, accts, divisions, false, isAllDiv, false);
		ISBSTypeDto nsbDrawings = fsHelper.getAccountBalances("NSB, Drawings", true,
				true, 50, accountLevel, "NSB, Drawings", 14, accts, divisions, false, isAllDiv, false);

		List<ISBSTypeDto> equities = new ArrayList<ISBSTypeDto>();
//		equities.add(equity);
		equities.add(nsbDrawings);
		equities.add(nsbCapital);
		bsMain.setEquities(equities);

		// Process Total Assets
		bsMain.setAssetsTotal(computeTotalsByType(new ArrayList<>(bsMain.getCurrentAssets()), 
				new ArrayList<>(bsMain.getNonCurrentAssets()), false, true));

		// Process Total Liabilities
		bsMain.setLiabilitiesTotal(computeTotalsByType(new ArrayList<>(bsMain.getCurrentLiabilities()), 
				new ArrayList<>(bsMain.getNonCurrentLiabilities()), false, true));

		// Process Total Equity
		bsMain.setEquityLiabilitiesTotal(computeTotalLiabilities(bsMain.getEquities(), bsMain.getLiabilitiesTotal()));
		return bsMain;
	}


	private List<ISBSTotalDto> computeTotalLiabilities(List<ISBSTypeDto> equity, List<ISBSTotalDto> totalLiabilities) {
		List<ISBSTotalDto> totalEquity = groupTotals(new ArrayList<>(equity), false, false);
		equity = null; // Free up memory. No longer needed.
		return computeTotals(totalEquity, totalLiabilities, true);
	}

	@Override
	protected List<AccountBalancesDto> getAccountBalances (int companyId, int accountId, int divisionId, 
			 Date dateFrom, Date dateTo) {
		return new ArrayList<AccountBalancesDto>(accountDao.getAccountBalances(companyId, accountId, divisionId,
				null ,dateTo, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT)).getData());
	}
}