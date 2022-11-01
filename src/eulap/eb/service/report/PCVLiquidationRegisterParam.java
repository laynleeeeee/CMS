package eulap.eb.service.report;

import java.util.Date;


/**
 * A class that handles parameters in filtering Petty Cash Voucher Liquidation Register.

 */
public class PCVLiquidationRegisterParam {


	private Integer companyId;
	private Integer divisionId;
	private Integer custodianId;
	private String requestorName;
	private Date dateFrom;
	private Date dateTo;
	private Integer transactionStatusId;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getCustodianId() {
		return custodianId;
	}

	public void setCustodianId(Integer custodianId) {
		this.custodianId = custodianId;
	}

	public String getRequestorName() {
		return requestorName;
	}

	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
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

	public Integer getTransactionStatusId() {
		return transactionStatusId;
	}

	public void setTransactionStatusId(Integer transactionStatusId) {
		this.transactionStatusId = transactionStatusId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionAgingParam [companyId=").append(companyId).append(", divisionId=")
				.append(divisionId).append(", custodianId=").append(custodianId).append(", requestorName=")
				.append(requestorName).append(", dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo)
				.append(", transactionStatusId=").append(transactionStatusId).append("]");
		return builder.toString();
	}

}
