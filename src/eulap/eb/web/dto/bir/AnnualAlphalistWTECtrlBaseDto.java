package eulap.eb.web.dto.bir;

import eulap.eb.service.bir.AlphaListControl;

/**
 * Annual Alphalist withhold.

 */

public abstract class AnnualAlphalistWTECtrlBaseDto  implements AlphaListControl {
	private String agentTin;
	private String branchCode;
	private String returnPeriod;

	protected String convertCommonToString() {
		StringBuilder csv = new StringBuilder();
		csv.append(agentTin);
		csv.append(',').append(branchCode);
		csv.append(',').append(returnPeriod);
		return csv.toString();
	}

	public String getAgentTin() {
		return agentTin;
	}

	public void setAgentTin(String agentTin) {
		this.agentTin = agentTin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnualAlphaWTESchedThreeCtrlDto [agentTin=").append(agentTin).append(", branchCode=")
				.append(branchCode).append(", returnPeriod=").append(returnPeriod).append("]");
		return builder.toString();
	}
}