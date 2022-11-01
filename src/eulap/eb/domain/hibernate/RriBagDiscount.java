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
 * A class that represents RRI_BAG_DISCOUNT table in the database.

 *
 */
@Entity
@Table(name="RRI_BAG_DISCOUNT")
public class RriBagDiscount extends BaseFormLine{

	@Expose
	private Double bagQuantity;
	@Expose
	private Double discountQuantity;
	private Boolean active;
	@Expose
	private Integer refenceObjectId;

	/**
	 * Object type ID for RRI_BAG_DISCOUNT = 137.
	 */
	public static final int RRI_BAG_DISCOUNT_OBJECT_TYPE_ID = 137;

	public enum FIELD {
		id, ebObjectId, bagQuantity, discountQuantity, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "RRI_BAG_DISCOUNT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="BAG_QUANTITY", columnDefinition="DOUBLE")
	public Double getBagQuantity() {
		return bagQuantity;
	}

	public void setBagQuantity(Double bagQuantity) {
		this.bagQuantity = bagQuantity;
	}

	@Column(name="DISCOUNT_QUANTITY", columnDefinition="DOUBLE")
	public Double getDiscountQuantity() {
		return discountQuantity;
	}

	public void setDiscountQuantity(Double discountQuantity) {
		this.discountQuantity = discountQuantity;
	}

	@Column(name="ACTIVE", columnDefinition="TINYINT(1)")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return RRI_BAG_DISCOUNT_OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RriBagDiscount [ebObjectId=").append(getEbObjectId()).append(", bagQuantity=").append(bagQuantity)
				.append(", discountQuantity=").append(discountQuantity).append(", active=").append(active)
				.append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}

}
