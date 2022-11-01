package eulap.eb.web.dto;

import java.util.List;

/**
 * Item sales dto.

 *
 */
public class DailyItemSale {
	private String stockCode;
	private String description;
	private String uom;
	private List<DailyItemSaleDetail> dailyItemSaleDetails;
	
	public static DailyItemSale getInstanceOf (String stockCode, 
			String description, String uom) {
		DailyItemSale dis = new DailyItemSale();
		dis.stockCode = stockCode;
		dis.description = description;
		dis.uom = uom;
		return dis;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public List<DailyItemSaleDetail> getDailyItemSaleDetails() {
		return dailyItemSaleDetails;
	}

	public void setDailyItemSaleDetails(
			List<DailyItemSaleDetail> dailyItemSaleDetails) {
		this.dailyItemSaleDetails = dailyItemSaleDetails;
	}

	@Override
	public String toString() {
		return "DailyItemSale [stockCode=" + stockCode + ", description="
				+ description + ", uom=" + uom + ", dailyItemSaleDetails="
				+ dailyItemSaleDetails + "]";
	}
}
