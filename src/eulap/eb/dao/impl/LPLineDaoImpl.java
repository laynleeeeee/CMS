package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.LPLineDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.LPLine;
import eulap.eb.domain.hibernate.AccountCombination;

/**
 * DAO implementation of {@link LPLineDao}

 *
 */
public class LPLineDaoImpl extends BaseDao<LPLine> implements LPLineDao{

	@Override
	protected Class<LPLine> getDomainClass() {
		return LPLine.class;
	}

	@Override
	public List<LPLine> getLPLines(int apInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(LPLine.FIELD.loadnProceedsId.name(), apInvoiceId));
		return getAll(dc);
	}

	@Override
	public double getTotalDebit(int companyId, Date asOfDate, int accountId) {
		return getTotalAccount(true, companyId, asOfDate, accountId);
	}

	@Override
	public double getTotalCredit(int companyId, Date asOfDate, int accountId) {
		return Math.abs(getTotalAccount(false, companyId, asOfDate, accountId));
	}

	private double getTotalAccount(boolean isDebit, int companyId, Date asOfDate, int accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(isDebit) {
			dc.add(Restrictions.gt(LPLine.FIELD.amount.name(), 0.0));
		} else {
			dc.add(Restrictions.lt(LPLine.FIELD.amount.name(), 0.0));
		}

		//Subquery for AP Invoice
		DetachedCriteria apInvoiceCriteria = DetachedCriteria.forClass(APInvoice.class);
		apInvoiceCriteria.add(Restrictions.le(APInvoice.FIELD.glDate.name(), asOfDate));
		apInvoiceCriteria.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(LPLine.FIELD.loadnProceedsId.name(), apInvoiceCriteria));

		// form Work flow
		filterByComplete(apInvoiceCriteria);

		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(LPLine.FIELD.accountCombinationId.name(), acctCombiCriteria));

		dc.setProjection(Projections.sum(LPLine.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

}
