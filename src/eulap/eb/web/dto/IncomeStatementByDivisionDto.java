package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Income Statement By Division DTO.

 * 
 */
public class IncomeStatementByDivisionDto {
	private List<DivisionDto> divisions;
	private List<ISBSTypeDto> sales;
	private List<ISBSTotalDto> saleTotal;
	private List<ISBSTypeDto> productionCosts;
	private List<ISBSTotalDto> productionCostsTotalDirect;
	private List<ISBSTotalDto> productionCostsTotal;
	private List<ISBSTotalDto> grossIncome;
	private List<ISBSTypeDto> sellingExpenses;
	private List<ISBSTotalDto> sellingExpensesTotal;
	private List<ISBSTypeDto> genAndAdminExpenses;
	private List<ISBSTotalDto> genAndAdminExpensesTotal;
	private List<ISBSTotalDto> operatingExpenses;
	private List<ISBSTypeDto> otherIncomeExpenses;
	private List<ISBSTypeDto> otherIncome;
	private List<ISBSTotalDto> otherIncomeExpensesTotal;
	private List<ISBSTotalDto> netIncomeBefore;
	private List<ISBSTypeDto> interestExpenses;
	private List<ISBSTotalDto> interestExpensesTotal;
	private List<ISBSTypeDto> depreciations;
	private List<ISBSTotalDto> depreciationsTotal;
	private List<ISBSTotalDto> netIncomeAfter;
	private List<ISBSTotalDto> unliquidatedExpenseTotal;
	private List<ISBSTotalDto> netIncomeAsOf;
	private Double salesTotalCentral;
	private Double salesTotalNSB3;
	private Double salesTotalNSB4;
	private Double salesTotalNSB5;
	private Double salesTotalNSB8;
	private Double salesTotalNSB8A;
	private Double salesGrandTotal;

	public IncomeStatementByDivisionDto() {}

	public IncomeStatementByDivisionDto(boolean isInitList) {
		if (isInitList) {
			this.setSales(new ArrayList<>());
			this.setProductionCosts(new ArrayList<>());
			this.setSellingExpenses(new ArrayList<>());
			this.setGenAndAdminExpenses(new ArrayList<>());
			this.setOtherIncomeExpenses(new ArrayList<>());
			this.setInterestExpenses(new ArrayList<>());
			this.setDepreciations(new ArrayList<>());
		}
	}

	public List<DivisionDto> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<DivisionDto> divisions) {
		this.divisions = divisions;
	}

	public List<ISBSTypeDto> getSales() {
		return sales;
	}

	public void setSales(List<ISBSTypeDto> sales) {
		this.sales = sales;
	}

	public List<ISBSTypeDto> getProductionCosts() {
		return productionCosts;
	}

	public void setProductionCosts(List<ISBSTypeDto> productionCosts) {
		this.productionCosts = productionCosts;
	}

	public List<ISBSTotalDto> getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(List<ISBSTotalDto> grossIncome) {
		this.grossIncome = grossIncome;
	}

	public List<ISBSTypeDto> getSellingExpenses() {
		return sellingExpenses;
	}

	public void setSellingExpenses(List<ISBSTypeDto> sellingExpenses) {
		this.sellingExpenses = sellingExpenses;
	}

	public List<ISBSTypeDto> getGenAndAdminExpenses() {
		return genAndAdminExpenses;
	}

	public void setGenAndAdminExpenses(List<ISBSTypeDto> genAndAdminExpenses) {
		this.genAndAdminExpenses = genAndAdminExpenses;
	}

	public List<ISBSTotalDto> getOperatingExpenses() {
		return operatingExpenses;
	}

	public void setOperatingExpenses(List<ISBSTotalDto> operatingExpenses) {
		this.operatingExpenses = operatingExpenses;
	}

	public List<ISBSTypeDto> getOtherIncomeExpenses() {
		return otherIncomeExpenses;
	}

	public void setOtherIncomeExpenses(List<ISBSTypeDto> otherIncomeExpenses) {
		this.otherIncomeExpenses = otherIncomeExpenses;
	}

	public List<ISBSTotalDto> getOtherIncomeExpensesTotal() {
		return otherIncomeExpensesTotal;
	}

	public void setOtherIncomeExpensesTotal(List<ISBSTotalDto> otherIncomeExpensesTotal) {
		this.otherIncomeExpensesTotal = otherIncomeExpensesTotal;
	}

	public List<ISBSTotalDto> getNetIncomeBefore() {
		return netIncomeBefore;
	}

	public void setNetIncomeBefore(List<ISBSTotalDto> netIncomeBefore) {
		this.netIncomeBefore = netIncomeBefore;
	}

	public List<ISBSTypeDto> getInterestExpenses() {
		return interestExpenses;
	}

	public void setInterestExpenses(List<ISBSTypeDto> interestExpenses) {
		this.interestExpenses = interestExpenses;
	}

	public List<ISBSTypeDto> getDepreciations() {
		return depreciations;
	}

	public void setDepreciations(List<ISBSTypeDto> depreciations) {
		this.depreciations = depreciations;
	}

	public List<ISBSTotalDto> getNetIncomeAfter() {
		return netIncomeAfter;
	}

	public void setNetIncomeAfter(List<ISBSTotalDto> netIncomeAfter) {
		this.netIncomeAfter = netIncomeAfter;
	}

	public List<ISBSTotalDto> getNetIncomeAsOf() {
		return netIncomeAsOf;
	}

	public void setNetIncomeAsOf(List<ISBSTotalDto> netIncomeAsOf) {
		this.netIncomeAsOf = netIncomeAsOf;
	}

	public List<ISBSTotalDto> getSaleTotal() {
		return saleTotal;
	}

	public void setSaleTotal(List<ISBSTotalDto> saleTotal) {
		this.saleTotal = saleTotal;
	}

	public List<ISBSTotalDto> getProductionCostsTotal() {
		return productionCostsTotal;
	}

	public void setProductionCostsTotal(List<ISBSTotalDto> productionCostsTotal) {
		this.productionCostsTotal = productionCostsTotal;
	}

	public List<ISBSTotalDto> getSellingExpensesTotal() {
		return sellingExpensesTotal;
	}

	public void setSellingExpensesTotal(List<ISBSTotalDto> sellingExpensesTotal) {
		this.sellingExpensesTotal = sellingExpensesTotal;
	}

	public List<ISBSTotalDto> getGenAndAdminExpensesTotal() {
		return genAndAdminExpensesTotal;
	}

	public void setGenAndAdminExpensesTotal(List<ISBSTotalDto> genAndAdminExpensesTotal) {
		this.genAndAdminExpensesTotal = genAndAdminExpensesTotal;
	}

	public List<ISBSTotalDto> getInterestExpensesTotal() {
		return interestExpensesTotal;
	}

	public void setInterestExpensesTotal(List<ISBSTotalDto> interestExpensesTotal) {
		this.interestExpensesTotal = interestExpensesTotal;
	}

	public List<ISBSTotalDto> getDepreciationsTotal() {
		return depreciationsTotal;
	}

	public void setDepreciationsTotal(List<ISBSTotalDto> depreciationsTotal) {
		this.depreciationsTotal = depreciationsTotal;
	}

	public List<ISBSTotalDto> getUnliquidatedExpenseTotal() {
		return unliquidatedExpenseTotal;
	}

	public void setUnliquidatedExpenseTotal(List<ISBSTotalDto> unliquidatedExpenseTotal) {
		this.unliquidatedExpenseTotal = unliquidatedExpenseTotal;
	}

	public List<ISBSTypeDto> getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(List<ISBSTypeDto> otherIncome) {
		this.otherIncome = otherIncome;
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

	public List<ISBSTotalDto> getProductionCostsTotalDirect() {
		return productionCostsTotalDirect;
	}

	public void setProductionCostsTotalDirect(List<ISBSTotalDto> productionCostsTotalDirect) {
		this.productionCostsTotalDirect = productionCostsTotalDirect;
	}
}
