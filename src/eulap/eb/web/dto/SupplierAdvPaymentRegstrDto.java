package eulap.eb.web.dto;

import java.util.Date;

/**
 * Supplier advance payment register dto

 */
public class SupplierAdvPaymentRegstrDto {
	private String divisionName;
	private Date date;
	private String supplier;
	private String supplierAcct;
	private String referenceNo;
	private String bmsNumber;
	private Double amount;
	private Double balanceAmount;
	private String status;
	private String cancellationRemarks;

	public static SupplierAdvPaymentRegstrDto getInstanceOf(String divisionName, Date date, String supplier,
			String supplierAcct, String referenceNo, String bmsNumber, Double amount, Double balanceAmount,
			String status, String cancellationRemarks) {
		SupplierAdvPaymentRegstrDto supplierAdvPaymentRegstrDto = new SupplierAdvPaymentRegstrDto();
		supplierAdvPaymentRegstrDto.divisionName = divisionName;
		supplierAdvPaymentRegstrDto.date = date;
		supplierAdvPaymentRegstrDto.supplier = supplier;
		supplierAdvPaymentRegstrDto.supplierAcct = supplierAcct;
		supplierAdvPaymentRegstrDto.referenceNo = referenceNo;
		supplierAdvPaymentRegstrDto.bmsNumber = bmsNumber;
		supplierAdvPaymentRegstrDto.amount = amount;
		supplierAdvPaymentRegstrDto.balanceAmount = balanceAmount;
		supplierAdvPaymentRegstrDto.status = status;
		supplierAdvPaymentRegstrDto.cancellationRemarks = cancellationRemarks;
		return supplierAdvPaymentRegstrDto;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
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

	public String getSupplierAcct() {
		return supplierAcct;
	}

	public void setSupplierAcct(String supplierAcct) {
		this.supplierAcct = supplierAcct;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
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
		return "SupplierAdvPaymentRegstrDto [divisionName=" + divisionName + ", date=" + date + ", supplier=" + supplier
				+ ", supplierAcct=" + supplierAcct + ", preferenceNo=" + referenceNo + ", bmsNumber=" + bmsNumber
				+ ", amount=" + amount + ", balanceAmount=" + balanceAmount + ", Status=" + status + "]";
	}

}
