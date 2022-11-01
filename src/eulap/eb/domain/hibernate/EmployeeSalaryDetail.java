package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object class that represents EMPLOYEE_SALARY_DETAILS
 * from the database

 *
 */
@Entity
@Table(name="EMPLOYEE_SALARY_DETAILS")
public class EmployeeSalaryDetail extends BaseDomain {
	private Integer employeeId;
	private Integer salaryTypeId;
	private double ecola;
	private double basicSalary;
	private double dailySalary;
	private double deMinimis;
	private boolean excludeSss;
	private boolean excludePhic;
	private boolean excludeHdmf;
	private boolean excludeWt;
	private double sssContribution;
	private double addSssContribution;
	private double philHealthContribution;
	private double pagIbigContribution;
	private double addPagIbigContribution;
	private double otherDeduction;
	private double bonus;

	public enum FIELD {
		employeeId, ecola, basicSalary, dailySalary, deMinimis, excludeSss, excludePhic, 
		excludeHdmf, sssContribution, addSssContribution, philHealthContribution, 
		pagIbigContribution, addPagIbigContribution, otherDeduction
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_SALARY_DETAIL_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="EMPLOYEE_ID", columnDefinition="int(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name="SALARY_TYPE_ID", columnDefinition="int(10)")
	public Integer getSalaryTypeId() {
		return salaryTypeId;
	}

	public void setSalaryTypeId(Integer salaryTypeId) {
		this.salaryTypeId = salaryTypeId;
	}

	@Column(name="ECOLA", columnDefinition="double(12,2)")
	public double getEcola() {
		return ecola;
	}

	public void setEcola(double ecola) {
		this.ecola = ecola;
	}

	@Column(name="BASIC_SALARY", columnDefinition="double(12,2)")
	public double getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(double basicSalary) {
		this.basicSalary = basicSalary;
	}

	@Column(name="DAILY_SALARY", columnDefinition="double(12,2)")
	public double getDailySalary() {
		return dailySalary;
	}

	public void setDailySalary(double dailySalary) {
		this.dailySalary = dailySalary;
	}

	@Column(name="DE_MINIMIS", columnDefinition="double(12,2)")
	public double getDeMinimis() {
		return deMinimis;
	}

	public void setDeMinimis(double deMinimis) {
		this.deMinimis = deMinimis;
	}

	@Column(name="EXCLUDE_SSS", columnDefinition="tinyint(1)")
	public boolean isExcludeSss() {
		return excludeSss;
	}

	public void setExcludeSss(boolean excludeSss) {
		this.excludeSss = excludeSss;
	}

	@Column(name="EXCLUDE_PHIC", columnDefinition="tinyint(1)")
	public boolean isExcludePhic() {
		return excludePhic;
	}

	public void setExcludePhic(boolean excludePhic) {
		this.excludePhic = excludePhic;
	}

	@Column(name="EXCLUDE_HDMF", columnDefinition="tinyint(1)")
	public boolean isExcludeHdmf() {
		return excludeHdmf;
	}

	public void setExcludeHdmf(boolean excludeHdmf) {
		this.excludeHdmf = excludeHdmf;
	}

	@Column(name="EXCLUDE_WT", columnDefinition="tinyint(1)")
	public boolean isExcludeWt() {
		return excludeWt;
	}

	public void setExcludeWt(boolean excludeWt) {
		this.excludeWt = excludeWt;
	}

	@Column(name="SSS", columnDefinition="double(12,2)")
	public double getSssContribution() {
		return sssContribution;
	}

	public void setSssContribution(double sssContribution) {
		this.sssContribution = sssContribution;
	}

	@Column(name="SSS_ADDITIONAL", columnDefinition="double(12,2)")
	public double getAddSssContribution() {
		return addSssContribution;
	}

	public void setAddSssContribution(double addSssContribution) {
		this.addSssContribution = addSssContribution;
	}

	@Column(name="PHILHEALTH", columnDefinition="double(12,2)")
	public double getPhilHealthContribution() {
		return philHealthContribution;
	}

	public void setPhilHealthContribution(double philHealthContribution) {
		this.philHealthContribution = philHealthContribution;
	}

	@Column(name="PAG_IBIG", columnDefinition="double(12,2)")
	public double getPagIbigContribution() {
		return pagIbigContribution;
	}

	public void setPagIbigContribution(double pagIbigContribution) {
		this.pagIbigContribution = pagIbigContribution;
	}

	@Column(name="PAG_IBIG_ADDITIONAL", columnDefinition="double(12,2)")
	public double getAddPagIbigContribution() {
		return addPagIbigContribution;
	}

	public void setAddPagIbigContribution(double addPagIbigContribution) {
		this.addPagIbigContribution = addPagIbigContribution;
	}

	@Column(name="OTHER_DEDUCTION", columnDefinition="double(12,2)")
	public double getOtherDeduction() {
		return otherDeduction;
	}

	public void setOtherDeduction(double otherDeduction) {
		this.otherDeduction = otherDeduction;
	}

	@Column(name="BONUS", columnDefinition="double(12,2)")
	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeSalaryDetail [employeeId=").append(employeeId)
				.append(", ecola=").append(ecola).append(", basicSalary=")
				.append(basicSalary).append(", dailySalary=")
				.append(dailySalary).append(", deMinimis=").append(deMinimis)
				.append(", excludeSss=").append(excludeSss)
				.append(", excludePhic=").append(excludePhic)
				.append(", excludeHdmf=").append(excludeHdmf)
				.append(", excludeWt=").append(excludeWt)
				.append(", sssContribution=").append(sssContribution)
				.append(", addSssContribution=").append(addSssContribution)
				.append(", philHealthContribution=")
				.append(philHealthContribution)
				.append(", pagIbigContribution=").append(pagIbigContribution)
				.append(", addPagIbigContribution=")
				.append(addPagIbigContribution).append(", otherDeduction=")
				.append(otherDeduction).append(", bonus=").append(bonus)
				.append("]");
		return builder.toString();
	}
}
