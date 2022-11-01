package eulap.eb.service;

/**
 * Handles the SSS computation for employee and employer.


 */

public class SssComputer {
	private static final double MIN_BASIC_SALARY = 1000;

	public static double getEmployeeContribution(Double basicMonthlySalary) {
		if (basicMonthlySalary < MIN_BASIC_SALARY) {
			return 0;
		}
		SSSCompansationRange salaryRange = SSSCompansationRange.getCompRange(basicMonthlySalary);
		return salaryRange.employeeContrib;
	}

	public static double getEmployerContribution(Double basicMonthlySalary) {
		if (basicMonthlySalary < MIN_BASIC_SALARY) {
			return 0;
		}
		SSSCompansationRange salaryRange = SSSCompansationRange.getCompRange(basicMonthlySalary);
		return salaryRange.employerContrib;
	}

	public static double getEc(Double basicMonthlySalary) {
		if (basicMonthlySalary < MIN_BASIC_SALARY) {
			return 0;
		}
		SSSCompansationRange salaryRange = SSSCompansationRange.getCompRange(basicMonthlySalary);
		return salaryRange.ec;
	}

	public enum SSSCompansationRange {
		ROC_1000_3249_99 (1000, 3249.99, 255, 135, 10), 
		ROC_3250_3749_99 (3250, 3749.99, 297.5, 157.5, 10), 
		ROC_3750_4249_99 (3750, 4249.99, 340, 180, 10), 
		ROC_4250_4749_99 (4250, 4749.99, 382.5, 202.5, 10), 
		ROC_4750_5249_99 (4750, 5249.99, 425, 225, 10), 
		ROC_5250_5749_99 (5250, 5749.99, 467.5, 247.5, 10), 
		ROC_5750_6249_99 (5750, 6249.99, 510, 270, 10), 
		ROC_6250_6749_99 (6250, 6749.99, 552.5, 292.5, 10), 
		ROC_6750_7249_99 (6750, 7249.99, 595, 315, 10), 
		ROC_7250_7749_99 (7250, 7749.99, 637.5, 337.5, 10), 
		ROC_7750_8249_99 (7750, 8249.99, 680, 360, 10), 
		ROC_8250_8749_99 (8250, 8749.99, 722.5, 382.5, 10), 
		ROC_8750_9249_99 (8750, 9249.99, 765, 405, 10), 
		ROC_9250_9749_99 (9250, 9749.99, 807.5, 427.5, 10), 
		ROC_9750_10249_99 (9750, 10249.99, 850, 450, 10), 
		ROC_10250_10749_99 (10250, 10749.99, 892.5, 472.5, 10), 
		ROC_10750_11249_99 (10750, 11249.99, 935, 495, 10), 
		ROC_11250_11749_99 (11250, 11749.99, 977.5, 517.5, 10), 
		ROC_11750_12249_99 (11750, 12249.99, 1020, 540, 10), 
		ROC_12250_12749_99 (12250, 12749.99, 1062.5, 562.5, 10), 
		ROC_12750_13249_99 (12750, 13249.99, 1105, 585, 10), 
		ROC_13250_13749_99 (13250, 13749.99, 1147.5, 607.5, 10), 
		ROC_13750_14249_99 (13750, 14249.99, 1190, 630, 10), 
		ROC_14250_14749_99 (14250, 14749.99, 1232.5, 652.5, 10), 
		ROC_14750_15249_99 (14750, 15249.99, 1275, 675, 30), 
		ROC_15250_15749_99 (15250, 15749.99, 1317.5, 697.5, 30), 
		ROC_15750_16249_99 (15750, 16249.99, 1360, 720, 30), 
		ROC_16250_16749_99 (16250, 16749.99, 1402.5, 742.5, 30), 
		ROC_16750_17249_99 (16750, 17249.99, 1445, 765, 30), 
		ROC_17250_17749_99 (17250, 17749.99, 1487.5, 787.5, 30), 
		ROC_17750_18249_99 (17750, 18249.99, 1530, 810, 30), 
		ROC_18250_18749_99 (18250, 18749.99, 1572.5, 832.5, 30), 
		ROC_18750_19249_99 (18750, 19249.99, 1615, 855, 30), 
		ROC_19250_19749_99 (19250, 19749.99, 1657.5, 877.5, 30), 
		ROC_19750_20249_99 (19750, 20249.99, 1700, 900, 30), 
		ROC_20250_20749_99 (20250, 20749.99, 1742.5, 922.5, 30), 
		ROC_20750_21249_99 (20750, 21249.99, 1785, 945, 30), 
		ROC_21250_21749_99 (21250, 21749.99, 1827.5, 967.5, 30), 
		ROC_21750_22249_99 (21750, 22249.99, 1870, 990, 30), 
		ROC_22250_22749_99 (22250, 22749.99, 1912.5, 1012.5, 30), 
		ROC_22750_23249_99 (22750, 23249.99, 1955, 1035, 30), 
		ROC_23250_23749_99 (23250, 23749.99, 1997.5, 1057.5, 30), 
		ROC_23750_24249_99 (23750, 24249.99, 2040, 1080, 30), 
		ROC_24250_24749_99 (24250, 24749.99, 2082.5, 1102.5, 30), 
		ROC_24750_OVER (24750, 24750, 2125, 1125, 30);

		double startRange;
		double endRange;
		double employeeContrib;
		double employerContrib;
		double ec;

		private SSSCompansationRange(double startRange, double endRange,
				double employerContrib, double employeeContrib, double ec) {
			this.startRange = startRange;
			this.endRange = endRange;
			this.employeeContrib = employeeContrib;
			this.employerContrib = employerContrib;
			this.ec = ec;
		}

		public static SSSCompansationRange getCompRange (double baseSalary) {
			for (SSSCompansationRange sssRange : SSSCompansationRange.values()){
				if (sssRange.startRange <= baseSalary && sssRange.endRange >= baseSalary)
					return sssRange;
				else if(baseSalary <= ROC_1000_3249_99.startRange)
					return ROC_1000_3249_99;
				else if(baseSalary > ROC_24750_OVER.endRange)
					return ROC_24750_OVER;
			}
			throw new RuntimeException("Unknown SSS salary range : " + baseSalary);
		}
	}
}
