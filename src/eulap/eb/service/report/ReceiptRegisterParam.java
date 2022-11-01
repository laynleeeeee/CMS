package eulap.eb.service.report;

import java.util.Date;
/**
 * A class that contains the different parameters in filtering the receipt register report.

 */
public class ReceiptRegisterParam {
	private int sourceId;
	private int companyId;
	private int receiptTypeId;
	private int receiptMethodId;
	private int customerId;
	private int customerAcctId;
	private String receiptNo;
	private int divisionId;
	private Date receiptDateFrom;
	private Date receiptDateTo;
	private Date maturityDateFrom;
	private Date maturityDateTo;
	private Double amountFrom;
	private Double amountTo;
	private int wfStatusId;
	private int appliedStatusId;





	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * The selected company Id.
	 */
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 *	The receipt type id.
	 */
	public int getReceiptTypeId() {
		return receiptTypeId;
	}

	public void setReceiptTypeId(int receiptTypeId) {
		this.receiptTypeId = receiptTypeId;
	}

	/**
	 *	The receipt method id.
	 */
	public int getReceiptMethodId() {
		return receiptMethodId;
	}

	public void setReceiptMethodId(int receiptMethodId) {
		this.receiptMethodId = receiptMethodId;
	}

	/**
	 *	The customer id.
	 */
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	/**
	 *	The customer account id.
	 */
	public int getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(int customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	/**
	 *	The receipt receipt no.
	 */
	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	/**
	 *	Starting range of receipt date.
	 */
	public Date getReceiptDateFrom() {
		return receiptDateFrom;
	}

	public void setReceiptDateFrom(Date receiptDateFrom) {
		this.receiptDateFrom = receiptDateFrom;
	}

	/**
	 *	Ending range of receipt date.
	 */
	public Date getReceiptDateTo() {
		return receiptDateTo;
	}

	public void setReceiptDateTo(Date receiptDateTo) {
		this.receiptDateTo = receiptDateTo;
	}

	/**
	 *	Starting range of maturity date.
	 */
	public Date getMaturityDateFrom() {
		return maturityDateFrom;
	}

	public void setMaturityDateFrom(Date maturityDateFrom) {
		this.maturityDateFrom = maturityDateFrom;
	}

	/**
	 *	Ending range of maturity date.
	 */
	public Date getMaturityDateTo() {
		return maturityDateTo;
	}

	public void setMaturityDateTo(Date maturityDateTo) {
		this.maturityDateTo = maturityDateTo;
	}
	
	/**
	 *	Starting range of amount.
	 */
	public Double getAmountFrom() {
		return amountFrom;
	}

	public void setAmountFrom(Double amountFrom) {
		this.amountFrom = amountFrom;
	}

	/**
	 *	Ending range of amount.
	 */
	public Double getAmountTo() {
		return amountTo;
	}

	public void setAmountTo(Double amountTo) {
		this.amountTo = amountTo;
	}

	/**
	 *	The form workflow log status id.
	 */
	public int getWfStatusId() {
		return wfStatusId;
	}

	public void setWfStatusId(int wfStatusId) {
		this.wfStatusId = wfStatusId;
	}

	/**
	 * The applied status id.
	 */
	public int getAppliedStatusId() {
		return appliedStatusId;
	}

	public void setAppliedStatusId(int appliedStatusId) {
		this.appliedStatusId = appliedStatusId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReceiptRegisterParam [sourceId=").append(sourceId).append(", companyId=").append(companyId)
				.append(", receiptTypeId=").append(receiptTypeId).append(", receiptMethodId=").append(receiptMethodId)
				.append(", customerId=").append(customerId).append(", customerAcctId=").append(customerAcctId)
				.append(", receiptNo=").append(receiptNo).append(", divisionId=").append(divisionId)
				.append(", receiptDateFrom=").append(receiptDateFrom).append(", receiptDateTo=").append(receiptDateTo)
				.append(", maturityDateFrom=").append(maturityDateFrom).append(", maturityDateTo=")
				.append(maturityDateTo).append(", amountFrom=").append(amountFrom).append(", amountTo=")
				.append(amountTo).append(", wfStatusId=").append(wfStatusId).append(", appliedStatusId=")
				.append(appliedStatusId).append("]");
		return builder.toString();
	}
}
