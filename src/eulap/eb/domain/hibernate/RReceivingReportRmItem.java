package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation of R_RECEIVING_REPORT_RM_ITEM table.

 *
 */
@Entity
@Table(name="R_RECEIVING_REPORT_RM_ITEM")
public class RReceivingReportRmItem extends BaseDomain implements OOChild{
	private Integer rReceivingReportItemId;
	private Integer ebObjectId;
	private EBObject ebObject;
	private Integer itemBDiscountId;
	private Integer itemBAddOnId;
	private Double discount;
	private Double addOn;
	private Double amount;

	public static final int OBJECT_TYPE_ID = 3;

	public enum FIELD {
		id, rReceivingReportItemId, itemBuyingDiscountId, itembuyingAddOnId,
		discount, addOn, amount
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_RECEIVING_REPORT_RM_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="R_RECEIVING_REPORT_ITEM_ID", columnDefinition="int(10)")
	public Integer getrReceivingReportItemId() {
		return rReceivingReportItemId;
	}

	public void setrReceivingReportItemId(Integer rReceivingReportItemId) {
		this.rReceivingReportItemId = rReceivingReportItemId;
	}

	@Column(name="ITEM_BUYING_DISCOUNT_ID", columnDefinition="int(10)")
	public Integer getItemBDiscountId() {
		return itemBDiscountId;
	}

	public void setItemBDiscountId(Integer itemBDiscountId) {
		this.itemBDiscountId = itemBDiscountId;
	}

	@Column(name="ITEM_BUYING_ADD_ON_ID", columnDefinition="int(10)")
	public Integer getItemBAddOnId() {
		return itemBAddOnId;
	}

	public void setItemBAddOnId(Integer itemBAddOnId) {
		this.itemBAddOnId = itemBAddOnId;
	}

	@Column(name="DISCOUNT", columnDefinition="double")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	@Column(name="ADD_ON", columnDefinition="double")
	public Double getAddOn() {
		return addOn;
	}

	public void setAddOn(Double addOn) {
		this.addOn = addOn;
	}

	@Column(name="AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RReceivingReportRmItem [rReceivingReportItemId=")
				.append(rReceivingReportItemId).append(", itemBDiscountId=")
				.append(itemBDiscountId).append(", itemBAddOnId=")
				.append(itemBAddOnId).append(", discount=").append(discount)
				.append(", addOn=").append(addOn).append(", amount=")
				.append(amount).append("]");
		return builder.toString();
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	@Override
	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		// R_RECEIVING_REPORT_RM_ITEM type in OBJECT_TYPE table.
		return 3;
	}

}
