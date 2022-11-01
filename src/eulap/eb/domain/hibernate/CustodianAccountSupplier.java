package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object class representation for CUSTODIAN_ACCOUNT_SUPPLIER.

 *
 */
@Entity
@Table(name="CUSTODIAN_ACCOUNT_SUPPLIER")
public class CustodianAccountSupplier extends BaseDomain {
	private Integer custodianAccountId;
	private Integer supplierId;
	private Integer supplierAccountId;
	private CustodianAccount custodianAccount;
	private Supplier supplier;
	private SupplierAccount supplierAccount;

	public enum FIELD {
		id, custodianAccountId, supplierId, supplierAccountId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CUSTODIAN_ACCOUNT_SUPPLIER_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "CUSTODIAN_ACCOUNT_ID", columnDefinition="INT(10)")
	public Integer getCustodianAccountId() {
		return custodianAccountId;
	}
	public void setCustodianAccountId(Integer custodianAccountId) {
		this.custodianAccountId = custodianAccountId;
	}

	@Column(name = "SUPPLIER_ID", columnDefinition="INT(10)")
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name = "SUPPLIER_ACCOUNT_ID", columnDefinition="INT(10)")
	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}
	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	@OneToOne
	@JoinColumn(name="CUSTODIAN_ACCOUNT_ID", insertable=false, updatable=false)
	public CustodianAccount getCustodianAccount() {
		return custodianAccount;
	}
	public void setCustodianAccount(CustodianAccount custodianAccount) {
		this.custodianAccount = custodianAccount;
	}

	@OneToOne
	@JoinColumn(name="SUPPLIER_ID", insertable=false, updatable=false)
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@OneToOne
	@JoinColumn(name="SUPPLIER_ACCOUNT_ID", insertable=false, updatable=false)
	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}
	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	@Override
	public String toString() {
		return "CustodianAccountSupplier [custodianAccountId=" + custodianAccountId + ", supplierId=" + supplierId
				+ ", supplierAccountId=" + supplierAccountId + "]";
	}
}
