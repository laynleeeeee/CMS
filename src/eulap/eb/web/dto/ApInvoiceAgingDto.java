package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the AP invoice aging report data.

 */
public class ApInvoiceAgingDto {

	private String invoiceType;
	private String invoiceNumber;
	private Date invoiceDate;
	private Date glDate;
	private Date dueDate;
	private double balance;
	private double amount;
	private double range1To30;
	private double range31To60;
	private double range61To90;
	private double range91To120;
	private double range121To150;
	private double range151ToUp;
	private int ageDays;
	private String termName;
	private String supplierName;
	private String supplierAccountName;
	private String companyName;
	private Integer sequenceNumber;
	private Integer termId;
	private Integer invoiceId;
	private Integer invoiceTypeId;
	private Integer supplierId;
	private Integer supplierAcctId;
	private double totalPayment;
	private String divisionName;

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getRange1To30() {
		return range1To30;
	}

	public void setRange1To30(double range1To30) {
		this.range1To30 = range1To30;
	}

	public double getRange31To60() {
		return range31To60;
	}

	public void setRange31To60(double range31To60) {
		this.range31To60 = range31To60;
	}

	public double getRange61To90() {
		return range61To90;
	}

	public void setRange61To90(double range61To90) {
		this.range61To90 = range61To90;
	}

	public double getRange91To120() {
		return range91To120;
	}

	public void setRange91To120(double range91To120) {
		this.range91To120 = range91To120;
	}

	public double getRange121To150() {
		return range121To150;
	}

	public void setRange121To150(double range121To150) {
		this.range121To150 = range121To150;
	}

	public double getRange151ToUp() {
		return range151ToUp;
	}

	public void setRange151ToUp(double range151ToUp) {
		this.range151ToUp = range151ToUp;
	}

	public int getAgeDays() {
		return ageDays;
	}

	public void setAgeDays(int ageDays) {
		this.ageDays = ageDays;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierNAme) {
		this.supplierName = supplierNAme;
	}

	public String getSupplierAccountName() {
		return supplierAccountName;
	}

	public void setSupplierAccountName(String supplierAccountName) {
		this.supplierAccountName = supplierAccountName;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Integer getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(Integer invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getSupplierAcctId() {
		return supplierAcctId;
	}

	public void setSupplierAcctId(Integer supplierAcctId) {
		this.supplierAcctId = supplierAcctId;
	}

	public double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Override
	public String toString() {
		return "ApInvoiceAgingDto [invoiceType=" + invoiceType + ", invoiceNumber="
				+ invoiceNumber + ", invoiceDate=" + invoiceDate + ", glDate="
				+ glDate + ", dueDate=" + dueDate + ", balance=" + balance
				+ ", amount=" + amount + ", range1To30=" + range1To30
				+ ", range31To60=" + range31To60 + ", range61To90="
				+ range61To90 + ", range91To120=" + range91To120
				+ ", range121To150=" + range121To150 + ", range151ToUp="
				+ range151ToUp + ", ageDays=" + ageDays + ", termName="
				+ termName + ", supplierNAme=" + supplierName
				+ ", supplierAccountName=" + supplierAccountName
				+ ", companyName=" + companyName + ", sequenceNumber="
				+ sequenceNumber + ", termId=" + termId + ", invoiceId="
				+ invoiceId + ", invoiceTypeId=" + invoiceTypeId
				+ ", supplierId=" + supplierId + ", supplierAcctId="
				+ supplierAcctId + ", totalPayment=" + totalPayment +
				", divisionName"+ divisionName  +"]";
	}
}
