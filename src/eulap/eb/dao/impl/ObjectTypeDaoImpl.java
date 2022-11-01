package eulap.eb.dao.impl;


import eulap.common.dao.BaseDao;
import eulap.eb.dao.ObjectTypeDao;
import eulap.eb.domain.hibernate.ObjectType;

/**
 * An implementation class for {@link ObjectTypeDao}

 *
 */
public class ObjectTypeDaoImpl extends BaseDao<ObjectType> implements ObjectTypeDao{
	public enum FIELDS {id, name, referenceTabe, serviceClass, createdBy, CreatedDate}
	
	@Override
	protected Class<ObjectType> getDomainClass() {
		return ObjectType.class;
	}

}
