package eulap.eb.web.dto;

/**
 * A class that handles the VAT Declaration data.

 */
public class VATDeclarationDto {
	private String name;
	private Double taxBase;
	private Double vat;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "VATDeclarationDto [name=" + name + ", taxBase=" + taxBase + ", vat=" + vat + "]";
	}

}
