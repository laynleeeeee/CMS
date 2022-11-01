package eulap.eb.service.fsp;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.ApPaymentService;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;
/**
 * A service that handles the searching of AP Payments.

 */
@Service
public class ApPaymentSearcher implements SearchableFormPlugin{
	@Autowired
	private ApPaymentDao apPaymentDao;
	@Autowired
	private ApPaymentService apPaymentService;

	@Override
	public List<FormSearchResult> search(User user, String searchCriteria) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");
		Page<ApPayment> payments = apPaymentDao.searchPayment(searchCriteriaFinal, new PageSetting(1), PaymentType.TYPE_AP_PAYMENT);
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (ApPayment payment : payments.getData()) {
			apPaymentService.getApPayment(payment);
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String supplier = payment.getSupplier().getName();
			String comp = payment.getCompany().getName();
			String bankAcct = payment.getBankAccount().getName();
			String supplierAcct = payment.getSupplierAccount().getName();
			String status = payment.getFormWorkflow().getCurrentFormStatus().getDescription();
			String title = ("Check# " + payment.getCheckNumber());
			properties.add(ResultProperty.getInstance("Company", comp));
			properties.add(ResultProperty.getInstance("Payment Date", DateUtil.formatDate(payment.getPaymentDate())));
			properties.add(ResultProperty.getInstance("Bank Account", bankAcct));
			properties.add(ResultProperty.getInstance("Check Date", DateUtil.formatDate(payment.getCheckDate())));
			properties.add(ResultProperty.getInstance("Check Amount", NumberFormatUtil.format(payment.getAmount())));
			properties.add(ResultProperty.getInstance("Supplier", supplier));
			properties.add(ResultProperty.getInstance("Supplier Account", supplierAcct));
			properties.add(ResultProperty.getInstance("Voucher No.", Integer.toString(payment.getVoucherNumber())));
			properties.add(ResultProperty.getInstance("Status", status));
			
			result.add(FormSearchResult.getInstanceOf(payment.getId(), title, properties));
		}
		return result;
	}
}
