package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation class of FORM_DEDUCTION table.

 *
 */
@Entity
@Table(name = "FORM_DEDUCTION")
public class FormDeduction extends BaseFormWorkflow {

	private Integer companyId;
	private Company company;
	private Integer employeeId;
	private Employee employee;
	private Integer deductionTypeId;
	private DeductionType deductionType;
	private Integer formDeductionTypeId;
	private FormDeductionType formDeductionType;
	private Integer divisionId;
	private Division division;
	private Integer sequenceNumber;
	private Date formDate;
	private Date startDate;
	private String remarks;
	private List<FormDeductionLine> formDeductionLines;
	private List<ReferenceDocument> referenceDocuments;
	private String formDeductionMessage;
	private String referenceDocJson;
	private String referenceDocsMessage;
	private String employeePosition;
	private String employeeFullName;
	private Integer noOfPayrollDeduction;
	private Double totalDeductionAmount;
	/**
	 * Object Type of Form Deduction.
	 */
	public static final int OBJECT_TYPE_ID = 69;

	public enum FIELD{
		ebObjectId, companyId, employeeId, deductionTypeId, sequenceNumber, 
		formDate, startDate, formDeductionTypeId, formWorkflowId
	}
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FORM_DEDUCTION_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "COMPANY_ID", columnDefinition = "INT(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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

	@Column(name = "DEDUCTION_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getDeductionTypeId() {
		return deductionTypeId;
	}

	public void setDeductionTypeId(Integer deductionTypeId) {
		this.deductionTypeId = deductionTypeId;
	}

	@OneToOne
	@JoinColumn(name = "DEDUCTION_TYPE_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public DeductionType getDeductionType() {
		return deductionType;
	}

	public void setDeductionType(DeductionType deductionType) {
		this.deductionType = deductionType;
	}

	@Column(name = "SEQUENCE_NO", columnDefinition = "INT(10)")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "FORM_DATE", columnDefinition = "DATE")
	public Date getFormDate() {
		return formDate;
	}

	public void setFormDate(Date formDate) {
		this.formDate = formDate;
	}

	@Column(name = "START_DATE", columnDefinition = "DATE")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "REMARKS", columnDefinition = "TEXT")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Transient
	public List<FormDeductionLine> getFormDeductionLines() {
		return formDeductionLines;
	}

	public void setFormDeductionLines(List<FormDeductionLine> formDeductionLines) {
		this.formDeductionLines = formDeductionLines;
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getFormDeductionMessage() {
		return formDeductionMessage;
	}

	public void setFormDeductionMessage(String formDeductionMessage) {
		this.formDeductionMessage = formDeductionMessage;
	}

	@Transient
	public String getReferenceDocJson() {
		return referenceDocJson;
	}

	public void setReferenceDocJson(String referenceDocJson) {
		this.referenceDocJson = referenceDocJson;
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public void serializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocJson, type);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if(formDeductionLines != null){
			children.addAll(formDeductionLines);
		}
		if(referenceDocuments != null){
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Column(name = "DIVISION_ID", columnDefinition = "INT(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Column(name = "FORM_DEDUCTION_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getFormDeductionTypeId() {
		return formDeductionTypeId;
	}

	public void setFormDeductionTypeId(Integer formDeductionTypeId) {
		this.formDeductionTypeId = formDeductionTypeId;
	}

	@OneToOne
	@JoinColumn(name = "FORM_DEDUCTION_TYPE_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public FormDeductionType getFormDeductionType() {
		return formDeductionType;
	}

	public void setFormDeductionType(FormDeductionType formDeductionType) {
		this.formDeductionType = formDeductionType;
	}

	@Transient
	@Override
	public String getWorkflowName() {
		return super.getWorkflowName() + formDeductionTypeId;
	}

	@Transient
	public String getEmployeePosition() {
		return employeePosition;
	}

	public void setEmployeePosition(String employeePosition) {
		this.employeePosition = employeePosition;
	}

	@Transient
	public String getEmployeeFullName() {
		return employeeFullName;
	}

	public void setEmployeeFullName(String employeeFullName) {
		this.employeeFullName = employeeFullName;
	}

	@Transient
	public Integer getNoOfPayrollDeduction() {
		return noOfPayrollDeduction;
	}

	public void setNoOfPayrollDeduction(Integer noOfPayrollDeduction) {
		this.noOfPayrollDeduction = noOfPayrollDeduction;
	}

	@Transient
	public Double getTotalDeductionAmount() {
		return totalDeductionAmount;
	}

	public void setTotalDeductionAmount(Double totalDeductionAmount) {
		this.totalDeductionAmount = totalDeductionAmount;
	}

	@Override
	public String toString() {
		return "FormDeduction [ebObjectId=" + getEbObjectId() + ", companyId=" + companyId + ", company=" + company
				+ ", employeeId=" + employeeId + ", employee=" + employee + ", deductionTypeId=" + deductionTypeId
				+ ", deductionType=" + deductionType + ", formDeductionTypeId=" + formDeductionTypeId
				+ ", formDeductionType=" + formDeductionType + ", divisionId=" + divisionId + ", division=" + division
				+ ", ebObject=" + getEbObject() + ", sequenceNumber=" + sequenceNumber + ", formDate=" + formDate
				+ ", startDate=" + startDate + ", remarks=" + remarks + ", formDeductionLines=" + formDeductionLines
				+ ", referenceDocuments=" + referenceDocuments + ", formDeductionMessage=" + formDeductionMessage
				+ ", referenceDocJson=" + referenceDocJson + ", referenceDocsMessage=" + referenceDocsMessage
				+ ", employeePosition=" + employeePosition + ", employeeFullName="
				+ employeeFullName + ", noOfPayrollDeduction=" + noOfPayrollDeduction + ", totalDeductionAmount="
				+ totalDeductionAmount + "]";
	}
}
