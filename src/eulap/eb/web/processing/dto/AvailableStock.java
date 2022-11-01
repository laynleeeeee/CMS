package eulap.eb.web.processing.dto;

/**
 * Available stock DTO.

 *
 */
public class AvailableStock {
	private Integer itemId;
	private Double quantity;
	private Double unitCost;
	private Integer ebObjectId;
	private String source;
	private String stockCode;
	private String description;
	private String unitOfMeasurement;

	/**
	 * Create an instance of {@link AvailableStock}
	 * @param itemId The item id.
	 * @param quantity The quantity.
	 * @param unitCost The unit cost of the item.
	 * @param ebObjectId The eb object id.
	 * @param source The source of the item.
	 * @param stockCode The stock code of the item.
	 * @param description The description of the item.
	 * @return The created instance.
	 */
	public static AvailableStock getInstanceOf (Integer itemId, Double quantity, Double unitCost,
			Integer ebObjectId, String source, String stockCode, String description) {
		AvailableStock availableStock = new AvailableStock();
		availableStock.itemId = itemId;
		availableStock.quantity = quantity;
		availableStock.unitCost = unitCost;
		availableStock.ebObjectId = ebObjectId;
		availableStock.source = source;
		availableStock.stockCode = stockCode;
		availableStock.description = description;
		return availableStock;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	@Override
	public String toString() {
		return "AvailableStock [itemId=" + itemId + ", quantity=" + quantity
				+ ", unitCost=" + unitCost + ", ebObjectId=" + ebObjectId
				+ ", source=" + source + ", stockCode=" + stockCode
				+ ", description=" + description + ", unitOfMeasurement="
				+ unitOfMeasurement + "]";
	}
}
