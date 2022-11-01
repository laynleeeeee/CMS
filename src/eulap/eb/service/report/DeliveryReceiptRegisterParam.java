package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles different parameters in filtering the Delivery
 * Receipt Register report.

 */

public class DeliveryReceiptRegisterParam{
	private Integer companyId;
	private Integer divisionId;
	private Integer customerId;
	private Integer customerAcctId;
	private String soNumber;
	private String poPcrNumber;
	private Integer drNumberFrom;
	private Integer drNumberTo;
	private Date drDateFrom;
	private Date drDateTo;
	private Integer deliveryReceiptStatus;
	private Date asOfDate;


	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId (Integer companyId) {
	this.companyId = companyId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(Integer customerAcctId) {
		this.customerAcctId = customerAcctId;
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

	public Integer getDrNumberFrom() {
		return drNumberFrom;
	}
	public void setDrNumberFrom(Integer drNumberFrom) {
		this.drNumberFrom = drNumberFrom;
	}

	public Integer getDrNumberTo() {
		return drNumberTo;
	}
	public void setDrNumberTo(Integer drNumberTo) {
		this.drNumberTo = drNumberTo;
	}

	public Date getDrDateFrom() {
		return drDateFrom;
	}
	public void setDrDateFrom(Date drDateFrom) {
		this.drDateFrom = drDateFrom;
	}

	public Date getDrDateTo() {
		return drDateTo;
	}
	public void setDrDateTo(Date drDateTo) {
		this.drDateTo = drDateTo;
	}

	public Integer getDeliveryReceiptStatus() {
		return deliveryReceiptStatus;
	}
	public void setDeliveryReceiptStatus (Integer deliveryReceiptStatus) {
		this.deliveryReceiptStatus = deliveryReceiptStatus;
	}

	public Date getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate (Date asOfDate) {
		this.asOfDate = asOfDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeliveryReceiptRegisterParam [companyId=").append(companyId).append(", divisionId=").append(divisionId)
			.append(", customerId=").append(customerId).append(", soNumber=").append(soNumber).append(", poPcrNumber=").append(poPcrNumber)
			.append(", drNumberFrom=").append(drNumberFrom).append(", drNumberTo=").append(drNumberTo).append(", drDateFrom=")
			.append(drDateFrom).append(", drDateTo=").append(drDateTo).append(", deliveryReceiptStatus=").append(deliveryReceiptStatus)
			.append(", asOfDate=").append(asOfDate).append(", customerAcctId=").append(customerAcctId).append("]");
		return builder.toString();
	}
}



