package eulap.eb.web.processing.dto;

/**
 * Milling recovery report data transfer object.

 *
 */
public class MillingRecoveryReport {
	private String itemOrName;
	private String description;
	private String uom;
	private Double itemBagQuantity;
	private Double quantity;
	private Double unitCost;
	private Double amount;

	public static MillingRecoveryReport getInstanceOf (String itemOrName, String description, 
			String uom, Double itemBagQuantity, Double quantity, Double unitCost, Double amount) {
		MillingRecoveryReport mrr = new MillingRecoveryReport();
		mrr.itemOrName = itemOrName;
		mrr.description = description;
		mrr.uom = uom;
		mrr.itemBagQuantity = itemBagQuantity;
		mrr.quantity = quantity;
		mrr.unitCost = unitCost;
		mrr.amount = amount;
		return mrr;
	}

	public String getItemOrName() {
		return itemOrName;
	}

	public void setItemOrName(String itemOrName) {
		this.itemOrName = itemOrName;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getItemBagQuantity() {
		return itemBagQuantity;
	}

	public void setItemBagQuantity(Double itemBagQuantity) {
		this.itemBagQuantity = itemBagQuantity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MillingRecoveryReport [itemOrName=").append(itemOrName).append(", description=")
				.append(description).append(", uom=").append(uom).append(", itemBagQuantity=").append(itemBagQuantity)
				.append(", quantity=").append(quantity).append(", unitCost=").append(unitCost).append(", amount=")
				.append(amount).append("]");
		return builder.toString();
	}
}
