package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the journal voucher register report data.

 */
public class JournalVoucherRegisterDto {
	private int sequenceNumber;
	private String jvNumber;
	private Date glDate;
	private double amount;
	private String description;
	private String status;
	private String cancellationRemarks;

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getJvNumber() {
		return jvNumber;
	}

	public void setJvNumber(String jvNumber) {
		this.jvNumber = jvNumber;
	}

	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JournalVoucherRegisterDto [sequenceNumber=");
		builder.append(sequenceNumber);
		builder.append(", jvNumber=");
		builder.append(jvNumber);
		builder.append(", glDate=");
		builder.append(glDate);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", description=");
		builder.append(description);
		builder.append(", status=");
		builder.append(status);
		builder.append(", cancellationRemarks=");
		builder.append(cancellationRemarks);
		builder.append("]");
		return builder.toString();
	}
}
