package eulap.eb.web.dto.bir;

import eulap.eb.service.bir.AlphalistDetail;

/**
 * Container class for the attributes needed for Summary List of Purchases.
 * 

 *
 */
public class SummaryLSPPurchasesDto implements AlphalistDetail {
	public static final String SUMMARY_TYPE_DSLP = "D,P";
	public static final String FTYPE_CODE_D255Q = "D2555";
	private String summaryType;
	private String ftypeCode;
	private Integer seqNum;
	private String taxableMonth;
	private String tinSup;
	private String branchCodeSup;
	private String registeredNameSup;
	private String lastNameSup;
	private String firstNameSup;
	private String middleNameSup;
	private String streetBrgy;
	private String cityProvince;
	private Double grossPurchasesAmt;
	private Double exemptPurchases;
	private Double zeroRatedPurchases;
	private Double taxablePurchases;
	private Double purchasesServicesAmt;
	private Double purchasesCapitalGoodsAmt;
	private Double purchasesGoodsOtherThanCapitalGoodsAmt;
	private Double inputTaxAmt;
	private Double grossTaxablePurchasesAmt;
	private String ownersTin;
	private Integer supplierId;
	private String registeredAddressSup;

	@Override
	public String convertDetailToCSV() {
		StringBuilder csv = new StringBuilder();
		csv.append(SUMMARY_TYPE_DSLP); // Type of summary List
		csv.append(',').append('"').append(tinSup).append('"'); // Supplier's TIN
		// Supplier's Registered Name
		if (registeredNameSup != null && !registeredNameSup.isEmpty()) {
			csv.append(',').append('"').append(registeredNameSup).append('"');
			csv.append(','); // Empty Supplier's First Name
			csv.append(','); // Empty Supplier's Middle Name
			csv.append(','); // Empty Supplier's Last Name
		} else {
			csv.append(','); // Empty Registered Name
			csv.append(',').append('"').append(lastNameSup).append('"'); // Supplier's Last Name
			csv.append(',').append('"').append(firstNameSup).append('"'); // Supplier's First Name
			csv.append(',').append('"').append(lastNameSup).append('"'); // Supplier's Middle Name
		}
		// Supplier's Address
		csv.append(',').append('"').append(streetBrgy).append('"');
		csv.append(',').append('"').append(cityProvince).append('"');
		// Amount of Exempt Purchases
		csv.append(',').append(exemptPurchases);
		// Amount of Zero Rated
		csv.append(',').append(zeroRatedPurchases);
		// Amount of Taxable - Services
		csv.append(',').append(purchasesServicesAmt);
		// Amount of Taxable purchases - capital goods
		csv.append(',').append(purchasesCapitalGoodsAmt);
		// Amount of Taxable purchases - other that Capital Goods
		csv.append(',').append(purchasesGoodsOtherThanCapitalGoodsAmt);
		// Amount of Input Tax
		csv.append(',').append(inputTaxAmt);
		// Owner's TIN
		csv.append(',').append('"').append(ownersTin).append('"');
		csv.append(',').append(taxableMonth);
		return csv.toString();
	}

	public String getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	public String getFtypeCode() {
		return ftypeCode;
	}

	public void setFtypeCode(String ftypeCode) {
		this.ftypeCode = ftypeCode;
	}

	public Integer getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(Integer seqNum) {
		this.seqNum = seqNum;
	}

	public String getTaxableMonth() {
		return taxableMonth;
	}

	public void setTaxableMonth(String taxableMonth) {
		this.taxableMonth = taxableMonth;
	}

	public String getTinSup() {
		return tinSup;
	}

	public void setTinSup(String tinSup) {
		this.tinSup = tinSup;
	}

	public String getBranchCodeSup() {
		return branchCodeSup;
	}

	public void setBranchCodeSup(String branchCodeSup) {
		this.branchCodeSup = branchCodeSup;
	}

	public String getRegisteredNameSup() {
		return registeredNameSup;
	}

	public void setRegisteredNameSup(String registeredNameSup) {
		this.registeredNameSup = registeredNameSup;
	}

	public String getLastNameSup() {
		return lastNameSup;
	}

	public void setLastNameSup(String lastNameSup) {
		this.lastNameSup = lastNameSup;
	}

	public String getFirstNameSup() {
		return firstNameSup;
	}

	public void setFirstNameSup(String firstNameSup) {
		this.firstNameSup = firstNameSup;
	}

	public String getMiddleNameSup() {
		return middleNameSup;
	}

	public void setMiddleNameSup(String middleNameSup) {
		this.middleNameSup = middleNameSup;
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

	public Double getGrossPurchasesAmt() {
		return grossPurchasesAmt;
	}

	public void setGrossPurchasesAmt(Double grossPurchasesAmt) {
		this.grossPurchasesAmt = grossPurchasesAmt;
	}

	public Double getExemptPurchases() {
		return exemptPurchases;
	}

	public void setExemptPurchases(Double exemptPurchases) {
		this.exemptPurchases = exemptPurchases;
	}

	public Double getZeroRatedPurchases() {
		return zeroRatedPurchases;
	}

	public void setZeroRatedPurchases(Double zeroRatedPurchases) {
		this.zeroRatedPurchases = zeroRatedPurchases;
	}

	public Double getTaxablePurchases() {
		return taxablePurchases;
	}

	public void setTaxablePurchases(Double taxablePurchases) {
		this.taxablePurchases = taxablePurchases;
	}

	public Double getPurchasesServicesAmt() {
		return purchasesServicesAmt;
	}

	public void setPurchasesServicesAmt(Double purchasesServicesAmt) {
		this.purchasesServicesAmt = purchasesServicesAmt;
	}

	public Double getPurchasesCapitalGoodsAmt() {
		return purchasesCapitalGoodsAmt;
	}

	public void setPurchasesCapitalGoodsAmt(Double purchasesCapitalGoodsAmt) {
		this.purchasesCapitalGoodsAmt = purchasesCapitalGoodsAmt;
	}

	public Double getPurchasesGoodsOtherThanCapitalGoodsAmt() {
		return purchasesGoodsOtherThanCapitalGoodsAmt;
	}

	public void setPurchasesGoodsOtherThanCapitalGoodsAmt(Double purchasesGoodsOtherThanCapitalGoodsAmt) {
		this.purchasesGoodsOtherThanCapitalGoodsAmt = purchasesGoodsOtherThanCapitalGoodsAmt;
	}

	public Double getInputTaxAmt() {
		return inputTaxAmt;
	}

	public void setInputTaxAmt(Double inputTaxAmt) {
		this.inputTaxAmt = inputTaxAmt;
	}

	public Double getGrossTaxablePurchasesAmt() {
		return grossTaxablePurchasesAmt;
	}

	public void setGrossTaxablePurchasesAmt(Double grossTaxablePurchasesAmt) {
		this.grossTaxablePurchasesAmt = grossTaxablePurchasesAmt;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getOwnersTin() {
		return ownersTin;
	}

	public void setOwnersTin(String ownersTin) {
		this.ownersTin = ownersTin;
	}

	public String getRegisteredAddressSup() {
		return registeredAddressSup;
	}

	public void setRegisteredAddressSup(String registeredAddressSup) {
		this.registeredAddressSup = registeredAddressSup;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SummaryLSPPurchasesDto [summaryType=").append(summaryType).append(", ftypeCode=")
				.append(ftypeCode).append(", seqNum=").append(seqNum).append(", taxableMonth=").append(taxableMonth)
				.append(", tinSup=").append(tinSup).append(", branchCodeSup=").append(branchCodeSup)
				.append(", registeredNameSup=").append(registeredNameSup).append(", lastNameSup=").append(lastNameSup)
				.append(", firstNameSup=").append(firstNameSup).append(", middleNameSup=").append(middleNameSup)
				.append(", streetBrgy=").append(streetBrgy).append(", cityProvince=").append(cityProvince)
				.append(", grossPurchasesAmt=").append(grossPurchasesAmt).append(", exemptPurchases=")
				.append(exemptPurchases).append(", zeroRatedPurchases=").append(zeroRatedPurchases)
				.append(", taxablePurchases=").append(taxablePurchases).append(", purchasesServicesAmt=")
				.append(purchasesServicesAmt).append(", purchasesCapitalGoodsAmt=").append(purchasesCapitalGoodsAmt)
				.append(", purchasesGoodsOtherThanCapitalGoodsAmt=").append(purchasesGoodsOtherThanCapitalGoodsAmt)
				.append(", inputTaxAmt=").append(inputTaxAmt).append(", grossTaxablePurchasesAmt=")
				.append(grossTaxablePurchasesAmt).append(", supplierId=").append(supplierId).append("]");
		return builder.toString();
	}
}
