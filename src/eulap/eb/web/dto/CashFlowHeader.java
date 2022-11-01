package eulap.eb.web.dto;

/**
 * Monthly cash flow header.

 *
 */
public class CashFlowHeader {
	private String month;

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMonth() {
		return month;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CashFlowHeader [month=").append(month).append("]");
		return builder.toString();
	}
}