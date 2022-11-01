package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.util.DateUtil;

/**
 * Object representation class of FORM_DEDUCTION_LINE table.

 *
 */
@Entity
@Table(name = "FORM_DEDUCTION_LINE")
public class FormDeductionLine extends BaseFormLine{

	@Expose
	private Integer referenceObjectId;
	@Expose
	private Date date;
	@Expose
	private Double amount;

	/**
	 * Object Type of Form Deduction Lines.
	 */
	public static final int OBJECT_TYPE_ID = 70;

	public enum FIELD{
		id, ebObjectId, date, amount
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FORM_DEDUCTION_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "AMOUNT", columnDefinition = "DOUBLE")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Transient
	public String getDeductionDate() {
		return DateUtil.formatDate(date);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FormDeductionLine [referenceObjectId=").append(referenceObjectId).append(", date=").append(date)
				.append(", amount=").append(amount).append(", getEbObjectId()=").append(getEbObjectId()).append("]");
		return builder.toString();
	}

}
