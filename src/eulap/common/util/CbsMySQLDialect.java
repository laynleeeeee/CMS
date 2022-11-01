package eulap.common.util;


import org.hibernate.dialect.MySQL5Dialect;

/**
 * CBS MySQL Dialect. This is a work around from 
 * the stored procedure error that could not find -1 hibernate
 * type. This will explicit register the -1 as String. 
 * This is not safe but will current use this as work around. 
 * 

 *
 */
public class CbsMySQLDialect extends MySQL5Dialect {

	public CbsMySQLDialect () {
		super();
		// Set negative one (-1) as string
		registerHibernateType(-1, "string");
	}
}
