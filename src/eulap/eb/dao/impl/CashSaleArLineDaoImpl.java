package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CashSaleArLineDao;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.FormStatus;

/**
 * DAO Implementation class of {@link CashSaleArLineDao}

 *
 */
public class CashSaleArLineDaoImpl extends BaseDao<CashSaleArLine> implements CashSaleArLineDao{

	@Override
	protected Class<CashSaleArLine> getDomainClass() {
		return CashSaleArLine.class;
	}

	@Override
	public List<CashSaleArLine> getCsArLines(int cashSaleId) {
		DetachedCriteria csArLineDc = getDetachedCriteria();
		csArLineDc.add(Restrictions.eq(CashSaleArLine.FIELD.cashSaleId.name(), cashSaleId));
		return getAll(csArLineDc);
	}

	@Override
	public double getTotalAmountCSAByDate(Date date) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria csDc = DetachedCriteria.forClass(CashSale.class);
		csDc.setProjection(Projections.property(CashSale.FIELD.id.name()));
		csDc.createAlias("formWorkflow", "fw");
		csDc.add(Restrictions.eq(CashSale.FIELD.receiptDate.name(), date));
		csDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(CashSaleArLine.FIELD.cashSaleId.name(), csDc));
		dc.setProjection(Projections.sum(CashSaleArLine.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
}
