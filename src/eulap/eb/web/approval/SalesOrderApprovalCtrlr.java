package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.service.fap.MultiFormPlugin;
import eulap.eb.service.fap.SalesOrderAppPlugin;

/**
 * Form approval controller for {@link SalesOrder}

 *
 */
@Controller
@RequestMapping("/salesOrderWorkflow/{typeId}")
public class SalesOrderApprovalCtrlr extends WSMultiFormController {
	@Autowired
	private SalesOrderAppPlugin plugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return plugin;
	}
}
