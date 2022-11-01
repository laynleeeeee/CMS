package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.TransactionClassification;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.TransactionClassificationService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ArTransactionRegisterDto;
import eulap.eb.web.dto.PaymentStatus;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for generating report for the AR Transasction Register.

 */
@Service
public class ArTransactionRegisterServiceImpl {
	private static Logger logger = Logger.getLogger(ArTransactionRegisterServiceImpl.class);
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private TransactionClassificationService transactionClassificationService;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;

	private static final int AR_INVOICE_CLASSIFICATION_TYPE = 4;
	private static final String AR_TRANSACTION_CONF_TYPE = "ArTransaction17" ;
	private static final String AR_INVOICE_CONF_TYPE = "ArInvoice3";

	/**
	 * Create the Payment Statuses.
	 * <br>The payment statuses are: Fully Paid, Partially Paid and Unpaid
	 * @return List of payment status.
	 */
	public List<PaymentStatus> paymentStatus() {
		logger.debug("Creating the payment statuses.");
		List<PaymentStatus> payments = new ArrayList<PaymentStatus>();
		payments.add(PaymentStatus.getInstanceOf(PaymentStatus.FULLY_PAID, "Fully Paid"));
		payments.add(PaymentStatus.getInstanceOf(PaymentStatus.PARTIALL_PAID, "Partially Paid"));
		payments.add(PaymentStatus.getInstanceOf(PaymentStatus.UNPAID, "Unpaid"));
		return payments;
	}

	/**
	 * Get the statuses of the forms.
	 * @param user The logged in user.
	 * @return The list of {@link FormStatus}.
	 */
	public List<FormStatus> getFormStatuses(User user) {
		List<FormStatus> arTransactStatuses = workflowServiceHandler.getAllStatuses(AR_TRANSACTION_CONF_TYPE, user, false);
		List<FormStatus>arInvoiceStatuses = workflowServiceHandler.getAllStatuses(AR_INVOICE_CONF_TYPE, user , false);
		
		// Set is a unique collection. 
		Set<FormStatus> statuses = new HashSet<FormStatus>();
		statuses.addAll(arTransactStatuses);
		statuses.addAll(arInvoiceStatuses);
		
		FormStatus cancelled = new FormStatus();
		cancelled.setDescription(FormStatus.CANCELLED_LABEL);
		cancelled.setId(FormStatus.CANCELLED_ID);
		cancelled.setSelected(true);
		statuses.add(cancelled);
		return new ArrayList<FormStatus>(statuses);
	}

	/**
	 * Generate Jasper report of transaction register.
	 * @param company object for Company
	 * @param param Transaction parameter.
	 * @return Generated transaction register.
	 */
	public JRDataSource generateTransactionRegister(ArTransactionRegisterParam param) {
		EBJRServiceHandler<ArTransactionRegisterDto> handler = new JRTransactionHandler(param, this);
		return new EBDataSource<ArTransactionRegisterDto>(handler);
	}

	private static class JRTransactionHandler implements EBJRServiceHandler<ArTransactionRegisterDto> {
		private ArTransactionRegisterParam param;
		private ArTransactionRegisterServiceImpl registerServiceImpl;

		private JRTransactionHandler (ArTransactionRegisterParam param,
				ArTransactionRegisterServiceImpl registerServiceImpl){
			this.param = param;
			this.registerServiceImpl = registerServiceImpl;
		}

		@Override
		public void close() throws IOException {
			registerServiceImpl = null;
		}

		@Override
		public Page<ArTransactionRegisterDto> nextPage(PageSetting pageSetting) {
			return registerServiceImpl.arTransactionDao.searchTransaction(param, pageSetting);
		}
	}

	/**
	 * Retrieve the list of transaction classifications and add ar invoice classification type.
	 * @param user The user object.
	 * @return The list of transaction classifications.
	 */
	public List<TransactionClassification> showTransactionClassification (User  user){
		List<TransactionClassification> transactionClassifications = transactionClassificationService.getAllTransactionClassifications(user);
		TransactionClassification transactionClassification = new TransactionClassification();
		transactionClassification.setId(AR_INVOICE_CLASSIFICATION_TYPE);
		transactionClassification.setName("Ar Invoice");
		transactionClassifications.add(transactionClassification);
		return transactionClassifications;
	}
}
