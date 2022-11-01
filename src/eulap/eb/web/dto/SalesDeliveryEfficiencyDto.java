package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the Sales Delivery Efficiency report data.

 * 
 */
public class SalesDeliveryEfficiencyDto {
	private Integer divisionId;
	private String divisionName;
	private Integer customerId;
	private String customerName;
	private String stockCode;
	private String description;
	private String poNumber;
	private String refNumber;
	private Double quantity;
	private String deliveryStatus;
	private Date deliveryDate;
	private Date dateReceived;
	private int month;
	private int year;
	private Integer deliveryReceiptId;

	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}
	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getDateReceived() {
		return dateReceived;
	}
	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public Integer getDeliveryReceiptId() {
		return deliveryReceiptId;
	}
	public void setDeliveryReceiptId(Integer deliveryReceiptId) {
		this.deliveryReceiptId = deliveryReceiptId;
	}

	@Override
	public String toString() {
		return "SalesDeliveryEfficiencyDto [divisionId=" + divisionId + ", divisionName=" + divisionName
				+ ", customerId=" + customerId + ", customerName=" + customerName + ", stockCode=" + stockCode
				+ ", description=" + description + ", poNumber=" + poNumber + ", refNumber=" + refNumber + ", quantity="
				+ quantity + ", deliveryStatus=" + deliveryStatus + ", deliveryDate=" + deliveryDate + ", dateReceived="
				+ dateReceived + ", month=" + month + ", year=" + year + ", deliveryReceiptId=" + deliveryReceiptId
				+ "]";
	}
}
