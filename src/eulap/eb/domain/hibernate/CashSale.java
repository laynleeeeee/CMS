package eulap.eb.domain.hibernate;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.inventory.InventoryTransaction;
import eulap.eb.service.oo.OOChild;


/**
 * A class that represents the CASH_SALE table in the CBS database.

 *
 */
@Entity
@Table(name="CASH_SALE")
public class CashSale extends BaseFormWorkflow implements InventoryTransaction, Serializable {

	/**
	 * Generated serial number
	 */
	private static final long serialVersionUID = -3376337887961455999L;

	private Integer cashSaleTypeId;
	private Integer csNumber;
	private Integer companyId;
	private Integer arReceiptTypeId;
	private String refNumber;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private String salesInvoiceNo;
	private Date receiptDate;
	private Date maturityDate;
	private Double cash;
	private Company company;
	private CashSaleType cashSaleType;
	private ArReceiptType arReceiptType;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<CashSaleItem> cashSaleItems;
	private Double totalAmount;
	private String errorCSItems;
	private String cashSaleItemsJson;
	private List<CashSaleArLine> cashSaleArLines;
	private String csArLinesJson;
	private String csArLineMesage;
	private Integer warehouseId;
	private String strItemMsg;
	private String strSrpMsg;
	private List<CsiRawMaterial> rawMaterials;
	private List<CsiFinishedProduct> finishedProducts;
	private String genericJson;
	private String warehouseName;
	private User user;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;

	public static final int MAX_REF_NUMBER = 20;
	public static final int MAX_SALE_INVOICE_NO = 100;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	public enum FIELD {
		id, formWorkflowId, csNumber, companyId, arReceiptTypeId, refNumber, arCustomerId, arCustomerAccountId, 
			salesInvoiceNo, receiptDate, maturityDate, createdDate, updatedDate, cashSaleTypeId
	}

	public static final int MAX_RECEIPT_NUMBER = 100;
	public static final int MAX_CHECK_NUMBER = 20;

	/**
	 * Object Type Id of cash sale order = 13.
	 */
	public static final int CS_OBJECT_TYPE_ID = 13;

	/**
	 * Object Type id of cash sale - is = 149.
	 */
	public static final int CS_IS_OBJECT_TYPE_ID = 149;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CASH_SALE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the type id of the cash sales.
	 * @return The cash sales type id.
	 */
	@Column(name = "CASH_SALE_TYPE_ID", columnDefinition="int(10)")
	public Integer getCashSaleTypeId() {
		return cashSaleTypeId;
	}

	public void setCashSaleTypeId(Integer cashSaleTypeId) {
		this.cashSaleTypeId = cashSaleTypeId;
	}

	/**
	 * Get the cash sales number.
	 * @return The cash sales number.
	 */
	@Column(name = "CS_NUMBER")
	public Integer getCsNumber() {
		return csNumber;
	}

	public void setCsNumber(Integer csNumber) {
		this.csNumber = csNumber;
	}
	
	/**
	 * Get the company id.
	 * @return The company id.
	 */
	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
		
	/**
	 * Get the unique id of the ar receipt type.
	 * @return ar receipt type id.
	 */
	@Column(name = "AR_RECEIPT_TYPE_ID")
	public Integer getArReceiptTypeId() {
		return arReceiptTypeId;
	}
	
	public void setArReceiptTypeId(Integer arReceiptTypeId) {
		this.arReceiptTypeId = arReceiptTypeId;
	}

	/**
	 * Get the reference number.
	 * @return reference number.
	 */
	@Column(name = "REF_NUMBER")
	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	/**
	 * Get the Id of the Ar customer id.
	 * @return The Id of Ar customer id.
	 */
	@Column(name = "AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	/**
	 * Get the Id of the Ar customer account id.
	 * @return The Id of Ar customer account.
	 */
	@Column(name = "AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	/**
	 * Get the sales invoice number.
	 * @return The sales invoice number.
	 */
	@Column(name = "SALE_INVOICE_NO")
	public String getSalesInvoiceNo() {
		return salesInvoiceNo;
	}

	public void setSalesInvoiceNo(String salesInvoiceNo) {
		this.salesInvoiceNo = salesInvoiceNo;
	}

	/**
	 * Get the receipt date.
	 * @return The receipt date.
	 */
	@Column(name = "RECEIPT_DATE")
	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	/**
	 * Get the maturity date.
	 * @return The maturity date.
	 */
	@Column(name = "MATURITY_DATE")
	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	
	@Column(name = "CASH")
	public Double getCash() {
		return cash;
	}
	
	public void setCash(Double cash) {
		this.cash = cash;
	}

	@ManyToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn (name = "CASH_SALE_TYPE_ID", insertable=false, updatable=false)
	public CashSaleType getCashSaleType() {
		return cashSaleType;
	}

	public void setCashSaleType(CashSaleType cashSaleType) {
		this.cashSaleType = cashSaleType;
	}

	@ManyToOne
	@JoinColumn (name = "AR_RECEIPT_TYPE_ID", insertable=false, updatable=false)
	public ArReceiptType getArReceiptType() {
		return arReceiptType;
	}
	
	public void setArReceiptType(ArReceiptType arReceiptType) {
		this.arReceiptType = arReceiptType;
	}
	
	@ManyToOne
	@JoinColumn (name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}
	
	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}
	
	@ManyToOne
	@JoinColumn (name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}
	
	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@OneToMany
	@JoinColumn (name = "CASH_SALE_ID", insertable=false, updatable=false)
	public List<CashSaleItem> getCashSaleItems() {
		return cashSaleItems;
	}
	
	public void setCashSaleItems(List<CashSaleItem> cashSaleItems) {
		this.cashSaleItems = cashSaleItems;
	}

	@Transient
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Transient
	public String getErrorCSItems() {
		return errorCSItems;
	}
	
	public void setErrorCSItems(String errorCSItems) {
		this.errorCSItems = errorCSItems;
	}

	@Transient
	public String getCashSaleItemsJson() {
		return cashSaleItemsJson;
	}

	public void setCashSaleItemsJson(String cashSaleItemsJson) {
		this.cashSaleItemsJson = cashSaleItemsJson;
	}

	@Transient
	public void serializeItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().
				serializeSpecialFloatingPointValues().create();
		cashSaleItemsJson = gson.toJson(cashSaleItems);
	}

	@Transient
	public void deserializeItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CashSaleItem>>(){}.getType();
		cashSaleItems = 
				gson.fromJson(cashSaleItemsJson, type);
	}

	/**
	 * Formats the RR Number to: M 1
	 * @return The formatted RR Number.
	 */
	@Transient
	public String getFormattedCSNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + csNumber;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + csNumber;
			}
		}
		return null;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getCashSaleTypeId();
	}

	@Override
	public String toString() {
		return "CashSales [ cashSaleTypeId=" + cashSaleTypeId
				+ "csNumber=" + csNumber + ", companyId=" + companyId
				+ ", arReceiptTypeId=" + arReceiptTypeId + ", refNumber="
				+ refNumber + ", arCustomerId=" + arCustomerId
				+ ", arCustomerAccountId=" + arCustomerAccountId
				+ ", salesInvoiceNo=" + salesInvoiceNo + ", receiptDate="
				+ receiptDate + ", maturityDate=" + maturityDate
				+ ", getId()=" + getId() + "]";
	}

	@Override
	@Transient
	public List<? extends BaseItem> getInventoryItems() {
		return cashSaleItems;
	}

	@OneToMany
	@JoinColumn (name = "CASH_SALE_ID", insertable=false, updatable=false)
	public List<CashSaleArLine> getCashSaleArLines() {
		return cashSaleArLines;
	}

	public void setCashSaleArLines(List<CashSaleArLine> cashSaleArLines) {
		this.cashSaleArLines = cashSaleArLines;
	}

	@Transient
	public String getCsArLinesJson() {
		return csArLinesJson;
	}

	public void setCsArLinesJson(String csArLinesJson) {
		this.csArLinesJson = csArLinesJson;
	}

	@Transient
	public String getCsArLineMesage() {
		return csArLineMesage;
	}

	public void setCsArLineMesage(String csArLineMesage) {
		this.csArLineMesage = csArLineMesage;
	}

	@Transient
	public void serializeCSArLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		csArLinesJson = gson.toJson(cashSaleArLines);
	}

	@Transient
	public void deserializeCSArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CashSaleArLine>>(){}.getType();
		cashSaleArLines = gson.fromJson(csArLinesJson, type);
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if(cashSaleTypeId == CashSaleType.RETAIL
				|| cashSaleTypeId == CashSaleType.PROCESSING) {
			return CS_OBJECT_TYPE_ID;
		} else if(cashSaleTypeId == CashSaleType.INDIV_SELECTION) {
			return CS_IS_OBJECT_TYPE_ID;
		}
		return null;
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
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if (cashSaleItems != null) {
			children.addAll(cashSaleItems);
		}
		if (cashSaleArLines != null) {
			children.addAll(cashSaleArLines);
		}
		if(finishedProducts != null) {
			children.addAll(finishedProducts);
		}
		if(rawMaterials != null) {
			children.addAll(rawMaterials);
		}
		return children;
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "CS" + " " + csNumber;
	}

	@Transient
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}


	@Transient
	public String getStrItemMsg() {
		return strItemMsg;
	}

	public void setStrItemMsg(String strItemMsg) {
		this.strItemMsg = strItemMsg;
	}

	@Transient
	public String getStrSrpMsg() {
		return strSrpMsg;
	}

	public void setStrSrpMsg(String strSrpMsg) {
		this.strSrpMsg = strSrpMsg;
	}

	@Transient
	public List<CsiRawMaterial> getRawMaterials() {
		return rawMaterials;
	}

	public void setRawMaterials(List<CsiRawMaterial> rawMaterials) {
		this.rawMaterials = rawMaterials;
	}

	@Transient
	public List<CsiFinishedProduct> getFinishedProducts() {
		return finishedProducts;
	}

	public void setFinishedProducts(List<CsiFinishedProduct> finishedProducts) {
		this.finishedProducts = finishedProducts;
	}

	@Transient
	public String getGenericJson() {
		return genericJson;
	}

	public void setGenericJson(String genericJson) {
		this.genericJson = genericJson;
	}

	@Transient
	public void serializeRawMaterials (){
		// Error message thrown if not impelemented
		// Error message: Failed to convert property value of type 'java.lang.String' to required type 'java.util.List'
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().
				serializeSpecialFloatingPointValues().create();
		genericJson = gson.toJson(rawMaterials);
	}

	@Transient
	public void serializeFinishedProducts (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().
				serializeSpecialFloatingPointValues().create();
		genericJson = gson.toJson(finishedProducts);
	}

	@Transient
	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Transient
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return receiptDate;
	}
}