package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.ObjectToObject;

/**
 * Implements the data access layer of the {@link CompanyDao}


 */
public class ObjectToObjectDaoImpl extends BaseDao<ObjectToObject> implements ObjectToObjectDao {

	@Override
	protected Class<ObjectToObject> getDomainClass() {
		return ObjectToObject.class;
	}
	
	@Override
	public EBObject getParent(Integer ebObjectId) {
		return getOtherReference(ebObjectId, ORType.PARENT_OR_TYPE_ID);
	}

	@Override
	public EBObject getOtherReference (Integer ebObjectId, int orTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), ebObjectId));
		dc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));
		ObjectToObject o2o = get(dc);
		if(o2o == null) {
			return null;
		}
		return o2o.getFromObject();
	}

	@Override
	public void deleteReference(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));
		
		List<ObjectToObject> relatedObject = getAll(dc);
		for (ObjectToObject obj : relatedObject) {
			deleteReference(obj.getToObjectId()); // Recursive function that will delete all
			// the reference of this object.
			delete(obj);
		}
	}

	@Override
	public List<ObjectToObject> getReferenceObjects(Integer fromObjectId, Integer toObjectId, Integer orTypeId) {
		Integer excludeOrType = null;
		return getReferenceObjects(fromObjectId, toObjectId, orTypeId, excludeOrType);
	}

	@Override
	public List<ObjectToObject> getReferenceObjects(Integer fromObjectId, Integer toObjectId, Integer orTypeId,
			Integer... excludeOrTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(fromObjectId != null) {
			dc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), fromObjectId));
		}
		if(toObjectId != null) {
			dc.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), toObjectId));
		}
		if(orTypeId != null){
			dc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));
		}
		if (excludeOrTypeId != null) {
			for (Integer orType : excludeOrTypeId) {
				if (orType != null) {
					dc.add(Restrictions.ne(ObjectToObject.FIELDS.orTypeId.name(), orType));
				}
			}
		}
		return getAll(dc);
	}

	@Override
	public List<EBObject> getParents() {
		List<ObjectToObject> o2os = getReferenceObjects(null, null, ORType.PARENT_OR_TYPE_ID);
		List<EBObject> parents = new ArrayList<>();
		for (ObjectToObject o2o : o2os) {
			parents.add(o2o.getFromObject());
		}
		return parents;
	}

	@Override
	public List<ObjectToObject> getReferenceObjects(Integer toObjectId, int orTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), toObjectId));
		dc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), orTypeId));
		return getAll(dc);
	}

	@Override
	public List<EBObject> getChildren (EBObject parent) {
		List<ObjectToObject> children =getReferenceObjects(parent.getId(), null, ORType.PARENT_OR_TYPE_ID);
		List<EBObject> ret = new ArrayList<>();
		for (ObjectToObject o2o : children) {
			ret.add(o2o.getToObject());
		}
		return ret;
	}
}