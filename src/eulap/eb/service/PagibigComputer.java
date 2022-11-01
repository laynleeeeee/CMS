package eulap.eb.service;

import eulap.common.util.NumberFormatUtil;

/**
 * Handles the  computation for employee and employer of PagIbig.

 */

public class PagibigComputer {
	private static final double PAGIBIG_MIN_MONTHLY_COMPENSATION = 1500;
	private static final double PAGIBIG_MAX_MONTHLY_COMPENSATION = 5000;
	private static final double PAGIBIG_MAX_CONTRIBUTION = 100;

	public static double getEmployeeContribution(Double basicMonthlySalary) {
		if (basicMonthlySalary >= PAGIBIG_MAX_MONTHLY_COMPENSATION) {
			return PAGIBIG_MAX_CONTRIBUTION;
		}
		PagibigCompansationRange salaryRange = PagibigCompansationRange.getCompRange(basicMonthlySalary);
		return NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(salaryRange.employeeShare, 100)), basicMonthlySalary);
	}

	public static double getEmployerContribution(Double basicMonthlySalary) {
		if (basicMonthlySalary >= PAGIBIG_MAX_MONTHLY_COMPENSATION) {
			return PAGIBIG_MAX_CONTRIBUTION;
		}
		PagibigCompansationRange salaryRange = PagibigCompansationRange.getCompRange(basicMonthlySalary);
		return NumberFormatUtil.multiplyWFP((NumberFormatUtil.divideWFP(salaryRange.employerShare, 100)), basicMonthlySalary);
	}

	public enum PagibigCompansationRange {
		ROC_1500_BELOW (1, 2),
		ROC_1501_4999 (2, 2);

		double employeeShare;
		double employerShare;

		private PagibigCompansationRange(double employeeShare, double employerShare) {
			this.employeeShare = employeeShare;
			this.employerShare = employerShare;
		}

		public static PagibigCompansationRange getCompRange (double baseSalary) {
			if (baseSalary <= PAGIBIG_MIN_MONTHLY_COMPENSATION) {
				return ROC_1500_BELOW;
			}
			return ROC_1501_4999;
		}
	}
}
