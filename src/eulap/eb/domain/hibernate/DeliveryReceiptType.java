package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation for DELIVERY_RECEIPT_TYPE table.
 * 

 */

@Entity
@Table(name = "DELIVERY_RECEIPT_TYPE")
public class DeliveryReceiptType extends BaseDomain {
	private String name;
	private boolean active;

	public enum FIELD {
		id, name, active
	}

	public static final int DR_TYPE_ID = 1;//Delivery receipt - Item
	public static final int WAYBILL_DR_TYPE_ID = 2;
	public static final int EQ_UTIL_DR_TYPE_ID = 3;
	public static final int DR_SERVICE_TYPE_ID = 4;
	public static final int DR_CENTRAL_TYPE_ID = 5;
	public static final int DR_NSB3_TYPE_ID = 6;
	public static final int DR_NSB4_TYPE_ID = 7;
	public static final int DR_NSB5_TYPE_ID = 8;
	public static final int DR_NSB8_TYPE_ID = 9;
	public static final int DR_NSB8A_TYPE_ID = 10;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "DELIVERY_RECEIPT_TYPE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeliveryReceiptType [name=").append(name).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
