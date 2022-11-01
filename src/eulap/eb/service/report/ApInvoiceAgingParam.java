package eulap.eb.service.report;

import java.util.Date;

/**
 * A class that handles parameters in filtering AP invoice aging report.

 */
public class ApInvoiceAgingParam {

	public static final int INVOICE_DATE_AGE_BASIS = 1;
	public static final int GL_DATE_AGE_BASIS = 2;

	private int companyId;
	private int invoiceTypeId;
	private int supplierId;
	private int supplierAccountId;
	private int termId;
	private String invoiceNumber;
	private Date invoiceDate;
	private int ageBasis;
	private boolean showInvoices;
	private Date asOfDate;
	private int typeId;
	private int divisionId;

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
	 * The selected invoice type. 
	 */
	public int getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(int invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}

	/**
	 * The selected supplier.
	 */
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * The selected supplier account.
	 */
	public int getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(int supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	/**
	 * The selected term.
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
	 * The invoice date to be searched.
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	/**
	 * The age basis: 1 = invoice date,  2 =  GL Date.
	 */
	public int getAgeBasis() {
		return ageBasis;
	}

	public void setAgeBasis(int ageBasis) {
		this.ageBasis = ageBasis;
	}

	/**
	 * True if show invoices, otherwise false
	 */
	public boolean isShowInvoices() {
		return showInvoices;
	}

	public void setShowInvoices(boolean showInvoices) {
		this.showInvoices = showInvoices;
	}

	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/**
	 * The division Id.
	 */
	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	public String toString() {
		return "ApInvoiceAgingParam [companyId=" + companyId
				+ ", invoiceTypeId=" + invoiceTypeId + ", supplierId="
				+ supplierId + ", supplierAccountId=" + supplierAccountId
				+ ", termId=" + termId + ", invoiceNumber=" + invoiceNumber
				+ ", invoiceDate=" + invoiceDate + ", ageBasis=" + ageBasis
				+ ", asOfDate=" + asOfDate + ", typeId=" + typeId + ", divisionId=" + divisionId + "]";
	}
}
