package eulap.eb.bitpos;

/**
 * DTO for Cash Sale ar line for BITPOS.

 *
 */
public class BCashSaleArLine {
	private String name;
	private Double amount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BCashSaleArLine [name=").append(name)
				.append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
