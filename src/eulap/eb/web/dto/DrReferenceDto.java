package eulap.eb.web.dto;

import java.util.List;

/**
 * Data transfer object class for DR-AR invoice referencing

 */

public class DrReferenceDto {
	private String arInvoiceNumber;
	private List<Integer> drReferenceIds;

	public String getArInvoiceNumber() {
		return arInvoiceNumber;
	}

	public void setArInvoiceNumber(String arInvoiceNumber) {
		this.arInvoiceNumber = arInvoiceNumber;
	}

	public List<Integer> getDrReferenceIds() {
		return drReferenceIds;
	}

	public void setDrReferenceIds(List<Integer> drReferenceIds) {
		this.drReferenceIds = drReferenceIds;
	}
}
