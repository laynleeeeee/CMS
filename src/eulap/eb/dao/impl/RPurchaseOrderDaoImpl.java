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
import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.BmsTrackerReportDto;
import eulap.eb.web.dto.PoRegisterDto;
/**
 * DAO Implementation of {@link RPurchaseOrderDao}

 *
 */
public class RPurchaseOrderDaoImpl extends BaseDao<RPurchaseOrder> implements RPurchaseOrderDao{

	@Override
	protected Class<RPurchaseOrder> getDomainClass() {
		return RPurchaseOrder.class;
	}

	@Override
	public Integer getMaxPONumber(int companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(RPurchaseOrder.FIELD.poNumber.name()));
		dc.add(Restrictions.eq(RPurchaseOrder.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			dc.add(Restrictions.eq(RPurchaseOrder.FIELD.divisionId.name(), divisionId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public Page<RPurchaseOrder> searchPOs(Integer divisionId, String criteria, PageSetting pageSetting) {
		DetachedCriteria poCriteria = getDetachedCriteria();
		if (divisionId != null) {
			poCriteria.add(Restrictions.eq(RPurchaseOrder.FIELD.divisionId.name(), divisionId));
		}
		if (StringFormatUtil.isNumeric(criteria)) {
			poCriteria.add(Restrictions.sqlRestriction("PO_NUMBER LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		poCriteria.addOrder(Order.asc(RPurchaseOrder.FIELD.poNumber.name()));
		return getAll(poCriteria, pageSetting);
	}

	@Override
	public Page<RPurchaseOrder> getAllPOsByStatus(final ApprovalSearchParam searchParam, final List<Integer> formStatusIds,
			final PageSetting pageSetting) {
		return getAllPOsByStatus(0, searchParam, formStatusIds, pageSetting);
	}

	private Page<RPurchaseOrder> getAllPOsByStatus(final int typeId, final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting) {
		HibernateCallback<Page<RPurchaseOrder>> hibernateCallback = new HibernateCallback<Page<RPurchaseOrder>>() {
			@Override
			public Page<RPurchaseOrder> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria poCriteria = session.createCriteria(RPurchaseOrder.class);
				if (typeId != 0) {
					poCriteria.add(Restrictions.eq(RPurchaseOrder.FIELD.divisionId.name(), typeId));
				}
				if (StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					poCriteria.add(Restrictions.sqlRestriction("PO_NUMBER LIKE ?",
							searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(poCriteria, null, "companyId",
						RPurchaseOrder.FIELD.poDate.name(), RPurchaseOrder.FIELD.poDate.name(),
						RPurchaseOrder.FIELD.poDate.name(), searchParam.getUser().getCompanyIds(), searchParam);
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				if (!formStatusIds.isEmpty()) {
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				}
				workflowCriteria.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				poCriteria.add(Subqueries.propertyIn(RPurchaseOrder.FIELD.formWorkflowId.name(), workflowCriteria));
				poCriteria.addOrder(Order.desc(RPurchaseOrder.FIELD.poDate.name()));
				poCriteria.addOrder(Order.desc(RPurchaseOrder.FIELD.poNumber.name()));
				return getAll(poCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	private Page<RPurchaseOrder> getReferencePurchaseOrders(User user, Integer companyId, Integer divisionId, Integer supplierId, String poNumber,
			String bmsNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting) {
		String sql ="SELECT * FROM ("
				+ "SELECT PO_ID, RR_QUANTITY AS TOTAL_RR_QUANTITY, SUM(PO_QUANTITY) AS TOTAL_PO_QUANTITY, REMARKS, PO_DATE, PO_NUMBER, BMS_NUMBER, SUPPLIER_NAME FROM ( "
				+ "SELECT PO_ID, SUM(QUANTITY) AS RR_QUANTITY, 0 AS PO_QUANTITY, REMARKS, PO_DATE, PO_NUMBER, BMS_NUMBER, SUPPLIER_NAME FROM ( "
				+ "SELECT SUM(RRI.QUANTITY) AS QUANTITY, PO.R_PURCHASE_ORDER_ID AS PO_ID, PO.REMARKS, "
				+ "PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, S.NAME AS SUPPLIER_NAME "
				+ "FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT PO_OTO ON PO_OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = PO_OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != ? "
				+ "AND OTO.OR_TYPE_ID = 1 AND PO_OTO.OR_TYPE_ID = 74 "
				+ (poNumber != null && !poNumber.trim().isEmpty() ? "AND PO.PO_NUMBER LIKE ? " : "")
				+ (companyId != null ? "AND PO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND PO.DIVISION_ID = ? " : "")
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (bmsNumber != null && !bmsNumber.trim().isEmpty() ? "AND PO.BMS_NUMBER LIKE ? " : "");
			if(dateFrom != null && dateTo != null){
				sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
			} else if (dateFrom != null) {
				sql += "AND PO.PO_DATE >= ? ";
			} else if (dateTo != null) {
				sql += "AND PO.PO_DATE <= ? ";
			}
			sql += "GROUP BY PO.R_PURCHASE_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT SUM(SI.QUANTITY) AS QUANTITY, PO.R_PURCHASE_ORDER_ID, PO.REMARKS, "
				+ "PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, S.NAME AS SUPPLIER_NAME "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT PO_OTO ON PO_OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = PO_OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE OTO.OR_TYPE_ID = 64 AND PO_OTO.OR_TYPE_ID = 74 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != ? "
				+ (poNumber != null && !poNumber.trim().isEmpty() ? "AND PO.PO_NUMBER LIKE ? " : "")
				+ (companyId != null ? "AND PO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND PO.DIVISION_ID = ? " : "")
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (bmsNumber != null && !bmsNumber.trim().isEmpty() ? "AND PO.BMS_NUMBER LIKE ? " : "");
			if(dateFrom != null && dateTo != null){
				sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
			} else if (dateFrom != null) {
				sql += "AND PO.PO_DATE >= ? ";
			} else if (dateTo != null) {
				sql += "AND PO.PO_DATE <= ? ";
			}
			sql += "GROUP BY PO.R_PURCHASE_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT SUM(COALESCE(APL.QUANTITY, 0)), PO.R_PURCHASE_ORDER_ID AS PO_ID, P"
				+ "O.REMARKS, PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, S.NAME AS SUPPLIER_NAME "
				+ "FROM AP_INVOICE_LINE APL "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = APL.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT PO_OTO ON PO_OTO.TO_OBJECT_ID = API.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = PO_OTO.FROM_OBJECT_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE PO_OTO.OR_TYPE_ID = 74 AND OTO.OR_TYPE_ID = 13 "
				+ "AND FW.CURRENT_STATUS_ID != ? "
				+ (poNumber != null && !poNumber.trim().isEmpty() ? "AND PO.PO_NUMBER LIKE ? " : "")
				+ (companyId != null ? "AND PO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND PO.DIVISION_ID = ? " : "")
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (bmsNumber != null && !bmsNumber.trim().isEmpty() ? "AND PO.BMS_NUMBER LIKE ? " : "");
			if(dateFrom != null && dateTo != null){
				sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
			} else if (dateFrom != null) {
				sql += "AND PO.PO_DATE >= ? ";
			} else if (dateTo != null) {
				sql += "AND PO.PO_DATE <= ? ";
			}
			sql += "GROUP BY PO.R_PURCHASE_ORDER_ID "
				+ ") AS REMAINING GROUP BY PO_ID "
				+ "UNION ALL "
				+ "SELECT PO.R_PURCHASE_ORDER_ID AS PO_ID, 0 AS RR_QUANTITY, SUM(POL.QUANTITY) AS PO_QUANTITY, "
				+ "PO.REMARKS, PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, S.NAME AS SUPPLIER_NAME "
				+ "FROM PURCHASE_ORDER_LINE POL "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON POL.R_PURCHASE_ORDER_ID = PO.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != ? "
				+ (poNumber != null && !poNumber.trim().isEmpty() ? "AND PO.PO_NUMBER LIKE ? " : "")
				+ (companyId != null ? "AND PO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND PO.DIVISION_ID = ? " : "")
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (bmsNumber != null && !bmsNumber.trim().isEmpty() ? "AND PO.BMS_NUMBER LIKE ? " : "");
			if(dateFrom != null && dateTo != null){
				sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
			} else if (dateFrom != null) {
				sql += "AND PO.PO_DATE >= ? ";
			} else if (dateTo != null) {
				sql += "AND PO.PO_DATE <= ? ";
			}
			sql += "GROUP BY PO.R_PURCHASE_ORDER_ID "
				+ "UNION ALL "
				+ "SELECT PO.R_PURCHASE_ORDER_ID AS PO_ID, 0 AS RR_QUANTITY, SUM(POI.QUANTITY) AS PO_QUANTITY, "
				+ "PO.REMARKS, PO.PO_DATE, PO.PO_NUMBER, PO.BMS_NUMBER, S.NAME AS SUPPLIER_NAME "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != ? "
				+ (poNumber != null && !poNumber.trim().isEmpty() ? "AND PO.PO_NUMBER LIKE ? " : "")
				+ (companyId != null ? "AND PO.COMPANY_ID = ? " : "")
				+ (divisionId != null ? "AND PO.DIVISION_ID = ? " : "")
				+ (supplierId != null ? "AND PO.SUPPLIER_ID = ? " : "")
				+ (bmsNumber != null && !bmsNumber.trim().isEmpty() ? "AND PO.BMS_NUMBER LIKE ? " : "");
			if(dateFrom != null && dateTo != null){
				sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
			} else if (dateFrom != null) {
				sql += "AND PO.PO_DATE >= ? ";
			} else if (dateTo != null) {
				sql += "AND PO.PO_DATE <= ? ";
			}
			sql += "GROUP BY PO.R_PURCHASE_ORDER_ID "
			+ ") AS BALANCE GROUP BY PO_ID) AS TBL ";
			if(status == RReceivingReport.STATUS_UNUSED){
				sql += "WHERE TOTAL_RR_QUANTITY = 0 ";
			} else if (status == RReceivingReport.STATUS_USED) {
				sql += "WHERE TOTAL_RR_QUANTITY != 0 AND  TOTAL_RR_QUANTITY != TOTAL_PO_QUANTITY ";
			} else if (status == RReceivingReport.STATUS_ALL) {
				sql += "WHERE TOTAL_RR_QUANTITY != TOTAL_PO_QUANTITY ";
			}
			sql += "ORDER BY PO_DATE DESC, PO_NUMBER DESC ";
		RPurchaseOrderHandler poHandler = new RPurchaseOrderHandler(companyId, divisionId, supplierId, poNumber, bmsNumber,
				dateFrom, dateTo);
		return getAllAsPage(sql, pageSetting, poHandler);
	}

	@Override
	public Page<RPurchaseOrder> getPurchaseOrders(User user, Integer companyId, Integer supplierId, String poNumber,
			Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting) {
		return getReferencePurchaseOrders(user, companyId, null, supplierId, null, poNumber, dateFrom, dateTo, status, pageSetting);
	}

	@Override
	public Page<RPurchaseOrder> getPurchaseOrders(User user, Integer companyId, Integer divisionId, Integer supplierId, String poNumber,
			String bmsNumber, Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting) {
		return getReferencePurchaseOrders(user, companyId, divisionId, supplierId, poNumber, bmsNumber, dateFrom, dateTo, status, pageSetting);
	}

	private static class RPurchaseOrderHandler implements QueryResultHandler<RPurchaseOrder> {
		private final Integer companyId;
		private final Integer supplierId;
		private final String poNumber;
		private final Date dateFrom;
		private final Date dateTo;
		private final Integer divisionId;
		private final String bmsNumber;

		private RPurchaseOrderHandler (Integer companyId, Integer divisionId, Integer supplierId, String poNumber, String bmsNumber,
				Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.supplierId = supplierId;
			this.bmsNumber = bmsNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.divisionId = divisionId;
			this.poNumber = poNumber;
		}

		@Override
		public List<RPurchaseOrder> convert(List<Object[]> queryResult) {
			List<RPurchaseOrder> purchaseOrders = new ArrayList<RPurchaseOrder>();
			RPurchaseOrder purchaseOrder = null;
			int index = 0;
			for (Object[] row : queryResult) {
				index = 0;
				purchaseOrder = new RPurchaseOrder();
				purchaseOrder.setRemarks((String) row[index++]);
				purchaseOrder.setPoDate((Date) row[index++]);
				purchaseOrder.setPoNumber((Integer) row[index++]);
				purchaseOrder.setId((Integer) row[index++]);
				purchaseOrder.setBmsNumber((String) row[index++]);
				purchaseOrder.setSupplierName((String) row[index++]);
				purchaseOrders.add(purchaseOrder);
			}
			return purchaseOrders;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int noOfTbls = 5;
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, FormStatus.CANCELLED_ID);
				if(poNumber != null && !poNumber.trim().isEmpty()){
					query.setParameter(++index, StringFormatUtil.appendWildCard(poNumber));
				}
				if(companyId != null){
					query.setParameter(++index, companyId);
				}
				if(divisionId != null){
					query.setParameter(++index, divisionId);
				}
				if(supplierId != null){
					query.setParameter(++index, supplierId);
				}
				if(bmsNumber != null && !bmsNumber.trim().isEmpty()){
					query.setParameter(++index, StringFormatUtil.appendWildCard(bmsNumber));
				}
				if(dateFrom != null && dateTo != null){
					query.setParameter(++index, dateFrom);
					query.setParameter(++index, dateTo);
				} else if (dateFrom != null) {
					query.setParameter(++index, dateFrom);
				} else if (dateTo != null){
					query.setParameter(++index, dateTo);
				}
				if (i < (noOfTbls-1)) {
					index++;
				}
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REMARKS", Hibernate.STRING);
			query.addScalar("PO_DATE", Hibernate.DATE);
			query.addScalar("PO_NUMBER", Hibernate.INTEGER);
			query.addScalar("PO_ID", Hibernate.INTEGER);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
		}
	}

	@Override
	public Double getLatestUCPerSupplierAndItem(int itemId, int supplierAcctId) {
		StringBuilder latestUCSql =
				new StringBuilder("SELECT PO_NUMBER, CREATED_DATE, UNIT_COST FROM( "
					+ "SELECT RPOI.R_PURCHASE_ORDER_ITEM_ID AS ID, "
					+ "RPO.PO_NUMBER AS PO_NUMBER, "
					+ "RPOI.UNIT_COST AS UNIT_COST, RPO.CREATED_DATE "
					+ "FROM R_PURCHASE_ORDER_ITEM RPOI "
					+ "INNER JOIN R_PURCHASE_ORDER RPO ON RPO.R_PURCHASE_ORDER_ID = RPOI.R_PURCHASE_ORDER_ID "
					+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ID = RPO.SUPPLIER_ID "
					+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RPO.FORM_WORKFLOW_ID "
					+ "WHERE FW.IS_COMPLETE = 1 "
					+ "AND RPOI.ITEM_ID = ? "
					+ "AND SA.SUPPLIER_ACCOUNT_ID = ?) AS LATEST_UC "
					+ "ORDER BY CREATED_DATE DESC LIMIT 1");
		Collection<Double> latestUC = get(latestUCSql.toString(), new LatestUcHandler(itemId, supplierAcctId));
		if(!(latestUC.isEmpty())) {
			return latestUC.iterator().next();
		}
		return 0.0;
	}

	private static class LatestUcHandler implements QueryResultHandler<Double> {
		private int itemId;
		private int supplierAcctId;

		private LatestUcHandler (int itemId, int supplierAcctId) {
			this.itemId = itemId;
			this.supplierAcctId = supplierAcctId;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			List<Double> ret = new ArrayList<Double>();
			for (Object[] row : queryResult) {
				Double latestUc = (Double) row[2];
				if (latestUc == null) {
					ret.add(0.0);
					break;
				}

				ret.add(latestUc);
				break; // Expecting one row only.
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			query.setParameter(index, itemId);
			query.setParameter(++index, supplierAcctId);
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("PO_NUMBER", Hibernate.STRING);
			query.addScalar("CREATED_DATE", Hibernate.TIMESTAMP);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
		}
	}

	@Override
	public double getRemainingQty(Integer refenceObjectId, Integer wsId) {
		String sql = "SELECT SUM(QTY_FROM-QTY_TO) AS REMAINING_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT POI.QUANTITY AS QTY_FROM, 0 AS QTY_TO, POI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND POI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, WSI.QUANTITY AS QTY_TO, OO.FROM_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM WITHDRAWAL_SLIP_ITEM WSI "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WSOO ON WSOO.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = WSOO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WS.FORM_WORKFLOW_ID "
				+ "WHERE WSI.ACTIVE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.FROM_OBJECT_ID = ? ";
		if(wsId != null) {
			sql += "AND WS.WITHDRAWAL_SLIP_ID != ? ";
		}
		sql += ") TBL_REMAINING";
		Collection<Double> remainingQty = get(sql, new RemainingBalance(refenceObjectId, wsId));
		if(!remainingQty.isEmpty()) {
			return remainingQty.iterator().next();
		}
		return 0;
	}

	private static class RemainingBalance implements QueryResultHandler<Double> {
		private Integer refenceObjectId;
		private Integer wsId;

		private RemainingBalance (Integer refenceObjectId, Integer wsId) {
			this.refenceObjectId = refenceObjectId;
			this.wsId = wsId;
		}

		@Override
		public List<Double> convert(List<Object[]> queryResult) {
			List<Double> ret = new ArrayList<Double>();
			for (Object[] row : queryResult) {
				Double remainingQty = (Double) row[0];
				if (remainingQty == null) {
					ret.add(0.0);
					break;
				}
				ret.add(remainingQty);
				break; // Expecting one row only.
			}
			return ret;
		}

		@Override
		public int setParamater(SQLQuery query) {
			Integer index = 0;
			query.setParameter(index, refenceObjectId);
			query.setParameter(++index, refenceObjectId);
			if(wsId != null) {
				query.setParameter(++index, wsId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			query.addScalar("REF_OBJECT_ID", Hibernate.INTEGER);
		}
	}

	@Override
	public List<PoRegisterDto> getPoRegisterData(Integer companyId, Integer divisionId, Integer supplierId, Integer supplierAccountId,
			Integer termId, Date poDateFrom, Date poDateTo, Date rrDateFrom, Date rrDateTo, Integer statusId, String deliveryStatus) {
		String sql = "SELECT PURCHASE_ORDER_ID, DIVISION, PO_NUMBER, PO_DATE, AMOUNT, EST_DELIVERY_DATE, RR_NUMBER, RR_DATE, "
				+ "SUPPLIER_NAME, SUPPLIER_ACCOUNT_NAME, TERM_NAME, STOCK_CODE, DESCRIPTION, UOM, ORDERED_QTY, DELIVERED_QTY, "
				+ "(ORDERED_QTY - DELIVERED_QTY) AS BALANCE, (CASE WHEN (ORDERED_QTY - DELIVERED_QTY) = 0 THEN 'FULLY SERVED' "
				+ "WHEN (ORDERED_QTY - DELIVERED_QTY) < ORDERED_QTY THEN 'PARTIALLY SERVED' "
				+ "WHEN (ORDERED_QTY - DELIVERED_QTY) = ORDERED_QTY THEN 'UNSERVED' "
				+ "END) AS DELIVERY_STATUS, FORM_STATUS, CANCELLATION_REMARKS FROM ( "
				+ "SELECT PURCHASE_ORDER_ID, DIVISION, PO_NUMBER, PO_DATE, AMOUNT, EST_DELIVERY_DATE, ";
		sql += "COALESCE((SELECT GROUP_CONCAT(DISTINCT SEQUENCE_NO Separator ', ') FROM ( "
				+ "SELECT DISTINCT PO.R_PURCHASE_ORDER_ID, RRI.ITEM_ID, API.SEQUENCE_NO "
				+ "FROM R_RECEIVING_REPORT_ITEM RRI "
				+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
				+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
				+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 13) OTO1 ON OTO1.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != ? ";
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE <= ? ";
		}
		sql += "UNION ALL "
			+ "SELECT PO.R_PURCHASE_ORDER_ID, SI.ITEM_ID, API.SEQUENCE_NO "
			+ "FROM SERIAL_ITEM SI "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 64) OTO2 ON OTO2.TO_OBJECT_ID = SI.EB_OBJECT_ID "
			+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO2.FROM_OBJECT_ID "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 74) OTO1 ON OTO1.TO_OBJECT_ID = API.EB_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.R_PURCHASE_ORDER_ID = PO.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 "
			+ "AND POI.ITEM_ID = SI.ITEM_ID "
			+ "AND SI.ACTIVE = 1 ";
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE <= ? ";
		}
		sql += ") AS TBL3 WHERE TBL3.ITEM_ID = INNER_TBL.ITEM_ID AND TBL3.R_PURCHASE_ORDER_ID = INNER_TBL.PURCHASE_ORDER_ID), '') AS RR_NUMBER, "
			+ "COALESCE((SELECT GROUP_CONCAT(DISTINCT DATE_FORMAT(GL_DATE, '%m/%d/%Y') Separator ', ') FROM ("
			+ "SELECT DISTINCT PO.R_PURCHASE_ORDER_ID, RRI.ITEM_ID, API.GL_DATE "
			+ "FROM R_RECEIVING_REPORT_ITEM RRI "
			+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 13) OTO1 ON OTO1.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 ";
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE <= ? ";
		}
		sql += "UNION ALL "
			+ "SELECT PO.R_PURCHASE_ORDER_ID, SI.ITEM_ID, API.GL_DATE FROM SERIAL_ITEM SI "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 64) OTO2 ON OTO2.TO_OBJECT_ID = SI.EB_OBJECT_ID "
			+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO2.FROM_OBJECT_ID "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 74) OTO1 ON OTO1.TO_OBJECT_ID = API.EB_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.R_PURCHASE_ORDER_ID = PO.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 "
			+ "AND POI.ITEM_ID = SI.ITEM_ID "
			+ "AND SI.ACTIVE = 1 ";
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE <= ? ";
		}
		sql += ") AS TBL2 WHERE TBL2.ITEM_ID = INNER_TBL.ITEM_ID AND TBL2.R_PURCHASE_ORDER_ID = INNER_TBL.PURCHASE_ORDER_ID), '') AS RR_DATE,"
			+ "SUPPLIER_NAME, SUPPLIER_ACCOUNT_NAME, TERM_NAME, STOCK_CODE, DESCRIPTION, UOM, ORDERED_QTY, "
			+ "COALESCE((SELECT SUM(QUANTITY) FROM ("
			+ "SELECT PO.R_PURCHASE_ORDER_ID, RRI.ITEM_ID, RRI.QUANTITY "
			+ "FROM R_RECEIVING_REPORT_ITEM RRI "
			+ "INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 13) OTO1 ON OTO1.TO_OBJECT_ID = RRI.EB_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 ";
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE <= ? ";
		}
		sql += "UNION ALL "
			+ "SELECT PO.R_PURCHASE_ORDER_ID, SI.ITEM_ID, SI.QUANTITY "
			+ "FROM SERIAL_ITEM SI "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 64) OTO2 ON OTO2.TO_OBJECT_ID = SI.EB_OBJECT_ID "
			+ "INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO2.FROM_OBJECT_ID "
			+ "INNER JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 74) OTO1 ON OTO1.TO_OBJECT_ID = API.EB_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.R_PURCHASE_ORDER_ID = PO.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE FW.CURRENT_STATUS_ID != 4 "
			+ "AND POI.ITEM_ID = SI.ITEM_ID "
			+ "AND SI.ACTIVE = 1 ";
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE <= ? ";
		}
		sql +=") AS TBL1 WHERE TBL1.ITEM_ID = INNER_TBL.ITEM_ID AND TBL1.R_PURCHASE_ORDER_ID = INNER_TBL.PURCHASE_ORDER_ID), 0) AS DELIVERED_QTY, "
			+ "FORM_STATUS, CANCELLATION_REMARKS FROM ( "
			+ "SELECT * FROM ("
			+ "SELECT PO.R_PURCHASE_ORDER_ID AS PURCHASE_ORDER_ID, D.NAME AS DIVISION, I.ITEM_ID, PO.PO_NUMBER, PO.PO_DATE, "
			+ "(POI.UNIT_COST * POI.QUANTITY) + COALESCE(POI.VAT_AMOUNT, 0) AS AMOUNT, PO.ESTIMATED_DELIVERY_DATE AS EST_DELIVERY_DATE, "
			+ "S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCOUNT_NAME, T.NAME AS TERM_NAME, "
			+ "I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, POI.QUANTITY AS ORDERED_QTY, PO_FS.DESCRIPTION AS FORM_STATUS, "
			+ "IF(FW.CURRENT_STATUS_ID = 4, (SELECT COALESCE(FWL.COMMENT, ' ') FROM FORM_WORKFLOW_LOG FWL "
			+ "WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = FW.CURRENT_STATUS_ID), ' ') AS CANCELLATION_REMARKS, "
			+ "D.DIVISION_ID FROM R_PURCHASE_ORDER PO "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.R_PURCHASE_ORDER_ID = PO.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN DIVISION D ON D.DIVISION_ID = PO.DIVISION_ID "
			+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
			+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = PO.SUPPLIER_ACCOUNT_ID "
			+ "INNER JOIN TERM T ON T.TERM_ID = PO.TERM_ID "
			+ "INNER JOIN ITEM I ON I.ITEM_ID = POI.ITEM_ID "
			+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
			+ "INNER JOIN FORM_STATUS PO_FS ON PO_FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
			+ "LEFT JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 13) OTO ON OTO.FROM_OBJECT_ID = POI.EB_OBJECT_ID "
			+ "LEFT JOIN R_RECEIVING_REPORT_ITEM RRI ON RRI.EB_OBJECT_ID = OTO.TO_OBJECT_ID "
			+ "LEFT JOIN AP_INVOICE API ON API.AP_INVOICE_ID = RRI.AP_INVOICE_ID "
			+ "LEFT JOIN FORM_WORKFLOW RR_FW ON RR_FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE PO.COMPANY_ID = ? ";
		if (divisionId > 0) {
			sql += "AND PO.DIVISION_ID = ? ";
		}
		if (supplierId != null) {
			sql += "AND PO.SUPPLIER_ID = ? ";
		}
		if (supplierAccountId > 0) {
			sql += "AND PO.SUPPLIER_ACCOUNT_ID = ? ";
		}
		if (termId > 0) {
			sql += "AND PO.TERM_ID = ? ";
		}
		if (poDateFrom != null && poDateTo != null) {
			sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
		}
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE BETWEEN ? AND ? "
				+ "AND RR_FW.CURRENT_STATUS_ID != 4 ";
		}
		if (statusId > 0) {
			sql += "AND FW.CURRENT_STATUS_ID = ? ";
		}
		sql += "UNION ALL "
			+ "SELECT PO.R_PURCHASE_ORDER_ID AS PURCHASE_ORDER_ID, D.NAME AS DIVISION, I.ITEM_ID, PO.PO_NUMBER, PO.PO_DATE, "
			+ "(POI.UNIT_COST * POI.QUANTITY) + COALESCE(POI.VAT_AMOUNT, 0) AS AMOUNT, PO.ESTIMATED_DELIVERY_DATE AS EST_DELIVERY_DATE, "
			+ "S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCOUNT_NAME, T.NAME AS TERM_NAME, "
			+ "I.STOCK_CODE, I.DESCRIPTION, UOM.NAME AS UOM, POI.QUANTITY AS ORDERED_QTY, PO_FS.DESCRIPTION AS FORM_STATUS, "
			+ "IF(FW.CURRENT_STATUS_ID = 4, (SELECT COALESCE(FWL.COMMENT, ' ') FROM FORM_WORKFLOW_LOG FWL "
			+ "WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID AND FWL.FORM_STATUS_ID = FW.CURRENT_STATUS_ID), ' ') AS CANCELLATION_REMARKS, "
			+ "D.DIVISION_ID FROM R_PURCHASE_ORDER PO "
			+ "INNER JOIN R_PURCHASE_ORDER_ITEM POI ON POI.R_PURCHASE_ORDER_ID = PO.R_PURCHASE_ORDER_ID "
			+ "INNER JOIN DIVISION D ON PO.DIVISION_ID = D.DIVISION_ID "
			+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PO.SUPPLIER_ID "
			+ "INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = PO.SUPPLIER_ACCOUNT_ID "
			+ "INNER JOIN TERM T ON T.TERM_ID = PO.TERM_ID "
			+ "INNER JOIN ITEM I ON I.ITEM_ID = POI.ITEM_ID "
			+ "INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
			+ "INNER JOIN FORM_STATUS PO_FS ON PO_FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
			+ "LEFT JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 74) OTO1 ON OTO1.FROM_OBJECT_ID = PO.EB_OBJECT_ID "
			+ "LEFT JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO1.TO_OBJECT_ID "
			+ "LEFT JOIN (SELECT DISTINCT FROM_OBJECT_ID, TO_OBJECT_ID, OR_TYPE_ID "
			+ "FROM OBJECT_TO_OBJECT WHERE OR_TYPE_ID = 64) OTO2 ON OTO2.FROM_OBJECT_ID = API.EB_OBJECT_ID "
			+ "LEFT JOIN SERIAL_ITEM RRI ON RRI.EB_OBJECT_ID = OTO2.TO_OBJECT_ID "
			+ "LEFT JOIN FORM_WORKFLOW RR_FW ON RR_FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID "
			+ "WHERE PO.COMPANY_ID = ? ";
		if (divisionId > 0) {
			sql += "AND PO.DIVISION_ID = ? ";
		}
		if (supplierId != null) {
			sql += "AND PO.SUPPLIER_ID = ? ";
		}
		if (supplierAccountId > 0) {
			sql += "AND PO.SUPPLIER_ACCOUNT_ID = ? ";
		}
		if (termId > 0) {
			sql += "AND PO.TERM_ID = ? ";
		}
		if (poDateFrom != null && poDateTo != null) {
			sql += "AND PO.PO_DATE BETWEEN ? AND ? ";
		}
		if (rrDateFrom != null && rrDateTo != null) {
			sql += "AND API.GL_DATE BETWEEN ? AND ?"
				+ "AND RRI.ACTIVE = 1 "
				+ "AND RRI.ITEM_ID = POI.ITEM_ID "
				+ "AND RR_FW.CURRENT_STATUS_ID != 4 ";
		}
		if (statusId > 0) {
			sql += "AND FW.CURRENT_STATUS_ID = ? ";
		}
		sql += ") AS TBL GROUP BY PO_NUMBER, DIVISION_ID, ITEM_ID) AS INNER_TBL) AS OUTER_TBL ";
		if (!deliveryStatus.equals("ALL")) {
			sql += " HAVING DELIVERY_STATUS = ? ";
		}
		sql += "ORDER BY PO_DATE ASC, PO_NUMBER ASC ";
		return (List<PoRegisterDto>) get(sql, new PoRegisterHandler(companyId, divisionId, supplierId,
					supplierAccountId, termId, poDateFrom, poDateTo, rrDateFrom,
					rrDateTo, statusId, deliveryStatus));
	}

	private static class PoRegisterHandler implements QueryResultHandler<PoRegisterDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer supplierId;
		private Integer supplierAccountId;
		private Integer termId;
		private Date poDateFrom;
		private Date poDateTo;
		private Date rrDateFrom;
		private Date rrDateTo;
		private Integer statusId;
		private String deliveryStatus;

		private PoRegisterHandler(Integer companyId, Integer divisionId, Integer supplierId, Integer supplierAccountId,
				Integer termId, Date poDateFrom, Date poDateTo, Date rrDateFrom, Date rrDateTo, Integer statusId,
				String deliveryStatus) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierId = supplierId;
			this.supplierAccountId = supplierAccountId;
			this.termId = termId;
			this.poDateFrom = poDateFrom;
			this.poDateTo = poDateTo;
			this.rrDateFrom = rrDateFrom;
			this.rrDateTo = rrDateTo;
			this.statusId = statusId;
			this.deliveryStatus = deliveryStatus;
		}

		@Override
		public List<PoRegisterDto> convert(List<Object[]> queryResult) {
			List<PoRegisterDto> dtos = new ArrayList<PoRegisterDto>();
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				Integer poNumber = (Integer) rowResult[colNum++];
				String division = (String) rowResult[colNum++];
				Date poDate = (Date) rowResult[colNum++];
				Double amount = (Double) rowResult[colNum++];
				Date estDeliveryDate = (Date) rowResult[colNum++];
				String rrNumber = (String) rowResult[colNum++];
				String rrDate = (String) rowResult[colNum++];
				String supplierName = (String) rowResult[colNum++];
				String supplierAccountName = (String) rowResult[colNum++];
				String term = (String) rowResult[colNum++];
				String stockCode = (String) rowResult[colNum++];
				String description = (String) rowResult[colNum++];
				String uom = (String) rowResult[colNum++];
				Integer orderedQty = (Integer) rowResult[colNum++];
				Integer deliveredQty = (Integer) rowResult[colNum++];
				Integer balance = (Integer) rowResult[colNum++];
				String deliveryStatus = (String) rowResult[colNum++];
				String poStatus = (String) rowResult[colNum++];
				String cancellationRemarks = (String) rowResult[colNum++];
				dtos.add(PoRegisterDto.getInstance(poNumber, division, poDate, amount, estDeliveryDate, rrNumber, rrDate,
						supplierName, supplierAccountName, term, stockCode, description, uom, orderedQty, deliveredQty,
						balance, deliveryStatus, poStatus, cancellationRemarks));
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, FormStatus.CANCELLED_ID);
			if (rrDateFrom != null && rrDateTo != null) {
				for (int counter = 0; counter < 6; counter++) {
					query.setParameter(++index, rrDateTo);
				}
			}
			for (int i = 0; i < 2; i++) {
				if (companyId != null) {
					query.setParameter(++index, companyId);
				}
				if (divisionId > 0) {
					query.setParameter(++index, divisionId);
				}
				if (supplierId != null) {
					query.setParameter(++index, supplierId);
				}
				if (supplierAccountId > 0) {
					query.setParameter(++index, supplierAccountId);
				}
				if (termId > 0) {
					query.setParameter(++index, termId);
				}
				if (poDateFrom != null && poDateTo != null) {
					query.setParameter(++index, poDateFrom);
					query.setParameter(++index, poDateTo);
				}
				if (rrDateFrom != null && rrDateTo != null) {
					query.setParameter(++index, rrDateFrom);
					query.setParameter(++index, rrDateTo);
				}
				if (statusId > 0) {
					query.setParameter(++index, statusId);
				}
			}
			if (!deliveryStatus.equals("ALL")) {
				query.setParameter(++index, deliveryStatus);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("PO_NUMBER", Hibernate.INTEGER);
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("PO_DATE", Hibernate.DATE);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("EST_DELIVERY_DATE", Hibernate.DATE);
			query.addScalar("RR_NUMBER", Hibernate.STRING);
			query.addScalar("RR_DATE", Hibernate.STRING);
			query.addScalar("SUPPLIER_NAME", Hibernate.STRING);
			query.addScalar("SUPPLIER_ACCOUNT_NAME", Hibernate.STRING);
			query.addScalar("TERM_NAME", Hibernate.STRING);
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("ORDERED_QTY", Hibernate.INTEGER);
			query.addScalar("DELIVERED_QTY", Hibernate.INTEGER);
			query.addScalar("BALANCE", Hibernate.INTEGER);
			query.addScalar("DELIVERY_STATUS", Hibernate.STRING);
			query.addScalar("FORM_STATUS", Hibernate.STRING);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public double getRemainingPrReferenceQty(Integer refRfObjectId, Integer itemId) {
		String sql = "SELECT SUM(QTY_FROM), SUM(QTY_TO) FROM ( "
				+ "SELECT COALESCE(PRI.QUANTITY, 0) AS QTY_FROM, 0 AS QTY_TO "
				+ "FROM PURCHASE_REQUISITION_ITEM PRI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = PRI.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM PR ON PR.REQUISITION_FORM_ID = PRI.PURCHASE_REQUISITION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND O2O.OR_TYPE_ID = 3012 "
				+ "AND PRI.EB_OBJECT_ID = ? "
				+ "AND PRI.ITEM_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, POI.QUANTITY AS QTY_TO "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = POI.EB_OBJECT_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND O2O.OR_TYPE_ID IN (3010, 3011) "
				+ "AND O2O.FROM_OBJECT_ID = ? "
				+ "AND POI.ITEM_ID = ? ) AS TBL ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			int noOfTbls = 2;
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index++, refRfObjectId);
				query.setParameter(index, itemId);
				if (i < (noOfTbls-1)) {
					index++;
				}
			}
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					double prQty = (Double) row[0] != null ? (Double) row[0] : 0;
					double savedPoQty = (Double) row[1] != null ? (Double) row[1] : 0;
					return prQty - savedPoQty;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return 0;
	}

	@Override
	public Page<RPurchaseOrder> getAllPODivsByStatus(final int typeId, final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting) {
		return getAllPOsByStatus(typeId, searchParam, formStatusIds, pageSetting);
	}

	@Override
	public Page<BmsTrackerReportDto> getBmsTrackerData(Integer companyId, Integer divisionId, Integer typeId, String bmsNo,
			Date poDateFrom, Date poDateTo, Date invoiceDateFrom, Date invoiceDateTo, Integer statusId, PageSetting pageSetting){
		return executePagedSP("GET_BMS_TRACKER_DATA", pageSetting, new BmsTrackerHandler(), companyId,
				divisionId, typeId, StringFormatUtil.appendWildCard(bmsNo), poDateFrom, poDateTo, invoiceDateFrom, invoiceDateTo, statusId);
	}

	private static class BmsTrackerHandler implements QueryResultHandler<BmsTrackerReportDto> {
		@Override
		public List<BmsTrackerReportDto> convert(List<Object[]> queryResult) {
			List<BmsTrackerReportDto> dtos = new ArrayList<BmsTrackerReportDto>();
			BmsTrackerReportDto dto = null;
			for (Object[] row : queryResult) {
				dto = new BmsTrackerReportDto();
				dto.setDivision((String) row[0]);
				dto.setBmsNo((String) row[1]);
				dto.setPoNo((Integer) row[3]);
				dto.setPoDate((Date) row[4]);
				dto.setPoAmount(NumberFormatUtil.convertBigDecimalToDouble((Double) row[5]));
				dto.setPoStatus((String) row[6]);
				dto.setAdvPaymentDate((Date) row[7]);
				dto.setPoAdvPayment((Double) row[8]);
				dto.setAdvPaymentRequestor((String) row[9]);
				dto.setPaymentVoucherNo((Integer) row[10]);
				dto.setInvoiceDate((Date) row[11]);
				dto.setRefNo((String) row[12]);
				double expenseAmt = NumberFormatUtil.convertBigDecimalToDouble((Double) row[13]);
				dto.setExpenseAmount(expenseAmt);
				dto.setCheckVoucherNo((Integer) row[14]);
				dto.setCheckNo((String) row[15]);
				dto.setCheckAmount(NumberFormatUtil.convertBigDecimalToDouble((Double) row[16]));
				dto.setCheckStatus((String) row[17]);
				dto.setBmsAmount(expenseAmt); // 6068: Set BMS amount to expense amount only
				dtos.add(dto);
			}
			return dtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("BMS_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PO_NUMBER", Hibernate.INTEGER);
			query.addScalar("PO_DATE", Hibernate.DATE);
			query.addScalar("PO_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("PO_STATUS", Hibernate.STRING);
			query.addScalar("SAP_DATE", Hibernate.DATE);
			query.addScalar("SAP_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("SAP_REQUESTOR", Hibernate.STRING);
			query.addScalar("API_NUMBER", Hibernate.INTEGER);
			query.addScalar("INVOICE_DATE", Hibernate.DATE);
			query.addScalar("REF_NUMBER", Hibernate.STRING);
			query.addScalar("INVOICE_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CV_NUMBER", Hibernate.INTEGER);
			query.addScalar("CHECK_NUMBER", Hibernate.STRING);
			query.addScalar("CHECK_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CV_STATUS", Hibernate.STRING);
		}
	}
}
