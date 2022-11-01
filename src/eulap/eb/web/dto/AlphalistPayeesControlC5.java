package eulap.eb.web.dto;

public class AlphalistPayeesControlC5 extends AlphalistPayeesControlBase {

	private String fringeBenefit;
	private String monetaryValue;
	private String actualAmtWthld;

	@Override
	public String convertControlToCSV() {
		// TODO Auto-generated method stub
		StringBuffer csv = new StringBuffer();
		csv.append(convertCommonFieldsToCSV());
		prependComma(csv, fringeBenefit);
		prependComma(csv, monetaryValue);
		prependComma(csv, actualAmtWthld);
		return csv.toString();
	}

	public String getFringeBenefit() {
		return fringeBenefit;
	}

	public void setFringeBenefit(String fringeBenefit) {
		this.fringeBenefit = fringeBenefit;
	}

	public String getMonetaryValue() {
		return monetaryValue;
	}

	public void setMonetaryValue(String monetaryValue) {
		this.monetaryValue = monetaryValue;
	}

	public String getActualAmtWthld() {
		return actualAmtWthld;
	}

	public void setActualAmtWthld(String actualAmtWthld) {
		this.actualAmtWthld = actualAmtWthld;
	}

}