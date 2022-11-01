package eulap.eb.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.FormWorkflowLogDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflowLog;

/**
 * Hibernate data access object implementation class of {@link FormWorkflowLogDao}.

 *
 */
public class FormWorkflowLogDaoImpl extends BaseDao<FormWorkflowLog> implements FormWorkflowLogDao{
	private static final Logger LOGGER = Logger.getLogger(FormWorkflowLogDaoImpl.class);
	private static final String TOTAL_TO_BE_DELETED_PAYMENT_LOGS="SELECT COUNT(FORM_WORKFLOW_LOG_ID) FROM FORM_WORKFLOW_LOG FWL "
			+ "INNER JOIN AP_PAYMENT AP ON AP.FORM_WORKFLOW_ID = FWL.FORM_WORKFLOW_ID "
			+ "WHERE FWL.FORM_STATUS_ID="+FormStatus.NEGOTIABLE_ID+";";
	private static final String TOTAL_TO_BE_DELETED_RECEIVABLE_LOGS="SELECT COUNT(FORM_WORKFLOW_LOG_ID) FROM FORM_WORKFLOW_LOG "
			+ "WHERE FORM_WORKFLOW_ID IN ( "
				+ "SELECT AR.FORM_WORKFLOW_ID FROM AR_RECEIPT AR "
					+ "INNER JOIN FORM_WORKFLOW_LOG FWL ON FWL.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID "
				+ "WHERE (FWL.FORM_STATUS_ID="+FormStatus.CHECKED_ID+" OR FWL.FORM_STATUS_ID="+FormStatus.REMITTED_ID+") "
				+ "UNION ALL "
				+ "SELECT AM.FORM_WORKFLOW_ID FROM AR_MISCELLANEOUS AM "
					+ "INNER JOIN FORM_WORKFLOW_LOG FWL ON FWL.FORM_WORKFLOW_ID = AM.FORM_WORKFLOW_ID "
					+ "WHERE (FWL.FORM_STATUS_ID="+FormStatus.CHECKED_ID+" OR FWL.FORM_STATUS_ID="+FormStatus.REMITTED_ID+") "
			+ ") AND FORM_STATUS_ID="+FormStatus.CHECKED_ID+" OR FORM_STATUS_ID="+FormStatus.REMITTED_ID+";";
	private static final String TOTAL_DELETED_PAYMENT_LOGS="SELECT COUNT(FWL.FORM_WORKFLOW_LOG_ID) FROM FORM_WORKFLOW_LOG FWL "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = FWL.FORM_WORKFLOW_ID "
			+ "INNER JOIN AP_PAYMENT APP ON APP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
			+ "WHERE FORM_STATUS_ID ="+FormStatus.NEGOTIABLE_ID+";";
	private static final String TOTAL_DELETED_AR_RECEIPT_LOGS="SELECT COUNT(FWL.FORM_WORKFLOW_LOG_ID) FROM FORM_WORKFLOW_LOG FWL "
			+ "INNER JOIN AR_RECEIPT ARR ON ARR.FORM_WORKFLOW_ID = FWL.FORM_WORKFLOW_ID "
			+ "WHERE FORM_STATUS_ID="+FormStatus.CHECKED_ID+" OR FORM_STATUS_ID = "+FormStatus.REMITTED_ID+";";
	private static final String TOTAL_DELETED_AR_MISC_LOGS="SELECT COUNT(FWL.FORM_WORKFLOW_LOG_ID) FROM FORM_WORKFLOW_LOG FWL "
			+ "INNER JOIN AR_MISCELLANEOUS ARM ON ARM.FORM_WORKFLOW_ID = FWL.FORM_WORKFLOW_ID "
			+ "WHERE FORM_STATUS_ID="+FormStatus.CHECKED_ID+" OR FORM_STATUS_ID = "+FormStatus.REMITTED_ID+";";

	@Override
	protected Class<FormWorkflowLog> getDomainClass() {
		return FormWorkflowLog.class;
	}

	@Override
	public FormWorkflowLog getFormWorkflowLog(Integer formWorkflowId,
			Integer formStatusId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FormWorkflowLog.FIELD.formWorkflowId.name(), formWorkflowId));
		dc.add(Restrictions.eq(FormWorkflowLog.FIELD.formStatusId.name(), formStatusId));
		return get(dc);
	}


	@Override
	public List<FormWorkflowLog> getWorkflowLogsByStatusId(int formWorkflowId, int statusId) {
		DetachedCriteria dc = getDetachedCriteria();
		getFormWorkflowlogs(dc, formWorkflowId, statusId);
		return getAll(dc);
	}

	private void getFormWorkflowlogs(DetachedCriteria dc, int formWorkflowId, int statusId) {
		dc.add(Restrictions.eq(FormWorkflowLog.FIELD.formWorkflowId.name(), formWorkflowId));
		if (statusId != 0) {
			dc.add(Restrictions.eq(FormWorkflowLog.FIELD.formStatusId.name(), statusId));
		}
	}
}
