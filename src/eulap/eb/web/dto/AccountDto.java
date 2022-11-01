package eulap.eb.web.dto;

import java.util.List;

/**
 * Data transfer object class for {@code Account}

 */

public class AccountDto {
	private Integer id;
	private String number;
	private String accountName;
	private String description;
	private Integer paId;
	private String atName;
	private String paName;
	private boolean active;
	private boolean isMainParent;
	private boolean isLastLevel;
	private boolean isInCombination;
	private int level;
	private double totalAmount;
	private String strAcctId;
	private AccountDto parentAccount;
	private List<AccountDto> childrenAccount;
	private Double budgetAmount;

	public static final int MAIN_LEVEL = 1;
	public static final int LEVEL_2 = 2;
	public static final int LEVEL_3 = 3;
	public static final int LEVEL_4 = 4;

	/**
	 * Create instance of {@code AccountDto}
	 */
	public static AccountDto getInstanceOf(Integer id, String number, String accountName, String description, Integer paId,
			String atName, String paName, boolean active, boolean isLastLevel) {
		AccountDto dto = new AccountDto();
		dto.id = id;
		dto.number = number;
		dto.accountName = accountName;
		dto.description = description;
		dto.paId = paId;
		dto.atName = atName;
		dto.paName = paName;
		dto.active = active;
		dto.isLastLevel = isLastLevel;
		return dto;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPaId() {
		return paId;
	}

	public void setPaId(Integer paId) {
		this.paId = paId;
	}

	public String getAtName() {
		return atName;
	}

	public void setAtName(String atName) {
		this.atName = atName;
	}

	public String getPaName() {
		return paName;
	}

	public void setPaName(String paName) {
		this.paName = paName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isMainParent() {
		return isMainParent;
	}

	public void setMainParent(boolean isMainParent) {
		this.isMainParent = isMainParent;
	}

	public boolean isLastLevel() {
		return isLastLevel;
	}

	public void setLastLevel(boolean isLastLevel) {
		this.isLastLevel = isLastLevel;
	}

	public boolean isInCombination() {
		return isInCombination;
	}

	public void setInCombination(boolean isInCombination) {
		this.isInCombination = isInCombination;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStrAcctId() {
		return strAcctId;
	}

	public void setStrAcctId(String strAcctId) {
		this.strAcctId = strAcctId;
	}

	public AccountDto getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(AccountDto parentAccount) {
		this.parentAccount = parentAccount;
	}

	public List<AccountDto> getChildrenAccount() {
		return childrenAccount;
	}

	public void setChildrenAccount(List<AccountDto> childrenAccount) {
		this.childrenAccount = childrenAccount;
	}

	public Double getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(Double budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	@Override
	public String toString() {
		return "AccountDto [id=" + id + ", number=" + number + ", accountName=" + accountName + ", description="
				+ description + ", paId=" + paId + ", atName=" + atName + ", paName=" + paName + ", active=" + active
				+ ", isMainParent=" + isMainParent + ", isLastLevel=" + isLastLevel + ", isInCombination="
				+ isInCombination + ", level=" + level + ", totalAmount=" + totalAmount + ", strAcctId=" + strAcctId
				+ ", parentAccount=" + parentAccount + ", childrenAccount=" + childrenAccount
				+ ", budgetAmount=" + budgetAmount + "]";
	}
}
