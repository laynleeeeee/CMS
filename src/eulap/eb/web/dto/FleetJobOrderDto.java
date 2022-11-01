package eulap.eb.web.dto;

/**
 * 

 *
 */
public class FleetJobOrderDto extends FleetAttribCostDto{
	private String accountName;
	private String remarks;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetJobOrderDto [accountName=").append(accountName).append(", remarks=").append(remarks)
				.append("]");
		return builder.toString();
	}
}
