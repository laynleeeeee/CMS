package eulap.eb.domain.hibernate;

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
 * Object representation of SERIAL_ITEM_SETUP table.

 */
@Entity
@Table(name="SERIAL_ITEM_SETUP")
public class SerialItemSetup extends BaseDomain {
	private Integer itemId;
	private Item item;
	private boolean serializedItem;

	public enum FIELD {
		id, itemId, serializedItem
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "SERIAL_ITEM_SETUP_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="ITEM_ID", columnDefinition="int(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name="SERIALIZED_ITEM", columnDefinition="tinyint(1)")
	public boolean isSerializedItem() {
		return serializedItem;
	}

	public void setSerializedItem(boolean serializedItem) {
		this.serializedItem = serializedItem;
	}

	@OneToOne
	@JoinColumn(name="ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SerialItemSetup [itemId=").append(itemId).append(", serializedItem=").append(serializedItem)
				.append("]");
		return builder.toString();
	}
}
