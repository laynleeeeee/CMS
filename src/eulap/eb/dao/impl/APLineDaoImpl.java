package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.APLineDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.AccountCombination;

/**
 * DAO implementation of {@link APLineDao}

 *
 */
public class APLineDaoImpl extends BaseDao<APLine> implements APLineDao{

	@Override
	protected Class<APLine> getDomainClass() {
		return APLine.class;
	}

	@Override
	public List<APLine> getAPLines(int apInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(APLine.FIELD.aPInvoiceId.name(), apInvoiceId));
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
			dc.add(Restrictions.gt(APLine.FIELD.amount.name(), 0.0));
		} else {
			dc.add(Restrictions.lt(APLine.FIELD.amount.name(), 0.0));
		}

		//Subquery for AP Invoice
		DetachedCriteria apInvoiceCriteria = DetachedCriteria.forClass(APInvoice.class);
		apInvoiceCriteria.add(Restrictions.le(APInvoice.FIELD.glDate.name(), asOfDate));
		apInvoiceCriteria.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(APLine.FIELD.aPInvoiceId.name(), apInvoiceCriteria));

		// form Work flow
		filterByComplete(apInvoiceCriteria);

		//Subquery for Account Combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(APLine.FIELD.accountCombinationId.name(), acctCombiCriteria));

		dc.setProjection(Projections.sum(APLine.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

}
