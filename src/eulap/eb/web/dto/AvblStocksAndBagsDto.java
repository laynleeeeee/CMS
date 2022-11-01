package eulap.eb.web.dto;

/**
 * Dto class for available stocks and available bags.

 *
 */
public class AvblStocksAndBagsDto {
	private String source;
	private Integer sourceObjId;
	private Double totalStocks;
	private Double totalBags;
	private Double unitCost;
	private Integer itemId;
	private String stockCode;
	private String description;
	private String unitOfMeasurement;
	private Integer ebObjectId;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getSourceObjId() {
		return sourceObjId;
	}

	public void setSourceObjId(Integer sourceObjId) {
		this.sourceObjId = sourceObjId;
	}

	public Double getTotalStocks() {
		return totalStocks;
	}

	public void setTotalStocks(Double totalStocks) {
		this.totalStocks = totalStocks;
	}

	public Double getTotalBags() {
		return totalBags;
	}

	public void setTotalBags(Double totalBags) {
		this.totalBags = totalBags;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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

	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AvblStocksAndBagsDto [source=").append(source).append(", sourceObjId=").append(sourceObjId)
				.append(", totalStocks=").append(totalStocks).append(", totalBags=").append(totalBags)
				.append(", unitCost=").append(unitCost).append(", itemId=").append(itemId).append(", stockCode=")
				.append(stockCode).append(", description=").append(description).append(", unitOfMeasurement=")
				.append(unitOfMeasurement).append(", ebObjectId=").append(ebObjectId).append("]");
		return builder.toString();
	}
}
