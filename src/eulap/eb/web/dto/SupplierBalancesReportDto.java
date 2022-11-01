package eulap.eb.web.dto;

import java.util.Date;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;

/**
 * A class that handles the supplier balances report data.

 */
public class SupplierBalancesReportDto {

	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private String invoiceNumber;
	private Date dueDate;
	private double amount;
	private double balance;
	
	public static SupplierBalancesReportDto getInstanceOf(APInvoice apInvoice, Supplier supplier,
			SupplierAccount supplierAccount, double balance) {

		SupplierBalancesReportDto dto = new SupplierBalancesReportDto();
		dto.supplier = supplier;
		dto.supplierAccount = supplierAccount;
		dto.invoiceNumber = apInvoice.getInvoiceNumber();
		dto.dueDate = apInvoice.getDueDate();
		dto.amount = apInvoice.getAmount();
		dto.balance = balance;
		return dto;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "SupplierBalancesReportDto [supplier=" + supplier
				+ ", supplierAccount=" + supplierAccount + ", invoiceNumber="
				+ invoiceNumber + ", dueDate=" + dueDate + ", amount=" + amount
				+ ", balance=" + balance + "]";
	}
}
