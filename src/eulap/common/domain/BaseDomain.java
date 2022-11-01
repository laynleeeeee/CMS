package eulap.common.domain;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * Domain abstract class that implements the domain objects.

 *
 */
public abstract class BaseDomain implements Domain {
	@Expose
	private int id;
	@Expose
	private int createdBy;
	@Expose
	private Date createdDate;
	@Expose
	private int updatedBy;
	@Expose
	private Date updatedDate;
	
	@Override public int getId() {
		return id;
	}
	
	@Override public void setId(int id) {
		this.id = id;
	}

	@Override public int getCreatedBy() {
		return createdBy;
	}

	@Override  public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	@Override public Date getCreatedDate() {
		return createdDate;
	}

	@Override public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override public int getUpdatedBy() {
		return updatedBy;
	}

	@Override public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override public Date getUpdatedDate() {
		return updatedDate;
	}

	@Override public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		} else if (!obj.getClass().equals(this.getClass())) {
			return super.equals(obj);
		}
		Domain other = (Domain) obj;
		return this.id == other.getId();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() * 123 * id;
	}
}
