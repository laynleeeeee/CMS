package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;

/**
 * A class that handles the invoice register report data.

 * 
 */
public class InvoiceRegisterDto {
	private InvoiceType invoiceType;
	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private Term term;
	private String invoiceNumber;
	private Date invoiceDate;
	private Date gLDate;
	private Date dueDate;
	private double amount;
	private double balance;
	private String formStatus;
	private String sequenceNumber;
	private String division;
	private String cancellationRemarks;
	private String bmsNumber;


	public static InvoiceRegisterDto getInstanceOf (Company company, String division, APInvoice apInvoice, InvoiceType invoiceType,
			Supplier supplier, SupplierAccount supplierAcct, Term term, String formStatus,
			double balance, String cancellationRemarks) {
		InvoiceRegisterDto dto = new InvoiceRegisterDto();
		dto.division = division;
		dto.bmsNumber = apInvoice.getBmsNumber();
		dto.invoiceNumber = apInvoice.getInvoiceNumber();
		dto.invoiceDate = apInvoice.getInvoiceDate();
		dto.gLDate = apInvoice.getGlDate();
		dto.dueDate = apInvoice.getDueDate();
		dto.amount = apInvoice.getAmount();
		dto.sequenceNumber = (company.getCompanyCode() != null ? company.getCompanyCode() + " " : "")  + apInvoice.getSequenceNumber();
		dto.invoiceType = invoiceType;
		dto.supplier = supplier;
		dto.supplierAccount = supplierAcct;
		dto.term = term;
		dto.formStatus = formStatus;
		dto.balance = balance;
		dto.cancellationRemarks = cancellationRemarks;
		return dto;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
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

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getGLDate() {
		return gLDate;
	}

	public void setGLDate(Date gLDate) {
		this.gLDate = gLDate;
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

	public String getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(String formStatus) {
		this.formStatus = formStatus;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceRegisterDto [invoiceType=").append(invoiceType).append(", supplier=").append(supplier)
				.append(", supplierAccount=").append(supplierAccount).append(", term=").append(term)
				.append(", invoiceNumber=").append(invoiceNumber).append(", invoiceDate=").append(invoiceDate)
				.append(", gLDate=").append(gLDate).append(", dueDate=").append(dueDate).append(", amount=")
				.append(amount).append(", balance=").append(balance).append(", formStatus=").append(formStatus)
				.append(", sequenceNumber=").append(sequenceNumber).append(", division=").append(division)
				.append(", cancellationRemarks=").append(cancellationRemarks)
				.append(", bmsNumber=").append(bmsNumber).append("]");
		return builder.toString();
	}
}
