package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.MillingItem;


/**
 * Object representation of RETAIL - TRANSFER_RECEIPT_ITEM table.

 *
 */
@Entity
@Table(name="R_TRANSFER_RECEIPT_ITEM")
public class RTransferReceiptItem extends BaseItem implements MillingItem {
	@Expose
	private Integer rTransferReceiptId;
	private RTransferReceipt rTransferReceipt;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private String stockCodeIs;
	@Expose
	private Double itemBagQuantity;

	/**
	 * Object Type Id for {@link RTransferReceiptItem} = 26.
	 */
	public static final int OBJECT_TYPE_ID = 26;

	public enum FIELD {
		id, rTransferReceiptId, itemId, quantity, unitCost
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "R_TRANSFER_RECEIPT_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the transfer receipt Id.
	 * @return The Id of the transfer receipt.
	 */
	@Column(name="R_TRANSFER_RECEIPT_ID", columnDefinition="int(10)")
	public Integer getrTransferReceiptId() {
		return rTransferReceiptId;
	}

	public void setrTransferReceiptId(Integer rTransferReceiptId) {
		this.rTransferReceiptId = rTransferReceiptId;
	}

	@ManyToOne
	@JoinColumn (name = "R_TRANSFER_RECEIPT_ID", insertable=false, updatable=false)
	public RTransferReceipt getrTransferReceipt() {
		return rTransferReceipt;
	}

	public void setrTransferReceipt(RTransferReceipt rTransferReceipt) {
		this.rTransferReceipt = rTransferReceipt;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Override
	public String toString() {
		return "RTransferReceiptItem [getId()=" + getId()
				+ ", rTransferReceiptId=" + rTransferReceiptId + ", stockCode="
				+ getStockCode() + ", rTransferReceipt=" + rTransferReceipt
				+ ", existingStocks=" + getExistingStocks() + ", getItem()="
				+ getItem() + ", getItemId()=" + getItemId()
				+ ", getQuantity()=" + getQuantity() + ", getUnitCost()="
				+ getUnitCost() + "]";
	}

	@Transient
	public String getStockCodeIs() {
		return stockCodeIs;
	}

	public void setStockCodeIs(String stockCodeIs) {
		this.stockCodeIs = stockCodeIs;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.refenceObjectId = referenceObjectId;
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
}