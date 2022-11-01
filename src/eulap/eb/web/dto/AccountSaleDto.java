package eulap.eb.web.dto;

import java.util.List;

import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.ArLine;

/**
 * Account Sale Dto

 */
public class AccountSaleDto {
	private Integer accountSaleId;
	private String accountSaleNo;
	private double amount;
	private List<AccountSaleItem> saleItems;
	private List<ArLine> arLines;

	private AccountSaleDto (Integer accountSaleId, String accountSaleNo, double amount,
			List<AccountSaleItem> accountSaleItems, List<ArLine> arLines) {
		this.accountSaleId = accountSaleId;
		this.accountSaleNo = accountSaleNo;
		this.amount = amount;
		this.saleItems = accountSaleItems;
		this.arLines = arLines;
	}

	public static AccountSaleDto getInstanceOf(List<AccountSaleItem> accountSaleItems, List<ArLine> arLines) {
		return new AccountSaleDto(null, null, 0, accountSaleItems, arLines);
	}

	public Integer getAccountSaleId() {
		return accountSaleId;
	}

	public void setAccountSaleId(Integer accountSaleId) {
		this.accountSaleId = accountSaleId;
	}

	public String getAccountSaleNo() {
		return accountSaleNo;
	}

	public void setAccountSaleNo(String accountSaleNo) {
		this.accountSaleNo = accountSaleNo;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<AccountSaleItem> getSaleItems() {
		return saleItems;
	}

	public void setSaleItems(List<AccountSaleItem> saleItems) {
		this.saleItems = saleItems;
	}

	public List<ArLine> getArLines() {
		return arLines;
	}

	public void setArLines(List<ArLine> arLines) {
		this.arLines = arLines;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountSaleDto [accountSaleId=").append(accountSaleId).append(", accountSaleNo=")
				.append(accountSaleNo).append(", amount=").append(amount).append(", saleItems=").append(saleItems)
				.append(", arLines=").append(arLines)
				.append("]");
		return builder.toString();
	}
}
