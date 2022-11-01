package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the Supplier Advance Payment Aging report data.

 */

public class SupplierAdvancePaymentAgingDto{

	private String companyName;
	private String division;
	private Date date;
	private String supplier;
	private String supplierAccount;
	private double initialAmount;
	private String requestorName;
	private String refNumber;
	private String bmsNumber;
	private Integer poNumber;
	private double amount;
	private double range1To30;
	private double range31To60;
	private double range61To90;
	private double range91To120;
	private double range120ToUp;
	private String status;
	private String cancellationRemarks;

	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplierAccount() {
		return supplierAccount;
	}
	public void setSupplierAccount(String supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	public double getInitialAmount() {
		return initialAmount;
	}
	public void setInitialAmount(double initialAmount) {
		this.initialAmount = initialAmount;
	}

	public String getRequestorName() {
		return requestorName;
	}
	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}

	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}
	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	public Integer getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(Integer poNumber) {
		this.poNumber = poNumber;
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

	public double getRange120ToUp() {
		return range120ToUp;
	}
	public void setRange120ToUp(double range120ToUp) {
		this.range120ToUp = range120ToUp;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}
	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	@Override
	public String toString() {
		return "SupplierAdvancePaymentAgingDto [companyName=" + companyName+", division="
				+ division +", date="+ date +", supplier="+ supplier +", supplierAccount="+ supplierAccount
				+", initialAmount="+ initialAmount +", requestorName="+ requestorName +", refNumber="+ refNumber
				+", bmsNumber="+ bmsNumber +", poNumber="+ poNumber+", amount="+ amount +", range1To30="+ range1To30
				+", range31To60="+ range31To60 +", range61To90="+ range61To90 +", range91To120="+ range91To120
				+", range120ToUp="+ range120ToUp +", status="+ status +", cancellationRemarks=" + cancellationRemarks +"]";
	}
}