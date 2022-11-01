package eulap.eb.validator;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * 
 * Validator Resource Bundle Messages. 
 * 

 *
 */
public class ValidatorMessages {
	private static final String BUNDLE_NAME = "validator-messages.properties";

	private static final FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
		new FileBasedConfigurationBuilder<FileBasedConfiguration>((Class<? extends FileBasedConfiguration>) PropertiesConfiguration.class)
		.configure( new Parameters().properties().setFileName(BUNDLE_NAME));
	
	/**
	 * Get the error message from the validator-message 
	 * @param key key in the property file. 
	 * @return property file value. 
	 */
	public static String getString(String key) {
		try {
			return builder.getConfiguration().getString(key);
		} catch (ConfigurationException e) {
			return '!' + key + '!';
		}
	}
}
