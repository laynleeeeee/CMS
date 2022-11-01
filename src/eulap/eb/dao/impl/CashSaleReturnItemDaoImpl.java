package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.CashSaleReturnItemDao;
import eulap.eb.domain.hibernate.CashSale;
import eulap.eb.domain.hibernate.CashSaleReturn;
import eulap.eb.domain.hibernate.CashSaleReturnItem;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.CashSaleReturnDto;

/**
 * Implementing class of {@link CashSaleReturnItem}

 *
 */
public class CashSaleReturnItemDaoImpl extends BaseDao<CashSaleReturnItem> implements CashSaleReturnItemDao {

	@Override
	protected Class<CashSaleReturnItem> getDomainClass() {
		return CashSaleReturnItem.class;
	}

	@Override
	public List<CashSaleReturnItem> getCashSaleReturnItems(Integer cashSaleReturnId,
			Integer itemId, Integer warehouseId, boolean isExchangedItemsOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleReturnItem.FIELD.cashSaleReturnId.name(), cashSaleReturnId));
		if(isExchangedItemsOnly) {
			//Get only the exchanged items.
			dc.add(Restrictions.gt(CashSaleReturnItem.FIELD.quantity.name(), 0.0));
		}
		if(itemId != null) {
			dc.add(Restrictions.eq(CashSaleReturnItem.FIELD.itemId.name(), itemId));
		}
		if(warehouseId != null) {
			dc.add(Restrictions.eq(CashSaleReturnItem.FIELD.warehouseId.name(), warehouseId));
		}
		return getAll(dc);
	}

	@Override
	public double getTotalCSRAmount(Integer cashSaleReturnId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CashSaleReturnItem.FIELD.cashSaleReturnId.name(), cashSaleReturnId));
		dc.setProjection(Projections.sum(CashSaleReturnItem.FIELD.amount.name()));
		return getBySumProjection(dc);
	}

	@Override
	public List<CashSaleReturnItem> getCSaleReturnItems(int referenceId) {
		DetachedCriteria csrItemDc = getDetachedCriteria();
		DetachedCriteria csReturnDc = DetachedCriteria.forClass(CashSaleReturn.class);
		csReturnDc.setProjection(Projections.property(CashSaleReturn.FIELD.id.name()));
		csReturnDc.createAlias("formWorkflow", "fw");
		csReturnDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		csrItemDc.add(Restrictions.eq(CashSaleReturnItem.FIELD.cashSaleItemId.name(), referenceId));
		csrItemDc.add(Subqueries.propertyIn(CashSaleReturnItem.FIELD.cashSaleReturnId.name(), csReturnDc));
		return getAll(csrItemDc);
	}

	@Override
	public List<CashSaleReturnItem> getCSRItemsByReference(int cashSaleId) {
		DetachedCriteria dc = getDetachedCriteria();
		
		DetachedCriteria csReturnDc = DetachedCriteria.forClass(CashSaleReturn.class);
		csReturnDc.setProjection(Projections.property(CashSaleReturn.FIELD.id.name()));
		csReturnDc.add(Restrictions.eq(CashSaleReturn.FIELD.cashSaleId.name(), cashSaleId));
		csReturnDc.createAlias("formWorkflow", "fw");
		csReturnDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		
		dc.add(Subqueries.propertyIn(CashSaleReturnItem.FIELD.cashSaleReturnId.name(), csReturnDc));
		return getAll(dc);
	}

	@Override
	public double getRemainingQty(int cashSaleItemId, boolean isCSAsReference) {
		StringBuffer sql = null;
		if(isCSAsReference) {
			sql = new StringBuffer("SELECT SUM(ORIG_QTY) AS ORIG_QTY, SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( "
					+ "SELECT CSI.QUANTITY AS ORIG_QTY, CSI.QUANTITY AS REMAINING_QTY "
					+ "FROM CASH_SALE_ITEM CSI "
					+ "INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 AND CSI.CASH_SALE_ITEM_ID = ? "
					+ "UNION ALL "
					+ "SELECT 0, CSRI.QUANTITY FROM CASH_SALE_RETURN_ITEM CSRI "
					+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 AND CSRI.CASH_SALE_ITEM_ID = ? "
					+ ") AS CS_REF_QTY");
		} else {
			sql = new StringBuffer("SELECT SUM(ORIG_QTY) AS ORIG_QTY, SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( "
					+ "SELECT CSRI.QUANTITY AS ORIG_QTY, CSRI.QUANTITY AS REMAINING_QTY "
					+ "FROM CASH_SALE_RETURN_ITEM CSRI "
					+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 AND CSRI.CASH_SALE_RETURN_ITEM_ID = ? "
					+ "UNION ALL "
					+ "SELECT 0, CSRI.QUANTITY FROM CASH_SALE_RETURN_ITEM CSRI "
					+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
					+ "INNER JOIN CASH_SALE_RETURN_ITEM CSRII ON CSRII.CASH_SALE_RETURN_ITEM_ID = CSRI.REF_CASH_SALE_RETURN_ITEM_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
					+ "WHERE FW.CURRENT_STATUS_ID != 4 AND CSRI.REF_CASH_SALE_RETURN_ITEM_ID = ? "
					+ ") AS CS_REF_QTY");
		}
		List<Double> remainingQties = getCsRemainingQty(sql.toString(), cashSaleItemId);
		return remainingQties.get(1); // Get the value from the REMAINING_QTY column
	}

	private List<Double> getCsRemainingQty(String sql, final int cashSaleItemId) {
		Collection<Double> begBalance = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {

					Double transaction = (Double) row[0];
					ret.add(transaction);
					Double receipt = (Double) row[1];
					ret.add(receipt);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index, cashSaleItemId);
				query.setParameter(++index, cashSaleItemId);
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ORIG_QTY", Hibernate.DOUBLE);
				query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			}
		});

		return (List<Double>) begBalance;
	}

	@Override
	public Page<CashSaleReturnDto> getCashSaleReturnRef(Integer referenceId, String refType, Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer refNumber, Date dateFrom, Date dateTo, Integer status, Integer typeId,
			User user, PageSetting pageSetting) {
		String sql = getCashSaleReturnStatement(referenceId, refType, companyId, arCustomerId, arCustomerAccountId, refNumber, dateFrom, dateTo, status, typeId, user);
		CashSaleReturnHandler handler = new CashSaleReturnHandler(referenceId, refType, companyId, arCustomerId,
				arCustomerAccountId, refNumber, dateFrom, dateTo, typeId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class CashSaleReturnHandler implements QueryResultHandler<CashSaleReturnDto> {
		private String refType;
		private Integer companyId;
		private Integer arCustomerId;
		private Integer arCustomerAccountId;
		private Integer refNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer typeId;
		private Integer referenceId;

		private CashSaleReturnHandler (Integer referenceId, String refType, Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
				Integer refNumber, Date dateFrom, Date dateTo, Integer typeId) {
			this.referenceId =referenceId;
			this.refType =refType;
			this.companyId =companyId;
			this.arCustomerId =arCustomerId;
			this.arCustomerAccountId =arCustomerAccountId;
			this.refNumber =refNumber;
			this.dateFrom =dateFrom;
			this.dateTo =dateTo;
			this.typeId =typeId;
		}

		@Override
		public List<CashSaleReturnDto> convert(List<Object[]> queryResult) {
			List<CashSaleReturnDto> saleReturnDtos = new ArrayList<CashSaleReturnDto>();
			CashSaleReturnDto saleReturnDto = null ;
			for (Object[] rowResult : queryResult) {
				int index = 0;
				saleReturnDto = new CashSaleReturnDto();
				saleReturnDto.setRefId((Integer) rowResult[index++]);
				saleReturnDto.setCompanyId((Integer) rowResult[index++]);
				saleReturnDto.setArCustomerId((Integer) rowResult[index++]);
				saleReturnDto.setArCustomerAcctId((Integer) rowResult[index++]);
				saleReturnDto.setSaleInvoice((String) rowResult[index++]);
				saleReturnDto.setSeqNo((String) rowResult[index++]);
				saleReturnDto.setCustomerName((String) rowResult[index++]);
				saleReturnDto.setCustomerAcctName((String) rowResult[index++]);
				saleReturnDto.setReference((String) rowResult[index++]);
				saleReturnDto.setCashSaleTypeId(typeId);
				saleReturnDto.setDate((Date) rowResult[index++]);
				saleReturnDtos.add(saleReturnDto);
			}
			return saleReturnDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, typeId);
			query.setParameter(++index, typeId);
			if (companyId != null && companyId != -1) {
				query.setParameter(++index, companyId);
			}
			if (arCustomerId != null && arCustomerId != -1) {
				query.setParameter(++index, arCustomerId);
			}
			if (arCustomerAccountId != null && arCustomerAccountId != -1) {
				query.setParameter(++index, arCustomerAccountId);
			}
			if (refNumber != null) {
				query.setParameter(++index, refNumber);
			}
			if(dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			}
			if (refType != null && refType != "") {
				query.setParameter(++index, refType);
			}
			if (referenceId != null && referenceId != 0) {
				query.setParameter(++index, referenceId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REF_ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("SALE_INVOICE_NO", Hibernate.STRING);
			query.addScalar("SEQ_NO", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCOUNT", Hibernate.STRING);
			query.addScalar("REF", Hibernate.STRING);
			query.addScalar("DATE", Hibernate.DATE);
		}
	}

	private String getCashSaleReturnStatement(Integer referenceId, String refType, Integer companyId, Integer arCustomerId,
			Integer arCustomerAccountId, Integer refNumber, Date dateFrom, Date dateTo, Integer status, Integer typeId,
			User user){
		String companyIds = StringFormatUtil.convertIntegersToString(user.getCompanyIds());
		String sql = "SELECT REF, REF_ID, COMPANY_ID, AR_CUSTOMER_ID, AR_CUSTOMER_ACCOUNT_ID, SALE_INVOICE_NO, "
				+ "DATE, SEQ_NO, REFERENCE_NO, CUSTOMER_NAME, CUSTOMER_ACCOUNT, "
				+ "STOCK_CODE, ITEM_ID, AMOUNT, ITEM_ADD_ON_ID, WAREHOUSE_ID, QUANTITY, ITEM_SRP_ID, "
				+ "ITEM_DISCOUNT_ID, SRP, UNIT_COST, DISCOUNT "
				+ "FROM ( "
				+ "SELECT 'CS' AS REF, CS.CASH_SALE_ID AS REF_ID, FW.CURRENT_STATUS_ID, "
				+ "CS.COMPANY_ID, CS.AR_CUSTOMER_ID, CS.AR_CUSTOMER_ACCOUNT_ID, CS.SALE_INVOICE_NO, "
				+ "COALESCE(CONCAT(C.COMPANY_CODE, ' ', CS.CS_NUMBER), CS.CS_NUMBER) as SEQ_NO, "
				+ "CS.CS_NUMBER AS REFERENCE_NO, "
				+ "CS.RECEIPT_DATE AS DATE, ARC.NAME AS CUSTOMER_NAME, "
				+ "ARCA.NAME AS CUSTOMER_ACCOUNT, CS.CREATED_DATE, "
				+ "CSI.ITEM_ID, I.STOCK_CODE, CSI.AMOUNT, CSI.ITEM_ADD_ON_ID, CSI.WAREHOUSE_ID, CSI.QUANTITY,"
				+ "CSI.ITEM_SRP_ID, CSI.ITEM_DISCOUNT_ID, CSI.SRP, CSI.UNIT_COST, CSI.DISCOUNT "
				+ "FROM CASH_SALE_ITEM CSI "
				+ "INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON CS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = CS.COMPANY_ID "
				+ "INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = CS.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ARCA ON ARCA.AR_CUSTOMER_ACCOUNT_ID = CS.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ITEM I ON CSI.ITEM_ID = I.ITEM_ID "
				+ "WHERE CASH_SALE_TYPE_ID = ? AND FW.IS_COMPLETE = 1 AND CSI.QUANTITY > 0 ";
			if (status != null && status != CashSale.STATUS_ALL) {
				if (status == CashSale.STATUS_USED) {
					sql += "AND EXISTS (SELECT COALESCE(CSRB.CASH_SALE_ID, 0) FROM CASH_SALE_RETURN CSRB "
							+ "INNER JOIN FORM_WORKFLOW FW ON CSRB.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID WHERE FW.CURRENT_STATUS_ID != 4 "
							+ "AND CSRB.CASH_SALE_ID = CS.CASH_SALE_ID) ";
				} else if (status == CashSale.STATUS_UNUSED){
					sql += "AND NOT EXISTS (SELECT COALESCE(CSRB.CASH_SALE_ID, 0) FROM CASH_SALE_RETURN CSRB "
							+ "INNER JOIN FORM_WORKFLOW FW ON CSRB.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID WHERE FW.CURRENT_STATUS_ID != 4 "
							+ "AND CSRB.CASH_SALE_ID = CS.CASH_SALE_ID) ";
				}
			}
			if(companyIds != null && !companyIds.trim().isEmpty()){
				sql += "AND CS.COMPANY_ID IN ("+companyIds+") ";
			}
			sql += "UNION ALL "
				+ "SELECT 'CSR' AS REF, CSR.CASH_SALE_RETURN_ID AS REF_ID, FW.CURRENT_STATUS_ID, "
				+ "CSR.COMPANY_ID, CSR.AR_CUSTOMER_ID, CSR.AR_CUSTOMER_ACCOUNT_ID, CSR.SALE_INVOICE_NO, "
				+ "COALESCE(CONCAT(C.COMPANY_CODE, ' ', CSR.CSR_NUMBER), CSR.CSR_NUMBER) as SEQ_NO, "
				+ "CSR.CSR_NUMBER AS REFERENCE_NO, "
				+ "CSR.DATE AS DATE, ARC.NAME AS CUSTOMER_NAME, "
				+ "ARCA.NAME AS CUSTOMER_ACCOUNT, CSR.CREATED_DATE, "
				+ "CSRI.ITEM_ID, I.STOCK_CODE, CSRI.AMOUNT, CSRI.ITEM_ADD_ON_ID, CSRI.WAREHOUSE_ID, CSRI.QUANTITY, "
				+ "CSRI.ITEM_SRP_ID, CSRI.ITEM_DISCOUNT_ID, CSRI.SRP, CSRI.UNIT_COST, CSRI.DISCOUNT "
				+ "FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON CSR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "INNER JOIN COMPANY C ON C.COMPANY_ID = CSR.COMPANY_ID "
				+ "INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = CSR.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ARCA ON ARCA.AR_CUSTOMER_ACCOUNT_ID = CSR.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN ITEM I ON CSRI.ITEM_ID = I.ITEM_ID "
				+ "WHERE CASH_SALE_TYPE_ID = ? AND FW.IS_COMPLETE = 1 AND CSRI.QUANTITY > 0 ";
			if (status != null && status != CashSale.STATUS_ALL) {
				if (status == CashSale.STATUS_USED) {
					sql += "AND EXISTS (SELECT COALESCE(CSRB.REF_CASH_SALE_RETURN_ID, 0) FROM CASH_SALE_RETURN CSRB "
							+ "INNER JOIN FORM_WORKFLOW FW ON CSRB.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID WHERE FW.CURRENT_STATUS_ID != 4 "
							+ "AND CSRB.REF_CASH_SALE_RETURN_ID=CSR.REF_CASH_SALE_RETURN_ID) ";
				} else if (status == CashSale.STATUS_UNUSED){
					sql += "AND NOT EXISTS (SELECT COALESCE(CSRB.REF_CASH_SALE_RETURN_ID, 0) FROM CASH_SALE_RETURN CSRB "
							+ "INNER JOIN FORM_WORKFLOW FW ON CSRB.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID WHERE FW.CURRENT_STATUS_ID != 4 "
							+ "AND CSRB.REF_CASH_SALE_RETURN_ID=CSR.REF_CASH_SALE_RETURN_ID) ";
				}
			}
			if(companyIds != null && !companyIds.trim().isEmpty()){
				sql += "AND CSR.COMPANY_ID IN ("+companyIds+") ";
			}
			sql += ") AS CSR_TBL "
				+ "WHERE CURRENT_STATUS_ID != 4 ";
			if(companyId != null && companyId != -1){
				sql +=	"AND COMPANY_ID = ? ";
			}
			if(arCustomerId != null && arCustomerId != -1){
				sql += "AND AR_CUSTOMER_ID = ? ";
			}
			if(arCustomerAccountId != null && arCustomerAccountId != -1){
				sql += "AND AR_CUSTOMER_ACCOUNT_ID = ? ";
			}
			if(refNumber != null){
				sql += "AND REFERENCE_NO = ? ";
			}
			if(dateFrom != null && dateTo != null){
				sql += "AND (DATE BETWEEN ? AND ?) ";
			}
			if(refType != null && refType != ""){
				sql += "AND REF = ? "
					+"AND REF_ID = ? ";
			}
			sql += "GROUP BY REF_ID, REF ORDER BY DATE DESC, CREATED_DATE DESC, SEQ_NO DESC";
			return sql; 
	}

	@Override
	public List<CashSaleReturnItem> getCSRItemsByReferenceCSR(int referenceId, int cashSaleReturnId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria csReturnDc = DetachedCriteria.forClass(CashSaleReturn.class);
		csReturnDc.setProjection(Projections.property(CashSaleReturn.FIELD.id.name()));
		csReturnDc.add(Restrictions.eq(CashSaleReturn.FIELD.refCashSaleReturnId.name(), referenceId));
		csReturnDc.add(Restrictions.ne(CashSaleReturn.FIELD.id.name(), cashSaleReturnId));
		csReturnDc.createAlias("formWorkflow", "fw");
		csReturnDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));

		dc.add(Subqueries.propertyIn(CashSaleReturnItem.FIELD.cashSaleReturnId.name(), csReturnDc));
		return getAll(dc);
	}

	@Override
	public double getRemainingQty(int referenceId, int itemId, int warehouseId, boolean isCSAsReference) {
		StringBuffer sql = null;
		if(isCSAsReference) {
			sql = new StringBuffer("SELECT SUM(ORIG_QTY) AS ORIG_QTY, "
				+ "SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( "
				+ "SELECT CSI.QUANTITY AS ORIG_QTY, CSI.QUANTITY AS REMAINING_QTY "
				+ "FROM CASH_SALE_ITEM CSI "
				+ "INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 AND CSI.CASH_SALE_ID = ? AND CSI.ITEM_ID = ? "
				+ "AND CSI.WAREHOUSE_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0, CSRI.QUANTITY FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 AND CSR.CASH_SALE_ID = ? AND CSRI.ITEM_ID = ? "
				+ "AND CSRI.WAREHOUSE_ID = ? "
				+ ") AS CSR_REF_QTY");
		} else {
			sql = new StringBuffer("SELECT SUM(ORIG_QTY) AS ORIG_QTY, SUM(REMAINING_QTY) AS REMAINING_QTY FROM ( "
				+ "SELECT CSRI.QUANTITY AS ORIG_QTY, CSRI.QUANTITY AS REMAINING_QTY "
				+ "FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 AND CSRI.CASH_SALE_RETURN_ID = ? "
				+ "AND CSRI.ITEM_ID = ? AND CSRI.WAREHOUSE_ID = ? "
				+ "AND CSRI.QUANTITY > 0 "
				+ "UNION ALL "
				+ "SELECT 0, CSRI.QUANTITY FROM CASH_SALE_RETURN_ITEM CSRI "
				+ "INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID "
				+ "INNER JOIN CASH_SALE_RETURN_ITEM CSRII ON CSRII.CASH_SALE_RETURN_ITEM_ID = CSRI.REF_CASH_SALE_RETURN_ITEM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 AND CSR.REF_CASH_SALE_RETURN_ID = ? "
				+ "AND CSRI.ITEM_ID = ? AND CSRI.WAREHOUSE_ID = ? "
				+ ") AS CSR_REF_QTY");
		}
		List<Double> remainingQties = getRemainingQty(sql.toString(), referenceId, itemId, warehouseId);
		return remainingQties.get(1); // Get the value from the REMAINING_QTY column
	}

	private List<Double> getRemainingQty(String sql, final int referenceId, final int itemId,
			final int warehouseId) {
		Collection<Double> begBalance = get(sql, new QueryResultHandler<Double>() {

			@Override
			public List<Double> convert(List<Object[]> queryResult) {
				List<Double> ret = new ArrayList<Double>();
				for (Object[] row : queryResult) {

					Double transaction = (Double) row[0];
					ret.add(transaction);
					Double receipt = (Double) row[1];
					ret.add(receipt);
					break; // Expecting one row only.
				}
				return ret;
			}

			@Override
			public int setParamater(SQLQuery query) {
				int index = 0;
				query.setParameter(index, referenceId);
				query.setParameter(++index, itemId);
				query.setParameter(++index, warehouseId);
				query.setParameter(++index, referenceId);
				query.setParameter(++index, itemId);
				query.setParameter(++index, warehouseId);
				return index;
			}

			@Override
			public void setScalars(SQLQuery query) {
				query.addScalar("ORIG_QTY", Hibernate.DOUBLE);
				query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			}
		});
		return (List<Double>) begBalance;
	}
}