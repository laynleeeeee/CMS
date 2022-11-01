package eulap.eb.web.dto;

import java.util.Date;
/**
 * A class that handles the transaction report data.

 */
public class SalesOrderRegisterDto {
	private Integer id;
	private String division;
	private Date date;
	private Date deliveryDate;
	private String soType;
	private String poNumber;
	private String termId;
	private String remarks;
	private Double amount;
	private Integer sequenceNumber;
	private String term;
	private String customerName;
	private String customerAcctName;
	private String cancellationRemarks;
	private String status;
	private Integer statusId;

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getSoType() {
		return soType;
	}

	public void setSoType(String soType) {
		this.soType = soType;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAcctName() {
		return customerAcctName;
	}

	public void setCustomerAcctName(String customerAcctName) {
		this.customerAcctName = customerAcctName;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesOrderRegisterDto [id=").append(id).append(", division=").append(division).append(", date=")
				.append(date).append(", deliveryDate=").append(deliveryDate).append(", soType=").append(soType)
				.append(", poNumber=").append(poNumber).append(", termId=").append(termId).append(", remarks=")
				.append(remarks).append(", amount=").append(amount).append(", sequenceNumber=").append(sequenceNumber)
				.append(", term=").append(term).append(", customerName=").append(customerName)
				.append(", customerAcctName=").append(customerAcctName).append(", cancellationRemarks=")
				.append(cancellationRemarks).append(", status=").append(status).append(", statusId=").append(statusId)
				.append(", getDivision()=").append(getDivision()).append(", getDate()=").append(getDate())
				.append(", getDeliveryDate()=").append(getDeliveryDate()).append(", getSoType()=").append(getSoType())
				.append(", getPoNumber()=").append(getPoNumber()).append(", getTermId()=").append(getTermId())
				.append(", getRemarks()=").append(getRemarks()).append(", getAmount()=").append(getAmount())
				.append(", getSequenceNumber()=").append(getSequenceNumber()).append(", getTerm()=").append(getTerm())
				.append(", getCustomerName()=").append(getCustomerName()).append(", getCustomerAcctName()=")
				.append(getCustomerAcctName()).append(", getCancellationRemarks()=").append(getCancellationRemarks())
				.append(", getStatus()=").append(getStatus()).append(", getId()=").append(getId())
				.append(", getStatusId()=").append(getStatusId()).append(", getClass()=").append(getClass())
				.append(", hashCode()=").append(hashCode()).append(", toString()=").append(super.toString())
				.append("]");
		return builder.toString();
	}

}
