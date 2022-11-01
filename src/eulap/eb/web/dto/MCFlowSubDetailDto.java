package eulap.eb.web.dto;

/**
 * Data transfer of object class for monthly cash flow sub detail

 */

public class MCFlowSubDetailDto extends MCFlowDetail {
	private Integer formId;
	private String sourceLabel;
	private Double wtaxAmount;
	private Double trAmount;
	private Double paidAmount;

	public Double getWtaxAmount() {
		return wtaxAmount;
	}

	public void setWtaxAmount(Double wtaxAmount) {
		this.wtaxAmount = wtaxAmount;
	}

	public Double getTrAmount() {
		return trAmount;
	}

	public void setTrAmount(Double trAmount) {
		this.trAmount = trAmount;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public String getSourceLabel() {
		return sourceLabel;
	}

	public void setSourceLabel(String sourceLabel) {
		this.sourceLabel = sourceLabel;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MCFlowSubDetailDto [formId=").append(formId).append(", sourceLabel=").append(sourceLabel)
				.append(", wtaxAmount=").append(wtaxAmount).append(", trAmount=").append(trAmount)
				.append(", paidAmount=").append(paidAmount).append(", getAccountId()=").append(getAccountId())
				.append(", getAccountName()=").append(getAccountName()).append(", getMonth()=").append(getMonth())
				.append(", getMonthName()=").append(getMonthName()).append(", getAmount()=").append(getAmount())
				.append(", getYear()=").append(getYear()).append(", getAccountTypeId()=").append(getAccountTypeId())
				.append("]");
		return builder.toString();
	}
}
