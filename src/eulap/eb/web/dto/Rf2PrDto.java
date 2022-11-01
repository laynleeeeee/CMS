package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.PurchaseRequisitionItem;

/**
 * Data access object for converting requisition form to purchase requisition form.

 *
 */
public class Rf2PrDto {
	private Integer rfId;
	private String rfSeqNo;
	private Integer joSeqNo;
	private Integer fleetProfileId;
	private Integer arCustomerId;
	private String projectName;
	private String fleetProfileCode;
	private String strRequestedDate;
	private String remarks;
	private Integer warehouseId;
	private List<PurchaseRequisitionItem> prItems;

	/**
	 * Create instance of dto that converts requisition form to purchase requisition form.
	 */
	public static Rf2PrDto getInstanceOf(Integer rfId, String rfSeqNo, Integer fleetProfileId,
			Integer arCustomerId, String projectName, String fleetProfileCode, String strRequestedDate,
			String remarks, Integer warehouseId, List<PurchaseRequisitionItem> prItems) {
		Rf2PrDto obj = new Rf2PrDto();
		obj.rfId = rfId;
		obj.rfSeqNo = rfSeqNo;
		obj.fleetProfileId = fleetProfileId;
		obj.arCustomerId = arCustomerId;
		obj.projectName = projectName;
		obj.fleetProfileCode = fleetProfileCode;
		obj.strRequestedDate = strRequestedDate;
		obj.remarks = remarks;
		obj.warehouseId = warehouseId;
		obj.prItems = prItems;
		return obj;
	}

	public Integer getRfId() {
		return rfId;
	}

	public void setRfId(Integer rfId) {
		this.rfId = rfId;
	}

	public String getRfSeqNo() {
		return rfSeqNo;
	}

	public void setRfSeqNo(String rfSeqNo) {
		this.rfSeqNo = rfSeqNo;
	}

	public Integer getJoSeqNo() {
		return joSeqNo;
	}

	public void setJoSeqNo(Integer joSeqNo) {
		this.joSeqNo = joSeqNo;
	}

	public Integer getFleetProfileId() {
		return fleetProfileId;
	}

	public void setFleetProfileId(Integer fleetProfileId) {
		this.fleetProfileId = fleetProfileId;
	}

	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getFleetProfileCode() {
		return fleetProfileCode;
	}

	public void setFleetProfileCode(String fleetProfileCode) {
		this.fleetProfileCode = fleetProfileCode;
	}

	public String getStrRequestedDate() {
		return strRequestedDate;
	}

	public void setStrRequestedDate(String strRequestedDate) {
		this.strRequestedDate = strRequestedDate;
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

	public List<PurchaseRequisitionItem> getPrItems() {
		return prItems;
	}

	public void setPrItems(List<PurchaseRequisitionItem> prItems) {
		this.prItems = prItems;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rf2PrDto [rfId=").append(rfId).append(", rfSeqNo=").append(rfSeqNo).append(", joSeqNo=")
				.append(joSeqNo).append(", fleetProfileId=").append(fleetProfileId).append(", arCustomerId=")
				.append(arCustomerId).append(", projectName=").append(projectName).append(", fleetProfileCode=")
				.append(fleetProfileCode).append(", strRequestedDate=").append(strRequestedDate).append(", remarks=")
				.append(remarks).append(", warehouseId=").append(warehouseId).append("]");
		return builder.toString();
	}
}
