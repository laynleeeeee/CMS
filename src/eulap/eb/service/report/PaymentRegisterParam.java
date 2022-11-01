package eulap.eb.service.report;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A class that handles different parameters in filtering the payment register report.

 */
public class PaymentRegisterParam {
	private int companyId;
	private int divisionId;
	private int bankAccountId;
	private int supplierId;
	private int supplierAccountId;
	private Date paymentDateFrom;
	private Date paymentDateTo;
	private Date checkDateFrom;
	private Date checkDateTo;
	private Double amountFrom;
	private Double amountTo;
	private Integer voucherNoFrom;
	private Integer voucherNoTo;
	private BigDecimal checkNoFrom;
	private BigDecimal checkNoTo;
	private int paymentStatusId;

	/**
	 * The selected company Id.
	 */
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * The division id
	 * @return
	 */
	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	/**
	 * The selected bank account Id.
	 */
	public int getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(int bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**
	 * The selected supplier Id.
	 */
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * The selected supplier account Id.
	 */
	public int getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(int supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	/**
	 *	Starting range of payment date.
	 */
	public Date getPaymentDateFrom() {
		return paymentDateFrom;
	}

	public void setPaymentDateFrom(Date paymentDateFrom) {
		this.paymentDateFrom = paymentDateFrom;
	}

	/**
	 *	Ending range of payment date.
	 */
	public Date getPaymentDateTo() {
		return paymentDateTo;
	}

	public void setPaymentDateTo(Date paymentDateTo) {
		this.paymentDateTo = paymentDateTo;
	}

	/**
	 *	Starting range of check date.
	 */
	public Date getCheckDateFrom() {
		return checkDateFrom;
	}

	public void setCheckDateFrom(Date checkDateFrom) {
		this.checkDateFrom = checkDateFrom;
	}

	/**
	 *	Ending range of check date.
	 */
	public Date getCheckDateTo() {
		return checkDateTo;
	}

	public void setCheckDateTo(Date checkDateTo) {
		this.checkDateTo = checkDateTo;
	}

	/**
	 *	Starting range of amount.
	 */
	public Double getAmountFrom() {
		return amountFrom;
	}

	public void setAmountFrom(Double amountFrom) {
		this.amountFrom = amountFrom;
	}

	/**
	 *	Ending range of amount.
	 */
	public Double getAmountTo() {
		return amountTo;
	}

	public void setAmountTo(Double amountTo) {
		this.amountTo = amountTo;
	}

	/**
	 *	Starting range of voucher no.
	 */
	public Integer getVoucherNoFrom() {
		return voucherNoFrom;
	}
	
	public void setVoucherNoFrom(Integer voucherNoFrom) {
		this.voucherNoFrom = voucherNoFrom;
	}

	/**
	 *	Ending range of voucher no.
	 */
	public Integer getVoucherNoTo() {
		return voucherNoTo;
	}

	public void setVoucherNoTo(Integer voucherNoTo) {
		this.voucherNoTo = voucherNoTo;
	}

	/**
	 *	Starting range of check no.
	 */
	public BigDecimal getCheckNoFrom() {
		return checkNoFrom;
	}

	public void setCheckNoFrom(BigDecimal checkNoFrom) {
		this.checkNoFrom = checkNoFrom;
	}

	/**
	 *	Ending range of check no.
	 */
	public BigDecimal getCheckNoTo() {
		return checkNoTo;
	}

	public void setCheckNoTo(BigDecimal checkNoTo) {
		this.checkNoTo = checkNoTo;
	}

	/**
	 *	The status of the payment.
	 */
	public int getPaymentStatusId() {
		return paymentStatusId;
	}

	public void setPaymentStatusId(int paymentStatusId) {
		this.paymentStatusId = paymentStatusId;
	}

	@Override
	public String toString() {
		return "PaymentRegisterParam [companyId=" + companyId
				+ ", divisionId=" + divisionId
				+ ", bankAccountId=" + bankAccountId + ", supplierId="
				+ supplierId + ", supplierAccountId=" + supplierAccountId
				+ ", paymentDateFrom=" + paymentDateFrom + ", paymentDateTo="
				+ paymentDateTo + ", checkDateFrom=" + checkDateFrom
				+ ", checkDateTo=" + checkDateTo + ", amountFrom=" + amountFrom
				+ ", amountTo=" + amountTo + ", voucherNoFrom=" + voucherNoFrom
				+ ", voucherNoTo=" + voucherNoTo + ", checkNoFrom="
				+ checkNoFrom + ", checkNoTo=" + checkNoTo
				+ ", paymentStatusId=" + paymentStatusId + "]";
	}
}
