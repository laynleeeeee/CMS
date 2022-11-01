package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.RequisitionForm;
import eulap.eb.domain.hibernate.WithdrawalSlip;

/**
 * Holds data to validate cancellation of {@link RequisitionForm}
 * when used in other forms such as {@link WithdrawalSlip} or Purchase Requisition

 *
 */
public class UsedRequisitionFormDto {

	private Integer requisitionFormId;
	private Integer rfSequenceNo;
	private String refererForm;
	private Integer refererFormSequenceNo;

	public Integer getRequisitionFormId() {
		return requisitionFormId;
	}

	public void setRequisitionFormId(Integer requisitionFormId) {
		this.requisitionFormId = requisitionFormId;
	}

	public Integer getRfSequenceNo() {
		return rfSequenceNo;
	}

	public void setRfSequenceNo(Integer rfSequenceNo) {
		this.rfSequenceNo = rfSequenceNo;
	}

	public String getRefererForm() {
		return refererForm;
	}

	public void setRefererForm(String refererForm) {
		this.refererForm = refererForm;
	}

	public Integer getRefererFormSequenceNo() {
		return refererFormSequenceNo;
	}

	public void setRefererFormSequenceNo(Integer refererFormSequenceNo) {
		this.refererFormSequenceNo = refererFormSequenceNo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UseRequisitionFormDto [requisitionFormId=").append(requisitionFormId).append(", rfSequenceNo=")
				.append(rfSequenceNo).append(", refererForm=").append(refererForm).append(", refererFormSequenceNo=")
				.append(refererFormSequenceNo).append("]");
		return builder.toString();
	}

}
