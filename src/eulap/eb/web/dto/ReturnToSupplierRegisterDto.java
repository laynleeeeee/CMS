package eulap.eb.web.dto;

import java.util.Date;


/**
 * Class that will contain the data for Return To Supllier Register.

 */

public class ReturnToSupplierRegisterDto {
	private int apInvoiceId;
	private int divisionId;
	private String division;
	private int rtsNo;
	private int rrNo;
	private int poNo;
	private String siSoaNo;
	private Date rtsDate;
	private Date rrDate;
	private int warehouseId;
	private String warehouse;
	private int supplierId;
	private String supplier;
	private int supplierAccountId;
	private String supplierAccount;
	private double amount;
	private double balance;
	private int rtsStatusId;
	private String cancellationRemarks;
	private String rrNumber;
	private String formStatus;
	private double paidAmount;

	public int getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(int apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public int getRtsNo() {
		return rtsNo;
	}

	public void setRtsNo(int rtsNo) {
		this.rtsNo = rtsNo;
	}

	public int getRrNo() {
		return rrNo;
	}

	public void setRrNo(int rrNo) {
		this.rrNo = rrNo;
	}

	public int getPoNo() {
		return poNo;
	}

	public void setPoNo(int poNo) {
		this.poNo = poNo;
	}

	public String getSiSoaNo() {
		return siSoaNo;
	}

	public void setSiSoaNo(String siSoaNo) {
		this.siSoaNo = siSoaNo;
	}

	public int getwarehouseId() {
		return warehouseId;
	}

	public void setwarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public Date getRtsDate() {
		return rtsDate;
	}

	public void setRtsDate(Date rtsDate) {
		this.rtsDate = rtsDate;
	}

	public Date getRrDate() {
		return rrDate;
	}

	public void setRrDate(Date rrDate) {
		this.rrDate = rrDate;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public int getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(int supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	public int getRtsStatusId() {
		return rtsStatusId;
	}

	public void setRtsStatusId(int rtsStatusId) {
		this.rtsStatusId = rtsStatusId;
	}

	public String getRrNumber() {
		return rrNumber;
	}

	public void setRrNumber(String rrNumber) {
		this.rrNumber = rrNumber;
	}

	public String getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(String formStatus) {
		this.formStatus = formStatus;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReturnToSupplierRegisterDto [apInvoiceId=").append(apInvoiceId).append(", divisionId=")
				.append(divisionId).append(", division=").append(division).append(", rtsNo=").append(rtsNo)
				.append(", rrNo=").append(rrNo).append(", poNo=").append(poNo).append(", siSoaNo=").append(siSoaNo)
				.append(", rtsDate=").append(rtsDate).append(", rrDate=").append(rrDate).append(", warehouseId=")
				.append(warehouseId).append(", warehouse=").append(warehouse).append(", supplierId=").append(supplierId)
				.append(", supplier=").append(supplier).append(", supplierAccountId=").append(supplierAccountId)
				.append(", supplierAccount=").append(supplierAccount).append(", amount=").append(amount)
				.append(", balance=").append(balance).append(", rtsStatusId=").append(rtsStatusId)
				.append(", cancellationRemarks=").append(cancellationRemarks).append(", rrNumber=").append(rrNumber)
				.append(", formStatus=").append(formStatus).append(", paidAmount=").append(paidAmount).append("]");
		return builder.toString();
	}






}
