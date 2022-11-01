package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.ItemSrpDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CurrencyRate;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.ItemSrp;

/**
 * Class that handles the business logic {@link ItemSrp}

 *
 */
@Service
public class ItemSrpService {
	@Autowired
	private ItemSrpDao itemSrpDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private CurrencyRateService currencyRateService;

	/**
	 * Get the Item SRP object using its unique id.
	 * @return the Item SRP.
	 */
	public ItemSrp getItemSrp(int itemSrpId) {
		return itemSrpDao.get(itemSrpId);
	}

	/**
	 * Get the list of item srps by item id.
	 * @param itemId The item id.
	 * @return The list of item srps.
	 */
	public List<ItemSrp> getItemSrpsByItem (Integer itemId) {
		return itemSrpDao.getItemSrpsByItem(itemId);
	}

	/**
	 * Processes the item srps that has no company id.
	 * @param itemSrps The item srps.
	 * @return List of item srps.
	 */
	public List<ItemSrp> processItemSrps (List<ItemSrp> itemSrps) {
		List<ItemSrp> ret = new ArrayList<ItemSrp>();
		if (itemSrps != null) {
			for (ItemSrp itemSrp : itemSrps) {
				String companyName = itemSrp.getCompanyName();
				if (companyName != null && !companyName.trim().isEmpty() 
						|| (itemSrp.getSrp() != null && itemSrp.getSrp() != 0.00)) {
					ret.add(itemSrp);	
				}
			}
		}
		return ret;
	}

	/**
	 * Checks if the item contains invalid item srps.
	 * @param itemSrps List of item srps.
	 * @return True if has invalid item srps, otherwise false. 
	 */
	public boolean hasInvalidItemSrp (List<ItemSrp> itemSrps) {
		if (itemSrps != null) {
			for (ItemSrp itemSrp : itemSrps) {
				String companyName = itemSrp.getCompanyName();
				if (companyName == null)
					return true;
				else if (companyName.trim().isEmpty())
					return true;
				else {
					Company company = companyDao.getCompanyByName(companyName.trim());
					if (company == null)
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the item srp.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @param divisionId The division id
	 * @return The item srp object.
	 */
	public ItemSrp getLatestItemSrp(Integer companyId, Integer itemId, Integer divisionId) {
		return itemSrpDao.getLatestItemSrp(companyId, itemId, divisionId);
	}

	/**
	 * Get the item srp.
	 * @param companyId The company id.
	 * @param itemId The item id.
	 * @param divisionId The division id
	 * @param currencyId The currency id
	 * @return The item srp object.
	 */
	public ItemSrp getLatestItemSrpWithRate(Integer companyId, Integer itemId, Integer divisionId, Integer currencyId) {
		ItemSrp itemSrp = itemSrpDao.getLatestItemSrp(companyId, itemId, divisionId);
		if(currencyId != null) {
			CurrencyRate rate = currencyRateService.getLatestRate(currencyId);
			if(rate != null && rate.isActive()) {
				itemSrp.setSrp(itemSrp.getSrp()/(rate.getRate() != null ? rate.getRate() : 1));
			}
		}
		return itemSrp;
	}

	/**
	 * Checks if the value of srp is negative.
	 * @param itemSrps The list of item srps.
	 * @return True if a value of an item srp is negative, otherwise false.
	 */
	public boolean hasZeroOrNegValues (List<ItemSrp> itemSrps) {
		if (itemSrps != null && !itemSrps.isEmpty()) {
			for (ItemSrp itemSrp : itemSrps) {
				Double srp = itemSrp.getSrp();
				if (srp != null) {
					if (Double.valueOf(srp) <= 0)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Extract the list of company ids from the list of srps.
	 * @param itemSrps the list of srps.
	 * @return The list of distinct company ids.
	 */
	public List<Integer> getCompanyIds(List<ItemSrp> itemSrps) {
		List<Integer> companyIds  = new ArrayList<Integer>();
		if(!itemSrps.isEmpty()) {
			for (ItemSrp srp : itemSrps) {
				if(Collections.frequency(companyIds, srp.getCompanyId()) == 0) {
					companyIds.add(srp.getCompanyId());
				}
			}
		}
		return companyIds;
	}

	/**
	 * Checks if the item has an srp.
	 * @param itemId The id of the item to be checked.
	 * @return True if it has srp, otherwise false.
	 */
	public boolean hasItemSrp (int itemId) {
		return itemSrpDao.hasItemSrp(itemId);
	}

	public boolean hasDuplicateSRPCombination(List<ItemSrp> itemSrps) {
		Map<String, String> dupCombiHm = new HashMap<String, String>();
		String key = "";
		for (ItemSrp itemSrp : itemSrps) {
			key = "c"+itemSrp.getCompanyId()+"d"+itemSrp.getDivisionId();
			String d = dupCombiHm.get(key);
			if(d != null) {
				return true;
			}
			dupCombiHm.put(key, key);
		}
		return false;
	}

	/**
	 * Validate division.
	 */
	public String hasInvalidDivisionSrp(List<ItemSrp> itemSrps) {
		if (itemSrps != null) {
			for (ItemSrp itemSrp : itemSrps) {
				if (itemSrp.getDivisionName() == null || itemSrp.getDivisionName().trim().isEmpty()) {
					return "Division is required.";
				} else {
					Division division = divisionDao.getDivisionByName(itemSrp.getDivisionName().trim(), true, false);
					if(division == null) {
						return "Invalid division.";
					}
				}
			}
		}
		return null;
	}
}
