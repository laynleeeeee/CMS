package bp.web.dto.ar;

import java.util.Collection;

import eulap.common.util.Page;
import eulap.eb.domain.hibernate.Company;

public class CompanyDto {
	private Collection<Company> companies;
	private String companyName;
	private Company company;
	private Page<Company> page;
	
	public void setPage(Page<Company> page) {
		this.page = page;
	}

	public Page<Company> getPage() {
		return page;
	}
	public void setCompanies(Collection<Company> companies) {
		this.companies = companies;
	}
	
	public Collection<Company> getCompanies() {
		return companies;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserGroupName() {
		return companyName;
	}

	public void setUserGroup(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}

	@Override
	public String toString() {
		return "CompanyDto [company=" + company + ", companyName="
				+ companyName + ", companies=" + companies + ", page=" + page
				+ "]";
	}
}
