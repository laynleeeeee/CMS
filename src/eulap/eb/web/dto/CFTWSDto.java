package eulap.eb.web.dto;

/**
 * Certificate of final tax withheld at source summary dto

 */

public class CFTWSDto {

	private String month;
	private Double addFinalTaxWithheld;
	private int monthId;

	public int getMonthId() {
		return monthId;
	}

	public void setMonthId(int monthId) {
		this.monthId = monthId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Double getAddFinalTaxWithheld() {
		return addFinalTaxWithheld;
	}

	public void setAddFinalTaxWithheld(Double addFinalTaxWithheld) {
		this.addFinalTaxWithheld = addFinalTaxWithheld;
	}

	@Override
	public String toString() {
		return "CertFinalTaxWithheldSummaryDto [month=" + month + ", addFinalTaxWithheld=" + addFinalTaxWithheld
				+ ", monthId=" + monthId + "]";
	}

}
