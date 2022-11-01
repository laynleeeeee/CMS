package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represent the item volume conversion.

 * 
 */
@Entity
@Table(name = "ITEM_VOLUME_CONVERSION")
public class ItemVolumeConversion extends BaseDomain {
	private int itemId;
	private int companyId;
	private double quantity;
	private double volumeConversion;
	private boolean active;
	private String stockCode;
	private String companyName;
	private Item item;

	public enum FIELD {
		id, itemId, active, volumeConversion, quantity, companyId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "ITEM_VOLUME_CONVERSION_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column (name="ITEM_ID", columnDefinition="int(10)")
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Column (name="COMPANY_ID", columnDefinition="int(10)")
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	@Column (name="QUANTITY", columnDefinition="double")
	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@Column (name="VOLUME_CONVERSION", columnDefinition="double")
	public double getVolumeConversion() {
		return volumeConversion;
	}

	public void setVolumeConversion(double volumeConversion) {
		this.volumeConversion = volumeConversion;
	}

	@Column (name="ACTIVE", columnDefinition="TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@Transient
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Transient
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "ItemVolumeConversion [itemId=" + itemId + ", companyId="
				+ companyId + ", quantity=" + quantity + ", volumeConversion="
				+ volumeConversion + ", active=" + active + "]";
	}
}
