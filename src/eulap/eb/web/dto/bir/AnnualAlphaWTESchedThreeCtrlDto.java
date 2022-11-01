package eulap.eb.web.dto.bir;

/**
 * Annual Alphalist withhold.

 */

public class AnnualAlphaWTESchedThreeCtrlDto  extends AnnualAlphalistWTECtrlBaseDto {
	private static final String SCHEDULE_NUM =  "C3";
	private static final String FTYPE_CODE = "1604E";
	private String totalAmountWh;

	public String getTotalAmountWh() {
		return totalAmountWh;
	}

	public void setTotalAmountWh(String totalAmountWh) {
		this.totalAmountWh = totalAmountWh;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnualAlphaWTESchedThreeCtrlDto [totalAmountWh=").append(totalAmountWh).append("]");
		return builder.toString();
	}

	@Override
	public String convertControlToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SCHEDULE_NUM);
		csv.append(',').append(FTYPE_CODE).append(',');
		csv.append(convertCommonToString());
		csv.append(',').append(totalAmountWh);
		return csv.toString();
	}

}