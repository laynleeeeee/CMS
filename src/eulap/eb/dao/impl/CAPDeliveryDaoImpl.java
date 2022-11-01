package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
import eulap.eb.dao.CAPDeliveryDao;
import eulap.eb.domain.hibernate.CAPDelivery;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentType;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ArTransactionRegisterDto;

/**
 * DAO Implementation layer of {@link CAPDeliveryDao}

 *
 */
public class CAPDeliveryDaoImpl extends BaseDao<CAPDelivery> implements CAPDeliveryDao {

	@Override
	protected Class<CAPDelivery> getDomainClass() {
		return CAPDelivery.class;
	}

	@Override
	public Page<CAPDelivery> getCAPDeliveries(final ApprovalSearchParam param, final List<Integer> formStatusIds,
			final Integer typeId, final PageSetting pageSetting) {

		HibernateCallback<Page<CAPDelivery>> hibernateCallback = new HibernateCallback<Page<CAPDelivery>>() {

			@Override
			public Page<CAPDelivery> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria deliveryCrit = session.createCriteria(CAPDelivery.class);
				SearchCommonUtil.searchCommonParams(deliveryCrit, null, "companyId",
						CAPDelivery.FIELD.deliveryDate.name(), CAPDelivery.FIELD.deliveryDate.name(),
						CAPDelivery.FIELD.deliveryDate.name(), param.getUser().getCompanyIds(), param);
				if(param.getSearchCriteria() != null) {
					Criterion invoiceCrit = Restrictions.like(CAPDelivery.FIELD.salesInvoiceNo.name(), "%"+param.getSearchCriteria().trim()+"%");
					if(StringFormatUtil.isNumeric(param.getSearchCriteria())) {
						deliveryCrit.add(Restrictions.or(invoiceCrit,
								Restrictions.sqlRestriction("CAPD_NUMBER LIKE ?", param.getSearchCriteria().trim(), Hibernate.STRING)));
					} else {
						deliveryCrit.add(invoiceCrit);
					}
				}
		
				DetachedCriteria workflowCrit = DetachedCriteria.forClass(FormWorkflow.class);
				workflowCrit.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				if (formStatusIds.size() > 0) {
					addAsOrInCritiria(workflowCrit, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				deliveryCrit.add(Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentTypeId.name(), typeId));
				deliveryCrit.add(Subqueries.propertyIn(CAPDelivery.FIELD.formWorkflowId.name(), workflowCrit));
				deliveryCrit.addOrder(Order.desc(CAPDelivery.FIELD.deliveryDate.name()));
				deliveryCrit.addOrder(Order.desc(CAPDelivery.FIELD.capdNumber.name()));
				return getAll(deliveryCrit, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<CAPDelivery> searchDeliveries(String criteria, int typeId, PageSetting pageSetting) {
		DetachedCriteria deliveryCrit = getDetachedCriteria();
		deliveryCrit.add(Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentTypeId.name(), typeId));
		if(StringFormatUtil.isNumeric(criteria)) {
			deliveryCrit.add(Restrictions.sqlRestriction("CAPD_NUMBER LIKE ?",
					criteria.trim(), Hibernate.STRING));
		}
		return getAll(deliveryCrit, pageSetting);
	}

	@Override
	public Page<ArTransactionRegisterDto> generatePAIDRegister(int companyId,
			int arCustomerId, int arCustomerAccountId, Date dateFrom,
			Date dateTo, int statusId, PageSetting pageSetting) {
		String sql = "SELECT DELIVERY_DATE, ARC.NAME AS CUSTOMER, ARCA.NAME AS CUSTOMER_ACCT, "
				+ "SALES_INVOICE_NO, SUM(AMOUNT) + COALESCE((SELECT SUM(AMOUNT) FROM CAP_DELIVERY_AR_LINE CAPDL "
				+ "WHERE CAPDL.CAP_DELIVERY_ID = CAPD.CAP_DELIVERY_ID GROUP BY CAPD.CAP_DELIVERY_ID), 0) AS AMOUNT, "
				+ "FS.DESCRIPTION AS STATUS FROM CAP_DELIVERY CAPD "
				+ "INNER JOIN CAP_DELIVERY_ITEM CAPDI ON CAPDI.CAP_DELIVERY_ID = CAPD.CAP_DELIVERY_ID "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = CAPD.COMPANY_ID "
				+ "INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = CAPD.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ARCA ON ARCA.AR_CUSTOMER_ACCOUNT_ID = CAPD.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "WHERE CAPD.COMPANY_ID = ? "
				+ "AND DELIVERY_DATE BETWEEN ? AND ? "
				+ (arCustomerId != -1 ? "AND CAPD.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAccountId != -1 ? "AND CAPD.AR_CUSTOMER_ACCOUNT_ID = ? " : "") 
				+ (statusId != -1 ? "AND FW.CURRENT_STATUS_ID = ? " : "")
				+ "GROUP BY CAPD.CAP_DELIVERY_ID ORDER BY CAPD.DELIVERY_DATE ASC, CAPD.SALES_INVOICE_NO";
		PIADRegisterHandler piadRegHandler = new PIADRegisterHandler(companyId, arCustomerId, arCustomerAccountId, 
				statusId, dateFrom, dateTo);
		return getAllAsPage(sql, pageSetting, piadRegHandler);
	}

	private static class PIADRegisterHandler implements QueryResultHandler<ArTransactionRegisterDto> {
		private int companyId;
		private int customerId;
		private int customerAcctId;
		private int statusId;
		private Date dateFrom;
		private Date dateTo;

		private PIADRegisterHandler (int companyId, int customerId, int customerAcctId,
				int statusId, Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.customerId = customerId;
			this.customerAcctId = customerAcctId;
			this.statusId = statusId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<ArTransactionRegisterDto> convert(List<Object[]> queryResult) {
			List<ArTransactionRegisterDto> registerRptData = new ArrayList<ArTransactionRegisterDto>();
			ArTransactionRegisterDto capd = null;
			int index = 0;
			for (Object[] row : queryResult) {
				index = 0;
				capd = new ArTransactionRegisterDto();
				capd.setGlDate((Date) row[index]);
				capd.setCustomerName((String) row[++index]);
				capd.setCustomerAcctName((String) row[++index]);
				capd.setRefNo((String) row[++index]);
				capd.setAmount((Double) row[++index]);
				capd.setStatus((String) row[++index]);
				registerRptData.add(capd);
			}
			return registerRptData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			query.setParameter(++index, dateFrom);
			query.setParameter(++index, dateTo);
			if(customerId != -1) {
				query.setParameter(++index, customerId); //optional
			}
			if(customerAcctId != -1) {
				query.setParameter(++index, customerAcctId); //optional
			}
			if(statusId != -1) {
				query.setParameter(++index, statusId); //optional
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DELIVERY_DATE", Hibernate.DATE);
			query.addScalar("CUSTOMER", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCT", Hibernate.STRING);
			query.addScalar("SALES_INVOICE_NO", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
		}
	}

	@Override
	public int generateSequenceNo(int typeId, int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(CAPDelivery.FIELD.capdNumber.name()));
		dc.add(Restrictions.eq(CAPDelivery.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentTypeId.name(), typeId));
		return generateSeqNo(dc);
	}

	@Override
	public double getTotalDeliveredTAmt(Integer arTransactionId) {
		StringBuffer sql = new StringBuffer("SELECT ID, COALESCE(SUM(AMOUNT), 0) AS AMOUNT FROM ("
				+ "SELECT CAPD.CAP_DELIVERY_ID AS ID, CAPDI.AMOUNT FROM CAP_DELIVERY_ITEM CAPDI "
				+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID "
				+ "INNER JOIN CAP_DELIVERY_TRANSACTION CDT ON CDT.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "WHERE CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 4 AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND CDT.AR_TRANSACTION_ID = "+arTransactionId+" ");
		sql.append("UNION ALL "
				+ "SELECT CAPD.CAP_DELIVERY_ID AS ID, OC.AMOUNT FROM CAP_DELIVERY_AR_LINE OC "
				+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = OC.CAP_DELIVERY_ID "
				+ "INNER JOIN CAP_DELIVERY_TRANSACTION CDT ON CDT.CAP_DELIVERY_ID = OC.CAP_DELIVERY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "WHERE CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 4 AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND CDT.AR_TRANSACTION_ID = "+arTransactionId+" ");
		sql.append(") AS TOTAL_SALES ");
		List<Double> totalAmt = getTotalAmt(sql.toString(), arTransactionId);
		return totalAmt.get(0);
	}

	private List<Double> getTotalAmt(String sql, final int capDeliveryId) {
		Collection<Double> capDeliveryAmt = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {
					ret.add((Double) row[1]); //Expecting only 1 row.
					break;
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				return -1;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ID", Hibernate.INTEGER);
				query.addScalar("AMOUNT", Hibernate.DOUBLE);
			}
		});

		return (List<Double>) capDeliveryAmt;
	}

	@Override
	public double getTotalDeliveredWipsoAmt(Integer arTransactionId, int capDeliveryId) {
		StringBuffer sql = new StringBuffer("SELECT ID, COALESCE(SUM(AMOUNT), 0) AS AMOUNT FROM ("
				+ "SELECT CAPD.CAP_DELIVERY_ID AS ID, CAPDI.AMOUNT FROM CAP_DELIVERY_ITEM CAPDI "
				+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID "
				+ "INNER JOIN CAP_DELIVERY_TRANSACTION CDT ON CDT.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "WHERE CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 5 AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND CDT.AR_TRANSACTION_ID = "+arTransactionId+" "
				+ "AND CAPD.CAP_DELIVERY_ID != " + capDeliveryId + " ");
		sql.append("UNION ALL "
				+ "SELECT CAPD.CAP_DELIVERY_ID AS ID, OC.AMOUNT FROM CAP_DELIVERY_AR_LINE OC "
				+ "INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = OC.CAP_DELIVERY_ID "
				+ "INNER JOIN CAP_DELIVERY_TRANSACTION CDT ON CDT.CAP_DELIVERY_ID = OC.CAP_DELIVERY_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID "
				+ "WHERE CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 5 AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND CDT.AR_TRANSACTION_ID = "+arTransactionId+" "
				+ "AND CAPD.CAP_DELIVERY_ID != " + capDeliveryId + " ");
		sql.append(") AS TOTAL_SALES ");
		List<Double> totalAmt = getTotalAmt(sql.toString(), arTransactionId);
		return totalAmt.get(0);
	}

	@Override
	public List<CAPDelivery> getDeliveryByCapId(int customerAdvancePaymentId) {
		DetachedCriteria deliveryCrit = getDetachedCriteria();
		deliveryCrit.add(Restrictions.or(Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentTypeId.name(), CustomerAdvancePaymentType.RETAIL),
				Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentTypeId.name(), CustomerAdvancePaymentType.INDIV_SELECTION)));
		deliveryCrit.add(Restrictions.eq(CAPDelivery.FIELD.customerAdvancePaymentId.name(), customerAdvancePaymentId));
		deliveryCrit.createAlias("formWorkflow", "fw");
		deliveryCrit.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		return getAll(deliveryCrit);
	}
}
