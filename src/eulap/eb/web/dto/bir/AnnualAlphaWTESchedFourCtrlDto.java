package eulap.eb.web.dto.bir;

/**
 * Annual Alphalist withhold.

 */

public class AnnualAlphaWTESchedFourCtrlDto  extends AnnualAlphalistWTECtrlBaseDto {
	private String incomePayment;
	private static final String SCHEDULE_NUM =  "C4";
	private static final String FTYPE_CODE = "1604E";

	@Override
	public String convertControlToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SCHEDULE_NUM);
		csv.append(',').append(FTYPE_CODE).append(',');
		csv.append(convertCommonToString());
		csv.append(',').append(incomePayment);
		return csv.toString();
	}

	public String getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(String incomePayment) {
		this.incomePayment = incomePayment;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnualAlphaWTESchedFourCtrlDto [incomePayment=").append(incomePayment).append("]");
		return builder.toString();
	}

}