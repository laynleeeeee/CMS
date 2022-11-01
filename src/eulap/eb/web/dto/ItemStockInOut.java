package eulap.eb.web.dto;

/**
 * Container class that will hold the values for Items per warehouse.

 * 
 */
public class ItemStockInOut {
	private String stockCode;
	private String description;
	private double quantity;
	private String uom;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemStockInOut [stockCode=").append(stockCode)
				.append(", description=").append(description)
				.append(", quantity=").append(quantity).append(", uom=")
				.append(uom).append("]");
		return builder.toString();
	}
}
