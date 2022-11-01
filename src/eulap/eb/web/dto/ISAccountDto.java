package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.NormalBalance;

/**
 * Income statement account dto.

 * 
 */
public class ISAccountDto {
	private Integer accountId;
	private String accountName;
	private Double debit;
	private Double credit;
	private Double amount;

	public ISAccountDto(Integer accountId, String accountName, Double debit,
			Double credit) {
		this.accountId = accountId;
		this.accountName = accountName;
		this.debit = debit;
		this.credit = credit;
	}

	/**
	 * Get the instance of this account DTO.
	 * @param accountBalances
	 * @param accountId
	 * @return
	 */
	public static ISAccountDto getInstance(
			List<AccountBalancesDto> accountBalances, int accountId) {
		for (AccountBalancesDto ab : accountBalances) {
			if (accountId == ab.getAccount().getId()) {
				ISAccountDto isAccountDto = new ISAccountDto(ab.getAccount()
						.getId(), ab.getAccountName(), ab.getDebit(), ab.getCredit());
				return isAccountDto;
			}
		}
		return null;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * Computes the amount based on the normal balance of the account type.
	 * @param accountType The account type.
	 * @return The computed amount.
	 */
	public double getAmount(AccountType accountType) {
		int normalBalanceId = accountType.getNormalBalanceId();
		if (normalBalanceId == NormalBalance.DEBIT)
			return debit - credit;
		else
			return credit - debit;
	}

	@Override
	public String toString() {
		return "ISAccountDto [accountId=" + accountId + ", accountName="
				+ accountName + ", debit=" + debit + ", credit=" + credit
				+ ", amount=" + amount + "]";
	}
}
