package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represent WT_ACCOUNT_SETTING table.

 */

@Entity
@Table (name="WT_ACCOUNT_SETTING")
public class WithholdingTaxAcctSetting extends BaseDomain {
	private Integer companyId;
	private String name;
	private double value;
	private Integer birAtcId;
	private Integer wtTypeId;
	private Integer acctCombinationId;
	private boolean active;
	private Integer orderId;
	private boolean creditable;

	public enum FIELD {
		id, companyId, name, birAtcId, wtTypeId, acctCombinationId, active, orderId, creditable
	}

	/**
	 * Object type id of Withholding Tax Account Setting
	 */
	public static final int WTAS_OBJECT_TYPE_ID = 132;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "WT_ACCOUNT_SETTING_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="NAME", columnDefinition="varchar(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="VALUE", columnDefinition="double")
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Column(name="ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getAcctCombinationId() {
		return acctCombinationId;
	}

	public void setAcctCombinationId(Integer acctCombinationId) {
		this.acctCombinationId = acctCombinationId;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name="ORDER_ID", columnDefinition="int(3)")
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	@Column(name="CREDITABLE", columnDefinition="tinyint(1)")
	public boolean isCreditable() {
		return creditable;
	}

	public void setCreditable(boolean creditable) {
		this.creditable = creditable;
	}

	@Column(name = "BIR_ATC_ID")
	public Integer getBirAtcId() {
		return birAtcId;
	}

	public void setBirAtcId(Integer birAtcId) {
		this.birAtcId = birAtcId;
	}

	@Column(name = "WT_TYPE_ID")
	public Integer getWtTypeId() {
		return wtTypeId;
	}

	public void setWtTypeId(Integer wtTypeId) {
		this.wtTypeId = wtTypeId;
	}

	@Override
	public String toString() {
		return "WithholdingTaxAcctSetting [companyId=" + companyId + ", name=" + name + ", value=" + value
				+ ", birAtcId=" + birAtcId + ", wtTypeId=" + wtTypeId + ", acctCombinationId=" + acctCombinationId
				+ ", active=" + active + ", orderId=" + orderId + ", creditable=" + creditable + "]";
	}
}
