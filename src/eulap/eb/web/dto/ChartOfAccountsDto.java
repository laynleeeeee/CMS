package eulap.eb.web.dto;

import java.util.List;

/**
 * DTO for chart of accounts.

 *
 */
public class ChartOfAccountsDto {
	private String accountType;
	private List<AccountDto> accountDtos;

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public List<AccountDto> getAccountDtos() {
		return accountDtos;
	}

	public void setAccountDtos(List<AccountDto> accountDtos) {
		this.accountDtos = accountDtos;
	}

	@Override
	public String toString() {
		return "ChartOfAccountsDto [accountType=" + accountType + ", accountDtos=" + accountDtos + "]";
	}
}
