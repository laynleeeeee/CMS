package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * A class that represents the WITHDRAWAL_SLIP_ITEM table.

 *
 */
@Entity
@Table(name = "WITHDRAWAL_SLIP_ITEM")
public class WithdrawalSlipItem extends BaseItem {
	@Expose
	private Double srp;
	@Expose
	private Integer buyingAddOnId;
	@Expose
	private Integer buyingDiscountId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Double prevUC;
	private boolean active;

	public static final int OBJECT_TYPE = 97;

	/**
	 * OR Type for Purchase Order Item to Withdrawal Slip Item Relationship = 73.
	 */
	public static final int OR_TYPE_ID = 73;

	public enum FIELD {
		id, active, ebObjectId, itemId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "WITHDRAWAL_SLIP_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Double getSrp() {
		return srp;
	}

	public void setSrp(Double srp) {
		this.srp = srp;
	}

	@Transient
	public Integer getBuyingAddOnId() {
		return buyingAddOnId;
	}

	public void setBuyingAddOnId(Integer buyingAddOnId) {
		this.buyingAddOnId = buyingAddOnId;
	}

	@Transient
	public Integer getBuyingDiscountId() {
		return buyingDiscountId;
	}

	public void setBuyingDiscountId(Integer buyingDiscountId) {
		this.buyingDiscountId = buyingDiscountId;
	}

	@Transient
	public Double getPrevUC() {
		return prevUC;
	}

	public void setPrevUC(Double prevUC) {
		this.prevUC = prevUC;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setReferenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Override
	public String toString() {
		return "WithdrawalSlipItem [srp=" + srp + ", buyingAddOnId=" + buyingAddOnId + ", buyingDiscountId="
				+ buyingDiscountId + ", ebObjectId=" + getEbObjectId() + ", ebObject=" + getEbObject() + ", refenceObjectId="
				+ refenceObjectId + ", getId()=" + getId() + "]";
	}
}
