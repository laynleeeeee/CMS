package eulap.eb.web.dto.bir;

import java.math.BigDecimal;
import java.util.List;

import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * DTO for Summary List of Sales and Purchases report header.

 */

public class SLImportationsHeaderDto implements AlphalistHeader{
	private static String SUMMARY_TYPE = "H,I";
	private static String RDO = "111";
	private final String tin;
	private final String registeredName;
	private final String lastName;
	private final String firstName;
	private final String middleName;
	private final String tradeName;
	private final String streetBrgy;
	private final String cityProvince;
	private final BigDecimal dutiableValue;
	private final BigDecimal chargesFromCustom;
	private final BigDecimal exemptImports;
	private final BigDecimal taxableImports;
	private final BigDecimal vat;
	private final String taxableMonth;
	private final Integer month;
	private final String fileName; 
	private List<AlphalistSchedule> controls;

	private SLImportationsHeaderDto (String tin, String registeredName, String lastName, String firstName, String middleName, String tradeName,
			String streetBrgy, String cityProvince, BigDecimal dutiableValue, BigDecimal chargesFromCustom, BigDecimal exemptImports, BigDecimal taxableImports, 
			BigDecimal vat, String taxableMonth, Integer month, String fileName, List<AlphalistSchedule> controls) {
		this.tin = tin; 
		this.registeredName = registeredName;
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.tradeName = tradeName;
		this.streetBrgy = streetBrgy;
		this.cityProvince = cityProvince;
		this.dutiableValue = dutiableValue;
		this.chargesFromCustom = chargesFromCustom;
		this.exemptImports = exemptImports;
		this.taxableImports = taxableImports;
		this.vat = vat;
		this.taxableMonth = taxableMonth;
		this.month = month;
		this.fileName = fileName;
		this.controls = controls;
	}

	public static SLImportationsHeaderDto getInstance (String tin, String registeredName, String lastName, String firstName, String middleName, String tradeName,
			String streetBrgy, String cityProvince, BigDecimal dutiableValue, BigDecimal chargesFromCustom, BigDecimal exemptImports, BigDecimal taxableImports, 
			BigDecimal vat, String taxableMonth, Integer month, String fileName, List<AlphalistSchedule> controls) {
		return new SLImportationsHeaderDto(tin, registeredName, lastName, firstName, middleName, tradeName, streetBrgy, cityProvince, dutiableValue, 
				chargesFromCustom, exemptImports, taxableImports, vat, taxableMonth, month, fileName, controls);
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
		csv.append(',').append(dutiableValue);
		csv.append(',').append(chargesFromCustom);
		csv.append(',').append(exemptImports);
		csv.append(',').append(taxableImports);
		csv.append(',').append(vat);
		csv.append(',').append('"').append(RDO).append('"');
		csv.append(',').append(taxableMonth);
		csv.append(',').append(month);
		return csv.toString();
	}

	public String getTin() {
		return tin;
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

	public String getTradeName() {
		return tradeName;
	}

	public String getStreetBrgy() {
		return streetBrgy;
	}

	public String getCityProvince() {
		return cityProvince;
	}

	public String getTaxableMonth() {
		return taxableMonth;
	}

	public Integer getMonth() {
		return month;
	}

	public BigDecimal getDutiableValue() {
		return dutiableValue;
	}

	public BigDecimal getChargesFromCustom() {
		return chargesFromCustom;
	}

	public BigDecimal getTaxableImports() {
		return taxableImports;
	}

	public BigDecimal getExemptImports() {
		return exemptImports;
	}

	public BigDecimal getVat() {
		return vat;
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
		builder.append("SLImportationsHeaderDto [tin=").append(tin).append(", registeredName=").append(registeredName)
				.append(", lastName=").append(lastName).append(", firstName=").append(firstName).append(", middleName=")
				.append(middleName).append(", tradeName=").append(tradeName).append(", streetBrgy=").append(streetBrgy)
				.append(", cityProvince=").append(cityProvince).append(", dutiableValue=").append(dutiableValue)
				.append(", chargesFromCustom=").append(chargesFromCustom).append(", exemptImports=")
				.append(exemptImports).append(", taxableImports=").append(taxableImports).append(", vat=").append(vat)
				.append(", taxableMonth=").append(taxableMonth).append(", month=").append(month).append(", fileName=")
				.append(fileName).append(", controls=").append(controls).append("]");
		return builder.toString();
	}
}

