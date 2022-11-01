package eulap.eb.web.dto;

/**
 * Income Statement Comparison Report Details DTO.

 * 
 */
public class IncomeStatementComparisonDataDto {
	private String particulars;
	private String currentyear;
	private double currentYAmount;
	private String prevYear;
	private double prevYAmount;

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public String getCurrentyear() {
		return currentyear;
	}

	public void setCurrentyear(String currentyear) {
		this.currentyear = currentyear;
	}

	public Double getCurrentYAmount() {
		return currentYAmount;
	}

	public void setCurrentYAmount(double currentYAmount) {
		this.currentYAmount = currentYAmount;
	}

	public String getPrevYear() {
		return prevYear;
	}

	public void setPrevYear(String prevYear) {
		this.prevYear = prevYear;
	}

	public Double getPrevYAmount() {
		return prevYAmount;
	}

	public void setPrevYAmount(double prevYAmount) {
		this.prevYAmount = prevYAmount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IncomeStatementComparisonDataDto [particulars=").append(particulars).append(", currentyear=")
				.append(currentyear).append(", currentYAmount=").append(currentYAmount).append(", prevYear=")
				.append(prevYear).append(", prevYAmount=").append(prevYAmount).append("]");
		return builder.toString();
	}
}
