package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.StockAdjustmentTypeDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.StockAdjustmentType;
import eulap.eb.domain.hibernate.User;

/**
 * Business logic for Stock Adjustment Type.

 *
 */
@Service
public class StockAdjustmentTypeService {
	private static Logger logger = Logger.getLogger(StockAdjustmentTypeService.class);
	@Autowired
	private StockAdjustmentTypeDao stockAdjustmentTypeDao;
	@Autowired
	private AccountCombinationService acctCombiService;

	/**
	 * Get the Stock Adjustment Type using the id.
	 * @param adjustmentTypeId The unique id of the stock adjustment type.
	 * @return The stock adjustment type object.
	 */
	public StockAdjustmentType getSAdjustmentType(int adjustmentTypeId) {
		return stockAdjustmentTypeDao.get(adjustmentTypeId);
	}

	/**
	 * Get the list of active stock adjustment types.
	 * @return The list of active stock adjustment types.
	 */
	public List<StockAdjustmentType> getActiveStockAdjustmentTypes() {
		return getActiveStockAdjustmentTypes(null);
	}

	/**
	 * Get the list of active stock adjustment types.
	 * @param companyId The id of the company of the stock adjustment type.
	 * @return The list of active stock adjustment types.
	 */
	public List<StockAdjustmentType> getActiveStockAdjustmentTypes(Integer companyId) {
		return getStockAdjustmentTypes(companyId, null, true);
	}

	/**
	 * Get the list of stock adjustment types.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param activeOnly If active only.
	 * @return List of {@link StockAdjustmentType}
	 */
	public List<StockAdjustmentType> getStockAdjustmentTypes(Integer companyId, Integer divisionId, Boolean activeOnly) {
		return stockAdjustmentTypeDao.getSATypes(companyId, divisionId, activeOnly);
	}
	/**
	 * Get all stock adjustment types.
	 * @param companyId The id of the company.
	 * @param name The name of the stock adjustment type.
	 * @param status The status of the stock adjustment type.
	 * @param pageNumber The page number.
	 * @return The list of stock adjustment types in paged format.
	 */
	public Page<StockAdjustmentType> getAllSATypes(Integer companyId, String name, String status, int pageNumber) {
		logger.info("Retrieving stock adjustment types.");
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		logger.debug("Searching for the parameters: Company="+companyId+", name="+name.trim()+", status="+status);
		return stockAdjustmentTypeDao.getAllSAdjustmentTypes(companyId, name.trim(),
				searchStatus, new PageSetting(pageNumber, 25));
	}

	/**
	 * Save the stock adjustment type.
	 */
	public void saveSAdjustmentType(StockAdjustmentType adjustmentType, User user) {
		logger.info("Saving the Stock Adjustment Type.");
		Integer adjustmentTypeId = adjustmentType.getId();
		boolean isNew = adjustmentTypeId == 0 ? true : false;
		AuditUtil.addAudit(adjustmentType, new Audit(user.getId(), isNew, new Date()));

		AccountCombination acctCombi = acctCombiService.getAccountCombination(adjustmentType.getCompanyId(),
				adjustmentType.getDivisionId(), adjustmentType.getAccountId());
		logger.debug("Account Combination to be saved:"+acctCombi.getId());
		adjustmentType.setAccountCombiId(acctCombi.getId());
		adjustmentType.setName(adjustmentType.getName().trim());
		stockAdjustmentTypeDao.saveOrUpdate(adjustmentType);
		logger.info("Successfully saved the Stock Adjustment type: "+adjustmentType.getName());
	}

	/**
	 * Validate if name is unique per company.
	 * @param name The name of the stock adjustment type.
	 * @param companyId The id of the company.
	 * @param adjustmentTypeId The id of the stock adjustment type.
	 * @param divisionId The division id
	 * @return True if unique, otherwise false.
	 */
	public boolean isUniqueSATName(String name, Integer companyId, Integer adjustmentTypeId, Integer divisionId) {
		return stockAdjustmentTypeDao.isUniqueSATName(name, companyId, adjustmentTypeId, divisionId);
	}
}
