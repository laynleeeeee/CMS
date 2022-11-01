package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.comparators.ComparatorChain;

import eulap.common.util.DateUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.service.AccountService;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.DivisionDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;
import eulap.eb.web.dto.IncomeStatementNSBDto;
import eulap.eb.web.dto.TimePeriodMonth;

/**
 * Helper class for Income statement and Balance Sheet reports.


 *
 */
public class FinancialStatementHelper {
	private AccountDao accountDao;
	private DivisionDao divisionDao;
	private Map<Integer, Division> divisionsCached;
	private Map<Integer, Division> parentDivisions;
	private Map<Integer, Account> accountsCached;
	private Map<Integer, Integer> parentChildLevel;
	private AccountService accountService;
	protected FinancialStatementHelper (AccountDao accountDao, DivisionDao divisionDao, AccountService accountService) {
		this.accountDao = accountDao;
		this.divisionDao = divisionDao;
		this.accountService = accountService;
		divisionsCached = new HashMap<>();
		parentDivisions = new HashMap<>();
		accountsCached = new HashMap<>();
		parentChildLevel = new HashMap<>();
	}

	protected ISBSTypeDto getAccountBalances (String label, boolean isAtPositive, boolean asOfBalance, int sequenceOrder,
			int accountLevel, String name, int accountTypeId, List<ISBSAccountDto> allAccounts, List<DivisionDto> divisions, 
			boolean isActualVsBudget, boolean isAllDiv, boolean isAddSalesPercent) throws CloneNotSupportedException {
		return getAccountBalances(label, isAtPositive, asOfBalance, sequenceOrder, accountLevel, name, accountTypeId,
				allAccounts, divisions, isActualVsBudget, isAllDiv, isAddSalesPercent, null, false, false);
	}

	protected ISBSTypeDto getAccountBalances (String label, boolean isAtPositive, boolean asOfBalance, int sequenceOrder,
			int accountLevel, String name, int accountTypeId, List<ISBSAccountDto> allAccounts, List<DivisionDto> divisions, 
			boolean isActualVsBudget, boolean isAllDiv, boolean isAddSalesPercent, List<TimePeriodMonth> months,
			boolean isByMonth) throws CloneNotSupportedException {
		return getAccountBalances(label, isAtPositive, asOfBalance, sequenceOrder, accountLevel, name, accountTypeId,
				allAccounts, divisions, isActualVsBudget, isAllDiv, isAddSalesPercent, months, isByMonth, false);
	}

	protected ISBSTypeDto getAccountBalances (String label, boolean isAtPositive, boolean asOfBalance, int sequenceOrder,
			int accountLevel, String name, int accountTypeId, List<ISBSAccountDto> allAccounts, List<DivisionDto> divisions, 
			boolean isActualVsBudget, boolean isAllDiv, boolean isAddSalesPercent, List<TimePeriodMonth> months,
			boolean isByMonth, boolean isOT) throws CloneNotSupportedException {
		List<ISBSAccountDto> filteredAcctsByDivision = isAllDiv ? new ArrayList<>() : 
			getAccountsByTypeId(allAccounts, accountTypeId, isAtPositive);
		ISBSTypeDto isbsTypeDto = new ISBSTypeDto();
		isbsTypeDto.setLabel(label);
		isbsTypeDto.setAtPositive(isAtPositive);
		isbsTypeDto.setAsOfBalance(asOfBalance);
		isbsTypeDto.setSequenceOrder(sequenceOrder);
		isbsTypeDto.setName(name);
		if (isAllDiv) {
			List<ISBSAccountDto> acctsByTypeId = getAccountsByTypeId(allAccounts, accountTypeId, isAtPositive);
			for (ISBSAccountDto acct : acctsByTypeId) {
				int divisionId = acct.getDivisionId();
				Division division = divisionsCached.get(divisionId);
				if (division == null) {
					division = divisionDao.get(divisionId);
					divisionsCached.put(divisionId, division);
				}

				// if division is sub-division, change it with the parent division. 
				if (!isByMonth && division.getParentDivisionId() != null) {
					int parentId = division.getParentDivisionId();
					Division parentDivision = parentDivisions.get(parentId);
					if (parentDivision == null) {
						parentDivision = divisionDao.get(parentId);
						parentDivisions.put(parentId, parentDivision);
					}
					acct.setDivisionId(parentDivision.getId());
					acct.setDivisionName(parentDivision.getName());
				}
				if(isByMonth) {
					acct.setMonthName(DateUtil.convertToStringMonth(acct.getMonth()));
				}
			}
			Map<Integer, Map<Integer, ISBSAccountDto>> hmAcctsByAcctAndDiv = new HashMap<>();
			for (ISBSAccountDto acct : acctsByTypeId) {
				int key = isByMonth ? acct.getMonth() : acct.getDivisionId();
				if (hmAcctsByAcctAndDiv.containsKey(key)) {
					Map<Integer, ISBSAccountDto> isPerAcctId = hmAcctsByAcctAndDiv.get(key);
					if (isPerAcctId.containsKey(acct.getAccountId())) {
						ISBSAccountDto dto = isPerAcctId.get(acct.getAccountId());
						dto.setAmount(dto.getAmount() + acct.getAmount());
						isPerAcctId.put(acct.getAccountId(), dto);
					} else {
						isPerAcctId.put(acct.getAccountId(), acct);
					}
				} else {
					Map<Integer, ISBSAccountDto> isPerAcctId = new HashMap<>();
					isPerAcctId.put(acct.getAccountId(), acct);
					hmAcctsByAcctAndDiv.put(key, isPerAcctId);
				}
			}
			acctsByTypeId = null; // No longer needed.
			for (Map.Entry<Integer, Map<Integer, ISBSAccountDto>> entry : hmAcctsByAcctAndDiv.entrySet()) {
				filteredAcctsByDivision.addAll(new ArrayList<>(entry.getValue().values()));
			}
			hmAcctsByAcctAndDiv = null; // No longer needed.
		}

		isbsTypeDto.setTotals(getISTotal(filteredAcctsByDivision, divisions, months, isByMonth, isAllDiv));
		filteredAcctsByDivision.addAll(createAndGroupByParent(filteredAcctsByDivision, null, false, isByMonth));

		// create a map that will link the account and division. this will be
		// used to identify the division that has no account. 
		Map<Integer, List<Integer>> account2Divisions = new HashMap<>();
		// We will use this as reference in creating zero amount account. 
		Map<Integer, List<ISBSAccountDto>> accountId2acctDto = new HashMap<>();
		for (ISBSAccountDto acct : filteredAcctsByDivision) {
			List<Integer> Ids = account2Divisions.get(acct.getAccountId());
			if (Ids == null) {
				Ids = new ArrayList<Integer>();
				Ids.add(isByMonth ? acct.getMonth() : acct.getDivisionId());
				account2Divisions.put(acct.getAccountId(), Ids);
			} else {
				Ids.add(isByMonth ? acct.getMonth() : acct.getDivisionId());
			}

			List<ISBSAccountDto> isbsAccounts = accountId2acctDto.get(acct.getAccountId());
			if (isbsAccounts == null) {
				isbsAccounts = new ArrayList<ISBSAccountDto>();
				isbsAccounts.add(acct);
				accountId2acctDto.put(acct.getAccountId(), isbsAccounts);
			} else {
				isbsAccounts.add(acct);
			}
		}

		// Create a zero account needed for jasper report format.
		List<ISBSAccountDto> zeroAccountDtos = new ArrayList<ISBSAccountDto>();
		for (Map.Entry<Integer, List<Integer>> entry : account2Divisions.entrySet()) {
			Integer accountId = entry.getKey();
			List<Integer> ids = entry.getValue();
			ISBSAccountDto accountDto = accountId2acctDto.get(accountId).iterator().next();
			if(isByMonth) {
				for (TimePeriodMonth month : months) {
					if (!ids.contains(month.getMonth())) {
						ISBSAccountDto zeroAccountDto = (ISBSAccountDto) accountDto.clone();
						zeroAccountDto.setAmount(0.0);
						zeroAccountDto.setMonth(month.getMonth());
						zeroAccountDto.setMonthName(month.getName());
						zeroAccountDto.setBudgetAmount(0.0);
						zeroAccountDtos.add(zeroAccountDto);
					}
				}
			} else {
				for (DivisionDto divisionDto : divisions) {
					if (!ids.contains(divisionDto.getId())) {
						ISBSAccountDto zeroAccountDto = (ISBSAccountDto) accountDto.clone();
						zeroAccountDto.setAmount(0.0);
						zeroAccountDto.setDivisionId(divisionDto.getId());
						zeroAccountDto.setDivisionName(divisionDto.getName());
						zeroAccountDto.setBudgetAmount(0.0);
						zeroAccountDtos.add(zeroAccountDto);
					}
				}
			}
		}

		filteredAcctsByDivision.addAll(zeroAccountDtos);
		ComparatorChain comparatorChain = new ComparatorChain();
		comparatorChain.addComparator(new Comparator<ISBSAccountDto>() {
			@Override
			public int compare(ISBSAccountDto o1, ISBSAccountDto o2) {
				return o1.getAcctNo().compareTo(o2.getAcctNo());
			}
		});
		comparatorChain.addComparator(new Comparator<ISBSAccountDto>() {
			@Override
			public int compare(ISBSAccountDto o1, ISBSAccountDto o2) {
				return isByMonth ? o1.getMonth().compareTo(o2.getMonth()) : o1.getDivisionId().compareTo(o2.getDivisionId());
			}
		});
		Collections.sort(filteredAcctsByDivision, comparatorChain);
		isbsTypeDto.setAccounts(filterAccountLevel(accountLevel, filteredAcctsByDivision, isByMonth));
		if (!isActualVsBudget) {
			processConsolidated(filteredAcctsByDivision, isbsTypeDto.getTotals(), false, isbsTypeDto, isAddSalesPercent, isByMonth, isAllDiv);
		}
		return isbsTypeDto;
	}

	private List<ISBSAccountDto> filterAccountLevel (int accountLevel, List<ISBSAccountDto> accts, boolean isByMonth) throws CloneNotSupportedException {
		List<ISBSAccountDto> filterResult = new ArrayList<>();
		for (ISBSAccountDto acct : accts) {
			if (acct.getLevel() != null && acct.getLevel() <= accountLevel) {
				if(!isByMonth) {
					processIndention(acct, accountLevel);
				}
				filterResult.add(acct);
			}
		}
		Collections.sort(filterResult, new Comparator<ISBSAccountDto>() {
			@Override
			public int compare(ISBSAccountDto dto1, ISBSAccountDto dto2) {
				return dto1.getAcctNo().compareTo(dto2.getAcctNo());
			}
		});
		return filterResult;
	}

	private void processIndention(ISBSAccountDto a, int selectedLevel) {
		if (a.getParentAccountId() == null) {
			a.setParentAccountId(a.getAccountId().toString());
		}
		a.setParentAccountId(processPAcctId(a.getpAcctId(), a.getParentAccountId()));
		String indention = "";
		int accountLevel = a.getLevel();
		for (int i=1; i<accountLevel; i++) {
			indention += ISBSByDivisionService.TAB_SPACE;
		}
		a.setAccountName(indention + a.getAccountName());
		if (selectedLevel != AccountDto.LEVEL_4) {
			String strAccts[] = a.getParentAccountId().split("\\.");
			if (accountLevel == AccountDto.MAIN_LEVEL) {
				a.setParentAccountId(strAccts[0]);
			} else if (selectedLevel == AccountDto.LEVEL_2 && strAccts.length >= 2) {
				a.setParentAccountId(strAccts[0] + "." + strAccts[1]);
			}  else if (selectedLevel == AccountDto.LEVEL_3 && strAccts.length >= 3) {
				a.setParentAccountId(strAccts[0] + "." + strAccts[1] + "." + strAccts[2]);
			} else if (selectedLevel == AccountDto.LEVEL_4 && strAccts.length == 4) {
				a.setParentAccountId(strAccts[0] + "." + strAccts[1] + "." + strAccts[2] + "." + strAccts[3]);
			} 
		}
	}

	private void processConsolidated(List<ISBSAccountDto> accts, List<ISBSTotalDto> totals, boolean isPerComp, ISBSTypeDto isbsTypeDto, boolean isAddSalesPercent,
			boolean isByMonth) throws CloneNotSupportedException {
		processConsolidated(accts, totals, isPerComp, isbsTypeDto, isAddSalesPercent, isByMonth, true);
	}

	private void processConsolidated(List<ISBSAccountDto> accts, List<ISBSTotalDto> totals, boolean isPerComp, ISBSTypeDto isbsTypeDto, boolean isAddSalesPercent,
			boolean isByMonth, boolean isAllDiv) throws CloneNotSupportedException {
		List<ISBSAccountDto> cons = new ArrayList<ISBSAccountDto>();

		Map<Integer, ISBSAccountDto> hmUniqueAccts = new HashMap<>();
		for (ISBSAccountDto a : accts) {
			ISBSAccountDto uniqueAcct = hmUniqueAccts.get(a.getAccountId());
			if (uniqueAcct != null) {
				uniqueAcct.setAmount(uniqueAcct.getAmount() + a.getAmount());
			} else {
				uniqueAcct = (ISBSAccountDto) a.clone();
				hmUniqueAccts.put(a.getAccountId(), uniqueAcct);
			}
		}

		ISBSAccountDto consoAcct = null;
		Integer id = null;
		for (ISBSAccountDto con : accts) {
			id = isByMonth ? con.getMonth() : (isPerComp ? con.getCompanyId() : con.getDivisionId());
			if ((id).equals(IncomeStatementNSBDto.TOTAL_DIV_ID) || (id).equals(IncomeStatementNSBDto.PERCENT_TO_SALES_ID)) {
				// Setting total amount for balance sheet
				con.setAmount(hmUniqueAccts.get(con.getAccountId()).getAmount());
				cons.add(con);
				if (isAddSalesPercent) {
					consoAcct = (ISBSAccountDto) con.clone();
					consoAcct.setAmount(hmUniqueAccts.get(con.getAccountId()).getAmount());
					consoAcct.setCompanyId(IncomeStatementNSBDto.PERCENT_TO_SALES_ID);
					consoAcct.setDivisionId(IncomeStatementNSBDto.PERCENT_TO_SALES_ID);
					consoAcct.setMonth(IncomeStatementNSBDto.PERCENT_TO_SALES_ID);
					cons.add(consoAcct);
				}
			}
			consoAcct = null;
		}
		id = null;
		double totalCon = 0;
		for (ISBSTotalDto t : totals) {
			Integer pId = isPerComp ? t.getCompanyId() : (isByMonth ? t.getMonth() : t.getDivisionId());
			if (!pId.equals(IncomeStatementNSBDto.TOTAL_DIV_ID) && !pId.equals(IncomeStatementNSBDto.PERCENT_TO_SALES_ID)) {
				totalCon += t.getAmount();
			} else {
				t.setAmount(totalCon);
			}
		}

		if(isPerComp) {
			isbsTypeDto.getAccounts().addAll(cons);
		}
	}

	private static class ParentChild {
		private Integer parentId;
		private Integer childId;
		private ParentChild (Integer parentId,  Integer childId) {
			this.parentId = parentId;
			this.childId = childId;
		}
		@Override
		public int hashCode() {
			if (parentId == null)
				return childId.hashCode();
			if (childId == null)
				return parentId.hashCode();
			return parentId.hashCode() + childId.hashCode();
		}
	}

	/*
	 * Create and group the account based on hierarchy (parent to child relationship). 
	 */
	private List<ISBSAccountDto> createAndGroupByParent (List<ISBSAccountDto> accts, Map<ParentAndDivision,
			ISBSAccountDto> parentId2Acct, boolean perCompany, boolean isByMonth) throws CloneNotSupportedException {
		if (parentId2Acct == null) {
			parentId2Acct = new HashMap<>();
		}
		Map<ParentAndDivision, ISBSAccountDto> newParentId2Acct = new HashMap<> ();
		List<ISBSAccountDto> ret = new ArrayList<ISBSAccountDto>();
		for (ISBSAccountDto acct : accts) {
			Integer parentAccountId = acct.getpAcctId();
			//ParentChild pc = new ParentChild(acct.getpAcctId(), acct.getAccountId());
			Integer level = parentChildLevel.get(acct.getAccountId());
			if (level == null ) {
				level = accountService.getAccountLevelByParent(acct.getpAcctId(), acct.getAccountId(), false);
				parentChildLevel.put(acct.getAccountId(), level);
			}
			acct.setLevel(level);
			if (parentAccountId != null) { // this is a sub-account. 
				ParentAndDivision pd = new ParentAndDivision(parentAccountId, acct.getDivisionId(), acct.getCompanyId(), perCompany, acct.getMonth());
				ISBSAccountDto parentAcct = parentId2Acct.get(pd); // Existing parent
				if (parentAcct == null) {
					parentAcct = newParentId2Acct.get(pd);
				}

				if (parentAcct == null) {
					parentAcct = (ISBSAccountDto) acct.clone();
					Account parentAccount = accountsCached.get(parentAccountId);
					if (parentAccount == null) {
						parentAccount = accountDao.get(parentAccountId);
						accountsCached.put(parentAccountId, parentAccount);
					}
					parentAcct.setAccountName(parentAccount.getAccountName());
					parentAcct.setAccountId(parentAccount.getId());
					parentAcct.setpAcctId(parentAccount.getParentAccountId());
					parentAcct.setDivisionId(acct.getDivisionId());
					parentAcct.setCompanyId(acct.getCompanyId());
					parentAcct.setMonth(acct.getMonth());
					parentAcct.setMonthName(acct.getMonthName());
					parentAcct.setParentAccountId(parentAccount.getNumber());
					parentAcct.setLevel(acct.getLevel() - 1);
					parentAcct.setAcctNo(parentAccount.getNumber());
					newParentId2Acct.put(pd, parentAcct);
				} else {
					double runningTotal = parentAcct.getAmount() + acct.getAmount();
					parentAcct.setAmount(runningTotal);
				}
			}
		}

		List<ISBSAccountDto> parentAccts = new ArrayList<ISBSAccountDto>(newParentId2Acct.values());
		ret.addAll(parentAccts);
		parentId2Acct.putAll(newParentId2Acct);
		if (!parentAccts.isEmpty()) {
			ret.addAll(createAndGroupByParent(parentAccts, parentId2Acct, perCompany, isByMonth));
		}
		return ret;
	}

	private static class ParentAndDivision {
		private Integer parentId;
		private Integer divisionId;
		private Integer companyId;
		private Integer month;
		private boolean perCompany;

		private ParentAndDivision(Integer parentId, Integer divisionId, Integer companyId, boolean perCompany, Integer month) {
			this.parentId = parentId;
			this.divisionId = divisionId;
			this.companyId = companyId;
			this.perCompany = perCompany;
			this.month = month;
		}

		@Override
		public boolean equals(Object obj) {
			ParentAndDivision other = (ParentAndDivision) obj;
			return (month == null ? (perCompany ? other.companyId.equals(this.companyId) : other.divisionId.equals(this.divisionId))
					: other.month.equals(this.month)) && other.parentId.equals(this.parentId);
		}

		@Override
		public int hashCode() {
			return 714 * parentId.hashCode() * (month == null ? (perCompany ? companyId.hashCode() : divisionId.hashCode()) : month.hashCode());
		}

		@Override
		public String toString() {
			return "parentId : " + parentId + ", divisionId: " + divisionId+ ", companyId: " + companyId;
		}
	}

	protected List<ISBSTotalDto> getISTotal (List<ISBSAccountDto> accts, List<DivisionDto> divisions,
			List<TimePeriodMonth> months, boolean isByMonth, boolean isAllDiv) {
		List<ISBSTotalDto> isbsTotalDtos = new ArrayList<ISBSTotalDto>();
		if(isByMonth) {
			for (TimePeriodMonth month : months) {
				double totalPerMonth = 0;
				for (ISBSAccountDto acct : accts) {
					if (acct.getMonth().equals(month.getMonth())) {
						totalPerMonth += acct.getAmount();
					}
				}
				isbsTotalDtos.add(ISBSTotalDto.getInstanceOf(totalPerMonth, 0.0, month.getMonth(), month.getMonth()));
			}
		} else {
			for (DivisionDto division : divisions) {
				double totalPerDivision = 0;
				for (ISBSAccountDto acct : accts) {
					if (division.getId().equals(acct.getDivisionId())) {
						totalPerDivision += acct.getAmount();
					}
				}
				isbsTotalDtos.add(ISBSTotalDto.getInstanceOf(totalPerDivision, 0.0, division.getId(), division.getId()));
			}
		}
		return isbsTotalDtos;
	}

	private List<ISBSTotalDto> getISTotalPerComp (List<ISBSAccountDto> accts, List<Integer> companyIds) {
		List<ISBSTotalDto> companyTotals = new ArrayList<ISBSTotalDto>();
		for (Integer companyId : companyIds) {
			double totalPerComp = 0;
			for (ISBSAccountDto acct : accts) {
				if (companyId.equals(acct.getCompanyId())) {
					totalPerComp += acct.getAmount();
				}
			}
			companyTotals.add(ISBSTotalDto.getInstanceOf(totalPerComp, companyId));
		}
		return companyTotals;
	}

	private List<ISBSAccountDto> getAccountsByTypeId (List<ISBSAccountDto> accts, int accountTypeId, boolean isAtPositive) {
		List<ISBSAccountDto> selectedAccount = new ArrayList<ISBSAccountDto>();
		for (ISBSAccountDto acct : accts) {
			if (acct.getAccountTypeId().equals(accountTypeId)) {
				selectedAccount.add(acct);
			}
		}
		return selectedAccount;
	}

	protected String processPAcctId(Integer paId, String strAcctId) {
		if (paId != null && paId.intValue() != 0) {
			strAcctId = paId + "." + strAcctId;
			Account a = accountsCached.get(paId);
			if (a == null) {
				a = accountDao.get(paId);
				accountsCached.put(paId, a);
			}
			return processPAcctId(a.getParentAccountId(), strAcctId);
		}
		return strAcctId;
	}

	protected ISBSTypeDto getAccountBalances (String label, boolean isAtPositive, boolean asOfBalance, int sequenceOrder,
			int accountLevel, String name, int accountTypeId, List<ISBSAccountDto> allAccounts, List<Integer> companyIds,
			boolean isAddSalesPercent) throws CloneNotSupportedException {
		List<ISBSAccountDto> filteredAcctsByComp = getAccountsByTypeId(allAccounts, accountTypeId, isAtPositive);
		ISBSTypeDto isbsTypeDto = new ISBSTypeDto();
		isbsTypeDto.setLabel(label);
		isbsTypeDto.setAtPositive(isAtPositive);
		isbsTypeDto.setAsOfBalance(asOfBalance);
		isbsTypeDto.setSequenceOrder(sequenceOrder);
		isbsTypeDto.setName(name);

		List<ISBSAccountDto> acctsByTypeId = new ArrayList<ISBSAccountDto>();
		Map<Integer, Map<Integer, ISBSAccountDto>> hmAcctsByAcctAndComp = new HashMap<>();
		Integer compId = null;
		for (ISBSAccountDto acct : filteredAcctsByComp) {
			compId = acct.getCompanyId();
			if (hmAcctsByAcctAndComp.containsKey(acct.getCompanyId())) {
				Map<Integer, ISBSAccountDto> isPerAcctId = hmAcctsByAcctAndComp.get(compId);
				if (isPerAcctId.containsKey(acct.getAccountId())) {
					ISBSAccountDto dto = isPerAcctId.get(acct.getAccountId());
					dto.setAmount(dto.getAmount() + acct.getAmount());
					dto.setCompanyId(compId);
					isPerAcctId.put(acct.getAccountId(), dto);
				} else {
					isPerAcctId.put(acct.getAccountId(), acct);
				}
			} else {
				Map<Integer, ISBSAccountDto> isPerAcctId = new HashMap<>();
				isPerAcctId.put(acct.getAccountId(), acct);
				hmAcctsByAcctAndComp.put(compId, isPerAcctId);
			}
		}
		// No longer needed.
		for (Map.Entry<Integer, Map<Integer, ISBSAccountDto>> entry : hmAcctsByAcctAndComp.entrySet()) {
			acctsByTypeId.addAll(new ArrayList<>(entry.getValue().values()));
		}
		hmAcctsByAcctAndComp = null; // No longer needed.

		isbsTypeDto.setTotals(getISTotalPerComp(acctsByTypeId, companyIds));
		acctsByTypeId.addAll(createAndGroupByParent(acctsByTypeId, null, true, false));

		// create a map that will link the account and company. this will be
		// used to identify the company that has no account. 
		Map<Integer, List<Integer>> account2Companies = new HashMap<>();
		// We will use this as reference in creating zero amount account. 
		Map<Integer, List<ISBSAccountDto>> accountId2acctDto = new HashMap<>();
		for (ISBSAccountDto acct : acctsByTypeId) {
			List<Integer> compIds = account2Companies.get(acct.getAccountId());
			if (compIds == null) {
				compIds = new ArrayList<Integer>();
				compIds.add(acct.getCompanyId());
				account2Companies.put(acct.getAccountId(), compIds);
			} else {
				compIds.add(acct.getCompanyId());
			}
			List<ISBSAccountDto> isbsAccounts = accountId2acctDto.get(acct.getAccountId());
			if (isbsAccounts == null) {
				isbsAccounts = new ArrayList<ISBSAccountDto>();
				isbsAccounts.add(acct);
				accountId2acctDto.put(acct.getAccountId(), isbsAccounts);
			} else {
				isbsAccounts.add(acct);
			}
		}

		// Create a zero account needed for jasper report format.
		List<ISBSAccountDto> zeroAccountDtos = new ArrayList<ISBSAccountDto>();
		for (Map.Entry<Integer, List<Integer>> entry : account2Companies.entrySet()) {
			Integer accountId = entry.getKey();
			List<Integer> compIds = entry.getValue();
			ISBSAccountDto accountDto = accountId2acctDto.get(accountId).iterator().next();
			for (Integer companyId : companyIds) {
				if (!compIds.contains(companyId)) {
					ISBSAccountDto zeroAccountDto = (ISBSAccountDto) accountDto.clone();
					zeroAccountDto.setAmount(0.0);
					zeroAccountDto.setCompanyId(companyId);
					zeroAccountDtos.add(zeroAccountDto);
				}
			}
		}

		acctsByTypeId.addAll(zeroAccountDtos);

		isbsTypeDto.setAccounts(filterAccountLevel(accountLevel, acctsByTypeId, false));
		processConsolidated(acctsByTypeId, isbsTypeDto.getTotals(), true, isbsTypeDto, isAddSalesPercent, false);
		ComparatorChain comparatorChain = new ComparatorChain();
		comparatorChain.addComparator(new Comparator<ISBSAccountDto>() {

			@Override
			public int compare(ISBSAccountDto o1, ISBSAccountDto o2) {
				return o1.getAccountId().compareTo(o2.getAccountId());
			}
		});
		comparatorChain.addComparator(new Comparator<ISBSAccountDto>() {
			@Override
			public int compare(ISBSAccountDto o1, ISBSAccountDto o2) {
				return o1.getCompanyId().compareTo(o2.getCompanyId());
			}
		});
		Collections.sort(isbsTypeDto.getAccounts(), comparatorChain);
		return isbsTypeDto;
	}
}
