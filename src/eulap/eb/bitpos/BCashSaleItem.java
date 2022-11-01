package eulap.eb.bitpos;

import java.io.Serializable;


/**
 * Container class that will hold the values for Cash Sales Items.

 *
 */
public class BCashSaleItem implements Serializable{

	private static final long serialVersionUID = 1L;

	private String stockCode;
	private Double quantity;
	private Double srp;
	private Double amount;
	private Double discount;
	private Double addOn;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getSrp() {
		return srp;
	}

	public void setSrp(Double srp) {
		this.srp = srp;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getAddOn() {
		return addOn;
	}

	public void setAddOn(Double addOn) {
		this.addOn = addOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BCashSaleItem [stockCode=").append(stockCode)
				.append(", quantity=").append(quantity).append(", srp=")
				.append(srp).append(", amount=").append(amount)
				.append(", discount=").append(discount).append(", addOn=")
				.append(addOn).append("]");
		return builder.toString();
	}

}
