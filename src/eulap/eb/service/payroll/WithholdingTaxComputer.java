package eulap.eb.service.payroll;

/**
 * Computation of Withholding tax of Employee.

 */
public class WithholdingTaxComputer {

	public static final int SEMI_MONTHLY_PERIOD = 1;
	public static final int WEEKLY_PERIOD = 2;
	public static final int MONTHLY_PERIOD = 3;
	public static final int STATUS_Z = 1;
	public static final int STATUS_S_ME = 2;
	public static final int STATUS_S_ME_1 = 3;
	public static final int STATUS_S_ME_2 = 4;
	public static final int STATUS_S_ME_3 = 5;
	public static final int STATUS_S_ME_4 = 6;

	public static double computeWithholdingTax(int period, WTEmployeeStatus status, double baseSalary) {

		WithholdingTaxTable  taxTable = WithholdingTaxTable.getWithholdingTax(period, status.getEmployeStatusId(), baseSalary);
		double excessSalary = baseSalary - taxTable.salaryLowerRange;
		double additionalTax = excessSalary * (taxTable.taxComputation.additionalComputation/100);
		return taxTable.taxComputation.fixedComputation + additionalTax;
	}

	public enum WithholdingTaxTable {
		//Semi-monthly period - Z
		SM_Z_LOWEST (SEMI_MONTHLY_PERIOD, STATUS_Z, 0.0, 0.0, TaxComputation.SM_1),
		SM_Z_0 (SEMI_MONTHLY_PERIOD, STATUS_Z, 0.0, 417.0, TaxComputation.SM_2),
		SM_Z_417 (SEMI_MONTHLY_PERIOD, STATUS_Z, 417.0, 1250.0, TaxComputation.SM_3),
		SM_Z_1250 (SEMI_MONTHLY_PERIOD, STATUS_Z, 1250.0, 2917.0, TaxComputation.SM_4),
		SM_Z_2917 (SEMI_MONTHLY_PERIOD, STATUS_Z, 2917.0, 5833.0, TaxComputation.SM_5),
		SM_Z_5833 (SEMI_MONTHLY_PERIOD, STATUS_Z, 5833.0, 10417.0, TaxComputation.SM_6),
		SM_Z_10417 (SEMI_MONTHLY_PERIOD, STATUS_Z, 10417.0, 20833.0, TaxComputation.SM_7),
		SM_Z_HIGHEST (SEMI_MONTHLY_PERIOD, STATUS_Z, 20833.0, 20833.0, TaxComputation.SM_8),

		//Semi-monthly period - S/ME
		SM_S_ME_LOWEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 50.0, 2083.0, TaxComputation.SM_1),
		SM_S_ME_2083 (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 2083.0, 2500.0, TaxComputation.SM_2),
		SM_S_ME_2500 (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 2500.0, 3333.0, TaxComputation.SM_3),
		SM_S_ME_3333 (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 3333.0, 5000.0, TaxComputation.SM_4),
		SM_S_ME_5000 (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 5000.0, 7917.0, TaxComputation.SM_5),
		SM_S_ME_7917 (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 7917.0, 12500.0, TaxComputation.SM_6),
		SM_S_ME_12500 (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 12500.0, 22917.0, TaxComputation.SM_7),
		SM_S_ME_HIGHEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME, 22917.0, 22917.0, TaxComputation.SM_8),

		//Semi-monthly period - S1/ME1
		SM_S1_ME1_LOWEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 75.0, 3125.0, TaxComputation.SM_1),
		SM_S1_ME1_3125 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 3125.0, 3542.0, TaxComputation.SM_2),
		SM_S1_ME1_3542 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 3542.0, 4375.0, TaxComputation.SM_3),
		SM_S1_ME1_4375 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 4375.0, 6042.0, TaxComputation.SM_4),
		SM_S1_ME1_6042 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 6042.0, 8958.0, TaxComputation.SM_5),
		SM_S1_ME1_8958 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 8958.0, 13542.0, TaxComputation.SM_6),
		SM_S1_ME1_13542 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 13542.0, 23958.0, TaxComputation.SM_7),
		SM_S1_ME1_HIGHEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_1, 23958.0, 23958.0, TaxComputation.SM_8),

		//Semi-monthly period - S2/ME2
		SM_S2_ME2_LOWEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 100.0, 4167.0, TaxComputation.SM_1),
		SM_S2_ME2_4167 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 4167.0, 4583.0, TaxComputation.SM_2),
		SM_S2_ME2_4583 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 4583.0, 5417.0, TaxComputation.SM_3),
		SM_S2_ME2_5417 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 5417.0, 7083.0, TaxComputation.SM_4),
		SM_S2_ME2_7083 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 7083.0, 10000.0, TaxComputation.SM_5),
		SM_S2_ME2_10000 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 10000.0, 14583.0, TaxComputation.SM_6),
		SM_S2_ME2_14583 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 14583.0, 25000.0, TaxComputation.SM_7),
		SM_S2_ME2_HIGHEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_2, 25000.0, 25000.0, TaxComputation.SM_8),

		//Semi-monthly period - S3/ME3
		SM_S3_ME3_LOWEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 125.0, 5208.0, TaxComputation.SM_1),
		SM_S3_ME3_5208 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 5208.0, 5625.0, TaxComputation.SM_2),
		SM_S3_ME3_5625 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 5625.0, 6458.0, TaxComputation.SM_3),
		SM_S3_ME3_6458 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 6458.0, 8125.0, TaxComputation.SM_4),
		SM_S3_ME3_8125 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 8125.0, 11042.0, TaxComputation.SM_5),
		SM_S3_ME3_11042 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 11042.0, 15625.0, TaxComputation.SM_6),
		SM_S3_ME3_15625 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 15625.0, 26042.0, TaxComputation.SM_7),
		SM_S3_ME3_HIGHEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_3, 26042.0, 26042.0, TaxComputation.SM_8),

		//Semi-monthly period - S4/ME4
		SM_S4_ME4_LOWEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 150.0, 6250.0, TaxComputation.SM_1),
		SM_S4_ME4_6250 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 6250.0, 6667.0, TaxComputation.SM_2),
		SM_S4_ME4_6667 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 6667.0, 7500.0, TaxComputation.SM_3),
		SM_S4_ME4_7500 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 7500.0, 9167.0, TaxComputation.SM_4),
		SM_S4_ME4_9167 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 9167.0, 12083.0, TaxComputation.SM_5),
		SM_S4_ME4_12083 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 12083.0, 16667.0, TaxComputation.SM_6),
		SM_S4_ME4_16667 (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 16667.0, 27083.0, TaxComputation.SM_7),
		SM_S4_ME4_HIGHEST (SEMI_MONTHLY_PERIOD, STATUS_S_ME_4, 27083.0, 27083.0, TaxComputation.SM_8),

		//Monthly period - Z
		M_Z_LOWEST (MONTHLY_PERIOD, STATUS_Z, 0.0, 0.0, TaxComputation.M_1),
		M_Z_0 (MONTHLY_PERIOD, STATUS_Z, 0.0, 833.0, TaxComputation.M_2),
		M_Z_833 (MONTHLY_PERIOD, STATUS_Z, 833.0, 2500.0, TaxComputation.M_3),
		M_Z_2500 (MONTHLY_PERIOD, STATUS_Z, 2500.0, 5833.0, TaxComputation.M_4),
		M_Z_5833 (MONTHLY_PERIOD, STATUS_Z, 5833.0, 11667.0, TaxComputation.M_5),
		M_Z_11667 (MONTHLY_PERIOD, STATUS_Z, 11667.0, 20833.0, TaxComputation.M_6),
		M_Z_20833 (MONTHLY_PERIOD, STATUS_Z, 20833.0, 41677.0, TaxComputation.M_7),
		M_Z_HIGHEST (MONTHLY_PERIOD, STATUS_Z, 41667.0, 41667.0, TaxComputation.M_8),

		//Monthly period - S/ME
		M_S_ME_LOWEST (MONTHLY_PERIOD, STATUS_S_ME, 50.0, 4167.0, TaxComputation.M_1),
		M_S_ME_4167 (MONTHLY_PERIOD, STATUS_S_ME, 4167.0, 5000.0, TaxComputation.M_2),
		M_S_ME_5000 (MONTHLY_PERIOD, STATUS_S_ME, 5000.0, 6667.0, TaxComputation.M_3),
		M_S_ME_6667 (MONTHLY_PERIOD, STATUS_S_ME, 6667.0, 10000.0, TaxComputation.M_4),
		M_S_ME_10000 (MONTHLY_PERIOD, STATUS_S_ME, 10000.0, 15833.0, TaxComputation.M_5),
		M_S_ME_15833 (MONTHLY_PERIOD, STATUS_S_ME, 15833.0, 25000.0, TaxComputation.M_6),
		M_S_ME_25000 (MONTHLY_PERIOD, STATUS_S_ME, 25000.0, 45833.0, TaxComputation.M_7),
		M_S_ME_HIGHEST (MONTHLY_PERIOD, STATUS_S_ME, 45833.0, 45833.0, TaxComputation.M_8),

		//Monthly period - S1/ME1
		M_S1_ME1_LOWEST (MONTHLY_PERIOD, STATUS_S_ME_1, 75.0, 6250.0, TaxComputation.M_1),
		M_S1_ME1_6250 (MONTHLY_PERIOD, STATUS_S_ME_1, 6250.0, 7083.0, TaxComputation.M_2),
		M_S1_ME1_7083 (MONTHLY_PERIOD, STATUS_S_ME_1, 7083.0, 8750.0, TaxComputation.M_3),
		M_S1_ME1_8750 (MONTHLY_PERIOD, STATUS_S_ME_1, 8750.0, 12083.0, TaxComputation.M_4),
		M_S1_ME1_12083 (MONTHLY_PERIOD, STATUS_S_ME_1, 12083.0, 17917.0, TaxComputation.M_5),
		M_S1_ME1_17917 (MONTHLY_PERIOD, STATUS_S_ME_1, 17917.0, 27083.0, TaxComputation.M_6),
		M_S1_ME1_27083 (MONTHLY_PERIOD, STATUS_S_ME_1, 27083.0, 47917.0, TaxComputation.M_7),
		M_S1_ME1_HIGHEST (MONTHLY_PERIOD, STATUS_S_ME_1, 47917.0, 47917.0, TaxComputation.M_8),

		//Monthly period - S3/ME2
		M_S2_ME2_LOWEST (MONTHLY_PERIOD, STATUS_S_ME_2, 100.0, 8333.0, TaxComputation.M_1),
		M_S2_ME2_8333 (MONTHLY_PERIOD, STATUS_S_ME_2, 8333.0, 9167.0, TaxComputation.M_2),
		M_S2_ME2_9167 (MONTHLY_PERIOD, STATUS_S_ME_2, 9167.0, 10833.0, TaxComputation.M_3),
		M_S2_ME2_10833 (MONTHLY_PERIOD, STATUS_S_ME_2, 10833.0, 14167.0, TaxComputation.M_4),
		M_S2_ME2_14167 (MONTHLY_PERIOD, STATUS_S_ME_2, 14167.0, 20000.0, TaxComputation.M_5),
		M_S2_ME2_20000 (MONTHLY_PERIOD, STATUS_S_ME_2, 20000.0, 29167.0, TaxComputation.M_6),
		M_S2_ME2_29167 (MONTHLY_PERIOD, STATUS_S_ME_2, 29167.0, 50000.0, TaxComputation.M_7),
		M_S2_ME2_HIGHEST (MONTHLY_PERIOD, STATUS_S_ME_2, 50000.0, 50000.0, TaxComputation.M_8),

		//Monthly period - S3/ME3
		M_S3_ME3_LOWEST (MONTHLY_PERIOD, STATUS_S_ME_3, 125.0, 10417.0, TaxComputation.M_1),
		M_S3_ME3_10417 (MONTHLY_PERIOD, STATUS_S_ME_3, 10417.0, 11250.0, TaxComputation.M_2),
		M_S3_ME3_11250 (MONTHLY_PERIOD, STATUS_S_ME_3, 11250.0, 12917.0, TaxComputation.M_3),
		M_S3_ME3_12917 (MONTHLY_PERIOD, STATUS_S_ME_3, 12917.0, 16250.0, TaxComputation.M_4),
		M_S3_ME3_16250 (MONTHLY_PERIOD, STATUS_S_ME_3, 16250.0, 22083.0, TaxComputation.M_5),
		M_S3_ME3_22083 (MONTHLY_PERIOD, STATUS_S_ME_3, 22083.0, 31250.0, TaxComputation.M_6),
		M_S3_ME3_31250 (MONTHLY_PERIOD, STATUS_S_ME_3, 31250.0, 52083.0, TaxComputation.M_7),
		M_S3_ME3_HIGHEST (MONTHLY_PERIOD, STATUS_S_ME_3, 52083.0, 52083.0, TaxComputation.M_8),

		//Monthly period - S4/ME4
		M_S4_ME4_LOWEST (MONTHLY_PERIOD, STATUS_S_ME_4, 150.0, 12500.0, TaxComputation.M_1),
		M_S4_ME4_12500 (MONTHLY_PERIOD, STATUS_S_ME_4, 12500.0, 13333.0, TaxComputation.M_2),
		M_S4_ME4_13333 (MONTHLY_PERIOD, STATUS_S_ME_4, 13333.0, 15000.0, TaxComputation.M_3),
		M_S4_ME4_15000 (MONTHLY_PERIOD, STATUS_S_ME_4, 15000.0, 18333.0, TaxComputation.M_4),
		M_S4_ME4_18333 (MONTHLY_PERIOD, STATUS_S_ME_4, 18333.0, 24167.0, TaxComputation.M_5),
		M_S4_ME4_24167 (MONTHLY_PERIOD, STATUS_S_ME_4, 24167.0, 33333.0, TaxComputation.M_6),
		M_S4_ME4_33333 (MONTHLY_PERIOD, STATUS_S_ME_4, 33333.0, 54167.0, TaxComputation.M_7),
		M_S4_ME4_HIGHEST (MONTHLY_PERIOD, STATUS_S_ME_4, 54167.0, 54167.0, TaxComputation.M_8),

		//Weekly period - Z
		W_Z_LOWEST (WEEKLY_PERIOD, STATUS_Z, 0.0, 0.0, TaxComputation.W_1),
		W_Z_0 (WEEKLY_PERIOD, STATUS_Z, 0.0, 192.0, TaxComputation.W_2),
		W_Z_192 (WEEKLY_PERIOD, STATUS_Z, 192.0, 577.0, TaxComputation.W_3),
		W_Z_577 (WEEKLY_PERIOD, STATUS_Z, 577.0, 1346.0, TaxComputation.W_4),
		W_Z_1346 (WEEKLY_PERIOD, STATUS_Z, 1346.0, 2692.0, TaxComputation.W_5),
		W_Z_2692 (WEEKLY_PERIOD, STATUS_Z, 2692.0, 4808.0, TaxComputation.W_6),
		W_Z_4808 (WEEKLY_PERIOD, STATUS_Z, 4808.0, 9615.0, TaxComputation.W_7),
		W_Z_HIGHEST (WEEKLY_PERIOD, STATUS_Z, 9615.0, 9615.0, TaxComputation.W_8),

		//Weekly period - S/ME
		W_S_ME_LOWEST (WEEKLY_PERIOD, STATUS_S_ME, 50.0, 962.0, TaxComputation.W_1),
		W_S_ME_962 (WEEKLY_PERIOD, STATUS_S_ME, 962.0, 1154.0, TaxComputation.W_2),
		W_S_ME_1154 (WEEKLY_PERIOD, STATUS_S_ME, 1154.0, 1538.0, TaxComputation.W_3),
		W_S_ME_1538 (WEEKLY_PERIOD, STATUS_S_ME, 1538.0, 2308.0, TaxComputation.W_4),
		W_S_ME_2308 (WEEKLY_PERIOD, STATUS_S_ME, 2308.0, 3654.0, TaxComputation.W_5),
		W_S_ME_3654 (WEEKLY_PERIOD, STATUS_S_ME, 3654.0, 5769.0, TaxComputation.W_6),
		W_S_ME_5769 (WEEKLY_PERIOD, STATUS_S_ME, 5769.0, 10577.0, TaxComputation.W_7),
		W_S_ME_HIGHEST (WEEKLY_PERIOD, STATUS_S_ME, 10577.0, 10577.0, TaxComputation.W_8),

		//Weekly period - S1/ME1
		W_S1_ME1_LOWEST (WEEKLY_PERIOD, STATUS_S_ME_1, 75.0, 1442.0, TaxComputation.W_1),
		W_S1_ME1_1442 (WEEKLY_PERIOD, STATUS_S_ME_1, 1442.0, 1635.0, TaxComputation.W_2),
		W_S1_ME1_1635 (WEEKLY_PERIOD, STATUS_S_ME_1, 1635.0, 2019.0, TaxComputation.W_3),
		W_S1_ME1_2019 (WEEKLY_PERIOD, STATUS_S_ME_1, 2019.0, 2788.0, TaxComputation.W_4),
		W_S1_ME1_2788 (WEEKLY_PERIOD, STATUS_S_ME_1, 2788.0, 4135.0, TaxComputation.W_5),
		W_S1_ME1_4135 (WEEKLY_PERIOD, STATUS_S_ME_1, 4135.0, 6250.0, TaxComputation.W_6),
		W_S1_ME1_6250 (WEEKLY_PERIOD, STATUS_S_ME_1, 6250.0, 11058.0, TaxComputation.W_7),
		W_S1_ME1_HIGHEST (WEEKLY_PERIOD, STATUS_S_ME_1, 11058.0, 11058.0, TaxComputation.W_8),

		//Weekly period - S2/ME2
		W_S2_ME2_LOWEST (WEEKLY_PERIOD, STATUS_S_ME_2, 100.0, 1923.0, TaxComputation.W_1),
		W_S2_ME2_1923 (WEEKLY_PERIOD, STATUS_S_ME_2, 1923.0, 2115.0, TaxComputation.W_2),
		W_S2_ME2_2115(WEEKLY_PERIOD, STATUS_S_ME_2, 2115.0, 2500.0, TaxComputation.W_3),
		W_S2_ME2_2500 (WEEKLY_PERIOD, STATUS_S_ME_2, 2500.0, 3269.0, TaxComputation.W_4),
		W_S2_ME2_3269 (WEEKLY_PERIOD, STATUS_S_ME_2, 3269.0, 4615.0, TaxComputation.W_5),
		W_S2_ME2_4615 (WEEKLY_PERIOD, STATUS_S_ME_2, 4615.0, 6731.0, TaxComputation.W_6),
		W_S2_ME2_6731 (WEEKLY_PERIOD, STATUS_S_ME_2, 6731.0, 11538.0, TaxComputation.W_7),
		W_S2_ME2_HIGHEST (WEEKLY_PERIOD, STATUS_S_ME_2, 11538.0, 11538.0, TaxComputation.W_8),

		//Weekly period - S3/ME3
		W_S3_ME3_LOWEST (WEEKLY_PERIOD, STATUS_S_ME_3, 125.0, 2404.0, TaxComputation.W_1),
		W_S3_ME3_2404 (WEEKLY_PERIOD, STATUS_S_ME_3, 2404.0, 2596.0, TaxComputation.W_2),
		W_S3_ME3_2596 (WEEKLY_PERIOD, STATUS_S_ME_3, 2596.0, 2981.0, TaxComputation.W_3),
		W_S3_ME3_2981 (WEEKLY_PERIOD, STATUS_S_ME_3, 2981.0, 3750.0, TaxComputation.W_4),
		W_S3_ME3_3750 (WEEKLY_PERIOD, STATUS_S_ME_3, 3750.0, 5096.0, TaxComputation.W_5),
		W_S3_ME3_5096 (WEEKLY_PERIOD, STATUS_S_ME_3, 5096.0, 7212.0, TaxComputation.W_6),
		W_S3_ME3_7212 (WEEKLY_PERIOD, STATUS_S_ME_3, 7212.0, 12019.0, TaxComputation.W_7),
		W_S3_ME3_HIGHEST (WEEKLY_PERIOD, STATUS_S_ME_3, 12019.0, 12019.0, TaxComputation.W_8),

		//Weekly period - S4/ME4
		W_S4_ME4_LOWEST (WEEKLY_PERIOD, STATUS_S_ME_4, 150.0, 2885.0, TaxComputation.W_1),
		W_S4_ME4_2885 (WEEKLY_PERIOD, STATUS_S_ME_4, 2885.0, 3077.0, TaxComputation.W_2),
		W_S4_ME4_3077 (WEEKLY_PERIOD, STATUS_S_ME_4, 3077.0, 3462.0, TaxComputation.W_3),
		W_S4_ME4_3462 (WEEKLY_PERIOD, STATUS_S_ME_4, 3462.0, 4231.0, TaxComputation.W_4),
		W_S4_ME4_4231 (WEEKLY_PERIOD, STATUS_S_ME_4, 4231.0, 5577.0, TaxComputation.W_5),
		W_S4_ME4_5577 (WEEKLY_PERIOD, STATUS_S_ME_4, 5577.0, 7692.0, TaxComputation.W_6),
		W_S4_ME4_33333 (WEEKLY_PERIOD, STATUS_S_ME_4, 7692.0, 12500.0, TaxComputation.W_7),
		W_S4_ME4_HIGHEST (WEEKLY_PERIOD, STATUS_S_ME_4, 12500.0, 12500.0, TaxComputation.W_8);

		private int period;
		private int status;
		private Double salaryLowerRange;
		private Double salaryUpperRange;
		private TaxComputation taxComputation;

		private WithholdingTaxTable(int period, int status, Double salaryLowerRange,
				Double salaryUpperRange, TaxComputation taxComputation) {
			this.period = period;
			this.status = status;
			this.salaryLowerRange = salaryLowerRange;
			this.salaryUpperRange = salaryUpperRange;
			this.taxComputation = taxComputation;
		}

		public static WithholdingTaxTable getWithholdingTax(int period, int status, Double baseSalary) {
			for (WithholdingTaxTable withTaxTable : WithholdingTaxTable.values()) {
				if(withTaxTable.period == period && withTaxTable.status == status) {
					WithholdingTaxTable taxTableLowest = null;
					WithholdingTaxTable taxTableHighest = null;
					switch (period) {

					case WEEKLY_PERIOD:
						if(baseSalary >= withTaxTable.salaryLowerRange && baseSalary < withTaxTable.salaryUpperRange) {
							return withTaxTable;
						} else {
							switch (status) {
							case STATUS_Z:
								taxTableHighest = W_Z_HIGHEST;
								taxTableLowest = W_Z_LOWEST;
								break;
							case STATUS_S_ME:
								taxTableHighest = W_S_ME_HIGHEST;
								taxTableLowest = W_S_ME_LOWEST;
								break;
							case STATUS_S_ME_1:
								taxTableHighest = W_S1_ME1_HIGHEST;
								taxTableLowest = W_S1_ME1_LOWEST;
								break;
							case STATUS_S_ME_2:
								taxTableHighest = W_S2_ME2_HIGHEST;
								taxTableLowest = W_S2_ME2_LOWEST;
								break;
							case STATUS_S_ME_3:
								taxTableHighest = W_S3_ME3_HIGHEST;
								taxTableLowest = W_S3_ME3_LOWEST;
								break;
							default:
								taxTableHighest = W_S4_ME4_HIGHEST;
								taxTableLowest = W_S4_ME4_LOWEST;
								break;
							}

							if(getHighestOrLowest(baseSalary, taxTableLowest, taxTableHighest) != null)
								return getHighestOrLowest(baseSalary, taxTableLowest, taxTableHighest);
						}

					case SEMI_MONTHLY_PERIOD:
						if(baseSalary != 0 && baseSalary >= withTaxTable.salaryLowerRange
								&& baseSalary < withTaxTable.salaryUpperRange) {
							return withTaxTable;
						} else {
							switch (status) {
							case STATUS_Z:
								taxTableLowest = SM_Z_LOWEST;
								taxTableHighest = SM_Z_HIGHEST;
								break;
							case STATUS_S_ME:
								taxTableHighest = SM_S_ME_HIGHEST;
								taxTableLowest = SM_S_ME_LOWEST;
								break;
							case STATUS_S_ME_1:
								taxTableHighest = SM_S1_ME1_HIGHEST;
								taxTableLowest = SM_S1_ME1_LOWEST;
								break;
							case STATUS_S_ME_2:
								taxTableHighest = SM_S2_ME2_HIGHEST;
								taxTableLowest = SM_S2_ME2_LOWEST;
								break;
							case STATUS_S_ME_3:
								taxTableHighest = SM_S3_ME3_HIGHEST;
								taxTableLowest = SM_S3_ME3_LOWEST;
								break;
							default:
								taxTableHighest = SM_S4_ME4_HIGHEST;
								taxTableLowest = SM_S4_ME4_LOWEST;
								break;
							}

							if(getHighestOrLowest(baseSalary, taxTableLowest, taxTableHighest) != null)
								return getHighestOrLowest(baseSalary, taxTableLowest, taxTableHighest);
						}

					case MONTHLY_PERIOD:
						if(baseSalary >= withTaxTable.salaryLowerRange && baseSalary < withTaxTable.salaryUpperRange) {
							return withTaxTable;
						} else {
							switch (status) {
							case STATUS_Z:
								taxTableHighest = M_Z_HIGHEST;
								taxTableLowest = M_Z_LOWEST;
								break;
							case STATUS_S_ME:
								taxTableHighest = M_S_ME_HIGHEST;
								taxTableLowest = M_S_ME_LOWEST;
								break;
							case STATUS_S_ME_1:
								taxTableHighest = M_S1_ME1_HIGHEST;
								taxTableLowest = M_S1_ME1_LOWEST;
								break;
							case STATUS_S_ME_2:
								taxTableHighest = M_S2_ME2_HIGHEST;
								taxTableLowest = M_S2_ME2_LOWEST;
								break;
							case STATUS_S_ME_3:
								taxTableHighest = M_S3_ME3_HIGHEST;
								taxTableLowest = M_S3_ME3_LOWEST;
								break;
							default:
								taxTableHighest = M_S4_ME4_HIGHEST;
								taxTableLowest = M_S4_ME4_LOWEST;
								break;
							}

							if(getHighestOrLowest(baseSalary, taxTableLowest, taxTableHighest) != null)
								return getHighestOrLowest(baseSalary, taxTableLowest, taxTableHighest);
						}
					}
				}
			}
			return null;
		}

		public enum TaxComputation {
			//Semi monthly period
			SM_1 (SEMI_MONTHLY_PERIOD, 0.0, 0.0),
			SM_2 (SEMI_MONTHLY_PERIOD, 0.0, 5.0),
			SM_3 (SEMI_MONTHLY_PERIOD, 20.83, 10.0),
			SM_4 (SEMI_MONTHLY_PERIOD, 104.17, 15.0),
			SM_5 (SEMI_MONTHLY_PERIOD, 354.17, 20.0),
			SM_6 (SEMI_MONTHLY_PERIOD, 937.5, 25.0),
			SM_7 (SEMI_MONTHLY_PERIOD, 2083.33, 30.0),
			SM_8 (SEMI_MONTHLY_PERIOD, 5208.33, 32.0),

			//Monthly period
			M_1 (MONTHLY_PERIOD, 0.0, 0.0),
			M_2 (MONTHLY_PERIOD, 0.0, 5.0),
			M_3 (MONTHLY_PERIOD, 41.67, 10.0),
			M_4 (MONTHLY_PERIOD, 208.33, 15.0),
			M_5 (MONTHLY_PERIOD, 708.33, 20.0),
			M_6 (MONTHLY_PERIOD, 1875.0, 25.0),
			M_7 (MONTHLY_PERIOD, 4166.67, 30.0),
			M_8 (MONTHLY_PERIOD, 10416.67, 32.0),

			//Weekly period
			W_1 (WEEKLY_PERIOD, 0.0, 0.0),
			W_2 (WEEKLY_PERIOD, 0.0, 5.0),
			W_3 (WEEKLY_PERIOD, 9.62, 10.0),
			W_4 (WEEKLY_PERIOD, 48.08, 15.0),
			W_5 (WEEKLY_PERIOD, 163.46, 20.0),
			W_6 (WEEKLY_PERIOD, 432.69, 25.0),
			W_7 (WEEKLY_PERIOD, 961.54, 30.0),
			W_8 (WEEKLY_PERIOD, 2403.85, 32.0);

			private int period;
			private Double fixedComputation;
			private Double additionalComputation;

			private TaxComputation(int period, Double fixedComputation, Double additionalComputation) {
				this.period = period;
				this.fixedComputation = fixedComputation;
				this.additionalComputation = additionalComputation;
			}
		}

		private static WithholdingTaxTable getHighestOrLowest(double baseSalary,
				WithholdingTaxTable taxTableLowest, WithholdingTaxTable taxTableHighest) {
			if(baseSalary <= taxTableLowest.salaryLowerRange) {
				return taxTableLowest;
			} else if(baseSalary > taxTableHighest.salaryUpperRange) {
				return taxTableHighest;
			}
			return null;
		}
	}
}
