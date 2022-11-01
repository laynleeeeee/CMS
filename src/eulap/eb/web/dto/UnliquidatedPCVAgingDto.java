package eulap.eb.web.dto;

import java.util.Date;

/**
 * A class that handles the Unliquidated PCV aging report data.

 */
public class UnliquidatedPCVAgingDto {
	private String division;
	private Date pcvDate;
	private Integer pcvNo;
	private String custodian;
	private String requestor;
	private double rqstdAmount;
	private double liquidatedAmount;
	private Date liquidatedDate;
	private double balance;
	private double range1To3;
	private double range4To6;
	private double range7To10;
	private double range11ToUp;

	public String getCustodian() {
		return custodian;
	}

	public void setCustodian(String custodian) {
		this.custodian = custodian;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public double getLiquidatedAmount() {
		return liquidatedAmount;
	}

	public void setLiquidatedAmount(double liquidatedAmount) {
		this.liquidatedAmount = liquidatedAmount;
	}

	public Date getLiquidatedDate() {
		return liquidatedDate;
	}

	public void setLiquidatedDate(Date liquidatedDate) {
		this.liquidatedDate = liquidatedDate;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Date getPcvDate() {
		return pcvDate;
	}

	public void setPcvDate(Date pcvDate) {
		this.pcvDate = pcvDate;
	}

	public Integer getPcvNo() {
		return pcvNo;
	}

	public void setPcvNo(Integer pcvNo) {
		this.pcvNo = pcvNo;
	}

	public double getRqstdAmount() {
		return rqstdAmount;
	}

	public void setRqstdAmount(double rqstdAmount) {
		this.rqstdAmount = rqstdAmount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getRange1To3() {
		return range1To3;
	}

	public void setRange1To3(double range1To3) {
		this.range1To3 = range1To3;
	}

	public double getRange4To6() {
		return range4To6;
	}

	public void setRange4To6(double range4To6) {
		this.range4To6 = range4To6;
	}

	public double getRange7To10() {
		return range7To10;
	}

	public void setRange7To10(double range7To10) {
		this.range7To10 = range7To10;
	}

	public double getRange11ToUp() {
		return range11ToUp;
	}

	public void setRange11ToUp(double range11ToUp) {
		this.range11ToUp = range11ToUp;
	}

	@Override
	public String toString() {
		return "UnliquidatedPCVAgingDto [division=" + division + ", pcvDate=" + pcvDate + ", pcvNo=" + pcvNo
				+ ", custodian=" + custodian + ", requestor=" + requestor + ", rqstdAmount=" + rqstdAmount
				+ ", liquidatedAmount=" + liquidatedAmount + ", liquidatedDate=" + liquidatedDate + ", balance="
				+ balance + ", range1To3=" + range1To3 + ", range4To6=" + range4To6 + ", range7To10=" + range7To10
				+ ", range11ToUp=" + range11ToUp + "]";
	}
}
