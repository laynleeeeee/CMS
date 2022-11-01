package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArLineDao;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;

/**
 * DAO implementation of {@link ArLineDao}

 */
public class ArLineDaoImpl extends BaseDao<ArLine> implements ArLineDao{

	@Override
	protected Class<ArLine> getDomainClass() {
		return ArLine.class;
	}

	@Override
	public List<ArLine> getArLines(int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArLine.FIELD.aRTransactionId.name(), arTransactionId));
		return getAll(dc);
	}

	@Override
	public List<ArLine> getArLinesBySetupId(int arLineSetupId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArLine.FIELD.arLineSetupId.name(), arLineSetupId));
		//AR Transaction
		DetachedCriteria transactionCriteria = DetachedCriteria.forClass(ArTransaction.class);
		transactionCriteria.setProjection(Projections.property(ArTransaction.FIELD.id.name()));
		// Workflow criteria.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		transactionCriteria.add(Subqueries.propertyIn(ArTransaction.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ArLine.FIELD.aRTransactionId.name(), transactionCriteria));
		return getAll(dc);
	}

	@Override
	public double getTotalArLineAmount(int arTransactionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArLine.FIELD.aRTransactionId.name(), arTransactionId));
		dc.setProjection(Projections.sum(ArLine.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
}
