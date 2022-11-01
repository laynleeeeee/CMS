
package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the transaction report data.

 */

public class BmsTrackerReportDto {
	private String division;
	private String bmsNo;
	private Double bmsAmount;
	private Integer poNo;
	private Date poDate;
	private Double poAmount;
	private Date advPaymentDate;
	private Double poAdvPayment;
	private String advPaymentRequestor;
	private String poStatus;
	private Integer paymentVoucherNo;
	private Date invoiceDate;
	private String refNo;
	private Double expenseAmount;
	private Integer checkVoucherNo;
	private String checkNo;
	private Double checkAmount;
	private String checkStatus;
	private Integer statusId;
	private Integer typeId;

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getBmsNo() {
		return bmsNo;
	}

	public void setBmsNo(String bmsNo) {
		this.bmsNo = bmsNo;
	}

	public Double getBmsAmount() {
		return bmsAmount;
	}

	public void setBmsAmount(Double bmsAmount) {
		this.bmsAmount = bmsAmount;
	}

	public Integer getPoNo() {
		return poNo;
	}

	public void setPoNo(Integer poNo) {
		this.poNo = poNo;
	}

	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	public Double getPoAmount() {
		return poAmount;
	}

	public void setPoAmount(Double poAmount) {
		this.poAmount = poAmount;
	}

	public Date getAdvPaymentDate() {
		return advPaymentDate;
	}

	public void setAdvPaymentDate(Date advPaymentDate) {
		this.advPaymentDate = advPaymentDate;
	}

	public Double getPoAdvPayment() {
		return poAdvPayment;
	}

	public void setPoAdvPayment(Double poAdvPayment) {
		this.poAdvPayment = poAdvPayment;
	}

	public String getAdvPaymentRequestor() {
		return advPaymentRequestor;
	}

	public void setAdvPaymentRequestor(String advPaymentRequestor) {
		this.advPaymentRequestor = advPaymentRequestor;
	}

	public String getPoStatus() {
		return poStatus;
	}

	public void setPoStatus(String poStatus) {
		this.poStatus = poStatus;
	}

	public Integer getPaymentVoucherNo() {
		return paymentVoucherNo;
	}

	public void setPaymentVoucherNo(Integer paymentVoucherNo) {
		this.paymentVoucherNo = paymentVoucherNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Double getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(Double expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

	public Integer getCheckVoucherNo() {
		return checkVoucherNo;
	}

	public void setCheckVoucherNo(Integer checkVoucherNo) {
		this.checkVoucherNo = checkVoucherNo;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public Double getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(Double checkAmount) {
		this.checkAmount = checkAmount;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BmsTrackerReportDto [division=").append(division).append(", bmsNo=").append(bmsNo)
				.append(", bmsAmount=").append(bmsAmount).append(", poNo=").append(poNo).append(", poDate=")
				.append(poDate).append(", poAmount=").append(poAmount).append(", advPaymentDate=")
				.append(advPaymentDate).append(", poAdvPayment=").append(poAdvPayment).append(", advPaymentRequestor=")
				.append(advPaymentRequestor).append(", poStatus=").append(poStatus).append(", paymentVoucherNo=")
				.append(paymentVoucherNo).append(", invoiceDate=").append(invoiceDate).append(", refNo=").append(refNo)
				.append(", expenseAmount=").append(expenseAmount).append(", checkVoucherNo=").append(checkVoucherNo)
				.append(", checkNo=").append(checkNo).append(", checkAmount=").append(checkAmount)
				.append(", checkStatus=").append(checkStatus).append(", statusId=").append(statusId).append("]");
		return builder.toString();
	}

}
