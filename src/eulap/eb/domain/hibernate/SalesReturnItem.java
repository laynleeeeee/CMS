package eulap.eb.domain.hibernate;

/**
 * Sales return base class. 

 *
 */
public interface SalesReturnItem {
	
	/**
	 * The item id. 
	 * @return
	 */
	
	Integer getItemId();
	/**
	 * Get the sales reference Id. 
	 * @return
	 */
	Integer getSalesReferenceId ();
}
