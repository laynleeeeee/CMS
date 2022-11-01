package eulap.eb.web.dto;

/**
 * Container class for the attributes needed for the Reclass Register.

 *
 */
public class ReclassRegisterDto {
	private Integer fromItemId;
	private Integer toItemId;
	private String division;
	private String fromStockCode;
	private String fscDescription;
	private Double reclassedQty;
	private String fscUom;
	private Double repackedUnitCost;
	private String toStockCode;
	private String tscDescription;
	private Double receivedQty;
	private String tscUom;
	private Double unitCost;
	private String cancellationRemarks;
	private Double amount;
	private Integer warehouseId;
	private Double fromExistingStocks;
	private Double toExistingStocks;
	private Integer rpNumber;
	private String formStatus;

	public Integer getFromItemId() {
		return fromItemId;
	}

	public void setFromItemId(Integer fromItemId) {
		this.fromItemId = fromItemId;
	}

	public Integer getToItemId() {
		return toItemId;
	}

	public void setToItemId(Integer toItemId) {
		this.toItemId = toItemId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getFromStockCode() {
		return fromStockCode;
	}

	public void setFromStockCode(String fromStockCode) {
		this.fromStockCode = fromStockCode;
	}

	public String getFscDescription() {
		return fscDescription;
	}

	public void setFscDescription(String fscDescription) {
		this.fscDescription = fscDescription;
	}

	public Double getReclassedQty() {
		return reclassedQty;
	}

	public void setReclassedQty(Double reclassedQty) {
		this.reclassedQty = reclassedQty;
	}

	public String getFscUom() {
		return fscUom;
	}

	public void setFscUom(String fscUom) {
		this.fscUom = fscUom;
	}

	public Double getRepackedUnitCost() {
		return repackedUnitCost;
	}

	public void setRepackedUnitCost(Double repackedUnitCost) {
		this.repackedUnitCost = repackedUnitCost;
	}

	public String getToStockCode() {
		return toStockCode;
	}

	public void setToStockCode(String toStockCode) {
		this.toStockCode = toStockCode;
	}

	public String getTscDescription() {
		return tscDescription;
	}

	public void setTscDescription(String tscDescription) {
		this.tscDescription = tscDescription;
	}

	public Double getReceivedQty() {
		return receivedQty;
	}

	public void setReceivedQty(Double receivedQty) {
		this.receivedQty = receivedQty;
	}

	public String getTscUom() {
		return tscUom;
	}

	public void setTscUom(String tscUom) {
		this.tscUom = tscUom;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getFromExistingStocks() {
		return fromExistingStocks;
	}

	public void setFromExistingStocks(Double fromExistingStocks) {
		this.fromExistingStocks = fromExistingStocks;
	}

	public Double getToExistingStocks() {
		return toExistingStocks;
	}

	public void setToExistingStocks(Double toExistingStocks) {
		this.toExistingStocks = toExistingStocks;
	}

	public Integer getRpNumber() {
		return rpNumber;
	}

	public void setRpNumber(Integer rpNumber) {
		this.rpNumber = rpNumber;
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
		builder.append("ReclassRegisterDto [fromItemId=").append(fromItemId).append(", toItemId=").append(toItemId)
				.append(", division=").append(division).append(", fromStockCode=").append(fromStockCode)
				.append(", fscDescription=").append(fscDescription).append(", reclassedQty=").append(reclassedQty)
				.append(", fscUom=").append(fscUom).append(", repackedUnitCost=").append(repackedUnitCost)
				.append(", toStockCode=").append(toStockCode).append(", tscDescription=").append(tscDescription)
				.append(", receivedQty=").append(receivedQty).append(", tscUom=").append(tscUom).append(", unitCost=")
				.append(unitCost).append(", cancellationRemarks=").append(cancellationRemarks).append(", amount=")
				.append(amount).append(", warehouseId=").append(warehouseId).append(", fromExistingStocks=")
				.append(fromExistingStocks).append(", toExistingStocks=").append(toExistingStocks).append(", rpNumber=")
				.append(rpNumber).append("]");
		return builder.toString();
	}
}
