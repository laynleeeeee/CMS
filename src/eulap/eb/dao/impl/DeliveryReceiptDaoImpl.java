package eulap.eb.dao.impl;

import java.util.ArrayList;
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
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.domain.hibernate.DeliveryReceipt;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.fap.FormPluginParam;
import eulap.eb.service.report.DeliveryReceiptRegisterParam;
import eulap.eb.web.dto.DeliveryReceiptRegisterDto;
import eulap.eb.web.dto.DrReferenceDto;

/**
 * Implementing class of {@link DeliveryReceiptDao}

 */

public class DeliveryReceiptDaoImpl extends BaseDao<DeliveryReceipt> implements DeliveryReceiptDao {

	@Override
	protected Class<DeliveryReceipt> getDomainClass() {
		return DeliveryReceipt.class;
	}

	@Override
	public Page<DeliveryReceipt> getDeliveryReceipts(int typeId, FormPluginParam param) {
		DetachedCriteria dc = getDetachedCriteria();
		addUserCompany(dc, param.getUser());
		if(!param.getSearchCriteria().trim().isEmpty()){
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", param.getSearchCriteria(), Hibernate.STRING));
		}
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.deliveryReceiptTypeId.name(), typeId));
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		if (param.getStatuses().size() > 0)
			addAsOrInCritiria(dcWorkflow, FormWorkflow.FIELD.currentStatusId.name(), param.getStatuses());
		dcWorkflow.addOrder(Order.asc(FormWorkflow.FIELD.currentStatusId.name()));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(DeliveryReceipt.FIELD.formWorkflowId.name(), dcWorkflow));
		dc.addOrder(Order.desc(DeliveryReceipt.FIELD.sequenceNo.name()));
		dc.addOrder(Order.desc(DeliveryReceipt.FIELD.date.name()));
		return getAll(dc, param.getPageSetting());
	}

	@Override
	public Page<DeliveryReceipt> searchDeliveryReceipts(Integer typeId, String criteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.deliveryReceiptTypeId.name(), typeId));
		if(StringFormatUtil.isNumeric(criteria)) {
			dc.add(Restrictions.sqlRestriction("SEQUENCE_NO LIKE ?", criteria.trim(), Hibernate.STRING));
		}
		dc.addOrder(Order.desc(DeliveryReceipt.FIELD.sequenceNo.name()));
		dc.addOrder(Order.desc(DeliveryReceipt.FIELD.date.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public int generateSeqNo(int companyId, Integer typeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(DeliveryReceipt.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.companyId.name(), companyId));
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.deliveryReceiptTypeId.name(), typeId));
		return generateSeqNo(dc);
	}

	private String excludedDrIds(int arInvoiceTypeId) {
		StringBuilder savedDrIds = new StringBuilder();
		String sql = "SELECT ARI.AR_INVOICE_ID, COALESCE(ARI.DR_REFERENCE_IDS, '') AS DR_REFERENCE_IDS FROM AR_INVOICE ARI "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND ARI.AR_INVOICE_TYPE_ID = ? ";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, arInvoiceTypeId);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				int count = 0;
				for (Object[] row : list) {
					if (count == 0) {
						savedDrIds.append((String) row[1]);
					} else {
						savedDrIds.append(", " + (String) row[1]);
					}
					count++;
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return savedDrIds.toString();
	}

	private String processStrIds(String strIds) {
		String processedIds = "";
		String tmpIds[] = strIds.split(";");
		int row = 0;
		for (String tmp : tmpIds) {
			if (tmp != null && !tmp.isEmpty()) {
				if (row == 0) {
					processedIds += tmp.trim();
				} else {
					processedIds += ", " + tmp.trim();
				}
			}
			row++;
		}
		return removeExtraCommas(processedIds);
	}

	@Override
	public Page<DeliveryReceipt> getDRReferences(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer drNumber, Integer statusId, Date dateFrom, Date dateTo, String drRefNumber, Integer typeId, PageSetting pageSetting) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ( ");
		String itemSql = "SELECT DR.DELIVERY_RECEIPT_ID, DR.SEQUENCE_NO, DR.COMPANY_ID, DR.AR_CUSTOMER_ID, "
				+ "DR.AR_CUSTOMER_ACCOUNT_ID, DR.REMARKS, DR.DELIVERY_RECEIPT_TYPE_ID, DR.DATE, DR.DR_REF_NUMBER, "
				+ "DR.WT_ACCOUNT_SETTING_ID, COALESCE(WTAS.NAME, '') AS WTAX_NAME, AC.NAME AS CUSTOMER_NAME, "
				+ "ACA.NAME AS CUSTOMER_ACCT_NAME, DR.CURRENCY_ID "
				+ "FROM DELIVERY_RECEIPT_ITEM DRI "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = DR.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = DR.AR_CUSTOMER_ACCOUNT_ID "
				+ "LEFT JOIN WT_ACCOUNT_SETTING WTAS ON WTAS.WT_ACCOUNT_SETTING_ID = DR.WT_ACCOUNT_SETTING_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1  "
				+ "AND DR.COMPANY_ID = ? "
				+ (divisionId != null ? "AND DR.DIVISION_ID = ? " : "")
				+ (drRefNumber != null && !drRefNumber.trim().isEmpty() ? "AND DR.DR_REF_NUMBER LIKE ? " : "")
				+ (drNumber != null ? "AND DR.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND DR.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAccountId != null ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ ((dateFrom != null && dateTo != null) ? "AND DR.DATE BETWEEN ? AND ? " : "")
				+ "UNION ALL "
				+ "SELECT DR.DELIVERY_RECEIPT_ID, DR.SEQUENCE_NO, DR.COMPANY_ID, DR.AR_CUSTOMER_ID, "
				+ "DR.AR_CUSTOMER_ACCOUNT_ID, DR.REMARKS, DR.DELIVERY_RECEIPT_TYPE_ID, DR.DATE, DR.DR_REF_NUMBER, "
				+ "DR.WT_ACCOUNT_SETTING_ID, COALESCE(WTAS.NAME, '') AS WTAX_NAME, AC.NAME AS CUSTOMER_NAME, "
				+ "ACA.NAME AS CUSTOMER_ACCT_NAME, DR.CURRENCY_ID "
				+ "FROM SERIAL_ITEM DRI "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = DRI.EB_OBJECT_ID "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID "
				+ "INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = DR.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = DR.AR_CUSTOMER_ACCOUNT_ID "
				+ "LEFT JOIN WT_ACCOUNT_SETTING WTAS ON WTAS.WT_ACCOUNT_SETTING_ID = DR.WT_ACCOUNT_SETTING_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND FW.IS_COMPLETE = 1  "
				+ "AND DRI.ACTIVE = 1 "
				+ "AND OTO.OR_TYPE_ID = 12004 "
				+ "AND DR.COMPANY_ID = ? "
				+ (divisionId != null ? "AND DR.DIVISION_ID = ? " : "")
				+ (drRefNumber != null && !drRefNumber.trim().isEmpty() ? "AND DR.DR_REF_NUMBER LIKE ? " : "")
				+ (drNumber != null ? "AND DR.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND DR.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAccountId != null ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ ((dateFrom != null && dateTo != null) ? "AND DR.DATE BETWEEN ? AND ? " : "");
		String drLineSql = "SELECT DR.DELIVERY_RECEIPT_ID, DR.SEQUENCE_NO, DR.COMPANY_ID, DR.AR_CUSTOMER_ID, DR.AR_CUSTOMER_ACCOUNT_ID, "
				+ "DR.REMARKS, DR.DELIVERY_RECEIPT_TYPE_ID, DR.DATE, DR.DR_REF_NUMBER, DR.WT_ACCOUNT_SETTING_ID, COALESCE(WTAS.NAME, '') AS WTAX_NAME, "
				+ "AC.NAME AS CUSTOMER_NAME, ACA.NAME AS CUSTOMER_ACCT_NAME, DR.CURRENCY_ID "
				+ "FROM DELIVERY_RECEIPT_LINE DRL "
				+ "INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRL.DELIVERY_RECEIPT_ID "
				+ "INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = DR.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON ACA.AR_CUSTOMER_ACCOUNT_ID = DR.AR_CUSTOMER_ACCOUNT_ID "
				+ "LEFT JOIN WT_ACCOUNT_SETTING WTAS ON WTAS.WT_ACCOUNT_SETTING_ID = DR.WT_ACCOUNT_SETTING_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ "AND DR.COMPANY_ID = ? "
				+ (divisionId != null ? "AND DR.DIVISION_ID = ? " : "")
				+ (drRefNumber != null && !drRefNumber.trim().isEmpty() ? "AND DR.DR_REF_NUMBER LIKE ? " : "")
				+ (drNumber != null ? "AND DR.SEQUENCE_NO = ? " : "")
				+ (arCustomerId != null ? "AND DR.AR_CUSTOMER_ID = ? " : "")
				+ (arCustomerAccountId != null ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : "")
				+ ((dateFrom != null && dateTo != null) ? "AND DR.DATE BETWEEN ? AND ? " : "");
		sql.append(itemSql);
		sql.append("UNION ALL ");
		sql.append(drLineSql);
		sql.append(") AS TBL WHERE DELIVERY_RECEIPT_ID NOT IN ( "+excludedDrIds(typeId)
				+ ") GROUP BY DELIVERY_RECEIPT_ID ORDER BY DATE DESC, SEQUENCE_NO DESC ");
		return getAllAsPage(sql.toString(), pageSetting, new DrReferenceHandler(companyId, divisionId, arCustomerId,
				arCustomerAccountId, drNumber, dateFrom, dateTo, drRefNumber, 3));
	}

	private static class DrReferenceHandler implements QueryResultHandler<DeliveryReceipt> {
		private Integer companyId;
		private Integer arCustomerId;
		private Integer arCustomerAccountId;
		private Integer drNumber;
		private Date dateFrom;
		private Date dateTo;
		private Integer divisionId;
		private String drRefNumber;
		private int noOfTbls;

		private DrReferenceHandler(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
				Integer drNumber, Date dateFrom, Date dateTo, String drRefNumber, int noOfTbls) {
			this.companyId = companyId;
			this.arCustomerId = arCustomerId;
			this.arCustomerAccountId = arCustomerAccountId;
			this.drNumber = drNumber;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.noOfTbls = noOfTbls;
			this.divisionId = divisionId;
			this.drRefNumber = drRefNumber;
		}

		@Override
		public List<DeliveryReceipt> convert(List<Object[]> queryResult) {
			List<DeliveryReceipt> deliveryReceipts = new ArrayList<DeliveryReceipt>();
			DeliveryReceipt dr = null;
			int index;
			for (Object[] row : queryResult) {
				index = 0;
				dr = new DeliveryReceipt();
				dr.setId((Integer) row[index]);
				dr.setSequenceNo((Integer) row[++index]);
				dr.setCompanyId((Integer) row[++index]);
				dr.setArCustomerId((Integer) row[++index]);
				dr.setArCustomerAccountId((Integer) row[++index]);
				dr.setRemarks((String) row[++index]);
				dr.setDeliveryReceiptTypeId((Integer) row[++index]);
				dr.setDate((Date) row[++index]);
				dr.setDrRefNumber((String) row[++index]);
				dr.setWtAcctSettingId((Integer) row[++index]);
				dr.setWtAcctPercentage((String) row[++index]);
				dr.setCustomerName((String) row[++index]);
				dr.setCustomerAcctName((String) row[++index]);
				dr.setCurrencyId((Integer) row[++index]);
				deliveryReceipts.add(dr);
			}
			return deliveryReceipts;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			for (int i = 0; i < noOfTbls; i++) {
				query.setParameter(index, companyId);
				if(divisionId != null) {
					query.setParameter(++index, divisionId);
				}
				if(drRefNumber != null && !drRefNumber.trim().isEmpty()) {
					query.setParameter(++index, StringFormatUtil.appendWildCard(drRefNumber));
				}
				if (drNumber != null) {
					query.setParameter(++index, drNumber);
				}
				if (arCustomerId != null) {
					query.setParameter(++index, arCustomerId);
				}
				if (arCustomerAccountId != null) {
					query.setParameter(++index, arCustomerAccountId);
				}
				if (dateFrom != null && dateTo != null) {
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
			query.addScalar("DELIVERY_RECEIPT_ID", Hibernate.INTEGER);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("COMPANY_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("AR_CUSTOMER_ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("REMARKS", Hibernate.STRING);
			query.addScalar("DELIVERY_RECEIPT_TYPE_ID", Hibernate.INTEGER);
			query.addScalar("DATE", Hibernate.DATE);
			query.addScalar("DR_REF_NUMBER", Hibernate.STRING);
			query.addScalar("WT_ACCOUNT_SETTING_ID", Hibernate.INTEGER);
			query.addScalar("WTAX_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_NAME", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCT_NAME", Hibernate.STRING);
			query.addScalar("CURRENCY_ID", Hibernate.INTEGER);
		}
	}

	@Override
	public List<DeliveryReceipt> getDRsByATWId(Integer atwId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));

		dc.add(Subqueries.propertyIn(DeliveryReceipt.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.authorityToWithdrawId.name(), atwId));
		return getAll(dc);
	}

	@Override
	public DeliveryReceipt getDrByTypeId(Integer drId, Integer drTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.id.name(), drId));
		if (drTypeId != null && drTypeId > 0) {
			dc.add(Restrictions.eq(DeliveryReceipt.FIELD.deliveryReceiptTypeId.name(), drTypeId));
		}
		return get(dc);
	}

	@Override
	public List<DeliveryReceipt> getUsedDrsBySoId(Integer soId, Integer drTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		if (drTypeId != null) {
			dc.add(Restrictions.eq(DeliveryReceipt.FIELD.deliveryReceiptTypeId.name(), drTypeId));
		}
		dc.add(Subqueries.propertyIn(DeliveryReceipt.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Restrictions.eq(DeliveryReceipt.FIELD.salesOrderId.name(), soId));
		return getAll(dc);
	}

	@Override
	public List<DrReferenceDto> getDrReferenceDtos() {
		List<DrReferenceDto> drReferenceDtos = new ArrayList<DrReferenceDto>();
		String sql = "SELECT CONCAT('ARI ', IF(ARI.AR_INVOICE_TYPE_ID = 1, 'G- ', 'S- '), ARI.SEQUENCE_NO) AS SEQUENCE_NO, ARI.DR_REFERENCE_IDS "
				+ "FROM AR_INVOICE ARI "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4";
		Session session = null;
		DrReferenceDto dto = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.addScalar("SEQUENCE_NO", Hibernate.STRING);
			query.addScalar("DR_REFERENCE_IDS", Hibernate.STRING);
			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					dto = new DrReferenceDto();
					dto.setArInvoiceNumber((String) row[0]);
					String strDrIds = processStrIds((String) row[1]);
					dto.setDrReferenceIds(getDrIds(strDrIds));
					drReferenceDtos.add(dto);
				}
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return drReferenceDtos;
	}

	private List<Integer> getDrIds(String strDrIds) {
		List<Integer> drIds = new ArrayList<Integer>();
		if (strDrIds != null && !strDrIds.isEmpty()) {
			String tmpIds[] = strDrIds.split(",");
			for (String id : tmpIds) {
				if (!id.trim().isEmpty()) {
					drIds.add(Integer.parseInt(id.trim()));
				}
			}
		}
		return drIds;
	}

	private String removeExtraCommas(String strIds) {
		String processedIds = "";
		String tmpIds[] = strIds.split(",");
		int row = 0;
		for (String tmp : tmpIds) {
			tmp = tmp.trim();
			if (!tmp.isEmpty()) {
				if (row == 0) {
					processedIds += tmp.trim();
				} else {
					processedIds += ", " + tmp.trim();
				}
				row++;
			}
		}
		return processedIds;
	}

	@Override
	public List<DeliveryReceiptRegisterDto> searchDeliveryReceiptsRegister(DeliveryReceiptRegisterParam param) {
		String sql = "SELECT DR.COMPANY_ID, DR.DIVISION_ID, D.NAME AS DIVISION, SO.SEQUENCE_NO AS SO_NUMBER, "
				+ "SO.PO_NUMBER AS PO_PCR_NUMBER, SO.AMOUNT AS SO_AMOUNT, C.NAME AS CUSTOMER, DR.AR_CUSTOMER_ID AS CUSTOMER_ID, "
				+ "CA.NAME AS CUSTOMER_ACCOUNT, DR.AR_CUSTOMER_ACCOUNT_ID AS CUSTOMER_ACCT_ID, TRM.NAME AS TERM, "
				+ "DR.DATE AS DR_DATE, DR.SEQUENCE_NO AS DR_NUMBER, DR.DR_REF_NUMBER AS REF_NUMBER, FS.DESCRIPTION AS DR_STATUS, "
				+ "FW.CURRENT_STATUS_ID AS DR_STATUS_ID, DR.REMARKS AS SHIP_TO, DR.RECEIVER AS DR_RECEIVED_BY, DR.DATE_RECEIVED AS DR_RECEIVED_DATE, "
				+ "IF (FW.CURRENT_STATUS_ID = 4, (SELECT FWL.COMMENT FROM FORM_WORKFLOW_LOG FWL WHERE FWL.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID "
				+ "AND FWL.FORM_STATUS_ID = 4), '') AS CANCELLATION_REMARKS "
				+ "FROM DELIVERY_RECEIPT DR "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = DR.DIVISION_ID "
				+ "INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID "
				+ "INNER JOIN AR_CUSTOMER C ON C.AR_CUSTOMER_ID = DR.AR_CUSTOMER_ID "
				+ "INNER JOIN AR_CUSTOMER_ACCOUNT CA ON CA.AR_CUSTOMER_ACCOUNT_ID = DR.AR_CUSTOMER_ACCOUNT_ID "
				+ "INNER JOIN TERM TRM ON TRM.TERM_ID = DR.TERM_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID "
				+ "INNER JOIN FORM_STATUS FS ON FS.FORM_STATUS_ID = FW.CURRENT_STATUS_ID "
				+ "WHERE DR.COMPANY_ID = ? "
				+ (param.getDivisionId() != -1 ? "AND DR.DIVISION_ID = ? " : "" )
				+ (param.getCustomerId() != -1 ? "AND DR.AR_CUSTOMER_ID = ? " : " " )
				+ (param.getCustomerAcctId() != -1 ? "AND DR.AR_CUSTOMER_ACCOUNT_ID = ? " : " " )
				+ (!param.getSoNumber().trim().isEmpty() ? "AND SO.SEQUENCE_NO LIKE ? " : " " )
				+ (!param.getPoPcrNumber().trim().isEmpty() ? "AND SO.PO_NUMBER LIKE ? " : " " )
				+ (param.getDrNumberFrom() != null && param.getDrNumberTo() != null ? "AND DR.SEQUENCE_NO BETWEEN ? AND ? " : " ")
				+ (param.getDrDateFrom() != null && param.getDrDateTo() != null ? "AND DR.DATE BETWEEN ? AND ? " : "")
				+ (param.getDeliveryReceiptStatus() != -1 ? "AND FW.CURRENT_STATUS_ID = ? " : "")
				+ "ORDER BY SO.SEQUENCE_NO";
		DeliveryReceiptRegisterHandler handler = new DeliveryReceiptRegisterHandler(param);
		return (List<DeliveryReceiptRegisterDto>) get(sql, handler);
	}

	private static class DeliveryReceiptRegisterHandler implements QueryResultHandler<DeliveryReceiptRegisterDto> {
		private DeliveryReceiptRegisterParam param;

		private DeliveryReceiptRegisterHandler(DeliveryReceiptRegisterParam param) {
			this.param = param;
		}

		@Override
		public List<DeliveryReceiptRegisterDto> convert(List<Object[]> queryResult) {
			List<DeliveryReceiptRegisterDto> deliveryRecieptRegister = new ArrayList<DeliveryReceiptRegisterDto>();
			DeliveryReceiptRegisterDto deliveryReceiptDto = null;
			for (Object[] rowResult : queryResult) {
				int colNum = 0;
				deliveryReceiptDto = new DeliveryReceiptRegisterDto();
				deliveryReceiptDto.setDivision((String) rowResult[colNum]);
				deliveryReceiptDto.setSoNumber((String) rowResult[++colNum]);
				deliveryReceiptDto.setPoPcrNumber((String) rowResult[++colNum]);
				deliveryReceiptDto.setSoAmount((double) rowResult[++colNum]);
				deliveryReceiptDto.setCustomerName((String) rowResult[++colNum]);
				deliveryReceiptDto.setCustomerAcct((String) rowResult[++colNum]);
				deliveryReceiptDto.setTermName((String) rowResult[++colNum]);
				deliveryReceiptDto.setDrDate((Date) rowResult[++colNum]);
				deliveryReceiptDto.setDrNumber((Integer) rowResult[++colNum]);
				deliveryReceiptDto.setDrRefNumber((String) rowResult[++colNum]);
				deliveryReceiptDto.setDeliveryReceiptStatus((String) rowResult[++colNum]);
				deliveryReceiptDto.setShipTo((String) rowResult[++colNum]);
				deliveryReceiptDto.setDrReceivedBy((String) rowResult[++colNum]);
				deliveryReceiptDto.setDrReceivedDate((Date) rowResult[++colNum]);
				deliveryReceiptDto.setCancellationRemarks((String) rowResult[++colNum]);
				deliveryRecieptRegister.add(deliveryReceiptDto);
			}
			return deliveryRecieptRegister;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			query.setParameter(index, param.getCompanyId());
			if (param.getDivisionId() != -1) {
				query.setParameter(++index, param.getDivisionId());
			}
			if (param.getCustomerId() != -1) {
				query.setParameter(++index, param.getCustomerId());
			}
			if (param.getCustomerAcctId() != -1) {
				query.setParameter(++index, param.getCustomerAcctId());
			}
			if (!param.getSoNumber().isEmpty()) {
				query.setParameter(++index, param.getSoNumber());
			}
			if (!param.getPoPcrNumber().isEmpty()) {
				query.setParameter(++index, param.getPoPcrNumber());
			}
			if (param.getDrNumberFrom() != null && param.getDrNumberTo() != null) {
				query.setParameter(++index, param.getDrNumberFrom());
				query.setParameter(++index, param.getDrNumberTo());
			}
			if (param.getDrDateFrom() != null && param.getDrDateTo() != null) {
			query.setParameter(++index, param.getDrDateFrom());
			query.setParameter(++index, param.getDrDateTo());
			}
			if (param.getDeliveryReceiptStatus() != -1) {
				query.setParameter(++index, param.getDeliveryReceiptStatus());
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("DIVISION", Hibernate.STRING);
			query.addScalar("SO_NUMBER", Hibernate.STRING);
			query.addScalar("PO_PCR_NUMBER", Hibernate.STRING);
			query.addScalar("SO_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("CUSTOMER", Hibernate.STRING);
			query.addScalar("CUSTOMER_ACCOUNT", Hibernate.STRING);
			query.addScalar("TERM", Hibernate.STRING);
			query.addScalar("DR_DATE", Hibernate.DATE);
			query.addScalar("DR_NUMBER", Hibernate.INTEGER);
			query.addScalar("REF_NUMBER", Hibernate.STRING);
			query.addScalar("DR_STATUS", Hibernate.STRING);
			query.addScalar("SHIP_TO", Hibernate.STRING);
			query.addScalar("DR_RECEIVED_BY", Hibernate.STRING);
			query.addScalar("DR_RECEIVED_DATE", Hibernate.DATE);
			query.addScalar("CANCELLATION_REMARKS", Hibernate.STRING);
			query.addScalar("CUSTOMER_ID", Hibernate.INTEGER);
			query.addScalar("CUSTOMER_ACCT_ID", Hibernate.INTEGER);
			query.addScalar("DR_STATUS_ID", Hibernate.INTEGER);
		}
	}
}
