package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EeSeminarAttendedDao;
import eulap.eb.domain.hibernate.EeSeminarAttended;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.web.dto.GvchEmployeeProfileDto;

/**
 * Implementation class of {@link EeSeminarAttendedDao}

 *
 */

public class EeSeminarAttendedDaoImpl extends BaseDao<EeSeminarAttended> implements EeSeminarAttendedDao {

	@Override
	protected Class<EeSeminarAttended> getDomainClass() {
		return EeSeminarAttended.class;
	}

	@Override
	public List<EeSeminarAttended> getSeminarAttendedByEbObjectId(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), GvchEmployeeProfileDto.EE_SEMINAR_OR_TYPE));
		dc.add(Subqueries.propertyIn(EeSeminarAttended.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(EeSeminarAttended.FIELD.active.name(), true));
		return getAll(dc);
	}

}
