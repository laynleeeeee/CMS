package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles different parameters in filtering the invoice register
 * report.
 * 

 * 
 */
public class InvoiceRegisterParam {
	private int companyId;
	private int invoiceTypeId;
	private int supplierId;
	private int supplierAccountId;
	private int termId;
	private String invoiceNumber;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private Date fromGLDate;
	private Date toGLDate;
	private Date fromDueDate;
	private Date toDueDate;
	private Double fromAmount;
	private Double toAmount;
	private Integer fromSeqNumber;
	private Integer toSeqNumber;
	private int invoiceStatus;
	private int paymentStatus;
	private Date asOfDate;
	private Integer divisionId;

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
	 * The selected invoice type, if the value is -1, this signify that the user selects all invoice type. 
	 */
	public int getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(int invoiceType) {
		this.invoiceTypeId = invoiceType;
	}

	/**
	 * The selected supplier, if the value is -1, this signify that the user selects all supplier.
	 */
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	
	/**
	 * The selected supplier account, if the value is -1, this signify that the user selects all supplier account.
	 */
	public int getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(int supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	/**
	 * The selected term, if the value is -1, this signify that the user selects all option or disregard this filter.
	 */
	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}
	
	/**
	 * The invoice number to be searched.
	 */
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	/**
	 * Starting range of invoice date. 
	 */
	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}

	public void setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
	}

	/**
	 * Ending range of invoice date. 
	 */
	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}

	public void setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
	}

	/**
	 * Starting range of GL Date. 
	 */
	public Date getFromGLDate() {
		return fromGLDate;
	}

	public void setFromGLDate(Date fromGLDate) {
		this.fromGLDate = fromGLDate;
	}

	/**
	 * Ending range of GL date.
	 */
	public Date getToGLDate() {
		return toGLDate;
	}

	public void setToGLDate(Date toGLDate) {
		this.toGLDate = toGLDate;
	}

	/**
	 * Starting range of due date. 
	 */
	public Date getFromDueDate() {
		return fromDueDate;
	}

	public void setFromDueDate(Date fromDueDate) {
		this.fromDueDate = fromDueDate;
	}

	/**
	 * Ending range of due date. 
	 */
	public Date getToDueDate() {
		return toDueDate;
	}

	public void setToDueDate(Date toDueDate) {
		this.toDueDate = toDueDate;
	}

	/**
	 * Starting range of amount
	 */
	public Double getFromAmount() {
		return fromAmount;
	}

	public void setFromAmount(Double fromAmount) {
		this.fromAmount = fromAmount;
	}

	/**
	 * Ending range of amount.
	 */
	public Double getToAmount() {
		return toAmount;
	}


	public void setToAmount(Double toAmount) {
		this.toAmount = toAmount;
	}

	/**
	 * Starting range of sequence number
	 */
	public Integer getFromSeqNumber() {
		return fromSeqNumber;
	}

	public void setFromSeqNumber(Integer fromSeqNumber) {
		this.fromSeqNumber = fromSeqNumber;
	}

	/**
	 * Ending range of sequence number.
	 */
	public Integer getToSeqNumber() {
		return toSeqNumber;
	}

	public void setToSeqNumber(Integer toSeqNumber) {
		this.toSeqNumber = toSeqNumber;
	}

	/**
	 * The selected invoice status, if the value is -1, this signify that the user selects all option or disregard this filter.
	 */
	public int getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(int invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	/**
	 * The payment status, if the value is -1, this signify that the user selects all option or disregard this filter.
	 */
	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/**
	 * The as of date. Default to current date.
	 */
	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {
		return "InvoiceRegisterParam [companyId=" + companyId
				+ ", invoiceTypeId=" + invoiceTypeId + ", supplierId="
				+ supplierId + ", supplierAccountId=" + supplierAccountId
				+ ", termId=" + termId + ", invoiceNumber=" + invoiceNumber
				+ ", fromInvoiceDate=" + fromInvoiceDate + ", toInvoiceDate="
				+ toInvoiceDate + ", fromGLDate=" + fromGLDate + ", toGLDate="
				+ toGLDate + ", fromDueDate=" + fromDueDate + ", toDueDate="
				+ toDueDate + ", fromAmount=" + fromAmount + ", toAmount="
				+ toAmount + ", fromSeqNumber=" + fromSeqNumber
				+ ", toSeqNumber=" + toSeqNumber + ", invoiceStatus="
				+ invoiceStatus + ", paymentStatus=" + paymentStatus
				+ ", asOfDate=" + asOfDate + "]";
	}
}
