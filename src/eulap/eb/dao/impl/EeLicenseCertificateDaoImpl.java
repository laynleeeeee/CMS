package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EeLicenseCertificateDao;
import eulap.eb.domain.hibernate.EeLicenseCertificate;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.web.dto.GvchEmployeeProfileDto;

/**
 * Implementation class of {@link EeLicenseCertificateDao}

 *
 */

public class EeLicenseCertificateDaoImpl extends BaseDao<EeLicenseCertificate> implements EeLicenseCertificateDao {

	@Override
	protected Class<EeLicenseCertificate> getDomainClass() {
		return EeLicenseCertificate.class;
	}

	@Override
	public List<EeLicenseCertificate> getLicCertByEbObjectId(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), GvchEmployeeProfileDto.EE_LIC_CERT_OR_TYPE));
		dc.add(Subqueries.propertyIn(EeLicenseCertificate.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.add(Restrictions.eq(EeLicenseCertificate.FIELD.active.name(), true));
		return getAll(dc);
	}
}
