package eulap.eb.web.dto;

import java.util.List;

/**
 * Income Statement By Division DTO.

 * 
 */
public class IncomeStatementNSBDto {
	List<IncomeStatementByDivisionDto> consolidatedByDivisionDtos;
	List<IncomeStatementByMonthDto> productCostByMonths;
	List<IncomeStatementByMonthDto> sellingByMonths;
	List<IncomeStatementByMonthDto> genAndAdminByMonths;
	List<IncomeStatementComparisonDto> comparisonDtos; 
	private Double salesTotalCentral;
	private Double salesTotalNSB3;
	private Double salesTotalNSB4;
	private Double salesTotalNSB5;
	private Double salesTotalNSB8;
	private Double salesTotalNSB8A;
	private Double salesGrandTotal;
	private int monthCount;
	private int divisionCount;

	public static final int PERCENT_TO_SALES_ID = 2147483647;
	public static final int TOTAL_DIV_ID = 2147483646;
	public static final String TOTAL_DIV_NAME = "TOTAL";
	public static final String PERCENT_TO_SALES = "% to Sales";

	public List<IncomeStatementByDivisionDto> getConsolidatedByDivisionDtos() {
		return consolidatedByDivisionDtos;
	}

	public void setConsolidatedByDivisionDtos(List<IncomeStatementByDivisionDto> consolidatedByDivisionDtos) {
		this.consolidatedByDivisionDtos = consolidatedByDivisionDtos;
	}

	public List<IncomeStatementByMonthDto> getProductCostByMonths() {
		return productCostByMonths;
	}

	public void setProductCostByMonths(List<IncomeStatementByMonthDto> productCostByMonths) {
		this.productCostByMonths = productCostByMonths;
	}

	public List<IncomeStatementByMonthDto> getSellingByMonths() {
		return sellingByMonths;
	}

	public void setSellingByMonths(List<IncomeStatementByMonthDto> sellingByMonths) {
		this.sellingByMonths = sellingByMonths;
	}

	public List<IncomeStatementByMonthDto> getGenAndAdminByMonths() {
		return genAndAdminByMonths;
	}

	public void setGenAndAdminByMonths(List<IncomeStatementByMonthDto> genAndAdminByMonths) {
		this.genAndAdminByMonths = genAndAdminByMonths;
	}

	public Double getSalesTotalCentral() {
		return salesTotalCentral;
	}

	public void setSalesTotalCentral(Double salesTotalCentral) {
		this.salesTotalCentral = salesTotalCentral;
	}

	public Double getSalesTotalNSB3() {
		return salesTotalNSB3;
	}

	public void setSalesTotalNSB3(Double salesTotalNSB3) {
		this.salesTotalNSB3 = salesTotalNSB3;
	}

	public Double getSalesTotalNSB4() {
		return salesTotalNSB4;
	}

	public void setSalesTotalNSB4(Double salesTotalNSB4) {
		this.salesTotalNSB4 = salesTotalNSB4;
	}

	public Double getSalesTotalNSB5() {
		return salesTotalNSB5;
	}

	public void setSalesTotalNSB5(Double salesTotalNSB5) {
		this.salesTotalNSB5 = salesTotalNSB5;
	}

	public Double getSalesTotalNSB8() {
		return salesTotalNSB8;
	}

	public void setSalesTotalNSB8(Double salesTotalNSB8) {
		this.salesTotalNSB8 = salesTotalNSB8;
	}

	public Double getSalesTotalNSB8A() {
		return salesTotalNSB8A;
	}

	public void setSalesTotalNSB8A(Double salesTotalNSB8A) {
		this.salesTotalNSB8A = salesTotalNSB8A;
	}

	public Double getSalesGrandTotal() {
		return salesGrandTotal;
	}

	public void setSalesGrandTotal(Double salesGrandTotal) {
		this.salesGrandTotal = salesGrandTotal;
	}

	public List<IncomeStatementComparisonDto> getComparisonDtos() {
		return comparisonDtos;
	}

	public void setComparisonDtos(List<IncomeStatementComparisonDto> comparisonDtos) {
		this.comparisonDtos = comparisonDtos;
	}

	@Override
	public String toString() {
		return "IncomeStatementNSBDto [salesTotalCentral=" + salesTotalCentral + ", salesTotalNSB3=" + salesTotalNSB3
				+ ", salesTotalNSB4=" + salesTotalNSB4 + ", salesTotalNSB5=" + salesTotalNSB5 + ", salesTotalNSB8="
				+ salesTotalNSB8 + ", salesTotalNSB8A=" + salesTotalNSB8A + ", salesGrandTotal=" + salesGrandTotal
				+ "]";
	}

	public int getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(int monthCount) {
		this.monthCount = monthCount;
	}

	public int getDivisionCount() {
		return divisionCount;
	}

	public void setDivisionCount(int divisionCount) {
		this.divisionCount = divisionCount;
	}
}
