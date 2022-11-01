package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PurchaseOrderLineDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PurchaseOrderLine;

/**
 * DAO implementation class for {@link PurchaseOrderLineDao}

 */

public class PurchaseOrderLineDaoImpl extends BaseDao<PurchaseOrderLine> implements PurchaseOrderLineDao {

	@Override
	protected Class<PurchaseOrderLine> getDomainClass() {
		return PurchaseOrderLine.class;
	}

	@Override
	public PurchaseOrderLine getByApInvoiceLineObjectId(Integer objectId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Object to object
		DetachedCriteria dcOto = DetachedCriteria.forClass(ObjectToObject.class);
		dcOto.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), objectId));
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), 13));
		dc.add(Subqueries.propertyIn(PurchaseOrderLine.FIELD.ebObjectId.name(), dcOto));
		return get(dc);
	}

	@Override
	public List<PurchaseOrderLine> getPOLines(int poId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PurchaseOrderLine.FIELD.purchaseOrderId.name(), poId));
		return getAll(dc);
	}
}
