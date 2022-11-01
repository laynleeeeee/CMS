package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Domain object representation class for AR_RECEIPT_LINE

 */

@Entity
@Table(name="AR_RECEIPT_LINE")
public class ArReceiptLine extends BaseFormLine {
	@Expose
	private Integer arReceiptId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private String referenceNo;
	@Expose
	private Double amount;
	@Expose
	private Integer arReceiptLineTypeId;
	@Expose
	private Double currencyRateValue;
	private String remarks;

	public static final int OBJECT_TYPE_ID = 24006;

	public enum FIELD {
		id, arReceiptId, arReceiptLineTypeId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_RECEIPT_LINE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AR_RECEIPT_ID")
	public Integer getArReceiptId() {
		return arReceiptId;
	}

	public void setArReceiptId(Integer arReceiptId) {
		this.arReceiptId = arReceiptId;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Transient
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
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
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name = "AR_RECEIPT_LINE_TYPE_ID")
	public Integer getArReceiptLineTypeId() {
		return arReceiptLineTypeId;
	}

	public void setArReceiptLineTypeId(Integer arReceiptLineTypeId) {
		this.arReceiptLineTypeId = arReceiptLineTypeId;
	}

	@Transient
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArReceiptLine [arReceiptId=").append(arReceiptId)
				.append(", refenceObjectId=").append(refenceObjectId).append(", referenceNo=").append(referenceNo)
				.append(", amount=").append(amount).append("]");
		return builder.toString();
	}

}
