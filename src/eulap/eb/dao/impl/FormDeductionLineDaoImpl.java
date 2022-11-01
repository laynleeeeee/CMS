package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FormDeductionLineDao;
import eulap.eb.domain.hibernate.FormDeductionLine;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implementation class of {@link FormDeductionLineDao}

 *
 */
public class FormDeductionLineDaoImpl extends BaseDao<FormDeductionLine> implements FormDeductionLineDao{

	@Override
	protected Class<FormDeductionLine> getDomainClass() {
		return FormDeductionLine.class;
	}

	@Override
	public List<FormDeductionLine> getFormDeductionLineByEbObject(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));

		dc.add(Subqueries.propertyIn(FormDeductionLine.FIELD.ebObjectId.name(), obj2ObjDc));
		return getAll(dc);
	}

}
