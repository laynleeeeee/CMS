package eulap.eb.web.dto;

import java.util.List;

/**
 * Data transfer class for AP invoice form printout

 */

public class InvoicePrintoutDto {
	private List<ApLineDto> apLineDtos;
	private List<COCTaxDto> cocTaxDtos;
	private List<ReceivingReportDto> receivingReportDtos;
	private List<ApInvoiceItemDto> apInvoiceItemDtos;

	public List<ApLineDto> getApLineDtos() {
		return apLineDtos;
	}

	public void setApLineDtos(List<ApLineDto> apLineDtos) {
		this.apLineDtos = apLineDtos;
	}

	public List<COCTaxDto> getCocTaxDtos() {
		return cocTaxDtos;
	}

	public void setCocTaxDtos(List<COCTaxDto> cocTaxDtos) {
		this.cocTaxDtos = cocTaxDtos;
	}

	public List<ReceivingReportDto> getReceivingReportDtos() {
		return receivingReportDtos;
	}

	public void setReceivingReportDtos(List<ReceivingReportDto> receivingReportDtos) {
		this.receivingReportDtos = receivingReportDtos;
	}

	public List<ApInvoiceItemDto> getApInvoiceItemDtos() {
		return apInvoiceItemDtos;
	}

	public void setApInvoiceItemDtos(List<ApInvoiceItemDto> apInvoiceItemDtos) {
		this.apInvoiceItemDtos = apInvoiceItemDtos;
	}
}
