package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.domain.hibernate.EBObject;

/**
 * Implements the data access layer of the {@link EBObjectDao}


 */
public class EBObjectDaoImpl extends BaseDao<EBObject> implements EBObjectDao{
	public enum FIELDS {id, createdBy, CreatedDate}

	@Override
	protected Class<EBObject> getDomainClass() {
		return EBObject.class;
	}

	@Override
	public List<EBObject> getEbObjectByObjectType(int objectTypId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EBObject.FIELD.objectTypeId.name(), objectTypId));
		return getAll(dc);
	};

}