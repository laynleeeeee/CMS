package eulap.eb.web.dto;

import java.util.Date;

/**
 * The sales po monitoring report DTO.

 */
public class SalesPoMonitoringDto {
	private Integer soId;
	private Integer lineId;
	private String month;
	private String requestor;
	private String poNumber;
	private Date SoDate;
	private Date estimatedDelivery;
	private String stockCode;
	private String description;
	private Double qty;
	private String uom;
	private Double unitPrice;
	private Double soAmount;
	private String soStatus;
	private Double balance;
	private Date drDate;
	private String drRef;
	private Double drQuantity;
	private Date ariDate;
	private String strAriDate;
	private Integer ariNumber;
	private String strAriNumber;
	private Double ariQty;
	private Double ariAmount;
	private Integer salesPersonnelId;
	private Integer customerId;
	private String customerName;
	private Integer deliveryReceiptId;

	public Integer getSoId() {
		return soId;
	}

	public void setSoId(Integer soId) {
		this.soId = soId;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public Date getSoDate() {
		return SoDate;
	}

	public void setSoDate(Date soDate) {
		SoDate = soDate;
	}

	public Date getEstimatedDelivery() {
		return estimatedDelivery;
	}

	public void setEstimatedDelivery(Date estimatedDelivery) {
		this.estimatedDelivery = estimatedDelivery;
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

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getSoAmount() {
		return soAmount;
	}

	public void setSoAmount(Double soAmount) {
		this.soAmount = soAmount;
	}

	public String getSoStatus() {
		return soStatus;
	}

	public void setSoStatus(String soStatus) {
		this.soStatus = soStatus;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Date getDrDate() {
		return drDate;
	}

	public void setDrDate(Date drDate) {
		this.drDate = drDate;
	}

	public String getDrRef() {
		return drRef;
	}

	public void setDrRef(String drRef) {
		this.drRef = drRef;
	}

	public Double getDrQuantity() {
		return drQuantity;
	}

	public void setDrQuantity(Double drQuantity) {
		this.drQuantity = drQuantity;
	}

	public Date getAriDate() {
		return ariDate;
	}

	public void setAriDate(Date ariDate) {
		this.ariDate = ariDate;
	}

	public Integer getAriNumber() {
		return ariNumber;
	}

	public void setAriNumber(Integer ariNumber) {
		this.ariNumber = ariNumber;
	}

	public Double getAriQty() {
		return ariQty;
	}

	public void setAriQty(Double ariQty) {
		this.ariQty = ariQty;
	}

	public Double getAriAmount() {
		return ariAmount;
	}

	public void setAriAmount(Double ariAmount) {
		this.ariAmount = ariAmount;
	}

	public Integer getSalesPersonnelId() {
		return salesPersonnelId;
	}

	public void setSalesPersonnelId(Integer salesPersonnelId) {
		this.salesPersonnelId = salesPersonnelId;
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

	public String getStrAriDate() {
		return strAriDate;
	}

	public void setStrAriDate(String strAriDate) {
		this.strAriDate = strAriDate;
	}

	public String getStrAriNumber() {
		return strAriNumber;
	}

	public void setStrAriNumber(String strAriNumber) {
		this.strAriNumber = strAriNumber;
	}

	public Integer getDeliveryReceiptId() {
		return deliveryReceiptId;
	}

	public void setDeliveryReceiptId(Integer deliveryReceiptId) {
		this.deliveryReceiptId = deliveryReceiptId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesPoMonitorintDto [month=").append(month).append(", requestor=").append(requestor)
				.append(", poNumber=").append(poNumber).append(", SoDate=").append(SoDate)
				.append(", estimatedDelivery=").append(estimatedDelivery).append(", stockCode=").append(stockCode)
				.append(", description=").append(description).append(", qty=").append(qty).append(", uom=").append(uom)
				.append(", unitPrice=").append(unitPrice).append(", soAmount=").append(soAmount).append(", soStatus=")
				.append(soStatus).append(", balance=").append(balance).append(", drDate=").append(drDate)
				.append(", drRef=").append(drRef).append(", ariDate=").append(ariDate).append(", ariNumber=")
				.append(ariNumber).append(", ariQty=").append(ariQty).append(", ariAmount=").append(ariAmount)
				.append("]");
		return builder.toString();
	}
}
