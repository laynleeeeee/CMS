package eulap.eb.service.report;

import java.util.Date;
/**
 * A class that handles the different parameters in filtering Customer Account History.

 */
public class CustomerAccountHistoryParam {
	private int companyId;
	private int customerAcctId;
	private Date dateFrom;
	private Date dateTo;
	private Integer divisionId;
	private Integer currencyId;


	public int getCompanyId() {
		return companyId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
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

	@Override
	public String toString() {
		return "CustomerAccountHistoryParam [companyId=" + companyId
				+ ", divisionId=" + divisionId +", currencyId=" + currencyId+ ", customerAcctId=" + customerAcctId
				+ ", dateFrom=" + dateFrom + ", dateTo=" + dateTo + "]";
	}
}
