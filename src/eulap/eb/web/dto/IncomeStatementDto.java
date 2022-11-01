package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.List;

import eulap.eb.domain.hibernate.Company;

/**
 * Income Statement DTO.


 * 
 */
public class IncomeStatementDto {
	private List<DivisionDto> divisions;
	private List<ISBSTypeDto> revenues;
	private List<ISBSTypeDto> directCost;
	private List<ISBSTypeDto> operatingExpense;
	private List<ISBSTypeDto> otherIncome;
	private List<ISBSTypeDto> otherExpense;
	private List<ISBSTotalDto> grossProfit;
	private List<ISBSTotalDto> operatingIncome;
	private List<ISBSTotalDto> netIncome;
	private List<Company> companies;

	public IncomeStatementDto() {}

	public IncomeStatementDto(boolean isInitList) {
		if (isInitList) {
			this.setRevenues(new ArrayList<>());
			this.setDirectCost(new ArrayList<>());
			this.setOperatingExpense(new ArrayList<>());
			this.setOtherIncome(new ArrayList<>());
			this.setOtherExpense(new ArrayList<>());
			this.setGrossProfit(new ArrayList<>());
			this.setOperatingIncome(new ArrayList<>());
			this.setNetIncome(new ArrayList<>());
		}
	}

	public List<DivisionDto> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<DivisionDto> divisions) {
		this.divisions = divisions;
	}

	public List<ISBSTypeDto> getRevenues() {
		return revenues;
	}

	public void setRevenues(List<ISBSTypeDto> revenues) {
		this.revenues = revenues;
	}

	public List<ISBSTypeDto> getDirectCost() {
		return directCost;
	}

	public void setDirectCost(List<ISBSTypeDto> directCost) {
		this.directCost = directCost;
	}

	public List<ISBSTypeDto> getOperatingExpense() {
		return operatingExpense;
	}

	public void setOperatingExpense(List<ISBSTypeDto> operatingExpense) {
		this.operatingExpense = operatingExpense;
	}

	public List<ISBSTypeDto> getOtherIncome() {
		return otherIncome;
	}

	public void setOtherIncome(List<ISBSTypeDto> otherIncome) {
		this.otherIncome = otherIncome;
	}

	public List<ISBSTypeDto> getOtherExpense() {
		return otherExpense;
	}

	public void setOtherExpense(List<ISBSTypeDto> otherExpense) {
		this.otherExpense = otherExpense;
	}

	public List<ISBSTotalDto> getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(List<ISBSTotalDto> grossProfit) {
		this.grossProfit = grossProfit;
	}

	public List<ISBSTotalDto> getOperatingIncome() {
		return operatingIncome;
	}

	public void setOperatingIncome(List<ISBSTotalDto> operatingIncome) {
		this.operatingIncome = operatingIncome;
	}

	public List<ISBSTotalDto> getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(List<ISBSTotalDto> netIncome) {
		this.netIncome = netIncome;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
}
