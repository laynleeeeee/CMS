package eulap.eb.web.dto;

import java.util.List;

/**
 * Container for the item selling details

 */
public class RItemDetail {
	private Integer itemId;
	private Integer companyId;
	private String companyName;
	private Double sellingPrice;
	private Double wholesalePrice;
	private Double buyingPrice;
	private Double unitCost;
	private List<RItemDetailValue> values;
	private String divisionName;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(Double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public Double getWholesalePrice() {
		return wholesalePrice;
	}

	public void setWholesalePrice(Double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}

	public Double getBuyingPrice() {
		return buyingPrice;
	}

	public void setBuyingPrice(Double buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public List<RItemDetailValue> getValues() {
		return values;
	}

	public void setValues(List<RItemDetailValue> values) {
		this.values = values;
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
		builder.append("RItemDetail [itemId=").append(itemId)
				.append(", companyId=").append(companyId)
				.append(", companyName=").append(companyName)
				.append(", sellingPrice=").append(sellingPrice)
				.append(", wholesalePrice=").append(wholesalePrice)
				.append(", buyingPrice=").append(buyingPrice)
				.append(", unitCost=").append(unitCost).append(", values=")
				.append(values).append("]");
		return builder.toString();
	}

}
