package eulap.eb.web.dto;



/**
 * A class that handles the daily petty cash fund report data.

 */

public class DailyPettyCashFundReportDto {
	private String division;
	private String custodianName;
	private Integer pcvNumber;
	private String requestor;
	private String reference;
	private String description;
	private double amount;
	private String status;
	private double totalLiquidation;

		public String getDivision() {
			return division;
		}

		public void setDivision(String division) {
			this.division = division;
		}


		public String getCustodianName() {
			return custodianName;
		}

		public void setCustodianName(String custodianName) {
			this.custodianName = custodianName;
		}


		public Integer getPcvNumber() {
			return pcvNumber;
		}

		public void setPcvNumber(Integer pcvNumber) {
			this.pcvNumber = pcvNumber;
		}

		public String getRequestor() {
			return requestor;
		}

		public void setRequestor(String requestor) {
			this.requestor = requestor;
		}


		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}


		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}


		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public double getTotalLiquidation() {
			return totalLiquidation;
		}

		public void setTotalLiquidation(double totalLiquidation) {
			this.totalLiquidation = totalLiquidation;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ArTransactionRegisterDto [division=").append(division).append(", custodianName=").append(custodianName)
			.append(", pcvNumber=").append(pcvNumber).append(", requestor=").append(requestor)
			.append(", reference=").append(reference).append(", description=").append(description)
			.append(", amount=").append(amount).append(", status=").append(status).append(", totalLiquidation=").append(totalLiquidation).append("]");
			return builder.toString();
		}
}