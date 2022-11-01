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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;
import eulap.eb.web.dto.PurchaseOrderItemDto;

/**
 * Object representation of R_PURCHASE_ORDER table.

 * 
 */
@Entity
@Table(name = "R_PURCHASE_ORDER")
public class RPurchaseOrder extends BaseFormWorkflow {
	private Integer companyId;
	private Integer supplierId;
	private Integer supplierAccountId;
	private Integer termId;
	private Date poDate;
	private Integer poNumber;
	private String remarks;
	private List<RPurchaseOrderItem> rPoItems;
	private String poMessage;
	private Company company;
	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private Term term;
	private String poItemsJson;
	private String supplierName;
	private Integer requestedById;
	private String employeeName;
	private String requesterName;
	private String prReference;
	private String strPrReferences;
	private String strCustomerName;
	private String strFleetCode;
	private List<PurchaseOrderLine> poLines;
	private String poLineJson;
	private Double grandTotal;
	private Integer divisionId;
	private Date estDeliveryDate;
	private String bmsNumber;
	private Integer currencyId;
	private Integer currencyRateId;
	private double currencyRateValue;
	private Division division;
	private Currency currency;
	private double totalNetOfVAT;
	private double totalVAT;
	private List<PurchaseOrderItemDto> poItemDtos;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;

	public static final int OBJECT_TYPE_ID = 38;
	public static final int REQUESTER_NAME_MAX_CHAR = 100;
	public static final int BMS_NUMBER_MAX_CHAR = 50;

	/**
	 * OR Type ID for Purchase Request-Purchase Order relationship: 3003
	 */
	public static final int PR_PO_OR_TYPE_ID = 3003;

	public enum FIELD {
		id, companyId, supplierId, supplierAccountId, termId, poDate,
		poNumber, remarks, formWorkflowId, ebObjectId, requestedById,
		divisionId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_PURCHASE_ORDER_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="SUPPLIER_ID", columnDefinition="int(10)")
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name="SUPPLIER_ACCOUNT_ID", columnDefinition="int(10)")
	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	@Column(name="TERM_ID", columnDefinition="int(10)")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
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
	@JoinColumn(name="R_PURCHASE_ORDER_ID", insertable=false, updatable=false)
	public List<RPurchaseOrderItem> getrPoItems() {
		return rPoItems;
	}

	public void setrPoItems(List<RPurchaseOrderItem> rPoItems) {
		this.rPoItems = rPoItems;
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
	@JoinColumn (name = "SUPPLIER_ID", insertable=false, updatable=false)
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@OneToOne
	@JoinColumn (name = "SUPPLIER_ACCOUNT_ID", insertable=false, updatable=false)
	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	@OneToOne
	@JoinColumn (name = "TERM_ID", insertable=false, updatable=false)
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
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
			} else {
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
		poItemsJson = gson.toJson(rPoItems);
	}

	@Transient
	public void deSerializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RPurchaseOrderItem>>(){}.getType();
		rPoItems = gson.fromJson(poItemsJson, type);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (rPoItems != null) {
			children.addAll(rPoItems);
		}
		if (poLines != null) {
			children.addAll(poLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Column(name="REQUESTED_BY_ID", columnDefinition="int(10)")
	public Integer getRequestedById() {
		return requestedById;
	}

	public void setRequestedById(Integer requestedById) {
		this.requestedById = requestedById;
	}

	@Transient
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Column (name = "REQUESTER_NAME", columnDefinition="VARCHAR(100)")
	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	@Column(name="PR_REFERENCE", columnDefinition="text")
	public String getPrReference() {
		return prReference;
	}

	public void setPrReference(String prReference) {
		this.prReference = prReference;
	}

	@Transient
	public String getStrPrReferences() {
		return strPrReferences;
	}

	public void setStrPrReferences(String strPrReferences) {
		this.strPrReferences = strPrReferences;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return poDate;
	}

	@Transient
	public String getStrCustomerName() {
		return strCustomerName;
	}

	public void setStrCustomerName(String strCustomerName) {
		this.strCustomerName = strCustomerName;
	}

	@Transient
	public String getStrFleetCode() {
		return strFleetCode;
	}

	public void setStrFleetCode(String strFleetCode) {
		this.strFleetCode = strFleetCode;
	}

	@Transient
	public List<PurchaseOrderLine> getPoLines() {
		return poLines;
	}

	public void setPoLines(List<PurchaseOrderLine> poLines) {
		this.poLines = poLines;
	}

	@Transient
	public String getPoLineJson() {
		return poLineJson;
	}

	public void setPoLineJson(String poLineJson) {
		this.poLineJson = poLineJson;
	}

	@Transient
	public void serializePOLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		poLineJson = gson.toJson(poLines);
	}

	@Transient
	public void deserializePOLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PurchaseOrderLine>>(){}.getType();
		poLines = gson.fromJson(poLineJson, type);
	}

	@Transient
	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	@Column (name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column (name = "ESTIMATED_DELIVERY_DATE")
	public Date getEstDeliveryDate() {
		return estDeliveryDate;
	}

	public void setEstDeliveryDate(Date estDeliveryDate) {
		this.estDeliveryDate = estDeliveryDate;
	}

	@Column (name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column (name = "BMS_NUMBER")
	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Column (name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column (name = "CURRENCY_RATE_VALUE")
	public double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		String workflowName = super.getWorkflowName();
		if (divisionId != null) {
			return workflowName + divisionId;
		}
		return workflowName;
	}

	@OneToOne
	@JoinColumn (name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@OneToOne
	@JoinColumn (name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Transient
	public double getTotalNetOfVAT() {
		return totalNetOfVAT;
	}

	public void setTotalNetOfVAT(double totalNetOfVAT) {
		this.totalNetOfVAT = totalNetOfVAT;
	}

	@Transient
	public double getTotalVAT() {
		return totalVAT;
	}

	public void setTotalVAT(double totalVAT) {
		this.totalVAT = totalVAT;
	}

	@Transient
	public List<PurchaseOrderItemDto> getPoItemDtos() {
		return poItemDtos;
	}

	public void setPoItemDtos(List<PurchaseOrderItemDto> poItemDtos) {
		this.poItemDtos = poItemDtos;
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
	public void serializeReferenceDocuments (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RPurchaseOrder [companyId=").append(companyId).append(", supplierId=").append(supplierId)
				.append(", supplierAccountId=").append(supplierAccountId).append(", termId=").append(termId)
				.append(", poDate=").append(poDate).append(", poNumber=").append(poNumber).append(", remarks=")
				.append(remarks).append(", rPoItems=").append(rPoItems).append(", poMessage=").append(poMessage)
				.append(", requestedById=").append(requestedById).append(", divisionId=").append(divisionId)
				.append("]");
		return builder.toString();
	}
}