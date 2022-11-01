package eulap.eb.web.processing.dto;

import java.util.List;

import eulap.eb.domain.hibernate.PrByProduct;
import eulap.eb.domain.hibernate.PrMainProduct;
import eulap.eb.domain.hibernate.PrOtherCharge;
import eulap.eb.domain.hibernate.PrRawMaterialsItem;

/**
 * Processing report printout data transfer object.

 *
 */
public class ProcessingReportPrintout {
	private List<Recovery> recoveries;
	private List<MillingRecoveryReport> millingRecoveryReports;
	private List<PrMainProduct> prMainProducts;
	private List<PrByProduct> prByProducts;
	private List<PrRawMaterialsItem> prRawMaterials;
	private List<PrOtherCharge> otherFees;

	public static ProcessingReportPrintout getInstanceOf (List<Recovery> recoveries, 
			List<MillingRecoveryReport> millingRecoveryReports, 
			List<PrMainProduct> prMainProducts, List<PrByProduct> prByProducts) {
		ProcessingReportPrintout prp = new ProcessingReportPrintout();
		prp.recoveries = recoveries;
		prp.millingRecoveryReports = millingRecoveryReports;
		prp.prMainProducts = prMainProducts;
		prp.prByProducts = prByProducts;
		return prp;
	}

	public List<Recovery> getRecoveries() {
		return recoveries;
	}

	public void setRecoveries(List<Recovery> recoveries) {
		this.recoveries = recoveries;
	}

	public List<MillingRecoveryReport> getMillingRecoveryReports() {
		return millingRecoveryReports;
	}

	public void setMillingRecoveryReports(
			List<MillingRecoveryReport> millingRecoveryReports) {
		this.millingRecoveryReports = millingRecoveryReports;
	}

	public List<PrMainProduct> getPrMainProducts() {
		return prMainProducts;
	}

	public void setPrMainProducts(List<PrMainProduct> prMainProducts) {
		this.prMainProducts = prMainProducts;
	}

	public List<PrByProduct> getPrByProducts() {
		return prByProducts;
	}

	public void setPrByProducts(List<PrByProduct> prByProducts) {
		this.prByProducts = prByProducts;
	}

	public List<PrRawMaterialsItem> getPrRawMaterials() {
		return prRawMaterials;
	}

	public void setPrRawMaterials(List<PrRawMaterialsItem> prRawMaterials) {
		this.prRawMaterials = prRawMaterials;
	}

	public List<PrOtherCharge> getOtherFees() {
		return otherFees;
	}

	public void setOtherFees(List<PrOtherCharge> otherFees) {
		this.otherFees = otherFees;
	}

}
