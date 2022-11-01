package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.AuthorityToWithdrawItem;
import eulap.eb.domain.hibernate.AuthorityToWithdrawLine;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.SerialItem;

/**
 * Data transfer object class for conversion of SO to ATW object

 */

public class SoToAtwDto {
	private SalesOrder salesOrder;
	private List<AuthorityToWithdrawItem> atwItems;
	private List<SerialItem> serialItems;
	private List<AuthorityToWithdrawLine> atwLines;

	public SalesOrder getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(SalesOrder salesOrder) {
		this.salesOrder = salesOrder;
	}

	public List<AuthorityToWithdrawItem> getAtwItems() {
		return atwItems;
	}

	public void setAtwItems(List<AuthorityToWithdrawItem> atwItems) {
		this.atwItems = atwItems;
	}

	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	public List<AuthorityToWithdrawLine> getAtwLines() {
		return atwLines;
	}

	public void setAtwLines(List<AuthorityToWithdrawLine> atwLines) {
		this.atwLines = atwLines;
	}
}
