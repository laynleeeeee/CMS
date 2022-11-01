package eulap.eb.service.report;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.CustodianAccountDao;
import eulap.eb.dao.PettyCashVoucherDao;
import eulap.eb.dao.PettyCashVoucherLiquidationDao;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.AccountAnalysisReport;
import eulap.eb.web.dto.DailyPettyCashFundReportDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Service class that will handle business logic for daily petty Cash fund report generation

 */

@Service
public class DailyPettyCashFundReportService {
	@Autowired
	private PettyCashVoucherDao pettyCashVoucherDao;
	@Autowired
	private WorkflowServiceHandler workflowServiceHandler;
	@Autowired
	private CustodianAccountDao custodianAccountDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private PettyCashVoucherLiquidationDao pcvlDao;

	/**
	 * Get the statuses of the forms.
	 * @param user The logged in user.
	 * @return The list of {@link FormStatus}.
	 */
	public List<FormStatus> getFormStatuses(User user) {
		List<FormStatus> formStatuses = workflowServiceHandler.getAllStatuses("PettyCashVoucher6", user, false);
		// Set is a unique collection.
		Set<FormStatus> statuses = new HashSet<FormStatus>();
		statuses.addAll(formStatuses);
		return new ArrayList<FormStatus>(statuses);
	}

	/**
	 * Generate jasper report of daily petty cash fund report.
	 * @param company object for Company
	 * @param param daily petty cash fund parameter object
	 * @return The generated daily petty cash fund report.
	 */
	public JRDataSource generateDailyPettyCashFundReport(DailyPettyCashFundReportParam param) {
		EBJRServiceHandler<DailyPettyCashFundReportDto> handler = new JRDailyPettyCashFundReportHandler(param, this);
		return new EBDataSource<DailyPettyCashFundReportDto>(handler);
	}

	private static class JRDailyPettyCashFundReportHandler implements EBJRServiceHandler<DailyPettyCashFundReportDto> {
		private DailyPettyCashFundReportParam param;
		private DailyPettyCashFundReportService reportService;

		private JRDailyPettyCashFundReportHandler (DailyPettyCashFundReportParam param,
				DailyPettyCashFundReportService reportService) {
			this.param = param;
			this.reportService = reportService;
		}

		@Override
		public void close() throws IOException {
			reportService = null;
		}

		@Override
		public Page<DailyPettyCashFundReportDto> nextPage(PageSetting pageSetting) {
			return reportService.pettyCashVoucherDao.searchDailyPettyCashFundReport(param, pageSetting);
		}
	}

	/**
	 * Get the custodian cash fund
	 * @param param The daily petty cash fund report parameter object
	 * @return The total custodian cash fund
	 */
	public Double getCashFundCustodian(DailyPettyCashFundReportParam param) {
		double cashfund = 0;
		int companyId = param.getCompanyId();
		int divisionId = param.getDivisionId();
		int userCustodianAcctId = param.getCustodianId();
		Date asOfDate = param.getDate();
		List<CustodianAccount> custodianAccts = filterDuplicateAccts(
				custodianAccountDao.getCustodianAccounts(companyId, divisionId, userCustodianAcctId));
		AccountAnalysisReport beginningBal = null;
		for (CustodianAccount ca : custodianAccts) {
			beginningBal = accountDao.getAccountBalance(companyId, ca.getFdAccountCombination().getAccount(),
					ca.getFdAccountCombination().getDivision().getNumber(),
					ca.getFdAccountCombination().getDivision().getNumber(), asOfDate, null);
			cashfund += beginningBal.getBalance();
			beginningBal = null;
		}
		return cashfund;
	}

	private List<CustodianAccount> filterDuplicateAccts(List<CustodianAccount> custodianAccts) {
		Map<Integer, CustodianAccount> hmCustodianAccts = new HashMap<Integer, CustodianAccount>();
		for (CustodianAccount ca : custodianAccts) {
			Integer key = ca.getFdAccountCombinationId();
			if (!hmCustodianAccts.containsKey(key)) {
				hmCustodianAccts.put(key, ca);
			}
		}
		return new ArrayList<CustodianAccount>(hmCustodianAccts.values());
	}

	/**
	 * Get the total liquidated petty cash amount per day.
	 * @param companyId The company id.
	 * @param divisionId The division id.
	 * @param userCustodianAcctId The user custodian account id.
	 * @param currentStatusId The current status id.
	 * @param date The date.
	 * @return The total liquidated petty cash amount per day.
	 */
	public Double getTotalPettyCashLiquidationPerDay(Integer companyId, Integer divisionId, Integer userCustodianAcctId, 
			Date date) {
		return pcvlDao.getTotalPettyCashLiquidationPerDay(companyId, divisionId, userCustodianAcctId, date);
	}
}