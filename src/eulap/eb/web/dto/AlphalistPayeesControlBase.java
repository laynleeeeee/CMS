package eulap.eb.web.dto;

import eulap.eb.service.bir.AlphaListControl;

/**
 * A class that handles the  base controls for Alphalist Payees report data.

 */
public abstract class AlphalistPayeesControlBase implements AlphaListControl{
	public static  String ALPHA_TYPE_C4 = "C4";
	public static  String ALPHA_TYPE_C5 = "C5";
	public static  String ALPHA_TYPE_C6 = "C6";
	public static  String F_TYPE_CODE_1604F = "1604F";
	private String tinWa;
	private String branchCodeWa;
	private String retrnPeriod;
	private String alphaType;
	private String fTypeCode;

	protected String convertCommonFieldsToCSV () {
		StringBuffer csv = new StringBuffer();
		csv.append(alphaType);
		prependComma(csv, fTypeCode);
		prependComma(csv, tinWa);
		prependComma(csv, branchCodeWa);
		prependComma(csv, retrnPeriod);
		return csv.toString();
	}

	protected void prependComma (StringBuffer csv, Object obj) {
		csv.append(',').append(obj);
	}

	public String getfTypeCode() {
		return fTypeCode;
	}

	public void setfTypeCode(String fTypeCode) {
		this.fTypeCode = fTypeCode;
	}

	public String getAlphaType() {
		return alphaType;
	}

	public void setAlphaType(String alphaType) {
		this.alphaType = alphaType;
	}

	public String getTinWa() {
		return tinWa;
	}

	public void setTinWa(String tinWa) {
		this.tinWa = tinWa;
	}

	public String getBranchCodeWa() {
		return branchCodeWa;
	}

	public void setBranchCodeWa(String branchCodeWa) {
		this.branchCodeWa = branchCodeWa;
	}

	public String getRetrnPeriod() {
		return retrnPeriod;
	}

	public void setRetrnPeriod(String retrnPeriod) {
		this.retrnPeriod = retrnPeriod;
	}
}