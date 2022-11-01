package eulap.eb.web.dto;

import java.util.List;

/**
 * Container for the values needed to generate the Bank Reconciliation report.

 * 
 */
public class BankRecon {
	private List<BankReconItem> ditReconItems;
	private List<BankReconItem> ocReconItems;
	private double totalDITAmount;
	private double totalOCAmount;

	public static BankRecon getInstanceOf(List<BankReconItem> ditReconItems, List<BankReconItem> ocReconItems,
			double totalDITAmount, double totalOCAmount) {
		BankRecon br = new BankRecon();
		br.ditReconItems = ditReconItems;
		br.ocReconItems = ocReconItems;
		br.totalDITAmount = totalDITAmount;
		br.totalOCAmount = totalOCAmount;
		return br;
	}

	public List<BankReconItem> getDitReconItems() {
		return ditReconItems;
	}

	public void setDitReconItems(List<BankReconItem> ditReconItems) {
		this.ditReconItems = ditReconItems;
	}

	public List<BankReconItem> getOcReconItems() {
		return ocReconItems;
	}

	public void setOcReconItems(List<BankReconItem> ocReconItems) {
		this.ocReconItems = ocReconItems;
	}

	public double getTotalDITAmount() {
		return totalDITAmount;
	}

	public void setTotalDITAmount(double totalDITAmount) {
		this.totalDITAmount = totalDITAmount;
	}

	public double getTotalOCAmount() {
		return totalOCAmount;
	}

	public void setTotalOCAmount(double totalOCAmount) {
		this.totalOCAmount = totalOCAmount;
	}
}
