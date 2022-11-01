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
 * Object representation of ELC_LINE table.

 *
 */
@Entity
@Table(name = "ELC_LINE")
public class EmployeeLeaveCreditLine extends BaseFormLine{

	@Expose
	private Integer referenceObjectId;
	@Expose
	private Integer employeeId;
	private Employee employee;
	@Expose
	private String employeeName;
	@Expose
	private Double availableLeaves;
	@Expose
	private Double deductDebit;
	@Expose
	private Double addCredit;
	@Expose
	private Double totalLeaves;

	/**
	 * Object type of EmployeeLeaveCreditLine.
	 */
	public final static int OBJECT_TYPE_ID = 68;

	public enum FIELD {
		id, companyId, employeeId, ebObjectId, deductDebit, addCredit, availableLeaves
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "ELC_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Column(name = "AVAILABLE_LEAVES", columnDefinition = "DOUBLE(4,2)")
	public Double getAvailableLeaves() {
		return availableLeaves;
	}

	public void setAvailableLeaves(Double availableLeaves) {
		this.availableLeaves = availableLeaves;
	}

	@Column(name = "DEDUCT_DEBIT", columnDefinition = "DOUBLE(4,2))")
	public Double getDeductDebit() {
		return deductDebit;
	}

	public void setDeductDebit(Double deductDebit) {
		this.deductDebit = deductDebit;
	}

	@Column(name = "ADD_CREDIT", columnDefinition = "DOUBLE(4,2)")
	public Double getAddCredit() {
		return addCredit;
	}

	public void setAddCredit(Double addCredit) {
		this.addCredit = addCredit;
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
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Transient
	public Double getTotalLeaves() {
		return totalLeaves;
	}

	public void setTotalLeaves(Double totalLeaves) {
		this.totalLeaves = totalLeaves;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeLeaveCreditLine [referenceObjectId=").append(referenceObjectId).append(", employeeId=")
				.append(employeeId).append(", employee=").append(employee).append(", employeeName=")
				.append(employeeName).append(", availableLeaves=").append(availableLeaves).append(", deductDebit=")
				.append(deductDebit).append(", addCredit=").append(addCredit).append(", totalLeaves=")
				.append(totalLeaves).append(", getEbObjectId()=").append(getEbObjectId()).append("]");
		return builder.toString();
	}
}
