package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the WITHDRAWAL_SLIP table.

 *
 */
@Entity
@Table(name = "WITHDRAWAL_SLIP")
public class WithdrawalSlip extends BaseFormWorkflow {
	private String remarks;
	private Date date;
	private String requestedBy;
	private Integer employeeId;
	private Integer wsNumber;
	private Integer companyId;
	private Integer warehouseId;
	private Integer arCustomerId;
	private Integer fleetId;
	private String poNumber;
	private Integer accountId;
	private Integer divisionId;
	private Integer customerAcctId;
	private String withdrawalItemsJson;
	private List<WithdrawalSlipItem> withdrawalSlipItems;
	private Integer purchaseOderId;
	private Integer refenceObjectId;
	private String companyName;
	private String warehouseName;
	private String fleetName;
	private String accountName;
	private String customerName;
	private String customerAccountName;
	private String requesterName;
	private Integer typeId;

	public static final int OBJECT_TYPE = 96;
	public static final int OR_TYPE_COMPANY = 44;
	public static final int OR_TYPE_WAREHOUSE = 45;
	public static final int OR_TYPE_ACCOUNT = 46;
	public static final int OR_TYPE_FLEET_PROFILE = 47;
	public static final int OR_TYPE_CUSTOMER = 48;
	public static final int OR_TYPE_EMPLOYEE_PROFILE = 50;
	public static final int OR_TYPE_CUSTOMER_ACCOUNT = 52;
	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;
	public static final int MAX_PO_CHARACTER = 20;
	public static final int MAX_REQUESTER_NAME_CHAR = 100;

	/**
	 * OR Type for Purchase Order to Withdrawal Slip Relationship = 72.
	 */
	public static final int OR_TYPE_WS_PO = 72;
	public static String WITHDRAWAL_SLIP_FORM_NAME = "WS";

	public enum FIELD {
		id, date, requestedBy, poNumber, ebObjectId, remarks, wsNumber, formWorkflowId, createdDate
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "WITHDRAWAL_SLIP_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column (name = "REMARKS", columnDefinition="text")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column (name = "DATE", columnDefinition="date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="WS_NUMBER", columnDefinition="int(20)")
	public Integer getWsNumber() {
		return wsNumber;
	}

	public void setWsNumber(Integer wsNumber) {
		this.wsNumber = wsNumber;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(withdrawalSlipItems != null) {
			children.addAll(withdrawalSlipItems);
		}
		return children;
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "WS-" + wsNumber;
	}

	@Transient
	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + (typeId != null ? typeId : "");
	}

	@Transient
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Transient
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Transient
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Transient
	public Integer getFleetId() {
		return fleetId;
	}

	public void setFleetId(Integer fleetId) {
		this.fleetId = fleetId;
	}

	@Column(name="PO_NUMBER", columnDefinition="VARCHAR(20)")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Transient
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Transient
	public String getWithdrawalItemsJson() {
		return withdrawalItemsJson;
	}

	public void setWithdrawalItemsJson(String withdrawalItemsJson) {
		this.withdrawalItemsJson = withdrawalItemsJson;
	}

	@Transient
	public List<WithdrawalSlipItem> getWithdrawalSlipItems() {
		return withdrawalSlipItems;
	}

	public void setWithdrawalSlipItems(List<WithdrawalSlipItem> withdrawalSlipItems) {
		this.withdrawalSlipItems = withdrawalSlipItems;
	}

	@Transient
	public void serializeWithdrawalSlipItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		withdrawalItemsJson = gson.toJson(withdrawalSlipItems);
	}

	@Transient
	public void deserializeWithdrawalSlipItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<WithdrawalSlipItem>>(){}.getType();
		withdrawalSlipItems = gson.fromJson(withdrawalItemsJson, type);
	}

	@Transient
	public Integer getPurchaseOderId() {
		return purchaseOderId;
	}

	public void setPurchaseOderId(Integer purchaseOderId) {
		this.purchaseOderId = purchaseOderId;
	}

	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return date;
	}

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Transient
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Transient
	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Transient
	public String getFleetName() {
		return fleetName;
	}

	public void setFleetName(String fleetName) {
		this.fleetName = fleetName;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Transient
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	@Transient
	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	@Transient
	public Integer getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(Integer customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	@Transient
	public String getCustomerAccountName() {
		return customerAccountName;
	}

	public void setCustomerAccountName(String customerAccountName) {
		this.customerAccountName = customerAccountName;
	}

	@Column(name="REQUESTER_NAME", columnDefinition="VARCHAR(100)")
	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WithdrawalSlip [remarks=").append(remarks).append(", date=").append(date)
				.append(", requestedBy=").append(requestedBy).append(", employeeId=").append(employeeId)
				.append(", wsNumber=").append(wsNumber).append(", ebObjectId=").append(getEbObjectId())
				.append(", companyId=").append(companyId).append(", warehouseId=").append(warehouseId)
				.append(", arCustomerId=").append(arCustomerId).append(", fleetId=").append(fleetId)
				.append(", poNumber=").append(poNumber).append(", accountId=").append(accountId).append(", divisionId=")
				.append(divisionId).append(", customerAcctId=").append(customerAcctId).append(", purchaseOderId=")
				.append(purchaseOderId).append(", refenceObjectId=").append(refenceObjectId).append(", companyName=")
				.append(companyName).append(", warehouseName=").append(warehouseName).append(", fleetName=")
				.append(fleetName).append(", accountName=").append(accountName).append(", customerName=")
				.append(customerName).append(", customerAccountName=").append(customerAccountName)
				.append(", requesterName=").append(requesterName).append("]");
		return builder.toString();
	}
}
