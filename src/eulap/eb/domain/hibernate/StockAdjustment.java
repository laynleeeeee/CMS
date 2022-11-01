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


/**
 * Object representation of STOCK_ADJUSTMENT table.

 * 
 */
@Entity
@Table(name = "STOCK_ADJUSTMENT")
public class StockAdjustment extends BaseFormWorkflow {
	private Integer stockAdjustmentClassificationId;
	private Integer companyId;
	private Integer divisionId;
	private String bmsNumber;
	private Integer warehouseId;
	private Integer stockAdjustmentTypeId;
	private Integer saNumber;
	private Date saDate;
	private String remarks;
	private List<StockAdjustmentItem> saItems;
	private Company company;
	private Warehouse warehouse;
	private StockAdjustmentType adjustmentType;
	private String adjustmentItemsJson;
	private String saMessage;
	private Integer typeId;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private Division division;
	private Currency currency;
	private CurrencyRate currencyRate;

	public static final int STOCK_ADJUSTMENT_IN = 1;
	public static final int STOCK_ADJUSTMENT_OUT = 2;
	public static final int STOCK_ADJUSTMENT_IS_IN = 3;
	public static final int STOCK_ADJUSTMENT_IS_OUT = 4;

	/**
	 * Object Type Id for Stock Adjustment = 10.
	 */
	public static final int STOCK_ADJUSTMENT_OBJECT_TYPE_ID = 10;

	public enum FIELD {
		id, stockAdjustmentClassificationId, companyId, warehouseId, stockAdjustmentTypeId,
		saNumber, saDate, remarks, formWorkflowId, ebObjectId, divisionId, bmsNumber, currencyId,
		currencyRateId, currencyRateValue
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "STOCK_ADJUSTMENT_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "STOCK_ADJUSTMENT_CLASSIFICATION_ID")
	public Integer getStockAdjustmentClassificationId() {
		return stockAdjustmentClassificationId;
	}

	public void setStockAdjustmentClassificationId(Integer stockAdjustmentClassificationId) {
		this.stockAdjustmentClassificationId = stockAdjustmentClassificationId;
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Column(name="STOCK_ADJUSTMENT_TYPE_ID", columnDefinition="int(10)")
	public Integer getStockAdjustmentTypeId() {
		return stockAdjustmentTypeId;
	}

	public void setStockAdjustmentTypeId(Integer stockAdjustmentTypeId) {
		this.stockAdjustmentTypeId = stockAdjustmentTypeId;
	}

	@Column(name="SA_NUMBER", columnDefinition="int(20)")
	public Integer getSaNumber() {
		return saNumber;
	}

	public void setSaNumber(Integer saNumber) {
		this.saNumber = saNumber;
	}

	@Column(name="SA_DATE", columnDefinition="date")
	public Date getSaDate() {
		return saDate;
	}

	public void setSaDate(Date saDate) {
		this.saDate = saDate;
	}

	@Column(name="REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name="BMS_NUMBER")
	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
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

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="STOCK_ADJUSTMENT_ID", insertable=false, updatable=false)
	public List<StockAdjustmentItem> getSaItems() {
		return saItems;
	}

	public void setSaItems(List<StockAdjustmentItem> saItems) {
		this.saItems = saItems;
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
	@JoinColumn (name = "WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	@OneToOne
	@JoinColumn (name = "STOCK_ADJUSTMENT_TYPE_ID", insertable=false, updatable=false)
	public StockAdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(StockAdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	@Override
	public String toString() {
		return "StockAdjustment [companyId=" + companyId + ", warehouseId="
				+ ", divisionId="+ divisionId
				+ ", bmsNumber="+ bmsNumber
				+ ", currencyId="+ currencyId
				+ ", currencyRateId="+ currencyRateId
				+ ", currencyRateValue="+ currencyRateValue
				+ warehouseId + ", stockAdjustmentTypeId="
				+ stockAdjustmentTypeId + ", saNumber=" + saNumber
				+ ", saDate=" + saDate + ", remarks=" + remarks + "]";
	}

	/**
	 * Generate formatted SA Number using the first letter of the company.
	 */
	@Transient
	public String getFormattedSANumber() {
		if (company != null){
			if(company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + saNumber;
		 	}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + saNumber;
			}
		}
		return null;
	}

	@Transient
	public String getAdjustmentItemsJson() {
		return adjustmentItemsJson;
	}

	public void setAdjustmentItemsJson(String adjustmentItemsJson) {
		this.adjustmentItemsJson = adjustmentItemsJson;
	}

	@Transient
	public void serializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		adjustmentItemsJson = gson.toJson(saItems);
	}

	@Transient
	public void deSerializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<StockAdjustmentItem>>(){}.getType();
		saItems = gson.fromJson(adjustmentItemsJson, type);
	}

	@Transient
	public String getSaMessage() {
		return saMessage;
	}

	public void setSaMessage(String saMessage) {
		this.saMessage = saMessage;
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
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		for (StockAdjustmentItem saItem : saItems) {
			children.add(saItem);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return STOCK_ADJUSTMENT_OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getStockAdjustmentClassificationId();
	}

	@Override
	@Transient
	public String getShortDescription() {
		Integer childTypeId = getChildTypeId();
		if (childTypeId == null)
			throw new RuntimeException("Unkknown child type id");
		// To determine if this is SA in or out, we need to check
		// the quantity of its children.
		String inOrOut = "IN";
		if (childTypeId == 11)
			inOrOut = "OUT";
		return "SA " + inOrOut + " " + saNumber;

	}

	@Transient
	private Integer getChildTypeId () {
		if (saItems == null || saItems.isEmpty()) {
			return null;
		}
		StockAdjustmentItem item = saItems.iterator().next();
		if (item == null || item.getEbObject() == null)
			return null;
		Integer childTypeId = item.getEbObject().getObjectTypeId();
		return childTypeId;
	}

	/**
	 * Get the short label of this form.
	 * @return The label of the form.
	 */
	@Transient
	public String getShortLabel () {
		String shortLabel = "";
		Integer childTypeId = getChildTypeId();
		if (childTypeId ==null) {
			if(saItems.isEmpty()) {
				return null;
			}
			StockAdjustmentItem item = saItems.iterator().next();
			boolean isIn = item.getQuantity() > 0;
			if (isIn) {
				shortLabel = "SA I";
			} else {
				shortLabel = "SA O";
			}

		} else {
			if (childTypeId == 11) {
				shortLabel = "SA O - IS";
			} else {
				shortLabel = "SA I - IS";
			}
		}
		return shortLabel;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return saDate;
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
}