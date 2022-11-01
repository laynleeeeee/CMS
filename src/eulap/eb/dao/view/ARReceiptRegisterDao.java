package eulap.eb.dao.view;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.view.hibernate.ARReceiptRegister;
import eulap.eb.service.report.ReceiptRegisterParam;

/**
 * Data Access Layer of {@link ARReceiptRegister}

 *
 */
public interface ARReceiptRegisterDao {

	/**
	 * Retrieve the data to generate the Receipt Register report.
	 * @param rRegister The AR Receipt Register object that will hold the search parameters.
	 * @param pageSetting The page setting of the report.
	 * @return The AR Receipt Register in paging format.
	 */
	Page<ARReceiptRegister> getReceiptRegisterData(ReceiptRegisterParam param, PageSetting pageSetting);
}
