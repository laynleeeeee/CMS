package eulap.eb.web.dto;


/**
 * Data transfer class for Alpha List Payees

 */

public class AlphaListPayeesDto {

	private String tin;
	private String corporationName;
	private String individualName;
	private String atc;
	private String natureOfIncomePayment;
	private Double firstMonthIncomePayment;
	private Double firstMonthTaxRate;
	private Double firstMonthTaxWithheld;
	private Double secondMonthIncomePayment;
	private Double secondMonthTaxRate;
	private Double secondMonthTaxWithheld;
	private Double thirdMonthTaxWithheld;
	public String getTin() {
		return tin;
	}
	public void setTin(String tin) {
		this.tin = tin;
	}
	public String getCorporationName() {
		return corporationName;
	}
	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}
	public String getIndividualName() {
		return individualName;
	}
	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}
	public String getAtc() {
		return atc;
	}
	public void setAtc(String atc) {
		this.atc = atc;
	}
	public String getNatureOfIncomePayment() {
		return natureOfIncomePayment;
	}
	public void setNatureOfIncomePayment(String natureOfIncomePayment) {
		this.natureOfIncomePayment = natureOfIncomePayment;
	}
	public Double getFirstMonthIncomePayment() {
		return firstMonthIncomePayment;
	}
	public void setFirstMonthIncomePayment(Double firstMonthIncomePayment) {
		this.firstMonthIncomePayment = firstMonthIncomePayment;
	}
	public Double getFirstMonthTaxRate() {
		return firstMonthTaxRate;
	}
	public void setFirstMonthTaxRate(Double firstMonthTaxRate) {
		this.firstMonthTaxRate = firstMonthTaxRate;
	}
	public Double getFirstMonthTaxWithheld() {
		return firstMonthTaxWithheld;
	}
	public void setFirstMonthTaxWithheld(Double firstMonthTaxWithheld) {
		this.firstMonthTaxWithheld = firstMonthTaxWithheld;
	}
	public Double getSecondMonthIncomePayment() {
		return secondMonthIncomePayment;
	}
	public void setSecondMonthIncomePayment(Double secondMonthIncomePayment) {
		this.secondMonthIncomePayment = secondMonthIncomePayment;
	}
	public Double getSecondMonthTaxRate() {
		return secondMonthTaxRate;
	}
	public void setSecondMonthTaxRate(Double secondMonthTaxRate) {
		this.secondMonthTaxRate = secondMonthTaxRate;
	}
	public Double getSecondMonthTaxWithheld() {
		return secondMonthTaxWithheld;
	}
	public void setSecondMonthTaxWithheld(Double secondMonthTaxWithheld) {
		this.secondMonthTaxWithheld = secondMonthTaxWithheld;
	}
	public Double getThirdMonthTaxWithheld() {
		return thirdMonthTaxWithheld;
	}
	public void setThirdMonthTaxWithheld(Double thirdMonthTaxWithheld) {
		this.thirdMonthTaxWithheld = thirdMonthTaxWithheld;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlphaListPayeesDto [tin=").append(tin).append(", corporationName=").append(corporationName)
				.append(", individualName=").append(individualName).append(", atc=").append(atc)
				.append(", natureOfIncomePayment=").append(natureOfIncomePayment).append(", firstMonthIncomePayment=")
				.append(firstMonthIncomePayment).append(", firstMonthTaxRate=").append(firstMonthTaxRate)
				.append(", firstMonthTaxWithheld=").append(firstMonthTaxWithheld).append(", secondMonthIncomePayment=")
				.append(secondMonthIncomePayment).append(", secondMonthTaxRate=").append(secondMonthTaxRate)
				.append(", secondMonthTaxWithheld=").append(secondMonthTaxWithheld).append(", thirdMonthTaxWithheld=")
				.append(thirdMonthTaxWithheld).append("]");
		return builder.toString();
	}
}
