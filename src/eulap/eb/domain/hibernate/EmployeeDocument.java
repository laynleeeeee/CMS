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
import javax.persistence.ManyToOne;
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
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.service.oo.OOChild;

/**
 * Object representation class of EMPLOYEE_DOCUMENT Table.

 *
 */
@Entity
@Table(name = "EMPLOYEE_DOCUMENT")
public class EmployeeDocument extends BaseFormWorkflow {
	private Integer sequenceNo;
	private Integer documentTypeId;
	private Integer employeeId;
	private Date date;
	private String remarks;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private BindingResult result;
	private String referenceDocsMessage;
	private Employee employee;
	private DocumentType documentType;
	private Integer companyId;
	private Company company;
	private String employeeName;

	/**
	 * Object type id of EMPLOYEE_DOCUMENT = 126.
	 */
	public static final int OBJECT_TYPE_ID = 126;

	public enum FIELD{
		id, sequenceNo, documentTypeId, formWorkflowId, employeeId, date, remarks, companyId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="EMPLOYEE_DOCUMENT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "DOCUMENT_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getDocumentTypeId() {
		return documentTypeId;
	}

	public void setDocumentTypeId(Integer documentTypeId) {
		this.documentTypeId = documentTypeId;
	}

	@Column(name = "SEQUENCE_NO", columnDefinition = "INT(10)")
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "INT(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "DATE", columnDefinition = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "REMARKS", columnDefinition = "text")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Transient
	public String getReferenceDocumentJson() {
		return referenceDocumentJson;
	}

	public void setReferenceDocumentJson(String referenceDocumentJson) {
		this.referenceDocumentJson = referenceDocumentJson;
	}

	@Transient
	public void serializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentJson, type);
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Transient
	public BindingResult getResult() {
		return result;
	}

	public void setResult(BindingResult result) {
		this.result = result;
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@ManyToOne
	@JoinColumn(name = "EMPLOYEE_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@ManyToOne
	@JoinColumn(name = "DOCUMENT_TYPE_ID", columnDefinition = "INT(10)", insertable = false, updatable = false)
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	@Column(name="COMPANY_ID", columnDefinition="INT(10)")
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

	@Override
	public String toString() {
		return "EmployeeDocument [ebObjectId=" + getEbObjectId() + ", sequenceNo=" + sequenceNo + ", ebObject=" + getEbObjectId()
				+ ", documentTypeId=" + documentTypeId + ", employeeId=" + employeeId + ", date=" + date + ", remarks="
				+ remarks + ", referenceDocumentJson=" + referenceDocumentJson + ", referenceDocuments="
				+ referenceDocuments + ", result=" + result + ", referenceDocsMessage=" + referenceDocsMessage
				+ ", employee=" + employee + ", documentType=" + documentType + ", companyId=" + companyId
				+ ", company=" + company + "]";
	}

	@Transient
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

}
