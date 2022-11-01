package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphaListControl;

/**
 * A class that handles the Summary Quarterly Alphalist of Payees report data.

 */
public class QAPControlsC1Dto implements AlphaListControl{
	private static  String alphaType = "C1";
	private static  String fTypeCode = "1601EQ";
	private String tin;
	private String branchCode;
	private String retrnPeriod;
	private String taxBase;
	private String actualAmtWthld;

	@Override
	public String convertControlToCSV() {
		return alphaType + "," + fTypeCode + "," + tin + "," + branchCode + "," + retrnPeriod + "," + taxBase
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

	public String getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(String taxBase) {
		this.taxBase = taxBase;
	}

	public String getActualAmtWthld() {
		return actualAmtWthld;
	}

	public void setActualAmtWthld(String actualAmtWthld) {
		this.actualAmtWthld = actualAmtWthld;
	}

	@Override
	public String toString() {
		return "QAPControlsC1Dto [tin=" + tin + ", branchCode=" + branchCode + ", retrnPeriod=" + retrnPeriod
				+ ", taxBase=" + taxBase + ", actualAmtWthld=" + actualAmtWthld + "]";
	}
}