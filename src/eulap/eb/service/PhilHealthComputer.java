package eulap.eb.service;

import java.util.Date;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;

/**
 * Computation for the PhilHealth Contribution of Employee and Employer.


 *
 */
public class PhilHealthComputer {
	private static final double SALARY_FLOOR = 10000.00;

	public static double getEmployeeContrib(double baseSalary) {
		return computeContribution(baseSalary);
	}

	public static double getEmployerContrib(double baseSalary) {
		return computeContribution(baseSalary);
	}

	private static double computeContribution(double baseSalary) {
		if (baseSalary == 0) {
			return 0;
		}
		PhilhealthCompansationRange salaryRange = PhilhealthCompansationRange.getCompRange();
		if (baseSalary < SALARY_FLOOR) {
			baseSalary = SALARY_FLOOR;
		} else if (baseSalary > salaryRange.endRange) {
			baseSalary = salaryRange.endRange;
		}
		return NumberFormatUtil.divideWFP((NumberFormatUtil.multiplyWFP(salaryRange.percentageMultiplier, baseSalary)), 2);
	}

	public enum PhilhealthCompansationRange {
		ROC_2019 (2019, 50000.00, 0.0275),
		ROC_2020 (2020, 60000.00, 0.03),
		ROC_2021 (2021, 70000.00, 0.035),
		ROC_2022 (2022, 80000.00, 0.04),
		ROC_2023 (2023, 90000.00, 0.045),
		ROC_2024 (2024, 100000.00, 0.05),
		ROC_2025 (2025, 100000.00, 0.05);

		int year;
		double endRange;
		double percentageMultiplier;

		private PhilhealthCompansationRange(int year, double endRange, double percentageMultiplier) {
			this.year = year;
			this.endRange = endRange;
			this.percentageMultiplier = percentageMultiplier;
		}

		public void setEndRange(double endRange) {
			this.endRange = endRange;
		}

		public static PhilhealthCompansationRange getCompRange () {
			int currentYear = DateUtil.getYear(new Date());
			for (PhilhealthCompansationRange range : PhilhealthCompansationRange.values()){
				if (range.year == currentYear) {
					return range;
				}
			}
			throw new RuntimeException("Unknown Philhealth year : " + currentYear);
		}
	}
}
