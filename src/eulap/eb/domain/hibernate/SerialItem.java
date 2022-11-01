package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.oo.OOChild;

/**
 * Object representation of Serial Item table.

 */

@Entity
@Table(name = "SERIAL_ITEM")
public class SerialItem extends SaleItem implements OOChild {
	@Expose
	private String serialNumber;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private String description;
	@Expose
	private String uom;
	@Expose
	private boolean active;
	@Expose
	private Integer itemDiscountTypeId;
	@Expose
	private Double discountValue;
	private String source;
	private ItemDiscountType itemDiscountType;
	@Expose
	private String poNumber;
	@Expose
	private Integer origItemId;
	@Expose
	private Double origUnitCost;

	/**
	 * Object Type Id for SERIAL_ITEM
	 */
	public static final int OBJECT_TYPE_ID = 105;
	public static final int MAX_SERIAL_NUMBER = 50;

	public enum FIELD {
		id, itemId, ebObjectId, quantity, unitCost, serialNumber, warehouseId,
		updatedBy, createdBy, createdDate, updatedDate, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "SERIAL_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="SERIAL_NUMBER", columnDefinition="varchar(50)")
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Column (name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Transient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	@Transient
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name = "ITEM_DISCOUNT_TYPE_ID")
	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
	}

	@Column(name = "DISCOUNT_VALUE")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_DISCOUNT_TYPE_ID", insertable=false, updatable=false)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}

	@Column(name = "PO_NUMBER")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SerialItem [serialNumber=").append(serialNumber).append(", refenceObjectId=")
				.append(refenceObjectId).append(", active=").append(active).append(", description=")
				.append(description).append(", uom=").append(uom).append(", source=").append(source)
				.append(", poNumber=").append(poNumber).append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}

	@Transient
	public Integer getOrigItemId() {
		return origItemId;
	}

	public void setOrigItemId(Integer origItemId) {
		this.origItemId = origItemId;
	}

	@Transient
	public Double getOrigUnitCost() {
		return origUnitCost;
	}

	public void setOrigUnitCost(Double origUnitCost) {
		this.origUnitCost = origUnitCost;
	}
}
