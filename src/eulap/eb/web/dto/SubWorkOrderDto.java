package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.WorkOrder;

/**
 * Data transfer object class for {@link WorkOrder}

 */

public class SubWorkOrderDto {
	private Integer workOrderId;
	private String woNumber;
	private String editUri;

	public Integer getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(Integer workOrderId) {
		this.workOrderId = workOrderId;
	}

	public String getWoNumber() {
		return woNumber;
	}

	public void setWoNumber(String woNumber) {
		this.woNumber = woNumber;
	}

	public String getEditUri() {
		return editUri;
	}

	public void setEditUri(String editUri) {
		this.editUri = editUri;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubWorkOrderDto [workOrderId=").append(workOrderId).append(", editUri=").append(editUri)
				.append(", woNumber=").append(woNumber).append("]");
		return builder.toString();
	}
}
