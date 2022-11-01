package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.UomConversion;

/**
 * Data access object of {@link UomConversion}

 *
 */
public interface UomConversionDao extends Dao<UomConversion> {

	/**
	 * Get the unit conversion by from and to.
	 * @param uomFrom The unit of measurement to be converted.
	 * @param uomTo The unit of measurement to be converted to.
	 * @return The unit conversion
	 */
	UomConversion getConvByFromAndTo (Integer uomFrom, Integer uomTo);

}
