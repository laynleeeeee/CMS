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

import eulap.eb.service.oo.OOChild;

/**
 * Domain object for PERSONNEL_ACTION_NOTICE table.

 *
 */
@Entity
@Table(name="PERSONNEL_ACTION_NOTICE")
public class PersonnelActionNotice extends BaseFormWorkflow {
	private Integer companyId;
	private Company company;
	private Integer employeeId;
	private Employee employee;
	private Integer sequenceNo;
	private Date date;
	private String justification;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private BindingResult result;
	private String referenceDocsMessage;
	private String employeeFullName;
	private String employeePosition;
	private String employeeDivision;
	private String hiredDate;
	private Integer actionNoticeId;
	private ActionNotice actionNotice;

	/**
	 * Object type of personnel action notice = 72.
	 */
	public static final int OBJECT_TYPE_ID = 72;

	public enum FIELD {
		id, companyId, employeeId, actionNoticeId, sequenceNo,
		date, dateHired, justification, formWorkflowId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PERSONNEL_ACTION_NOTICE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "EMPLOYEE_ID", columnDefinition = "int(10)")
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Column(name = "SEQUENCE_NO", columnDefinition = "int(10)")
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "JUSTIFICATION", columnDefinition = "TEXT")
	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	@Transient
	public void serializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
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

	@Transient
	public String getEmployeeFullName() {
		return employeeFullName;
	}

	public void setEmployeeFullName(String employeeFullName) {
		this.employeeFullName = employeeFullName;
	}

	@Transient
	public String getEmployeePosition() {
		return employeePosition;
	}

	public void setEmployeePosition(String employeePosition) {
		this.employeePosition = employeePosition;
	}

	@Transient
	public String getEmployeeDivision() {
		return employeeDivision;
	}

	public void setEmployeeDivision(String employeeDivision) {
		this.employeeDivision = employeeDivision;
	}

	@Transient
	public String getHiredDate() {
		return hiredDate;
	}

	public void setHiredDate(String hiredDate) {
		this.hiredDate = hiredDate;
	}

	@Column(name = "ACTION_NOTICE_ID", columnDefinition = "INT(10)")
	public Integer getActionNoticeId() {
		return actionNoticeId;
	}

	public void setActionNoticeId(Integer actionNoticeId) {
		this.actionNoticeId = actionNoticeId;
	}

	@OneToOne
	@JoinColumn(name = "ACTION_NOTICE_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public ActionNotice getActionNotice() {
		return actionNotice;
	}

	public void setActionNotice(ActionNotice actionNotice) {
		this.actionNotice = actionNotice;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(referenceDocuments != null){
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	public String toString() {
		return "PersonnelActionNotice [ebObjectId=" + getEbObjectId() + ", ebObject=" + getEbObject() + ", companyId=" + companyId
				+ ", company=" + company + ", employeeId=" + employeeId + ", employee=" + employee + ", sequenceNo="
				+ sequenceNo + ", date=" + date + ", justification=" + justification + ", referenceDocuments="
				+ referenceDocuments + ", referenceDocumentsJson=" + referenceDocumentsJson + ", actionNoticeId="
				+ actionNoticeId + ", actionNotice=" + actionNotice + "]";
	}

}
