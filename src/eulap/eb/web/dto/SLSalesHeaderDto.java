package eulap.eb.web.dto;

import java.math.BigDecimal;
import java.util.List;

import eulap.eb.service.bir.AlphalistHeader;
import eulap.eb.service.bir.AlphalistSchedule;

/**
 * DTO for Summary List of Sales and Purchases report header.

 */

public class SLSalesHeaderDto implements AlphalistHeader{
	private static String SUMMARY_TYPE = "H,S";
	private static String RDO = "111";
	private final String tin;
	private final String branchCode;
	private final String registeredName;
	private final String lastName;
	private final String firstName;
	private final String middleName;
	private final String tradeName;
	private final String streetBrgy;
	private final String cityProvince;
	private final BigDecimal exemptSales;
	private final BigDecimal zeroRatedSales;
	private final BigDecimal taxableSales;
	private final BigDecimal outputTax;
	private final String taxableMonth;
	private final Integer month;
	private final String fileName; 
	private List<AlphalistSchedule> controls;

	private SLSalesHeaderDto (String tin, String branchCode, String registeredName, String lastName, String firstName, String middleName, String tradeName,
			String streetBrgy, String cityProvince, BigDecimal exemptSales, BigDecimal zeroRatedSales, BigDecimal taxableSales, BigDecimal outputTax, 
			String taxableMonth, Integer month, String fileName, List<AlphalistSchedule> controls) {
		this.tin = tin; 
		this.branchCode = branchCode; 
		this.registeredName = registeredName;
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.tradeName = tradeName;
		this.streetBrgy = streetBrgy;
		this.cityProvince = cityProvince;
		this.exemptSales = exemptSales;
		this.zeroRatedSales = zeroRatedSales;
		this.taxableSales = taxableSales;
		this.outputTax = outputTax;
		this.taxableMonth = taxableMonth;
		this.month = month;
		this.fileName = fileName;
		this.controls = controls;
	}

	public static SLSalesHeaderDto getInstance (String tin, String branchCode, String registeredName, String lastName, String firstName, String middleName, String tradeName,
			String streetBrgy, String cityProvince, BigDecimal exemptSales, BigDecimal zeroRatedSales, BigDecimal taxableSales, BigDecimal outputTax, 
			String taxableMonth, Integer month, String fileName, List<AlphalistSchedule> controls) {
		return new SLSalesHeaderDto(tin, branchCode, registeredName, lastName, firstName, middleName, tradeName, streetBrgy, cityProvince, exemptSales, zeroRatedSales, 
				taxableSales, outputTax, taxableMonth, month, fileName, controls);
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
		csv.append(',').append(exemptSales);
		csv.append(',').append(zeroRatedSales);
		csv.append(',').append(taxableSales);
		csv.append(',').append(outputTax);
		csv.append(',').append('"').append(RDO).append('"');
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

	public BigDecimal getExemptSales() {
		return exemptSales;
	}

	public BigDecimal getZeroRatedSales() {
		return zeroRatedSales;
	}

	public BigDecimal getTaxableSales() {
		return taxableSales;
	}

	public BigDecimal getOutputTax() {
		return outputTax;
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
		builder.append("SLSalesHeaderDto [tin=").append(tin).append(", branchCode=").append(branchCode)
				.append(", registeredName=").append(registeredName).append(", lastName=").append(lastName)
				.append(", firstName=").append(firstName).append(", middleName=").append(middleName)
				.append(", tradeName=").append(tradeName).append(", streetBrgy=").append(streetBrgy)
				.append(", cityProvince=").append(cityProvince).append(", exemptSales=").append(exemptSales)
				.append(", zeroRatedSales=").append(zeroRatedSales).append(", taxableSales=").append(taxableSales)
				.append(", outputTax=").append(outputTax).append(", fileName=").append(fileName).append(", controls=")
				.append(controls).append("]");
		return builder.toString();
	}
}

