package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.PettyCashReplenishmentLineDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PettyCashReplenishmentLine;
import eulap.eb.service.report.PCVRRegisterParam;
import eulap.eb.web.dto.PCReplenishmentRegisterDto;

/**
 * DAO implementation class for {@link PettyCashReplenishmentLineDao}

 */

public class PettyCashReplenishmentLineDaoImpl extends BaseDao<PettyCashReplenishmentLine> implements PettyCashReplenishmentLineDao {

	@Override
	protected Class<PettyCashReplenishmentLine> getDomainClass() {
		return PettyCashReplenishmentLine.class;
	}

	@Override
	public Page<PettyCashReplenishmentLine> getReplenishments(Integer divisionId, Integer userCustodianId, PageSetting pageSetting) {
		boolean hasDivision = divisionId != null;
		boolean hasUserCustodian = userCustodianId != null;
		String sql = "SELECT PCVLL.PETTY_CASH_VOUCHER_LIQUIDATION_LINE_ID ,PCVL.PCVL_DATE, PCVL.SEQUENCE_NO, PCVLL.BMS_NUMBER, PCVLL.OR_NUMBER, PCVL.REQUESTOR, S.NAME, S.TIN, "
				+ "S.STREET_BRGY, S.CITY_PROVINCE, PCVLL.DESCRIPTION, D.NAME AS DIVISION_NAME, D.DIVISION_ID, A.ACCOUNT_NAME, A.ACCOUNT_ID, PCVLL.UP_AMOUNT, "
				+ "TT.NAME AS TAX_TYPE_NAME, PCVLL.VAT_AMOUNT, PCVLL.AMOUNT, PCVLL.EB_OBJECT_ID "
				+ "FROM PETTY_CASH_VOUCHER_LIQUIDATION_LINE PCVLL "
				+ "INNER JOIN PETTY_CASH_VOUCHER_LIQUIDATION PCVL ON PCVL.PETTY_CASH_VOUCHER_LIQUIDATION_ID = PCVLL.PETTY_CASH_VOUCHER_LIQUIDATION_ID "
				+ "INNER JOIN USER_CUSTODIAN UC ON UC.USER_CUSTODIAN_ID = PCVL.USER_CUSTODIAN_ID "
				+ "INNER JOIN CUSTODIAN_ACCOUNT CA ON CA.CUSTODIAN_ACCOUNT_ID = UC.CUSTODIAN_ACCOUNT_ID "
				+ "INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = PCVLL.ACCOUNT_COMBINATION_ID "
				+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID "
				+ "INNER JOIN DIVISION D ON D.DIVISION_ID = AC.DIVISION_ID "
				+ "INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = PCVLL.SUPPLIER_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PCVL.FORM_WORKFLOW_ID "
				+ "LEFT JOIN TAX_TYPE TT ON TT.TAX_TYPE_ID = PCVLL.TAX_TYPE_ID "
				+ "WHERE FW.IS_COMPLETE = 1 "
				+ (hasDivision ? "AND PCVL.DIVISION_ID = ? " : "")
				+ (hasUserCustodian ? "AND PCVL.USER_CUSTODIAN_ID = ? " : "")
				+ "AND PCVLL.EB_OBJECT_ID NOT IN (SELECT OTO.FROM_OBJECT_ID FROM PETTY_CASH_REPLENISHMENT_LINE PCRL "
				+ "INNER JOIN AP_INVOICE AP ON AP.AP_INVOICE_ID = PCRL.AP_INVOICE_ID "
				+ "INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = PCRL.EB_OBJECT_ID "
				+ "INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID "
				+ "WHERE FW.CURRENT_STATUS_ID != 4 "
				+ "AND OTO.OR_TYPE_ID = 24008) "
				+ "ORDER BY PCVL.PCVL_DATE, PCVL.SEQUENCE_NO";
		ReplenishmentsHandler handler = new ReplenishmentsHandler(divisionId, userCustodianId);
		return getAllAsPage(sql, pageSetting, handler);
	}

	private static class ReplenishmentsHandler implements QueryResultHandler<PettyCashReplenishmentLine>{
		private Integer divisionId;
		private Integer userCustodianId;

		private ReplenishmentsHandler(Integer divisionId, Integer userCustodianId) {
			this.divisionId = divisionId;
			this.userCustodianId = userCustodianId;
		}

		@Override
		public List<PettyCashReplenishmentLine> convert(List<Object[]> queryResult) {
			List<PettyCashReplenishmentLine> pcrls = new ArrayList<PettyCashReplenishmentLine>();
			for(Object[] rowResult : queryResult) {
				PettyCashReplenishmentLine pcrl = new PettyCashReplenishmentLine();
				int colNum = 0;
				pcrl.setPcvllId((Integer) rowResult[colNum++]);
				pcrl.setPcvlDateString(DateUtil.formatDate((Date) rowResult[colNum++]));
				pcrl.setSequenceNo((Integer) rowResult[colNum++]);
				pcrl.setBmsNumber((String) rowResult[colNum++]);
				pcrl.setOrNumber((String) rowResult[colNum++]);
				pcrl.setRequestor((String) rowResult[colNum++]);
				pcrl.setSupplierName((String) rowResult[colNum++]);
				pcrl.setSupplierTin(StringFormatUtil.processTin((String) rowResult[colNum++]));
				pcrl.setBrgyStreet((String) rowResult[colNum++]);
				pcrl.setCity((String) rowResult[colNum++]);
				pcrl.setDescription((String) rowResult[colNum++]);
				pcrl.setDivisionName((String) rowResult[colNum++]);
				pcrl.setDivisionId((Integer) rowResult[colNum++]);
				pcrl.setAccountName((String) rowResult[colNum++]);
				pcrl.setAccountId((Integer) rowResult[colNum++]);
				pcrl.setGrossAmount((Double) rowResult[colNum++]);
				pcrl.setTaxName((String) rowResult[colNum++]);
				pcrl.setVatAmount((Double) rowResult[colNum++]);
				pcrl.setAmount((Double) rowResult[colNum++]);
				pcrl.setRefenceObjectId((Integer) rowResult[colNum++]);
				pcrls.add(pcrl);
			}
			return pcrls;
		}

		@Override
		public int setParamater(SQLQuery query) {
			int index = 0;
			if(divisionId != null) {
				query.setParameter(index, divisionId);
			}
			if(userCustodianId != null) {
				query.setParameter(++index, userCustodianId);
			}
			return index;
		}

		@Override
		public void setScalars(SQLQuery query) {
			query.addScalar("PETTY_CASH_VOUCHER_LIQUIDATION_LINE_ID", Hibernate.INTEGER);
			query.addScalar("PCVL_DATE", Hibernate.DATE);
			query.addScalar("SEQUENCE_NO", Hibernate.INTEGER);
			query.addScalar("BMS_NUMBER", Hibernate.STRING);
			query.addScalar("OR_NUMBER", Hibernate.STRING);
			query.addScalar("REQUESTOR", Hibernate.STRING);
			query.addScalar("NAME", Hibernate.STRING);
			query.addScalar("TIN", Hibernate.STRING);
			query.addScalar("STREET_BRGY", Hibernate.STRING);
			query.addScalar("CITY_PROVINCE", Hibernate.STRING);
			query.addScalar("DESCRIPTION", Hibernate.STRING);
			query.addScalar("DIVISION_NAME", Hibernate.STRING);
			query.addScalar("DIVISION_ID", Hibernate.INTEGER);
			query.addScalar("ACCOUNT_NAME", Hibernate.STRING);
			query.addScalar("ACCOUNT_ID", Hibernate.INTEGER);
			query.addScalar("UP_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("TAX_TYPE_NAME", Hibernate.STRING);
			query.addScalar("VAT_AMOUNT", Hibernate.DOUBLE);
			query.addScalar("AMOUNT", Hibernate.DOUBLE);
			query.addScalar("EB_OBJECT_ID", Hibernate.INTEGER);
		}
	}

	@Override
	public List<PettyCashReplenishmentLine> getAssociatedPettyCashReplenishment(Integer objectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria otoDc = DetachedCriteria.forClass(ObjectToObject.class);
		otoDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.orTypeId.name(), PettyCashReplenishmentLine.PCRL_PCVLL_RELATIONSHIP));
		otoDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), objectId));

		DetachedCriteria fwDc = DetachedCriteria.forClass(FormWorkflow.class);
		fwDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		fwDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));

		DetachedCriteria aiDc = DetachedCriteria.forClass(APInvoice.class);
		aiDc.setProjection(Projections.property(APInvoice.FIELD.id.name()));
		aiDc.add(Subqueries.propertyIn(APInvoice.FIELD.formWorkflowId.name(), fwDc));
		dc.add(Subqueries.propertyIn(PettyCashReplenishmentLine.FIELD.apInvoiceId.name(), aiDc));

		dc.add(Subqueries.propertyIn(PettyCashReplenishmentLine.FIELD.ebObjectId.name(), otoDc));
		return getAll(dc);
	}

	@Override
	public Page<PCReplenishmentRegisterDto> getReplenishmentsRegister(PCVRRegisterParam param,
			PageSetting pageSetting) {
		PCReplenishmentHandler pcreplenishmenthandler = new PCReplenishmentHandler();
		return executePagedSP("GET_PC_REPLENISHMENT_REGISTER", pageSetting, pcreplenishmenthandler,
				param.getCompanyId(), param.getDivisionId(), param.getCustodianId(), param.getPcrNo(),
				param.getDateFrom(), param.getDateTo(), param.getStatus());
	}

	private static class PCReplenishmentHandler implements QueryResultHandler<PCReplenishmentRegisterDto> {

		private PCReplenishmentHandler() {}

		@Override
		public List<PCReplenishmentRegisterDto> convert(List<Object[]> queryResult) {
			List<PCReplenishmentRegisterDto> pcReplenishmentItems = new ArrayList<PCReplenishmentRegisterDto>();
			PCReplenishmentRegisterDto pcRR = null;
			for (Object[] rowResult : queryResult) {
				pcRR = new PCReplenishmentRegisterDto();
				pcRR.setDivisionName((String)rowResult[0]);
				pcRR.setPcrDate((Date)rowResult[1]);
				pcRR.setPcrNo((Integer)rowResult[2]);
				pcRR.setCustodianName((String)rowResult[3]);
				pcRR.setPcvlDate((Date)rowResult[4]);
				pcRR.setPcvlNo((Integer)rowResult[5]);
				pcRR.setBmsNo((String)rowResult[6]);
				pcRR.setOrSi((String)rowResult[7]);
				pcRR.setRequestor((String)rowResult[8]);
				pcRR.setSupplier((String)rowResult[9]);
				pcRR.setTinNo((String)rowResult[10]);
				pcRR.setStreet((String)rowResult[11]);
				pcRR.setCityProvince((String)rowResult[12]);
				pcRR.setDescription((String)rowResult[13]);
				pcRR.setAccount((String)rowResult[14]);
				pcRR.setGrossAmount((double)rowResult[15]);
				pcRR.setStatus((String)rowResult[16]);
				pcRR.setPvcrId((Integer)rowResult[17]);
				pcRR.setRemarks((String)rowResult[18]);
				pcReplenishmentItems.add(pcRR);
			}
			return pcReplenishmentItems;
		}

		@Override
		public int setParamater(SQLQuery query) {
			return 0;
		}

		@Override
		public void setScalars(SQLQuery query) {
			// do nothing
		}
	}
}
