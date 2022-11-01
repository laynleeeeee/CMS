package eulap.eb.service.report;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.view.hibernate.ARReceiptRegister;

/**
 * The business logic of receipt register report.

 */
public interface ReceiptRegisterService {

	/**
	 * Generate the AR Receipt Register report.
	 * Sources are from the following:
	 * <br> Account Collection (AR Receipt)
	 * <br> AR Miscellaneous
	 * <br> Cash Sales
	 * <br> For the parameter please refer to {@link ReceiptRegisterParam}
	 * 
	 * @param user The current logged user.
	 * @param pageSetting The page setting.
	 * @param param The parameters that the user selected.
	 * @return The paged {@link ARReceiptRegister}.
	 */
	Page<ARReceiptRegister> generateReport(User user, PageSetting pageSetting, ReceiptRegisterParam param);
}
