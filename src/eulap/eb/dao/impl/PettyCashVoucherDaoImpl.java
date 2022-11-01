package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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
import eulap.eb.dao.PettyCashVoucherDao;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PettyCashVoucher;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.report.DailyPettyCashFundReportParam;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.DailyPettyCashFundReportDto;
import eulap.eb.web.dto.PettyCashVoucherDto;
import eulap.eb.web.dto.UnliquidatedPCVAgingDto;

/**
 * DAO Implementation class of {@link PettyCashVoucher}

 *
 */
public class PettyCashVoucherDaoImpl extends BaseDao<PettyCashVoucher> implements PettyCashVoucherDao {

	@Override
	protected Class<PettyCashVoucher> getDomainClass() {
		return PettyCashVoucher.class;
	}

	@Override
	public Integer generateSequenceNo(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(PettyCashVoucher.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(PettyCashVoucher.FIELD.companyId.name(), companyId));
		if(divisionId != null) {
			dc.add(Restrictions.eq(PettyCashVoucher.FIELD.divisionId.name(), divisionId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public List<PettyCashVoucher> searchPettyCashVouchers(final int divisionId, final String searchCriteria, final User user) {
		return getHibernateTemplate().execute(new HibernateCallback<List<PettyCashVoucher>>() {
			@Override
			public List<PettyCashVoucher> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(PettyCashVoucher.class);
				if(StringFormatUtil.isNumeric(searchCriteria)){
					criteria.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchCriteria, Hibernate.STRING));
				}
				if(!user.getCompanyIds().isEmpty()){
					criteria.add(Restrictions.in(PettyCashVoucher.FIELD.companyId.name(), user.getCompanyIds()));
				}
				criteria.add(Restrictions.eq(PettyCashVoucher.FIELD.divisionId.name(), divisionId));
				criteria.addOrder(Order.asc(PettyCashVoucher.FIELD.sequenceNo.name()));
				return getAllByCriteria(criteria);
			}
		});
	}

	@Override
	public Page<PettyCashVoucher> getPettyCashVouchers(int typeId, ApprovalSearchParam searchParam,
			List<Integer> statuses, PageSetting pageSetting) {
		HibernateCallback<Page<PettyCashVoucher>> hibernateCallback = new HibernateCallback<Page<PettyCashVoucher>>() {

			@Override
			public Page<PettyCashVoucher> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(PettyCashVoucher.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						PettyCashVoucher.FIELD.pcvDate.name(), PettyCashVoucher.FIELD.pcvDate.name(),
						PettyCashVoucher.FIELD.pcvDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(statuses.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(PettyCashVoucher.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.add(Restrictions.eq(PettyCashVoucher.FIELD.divisionId.name(), typeId));
				dc.addOrder(Order.desc(PettyCashVoucher.FIELD.pcvDate.name()));
				dc.addOrder(Order.desc(PettyCashVoucher.FIELD.sequenceNo.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Page<PettyCashVoucherDto> getPettyCashVoucherRegisterData(Integer companyId, Integer divisionId,
			Integer custodianId, String requestor, Date dateFrom, Date dateTo, int statusId,
			PageSetting pageSetting) {
		String sql = "SELECT D.NAME AS DIVISION, PCV.PCV_DATE, PCV.SEQUENCE_NO AS PCV_NO, "
				+ "CA.CUSTODIAN_NAME AS CUSTODIAN, PCV.REQUESTOR, PCV.REFERENCE_NO AS REFERENCE, "
				+ "PCV.DESCRIPTION AS DESCRIPTION, PCV.AMOUNT, FS.DESCRIPTION AS STATUS, "
				+ "IF(FW.CURRENT_STATUS_ID = 4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL "
				+ "WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = 4), '') AS CANCELLATION_REMARKS "
				+ "FROM PETTY_CASH_VOUCHER PCV "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = PCV.DIVISION_ID "
				+ "INNER JOIN USER_CUSTODIAN UC ON UC.USER_CUSTODIAN_ID = PCV.USER_CUSTODIAN_ID "
				+ "INNER JOIN CUSTODIAN_ACCOUNT CA ON CA.CUSTODIAN_ACCOUNT_ID = UC.CUSTODIAN_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PCV.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "WHERE PCV.COMPANY_ID = ? "
				+ "AND PCV.PCV_DATE BETWEEN ? AND ? ";
		if (divisionId > 0) {
			sql += "AND PCV.DIVISION_ID = ? ";
		}
		if (custodianId > 0) {
			sql += "AND PCV.USER_CUSTODIAN_ID = ? ";
		}
		if (!requestor.isEmpty()) {
			sql += "AND PCV.REQUESTOR LIKE ? ";
		}
		if (statusId > 0) {
			sql += "AND FW.CURRENT_STATUS_ID = ? ";
		}
		sql += "ORDER BY CA.CUSTODIAN_ACCOUNT_NAME ";
		return getAllAsPage(sql, pageSetting, new pcvRegisterHandler(companyId, divisionId,
				custodianId, requestor, dateFrom, dateTo, statusId));
	}

	private static class pcvRegisterHandler implements QueryResultHandler<PettyCashVoucherDto> {
		private int companyId;
		private int divisionId;
		private int custodianId;
		private String requestor;
		private Date dateFrom;
		private Date dateTo;
		private int statusId;

		private pcvRegisterHandler(Integer companyId, Integer divisionId,
				Integer custodianId, String requestor, Date dateFrom, Date dateTo, int statusId) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.custodianId = custodianId;
			this.requestor = requestor;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.statusId = statusId;
		}

		@Override
		public List<PettyCashVoucherDto> convert(List<Object[]> queryResult) {
			List<PettyCashVoucherDto> pcvRegisterData = new ArrayList<PettyCashVoucherDto>();
			PettyCashVoucherDto dto = null;
			for (Object[] rowResult : queryResult) {
				int counter = 0;
				dto = new PettyCashVoucherDto();
				dto.setDivision((String) rowResult[counter++]);
				dto.setPcvDate((Date) rowResult[counter++]);
				dto.setPcvNo((String) rowResult[counter++]);
				dto.setCustodianAcctName((String) rowResult[counter++]);
				dto.setRequestor((String) rowResult[counter++]);
				dto.setReference((String) rowResult[counter++]);
				dto.setDescription((String) rowResult[counter++]);
				dto.setRqstdAmount((Double) rowResult[counter++]);
				dto.setTransStatus((String) rowResult[counter++]);
				dto.setCancellationRemarks((String) rowResult[counter++]);
				pcvRegisterData.add(dto);
				// free up memory
				dto = null;
			}
			return pcvRegisterData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			query.setParameter(++index, dateFrom);
			query.setParameter(++index, dateTo);
			if (divisionId > 0) {
				query.setParameter(++index, divisionId);
			}
			if (custodianId > 0) {
				query.setParameter(++index, custodianId);
			}
			if (!requestor.isEmpty()) {
				query.setParameter(++index, StringFormatUtil.appendWildCard(requestor));
			}
			if (statusId > 0) {
				query.setParameter(++index, statusId);
			}

			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("PCV_DATE", Hibernate.DATE);
			query.addScalar("PCV_NO", Hibernate.STRING);
			query.addScalar("CUSTODIAN", Hibernate.STRING);
			query.addScalar("REQUESTOR", Hibernate.STRING);
			query.addScalar("REFERENCE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public Page<DailyPettyCashFundReportDto> searchDailyPettyCashFundReport(DailyPettyCashFundReportParam param, PageSetting pageSetting) {
		return executePagedSP("GET_DAILY_PETTY_CASH_FUND_REPORT", pageSetting, new DailyPettyCashFundReportHandler(),
				param.getCompanyId(), param.getDivisionId(), param.getCustodianId(), param.getDate(),
				param.getTransactionStatusId());
	}

	private static class DailyPettyCashFundReportHandler implements QueryResultHandler<DailyPettyCashFundReportDto> {
		@Override
		public List<DailyPettyCashFundReportDto> convert(List<Object[]> queryResult) {
			List<DailyPettyCashFundReportDto> dailyPettyCashFundReport = new ArrayList<DailyPettyCashFundReportDto>();
			DailyPettyCashFundReportDto dailyPettyCashFundReportdto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				dailyPettyCashFundReportdto = new DailyPettyCashFundReportDto();
				dailyPettyCashFundReportdto.setDivision((String) rowResult[colNum]);
				dailyPettyCashFundReportdto.setCustodianName((String) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setPcvNumber((Integer) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setRequestor((String) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setReference((String) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setDescription((String) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setAmount((double) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setStatus((String) rowResult[++colNum]);
				dailyPettyCashFundReportdto.setTotalLiquidation((double) rowResult[++colNum]);
				dailyPettyCashFundReport.add(dailyPettyCashFundReportdto);
			}
			return dailyPettyCashFundReport;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("CUSTODIAN_NAME", Hibernate.STRING);
			query.addScalar("PCV_NUMBER", Hibernate.INTEGER);
			query.addScalar("REQUESTOR", Hibernate.STRING);
			query.addScalar("REFERENCE_NO", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("STATUS", Hibernate.STRING);
			query.addScalar("CURRENT_STATUS_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_LIQUIDATION", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<UnliquidatedPCVAgingDto> getUnliquidatedPCVAgingData(Integer companyId, Integer divisionId,
			Integer custodianId, String requestor, Date asOfDate, PageSetting pageSetting) {
		UnliquidatedpcvRegisterHandler handler = new UnliquidatedpcvRegisterHandler();
		return executePagedSP("GET_UNLIQUIDATED_PETTY_CASH_VOUCHER", pageSetting, handler, companyId,
				divisionId, custodianId, StringFormatUtil.appendWildCard(requestor), asOfDate);
	}

	private static class UnliquidatedpcvRegisterHandler implements QueryResultHandler<UnliquidatedPCVAgingDto> {
		private Logger handlerLogger = Logger.getLogger(pcvRegisterHandler.class);

		public UnliquidatedpcvRegisterHandler() {
		}

		@Override
		public List<UnliquidatedPCVAgingDto> convert(List<Object[]> queryResult) {
			handlerLogger.info("Converting the retrieved data to list.");
			List<UnliquidatedPCVAgingDto> unliquidatedPCVAgingData = new ArrayList<UnliquidatedPCVAgingDto>();
			handlerLogger.debug("Looping through the retrieved data.");
			handlerLogger.trace("Retrieved " + queryResult.size() + " data.");
			UnliquidatedPCVAgingDto dto = null;
			for (Object[] rowResult : queryResult) {
				int counter = 0;
				dto = new UnliquidatedPCVAgingDto();
				dto.setDivision((String) rowResult[counter++]);
				dto.setPcvDate((Date) rowResult[counter++]);
				dto.setPcvNo((Integer) rowResult[counter++]);
				dto.setCustodian((String) rowResult[counter++]);
				dto.setRequestor((String) rowResult[counter++]);
				dto.setRqstdAmount((Double) rowResult[counter++]);
				dto.setRange1To3(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[counter++]));
				dto.setRange4To6(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[counter++]));
				dto.setRange7To10(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[counter++]));
				dto.setRange11ToUp(NumberFormatUtil.convertBigDecimalToDouble((Double) rowResult[counter++]));
				unliquidatedPCVAgingData.add(dto);
				// free up memory
				dto = null;
			}
			handlerLogger.info("Added " + unliquidatedPCVAgingData.size() + " records to the Unliquidated Petty Cash Voucher Register Report.");
			return unliquidatedPCVAgingData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
		}
	}
}
