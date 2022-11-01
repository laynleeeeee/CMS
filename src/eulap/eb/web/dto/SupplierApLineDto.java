package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object for supplier account per AP Line

 */

public class SupplierApLineDto {
	private String apInvoiceSource;
	private String supplierName;
	private String supplierAcctName;
	private String divisionName;
	private String acctName;
	private double amount;
	private String desc;
	private Date date;

	public String getApInvoiceSource() {
		return apInvoiceSource;
	}

	public void setApInvoiceSource(String apInvoiceSource) {
		this.apInvoiceSource = apInvoiceSource;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierAcctName() {
		return supplierAcctName;
	}

	public void setSupplierAcctName(String supplierAcctName) {
		this.supplierAcctName = supplierAcctName;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
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
		builder.append("SupplierApLineDto [apInvoiceSource=").append(apInvoiceSource).append(", supplierName=")
				.append(supplierName).append(", supplierAcctName=").append(supplierAcctName).append(", divisionName=")
				.append(divisionName).append(", acctName=").append(acctName).append(", amount=").append(amount)
				.append(", desc=").append(desc).append(", date=").append(date).append("]");
		return builder.toString();
	}
}
