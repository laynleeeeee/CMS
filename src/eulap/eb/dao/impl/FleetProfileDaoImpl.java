package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.FleetProfileDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FleetProfile;
import eulap.eb.domain.hibernate.FleetType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FleetItemConsumedDto;
import eulap.eb.web.dto.FleetJobOrderDto;

/**
 * Implementing class of {@link FleetProfileDao}

 *
 */
public class FleetProfileDaoImpl extends BaseDao<FleetProfile> implements FleetProfileDao{

	@Override
	protected Class<FleetProfile> getDomainClass() {
		return FleetProfile.class;
	}

	@Override
	public List<FleetProfile> getFleetProfiles(String codeVesselName, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		if (codeVesselName != null && !codeVesselName.trim().isEmpty()) {
			dc.add(Restrictions.like(FleetProfile.FIELD.codeVesselName.name(), "%" + codeVesselName + "%"));
		}
		addUserCompany(dc, user);
		dc.getExecutableCriteria(getSession()).setMaxResults(20);
		dc.addOrder(Order.asc(FleetProfile.FIELD.codeVesselName.name()));
		return getAll(dc);
	}

	@Override
	public boolean isUniqueCode(FleetProfile fleetProfile) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetProfile.FIELD.codeVesselName.name(), fleetProfile.getCodeVesselName()));
		dc.add(Restrictions.eq(FleetProfile.FIELD.companyId.name(), fleetProfile.getCompanyId()));
		if (fleetProfile.getId() != 0) {
			dc.add(Restrictions.ne(FleetType.FIELD.id.name(), fleetProfile.getId()));
		}
		return getAll(dc).isEmpty();
	}

	private String getBeginningBalance() {
		return "SELECT DATE, 'BEGINNING BALANCE' AS REF_NO, DESCRIPTION, ACCOUNT_NAME, SUM(AMOUNT) AS AMOUNT, REMARKS FROM ( "
				+ "SELECT NULL AS DATE, '' AS REF_NO, '' AS DESCRIPTION, '' AS ACCOUNT_NAME, "
				+ "API.AMOUNT, '' AS REMARKS FROM AP_INVOICE AI "
				+ "INNER JOIN AP_LINE API ON API.AP_INVOICE_ID = AI.AP_INVOICE_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = API.ACCOUNT_COMBINATION_ID "
				+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 AND AC.DIVISION_ID = ? AND A.ACCOUNT_TYPE_ID = 7 "
				+ "AND AI.GL_DATE < ? "
				+ "UNION ALL "
				+ "SELECT NULL AS DATE, '' AS REF_NO, '' AS DESCRIPTION, '' AS ACCOUNT_NAME, "
				+ "GLE.AMOUNT, '' AS REMARKS FROM GENERAL_LEDGER GL "
				+ "INNER JOIN GL_ENTRY GLE ON GLE.GENERAL_LEDGER_ID = GL.GENERAL_LEDGER_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = GLE.ACCOUNT_COMBINATION_ID "
				+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID AND A.ACCOUNT_TYPE_ID = 7 "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = GL.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 AND AC.DIVISION_ID = ? AND A.ACCOUNT_TYPE_ID = 7 "
				+ "AND GL.GL_DATE < ? "
				+ ") AS BEGINNING_BALANCE GROUP BY REF_NO "
				+ "UNION ALL ";
	}

	@Override
	public Page<FleetJobOrderDto> getFleetJobOrders(Integer divisionId, Date dateFrom, Date dateTo, PageSetting pageSetting) {
		boolean hasDateFromAndTo = dateFrom != null && dateTo != null;
		boolean hasDateFrom = dateFrom != null;
		boolean hasDateTo = dateTo != null;
		String sql = "SELECT DATE, REF_NO, DESCRIPTION, ACCOUNT_NAME, AMOUNT, REMARKS FROM ( "
			+ (hasDateFromAndTo || hasDateFrom ? getBeginningBalance() : "")
			+ "SELECT DATE, REF_NO, DESCRIPTION, ACCOUNT_NAME, AMOUNT, REMARKS FROM ( "
			+ "SELECT AI.GL_DATE AS DATE, INVOICE_NUMBER AS REF_NO, AI.DESCRIPTION, A.ACCOUNT_NAME, "
			+ "API.AMOUNT, API.DESCRIPTION AS REMARKS FROM AP_INVOICE AI "
			+ "INNER JOIN AP_LINE API ON API.AP_INVOICE_ID = AI.AP_INVOICE_ID "
			+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = API.ACCOUNT_COMBINATION_ID "
			+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 AND AC.DIVISION_ID = ? AND A.ACCOUNT_TYPE_ID = 7 "
			+ (hasDateFromAndTo ? "AND (AI.GL_DATE BETWEEN ? AND ?) " : "")
			+ (hasDateFrom && !hasDateTo ? "AND AI.GL_DATE >= ? " : "")
			+ (!hasDateFrom && hasDateTo ? "AND AI.GL_DATE <= ? " : "")
			+ "UNION ALL "
			+ "SELECT GL_DATE AS DATE, CONCAT('JV ', SEQUENCE_NO) AS REF_NO, COMMENT AS DESCRIPTION, A.ACCOUNT_NAME, "
			+ "GLE.AMOUNT, GLE.DESCRIPTION AS REMARKS FROM GENERAL_LEDGER GL "
			+ "INNER JOIN GL_ENTRY GLE ON GLE.GENERAL_LEDGER_ID = GL.GENERAL_LEDGER_ID "
			+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = GLE.ACCOUNT_COMBINATION_ID "
			+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID AND A.ACCOUNT_TYPE_ID = 7 "
			+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = GL.FORM_WORKFLOW_ID "
			+ "WHERE FW.IS_COMPLETE = 1 AND AC.DIVISION_ID = ? AND A.ACCOUNT_TYPE_ID = 7 "
			+ (hasDateFromAndTo ? "AND (GL.GL_DATE BETWEEN ? AND ?) " : "")
			+ (hasDateFrom && !hasDateTo ? "AND GL.GL_DATE >= ? " : "")
			+ (!hasDateFrom && hasDateTo ? "AND GL.GL_DATE <= ? " : "")
			+ ") AS JOB_ORDER_ENTRY ORDER BY DATE "
			+ ") AS JOB_ORDER";
		FleetJobOrderHandler handler = new  FleetJobOrderHandler(divisionId, dateFrom, dateTo);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class FleetJobOrderHandler implements QueryResultHandler<FleetJobOrderDto> {
		private Integer divisionId; 
		private Date dateFrom; 
		private Date dateTo;

		private FleetJobOrderHandler (Integer divisionId, Date dateFrom, Date dateTo) {
			this.divisionId = divisionId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<FleetJobOrderDto> convert(List<Object[]> queryResult) {
			List<FleetJobOrderDto> fleetJobOrderDtos = new ArrayList<>();
			FleetJobOrderDto fleetJobOrderDto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				fleetJobOrderDto = new FleetJobOrderDto();
				fleetJobOrderDto.setDate((Date) row[index++]);
				fleetJobOrderDto.setRefNo((String) row[index++]);
				fleetJobOrderDto.setDescription((String) row[index++]);
				fleetJobOrderDto.setAccountName((String) row[index++]);
				fleetJobOrderDto.setAmount((Double) row[index++]);
				fleetJobOrderDto.setRemarks((String) row[index++]);
				fleetJobOrderDtos.add(fleetJobOrderDto);
			}
			return fleetJobOrderDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			int numOfTables = 2;
			boolean hasDateFromAndTo = dateFrom != null && dateTo != null;
			boolean hasDateFrom = dateFrom != null;
			boolean hasDateTo = dateTo != null;
			if (hasDateFromAndTo || hasDateFrom) {
				// BEGINNING BALANCE
				query.setParameter(index, divisionId);
				query.setParameter(++index, dateFrom);

				query.setParameter(++index, divisionId);
				query.setParameter(++index, dateFrom);

				if (hasDateFromAndTo) {
					// AP INVOICE & GL
					for (int i=0; i<numOfTables; i++) {
						query.setParameter(++index, divisionId);
						query.setParameter(++index, dateFrom);
						query.setParameter(++index, dateTo);
					}
				}
			}
			if (hasDateFrom && !hasDateFromAndTo) {

				// AP INVOICE & GL
				for (int i=0; i<numOfTables; i++) {
					query.setParameter(++index, divisionId);
					query.setParameter(++index, dateFrom);
				}
			} else if (hasDateTo && !hasDateFromAndTo) {
				// AP INVOICE & GL
				for (int i=0; i<numOfTables; i++) {
					query.setParameter(i== 0 ? index : ++index, divisionId);
					query.setParameter(++index, dateTo);
				}
			} else if (!hasDateFromAndTo){
				query.setParameter(index, divisionId);
				query.setParameter(++index, divisionId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("REF_NO", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("ACCOUNT_NAME", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public List<FleetProfile> getFleetProfilesByCompanyId(String codeVesselName, Integer companyId, Boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		if (codeVesselName != null && (isExact == null || !isExact)) {
			dc.add(Restrictions.like(FleetProfile.FIELD.codeVesselName.name(), "%" + codeVesselName.trim() + "%"));
		} else {
			dc.add(Restrictions.eq(FleetProfile.FIELD.codeVesselName.name(), codeVesselName.trim()));
		}
		if(companyId != null && companyId != -1){
			DetachedCriteria comapnyCrit = DetachedCriteria.forClass(Company.class);
			restrictO2OReference(dc, comapnyCrit, companyId);
		}
		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public Page<FleetItemConsumedDto> getFleetItemsConsumed(Integer divisionId, Date dateFrom, Date dateTo,
			String stockCode, String description, boolean isDescending, Integer itemCategoryId, PageSetting pageSetting) {
		boolean hasDateFromAndTo = dateFrom != null && dateTo != null;
		boolean hasDateFrom = dateFrom != null;
		boolean hasDateTo = dateTo != null;
		String sql = "SELECT WS.DATE, WS.WS_NUMBER AS REF_NO, I.STOCK_CODE, I.DESCRIPTION, "
				+ "WSI.QUANTITY, UM.NAME AS UOM, WSI.UNIT_COST AS AMOUNT, WSI.EB_OBJECT_ID, I.ITEM_ID FROM WITHDRAWAL_SLIP_ITEM WSI "
				+ "INNER JOIN OBJECT_TO_OBJECT OOWSI ON OOWSI.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = OOWSI.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = WS.EB_OBJECT_ID "
				+ "INNER JOIN FLEET_PROFILE FP ON FP.EB_OBJECT_ID = OO.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OOD ON OOD.TO_OBJECT_ID = FP.EB_OBJECT_ID "
				+ "INNER JOIN DIVISION D ON D.EB_OBJECT_ID = OOD.FROM_OBJECT_ID "
				+ "INNER JOIN ITEM I ON I.ITEM_ID = WSI.ITEM_ID "
				+ "INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WS.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND D.DIVISION_ID = ? "
				+ (stockCode != null && !stockCode.trim().isEmpty() ? "AND I.STOCK_CODE LIKE '%" + stockCode + "%'" : "")
				+ (description != null && !description.trim().isEmpty() ? "AND I.DESCRIPTION LIKE '%" + description + "%'" : "") 
				+ (hasDateFromAndTo ? "AND (WS.DATE BETWEEN ? AND ?) " : "")
				+ (hasDateTo && !hasDateFrom ? "AND WS.DATE <= ? " : "")
				+ (!hasDateTo && hasDateFrom ? "AND WS.DATE >= ? " : "")
				+ (itemCategoryId != null ? "AND I.ITEM_CATEGORY_ID = ? " : "AND I.ITEM_CATEGORY_ID != 1 ")
				+ "AND WSI.ACTIVE = 1 "
				+ "GROUP BY EB_OBJECT_ID "
				+ "ORDER BY WSI.WITHDRAWAL_SLIP_ITEM_ID, WS.DATE " + (isDescending ? "DESC" : "");
		FleetItemConsumedHandler handler = new  FleetItemConsumedHandler(divisionId, dateFrom, dateTo, itemCategoryId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class FleetItemConsumedHandler implements QueryResultHandler<FleetItemConsumedDto> {
		private Integer divisionId;
		private Date dateFrom; 
		private Date dateTo;
		private Integer itemCategoryId;

		private FleetItemConsumedHandler (Integer divisionId, Date dateFrom, Date dateTo, Integer itemCategoryId) {
			this.divisionId = divisionId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.itemCategoryId = itemCategoryId;
		}

		@Override
		public List<FleetItemConsumedDto> convert(List<Object[]> queryResult) {
			List<FleetItemConsumedDto> fleetItemConsumedDtos = new ArrayList<>();
			FleetItemConsumedDto fleetItemConsumed = null;
			for (Object[] row : queryResult) {
				int index = 0;
				fleetItemConsumed = new FleetItemConsumedDto();
				fleetItemConsumed.setDate((Date) row[index++]);
				fleetItemConsumed.setRefNo((String) row[index++]);
				fleetItemConsumed.setStockCode((String) row[index++]);
				fleetItemConsumed.setDescription((String) row[index++]);
				fleetItemConsumed.setQty((Double) row[index++]);
				fleetItemConsumed.setUom((String) row[index++]);
				fleetItemConsumed.setAmount((Double) row[index++]);
				fleetItemConsumed.setEbObjectId((Integer) row[index++]);
				fleetItemConsumed.setItemId((Integer) row[index]);
				fleetItemConsumedDtos.add(fleetItemConsumed);
			}
			return fleetItemConsumedDtos;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, divisionId);
			if (dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			} else if (dateTo != null) {
				query.setParameter(++index, dateTo);
			} else if (dateFrom != null) {
				query.setParameter(++index, dateFrom);
			}
			if (itemCategoryId != null) {
				query.setParameter(++index, itemCategoryId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("REF_NO", Hibernate.STRING);
			query.addScalar("STOCK_CODE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("QUANTITY", Hibernate.DOUBLE);
			query.addScalar("UOM", Hibernate.STRING);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("ITEM_ID", Hibernate.INTEGER);
		}
	}

	@Override
	public boolean isUniquePlateNo(String plateNo, Integer fleetProfileId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetProfile.FIELD.plateNo.name(), plateNo));
		if(fleetProfileId != 0) {
			dc.add(Restrictions.ne(FleetProfile.FIELD.id.name(), fleetProfileId));
		}
		return getAll(dc).size() == 0;
	}

	@Override
	public List<FleetProfile> getFleetsByPlateNo(Integer companyId, String plateNo, Boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(FleetProfile.FIELD.companyId.name(), companyId));
		if(!isExact) {
			dc.add(Restrictions.like(FleetProfile.FIELD.plateNo.name(), StringFormatUtil.appendWildCard(plateNo)));
		} else {
			dc.add(Restrictions.eq(FleetProfile.FIELD.plateNo.name(), plateNo));
		}
		return getAll(dc, 10);//Limiting the maximum result to 10.
	}
}
