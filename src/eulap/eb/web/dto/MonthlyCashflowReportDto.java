package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Monthly cash flow report

 *
 */
public class MonthlyCashflowReportDto {
	private List<MCFlowDetail> mcInTotals;
	private List<MCFlowDetail> mcOutTotals;
	private List<MCFlowDetail> mcBankBeg;
	private List<MCFlowDetail> mcWithholdTax;
	private List<MCFlowDetail> mcNet;
	private List<MCFlowDetail> mcBalEnd;
	private List<MCFlowDetail> cashInflows;
	private List<MCFlowDetail> cashOutFlows;
	private List<CashFlowHeader> cashFlowHeaders;

	public MonthlyCashflowReportDto() {
		this.cashInflows = new ArrayList<>();
		this.cashOutFlows = new ArrayList<>();
	}

	public List<MCFlowDetail> getCashInflows() {
		return cashInflows;
	}

	public void setCashInflows(List<MCFlowDetail> cashInflows) {
		this.cashInflows = cashInflows;
	}

	public List<MCFlowDetail> getCashOutFlows() {
		return cashOutFlows;
	}

	public void setCashOutFlows(List<MCFlowDetail> cashOutFlows) {
		this.cashOutFlows = cashOutFlows;
	}

	public List<MCFlowDetail> getMcInTotals() {
		return mcInTotals;
	}

	public void setMcInTotals(List<MCFlowDetail> mcInTotals) {
		this.mcInTotals = mcInTotals;
	}

	public List<MCFlowDetail> getMcOutTotals() {
		return mcOutTotals;
	}

	public void setMcOutTotals(List<MCFlowDetail> mcOutTotals) {
		this.mcOutTotals = mcOutTotals;
	}

	public List<MCFlowDetail> getMcBankBeg() {
		return mcBankBeg;
	}

	public void setMcBankBeg(List<MCFlowDetail> mcBankBeg) {
		this.mcBankBeg = mcBankBeg;
	}

	public List<MCFlowDetail> getMcWithholdTax() {
		return mcWithholdTax;
	}

	public void setMcWithholdTax(List<MCFlowDetail> mcWithholdTax) {
		this.mcWithholdTax = mcWithholdTax;
	}

	public List<MCFlowDetail> getMcNet() {
		return mcNet;
	}

	public void setMcNet(List<MCFlowDetail> mcNet) {
		this.mcNet = mcNet;
	}

	public List<MCFlowDetail> getMcBalEnd() {
		return mcBalEnd;
	}

	public void setMcBalEnd(List<MCFlowDetail> mcBalEnd) {
		this.mcBalEnd = mcBalEnd;
	}

	public List<CashFlowHeader> getCashFlowHeaders() {
		return cashFlowHeaders;
	}

	public void setCashFlowHeaders(List<CashFlowHeader> cashFlowHeaders) {
		this.cashFlowHeaders = cashFlowHeaders;
	}

	@Override
	public String toString() {
		return "MonthlyCashflowReportDto [cashInflows=" + cashInflows + ", cashOutFlows=" + cashOutFlows + "]";
	}
}
