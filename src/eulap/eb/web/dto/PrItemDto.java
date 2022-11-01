package eulap.eb.web.dto;

import java.util.List;

/**
 * Production report item dto.

 *
 */
public class PrItemDto {
	private List<PrItemRawMatDto> prRawMatDtos;
	private String stockCode;
	private String description;
	private String warehouse;
	private Double quantity;
	private String uom;

	public List<PrItemRawMatDto> getPrRawMatDtos() {
		return prRawMatDtos;
	}

	public void setPrRawMatDtos(List<PrItemRawMatDto> prRawMatDtos) {
		this.prRawMatDtos = prRawMatDtos;
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

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
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
		builder.append("PrItemDto [prRawMatDtos=").append(prRawMatDtos).append(", stockCode=").append(stockCode)
				.append(", description=").append(description).append(", warehouse=").append(warehouse)
				.append(", quantity=").append(quantity).append(", uom=").append(uom).append("]");
		return builder.toString();
	}
}
