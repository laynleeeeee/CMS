package eulap.eb.domain.hibernate;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Domain object for IS_CLASS_SETUP table.

 *
 */
@Entity
@Table(name = "IS_CLASS_SETUP")
public class IsClassSetup extends BaseDomain{
	private String name;
	private boolean isRevenue;
	private boolean isGrossProfit;
	private Integer sequenceOrder;
	private List<IsAtSetup> isAtSetups;
		
	public enum FIELD {
		id, name, isRevenue, isGrossProfit, sequenceOrder
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "IS_CLASS_SETUP_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column (name = "NAME")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column (name = "IS_REVENUE")
	public boolean isRevenue() {
		return isRevenue;
	}
	
	public void setRevenue(boolean isRevenue) {
		this.isRevenue = isRevenue;
	}
	
	@Column (name = "IS_GROSS_PROFIT")
	public boolean isGrossProfit() {
		return isGrossProfit;
	}
	
	public void setGrossProfit(boolean isGrossProfit) {
		this.isGrossProfit = isGrossProfit;
	}
	
	@Column (name = "SEQUENCE_ORDER")
	public Integer getSequenceOrder() {
		return sequenceOrder;
	}
	
	public void setSequenceOrder(Integer sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}
	
	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn (name = "IS_CLASS_SETUP_ID", insertable=false, updatable=false)
	public List<IsAtSetup> getIsAtSetups() {
		return isAtSetups;
	}
	
	public void setIsAtSetups(List<IsAtSetup> isAtSetups) {
		this.isAtSetups = isAtSetups;
	}

	@Override
	public String toString() {
		return "IsClassSetup [name=" + name + ", isRevenue=" + isRevenue
				+ ", isGrossProfit=" + isGrossProfit + ", sequenceOrder="
				+ sequenceOrder + ", isAtSetups=" + isAtSetups + "]";
	}
}
