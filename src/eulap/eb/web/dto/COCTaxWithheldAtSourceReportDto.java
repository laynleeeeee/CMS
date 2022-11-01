package eulap.eb.web.dto;

import java.util.Date;

/**
 * Certificate of Creditable Tax Withheld at Source Report dto.

 *
 */
public class COCTaxWithheldAtSourceReportDto {
	private Date startPeriod;
	private Date endPeriod;
	private Date cutOffDate;
	private String customer;
	private String atcCode;
	private String natureOfPayment;
	private Double taxBase;
	private Double taxRate;
	private Double taxAmt;

	public Date getStartPeriod() {
		return startPeriod;
	}
	public void setStartPeriod(Date startPeriod) {
		this.startPeriod = startPeriod;
	}
	public Date getEndPeriod() {
		return endPeriod;
	}
	public void setEndPeriod(Date endPeriod) {
		this.endPeriod = endPeriod;
	}
	public Date getCutOffDate() {
		return cutOffDate;
	}
	public void setCutOffDate(Date cutOffDate) {
		this.cutOffDate = cutOffDate;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getAtcCode() {
		return atcCode;
	}
	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}
	public String getNatureOfPayment() {
		return natureOfPayment;
	}
	public void setNatureOfPayment(String natureOfPayment) {
		this.natureOfPayment = natureOfPayment;
	}
	public Double getTaxBase() {
		return taxBase;
	}
	public void setTaxBase(Double taxBase) {
		this.taxBase = taxBase;
	}
	public Double getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}
	public Double getTaxAmt() {
		return taxAmt;
	}
	public void setTaxAmt(Double taxAmt) {
		this.taxAmt = taxAmt;
	}
	@Override
	public String toString() {
		return "COCTaxWithheldAtSourceReportDto [startPeriod=" + startPeriod + ", endPeriod=" + endPeriod
				+ ", cutOffDate=" + cutOffDate + ", customer=" + customer + ", atcCode=" + atcCode
				+ ", natureOfPayment=" + natureOfPayment + ", taxBase=" + taxBase + ", taxRate=" + taxRate + ", taxAmt="
				+ taxAmt + "]";
	}
}
