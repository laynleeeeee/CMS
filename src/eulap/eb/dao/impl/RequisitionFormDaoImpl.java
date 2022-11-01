package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.RequisitionFormDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ORType;
import eulap.eb.domain.hibernate.RPurchaseOrder;
import eulap.eb.domain.hibernate.RequisitionClassification;
import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.PRReferenceDto;
import eulap.eb.web.dto.RequisitionFormDto;
import eulap.eb.web.dto.RfReferenceDto;
import eulap.eb.web.dto.UsedRequisitionFormDto;

/**
 * Implementation class for {@code RequisitionFormDao}

 */
public class RequisitionFormDaoImpl extends BaseDao<RequisitionForm> implements RequisitionFormDao {

	@Override
	protected Class<RequisitionForm> getDomainClass() {
		return RequisitionForm.class;
	}

	@Override
	public Integer generateSequenceNumber(Integer companyId,
			boolean isPurchaseRequisition, Integer requisitionTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(RequisitionForm.FIELD.sequenceNumber.name()));
		if(companyId != null) {
			dc.add(Restrictions.eq(RequisitionForm.FIELD.companyId.name(), companyId));
		}

		if(isPurchaseRequisition) {
			dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionClassificationId.name(), RequisitionClassification.RC_PURCHASE_REQUISITION));
		}

		if(requisitionTypeId != null) {
			dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionTypeId.name(), requisitionTypeId));
		}

		return generateSeqNo(dc);
	}

	@Override
	public Page<RequisitionForm> getRequisitionForms(ApprovalSearchParam searchParam, int typeId,
			List<Integer> formStatusIds, boolean isPurchaseRequest, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionTypeId.name(), typeId));
		if (isPurchaseRequest) {
			dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionClassificationId.name(), RequisitionClassification.RC_PURCHASE_REQUISITION));
		} else {
			dc.add(Restrictions.or(Restrictions.ne(RequisitionForm.FIELD.requisitionClassificationId.name(), RequisitionClassification.RC_PURCHASE_REQUISITION),
					Restrictions.isNull(RequisitionForm.FIELD.requisitionClassificationId.name())));
		}
		addUserCompany(dc, searchParam.getUser());
		if(!searchParam.getSearchCriteria().trim().isEmpty()) {
			dc.add(Restrictions.sqlRestriction("this_.SEQUENCE_NO LIKE ?", searchParam.getSearchCriteria().trim(), Hibernate.STRING));
		}
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if(formStatusIds.size() > 0) {
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
		}
		SearchCommonUtil.searchDCCommonParams(dc, null, "companyId", RequisitionForm.FIELD.date.name(), RequisitionForm.FIELD.date.name(),
				RequisitionForm.FIELD.date.name(), searchParam.getUser().getCompanyIds(), searchParam);
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(RequisitionForm.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(RequisitionForm.FIELD.createdDate.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<RequisitionForm> searchRequisitionForms(String criteria, int typeId, boolean isPurchaseRequest, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (isPurchaseRequest) {
			dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionClassificationId.name(), RequisitionClassification.RC_PURCHASE_REQUISITION));
		} else {
			dc.add(Restrictions.isNull(RequisitionForm.FIELD.requisitionClassificationId.name()));
		}
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("this_.SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionTypeId.name(), typeId));
		dc.addOrder(Order.asc(RequisitionForm.FIELD.sequenceNumber.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public double getRemainingQuantity(Integer referenceObjectId, Integer wsId,
			Integer prId, boolean isExcludePr) {
		String sql = "SELECT SUM(QTY_FROM - QTY_TO) AS REMAINING_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT RFI.QUANTITY AS QTY_FROM, 0 AS QTY_TO, RFI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM REQUISITION_FORM_ITEM RFI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RFI.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RF.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND RFI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, SI.QUANTITY AS QTY_TO, RFI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = O2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WSO2O ON WSO2O.TO_OBJECT_ID = WS.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = WSO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RFO2O ON RFO2O.FROM_OBJECT_ID = RF.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = RFO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WS.FORM_WORKFLOW_ID "
				+ "WHERE SI.ACTIVE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND O2O.OR_TYPE_ID = 3000 "
				+ "AND SI.ITEM_ID = RFI.ITEM_ID "
				+ "AND RFI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, ASI.QUANTITY AS QTY_TO, RFI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT ATO2O ON ATO2O.TO_OBJECT_ID = AT.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RFO2O ON RFO2O.FROM_OBJECT_ID = ATO2O.FROM_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = RFO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID = 4 "
				+ "AND ATO2O.OR_TYPE_ID = 3009 "
				+ "AND ASI.ITEM_ID = RFI.ITEM_ID "
				+ "AND RFI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, SI.QUANTITY AS QTY_TO, RFI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT SIO2O ON SIO2O.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.EB_OBJECT_ID = SIO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT ATO2O ON ATO2O.TO_OBJECT_ID = AT.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RFO2O ON RFO2O.FROM_OBJECT_ID = ATO2O.FROM_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = RFO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID = 4 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND SIO2O.OR_TYPE_ID = 3004 "
				+ "AND ATO2O.OR_TYPE_ID = 3009 "
				+ "AND SI.ITEM_ID = RFI.ITEM_ID "
				+ "AND RFI.EB_OBJECT_ID = ? ";
		sql += "UNION ALL "
				+ "SELECT DISTINCT 0 AS QTY_FROM, WSI.QUANTITY AS QTY_TO, OO.FROM_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM WITHDRAWAL_SLIP_ITEM WSI "
				+ "INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WSOO ON WSOO.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = WSOO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WS.FORM_WORKFLOW_ID "
				+ "WHERE WSI.ACTIVE = 1 "
				+ "AND FW.CURRENT_STATUS_ID != 4 "
				+ "AND OO.OR_TYPE_ID = " + RequisitionFormDto.WS_ITEM_RF_ITEM_OR_TYPE_ID + " "
				+ "AND WSOO.OR_TYPE_ID = " + ORType.PARENT_OR_TYPE_ID + " "
				+ "AND OO.FROM_OBJECT_ID = ? ";
		if (wsId != null) {
			sql += "AND WS.WITHDRAWAL_SLIP_ID != ? ";
		}
		if (!isExcludePr) {
			sql += "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, PRI.QUANTITY AS QTY_TO, O2O.FROM_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM PURCHASE_REQUISITION_ITEM PRI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = PRI.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM PR ON PR.REQUISITION_FORM_ID = PRI.PURCHASE_REQUISITION_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND REQUISITION_CLASSIFICATION_ID = 3 "
				+ "AND O2O.OR_TYPE_ID = 3012 "
				+ "AND O2O.FROM_OBJECT_ID = ? ";
			if (prId != null) {
				sql += "AND PR.REQUISITION_FORM_ID != ? ";
			}
		}
		sql += ") REMAINING_QTY_TBL ";
		Collection<Double> remainingQty = get(sql, new RemainingBalance(referenceObjectId, wsId,
				prId, isExcludePr));
		if (remainingQty != null && !remainingQty.isEmpty()) {
			double quantity = remainingQty.iterator().next();
			return NumberFormatUtil.roundOffTo2DecPlaces(quantity) ;
		}
		return 0;
	}

	private static class RemainingBalance implements QueryResultHandler<Double> {
		private Integer refenceObjectId;
		private Integer wsId;
		private Integer prId;
		private boolean isExcludePr;

		private RemainingBalance (Integer refenceObjectId, Integer wsId,
				Integer prId, boolean isExcludePr) {
			this.refenceObjectId = refenceObjectId;
			this.wsId = wsId;
			this.prId = prId;
			this.isExcludePr = isExcludePr;
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
			int totalNoOfTables = 4;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, refenceObjectId);
				if(i < (totalNoOfTables-1)) {
					++index;
				}
			}
			if(wsId != null) {
				query.setParameter(++index, wsId);
			}
			query.setParameter(++index, refenceObjectId);
			if (!isExcludePr) {
				query.setParameter(++index, refenceObjectId);
				if (prId != null) {
					query.setParameter(++index, prId);
				}
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
	public Page<PRReferenceDto> getPrReferences(User user, Integer companyId, Integer fleetId, Integer projectId, Integer prNumber,
			Date dateFrom, Date dateTo, Integer status, PageSetting pageSetting) {
		String sql = "SELECT REQUISITION_FORM_ID, SOURCE_REF_OBJECT_ID, DATE, "
				+ "CONCAT((CASE WHEN REQUISITION_TYPE_ID = 1 THEN 'TIRE ' "
				+ "WHEN REQUISITION_TYPE_ID = 2 THEN 'FUEL ' "
				+ "WHEN REQUISITION_TYPE_ID = 3 THEN 'PMS ' "
				+ "WHEN REQUISITION_TYPE_ID = 4 THEN 'ELECTRICAL ' "
				+ "WHEN REQUISITION_TYPE_ID = 5 THEN 'CM ' "
				+ "WHEN REQUISITION_TYPE_ID = 6 THEN 'ADMIN ' "
				+ "WHEN REQUISITION_TYPE_ID = 7 THEN 'MOTORPOOL ' "
				+ "WHEN REQUISITION_TYPE_ID = 8 THEN 'OIL ' "
				+ "WHEN REQUISITION_TYPE_ID = 9 THEN 'SUBCON ' "
				+ "WHEN REQUISITION_TYPE_ID = 10 THEN 'PAKYAWAN ' END), SEQUENCE_NO) AS PR_NO, "
				+ "PROJECT_NAME, FLEET_CODE, REMARKS, COMPANY_ID, FLEET_PROFILE_ID, AR_CUSTOMER_ID, "
				+ "SEQUENCE_NO, SUM(PR_QTY) AS TOTAL_PRI_QTY, SUM(PO_QTY) AS TOTAL_POI_QTY FROM ( "
				+ "SELECT PR.REQUISITION_FORM_ID, PR.EB_OBJECT_ID AS SOURCE_REF_OBJECT_ID, "
				+ "PR.DATE, PR.REQUISITION_TYPE_ID, PR.SEQUENCE_NO, AC.NAME AS PROJECT_NAME, "
				+ "FP.CODE_VESSEL_NAME AS FLEET_CODE, PR.REMARKS, PR.COMPANY_ID, RF.FLEET_PROFILE_ID, "
				+ "RF.AR_CUSTOMER_ID, PRI.QUANTITY AS PR_QTY, 0 AS PO_QTY "
				+ "FROM PURCHASE_REQUISITION_ITEM PRI "
				+ "INNER JOIN REQUISITION_FORM PR ON PR.REQUISITION_FORM_ID = PRI.PURCHASE_REQUISITION_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.REQUISITION_FORM_ID = PR.REQ_FORM_REF_ID "
				+ "LEFT JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = RF.AR_CUSTOMER_ID "
				+ "LEFT JOIN FLEET_PROFILE FP ON FP.FLEET_PROFILE_ID = RF.FLEET_PROFILE_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND PR.REQUISITION_CLASSIFICATION_ID = 3 "
				+ "AND PR.REQ_FORM_REF_ID IS NOT NULL "
				+ "UNION ALL "
				+ "SELECT PR.REQUISITION_FORM_ID, PR.EB_OBJECT_ID AS SOURCE_REF_OBJECT_ID, "
				+ "PR.DATE, PR.REQUISITION_TYPE_ID, PR.SEQUENCE_NO, AC.NAME AS PROJECT_NAME, "
				+ "FP.CODE_VESSEL_NAME AS FLEET_CODE, PR.REMARKS, PR.COMPANY_ID, "
				+ "RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, 0 AS PR_QTY, POI.QUANTITY AS PO_QTY "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN OBJECT_TO_OBJECT PO2O ON PO2O.TO_OBJECT_ID = POI.EB_OBJECT_ID "
				+ "INNER JOIN PURCHASE_REQUISITION_ITEM PRI ON PRI.EB_OBJECT_ID = PO2O.FROM_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM PR ON PR.REQUISITION_FORM_ID = PRI.PURCHASE_REQUISITION_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.REQUISITION_FORM_ID = PR.REQ_FORM_REF_ID "
				+ "LEFT JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = RF.AR_CUSTOMER_ID "
				+ "LEFT JOIN FLEET_PROFILE FP ON FP.FLEET_PROFILE_ID = RF.FLEET_PROFILE_ID "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND PO2O.OR_TYPE_ID IN (3010, 3011) "
				+ ") AS TBL WHERE COMPANY_ID = ? ";
		sql += (fleetId != null && fleetId > 0 ? "AND FLEET_PROFILE_ID = ? " : "")
			+ (projectId != null && projectId > 0 ? "AND AR_CUSTOMER_ID = ? " : "")
			+ (prNumber != null && prNumber > 0 ? "AND SEQUENCE_NO = ? " : "");
		if (dateFrom != null && dateTo != null) {
			sql += "AND DATE BETWEEN ? AND ? ";
		} else if (dateFrom != null) {
			sql += "AND DATE >= ? ";
		} else if (dateTo != null) {
			sql += "AND DATE <= ? ";
		}
		sql += "GROUP BY REQUISITION_FORM_ID ";
		if (status == RequisitionForm.STATUS_USED) {
			sql += "HAVING TOTAL_POI_QTY != 0 AND (TOTAL_PRI_QTY - TOTAL_POI_QTY) > 0 ";
		} else if (status == RequisitionForm.STATUS_UNUSED) {
			sql += "HAVING TOTAL_POI_QTY = 0 ";
		} else {
			sql += "HAVING (TOTAL_PRI_QTY - TOTAL_POI_QTY) > 0 ";
		}
		sql += "ORDER BY DATE DESC, SEQUENCE_NO DESC ";
		return getAllAsPage(sql, pageSetting, new PRReferenceHandler(companyId,
				fleetId, projectId, prNumber, dateFrom, dateTo));
	}

	private static class PRReferenceHandler implements QueryResultHandler<PRReferenceDto> {
		private final Integer companyId;
		private final Integer fleetId;
		private final Integer projectId;
		private final Integer prNumber;
		private final Date dateFrom;
		private final Date dateTo;

		private PRReferenceHandler (Integer companyId, Integer fleetId, Integer projectId,
				Integer prNumber, Date dateFrom, Date dateTo) {
			this.companyId = companyId;
			this.fleetId = fleetId;
			this.projectId = projectId;
			this.prNumber = prNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public List<PRReferenceDto> convert(List<Object[]> queryResult) {
			List<PRReferenceDto> prReferences = new ArrayList<>();
			PRReferenceDto prr = null;
			int index = 0;
			for (Object[] row : queryResult) {
				index = 0;
				Integer prId = (Integer) row[index++];
				Integer prObjId = (Integer) row[index++];
				Date date = (Date) row[index++];
				String prNo = (String) row[index++];
				String projectName = (String) row[index++];
				String fleetCode = (String) row[index++];
				String remarks = (String) row[index++];
				prr = PRReferenceDto.getInstanceOf(prId, prObjId, date, prNo, projectName,
						fleetCode, remarks, null, null);
				prReferences.add(prr);
			}
			return prReferences;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			if (fleetId != null && fleetId > 0) {
				query.setParameter(++index, fleetId);
			}
			if (projectId != null && projectId > 0) {
				query.setParameter(++index, projectId);
			}
			if (prNumber != null && prNumber > 0) {
				query.setParameter(++index, prNumber);
			}
			if (dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			} else if (dateFrom != null) {
				query.setParameter(++index, dateFrom);
			} else if (dateTo != null) {
				query.setParameter(++index, dateTo);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REQUISITION_FORM_ID", Hibernate.INTEGER);
			query.addScalar("SOURCE_REF_OBJECT_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("PR_NO", Hibernate.STRING);
			query.addScalar("PROJECT_NAME", Hibernate.STRING);
			query.addScalar("FLEET_CODE", Hibernate.STRING);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public List<RequisitionForm> getRequisitionFormsByJobOrderId(Integer jobOrderId, Boolean isComplete) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(RequisitionForm.FIELD.jobOrderId.name(), jobOrderId));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		if(!isComplete) {
			dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		} else {
			dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		}
		dc.add(Subqueries.propertyIn(RequisitionForm.FIELD.formWorkflowId.name(), dcWorkflow));
		return getAll(dc);
	}

	@Override
	public List<UsedRequisitionFormDto> getUsedRequisitionForm(Integer requisitionFormId) {
		List<UsedRequisitionFormDto> usedRFs = new ArrayList<>();
		UsedRequisitionFormDto useRF = null;
		List<Object> objs = new ArrayList<>();
		objs = executeSP("GET_USED_REQUISITION_FORM", requisitionFormId);
		if(objs != null && !objs.isEmpty()) {
			for(Object obj : objs) {
				Object[] row = (Object[]) obj;
				int index = 0;
				useRF = new UsedRequisitionFormDto();
				useRF.setRequisitionFormId((Integer) row[index++]);
				useRF.setRfSequenceNo((Integer) row[index++]);
				useRF.setRefererForm((String) row[index++]);
				useRF.setRefererFormSequenceNo((Integer) row[index++]);
				usedRFs.add(useRF);
			}
		}
		return usedRFs;
	}

	@Override
	public RequisitionForm getPrfByRefRequisitionFormId(Integer pRequisitionFormId, Integer refRequisitionFormId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (pRequisitionFormId != null) {
			dc.add(Restrictions.ne(RequisitionForm.FIELD.id.name(), pRequisitionFormId));
		}
		dc.add(Restrictions.eq(RequisitionForm.FIELD.reqFormRefId.name(), refRequisitionFormId));
		dc.add(Restrictions.eq(RequisitionForm.FIELD.requisitionClassificationId.name(), RequisitionClassification.RC_PURCHASE_REQUISITION));

		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		dc.add(Subqueries.propertyIn(RequisitionForm.FIELD.formWorkflowId.name(), dcWorkflow));
		return get(dc);
	}

	@Override
	public UsedRequisitionFormDto getUsedPR(Integer requisitionFormID) {
		StringBuilder strSqlBldr = new StringBuilder("SELECT RF.REQUISITION_FORM_ID, RF.SEQUENCE_NO, "
				+ "'Purchase Order', PO.PO_NUMBER FROM REQUISITION_FORM RF ");
			strSqlBldr.append("INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = RF.EB_OBJECT_ID ");
			strSqlBldr.append("INNER JOIN R_PURCHASE_ORDER PO ON PO.EB_OBJECT_ID = OTO.TO_OBJECT_ID ");
			strSqlBldr.append("INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID ");
			strSqlBldr.append("WHERE RF.REQUISITION_CLASSIFICATION_ID = ? ");
			strSqlBldr.append("AND FW.CURRENT_STATUS_ID != ? ");
			strSqlBldr.append("AND OTO.OR_TYPE_ID = ? ");
			strSqlBldr.append("AND RF.REQUISITION_FORM_ID = ?");

		Session session = null;
		UsedRequisitionFormDto usedPR = null;

		try {
			int index = 0;
			session = getSession();
			SQLQuery query = session.createSQLQuery(strSqlBldr.toString());
			query.setParameter(index, RequisitionClassification.RC_PURCHASE_REQUISITION);
			query.setParameter(++index, FormStatus.CANCELLED_ID);
			query.setParameter(++index, RPurchaseOrder.PR_PO_OR_TYPE_ID);
			query.setParameter(++index, requisitionFormID);

			List<Object[]> res = query.list();
			if(res != null && !res.isEmpty()) {
				for(Object[] obj : res) {
					int col = 0;
					usedPR = new UsedRequisitionFormDto();
					usedPR.setRequisitionFormId((Integer) obj[col++]);
					usedPR.setRfSequenceNo((Integer) obj[col++]);
					usedPR.setRefererForm((String) obj[col++]);
					usedPR.setRefererFormSequenceNo((Integer) obj[col++]);
					break;
				}
			}
		} finally {
			if(session != null) {
				session.close();
			}
		}
		return usedPR;
	}

	@Override
	public Page<RfReferenceDto> getRfReferenceForms(Integer companyId, Integer fleetId, Integer projectId,
			Integer rfNumber, Date dateFrom, Date dateTo, Integer status, Integer reqTypeId,
			boolean isExcludePakyawanSubcon, boolean isPakyawanSubconOnly, boolean isExcludePrForms,
			PageSetting pageSetting) {
		String sql = "SELECT *, SUM(QTY_FROM), SUM(QTY_TO) FROM ( "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, RF.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, COALESCE(FP.CODE_VESSEL_NAME, '') AS FLEET_NAME, AC.NAME AS CUSTOMER_NAME, RF.REMARKS, "
				+ "RFI.QUANTITY AS QTY_FROM, 0 AS QTY_TO "
				+ "FROM REQUISITION_FORM_ITEM RFI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = RFI.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = O2O.FROM_OBJECT_ID "
				+ "LEFT JOIN FLEET_PROFILE FP ON FP.FLEET_PROFILE_ID = RF.FLEET_PROFILE_ID "
				+ "LEFT JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = RF.AR_CUSTOMER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RF.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND O2O.OR_TYPE_ID = 1 "
				+ "AND RF.REQ_FORM_REF_ID IS NULL "
				+ "UNION ALL "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, TR.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, NULL AS FLEET_NAME, NULL AS CUSTOMER_NAME, '' AS REMARKS, "
				+ "0 AS QTY_FROM, TRI.QUANTITY AS QTY_TO "
				+ "FROM R_TRANSFER_RECEIPT_ITEM TRI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = TRI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RO2O ON RO2O.TO_OBJECT_ID = O2O.FROM_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = RO2O.FROM_OBJECT_ID "
				+ "INNER JOIN R_TRANSFER_RECEIPT TR ON TR.R_TRANSFER_RECEIPT_ID = TRI.R_TRANSFER_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND O2O.OR_TYPE_ID = 3002 "
				+ "AND RO2O.OR_TYPE_ID = 1 "
				+ "UNION ALL "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, RF.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, NULL AS FLEET_NAME, NULL AS CUSTOMER_NAME, '' AS REMARKS, "
				+ "0 AS QTY_FROM, WSI.QUANTITY AS QTY_TO "
				+ "FROM WITHDRAWAL_SLIP_ITEM WSI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RO2O ON RO2O.TO_OBJECT_ID = O2O.FROM_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = RO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WSOO ON WSOO.TO_OBJECT_ID = WSI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = WSOO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WS.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND WSI.ACTIVE = 1 "
				+ "AND O2O.OR_TYPE_ID = 73 "
				+ "AND RO2O.OR_TYPE_ID = 1 "
				+ "GROUP BY WITHDRAWAL_SLIP_ITEM_ID "
				+ "UNION ALL "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, RF.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, NULL AS FLEET_NAME, NULL AS CUSTOMER_NAME, '' AS REMARKS, "
				+ "0 AS QTY_FROM, SI.QUANTITY AS QTY_TO "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = O2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT WSO2O ON WSO2O.TO_OBJECT_ID = WS.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = WSO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RO2O ON RO2O.FROM_OBJECT_ID = RF.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = RO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = WS.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND O2O.OR_TYPE_ID = 3000 "
				+ "AND RO2O.OR_TYPE_ID = 1 "
				+ "AND SI.ITEM_ID = RFI.ITEM_ID "
				+ "UNION ALL "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, AT.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, NULL AS FLEET_NAME, NULL AS CUSTOMER_NAME, '' AS REMARKS, "
				+ "0 AS QTY_FROM, ASI.QUANTITY AS QTY_TO "
				+ "FROM ACCOUNT_SALE_ITEM ASI "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT TO2O ON TO2O.TO_OBJECT_ID = AT.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = TO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RO2O ON RO2O.TO_OBJECT_ID = RF.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = RO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID = 4 "
				+ "AND TO2O.OR_TYPE_ID = 3009 "
				+ "AND RO2O.OR_TYPE_ID = 1 "
				+ "AND ASI.ITEM_ID = RFI.ITEM_ID "
				+ "UNION ALL "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, AT.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, NULL AS FLEET_NAME, NULL AS CUSTOMER_NAME, '' AS REMARKS, "
				+ "0 AS QTY_FROM, SI.QUANTITY AS QTY_TO "
				+ "FROM SERIAL_ITEM SI "
				+ "INNER JOIN OBJECT_TO_OBJECT SO2O ON SO2O.TO_OBJECT_ID = SI.EB_OBJECT_ID "
				+ "INNER JOIN AR_TRANSACTION AT ON AT.EB_OBJECT_ID = SO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT TO2O ON TO2O.TO_OBJECT_ID = AT.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = TO2O.FROM_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RO2O ON RO2O.TO_OBJECT_ID = RF.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM_ITEM RFI ON RFI.EB_OBJECT_ID = RO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND SI.ACTIVE = 1 "
				+ "AND AT.AR_TRANSACTION_TYPE_ID = 4 "
				+ "AND SO2O.OR_TYPE_ID = 3004 "
				+ "AND TO2O.OR_TYPE_ID = 3009 "
				+ "AND RO2O.OR_TYPE_ID = 1 "
				+ "AND SI.ITEM_ID = RFI.ITEM_ID ";
		if (!isExcludePrForms) {
			sql += "UNION ALL "
				+ "SELECT RF.REQUISITION_FORM_ID, RF.REQUISITION_TYPE_ID, PR.COMPANY_ID, RF.FLEET_PROFILE_ID, RF.AR_CUSTOMER_ID, "
				+ "RF.DATE, RF.SEQUENCE_NO, NULL AS FLEET_NAME, NULL AS CUSTOMER_NAME, '' AS REMARKS, "
				+ "0 AS QTY_FROM, PRI.QUANTITY AS QTY_TO "
				+ "FROM PURCHASE_REQUISITION_ITEM PRI "
				+ "INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.TO_OBJECT_ID = PRI.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM PR ON PR.REQUISITION_FORM_ID = PRI.PURCHASE_REQUISITION_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.REQUISITION_FORM_ID = PR.REQ_FORM_REF_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND O2O.OR_TYPE_ID = 3012 "
				+ "AND PR.REQUISITION_CLASSIFICATION_ID = 3 "
				+ "AND PR.REQ_FORM_REF_ID IS NOT NULL ";
		}
		sql += ") AS TBL WHERE COMPANY_ID = ? ";
		if (fleetId != null) {
			sql += "AND FLEET_PROFILE_ID = ? ";
		}
		if (projectId != null) {
			sql += "AND AR_CUSTOMER_ID = ? ";
		}
		if (rfNumber != null) {
			sql += "AND SEQUENCE_NO = ? ";
		}
		if (dateFrom != null && dateTo != null) {
			sql += "AND DATE BETWEEN ? AND ? ";
		}
		if (isPakyawanSubconOnly) {
			sql += "AND REQUISITION_TYPE_ID IN (9, 10) ";
		} else if (isExcludePakyawanSubcon) {
			sql += "AND REQUISITION_TYPE_ID NOT IN (9, 10) ";
		}
		if (reqTypeId > 0) {
			sql += "AND REQUISITION_TYPE_ID = ? ";
		}
		sql += "GROUP BY REQUISITION_FORM_ID ";
		if (status == RequisitionForm.STATUS_USED) {
			sql += "HAVING SUM(QTY_TO) != 0 AND (SUM(QTY_FROM) - SUM(QTY_TO)) > 0 ";
		} else if (status == RequisitionForm.STATUS_UNUSED) {
			sql += "HAVING SUM(QTY_TO) = 0 ";
		} else {
			sql += "HAVING (SUM(QTY_FROM) - SUM(QTY_TO)) > 0 ";
		}
		sql += "ORDER BY DATE DESC, SEQUENCE_NO DESC";
		return getAllAsPage(sql, pageSetting, new RfReferenceFormHandler(companyId,
				fleetId, projectId, rfNumber, dateFrom, dateTo, reqTypeId));
	}

	private static class RfReferenceFormHandler implements QueryResultHandler<RfReferenceDto> {
		private Integer companyId;
		private Integer fleetId;
		private Integer projectId;
		private Integer rfNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer reqTypeId;

		private RfReferenceFormHandler(Integer companyId, Integer fleetId, Integer projectId,
				Integer rfNumber, Date dateFrom, Date dateTo, Integer reqTypeId) {
			this.companyId = companyId;
			this.fleetId = fleetId;
			this.projectId = projectId;
			this.rfNumber = rfNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.reqTypeId = reqTypeId;
		}
		@Override
		public List<RfReferenceDto> convert(List<Object[]> queryResult) {
			List<RfReferenceDto> rfReferences = new ArrayList<>();
			RfReferenceDto rfDto = null;
			for (Object[] row : queryResult) {
				int index = 0;
				rfDto = new RfReferenceDto();
				rfDto.setId((Integer) row[index++]);
				rfDto.setRequisitionTypeId((Integer) row[index++]);
				rfDto.setCompanyId((Integer) row[index++]);
				rfDto.setFleetProfileId((Integer) row[index++]);
				rfDto.setProjectId((Integer) row[index++]);
				rfDto.setDate((Date) row[index++]);
				rfDto.setSequenceNumber((Integer) row[index++]);
				rfDto.setFleetName((String) row[index++]);
				rfDto.setProjectName((String) row[index++]);
				rfDto.setRemarks((String) row[index++]);
				rfReferences.add(rfDto);
			}
			return rfReferences;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, companyId);
			if (fleetId != null) {
				query.setParameter(++index, fleetId);
			}
			if (projectId != null) {
				query.setParameter(++index, projectId);
			}
			if (rfNumber != null) {
				query.setParameter(++index, rfNumber);
			}
			if (dateFrom != null && dateTo != null) {
				query.setParameter(++index, dateFrom);
				query.setParameter(++index, dateTo);
			}
			if (reqTypeId > 0) {
				query.setParameter(++index, reqTypeId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REQUISITION_FORM_ID", Hibernate.INTEGER);
			query.addScalar("REQUISITION_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("FLEET_PROFILE_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("FLEET_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("REMARKS", Hibernate.STRING);
		}
	}

	@Override
	public List<RequisitionForm> getReqFormByRefId(Integer refReqFormId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);

		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));

		dc.add(Restrictions.eq(RequisitionForm.FIELD.reqFormRefId.name(), refReqFormId));
		dc.add(Subqueries.propertyIn(RequisitionForm.FIELD.formWorkflowId.name(), fwDc));

		return getAll(dc);
	}

	@Override
	public List<RequisitionForm> getReqFormByWoId(Integer woId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(RequisitionForm.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(RequisitionForm.FIELD.workOrderId.name(), woId));
		return getAll(dc);
	}

	@Override
	public double getRemainingPRQuantity(Integer referenceObjectId, Integer poId) {
		String sql = "SELECT SUM(QTY_FROM - QTY_TO) AS REMAINING_QTY, REF_OBJECT_ID FROM ( "
				+ "SELECT PRI.QUANTITY AS QTY_FROM, 0 AS QTY_TO, PRI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM PURCHASE_REQUISITION_ITEM PRI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = PRI.EB_OBJECT_ID "
				+ "INNER JOIN REQUISITION_FORM RF ON RF.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = RF.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND RF.REQUISITION_CLASSIFICATION_ID = 3 "
				+ "AND PRI.EB_OBJECT_ID = ? "
				+ "UNION ALL "
				+ "SELECT 0 AS QTY_FROM, POI.QUANTITY AS QTY_TO, PRI.EB_OBJECT_ID AS REF_OBJECT_ID "
				+ "FROM R_PURCHASE_ORDER_ITEM POI "
				+ "INNER JOIN R_PURCHASE_ORDER PO ON PO.R_PURCHASE_ORDER_ID = POI.R_PURCHASE_ORDER_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT POO2O ON POO2O.TO_OBJECT_ID = PO.EB_OBJECT_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT RFO2O ON (RFO2O.FROM_OBJECT_ID = POO2O.FROM_OBJECT_ID && RFO2O.OR_TYPE_ID = 1) "
				+ "INNER JOIN PURCHASE_REQUISITION_ITEM PRI ON PRI.EB_OBJECT_ID = RFO2O.TO_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PO.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND POO2O.OR_TYPE_ID = 3003 "
				+ "AND POI.ITEM_ID = PRI.ITEM_ID "
				+ "AND PRI.EB_OBJECT_ID = ? ";
		if (poId != null && poId != 0) {
			sql += "AND PO.R_PURCHASE_ORDER_ID != ? ";
		}
		sql += ") REMAINING_QTY_TBL ";
		Collection<Double> remainingQty = get(sql, new PRRemainingBalance(referenceObjectId, poId));
		if (remainingQty != null && !remainingQty.isEmpty()) {
			double quantity = remainingQty.iterator().next();
			return NumberFormatUtil.roundOffTo2DecPlaces(quantity) ;
		}
		return 0;
	}

	private static class PRRemainingBalance implements QueryResultHandler<Double> {
		private Integer refenceObjectId;
		private Integer poId;

		private PRRemainingBalance (Integer refenceObjectId, Integer poId) {
			this.refenceObjectId = refenceObjectId;
			this.poId = poId;
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
			int totalNoOfTables = 2;
			for (int i = 0; i < totalNoOfTables; i++) {
				query.setParameter(index, refenceObjectId);
				if(i < (totalNoOfTables-1)) {
					++index;
				}
			}
			if(poId != null && poId != 0) {
				query.setParameter(++index, poId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("REMAINING_QTY", Hibernate.DOUBLE);
			query.addScalar("REF_OBJECT_ID", Hibernate.INTEGER);
		}
	}
}
