package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles different parameters in filtering the Daily Petty Cash
 * Fund Report.
 *

 */

public class DailyPettyCashFundReportParam {
	private int companyId;
	private int divisionId;
	private int custodianId;
	private int transactionStatusId;
	private Date date;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public int getCustodianId() {
		return custodianId;
	}

	public void setCustodianId(int custodianId) {
		this.custodianId = custodianId;
	}

	public int getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(int transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArTransactionRegisterParam [companyId=").append(companyId).append(", divisionId=")
		.append(divisionId).append(", custodianId=").append(custodianId).append(", transactionStatusId=")
		.append(transactionStatusId).append(", date=").append(date).append("]");
		return builder.toString();
		}
}