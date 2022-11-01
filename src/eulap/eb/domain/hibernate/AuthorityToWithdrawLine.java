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
 * Object representation class for AR_INVOICE_LINE table

 */

@Entity
@Table(name="AUTHORITY_TO_WITHDRAW_LINE")
public class AuthorityToWithdrawLine extends AROtherCharge {
	@Expose
	private Integer authorityToWithdrawId;
	@Expose
	private Double discountValue;
	@Expose
	private Double discount;
	@Expose
	private Integer refenceObjectId;

	public static final int OBJECT_TYPE_ID = 12024;

	public enum FIELD {
		id, authorityToWithdrawId, ebObjectId, refenceObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AUTHORITY_TO_WITHDRAW_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AUTHORITY_TO_WITHDRAW_ID")
	public Integer getAuthorityToWithdrawId() {
		return authorityToWithdrawId;
	}

	public void setAuthorityToWithdrawId(Integer authorityToWithdrawId) {
		this.authorityToWithdrawId = authorityToWithdrawId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorityToWithdrawLine [authorityToWithdrawId=").append(authorityToWithdrawId)
				.append(", discountValue=").append(discountValue).append(", discount=").append(discount)
				.append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}
}
