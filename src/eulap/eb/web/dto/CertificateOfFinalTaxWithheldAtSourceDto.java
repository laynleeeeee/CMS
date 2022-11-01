package eulap.eb.web.dto;

import java.util.List;

/**
 * Certificate of final tax withheld at source dto

 */
public class CertificateOfFinalTaxWithheldAtSourceDto {
	List<CertFinalTaxWithheldMonthlyDto> cftwsMonthly;
	List<CFTWSDto> cftwsSummary;

	public List<CertFinalTaxWithheldMonthlyDto> getCftwsMonthly() {
		return cftwsMonthly;
	}

	public void setCftwsMonthly(List<CertFinalTaxWithheldMonthlyDto> cftwsMonthly) {
		this.cftwsMonthly = cftwsMonthly;
	}

	public List<CFTWSDto> getCftwsSummary() {
		return cftwsSummary;
	}

	public void setCftwsSummary(List<CFTWSDto> cftwsSummary) {
		this.cftwsSummary = cftwsSummary;
	}

	@Override
	public String toString() {
		return "CertificateOfFinalTaxWithheldAtSourceDto [cftwsMonthly=" + cftwsMonthly + ", cftwsSummary="
				+ cftwsSummary + "]";
	}

}
