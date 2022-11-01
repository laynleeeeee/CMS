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
import org.springframework.validation.BindingResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation of EMPLOYEE_LEAVE_CREDIT table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_LEAVE_CREDIT")
public class EmployeeLeaveCredit extends BaseFormWorkflow{
	private Integer companyId;
	private Company company;
	private Integer typeOfLeaveId;
	private TypeOfLeave typeOfLeave;
	private Integer sequenceNumber;
	private Date date;
	private String remarks;
	private List<EmployeeLeaveCreditLine> elcLines;
	private List<ReferenceDocument> referenceDocuments;
	private String leavesJson;
	private String leavesMessage;
	private String referenceDocJson;
	private String referenceDocsMessage;
	private BindingResult bindingResult;
	private Integer divisionId;
	private Division division;
	/**
	 * Object type of EMPLOYEE_LEAVE_CREDIT
	 */
	public static final int OBJECT_TYPE_ID = 67;

	public enum FIELD{
		id, formWorkflowId, ebObjectId, typeOfLeaveId, sequenceNumber, date, remarks, companyId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_LEAVE_CREDIT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
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

	@Column(name = "TYPE_OF_LEAVE_ID", columnDefinition = "INT(10)")
	public Integer getTypeOfLeaveId() {
		return typeOfLeaveId;
	}

	public void setTypeOfLeaveId(Integer typeOfLeaveId) {
		this.typeOfLeaveId = typeOfLeaveId;
	}

	@OneToOne
	@JoinColumn(name = "TYPE_OF_LEAVE_ID", insertable = false, updatable = false)
	public TypeOfLeave getTypeOfLeave() {
		return typeOfLeave;
	}

	public void setTypeOfLeave(TypeOfLeave typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}

	@Column(name = "SEQUENCE_NO", columnDefinition = "INT(10)")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "REMARKS", columnDefinition = "TEXT")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Transient
	public List<EmployeeLeaveCreditLine> getElcLines() {
		return elcLines;
	}

	public void setElcLines(List<EmployeeLeaveCreditLine> elcLines) {
		this.elcLines = elcLines;
	}

	@Transient
	public void serializeLeavesLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		leavesJson = gson.toJson(elcLines);
	}

	@Transient
	public void deserializeLeavesLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<EmployeeLeaveCreditLine>>(){}.getType();
		elcLines = gson.fromJson(leavesJson, type);
	}
	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
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
	@Transient
	public String getLeavesJson() {
		return leavesJson;
	}

	public void setLeavesJson(String leavesJson) {
		this.leavesJson = leavesJson;
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

	@Transient
	public BindingResult getBindingResult() {
		return bindingResult;
	}

	public void setBindingResult(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(elcLines != null){
			children.addAll(elcLines);
		}
		if(referenceDocuments != null){
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Transient
	public String getLeavesMessage() {
		return leavesMessage;
	}

	public void setLeavesMessage(String leavesMessage) {
		this.leavesMessage = leavesMessage;
	}

	@Override
	public String toString() {
		return "EmployeeLeaveCredit [ebObjectId=" + getEbObjectId() + ", typeOfLeaveId=" + typeOfLeaveId + ", typeOfLeave="
				+ typeOfLeave + ", ebObject=" + getEbObject() + ", sequenceNumber=" + sequenceNumber + ", date=" + date
				+ ", remarks=" + remarks + ", elcLines=" + elcLines + ", referenceDocuments=" + referenceDocuments
				+ ", leavesJson=" + leavesJson + ", referenceDocJson=" + referenceDocJson + "]";
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

}
