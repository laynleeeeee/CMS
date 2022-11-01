package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.RReceivingReportItem;
import eulap.eb.domain.hibernate.RriBagDiscount;
import eulap.eb.domain.hibernate.RriBagQuantity;

/**
 * Receiving report - raw materials DTO

 */
public class RrRawMaterialsDto {
	private Integer apInvoiceId;
	private List<RReceivingReportItem> rrItems;
	private List<ApInvoiceLine> apInvoiceLines;
	private List<RriBagQuantity> rriBagQuantities;
	private List<RriBagDiscount> rriBagDiscounts;

	private RrRawMaterialsDto (Integer apInvoiceId, List<RReceivingReportItem> rrItems,
		List<ApInvoiceLine> apInvoiceLines, List<RriBagQuantity> rriBagQuantities, List<RriBagDiscount> rriBagDiscounts) {
		this.apInvoiceId = apInvoiceId;
		this.rrItems = rrItems;
		this.apInvoiceLines = apInvoiceLines;
		this.rriBagQuantities = rriBagQuantities;
		this.rriBagDiscounts = rriBagDiscounts;
	}

	public static RrRawMaterialsDto getInstanceOf(APInvoice apInvoice) {
		return new RrRawMaterialsDto(apInvoice.getId(), apInvoice.getRrItems(), apInvoice.getApInvoiceLines(),
				apInvoice.getRriBagQuantities(), apInvoice.getRriBagDiscounts());
	}

	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	public List<RReceivingReportItem> getRrItems() {
		return rrItems;
	}

	public void setRrItems(List<RReceivingReportItem> rrItems) {
		this.rrItems = rrItems;
	}

	public List<ApInvoiceLine> getApInvoiceLines() {
		return apInvoiceLines;
	}

	public void setApInvoiceLines(List<ApInvoiceLine> apInvoiceLines) {
		this.apInvoiceLines = apInvoiceLines;
	}

	public List<RriBagQuantity> getRriBagQuantities() {
		return rriBagQuantities;
	}

	public void setRriBagQuantities(List<RriBagQuantity> rriBagQuantities) {
		this.rriBagQuantities = rriBagQuantities;
	}

	public List<RriBagDiscount> getRriBagDiscounts() {
		return rriBagDiscounts;
	}

	public void setRriBagDiscounts(List<RriBagDiscount> rriBagDiscounts) {
		this.rriBagDiscounts = rriBagDiscounts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RrRawMaterialsDto [apInvoiceId=").append(apInvoiceId).append(", rrItems=").append(rrItems)
				.append(", apInvoiceLines=").append(apInvoiceLines).append(", rriBagQuantities=")
				.append(rriBagQuantities).append(", rriBagDiscounts=").append(rriBagDiscounts).append("]");
		return builder.toString();
	}

}