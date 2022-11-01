package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.eb.service.oo.OOChild;


/**
 * Object representation of CSI_RAW_MATERIAL table.

 *
 */
@Entity
@Table(name="CSI_RAW_MATERIAL")
public class CsiRawMaterial extends BaseItem implements OOChild{
	private Integer cashSaleId;
	private Integer csiFinishedProdId;
	private Integer warehouseId;
	private Integer referenceObjectId;
	private Integer productLineId;

	public enum FIELD {
		id, cashSaleId, csiFinishedProdId, itemId, quantity, unitCost
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CSI_RAW_MATERIAL_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the cash sale Id.
	 * @return The Id of the cash sales.
	 */
	@Column(name="CASH_SALE_ID", columnDefinition="int(10)")
	public Integer getCashSaleId() {
		return cashSaleId;
	}

	public void setCashSaleId(Integer cashSaleId) {
		this.cashSaleId = cashSaleId;
	}

	/**
	 * Get the Finished item Id.
	 * @return The id of the finished item.
	 */
	@Column(name="CSI_FINISHED_PRODUCT_ID", columnDefinition="int(10)")
	public Integer getCsiFinishedProdId() {
		return csiFinishedProdId;
	}

	public void setCsiFinishedProdId(Integer csiFinishedProdId) {
		this.csiFinishedProdId = csiFinishedProdId;
	}

	/**
	 * Get the Id of the warehouse.
	 * @return The id of the warehouse.
	 */
	@Column(name="WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return 44;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Transient
	public Integer getProductLineId() {
		return productLineId;
	}

	public void setProductLineId(Integer productLineId) {
		this.productLineId = productLineId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CsiRawMaterial [cashSaleId=").append(cashSaleId).append(", csiFinishedProdId=")
				.append(csiFinishedProdId).append(", warehouseId=").append(warehouseId).append(", ebObjectId=")
				.append(getEbObjectId()).append(", referenceObjectId=").append(referenceObjectId).append(", getId()=")
				.append(getId()).append(", getItemId()=").append(getItemId()).append(", getQuantity()=")
				.append(getQuantity()).append(", getUnitCost()=").append(getUnitCost()).append("]");
		return builder.toString();
	}

}
