package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RequisitionFormItemDao;
import eulap.eb.dao.RequisitionTypeDao;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RequisitionFormItem;

/**
 * Implementation class for {@link RequisitionTypeDao}

 */
public class RequisitionFormItemDaoImpl extends BaseDao<RequisitionFormItem> implements RequisitionFormItemDao {

	@Override
	protected Class<RequisitionFormItem> getDomainClass() {
		return RequisitionFormItem.class;
	}

	@Override
	public List<RequisitionFormItem> getAllByParent(Integer refObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria ooDc = DetachedCriteria.forClass(ObjectToObject.class);
		ooDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), refObjectId));
		ooDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), ORType.PARENT_OR_TYPE_ID));

		dc.add(Subqueries.propertyIn(RequisitionFormItem.FIELD.ebObjectId.name(), ooDc));
		return getAll(dc);
	}
}
