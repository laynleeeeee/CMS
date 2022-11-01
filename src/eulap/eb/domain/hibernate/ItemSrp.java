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
 * Domain object of {@link ITEM_SRP}

 *
 */
@Entity
@Table (name="ITEM_SRP")
public class ItemSrp extends BaseDomain{
	private Integer itemId;
	private Integer companyId;
	private Double srp;
	private boolean active;
	private Item item;
	private Company company;
	private String companyName;
	private Double itemUnitCost;
	private Double sellingPrice;
	private Integer divisionId;
	private Division division;
	private String divisionName;

	public enum FIELD {
		id, itemId, companyId, srp, active, item, company, divisionId
	}

	public static ItemSrp getInstanceOf (Integer itemId, Integer companyId, 
			Double srp, boolean active, Integer divisionId) {
		ItemSrp itemSrp = new ItemSrp();
		itemSrp.setItemId(itemId);
		itemSrp.setCompanyId(companyId);
		itemSrp.setSrp(srp);
		itemSrp.setActive(active);
		itemSrp.setDivisionId(divisionId);
		return itemSrp;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ITEM_SRP_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get id of retail item.
	 * @return The id.
	 */
	@Column(name = "ITEM_ID", columnDefinition="int(10)")
	public Integer getItemId() {
		return itemId;
	}
	
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	/**
	 * Get id of company.
	 * @return The company id.
	 */
	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the suggested retail price.
	 * @return The srp.
	 */
	@Column(name = "SRP", columnDefinition="double")
	public Double getSrp() {
		return srp;
	}

	public void setSrp(Double srp) {
		this.srp = srp;
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

	@Transient
	public Double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(Double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	@Column(name = "DIVISION_ID", columnDefinition="int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@ManyToOne
	@JoinColumn (name = "DIVISION_ID", insertable=false, updatable=false, nullable=true)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Transient
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ItemSrp [itemId=").append(itemId).append(", companyId=").append(companyId).append(", srp=")
				.append(srp).append(", active=").append(active).append(", companyName=").append(companyName)
				.append(", itemUnitCost=").append(itemUnitCost).append(", sellingPrice=").append(sellingPrice)
				.append(", divisionId=").append(divisionId).append("]");
		return builder.toString();
	}

	@Transient
	public Double getItemUnitCost() {
		return itemUnitCost;
	}

	public void setItemUnitCost(Double itemUnitCost) {
		this.itemUnitCost = itemUnitCost;
	}
}
