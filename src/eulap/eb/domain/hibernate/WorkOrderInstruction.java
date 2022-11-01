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
 * Object representation class for WORK_ORDER_INSTRUCTION table

 */

@Entity
@Table(name="WORK_ORDER_INSTRUCTION")
public class WorkOrderInstruction extends BaseFormLine {
	@Expose
	private Integer workOrderId;
	@Expose
	private String workDescription;
	@Expose
	private Integer refenceObjectId;

	public enum FIELD {
		id, workOrderId, ebObjectId
	}

	public static final int OBJECT_TYPE_ID = 12014;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "WORK_ORDER_INSTRUCTION_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "WORK_ORDER_ID")
	public Integer getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(Integer workOrderId) {
		this.workOrderId = workOrderId;
	}

	@Column(name = "WORK_INSTRUCTION")
	public String getWorkDescription() {
		return workDescription;
	}

	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
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

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkOrderInstruction [workOrderId=").append(workOrderId).append(", workDescription=")
				.append(workDescription).append("]");
		return builder.toString();
	}
}
