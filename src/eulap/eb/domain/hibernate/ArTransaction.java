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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.inventory.InventoryTransaction;
import eulap.eb.service.oo.OOChild;

/**
 * Class representation of AR_TRANSACTION table from the database.

 */
@Entity
@Table(name = "AR_TRANSACTION")
public class ArTransaction extends BaseFormWorkflow implements InventoryTransaction {
	private Integer serviceLeaseKeyId;
	private Integer sequenceNumber;
	private Integer companyId;
	private Integer divisionId;
	private Integer customerId;
	private Integer customerAcctId;
	private Integer transactionTypeId;
	private Integer accountSaleId;
	private Integer termId;
	private String transactionNumber;
	private Double amount;
	private Date transactionDate;
	private Date glDate;
	private Date dueDate;
	private String description;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private List<ArLine> arLines;
	private List<ArServiceLine> arServiceLines;
	private String aRlineMessage;
	private boolean isPosting;
	private Double balance;
	private Integer paymentStatus;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private ArTransactionType arTransactionType;
	private Term term;
	private String arLineError;
	private Double creditLimit;
	private Double totalAmount;
	private Double availableBalance;
	private List<AccountSaleItem> accountSaleItems;
	private String accountSalesItemsJson;
	private String arLinesJson;
	private String arServiceLinesJson;
	private String errorASItems;
	private String otherChargesMessage;
	private String itemsRequiredError;
	private String errorPrescriptions;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private Integer referenceObjectId;
	private String referenceNo;
	private Division division;
	private Currency currency;
	private CurrencyRate currencyRate;
	private Double wtVatAmount;
	private Integer transactionClassificationId;
	private TransactionClassification transactionClassification;
	private String customerName;

	public enum FIELD {
		id, formWorkflowId, serviceLeaseKeyId, sequenceNumber, companyId, 
		transactionStatusId, customerId, customerAcctId, transactionTypeId, 
		accountSaleId, termId, transactionNumber,
		amount, transactionDate, glDate, dueDate, description, deleted, paymentStatus,
		arCustomer, arCustomerAccount, status, createdDate, ebObjectId, divisionId,
		currencyId, currencyRateId
	}
	
	public static final int MAX_TRANSACTION_NUMBER = 100;
	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;
	public static final int MAX_DESCRIPTION = 200;

	public static final int AR_TRANSACTION_OBJECT_TYPE_ID = 14;
	public static final int ACCOUNT_SALE_ORDER_OBJECT_TYPE_ID = 127;
	public static final int ACCOUNT_SALE_RETURN_OBJECT_TYPE_ID = 22;
	public static final int ACCOUNT_SALE_IS_OBJECT_TYPE_ID = 154;
	public static final int ACCOUNT_SALE_RETURN_IS_OBJECT_TYPE_ID = 155;
	public static final int ACCOUNT_SALE_RETURN_EB_OBJECT_TYPE_ID = 168;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_TRANSACTION_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "EB_SL_KEY_ID")
	public Integer getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(Integer serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
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

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "CUSTOMER_ID")
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	@Column(name = "CUSTOMER_ACCOUNT_ID")
	public Integer getCustomerAcctId() {
		return customerAcctId;
	}

	public void setCustomerAcctId(Integer customerAcctId) {
		this.customerAcctId = customerAcctId;
	}

	@Column(name = "AR_TRANSACTION_TYPE_ID")
	public Integer getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(Integer transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}
	
	@Column(name = "ACCOUNT_SALE_ID")
	public Integer getAccountSaleId() {
		return accountSaleId;
	}
	
	public void setAccountSaleId(Integer accountSaleId) {
		this.accountSaleId = accountSaleId;
	}

	@Column(name = "TERM_ID")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Column(name = "TRANSACTION_NUMBER")
	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "TRANSACTION_DATE")
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Column(name = "GL_DATE")
	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	@Column(name = "DUE_DATE")
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column(name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column(name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}
	
	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "AR_TRANSACTION_ID", insertable=false, updatable=false)
	public List<ArLine> getArLines() {
		return arLines;
	}

	public void setArLines(List<ArLine> arLines) {
		this.arLines = arLines;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "AR_TRANSACTION_ID", insertable=false, updatable=false)
	public List<ArServiceLine> getArServiceLines() {
		return arServiceLines;
	}

	public void setArServiceLines(List<ArServiceLine> arServiceLines) {
		this.arServiceLines = arServiceLines;
	}

	@Transient
	public String getaRlineMessage() {
		return aRlineMessage;
	}

	public void setaRlineMessage(String aRlineMessage) {
		this.aRlineMessage = aRlineMessage;
	}

	@Transient
	public boolean isPosting() {
		return isPosting;
	}

	public void setPosting(boolean isPosting) {
		this.isPosting = isPosting;
	}

	@Transient
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Transient
	public Integer getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	/**
	 * Get the Company associated with AR Transaction
	 */
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@OneToOne
	@JoinColumn(name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@OneToOne
	@JoinColumn(name = "CURRENCY_RATE_ID", insertable=false, updatable=false)
	public CurrencyRate getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(CurrencyRate currencyRate) {
		this.currencyRate = currencyRate;
	}

	@Column(name = "TRANSACTION_CLASSIFICATION_ID")
	public Integer getTransactionClassificationId() {
		return transactionClassificationId;
	}

	public void setTransactionClassificationId(Integer transactionClassificationId) {
		this.transactionClassificationId = transactionClassificationId;
	}

	@OneToOne
	@JoinColumn(name = "TRANSACTION_CLASSIFICATION_ID", insertable=false, updatable=false)
	public TransactionClassification getTransactionClassification() {
		return transactionClassification;
	}

	public void setTransactionClassification(TransactionClassification transactionClassification) {
		this.transactionClassification = transactionClassification;
	}

	/**
	 * Get the Customer associated with AR Transaction
	 */
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}
	
	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}
	
	/**
	 * Get the Customer account associated with AR Transaction
	 */
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}
	
	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}
	
	/**
	 * Get the transaction type associated with AR Transaction
	 */
	@ManyToOne
	@JoinColumn(name = "AR_TRANSACTION_TYPE_ID", insertable=false, updatable=false)
	public ArTransactionType getArTransactionType() {
		return arTransactionType;
	}
	
	public void setArTransactionType(ArTransactionType arTransactionType) {
		this.arTransactionType = arTransactionType;
	}
	
	/**
	 * Get the term associated with AR Transaction
	 */
	@ManyToOne
	@JoinColumn(name = "TERM_ID", insertable=false, updatable=false)
	public Term getTerm() {
		return term;
	}
	
	public void setTerm(Term term) {
		this.term = term;
	}
	
	@Transient
	public String getArLineError() {
		return arLineError;
	}

	public void setArLineError(String arLineError) {
		this.arLineError = arLineError;
	}
	
	@Transient
	public Double getCreditLimit() {
		return creditLimit;
	}
	
	public void setCreditLimit(Double creditLimit) {
		this.creditLimit = creditLimit;
	}
		
	@Transient
	public Double getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	@Transient
	public Double getAvailableBalance() {
		return availableBalance;
	}
	
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}
	
	@OneToMany
	@JoinColumn(name = "AR_TRANSACTION_ID", insertable=false, updatable=false)
	public List<AccountSaleItem> getAccountSaleItems() {
		return accountSaleItems;
	}
	
	public void setAccountSaleItems(List<AccountSaleItem> accountSaleItems) {
		this.accountSaleItems = accountSaleItems;
	}

	@Transient
	public String getAccountSalesItemsJson() {
		return accountSalesItemsJson;
	}

	public void setAccountSalesItemsJson(String accountSalesItemsJson) {
		this.accountSalesItemsJson = accountSalesItemsJson;
	}

	@Transient
	public void serializeAccountSaleItems (){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		accountSalesItemsJson = gson.toJson(accountSaleItems);
	}

	@Transient
	public void deserializeAccountSalesItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<AccountSaleItem>>(){}.getType();
		accountSaleItems = 
				gson.fromJson(accountSalesItemsJson, type);
	}

	@Transient
	public String getArLinesJson() {
		return arLinesJson;
	}

	public void setArLinesJson(String arLinesJson) {
		this.arLinesJson = arLinesJson;
	}

	@Transient
	public void serializeArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arLinesJson = gson.toJson(arLines);
	}

	@Transient
	public void deserializeArLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArLine>>(){}.getType();
		arLines = gson.fromJson(arLinesJson, type);
	}

	@Transient
	public String getArServiceLinesJson() {
		return arServiceLinesJson;
	}

	public void setArServiceLinesJson(String arServiceLinesJson) {
		this.arServiceLinesJson = arServiceLinesJson;
	}

	@Transient
	public void serializeArServiceLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arServiceLinesJson = gson.toJson(arServiceLines);
	}

	public void deserializeArServiceLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArServiceLine>>(){}.getType();
		arServiceLines = gson.fromJson(arServiceLinesJson, type);
	}

	@Transient
	public String getErrorASItems() {
		return errorASItems;
	}

	public void setErrorASItems(String errorASItems) {
		this.errorASItems = errorASItems;
	}

	@Transient
	public String getErrorPrescriptions() {
		return errorPrescriptions;
	}

	public void setErrorPrescriptions(String errorPrescriptions) {
		this.errorPrescriptions = errorPrescriptions;
	}

	@Transient
	public String getOtherChargesMessage() {
		return otherChargesMessage;
	}

	public void setOtherChargesMessage(String otherChargesMessage) {
		this.otherChargesMessage = otherChargesMessage;
	}

	@Transient
	public String getItemsRequiredError() {
		return itemsRequiredError;
	}

	public void setItemsRequiredError(String itemsRequiredError) {
		this.itemsRequiredError = itemsRequiredError;
	}

	/**
	 * Formats the  Account sale number to: C 1
	 * @return The formatted AS Number.
	 */
	@Transient
	public String getFormattedASNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + sequenceNumber;
			}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + sequenceNumber;
			}
		}
		return null;
	}

	@Column(name = "WT_VAT_AMOUNT")
	public Double getWtVatAmount() {
		return wtVatAmount;
	}

	public void setWtVatAmount(Double wtVatAmount) {
		this.wtVatAmount = wtVatAmount;
	}

	@Override
	public String toString() {
		return "ArTransaction [serviceLeaseKeyId=" + serviceLeaseKeyId
				+ ", sequenceNumber=" + sequenceNumber + ", customerId="
				+ customerId + ", customerAcctId=" + customerAcctId
				+ ", transactionTypeId=" + transactionTypeId + ", termId="
				+ termId + ", transactionNumber=" + transactionNumber
				+ ", amount=" + amount + ", transactionDate=" + transactionDate
				+ ", glDate=" + glDate + ", dueDate=" + dueDate
				+ ", divisionId=" + divisionId
				+ ", currencyId=" + currencyId
				+ ", currencyRateId=" + currencyRateId
				+ ", wtVatAmount=" + wtVatAmount
				+ ", currencyRateValue=" + currencyRateValue
				+ ", description=" + description 
				+ ", aRlineMessage=" + aRlineMessage + ", isPosting="
				+ isPosting + ", balance=" + balance + ", paymentStatus="
				+ paymentStatus + ", getId()=" + getId() + ", getCreatedBy()="
				+ getCreatedBy() + ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + ", getFormWorkflowId()="
				+ getFormWorkflowId() + "]";
	}

	@Override
	@Transient
	public List<? extends BaseItem> getInventoryItems() {
		return accountSaleItems;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		switch (transactionTypeId) {
		case ArTransactionType.TYPE_AR_TRANSACTION_CENTRAL:
		case ArTransactionType.TYPE_AR_TRANSACTION_NSB3:
		case ArTransactionType.TYPE_AR_TRANSACTION_NSB4:
		case ArTransactionType.TYPE_AR_TRANSACTION_NSB5:
		case ArTransactionType.TYPE_AR_TRANSACTION_NSB8:
		case ArTransactionType.TYPE_AR_TRANSACTION_NSB8A:
			return AR_TRANSACTION_OBJECT_TYPE_ID;
		case ArTransactionType.TYPE_ACCOUNT_SALE:
			return ACCOUNT_SALE_ORDER_OBJECT_TYPE_ID;
		case ArTransactionType.TYPE_SALE_RETURN:
			return ACCOUNT_SALE_RETURN_OBJECT_TYPE_ID;
		case ArTransactionType.TYPE_ACCOUNT_SALES_IS:
			return ACCOUNT_SALE_IS_OBJECT_TYPE_ID;
		case ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS:
			return ACCOUNT_SALE_RETURN_IS_OBJECT_TYPE_ID;
		case ArTransactionType.TYPE_SALE_RETURN_EMPTY_BOTTLE:
			return ACCOUNT_SALE_RETURN_IS_OBJECT_TYPE_ID;
		}
		throw new RuntimeException("No specified Object Type Id for ArTransactionType " + transactionTypeId);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if(accountSaleItems != null){
			for (AccountSaleItem asi : accountSaleItems) {
				children.add(asi);
			}
		}
		if(arLines != null) {
			for (ArLine arLine : arLines) {
				children.add(arLine);
			}
		}
		if(arServiceLines != null) {
			for (ArServiceLine arServiceLine : arServiceLines) {
				children.add(arServiceLine);
			}
		}
		if(referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public String getShortDescription() {
		if(transactionTypeId != null) {
			String shortDesc = (transactionTypeId.equals(ArTransactionType.TYPE_ACCOUNT_SALE)) ? "AS" : "ASR";
			return shortDesc + " " + sequenceNumber;
		}

		//Default to Account sales type.
		return "AS" + " " + sequenceNumber;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getTransactionTypeId();
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
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Transient
	public void serializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type  = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Override
	@Transient
	public Date getGLDate() {
		return glDate != null ? glDate : transactionDate;
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

	@Transient
	public Integer getReferenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Transient
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Transient
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}