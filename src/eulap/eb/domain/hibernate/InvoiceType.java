package eulap.eb.domain.hibernate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the INVOICE_TYPE table in the database.

 *
 */
@Entity
@Table (name="INVOICE_TYPE")
public class InvoiceType extends BaseDomain {
	private Integer serviceLeaseKeyId;
	private String name;
	private boolean active;

	public enum FIELD {id, serviceLeaseKeyId, name, active}

	public static final int REGULAR_TYPE_ID = 1;
	public static final int PREPAID_TYPE_ID = 2;
	public static final int DEBIT_MEMO_TYPE_ID = 3;
	public static final int CREDIT_MEMO_TYPE_ID = 4;
	public static final int RR_TYPE_ID = 5;
	public static final int RTS_TYPE_ID = 6;
	public static final int RTS_EB_TYPE_ID = 7;
	public static final int RR_RAW_MAT_TYPE_ID = 8;
	public static final int RR_RM_NET_WEIGHT_TYPE_ID = 9;
	public static final int INVOICE_ITEM_TYPE_ID = 10;
	public static final int INVOICE_SERVICE_TYPE_ID = 12;
	public static final int RR_CENTRAL_TYPE_ID = 13;
	public static final int RR_NSB3_TYPE_ID = 14;
	public static final int RR_NSB4_TYPE_ID = 15;
	public static final int RR_NSB5_TYPE_ID = 16;
	public static final int RR_NSB8_TYPE_ID = 17;
	public static final int RR_NSB8A_TYPE_ID = 18;
	public static final int API_NON_PO_CENTRAL = 19;
	public static final int API_NON_PO_NSB3 = 20;
	public static final int API_NON_PO_NSB4 = 21;
	public static final int API_NON_PO_NSB5 = 22;
	public static final int API_NON_PO_NSB8 = 23;
	public static final int API_NON_PO_NSB8A = 24;
	public static final int API_GS_CENTRAL = 25;
	public static final int API_GS_NSB3 = 26;
	public static final int API_GS_NSB4 = 27;
	public static final int API_GS_NSB5 = 28;
	public static final int API_GS_NSB8 = 29;
	public static final int API_GS_NSB8A = 30;
	public static final int RTS_CENTRAL_TYPE_ID = 31;
	public static final int RTS_NSB3_TYPE_ID = 32;
	public static final int RTS_NSB4_TYPE_ID = 33;
	public static final int RTS_NSB5_TYPE_ID = 34;
	public static final int RTS_NSB8_TYPE_ID = 35;
	public static final int RTS_NSB8A_TYPE_ID = 36;

	// AP Invoice confidential
	public static final int API_CONF_CENTRAL = 37;
	public static final int API_CONF_NSB3 = 38;
	public static final int API_CONF_NSB4 = 39;
	public static final int API_CONF_NSB5 = 40;
	public static final int API_CONF_NSB8 = 41;
	public static final int API_CONF_NSB8A = 42;

	// AP Invoice importation
	public static final int API_IMPORT_CENTRAL = 43;
	public static final int API_IMPORT_NSB3 = 44;
	public static final int API_IMPORT_NSB4 = 45;
	public static final int API_IMPORT_NSB5 = 46;
	public static final int API_IMPORT_NSB8 = 47;
	public static final int API_IMPORT_NSB8A = 48;

	//AP Loan
	public static final int AP_LOAN_CENTRAL = 49;
	public static final int AP_LOAN_NSB3 = 50;
	public static final int AP_LOAN_NSB4 = 51;
	public static final int AP_LOAN_NSB5 = 52;
	public static final int AP_LOAN_NSB8 = 53;
	public static final int AP_LOAN_NSB8A = 54;

	//Petty cash replenishment
	public static final int PCR_CENTRAL = 55;
	public static final int PCR_NSB3 = 56;
	public static final int PCR_NSB4 = 57;
	public static final int PCR_NSB5 = 58;
	public static final int PCR_NSB8 = 59;
	public static final int PCR_NSB8A = 60;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "INVOICE_TYPE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EB_SL_KEY_ID", columnDefinition = "INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(int serviceLeaseKey) {
		this.serviceLeaseKeyId = serviceLeaseKey;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(20)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the formatted number and name of the supplier
	 * @return
	 */
	@Transient
	public String getNumberAndName () {
		NumberFormat formatter = new DecimalFormat("####");
		formatter.setMinimumIntegerDigits(10);
		return formatter.format(getId()) + " - " + name;
	}

	@Override
	public String toString() {
		return "InvoiceType [id="+getId()+", serviceLeaseKeyId=" + serviceLeaseKeyId
				+ ", name=" + name + ", active=" + active + "]";
	}
}
