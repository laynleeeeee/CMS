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
 * A class that represents the PETTY_CASH_VOUCHER_LIQUIDATION_LINE table.

 *
 */
@Entity
@Table(name="PETTY_CASH_VOUCHER_LIQUIDATION_LINE")
public class PettyCashVoucherLiquidationLine extends OtherCharge {
	@Expose
	private Integer pcvlId;
	private Date pcvDate;
	@Expose
	private String bmsNumber;
	@Expose
	private String orNumber;
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
	private Integer accountCombinationId;
	private Supplier supplier;
	private AccountCombination accountCombination;
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
	private String pcvDateString;
	private Integer referenceObjectId;

	public enum FIELD {
		id, pcvlId, divisionId, userCustodianId, pcvlDate, requestor, referenceNo, description, amount, cashReturned, ebObjectId
	}

	public static final int PCVL_OBJECT_TYPE_ID = 24014;
	public static final int MAX_CHARACTERS = 50;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PETTY_CASH_VOUCHER_LIQUIDATION_LINE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PETTY_CASH_VOUCHER_LIQUIDATION_ID")
	public Integer getPcvlId() {
		return pcvlId;
	}
	public void setPcvlId(Integer pcvlId) {
		this.pcvlId = pcvlId;
	}

	@Column(name = "PCV_DATE")
	public Date getPcvDate() {
		return pcvDate;
	}
	public void setPcvDate(Date pcvDate) {
		this.pcvDate = pcvDate;
	}

	@Column(name = "BMS_NUMBER")
	public String getBmsNumber() {
		return bmsNumber;
	}
	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Column(name = "OR_NUMBER")
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	@Column(name = "SUPPLIER_ID")
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@OneToOne
	@JoinColumn(name="SUPPLIER_ID", insertable=false, updatable=false)
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Column(name = "DESCRIPTION")
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return PCVL_OBJECT_TYPE_ID;
	}

	@Override
	public String toString() {
		return "PettyCashVoucherLiquidationLine [pcvlId=" + pcvlId + ", pcvDate=" + pcvDate + ", bmsNumber=" + bmsNumber
				+ ", orNumber=" + orNumber + ", supplierId=" + supplierId + ", description=" + description
				+ ", divisionId=" + divisionId + ", accountId=" + accountId + ", accountCombinationId="
				+ accountCombinationId + "]";
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
	public String getPcvDateString() {
		return pcvDateString;
	}
	public void setPcvDateString(String pcvDateString) {
		this.pcvDateString = pcvDateString;
	}

	@Transient
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer refenceObjectId) {
		this.referenceObjectId = refenceObjectId;
	}
}
