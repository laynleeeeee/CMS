package eulap.eb.web.dto;

import java.util.Date;

/**
 * Class that will contain the data for Stock Adjustment Register.
 * 

 * 
 */
public class StockAdjustmentDto {
	private int formId;
	private String division;
	private Date date;
	private String number;
	private String stockCode;
	private String description;
	private String bmsNumber;
	private String remarks;
	private String uom;
	private Double quantity;
	private Double itemBagQuantity;
	private Double unitCost;
	private Double amount;
	private String cancellationRemarks;
	private String formStatus;

	private StockAdjustmentDto(int formId, String division, Date date, String number, String stockCode,
			String description, String bmsNumber, String remarks, String uom, Double quantity, Double itemBagQuantity,
			Double unitCost, Double amount, String cancellationRemakrs, String formStatus) {
		this.formId = formId;
		this.division = division;
		this.date = date;
		this.number = number;
		this.stockCode = stockCode;
		this.description = description;
		this.bmsNumber = bmsNumber;
		this.remarks = remarks;
		this.uom = uom;
		this.quantity = quantity;
		this.itemBagQuantity = itemBagQuantity;
		this.unitCost = unitCost;
		this.amount = amount;
		this.cancellationRemarks = cancellationRemakrs;
		this.formStatus = formStatus;
	}

	/**
	 * Get the instance of {@link StockAdjustmentDto}
	 */
	public static StockAdjustmentDto getInstanceOf(int formId, String division, Date date, String number,
			String stockCode, String description, String bmsNumber, String remarks, String uom, Double quantity,
			Double itemBagQuantity, Double unitCost, Double amount, String cancellationRemarks, String formStatus) {
		return new StockAdjustmentDto(formId, division, date, number, stockCode, description, bmsNumber, remarks, uom,
				quantity, itemBagQuantity, unitCost, amount, cancellationRemarks, formStatus);
	}

	/**
	 * Get the instance of {@link StockAdjustmentDto}
	 */
	public static StockAdjustmentDto getInstanceOf(int formId, String division, Date date, String number,
			String stockCode, String description, String bmsNumber, String remarks, String uom, Double quantity,
			Double itemBagQuantity, Double unitCost, Double amount) {
		return getInstanceOf(formId, division, date, number, stockCode, description, bmsNumber, remarks, uom,
				quantity, itemBagQuantity, unitCost, amount, null, null);
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getItemBagQuantity() {
		return itemBagQuantity;
	}

	public void setItemBagQuantity(Double itemBagQuantity) {
		this.itemBagQuantity = itemBagQuantity;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public String getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(String formStatus) {
		this.formStatus = formStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockAdjustmentDto [formId=").append(formId).append(", division=").append(division)
				.append(", date=").append(date).append(", number=").append(number).append(", stockCode=")
				.append(stockCode).append(", description=").append(description).append(", bmsNumber=").append(bmsNumber)
				.append(", remarks=").append(remarks).append(", uom=").append(uom).append(", quantity=")
				.append(quantity).append(", itemBagQuantity=").append(itemBagQuantity).append(", unitCost=")
				.append(unitCost).append(", amount=").append(amount).append(", cancellationRemarks=")
				.append(cancellationRemarks).append(", formStatus=").append(formStatus).append("]");
		return builder.toString();
	}
}
