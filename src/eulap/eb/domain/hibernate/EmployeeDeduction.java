package eulap.eb.domain.hibernate;

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

/**
 * Object representation class of EMPLOYEE_DEDUCTION Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_DEDUCTION")
public class EmployeeDeduction extends BaseFormLine{
	@Expose
	private Integer payrollId;
	@Expose
	private Integer employeeId;
	@Expose
	private Integer deductionTypeId;
	@Expose
	private Double amount;
	@Expose
	private Double paidAmount;
	@Expose
	private boolean fromDeductionForm; 
	@Expose
	private boolean active;
	private String deductionTypeName;
	private DeductionType deductionType;
	@Expose
	private Integer fdlEbObjectId;

	/**
	 * Employee deduction object type = 102.
	 */
	public static final int OBJECT_TYPE_ID = 102;
	public static final int OR_TYPE_ID = 49;

	public enum FIELD {
		id, payrollId, employeeId, deductionTypeId, active, ebObjectId, orTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_DEDUCTION_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="PAYROLL_ID", columnDefinition="INT(10)")
	public Integer getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(Integer payrollId) {
		this.payrollId = payrollId;
	}

	@Column(name="EMPLOYEE_ID", columnDefinition="INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name="DEDUCTION_TYPE_ID", columnDefinition="INT(10)")
	public Integer getDeductionTypeId() {
		return deductionTypeId;
	}

	public void setDeductionTypeId(Integer deductionTypeId) {
		this.deductionTypeId = deductionTypeId;
	}

	@Column(name="AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name="PAID_AMOUNT", columnDefinition="double")
	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	@Column(name="FROM_DEDUCTION_FORM", columnDefinition="tinyint(1)")
	public boolean isFromDeductionForm() {
		return fromDeductionForm;
	}

	public void setFromDeductionForm(boolean fromDeductionForm) {
		this.fromDeductionForm = fromDeductionForm;
	}

	@Transient
	public String getDeductionTypeName() {
		return deductionTypeName;
	}

	public void setDeductionTypeName(String deductionTypeName) {
		this.deductionTypeName = deductionTypeName;
	}

	@OneToOne
	@JoinColumn(name = "DEDUCTION_TYPE_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public DeductionType getDeductionType() {
		return deductionType;
	}

	public void setDeductionType(DeductionType deductionType) {
		this.deductionType = deductionType;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
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

	@Transient
	public Integer getOrTypeId() {
		return OR_TYPE_ID;
	}

	@Transient
	public Integer getFdlEbObjectId() {
		return fdlEbObjectId;
	}

	public void setFdlEbObjectId(Integer fdlEbObjectId) {
		this.fdlEbObjectId = fdlEbObjectId;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeDeduction [payrollId=").append(payrollId).append(", employeeId=").append(employeeId)
				.append(", deductionTypeId=").append(deductionTypeId).append(", amount=").append(amount)
				.append(", paidAmount=").append(paidAmount).append(", fromDeductionForm=").append(fromDeductionForm)
				.append(", active=").append(active).append(", deductionTypeName=").append(deductionTypeName)
				.append(", fdlEbObjectId=").append(fdlEbObjectId)
				.append(", getObjectTypeId()=").append(getObjectTypeId()).append(", getOrTypeId()=")
				.append(getOrTypeId()).append("]");
		return builder.toString();
	}

}
