package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FormWorkflowDao;
import eulap.eb.domain.hibernate.FormWorkflow;

/**
 * A class that implements the database transaction of FormWorkflow.

 *
 */
public class FormWorkflowDaoImpl extends BaseDao<FormWorkflow> implements FormWorkflowDao{

	@Override
	protected Class<FormWorkflow> getDomainClass() {
		return FormWorkflow.class;
	}

}
