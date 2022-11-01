package eulap.eb.web.dto;

import java.util.Date;

/**
 * Data transfer object class for purhcase order

 */

public class PurchaseOrderDto {
	private Integer purchaseOrderId;
	private Date poDate;
	private Integer poNumber;
	private String bmsNumber;
	private String supplierName;
	private String supplierAcctName;

	public Integer getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Integer purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	public Integer getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(Integer poNumber) {
		this.poNumber = poNumber;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderDto [purchaseOrderId=").append(purchaseOrderId).append(", poDate=").append(poDate)
				.append(", poNumber=").append(poNumber).append(", bmsNumber=").append(bmsNumber)
				.append(", supplierName=").append(supplierName).append(", supplierAcctName=").append(supplierAcctName)
				.append("]");
		return builder.toString();
	}
}
