package eulap.eb.web.dto;


import java.util.Date;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidation;
/**
 * Petty Cash Voucher Liquidation Register.

 */

public class PettyCashVoucherLiquidationRegisterDTO {
	private String companyId;
	private String divisionName;
	private String custodianName;
	private String requestorName;
	private Date pcvDate;
	private String reference;
	private double amountRequested;
	private double totalCashReturned;
	private String status;
	private String cancellationRemarks;
	private Integer transactionStatus;
	private Integer sequenceNumber;
	private String formattedSequenceNo;
	private Integer pcvlNo;
	private Integer pcvNo;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getCustodianName() {
		return custodianName;
	}

	public void setCustodianName(String custodianName) {
		this.custodianName = custodianName;
	}

	public String getRequestorName() {
		return requestorName;
	}

	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public double getAmountRequested() {
		return amountRequested;
	}

	public void setAmountRequested(double amountRequested) {
		this.amountRequested = amountRequested;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getTotalCashReturned() {
		return totalCashReturned;
	}
	public void setTotalCashReturned(double totalCashReturned) {
		this.totalCashReturned = totalCashReturned;
	}

	 public Integer getSequenceNumber() {
		 return sequenceNumber;
	}

	 public void setSequenceNumber(Integer sequenceNumber) {
		 this.sequenceNumber = sequenceNumber;
	 }

	public String getFormattedSequenceNo() {
		return formattedSequenceNo;
	}

	public void setFormattedSequenceNo(String formattedSequenceNo) {
		this.formattedSequenceNo = formattedSequenceNo;
	}

	public Integer getPcvlNo() {
		return pcvlNo;
	}

	public void setPcvlNo(Integer pcvlNo) {
		this.pcvlNo = pcvlNo;
	}

	public Integer getPcvNo() {
		return pcvNo;
	}

	public void setPcvNo(Integer pcvNo) {
		this.pcvNo = pcvNo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PettyCashVoucherLiquidationRegisterDTO [companyId=").append(companyId).append(", divisionName=")
				.append(divisionName).append(", custodianName=").append(custodianName)
				.append(", requestorName=").append(requestorName).append(", pcvDate=").append(pcvDate)
				.append(", reference=").append(reference).append(", amountRequested=").append(amountRequested).append(", setTotalCashReturned=")
				.append(totalCashReturned).append(", status=").append(status).append(", cancellationRemarks=").append(cancellationRemarks)
				.append(", sequenceNumber=").append(sequenceNumber).append(", transactionStatus=").append(transactionStatus)
				.append(", formattedSequenceNo=").append(formattedSequenceNo).append("]");
		return builder.toString();
	}

	public Integer getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Integer transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public Date getPcvDate() {
		return pcvDate;
	}

	public void setPcvDate(Date pcvDate) {
		this.pcvDate = pcvDate;
	}


}
