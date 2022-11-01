package eulap.eb.web.dto;

import java.util.List;

/**
 * General Ledger Dto.

 * 
 */
public class GeneralLedgerListingDto {
	private String accountName;
	private List<JournalEntriesRegisterDto> entriesRegisterDtos;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public List<JournalEntriesRegisterDto> getEntriesRegisterDtos() {
		return entriesRegisterDtos;
	}

	public void setEntriesRegisterDtos(List<JournalEntriesRegisterDto> entriesRegisterDtos) {
		this.entriesRegisterDtos = entriesRegisterDtos;
	}

	@Override
	public String toString() {
		return "GeneralLedgerListingDto [accountName=" + accountName + ", entriesRegisterDtos=" + entriesRegisterDtos
				+ "]";
	}
}
