package eulap.eb.service.report;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.SupplierBalancesReportDto;

/**
 * The business logic of supplier balances report.

 */
public interface SupplierBalancesReportService {

	/**
	 * Generate the supplier balances report.
	 * 
	 * @param user the current logged user.
	 * @param pageSetting The page setting.
	 * @param param The parameters that the user selected.
	 * @return The list of supplier balances dto.
	 */
	Page<SupplierBalancesReportDto> generateReport(User user, PageSetting pageSetting, SupplierBalancesParam param);
}
