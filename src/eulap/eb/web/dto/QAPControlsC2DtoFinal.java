package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphaListControl;

public class QAPControlsC2DtoFinal implements AlphaListControl{
	private static  String alphaType = "C2";
	private static  String fTypeCode = "1601FQ";
	private String tin;
	private String branchCode;
	private String retrnPeriod;
	private String fringeBenefit;
	private String monetaryValue;
	private String actualAmtWthhld;

	@Override
	public String convertControlToCSV() {
		return alphaType + "," + fTypeCode + "," + tin + "," + branchCode + "," + retrnPeriod+ "," + fringeBenefit +
				", " + monetaryValue + " ,  " + actualAmtWthhld;
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

	public String getActualAmtWthhld() {
		return actualAmtWthhld;
	}

	public void setActualAmtWthhld(String actualAmtWthhld) {
		this.actualAmtWthhld = actualAmtWthhld;
	}

	@Override
	public String toString() {
		return "QAPControlsC2DtoFinal [tin=" + tin + ", branchCode=" + branchCode + ", retrnPeriod=" + retrnPeriod
				+ ", fringeBenefit=" + fringeBenefit + ", monetaryValue=" + monetaryValue + ", actualAmtWthhld="
				+ actualAmtWthhld + "]";
	}
}
