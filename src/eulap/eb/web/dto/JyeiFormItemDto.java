package eulap.eb.web.dto;

/**
 * Data transfer object class for withdrawal slip serialized and non serialized item.

 */
public class JyeiFormItemDto {
	private String stockCode;
	private String description;
	private String serialNumber;
	private double quantity;
	private String uom;
	private double unitCost;
	private double amount;

	public static final JyeiFormItemDto getInstanceOf(String stockCode, String description,
			String serialNumber, double quantity, String uom, double unitCost, double amount) {
		JyeiFormItemDto wsItemDto = new JyeiFormItemDto();
		wsItemDto.stockCode = stockCode;
		wsItemDto.description = description;
		wsItemDto.serialNumber = serialNumber;
		wsItemDto.quantity = quantity;
		wsItemDto.uom = uom;
		wsItemDto.unitCost = unitCost;
		wsItemDto.amount = amount;
		return wsItemDto;
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JyeiFormItemDto [stockCode=").append(stockCode).append(", description=").append(description)
				.append(", serialNumber=").append(serialNumber).append(", quantity=").append(quantity).append(", uom=")
				.append(uom).append(", unitCost=").append(unitCost).append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
