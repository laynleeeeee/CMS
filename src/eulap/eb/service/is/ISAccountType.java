package eulap.eb.service.is;

import java.util.List;

import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.web.dto.ISAccountDto;

/**
 * Income statement account type
 * 

 * 
 */
public class ISAccountType {
	private Integer accountTypeId;
	private boolean isPositive;
	private boolean asOfBalance;
	private Integer sequenceOrder;
	private AccountType accountType;
	private List<ISAccountDto> accounts;
	private double amount;

	private ISAccountType () {
		// use only for IncomeStatementConfParser
	}
	
	/**
	 * Get the instance of the account type.
	 * @param accountTypeId The account type id.
	 * @param isPositive true if positive, otherwise negative.  
	 * @param sequenceNumber The sequence number of this account type. 
	 */
	public static ISAccountType getInstanceOf (int accountTypeId, boolean isPositive, 
			boolean asOfBalance, int sequenceNumber) {
		ISAccountType accountType = new ISAccountType();
		accountType.accountTypeId = accountTypeId;
		accountType.isPositive = isPositive;
		accountType.asOfBalance = asOfBalance;
		accountType.sequenceOrder = sequenceNumber;
		return accountType;
		
	}
	
	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public boolean isPositive() {
		return isPositive;
	}
	
	public boolean isAsOfBalance() {
		return asOfBalance;
	}
	
	public Integer getSequenceOrder() {
		return sequenceOrder;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public List<ISAccountDto> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<ISAccountDto> accounts) {
		this.accounts = accounts;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "ISAccountType [accountTypeId=" + accountTypeId
				+ ", isPositive=" + isPositive + ", sequenceOrder="
				+ sequenceOrder + ", accountType=" + accountType
				+ ", accounts=" + accounts + ", amount=" + amount + "]";
	}
}
