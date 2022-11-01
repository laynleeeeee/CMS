package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * A class that represents the PETTY_CASH_REPLENISHMENT_LINE table.

 *
 */
@Entity
@Table(name="PETTY_CASH_REPLENISHMENT_LINE")
public class PettyCashReplenishmentLine extends BaseFormLine {
	private Integer pcvlId;
	@Expose
	private Integer pcvllId;
	@Expose
	private Integer accountCombinationId;
	@Expose
	private Integer apInvoiceId;
	@Expose
	private Date pcvlDate;
	@Expose
	private Integer sequenceNo;
	@Expose
	private String bmsNumber;
	@Expose
	private String orNumber;
	@Expose
	private String requestor;
	@Expose
	private Integer supplierId;
	@Expose
	private String supplierName;
	@Expose
	private String description;
	@Expose
	private Integer divisionId;
	@Expose
	private Integer accountId;
	@Expose
	private String supplierTin;
	@Expose
	private String brgyStreet;
	@Expose
	private String city;
	@Expose
	private String divisionName;
	@Expose
	private String accountName;
	@Expose
	private String pcvlDateString;
	@Expose
	private Double grossAmount;
	@Expose
	private String taxName;
	@Expose
	private Double vatAmount;
	@Expose
	private Double amount;
	@Expose
	private Integer refenceObjectId;
	private AccountCombination accountCombination;

	public enum FIELD {
		id, apInvoiceId, ebObjectId
	}

	public static final int PCRL_OBJECT_TYPE_ID = 24015;
	public static final int PCRL_PCVLL_RELATIONSHIP = 24008;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PETTY_CASH_REPLENISHMENT_LINE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Transient
	public Integer getPcvlId() {
		return pcvlId;
	}
	public void setPcvlId(Integer pcvlId) {
		this.pcvlId = pcvlId;
	}

	@Transient
	public Date getPcvDate() {
		return pcvlDate;
	}
	public void setPcvDate(Date pcvDate) {
		this.pcvlDate = pcvDate;
	}

	@Transient
	public String getBmsNumber() {
		return bmsNumber;
	}
	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Transient
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	@Transient
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Transient
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return PCRL_OBJECT_TYPE_ID;
	}

	@Transient
	public String getSupplierTin() {
		return supplierTin;
	}
	public void setSupplierTin(String supplierTin) {
		this.supplierTin = supplierTin;
	}

	@Transient
	public String getBrgyStreet() {
		return brgyStreet;
	}
	public void setBrgyStreet(String brgyStreet) {
		this.brgyStreet = brgyStreet;
	}

	@Transient
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	@Transient
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public String getPcvlDateString() {
		return pcvlDateString;
	}
	public void setPcvlDateString(String pcvlDateString) {
		this.pcvlDateString = pcvlDateString;
	}

	@Transient
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Transient
	@Override
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Transient
	public Integer getPcvllId() {
		return pcvllId;
	}
	public void setPcvllId(Integer pcvllId) {
		this.pcvllId = pcvllId;
	}

	@Column(name = "AP_INVOICE_ID")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}
	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	@Column(name = "ACCOUNT_COMBINATION_ID")
	public Integer getAccountCombinationId() {
		return accountCombinationId;
	}
	public void setAccountCombinationId(Integer accountCombinationId) {
		this.accountCombinationId = accountCombinationId;
	}

	@OneToOne
	@JoinColumn(name="ACCOUNT_COMBINATION_ID", insertable=false, updatable=false)
	public AccountCombination getAccountCombination() {
		return accountCombination;
	}
	public void setAccountCombination(AccountCombination accountCombination) {
		this.accountCombination = accountCombination;
	}

	@Transient
	public Integer getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Transient
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	@Transient
	public Double getGrossAmount() {
		return grossAmount;
	}
	public void setGrossAmount(Double grossAmount) {
		this.grossAmount = grossAmount;
	}

	@Transient
	public String getTaxName() {
		return taxName;
	}
	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	@Transient
	public Double getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}

	@Transient
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "PettyCashReplenishmentLine [pcvlId=" + pcvlId + ", pcvllId=" + pcvllId + ", accountCombinationId="
				+ accountCombinationId + ", apInvoiceId=" + apInvoiceId + ", pcvlDate=" + pcvlDate + ", sequenceNo="
				+ sequenceNo + ", bmsNumber=" + bmsNumber + ", orNumber=" + orNumber + ", requestor=" + requestor
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", description=" + description
				+ ", divisionId=" + divisionId + ", accountId=" + accountId + ", supplierTin=" + supplierTin
				+ ", brgyStreet=" + brgyStreet + ", city=" + city + ", divisionName=" + divisionName + ", accountName="
				+ accountName + ", pcvlDateString=" + pcvlDateString + ", grossAmount=" + grossAmount + ", taxName="
				+ taxName + ", vatAmount=" + vatAmount + ", amount=" + amount + ", refenceObjectId=" + refenceObjectId
				+ "]";
	}
}
