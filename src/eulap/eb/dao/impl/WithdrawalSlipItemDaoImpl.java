package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WithdrawalSlipItemDao;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.WithdrawalSlipItem;

/**
 * DAO Implementation of {@link WithdrawalSlipItemDao}

 *
 */
public class WithdrawalSlipItemDaoImpl extends BaseDao<WithdrawalSlipItem> implements WithdrawalSlipItemDao {

	@Override
	protected Class<WithdrawalSlipItem> getDomainClass() {
		return WithdrawalSlipItem.class;
	}

	@Override
	public List<WithdrawalSlipItem> getAllActiveWsItems(Integer wsEbObjectId, Integer itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(WithdrawalSlipItem.FIELD.active.name(), true));
		if(itemId != null){
			dc.add(Restrictions.eq(WithdrawalSlipItem.FIELD.itemId.name(), itemId));
		}
		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), wsEbObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));
		dc.add(Subqueries.propertyIn(WithdrawalSlipItem.FIELD.ebObjectId.name(), ooDc));
		return getAll(dc);
	}
}
