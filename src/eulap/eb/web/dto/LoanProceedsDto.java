package eulap.eb.web.dto;


import java.util.Date;
/**
 * Loan proceeds DTO.

 */

public class LoanProceedsDto{
	private Integer loanProceedsId;
	private Date date;
	private Integer supplierId;
	private String supplierName;
	private Integer supplierAccountId;
	private String supplierAccountName;
	private Date dateCleared;
	private Integer sequenceNumber;

	public Integer getLoanProceedsId() {
		return loanProceedsId;
	}

	public void setLoanProceedsId(Integer loanProceedsId) {
		this.loanProceedsId = loanProceedsId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	public String getSupplierAccountName() {
		return supplierAccountName;
	}

	public void setSupplierAccountName(String supplierAccountName) {
		this.supplierAccountName = supplierAccountName;
	}

	public Date getDateCleared() {
		return dateCleared;
	}

	public void setDateCleared(Date dateCleared) {
		this.dateCleared = dateCleared;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoanProceedsDto [loanProceedsId=").append(loanProceedsId).append(", date=").append(date)
				.append(", supplierId=").append(supplierId).append(", supplierName=").append(supplierName)
				.append(", supplierAccountId=").append(supplierAccountId).append(", supplierAccountName=")
				.append(supplierAccountName).append(", dateCleared=").append(dateCleared).append(", sequenceNumber=")
				.append(sequenceNumber).append("]");
		return builder.toString();
	}
}
