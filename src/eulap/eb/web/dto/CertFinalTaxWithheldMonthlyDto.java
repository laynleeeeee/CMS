package eulap.eb.web.dto;

import java.util.Date;

/**
 * Certificate of final tax withheld at source monthly dto

 */
public class CertFinalTaxWithheldMonthlyDto {
	private String month;
	private Date date;
	private String customer;
	private String officialReceipt;
	private Double netCash;
	private Double creditableTaxWIthheld;
	private Double addFinalTaxWithheld;
	private Double gross;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getOfficialReceipt() {
		return officialReceipt;
	}

	public void setOfficialReceipt(String officialReceipt) {
		this.officialReceipt = officialReceipt;
	}

	public Double getNetCash() {
		return netCash;
	}

	public void setNetCash(Double netCash) {
		this.netCash = netCash;
	}

	public Double getCreditableTaxWIthheld() {
		return creditableTaxWIthheld;
	}

	public void setCreditableTaxWIthheld(Double creditableTaxWIthheld) {
		this.creditableTaxWIthheld = creditableTaxWIthheld;
	}

	public Double getAddFinalTaxWithheld() {
		return addFinalTaxWithheld;
	}

	public void setAddFinalTaxWithheld(Double addFinalTaxWithheld) {
		this.addFinalTaxWithheld = addFinalTaxWithheld;
	}

	public Double getGross() {
		return gross;
	}

	public void setGross(Double gross) {
		this.gross = gross;
	}

	@Override
	public String toString() {
		return "CertFinalTaxWithheldMonthlyDto [month=" + month + ", date=" + date + ", customer=" + customer
				+ ", officialReceipt=" + officialReceipt + ", netCash=" + netCash + ", creditableTaxWIthheld="
				+ creditableTaxWIthheld + ", addFinalTaxWithheld=" + addFinalTaxWithheld + ", gross=" + gross + "]";
	}

}
