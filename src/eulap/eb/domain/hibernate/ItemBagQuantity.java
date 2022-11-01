package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * A class that represents ITEM_BAG_QUANTITY in the database.

 *
 */
@Entity
@Table(name="ITEM_BAG_QUANTITY")
public class ItemBagQuantity extends BaseFormLine{
	private Integer itemId;
	private Double quantity;
	private boolean active;
	private Integer sourceObjectId;
	private String sourceForm;

	public static final int OBJECT_TYPE_ID = 135;

	/**
	 * OR TYPE for SAI-Bag-Qty-Relationship = 84.
	 */
	public static final int SAI_BAG_QTY = 84;

	/**
	 * OR TYPE for CS-IS-Bag-Qty-Relationship = 86.
	 */
	public static final int CS_IS_BAG_QTY = 86;

	/**
	 * OR TYPE for CSR-IS-Bag-Qty-Relationship = 85.
	 */
	public static final int CSR_IS_BAG_QTY = 85;

	/**
	 * OR TYPE for AS-IS-Bag-Qty-Relationship = 87.
	 */
	public static final int AS_IS_BAG_QTY = 87;

	/**
	 * OR TYPE for ASR-IS-Bag-Qty-Relationship = 88.
	 */
	public static final int ASR_IS_BAG_QTY = 88;

	/**
	 * OR TYPE for TR-IS-Bag-Qty-Relationship = 89.
	 */
	public static final int TR_IS_BAG_QTY = 89;

	/**
	 * OR TYPE for CAP-IS-Bag-Qty-Relationship = 90.
	 */
	public static final int CAP_IS_BAG_QTY = 90;

	/**
	 * OR TYPE for PIAD-IS-Bag-Qty-Relationship = 91.
	 */
	public static final int PIAD_IS_BAG_QTY = 91;

	/**
	 * OR TYPE for SAO-Bag-Qty-Relationship = 91.
	 */
	public static final int SAO_BAG_QTY = 92;

	/**
	 * OR TYPE for PR-MAIN-RAW-MAT-Bag-Relationship = 95
	 */
	public static final int PR_MAIN_RAW_MAT_BAG_QTY = 95;

	/**
	 * OR TYPE for PR-OTHER-MAT-Bag-Relationship = 96
	 */
	public static final int PR_OTHER_MAT_BAG_QTY = 96;

	/**
	 * OR TYPE for PR-MAIN-PRODUCT-Bag-Relationship = 97
	 */
	public static final int PR_MAIN_PRODUCT_BAG_QTY = 97;

	/**
	 * OR TYPE for PR-BY-PRODUCT-Bag-Relationship = 98
	 */
	public static final int PR_BY_PRODUCT_BAG_QTY = 98;

	public enum FIELD {
		id, ebObjectId, quantity, active
	}

	/**
	 * Create an instance of {@code ItemBagQuantity}
	 * @param itemId The item id.
	 * @param ebObjectId The eb object id.
	 * @param quantity The quantity in bag/s.
	 * @return The instance of {@code ItemBagQuantity}
	 */
	public static ItemBagQuantity getInstanceOf(Integer itemId, Integer ebObjectId, Double quantity) {
		ItemBagQuantity ibq = new ItemBagQuantity();
		ibq.setItemId(itemId);
		ibq.setEbObjectId(ebObjectId);
		ibq.setQuantity(quantity);
		ibq.setActive(true);
		return ibq;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_BAG_QUANTITY_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "ITEM_ID", columnDefinition="int(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Integer getSourceObjectId() {
		return sourceObjectId;
	}

	public void setSourceObjectId(Integer sourceObjectId) {
		this.sourceObjectId = sourceObjectId;
	}

	@Transient
	public String getSourceForm() {
		return sourceForm;
	}

	public void setSourceForm(String sourceForm) {
		this.sourceForm = sourceForm;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemBagQuantity [itemId=").append(itemId).append(", ebObjectId=").append(getEbObjectId())
				.append(", quantity=").append(quantity).append(", active=").append(active).append("]");
		return builder.toString();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}
}
