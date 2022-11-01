package eulap.eb.dao.impl.sql;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Reads the SQL Resource bundle. 

 *
 */
public class SQLProperyReader {

	public static final String ITEM_SQL = "eulap.eb.dao.impl.sql.itemSql";
	public static final String RETAIL_ITEM_SQL = "eulap.eb.dao.impl.sql.retailItemSql";

	private SQLProperyReader() {
	}
	
	/**
	 * get the value in the sql property file.
	 * @param key The key in the property file.
	 * @param path The path of the property file.
	 * @return The sql value.
	 */
	public static String getValue(String key, String path) {
		try {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(path);
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			throw new RuntimeException('!' + key + '!');
		}
	}
}
