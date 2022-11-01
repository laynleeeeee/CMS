package eulap.eb.payroll;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * The employee DTR dto.
 * Object data that represents the important data from parsed
 * DTR file from biometrics.

 *
 */
public class EmployeeDtr implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6996001272666248915L;
	private Integer rowNum;
	private Integer employeeId;
	private String number;
	private Integer biometricId;
	private String employeeName;
	private Date date;

	public static EmployeeDtr getInstanceOf(Integer employeeId, Date date) {
		EmployeeDtr dtr = new EmployeeDtr();
		dtr.employeeId = employeeId;
		dtr.date = date;
		return dtr;
	}

	public static EmployeeDtr getInstanceOf(String number, Integer biometricId, String employeeName, 
			Date date) throws ParseException {
		EmployeeDtr dtr = new EmployeeDtr();
		dtr.number = number;
		dtr.biometricId = biometricId;
		dtr.employeeName = employeeName;
		dtr.date = date;
		return dtr;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getBiometricId() {
		return biometricId;
	}

	public void setBiometricId(Integer biometricId) {
		this.biometricId = biometricId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "EmployeeDtr [rowNum=" + rowNum + ", employeeId=" + employeeId + ", number=" + number + ", biometricId="
				+ biometricId + ", employeeName=" + employeeName + ", date=" + date + "]";
	}
}
