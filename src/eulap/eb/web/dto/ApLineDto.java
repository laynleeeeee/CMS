package eulap.eb.web.dto;

/**
 * AP Line DTO

 *
 */
public class ApLineDto {
	private String accountNo;
	private String accountDescription;
	private Double debit;
	private Double credit;

	public ApLineDto(String accountNo, String accountDescription,
			Double debit, Double credit) {
		this.accountNo = accountNo;
		this.accountDescription = accountDescription;
		this.debit = debit;
		this.credit = credit;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
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

	@Override
	public String toString() {
		return "ApLineDto [accountNo=" + accountNo + ", accountDescription="
				+ accountDescription + ", debit=" + debit + ", credit="
				+ credit + "]";
	}
}
