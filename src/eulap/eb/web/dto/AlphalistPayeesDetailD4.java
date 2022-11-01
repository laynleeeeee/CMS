package eulap.eb.web.dto;

public class AlphalistPayeesDetailD4 extends AlphalistPayeesDetailBase {
	private String registeredName;
	private Double taxRate;
	private String incomePayment;
	private String actualAmtWthld;
	private String statusCode;

	@Override
	public String convertDetailToCSV() {

		StringBuffer csv = new StringBuffer();
		csv.append(convertCommonFieldsToCSV());

		if (!registeredName.isEmpty()) {
			//Registered Name
			csv.append(',').append('"').append(registeredName).append('"');
			csv.append(','); // Last Name
			csv.append(','); // First Name
			csv.append(','); // Middle Name
		} else {
			csv.append(','); // Registered Name
			csv.append(',').append((getLastName().isEmpty()) ? "" : '"'+getLastName()+'"');
			csv.append(',').append((getFirstName().isEmpty()) ? "" : '"'+getFirstName()+'"');
			csv.append(',').append((getMiddleName().isEmpty()) ? "" : '"'+getMiddleName()+'"');
		}
		prependComma(csv, statusCode);
		prependComma(csv, getAtcCode());
		prependComma(csv, incomePayment);
		prependComma(csv, taxRate);
		prependComma(csv, actualAmtWthld);
		return csv.toString();
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
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

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
