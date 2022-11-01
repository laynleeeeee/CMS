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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * Object representation of ACCOUNT_SALE table.

 * 
 */
@Entity
@Table(name = "ACCOUNT_SALE")
public class AccountSales extends BaseFormWorkflow {
	private Integer companyId;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private Date poDate;
	private Integer poNumber;
	private String remarks;
	private List<AccountSalesPoItem> asPoItems;
	private String poMessage;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private String poItemsJson;
	@Expose
	private Integer referenceObjectId;
	private String arCustomerName;

	public static final int OBJECT_TYPE_ID = 166;
	public static final int MAX_REMARKS_CHAR = 100;
	public static final int ACCOUNT_SALES_PO_AR_TRANSACTION_OR_TYPE = 102;
	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	public enum FIELD {
		id, companyId, arCustomerId, arCustomerAccountId, poDate,
		poNumber, remarks, formWorkflowId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ACCOUNT_SALE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
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
	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	@Column(name="PO_DATE", columnDefinition="date")
	public Date getPoDate() {
		return poDate;
	}

	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	@Column(name="PO_NUMBER", columnDefinition="int(20)")
	public Integer getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(Integer poNumber) {
		this.poNumber = poNumber;
	}

	@Column(name="REMARKS", columnDefinition="varchar(100)")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="ACCOUNT_SALE_ID", insertable=false, updatable=false)
	public List<AccountSalesPoItem> getAsPoItems() {
		return asPoItems;
	}

	public void setAsPoItems(List<AccountSalesPoItem> asPoItems) {
		this.asPoItems = asPoItems;
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

	/**
	 * Formats the PO Number to: M 1
	 * @return The formatted PO Number.
	 */
	@Transient
	public String getFormattedPONumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + poNumber;
			}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + poNumber;
			}
		}
		return null;
	}

	@Transient
	public String getPoMessage() {
		return poMessage;
	}

	public void setPoMessage(String poMessage) {
		this.poMessage = poMessage;
	}

	@Transient
	public String getPoItemsJson() {
		return poItemsJson;
	}

	public void setPoItemsJson(String poItemsJson) {
		this.poItemsJson = poItemsJson;
	}

	@Transient
	public void serializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		poItemsJson = gson.toJson(asPoItems);
	}

	@Transient
	public void deSerializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<AccountSalesPoItem>>(){}.getType();
		asPoItems = gson.fromJson(poItemsJson, type);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(asPoItems != null) {
			children.addAll(asPoItems);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public String getArCustomerName() {
		return arCustomerName;
	}

	public void setArCustomerName(String arCustomerName) {
		this.arCustomerName = arCustomerName;
	}

	@Override
	public String toString() {
		return "AccountSalesOrder [companyId=" + companyId + ", arCustomerId=" + arCustomerId + ", arCustomerAccountId="
				+ arCustomerAccountId + ", poDate=" + poDate + ", poNumber=" + poNumber
				+ ", remarks=" + remarks + ", ebObjectId=" + getEbObjectId() + ", referenceObjectId=" + referenceObjectId
				+ ", arCustomerName=" + arCustomerName + "]";
	}
}