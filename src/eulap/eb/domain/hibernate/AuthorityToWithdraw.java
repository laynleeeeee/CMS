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

import eulap.eb.service.oo.OOChild;

/**
 * Object class representation for AUTHORITY_TO_WITHDRAW

 */

@Entity
@Table(name="AUTHORITY_TO_WITHDRAW")
public class AuthorityToWithdraw extends BaseFormWorkflow {
	private Integer sequenceNumber;
	private Integer companyId;
	private Integer salesOrderId;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private String remarks;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<AuthorityToWithdrawItem> atwItems;
	private String atwItemJson;
	private List<SerialItem> serialItems;
	private String serialItemJson;
	private String soNumber;
	private Date date;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String commonErrorMsg;
	private Integer fleetProfileId;
	private Integer driverId;
	private FleetProfile fleetProfile;
	private Driver driver;
	private String shipTo;
	private String driverName;
	private List<AuthorityToWithdrawLine> atwLines;
	private String atwLinesJson;

	public enum FIELD {
		id, companyId, salesOrderId, arCustomerId, arCustomerAcctId, formWorkflowId, ebObjectId, sequenceNumber, date,
		fleetProfileId, driverId
	}

	public static final int OBJECT_TYPE_ID = 12006;
	public static final int ATW_SERIAL_ITEM_OR_TYPE_ID = 12000;
	public static final int ATW_SO_ITEM_OR_TYPE_ID = 12001;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AUTHORITY_TO_WITHDRAW_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (atwItems != null) {
			children.addAll(atwItems);
		}
		if (serialItems != null) {
			children.addAll(serialItems);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		if (atwLines != null) {
			children.addAll(atwLines);
		}
		return children;
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

	@Column(name = "SEQUENCE_NO")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "SALES_ORDER_ID")
	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	@Column(name = "AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name = "AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAcctId() {
		return arCustomerAcctId;
	}

	public void setArCustomerAcctId(Integer arCustomerAcctId) {
		this.arCustomerAcctId = arCustomerAcctId;
	}

	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@Transient
	public List<AuthorityToWithdrawItem> getAtwItems() {
		return atwItems;
	}

	public void setAtwItems(List<AuthorityToWithdrawItem> atwItems) {
		this.atwItems = atwItems;
	}

	@Transient
	public String getAtwItemJson() {
		return atwItemJson;
	}

	public void setAtwItemJson(String atwItemJson) {
		this.atwItemJson = atwItemJson;
	}

	@Transient
	public void serializeATWItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		atwItemJson = gson.toJson(atwItems);
	}

	@Transient
	public void deserializeATWItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<AuthorityToWithdrawItem>>(){}.getType();
		atwItems = gson.fromJson(atwItemJson, type);
	}

	@Transient
	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	@Transient
	public String getSerialItemJson() {
		return serialItemJson;
	}

	public void setSerialItemJson(String serialItemJson) {
		this.serialItemJson = serialItemJson;
	}

	@Transient
	public void serializeSerialItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		serialItemJson = gson.toJson(serialItems);
	}

	@Transient
	public void deserializeSerialItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialItems = gson.fromJson(serialItemJson, type);
	}

	@Override
	@Transient
	public Date getGLDate() {
		return null;
	}

	@Transient
	public String getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(String soNumber) {
		this.soNumber = soNumber;
	}

	@Column(name = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
	public void serializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Transient
	public String getCommonErrorMsg() {
		return commonErrorMsg;
	}

	public void setCommonErrorMsg(String commonErrorMsg) {
		this.commonErrorMsg = commonErrorMsg;
	}

	@Column(name = "DRIVER_ID")
	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	@Column(name = "FLEET_PROFILE_ID")
	public Integer getFleetProfileId() {
		return fleetProfileId;
	}

	public void setFleetProfileId(Integer fleetProfileId) {
		this.fleetProfileId = fleetProfileId;
	}

	@Transient
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@Transient
	public FleetProfile getFleetProfile() {
		return fleetProfile;
	}

	public void setFleetProfile(FleetProfile fleetProfile) {
		this.fleetProfile = fleetProfile;
	}

	@Column(name = "SHIP_TO")
	public String getShipTo() {
		return shipTo;
	}

	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	@Transient
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Transient
	public List<AuthorityToWithdrawLine> getAtwLines() {
		return atwLines;
	}

	public void setAtwLines(List<AuthorityToWithdrawLine> atwLines) {
		this.atwLines = atwLines;
	}

	@Transient
	public String getAtwLinesJson() {
		return atwLinesJson;
	}

	public void setAtwLinesJson(String atwLinesJson) {
		this.atwLinesJson = atwLinesJson;
	}

	@Transient
	public void serializeAtwLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		atwLinesJson = gson.toJson(atwLines);
	}

	@Transient
	public void deserializeAtwLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<AuthorityToWithdrawLine>>(){}.getType();
		atwLines = gson.fromJson(atwLinesJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorityToWithdraw [sequenceNumber=").append(sequenceNumber).append(", companyId=")
				.append(companyId).append(", salesOrderId=").append(salesOrderId).append(", arCustomerId=")
				.append(arCustomerId).append(", arCustomerAcctId=").append(arCustomerAcctId)
				.append(", date=").append(date).append("]");
		return builder.toString();
	}
}
