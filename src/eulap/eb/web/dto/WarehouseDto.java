package eulap.eb.web.dto;

import java.util.List;

/**
 * Data transfer object class for warehouse

 */

public class WarehouseDto {
	private Integer id;
	private String companyName;
	private String name;
	private String address;
	private Integer parentWarehouseId;
	private String parentWarehouseName;
	private WarehouseDto parentWarehouse;
	private List<WarehouseDto> childrenWarehouse;
	private boolean active;
	private boolean isMainParent;
	private boolean isLastLevel;
	private String divisionName;

	public static WarehouseDto getInstanceOf(Integer id, String companyName, String name, String address,
			Integer parentWarehouseId, String parentWarehouseName, boolean active, boolean isLastLevel, String divisionName) {
		WarehouseDto warehouseDto = new WarehouseDto();
		warehouseDto.setId(id);
		warehouseDto.setCompanyName(companyName);
		warehouseDto.setName(name);
		warehouseDto.setAddress(address);
		warehouseDto.setParentWarehouseId(parentWarehouseId);
		warehouseDto.setParentWarehouseName(parentWarehouseName);
		warehouseDto.setActive(active);
		warehouseDto.setLastLevel(isLastLevel);
		warehouseDto.setDivisionName(divisionName);
		return warehouseDto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getParentWarehouseId() {
		return parentWarehouseId;
	}

	public void setParentWarehouseId(Integer parentWarehouseId) {
		this.parentWarehouseId = parentWarehouseId;
	}

	public String getParentWarehouseName() {
		return parentWarehouseName;
	}

	public void setParentWarehouseName(String parentWarehouseName) {
		this.parentWarehouseName = parentWarehouseName;
	}

	public WarehouseDto getParentWarehouse() {
		return parentWarehouse;
	}

	public void setParentWarehouse(WarehouseDto parentWarehouse) {
		this.parentWarehouse = parentWarehouse;
	}

	public List<WarehouseDto> getChildrenWarehouse() {
		return childrenWarehouse;
	}

	public void setChildrenWarehouse(List<WarehouseDto> childrenWarehouse) {
		this.childrenWarehouse = childrenWarehouse;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isMainParent() {
		return isMainParent;
	}

	public void setMainParent(boolean isMainParent) {
		this.isMainParent = isMainParent;
	}

	public boolean isLastLevel() {
		return isLastLevel;
	}

	public void setLastLevel(boolean isLastLevel) {
		this.isLastLevel = isLastLevel;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WarehouseDto [id=").append(id).append(", companyName=").append(companyName).append(", name=")
				.append(name).append(", address=").append(address).append(", parentWarehouseId=")
				.append(parentWarehouseId).append(", parentWarehouseName=").append(parentWarehouseName)
				.append(", active=").append(active).append(", isMainParent=").append(isMainParent)
				.append(", isLastLevel=").append(isLastLevel).append("]");
		return builder.toString();
	}
}
