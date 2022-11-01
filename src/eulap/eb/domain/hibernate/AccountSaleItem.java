package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.MillingItem;

/**
 *A class representation of ACCOUNT_SALE_ITEM table in the CBS database.

 *
 */
@Entity
@Table(name="ACCOUNT_SALE_ITEM")
public class AccountSaleItem extends SaleItem implements SalesReturnItem, MillingItem {
	@Expose
	private Integer refAccountSaleItemId;
	@Expose
	private Integer arTransactionId;
	@Expose
	private Integer referenceObjectId;
	private Integer transactionTypeId;
	@Expose
	private Integer salesRefId;
	@Expose
	private Double itemBagQuantity;
	@Expose
	private Double origBagQty;

	public static final int ACCOUNT_SALE_ITEM_OBJECT_TYPE_ID = 15;
	public static final int ACCOUNT_SALE_RETURN_ITEM_OBJECT_TYPE_ID = 23;
	public static final int ACCOUNT_SALE_EXCHANGE_ITEM_OBJECT_TYPE_ID = 24;
	public static final int ACCOUNT_SALE_RETURN_ITEM_IS_OBJECT_TYPE_ID = 157;
	public static final int ACCOUNT_SALE_EXCHANGE_ITEM_IS_OBJECT_TYPE_ID = 158;
	public static final int ACCOUNT_SALE_ITEM_IS_OBJECT_TYPE_ID = 159;
	public static final int ASR_ITEM_EMPTY_BOTTLE_OBJECT_TYPE_ID = 170;

	public static final int ASI_OR_TYPE_ID = 5;

	public enum FIELD {
		id, itemId, arTransactionId, warehouseId, itemDiscountId, itemSrpId,
		discount, amount, quantity, refAccountSaleItemId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "ACCOUNT_SALE_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column(name = "REF_ACCOUNT_SALE_ITEM_ID")
	public Integer getRefAccountSaleItemId() {
		return refAccountSaleItemId;
	}
	
	public void setRefAccountSaleItemId(Integer refAccountSaleItemId) {
		this.refAccountSaleItemId = refAccountSaleItemId;
	}
	
	@Column(name = "AR_TRANSACTION_ID")
	public Integer getArTransactionId() {
		return arTransactionId;
	}
	
	public void setArTransactionId(Integer arTransactionId) {
		this.arTransactionId = arTransactionId;
	}

	@Override
	public String toString() {
		return "AccountSaleItem [accountSaleId=" + arTransactionId
				+ ", itemId=" + getItemId() + ", warehouseId=" + getWarehouseId() + ", itemDiscountId="
				+ getItemDiscountId() + ", itemSrpId=" + getItemSrpId()
				+ ", srp=" + getSrp() + ", discount=" + getDiscount() + ", amount=" + getAmount() + "]";
	}

	@Override
	@Transient
	public Integer getSalesReferenceId() {
		return refAccountSaleItemId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if (transactionTypeId != null) {
			switch (transactionTypeId) {
			case ArTransactionType.TYPE_ACCOUNT_SALE:
				return ACCOUNT_SALE_ITEM_OBJECT_TYPE_ID;
			case ArTransactionType.TYPE_ACCOUNT_SALES_IS:
				return ACCOUNT_SALE_ITEM_IS_OBJECT_TYPE_ID;
			case ArTransactionType.TYPE_SALE_RETURN:
				if (super.getQuantity() < 0) {
					return ACCOUNT_SALE_RETURN_ITEM_OBJECT_TYPE_ID;
				}
				return ACCOUNT_SALE_EXCHANGE_ITEM_OBJECT_TYPE_ID;
			case ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS:
				if (super.getQuantity() < 0) {
					return ACCOUNT_SALE_RETURN_ITEM_IS_OBJECT_TYPE_ID;
				}
				return ACCOUNT_SALE_EXCHANGE_ITEM_IS_OBJECT_TYPE_ID;
			case ArTransactionType.TYPE_SALE_RETURN_EMPTY_BOTTLE:
				return ASR_ITEM_EMPTY_BOTTLE_OBJECT_TYPE_ID;
			}
		}
		throw new RuntimeException("No specified object type Id for AR transaction type id = " + transactionTypeId);
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		if(getQuantity() == null) {
			return null;
		}
		if(getQuantity() < 0) {
			//No reference of returned stocks
			return null;
		}
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Transient
	public Integer getReferenceObjectId() {
		return referenceObjectId;
	}

	@Transient
	public Integer getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(Integer transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	@Transient
	public Integer getSalesRefId() {
		return salesRefId;
	}

	public void setSalesRefId(Integer salesRefId) {
		this.salesRefId = salesRefId;
	}

	@Transient
	@Override
	public Double getItemBagQuantity() {
		return itemBagQuantity;
	}

	@Override
	public void setItemBagQuantity(Double itemBagQuantity) {
		this.itemBagQuantity = itemBagQuantity;
	}

	@Transient
	public Double getOrigBagQty() {
		return origBagQty;
	}

	public void setOrigBagQty(Double origBagQty) {
		this.origBagQty = origBagQty;
	}
}
