package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.BirAtcDao;
import eulap.eb.domain.hibernate.BirAtc;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;

/**
 * DAO implementation class of {@link BirAtcDao}

 */

public class BirAtcDaoImpl extends BaseDao<BirAtc> implements BirAtcDao {

	@Override
	protected Class<BirAtc> getDomainClass() {
		return BirAtc.class;
	}

	@Override
	public List<BirAtc> getListOfBirAtcByWtType(Integer[] wtTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(wtTypeId != null) {
			DetachedCriteria wtDc = DetachedCriteria.forClass(WithholdingTaxAcctSetting.class);
			wtDc.setProjection(Projections.property(WithholdingTaxAcctSetting.FIELD.birAtcId.name()));
			wtDc.add(Restrictions.in(WithholdingTaxAcctSetting.FIELD.wtTypeId.name(), wtTypeId));
			dc.add(Subqueries.propertyIn(BirAtc.FIELD.id.name(), wtDc));
		}
		return getAll(dc);
	}
}
