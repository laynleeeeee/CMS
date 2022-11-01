package eulap.eb.web.dto;


import java.util.Date;

/**
 * A class that handles the payment report data.

 */
public class PaymentRegisterDto {
	private String division;
	private Date paymentDate;
	private String bankAcct;
	private String checkNo;
	private Date checkDate;
	private String supplierName;
	private String supplierAcct;
	private Double amount;
	private String voucherNo;
	private String formStatus;
	private Integer formStatusId;
	private Date date;
	private String cancellationRemarks;

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getBankAcct() {
		return bankAcct;
	}

	public void setBankAcct(String bankAcct) {
		this.bankAcct = bankAcct;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierAcct() {
		return supplierAcct;
	}

	public void setSupplierAcct(String supplierAcct) {
		this.supplierAcct = supplierAcct;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(String formStatus) {
		this.formStatus = formStatus;
	}

	public Integer getFormStatusId() {
		return formStatusId;
	}

	public void setFormStatusId(Integer formStatusId) {
		this.formStatusId = formStatusId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaymentRegisterDto [division=");
		builder.append(division);
		builder.append(", paymentDate=");
		builder.append(paymentDate);
		builder.append(", bankAcct=");
		builder.append(bankAcct);
		builder.append(", checkNo=");
		builder.append(checkNo);
		builder.append(", checkDate=");
		builder.append(checkDate);
		builder.append(", supplierName=");
		builder.append(supplierName);
		builder.append(", supplierAcct=");
		builder.append(supplierAcct);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", voucherNo=");
		builder.append(voucherNo);
		builder.append(", formStatus=");
		builder.append(formStatus);
		builder.append(", formStatusId=");
		builder.append(formStatusId);
		builder.append(", date=");
		builder.append(date);
		builder.append(", cancellationRemarks=");
		builder.append(cancellationRemarks);
		builder.append("]");
		return builder.toString();
	}

}
