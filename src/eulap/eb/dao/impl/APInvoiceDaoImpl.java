package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RReturnToSupplier;
import eulap.eb.domain.hibernate.RReturnToSupplierItem;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.service.report.ApInvoiceAgingParam;
import eulap.eb.web.dto.AnnualAlphalistPayeesDetailDto;
import eulap.eb.web.dto.AnnualAlphalsitWTEDetailsDto;
import eulap.eb.web.dto.ApInvoiceAgingDto;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.COCTaxDto;
import eulap.eb.web.dto.MonthlyAlphalistPayeesDto;
import eulap.eb.web.dto.SupplierApLineDto;
import eulap.eb.web.dto.VATDeclarationDto;

/**
 * Implementation class of {@link APInvoiceDao}.

 *
 */
public class APInvoiceDaoImpl extends BaseDao<APInvoice> implements APInvoiceDao{

	@Override
	protected Class<APInvoice> getDomainClass() {
		return APInvoice.class;
	}

	@Override
	public boolean isUniqueInvoiceNo(String invoiceNumber, int supplierAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RR_TYPE_ID),
				Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_TYPE_ID)));
		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAccountId));
		dc.add(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), invoiceNumber));
		return getAll(dc).size() < 1;
	}

	@Override
	public boolean isUniqueReferenceNo(String invoiceNumber, Integer divisionId, Integer invoiceTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(APInvoice.FIELD.divisionId.name(), divisionId));
		dc.add(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), invoiceNumber));
		dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		return getAll(dc).size() < 1;
	}

	@Override
	public Collection<APInvoice> getInvoicesBySupplierAcct(int supplierAccountId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RR_TYPE_ID),
				Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_TYPE_ID)));
		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAccountId));
		return getAll(dc);
	}

	@Override
	public int generateSequenceNumber(int invoiceTypeId, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (invoiceTypeId == InvoiceType.RR_TYPE_ID || invoiceTypeId == InvoiceType.RR_RAW_MAT_TYPE_ID
				|| invoiceTypeId == InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID) {
			//Generate RR No.
			DetachedCriteria rrDc = DetachedCriteria.forClass(RReceivingReport.class);
			rrDc.setProjection(Projections.property(RReceivingReport.FIELD.apInvoiceId.name()));
			rrDc.add(Restrictions.eq(RReceivingReport.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(RReceivingReport.FIELD.id.name(), rrDc));
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		} else if (invoiceTypeId == InvoiceType.RTS_TYPE_ID) {
			//Generate RTS No.
			DetachedCriteria rtsDc = DetachedCriteria.forClass(RReturnToSupplier.class);
			rtsDc.setProjection(Projections.property(RReturnToSupplier.FIELD.apInvoiceId.name()));
			rtsDc.add(Restrictions.eq(RReturnToSupplier.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(RReturnToSupplier.FIELD.id.name(), rtsDc));
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		} else {
			//Generate AP Invoice Sequence No.
			DetachedCriteria saDc = DetachedCriteria.forClass(SupplierAccount.class);
			saDc.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
			saDc.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(APInvoice.FIELD.supplierAccountId.name(), saDc));
			if (invoiceTypeId <= InvoiceType.CREDIT_MEMO_TYPE_ID ) {
				dc.add(Restrictions.between(APInvoice.FIELD.invoiceTypeId.name(),
						InvoiceType.REGULAR_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID));
			} else {
				dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
			}
		}
		dc.setProjection(Projections.max(APInvoice.FIELD.sequenceNumber.name()));
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}


	@Override
	public Page<APInvoice> searchAPInvoice(String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.between(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.REGULAR_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID));
		dc.add(Restrictions.or(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), "%" + searchCriteria.trim() + "%"),
				Restrictions.like(APInvoice.FIELD.description.name(), "%" + searchCriteria.trim() + "%")));
		dc.addOrder(Order.asc(APInvoice.FIELD.invoiceNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<APInvoice> searchAPInvoice(String searchCriteria, PageSetting pageSetting, int divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.between(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.REGULAR_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID));
		dc.add(Restrictions.eq(APInvoice.FIELD.divisionId.name(), divisionId));
		dc.add(Restrictions.or(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), "%" + searchCriteria.trim() + "%"),
				Restrictions.like(APInvoice.FIELD.description.name(), "%" + searchCriteria.trim() + "%")));
		dc.addOrder(Order.asc(APInvoice.FIELD.invoiceNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public APInvoice getInvoice(int supplierAccountId, String invoiceNumber, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		Integer invoiceTypeId = null;
		boolean isOtherInvoice = invoiceNumber.trim().startsWith("RM IS-")
				|| invoiceNumber.trim().startsWith("RR-") || invoiceNumber.trim().startsWith("RTS-")
				|| invoiceNumber.trim().startsWith("RTS-EB-") || invoiceNumber.trim().startsWith("RM P-")
				|| invoiceNumber.trim().startsWith("AII-") || invoiceNumber.trim().startsWith("AIS-");
		if (isOtherInvoice) {
			if (invoiceNumber.trim().startsWith("RR-")) {
				invoiceNumber = (invoiceNumber.replace("RR-", "").trim()).split(" ")[0];
				invoiceTypeId = InvoiceType.RR_TYPE_ID;
			} else if (invoiceNumber.trim().startsWith("RM IS-")) {
				invoiceNumber = (invoiceNumber.replace("RM IS-", "").trim()).split(" ")[0];
				invoiceTypeId = InvoiceType.RR_RAW_MAT_TYPE_ID;
			} else if (invoiceNumber.trim().startsWith("RTS-EB-")) {
				invoiceNumber = invoiceNumber.replace("RTS-EB-", "").trim().split(" ")[0];
				invoiceTypeId = InvoiceType.RTS_EB_TYPE_ID;
			} else if (invoiceNumber.trim().startsWith("RTS-")) {
				invoiceNumber = invoiceNumber.replace("RTS-", "").trim().split(" ")[0];
				invoiceTypeId = InvoiceType.RTS_TYPE_ID;
			} else if (invoiceNumber.trim().startsWith("RM P-")) {
				invoiceNumber = invoiceNumber.replace("RM P-", "").trim().split(" ")[0];
				invoiceTypeId = InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID;
			} else if (invoiceNumber.trim().startsWith("AII-")) {
				invoiceNumber = invoiceNumber.replace("AII-", "").trim().split(" ")[0];
				invoiceTypeId = InvoiceType.INVOICE_ITEM_TYPE_ID;
			} else if (invoiceNumber.trim().startsWith("AIS-")) {
				invoiceNumber = invoiceNumber.replace("AIS-", "").trim().split(" ")[0];
				invoiceTypeId = InvoiceType.INVOICE_SERVICE_TYPE_ID;
			}
			dc.add(Restrictions.eq("sequenceNumber", Integer.valueOf(invoiceNumber)));
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		} else {
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceNumber.name(), invoiceNumber));
		}

		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAccountId));
		dc.add(Restrictions.eq(APInvoice.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	@Override
	public double getTotalDebit(int companyId, Date asOfDate, int accountId) {
		return Math.abs(getTotalAccount(true, companyId, asOfDate, accountId));
	}

	@Override
	public double getTotalCredit(int companyId, Date asOfDate, int accountId) {
		return getTotalAccount(false, companyId, asOfDate, accountId);
	}

	private Double getTotalAccount(boolean isDebit, int companyId, Date asOfDate, int accountId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (isDebit)
			dc.add(Restrictions.lt(APInvoice.FIELD.amount.name(), 0.0));
		else
			dc.add(Restrictions.gt(APInvoice.FIELD.amount.name(), 0.0));
		dc.add(Restrictions.le(APInvoice.FIELD.glDate.name(), asOfDate));
		
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		
		//Subquery to Account combination
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.accountId.name(), accountId));
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		//Subquery for Supplier Account
		DetachedCriteria supplierAcctCriteria =  DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Subqueries.propertyIn(SupplierAccount.FIELD.defaultCreditACId.name(), acctCombiCriteria));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.supplierAccountId.name(), supplierAcctCriteria));

		dc.setProjection(Projections.sum(APInvoice.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public Page<APInvoice> searchInvoices(int companyId, int invoiceTypeId, int supplierId,
			int supplierAcctId, int termId, String invoiceNumber, Date fromInvoiceDate,
			Date toInvoiceDate, Date fromGLDate, Date toGLDate, Date fromDueDate, Date toDueDate,
			Double fromAmount, Double toAmount, Integer fromSequenceNumber, Integer toSequenceNumber,
			int invoiceStatusId, int paymentStatusId, Date asOfDate, PageSetting pageSetting) {
		return searchInvoices(companyId, null, invoiceTypeId, supplierId, supplierAcctId, termId, invoiceNumber,
				fromInvoiceDate, toInvoiceDate, fromGLDate, toGLDate, fromDueDate, toDueDate, fromAmount, 
				toAmount, fromSequenceNumber, toSequenceNumber, invoiceStatusId, paymentStatusId, asOfDate, pageSetting);
	}

	public Page<APInvoice> searchInvoices(int companyId, int supplierId,
			int supplierAccountId, Date asOfDate, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RR_TYPE_ID),
				Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_TYPE_ID)));
		DetachedCriteria supplierAcctCriteria =  DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		//Supplier
		if (supplierId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.supplierId.name(), supplierId));
		//Supplier Account
		if (supplierAccountId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAccountId));
		dc.add(Restrictions.le(APInvoice.FIELD.dueDate.name(), asOfDate));
		dc.createAlias("supplier", "supplier").addOrder(Order.asc("supplier.name"));
		dc.createAlias("supplierAccount", "supplierAccount").addOrder(Order.asc("supplierAccount.name"));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.supplierAccountId.name(), supplierAcctCriteria));
		dc.addOrder(Order.asc(APInvoice.FIELD.dueDate.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<ApInvoiceAgingDto> searchInvoiceAging(ApInvoiceAgingParam param, PageSetting pageSetting) {
		if (param.getTypeId() == 1) {
			String ageBasisColumn = getAgeBasisColumn(param.getAgeBasis());
			String sqlGroupBy = "";
			String sqlSelect = "";
			if (param.isShowInvoices()){
				sqlSelect = "SELECT *, (AMOUNT-TOTAL_PAYMENT) AS BALANCE, if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 0 AND 30, (AMOUNT - TOTAL_PAYMENT), 0) AS 1_30_DAYS, " 
						+ "if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 31 AND 60, (AMOUNT - TOTAL_PAYMENT), 0) AS 31_60_DAYS, "
						+ "if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 61 AND 90, (AMOUNT - TOTAL_PAYMENT), 0) AS 61_90_DAYS, "
						+ "if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 91 AND 120, (AMOUNT - TOTAL_PAYMENT), 0) AS 91_120_DAYS, "
						+ "if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 121 AND 150, (AMOUNT - TOTAL_PAYMENT), 0) AS 121_150_DAYS, "
						+ "if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") >= 151, (AMOUNT - TOTAL_PAYMENT), 0) AS 151_UP ";
			} else { // Group the result by INVOICE_TYPE_ID and SUPPLIER_ACCOUNT_ID.
				// Add all the invoice of the supplier account.
				sqlSelect = "SELECT *, SUM(AMOUNT-TOTAL_PAYMENT) AS BALANCE, SUM(if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 0 AND 30, (AMOUNT - TOTAL_PAYMENT), 0)) AS 1_30_DAYS, " 
						+ "SUM(if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 31 AND 60, (AMOUNT - TOTAL_PAYMENT), 0)) AS 31_60_DAYS, "
						+ "SUM(if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 61 AND 90, (AMOUNT - TOTAL_PAYMENT), 0)) AS 61_90_DAYS, "
						+ "SUM(if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 91 AND 120, (AMOUNT - TOTAL_PAYMENT), 0)) AS 91_120_DAYS, "
						+ "SUM(if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") BETWEEN 121 AND 150, (AMOUNT - TOTAL_PAYMENT), 0)) AS 121_150_DAYS, "
						+ "SUM(if (DATEDIFF(CURDATE(), "+ ageBasisColumn+") >= 151, (AMOUNT - TOTAL_PAYMENT), 0)) AS 151_UP ";
				sqlGroupBy = "GROUP BY INVOICE_TYPE_ID, SUPPLIER_ACCOUNT_ID ";
			}

			String sql = sqlSelect
					+ "FROM V_INVOICE_HISTORY WHERE COMPANY_ID = ? "
					+ (param.getInvoiceTypeId() != -1 ? "AND INVOICE_TYPE_ID = ? " : " ")
					+ (param.getSupplierId() != -1 ? "AND SUPPLIER_ID = ? " : " ")
					+ (param.getSupplierAccountId() != -1 ? "AND SUPPLIER_ACCOUNT_ID = ? " : " ")
					+ "AND CURRENT_STATUS_ID != 4 "
					+ "AND (AMOUNT - TOTAL_PAYMENT) != 0 "
					+ "AND (AMOUNT - TOTAL_PAYMENT) NOT BETWEEN -0.009 AND 0.009 "
					+ "AND CURDATE() > " + ageBasisColumn + " "
					+ sqlGroupBy
					+ "ORDER BY INVOICE_TYPE_ID, SUPPLIER_NAME, SUPPLIER_ACCOUNT";

			InvoiceAgingHandler handler = new InvoiceAgingHandler(param);
			return getAllAsPage(sql, pageSetting, handler);
		} else {
			// Different source when generating report for AP Invoice Aging with As of Date parameter.
			InvoiceAgingWithAsOfDateHandler handler = new InvoiceAgingWithAsOfDateHandler(param);
			// Set the grouping of data for the report, if isShowInvoices is true, then the value for
			// groupByOption is 1, otherwise value is 2.
			int groupByOption = param.isShowInvoices() ? 1 : 2;
			return executePagedSP("GET_INVOICE_AGING", pageSetting, handler, param.getCompanyId(), param.getDivisionId(), param.getInvoiceTypeId(),
					param.getSupplierId(), param.getSupplierAccountId(), groupByOption, param.getAgeBasis(), param.getAsOfDate());
		}
	}

	private String getAgeBasisColumn (int ageBasis) {
		String ageBasisColumn = "";
		switch (ageBasis) {
		case ApInvoiceAgingParam.GL_DATE_AGE_BASIS:
			ageBasisColumn = "GL_DATE";
			break;
		case ApInvoiceAgingParam.INVOICE_DATE_AGE_BASIS:
			ageBasisColumn = "INVOICE_DATE";
			break;
		default:
			throw new RuntimeException("Unable to find age basis : " + ageBasisColumn);
		}
		return ageBasisColumn;
	}

	private static class InvoiceAgingHandler implements QueryResultHandler<ApInvoiceAgingDto> {
		private ApInvoiceAgingParam param;

		private InvoiceAgingHandler(ApInvoiceAgingParam param) {
			this.param = param;
		}

		@Override
		public List<ApInvoiceAgingDto> convert(List<Object[]> queryResult) {
			List<ApInvoiceAgingDto> invoiceAging =
					new ArrayList<ApInvoiceAgingDto>();
			for (Object[] rowResult : queryResult) {
				ApInvoiceAgingDto agingDto = new ApInvoiceAgingDto();
				int colNum = 0;
				agingDto.setCompanyName((String) rowResult[colNum++]);
				agingDto.setInvoiceType((String) rowResult[colNum++]);
				agingDto.setSupplierName((String) rowResult[colNum++]);
				agingDto.setSupplierAccountName((String) rowResult[colNum++]);
				agingDto.setInvoiceNumber((String) rowResult[colNum++]);
				agingDto.setAmount((Double) rowResult[colNum++]);
				agingDto.setTermId((Integer) rowResult[colNum++]);
				agingDto.setDueDate((Date) rowResult[colNum++]);
				agingDto.setInvoiceDate((Date) rowResult[colNum++]);
				agingDto.setGlDate((Date) rowResult[colNum++]);
				agingDto.setTermName((String) rowResult[colNum++]);
				agingDto.setInvoiceId((Integer) rowResult[colNum++]);
				agingDto.setInvoiceTypeId((Integer) rowResult[colNum++]);
				agingDto.setSupplierId((Integer) rowResult[colNum++]);
				agingDto.setSupplierAcctId((Integer) rowResult[colNum++]);
				agingDto.setTotalPayment((Double) rowResult[colNum++]);
				agingDto.setBalance((Double) rowResult[colNum++]);
				agingDto.setRange1To30((Double) rowResult[colNum++]);
				agingDto.setRange31To60((Double) rowResult[colNum++]);
				agingDto.setRange61To90((Double) rowResult[colNum++]);
				agingDto.setRange91To120((Double) rowResult[colNum++]);
				agingDto.setRange121To150((Double) rowResult[colNum++]);
				agingDto.setRange151ToUp((Double) rowResult[colNum++]);
				invoiceAging.add(agingDto);
			}
			return invoiceAging;
		}

		@Override
		public int setParamater(SQLQuery query) {

			int index = 0;
			query.setParameter(index, param.getCompanyId());
			if (param.getInvoiceTypeId() != - 1) {
				query.setParameter(++index, param.getInvoiceTypeId());
			}
			if (param.getSupplierId() != - 1) {
				query.setParameter(++index, param.getSupplierId());
			}
			if (param.getSupplierAccountId() != - 1) {
				query.setParameter(++index, param.getSupplierAccountId());
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("COMPANY_NAME", Hibernate.STRING);
			query.addScalar("TYPE", Hibernate.STRING);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCOUNT", Hibernate.STRING);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("TERM_ID", Hibernate.INTEGER);
			query.addScalar("DUE_DATE", Hibernate.DATE);
			query.addScalar("INVOICE_DATE", Hibernate.DATE);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("AP_INVOICE_ID", Hibernate.INTEGER);
			query.addScalar("INVOICE_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_ID", Hibernate.INTEGER);
			query.addScalar("TOTAL_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("1_30_DAYS", Hibernate.DOUBLE);
			query.addScalar("31_60_DAYS", Hibernate.DOUBLE);
			query.addScalar("61_90_DAYS", Hibernate.DOUBLE);
			query.addScalar("91_120_DAYS", Hibernate.DOUBLE);
			query.addScalar("121_150_DAYS", Hibernate.DOUBLE);
			query.addScalar("151_UP", Hibernate.DOUBLE);
		}
	}

	@Override
	public APInvoice getInvoice(int supplierAccountId, int apInvoiceId, String invoiceNumber,
			int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (invoiceNumber.trim().startsWith("RR-") || invoiceNumber.trim().startsWith("RM-")||
				invoiceNumber.trim().startsWith("RTS-")) {
			if (invoiceNumber.trim().startsWith("RR-")) {
				invoiceNumber = (invoiceNumber.replace("RR-", "").trim()).split(" ")[0];
				dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RR_TYPE_ID));
			} else if (invoiceNumber.trim().startsWith("RM-")) {
				invoiceNumber = (invoiceNumber.replace("RM-", "").trim()).split(" ")[0];
				dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RR_RAW_MAT_TYPE_ID));
			} else if (invoiceNumber.trim().startsWith("RTS-")) {
				invoiceNumber = (invoiceNumber.replace("RTS-", "").trim()).split(" ")[0];
				dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_TYPE_ID));
			}
			dc.add(Restrictions.eq("sequenceNumber", Integer.valueOf(invoiceNumber)));
		} else {
			dc.add(Restrictions.eq(APInvoice.FIELD.id.name(), apInvoiceId));
		}

		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAccountId));
		dc.add(Restrictions.eq(APInvoice.FIELD.serviceLeaseKeyId.name(), serviceLeaseKeyId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	private static class InvoiceAgingWithAsOfDateHandler extends InvoiceAgingHandler {
		private InvoiceAgingWithAsOfDateHandler (ApInvoiceAgingParam param) {
			super (param);
		}

		@Override
		public List<ApInvoiceAgingDto> convert(List<Object[]> queryResult) {
			List<ApInvoiceAgingDto> invoiceAging =
					new ArrayList<ApInvoiceAgingDto>();
			for (Object[] rowResult : queryResult) {
				ApInvoiceAgingDto agingDto = new ApInvoiceAgingDto();
				int colNum = 0;
				agingDto.setDivisionName((String) rowResult[colNum++]);
				agingDto.setInvoiceType((String) rowResult[colNum++]);
				agingDto.setInvoiceId((Integer) rowResult[colNum++]); // ID
				agingDto.setSupplierName((String) rowResult[colNum++]);
				agingDto.setSupplierAccountName((String) rowResult[colNum++]);
				agingDto.setCompanyName((String) rowResult[colNum++]); // TYPE_SA
				agingDto.setInvoiceNumber((String) rowResult[colNum++]);
				agingDto.setTermName((String) rowResult[colNum++]);
				agingDto.setAmount(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++])); // TOTAL_INVOICE
				agingDto.setTotalPayment(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setBalance(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange1To30(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange31To60(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange61To90(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange91To120(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange121To150(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				agingDto.setRange151ToUp(NumberFormatUtil.convertBigDecimalToDouble(rowResult[colNum++]));
				invoiceAging.add(agingDto);
			}
			return invoiceAging;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("TYPE", Hibernate.STRING);
			query.addScalar("ID", Hibernate.INTEGER);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCOUNT", Hibernate.STRING);
			query.addScalar("TYPE_SA", Hibernate.STRING);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("TOTAL_INVOICE", Hibernate.DOUBLE);
			query.addScalar("TOTAL_PAYMENT", Hibernate.DOUBLE);
			query.addScalar("BALANCE", Hibernate.DOUBLE);
			query.addScalar("1_30_DAYS", Hibernate.DOUBLE);
			query.addScalar("31_60_DAYS", Hibernate.DOUBLE);
			query.addScalar("61_90_DAYS", Hibernate.DOUBLE);
			query.addScalar("91_120_DAYS", Hibernate.DOUBLE);
			query.addScalar("121_150_DAYS", Hibernate.DOUBLE);
			query.addScalar("151_UP", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<APInvoice> getAllAPInvoice(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting, final int typeId) {
		HibernateCallback<Page<APInvoice>> hibernateCallback = new HibernateCallback<Page<APInvoice>>() {
			@Override
			public Page<APInvoice> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria invoiceCriteria = session.createCriteria(APInvoice.class);
				if (typeId <= InvoiceType.CREDIT_MEMO_TYPE_ID) {
					invoiceCriteria.add(Restrictions.between(APInvoice.FIELD.invoiceTypeId.name(),
							InvoiceType.REGULAR_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID));
				} else {
					invoiceCriteria.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), typeId));
				}

				DetachedCriteria supplierAcctCrit = DetachedCriteria.forClass(SupplierAccount.class);

				//Search for company number, date and amount
				SearchCommonUtil.searchCommonParams(invoiceCriteria, supplierAcctCrit, "supplierAccountId",
						APInvoice.FIELD.invoiceDate.name(), APInvoice.FIELD.dueDate.name(),
						APInvoice.FIELD.glDate.name(), searchParam.getUser().getCompanyIds(), searchParam);

				//Search for receipt number and/or description
				String criteria = searchParam.getSearchCriteria();
				if (!criteria.isEmpty()) {
					LogicalExpression invoiceNumOrDescription =
							Restrictions.or(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), "%"+criteria.trim()+"%"),
							Restrictions.like(APInvoice.FIELD.description.name(), "%"+criteria.trim()+"%"));
					if (StringFormatUtil.isNumeric(criteria)) {
						//Removing 'this_.' will throw an error:
						//MySQLIntegrityConstraintViolationException: Column 'SEQUENCE_NO' in where clause is ambiguous
						invoiceCriteria.add(Restrictions.or(invoiceNumOrDescription,
								Restrictions.sqlRestriction("this_.SEQUENCE_NO LIKE ?", "%" + criteria.trim() + "%", Hibernate.STRING)));
					} else {
						invoiceCriteria.add(invoiceNumOrDescription);
					}
				}
				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				invoiceCriteria.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkFlow));
				invoiceCriteria.addOrder(Order.desc(APInvoice.FIELD.invoiceDate.name()));
				invoiceCriteria.addOrder(Order.asc(APInvoice.FIELD.dueDate.name()));
				invoiceCriteria.addOrder(Order.desc(APInvoice.FIELD.sequenceNumber.name()));
				Page<APInvoice> invoices = getAll(invoiceCriteria, pageSetting);
				for (APInvoice apInvoice : invoices.getData()) {
					getHibernateTemplate().initialize(apInvoice.getSupplier());
					getHibernateTemplate().initialize(apInvoice.getSupplierAccount());
				}
				return invoices;
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public boolean hasAssociatedPayment(int invoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(APInvoice.FIELD.id.name(), invoiceId));
		
		// Payment workflow
		DetachedCriteria dcPWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcPWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcPWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		
		// Payment Subquery
		DetachedCriteria paymentCriteria = DetachedCriteria.forClass(ApPayment.class);
		paymentCriteria.setProjection(Projections.property(ApPayment.FIELD.id.name()));
		paymentCriteria.add(Subqueries.propertyIn(ApPayment.FIELD.formWorkflowId.name(), dcPWorkflow));
		
		// Payment Invoice Subquery
		DetachedCriteria pInvoiceCriteria = DetachedCriteria.forClass(ApPaymentInvoice.class);
		pInvoiceCriteria.setProjection(Projections.property(ApPaymentInvoice.FIELD.invoiceId.name()));
		pInvoiceCriteria.add(Subqueries.propertyIn(ApPaymentInvoice.FIELD.apPaymentId.name(), paymentCriteria));

		dc.add(Subqueries.propertyIn(APInvoice.FIELD.id.name(), pInvoiceCriteria));
		return getAll(dc).size() > 0;
	}

	@Override
	public APInvoice getAPInvoiceByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(APInvoice.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}

	@Override
	public double getTotalInvoiceAmount(Integer companyId, Integer supplierAcctId, Date asOfDate, boolean isApproved) {
		DetachedCriteria dc = getDetachedCriteria();
		if (asOfDate != null)
			dc.add(Restrictions.le(APInvoice.FIELD.glDate.name(), asOfDate));
		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAcctId));
		dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_CENTRAL_TYPE_ID));
		dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB3_TYPE_ID));
		dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB4_TYPE_ID));
		dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB5_TYPE_ID));
		dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB8_TYPE_ID));
		dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB8A_TYPE_ID));
		//Supplier account criteria
		DetachedCriteria dcSupplierAcct =  DetachedCriteria.forClass(SupplierAccount.class);
		dcSupplierAcct.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		dcSupplierAcct.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		//Workflow criteria
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		if (isApproved)
			dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));

		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.supplierAccountId.name(), dcSupplierAcct));
		dc.setProjection(Projections.sum(APInvoice.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public double getTotalAmountOfInvoice(Integer companyId,
			Integer supplierAcctId, Date asOfDate, boolean isRtsType) {
		DetachedCriteria dc = getDetachedCriteria();
		if (asOfDate != null)
			dc.add(Restrictions.le(APInvoice.FIELD.glDate.name(), asOfDate));
		if (isRtsType) {
			dc.add(Restrictions.or(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_TYPE_ID),
					Restrictions.between(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_CENTRAL_TYPE_ID, InvoiceType.RTS_NSB8A_TYPE_ID)));
		} else {
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_TYPE_ID));
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_CENTRAL_TYPE_ID));
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB3_TYPE_ID));
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB4_TYPE_ID));
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB5_TYPE_ID));
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB8_TYPE_ID));
			dc.add(Restrictions.ne(APInvoice.FIELD.invoiceTypeId.name(), InvoiceType.RTS_NSB8A_TYPE_ID));
		}
		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAcctId));
		//Supplier account criteria
		DetachedCriteria dcSupplierAcct =  DetachedCriteria.forClass(SupplierAccount.class);
		dcSupplierAcct.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		dcSupplierAcct.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		//Workflow criteria
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));

		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.supplierAccountId.name(), dcSupplierAcct));
		dc.setProjection(Projections.sum(APInvoice.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public APInvoice getInvoiceByRefId(Integer refId, boolean isRrItem) {
		DetachedCriteria invoiceDc = getDetachedCriteria();
		if (isRrItem) {
			//RR Item Criteria
			DetachedCriteria rrItemDc = DetachedCriteria.forClass(RReceivingReportItem.class);
			rrItemDc.setProjection(Projections.property(RReceivingReportItem.FIELD.apInvoiceId.name()));
			rrItemDc.add(Restrictions.eq(RReceivingReportItem.FIELD.id.name(), refId));
			invoiceDc.add(Subqueries.propertyIn(APInvoice.FIELD.id.name(), rrItemDc));
		} else {
			//RTS Item Criteria
			DetachedCriteria rtsItemDc = DetachedCriteria.forClass(RReturnToSupplierItem.class);
			rtsItemDc.setProjection(Projections.property(RReturnToSupplierItem.FIELD.apInvoiceId.name()));
			rtsItemDc.add(Restrictions.eq(RReturnToSupplierItem.FIELD.id.name(), refId));
			invoiceDc.add(Subqueries.propertyIn(APInvoice.FIELD.id.name(), rtsItemDc));
		}
		invoiceDc.createAlias("formWorkflow", "fw");
		invoiceDc.add(Restrictions.eq("fw.complete", true));
		return get(invoiceDc);
	}

	@Override
	public Page<APInvoice> getUnpaidInvoices(int supplierAcctId, String invoiceNumber, String invoiceIds, PageSetting pageSetting) {
		String sql = "SELECT AP_INVOICE_ID, SEQUENCE_NO, INVOICE_TYPE_ID, INVOICE_NUMBER, AMOUNT, PAID_AMOUNT FROM ("
				// Select invoices with payments
				+ "SELECT AI.AP_INVOICE_ID, AI.SEQUENCE_NO, AI.INVOICE_TYPE_ID, "
				+ "CONCAT(CASE WHEN AI.INVOICE_TYPE_ID <= 4 THEN AI.INVOICE_NUMBER "
				+ "WHEN AI.INVOICE_TYPE_ID = 6 THEN CONCAT('RTS-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID = 7 THEN CONCAT('RTS-EB-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID = 10 THEN CONCAT('AII-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID = 12 THEN CONCAT('AIS-', AI.SEQUENCE_NO) "
				+ "ELSE CONCAT('AP INVOICE -', AI.SEQUENCE_NO) END, "
				+ "(CASE WHEN AI.INVOICE_TYPE_ID > 4 && AI.INVOICE_NUMBER IS NOT NULL AND AI.INVOICE_NUMBER != '' "
				+ "THEN CONCAT((if (AI.INVOICE_TYPE_ID != 9,' Inv#: ', ' SS#: ')), AI.INVOICE_NUMBER) ELSE '' END), "
				+ "(CASE WHEN AI.INVOICE_TYPE_ID = 6 THEN (SELECT CASE WHEN RRR.DELIVERY_RECEIPT_NO != '' "
				+ "THEN CONCAT(' DR#: ', RRR.DELIVERY_RECEIPT_NO) ELSE '' END FROM R_RETURN_TO_SUPPLIER_ITEM RTSI "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RRI ON RRI.R_RECEIVING_REPORT_ITEM_ID = RTSI.R_RECEIVING_REPORT_ITEM_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RRR ON RRR.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "WHERE RTSI.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "GROUP BY RTSI.AP_INVOICE_ID) "
				+ "WHEN RR.DELIVERY_RECEIPT_NO IS NOT NULL AND RR.DELIVERY_RECEIPT_NO != '' "
				+ "THEN if (AI.INVOICE_TYPE_ID IN (5, 8), CONCAT(' DR#: ', RR.DELIVERY_RECEIPT_NO), '') ELSE '' END)) AS INVOICE_NUMBER, "
				// Negate amount for RTS and RTS - EB invoice type
				+ "if (AI.INVOICE_TYPE_ID = 7 OR AI.INVOICE_TYPE_ID = 6, -AI.AMOUNT, AI.AMOUNT) AS AMOUNT, "
				+ "COALESCE((SELECT SUM(API.PAID_AMOUNT) FROM AP_PAYMENT_INVOICE API "
				+ "INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = API.AP_PAYMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID), 0 ) AS PAID_AMOUNT "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN INVOICE_TYPE IT ON IT.INVOICE_TYPE_ID = AI.INVOICE_TYPE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "LEFT JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND IT.INVOICE_TYPE_ID NOT IN (5,8,9,13,14,15,16,17,18) "//Filter RR invoices
				+ "AND AI.SUPPLIER_ACCOUNT_ID = ? "
				+ "UNION ALL "
				// Select invoices without payments
				+ "SELECT AI.AP_INVOICE_ID, AI.SEQUENCE_NO, AI.INVOICE_TYPE_ID, "
				+ "CONCAT(CASE WHEN AI.INVOICE_TYPE_ID <= 4 THEN AI.INVOICE_NUMBER "
				+ "WHEN AI.INVOICE_TYPE_ID = 6 THEN CONCAT('RTS-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID = 7 THEN CONCAT('RTS-EB-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID = 10 THEN CONCAT('AII-', AI.SEQUENCE_NO) "
				+ "WHEN AI.INVOICE_TYPE_ID = 12 THEN CONCAT('AIS-', AI.SEQUENCE_NO) "
				+ "ELSE CONCAT('AP INVOICE -', AI.SEQUENCE_NO) END, "
				+ "(CASE WHEN AI.INVOICE_TYPE_ID >= 4 && AI.INVOICE_NUMBER IS NOT NULL AND AI.INVOICE_NUMBER != '' "
				+ "THEN CONCAT((if (AI.INVOICE_TYPE_ID != 9,' Inv#: ', ' SS#: ')), AI.INVOICE_NUMBER) ELSE '' END), "
				+ "(CASE WHEN AI.INVOICE_TYPE_ID = 6 THEN (SELECT CASE WHEN RRR.DELIVERY_RECEIPT_NO != '' "
				+ "THEN CONCAT(' DR#: ', RRR.DELIVERY_RECEIPT_NO) ELSE '' END FROM R_RETURN_TO_SUPPLIER_ITEM RTSI "
				+ "INNER JOIN R_RECEIVING_REPORT_ITEM RRI ON RRI.R_RECEIVING_REPORT_ITEM_ID = RTSI.R_RECEIVING_REPORT_ITEM_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RRR ON RRR.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "WHERE RTSI.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "GROUP BY RTSI.AP_INVOICE_ID) "
				+ "WHEN RR.DELIVERY_RECEIPT_NO IS NOT NULL AND RR.DELIVERY_RECEIPT_NO != '' "
				+ "THEN if (AI.INVOICE_TYPE_ID IN (5, 8), CONCAT(' DR#: ', RR.DELIVERY_RECEIPT_NO), '') ELSE '' END)) AS INVOICE_NUMBER, "
				// Negate amount for RTS and RTS - EB invoice type
				+ "if (AI.INVOICE_TYPE_ID = 7 OR AI.INVOICE_TYPE_ID = 6, -AI.AMOUNT, AI.AMOUNT) AS AMOUNT, 0 AS PAID_AMOUNT "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN INVOICE_TYPE IT ON IT.INVOICE_TYPE_ID = AI.INVOICE_TYPE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "LEFT JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND IT.INVOICE_TYPE_ID NOT IN (5,8,9,13,14,15,16,17,18) "//Filter RR invoices
				+ "AND AI.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND AI.AP_INVOICE_ID NOT IN "
				+ "(SELECT AP_INVOICE_ID FROM AP_PAYMENT_INVOICE)) "
				+ "AS TBL WHERE ABS(AMOUNT) > ABS(PAID_AMOUNT)";
				if (invoiceIds != null && !invoiceIds.trim().isEmpty()) {
					String tmpInvoiceIds[] = invoiceIds.split(";");
					for(String tmp : tmpInvoiceIds) {
						sql += "AND AP_INVOICE_ID != '" + tmp.trim() + "'";
					}
				}
				sql += (invoiceNumber.trim().isEmpty() ? "" : " AND INVOICE_NUMBER LIKE '%"+invoiceNumber.trim()+"%' ")
					+" GROUP BY AP_INVOICE_ID ";
		UnpaidInvoicesHandler handler = new UnpaidInvoicesHandler(supplierAcctId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class UnpaidInvoicesHandler implements QueryResultHandler<APInvoice> {
		private int supplierAcctId;

		private UnpaidInvoicesHandler(int supplierAcctId) {
			this.supplierAcctId = supplierAcctId;
		}

		@Override
		public List<APInvoice> convert(List<Object[]> queryResult) {
			List<APInvoice> unpaidInvoices =
					new ArrayList<APInvoice>();
			for (Object[] rowResult : queryResult) {
				APInvoice invoice = new APInvoice();
				int colNum = 0;
				Integer invoiceId = (Integer) rowResult[colNum++];
				Integer sequenceNo = (Integer) rowResult[colNum++];
				Integer invoiceTypeId = (Integer) rowResult[colNum++];
				String invoiceNumber = (String) rowResult[colNum++];
				Double amount = (Double)  rowResult[colNum++];
				Double paidAmount = (Double)  rowResult[colNum++];

				invoice.setId(invoiceId);
				invoice.setSequenceNumber(sequenceNo);
				invoice.setInvoiceTypeId(invoiceTypeId);
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setReferenceNo(invoiceNumber);
				invoice.setAmount(amount);
				invoice.setBalance(amount - paidAmount);
				unpaidInvoices.add(invoice);
			}
			return unpaidInvoices;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, supplierAcctId);
			query.setParameter(++index, supplierAcctId);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("AP_INVOICE_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("INVOICE_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PAID_AMOUNT", Hibernate.DOUBLE);
		}
	}

	@Override
	public APInvoice getAPInvoice(Integer supplierAccountId, String invoiceNumber, Integer invoiceTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (invoiceTypeId.equals(InvoiceType.RR_TYPE_ID)) {
			invoiceNumber = (invoiceNumber.replace("RR-", "").trim()).split(" ")[0];
			dc.add(Restrictions.eq(APInvoice.FIELD.sequenceNumber.name(), Integer.valueOf(invoiceNumber)));
		} else if (invoiceTypeId.equals(InvoiceType.RR_RAW_MAT_TYPE_ID)) {
			invoiceNumber = (invoiceNumber.replace("RM-", "").trim()).split(" ")[0];
			dc.add(Restrictions.eq(APInvoice.FIELD.sequenceNumber.name(), Integer.valueOf(invoiceNumber)));
		} else if (invoiceTypeId.equals(InvoiceType.RTS_TYPE_ID)) {
			invoiceNumber = (invoiceNumber.replace("RTS-", "").trim()).split(" ")[0];
			dc.add(Restrictions.eq(APInvoice.FIELD.sequenceNumber.name(), Integer.valueOf(invoiceNumber)));
		} else {
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceNumber.name(), invoiceNumber));
		}
		dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAccountId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	@Override
	public Page<APInvoice> getReceivingReports(Integer formId, Integer supplierAcctId, String invoiceNumber,
			String invoiceIds, boolean isExact, PageSetting pageSetting) {
		String sql = "SELECT AP_INVOICE_ID, SEQUENCE_NO, INVOICE_TYPE_ID, INVOICE_NUMBER, AMOUNT FROM ( "
				+ "SELECT AI.AP_INVOICE_ID, AI.SEQUENCE_NO, AI.INVOICE_TYPE_ID, "
				+ "CONCAT(CONCAT('RR-', AI.SEQUENCE_NO), "
				+ "CASE WHEN (AI.INVOICE_NUMBER IS NOT NULL AND AI.INVOICE_NUMBER != '') "
				+ "THEN CONCAT(' Inv # ', AI.INVOICE_NUMBER) ELSE '' END, "
				+ "CASE WHEN RR.DELIVERY_RECEIPT_NO IS NOT NULL AND RR.DELIVERY_RECEIPT_NO != '' "
				+ "THEN CONCAT(' DR # ', RR.DELIVERY_RECEIPT_NO) ELSE '' END) AS INVOICE_NUMBER, "
				+ "AI.AMOUNT FROM AP_INVOICE AI "
				+ "INNER JOIN INVOICE_TYPE IT ON IT.INVOICE_TYPE_ID = AI.INVOICE_TYPE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "LEFT JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND AI.INVOICE_TYPE_ID = 5 "
				+ "AND AI.SUPPLIER_ACCOUNT_ID = ? "
				+ "AND AI.AP_INVOICE_ID NOT IN (SELECT APII.AP_INVOICE_ID FROM AP_INVOICE_ITEM APII "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APII.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 AND APII.ACTIVE = 1 ";
		if (formId != null && formId > 0) {
			sql += "AND API.AP_INVOICE_ID != ? ";
		}
		sql += ")) AS TBL WHERE AMOUNT > 0 ";
		if (!isExact && invoiceIds != null && !invoiceIds.trim().isEmpty()) {
			String tmpInvoiceIds[] = invoiceIds.split(";");
			for(String tmp : tmpInvoiceIds) {
				sql += "AND AP_INVOICE_ID != '" + tmp.trim() + "' ";
			}
		}
		if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
			if (isExact) {
				sql += "AND INVOICE_NUMBER = '"+invoiceNumber.trim()+"'";
			} else {
				sql += "AND INVOICE_NUMBER LIKE '%"+invoiceNumber.trim()+"%'";
			}
		}
		sql += "GROUP BY AP_INVOICE_ID ";
		return getAllAsPage(sql, pageSetting, new UnsettledRrFormsHandler(supplierAcctId, formId));
	}

	private static class UnsettledRrFormsHandler implements QueryResultHandler<APInvoice> {
		private int supplierAcctId;
		private Integer formId;

		private UnsettledRrFormsHandler(int supplierAcctId, Integer formId) {
			this.supplierAcctId = supplierAcctId;
			this.formId = formId;
		}

		@Override
		public List<APInvoice> convert(List<Object[]> queryResult) {
			List<APInvoice> unpaidInvoices = new ArrayList<APInvoice>();
			APInvoice invoice = null;
			for (Object[] rowResult : queryResult) {
				invoice = new APInvoice();
				int colNum = 0;
				Integer invoiceId = (Integer) rowResult[colNum++];
				Integer sequenceNo = (Integer) rowResult[colNum++];
				Integer invoiceTypeId = (Integer) rowResult[colNum++];
				String invoiceNumber = (String) rowResult[colNum++];
				Double amount = (Double) rowResult[colNum++];

				invoice.setId(invoiceId);
				invoice.setSequenceNumber(sequenceNo);
				invoice.setInvoiceTypeId(invoiceTypeId);
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setReferenceNo(invoiceNumber);
				invoice.setAmount(amount);
				invoice.setBalance(amount);
				unpaidInvoices.add(invoice);
			}
			return unpaidInvoices;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, supplierAcctId);
			if (formId != null && formId > 0) {
				query.setParameter(++index, formId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("AP_INVOICE_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("INVOICE_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("INVOICE_NUMBER", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
		}
	}

	@Override
	public Page<APInvoice> retrieveApInvoices(String criteria, Integer typeId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), typeId));
		if (typeId.equals(InvoiceType.INVOICE_ITEM_TYPE_ID)
				|| typeId.equals(InvoiceType.INVOICE_ITEM_TYPE_ID)) {
			dc.add(Restrictions.or(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), "%" + criteria.trim() + "%"),
					Restrictions.like(APInvoice.FIELD.description.name(), "%" + criteria.trim() + "%")));
			dc.addOrder(Order.asc(APInvoice.FIELD.invoiceNumber.name()));
		} else {
			if (StringFormatUtil.isNumeric(criteria)) {
				dc.add(Restrictions.sqlRestriction("this_.SEQUENCE_NO LIKE ? ", criteria.trim(), Hibernate.STRING));
				dc.addOrder(Order.asc(APInvoice.FIELD.sequenceNumber.name()));
			}
		}
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<SupplierApLineDto> getSupplierApLine(int companyId, int supplierId, int supplierAcctId, int divisionId,
			int acctId, Date dateFrom, Date dateTo, PageSetting pageSetting) {
		String sql = "SELECT CONCAT((CASE WHEN AI.INVOICE_TYPE_ID = 1 THEN 'REG' "
				+ "WHEN AI.INVOICE_TYPE_ID = 2 THEN 'PRE' "
				+ "WHEN AI.INVOICE_TYPE_ID = 3 THEN 'DM' "
				+ "WHEN AI.INVOICE_TYPE_ID = 4 THEN 'CM' "
				+ "WHEN AI.INVOICE_TYPE_ID = 12 THEN 'AIS' END),' ',AI.SEQUENCE_NO) AS AP_INVOICE_SOURCE, "
				+ "S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACC_NAME, D.NAME AS DIVISION_NAME, "
				+ "A.ACCOUNT_NAME, AL.AMOUNT, AL.DESCRIPTION, AI.GL_DATE "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN AP_LINE AL ON AI.AP_INVOICE_ID = AL.AP_INVOICE_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = AL.ACCOUNT_COMBINATION_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AI.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = AI.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN DIVISION D ON AC.DIVISION_ID = D.DIVISION_ID "
				+ "INNER JOIN COMPANY C ON AC.COMPANY_ID = C.COMPANY_ID "
				+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "WHERE C.COMPANY_ID = ? "
				+ "AND AI.GL_DATE BETWEEN ? AND ? "
				+ "AND FW.IS_COMPLETE = 1 ";
				if (supplierId != -1) {
					sql += "AND AI.SUPPLIER_ID = ? ";
				}
				if (supplierAcctId != -1) {
					sql += "AND AI.SUPPLIER_ACCOUNT_ID = ? ";
				}
				if (divisionId != -1) {
					sql += "AND AC.DIVISION_ID = ? ";
				}
				if (acctId != -1) {
					sql += "AND AC.ACCOUNT_ID = ? ";
				}
				sql += "ORDER BY AI.SEQUENCE_NO ";
		SupplierApLineHandler handler = new SupplierApLineHandler(companyId, supplierId, supplierAcctId, divisionId, 
				acctId, dateFrom, dateTo);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class SupplierApLineHandler implements QueryResultHandler<SupplierApLineDto> {
		private int companyId;
		private int supplierId;
		private int supplierAcctId;
		private int divisionId;
		private int acctId;
		private Date dateFrom;
		private Date dateTo;

		private SupplierApLineHandler(int companyId, int supplierId, int supplierAcctId, int divisionId, int acctId, 
				Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.divisionId = divisionId;
			this.acctId = acctId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<SupplierApLineDto> convert(List<Object[]> queryResult) {
			List<SupplierApLineDto> supplierApLine =
					new ArrayList<SupplierApLineDto>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				SupplierApLineDto supplierApLineDto = new SupplierApLineDto();
				String apInvoiceSource = (String) rowResult[colNum++];
				String supplierName = (String) rowResult[colNum++];
				String supplierAcctName = (String) rowResult[colNum++];
				String divisionName = (String) rowResult[colNum++];
				String acctName = (String) rowResult[colNum++];
				Double amount = (Double)  rowResult[colNum++];
				String desc = (String)  rowResult[colNum++];
				Date date = (Date) rowResult[colNum++];


				supplierApLineDto.setApInvoiceSource(apInvoiceSource);
				supplierApLineDto.setSupplierName(supplierName);
				supplierApLineDto.setSupplierAcctName(supplierAcctName);
				supplierApLineDto.setDivisionName(divisionName);
				supplierApLineDto.setAcctName(acctName);
				supplierApLineDto.setAmount(amount);
				supplierApLineDto.setDesc(desc);
				supplierApLineDto.setDate(date);
				supplierApLine.add(supplierApLineDto);
			}
			return supplierApLine;
		}
		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
				query.setParameter(index, companyId);
				query.setParameter(++index, DateUtil.formatToSqlDate(dateFrom));
				query.setParameter(++index, DateUtil.formatToSqlDate(dateTo));
			if (supplierId != -1) {
				query.setParameter(++index, supplierId);
			}
			if (supplierAcctId != -1) {
				query.setParameter(++index, supplierAcctId);
			}
			if (divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			if (acctId != -1) {
				query.setParameter(++index, acctId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("AP_INVOICE_SOURCE", Hibernate.STRING);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACC_NAME", Hibernate.STRING);
			query.addScalar("DIVISION_NAME", Hibernate.STRING);
			query.addScalar("ACCOUNT_NAME", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("GL_DATE", Hibernate.DATE);
		}
	}

	@Override
	public Page<ApInvoiceDto> getRrReferences(Integer companyId, Integer divisionId, Integer supplierId, Integer rrNumber,
			String bmsNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting) {
		String sql = "SELECT AP_INVOICE_ID, SEQUENCE_NO, GL_DATE, BMS_NUMBER, SUPPLIER_NAME, SUPPLIER_ACCT_NAME, "
				+ "SUM(RR_QTY) AS TOTAL_RR, SUM(API_QTY) AS TOTAL_API FROM ( "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, SI.QUANTITY AS RR_QTY, 0 AS API_QTY  "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = API.AP_INVOICE_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (13, 14, 15, 16, 17, 18) "
				+ "AND OTO.OR_TYPE_ID = 64 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND RR.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (rrNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, RRI.QUANTITY AS RR_QTY, 0 AS API_QTY  "
				+ "FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = API.AP_INVOICE_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (13, 14, 15, 16, 17, 18) "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND RR.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (rrNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, APIL.QUANTITY AS RR_QTY, 0 AS API_QTY  "
				+ "FROM AP_INVOICE_LINE APIL "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = APIL.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR ON RR.AP_INVOICE_ID = API.AP_INVOICE_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (13, 14, 15, 16, 17, 18) "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND RR.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (rrNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT RR.AP_INVOICE_ID, RR.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS RR_QTY, SI.QUANTITY AS API_QTY  "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID  "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO2 ON OTO2.TO_OBJECT_ID = OTO1.FROM_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE RR ON RR.EB_OBJECT_ID = OTO2.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR1 ON RR1.AP_INVOICE_ID = RR.AP_INVOICE_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) "
				+ "AND OTO1.OR_TYPE_ID = 24001 "
				+ "AND OTO2.OR_TYPE_ID = 24002 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND RR1.COMPANY_ID = ? "
				+ "AND RR.DIVISION_ID = ? "
				+ (supplierId != null ? "AND RR.SUPPLIER_ID = ? " : "")
				+ (rrNumber != null ? "AND RR.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND RR.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND RR.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT RR.AP_INVOICE_ID, RR.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS RR_QTY, RRI.QUANTITY AS API_QTY  "
				+ "FROM AP_INVOICE_GOODS RRI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE RR ON RR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR1 ON RR1.AP_INVOICE_ID = RR.AP_INVOICE_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) "
				+ "AND OTO.OR_TYPE_ID = 24002 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND RR1.COMPANY_ID = ? "
				+ "AND RR.DIVISION_ID = ? "
				+ (supplierId != null ? "AND RR.SUPPLIER_ID = ? " : "")
				+ (rrNumber != null ? "AND RR.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND RR.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND RR.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT RR.AP_INVOICE_ID, RR.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS RR_QTY, APIL.QUANTITY AS API_QTY "
				+ "FROM AP_INVOICE_LINE APIL "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = APIL.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE RR ON RR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "INNER JOIN R_RECEIVING_REPORT RR1 ON RR1.AP_INVOICE_ID = RR.AP_INVOICE_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) "
				+ "AND OTO.OR_TYPE_ID = 24002 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND RR1.COMPANY_ID = ? "
				+ "AND RR.DIVISION_ID = ? "
				+ (supplierId != null ? "AND RR.SUPPLIER_ID = ? " : "")
				+ (rrNumber != null ? "AND RR.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND RR.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND RR.GL_DATE BETWEEN ? AND ? " : "")
				+ ") AS TBL "
				+ "GROUP BY AP_INVOICE_ID ";
		if (status == RReceivingReport.STATUS_UNUSED) {
			sql += "HAVING TOTAL_API = 0 ";
		} else if (status == RReceivingReport.STATUS_USED) {
			sql += "HAVING TOTAL_API != 0 AND (TOTAL_RR - TOTAL_API) > 0 ";
		} else {
			sql += "HAVING (TOTAL_RR - TOTAL_API) > 0 ";
		}
		sql += "ORDER BY GL_DATE DESC, SEQUENCE_NO DESC ";
		return getAllAsPage(sql, pageSetting, new InvoiceRefHandler(companyId, divisionId, supplierId,
				null, rrNumber, bmsNumber, dateFrom, dateTo, 6));
	}

	private static class InvoiceRefHandler implements QueryResultHandler<ApInvoiceDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer supplierId;
		private Integer supplierAcctId;
		private Integer rrNumber;
		private String bmsNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer noOfTables;

		private InvoiceRefHandler(Integer companyId, Integer divisionId, Integer supplierId,
				Integer supplierAcctId, Integer rrNumber, String bmsNumber, Date dateFrom,
				Date dateTo, Integer noOfTables) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.rrNumber = rrNumber;
			this.bmsNumber = bmsNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.noOfTables = noOfTables;
		}

		@Override
		public List<ApInvoiceDto> convert(List<Object[]> queryResult) {
			List<ApInvoiceDto> poDtos = new ArrayList<ApInvoiceDto>();
			ApInvoiceDto dto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				dto = new ApInvoiceDto();
				dto.setInvoiceId((Integer) row[index++]);
				dto.setReferenceNumber((Integer) row[index++]);
				dto.setDate((Date) row[index++]);
				dto.setBmsNumber((String) row[index++]);
				dto.setSupplierName((String) row[index++]);
				dto.setSupplierAcctName((String) row[index++]);
				poDtos.add(dto);

				// free up memory
				dto = null;
			}
			return poDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			for (int i = 0; i < noOfTables; i++) {
				query.setParameter(index, companyId);
				query.setParameter(++index, divisionId);
				if (supplierId != null) {
					query.setParameter(++index, supplierId);
				}
				if (supplierAcctId != null) {
					query.setParameter(++index, supplierAcctId);
				}
				if (rrNumber != null) {
					query.setParameter(++index, rrNumber);
				}
				if (bmsNumber != null && !bmsNumber.isEmpty()) {
					query.setParameter(++index, bmsNumber);
				}
				if (dateFrom != null && dateTo != null) {
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				}
				if (i < (noOfTables-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("AP_INVOICE_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("GL_DATE", Hibernate.DATE);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCT_NAME", Hibernate.STRING);
		}
	}

	@Override
	public Page<APInvoice> searchInvoiceGoodsAndServices(int typeId, int divisionId, String searchCriteria,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), typeId));
		dc.add(Restrictions.eq(APInvoice.FIELD.divisionId.name(), divisionId));
		if (StringFormatUtil.isNumeric(searchCriteria)) {
			dc.add(Restrictions.like(APInvoice.FIELD.sequenceNumber.name(),
					Integer.parseInt(searchCriteria.trim())));
		}
		dc.addOrder(Order.desc(APInvoice.FIELD.sequenceNumber.name()));
		dc.addOrder(Order.desc(APInvoice.FIELD.invoiceDate.name()));
		dc.addOrder(Order.desc(APInvoice.FIELD.glDate.name()));
		return getAll(dc, pageSetting);
	}

	public APInvoice getByChildEbObject(int childEbObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Object to object
		DetachedCriteria dcOto = DetachedCriteria.forClass(ObjectToObject.class);
		dcOto.setProjection(Projections.property(ObjectToObject.FIELDS.fromObjectId.name()));
		dcOto.add(Restrictions.eq(ObjectToObject.FIELDS.toObjectId.name(), childEbObjectId));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.ebObjectId.name(), dcOto));
		return get(dc);
	}

	@Override
	public Page<ApInvoiceDto> getInvGsReferences(Integer companyId, Integer divisionId, Integer supplierId,
			Integer supplierAcctId, Integer invGsNumber, String bmsNumber, Date dateFrom, Date dateTo, Integer status,
			PageSetting pageSetting) {
		String sql = "SELECT AP_INVOICE_ID, SEQUENCE_NO, GL_DATE, BMS_NUMBER, SUPPLIER_NAME, SUPPLIER_ACCT_NAME, "
				+ "SUM(API_AMOUNT) AS TOTAL_GS_AMOUNT, SUM(RTS_AMOUNT) AS TOTAL_RTS_AMOUNT FROM ( "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, (SI.QUANTITY * COALESCE(SI.UNIT_COST, 0)) AS API_AMOUNT, 0 AS RTS_AMOUNT "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) "
				+ "AND OTO1.OR_TYPE_ID = 24001 "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND API.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (invGsNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, (RRI.QUANTITY * COALESCE(RRI.UNIT_COST, 0)) AS API_AMOUNT, 0 AS RTS_AMOUNT "
				+ "FROM AP_INVOICE_GOODS RRI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) "
				+ "AND FW.IS_COMPLETE = 1  "
				+ "AND API.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (invGsNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, APIL.AMOUNT AS API_AMOUNT, 0 AS RTS_AMOUNT "
				+ "FROM AP_INVOICE_LINE APIL "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = APIL.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) "
				+ "AND FW.IS_COMPLETE = 1 "
				+ "AND API.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (invGsNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS API_AMOUNT, (SI.QUANTITY * COALESCE(SI.UNIT_COST, 0)) AS RTS_AMOUNT "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (31, 32, 33, 34, 35, 36) "
				+ "AND OTO1.OR_TYPE_ID = 105 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND API.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (invGsNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS API_AMOUNT, APIL.AMOUNT AS RTS_AMOUNT "
				+ "FROM AP_INVOICE_LINE APIL "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = APIL.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (31, 32, 33, 34, 35, 36) "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND API.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (invGsNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT API.AP_INVOICE_ID, API.SEQUENCE_NO, API.GL_DATE, API.BMS_NUMBER, S.NAME AS SUPPLIER_NAME, "
				+ "SA.NAME AS SUPPLIER_ACCT_NAME, 0 AS API_AMOUNT, (RTSI.QUANTITY * COALESCE(RTSI.UNIT_COST, 0)) AS RTS_AMOUNT  "
				+ "FROM R_RETURN_TO_SUPPLIER_ITEM RTSI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RTSI.AP_INVOICE_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = API.SUPPLIER_ID "
				+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = API.SUPPLIER_ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE API.INVOICE_TYPE_ID IN (31, 32, 33, 34, 35, 36) "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND API.COMPANY_ID = ? "
				+ "AND API.DIVISION_ID = ? "
				+ (supplierId != null ? "AND API.SUPPLIER_ID = ? " : "")
				+ (supplierAcctId != null ? "AND API.SUPPLIER_ACCOUNT_ID = ? " : "")
				+ (invGsNumber != null ? "AND API.SEQUENCE_NO = ? " : "")
				+ (bmsNumber != null && !bmsNumber.isEmpty() ? "AND API.BMS_NUMBER = ? " : "")
				+ (dateFrom != null && dateTo != null ? "AND API.GL_DATE BETWEEN ? AND ? " : "")
				+ ") AS TBL GROUP BY BMS_NUMBER, SUPPLIER_NAME ";
		if (status == RReceivingReport.STATUS_UNUSED) {
			sql += "HAVING TOTAL_RTS_AMOUNT = 0 ";
		} else if (status == RReceivingReport.STATUS_USED) {
			sql += "HAVING TOTAL_RTS_AMOUNT != 0 AND (TOTAL_GS_AMOUNT - TOTAL_RTS_AMOUNT) > 0 ";
		} else {
			sql += "HAVING (TOTAL_GS_AMOUNT - TOTAL_RTS_AMOUNT) > 0 ";
		}
		sql += "ORDER BY GL_DATE DESC, SEQUENCE_NO DESC, SUPPLIER_NAME DESC";
		return getAllAsPage(sql, pageSetting, new InvoiceRefHandler(companyId, divisionId, supplierId,
				supplierAcctId, invGsNumber, bmsNumber, dateFrom, dateTo, 6));
	}

	@Override
	public APInvoice getApInvoiceGsByRR(Integer rrId) {
		DetachedCriteria dc = getDetachedCriteria();
		//Object to object (API GS)
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.ebObjectId.name(), otoDc));
		//AP Invoice RR
		DetachedCriteria apiRrDc = DetachedCriteria.forClass(APInvoice.class);
		apiRrDc.setProjection(Projections.property(APInvoice.FIELD.ebObjectId.name()));
		otoDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), apiRrDc));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), fwDc));
		//Receiving Report
		DetachedCriteria rrDc = DetachedCriteria.forClass(RReceivingReport.class);
		rrDc.setProjection(Projections.property(RReceivingReport.FIELD.apInvoiceId.name()));
		rrDc.add(Restrictions.eq(RReceivingReport.FIELD.id.name(), rrId));
		apiRrDc.add(Subqueries.propertyIn(APInvoice.FIELD.id.name(), rrDc));
		return get(dc);
	}

	@Override
	public List<APInvoice> getChildInvoicesByParentInvoiceObjectId(Integer parentObjectId) {
		//Child Invoice
		DetachedCriteria dc = getDetachedCriteria();
		//Parent Invoice
		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), parentObjectId));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.ebObjectId.name(), otoDc));
		//Form Workflow
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), fwDc));
		return getAll(dc);
	}

	@Override
	public Page<APInvoice> searchInvoices(int companyId, Integer divisionId, int invoiceTypeId, int supplierId,
			int supplierAcctId, int termId, String invoiceNumber, Date fromInvoiceDate,
			Date toInvoiceDate, Date fromGLDate, Date toGLDate, Date fromDueDate, Date toDueDate,
			Double fromAmount, Double toAmount, Integer fromSequenceNumber, Integer toSequenceNumber,
			int invoiceStatusId, int paymentStatusId, Date asOfDate, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria supplierAcctCriteria =  DetachedCriteria.forClass(SupplierAccount.class);
		supplierAcctCriteria.add(Restrictions.eq(SupplierAccount.FIELD.companyId.name(), companyId));
		supplierAcctCriteria.setProjection(Projections.property(SupplierAccount.FIELD.id.name()));
		//Division
		if (divisionId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.divisionId.name(), divisionId));
		//Invoice Type
		if (invoiceTypeId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		//Supplier
		if (supplierId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.supplierId.name(), supplierId));
		//Supplier Account
		if (supplierAcctId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.supplierAccountId.name(), supplierAcctId));
		//Term
		if (termId != -1)
			dc.add(Restrictions.eq(APInvoice.FIELD.termId.name(), termId));
		//Invoice Number
		if (!invoiceNumber.trim().isEmpty() && invoiceNumber != null)
			dc.add(Restrictions.like(APInvoice.FIELD.invoiceNumber.name(), "%"+invoiceNumber+"%"));
		//Invoice Date
		if (fromInvoiceDate != null && toInvoiceDate != null)
			dc.add(Restrictions.between(APInvoice.FIELD.invoiceDate.name(), fromInvoiceDate, toInvoiceDate));
		else if (fromInvoiceDate != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceDate.name(), fromInvoiceDate));
		else if (toInvoiceDate != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.invoiceDate.name(), toInvoiceDate));
		//GL Date
		if (fromGLDate != null && toGLDate != null)
			dc.add(Restrictions.between(APInvoice.FIELD.glDate.name(), fromGLDate, toGLDate));
		else if (fromGLDate != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.glDate.name(), fromGLDate));
		else if (toGLDate != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.glDate.name(), toGLDate));
		//Due Date
		if (fromDueDate != null && toDueDate != null && invoiceTypeId != InvoiceType.RTS_TYPE_ID)
			dc.add(Restrictions.between(APInvoice.FIELD.dueDate.name(), fromDueDate, toDueDate));
		else if (fromDueDate != null && invoiceTypeId != InvoiceType.RTS_TYPE_ID)
			dc.add(Restrictions.eq(APInvoice.FIELD.dueDate.name(), fromDueDate));
		else if (toDueDate != null && invoiceTypeId != InvoiceType.RTS_TYPE_ID)
			dc.add(Restrictions.eq(APInvoice.FIELD.dueDate.name(), toDueDate));
		//Amount
		if (fromAmount != null && toAmount != null)
			dc.add(Restrictions.between(APInvoice.FIELD.amount.name(), fromAmount, toAmount));
		else if (fromAmount != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.amount.name(), fromAmount));
		else if (toAmount != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.amount.name(), toAmount));
		//Sequence Number
		if (fromSequenceNumber != null && toSequenceNumber != null)
			dc.add(Restrictions.between(APInvoice.FIELD.sequenceNumber.name(),
					fromSequenceNumber, toSequenceNumber));
		else if (fromSequenceNumber != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.sequenceNumber.name(), fromSequenceNumber));
		else if (toSequenceNumber != null)
			dc.add(Restrictions.eq(APInvoice.FIELD.sequenceNumber.name(), toSequenceNumber));
		//Invoice Status
		if (invoiceStatusId != -1){
			DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
			workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			workflowCriteria.add(Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), invoiceStatusId));
			dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), workflowCriteria));
		}
		//Exclude RR invoice types and unused invoice types.
		addNotInCriteria(dc, APInvoice.FIELD.invoiceTypeId.name(),
				Arrays.asList(InvoiceType.RR_TYPE_ID, InvoiceType.RR_CENTRAL_TYPE_ID, InvoiceType.RTS_TYPE_ID, InvoiceType.RR_NSB3_TYPE_ID,
				InvoiceType.RR_NSB4_TYPE_ID, InvoiceType.RR_NSB5_TYPE_ID, InvoiceType.RR_NSB8_TYPE_ID, InvoiceType.RR_NSB8A_TYPE_ID, InvoiceType.RR_RAW_MAT_TYPE_ID,
				InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID, InvoiceType.DEBIT_MEMO_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID, InvoiceType.REGULAR_TYPE_ID, InvoiceType.PREPAID_TYPE_ID));
		//As of date
		if (asOfDate != null)
			dc.add(Restrictions.le(APInvoice.FIELD.glDate.name(), asOfDate));
		dc.addOrder(Order.asc(APInvoice.FIELD.glDate.name()));
		dc.createAlias("supplier", "supplier").addOrder(Order.asc("supplier.name"));
		dc.createAlias("supplierAccount", "supplierAccount").addOrder(Order.asc("supplierAccount.name"));
		dc.add(Subqueries.propertyIn(APInvoice.FIELD.supplierAccountId.name(), supplierAcctCriteria));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueInvoiceNoBySupplier(int supplierId, Integer invoiceId, String invoiceNumber) {
		DetachedCriteria dc = getDetachedCriteria();
		List<Integer> rtsTypes = Arrays.asList(InvoiceType.RTS_TYPE_ID, 
				InvoiceType.RTS_CENTRAL_TYPE_ID, InvoiceType.RTS_NSB3_TYPE_ID, InvoiceType.RTS_NSB4_TYPE_ID,
				InvoiceType.RTS_NSB5_TYPE_ID, InvoiceType.RTS_NSB8_TYPE_ID, InvoiceType.RTS_NSB8A_TYPE_ID,
				InvoiceType.RR_CENTRAL_TYPE_ID, InvoiceType.RR_NSB3_TYPE_ID, InvoiceType.RR_NSB4_TYPE_ID,
				InvoiceType.RR_NSB5_TYPE_ID, InvoiceType.RR_NSB8_TYPE_ID, InvoiceType.RR_NSB8A_TYPE_ID);
		dc.add(Restrictions.eq(APInvoice.FIELD.supplierId.name(), supplierId));
		if(invoiceId != null && invoiceId != 0) {
			dc.add(Restrictions.ne(APInvoice.FIELD.id.name(), invoiceId));
		}
		dc.add(Restrictions.eq(APInvoice.FIELD.invoiceNumber.name(), invoiceNumber));
		dc.add(Restrictions.not(Restrictions.in(APInvoice.FIELD.invoiceTypeId.name(), rtsTypes)));//Exclude RTS transactions
		return getAll(dc).size() == 0;
	}

	@Override
	public Page<APInvoice> retrieveReplenishments(ApprovalSearchParam searchParam,
			List<Integer> statuses, PageSetting pageSetting, int typeId) {
		HibernateCallback<Page<APInvoice>> hibernateCallback = new HibernateCallback<Page<APInvoice>>() {

			@Override
			public Page<APInvoice> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria dc = session.createCriteria(APInvoice.class);
				if(!searchParam.getSearchCriteria().trim().isEmpty()){
					dc.add(Restrictions.like(APInvoice.FIELD.sequenceNumber.name(),
							Integer.parseInt(searchParam.getSearchCriteria().trim())));
				}
				SearchCommonUtil.searchCommonParams(dc, null, "companyId",
						APInvoice.FIELD.divisionId.name(), APInvoice.FIELD.glDate.name(),
						APInvoice.FIELD.userCustodianId.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
				if(statuses.size() > 0){
					addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), statuses);
				}
				dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				dc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), dcWorkflow));
				dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), typeId));
				dc.addOrder(Order.desc(APInvoice.FIELD.sequenceNumber.name()));
				return getAll(dc, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public Integer generateReplenishmentSequenceNo(int invoiceTypeId, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(APInvoice.FIELD.sequenceNumber.name()));
		dc.add(Restrictions.eq(APInvoice.FIELD.companyId.name(), companyId));
		if(divisionId != null) {
			dc.add(Restrictions.eq(APInvoice.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Restrictions.eq(APInvoice.FIELD.invoiceTypeId.name(), invoiceTypeId));
		return generateSeqNo(dc);
	}

	@Override
	public List<AnnualAlphalistPayeesDetailDto> getAnnualAlphalistWTFinal(Integer companyId, Integer divisionId, Integer year,
			boolean isTinFormatted, String schedule) {
		//schedule handles a string as a list and comma separated. Format: "3,4,5".
		List<Object> objects = executeSP("GET_ALPHALIST_OF_PAYEES", companyId, divisionId, 1, 12 ,year,  schedule);
		List<AnnualAlphalistPayeesDetailDto> sDto = new ArrayList<AnnualAlphalistPayeesDetailDto>();
		if(objects != null && !objects.isEmpty()) {
			AnnualAlphalistPayeesDetailDto dto = null;
			for(Object obj: objects) {
				dto = new AnnualAlphalistPayeesDetailDto();
				Object[] row = (Object[]) obj;
				if((Integer) row[0] != null) {
					int index = 0;
					index++; // skip supplier id
					String tin = (String) row[index++];
					dto.setTin(isTinFormatted ? StringFormatUtil.processBirTinTo13Digits(tin) : StringFormatUtil.parseBIRTIN(tin));
					String branchCode = StringFormatUtil.parseBranchCode(tin);
					dto.setBranchCode(branchCode != null && !branchCode.isEmpty() ? branchCode : "");
					dto.setRegisteredName((String) row[index++]);
					dto.setEmployeeName((String) row[index++]);
					dto.setLastName((String) row[index++]);
					dto.setFirstName((String) row[index++]);
					dto.setMiddleName((String) row[index++]);
					dto.setAtcCode(((String) row[index++]).replaceAll("\\s+",""));//Remove spaces for atc code
					dto.setNatureOfIncomePayment((String) row[index++]);
					dto.setIncomePayment((Double) row[index++]);
					dto.setTaxRate((Double)row[index++]);
					dto.setActualAmtWthld((Double) row[index++]);
					index++;
					dto.setPayeesAddress((String) row[index++]);
					String statusCode = "A";
					dto.setStatusCode(statusCode);//TODO:need to add status residence code.
					sDto.add(dto);
				}
			}
		}
		return sDto;
	}

	@Override

	public Page<COCTaxDto> getWithholdingTaxesSummary(Integer companyId, Integer divisionId, Integer year, Integer month, String wtTaxType,
			PageSetting pageSetting) {
		String sql = "SELECT ATC_CODE, NATURE_OF_PAYMENT, TAX_RATE, TAX_AMOUNT FROM ( "
				+ "SELECT ATC_CODE, NATURE_OF_PAYMENT, TAX_RATE, SUM(TAX_AMOUNT) AS TAX_AMOUNT FROM ( "
				+ "SELECT ATC.NAME AS ATC_CODE, ATC.DESCRIPTION AS NATURE_OF_PAYMENT, WAS.VALUE AS TAX_RATE, SUM(AP.WT_AMOUNT) AS TAX_AMOUNT FROM AP_INVOICE AP "
				+ "INNER JOIN WT_ACCOUNT_SETTING WAS ON WAS.WT_ACCOUNT_SETTING_ID = AP.WT_ACCOUNT_SETTING_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AP.SUPPLIER_ID "
				+ "INNER JOIN BIR_ATC ATC ON ATC.BIR_ATC_ID = WAS.BIR_ATC_ID  "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND FIND_IN_SET(WAS.WT_TYPE_ID, ?) > 0 " // 1, 2 for expanded and 3, 4, 5 for final withholding taxes
				// Non-PO, goods/services, confidential, and AP loan
				+ "AND INVOICE_TYPE_ID IN (19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 37, 38, 39, 40, 41, 42, 49, 50, 51, 52, 53, 54) "
				+ "AND AP.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND AP.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(AP.GL_DATE)= ? " : "")
				+ "AND YEAR(AP.GL_DATE) = ? "
				+ "GROUP BY ATC.NAME "
				+ "UNION ALL "
				+ "SELECT ATC.NAME AS ATC_CODE, ATC.DESCRIPTION AS NATURE_OF_PAYMENT, WAS.VALUE AS TAX_RATE, "
				+ "SUM(-AP.WT_AMOUNT) AS TAX_AMOUNT FROM AP_INVOICE AP "
				+ "INNER JOIN WT_ACCOUNT_SETTING WAS ON WAS.WT_ACCOUNT_SETTING_ID = AP.WT_ACCOUNT_SETTING_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AP.SUPPLIER_ID "
				+ "INNER JOIN BIR_ATC ATC ON ATC.BIR_ATC_ID = WAS.BIR_ATC_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND FIND_IN_SET(WAS.WT_TYPE_ID, ?) > 0 " // 1, 2 for expanded and 3, 4, 5 for final withholding taxes
				// Return to supplier
				+ "AND INVOICE_TYPE_ID IN (31, 32, 33, 34, 35, 36) "
				+ "AND AP.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND AP.DIVISION_ID = ? " : "")
				+ (month != -1 ? "AND MONTH(AP.GL_DATE)= ? " : "")
				+ "AND YEAR(AP.GL_DATE) = ? "
				+ "GROUP BY ATC.NAME "
				+ ") AS TBL GROUP BY ATC_CODE "
				+ ") AS FTBL WHERE TAX_AMOUNT > 0 "; // Exclude negative and zero (0) tax amount values
			return getAllAsPage(sql, pageSetting, new MonthlySummaryWTHandler(companyId, divisionId, year, month, wtTaxType));
		}

	private static class MonthlySummaryWTHandler implements QueryResultHandler<COCTaxDto> {
		private final Integer companyId;
		private final Integer divisionId;
		private final Integer month;
		private final Integer year;
		private final String wtTaxType;

		private MonthlySummaryWTHandler (Integer companyId, Integer divisionId, Integer year, Integer month, String wtTaxType) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.month = month;
			this.year = year;
			this.wtTaxType=wtTaxType;
		}

		@Override
		public List<COCTaxDto> convert(List<Object[]> queryResult) {
			List<COCTaxDto> sDto = new ArrayList<COCTaxDto>();
			COCTaxDto dto = null;
			for (Object[] rowResult : queryResult) {
				dto = new COCTaxDto();
				dto.setAtc(((String) rowResult[0]).replaceAll("\\s+",""));
				dto.setNatureOfPayment((String) rowResult[1]);
				dto.setTaxRate(NumberFormatUtil.roundOffTo2DecPlaces((Double)rowResult[2]));
				dto.setTaxAmount(NumberFormatUtil.roundOffTo2DecPlaces((Double)rowResult[3]));
				sDto.add(dto);
			}
			return sDto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int noOfTables = 2;
			for (int i = 0; i < noOfTables; i++) {
				query.setParameter(index, wtTaxType);
				query.setParameter(++index, companyId);
				if (divisionId != -1 ) {
					query.setParameter(++index, divisionId);
				}
				if (month != -1 ) {//index of January is stating in 0
					query.setParameter(++index, month + 1);
				}
				query.setParameter(++index, year);
				if (i < (noOfTables-1)) {
					++index;
				}
			}

			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ATC_CODE", Hibernate.STRING);
			query.addScalar("NATURE_OF_PAYMENT", Hibernate.STRING);
			query.addScalar("TAX_RATE", Hibernate.DOUBLE);
			query.addScalar("TAX_AMOUNT", Hibernate.DOUBLE);
		}
	}

	public Page<COCTaxDto> getQuarterlySummaryWT(Integer companyId, Integer divisionId, Integer year, Integer quarter,
			PageSetting pageSetting) {
		String sql = "SELECT ATC_CODE, NATURE_OF_PAYMENT, TAX_BASE, TAX_RATE, TAX_AMOUNT FROM ( "
				+ "SELECT  TBL.ATC_CODE, TBL.DESCRIPTION AS NATURE_OF_PAYMENT, sum(TBL.AMOUNT) AS TAX_BASE , TBL.TAX_RATE, sum(TBL.WT_AMOUNT) AS TAX_AMOUNT FROM ( "
				+ "SELECT BA.NAME AS ATC_CODE, BA.DESCRIPTION,  "
				+ "ifnull((SELECT sum(AL.AMOUNT) FROM AP_LINE AL WHERE AL.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum(AIL.AMOUNT) FROM AP_INVOICE_LINE AIL WHERE AIL.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum(AII.AMOUNT) FROM AP_INVOICE_ITEM AII WHERE AII.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum((AIG.QUANTITY*AIG.UNIT_COST)) FROM AP_INVOICE_GOODS AIG WHERE AIG.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum(SI.UNIT_COST) FROM SERIAL_ITEM SI INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID AND OTO.OR_TYPE_ID = 24001 AND SI.ACTIVE = 1) , 0) "
				+ "AS AMOUNT, WAS.VALUE AS TAX_RATE, AI.WT_AMOUNT "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AI.SUPPLIER_ID "
				+ "INNER JOIN WT_ACCOUNT_SETTING WAS ON WAS.WT_ACCOUNT_SETTING_ID = AI.WT_ACCOUNT_SETTING_ID "
				+ "INNER JOIN BIR_ATC BA ON BA.BIR_ATC_ID = WAS.BIR_ATC_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE "
				//Invoices of Non PO, Goods and Services, Confidential,Loan
				+ "AND AI.INVOICE_TYPE_ID IN (19,20,21,22,23,24,25,26,27,28,29,30,37,38,39,40,41,42,49,50,51,52,53,54) "
				+ "AND WAS.WT_TYPE_ID IN (1,2) "
				+ "AND AI.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND AI.DIVISION_ID = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(AI.GL_DATE)= ? " : "")
				+ "AND (YEAR(AI.GL_DATE)) = ? "
				+ "UNION ALL "
				//Return to Supplier
				+ "SELECT BA.NAME AS ATC_CODE, BA.DESCRIPTION,-( "
				+ "ifnull((SELECT sum(AL.AMOUNT) FROM AP_LINE AL WHERE AL.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum(AIL.AMOUNT) FROM AP_INVOICE_LINE AIL WHERE AIL.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum(AII.AMOUNT) FROM AP_INVOICE_ITEM AII WHERE AII.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum((AIG.QUANTITY*AIG.UNIT_COST)) FROM AP_INVOICE_GOODS AIG WHERE AIG.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0)+ "
				+ "ifnull((SELECT sum(SI.UNIT_COST) FROM SERIAL_ITEM SI INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "WHERE OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID AND OTO.OR_TYPE_ID IN(24005, 105) AND SI.ACTIVE = 1), 0)+ "
				+ "ifnull((SELECT sum((RTSI.QUANTITY*RTSI.UNIT_COST)-ifnull(RTSI.DISCOUNT,0)) FROM R_RETURN_TO_SUPPLIER_ITEM RTSI WHERE RTSI.AP_INVOICE_ID = AI.AP_INVOICE_ID) , 0) "
				+ ")AS AMOUNT, -WAS.VALUE AS TAX_RATE, -AI.WT_AMOUNT "
				+ "FROM AP_INVOICE AI "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AI.SUPPLIER_ID "
				+ "INNER JOIN WT_ACCOUNT_SETTING WAS ON WAS.WT_ACCOUNT_SETTING_ID = AI.WT_ACCOUNT_SETTING_ID "
				+ "INNER JOIN BIR_ATC BA ON BA.BIR_ATC_ID = WAS.BIR_ATC_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE "
				+ "AND AI.INVOICE_TYPE_ID IN (31,32,33,34,35,36) "
				+ "AND WAS.WT_TYPE_ID IN (1,2) "
				+ "AND AI.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND AI.DIVISION_ID = ? " : "")
				+ (quarter != -1 ? "AND QUARTER(AI.GL_DATE)= ? " : "")
				+ "AND (YEAR(AI.GL_DATE)) = ? "
				+ ") as TBL GROUP BY TBL.ATC_CODE "
				+ ") AS FTBL WHERE TAX_AMOUNT > 0 ";
			return getAllAsPage(sql, pageSetting, new QuarterlySummaryWTtHandler(companyId, divisionId, year, quarter));
		}
	private static class QuarterlySummaryWTtHandler implements QueryResultHandler<COCTaxDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer quarter;
		private Integer year;

		private QuarterlySummaryWTtHandler (Integer companyId, Integer divisionId, Integer year, Integer quarter) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.quarter = quarter;
			this.year = year;
		}

		@Override
		public List<COCTaxDto> convert(List<Object[]> queryResult) {
			List<COCTaxDto> sDto = new ArrayList<COCTaxDto>();
			COCTaxDto dto = null;
			for (Object[] rowResult : queryResult) {
				dto = new COCTaxDto();
				dto.setAtc((String) rowResult[0]);
				dto.setNatureOfPayment((String) rowResult[1]);
				dto.setTaxBase((Double)rowResult[2]);
				dto.setTaxRate((Double)rowResult[3]);
				dto.setTaxAmount((Double)rowResult[4]);
				sDto.add(dto);
			}
			return sDto;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int noOfTables = 2;
			for (int i = 0; i < noOfTables; i++) {
				query.setParameter(index, companyId);
				if (divisionId != -1 ) {
					query.setParameter(++index, divisionId);
				}
				if (quarter != -1 ) {//index of first quarter is stating in 0
					query.setParameter(++index, quarter + 1);
				}
				query.setParameter(++index, year);
				if (i < (noOfTables-1)) {
					++index;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("ATC_CODE", Hibernate.STRING);
			query.addScalar("NATURE_OF_PAYMENT", Hibernate.STRING);
			query.addScalar("TAX_BASE", Hibernate.DOUBLE);
			query.addScalar("TAX_RATE", Hibernate.DOUBLE);
			query.addScalar("TAX_AMOUNT", Hibernate.DOUBLE);
		}
	}

	@Override
	public List<VATDeclarationDto> getQrtrlyValAddedTaxDeclrtnData(int companyId, int divisionId,
			int year, int quarter, int month) {
		List<Object> getVatDeclaration =
				executeSP("GET_VAT_DECLARATION", companyId, divisionId, year, quarter, month);
		List<VATDeclarationDto> vatDto = new ArrayList<VATDeclarationDto>();
		VATDeclarationDto dto = null;
		for (Object obj : getVatDeclaration) {
			Object[] row = (Object[]) obj;
			int counter = 0;
			dto = new VATDeclarationDto();
			dto.setName((String) row[counter++]);
			dto.setTaxBase(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[counter++]));
			dto.setVat(NumberFormatUtil.roundOffTo2DecPlaces((Double)row[counter++]));
			vatDto.add(dto);
			dto = null;
		}
		return vatDto;
	}

	public List<AnnualAlphalsitWTEDetailsDto> getAnnualAlphalistWTExpanded(int companyId, Integer divisionId,
			Integer monthFrom, Integer monthTo, Integer year, String schedTypeId, boolean isPDF) {
		List<AnnualAlphalsitWTEDetailsDto> dtos = new ArrayList<AnnualAlphalsitWTEDetailsDto>();
		List<Object> objects = executeSP("GET_ALPHALIST_OF_PAYEES", companyId, divisionId, 1, 12, year, schedTypeId);
		if (objects != null && !objects.isEmpty()) {
			AnnualAlphalsitWTEDetailsDto annualAWTdto = null;
			for (Object obj : objects) {
				Object[] row = (Object[]) obj;
				annualAWTdto = new AnnualAlphalsitWTEDetailsDto();
				String tin = (String) row[1];
				annualAWTdto.setTin(isPDF ? StringFormatUtil.processBirTinTo13Digits(tin) : tin);
				annualAWTdto.setRegistName((String)row[2]);
				annualAWTdto.setLastName((String)row[4]);
				annualAWTdto.setFirstName((String)row[5]);
				annualAWTdto.setMiddleName((String)row[6]);
				annualAWTdto.setAtcCode((String)row[7]);
				annualAWTdto.setIncomePayment((Double)row[9]);
				annualAWTdto.setTaxRate((Double)row[10]);
				annualAWTdto.setAmountTaxheld((Double)row[11]);
				dtos.add(annualAWTdto);
			}
		}
		return dtos;
	}

	@Override
	public List<MonthlyAlphalistPayeesDto> getMAPayees(int companyId, int divisionId, Integer fromMonth,
			Integer toMonth, Integer year, String schedTypeId) {
		List<MonthlyAlphalistPayeesDto> dtos = new ArrayList<MonthlyAlphalistPayeesDto>();
		List<Object> objects = executeSP("GET_ALPHALIST_OF_PAYEES", companyId, divisionId, fromMonth, toMonth, year, schedTypeId);
		if(objects != null && !objects.isEmpty()) {
			MonthlyAlphalistPayeesDto mapayees = null;
			for(Object obj : objects) {
				Object[] rowResult = (Object[]) obj;
				mapayees = new MonthlyAlphalistPayeesDto();
				mapayees.setTin(StringFormatUtil.processBirTinTo13Digits((String) rowResult[1]));
				String registeredName = (String)rowResult[2];
				mapayees.setRegisName((registeredName != null && !registeredName.trim().isEmpty()) ? registeredName : (String)rowResult[3]);
				mapayees.setAtcCode(((String)rowResult[7]).replaceAll("\\s+",""));
				mapayees.setNaturePayment((String)rowResult[8]);
				mapayees.setAmountTaxBase(NumberFormatUtil.roundOffTo2DecPlaces((double)rowResult[9]));
				mapayees.setTaxRate(NumberFormatUtil.roundOffTo2DecPlaces((double)rowResult[10]));
				mapayees.setTaxHeld(NumberFormatUtil.roundOffTo2DecPlaces((double)rowResult[11]));
				dtos.add(mapayees);
			}
		}
		return dtos;
	}
}
