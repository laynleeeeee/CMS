package eulap.eb.web.dto;

public class QrtrlyValAddedTaxDeclrtnDetailsDto {
	private String vatType;
	private String entries;
	private Double taxBase;
	private Double vat;

	public String getEntries() {
		return entries;
	}

	public void setEntries(String entries) {
		this.entries = entries;
	}

	public String getVatType() {
		return vatType;
	}

	public void setVatType(String vatType) {
		this.vatType = vatType;
	}

	public Double getTaxBase() {
		return taxBase;
	}

	public void setTaxBase(Double taxBase) {
		this.taxBase = taxBase;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	@Override
	public String toString() {
		return "QrtrlyValAddedTaxDeclrtnDetailsDto [vatType=" + vatType + ", entries=" + entries + ", taxBase="
				+ taxBase + ", vat=" + vat + "]";
	}
}
