package eulap.eb.service.report;

import java.util.List;

import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.PaymentRegisterDto;

/**
 * The business logic of payment report register.

 */

public interface PaymentRegisterService {

	/**
	 * Get the payment register report data
	 * Retrieves from the AP Payment table given the parameter that the user selected.
	 * For the parameter please refer to {@link PaymentRegisterParam}
	 * @param user the current logged user.
	 * @param param The parameters that the user selected.
	 * @return The payment register report data
	 */
	List<PaymentRegisterDto> generateReport(User user, PaymentRegisterParam param);
}
