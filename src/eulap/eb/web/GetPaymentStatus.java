package eulap.eb.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import bp.web.ar.CurrentSessionHandler;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.workflow.WorkflowServiceHandler;

/**
 * Controller that will retrieve the payment statuses
 * based on the service lease key of the logged user.

 *
 */
@Controller
@RequestMapping(value="getPaymentStatus")
public class GetPaymentStatus {
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;

	private static String AP_PAYMENT = "ApPayment";
	private static String DIVISION_CENTRAL = "1";
	private static Logger logger = Logger.getLogger(GetPaymentStatus.class);

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getPaymentStatus(HttpSession session) {
		logger.info("Retrieving the AP Payment statuses.");
		User user = CurrentSessionHandler.getLoggedInUser(session);
		List<FormStatus> paymentStatuses = workflowHandler.getAllStatuses(AP_PAYMENT+DIVISION_CENTRAL, user, false);

		//Add APPROVED Status to the list
//		FormStatus approvedStatus = formStatusDao.get(FormStatus.APPROVED_ID);
//		paymentStatuses.add(approvedStatus);

		//Add CANCELLED Status to the list
		FormStatus cancelledStatus = formStatusDao.get(FormStatus.CANCELLED_ID);
		paymentStatuses.add(cancelledStatus);


		JsonConfig jConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(paymentStatuses, jConfig);
		logger.debug("Retrieved "+paymentStatuses.size()+" statuses for AP Payment.");
		return jsonArray.toString();
	}
}
