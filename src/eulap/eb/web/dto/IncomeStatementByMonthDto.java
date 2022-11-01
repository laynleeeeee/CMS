package eulap.eb.web.dto;

import java.util.List;

/**
 * Income Statement By Month DTO.

 * 
 */
public class IncomeStatementByMonthDto {
	private List<TimePeriodMonth> months;
	private List<ISBSTotalDto> productionCostTotal;

	// Payroll related cost
	private List<ISBSTotalDto> payrollRelatedCost;
	private List<ISBSTotalDto> payrollRelatedCostOT;
	private List<ISBSTotalDto> payroll;

	// Production Cost
	private List<ISBSTypeDto> directMaterials;
	private List<ISBSTypeDto> directLabors;
	private List<ISBSTypeDto> manufacturingOverhead;
	private List<ISBSTypeDto> mfgOverhead;

	private List<ISByMonthDto> byMonthDtos;
	private List<ISBSTotalDto> isbsTotalDtos;

	private int reportType;
	public static final int PRODUCTION_REPORT_TYPE_ID = 1;
	public static final int SELLING_REPORT_TYPE_ID = 2;
	public static final int GEN_AND_ADMIN_REPORT_TYPE_ID = 3;

	public List<TimePeriodMonth> getMonths() {
		return months;
	}

	public void setMonths(List<TimePeriodMonth> months) {
		this.months = months;
	}

	// Start of Production Cost
	public List<ISBSTypeDto> getDirectMaterials() {
		return directMaterials;
	}

	public void setDirectMaterials(List<ISBSTypeDto> directMaterials) {
		this.directMaterials = directMaterials;
	}

	public List<ISBSTypeDto> getDirectLabors() {
		return directLabors;
	}

	public void setDirectLabors(List<ISBSTypeDto> directLabors) {
		this.directLabors = directLabors;
	}

	public List<ISBSTypeDto> getManufacturingOverhead() {
		return manufacturingOverhead;
	}

	public void setManufacturingOverhead(List<ISBSTypeDto> manufacturingOverhead) {
		this.manufacturingOverhead = manufacturingOverhead;
	}

	public List<ISBSTypeDto> getMfgOverhead() {
		return mfgOverhead;
	}

	public void setMfgOverhead(List<ISBSTypeDto> mfgOverhead) {
		this.mfgOverhead = mfgOverhead;
	}

	public List<ISBSTotalDto> getPayrollRelatedCost() {
		return payrollRelatedCost;
	}

	public void setPayrollRelatedCost(List<ISBSTotalDto> payrollRelatedCost) {
		this.payrollRelatedCost = payrollRelatedCost;
	}

	public List<ISBSTotalDto> getPayrollRelatedCostOT() {
		return payrollRelatedCostOT;
	}

	public void setPayrollRelatedCostOT(List<ISBSTotalDto> payrollRelatedCostOT) {
		this.payrollRelatedCostOT = payrollRelatedCostOT;
	}

	public List<ISBSTotalDto> getPayroll() {
		return payroll;
	}

	public void setPayroll(List<ISBSTotalDto> payroll) {
		this.payroll = payroll;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public List<ISBSTotalDto> getProductionCostTotal() {
		return productionCostTotal;
	}

	public void setProductionCostTotal(List<ISBSTotalDto> productionCostTotal) {
		this.productionCostTotal = productionCostTotal;
	}

	public List<ISByMonthDto> getByMonthDtos() {
		return byMonthDtos;
	}

	public void setByMonthDtos(List<ISByMonthDto> byMonthDtos) {
		this.byMonthDtos = byMonthDtos;
	}

	public List<ISBSTotalDto> getIsbsTotalDtos() {
		return isbsTotalDtos;
	}

	public void setIsbsTotalDtos(List<ISBSTotalDto> isbsTotalDtos) {
		this.isbsTotalDtos = isbsTotalDtos;
	}

}
