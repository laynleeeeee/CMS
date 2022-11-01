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
 * Domain object for request for leave table.

 *
 */
@Entity
@Table(name="EMPLOYEE_REQUEST")
public class EmployeeRequest extends BaseFormWorkflow {
	private Integer companyId;
	private Integer employeeId;
	private Employee employee;
	private Integer sequenceNo;
	private Date date;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private BindingResult result;
	private String referenceDocsMessage;
	private String errorLines;
	private BindingResult bindingResult;
	private Company company;
	private String employeePosition;
	private String employeeFullName;
	private Integer requestTypeId;
	private LeaveDetail leaveDetail;
	private OvertimeDetail overtimeDetail;
	private String employeeDivision;

	/**
	 * Object type id of REQUEST_FOR_LEAVE and REQUEST_FOR_OVERTIME
	 */
	public static final int RFL_OBJECT_TYPE_ID = 66;
	public static final int RFO_OBJECT_TYPE_ID = 71;

	public enum FIELD {
		id, companyId, employeeId, typeOfLeaveId, sequenceNo, date, dateFrom, dateTo,
		days, remarks, formWorkflowId, ebObjectId, requestTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_REQUEST_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if(requestTypeId.equals(RequestType.REQUEST_FOR_LEAVE)) {
			return RFL_OBJECT_TYPE_ID;
		}
		if(requestTypeId.equals(RequestType.REQUEST_FOR_OVERTIME)) {
			return RFO_OBJECT_TYPE_ID;
		}
		return null;
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

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(referenceDocuments != null){
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

	@Transient
	public String getErrorLines() {
		return errorLines;
	}

	public void setErrorLines(String errorLines) {
		this.errorLines = errorLines;
	}

	@Transient
	public BindingResult getBindingResult() {
		return bindingResult;
	}

	public void setBindingResult(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}
	@OneToOne
	@JoinColumn(name = "COMPANY_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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

	@Column(name = "REQUEST_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getRequestTypeId() {
		return requestTypeId;
	}

	public void setRequestTypeId(Integer requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_REQUEST_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public LeaveDetail getLeaveDetail() {
		return leaveDetail;
	}

	public void setLeaveDetail(LeaveDetail leaveDetail) {
		this.leaveDetail = leaveDetail;
	}

	@OneToOne
	@JoinColumn(name = "EMPLOYEE_REQUEST_ID", columnDefinition = "int(10)", insertable = false, updatable = false)
	public OvertimeDetail getOvertimeDetail() {
		return overtimeDetail;
	}

	public void setOvertimeDetail(OvertimeDetail overtimeDetail) {
		this.overtimeDetail = overtimeDetail;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeRequest [ebObjectId=").append(getEbObjectId()).append(", companyId=").append(companyId)
				.append(", employeeId=").append(employeeId).append(", employee=").append(employee)
				.append(", sequenceNo=").append(sequenceNo).append(", date=").append(date)
				.append(", requestTypeId=").append(requestTypeId).append("]");
		return builder.toString();
	}

	@Transient
	@Override
	public String getWorkflowName() {
		return super.getWorkflowName() + requestTypeId;
	}

	@Transient
	public String getEmployeeDivision() {
		return employeeDivision;
	}

	public void setEmployeeDivision(String employeeDivision) {
		this.employeeDivision = employeeDivision;
	}
}
