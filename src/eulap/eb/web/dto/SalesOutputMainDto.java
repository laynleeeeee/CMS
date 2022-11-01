package eulap.eb.web.dto;

import java.util.List;

/**
 * The sales output report main DTO.

 */
public class SalesOutputMainDto {
	private List<SalesPoMonitoringDto> salesPoMonitoringDtos;
	private List<InvoicePerRepresentativeDto> invoicePerRepresentativeDtos;
	private List<SalesOutputDto> salesOutputDtos;
	private Integer noOfMonths;

	public List<SalesPoMonitoringDto> getSalesPoMonitoringDtos() {
		return salesPoMonitoringDtos;
	}

	public void setSalesPoMonitoringDtos(List<SalesPoMonitoringDto> salesPoMonitoringDtos) {
		this.salesPoMonitoringDtos = salesPoMonitoringDtos;
	}

	public List<InvoicePerRepresentativeDto> getInvoicePerRepresentativeDtos() {
		return invoicePerRepresentativeDtos;
	}

	public void setInvoicePerRepresentativeDtos(List<InvoicePerRepresentativeDto> invoicePerRepresentativeDtos) {
		this.invoicePerRepresentativeDtos = invoicePerRepresentativeDtos;
	}

	public List<SalesOutputDto> getSalesOutputDtos() {
		return salesOutputDtos;
	}

	public void setSalesOutputDtos(List<SalesOutputDto> salesOutputDtos) {
		this.salesOutputDtos = salesOutputDtos;
	}

	public Integer getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(Integer noOfMonths) {
		this.noOfMonths = noOfMonths;
	}
}
