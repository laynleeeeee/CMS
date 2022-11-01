package eulap.eb.web.dto;

import java.util.List;

/**
 * Income Statement Comparison Report DTO.

 * 
 */
public class IncomeStatementComparisonDto {
	private double currentYSales;
	private double prevYSales;
	private List<IncomeStatementComparisonDataDto> productionCosts;
	private List<IncomeStatementComparisonDataDto> grossIncome;
	private List<IncomeStatementComparisonDataDto> sellingExpenses;
	private List<IncomeStatementComparisonDataDto> genAndAdminExpenses;
	private List<IncomeStatementComparisonDataDto> operatingExpenses;
	private List<IncomeStatementComparisonDataDto> otheIncome;
	private List<IncomeStatementComparisonDataDto> netIncomeBefore;
	private List<IncomeStatementComparisonDataDto> interestExpenses;
	private List<IncomeStatementComparisonDataDto> depreciation;
	private List<IncomeStatementComparisonDataDto> netIncomeAfter;

	public List<IncomeStatementComparisonDataDto> getProductionCosts() {
		return productionCosts;
	}

	public void setProductionCosts(List<IncomeStatementComparisonDataDto> productionCosts) {
		this.productionCosts = productionCosts;
	}

	public List<IncomeStatementComparisonDataDto> getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(List<IncomeStatementComparisonDataDto> grossIncome) {
		this.grossIncome = grossIncome;
	}

	public List<IncomeStatementComparisonDataDto> getSellingExpenses() {
		return sellingExpenses;
	}

	public void setSellingExpenses(List<IncomeStatementComparisonDataDto> sellingExpenses) {
		this.sellingExpenses = sellingExpenses;
	}

	public List<IncomeStatementComparisonDataDto> getGenAndAdminExpenses() {
		return genAndAdminExpenses;
	}

	public void setGenAndAdminExpenses(List<IncomeStatementComparisonDataDto> genAndAdminExpenses) {
		this.genAndAdminExpenses = genAndAdminExpenses;
	}

	public List<IncomeStatementComparisonDataDto> getOperatingExpenses() {
		return operatingExpenses;
	}

	public void setOperatingExpenses(List<IncomeStatementComparisonDataDto> operatingExpenses) {
		this.operatingExpenses = operatingExpenses;
	}

	public List<IncomeStatementComparisonDataDto> getOtheIncome() {
		return otheIncome;
	}

	public void setOtheIncome(List<IncomeStatementComparisonDataDto> otheIncome) {
		this.otheIncome = otheIncome;
	}

	public List<IncomeStatementComparisonDataDto> getNetIncomeBefore() {
		return netIncomeBefore;
	}

	public void setNetIncomeBefore(List<IncomeStatementComparisonDataDto> netIncomeBefore) {
		this.netIncomeBefore = netIncomeBefore;
	}

	public List<IncomeStatementComparisonDataDto> getInterestExpenses() {
		return interestExpenses;
	}

	public void setInterestExpenses(List<IncomeStatementComparisonDataDto> interestExpenses) {
		this.interestExpenses = interestExpenses;
	}

	public List<IncomeStatementComparisonDataDto> getDepreciation() {
		return depreciation;
	}

	public void setDepreciation(List<IncomeStatementComparisonDataDto> depreciation) {
		this.depreciation = depreciation;
	}

	public List<IncomeStatementComparisonDataDto> getNetIncomeAfter() {
		return netIncomeAfter;
	}

	public void setNetIncomeAfter(List<IncomeStatementComparisonDataDto> netIncomeAfter) {
		this.netIncomeAfter = netIncomeAfter;
	}

	public Double getCurrentYSales() {
		return currentYSales;
	}

	public void setCurrentYSales(Double currentYSales) {
		this.currentYSales = currentYSales;
	}

	public Double getPrevYSales() {
		return prevYSales;
	}

	public void setPrevYSales(Double prevYSales) {
		this.prevYSales = prevYSales;
	}
}
