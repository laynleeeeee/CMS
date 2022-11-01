package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.StockAdjustmentTypeDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.StockAdjustmentType;

/**
 * DAO Implementation of {@link StockAdjustmentTypeDao}

 */
public class StockAdjustmentTypeDaoImpl extends BaseDao<StockAdjustmentType> implements StockAdjustmentTypeDao{

	@Override
	protected Class<StockAdjustmentType> getDomainClass() {
		return StockAdjustmentType.class;
	}

	@Override
	public List<StockAdjustmentType> getSATypes(Integer companyId, Integer divisionId, Boolean activeOnly) {
		DetachedCriteria satDC = getDetachedCriteria();
		satDC.createAlias("acctCombi", "ac");
		if(activeOnly != null) {
			satDC.add(Restrictions.eq(StockAdjustmentType.FIELD.active.name(), activeOnly));
		}
		if(companyId != null) {
			satDC.add(Restrictions.eq("ac.companyId", companyId));
		}
		if(divisionId != null) {
			satDC.add(Restrictions.eq("ac.divisionId", divisionId));
		}
		return getAll(satDC);
	}

	@Override
	public Page<StockAdjustmentType> getAllSAdjustmentTypes(Integer companyId,
			String name, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria satCriteria = getDetachedCriteria();
		if (!name.isEmpty()) {
			satCriteria.add(Restrictions.like(StockAdjustmentType.FIELD.name.name(), "%"+name+"%"));
		}
		if (companyId != -1) {
			getCompanyIdDc(satCriteria, companyId, null);
		}
		satCriteria = DaoUtil.setSearchStatus(satCriteria, StockAdjustmentType.FIELD.active.name(), status);
		satCriteria.addOrder(Order.asc(StockAdjustmentType.FIELD.name.name()));
		return getAll(satCriteria, pageSetting);
	}

	@Override
	public boolean isUniqueSATName(String name, Integer companyId, Integer adjustmentTypeId, Integer divisionId) {
		DetachedCriteria satCriteria = getDetachedCriteria();
		satCriteria.add(Restrictions.eq(StockAdjustmentType.FIELD.name.name(), name.trim()));
		if (adjustmentTypeId != 0) {
			satCriteria.add(Restrictions.ne(StockAdjustmentType.FIELD.id.name(), adjustmentTypeId));
		}
		if (companyId != null) {
			getCompanyIdDc(satCriteria, companyId, divisionId);
		}
		return getAll(satCriteria).isEmpty();
	}

	private void getCompanyIdDc(DetachedCriteria satDc, Integer companyId, Integer divisionId) {
		DetachedCriteria accountCombiDc = DetachedCriteria.forClass(AccountCombination.class);
		accountCombiDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		accountCombiDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			accountCombiDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		}
		satDc.add(Subqueries.propertyIn(StockAdjustmentType.FIELD.accountCombiId.name(), accountCombiDc));
	}
}
