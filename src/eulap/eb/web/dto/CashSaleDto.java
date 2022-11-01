package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.CashSaleArLine;
import eulap.eb.domain.hibernate.CashSaleItem;

/**
 * Container class that will hold the values for Cash Sales.

 * 
 */
public class CashSaleDto {
	private List<CashSaleItem> saleItems;
	private List<CashSaleArLine> arLines;

	private CashSaleDto (List<CashSaleItem> cashSaleItems, List<CashSaleArLine> arLines) {
		this.saleItems = cashSaleItems;
		this.arLines = arLines;
	}

	public static CashSaleDto getInstanceOf(List<CashSaleItem> cashSaleItems, List<CashSaleArLine> arLines) {
		return new CashSaleDto(cashSaleItems, arLines);
	}

	public List<CashSaleItem> getSaleItems() {
		return saleItems;
	}

	public void setSaleItems(List<CashSaleItem> saleItems) {
		this.saleItems = saleItems;
	}

	public List<CashSaleArLine> getArLines() {
		return arLines;
	}

	public void setArLines(List<CashSaleArLine> arLines) {
		this.arLines = arLines;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CashSaleDto [saleItems=").append(saleItems)
				.append(", arLines=").append(arLines)
				.append("]");
		return builder.toString();
	}
}
