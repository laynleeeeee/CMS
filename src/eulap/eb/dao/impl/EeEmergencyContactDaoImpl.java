package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EeEmergencyContactDao;
import eulap.eb.domain.hibernate.EeEmergencyContact;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.web.dto.GvchEmployeeProfileDto;

/**
 * Implementation class of {@link EeEmergencyContactDao}

 *
 */

public class EeEmergencyContactDaoImpl extends BaseDao<EeEmergencyContact> implements EeEmergencyContactDao {

	@Override
	protected Class<EeEmergencyContact> getDomainClass() {
		return EeEmergencyContact.class;
	}

	@Override
	public List<EeEmergencyContact> getEmerContactsByEbObjId(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), GvchEmployeeProfileDto.EE_EMER_CONTACT_OR_TYPE));
		dc.add(Subqueries.propertyIn(EeEmergencyContact.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(EeEmergencyContact.FIELD.active.name(), true));
		return getAll(dc);
	}

}
