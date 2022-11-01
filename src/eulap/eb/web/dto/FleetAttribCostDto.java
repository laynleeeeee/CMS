package eulap.eb.web.dto;

import java.util.Date;

/**
 * 

 *
 */
public class FleetAttribCostDto {
	private Date date;
	private String refNo; // WS No. for Item
	private String description;
	private Double amount;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		builder.append("FleetAttribCostDto [date=").append(date).append(", refNo=").append(refNo)
				.append(", description=").append(description).append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
