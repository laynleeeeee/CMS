package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * Class that represents PR_OTHER_CHARGE table of the database

 */
@Entity
@Table(name = "PR_OTHER_CHARGE")
public class PrOtherCharge extends AROtherCharge {
	private Integer processingReportId;

	/**
	 * Object Type for {@link PrOtherCharge} = 7
	 */
	public static final int OBJECT_TYPE_ID = 7;

	public enum FIELD {
		id, processReportId, arLineSetupId, unitOfMeasurementId, amount, upAmount, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PR_OTHER_CHARGE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PROCESSING_REPORT_ID", columnDefinition="int(10)")
	public Integer getProcessingReportId() {
		return processingReportId;
	}

	public void setProcessingReportId(Integer processingReportId) {
		this.processingReportId = processingReportId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		//PR_OTHER_CHARGE_ITEM type in OBJECT_TYPE table
		return 7;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		// TODO Auto-generated method stub
		return null;
	}
}
