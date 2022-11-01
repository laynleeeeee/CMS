package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer class for BIR 2307 - Certificate of Creditable Tax Withheld At Source printout

 */

public class COCTaxDto {
	private Date startPeriod;
	private Date endPeriod;
	private Date dateFrom;
	private Date dateTo;
	private Date cutOffDate;
	private String customer;
	private String natureOP;
	private String wtDesc;
	private String atc;
	private Double firstQuarter;
	private Double secondQuarter;
	private Double thirdQuarter;
	private Double taxWithheld;
	private String division;
	private Double taxRate;
	private Double taxAmount;
	private String natureOfPayment;
	private Double taxBase;
	private Double taxAmt;

	public Date getStartPeriod() {
		return startPeriod;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getCutOffDate() {
		return cutOffDate;
	}

	public void setCutOffDate(Date cutOffDate) {
		this.cutOffDate = cutOffDate;
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

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getNatureOP() {
		return natureOP;
	}

	public void setNatureOP(String natureOP) {
		this.natureOP = natureOP;
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

	public void setStartPeriod(Date startPeriod) {
		this.startPeriod = startPeriod;
	}

	public Date getEndPeriod() {
		return endPeriod;
	}

	public void setEndPeriod(Date endPeriod) {
		this.endPeriod = endPeriod;
	}

	public String getWtDesc() {
		return wtDesc;
	}

	public void setWtDesc(String wtDesc) {
		this.wtDesc = wtDesc;
	}

	public String getAtc() {
		return atc;
	}

	public void setAtc(String atc) {
		this.atc = atc;
	}

	public Double getFirstQuarter() {
		return firstQuarter;
	}

	public void setFirstQuarter(Double firstQuarter) {
		this.firstQuarter = firstQuarter;
	}

	public Double getSecondQuarter() {
		return secondQuarter;
	}

	public void setSecondQuarter(Double secondQuarter) {
		this.secondQuarter = secondQuarter;
	}

	public Double getThirdQuarter() {
		return thirdQuarter;
	}

	public void setThirdQuarter(Double thirdQuarter) {
		this.thirdQuarter = thirdQuarter;
	}

	public Double getTaxWithheld() {
		return taxWithheld;
	}

	public void setTaxWithheld(Double taxWithheld) {
		this.taxWithheld = taxWithheld;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Double getTaxRatePercentage() {
		return taxRate/100;
	}

	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getNatureOfPayment() {
		return natureOfPayment;
	}

	public void setNatureOfPayment(String natureOfPayment) {
		this.natureOfPayment = natureOfPayment;
	}

	@Override
	public String toString() {
		return "COCTaxDto [startPeriod=" + startPeriod + ", endPeriod=" + endPeriod + ", dateFrom=" + dateFrom
				+ ", dateTo=" + dateTo + ", cutOffDate=" + cutOffDate + ", customer=" + customer + ", natureOP="
				+ natureOP + ", wtDesc=" + wtDesc + ", atc=" + atc + ", firstQuarter=" + firstQuarter
				+ ", secondQuarter=" + secondQuarter + ", thirdQuarter=" + thirdQuarter + ", taxWithheld=" + taxWithheld
				+ ", taxBase=" + taxBase + ", taxRate=" + taxRate + ", taxAmt=" + taxAmt + "]";
	}
}