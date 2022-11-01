package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles the different parameters in filtering statement of account.

 */
public class StatementOfAccountParam {
	private int companyId;
	private int customerId;
	private int customerAcctId;
	private Date dateFrom;
	private Date dateTo;
	private int divisionId;
	private int currencyId;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(int customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public int getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}

	@Override
	public String toString() {
		return "StatementOfAccountParam [companyId=" + companyId + ", customerId=" + customerId + ", customerAcctId="
				+ customerAcctId + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", divisionId=" + divisionId
				+ ", currencyId=" + currencyId + "]";
	}
}
