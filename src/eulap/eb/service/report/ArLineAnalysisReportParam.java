package eulap.eb.service.report;

import java.util.Date;
/**
 * A class that handles different parameters in filtering
 * the AR Line Analysis report.

 */
public class ArLineAnalysisReportParam {
	private Integer companyId;
	private Integer arLineSetupId;
	private Integer unitOfMeasureId;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private Date glDateFrom;
	private Date glDateTo;
	private Integer customerId;
	private Integer customerAcctId;
	private Integer sourceId;
	private Integer divisionId;
	private Integer serviceId;

	/**
	 * The selected company Id.
	 */
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * The selected division Id.
	 */
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	/**
	 *	The ar line id.
	 */
	public Integer getArLineSetupId() {
		return arLineSetupId;
	}

	public void setArLineSetupId(Integer arLineSetupId) {
		this.arLineSetupId = arLineSetupId;
	}

	/**
	 *	The unit of measure id.
	 */
	public Integer getUnitOfMeasureId() {
		return unitOfMeasureId;
	}

	public void setUnitOfMeasureId(Integer unitOfMeasureId) {
		this.unitOfMeasureId = unitOfMeasureId;
	}

	/**
	 *	Starting range of transaction date.
	 */
	public Date getTransactionDateFrom() {
		return transactionDateFrom;
	}

	public void setTransactionDateFrom(Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	/**
	 *	Ending range of transaction date.
	 */
	public Date getTransactionDateTo() {
		return transactionDateTo;
	}

	public void setTransactionDateTo(Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

	/**
	 *	Starting range of gl date.
	 */
	public Date getGlDateFrom() {
		return glDateFrom;
	}

	public void setGlDateFrom(Date glDateFrom) {
		this.glDateFrom = glDateFrom;
	}

	/**
	 *	Ending range of transaction date.
	 */
	public Date getGlDateTo() {
		return glDateTo;
	}

	public void setGlDateTo(Date glDateTo) {
		this.glDateTo = glDateTo;
	}

	/**
	 *	The customer id.
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 *	The customer account id.
	 */
	public Integer getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(Integer customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	/**
	 * The source id.
	 */
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 *  The service id
	 */
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArLineAnalysisReportParam [companyId=")
				.append(companyId).append(", arLineSetupId=").append(arLineSetupId)
				.append(", unitOfMeasureId=").append(unitOfMeasureId)
				.append(", transactionDateFrom=").append(transactionDateFrom)
				.append(", transactionDateTo=").append(transactionDateTo)
				.append(", glDateFrom=").append(glDateFrom)
				.append(", glDateTo=").append(glDateTo).append(", customerId=")
				.append(customerId).append(", customerAcctId=")
				.append(customerAcctId).append(", sourceId=").append(sourceId)
				.append(", divisionId=").append(divisionId).append(", serviceId=").append(serviceId)
				.append("]");
		return builder.toString();
	}
}
