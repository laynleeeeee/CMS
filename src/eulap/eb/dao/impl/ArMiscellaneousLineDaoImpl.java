package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArMiscellaneousLineDao;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;

/**
 * Implementing class of {@link ArMiscellaneousLineDao}

 *
 */
public class ArMiscellaneousLineDaoImpl extends BaseDao<ArMiscellaneousLine> implements ArMiscellaneousLineDao {

	@Override
	protected Class<ArMiscellaneousLine> getDomainClass() {
		return ArMiscellaneousLine.class;
	}

	@Override
	public List<ArMiscellaneousLine> getArMiscLines(int arLineSetupId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArMiscellaneousLine.FIELD.arLineSetupId.name(), arLineSetupId));
		//AR Miscellaneous
		DetachedCriteria miscellaneousCriteria = DetachedCriteria.forClass(ArMiscellaneous.class);
		miscellaneousCriteria.setProjection(Projections.property(ArMiscellaneous.FIELD.id.name()));
		// Workflow criteria.
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		miscellaneousCriteria.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(ArMiscellaneousLine.FIELD.arMiscellaneousId.name(), miscellaneousCriteria));
		return getAll(dc);
	}

}
