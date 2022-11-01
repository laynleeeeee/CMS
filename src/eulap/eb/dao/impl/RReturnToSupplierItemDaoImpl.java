package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RReturnToSupplierItemDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;

/**
 * Implementation class of {@link RReturnToSupplierItemDao}

 */
public class RReturnToSupplierItemDaoImpl extends BaseDao<RReturnToSupplierItem> implements RReturnToSupplierItemDao{

	@Override
	protected Class<RReturnToSupplierItem> getDomainClass() {
		return RReturnToSupplierItem.class;
	}

	@Override
	public List<RReturnToSupplierItem> getRtsItems(int apInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RReturnToSupplierItem.FIELD.apInvoiceId.name(), apInvoiceId));
		return getAll(dc);
	}

	@Override
	public List<RReturnToSupplierItem> getRtsItemsByRrItem(boolean isCompleted, int rrItemId) {
		DetachedCriteria rtsItemDc = getDetachedCriteria();
		rtsItemDc.add(Restrictions.eq(RReturnToSupplierItem.FIELD.rReceivingReportItemId.name(), rrItemId));
		DetachedCriteria apInvoiceDc = DetachedCriteria.forClass(APInvoice.class);
		apInvoiceDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		apInvoiceDc.createAlias("formWorkflow", "fw");
		if(isCompleted) {
			apInvoiceDc.add(Restrictions.eq("fw.complete", true));
		}
		apInvoiceDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		rtsItemDc.add(Subqueries.propertyIn(RReturnToSupplierItem.FIELD.apInvoiceId.name(), apInvoiceDc));
		return getAll(rtsItemDc);
	}

	@Override
	public List<RReturnToSupplierItem> getRtsItems(int apInvoiceId, int itemId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RReturnToSupplierItem.FIELD.apInvoiceId.name(), apInvoiceId));
		dc.add(Restrictions.eq(RReturnToSupplierItem.FIELD.itemId.name(), itemId));
		return getAll(dc);
	}
}
