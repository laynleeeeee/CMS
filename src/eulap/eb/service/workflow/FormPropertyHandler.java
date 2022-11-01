package eulap.eb.service.workflow;

import org.apache.commons.configuration2.Configuration;

/**
 * Handler class for form property reader. 

 *
 */
public interface FormPropertyHandler {
	/**
	 * Handle the form property
	 * @param config The apache commons Configuration
	 */
	void handleProperties (FormProperty formProperty, String propertyName, Configuration config);
}
