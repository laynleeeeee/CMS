package eulap.eb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.UomConversionDao;
import eulap.eb.domain.hibernate.UomConversion;

/**
 * Class the business logic of {@link UomConversion}

 *
 */
@Service
public class UomConversionService {
	@Autowired
	private UomConversionDao uomConversionDao;

	/**
	 * Get the unit measurement conversion object.
	 * @param uomConversionId The uom conversion id.
	 * @return The unit conversion object.
	 */
	public UomConversion getUomConversion (int uomConversionId) {
		return uomConversionDao.get(uomConversionId);
	}

	/**
	 * Get the unit conversion by from and to.
	 * @param uomFrom The unit of measurement to be converted.
	 * @param uomTo The unit of measurement to be converted to.
	 * @return The unit conversion
	 */
	public UomConversion getConvByFromAndTo (Integer uomFrom, Integer uomTo) {
		return uomConversionDao.getConvByFromAndTo(uomFrom, uomTo);
	}

	/**
	 * Convert the unit conversion from one measurement to another.
	 * @param uomFrom The unit of measurement to be converted.
	 * @param uomTo The unit of measurement to be converted to.
	 * @return The converted value.
	 */
	public double convert (Integer uomFrom, Integer uomTo, double quantity) {
		UomConversion uomConversion = getConvByFromAndTo(uomFrom, uomTo);
		return convert(uomConversion, quantity);
	}

	/**
	 * Convert the unit conversion from one measurement to another.
	 * @param uomConversionId The uom conversion id.
	 * @return The converted value.
	 */
	public double convert (Integer uomConversionId, double quantity) {
		UomConversion uomConversion =  uomConversionDao.get(uomConversionId);
		return convert(uomConversion, quantity);
	}

	private double convert (UomConversion uomConversion, double quantity) {
		if (uomConversion == null) {
			throw new RuntimeException("Invalid unit measurement conversion.");
		}
		return NumberFormatUtil.multiplyWFP(uomConversion.getValue(), quantity);
	}

	/**
	 * Convert from uom to to uom, returns zero if UOM Conversion is not available.
	 * @param uomFromId The ud of the from uom.
	 * @param uomToId The id of the to uom.
	 * @param strQuantity quantity to be computed
	 * @return The converted quantity.
	 */
	public double convert(int uomFromId, int uomToId, String strQuantity) {
		// If the combination of the from and to uom ids is null, reverse values of both ids.
		UomConversion uomConversion = getConvByFromAndTo(uomFromId, uomToId);
		boolean isReversed = false;
		if(uomConversion == null) {
			uomConversion = getConvByFromAndTo(uomToId, uomFromId);
			isReversed = true;
		}

		if(uomConversion != null) {
			// Check if quantity is numeric
			boolean isQtyNumeric = StringFormatUtil.isNumeric(strQuantity.trim());
			double fromQuantity = isQtyNumeric ? Double.parseDouble(strQuantity) : 0;
			double value = uomConversion.getValue();
			// If the retrieved UOM Conversion was reversed, divide value from the quantity
			// Otherwise, multiply value of quantity to the value of the uom conversion.
			double computedQty = isReversed ? NumberFormatUtil.divideWFP(fromQuantity, value) : NumberFormatUtil.multiplyWFP(fromQuantity, value);
			return computedQty;
		}

		// Return zero if no uom conversion retrieved.
		return 0;
	}
}
