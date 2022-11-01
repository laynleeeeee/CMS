package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * Object representation of CAP_DELIVERY_AR_LINE table.

 *
 */
@Entity
@Table(name="CAP_DELIVERY_AR_LINE")
public class CAPDeliveryArLine extends AROtherCharge {
	private Integer capDeliveryId;

	/**
	 * Object type id for {@link CAPDeliveryArLine} = 55
	 */
	public static final int OBJECT_TYPE_ID = 55;

	/**
	 * Get the instance of {@link CAPDeliveryArLine}.
	 */
	public static CAPDeliveryArLine getInstanceOf (Integer arLineSetupId, Integer uomId, Double quantity,
			Double upAmount, Double amount, ArLineSetup arLineSetup, UnitMeasurement measurement) {
		CAPDeliveryArLine arLine = new CAPDeliveryArLine();
		arLine.setArLineSetupId(arLineSetupId);
		arLine.setUnitOfMeasurementId(uomId);
		arLine.setQuantity(quantity);
		arLine.setUpAmount(upAmount);
		arLine.setAmount(amount);
		if(arLineSetup != null) {
			arLine.setArLineSetup(arLineSetup);
			arLine.setArLineSetupName(arLineSetup.getName());
		}
		if(measurement != null) {
			arLine.setUnitMeasurement(measurement);
			arLine.setUnitMeasurementName(measurement.getName());
		}
		return arLine;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CAP_DELIVERY_AR_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="CAP_DELIVERY_ID", columnDefinition="int(10)")
	public Integer getCapDeliveryId() {
		return capDeliveryId;
	}

	public void setCapDeliveryId(Integer capDeliveryId) {
		this.capDeliveryId = capDeliveryId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		// No reference for this object.
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CAPDeliveryArLine [capDeliveryId=").append(capDeliveryId).append(", ebObjectId=")
				.append(getEbObjectId()).append(", ebObject=").append(getEbObject()).append(", getId()=").append(getId())
				.append(", getCapDeliveryId()=").append(getCapDeliveryId()).append(", getArLineSetupId()=")
				.append(getArLineSetupId()).append(", getUnitOfMeasurementId()=").append(getUnitOfMeasurementId())
				.append(", getAmount()=").append(getAmount()).append(", getQuantity()=").append(getQuantity())
				.append(", getUpAmount()=").append(getUpAmount()).append("]");
		return builder.toString();
	}

}
