package eulap.eb.web.dto;

import java.math.BigDecimal;
import java.util.List;

import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * DTO for Summary List of Sales and Purchases report header.

 */

public class SLPuchasesHeaderDto implements AlphalistHeader{
	private static String SUMMARY_TYPE = "H,P";
	private static String RDO = "111";
	private final String tin;
	private final String branchCode;
	private final String registeredName;
	private final String lastName;
	private final String firstName;
	private final String middleName;
	private final String fileName;
	private final String streetBrgy;
	private final String cityProvince;
	private final String tradeName;
	private final BigDecimal exemptPurchases;
	private final BigDecimal zeroRatedPurchases;
	private final BigDecimal taxablePurchases;
	private final BigDecimal servicePurchases;
	private final BigDecimal capitalGoodsPurchases;
	private final BigDecimal goodsPurchases;
	private final BigDecimal inputTax;
	private final BigDecimal nonCreditable;
	private final String taxableMonth;
	private final Integer month;
	private List<AlphalistSchedule> controls;

	private SLPuchasesHeaderDto (String tin, String branchCode, String registeredName, String lastName, String firstName, String middleName,
			String tradeName, String streetBrgy, String cityProvince, BigDecimal exemptPurchases, BigDecimal zeroRatedPurchases, BigDecimal taxablePurchases, BigDecimal servicePurchases, 
			BigDecimal capitalGoodsPurchases, BigDecimal goodsPurchases, BigDecimal inputTax, BigDecimal nonCreditable, String taxableMonth, Integer month, 
			String fileName, List<AlphalistSchedule> controls) {
		this.tin = tin; 
		this.branchCode = branchCode;
		this.registeredName = registeredName;
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.tradeName = tradeName;
		this.streetBrgy = streetBrgy;
		this.cityProvince = cityProvince;
		this.exemptPurchases = exemptPurchases;
		this.zeroRatedPurchases = zeroRatedPurchases;
		this.taxablePurchases = taxablePurchases;
		this.servicePurchases = servicePurchases;
		this.capitalGoodsPurchases = capitalGoodsPurchases;
		this.goodsPurchases = goodsPurchases;
		this.inputTax = inputTax;
		this.nonCreditable = nonCreditable;
		this.taxableMonth = taxableMonth;
		this.month = month;
		this.fileName = fileName;
		this.controls = controls;
	}

	public static SLPuchasesHeaderDto getInstance (String tin, String branchCode, String registeredName, String lastName, String firstName, String middleName,
			String tradeName, String streetBrgy, String cityProvince, BigDecimal exemptPurchases, BigDecimal zeroRatedPurchases, BigDecimal taxablePurchases, BigDecimal servicePurchases, 
			BigDecimal capitalGoodsPurchases, BigDecimal goodsPurchases, BigDecimal inputTax, BigDecimal nonCreditable, String taxableMonth, Integer month, 
			String fileName, List<AlphalistSchedule> controls) {
		return new SLPuchasesHeaderDto(tin, branchCode, registeredName, lastName, firstName,  middleName, tradeName,  streetBrgy, cityProvince, 
				exemptPurchases, zeroRatedPurchases, taxablePurchases, servicePurchases, capitalGoodsPurchases, goodsPurchases, inputTax, nonCreditable, taxableMonth, month, 
				fileName, controls);
	}

	@Override
	public String convertHeaderToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SUMMARY_TYPE);
		csv.append(',').append('"').append(tin).append('"');
		csv.append(',').append('"').append(registeredName).append('"');
		csv.append(',').append('"').append(lastName).append('"');
		csv.append(',').append('"').append(firstName).append('"');
		csv.append(',').append('"').append(middleName).append('"');
		csv.append(',').append('"').append(tradeName).append('"');
		csv.append(',').append('"').append(streetBrgy).append('"');
		csv.append(',').append('"').append(cityProvince).append('"');
		csv.append(',').append(exemptPurchases);
		csv.append(',').append(zeroRatedPurchases);
		csv.append(',').append(servicePurchases);
		csv.append(',').append(capitalGoodsPurchases);
		csv.append(',').append(goodsPurchases);
		csv.append(',').append(inputTax);
		csv.append(',').append(inputTax);
		csv.append(',').append(nonCreditable);
		csv.append(',').append(RDO);
		csv.append(',').append(taxableMonth);
		csv.append(',').append(month);
		return csv.toString();
	}

	public String getTin() {
		return tin;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getStreetBrgy() {
		return streetBrgy;
	}

	public String getCityProvince() {
		return cityProvince;
	}

	public String getTradeName() {
		return tradeName;
	}

	public BigDecimal getExemptPurchases() {
		return exemptPurchases;
	}

	public BigDecimal getZeroRatedPurchases() {
		return zeroRatedPurchases;
	}

	public BigDecimal getTaxablePurchases() {
		return taxablePurchases;
	}

	public BigDecimal getServicePurchases() {
		return servicePurchases;
	}

	public BigDecimal getCapitalGoodsPurchases() {
		return capitalGoodsPurchases;
	}

	public BigDecimal getGoodsPurchases() {
		return goodsPurchases;
	}

	public BigDecimal getInputTax() {
		return inputTax;
	}

	public BigDecimal getNonCreditable() {
		return nonCreditable;
	}

	public String getTaxableMonth() {
		return taxableMonth;
	}

	public Integer getMonth() {
		return month;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public List<AlphalistSchedule> getSchedules() {
		return controls;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SLPuchasesHeaderDto [tin=").append(tin).append(", branchCode=").append(branchCode)
				.append(", registeredName=").append(registeredName).append(", lastName=").append(lastName)
				.append(", firstName=").append(firstName).append(", middleName=").append(middleName)
				.append(", fileName=").append(fileName).append(", streetBrgy=").append(streetBrgy)
				.append(", cityProvince=").append(cityProvince).append(", tradeName=").append(tradeName)
				.append(", exemptPurchases=").append(exemptPurchases).append(", zeroRatedPurchases=")
				.append(zeroRatedPurchases).append(", servicePurchases=").append(servicePurchases)
				.append(", capitalGoodsPurchases=").append(capitalGoodsPurchases).append(", goodsPurchases=")
				.append(goodsPurchases).append(", inputTax=").append(inputTax)
				.append(", taxableMonth=").append(taxableMonth).append(", month=").append(month)
				.append(", controls=").append(controls).append("]");
		return builder.toString();
	}
}

