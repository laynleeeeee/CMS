package eulap.eb.web.dto;



/**
 * A class that handles the statement of account report.

 */
public class StatementAccountDetail {
	private String arDescription;
	private Double arAmount;
	private Integer arLineId;
	public String getArDescription() {
		return arDescription;
	}
	public void setArDescription(String arDescription) {
		this.arDescription = arDescription;
	}
	public Double getArAmount() {
		return arAmount;
	}
	public void setArAmount(Double arAmount) {
		this.arAmount = arAmount;
	}
	public Integer getArLineId() {
		return arLineId;
	}
	public void setArLineId(Integer arLineId) {
		this.arLineId = arLineId;
	}
	@Override
	public String toString() {
		return "StatementAccountDetail [arDescription=" + arDescription
				+ ", arAmount=" + arAmount + ", arLineId=" + arLineId + "]";
	}
}
