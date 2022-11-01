package eulap.common.domain;

import java.util.Date;

/**
 * Base domain object. 

 *
 */
public interface Domain {
	
	/**
	 * Set the unique id of this domain.
	 * @param id The unique id.
	 */
	void setId (int id);
	
	/**
	 * Get the id of this domain.
	 * @return The unique id of this domain.
	 */
	int getId ();
	
	/**
	 * Set the user who created this domain. 
	 * @param id The id of the user.
	 */
	void setCreatedBy (int id);
	
	/**
	 * Get the id of the creator of this domain.
	 * @return The id of the user.
	 */
	int getCreatedBy ();
	
	/**
	 * Set the creation date.
	 * @param date The creation date.
	 */
	void setCreatedDate (Date date);

	/**
	 * Get the creation date.
	 * @return The creation date.
	 */
	Date getCreatedDate ();
	
	/**
	 * Set the user who updated this domain.
	 * @param id The unique id of the user.
	 */
	void setUpdatedBy (int id);
	
	/**
	 * Get the id of the modifier of this domain.
	 * @return The unique id of the user.
	 */
	int getUpdatedBy ();
	
	/**
	 * Set the date in which this domain was updated.
	 * @param date The date where this domain was updated.
	 */
	void setUpdatedDate (Date date);
	
	/**
	 * Get the updated date.
	 * @return The updated date.
	 */
	Date getUpdatedDate ();
}
