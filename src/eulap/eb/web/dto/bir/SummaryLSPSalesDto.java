package eulap.eb.web.dto.bir;

import java.util.Date;

import eulap.common.util.DateUtil;
import eulap.eb.service.bir.AlphalistDetail;

/**
 * Container class for the attributes needed for Summary List of Sales.
 * 

 *
 */
public class SummaryLSPSalesDto implements AlphalistDetail {
	public static final String SUMMARY_TYPE_DSLS = "D,S";
	private Integer seqNum;
	private Date taxableMonth;
	private String tinCust;
	private String branchCodeCust;
	private String registeredNameCust;
	private String lastNameCust;
	private String firstNameCust;
	private String middleNameCust;
	private String streetBrgy;
	private String cityProvince;
	private Double grossSalesAmt;
	private Double exemptSalesAmt;
	private Double zeroRatedSales;
	private Double taxableSales;
	private Double outputTax;
	private Double grossTaxableSales;
	private String ownersTin;
	private String registeredAddressCust;

	public static SummaryLSPSalesDto getInstanceOf (Date taxableMonth, String tinCust, String registeredNameCust, String lastNameCust,
			String firstNameCust, String middleNameCust, Double grossSalesAmt, Double exemptSalesAmt, Double zeroRatedSales,
			Double taxableSales, Double outputTax, Double grossTaxableSales) {
		SummaryLSPSalesDto sls = new SummaryLSPSalesDto();
		sls.taxableMonth = taxableMonth;
		sls.tinCust = tinCust;
		sls.registeredNameCust = registeredNameCust;
		sls.lastNameCust = lastNameCust;
		sls.firstNameCust = firstNameCust;
		sls.middleNameCust = middleNameCust;
		sls.grossSalesAmt = grossSalesAmt;
		sls.exemptSalesAmt = exemptSalesAmt;
		sls.zeroRatedSales = zeroRatedSales;
		sls.taxableSales = taxableSales;
		sls.outputTax = outputTax;
		sls.grossTaxableSales = grossTaxableSales;
		return sls;
	}

	@Override
	public String convertDetailToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SUMMARY_TYPE_DSLS);
		csv.append(',').append('"').append(tinCust).append('"');
		// Customer's Registered Name
		if (registeredNameCust != null && !registeredNameCust.isEmpty()) {
			csv.append(',').append('"').append(registeredNameCust).append('"');
			csv.append(','); // Empty Customer's First Name
			csv.append(','); // Empty Customer's Middle Name
			csv.append(','); // Empty Customer's Last Name
		} else {
			csv.append(','); // Empty Registered Name
			csv.append(',').append('"').append(lastNameCust).append('"');
			csv.append(',').append('"').append(firstNameCust).append('"');
			csv.append(',').append('"').append(middleNameCust).append('"');
		}
		csv.append(',').append('"').append(streetBrgy).append('"');
		csv.append(',').append('"').append(cityProvince).append('"');
		csv.append(',').append(exemptSalesAmt);
		csv.append(',').append(zeroRatedSales);
		csv.append(',').append(taxableSales);
		csv.append(',').append(outputTax);
		csv.append(',').append('"').append(ownersTin).append('"');
		csv.append(',').append(DateUtil.formatDate(taxableMonth));
		return csv.toString();
	}

	public Integer getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public Date getTaxableMonth() {
		return taxableMonth;
	}

	public void setTaxableMonth(Date taxableMonth) {
		this.taxableMonth = taxableMonth;
	}

	public String getTinCust() {
		return tinCust;
	}

	public void setTinCust(String tinCust) {
		this.tinCust = tinCust;
	}

	public String getBranchCodeCust() {
		return branchCodeCust;
	}

	public void setBranchCodeCust(String branchCodeCust) {
		this.branchCodeCust = branchCodeCust;
	}

	public String getRegisteredNameCust() {
		return registeredNameCust;
	}

	public void setRegisteredNameCust(String registeredNameCust) {
		this.registeredNameCust = registeredNameCust;
	}

	public String getLastNameCust() {
		return lastNameCust;
	}

	public void setLastNameCust(String lastNameCust) {
		this.lastNameCust = lastNameCust;
	}

	public String getFirstNameCust() {
		return firstNameCust;
	}

	public void setFirstNameCust(String firstNameCust) {
		this.firstNameCust = firstNameCust;
	}

	public String getMiddleNameCust() {
		return middleNameCust;
	}

	public void setMiddleNameCust(String middleNameCust) {
		this.middleNameCust = middleNameCust;
	}

	public Double getGrossSalesAmt() {
		return grossSalesAmt;
	}

	public void setGrossSalesAmt(Double grossSalesAmt) {
		this.grossSalesAmt = grossSalesAmt;
	}

	public Double getExemptSalesAmt() {
		return exemptSalesAmt;
	}

	public void setExemptSalesAmt(Double exemptSalesAmt) {
		this.exemptSalesAmt = exemptSalesAmt;
	}

	public Double getZeroRatedSales() {
		return zeroRatedSales;
	}

	public void setZeroRatedSales(Double zeroRatedSales) {
		this.zeroRatedSales = zeroRatedSales;
	}

	public Double getTaxableSales() {
		return taxableSales;
	}

	public void setTaxableSales(Double taxableSales) {
		this.taxableSales = taxableSales;
	}

	public Double getOutputTax() {
		return outputTax;
	}

	public void setOutputTax(Double outputTax) {
		this.outputTax = outputTax;
	}

	public Double getGrossTaxableSales() {
		return grossTaxableSales;
	}

	public void setGrossTaxableSales(Double grossTaxableSales) {
		this.grossTaxableSales = grossTaxableSales;
	}

	public String getStreetBrgy() {
		return streetBrgy;
	}

	public void setStreetBrgy(String streetBrgy) {
		this.streetBrgy = streetBrgy;
	}

	public String getCityProvince() {
		return cityProvince;
	}

	public void setCityProvince(String cityProvince) {
		this.cityProvince = cityProvince;
	}

	public String getOwnersTin() {
		return ownersTin;
	}

	public void setOwnersTin(String ownersTin) {
		this.ownersTin = ownersTin;
	}

	public String getRegisteredAddressCust() {
		return registeredAddressCust;
	}

	public void setRegisteredAddressCust(String registeredAddressCust) {
		this.registeredAddressCust = registeredAddressCust;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SummaryLSPSalesDto [seqNum=").append(seqNum).append(", taxableMonth=").append(taxableMonth)
				.append(", tinCust=").append(tinCust).append(", branchCodeCust=").append(branchCodeCust)
				.append(", registeredNameCust=").append(registeredNameCust).append(", lastNameCust=")
				.append(lastNameCust).append(", firstNameCust=").append(firstNameCust).append(", middleNameCust=")
				.append(middleNameCust).append(", streetBrgy=").append(streetBrgy).append(", cityProvince=")
				.append(cityProvince).append(", grossSalesAmt=").append(grossSalesAmt).append(", exemptSalesAmt=")
				.append(exemptSalesAmt).append(", zeroRatedSales=").append(zeroRatedSales).append(", taxableSales=")
				.append(taxableSales).append(", outputTax=").append(outputTax).append(", grossTaxableSales=")
				.append(grossTaxableSales).append(", ownersTin=").append(ownersTin).append("]");
		return builder.toString();
	}
}
