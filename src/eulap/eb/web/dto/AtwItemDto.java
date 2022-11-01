package eulap.eb.web.dto;

/**
 * Data transfer object for ATW item to DR item conversion

 */

public class AtwItemDto {
	private Integer atwItemId;
	private Integer itemId;
	private double srp;
	private double discount;
	private Integer taxTypeId;
	private double vatAmount;
	private Integer itemDiscountTypeId;
	private double discountValue;

	public Integer getAtwItemId() {
		return atwItemId;
	}

	public void setAtwItemId(Integer atwItemId) {
		this.atwItemId = atwItemId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public double getSrp() {
		return srp;
	}

	public void setSrp(double srp) {
		this.srp = srp;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	public double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}

	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
	}

	public double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(double discountValue) {
		this.discountValue = discountValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AtwItemDto [atwItemId=").append(atwItemId).append(", itemId=").append(itemId).append(", srp=")
				.append(srp).append(", discount=").append(discount).append(", taxTypeId=").append(taxTypeId)
				.append(", vatAmount=").append(vatAmount).append(", itemDiscountTypeId=").append(itemDiscountTypeId)
				.append(", discountValue=").append(discountValue).append("]");
		return builder.toString();
	}
}
