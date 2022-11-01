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

import eulap.common.domain.BaseDomain;

/**
 * Class representation of ITEM_BUYING_PRICE table in the database.

 * 
 */
@Entity
@Table(name = "ITEM_BUYING_PRICE")
public class ItemBuyingPrice extends BaseDomain {
	private Integer itemId;
	private Integer companyId;
	private Double buyingPrice;
	private boolean active;
	private Item item;
	private Company company;
	private String companyName;

	public enum FIELD {
		id, itemId, companyId, buyingPrice, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_BUYING_PRICE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "ITEM_ID", columnDefinition="int(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "BUYING_PRICE", columnDefinition="double")
	public Double getBuyingPrice() {
		return buyingPrice;
	}

	public void setBuyingPrice(Double buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne
	@JoinColumn (name = "ITEM_ID", insertable=false, updatable=false, nullable=true)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@ManyToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false, nullable=true)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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
		StringBuilder builder = new StringBuilder();
		builder.append("ItemBuyingPrice [itemId=").append(itemId)
				.append(", companyId=").append(companyId)
				.append(", buyingPrice=").append(buyingPrice)
				.append(", active=").append(active).append("]");
		return builder.toString();
	}

}
