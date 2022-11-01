package eulap.eb.web.dto;

import java.util.List;

/**
 * A class that handles the Value Added Tax Summary Data.

 */

public class ValueAddedTaxSummaryDto {
	private String subTitle;
	private List<VATDeclarationDto> outputVATDeclarations;
	private List<VATDeclarationDto> inputVATDeclarations;

	public String getsubTitle() {
		return subTitle;
	}

	public void setsubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public List<VATDeclarationDto> getOutputVATDeclarations() {
		return outputVATDeclarations;
	}

	public void setOutputVATDeclarations(List<VATDeclarationDto> outputVATDeclarations) {
		this.outputVATDeclarations = outputVATDeclarations;
	}

	public List<VATDeclarationDto> getInputVATDeclarations() {
		return inputVATDeclarations;
	}

	public void setInputVATDeclarations(List<VATDeclarationDto> inputVATDeclarations) {
		this.inputVATDeclarations = inputVATDeclarations;
	}

	@Override
	public String toString() {
		return "ValueAddedTaxSummaryDto [subTitle=" + subTitle + ", outputVATDeclarations=" + outputVATDeclarations
				+ ", inputVATDeclarations=" + inputVATDeclarations + "]";
	}
}
