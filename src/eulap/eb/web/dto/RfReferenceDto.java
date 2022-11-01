package eulap.eb.web.dto;

import java.util.Date;

import eulap.eb.domain.hibernate.RequisitionForm;

/**
 * Data transfer object class form {@link RequisitionForm} reference

 */

public class RfReferenceDto {
	private Integer id;
	private Integer requisitionTypeId;
	private Integer companyId;
	private Integer fleetProfileId;
	private Integer projectId;
	private Date date;
	private Integer sequenceNumber;
	private String fleetName;
	private String projectName;
	private String remarks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRequisitionTypeId() {
		return requisitionTypeId;
	}

	public void setRequisitionTypeId(Integer requisitionTypeId) {
		this.requisitionTypeId = requisitionTypeId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getFleetProfileId() {
		return fleetProfileId;
	}

	public void setFleetProfileId(Integer fleetProfileId) {
		this.fleetProfileId = fleetProfileId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getFleetName() {
		return fleetName;
	}

	public void setFleetName(String fleetName) {
		this.fleetName = fleetName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequisitionFormReferenceDto [id=").append(id).append(", requisitionTypeId=")
				.append(requisitionTypeId).append(", companyId=").append(companyId).append(", fleetProfileId=")
				.append(fleetProfileId).append(", projectId=").append(projectId).append(", date=").append(date)
				.append(", sequenceNumber=").append(sequenceNumber).append(", fleetName=").append(fleetName)
				.append(", projectName=").append(projectName).append(", remarks=").append(remarks).append("]");
		return builder.toString();
	}

}
