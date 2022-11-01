package eulap.eb.domain.hibernate;

import java.util.Date;

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

import eulap.common.domain.BaseDomain;
import eulap.eb.web.dto.FleetItemConsumedDto;

/**
 * Object representation class of FLEET_TOOL_CONDITION Table.

 *
 */
@Entity
@Table(name="FLEET_TOOL_CONDITION")
public class FleetToolCondition extends BaseDomain {
	@Expose
	private Integer itemId;
	private Item item;
	@Expose
	private String stockCode;
	@Expose
	private Integer refObjectId;
	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	@Expose
	private String toolCondition;
	@Expose
	private Boolean status;
	private Boolean active;
	private FleetItemConsumedDto fleetItemConsumedDto;

	public static final int OR_TYPE_ID = 54;
	public static final int OBJECT_TYPE_ID = 104;

	public enum FIELD {
		id, itemId, refObjectId, ebObjectId, status, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FLEET_TOOL_CONDITION_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate(){
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "ITEM_ID", columnDefinition = "INT(10)")
	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@OneToOne
	@JoinColumn (name="ITEM_ID", columnDefinition="INT(10)", insertable=false, updatable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@Transient
	public Integer getRefObjectId() {
		return refObjectId;
	}

	public void setRefObjectId(Integer refObjectId) {
		this.refObjectId = refObjectId;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "INT(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", columnDefinition="INT(10)", insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="TOOL_CONDITION", columnDefinition="TEXT")
	public String getToolCondition() {
		return toolCondition;
	}

	public void setToolCondition(String toolCondition) {
		this.toolCondition = toolCondition;
	}

	@Column(name="STATUS", columnDefinition="TINYINT(1)")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name="ACTIVE", columnDefinition="TINYINT(1)")
	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Transient
	public FleetItemConsumedDto getFleetItemConsumedDto() {
		return fleetItemConsumedDto;
	}

	public void setFleetItemConsumedDto(FleetItemConsumedDto fleetItemConsumedDto) {
		this.fleetItemConsumedDto = fleetItemConsumedDto;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetToolCondition [itemId=").append(itemId).append(", stockCode=").append(stockCode)
				.append(", refObjectId=").append(refObjectId).append(", ebObjectId=").append(ebObjectId)
				.append(", status=").append(status).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
