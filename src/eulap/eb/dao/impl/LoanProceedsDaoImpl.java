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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.LoanProceedsDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.LoanProceeds;
import eulap.eb.domain.hibernate.Payroll;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.LoanAcctHistoryDto;
import eulap.eb.web.dto.LoanProceedsDto;

/**
 * DAO Implementation class of {@link LoanProceedsDao}

 *
 */
public class LoanProceedsDaoImpl extends BaseDao<LoanProceeds> implements LoanProceedsDao{

	@Override
	protected Class<LoanProceeds> getDomainClass() {
		return LoanProceeds.class;
	}

	@Override
	public Page<LoanProceeds> getAllLoanProceeds(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final PageSetting pageSetting, final int typeId) {
		HibernateCallback<Page<LoanProceeds>> hibernateCallback = new HibernateCallback<Page<LoanProceeds>>() {
			@Override
			public Page<LoanProceeds> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria lpCriteria = session.createCriteria(LoanProceeds.class);

				User user = searchParam.getUser();
				List<Integer> userCompanyIds = user.getCompanyIds();
				if(typeId != 0) {
					lpCriteria.add(Restrictions.eq(LoanProceeds.FIELD.loanProceedsTypeId.name(), typeId));
				}
				if (!userCompanyIds.isEmpty()) {
					// The user has now company assignment.
					// No company assignment is assume to have access to all companies
					lpCriteria.add(Restrictions.in(LoanProceeds.FIELD.companyId.name(), userCompanyIds));
				}

				SearchCommonUtil.searchCommonParams(lpCriteria, null, "companyId",
						LoanProceeds.FIELD.date.name(), LoanProceeds.FIELD.glDate.name(),
						LoanProceeds.FIELD.glDate.name(), searchParam.getUser().getCompanyIds(), searchParam);

				String criteria = searchParam.getSearchCriteria();
				if(!criteria.isEmpty()) {
					if (StringFormatUtil.isNumeric(criteria)) {
						lpCriteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", "%" + criteria.trim() + "%", Hibernate.STRING));
					}
				}

				// Workflow status
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0) {
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(),formStatusIds);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				lpCriteria.add(Subqueries.propertyIn(Payroll.FIELD.formWorkflowId.name(), dcWorkflow));

				lpCriteria.addOrder(Order.desc(LoanProceeds.FIELD.date.name()));
				lpCriteria.addOrder(Order.desc(LoanProceeds.FIELD.createdDate.name()));
				return getAll(lpCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<LoanProceeds> searchLoanProceeds(Integer typeId, String criteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(LoanProceeds.FIELD.loanProceedsTypeId.name(), typeId));
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		return getAll(dc, pageSetting);
	}

	@Override
	public List<LoanAcctHistoryDto> getLoanAcctHistoryData(int companyId, int divisionId,
			int supplierId, int supplierAcctId, Date dateFrom, Date dateTo) {
		List<LoanAcctHistoryDto> loanAcctHistoryDtos = new ArrayList<LoanAcctHistoryDto>();
		List<Object> objects = executeSP("GET_LOAN_ACCOUNT_HISTORY", companyId, divisionId, supplierId, supplierAcctId, dateFrom, dateTo);
		if (objects != null && !objects.isEmpty()) {
			LoanAcctHistoryDto dto = null;
			double balance = 0;
			for (Object obj : objects) {
				Object[] row = (Object[]) obj;
				dto = new LoanAcctHistoryDto();
				dto.setDivision((String) row[0]);
				dto.setDate((Date) row[1]);
				dto.setRefNo((String) row[2]);
				dto.setCheckNo((String) row[3]);
				dto.setDescription((String) row[4]);
				double loanAmount = NumberFormatUtil.convertBigDecimalToDouble(row[5]);
				dto.setLoanAmount(loanAmount);
				double principalAmt = NumberFormatUtil.convertBigDecimalToDouble(row[6]);
				dto.setPrincipal(principalAmt);
				dto.setInterest(NumberFormatUtil.convertBigDecimalToDouble(row[7]));
				dto.setTotalPayment(NumberFormatUtil.convertBigDecimalToDouble(row[8]));
				balance += (loanAmount - principalAmt);
				dto.setBalance(balance);
				loanAcctHistoryDtos.add(dto);
			}
		}
		return loanAcctHistoryDtos;
	}

	public Page<LoanProceedsDto> getReferenceLoanProceeds(Integer companyId, Integer divisionId, Integer supplierid,
			Integer supplierAcctId, Date dateFrom, Date dateTo, Integer statusId, Integer lpNumber, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT ID, DATE, LP_NO, SUPPLIER_ID, SUPPLIER_ACCOUNT_ID, SUM(LOAN) AS TOTAL_LOAN, "
				+ "SUM(PAID_LOAN) AS TOTAL_PAID_LOAN, CLEARED_DATE, SUPPLIER_NAME, SUPPLIER_ACCT_NAME FROM ( "
				+ "SELECT LP.LOAN_PROCEEDS_ID AS ID, LP.DATE, LP.SEQUENCE_NO AS LP_NO, LP.SUPPLIER_ID, LP.SUPPLIER_ACCOUNT_ID, "
				+ "LP.AMOUNT AS LOAN, 0 AS PAID_LOAN, DATE(FWL.CREATED_DATE) AS CLEARED_DATE, "
				+ "S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCT_NAME "
				+ "FROM LOAN_PROCEEDS LP "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = LP.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = LP.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = LP.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_WORKFLOW_LOG FWL ON (FW.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = 16) "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND LP.COMPANY_ID = ? "
				+ (divisionId != null ? "AND LP.DIVISION_ID = ? " : "")
				+ (supplierid != null ? "AND LP.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND LP.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (lpNumber != null ? "AND LP.SEQUENCE_NO = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND LP.DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT LP.LOAN_PROCEEDS_ID AS ID, LP.DATE, LP.SEQUENCE_NO AS LP_NO, LP.SUPPLIER_ID, LP.SUPPLIER_ACCOUNT_ID, "
				+ "0 AS LOAN, API.PRINCIPAL_PAYMENT AS PAID_LOAN, NULL AS CLEARED_DATE, '' AS SUPPLIER_NAME, '' AS SUPPLIER_ACCT_NAME "
				+ "FROM AP_INVOICE API "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN LOAN_PROCEEDS LP ON LP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (49, 50, 51, 52, 53, 54) "
				+ "AND OTO.OR_TYPE_ID = 24009 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND LP.COMPANY_ID = ? "
				+ (divisionId != null ? "AND LP.DIVISION_ID = ? " : "")
				+ (supplierid != null ? "AND LP.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND LP.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (lpNumber != null ? "AND LP.SEQUENCE_NO = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND LP.DATE BETWEEN ? AND ? " : "")
				+ ") AS LOAN_REF GROUP BY ID ");
		if (statusId == SalesOrder.STATUS_UNUSED) {
			sql.append("HAVING TOTAL_PAID_LOAN = 0 ");
		} else if(statusId == SalesOrder.STATUS_USED) {
			sql.append("HAVING TOTAL_PAID_LOAN != 0 AND TOTAL_LOAN - TOTAL_PAID_LOAN > 0 ");
		} else {
			sql.append("HAVING TOTAL_PAID_LOAN != 0 OR TOTAL_LOAN - TOTAL_PAID_LOAN > 0 ");
		}
		sql.append("ORDER BY DATE DESC, LP_NO DESC ");
		return getAllAsPage(sql.toString(), pageSetting, new LpReferenceHandler(companyId, divisionId, supplierid, 
				supplierAcctId, lpNumber, dateFrom, dateTo));
	}

	private static class LpReferenceHandler implements QueryResultHandler<LoanProceedsDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer supplierid;
		private Integer supplierAcctId;
		private Integer lpNumber;
		private Date dateFrom;
		private Date dateTo;

		private LpReferenceHandler(Integer companyId, Integer divisionId, Integer supplierid,
				Integer supplierAcctId, Integer lpNumber, Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierid = supplierid;
			this.supplierAcctId = supplierAcctId;
			this.lpNumber = lpNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<LoanProceedsDto> convert(List<Object[]> queryResult) {
			List<LoanProceedsDto> lps = new ArrayList<LoanProceedsDto>();
			LoanProceedsDto lp = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				lp = new LoanProceedsDto();
				lp.setLoanProceedsId((Integer) row[index]);
				lp.setDate((Date) row[++index]);
				lp.setSequenceNumber((Integer) row[++index]);
				lp.setSupplierId((Integer) row[++index]);
				lp.setSupplierAccountId((Integer) row[++index]);
				lp.setDateCleared((Date) row[++index]);
				lp.setSupplierName((String) row[++index]);
				lp.setSupplierAccountName((String) row[++index]);
				lps.add(lp);
			}
			return lps;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numberOfTbls = 2;
			for (int i = 0; i < numberOfTbls; i++) {
				query.setParameter(index, companyId);
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if(supplierid != null) {
					query.setParameter(++index, supplierid);
				}
				if(supplierAcctId != null) {
					query.setParameter(++index, supplierAcctId);
				}
				if(lpNumber != null) {
					query.setParameter(++index, lpNumber);
				}
				if(dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (numberOfTbls-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("LP_NO", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("CLEARED_DATE", Hibernate.DATE);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCT_NAME", Hibernate.STRING);
		}
	}

	@Override
	public List<LoanAcctHistoryDto> getLoanBalancesSummaryData(int companyId, int divisionId,
			int balanceOption, Date asOfDate) {
		List<LoanAcctHistoryDto> loanBalances = new ArrayList<LoanAcctHistoryDto>();
		List<Object> objects = executeSP("GET_LOAN_BALANCE_SUMMARY", companyId, divisionId, balanceOption, asOfDate);
		if (objects != null && !objects.isEmpty()) {
			LoanAcctHistoryDto dto = null;
			for (Object obj : objects) {
				Object[] row = (Object[]) obj;
				dto = new LoanAcctHistoryDto();
				dto.setSupplier((String) row[0]);
				dto.setDate((Date) row[1]);
				dto.setRefNo((String) row[2]);
				dto.setDescription((String) row[3]);
				dto.setLoanAmount(NumberFormatUtil.convertBigDecimalToDouble(row[4]));
				dto.setPrincipal(NumberFormatUtil.convertBigDecimalToDouble(row[5]));
				dto.setInterest(NumberFormatUtil.convertBigDecimalToDouble(row[6]));
				dto.setTotalPayment(NumberFormatUtil.convertBigDecimalToDouble(row[7]));
				dto.setBalance(NumberFormatUtil.convertBigDecimalToDouble(row[8]));
				loanBalances.add(dto);
			}
		}
		return loanBalances;
	}

	@Override
	public int generateLpSequenceNo(int companyId, int typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(LoanProceeds.FIELD.sequenceNumber.name()));
		dc.add(Restrictions.eq(LoanProceeds.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(LoanProceeds.FIELD.loanProceedsTypeId.name(), typeId));
		return generateSeqNo(dc);
	}
}
