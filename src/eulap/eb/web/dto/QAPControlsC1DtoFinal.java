package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphaListControl;

/**
 * A class that handles the Summary Quarterly Alphalist of Payees Final report data.

 */
public class QAPControlsC1DtoFinal implements AlphaListControl{
	private static  String alphaType = "C1";
	private static  String fTypeCode = "1601FQ";
	private String tin;
	private String branchCode;
	private String retrnPeriod;
	private String incomePayment;
	private String actualAmtWthld;

	@Override
	public String convertControlToCSV() {
		return alphaType + "," + fTypeCode + "," + tin + "," + branchCode + "," + retrnPeriod + "," + incomePayment
				+ "," + actualAmtWthld;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getRetrnPeriod() {
		return retrnPeriod;
	}

	public void setRetrnPeriod(String retrnPeriod) {
		this.retrnPeriod = retrnPeriod;
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

	@Override
	public String toString() {
		return "QAPControlsC1DtoFinal [tin=" + tin + ", branchCode=" + branchCode + ", retrnPeriod=" + retrnPeriod
				+ ", incomePayment=" + incomePayment + ", actualAmtWthld=" + actualAmtWthld + "]";
	}
}