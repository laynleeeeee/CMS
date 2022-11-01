package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphaListControl;

/**
 * A class that handles the Summary Quarterly Alphalist of Payees report data.

 */
public class QAPControlsC2Dto implements AlphaListControl{
	private static  String alphaType = "C2";
	private static  String fTypeCode = "1601EQ";
	private String tin;
	private String branchCode;
	private String retrnPeriod;
	private String incomePayment;

	@Override
	public String convertControlToCSV() {
		return alphaType + "," + fTypeCode + "," + tin + "," + branchCode + "," + retrnPeriod+ "," + incomePayment;
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

	@Override
	public String toString() {
		return "QAPControlsC2Dto [tin=" + tin + ", branchCode=" + branchCode + ", retrnPeriod=" + retrnPeriod
				+ ", incomePayment=" + incomePayment + "]";
	}
}