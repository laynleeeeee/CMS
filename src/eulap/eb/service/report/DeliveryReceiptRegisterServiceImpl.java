package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.DeliveryReceiptDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.DeliveryReceiptRegisterDto;

/**
 * Business logic for generating report for the Delivery Receipt Register.

 */

@Service
public class DeliveryReceiptRegisterServiceImpl {
	private static Logger logger = Logger.getLogger(DeliveryReceiptRegisterServiceImpl.class);
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private DeliveryReceiptDao deliverReceiptDao;

	private static final String AR_INVOICE_CONF_TYPE = "ArInvoice3";


	/**
	 * Get the statuses of the forms.
	 * @param user The logged in user.
	 * @return The list of {@link FormStatus}.
	 */
	public List<FormStatus> getFormStatuses(User user) {
		List<FormStatus>arInvoiceStatuses = workflowServiceHandler.getAllStatuses(AR_INVOICE_CONF_TYPE, user , false);

		// Set is a unique collection.
		Set<FormStatus> statuses = new HashSet<FormStatus>();
		statuses.addAll(arInvoiceStatuses);

		FormStatus cancelled = new FormStatus();
		cancelled.setDescription(FormStatus.CANCELLED_LABEL);
		cancelled.setId(FormStatus.CANCELLED_ID);
		cancelled.setSelected(true);
		statuses.add(cancelled);
		return new ArrayList<FormStatus>(statuses);
	}

	/**
	 * Generate Jasper report of delivery receipt register.
	 * @param company object for Company
	 * @param param delivery receipt parameter.
	 * @return Generated delivery receipt register.
	 */
	public List<DeliveryReceiptRegisterDto> generateDeliveryReceiptRegister(DeliveryReceiptRegisterParam param) {
		logger.info("Retrieving report data");
		return deliverReceiptDao.searchDeliveryReceiptsRegister(param);
	}
}
