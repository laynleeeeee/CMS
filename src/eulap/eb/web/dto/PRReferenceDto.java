package eulap.eb.web.dto;

import java.util.Date;

/**
 *  Purchase requisition DTO for Purchase order referencing.

 *
 */
public class PRReferenceDto {
	private Integer prId;
	private Integer prObjId;
	private Date date;
	private String prNo;
	private String projectName;
	private String fleetCode;
	private String remarks;
	private Integer warehouseId;
	private String warehouseName;

	public static final int PRI_POI_OR_TYPE_ID = 3010;
	public static final int MERGED_PRI_POI_OR_TYPE_ID = 3011;

	/**
	 * Create an instance of {@code PRReferenceDto}
	 */
	public static PRReferenceDto getInstanceOf (Integer prId, Integer prObjId, Date date, String prNo,
			String projectName, String fleetCode, String remarks, Integer warehouseId, String warehouseName) {
		PRReferenceDto dto = new PRReferenceDto();
		dto.prId = prId;
		dto.prObjId = prObjId;
		dto.date = date;
		dto.prNo = prNo;
		dto.projectName = projectName;
		dto.fleetCode = fleetCode;
		dto.remarks = remarks;
		dto.warehouseId = warehouseId;
		dto.warehouseName = warehouseName;
		return dto;
	}

	public Integer getPrId() {
		return prId;
	}

	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	public Integer getPrObjId() {
		return prObjId;
	}

	public void setPrObjId(Integer prObjId) {
		this.prObjId = prObjId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPrNo() {
		return prNo;
	}

	public void setPrNo(String prNo) {
		this.prNo = prNo;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getFleetCode() {
		return fleetCode;
	}

	public void setFleetCode(String fleetCode) {
		this.fleetCode = fleetCode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PRReferenceDto [prId=").append(prId).append(", prObjId=").append(prObjId).append(", date=")
				.append(date).append(", prNo=").append(prNo).append(", projectName=").append(projectName)
				.append(", fleetCode=").append(fleetCode).append(", remarks=").append(remarks)
				.append(", warehouseId=").append(warehouseId).append(", warehouseName=").append(warehouseName)
				.append("]");
		return builder.toString();
	}
}
