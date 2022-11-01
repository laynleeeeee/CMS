package eulap.eb.web.dto;

import java.util.Date;

/**
 * Purchase Order Register dto.
 * 

 */
public class PoRegisterDto {
	private Integer poNumber;
	private String division;
	private Date poDate;
	private Double amount;
	private Date estDeliveryDate;
	private String rrNumber;
	private String rrDate;
	private String supplierName;
	private String supplierAccountName;
	private String term;
	private String stockCode;
	private String description;
	private String uom;
	private Integer orderedQty;
	private Integer deliveredQty;
	private Integer balance;
	private String status;
	private String poStatus;
	private String cancellationRemarks;

	public static Integer ALL = -1;

	/**
	 * Get the instance of {@link PoRegisterDto}.
	 * 
	 * @param poNumber            The purchase order sequence number.
	 * @param poDate              The purchase order date.
	 * @param amount              The total amount of the PO.
	 * @param estDeliveryDate     The Estimated Delivery date of the items.
	 * @param rrNumber            The receiving report sequence number.
	 * @param rrDate              The receiving report date.
	 * @param supplierName        The supplier name.
	 * @param supplierAccountName The supplier account name.
	 * @param term                The term name.
	 * @param stockCode           The item stock code.
	 * @param description         The item description.
	 * @param uom                 The unit of measure name.
	 * @param orderedQty          The purchase order quantity.
	 * @param deliveredQty        The receiving report quantity.
	 * @param balance             The difference between orderedQty and
	 *                            deliveredQty.
	 * @param status              The status of purchase order items.
	 * @return The {@link PoRegisterDto}.
	 */
	public static PoRegisterDto getInstance(Integer poNumber, String division, Date poDate, Double amount,
			Date estDeliveryDate, String rrNumber, String rrDate, String supplierName, String supplierAccountName,
			String term, String stockCode, String description, String uom, Integer orderedQty, Integer deliveredQty,
			Integer balance, String status, String poStatus, String cancellationRemarks) {
		PoRegisterDto dto = new PoRegisterDto();
		dto.poNumber = poNumber;
		dto.division = division;
		dto.poDate = poDate;
		dto.amount = amount;
		dto.estDeliveryDate = estDeliveryDate;
		dto.rrNumber = rrNumber;
		dto.rrDate = rrDate;
		dto.supplierName = supplierName;
		dto.supplierAccountName = supplierAccountName;
		dto.term = term;
		dto.stockCode = stockCode;
		dto.description = description;
		dto.uom = uom;
		dto.orderedQty = orderedQty;
		dto.deliveredQty = deliveredQty;
		dto.balance = balance;
		dto.status = status;
		dto.poStatus = poStatus;
		dto.cancellationRemarks = cancellationRemarks;
		return dto;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Integer getDeliveredQty() {
		return deliveredQty;
	}

	public void setDeliveredQty(Integer deliveredQty) {
		this.deliveredQty = deliveredQty;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public Integer getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(Integer poNumber) {
		this.poNumber = poNumber;
	}

	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getEstDeliveryDate() {
		return estDeliveryDate;
	}

	public void setEstDeliveryDate(Date estDeliveryDate) {
		this.estDeliveryDate = estDeliveryDate;
	}

	public String getRrNumber() {
		return rrNumber;
	}

	public void setRrNumber(String rrNumber) {
		this.rrNumber = rrNumber;
	}

	public String getRrDate() {
		return rrDate;
	}

	public void setRrDate(String rrDate) {
		this.rrDate = rrDate;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierAccountName() {
		return supplierAccountName;
	}

	public void setSupplierAccountName(String supplierAccountName) {
		this.supplierAccountName = supplierAccountName;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
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

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getOrderedQty() {
		return orderedQty;
	}

	public void setOrderedQty(Integer orderedQty) {
		this.orderedQty = orderedQty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPoStatus() {
		return poStatus;
	}

	public void setPoStatus(String poStatus) {
		this.poStatus = poStatus;
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
		builder.append("PoRegisterDto [poNumber=").append(poNumber).append(", division= ").append(division)
				.append(", poDate=").append(poDate).append(", rrNumber=").append(rrNumber).append(", rrDate=")
				.append(rrDate).append(", supplierName=").append(supplierName).append(", supplierAccountName=")
				.append(supplierAccountName).append(", term=").append(term).append(", stockCode=").append(stockCode)
				.append(", description=").append(description).append(", uom=").append(uom).append(", orderedQty=")
				.append(orderedQty).append(", deliveredQty=").append(deliveredQty).append(", balance=").append(balance)
				.append(", status=").append(status).append(", poStatus=").append(poStatus)
				.append(", cancellationRemarks=").append(cancellationRemarks).append("]");
		return builder.toString();
	}

}
