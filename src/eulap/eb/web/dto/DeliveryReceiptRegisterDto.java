package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.Term;

/**
 * A class that handles the delivery receipt report data.

 */

public class DeliveryReceiptRegisterDto{
	private Integer id;
	private String division;
	private String soNumber;
	private String poPcrNumber;
	private double soAmount;
	private ArCustomer customer;
	private ArCustomerAccount customerAccount;
	private Term term;
	private Date drDate;
	private String drRefNumber;
	private String deliveryReceiptStatus;
	private String shipTo;
	private String drReceivedBy;
	private Date drReceivedDate;
	private String cancellationRemarks;
	private Integer drNumber;
	private String customerName;
	private String customerAcct;
	private String termName;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getSoNumber() {
		return soNumber;
	}
	public void setSoNumber(String soNumber) {
		this.soNumber = soNumber;
	}

	public String getPoPcrNumber() {
		return poPcrNumber;
	}
	public void setPoPcrNumber(String poPcrNumber) {
		this.poPcrNumber = poPcrNumber;
	}

	public double getSoAmount() {
		return soAmount;
	}
	public void setSoAmount(double soAmount) {
		this.soAmount = soAmount;
	}

	public ArCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(ArCustomer customer) {
		this.customer = customer;
	}

	public ArCustomerAccount getCustomerAccount() {
		return customerAccount;
	}
	public void setCustomerAccount(ArCustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public Term getTerm() {
		return term;
	}
	public void setTerm(Term term) {
		this.term = term;
	}

	public Date getDrDate() {
		return drDate;
	}
	public void setDrDate(Date drDate) {
		this.drDate = drDate;
	}

	public String getDrRefNumber() {
		return drRefNumber;
	}
	public void setDrRefNumber(String drRefNumber) {
		this.drRefNumber = drRefNumber;
	}

	public String getDeliveryReceiptStatus() {
		return deliveryReceiptStatus;
	}
	public void setDeliveryReceiptStatus(String deliveryReceiptStatus) {
		this.deliveryReceiptStatus = deliveryReceiptStatus;
	}

	public String getShipTo() {
		return shipTo;
	}
	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	public String getDrReceivedBy() {
		return drReceivedBy;
	}
	public void setDrReceivedBy(String drReceivedBy) {
		this.drReceivedBy = drReceivedBy;
	}

	public Date getDrReceivedDate() {
		return drReceivedDate;
	}
	public void setDrReceivedDate(Date drReceivedDate) {
		this.drReceivedDate = drReceivedDate;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}
	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public Integer getDrNumber() {
		return drNumber;
	}
	public void setDrNumber(Integer drNumber) {
		this.drNumber = drNumber;
	}

	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAcct() {
		return customerAcct;
	}
	public void setCustomerAcct(String customerAcct) {
		this.customerAcct = customerAcct;
	}

	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeliveryReceiptRegisterDto [id=").append(id).append(", division=").append(division)
		.append(", soNumber=").append(soNumber).append(", poPcrNumber=").append(poPcrNumber).append(", soAmount=").append(soAmount)
		.append(", customer=").append(customer).append(", customerAccount=").append(customerAccount).append(", term=").append(term)
		.append(", drDate=").append(drDate).append(", drRefNumber=").append(drRefNumber).append(", deliveryReceiptStatus=").append(deliveryReceiptStatus)
		.append(", shipTo=").append(shipTo).append(", drReceivedBy=").append(drReceivedBy).append(", drReceivedDate=").append(drReceivedDate)
		.append(", CancellationRemarks=").append(cancellationRemarks).append(", drNumber=").append(drNumber)
		.append(", customerAcct=").append(customerAcct).append(", customerName=").append(customerName).append(", termName=").append(termName).append("]");
		return builder.toString();
	}
}