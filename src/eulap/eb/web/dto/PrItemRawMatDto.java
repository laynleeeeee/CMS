package eulap.eb.web.dto;

/**
 * Production report raw item dto.

 *
 */
public class PrItemRawMatDto {
	private String stockCode;
	private String description;
	private Double quantity;
	private String uom;

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

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
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
		builder.append("PrItemRawMatDto [stockCode=").append(stockCode).append(", description=").append(description)
				.append(", quantity=").append(quantity).append(", uom=").append(uom).append("]");
		return builder.toString();
	}
}
