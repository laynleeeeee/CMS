package eulap.eb.web.dto;

/**
 * Annual Alphalist withhold schedule 3.

 */

public class AnnualAlphaWTESchedThreeDetailsDto extends AnnualAlphaWTESchedBaseDetaisDto {
	private static final String SCHEDULE_NUM =  "D3";
	private static final String FTYPE_CODE = "1604E";
	private double taxRate;
	private String amountTaxheld;

	@Override
	public String convertDetailToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SCHEDULE_NUM);
		csv.append(',').append(FTYPE_CODE);
		csv.append(convertCommonToString());
		csv.append(',').append(taxRate);
		csv.append(',').append(amountTaxheld);
		return csv.toString();
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public String getAmountTaxheld() {
		return amountTaxheld;
	}

	public void setAmountTaxheld(String amountTaxheld) {
		this.amountTaxheld = amountTaxheld;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AnnualAlphaWTESchedThreeDetailsDto [taxRate=").append(taxRate).append(", amountTaxheld=")
				.append(amountTaxheld).append("]");
		return builder.toString();
	}
}