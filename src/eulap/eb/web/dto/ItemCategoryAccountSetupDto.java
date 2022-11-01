package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;

/**
 * Item Category Account Setup Dto

 */
public class ItemCategoryAccountSetupDto {
	private String companyName;
	private String hdnId;
	private List<ItemCategoryAccountSetup> accountSetups;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<ItemCategoryAccountSetup> getAccountSetups() {
		return accountSetups;
	}

	public void setAccountSetups(List<ItemCategoryAccountSetup> accountSetups) {
		this.accountSetups = accountSetups;
	}

	public String getHdnId() {
		return hdnId;
	}

	public void setHdnId(String hdnId) {
		this.hdnId = hdnId;
	}

	@Override
	public String toString() {
		return "ItemCategoryAccountSetupDto [companyName=" + companyName + ", accountSetups=" + accountSetups + "]";
	}

}
