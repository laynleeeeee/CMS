package eulap.eb.web.dto;


import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.NormalBalance;
/**
 * Account balances dto.

 */

public class AccountBalancesDto{
	private Account account;
	private Integer divisionId;
	private double debit;
	private double credit;
	private Integer accountTypeId;

	private AccountBalancesDto (Account account, double debit, double credit, Integer accountTypeId) {
		this.account = account;
		this.debit = debit;
		this.credit = credit;
		this.accountTypeId = accountTypeId;
	}

	public static AccountBalancesDto getInstance (Account account, double debit, double credit, Integer accountTypeId) {
		return new AccountBalancesDto(account, debit, credit, accountTypeId);
	}

	public static AccountBalancesDto getInstance (Account account, int divisionId, double debit, double credit, Integer accountTypeId) {
		AccountBalancesDto dto = new AccountBalancesDto(account, debit, credit, accountTypeId);
		dto.setDivisionId(divisionId);
		return dto; 
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	/**
	 * Get the account name
	 * @return The account name
	 */
	public String getAccountName () {
		return account.getAccountName();
	}

	/**
	 * Get the account number
	 * @return The account number
	 */
	public String getNumber () {
		return account.getNumber();
	}

	/**
	 * Get the debit amount of the account
	 * @return The debit amount
	 */
	public double getDebit() {
		return debit;
	}

	/**
	 * Get the credit amount of the account
	 * @return The credit amount
	 */
	public double getCredit() {
		return credit;
	}

	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	/**
	 * Get the balance.
	 * @return The balance.
	 */
	public double getBalance () {
		if(account != null && account.getAccountType() != null) {
			if (account.getAccountType().getNormalBalance().getId() == NormalBalance.CREDIT) {
				//Credit normal balance = credit - debit
				return credit - debit;
			}
			//Debit normal balance = else
			return debit - credit;
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountBalancesDto [account=").append(account).append(", divisionId=").append(divisionId)
				.append(", debit=").append(debit).append(", credit=").append(credit).append(", accountTypeId=")
				.append(accountTypeId).append("]");
		return builder.toString();
	}
}
