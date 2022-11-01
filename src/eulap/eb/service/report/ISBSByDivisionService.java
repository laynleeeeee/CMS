package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.AccountTypeDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.service.AccountService;
import eulap.eb.service.DivisionService;
import eulap.eb.web.dto.AccountBalancesDto;
import eulap.eb.web.dto.AccountDto;
import eulap.eb.web.dto.DivisionDto;
import eulap.eb.web.dto.ISBSAccountDto;
import eulap.eb.web.dto.ISBSTotalDto;
import eulap.eb.web.dto.ISBSTypeDto;
import eulap.eb.web.dto.IncomeStatementNSBDto;

/**
 * Common service class for IS and BS.

 *
 */
@Service
public abstract class ISBSByDivisionService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private AccountTypeDao accountTypeDao;

	protected static final String TAB_SPACE = "&nbsp;&nbsp;&nbsp;&nbsp;";
	protected static final String CONSOLIDATED_DIV_NAME = "CONSOLIDATED";

	protected void sortAcctTypes(List<ISBSTypeDto> acctTypes) {
		Collections.sort(acctTypes, new Comparator<ISBSTypeDto>() {
			@Override
			public int compare(ISBSTypeDto o1, ISBSTypeDto o2) {
				if (o1.getSequenceOrder() == o2.getSequenceOrder()) {
					return 0;
				} else if (o1.getSequenceOrder() < o2.getSequenceOrder()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	protected List<DivisionDto> getDivisions(Integer companyId, Integer divisionId, Integer subDivision, boolean isAddSalesPercent, boolean isIncomeStatment) {
		List<DivisionDto> divisions = null;
		if (divisionId == DivisionDto.DIV_ALL) {
			divisions = divisionService.getDivisionsByCombination(companyId, true);
		} else {
			divisions = divisionService.getAllChildren(divisionId);
			if (divisions.isEmpty()) {
				divisions.add(divisionService.domain2Dto(divisionDao.get(divisionId)));
			} else {
				if (subDivision.intValue() != DivisionDto.DIV_ALL) {
					DivisionDto divDto = null;
					for (DivisionDto d : divisions) {
						if (d.getId().intValue() == subDivision.intValue()) {
							divDto = d;
							break;
						}
					}
					divisions.clear();
					divisions.add(divDto);
				}
			}
		}
		String totalName = isIncomeStatment ? IncomeStatementNSBDto.TOTAL_DIV_NAME : CONSOLIDATED_DIV_NAME;
		divisions.add(DivisionDto.getInstanceOf(IncomeStatementNSBDto.TOTAL_DIV_ID, "", totalName, totalName, null, "", true, true));
		if(isAddSalesPercent) {
			divisions.add(DivisionDto.getInstanceOf(IncomeStatementNSBDto.PERCENT_TO_SALES_ID, "", IncomeStatementNSBDto.PERCENT_TO_SALES, IncomeStatementNSBDto.PERCENT_TO_SALES, null, "", true, true));
		}
		Collections.sort(divisions, new Comparator<DivisionDto>() {
			@Override
			public int compare(DivisionDto o1, DivisionDto o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		return divisions;
	}

	protected List<AccountDto> initAcctsPerDivision(Integer companyId, Integer accountTypeId, List<DivisionDto> divisions) {
		Map<Integer, AccountDto> hmUniqueAccts = new HashMap<>();
		List<AccountDto> acctsPerDivision = null;
		for (DivisionDto div : divisions) {
			acctsPerDivision = accountService.getByCombinationAndType(companyId, div.getId(), accountTypeId, null);
			for (AccountDto a : acctsPerDivision) {
				if (!hmUniqueAccts.containsKey(a.getId())) {
					hmUniqueAccts.put(a.getId(), a);
				}
			}
		}
		return new ArrayList<>(hmUniqueAccts.values());
	}

	/**
	 * Override this method!!!
	 */
	protected List<AccountBalancesDto> getAccountBalances (int companyId, int accountId, int divisionId, 
			 Date dateFrom, Date dateTo) {
		return new ArrayList<>();
	}

	protected List<ISBSTotalDto> computeTotalsByCompType(List<ISBSTypeDto> listType1, List<ISBSTypeDto> listType2, boolean isAdd,
			List<Integer> companyIds) {
		List<ISBSTotalDto> totalBs1 = groupCompTotals(new ArrayList<>(listType1));
		listType1 = null; // Free up memory. No longer needed.
		List<ISBSTotalDto> totalBs2 = groupCompTotals(new ArrayList<>(listType2));
		listType2 = null; // Free up memory. No longer needed.
		return computeCompanyTotals(totalBs1, totalBs2, isAdd, companyIds);
	}

	protected List<ISBSTotalDto> computeTotalsByType(List<ISBSTypeDto> listType1, List<ISBSTypeDto> listType2, boolean  isActualVsBudget, boolean isAdd) {
		return computeTotalsByType(listType1, listType2, isActualVsBudget, isAdd, false);
	}

	protected List<ISBSTotalDto> computeTotalsByType(List<ISBSTypeDto> listType1, List<ISBSTypeDto> listType2, boolean  isActualVsBudget, boolean isAdd, boolean isByMonth) {
		List<ISBSTotalDto> totalBs1 = groupTotals(new ArrayList<>(listType1), isActualVsBudget, isByMonth);
		listType1 = null; // Free up memory. No longer needed.
		List<ISBSTotalDto> totalBs2 = groupTotals(new ArrayList<>(listType2), isActualVsBudget, isByMonth);
		listType2 = null; // Free up memory. No longer needed.
		return computeTotals(totalBs1, totalBs2, isAdd, isByMonth);
	}

	protected List<ISBSTotalDto> computeCompanyTotals(List<ISBSTotalDto> list1, List<ISBSTotalDto> list2, boolean isAdd,
			List<Integer> companyIds) {
		List<ISBSTotalDto> totals = new ArrayList<>();

		Map<Integer, ISBSTotalDto> hm1 = new HashMap<>();
		for (ISBSTotalDto l1 : list1) {
			hm1.put(l1.getCompanyId(), l1);
		}

		Map<Integer, ISBSTotalDto> hm2 = new HashMap<>();
		for (ISBSTotalDto l2 : list2) {
			hm2.put(l2.getCompanyId(), l2);
		}

		for (Integer companyId : companyIds) {
			if (hm1.containsKey(companyId) && hm2.containsKey(companyId)) {
				totals.add(ISBSTotalDto.getInstanceOf(hm1.get(companyId).getAmount() + (isAdd ? hm2.get(companyId).getAmount() : - hm2.get(companyId).getAmount()), companyId));
			} else if (hm1.containsKey(companyId)) {
				totals.add(ISBSTotalDto.getInstanceOf(hm1.get(companyId).getAmount(), companyId));
			} else if (hm2.containsKey(companyId)) {
				totals.add(ISBSTotalDto.getInstanceOf(hm2.get(companyId).getAmount(), companyId));
			}
		}
		return totals;
	}

	protected List<ISBSTotalDto> computeTotals(List<ISBSTotalDto> list1, List<ISBSTotalDto> list2, boolean isAdd) {
		return computeTotals(list1, list2, isAdd, false);
	}

	protected List<ISBSTotalDto> computeTotals(List<ISBSTotalDto> list1, List<ISBSTotalDto> list2, boolean isAdd, boolean isByMonth) {
		List<ISBSTotalDto> totals = new ArrayList<>();
		Set<Integer> uniqueDivIds = new HashSet<>();

		Map<Integer, ISBSTotalDto> hm1 = new HashMap<>();
		for (ISBSTotalDto l1 : list1) {
			uniqueDivIds.add(isByMonth ? l1.getMonth() : l1.getDivisionId());
			hm1.put(isByMonth ? l1.getMonth() : l1.getDivisionId(), l1);
		}

		Map<Integer, ISBSTotalDto> hm2 = new HashMap<>();
		for (ISBSTotalDto l2 : list2) {
			uniqueDivIds.add(isByMonth ? l2.getMonth() : l2.getDivisionId());
			hm2.put(isByMonth ? l2.getMonth() : l2.getDivisionId(), l2);
		}
		List<Integer> listUniqueDivIds = new ArrayList<>(uniqueDivIds);
		uniqueDivIds = null;
		list1 = null; // Free up memory. No longer needed.
		list2 = null; // Free up memory. No longer needed.

		Collections.sort(listUniqueDivIds, new Comparator<Integer>() {
			@Override
			public int compare(Integer div1, Integer div2) {
				return div1.compareTo(div2);
			}
		});

		for (Integer divId : listUniqueDivIds) {
			if (hm1.containsKey(divId) && hm2.containsKey(divId)) {
				totals.add(ISBSTotalDto.getInstanceOf(hm1.get(divId).getAmount() + 
					(isAdd ? hm2.get(divId).getAmount() : - hm2.get(divId).getAmount()), 
					hm1.get(divId).getBudgetAmount()-hm2.get(divId).getBudgetAmount(), divId, divId));
			} else if (hm1.containsKey(divId)) {
				totals.add(ISBSTotalDto.getInstanceOf(hm1.get(divId).getAmount(), hm1.get(divId).getBudgetAmount(), divId, divId));
			} else if (hm2.containsKey(divId)) {
				totals.add(ISBSTotalDto.getInstanceOf(hm2.get(divId).getAmount(), hm2.get(divId).getBudgetAmount(), divId, divId));
			}
		}
		return totals;
	}

	protected List<ISBSTotalDto> groupTotals(List<ISBSTypeDto> isBsTypeDtos) {
		Map<Integer, ISBSTotalDto> hmTotals = new HashMap<>();
		ISBSTotalDto obj = null;
		for (ISBSTypeDto is : isBsTypeDtos) {
			for (ISBSTotalDto isTotal : is.getTotals()) {
				if (hmTotals.containsKey(isTotal.getCompanyId())) {
					obj = hmTotals.get(isTotal.getCompanyId());
					obj.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(obj.getAmount() + isTotal.getAmount()));
				} else {
					hmTotals.put(isTotal.getCompanyId(), isTotal);
				}
			}
		}
		return new ArrayList<>(hmTotals.values());
	}

	protected List<ISBSTotalDto> groupTotals(List<ISBSTypeDto> isBsTypeDtos, boolean isActualVsBudget, boolean isByMonth) {
		Map<Integer, ISBSTotalDto> hmTotals = new HashMap<>();
		ISBSTotalDto obj = null;
		for (ISBSTypeDto is : isBsTypeDtos) {
			for (ISBSTotalDto isTotal : is.getTotals()) {
				if (hmTotals.containsKey(isByMonth ? isTotal.getMonth() : isTotal.getDivisionId())) {
					obj = hmTotals.get(isByMonth ? isTotal.getMonth() : isTotal.getDivisionId());
					obj.setAmount(obj.getAmount() + isTotal.getAmount());
					if(isActualVsBudget) {
						obj.setBudgetAmount(obj.getBudgetAmount() + isTotal.getBudgetAmount());
					}
				} else {
					obj = ISBSTotalDto.getInstanceOf(isTotal.getAmount(), isTotal.getBudgetAmount(), isTotal.getDivisionId(), isTotal.getMonth());
					hmTotals.put(isByMonth ? isTotal.getMonth() : isTotal.getDivisionId(), obj);
				}
			}
		}
		return new ArrayList<>(hmTotals.values());
	}

	protected List<ISBSTotalDto> groupCompTotals(List<ISBSTypeDto> isBsTypeDtos) {
		Map<Integer, ISBSTotalDto> hmTotals = new HashMap<>();
		ISBSTotalDto obj = null;
		for (ISBSTypeDto is : isBsTypeDtos) {
			for (ISBSTotalDto isTotal : is.getTotals()) {
				if (hmTotals.containsKey(isTotal.getCompanyId())) {
					obj = hmTotals.get(isTotal.getCompanyId());
					obj.setAmount(obj.getAmount() + isTotal.getAmount());
				} else {
					hmTotals.put(isTotal.getCompanyId(), isTotal);
				}
			}
		}
		return new ArrayList<>(hmTotals.values());
	}

	protected List<ISBSAccountDto> getISBSAccounts (int companyId, int accountId, int divisionId, 
			 Date dateFrom, Date dateTo, boolean isActualVsBudget) {
		return getISBSAccounts(companyId, accountId, divisionId, dateFrom, dateTo, isActualVsBudget, false, false);
	}

	protected List<ISBSAccountDto> getISBSAccounts (int companyId, int accountId, int divisionId, 
			 Date dateFrom, Date dateTo, boolean isActualVsBudget, boolean isIS) {
		return getISBSAccounts(companyId, accountId, divisionId, dateFrom, dateTo, isActualVsBudget, false, isIS);
	}

	protected List<ISBSAccountDto> getISBSAccounts (int companyId, int accountId, int divisionId, 
			 Date dateFrom, Date dateTo, boolean isActualVsBudget, boolean isByMonth, boolean isIS) {
		List<AccountType> accountTypes = accountTypeDao.getAllActive();
		List<ISBSAccountDto> accts = accountDao.getISBSAccounts(companyId, accountId, divisionId,
				accountTypes, dateFrom, dateTo, isIS, isByMonth);
		return accts;
	}

	protected String processPAcctId(Integer paId, String strAcctId) {
		if (paId != null && paId.intValue() != 0) {
			strAcctId = paId + "." + strAcctId;
			Account a = accountDao.get(paId); 
			return processPAcctId(a.getParentAccountId(), strAcctId);
		}
		return strAcctId;
	}

}