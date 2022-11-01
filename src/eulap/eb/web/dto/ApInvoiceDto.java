package eulap.eb.web.dto;

import java.util.Date;

/**
 * Account Payables Invoice DTO.

 */
public class ApInvoiceDto {
	private Integer invoiceId;
	private Double amount;
	private String invoiceNumber;
	private String particular;
	private String accountNo;
	private String accountName;
	private Double debit;
	private Double credit;
	private Integer invoiceTypeId;
	private Date date;
	private Integer referenceNumber;
	private String bmsNumber;
	private String supplierName;
	private String supplierAcctName;
	private Integer accountId;

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getParticular() {
		return particular;
	}

	public void setParticular(String particular) {
		this.particular = particular;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Integer getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(Integer invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}

	public static ApInvoiceDto getInstanceOf(Integer invoiceId, Double debit, Double credit, String invoiceNumber, 
		String accountNo, String accountName) {
		ApInvoiceDto ap = new ApInvoiceDto();
		ap.invoiceId = invoiceId;
		ap.debit = debit;
		ap.credit = credit;
		ap.invoiceNumber = invoiceNumber;
		ap.accountNo = accountNo;
		ap.accountName = accountName;
		return ap;
	}
	
	public static ApInvoiceDto getInstanceOf(Double debit, Double credit, String invoiceNumber, String particular,
		String accountNo, String accountName) {
		ApInvoiceDto ap = new ApInvoiceDto();
		ap.debit = debit;
		ap.credit = credit;
		ap.invoiceNumber = invoiceNumber;
		ap.particular = particular;
		ap.accountNo = accountNo;
		ap.accountName = accountName;
		return ap;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierAcctName() {
		return supplierAcctName;
	}

	public void setSupplierAcctName(String supplierAcctName) {
		this.supplierAcctName = supplierAcctName;
	}

	public Integer getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(Integer referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApInvoiceDto [invoiceId=").append(invoiceId).append(", amount=").append(amount)
				.append(", invoiceNumber=").append(invoiceNumber).append(", particular=").append(particular)
				.append(", accountNo=").append(accountNo).append(", accountName=").append(accountName)
				.append(", debit=").append(debit).append(", credit=").append(credit).append(", invoiceTypeId=")
				.append(invoiceTypeId).append(", date=").append(date).append(", bmsNumber=").append(bmsNumber)
				.append(", supplierName=").append(supplierName).append(", supplierAcctName=").append(supplierAcctName)
				.append(", referenceNumber=").append(referenceNumber).append("]");
		return builder.toString();
	}
}
