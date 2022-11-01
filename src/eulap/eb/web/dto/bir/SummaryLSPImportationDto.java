package eulap.eb.web.dto.bir;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * Container class for the attributes needed for Summary List of importations.

 *
 */
public class SummaryLSPImportationDto implements AlphalistDetail {
	public static final String SUMMARY_TYPE_DSLI = "D,I";
	private String taxableMonth;
	private String importNumber;
	private String releaseDate;
	private String registeredName;
	private String importDate;
	private String countryOfOrigin;
	private Double landedCost;
	private Double dutiableValue;
	private Double chargesFromCustom;
	private Double taxableImports;
	private Double exemptImports;
	private Double vat;
	private String orNumber;
	private String ownersTin;
	private String dateOfPayment;

	@Override
	public String convertDetailToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SUMMARY_TYPE_DSLI); // Type of summary List
		csv.append(',').append('"').append(importNumber).append('"');
		csv.append(',').append(releaseDate);
		csv.append(',').append('"').append(registeredName).append('"');
		csv.append(',').append(importDate);
		csv.append(',').append('"').append(countryOfOrigin).append('"');
		csv.append(',').append(dutiableValue);
		csv.append(',').append(chargesFromCustom);
		csv.append(',').append(exemptImports);
		csv.append(',').append(taxableImports);
		csv.append(',').append(vat);
		csv.append(',').append('"').append(orNumber).append('"');
		csv.append(',').append(dateOfPayment);
		csv.append(',').append('"').append(ownersTin).append('"');
		csv.append(',').append(taxableMonth);
		return csv.toString();
	}

	public String getTaxableMonth() {
		return taxableMonth;
	}

	public void setTaxableMonth(String taxableMonth) {
		this.taxableMonth = taxableMonth;
	}

	public String getImportNumber() {
		return importNumber;
	}

	public void setImportNumber(String importNumber) {
		this.importNumber = importNumber;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getRegisteredName() {
		return registeredName;
	}

	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public Double getLandedCost() {
		return landedCost;
	}

	public void setLandedCost(Double landedCost) {
		this.landedCost = landedCost;
	}

	public Double getDutiableValue() {
		return dutiableValue;
	}

	public void setDutiableValue(Double dutiableValue) {
		this.dutiableValue = dutiableValue;
	}

	public Double getChargesFromCustom() {
		return chargesFromCustom;
	}

	public void setChargesFromCustom(Double chargesFromCustom) {
		this.chargesFromCustom = chargesFromCustom;
	}

	public Double getTaxableImports() {
		return taxableImports;
	}

	public void setTaxableImports(Double taxableImports) {
		this.taxableImports = taxableImports;
	}

	public Double getExemptImports() {
		return exemptImports;
	}

	public void setExemptImports(Double exemptImports) {
		this.exemptImports = exemptImports;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public String getOrNumber() {
		return orNumber;
	}

	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	public String getDateOfPayment() {
		return dateOfPayment;
	}

	public void setDateOfPayment(String dateOfPayment) {
		this.dateOfPayment = dateOfPayment;
	}

	public String getOwnersTin() {
		return ownersTin;
	}

	public void setOwnersTin(String ownersTin) {
		this.ownersTin = ownersTin;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SummaryLSPImportationDto [taxableMonth=").append(taxableMonth).append(", importNumber=")
				.append(importNumber).append(", releaseDate=").append(releaseDate).append(", registeredName=")
				.append(registeredName).append(", importDate=").append(importDate).append(", countryOfOrigin=")
				.append(countryOfOrigin).append(", landedCost=").append(landedCost).append(", dutiableValue=")
				.append(dutiableValue).append(", chargesFromCustom=").append(chargesFromCustom)
				.append(", taxableImports=").append(taxableImports).append(", exemptImports=").append(exemptImports)
				.append(", vat=").append(vat).append(", orNumber=").append(orNumber).append(", ownersTin=")
				.append(ownersTin).append(", dateOfPayment=").append(dateOfPayment).append("]");
		return builder.toString();
	}

}
