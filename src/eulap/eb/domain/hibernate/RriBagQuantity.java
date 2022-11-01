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
 * A class that represents RRI_BAG_QUANTITY in the database.

 *
 */
@Entity
@Table (name="RRI_BAG_QUANTITY")
public class RriBagQuantity extends BaseFormLine{
	@Expose
	private Double quantity;
	@Expose
	private Double bagQuantity;
	@Expose
	private Double netWeight;
	private boolean active;
	@Expose
	private Integer refenceObjectId;

	/**
	 * Object type for RRI_BAG_QUANTITY = 136.
	 */
	public static final int RRI_BAG_QUANTITY_OBJECT_TYPE_ID = 136;

	public enum FIELD {
		id, ebObjectId, quantity, bagQuantity, netWeight, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "RRI_BAG_QUANTITY_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Column(name = "BAG_QUANTITY", columnDefinition="double")
	public Double getBagQuantity() {
		return bagQuantity;
	}

	public void setBagQuantity(Double bagQuantity) {
		this.bagQuantity = bagQuantity;
	}

	@Column(name = "NET_WEIGHT", columnDefinition="double")
	public Double getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(Double netWeight) {
		this.netWeight = netWeight;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return RRI_BAG_QUANTITY_OBJECT_TYPE_ID;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RriBagQuantity [ebObjectId=").append(getEbObjectId()).append(", quantity=").append(quantity)
				.append(", bagQuantity=").append(bagQuantity).append(", netWeight=").append(netWeight)
				.append(", active=").append(active).append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}
}
