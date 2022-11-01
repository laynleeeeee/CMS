package eulap.eb.web.dto;

import java.util.Date;

/**
 * Dto for Item Unit Cost History Per Supplier report.

 *
 */
public class ItemUnitCostHistoryPerSupplier {
	private String divisionName;
	private Date date;
	private String supplier;
	private String rrNumber;
	private Double unitCost;
	
	public static ItemUnitCostHistoryPerSupplier getInstanceOf(String divisionName, Date date, String supplier, String rrNumber,
			Double unitCost) {
		ItemUnitCostHistoryPerSupplier itemUnitCostHistoryPerSupplier = new ItemUnitCostHistoryPerSupplier();
		itemUnitCostHistoryPerSupplier.divisionName = divisionName;
		itemUnitCostHistoryPerSupplier.date = date;
		itemUnitCostHistoryPerSupplier.supplier = supplier;
		itemUnitCostHistoryPerSupplier.rrNumber = rrNumber;
		itemUnitCostHistoryPerSupplier.unitCost = unitCost;
		return itemUnitCostHistoryPerSupplier;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getSupplier() {
		return supplier;
	}
	
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	public String getRrNumber() {
		return rrNumber;
	}
	
	public void setRrNumber(String rrNumber) {
		this.rrNumber = rrNumber;
	}
	
	public Double getUnitCost() {
		return unitCost;
	}
	
	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Override
	public String toString() {
		return "ItemUnitCostHistoryPerSupplier [divisionName=" + divisionName + ", date=" + date + ", supplier="
				+ supplier + ", rrNumber=" + rrNumber + ", unitCost=" + unitCost + "]";
	}
}
