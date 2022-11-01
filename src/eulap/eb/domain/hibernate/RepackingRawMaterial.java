package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation class for REPACKING_RAW_MATERIAL table

 */

@Entity
@Table(name = "REPACKING_RAW_MATERIAL")
public class RepackingRawMaterial extends BaseItem {
	private Integer repackingId;
	@Expose
	private Integer refenceObjectId;

	public enum FIELD {
		id, repackingId, ebObjectId, itemId
	}

	public static final int RP_RAW_MATERIAL_OBJ_TYPE_ID = 164;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REPACKING_RAW_MATERIAL_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="REPACKING_ID", columnDefinition="int(10)")
	public Integer getRepackingId() {
		return repackingId;
	}

	public void setRepackingId(Integer repackingId) {
		this.repackingId = repackingId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return RP_RAW_MATERIAL_OBJ_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RepackingRawMaterial [repackingId=").append(repackingId).append(", refenceObjectId=")
				.append(refenceObjectId).append(", getItemId()=").append(getItemId()).append(", getQuantity()=")
				.append(getQuantity()).append(", getUnitCost()=").append(getUnitCost()).append(", getEbObjectId()=")
				.append(getEbObjectId()).append("]");
		return builder.toString();
	}

}
