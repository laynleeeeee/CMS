package eulap.eb.web.dto;

/**
 * Annual Alphalist withhold schedule 4 details.

 */

public class AnnualAlphaWTESchedFourDetaisDto extends AnnualAlphaWTESchedBaseDetaisDto {
	private static final String SCHEDULE_NUM =  "D4";
	private static final String FTYPE_CODE = "1604E";

	@Override
	public String convertDetailToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SCHEDULE_NUM);
		csv.append(',').append(FTYPE_CODE);
		csv.append(convertCommonToString());
		return csv.toString();
	}
}