package eulap.eb.web.dto;

public class AlphalistPayeesDetailD5 extends AlphalistPayeesDetailBase{

	private String fringeBenefit;
	private String monetaryValue;
	private String actualAmtWthld;

	@Override
	public String convertDetailToCSV() {
		StringBuffer csv = new StringBuffer();
		csv.append(convertCommonFieldsToCSV());
		csv.append(',').append((getLastName().isEmpty()) ? "" : '"'+getLastName()+'"');
		csv.append(',').append((getFirstName().isEmpty()) ? "" : '"'+getFirstName()+'"');
		csv.append(',').append((getMiddleName().isEmpty()) ? "" : '"'+getMiddleName()+'"');
		prependComma(csv, getAtcCode());
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
