package eulap.eb.web.dto;

import java.util.Date;

/**
 * Journal Entries Register Dto.

 * 
 */
public class JournalEntriesRegisterDto {
	private Date glDate;
	private String source;
	private String refNo;
	private String accountNo;
	private String accountDesc;
	private String description;
	private Double debit;
	private Double credit;
	private String status;
	private String division;
	private String customerPo;

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getCustomerPo() {
		return customerPo;
	}

	public void setCustomerPo(String customerPo) {
		this.customerPo = customerPo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JournalEntriesRegisterDto [glDate=").append(glDate).append(", source=").append(source)
				.append(", refNo=").append(refNo).append(", accountNo=").append(accountNo).append(", accountDesc=")
				.append(accountDesc).append(", description=").append(description).append(", debit=").append(debit)
				.append(", credit=").append(credit).append(", status=").append(status).append(", division=")
				.append(division).append(", customerPo=").append(customerPo).append("]");
		return builder.toString();
	}
}