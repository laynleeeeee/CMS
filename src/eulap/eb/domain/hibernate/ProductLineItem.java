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

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the PRODUCT_LINE_ITEM table.

 * 
 */
@Entity
@Table (name="PRODUCT_LINE_ITEM")
public class ProductLineItem extends BaseDomain {
	@Expose
	private Integer productLineId;
	@Expose
	private Integer itemId;
	@Expose
	private Double quantity;
	private Item item;
	@Expose
	private String itemStockCode;
	@Expose
	private String description;
	@Expose
	private String uom;
	private Integer warehouseId;

	public enum FIELD {productLineId, itemId, quantity};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PRODUCT_LINE_ITEM_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PRODUCT_LINE_ID", columnDefinition = "INT(10)")
	public Integer getProductLineId() {
		return productLineId;
	}

	public void setProductLineId(Integer productLineId) {
		this.productLineId = productLineId;
	}

	@Column(name = "ITEM_ID", columnDefinition = "INT(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "QUANTITY", columnDefinition = "DOUBLE")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@ManyToOne
	@JoinColumn(name = "ITEM_ID", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductLineItem [productLineId=").append(productLineId).append(", itemId=").append(itemId)
				.append(", quantity=").append(quantity).append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}

	@Transient
	public String getItemStockCode() {
		return itemStockCode;
	}

	public void setItemStockCode(String itemStockCode) {
		this.itemStockCode = itemStockCode;
	}

	@Transient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	@Transient
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
}
