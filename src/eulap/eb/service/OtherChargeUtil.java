package eulap.eb.service;

import java.util.List;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.TaxUtil;
import eulap.eb.domain.hibernate.OtherCharge;

/**
 * Utility service class for {@link OtherCharge}.

 * @param <T>
 *
 */
public class OtherChargeUtil<T extends OtherCharge> {
	/**
	 * Recompute vat amount and set null up amount to zero.
	 * @param OtherCharge The {@link OtherCharge}.
	 */
	public static <T extends OtherCharge> void recomputeOtherCharges(List<T> otherCharges) {
		for(OtherCharge otherCharge : otherCharges) {
			setNullUpAmountToZero(otherCharge);
			double vatAmount = 0;
			if(TaxUtil.isVatable(otherCharge.getTaxTypeId())) {
				vatAmount = TaxUtil.computeVat(otherCharge.getUpAmount());
				otherCharge.setVatAmount(NumberFormatUtil.multiplyWFP(vatAmount, otherCharge.getQuantity()));
			}
		}
	}

	/**
	 * Set null UpAmount to zero.
	 * @param otherCharge The {@link OtherCharge}.
	 */
	public static <T extends OtherCharge> void setNullUpAmountToZero(T otherCharge) {
		if(otherCharge.getUpAmount() == null) {
			otherCharge.setUpAmount(0.0);
		}
	}

	/**
	 * Recompute {@link OtherCharge} costs data.
	 * Do this before saving. 
	 * @param <T> Type of {@link OtherCharge}.
	 * @param otherCharge The {@link OtherCharge}.
	 * @param discount The discount amount.
	 * @param currencyRate The currency rate.
	 */
	public static <T extends OtherCharge> void recomputeOCCosts(T otherCharge,  Double discount, double currencyRate) {
		double quantity = otherCharge.getQuantity() != null ? otherCharge.getQuantity() : 1;
		double vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(otherCharge.getVatAmount() != null ? otherCharge.getVatAmount() : 0);
		double upAmount = otherCharge.getUpAmount() != null ? otherCharge.getUpAmount() : 0;
		otherCharge.setUpAmount(CurrencyUtil.convertAmountToPhpRate(upAmount, currencyRate));
		otherCharge.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, currencyRate, true));
		discount = NumberFormatUtil.roundOffTo2DecPlaces(discount != null ? discount : 0);
		double amount = NumberFormatUtil.roundOffTo2DecPlaces(NumberFormatUtil.multiplyWFP(quantity, upAmount) - vatAmount - discount);
		otherCharge.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
	}
}
