package eulap.eb.service.report;

import java.util.Date;

/**
* A class that hadles parameters in filtering the Supplier Advance Payment Aging

*/
public class SupplierAdvancePaymentAgingParam{

	public static final int GL_DATE_AGE_BASIS = 1;
	public static final int TRANSACTION_DATE_AGE_BASIS = 2;
	public static final int DUE_DATE_AGE_BASIS = 3;
	public static final int CLEARING_DATE_AGE_BASIS = 4;


	private Integer companyId;
	private Integer divisionId;
	private Integer supplierId;
	private Integer supplierAcctId;
	private String bmsNumber;
	private Date dateFrom;
	private Date dateTo;
	private int ageBasis;
	private Integer statusId;

	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getSupplierAcctId() {
		return supplierAcctId;
	}
	public void setSupplierAcctId(Integer supplierAcctId) {
		this.supplierAcctId = supplierAcctId;
	}

	public String getBmsNumber() {
		return bmsNumber;
	}
	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	public Date getDateFrom() {
		return dateFrom;
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

	public int getAgeBasis() {
		return ageBasis;
	}
	public void setAgeBasis(int ageBasis) {
		this.ageBasis = ageBasis;
	}

	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getAgeBasisDescription() {
		String ageBasisDescription = "";
		switch (ageBasis) {
		case GL_DATE_AGE_BASIS:
			ageBasisDescription = "Due Date";
			 break;
		case TRANSACTION_DATE_AGE_BASIS:
			ageBasisDescription = "Transaction Date";
			 break;
		case DUE_DATE_AGE_BASIS:
			ageBasisDescription = "GL Date";
			 break;
		case CLEARING_DATE_AGE_BASIS:
			ageBasisDescription = "Clearing Date";
			 break;
		};
		return ageBasisDescription;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransactionAgingParam [companyId=").append(companyId).append(", divisionId=").append(divisionId)
		.append(", supplierId=").append(supplierId).append(", supplierAcctId=").append(supplierAcctId)
		.append(", bmsNumber=").append(bmsNumber).append(", dateFrom=").append(dateFrom).append(", dateTo=").append(dateTo)
		.append(", ageBasis=").append(ageBasis).append(", statusId=").append(statusId).append("]");
		return builder.toString();
	}
}