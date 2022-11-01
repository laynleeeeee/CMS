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
 * Object representation of INVENTORY_ACCOUNT table.

 */

@Entity
@Table(name="INVENTORY_ACCOUNT")
public class InventoryAccount extends BaseDomain {
	private Integer companyId;
	private Integer cashSalesRmId;
	private Integer customerAdvPaymentRmId;
	private boolean active;
	private Company company;
	private ReceiptMethod cashSaleRM;
	private ReceiptMethod customerAdvPaymentRM;

	public enum FIELD {
		id, companyId, cashSalesRmId, customerAdvPaymentRmId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "INVENTORY_ACCOUNT_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="CASH_SALES_RM_ID", columnDefinition="int(10)")
	public Integer getCashSalesRmId() {
		return cashSalesRmId;
	}

	public void setCashSalesRmId(Integer cashSalesRmId) {
		this.cashSalesRmId = cashSalesRmId;
	}

	@Column(name="CUSTOMER_ADV_PAYMENT_RM_ID", columnDefinition="int(10)")
	public Integer getCustomerAdvPaymentRmId() {
		return customerAdvPaymentRmId;
	}

	public void setCustomerAdvPaymentRmId(Integer customerAdvPaymentRmId) {
		this.customerAdvPaymentRmId = customerAdvPaymentRmId;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn (name = "CASH_SALES_RM_ID", insertable=false, updatable=false)
	public ReceiptMethod getCashSaleRM() {
		return cashSaleRM;
	}

	public void setCashSaleRM(ReceiptMethod cashSaleRM) {
		this.cashSaleRM = cashSaleRM;
	}

	@OneToOne
	@JoinColumn (name = "CUSTOMER_ADV_PAYMENT_RM_ID", insertable=false, updatable=false)
	public ReceiptMethod getCustomerAdvPaymentRM() {
		return customerAdvPaymentRM;
	}

	public void setCustomerAdvPaymentRM(ReceiptMethod customerAdvPaymentRM) {
		this.customerAdvPaymentRM = customerAdvPaymentRM;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InventoryAccount [companyId=").append(companyId).append(", cashSalesRmId=")
				.append(cashSalesRmId).append(", customerAdvPaymentRmId=").append(customerAdvPaymentRmId)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}
}
