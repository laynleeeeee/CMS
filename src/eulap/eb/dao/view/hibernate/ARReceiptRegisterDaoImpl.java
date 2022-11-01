package eulap.eb.dao.view.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.PageDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.view.ARReceiptRegisterDao;
import eulap.eb.domain.view.hibernate.ARReceiptRegister;
import eulap.eb.service.report.ReceiptRegisterParam;

/**
 * Implementation class of {@link ARReceiptRegisterDao}

 *
 */
public class ARReceiptRegisterDaoImpl extends PageDao<ARReceiptRegister> implements ARReceiptRegisterDao {
	private static Logger logger = Logger.getLogger(ARReceiptRegisterDaoImpl.class);

	@Override
	protected Class<ARReceiptRegister> getDomainClass() {
		return ARReceiptRegister.class;
	}

	@Override
	public Page<ARReceiptRegister> getReceiptRegisterData(ReceiptRegisterParam param, PageSetting pageSetting) {
		DetachedCriteria rRegisterDc = getDetachedCriteria();

		//TODO : Add division filter.
		//Company
		int companyId = param.getCompanyId();
		if(companyId != ARReceiptRegister.ALL_OPTION){
			logger.debug("Searching for company with id: "+companyId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.companyId.name(), companyId));
		}

		//Division
		int divisionId = param.getDivisionId();
		if(divisionId != ARReceiptRegister.ALL_OPTION){
			logger.debug("Searching for division with id: "+divisionId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.divisionId.name(), divisionId));
		}

		//Source
		int sourceId = param.getSourceId();
		if(sourceId != ARReceiptRegister.ALL_OPTION){
			logger.debug("Searching for source with id: "+sourceId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.sourceId.name(), sourceId));
		}

		//Receipt Type
		int receiptTypeId = param.getReceiptTypeId();
		if(receiptTypeId != ARReceiptRegister.ALL_OPTION) {
			logger.debug("Searching for receipt type with id: "+receiptTypeId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.receiptTypeId.name(), receiptTypeId));
		}

		//Receipt Method
		int receiptMethodId = param.getReceiptMethodId();
		if(receiptMethodId != ARReceiptRegister.ALL_OPTION) {
			logger.debug("Searching for receipt method with id: "+receiptMethodId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.receiptMethodId.name(), receiptMethodId));
		}

		//Receipt Number
		String receiptNumber = param.getReceiptNo();
		if(receiptNumber != null) {
			if(!receiptNumber.trim().isEmpty()) {
				logger.debug("Searching for receipt number: "+receiptNumber);
				rRegisterDc.add(Restrictions.like(ARReceiptRegister.FIELD.receiptNumber.name(),
						"%"+receiptNumber.trim()+"%"));
			}
		}

		//Customer
		int customerId = param.getCustomerId();
		if(customerId != ARReceiptRegister.ALL_OPTION) {
			logger.debug("Searching for customer with id: "+customerId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.customerId.name(), customerId));
		}

		//Customer Account
		int customerAcctId = param.getCustomerAcctId();
		if(customerAcctId != -1) {
			logger.debug("Searching for customer account with id: "+customerAcctId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.customerAcctId.name(), customerAcctId));
		}

		//Receipt Date
		if(param.getReceiptDateFrom() != null || param.getReceiptDateTo() != null) {
			rRegisterDc.add(getDateCriterion(param.getReceiptDateFrom(),
					param.getReceiptDateTo(), ARReceiptRegister.FIELD.receiptDate.name()));
		}

		//Maturity Date
		if(param.getMaturityDateFrom() != null || param.getMaturityDateTo() != null) {
			rRegisterDc.add(getDateCriterion(param.getMaturityDateFrom(),
					param.getMaturityDateTo(), ARReceiptRegister.FIELD.maturityDate.name()));
		}

		//Amount
		Double amountFrom = param.getAmountFrom();
		Double amountTo = param.getAmountTo();
		if(amountFrom != null && amountTo != null) {
			logger.debug("Searching for amount between "+amountFrom+" to "+amountTo);
			rRegisterDc.add(Restrictions.between(ARReceiptRegister.FIELD.amount.name(), amountFrom, amountTo));
		} else if(amountFrom != null || amountTo != null) {
			Double amount = amountFrom != null ? amountFrom : amountTo;
			logger.debug("Searching for amount "+amount);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.amount.name(), amount));
		}

		//Workflow Status
		int wfStatusId = param.getWfStatusId();
		if(wfStatusId != ARReceiptRegister.ALL_OPTION) {
			logger.debug("Searching for status: "+wfStatusId);
			rRegisterDc.add(Restrictions.eq(ARReceiptRegister.FIELD.statusId.name(), wfStatusId));
		}

		//Order
		rRegisterDc.addOrder(Order.asc(ARReceiptRegister.FIELD.receiptDate.name()));
		rRegisterDc.addOrder(Order.asc(ARReceiptRegister.FIELD.receiptTypeId.name()));
		rRegisterDc.addOrder(Order.asc(ARReceiptRegister.FIELD.receiptNumber.name()));
		rRegisterDc.addOrder(Order.asc(ARReceiptRegister.FIELD.customer.name()));
		rRegisterDc.addOrder(Order.asc(ARReceiptRegister.FIELD.customerAcct.name()));
		rRegisterDc.addOrder(Order.asc(ARReceiptRegister.FIELD.source.name()));
		logger.debug("Successfully retrieved data from AR Receipt Register.");
		return getAll(rRegisterDc, pageSetting);
	}
}
