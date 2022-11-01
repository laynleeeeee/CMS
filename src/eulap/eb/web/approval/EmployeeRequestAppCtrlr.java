package eulap.eb.web.approval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.service.EmployeeRequestAppPlugin;
import eulap.eb.service.fap.MultiFormPlugin;

/**
 * Search controller for {@link EmployeeRequest}

 *
 */
@Controller
@RequestMapping("/employeeRequestWorkFlow/{typeId}")
public class EmployeeRequestAppCtrlr extends WSMultiFormController {
	@Autowired
	private EmployeeRequestAppPlugin plugin;

	@Override
	MultiFormPlugin getMultiFormPlugin() {
		return plugin;
	}

}
