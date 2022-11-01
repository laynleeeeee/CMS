package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object class for RR register report

 */

public class RrRegisterDto {
	private String division;
	private String rrNo;
	private Date glDate;
	private String warehouse;
	private String poNo;
	private String bmsNo;
	private String supplier;
	private String supplierAcct;
	private String term;
	private double poCost;
	private double invoiceAmt;
	private double balance;
	private String status;
	private String remarks;

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getRrNo() {
		return rrNo;
	}

	public void setRrNo(String rrNo) {
		this.rrNo = rrNo;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

	public String getBmsNo() {
		return bmsNo;
	}

	public void setBmsNo(String bmsNo) {
		this.bmsNo = bmsNo;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplierAcct() {
		return supplierAcct;
	}

	public void setSupplierAcct(String supplierAcct) {
		this.supplierAcct = supplierAcct;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public double getPoCost() {
		return poCost;
	}

	public void setPoCost(double poCost) {
		this.poCost = poCost;
	}

	public double getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(double invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RrRegisterDto [division=").append(division).append(", rrNo=").append(rrNo).append(", glDate=")
				.append(glDate).append(", warehouse=").append(warehouse).append(", poNo=").append(poNo)
				.append(", bmsNo=").append(bmsNo).append(", supplier=").append(supplier).append(", supplierAcct=")
				.append(supplierAcct).append(", term=").append(term).append(", poCost=").append(poCost)
				.append(", invoiceAmt=").append(invoiceAmt).append(", balance=").append(balance).append(", status=")
				.append(status).append(", remarks=").append(remarks).append("]");
		return builder.toString();
	}
}
