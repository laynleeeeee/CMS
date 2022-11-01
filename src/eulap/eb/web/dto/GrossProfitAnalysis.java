package eulap.eb.web.dto;

/**
 * Gross profit analysis dto.

 *
 */
public class GrossProfitAnalysis {
	private String stockCode;
	private String division;
	private String description;
	private Double qtySold;
	private String uom;
	private Double netSales;
	private Double costOfSales;
	private Double grossProfit;
	private Double grossProfitPercent;

	public static GrossProfitAnalysis getInstanceOf (String stockCode, String division, String description, Double qtySold,
			String uom, Double netSales, Double costOfSales, Double grossProfit, Double grossProfitPercent) {
		GrossProfitAnalysis gpa = new GrossProfitAnalysis();
		gpa.stockCode = stockCode;
		gpa.division = division;
		gpa.description = description;
		gpa.qtySold = qtySold;
		gpa.uom = uom;
		gpa.netSales = netSales;
		gpa.costOfSales = costOfSales;
		gpa.grossProfit = grossProfit;
		gpa.grossProfitPercent = grossProfitPercent;
		return gpa;
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

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getQtySold() {
		return qtySold;
	}

	public void setQtySold(Double qtySold) {
		this.qtySold = qtySold;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getNetSales() {
		return netSales;
	}

	public void setNetSales(Double netSales) {
		this.netSales = netSales;
	}

	public Double getCostOfSales() {
		return costOfSales;
	}

	public void setCostOfSales(Double costOfSales) {
		this.costOfSales = costOfSales;
	}

	public Double getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Double grossProfit) {
		this.grossProfit = grossProfit;
	}

	public Double getGrossProfitPercent() {
		return grossProfitPercent;
	}

	public void setGrossProfitPercent(Double grossProfitPercent) {
		this.grossProfitPercent = grossProfitPercent;
	}

	@Override
	public String toString() {
		return "GrossProfitAnalysis [stockCode=" + stockCode + ", division=" + division + ", description=" + description
				+ ", qtySold=" + qtySold + ", uom=" + uom + ", netSales=" + netSales + ", costOfSales=" + costOfSales
				+ ", grossProfit=" + grossProfit + "]";
	}

}
