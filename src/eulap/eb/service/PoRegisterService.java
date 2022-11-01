package eulap.eb.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.RPurchaseOrderDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.PoRegisterDto;

/**
 * Class that handles all the business logic of purchase order register report

 *
 */
@Service
public class PoRegisterService {
	@Autowired
	private RPurchaseOrderDao poDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private TermService termService;

	/**
	 * Generate purchase order register report data.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param supplierId The supplier id.
	 * @param supplierAccountId The supplier account id.
	 * @param termId The term id.
	 * @param poDateFrom The purchase order start date.
	 * @param poDateTo The purchase order end date.
	 * @param rrDateFrom The receiving report start date.
	 * @param rrDateTo The receiving report end date.
	 * @param statusId The status id.
	 * @param deliveryStatus The delivery status.
	 * @return The purchase order register report data.
	 */
	public List<PoRegisterDto> getPoRegisterData(Integer companyId, Integer divisionId, Integer supplierId, Integer supplierAccountId,
			Integer termId, Date poDateFrom, Date poDateTo, Date rrDateFrom, Date rrDateTo, Integer statusId,
			String deliveryStatus) {
		return poDao.getPoRegisterData(companyId, divisionId, supplierId, supplierAccountId, termId, poDateFrom, poDateTo,
				rrDateFrom, rrDateTo, statusId, deliveryStatus);
	}

	/**
	 * Get all enabled status for purchase order.
	 * @param user The user object.
	 * @return List of form statuses enabled for purchase order.
	 * @throws ConfigurationException
	 */
	public List<FormStatus> getFormStatuses(User user) throws ConfigurationException {
		List<FormStatus> poStatus = workflowHandler.getAllStatuses("RPurchaseOrder1", user, false);
		poStatus.add(formStatusService.getFormStatus(FormStatus.CANCELLED_ID));
		return poStatus;
	}

	/**
	 * Get the list of terms.
	 * @param user The user object.
	 * @return The list of terms.
	 */
	public List<Term> getTerms(User user) {
		return termService.getTerms(user);
	}
}
