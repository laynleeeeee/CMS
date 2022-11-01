package eulap.eb.service.report;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApPaymentInvoiceDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApPaymentInvoice;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.PaymentStatus;
import eulap.eb.web.dto.SupplierBalancesReportDto;
/**
 * Business logic for generating report for supplier balances.

 */
@Service
public class SupplierBalancesReportServiceImpl implements SupplierBalancesReportService{

	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private ApPaymentInvoiceDao paymentInvoiceDao;
	private final Logger logger = Logger.getLogger(SupplierBalancesReportServiceImpl.class);

	@Override
	public Page<SupplierBalancesReportDto> generateReport(User user,
			PageSetting pageSetting, SupplierBalancesParam param) {
		Page<APInvoice> invoices = apInvoiceDao.searchInvoices(param.getCompanyId(),
				param.getSupplierId(), param.getSupplierAccountId(),
				param.getAsOfDate(), pageSetting);
		Collection<SupplierBalancesReportDto> supplierBalances = new ArrayList<SupplierBalancesReportDto>();
		for (APInvoice inv : invoices.getData()) {
			Double balance = inv.getAmount() - totalPaidAmount(inv);
			setPaymentStatus(inv, balance);
			if(inv.getPaymentStatus() != 1){
				Supplier supplier = supplierDao.get(inv.getSupplierId());
				SupplierAccount supplierAcct = supplierAccountDao.get(inv.getSupplierAccountId());
				//Add data to supplier balances dto
				supplierBalances.add(SupplierBalancesReportDto.getInstanceOf(inv, supplier, supplierAcct, balance));
			}
		}
		return new Page<SupplierBalancesReportDto>(pageSetting, supplierBalances, invoices.getTotalRecords());
	}

	/**
	 * Compute the total paid amount of the invoice.
	 * @param apInvoice The invoice object.
	 * @return the total paid amount
	 */
	public Double totalPaidAmount(APInvoice apInvoice) {
		Collection<ApPaymentInvoice> payments = paymentInvoiceDao.getPaidInvoices(apInvoice.getId());
		Double totalPaidAmount = 0.0;
		for (ApPaymentInvoice apPaymentInvoice : payments) {
			totalPaidAmount += apPaymentInvoice.getPaidAmount();
		}
		logger.debug("The total paid amount of invoice number "+
				apInvoice.getInvoiceNumber()+" is: " +totalPaidAmount);
		return totalPaidAmount;
	}

	/**
	 * Set the payment status of the AP Invoice.
	 * <br>FULLY PAID : balance == 0.0
	 * <br>PARTIALLY PAID : balance < invoice.getAmount()
	 * <br>UNPAID : balance == invoice.getAmount()
	 */
	private void setPaymentStatus(APInvoice invoice, Double balance) {
		if(balance == 0.0)
			invoice.setPaymentStatus(PaymentStatus.FULLY_PAID);
		else if(balance < invoice.getAmount())
			invoice.setPaymentStatus(PaymentStatus.PARTIALL_PAID);
		else if (balance.equals(invoice.getAmount()))
			invoice.setPaymentStatus(PaymentStatus.UNPAID);
	}
}
