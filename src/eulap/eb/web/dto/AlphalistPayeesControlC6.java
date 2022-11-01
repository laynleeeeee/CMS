package eulap.eb.web.dto;

public class AlphalistPayeesControlC6 extends AlphalistPayeesControlBase {

	private String incomePayment;

	@Override
	public String convertControlToCSV() {
		// TODO Auto-generated method stub
		StringBuffer csv = new StringBuffer();
		csv.append(convertCommonFieldsToCSV());
		prependComma(csv, incomePayment);
		return csv.toString();
	}

	public String getIncomePayment() {
		return incomePayment;
	}

	public void setIncomePayment(String incomePayment) {
		this.incomePayment = incomePayment;
	}

}