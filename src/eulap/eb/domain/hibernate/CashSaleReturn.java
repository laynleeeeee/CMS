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
 * A class that represents the CASH_SALE_RETURN table in the CBS database.

 * 
 */
@Entity
@Table(name = "CASH_SALE_RETURN")
public class CashSaleReturn extends BaseFormWorkflow {
	private Integer cashSaleTypeId;
	private Integer cashSaleId;
	private Integer refCashSaleReturnId;
	private Integer csrNumber;
	private Integer companyId;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private String salesInvoiceNo;
	private Date date;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<CashSaleReturnItem> cashSaleReturnItems;
	private List<CashSaleReturnArLine> cashSaleReturnArLines;
	private Double totalAmount;
	private String errorCSRItems;
	private String csrArLineMesage;
	private String cashSaleReturnItemsJson;
	private String csrArLinesJson;
	private String referenceNo;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;

	public static final int MAX_REF_NUMBER = 20;
	public static final int MAX_SALE_INVOICE_NO = 100;
	public static final int MAX_RECEIPT_NUMBER = 100;

	/**
	 * Object type id for Cash sale return = 19.
	 */
	public static final int CSR_OBJECT_TYPE_ID = 19;
	/**
	 * Object type id for Cash Sale Return IS = 145
	 */
	public static final int CSR_IS_OBJECT_TYPE_ID = 145;

	public enum FIELD {
		id, cashSaleId, formWorkflowId, csrNumber, arReceiptTypeId, refNumber, arCustomerId,
		arCustomerAccountId, salesInvoiceNo, date, createdDate, cashSaleTypeId, companyId, refCashSaleReturnId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CASH_SALE_RETURN_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the type id of the Cash Sales {1 = Retail}
	 * @return The cash sales type id.
	 */
	@Column(name = "CASH_SALE_TYPE_ID", columnDefinition = "int(10)")
	public Integer getCashSaleTypeId() {
		return cashSaleTypeId;
	}

	public void setCashSaleTypeId(Integer cashSaleTypeId) {
		this.cashSaleTypeId = cashSaleTypeId;
	}

	/**
	 * Get the cash sales id.
	 * @return The cash sales id.
	 */
	@Column(name = "CASH_SALE_ID", columnDefinition = "int(10)", nullable=true)
	public Integer getCashSaleId() {
		return cashSaleId;
	}

	public void setCashSaleId(Integer cashSaleId) {
		this.cashSaleId = cashSaleId;
	}

	@Column(name = "REF_CASH_SALE_RETURN_ID", columnDefinition = "int(10)", nullable=true)
	public Integer getRefCashSaleReturnId() {
		return refCashSaleReturnId;
	}

	public void setRefCashSaleReturnId(Integer refCashSaleReturnId) {
		this.refCashSaleReturnId = refCashSaleReturnId;
	}

	/**
	 * Get the cash sales number.
	 * @return The cash sales number.
	 */
	@Column(name = "CSR_NUMBER", columnDefinition = "int(20)")
	public Integer getCsrNumber() {
		return csrNumber;
	}

	public void setCsrNumber(Integer csrNumber) {
		this.csrNumber = csrNumber;
	}

	/**
	 * Get the company id.
	 * @return The company id.
	 */
	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the Id of the Ar customer id.
	 * @return The Id of Ar customer id.
	 */
	@Column(name = "AR_CUSTOMER_ID", columnDefinition = "int(10)")
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
	@Column(name = "AR_CUSTOMER_ACCOUNT_ID", columnDefinition = "int(10)")
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
	@Column(name = "SALE_INVOICE_NO", columnDefinition = "varchar(100)")
	public String getSalesInvoiceNo() {
		return salesInvoiceNo;
	}

	public void setSalesInvoiceNo(String salesInvoiceNo) {
		this.salesInvoiceNo = salesInvoiceNo;
	}

	/**
	 * Get the date.
	 * @return The date.
	 */
	@Column(name = "DATE", columnDefinition = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn(name = "AR_CUSTOMER_ID", insertable = false, updatable = false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@ManyToOne
	@JoinColumn(name = "AR_CUSTOMER_ACCOUNT_ID", insertable = false, updatable = false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@OneToMany
	@JoinColumn(name = "CASH_SALE_RETURN_ID", insertable = false, updatable = false)
	public List<CashSaleReturnItem> getCashSaleReturnItems() {
		return cashSaleReturnItems;
	}

	public void setCashSaleReturnItems(
			List<CashSaleReturnItem> cashSaleReturnItems) {
		this.cashSaleReturnItems = cashSaleReturnItems;
	}

	@Transient
	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Transient
	public String getErrorCSRItems() {
		return errorCSRItems;
	}

	public void setErrorCSRItems(String errorCSRItems) {
		this.errorCSRItems = errorCSRItems;
	}

	@Transient
	public String getCashSaleReturnItemsJson() {
		return cashSaleReturnItemsJson;
	}

	public void setCashSaleReturnItemsJson(String cashSaleReturnItemsJson) {
		this.cashSaleReturnItemsJson = cashSaleReturnItemsJson;
	}

	@Transient
	public void serializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		cashSaleReturnItemsJson = gson.toJson(cashSaleReturnItems);
	}

	@Transient
	public void deserializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		Type type = new TypeToken<List<CashSaleReturnItem>>() {
		}.getType();
		cashSaleReturnItems = gson.fromJson(cashSaleReturnItemsJson, type);
	}

	/**
	 * Get the reference no.
	 */
	@Transient
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	/**
	 * Formats the RR Number to: M 1
	 * @return The formatted RR Number.
	 */
	@Transient
	public String getFormattedCSRNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + csrNumber;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + csrNumber;
			}
		}
		return null;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if(cashSaleTypeId.equals(CashSaleType.RETAIL)) {
			return CSR_OBJECT_TYPE_ID;
		} else if(cashSaleTypeId.equals(CashSaleType.INDIV_SELECTION)) {
			return CSR_IS_OBJECT_TYPE_ID;
		}
		return null;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if(cashSaleReturnItems != null){
			children.addAll(cashSaleReturnItems);
		}
		if(cashSaleReturnArLines != null){
			children.addAll(cashSaleReturnArLines);
		}
		return children;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getCashSaleTypeId();
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "CSR " + " " + csrNumber;
	}

	@Transient
	public void serializeCSRArLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		csrArLinesJson = gson.toJson(cashSaleReturnArLines);
	}

	@Transient
	public void deserializeCSRArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<CashSaleReturnArLine>>(){}.getType();
		cashSaleReturnArLines = gson.fromJson(csrArLinesJson, type);
	}

	@Transient
	public String getCsrArLineMesage() {
		return csrArLineMesage;
	}

	public void setCsrArLineMesage(String csrArLineMesage) {
		this.csrArLineMesage = csrArLineMesage;
	}

	@OneToMany
	@JoinColumn (name = "CASH_SALE_RETURN_ID", insertable=false, updatable=false)
	public List<CashSaleReturnArLine> getCashSaleReturnArLines() {
		return cashSaleReturnArLines;
	}

	public void setCashSaleReturnArLines(List<CashSaleReturnArLine> cashSaleReturnArLines) {
		this.cashSaleReturnArLines = cashSaleReturnArLines;
	}

	@Transient
	public String getCsrArLinesJson() {
		return csrArLinesJson;
	}

	public void setCsrArLinesJson(String csrArLinesJson) {
		this.csrArLinesJson = csrArLinesJson;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return date;
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
		builder.append("CashSaleReturn [cashSaleTypeId=").append(cashSaleTypeId).append(", cashSaleId=")
				.append(cashSaleId).append(", refCashSaleReturnId=").append(", ebObjectId=")
				.append(getEbObjectId()).append(", ebObject=").append(getEbObject())
				.append(refCashSaleReturnId).append(", csrNumber=")
				.append(csrNumber).append(", companyId=").append(companyId).append(", arCustomerId=")
				.append(arCustomerId).append(", arCustomerAccountId=").append(arCustomerAccountId)
				.append(", salesInvoiceNo=").append(salesInvoiceNo).append(", date=").append(date)
				.append(", totalAmount=").append(totalAmount).append(", referenceNo=").append(referenceNo).append("]");
		return builder.toString();
	}
}