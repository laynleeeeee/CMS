package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Domain object class representation for PURCHASE_ORDER_LINE table

 */

@Entity
@Table(name="PURCHASE_ORDER_LINE")
public class PurchaseOrderLine extends OtherCharge {
	@Expose
	private Integer purchaseOrderId;
	@Expose
	private Integer apLineSetupId;
	@Expose
	private String apLineSetupName;
	private ApLineSetup apLineSetup;

	/**
	 * Object type id for PO other charges line
	 */
	public static final int PO_LINE_OBJ_TYPE_ID = 21000;

	public enum FIELD {
		id, purchaseOrderId, apLineSetupId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PURCHASE_ORDER_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="R_PURCHASE_ORDER_ID", columnDefinition="int(10)")
	public Integer getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Integer purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	@Column(name="AP_LINE_SETUP_ID", columnDefinition="int(10)")
	public Integer getApLineSetupId() {
		return apLineSetupId;
	}

	public void setApLineSetupId(Integer apLineSetupId) {
		this.apLineSetupId = apLineSetupId;
	}

	@ManyToOne
	@JoinColumn (name="AP_LINE_SETUP_ID", insertable=false, updatable=false)
	public ApLineSetup getApLineSetup() {
		return apLineSetup;
	}

	public void setApLineSetup(ApLineSetup apLineSetup) {
		this.apLineSetup = apLineSetup;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return PO_LINE_OBJ_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Transient
	public String getApLineSetupName() {
		return apLineSetupName;
	}

	public void setApLineSetupName(String apLineSetupName) {
		this.apLineSetupName = apLineSetupName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderLine [purchaseOrderId=").append(purchaseOrderId).append(", apLineSetupId=")
				.append(apLineSetupId).append(", apLineSetupName=").append(apLineSetupName).append("]");
		return builder.toString();
	}
}
