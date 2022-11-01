package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the GL_ENTRY_SOURCE table.

 *
 */
@Entity
@Table(name = "GL_ENTRY_SOURCE")
public class GlEntrySource extends BaseDomain {
	public static final int SOURCE_GENERAL_LEDGER = 1;
	private String description;
	
	public enum FIELD {id, description}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "GL_ENTRY_SOURCE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	/**
	 * Get the description.
	 * @return The description.
	 */
	@Column (name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "GlStatus [description=" + description + "]";
	}
}
