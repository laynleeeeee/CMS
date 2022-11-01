package eulap.eb.web.dto;

import java.util.Date;

/**
 * Bank Reconciliation subreport container.

 * 
 */
public class BankReconItem {
	private Integer id;
	private Date date;
	private Date checkDate;
	private String receiptNo;
	private String checkNo;
	private String customer;
	private String supplier;
	private Double amount;
	private String status;
	private Integer brField;

	public static BankReconItem getInstanceOf(Integer id, Date date, Date checkDate, String receiptNo, String checkNo,
			String customer, String supplier, Double amount, String status, Integer brField) {
		BankReconItem brData = new BankReconItem();
		brData.id = id;
		brData.date = date;
		brData.checkDate = checkDate;
		brData.receiptNo = receiptNo;
		brData.checkNo = checkNo;
		brData.customer = customer;
		brData.supplier = supplier;
		brData.amount = amount;
		brData.status = status;
		brData.brField = brField;
		return brData;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getBrField() {
		return brField;
	}

	public void setBrField(Integer brField) {
		this.brField = brField;
	}

	@Override
	public String toString() {
		return "BankReconData [id=" + id + ", date=" + date + ", checkDate="
				+ checkDate + ", receiptNo=" + receiptNo + ", checkNo="
				+ checkNo + ", customer=" + customer + ", supplier=" + supplier
				+ ", amount=" + amount + ", status=" + status
				+ ", brField=" + brField + "]";
	}
}
