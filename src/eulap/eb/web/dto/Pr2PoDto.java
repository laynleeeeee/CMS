package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.RPurchaseOrder;

/**
 * Data access object for converting purchase requisition form to purchase order.

 *
 */
public class Pr2PoDto {
	private List<Integer> prObjIds;
	private String strPrNumbers;
	private String strPrNumbersComma;
	private String projectName;
	private String fleetProfileCode;
	private String remarks;
	private Integer warehouseId;
	private RPurchaseOrder purchaseOrder;

	/**
	 * Create an instance of {@code Pr2PoDto}
	 */
	public static Pr2PoDto getInstanceOf(List<Integer> prObjIds, String strPrNumbers, String strPrNumbersComma, String projectName, 
			String fleetProfileCode, String remarks, Integer warehouseId, RPurchaseOrder purchaseOrder) {
		Pr2PoDto obj = new Pr2PoDto();
		obj.prObjIds = prObjIds;
		obj.strPrNumbers = strPrNumbers;
		obj.strPrNumbersComma = strPrNumbersComma;
		obj.projectName = projectName;
		obj.fleetProfileCode = fleetProfileCode;
		obj.remarks = remarks;
		obj.warehouseId = warehouseId;
		obj.purchaseOrder = purchaseOrder;
		return obj;
	}

	public List<Integer> getPrObjIds() {
		return prObjIds;
	}

	public void setPrObjIds(List<Integer> prObjIds) {
		this.prObjIds = prObjIds;
	}

	public String getStrPrNumbers() {
		return strPrNumbers;
	}

	public void setStrPrNumbers(String strPrNumbers) {
		this.strPrNumbers = strPrNumbers;
	}

	public String getStrPrNumbersComma() {
		return strPrNumbersComma;
	}

	public void setStrPrNumbersComma(String strPrNumbersComma) {
		this.strPrNumbersComma = strPrNumbersComma;
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

	public RPurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(RPurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pr2PoDto [prObjIds=").append(prObjIds).append(", strPrNumbers=").append(strPrNumbers)
				.append(", projectName=").append(projectName).append(", fleetProfileCode=").append(fleetProfileCode)
				.append(", remarks=").append(remarks).append(", warehouseId=").append(warehouseId).append("]");
		return builder.toString();
	}
}
