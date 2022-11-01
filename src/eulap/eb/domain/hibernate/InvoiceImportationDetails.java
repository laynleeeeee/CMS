package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of INVOICE_IMPORTATION_DETAILS Table.

 *
 */
@Entity
@Table(name = "INVOICE_IMPORTATION_DETAILS")
public class InvoiceImportationDetails extends BaseDomain{
	private Integer apInvoiceId;
	private String importEntryNo;
	private Date assessmentReleaseDate;
	private String registeredName;
	private Date importationDate;
	private String countryOfOrigin;
	private Double totalLandedCost;
	private Double dutiableValue;
	private Double chargesFromCustom;
	private Double taxableImport;
	private Double exemptImport;
	private String orNumber;
	private Date paymentDate;

	/**
	 *  Maximum character for importation detail fields is 100.
	 */
	public static final int MAX_FIELD_CHARACTERS = 100;

	public enum FIELD{
		id, apInvoiceId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column(name="INVOICE_IMPORTATION_DETAILS_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AP_INVOICE_ID")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}
	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	@Column(name = "IMPORT_ENTRY_NO")
	public String getImportEntryNo() {
		return importEntryNo;
	}
	public void setImportEntryNo(String importEntryNo) {
		this.importEntryNo = importEntryNo;
	}

	@Column(name = "ASSESSMENT_RELEASE_DATE")
	public Date getAssessmentReleaseDate() {
		return assessmentReleaseDate;
	}
	public void setAssessmentReleaseDate(Date assessmentReleaseDate) {
		this.assessmentReleaseDate = assessmentReleaseDate;
	}

	@Column(name = "REGISTERED_NAME")
	public String getRegisteredName() {
		return registeredName;
	}
	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}

	@Column(name = "IMPORTATION_DATE")
	public Date getImportationDate() {
		return importationDate;
	}
	public void setImportationDate(Date importationDate) {
		this.importationDate = importationDate;
	}

	@Column(name = "COUNTRY_OF_ORIGIN")
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	@Column(name = "TOTAL_LANDED_COST")
	public Double getTotalLandedCost() {
		return totalLandedCost;
	}
	public void setTotalLandedCost(Double totalLandedCost) {
		this.totalLandedCost = totalLandedCost;
	}

	@Column(name = "DUTIABLE_VALUE")
	public Double getDutiableValue() {
		return dutiableValue;
	}
	public void setDutiableValue(Double dutiableValue) {
		this.dutiableValue = dutiableValue;
	}

	@Column(name = "CHARGES_FROM_CUSTOM")
	public Double getChargesFromCustom() {
		return chargesFromCustom;
	}
	public void setChargesFromCustom(Double chargesFromCustom) {
		this.chargesFromCustom = chargesFromCustom;
	}

	@Column(name = "TAXABLE_IMPORT")
	public Double getTaxableImport() {
		return taxableImport;
	}
	public void setTaxableImport(Double taxableImport) {
		this.taxableImport = taxableImport;
	}

	@Column(name = "EXEMPT_IMPORT")
	public Double getExemptImport() {
		return exemptImport;
	}
	public void setExemptImport(Double exemptImport) {
		this.exemptImport = exemptImport;
	}

	@Column(name = "OR_NUMBER")
	public String getOrNumber() {
		return orNumber;
	}
	public void setOrNumber(String orNumber) {
		this.orNumber = orNumber;
	}

	@Column(name = "PAYMENT_DATE")
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Override
	public String toString() {
		return "InvoiceImportationDetails [apInvoiceId=" + apInvoiceId + ", importEntryNo=" + importEntryNo
				+ ", assessmentReleaseDate=" + assessmentReleaseDate + ", registeredName=" + registeredName
				+ ", importationDate=" + importationDate + ", countryOfOrigin=" + countryOfOrigin + ", totalLandedCost="
				+ totalLandedCost + ", dutiableValue=" + dutiableValue + ", chargesFromCustom=" + chargesFromCustom
				+ ", taxableImport=" + taxableImport + ", exemptImport=" + exemptImport + ", orNumber=" + orNumber
				+ ", paymentDate=" + paymentDate + "]";
	}
}
