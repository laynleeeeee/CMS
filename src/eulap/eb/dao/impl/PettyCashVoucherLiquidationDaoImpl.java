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
import eulap.eb.dao.PettyCashVoucherLiquidationDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
import eulap.eb.domain.hibernate.SupplierAdvancePaymentLine;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.report.PCVLiquidationRegisterParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.PettyCashVoucherLiquidationRegisterDTO;
/**
 * DAO Implementation class of {@link PettyCashVoucherLiquidation}

 *
 */
public class PettyCashVoucherLiquidationDaoImpl extends BaseDao<PettyCashVoucherLiquidation> implements PettyCashVoucherLiquidationDao{

	@Override
	protected Class<PettyCashVoucherLiquidation> getDomainClass() {
		return PettyCashVoucherLiquidation.class;
	}

	@Override
	public Integer generateSequenceNo(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(PettyCashVoucherLiquidation.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(PettyCashVoucherLiquidation.FIELD.companyId.name(), companyId));
		if(divisionId != null) {
			dc.add(Restrictions.eq(PettyCashVoucherLiquidation.FIELD.divisionId.name(), divisionId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public List<PettyCashVoucherLiquidation> searchPettyCashVoucherLiquidations(final int divisionId, final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<PettyCashVoucherLiquidation>>() {
			@Override
			public List<PettyCashVoucherLiquidation> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PettyCashVoucherLiquidation.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(PettyCashVoucherLiquidation.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.add(Restrictions.eq(PettyCashVoucherLiquidation.FIELD.divisionId.name(), divisionId));
				criteria.addOrder(Order.asc(PettyCashVoucherLiquidation.FIELD.sequenceNo.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

	@Override
	public Page<PettyCashVoucherLiquidation> getPettyCashVoucherLiquidations(int typeId, ApprovalSearchParam searchParam,
			List<Integer> statuses, PageSetting pageSetting) {
		HibernateCallback<Page<PettyCashVoucherLiquidation>> hibernateCallback = new HibernateCallback<Page<PettyCashVoucherLiquidation>>() {
			@Override
			public Page<PettyCashVoucherLiquidation> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria pcvlCriteria = session.createCriteria(PettyCashVoucherLiquidation.class);
				if (StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					pcvlCriteria.add(Restrictions.like(PettyCashVoucherLiquidation.FIELD.sequenceNo.name(),
							Integer.parseInt(searchParam.getSearchCriteria().trim())));
				}
				SearchCommonUtil.searchCommonParams(pcvlCriteria, null, "companyId",
						PettyCashVoucherLiquidation.FIELD.pcvlDate.name(), PettyCashVoucherLiquidation.FIELD.pcvlDate.name(),
						PettyCashVoucherLiquidation.FIELD.pcvlDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if (statuses.size() > 0) {
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				pcvlCriteria.add(Subqueries.propertyIn(PettyCashVoucherLiquidation.FIELD.formWorkflowId.name(), dcWorkflow));
				pcvlCriteria.add(Restrictions.eq(PettyCashVoucherLiquidation.FIELD.divisionId.name(), typeId));
				pcvlCriteria.addOrder(Order.desc(PettyCashVoucherLiquidation.FIELD.sequenceNo.name()));
				return getAll(pcvlCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<PettyCashVoucher> getPCVReferences(Integer companyId, Integer divisionId,
			Integer userCustodianId, String requestor, Integer pcvNo, Date dateFrom, Date dateTo,
			PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT PETTY_CASH_VOUCHER_ID, SEQUENCE_NO, COMPANY_ID, DIVISION_ID, USER_CUSTODIAN_ID, PCV_DATE, REQUESTOR, REFERENCE_NO, DESCRIPTION, AMOUNT "
				+ "FROM PETTY_CASH_VOUCHER PCV "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PCV.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND PCV.PETTY_CASH_VOUCHER_ID NOT IN( "
				+ "SELECT PCVL.PETTY_CASH_VOUCHER_ID FROM PETTY_CASH_VOUCHER_LIQUIDATION PCVL "
				+ "INNER JOIN FORM_WORKFLOW FW2 ON PCVL.FORM_WORKFLOW_ID = FW2.FORM_WORKFLOW_ID "
				+ "AND FW2.CURRENT_STATUS_ID != 4) "
				+ (companyId != null ? "AND PCV.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND PCV.DIVISION_ID = ? " : "")
				+ (userCustodianId != null ? "AND PCV.USER_CUSTODIAN_ID = ? " : "")
				+ (!requestor.isEmpty() ? "AND PCV.REQUESTOR LIKE ? " : "")
				+ (pcvNo != null ? "AND PCV.SEQUENCE_NO = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND PCV.PCV_DATE BETWEEN ? AND ? " : ""));
				sql.append("ORDER BY SEQUENCE_NO, PCV_DATE ");
				int noOfTbls = 1;
		return getAllAsPage(sql.toString(), pageSetting, new PettyCashVoucherReferenceHandler(companyId, divisionId, userCustodianId, requestor, pcvNo, dateFrom, dateTo, noOfTbls));
	}

		private static class PettyCashVoucherReferenceHandler implements QueryResultHandler<PettyCashVoucher>{
			private Integer companyId;
			private Integer divisionId;
			private Integer userCustodianId;
			private String requestor;
			private Integer pcvNo;
			private Date dateFrom;
			private Date dateTo;
			private Integer noOfTbls;

			private PettyCashVoucherReferenceHandler(Integer companyId, Integer divisionId,
			Integer userCustodianId, String requestor, Integer pcvNo, Date dateFrom, Date dateTo,
			Integer noOfTbls) {
				this.companyId = companyId;
				this.divisionId = divisionId;
				this.userCustodianId = userCustodianId;
				this.requestor = requestor;
				this.pcvNo = pcvNo;
				this.dateFrom = dateFrom;
				this.dateTo = dateTo;
				this.noOfTbls = noOfTbls;
			}

			@Override
			public List<PettyCashVoucher> convert(List<Object[]> queryResult) {
				List<PettyCashVoucher> pcvs = new ArrayList<PettyCashVoucher>();
				PettyCashVoucher pcv = null;
				int index;
				for(Object [] row : queryResult) {
					index = 0;
					pcv = new PettyCashVoucher();
					pcv.setId((Integer) row[index]);
					pcv.setSequenceNo((Integer) row[++index]);
					pcv.setCompanyId((Integer) row[++index]);
					pcv.setDivisionId((Integer) row[++index]);
					pcv.setUserCustodianId((Integer) row[++index]);
					pcv.setPcvDate((Date) row[++index]);
					pcv.setRequestor((String) row[++index]);
					pcv.setReferenceNo((String) row[++index]);
					pcv.setDescription((String) row[++index]);
					pcv.setAmount((Double) row[++index]);
					pcvs.add(pcv);
				}
				return pcvs;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				for (int i = 0; i < noOfTbls; i++) {
					if(companyId != null) {
						query.setParameter(index, companyId);
					}
					if (divisionId != null) {
						query.setParameter(++index, divisionId);
					}
					if (userCustodianId != null) {
						query.setParameter(++index, userCustodianId);
					}
					if (!requestor.isEmpty()) {
						query.setParameter(++index, StringFormatUtil.appendWildCard(requestor));
					}
					if (pcvNo != null) {
						query.setParameter(++index, pcvNo);
					}
					if(dateFrom != null && dateTo != null) {
						query.setParameter(++index, dateFrom);
						query.setParameter(++index, dateTo);
					}
					if (i < (noOfTbls-1)) {
						++index;
					}
				}
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("PETTY_CASH_VOUCHER_ID", Hibernate.INTEGER);
				query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
				query.addScalar("COMPANY_ID", Hibernate.INTEGER);
				query.addScalar("DIVISION_ID", Hibernate.INTEGER);
				query.addScalar("USER_CUSTODIAN_ID", Hibernate.INTEGER);
				query.addScalar("PCV_DATE", Hibernate.DATE);
				query.addScalar("REQUESTOR", Hibernate.STRING);
				query.addScalar("REFERENCE_NO", Hibernate.STRING);
				query.addScalar("DESCRIPTION", Hibernate.STRING);
				query.addScalar("AMOUNT", Hibernate.DOUBLE);
			}
		}


	@Override
	public List<PettyCashVoucherLiquidation> getAssociatedLiquidations(Integer pcvId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(PettyCashVoucherLiquidation.FIELD.pettyCashVoucherId.name(), pcvId));

		// Payment workflow
		DetachedCriteria formWorkflowDC = DetachedCriteria.forClass(FormWorkflow.class);
		formWorkflowDC.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		formWorkflowDC.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(PettyCashVoucherLiquidation.FIELD.formWorkflowId.name(), formWorkflowDC));
		return getAll(dc);
	}

	@Override
	public Page<PettyCashVoucherLiquidationRegisterDTO> getPettyCashVoucherLiquidationRegister(
			PCVLiquidationRegisterParam param, PageSetting pageSetting) {
		pcvLiquidationRegisterHandler handler = new pcvLiquidationRegisterHandler();
		return executePagedSP("GET_PETTY_CASH_LIQUIDATION_REGISTER", pageSetting, handler, param.getCompanyId(),
				param.getDivisionId(), param.getCustodianId(), StringFormatUtil.appendWildCard(param.getRequestorName()),
				param.getDateFrom(), param.getDateTo(), param.getTransactionStatusId());
	}
	private static class pcvLiquidationRegisterHandler implements QueryResultHandler<PettyCashVoucherLiquidationRegisterDTO>{
		@Override
		public List<PettyCashVoucherLiquidationRegisterDTO> convert(List<Object[]> queryResult) {
			List<PettyCashVoucherLiquidationRegisterDTO> pcvLiquidationRegisterItems = new ArrayList<PettyCashVoucherLiquidationRegisterDTO>();
			PettyCashVoucherLiquidationRegisterDTO pcvlRegisterdto = null;
			for (Object[] rowResult : queryResult) {
				pcvlRegisterdto = new PettyCashVoucherLiquidationRegisterDTO();
				pcvlRegisterdto.setPcvDate((Date)rowResult[0]);
				pcvlRegisterdto.setDivisionName((String)rowResult[1]);
				pcvlRegisterdto.setPcvNo((Integer)rowResult[2]);
				pcvlRegisterdto.setPcvlNo((Integer)rowResult[3]);
				pcvlRegisterdto.setCustodianName((String)rowResult[4]);
				pcvlRegisterdto.setRequestorName((String)rowResult[5]);
				pcvlRegisterdto.setReference((String)rowResult[6]);
				pcvlRegisterdto.setAmountRequested((Double)rowResult[7]);
				pcvlRegisterdto.setTotalCashReturned((Double)rowResult[8]);
				pcvlRegisterdto.setStatus((String)rowResult[9]);
				pcvlRegisterdto.setCancellationRemarks((String)rowResult[10]);
				pcvLiquidationRegisterItems.add(pcvlRegisterdto);
			}
			return pcvLiquidationRegisterItems;
		}

		@Override
		public int setParamater(SQLQuery query) {
			// Do nothing
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("PCVL_DATE", Hibernate.DATE);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("PCV", Hibernate.INTEGER);
			query.addScalar("PCVLI", Hibernate.INTEGER);
			query.addScalar("CUSTODIAN", Hibernate.STRING);
			query.addScalar("REQUESTOR", Hibernate.STRING);
			query.addScalar("REFERENCE", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CASH_RETURNED", Hibernate.DOUBLE);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
		}
	}


	@Override
	public double getTotalPettyCashLiquidationPerDay(Integer companyId, Integer divisionId, Integer userCustodianAcctId, 
			Date date) {
		StringBuilder sql = new StringBuilder("SELECT PCVL.PETTY_CASH_VOUCHER_LIQUIDATION_ID, SUM(PCVL.AMOUNT - PCVL.CASH_RETURNED) AS AMOUNT "
				+ "FROM PETTY_CASH_VOUCHER_LIQUIDATION PCVL "
				+ "INNER JOIN FORM_WORKFLOW PCVL_FW ON PCVL_FW.FORM_WORKFLOW_ID = PCVL.FORM_WORKFLOW_ID "
				+ "WHERE PCVL.COMPANY_ID = ? "
				+ "AND PCVL.PCVL_DATE = ? "
				+ "AND PCVL_FW.IS_COMPLETE = 1 " 
				+ (divisionId != -1 ? "AND PCVL.DIVISION_ID = ? " : "")
				+ (userCustodianAcctId != -1 ? "AND PCVL.USER_CUSTODIAN_ID = ? " : ""));
		Session session = null;
		double pcvlTotalPerDay = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql.toString());
			Integer index = 0;
			query.setParameter(index, companyId);
			query.setParameter(++index, date);
			if(divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			if(userCustodianAcctId != -1) {
				query.setParameter(++index, userCustodianAcctId);
			}
			query.addScalar("PETTY_CASH_VOUCHER_LIQUIDATION_ID", Hibernate.INTEGER);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					if((Double) row[1] != null) {
						pcvlTotalPerDay = (Double) row[1];//Expecting 1 result.
					}
					break;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return pcvlTotalPerDay;
	}
}
