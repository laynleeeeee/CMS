package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.List;

import eulap.eb.domain.hibernate.Company;

/**
 * Balance Sheet DTO.


 * 
 */
public class BalanceSheetDto {
	private List<DivisionDto> divisions;
	private List<ISBSTypeDto> currentAssets;
	private List<ISBSTypeDto> nonCurrentAssets;
	private List<ISBSTypeDto> currentLiabilities;
	private List<ISBSTypeDto> nonCurrentLiabilities;
	private List<ISBSTypeDto> equities;
	private List<ISBSTotalDto> assetsTotal;
	private List<ISBSTotalDto> liabilitiesTotal;
	private List<ISBSTotalDto> equityLiabilitiesTotal;
	private List<Company> companies;

	public BalanceSheetDto() {}

	public BalanceSheetDto(boolean isInitList) {
		if (isInitList) {
			this.setCurrentAssets(new ArrayList<>());
			this.setNonCurrentAssets(new ArrayList<>());
			this.setCurrentLiabilities(new ArrayList<>());
			this.setNonCurrentLiabilities(new ArrayList<>());
			this.setEquities(new ArrayList<>());
			this.setAssetsTotal(new ArrayList<>());
			this.setLiabilitiesTotal(new ArrayList<>());
			this.setEquityLiabilitiesTotal(new ArrayList<>());
		}
	}

	public List<DivisionDto> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<DivisionDto> divisions) {
		this.divisions = divisions;
	}

	public List<ISBSTypeDto> getCurrentAssets() {
		return currentAssets;
	}

	public void setCurrentAssets(List<ISBSTypeDto> currentAssets) {
		this.currentAssets = currentAssets;
	}

	public List<ISBSTypeDto> getNonCurrentAssets() {
		return nonCurrentAssets;
	}

	public void setNonCurrentAssets(List<ISBSTypeDto> nonCurrentAssets) {
		this.nonCurrentAssets = nonCurrentAssets;
	}

	public List<ISBSTypeDto> getCurrentLiabilities() {
		return currentLiabilities;
	}

	public void setCurrentLiabilities(List<ISBSTypeDto> currentLiabilities) {
		this.currentLiabilities = currentLiabilities;
	}

	public List<ISBSTypeDto> getNonCurrentLiabilities() {
		return nonCurrentLiabilities;
	}

	public void setNonCurrentLiabilities(List<ISBSTypeDto> nonCurrentLiabilities) {
		this.nonCurrentLiabilities = nonCurrentLiabilities;
	}

	public List<ISBSTypeDto> getEquities() {
		return equities;
	}

	public void setEquities(List<ISBSTypeDto> equities) {
		this.equities = equities;
	}

	public List<ISBSTotalDto> getAssetsTotal() {
		return assetsTotal;
	}

	public void setAssetsTotal(List<ISBSTotalDto> assetsTotal) {
		this.assetsTotal = assetsTotal;
	}

	public List<ISBSTotalDto> getLiabilitiesTotal() {
		return liabilitiesTotal;
	}

	public void setLiabilitiesTotal(List<ISBSTotalDto> liabilitiesTotal) {
		this.liabilitiesTotal = liabilitiesTotal;
	}

	public List<ISBSTotalDto> getEquityLiabilitiesTotal() {
		return equityLiabilitiesTotal;
	}

	public void setEquityLiabilitiesTotal(List<ISBSTotalDto> equityLiabilitiesTotal) {
		this.equityLiabilitiesTotal = equityLiabilitiesTotal;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
}
