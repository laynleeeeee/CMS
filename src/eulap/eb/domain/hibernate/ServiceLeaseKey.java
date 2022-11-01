package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of EB_SK_KEY table in the CBS database.
 * 

 * 
 */
@Entity
@Table(name = "EB_SL_KEY")
public class ServiceLeaseKey extends BaseDomain {
	private Date startDate;
	private Date endDate;
	private int ebClientInfoId;
	private boolean active;
	private EBClientInfo clientInfo;

	public enum FIELD {id, startDate, endDate, ebClientInfoId, active};
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EB_SL_KEY_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "START_DATE", columnDefinition = "TIMESTAMP")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE", columnDefinition = "TIMESTAMP")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "EB_CLIENT_INFO_ID", columnDefinition = "INT(10)")
	public int getEbClientInfoId() {
		return ebClientInfoId;
	}

	public void setEbClientInfoId(int ebClientInfoId) {
		this.ebClientInfoId = ebClientInfoId;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToOne
	@JoinColumn(name = "EB_CLIENT_INFO_ID", insertable = false, updatable = false)
	public EBClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(EBClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	public String toString() {
		return "ServiceLeaseKey [id="+getId()+"startDate=" + startDate + ", endDate="
				+ endDate + ", ebClientInfoId=" + ebClientInfoId + ", active="
				+ active + ", clientInfo=" + clientInfo + "]";
	}
}
