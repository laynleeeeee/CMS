package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

/**
 * Base class for Other Charges that used AR Line Setup.

 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AROtherCharge extends OtherCharge {
	@Expose
	private Integer arLineSetupId;
	@Expose
	private String arLineSetupName;
	@Expose
	private Integer discountTypeId;
	private ArLineSetup arLineSetup;
	private ItemDiscountType itemDiscountType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AR_LINE_SETUP_ID", insertable=false, updatable=false)
	public ArLineSetup getArLineSetup() {
		return arLineSetup;
	}

	public void setArLineSetup(ArLineSetup arLineSetup) {
		this.arLineSetup = arLineSetup;
	}

	@Column(name = "AR_LINE_SETUP_ID", columnDefinition="int(10)")
	public Integer getArLineSetupId() {
		return arLineSetupId;
	}

	public void setArLineSetupId(Integer arLineSetupId) {
		this.arLineSetupId = arLineSetupId;
	}

	@Transient
	public String getArLineSetupName() {
		return arLineSetupName;
	}

	public void setArLineSetupName(String arLineSetupName) {
		this.arLineSetupName = arLineSetupName;
	}

	@OneToOne
	@JoinColumn(name = "DISCOUNT_TYPE_ID", insertable=false, updatable=false)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}

	@Column(name = "DISCOUNT_TYPE_ID")
	public Integer getDiscountTypeId() {
		return discountTypeId;
	}

	public void setDiscountTypeId(Integer discountTypeId) {
		this.discountTypeId = discountTypeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AROtherCharge [arLineSetupId=").append(arLineSetupId).append(", arLineSetupName=")
				.append(arLineSetupName).append(", taxTypeId=").append("]");
		return builder.toString();
	}
}
