package eulap.eb.service;

import java.util.List;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.TaxUtil;
import eulap.eb.domain.hibernate.SerialItem;
import eulap.eb.domain.hibernate.ServiceLine;

/**
 * Utility class to convert currency to PHP value

 */

public class CurrencyUtil {
	public static final int MIN_DECIMAL_PLACES = 2;
	public static final int MAX_DECIMAL_PLACES = 6;

	/**
	 * Convert the other charge cost/amount to PHP value
	 * @param serviceLines The list of other charges
	 * @param currencyRate The currency rate
	 * @return The other charge cost/amount to PHP value
	 */
	public static<T extends ServiceLine> List<T> convertServiceLineCostsToPhp(List<T> serviceLines, double currencyRate) {
		for (T t : serviceLines) {
			double quantity = t.getQuantity() != null ? t.getQuantity() : 1.0;
			double upAmount = t.getUpAmount();
			t.setUpAmount(convertAmountToPhpRate(upAmount, currencyRate));
			double vatAmount = 0;
			if (TaxUtil.isVatable(t.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(t.getVatAmount() != null ? t.getVatAmount() : 0);
				t.setVatAmount(convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, upAmount) - vatAmount);
			t.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		}
		return serviceLines;
	}

	/**
	 * Convert the other charge cost/amount to PHP value
	 * @param serialItems The list of other charges
	 * @param currencyRate The currency rate
	 * @return The other charge cost/amount to PHP value
	 */
	public static<T extends SerialItem> List<T> convertSerialItemsAmountToPhp(List<T> serialItems, double currencyRate) {
		for (T t : serialItems) {
			double quantity = t.getQuantity() != null ? t.getQuantity() : 1.0;
			double unitCost = convertAmountToPhpRate(t.getUnitCost(), currencyRate);
			t.setUnitCost(unitCost);
			double vatAmount = 0;
			if (TaxUtil.isVatable(t.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(t.getVatAmount());
				t.setVatAmount(convertAmountToPhpRate(vatAmount, currencyRate, true));
			}
			double discount = 0;
			if (t.getDiscount() != null) {
				discount = NumberFormatUtil.roundOffTo2DecPlaces(t.getDiscount());
				t.setDiscount(convertAmountToPhpRate(discount, currencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, unitCost) - vatAmount - discount);
			t.setAmount(convertAmountToPhpRate(amount, currencyRate, true));
		}
		return serialItems;
	}

	/**
	 * Convert monetary value from based on the rate
	 * @param value The monetary value
	 * @param rate The currency rate
	 * @return The converted monetary value
	 */
	public static double convertMonetaryValues(Double value, double rate) {
		return convertMonetaryValues(value, rate, false);
	}

	/**
	 * Convert monetary value from based on the rate
	 * @param value The monetary value
	 * @param rate The currency rate
	 * @param isRoundTo2DecPlaces True if round the value to nearest 2 decimal places, otherwise false
	 * @return The converted monetary value
	 */
	public static double convertMonetaryValues(Double value, double rate, boolean isRoundTo2DecPlaces) {
		double amount = NumberFormatUtil.divideWFP((value != null ? value : 0), rate);
		return isRoundTo2DecPlaces ? NumberFormatUtil.roundOffTo2DecPlaces(amount)
				: NumberFormatUtil.roundOffNumber(amount, MAX_DECIMAL_PLACES);
	}

	/**
	 * Convert monetary value to PHP rate
	 * @param value The monetary value
	 * @param rate The currency rate
	 * @return The converted monetary value
	 */
	public static double convertAmountToPhpRate(Double value, double rate) {
		return convertAmountToPhpRate(value, rate, false);
	}

	/**
	 * Convert monetary value to PHP rate
	 * @param value The monetary value
	 * @param rate The currency rate
	 * @param isRoundTo2DecPlaces True if round the value to nearest 2 decimal places, otherwise false
	 * @return The converted monetary value
	 */
	public static double convertAmountToPhpRate(Double value, double rate, boolean isRoundTo2DecPlaces) {
		double amount = NumberFormatUtil.multiplyWFP((value != null ? value : 0), rate);
		return isRoundTo2DecPlaces ? NumberFormatUtil.roundOffTo2DecPlaces(amount)
				: NumberFormatUtil.roundOffNumber(amount, MAX_DECIMAL_PLACES);
	}
}
