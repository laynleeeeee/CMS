package eulap.eb.web.dto;

public class AlphalistPayeesControlC4 extends AlphalistPayeesControlBase {

	private String incomePayment;
	private String actualAmtWthld;

	@Override
	public String convertControlToCSV() {
		StringBuffer csv = new StringBuffer();
		csv.append(convertCommonFieldsToCSV());
		prependComma(csv, incomePayment);
		prependComma(csv, actualAmtWthld);
		return csv.toString();
	}

	public String getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(String incomePayment) {
		this.incomePayment = incomePayment;
	}

	public String getActualAmtWthld() {
		return actualAmtWthld;
	}

	public void setActualAmtWthld(String actualAmtWthld) {
		this.actualAmtWthld = actualAmtWthld;
	}
}