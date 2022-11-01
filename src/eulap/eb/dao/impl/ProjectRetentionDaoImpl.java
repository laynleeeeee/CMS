package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.ProjectRetentionDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ProjectRetention;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.RetentionCostDto;

/**
 * Implementing class of {@link ProjectRetentionDao}

 */

public class ProjectRetentionDaoImpl extends BaseDao<ProjectRetention> implements ProjectRetentionDao {

	@Override
	protected Class<ProjectRetention> getDomainClass() {
		return ProjectRetention.class;
	}

	@Override
	public int generateSeqNo(int companyId, Integer typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(ProjectRetention.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(ProjectRetention.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(ProjectRetention.FIELD.projectRetentionTypeId.name(), typeId));
		return generateSeqNo(dc);
	}

	@Override
	public Page<ProjectRetention> getAllPrByStatus(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting, final int typeId) {
		HibernateCallback<Page<ProjectRetention>> hibernateCallback = new HibernateCallback<Page<ProjectRetention>>() {
			@Override
			public Page<ProjectRetention> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria prCriteria = session.createCriteria(ProjectRetention.class);
				if (typeId != 0) {
					prCriteria.add(Restrictions.eq(ProjectRetention.FIELD.projectRetentionTypeId.name(), typeId));
				}
				if (StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					prCriteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?",
							searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(prCriteria, null, "companyId",
						ProjectRetention.FIELD.date.name(), ProjectRetention.FIELD.date.name(),
						ProjectRetention.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				if (!formStatusIds.isEmpty()) {
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				workflowCriteria.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				prCriteria.add(Subqueries.propertyIn(ProjectRetention.FIELD.formWorkflowId.name(), workflowCriteria));
				prCriteria.addOrder(Order.desc(ProjectRetention.FIELD.date.name()));
				prCriteria.addOrder(Order.desc(ProjectRetention.FIELD.sequenceNo.name()));
				return getAll(prCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<ProjectRetention> searchProjectRetentions(Integer typeId, String criteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ProjectRetention.FIELD.projectRetentionTypeId.name(), typeId));
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		dc.addOrder(Order.desc(ProjectRetention.FIELD.sequenceNo.name()));
		dc.addOrder(Order.desc(ProjectRetention.FIELD.date.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<RetentionCostDto> generateRetentionCostRprt(Integer companyId, Integer divisionId, Integer customerId,
			Integer customerAcctId, Date dateFrom, Date dateTo, Date asOfDate, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT SO_ID, DIVISION, CUSTOMER_ACCT, CUSTOMER_NAME, SO_NO, PO_NUMBER, DELIVERY_DATE, SI_NO, SI_RETENTION, "
				+ "AC_NO, AC_RETENTION, COALESCE(COLLECTED_PR, 0) AS COLLECTED_PR, DUE_DATE  FROM ( "
				+ "SELECT SO.SALES_ORDER_ID AS SO_ID, D.NAME AS DIVISION, ARCA.NAME AS CUSTOMER_ACCT, ARC.NAME AS CUSTOMER_NAME, SO.SEQUENCE_NO AS SO_NO, "
				+ "SO.PO_NUMBER AS PO_NUMBER, SO.DELIVERY_DATE AS DELIVERY_DATE, ARI.SEQUENCE_NO AS SI_NO, ARI.RETENTION AS SI_RETENTION, NULL AS AC_NO, "
				+ "0 AS AC_RETENTION, "
				+ "(SELECT ROUND(SUM(ARL1.AMOUNT) * (PRL.AMOUNT/PR.AMOUNT+PR.WT_AMOUNT), 2) "
				+ "FROM PROJECT_RETENTION PR "
				+ "INNER JOIN PROJECT_RETENTION_LINE PRL ON PRL.PROJECT_RETENTION_ID = PR.PROJECT_RETENTION_ID "
				+ "INNER JOIN FORM_WORKFLOW PR_FW ON PR_FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT PROTO ON PROTO.TO_OBJECT_ID = PRL.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT COTO ON COTO.FROM_OBJECT_ID = PR.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL1 ON ARL1.EB_OBJECT_ID = COTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR1 ON ARR1.AR_RECEIPT_ID = ARL1.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW ARR_FW ON ARR_FW.FORM_WORKFLOW_ID = ARR1.FORM_WORKFLOW_ID "
				+ "WHERE PROTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID "
				+ "AND PROTO.OR_TYPE_ID = 24007 "
				+ "AND PR_FW.IS_COMPLETE = 1 "
				+ "AND ARR_FW.CURRENT_STATUS_ID = " + FormStatus.CLEARED_ID + " "
				+ "GROUP BY PROTO.FROM_OBJECT_ID) AS COLLECTED_PR, "
				+ "(SELECT PR.DUE_DATE FROM PROJECT_RETENTION PR "
				+ "    INNER JOIN FORM_WORKFLOW PR_FW ON PR_FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "    WHERE PR.SALES_ORDER_ID = SO.SALES_ORDER_ID AND PR_FW.CURRENT_STATUS_ID != 4 "
				+ "    GROUP BY SO.SALES_ORDER_ID) AS DUE_DATE "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = SO.DIVISION_ID "
				+ "INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ARCA ON ARCA.AR_CUSTOMER_ACCOUNT_ID = SO.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW ARI_FW ON ARI_FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE ARI_FW.IS_COMPLETE = 1 "
				+ "AND ARI.RETENTION > 0 "
				+ "AND SO.COMPANY_ID = ? "
				+ (divisionId != - 1 ? "AND SO.DIVISION_ID = ? " : "")
				+ (customerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (customerAcctId != -1 ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SO.DELIVERY_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT SO.SALES_ORDER_ID AS SO_ID, D.NAME AS DIVISION, ARCA.NAME AS CUSTOMER_ACCT, ARC.NAME AS CUSTOMER_NAME, SO.SEQUENCE_NO AS SO_NO, "
				+ "SO.PO_NUMBER AS PO_NUMBER, SO.DELIVERY_DATE AS DELIVERY_DATE, NULL AS SI_NO, 0 AS SI_RETENTION, ARR.SEQUENCE_NO AS AC_NO, ARR.RETENTION AS AC_RETENTION, "
				+ "(SELECT ROUND(SUM(ARL1.AMOUNT) * (PRL.AMOUNT/PR.AMOUNT+PR.WT_AMOUNT), 2) "
				+ "FROM PROJECT_RETENTION PR "
				+ "INNER JOIN PROJECT_RETENTION_LINE PRL ON PRL.PROJECT_RETENTION_ID = PR.PROJECT_RETENTION_ID "
				+ "INNER JOIN FORM_WORKFLOW PR_FW ON PR_FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT PROTO ON PROTO.TO_OBJECT_ID = PRL.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT COTO ON COTO.FROM_OBJECT_ID = PR.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL1 ON ARL1.EB_OBJECT_ID = COTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR1 ON ARR1.AR_RECEIPT_ID = ARL1.AR_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW ARR_FW ON ARR_FW.FORM_WORKFLOW_ID = ARR1.FORM_WORKFLOW_ID "
				+ "WHERE PROTO.FROM_OBJECT_ID = ARR.EB_OBJECT_ID "
				+ "AND PROTO.OR_TYPE_ID = 24007 "
				+ "AND PR_FW.IS_COMPLETE = 1 "
				+ "AND ARR_FW.CURRENT_STATUS_ID = " + FormStatus.CLEARED_ID + " "
				+ "GROUP BY PROTO.FROM_OBJECT_ID) AS COLLECTED_PR, "
				+ "(SELECT PR.DUE_DATE FROM PROJECT_RETENTION PR "
				+ "INNER JOIN FORM_WORKFLOW PR_FW ON PR_FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "    WHERE PR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "    AND PR_FW.CURRENT_STATUS_ID != 4 "
				+ "    GROUP BY SO.SALES_ORDER_ID) AS DUE_DATE "
				+ "FROM SALES_ORDER SO "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID "
				+ "INNER JOIN AR_INVOICE ARI ON ARI.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
				+ "INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARL.AR_RECEIPT_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = SO.DIVISION_ID "
				+ "INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ARCA ON ARCA.AR_CUSTOMER_ACCOUNT_ID = SO.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW ARR_FW ON ARR_FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID "
				+ "WHERE ARR_FW.CURRENT_STATUS_ID != 4 "
				+ "AND ARR.RETENTION > 0 "
				+ "AND SO.COMPANY_ID = ? "
				+ (divisionId != - 1 ? "AND SO.DIVISION_ID = ? " : "")
				+ (customerId != null ? "AND SO.AR_CUSTOMER_ID = ? " : "")
				+ (customerAcctId != -1 ? "AND SO.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND SO.DELIVERY_DATE BETWEEN ? AND ? " : "")
				+ ") AS RETENTION_COST_TBL "
				+ "HAVING ROUND(SI_RETENTION + AC_RETENTION - COLLECTED_PR, 2)  > 0");
		RetentionCostsHandler handler = new RetentionCostsHandler(companyId, divisionId, customerId, customerAcctId,
				dateFrom, dateTo, asOfDate);
		return getAllAsPage(sql.toString(), pageSetting, handler);
	}

	private static class RetentionCostsHandler implements QueryResultHandler<RetentionCostDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer customerId;
		private Integer customerAcctId;
		private Date dateFrom;
		private Date dateTo;
		private Date asOfDate;

		private RetentionCostsHandler (Integer companyId, Integer divisionId, Integer customerId,
				Integer customerAcctId, Date dateFrom, Date dateTo, Date asOfDate) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.customerId = customerId;
			this.customerAcctId = customerAcctId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.asOfDate = asOfDate;
		}

		@Override
		public List<RetentionCostDto> convert(List<Object[]> queryResult) {
			List<RetentionCostDto> dtos = new ArrayList<RetentionCostDto>();
			RetentionCostDto dto = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				dto = new RetentionCostDto();
				dto.setSoId((Integer) row[index]);
				dto.setDivision((String) row[++index]);
				dto.setCustomerAcct((String) row[++index]);
				dto.setCustomer((String) row[++index]);
				dto.setSoNumber((Integer) row[++index]);
				dto.setPoNumber((String) row[++index]);
				dto.setDeliveryDate((Date) row[++index]);
				dto.setAriNumber((String) row[++index]);
				Double ariRetention = (Double) row[++index];
				dto.setArReceiptNo((String) row[++index]);
				Double receiptRetention = (Double) row[++index];
				Double totalCollected = (Double) row[++index];
				dto.setDueDate((Date) row[++index]);
				dto.setAriRetention(ariRetention);
				dto.setArReceiptRetention(receiptRetention);
				Double totalRetention = ariRetention + receiptRetention;
				dto.setTotalRetention(totalRetention);
				dto.setTotalCollected(totalCollected);
				dto.setBalance(totalRetention - totalCollected);
				dtos.add(dto);
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int noOfTbls = 2;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				if(divisionId != -1) {
					query.setParameter(++index, divisionId);
				}
				if(customerId != null) {
					query.setParameter(++index, customerId);
				}
				if(customerAcctId != -1) {
					query.setParameter(++index, customerAcctId);
				}
				if(dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if(asOfDate != null) {
					if(i+1 == noOfTbls) {//only filter as of date of the third query.
						query.setParameter(++index, asOfDate);
					}
				}
				if (i < (noOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("SO_ID", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCT", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("SO_NO", Hibernate.INTEGER);
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("DELIVERY_DATE", Hibernate.DATE);
			query.addScalar("SI_NO", Hibernate.STRING);
			query.addScalar("SI_RETENTION", Hibernate.DOUBLE);
			query.addScalar("AC_NO", Hibernate.STRING);
			query.addScalar("AC_RETENTION", Hibernate.DOUBLE);
			query.addScalar("COLLECTED_PR", Hibernate.DOUBLE);
			query.addScalar("DUE_DATE", Hibernate.DATE);
		}
	}
}
