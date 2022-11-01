package eulap.eb.service.report;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.web.dto.CashFlowHeader;
import eulap.eb.web.dto.MCFlowAccountDateDto;
import eulap.eb.web.dto.MCFlowDetail;
import eulap.eb.web.dto.MCFlowSubDetailDto;
import eulap.eb.web.dto.MonthlyCashflowReportDto;

/**
 * Service class that will handle business logic for monthly cashflow report generation

 *
 */
@Service
public class MonthlyCashflowRprtService {
	@Autowired
	private ApPaymentDao apPaymentDao;

	private static final String CASH_OUT_LABEL = "CASH OUT";
	private static final String NET_CASH_FLOW_LABEL = "NET CASH FLOW";
	// private static final String WITHHOLDING_TAX = "WITHHOLDING TAX";
	private static final String DIRECT_PAYMENT = "DP";
	private static final String AP_PAYMENT = "APP";
	private static final String ACCT_COLLECTION = "AC";
	private static final String OTHER_RECEIPT = "OR";

	/**
	 * Get the monthly cash flow report.
	 * @param companyId The company id.
	 * @param divisionId The division id
	 * @param currentMonth The current month.
	 * @param prevMonths The number of previous month.
	 * @return The monthly cash flow report DTO.
	 */
	public MonthlyCashflowReportDto genMonthlyCashFlow(int companyId, int divisionId, int currentMonth, int prevMonths) {
		MonthlyCashflowReportDto mcfd = new MonthlyCashflowReportDto();
		List<MCFlowDetail> mcOutTotals = new ArrayList<MCFlowDetail>();
		List<MCFlowDetail> mcBankBegs = new ArrayList<MCFlowDetail>();
		// List<MCFlowDetail> mcWithholdTaxes = new ArrayList<MCFlowDetail>();
		List<MCFlowDetail> mcNet = new ArrayList<MCFlowDetail>();
		List<MCFlowDetail> mcBalEnd = new ArrayList<MCFlowDetail>();

		List<MCFlowAccountDateDto> months = generateMonthList(currentMonth, prevMonths+1);
		List<MCFlowDetail> cashInflows = new ArrayList<MCFlowDetail>();
		List<MCFlowDetail> cashOutFlows = new ArrayList<MCFlowDetail>();
		List<MCFlowDetail> mcFlowDetail = null;
		List<CashFlowHeader> cashFlowHeaders = new ArrayList<>();
		CashFlowHeader cashFlowHeader = new CashFlowHeader();
//		cashFlowHeader.setMonth("TOTAL");
//		cashFlowHeaders.add(cashFlowHeader);
		int index = 0;
		int size = months.size();
		List<MCFlowSubDetailDto> subDetailDtos = null;
		MCFlowAccountDateDto nextMDateDto = months.get(1);
		double totaNetAmount = 0;
		for (MCFlowAccountDateDto m : months) {
			int month = m.getMonth();
			int year = m.getYear();
			String monthName = m.getMonthName();
			if (size-1 != index) {
				nextMDateDto = months.get(index+1);
			} else {
				nextMDateDto = null;
			}
			if (index != 0) {
				cashFlowHeader = new CashFlowHeader();
				cashFlowHeader.setMonth(m.getMonthName());
				cashFlowHeaders.add(cashFlowHeader);

				// set empty values
				// mcWithholdTaxes.add(MCFlowDetail.getInstanceOf(0, WITHHOLDING_TAX, month, year, monthName, 0.00, 0));
				mcOutTotals.add(MCFlowDetail.getInstanceOf(0, CASH_OUT_LABEL, month, year, monthName, 0.00, 0));
				mcNet.add(MCFlowDetail.getInstanceOf(0, NET_CASH_FLOW_LABEL, month, year, monthName, 0.00, 0));
			}

			// get current month cash flow details
			subDetailDtos = apPaymentDao.getMcFlowSubDetailDtos(companyId, divisionId, m.getDateFrom(), m.getDateTo());
			for (MCFlowSubDetailDto subDetailDto : subDetailDtos) {
				String sourceLabel = subDetailDto.getSourceLabel();
				if (sourceLabel.equalsIgnoreCase(DIRECT_PAYMENT)) {
					// Out flow - Direct payment.
					if (index != 0) {
						double paidAmount = subDetailDto.getAmount();
						mcFlowDetail = new ArrayList<MCFlowDetail>();
						mcFlowDetail.add(MCFlowDetail.getInstanceOf(subDetailDto.getAccountId(), subDetailDto.getAccountName(),
								month, year, monthName, paidAmount, subDetailDto.getAccountTypeId()));
						// mcWithholdTaxes.add(MCFlowDetail.getInstanceOf(0, WITHHOLDING_TAX, month, year, monthName, 0.0, 0));
						mcOutTotals.add(MCFlowDetail.getInstanceOf(0, CASH_OUT_LABEL, month, year, monthName, paidAmount, 0));
						mcNet.add(MCFlowDetail.getInstanceOf(0, NET_CASH_FLOW_LABEL, month, year, monthName, -paidAmount, 0));
						cashOutFlows.addAll(mcFlowDetail);
						totaNetAmount += -paidAmount;
					}
				} else if (sourceLabel.equalsIgnoreCase(AP_PAYMENT)) {
					// Out flow - AP payment.
					if (index != 0) {
						double linePercentage = subDetailDto.getAmount() / subDetailDto.getTrAmount();
						double totalPaidAmt = subDetailDto.getPaidAmount() * linePercentage;
						mcFlowDetail = new ArrayList<MCFlowDetail>();
						// double wtaxPercentage = subDetailDto.getWtaxAmount() * linePercentage;
						mcFlowDetail.add(MCFlowDetail.getInstanceOf(subDetailDto.getAccountId(), subDetailDto.getAccountName(),
								month, year, monthName, totalPaidAmt, subDetailDto.getAccountTypeId()));
						// mcWithholdTaxes.add(MCFlowDetail.getInstanceOf(0, WITHHOLDING_TAX, month, year, monthName, wtaxPercentage, 0));
						mcOutTotals.add(MCFlowDetail.getInstanceOf(0, CASH_OUT_LABEL, month, year, monthName, totalPaidAmt, 0));
						mcNet.add(MCFlowDetail.getInstanceOf(0, NET_CASH_FLOW_LABEL, month, year, monthName, -totalPaidAmt, 0));
						cashOutFlows.addAll(mcFlowDetail);
						totaNetAmount += -totalPaidAmt;
					}
				} else if (sourceLabel.equalsIgnoreCase(ACCT_COLLECTION)) {
					// In flow - AR Collection.
					if (index != 0) {
						double linePercentage = subDetailDto.getAmount() / subDetailDto.getTrAmount();
						double totalCollectedAmt = subDetailDto.getPaidAmount() * linePercentage;
						mcFlowDetail = new ArrayList<MCFlowDetail>();
						mcFlowDetail.add(MCFlowDetail.getInstanceOf(subDetailDto.getAccountId(), subDetailDto.getAccountName(),
								month, year, monthName, totalCollectedAmt, subDetailDto.getAccountTypeId()));
						mcNet.add(MCFlowDetail.getInstanceOf(0, NET_CASH_FLOW_LABEL, month, year, monthName, totalCollectedAmt, 0));
						cashInflows.addAll(mcFlowDetail);
						totaNetAmount += totalCollectedAmt;
					}
				} else if (sourceLabel.equalsIgnoreCase(OTHER_RECEIPT)) {
					// In flow - AR Miscellaneous
					if (index != 0) {
						double collectedAmt = subDetailDto.getAmount();
						mcFlowDetail = new ArrayList<MCFlowDetail>();
						mcFlowDetail.add(MCFlowDetail.getInstanceOf(subDetailDto.getAccountId(), subDetailDto.getAccountName(),
								month, year, monthName, subDetailDto.getAmount(), subDetailDto.getAccountTypeId()));
						mcNet.add(MCFlowDetail.getInstanceOf(0, NET_CASH_FLOW_LABEL, month, year, monthName, collectedAmt, 0));
						cashInflows.addAll(mcFlowDetail);
						totaNetAmount += collectedAmt;
					}
				}
			}

			if (nextMDateDto != null) {
				// set beginning balance for the next month
				mcBankBegs.add(MCFlowDetail.getInstanceOf(0, "CASH IN BANK", nextMDateDto.getMonth(),
						nextMDateDto.getYear(), nextMDateDto.getMonthName(), totaNetAmount, 0));
			}

			index++;
			subDetailDtos = null;
		}

		initCashFlow(cashInflows, months, mcFlowDetail);
		initCashFlow(cashOutFlows, months, mcFlowDetail);
		initCashFlow(mcBankBegs, months, mcFlowDetail);
		mcBalEnd.addAll(mcNet);
		mcBalEnd.addAll(mcBankBegs);

		mcfd.setMcOutTotals(mcOutTotals);
		mcfd.setMcNet(mcNet);
		mcfd.setCashInflows(cashInflows);
		mcfd.setMcBankBeg(mcBankBegs);
		mcfd.setCashOutFlows(cashOutFlows);
		// mcfd.setMcWithholdTax(mcWithholdTaxes);
		mcfd.setCashFlowHeaders(cashFlowHeaders);
		mcfd.setMcBalEnd(mcBalEnd);
		return mcfd;
	}

	private void initCashFlow(List<MCFlowDetail> flowDetails, List<MCFlowAccountDateDto> months,
			List<MCFlowDetail> mcFlowDetail) {
		if (!flowDetails.isEmpty()) {
			MCFlowDetail flowDetail = flowDetails.iterator().next();
			Integer acctId = flowDetail.getAccountId();
			String accountName = flowDetail.getAccountName();
			int cnt = 0;
			for (MCFlowAccountDateDto m : months) {
				if (cnt == 0) {
					cnt++;
					continue;
				}
				mcFlowDetail = new ArrayList<MCFlowDetail>();
				mcFlowDetail.add(MCFlowDetail.getInstanceOf(acctId, accountName, m.getMonth(), m.getYear(),
						m.getMonthName(), 0.0, flowDetail.getAccountTypeId()));
				flowDetails.addAll(mcFlowDetail);
			}
		}
	}

	/**
	 * Generate the list of months.
	 * @param currentMonth The current month.
	 * @param prevMonths The number of previous months.
	 * @return The list of {@code MCFlowAccountDateDto}
	 */
	private List<MCFlowAccountDateDto> generateMonthList(int currentMonth, int prevMonths) {
		List<MCFlowAccountDateDto> months = new ArrayList<>();
		Date currentDate = new Date();
		currentDate = DateUtil.createDate(DateUtil.getYear(currentDate), currentMonth, 1);
		Date origDate = currentDate;
		Date dateFrom = null;
		Date dateTo = null;
		String[] monthNames = new DateFormatSymbols().getMonths();
		int currentYear = 0;
		for (int month=prevMonths; month>0; month--) {
			currentDate = DateUtil.addMonthsToDate(origDate, -month);
			currentYear = DateUtil.getYear(currentDate);
			int curMonth = DateUtil.getMonth(currentDate);
			dateFrom = DateUtil.createDate(currentYear, curMonth, 1);
			dateTo = DateUtil.createDate(currentYear, curMonth, DateUtil.getMaxDaysInMonth(currentYear, curMonth));
			months.add(new MCFlowAccountDateDto(curMonth, currentYear, monthNames[curMonth], dateFrom, dateTo));
		}
		currentYear = DateUtil.getYear(origDate);
		dateFrom = DateUtil.getFirstDayOfMonth(origDate);
		months.add(new MCFlowAccountDateDto(currentMonth, currentYear, monthNames[currentMonth], dateFrom, 
				DateUtil.createDate(currentYear, currentMonth, DateUtil.getMaxDaysInMonth(currentYear, currentMonth))));
		return months;
	}

}
