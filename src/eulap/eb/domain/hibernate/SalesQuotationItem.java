package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation class for SALES_QUOTATION_ITEM table

 */

@Entity
@Table(name="SALES_QUOTATION_ITEM")
public class SalesQuotationItem extends BaseFormLine {
	@Expose
	private Integer salesQuotationId;
	@Expose
	private Double grossAmount;
	@Expose
	private Integer taxTypeId;
	@Expose
	private Double vatAmount;
	@Expose
	private Double amount;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Double discountValue;
	@Expose
	private String memo;
	@Expose
	private Integer itemId;
	@Expose
	private Double quantity;
	@Expose
	private String stockCode;
	@Expose
	private Integer itemDiscountTypeId;
	@Expose
	private Double discount;
	private Item item;
	private ItemDiscountType itemDiscountType;
	private TaxType taxType;

	public static final int OBJECT_TYPE = 12001;

	public enum FIELD {
		id, salesQuotationId, itemId, itemDiscountId, discount, amount, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SALES_QUOTATION_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setReferenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Column(name = "SALES_QUOTATION_ID")
	public Integer getSalesQuotationId() {
		return salesQuotationId;
	}

	public void setSalesQuotationId(Integer salesQuotationId) {
		this.salesQuotationId = salesQuotationId;
	}

	@Column(name = "GROSS_AMOUNT")
	public Double getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(Double grossAmount) {
		this.grossAmount = grossAmount;
	}

	@Column(name = "TAX_TYPE_ID")
	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	@Column(name = "VAT_AMOUNT")
	public Double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "ITEM_DISCOUNT_TYPE_ID")
	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
	}

	@Column(name = "DISCOUNT_VALUE")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@Column(name = "MEMO")
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "ITEM_ID")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "QUANTITY")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Column(name = "DISCOUNT")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_DISCOUNT_TYPE_ID", insertable=false, updatable=false)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}

	@OneToOne
	@JoinColumn(name = "TAX_TYPE_ID", insertable=false, updatable=false)
	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesQuotationItem [salesQuotationId=").append(salesQuotationId).append(", grossAmount=")
				.append(grossAmount).append(", taxTypeId=").append(taxTypeId).append(", vatAmount=").append(vatAmount)
				.append(", amount=").append(amount).append(", refenceObjectId=").append(refenceObjectId)
				.append(", discountValue=").append(discountValue).append(", memo=").append(memo).append(", itemId=")
				.append(itemId).append(", quantity=").append(quantity).append(", stockCode=").append(stockCode)
				.append(", itemDiscountTypeId=").append(itemDiscountTypeId).append(", discount=").append(discount).append("]");
		return builder.toString();
	}
}
