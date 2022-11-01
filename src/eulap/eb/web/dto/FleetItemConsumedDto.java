package eulap.eb.web.dto;

/**
 * 

 *
 */
public class FleetItemConsumedDto extends FleetAttribCostDto {
	private String stockCode;
	private Double qty;
	private String uom;
	private Integer ebObjectId;
	private Integer itemId;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {
		return "FleetItemConsumedDto [stockCode=" + stockCode + ", qty=" + qty + ", uom=" + uom + ", ebObjectId="
				+ ebObjectId + ", itemId=" + itemId + "]";
	}
}
