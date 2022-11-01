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
import eulap.eb.dao.RepackingDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.Repacking;
import eulap.eb.domain.hibernate.RepackingItem;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.ReclassRegisterDto;

/**
 * DAO Implementation of {@link RepackingDao}

 */
public class RepackingDaoImpl extends BaseDao<Repacking> implements RepackingDao{

	@Override
	protected Class<Repacking> getDomainClass() {
		return Repacking.class;
	}

	@Override
	public int generateRNumber(int companyId) {
		return generateSequenceNumber(Repacking.FIELD.rNumber.name(),
				Repacking.FIELD.companyId.name(), companyId);
	}

	@Override
	public Page<Repacking> getAllRepackingByStatus(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting, int typeId){
		return getAllRepackingByStatus(searchParam, formStatusIds, pageSetting, typeId, -1);
	}

	@Override
	public Page<Repacking> getAllRepackingByStatus(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting, int typeId, int divisionId) {
		HibernateCallback<Page<Repacking>> hibernateCallback = new HibernateCallback<Page<Repacking>>() {
			@Override
			public Page<Repacking> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria rpCriteria = session.createCriteria(Repacking.class);
				if(StringFormatUtil.isNumeric(searchParam.getSearchCriteria())) {
					rpCriteria.add(Restrictions.sqlRestriction("R_NUMBER LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
				}
				SearchCommonUtil.searchCommonParams(rpCriteria, null, "companyId",
						Repacking.FIELD.rNumber.name(), Repacking.FIELD.rNumber.name(),
						Repacking.FIELD.rNumber.name(), searchParam.getUser().getCompanyIds(), searchParam);
				rpCriteria.add(Restrictions.eq(Repacking.FIELD.repackingTypeId.name(), typeId));
				if(divisionId != -1) {
					rpCriteria.add(Restrictions.eq(Repacking.FIELD.divisionId.name(), divisionId));
				}
				DetachedCriteria workflowCriteria = DetachedCriteria.forClass(FormWorkflow.class);
				workflowCriteria.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				if(!formStatusIds.isEmpty())
					addAsOrInCritiria(workflowCriteria, FormWorkflow.FIELD.currentStatusId.name(),formStatusIds);
				rpCriteria.add(Subqueries.propertyIn(Repacking.FIELD.formWorkflowId.name(), workflowCriteria));
				rpCriteria.addOrder(Order.desc(Repacking.FIELD.rNumber.name()));
				rpCriteria.addOrder(Order.desc(Repacking.FIELD.rDate.name()));
				return getAll(rpCriteria, pageSetting);
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	private void searchRNumber(DetachedCriteria rpCriteria, String criteria) {
		if(StringFormatUtil.isNumeric(criteria)) {
			rpCriteria.add(Restrictions.sqlRestriction("R_NUMBER LIKE ?", criteria.trim(), Hibernate.STRING));
		}
	}

	@Override
	public Page<Repacking> searchRepackingForms(int typeId, Integer divisionId, String criteria, PageSetting pageSetting) {
		DetachedCriteria rpCriteria = getDetachedCriteria();
		searchRNumber(rpCriteria, criteria);
		rpCriteria.add(Restrictions.eq(Repacking.FIELD.repackingTypeId.name(), typeId));
		if(divisionId != null) {
			rpCriteria.add(Restrictions.eq(Repacking.FIELD.divisionId.name(), divisionId));
		}
		rpCriteria.addOrder(Order.asc(Repacking.FIELD.rNumber.name()));
		return getAll(rpCriteria, pageSetting);
	}

	@Override
	public Page<Repacking> getRepackingTransactions(int itemId,
			int warehouseId, Date date, PageSetting pageSetting) {
		DetachedCriteria repackingDc = getDetachedCriteria();
		repackingDc.createAlias("formWorkflow", "fw");
		repackingDc.add(Restrictions.ne("fw.currentStatusId", FormStatus.CANCELLED_ID));
		repackingDc.add(Restrictions.eq(Repacking.FIELD.rDate.name(), date));
		repackingDc.add(Restrictions.eq(Repacking.FIELD.warehouseId.name(), warehouseId));
		
		DetachedCriteria repackingItemDc = DetachedCriteria.forClass(RepackingItem.class);
		repackingItemDc.setProjection(Projections.property(RepackingItem.FIELD.repackingId.name()));
		repackingItemDc.add(Restrictions.eq(RepackingItem.FIELD.fromItemId.name(), itemId));
		
		repackingDc.add(Subqueries.propertyIn(Repacking.FIELD.id.name(), repackingItemDc));
		return getAll(repackingDc, pageSetting);
	}

	@Override
	public int generateRNumberByTypeId(int companyId, int repackingTypeId) {
		return generateRNumberByTypeId(companyId, repackingTypeId, null);
	}

	@Override
	public int generateRNumberByTypeId(int companyId, int repackingTypeId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(Repacking.FIELD.rNumber.name()));
		dc.add(Restrictions.eq(Repacking.FIELD.companyId.name(), companyId));
		if(divisionId != null) {
			dc.add(Restrictions.eq(Repacking.FIELD.divisionId.name(), divisionId));
		}
		dc.add(Restrictions.eq(Repacking.FIELD.repackingTypeId.name(), repackingTypeId));
		return generateSeqNo(dc);
	}

	@Override
	public Page<ReclassRegisterDto> getReclassRegisterData(Integer companyId, Integer divisionId, Integer warehouseId,
			Date dateFrom, Date dateTo, Integer statusId, PageSetting pageSetting) {
		StringBuffer sql = new StringBuffer("SELECT DIVISION, WAREHOUSE, FROM_STOCK_CODE, "
				+ "DESCRIPTION, QTY_FOR_RECLASS, UOM, REPACKED_UNIT_COST, TO_STOCK_CODE, "
				+ "TO_DESCRIPTION, RECEIVED_QUANTITY, TO_UOM, UNIT_COST, RECEIVED_QUANTITY * UNIT_COST AS AMOUNT, "
				+ "CANCELLATION_REMARKS, F_ITEM_ID, T_ITEM_ID, W_ID, R_NUMBER, STATUS FROM ("
				+ "SELECT D.NAME AS DIVISION, W.NAME AS WAREHOUSE, I.STOCK_CODE AS "
				+ "FROM_STOCK_CODE, I.DESCRIPTION AS DESCRIPTION, RI.REPACKED_QUANTITY AS "
				+ "QTY_FOR_RECLASS, UM.NAME AS UOM, RI.REPACKED_UNIT_COST AS REPACKED_UNIT_COST,"
				+ "I2.STOCK_CODE AS TO_STOCK_CODE, I2.DESCRIPTION AS TO_DESCRIPTION, "
				+ "RI.QUANTITY AS RECEIVED_QUANTITY, UM2.NAME AS TO_UOM, RI.UNIT_COST AS UNIT_COST, "
				+ "0 AS AMOUNT, IF(FWL.FORM_STATUS_ID = 4, FWL.COMMENT, '') AS CANCELLATION_REMARKS, "
				+ "FROM_ITEM_ID AS F_ITEM_ID, TO_ITEM_ID AS T_ITEM_ID, R.WAREHOUSE_ID AS W_ID, R.R_NUMBER,"
				+ "FS.DESCRIPTION AS STATUS "
				+ "FROM REPACKING R "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = R.DIVISION_ID "
				+ "INNER JOIN REPACKING_ITEM RI ON RI.REPACKING_ID = R.REPACKING_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = RI.FROM_ITEM_ID "
				+ "INNER JOIN ITEM I2 ON I2.ITEM_ID = RI.TO_ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM2 ON UM2.UNITOFMEASUREMENT_ID = I2.UNIT_MEASUREMENT_ID "
				+ "INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = R.WAREHOUSE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = R.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "LEFT JOIN FORM_WORKFLOW_LOG FWL ON FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "AND FWL.FORM_STATUS_ID = FW.CURRENT_STATUS_ID  "
				+ "WHERE R.COMPANY_ID = ? "
				+ (divisionId != -1 ? "AND R.DIVISION_ID = ? "  : "")
				+ (warehouseId != -1 ? "AND R.WAREHOUSE_ID = ? "  : "")
				+ (dateFrom != null && dateTo != null ? " AND R.R_DATE BETWEEN ? AND ? " : "")
				+ (statusId > 0 ? "AND FW.CURRENT_STATUS_ID = ? " : "")
				+ "GROUP BY RI.REPACKING_ID)AS TBL ");
		return getAllAsPage(sql.toString(), pageSetting, new ReclassRegisterHandler(companyId, divisionId,
				warehouseId, dateFrom, dateTo, statusId));
	}

	private static class ReclassRegisterHandler implements QueryResultHandler<ReclassRegisterDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer warehouseId;
		private Date dateFrom;
		private Date dateTo;
		private Integer statusId;

		private ReclassRegisterHandler (Integer companyId, Integer divisionId, Integer warehouseId,
				Date dateFrom, Date dateTo, Integer statusId) {
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.warehouseId = warehouseId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.statusId = statusId;
		}

		@Override
		public List<ReclassRegisterDto> convert(List<Object[]> queryResult) {
			List<ReclassRegisterDto> rrData = new ArrayList<ReclassRegisterDto>();
			ReclassRegisterDto rrDto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				rrDto = new ReclassRegisterDto();
				rrDto.setDivision((String) row[index++]); //Division
				rrDto.setFromStockCode((String) row[index++]); //From stock code
				rrDto.setFscDescription((String) row[index++]); //Description of fromStockCode
				rrDto.setReclassedQty((Double) row[index++]); //Reclassed quantity
				rrDto.setFscUom((String) row[index++]); //UOM of fromStockCode
				rrDto.setRepackedUnitCost((Double) row[index++]); //Unit cost of reclassed items.
				rrDto.setToStockCode((String) row[index++]); //To stock code
				rrDto.setTscDescription((String) row[index++]); //Description of toStockCode
				rrDto.setReceivedQty((Double) row[index++]); //Received quantity
				rrDto.setTscUom((String) row[index++]); //UOM of toStockCode
				rrDto.setUnitCost((Double) row[index++]); //Unit cost of items from toStockCode
				rrDto.setAmount((Double) row[index++]); //Amount
				rrDto.setCancellationRemarks((String) row[index++]); //Cancellation remarks
				rrDto.setFromItemId((Integer) row[index++]); //fromItemID setter
				rrDto.setToItemId((Integer) row[index++]); //toItemID setter
				rrDto.setWarehouseId((Integer) row[index++]); //Warehouse ID setter
				rrDto.setRpNumber((Integer) row[index++]);
				rrDto.setFormStatus((String) row[index++]);
				rrData.add(rrDto);
			}
			return rrData;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			if(divisionId != -1) {
				query.setParameter(++index, divisionId);
			}
			if(warehouseId != -1) {
				query.setParameter(++index, warehouseId);
			}
			if (dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			}
			if (statusId > 0) {
				query.setParameter(++index, statusId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("FROM_STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("QTY_FOR_RECLASS", Hibernate.DOUBLE);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("REPACKED_UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("TO_STOCK_CODE", Hibernate.STRING);
			query.addScalar("TO_DESCRIPTION", Hibernate.STRING);
			query.addScalar("RECEIVED_QUANTITY", Hibernate.DOUBLE);
			query.addScalar("TO_UOM", Hibernate.STRING);
			query.addScalar("UNIT_COST", Hibernate.DOUBLE);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("F_ITEM_ID", Hibernate.INTEGER);
			query.addScalar("T_ITEM_ID", Hibernate.INTEGER);
			query.addScalar("W_ID", Hibernate.INTEGER);
			query.addScalar("R_NUMBER", Hibernate.INTEGER);
			query.addScalar("STATUS", Hibernate.STRING);
		}
	}
}
