package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of ITEM_CATEGORY_ACCOUNT_SETUP table.

 *
 */
@Entity
@Table(name="ITEM_CATEGORY_ACCOUNT_SETUP")
public class ItemCategoryAccountSetup extends BaseDomain {
	@Expose
	private Integer companyId;
	@Expose
	private Integer itemCategoryId;
	@Expose
	private Integer costAccount;
	@Expose
	private Integer inventoryAccount;
	@Expose
	private Integer salesAccount;
	@Expose
	private Integer salesDiscountAccount;
	@Expose
	private Integer salesReturnAccount;
	@Expose
	private boolean active;
	@Expose
	private Integer divisionId;
	@Expose
	private String companyName;
	@Expose
	private String costAccountName;
	@Expose
	private String inventoryAccountName;
	@Expose
	private String salesAccountName;
	@Expose
	private String salesDiscountAccountName;
	@Expose
	private String salesReturnAccountName;
	@Expose
	private String divisionName;
	// Associatied Objects
	private AccountCombination costAccountCombi;
	private AccountCombination inventoryAccountCombi;
	private AccountCombination salesAccountCombi;
	private AccountCombination salesDiscountAccountCombi;
	private AccountCombination salesReturnAccountCombi;
	private Company company;
	private ItemCategory Itemcategory;

	public static final int COST_OF_SALES = 343;
	public static final int MERCHANDISE_INVENTORY = 347;
	public static final int SALES = 348;
	public static final int SALES_DISCOUNT = 341;
	public static final int SALES_RETURN = 349;

	public enum FIELD {id, companyId, costAccount, inventoryAccount, salesAccount,
		salesDiscountAccount, salesReturnAccount, itemCategoryId, active};
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "ITEM_CATEGORY_ACCOUNT_SETUP_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "ITEM_CATEGORY_ID", columnDefinition="int(10)")
	public Integer getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(Integer itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	@Column(name = "COST_ACCOUNT", columnDefinition="int(10)")
	public Integer getCostAccount() {
		return costAccount;
	}

	public void setCostAccount(Integer costAccount) {
		this.costAccount = costAccount;
	}

	@Column(name = "INVENTORY_ACCOUNT", columnDefinition="int(10)")
	public Integer getInventoryAccount() {
		return inventoryAccount;
	}

	public void setInventoryAccount(Integer inventoryAccount) {
		this.inventoryAccount = inventoryAccount;
	}

	@Column(name = "SALES_ACCOUNT", columnDefinition="int(10)")
	public Integer getSalesAccount() {
		return salesAccount;
	}

	public void setSalesAccount(Integer salesAccount) {
		this.salesAccount = salesAccount;
	}

	@Column(name = "SALES_DISCOUNT_ACCOUNT", columnDefinition="int(10)")
	public Integer getSalesDiscountAccount() {
		return salesDiscountAccount;
	}

	public void setSalesDiscountAccount(Integer salesDiscountAccount) {
		this.salesDiscountAccount = salesDiscountAccount;
	}

	@Column(name = "SALES_RETURN_ACCOUNT", columnDefinition="int(10)")
	public Integer getSalesReturnAccount() {
		return salesReturnAccount;
	}

	public void setSalesReturnAccount(Integer salesReturnAccount) {
		this.salesReturnAccount = salesReturnAccount;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "ItemCategoryAccountSetup [companyId=" + companyId
				+ ", itemCategoryId=" + itemCategoryId + ", costAccount="
				+ costAccount + ", inventoryAccount=" + inventoryAccount
				+ ", salesAccount=" + salesAccount + ", salesDiscountAccount="
				+ salesDiscountAccount + ", salesReturnAccount="
				+ salesReturnAccount + "]";
	}

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name = "COST_ACCOUNT", insertable = false, updatable = false)
	public AccountCombination getCostAccountCombi() {
		return costAccountCombi;
	}

	public void setCostAccountCombi(AccountCombination costAccountCombi) {
		this.costAccountCombi = costAccountCombi;
	}

	@OneToOne
	@JoinColumn(name = "INVENTORY_ACCOUNT", insertable = false, updatable = false)
	public AccountCombination getInventoryAccountCombi() {
		return inventoryAccountCombi;
	}

	public void setInventoryAccountCombi(AccountCombination inventoryAccountCombi) {
		this.inventoryAccountCombi = inventoryAccountCombi;
	}

	@OneToOne
	@JoinColumn(name = "SALES_ACCOUNT", insertable = false, updatable = false)
	public AccountCombination getSalesAccountCombi() {
		return salesAccountCombi;
	}

	public void setSalesAccountCombi(AccountCombination salesAccountCombi) {
		this.salesAccountCombi = salesAccountCombi;
	}

	@OneToOne
	@JoinColumn(name = "SALES_DISCOUNT_ACCOUNT", insertable = false, updatable = false)
	public AccountCombination getSalesDiscountAccountCombi() {
		return salesDiscountAccountCombi;
	}

	public void setSalesDiscountAccountCombi(
			AccountCombination salesDiscountAccountCombi) {
		this.salesDiscountAccountCombi = salesDiscountAccountCombi;
	}

	@OneToOne
	@JoinColumn(name = "SALES_RETURN_ACCOUNT", insertable = false, updatable = false)
	public AccountCombination getSalesReturnAccountCombi() {
		return salesReturnAccountCombi;
	}

	public void setSalesReturnAccountCombi(
			AccountCombination salesReturnAccountCombi) {
		this.salesReturnAccountCombi = salesReturnAccountCombi;
	}

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn(name = "ITEM_CATEGORY_ID", insertable = false, updatable = false)
	public ItemCategory getItemcategory() {
		return Itemcategory;
	}

	public void setItemcategory(ItemCategory itemcategory) {
		Itemcategory = itemcategory;
	}

	@Transient
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Transient
	public String getCostAccountName() {
		return costAccountName;
	}

	public void setCostAccountName(String costAccountName) {
		this.costAccountName = costAccountName;
	}

	@Transient
	public String getInventoryAccountName() {
		return inventoryAccountName;
	}

	public void setInventoryAccountName(String inventoryAccountName) {
		this.inventoryAccountName = inventoryAccountName;
	}

	@Transient
	public String getSalesAccountName() {
		return salesAccountName;
	}

	public void setSalesAccountName(String salesAccountName) {
		this.salesAccountName = salesAccountName;
	}

	@Transient
	public String getSalesDiscountAccountName() {
		return salesDiscountAccountName;
	}

	public void setSalesDiscountAccountName(String salesDiscountAccountName) {
		this.salesDiscountAccountName = salesDiscountAccountName;
	}

	@Transient
	public String getSalesReturnAccountName() {
		return salesReturnAccountName;
	}

	public void setSalesReturnAccountName(String salesReturnAccountName) {
		this.salesReturnAccountName = salesReturnAccountName;
	}

	@Transient
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
}
