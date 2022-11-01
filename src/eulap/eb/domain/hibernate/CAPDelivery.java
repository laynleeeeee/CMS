package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * Object representation of CAP_DELIVERY table.

 *
 */
@Entity
@Table(name="CAP_DELIVERY")
public class CAPDelivery extends BaseFormWorkflow {
	private Integer customerAdvancePaymentId;
	private Integer customerAdvancePaymentTypeId;
	private Integer companyId;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private Integer capdNumber;
	private String salesInvoiceNo;
	private Date deliveryDate;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<CAPDeliveryItem> deliveryItems;
	private List<CAPDeliveryArLine> deliveryArLines;
	private String deliveryItemsJson;
	private String deliveryArLinesJson;
	private String errorMessage;
	private Double totalAmount;
	private Double totalReservedAmount;
	private Integer arTransactionId;
	private CapDeliveryTransaction capDeliveryTransaction;
	private Integer wipsoId;
	private String wipSoNo;// Wip special order sequence number.
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;

	/**
	 * Object Type Id for PIAD Retail = 53.
	 */
	public static final int PIAD_RETAIL_OBJECT_TYPE_ID = 53;

	/**
	 * Object Type Id for PIAD Individual Selection = 150.
	 */
	public static final int PIAD_IS_OBJECT_TYPE_ID = 150;

	/**
	 * Object Type Id for PIAD WIP Special Order = 151.
	 */
	public static final int PIAD_WIP_SO_OBJECT_TYPE_ID = 151;

	public enum FIELD {
		id, customerAdvancePaymentId, companyId, arCustomerId, arCustomerAcctId,
		capdNumber, salesInvoiceNo, deliveryDate, formWorkflowId, customerAdvancePaymentTypeId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CAP_DELIVERY_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="CUSTOMER_ADVANCE_PAYMENT_ID", columnDefinition="int(10)")
	public Integer getCustomerAdvancePaymentId() {
		return customerAdvancePaymentId;
	}

	public void setCustomerAdvancePaymentId(Integer customerAdvancePaymentId) {
		this.customerAdvancePaymentId = customerAdvancePaymentId;
	}

	/**
	 * Get the customer advance payment type id.
	 * @return The customer advance payment type id.
	 */
	@Column(name = "CUSTOMER_ADVANCE_PAYMENT_TYPE_ID", columnDefinition="INT(10")
	public Integer getCustomerAdvancePaymentTypeId() {
		return customerAdvancePaymentTypeId;
	}

	public void setCustomerAdvancePaymentTypeId(Integer customerAdvancePaymentTypeId) {
		this.customerAdvancePaymentTypeId = customerAdvancePaymentTypeId;
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="AR_CUSTOMER_ID", columnDefinition="int(10)")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name="AR_CUSTOMER_ACCOUNT_ID", columnDefinition="int(10)")
	public Integer getArCustomerAcctId() {
		return arCustomerAcctId;
	}

	public void setArCustomerAcctId(Integer arCustomerAcctId) {
		this.arCustomerAcctId = arCustomerAcctId;
	}

	@Column(name="CAPD_NUMBER", columnDefinition="int(20)")
	public Integer getCapdNumber() {
		return capdNumber;
	}

	public void setCapdNumber(Integer capdNumber) {
		this.capdNumber = capdNumber;
	}

	@Column(name="SALES_INVOICE_NO", columnDefinition="varchar(100)")
	public String getSalesInvoiceNo() {
		return salesInvoiceNo;
	}

	public void setSalesInvoiceNo(String salesInvoiceNo) {
		this.salesInvoiceNo = salesInvoiceNo;
	}

	@Column(name="DELIVERY_DATE", columnDefinition="date")
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@OneToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn (name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@OneToOne
	@JoinColumn (name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn (name="CAP_DELIVERY_ID", insertable=false, updatable=false)
	public List<CAPDeliveryItem> getDeliveryItems() {
		return deliveryItems;
	}

	public void setDeliveryItems(List<CAPDeliveryItem> deliveryItems) {
		this.deliveryItems = deliveryItems;
	}

	@OneToMany
	@JoinColumn (name="CAP_DELIVERY_ID", insertable=false, updatable=false)
	public List<CAPDeliveryArLine> getDeliveryArLines() {
		return deliveryArLines;
	}

	public void setDeliveryArLines(List<CAPDeliveryArLine> deliveryArLines) {
		this.deliveryArLines = deliveryArLines;
	}

	@Transient
	public String getDeliveryItemsJson() {
		return deliveryItemsJson;
	}
	
	public void setDeliveryItemsJson(String deliveryItemsJson) {
		this.deliveryItemsJson = deliveryItemsJson;
	}

	@Transient
	public String getDeliveryArLinesJson() {
		return deliveryArLinesJson;
	}

	public void setDeliveryArLinesJson(String deliveryArLinesJson) {
		this.deliveryArLinesJson = deliveryArLinesJson;
	}

	@Transient
	public void serializeItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		deliveryItemsJson = gson.toJson(deliveryItems);
	}

	@Transient
	public void deserializeItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CAPDeliveryItem>>(){}.getType();
		deliveryItems = 
				gson.fromJson(deliveryItemsJson, type);
	}

	@Transient
	public void serializeArLines() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		deliveryArLinesJson = gson.toJson(deliveryArLines);
	}

	public void deserializeArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CAPDeliveryArLine>>(){}.getType();
		deliveryArLines = gson.fromJson(deliveryArLinesJson, type);
	}

	/**
	 * Formats the CAPD Number to: F 1
	 * @return The formatted CAPD Number.
	 */
	@Transient
	public String getFormattedCAPDNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + capdNumber;
			}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + capdNumber;
			}
		}
		return null;
	}

	@Transient
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Transient
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		switch (customerAdvancePaymentTypeId) {
		case CustomerAdvancePaymentType.RETAIL:
			return PIAD_RETAIL_OBJECT_TYPE_ID;
		case CustomerAdvancePaymentType.INDIV_SELECTION:
			return PIAD_IS_OBJECT_TYPE_ID;
		case CustomerAdvancePaymentType.WIP_SPECIAL_ORDER:
			return PIAD_WIP_SO_OBJECT_TYPE_ID;
		}
		throw new RuntimeException("No Object Type specified for PIAD CustomerAdvancePaymentType " + customerAdvancePaymentId);
	}

	@Override
	@Transient
	public String getShortDescription() {
		if(customerAdvancePaymentTypeId != null) {
			switch (customerAdvancePaymentTypeId) {
			case CustomerAdvancePaymentType.INDIV_SELECTION:
				return "PIAD - IS " + getFormattedCAPDNumber();

			case CustomerAdvancePaymentType.WIP_SPECIAL_ORDER:
				return "PIAD - WIPSO " + getFormattedCAPDNumber();

			default:
				return "PIAD " + getFormattedCAPDNumber();
			}
		}
		return "PIAD " + capdNumber;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + customerAdvancePaymentTypeId;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(deliveryItems != null) {
			children.addAll(deliveryItems);
		}
		if(deliveryArLines != null) {
			children.addAll(deliveryArLines);
		}
		return children;
	}


	@Transient
	public Double getTotalReservedAmount() {
		return totalReservedAmount;
	}

	public void setTotalReservedAmount(Double totalReservedAmount) {
		this.totalReservedAmount = totalReservedAmount;
	}

	@Transient
	public Integer getArTransactionId() {
		return arTransactionId;
	}

	public void setArTransactionId(Integer arTransactionId) {
		this.arTransactionId = arTransactionId;
	}

	@Transient
	public CapDeliveryTransaction getCapDeliveryTransaction() {
		return capDeliveryTransaction;
	}

	public void setCapDeliveryTransaction(CapDeliveryTransaction capDeliveryTransaction) {
		this.capDeliveryTransaction = capDeliveryTransaction;
	}

	@Transient
	public Integer getWipsoId() {
		return wipsoId;
	}

	public void setWipsoId(Integer wipsoId) {
		this.wipsoId = wipsoId;
	}

	@Transient
	public String getWipSoNo() {
		return wipSoNo;
	}

	public void setWipSoNo(String wipSoNo) {
		this.wipSoNo = wipSoNo;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return deliveryDate;
	}

	@Column(name = "WT_ACCOUNT_SETTING_ID")
	public Integer getWtAcctSettingId() {
		return wtAcctSettingId;
	}

	public void setWtAcctSettingId(Integer wtAcctSettingId) {
		this.wtAcctSettingId = wtAcctSettingId;
	}

	@Column(name = "WT_AMOUNT")
	public Double getWtAmount() {
		return wtAmount;
	}

	public void setWtAmount(Double wtAmount) {
		this.wtAmount = wtAmount;
	}

	@OneToOne
	@JoinColumn(name = "WT_ACCOUNT_SETTING_ID", insertable=false, updatable=false)
	public WithholdingTaxAcctSetting getWtAcctSetting() {
		return wtAcctSetting;
	}

	public void setWtAcctSetting(WithholdingTaxAcctSetting wtAcctSetting) {
		this.wtAcctSetting = wtAcctSetting;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CAPDelivery [customerAdvancePaymentId=").append(customerAdvancePaymentId)
				.append(", customerAdvancePaymentTypeId=").append(customerAdvancePaymentTypeId).append(", companyId=")
				.append(companyId).append(", arCustomerId=").append(arCustomerId).append(", arCustomerAcctId=")
				.append(arCustomerAcctId).append(", capdNumber=").append(capdNumber).append(", salesInvoiceNo=")
				.append(salesInvoiceNo).append(", deliveryDate=").append(deliveryDate).append(", company=")
				.append(company).append(", arTransactionId=").append(arTransactionId).append(", wtAcctSettingId=")
				.append(wtAcctSettingId).append(", wtAmount=").append(wtAmount).append("]");
		return builder.toString();
	}
}
