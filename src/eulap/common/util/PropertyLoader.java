package eulap.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;


/**
 * A class that loads the property file. 
 * This will read the resources starting from after src/.

 *
 */
public class PropertyLoader {

	/**
	 * Convert the property file to Properties object. 
	 * @param fileName The file name of the property file. 
	 */
	public static Properties getProperties (String fileName) {
		InputStream fis = null;
		Properties props = null;
		try {
			fis = PropertyLoader.class.getResourceAsStream(fileName);
			props = new Properties();
			props.load(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return props;
	}

	/**
	 * Get the configuration property file.
	 * @param path the path of the configuration file. 
	 */
	public static Configuration getConfiguration (String path) throws ConfigurationException {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
			new FileBasedConfigurationBuilder<FileBasedConfiguration>((Class<? extends FileBasedConfiguration>) PropertiesConfiguration.class)
			.configure(params.properties().setFileName(path));
		return builder.getConfiguration();
	}
}
