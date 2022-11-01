package eulap.eb.web.dto;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Check Printing Dto

 */
public class CheckPrintingDto {
	private String name;
	private String nameSecondLine;
	private String amountInWords;
	private String amountInWordsSecondLine;
	private BigDecimal amount;
	private Date date;

	public static CheckPrintingDto getInstance (String name, String amountInWords,
			String amountInWordsSecondLine, Double amount, Date date, String nameSecondLine) {
		CheckPrintingDto printingDto = new CheckPrintingDto();
		printingDto.name = name;
		printingDto.amountInWords = amountInWords;
		printingDto.amount = new BigDecimal(amount+"");
		printingDto.amountInWordsSecondLine = amountInWordsSecondLine;
		printingDto.nameSecondLine = nameSecondLine;
		printingDto.date = date;
		return printingDto;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmountInWords() {
		return amountInWords;
	}

	public void setAmountInWords(String amountInWords) {
		this.amountInWords = amountInWords;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CheckPrintingDto [name=").append(name).append(", amountInWords=").append(amountInWords)
				.append(", amount=").append(amount).append(", date=").append(date).append("]");
		return builder.toString();
	}

	public String getAmountInWordsSecondLine() {
		return amountInWordsSecondLine;
	}

	public void setAmountInWordsSecondLine(String amountInWordsSecondLine) {
		this.amountInWordsSecondLine = amountInWordsSecondLine;
	}

	public String getNameSecondLine() {
		return nameSecondLine;
	}

	public void setNameSecondLine(String nameSecondLine) {
		this.nameSecondLine = nameSecondLine;
	}
}
