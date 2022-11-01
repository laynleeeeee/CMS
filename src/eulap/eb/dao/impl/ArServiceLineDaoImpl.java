package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArServiceLineDao;
import eulap.eb.domain.hibernate.ArServiceLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;

/**
 * DAO implementation of {@link ArServiceLineDao}

 */
public class ArServiceLineDaoImpl extends BaseDao<ArServiceLine> implements ArServiceLineDao{

	@Override
	protected Class<ArServiceLine> getDomainClass() {
		return ArServiceLine.class;
	}

	@Override
	public List<ArServiceLine> getArServiceLines(int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArServiceLine.FIELD.aRTransactionId.name(), arTransactionId));
		return getAll(dc);
	}

	@Override
	public List<ArServiceLine> getArServiceLinesBySetupId(int arServiceLineSetupId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArServiceLine.FIELD.serviceSettingId.name(), arServiceLineSetupId));
		//AR Transaction
		DetachedCriteria transactionCriteria = DetachedCriteria.forClass(ArTransaction.class);
		transactionCriteria.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		// Workflow criteria.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		transactionCriteria.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ArServiceLine.FIELD.aRTransactionId.name(), transactionCriteria));
		return getAll(dc);
	}

	@Override
	public double getTotalArServiceLineAmount(int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArServiceLine.FIELD.aRTransactionId.name(), arTransactionId));
		dc.setProjection(Projections.sum(ArServiceLine.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
}
