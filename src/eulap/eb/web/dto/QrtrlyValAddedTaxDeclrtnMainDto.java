package eulap.eb.web.dto;

public class QrtrlyValAddedTaxDeclrtnMainDto {
	private String vatType;
	private String entries;

	public String getVatType() {
		return vatType;
	}

	public void setVatType(String vatType) {
		this.vatType = vatType;
	}

	public String getEntries() {
		return entries;
	}

	public void setEntries(String entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "QrtrlyValAddedTaxDeclrtnMainDto [vatType=" + vatType + ", entries=" + entries + "]";
	}
}
