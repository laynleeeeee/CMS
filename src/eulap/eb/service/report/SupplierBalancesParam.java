package eulap.eb.service.report;

import java.util.Date;
/**
 * A class that handles different parameters in filtering supplier balances report.

 *
 */

public class SupplierBalancesParam {
	private int companyId;
	private int supplierId;
	private int supplierAccountId;
	private Date asOfDate;

	/**
	 * Get the selected company id.
	 */
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the selected supplier id.
	 */
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * Get the selected supplier account id.
	 */
	public int getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(int supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	/**
	 * Chosen date to be included in the report.
	 */
	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}

	@Override
	public String toString() {
		return "SupplierBalancesParam [companyId=" + companyId
				+ ", supplierId=" + supplierId + ", supplierAccountId="
				+ supplierAccountId + ", asOfDate=" + asOfDate + "]";
	}
}
