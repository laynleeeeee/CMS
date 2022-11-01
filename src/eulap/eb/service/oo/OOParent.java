package eulap.eb.service.oo;

import java.util.List;

/**
 * Interface of object to object for parent domains.

 *
 */
public interface OOParent extends OODomain{
	
	/**
	 * The reference object of this domain. 
	 * @return
	 */
	List<OOChild> getChildren ();
}
