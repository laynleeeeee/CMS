package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphaListControl;

/**
 * A class that handles the Summary Alphalist of Withholding Taxes report data.

 */
public class SAWTControlsDto implements AlphaListControl{
	private static  String alphaType = "CSAWT";
	private String fTypeCode;
	private String tin;
	private String branchCode;
	private String retrnPeriod;
	private Double taxBase;
	private Double actualAmtWthld;
	private String strTaxBase;
	private String strActualAmtWthld;

	@Override
	public String convertControlToCSV() {
		return alphaType + "," + fTypeCode + "," + tin + "," + branchCode + "," + retrnPeriod + "," + strTaxBase
				+ "," + strActualAmtWthld;
	}

	public String getfTypeCode() {
		return fTypeCode;
	}

	public void setfTypeCode(String fTypeCode) {
		this.fTypeCode = fTypeCode;
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

	public Double getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(Double taxBase) {
		this.taxBase = taxBase;
	}

	public Double getActualAmtWthld() {
		return actualAmtWthld;
	}

	public void setActualAmtWthld(Double actualAmtWthld) {
		this.actualAmtWthld = actualAmtWthld;
	}

	public String getStrTaxBase() {
		return strTaxBase;
	}

	public void setStrTaxBase(String strTaxBase) {
		this.strTaxBase = strTaxBase;
	}

	public String getStrActualAmtWthld() {
		return strActualAmtWthld;
	}

	public void setStrActualAmtWthld(String strActualAmtWthld) {
		this.strActualAmtWthld = strActualAmtWthld;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SAWTControlsDto [fTypeCode=").append(fTypeCode).append(", tin=").append(tin)
				.append(", branchCode=").append(branchCode).append(", retrnPeriod=").append(retrnPeriod)
				.append(", taxBase=").append(taxBase).append(", actualAmtWthld=").append(actualAmtWthld)
				.append(", strTaxBase=").append(strTaxBase).append(", strActualAmtWthld=").append(strActualAmtWthld)
				.append("]");
		return builder.toString();
	}
}