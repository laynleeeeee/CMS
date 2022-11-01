package eulap.eb.web.dto;

import java.util.Date;

/**
 * The retention cost dto.

 */
public class RetentionCostDto {
	private Integer soId;
	private String division;
	private String customerAcct;
	private String customer;
	private Integer soNumber;
	private String poNumber;
	private Date deliveryDate;
	private String ariNumber;
	private Double ariRetention;
	private String arReceiptNo;
	private Double arReceiptRetention;
	private Double totalRetention;
	private Double totalCollected;
	private Double balance;
	private Date dueDate;

	public Integer getSoId() {
		return soId;
	}

	public void setSoId(Integer soId) {
		this.soId = soId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getCustomerAcct() {
		return customerAcct;
	}

	public void setCustomerAcct(String customerAcct) {
		this.customerAcct = customerAcct;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Integer getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(Integer soNumber) {
		this.soNumber = soNumber;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getAriNumber() {
		return ariNumber;
	}

	public void setAriNumber(String ariNumber) {
		this.ariNumber = ariNumber;
	}

	public Double getAriRetention() {
		return ariRetention;
	}

	public void setAriRetention(Double ariRetention) {
		this.ariRetention = ariRetention;
	}

	public String getArReceiptNo() {
		return arReceiptNo;
	}

	public void setArReceiptNo(String arReceiptNo) {
		this.arReceiptNo = arReceiptNo;
	}

	public Double getArReceiptRetention() {
		return arReceiptRetention;
	}

	public void setArReceiptRetention(Double arReceiptRetention) {
		this.arReceiptRetention = arReceiptRetention;
	}

	public Double getTotalRetention() {
		return totalRetention;
	}

	public void setTotalRetention(Double totalRetention) {
		this.totalRetention = totalRetention;
	}

	public Double getTotalCollected() {
		return totalCollected;
	}

	public void setTotalCollected(Double totalCollected) {
		this.totalCollected = totalCollected;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RetentionCostDto [soId=").append(soId).append(", division=").append(division)
				.append(", customerAcct=").append(customerAcct).append(", customer=").append(customer)
				.append(", soNumber=").append(soNumber).append(", poNumber=").append(poNumber).append(", deliveryDate=")
				.append(deliveryDate).append(", ariNumber=").append(ariNumber).append(", ariRetention=")
				.append(ariRetention).append(", arReceiptNo=").append(arReceiptNo).append(", arReceiptRetention=")
				.append(arReceiptRetention).append(", totalRetention=").append(totalRetention)
				.append(", totalCollected=").append(totalCollected).append(", balance=").append(balance)
				.append(", dueDate=").append(dueDate).append("]");
		return builder.toString();
	}
}
