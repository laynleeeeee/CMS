package eulap.eb.bitpos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Container class that will hold the values for Cash Sales.

 *
 */
public class BCashSaleDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private Date receiptDate;
	private Date maturityDate;
	private Double cash;
	private int orNumber;
	private boolean cancelled;
	private String username;
	private String usernameUpdate;
	private List<BCashSaleItem> bCashSaleItems;
	private List<BCashSaleArLine> bCashSaleArLines;

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public int getOrNumber() {
		return orNumber;
	}

	public void setOrNumber(int orNumber) {
		this.orNumber = orNumber;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsernameUpdate() {
		return usernameUpdate;
	}

	public void setUsernameUpdate(String usernameUpdate) {
		this.usernameUpdate = usernameUpdate;
	}

	public List<BCashSaleItem> getbCashSaleItems() {
		return bCashSaleItems;
	}

	public void setbCashSaleItems(List<BCashSaleItem> bCashSaleItems) {
		this.bCashSaleItems = bCashSaleItems;
	}

	public List<BCashSaleArLine> getbCashSaleArLines() {
		return bCashSaleArLines;
	}

	public void setbCashSaleArLines(List<BCashSaleArLine> bCashSaleArLines) {
		this.bCashSaleArLines = bCashSaleArLines;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BCashSaleDto [receiptDate=").append(receiptDate)
				.append(", maturityDate=").append(maturityDate)
				.append(", cash=").append(cash).append(", orNumber=")
				.append(orNumber).append(", cancelled=").append(cancelled)
				.append(", username=").append(username)
				.append(", usernameUpdate=").append(usernameUpdate)
				.append(", bCashSaleItems=").append(bCashSaleItems)
				.append(", bCashSaleArLines=").append(bCashSaleArLines)
				.append("]");
		return builder.toString();
	}
}
