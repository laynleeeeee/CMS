package eulap.eb.web.dto;

/**
 * Data transfer object class for purhcase order item

 */

public class PurchaseOrderItemDto {
	private double quantity;
	private String uomName;
	private String stockCodeDesc;
	private double unitCost;
	private double amount;
	private boolean notRemarks;

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public String getStockCodeDesc() {
		return stockCodeDesc;
	}

	public void setStockCodeDesc(String stockCodeDesc) {
		this.stockCodeDesc = stockCodeDesc;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public boolean isNotRemarks() {
		return notRemarks;
	}

	public void setNotRemarks(boolean notRemarks) {
		this.notRemarks = notRemarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderItemDto [quantity=").append(quantity).append(", uomName=").append(uomName)
				.append(", stockCodeDesc=").append(stockCodeDesc).append(", unitCost=").append(unitCost)
				.append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
