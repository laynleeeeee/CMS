package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ArTransactionRegisterDto;
import eulap.eb.web.dto.PaymentStatus;

/**
 *  Customer Advance Payment Register Service.

 *
 */
@Service
public class CustomerAdvancePaymentRegisterService {
	@Autowired
	private CustomerAdvancePaymentDao capDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusService formStatusService;
	private static String CAP = "CustomerAdvancePayment6";
	private static Logger logger = Logger.getLogger(CustomerAdvancePaymentRegisterService.class);

	/**
	 * Generate the data source of CAP Register Report.
	 */
	public JRDataSource generateCAPRegister(Integer companyId, Integer divisionId, Integer arCustomerId,
			Integer arCustomerAccountId, Date dateFrom, Date dateTo, Integer statusId) {
		EBJRServiceHandler<ArTransactionRegisterDto> handler = new CAPRegisterJRHandler(
				companyId, divisionId ,arCustomerId, arCustomerAccountId, dateFrom, dateTo, statusId,capDao);
		return new EBDataSource<ArTransactionRegisterDto>(handler);
	}

	private static class CAPRegisterJRHandler implements EBJRServiceHandler<ArTransactionRegisterDto> {
		private static Logger logger = Logger.getLogger(CAPRegisterJRHandler.class);
		private int companyId;
		private int arCustomerId;
		private int arCustomerAccountId;
		private Date dateFrom;
		private Date dateTo;
		private int statusId;
		private int divisionId;
		private CustomerAdvancePaymentDao capDao;

		private CAPRegisterJRHandler(Integer companyId, Integer divisionId, Integer arCustomerId, Integer arCustomerAccountId,
				Date dateFrom, Date dateTo,	Integer statusId, CustomerAdvancePaymentDao capDao) {
			this.companyId = companyId;
			this.arCustomerId = arCustomerId;
			this.arCustomerAccountId = arCustomerAccountId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.statusId = statusId;
			this.capDao = capDao;
			this.divisionId=divisionId;
		}

		@Override
		public void close() throws IOException {
			capDao = null;
		}

		@Override
		public Page<ArTransactionRegisterDto> nextPage(PageSetting pageSetting) {
			Page<ArTransactionRegisterDto> capRegisterData = capDao.getCAPRegisterData(companyId, divisionId, arCustomerId,
					arCustomerAccountId, dateFrom, dateTo, statusId, pageSetting);
			if(capRegisterData.getTotalRecords() < 1) {
				logger.info("No CAP transactions found for company id: "+companyId+" for date "+
						DateUtil.formatDate(dateFrom)+" to "+DateUtil.formatDate(dateTo));
				return capRegisterData;
			}
			logger.info("Generating page "+pageSetting.getPageNumber()+" of CAP Register.");
			return capRegisterData;
		}
	}

	/**
	 * Generate the list of {@link PaymentStatus} for CAP Register Report.
	 * @return The list of Payment Statuses: Fully Served, Partially Served and Unserved.
	 */
	public List<PaymentStatus> getPaymentStatuses() {
		logger.info("Generating the list of payment statuses.");
		List<PaymentStatus> statuses = new ArrayList<PaymentStatus>();
		statuses.add(PaymentStatus.getInstanceOf(PaymentStatus.FULLY_PAID, "Fully Served"));
		statuses.add(PaymentStatus.getInstanceOf(PaymentStatus.PARTIALL_PAID, "Partially Served"));
		statuses.add(PaymentStatus.getInstanceOf(PaymentStatus.UNPAID, "Unserved"));
		statuses.add(PaymentStatus.getInstanceOf(PaymentStatus.CANCELLED, "Cancelled"));
		logger.info("Successfully generated "+statuses.size()+" payment statuses.");
		return statuses;
	}

	/**
	 * Get all enabled status for Customer Advance Payment.
	 * @param user The user object.
	 * @return List of form statuses enabled for purchase order.
	 * @throws ConfigurationException
	 */
	public List<FormStatus> getFormStatuses(User user) throws ConfigurationException {
		List<FormStatus> capStatus = workflowHandler.getAllStatuses(CAP, user, false);
		capStatus.add(formStatusService.getFormStatus(FormStatus.CANCELLED_ID));
		return capStatus;
	}
}

