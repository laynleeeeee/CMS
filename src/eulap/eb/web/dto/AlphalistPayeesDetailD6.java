package eulap.eb.web.dto;

public class AlphalistPayeesDetailD6 extends AlphalistPayeesDetailBase {

	private String incomePayment;
	private String registeredName;
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
		return csv.toString();
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}

	public String getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(String incomePayment) {
		this.incomePayment = incomePayment;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
