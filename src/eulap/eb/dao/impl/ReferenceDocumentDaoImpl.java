package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;

/**
 * DAO Implementation of {@link ReferenceDocumentDao}

 */
public class ReferenceDocumentDaoImpl extends BaseDao<ReferenceDocument> implements ReferenceDocumentDao{

	@Override
	protected Class<ReferenceDocument> getDomainClass() {
		return ReferenceDocument.class;
	}

	@Override
	public ReferenceDocument getRDByEbObject(int ebObjectId) {
		DetachedCriteria dc = processRDByEbObject(ebObjectId, null);
		return get(dc);
	}

	@Override
	public ReferenceDocument getRDByEbObject(int ebObjectId, Integer orTypeId) {
		DetachedCriteria dc = processRDByEbObject(ebObjectId, orTypeId);
		return get(dc);
	}

	@Override
	public List<ReferenceDocument> getRDsByEbObject(int ebObjectId) {
		DetachedCriteria dc = processRDByEbObject(ebObjectId, null);
		return getAll(dc);
	}

	private DetachedCriteria processRDByEbObject(int ebObjectId, Integer orTypeId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));
		if (orTypeId != null) {
			obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));
		}

		dc.add(Subqueries.propertyIn(ReferenceDocument.FIELD.ebObjectId.name(), obj2ObjDc));
		return dc;
	}

	@Override
	public List<ReferenceDocument> getRDsByEbObject(int ebObjectId, Integer orTypeId) {
		DetachedCriteria dc = processRDByEbObject(ebObjectId, orTypeId);
		return getAll(dc);
	}
}
