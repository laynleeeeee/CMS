package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation of AP_INVOICE_LINE table.

 *
 */
@Entity
@Table(name="AP_INVOICE_LINE")
public class ApInvoiceLine extends OtherCharge {
	@Expose
	private Integer apInvoiceId;
	@Expose
	private Integer apLineSetupId;
	@Expose
	private String apLineSetupName;
	@Expose
	private Integer discountTypeId;
	@Expose
	private Double discountValue;
	@Expose
	private Double discount;
	private ApLineSetup apLineSetup;
	private ItemDiscountType itemDiscountType;
	@Expose
	private Double percentile;
	@Expose
	private Integer refenceObjectId;

	/**
	 * Object Type Id for {@link ApInvoiceLine} = 2;
	 */
	public static final int OBJECT_TYPE_ID = 2;

	public enum FIELD {
		id, apInvoiceId, apLineSetupId, unitOfMeasurementId, amount, upAmount, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AP_INVOICE_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="AP_INVOICE_ID", columnDefinition="int(10)")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	@Column(name="AP_LINE_SETUP_ID", columnDefinition="int(10)")
	public Integer getApLineSetupId() {
		return apLineSetupId;
	}

	public void setApLineSetupId(Integer apLineSetupId) {
		this.apLineSetupId = apLineSetupId;
	}

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "AP_LINE_SETUP_ID", insertable=false, updatable=false)
	public ApLineSetup getApLineSetup() {
		return apLineSetup;
	}

	public void setApLineSetup(ApLineSetup apLineSetup) {
		this.apLineSetup = apLineSetup;
	}

	@Transient
	public String getApLineSetupName() {
		return apLineSetupName;
	}

	public void setApLineSetupName(String apLineSetupName) {
		this.apLineSetupName = apLineSetupName;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name = "DISCOUNT_TYPE_ID")
	public Integer getDiscountTypeId() {
		return discountTypeId;
	}

	public void setDiscountTypeId(Integer discountTypeId) {
		this.discountTypeId = discountTypeId;
	}

	@OneToOne
	@JoinColumn(name = "DISCOUNT_TYPE_ID", insertable=false, updatable=false)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}

	@Column(name = "DISCOUNT_VALUE")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@Column(name = "DISCOUNT")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	@Column(name = "PERCENTILE", columnDefinition = "double")
	public Double getPercentile() {
		return percentile;
	}

	public void setPercentile(Double percentile) {
		this.percentile = percentile;
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
		builder.append("ApInvoiceLine [apInvoiceId=").append(apInvoiceId).append(", apLineSetupId=")
				.append(apLineSetupId).append(", apLineSetupName=").append(apLineSetupName).append("]");
		return builder.toString();
	}
}
