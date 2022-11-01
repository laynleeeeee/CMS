package eulap.eb.service.jr;

import java.io.Closeable;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;

/**
 * An interface that will define the implementation of the business logic in generating the report 

 *
 */
public interface EBJRServiceHandler<T> extends Closeable{
	/**
	 * Get the next page data 
	 * @return The next page
	 */
	Page<T> nextPage (PageSetting pageSetting);
}
