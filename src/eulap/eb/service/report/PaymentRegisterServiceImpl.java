package eulap.eb.service.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.ApPaymentDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.PaymentRegisterDto;

/**
 * Business logic for generating report for the Payment Register.

 */

@Service
public class PaymentRegisterServiceImpl implements PaymentRegisterService {
	@Autowired
	private ApPaymentDao paymentDao;

	/**
	 * Get the payment register report data
	 * @param user The current user logged
	 * @param param The payment register parameter object
	 * @return The payment register report data
	 */
	@Override
	public List<PaymentRegisterDto> generateReport(User user, PaymentRegisterParam param) {
		return paymentDao.searchPayments(param.getCompanyId(), param.getDivisionId(), param.getBankAccountId(),
				param.getSupplierId(), param.getSupplierAccountId(), param.getPaymentDateFrom(), param.getPaymentDateTo(),
				param.getCheckDateFrom(), param.getCheckDateTo(), param.getAmountFrom(), param.getAmountTo(),
				param.getVoucherNoFrom(), param.getVoucherNoTo(), param.getCheckNoFrom(), param.getCheckNoTo(),
				param.getPaymentStatusId());
	}
}
