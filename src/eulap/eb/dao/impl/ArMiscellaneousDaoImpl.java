package eulap.eb.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
import eulap.eb.dao.ArMiscellaneousDao;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArMiscellaneous;
import eulap.eb.domain.hibernate.ArMiscellaneousLine;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.web.dto.ApprovalSearchParam;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDATDto;
import eulap.eb.web.dto.QuarterlyAlphaListOfPayeesDto;


/**
 * Implementing class of {@link ArMiscellaneousDao}

 *
 */
public class ArMiscellaneousDaoImpl extends BaseDao<ArMiscellaneous> implements ArMiscellaneousDao{

	@Override
	protected Class<ArMiscellaneous> getDomainClass() {
		return ArMiscellaneous.class;
	}

	@Override
	public Page<ArMiscellaneous> searchArMiscellaneous(final String searchCriteria,
			final PageSetting pageSetting) {
		return searchArMiscellaneous(searchCriteria, pageSetting, 0);
	}
	@Override
	public Page<ArMiscellaneous> searchArMiscellaneous(final String searchCriteria,
			final PageSetting pageSetting, int divisionId) {
		HibernateCallback<Page<ArMiscellaneous>> arMiscellaneousCallBack = new HibernateCallback<Page<ArMiscellaneous>>() {
			@Override
			public Page<ArMiscellaneous> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria =  session.createCriteria(ArMiscellaneous.class);
				if (!searchCriteria.trim().isEmpty())  {
					Criterion rNumberCrit = Restrictions.like(ArMiscellaneous.FIELD.receiptNumber.name(), 
							"%" + searchCriteria.trim() + "%");
					Criterion descCrit = Restrictions.like(ArMiscellaneous.FIELD.description.name(), 
							"%" + searchCriteria.trim() + "%");

					if (!StringFormatUtil.isNumeric(searchCriteria)) {
						criteria.add(Restrictions.or(rNumberCrit, descCrit));
					} else {
						Criterion tNumOrDescCrit = Restrictions.or(rNumberCrit, descCrit);
						criteria.add(Restrictions.or(tNumOrDescCrit,
								Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", "%" + searchCriteria.trim() + "%", 
										Hibernate.STRING)));
					}
				}
				if(divisionId != 0) {
					criteria.add(Restrictions.eq(ArMiscellaneous.FIELD.divisionId.name(), divisionId));
				}
				Page<ArMiscellaneous> arMiscellaneouses = getAll(criteria, pageSetting);
				for (ArMiscellaneous arM: arMiscellaneouses.getData()) {
					getHibernateTemplate().initialize(arM.getArCustomer());
					getHibernateTemplate().initialize(arM.getArCustomerAccount());
				}
				return arMiscellaneouses;
			}
		};
		return getHibernateTemplate().execute(arMiscellaneousCallBack);
	}

	@Override
	public Page<ArMiscellaneous> getAllArMiscellaneous(final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting) {
			return getAllArMiscellaneous(0, searchParam, formStatusIds, pageSetting);
		}

	@Override
	public Page<ArMiscellaneous> getAllArMiscellaneous(final int typeId,final ApprovalSearchParam searchParam,
			final List<Integer> formStatusIds, final PageSetting pageSetting) {
		HibernateCallback<Page<ArMiscellaneous>> hibernateCallback = new HibernateCallback<Page<ArMiscellaneous>>() {

			@Override
			public Page<ArMiscellaneous> doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria arMiscCriteria = session.createCriteria(ArMiscellaneous.class);
				DetachedCriteria arCusAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
				SearchCommonUtil.searchCommonParams(arMiscCriteria, arCusAcctCriteria, ArMiscellaneous.FIELD.arCustomerAccountId.name(),
						ArMiscellaneous.FIELD.receiptDate.name(), ArMiscellaneous.FIELD.maturityDate.name(), null, searchParam.getUser().getCompanyIds(), searchParam);

				if(typeId != 0) {
					arMiscCriteria.add(Restrictions.eq(ArMiscellaneous.FIELD.divisionId.name(), typeId));
				}

				//Search for receipt number and/or description
				String criteria = searchParam.getSearchCriteria();
				if (criteria != null) {
					Criterion rNumberCrit = Restrictions.like(ArMiscellaneous.FIELD.receiptNumber.name(), "%" + criteria.trim() + "%");
					Criterion descCrit = Restrictions.like(ArMiscellaneous.FIELD.description.name(), "%" + criteria.trim() + "%");
					if (StringFormatUtil.isNumeric(criteria)) {
						arMiscCriteria.add(Restrictions.or(rNumberCrit, descCrit));
					} else {
						Criterion tNumOrDescCrit = Restrictions.or(rNumberCrit, descCrit);
						arMiscCriteria.add(Restrictions.or(tNumOrDescCrit,
								Restrictions.sqlRestriction("SEQUENCE_NO LIKE ? ", "%" + criteria.trim() + "%", 
										Hibernate.STRING)));
					}
				}
				// Workflow status
				DetachedCriteria dcWorkFlow = DetachedCriteria.forClass(FormWorkflow.class);
				if (formStatusIds.size() > 0)
					addAsOrInCritiria(dcWorkFlow, FormWorkflow.FIELD.currentStatusId.name(), formStatusIds);
				dcWorkFlow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
				arMiscCriteria.addOrder(Order.desc(ArMiscellaneous.FIELD.receiptDate.name()));
				arMiscCriteria.addOrder(Order.desc(ArMiscellaneous.FIELD.maturityDate.name()));
				arMiscCriteria.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.formWorkflowId.name(), dcWorkFlow));
				Page<ArMiscellaneous> ret = getAll(arMiscCriteria, pageSetting);
				for (ArMiscellaneous miscellaneous : ret.getData()) {
					getHibernateTemplate().initialize(miscellaneous.getArCustomer());
					getHibernateTemplate().initialize(miscellaneous.getArCustomerAccount());
				}
				return ret;
			}
		};
		return getHibernateTemplate().execute(hibernateCallback);
	}

	@Override
	public boolean isUniqueReceiptNo(ArMiscellaneous arMiscellaneous, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();

		// AR Customer Account criteria.
		DetachedCriteria arCustAcctDc = DetachedCriteria.forClass(ArCustomerAccount.class);
		arCustAcctDc.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
		arCustAcctDc.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
		dc.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.arCustomerAccountId.name(), arCustAcctDc));

		dc.add(Restrictions.eq(ArMiscellaneous.FIELD.receiptNumber.name(), arMiscellaneous.getReceiptNumber().trim()));
		dc.add(Restrictions.ne(ArMiscellaneous.FIELD.id.name(), arMiscellaneous.getId()));

		return getAll(dc).size() == 0;
	}

	@Override
	public Page<ArMiscellaneous> getArMiscellaneous(int companyId,	int miscellaneousTypeId, int receiptMethodId, int customerId,
			int customerAcctId, String receiptNumber, Date receiptDateFrom,	Date receiptDateTo, Date maturityDateFrom, 
			Date maturityDateTo, Double amountFrom, Double amountTo, int wfStatusId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		//Company
		if(companyId != -1){
			customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.arCustomerAccountId.name(), customerAcctCriteria));
		}
		//Miscellaneous Type
		if(miscellaneousTypeId != -1)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.arMiscellaneousTypeId.name(), miscellaneousTypeId));
		//Receipt Method
		if(receiptMethodId != -1)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.receiptMethodId.name(), receiptMethodId));
		//Customer
		if(customerId != -1)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.arCustomerId.name(), customerId));
		//Customer Account
		if(customerAcctId != -1)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.arCustomerAccountId.name(), customerAcctId));
		//Receipt Number
		if(!receiptNumber.isEmpty() || receiptNumber != null)
			dc.add(Restrictions.like(ArMiscellaneous.FIELD.receiptNumber.name(), "%"+receiptNumber.trim()+"%"));
		//Receipt Date
		if(receiptDateFrom != null && receiptDateTo != null)
			dc.add(Restrictions.between(ArMiscellaneous.FIELD.receiptDate.name(), receiptDateFrom, receiptDateTo));
		else if(receiptDateFrom != null)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.receiptDate.name(), receiptDateFrom));
		else if(receiptDateTo != null)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.receiptDate.name(), receiptDateTo));
		//Maturity Date
		if(maturityDateFrom != null && maturityDateTo != null)
			dc.add(Restrictions.between(ArMiscellaneous.FIELD.maturityDate.name(), maturityDateFrom, maturityDateTo));
		else if(maturityDateFrom != null)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.maturityDate.name(), maturityDateFrom));
		else if(maturityDateTo != null)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.maturityDate.name(), maturityDateTo));
		//Amount
		if(amountFrom != null && amountTo != null)
			dc.add(Restrictions.between(ArMiscellaneous.FIELD.amount.name(), amountFrom, amountTo));
		else if(amountFrom != null)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.amount.name(), amountFrom));
		else if(amountTo != null)
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.amount.name(), amountTo));
		//Receipt Status
		if(wfStatusId != -1) {
			DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
			dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.currentStatusId.name(), wfStatusId));
			dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.formWorkflowId.name(), dcWorkflow));
		}
		dc.addOrder(Order.asc(ArMiscellaneous.FIELD.receiptDate.name()));
		dc.addOrder(Order.asc(ArMiscellaneous.FIELD.arMiscellaneousTypeId.name()));
		dc.addOrder(Order.asc(ArMiscellaneous.FIELD.receiptNumber.name()));
		dc.createAlias("arCustomer", "arCustomer").addOrder(Order.asc("arCustomer.name"));
		dc.createAlias("arCustomerAccount", "arCustomerAccount").addOrder(Order.asc("arCustomerAccount.name"));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<ArMiscellaneous> getArMiscellaneous(Integer companyId,
			Integer arLineSetupId, Integer unitOfMeasureId, Date receiptDateFrom,
			Date receiptDateTo, Date maturityDateFrom, Date maturityDateTo,
			Integer customerId, Integer customerAcctId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria customerAcctCriteria = DetachedCriteria.forClass(ArCustomerAccount.class);
		//Company
		if(companyId != -1){
			customerAcctCriteria.add(Restrictions.eq(ArCustomerAccount.FIELD.companyId.name(), companyId));
			customerAcctCriteria.setProjection(Projections.property(ArCustomerAccount.FIELD.id.name()));
			dc.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.arCustomerAccountId.name(), customerAcctCriteria));
		}
		//AR Miscellaneous Line
		DetachedCriteria arLineCriteria = DetachedCriteria.forClass(ArMiscellaneousLine.class);
		arLineCriteria.add(Restrictions.eq(ArMiscellaneousLine.FIELD.arLineSetupId.name(), arLineSetupId));
		if (unitOfMeasureId != 0) // If unit of measure not equal to None
			arLineCriteria.add(Restrictions.eq(ArMiscellaneousLine.FIELD.unitOfMeasurementId.name(), unitOfMeasureId));
		else
			arLineCriteria.add(Restrictions.isNull(ArMiscellaneousLine.FIELD.unitOfMeasurementId.name()));
		arLineCriteria.setProjection(Projections.property(ArMiscellaneousLine.FIELD.arMiscellaneousId.name()));
		dc.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.id.name(), arLineCriteria));

		//Receipt Date
		processDate(dc, ArMiscellaneous.FIELD.receiptDate.name(), receiptDateFrom, receiptDateTo);
		//Maturity Date
		processDate(dc, ArMiscellaneous.FIELD.maturityDate.name(), maturityDateFrom, maturityDateTo);
		//Customer
		if(customerId != -1) {
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.arCustomerId.name(), customerId));
			//Customer Account
			if(customerAcctId != -1)
				dc.add(Restrictions.eq(ArMiscellaneous.FIELD.arCustomerAccountId.name(), customerAcctId));
		}

		//Workflow status criteria
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(ArMiscellaneous.FIELD.formWorkflowId.name(), dcWorkflow));

		dc.addOrder(Order.asc(ArMiscellaneous.FIELD.receiptDate.name()));
		dc.addOrder(Order.asc(ArMiscellaneous.FIELD.maturityDate.name()));
		return getAll(dc, pageSetting);
	}

	private void processDate (DetachedCriteria dc, String fieldName, Date fromDate,Date toDate) {
		if(fromDate != null && toDate != null)
			dc.add(Restrictions.between(fieldName, fromDate, toDate));
		else if(fromDate != null)
			dc.add(Restrictions.eq(fieldName, fromDate));
		else if(toDate != null)
			dc.add(Restrictions.eq(fieldName, toDate));
	}

	@Override
	public ArMiscellaneous getArMiscellaneousByWorkflow(Integer formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArMiscellaneous.FIELD.formWorkflowId.name(), formWorkflowId));
		return get(dc);
	}

	@Override
	public Integer generateSequenceNumber (int companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(ArMiscellaneous.FIELD.sequenceNo.name()));
		dc.add(Restrictions.eq(ArMiscellaneous.FIELD.companyId.name(), companyId));
		if(divisionId != null) {
			dc.add(Restrictions.eq(ArMiscellaneous.FIELD.divisionId.name(), divisionId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public List<QuarterlyAlphaListOfPayeesDto> getQuarterlyAlphalistOfPayees(Integer companyId, Integer divisionId,
			Integer month, Integer year, boolean processTin, String wtTypeId) {
		List<QuarterlyAlphaListOfPayeesDto> qaops = new ArrayList<QuarterlyAlphaListOfPayeesDto>();
		//wtTypeId handles a string as a list and comma separated. Format: "0,1,2" or "0"
		List<Object> objects = executeSP("GET_QUARTERLY_ALPHALIST_OF_PAYEES", companyId, divisionId, month, year, wtTypeId);
		if(objects != null && !objects.isEmpty()) {
			QuarterlyAlphaListOfPayeesDto dto = null;
			Object[] row = null;
			for (Object obj: objects) {
				row = (Object[]) obj;
				dto = new QuarterlyAlphaListOfPayeesDto();
				String tin = (String) row[1];
				if (tin != null && !tin.isEmpty()) {
					dto.setTin(processTin ? StringFormatUtil.processBirTinTo13Digits(tin) : StringFormatUtil.parseBIRTIN(tin));
				}
				dto.setCorporateName((String) row[2]);
				dto.setIndividualName((String) row[3]);
				dto.setLastName((String) row[4]);
				dto.setFirstName((String) row[5]);
				dto.setMiddleName((String) row[6]);
				dto.setAtcCode((String) row[7]);
				dto.setNatureOfPayment((String) row[8]);
				dto.setFirstAmount(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[9]));
				dto.setFirstTaxRate(getTaxValue((String) row[10]));
				dto.setFirstTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[11]));
				dto.setSecondAmount(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[12]));
				dto.setSecondTaxRate(getTaxValue((String) row[13]));
				dto.setSecondTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[14]));
				dto.setThirdAmount(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[15]));
				dto.setThirdTaxRate(getTaxValue((String) row[16]));
				dto.setThirdTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces((Double) row[17]));
				double totalAmount = dto.getFirstAmount() + dto.getSecondAmount() + dto.getThirdAmount();
				double totalTaxWithheld = dto.getFirstTaxWithheld() + dto.getSecondTaxWithheld() + dto.getThirdTaxWithheld();
				dto.setTotalAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount));
				dto.setTotalTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces(totalTaxWithheld));
				qaops.add(dto);
				dto = null;
			}
		}
		return qaops;
	}

	private double getTaxValue(String strTaxValue) {
		String taxValues[] = strTaxValue.split(",");
		double taxValue = 0;
		for (String str : taxValues) {
			if (!str.trim().isEmpty()) {
				taxValue += Integer.parseInt(str.trim());
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(taxValue);
	}

	@Override
	public List<QuarterlyAlphaListOfPayeesDATDto> getAlphalistOfPayees(Integer companyId, Integer divisionId,
			Integer monthFrom, Integer monthTo, Integer year, String wtTypeId) {
		List<QuarterlyAlphaListOfPayeesDATDto> qaops = new ArrayList<QuarterlyAlphaListOfPayeesDATDto>();
		//wtTypeId handles a string as a list and comma separated. Format: "0,1,2" or "0"
		List<Object> objects = executeSP("GET_ALPHALIST_OF_PAYEES", companyId, divisionId, monthFrom, monthTo, year, wtTypeId);
		if(objects != null && !objects.isEmpty()) {
			QuarterlyAlphaListOfPayeesDATDto dto = null;
			for(Object obj: objects) {
				Object[] row = (Object[]) obj;
				Double totalAmount = 0.0;
				Double totalTaxWithheld = 0.0;
				if((Integer) row[0] != null) {
					dto = new QuarterlyAlphaListOfPayeesDATDto();
					String tin = (String) row[1];
					dto.setTin(StringFormatUtil.parseBIRTIN(tin));
					String branchCode = StringFormatUtil.parseBranchCode(tin);
					dto.setBranchCode(branchCode);
					dto.setCorporateName((String) row[2]);
					dto.setIndividualName((String) row[3]);
					dto.setLastName((String) row[4]);
					dto.setFirstName((String) row[5]);
					dto.setMiddleName((String) row[6]);
					dto.setAtcCode((String) row[7]);
					dto.setNatureOfPayment((String) row[8]);
					dto.setFirstAmount(NumberFormatUtil.convertDouble((Double) row[9]));
					dto.setFirstTaxRate(NumberFormatUtil.convertDouble((Double) row[10]));
					dto.setFirstTaxWithheld(NumberFormatUtil.convertDouble((Double) row[11]));
					totalAmount +=  Double.parseDouble(dto.getFirstAmount());
					totalTaxWithheld += Double.parseDouble(dto.getFirstTaxWithheld());
					dto.setTotalAmount(NumberFormatUtil.convertDouble(totalAmount));
					dto.setTotalTaxWithheld(NumberFormatUtil.convertDouble(totalTaxWithheld));
					qaops.add(dto);
				}
			}
		}
		return qaops;
	}
}
