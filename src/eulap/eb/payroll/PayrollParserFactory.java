package eulap.eb.payroll;

import java.util.ArrayList;
import java.util.List;

/**
 * A factor class for Payroll parser {@link PayrollParser}.


 *
 */
public class PayrollParserFactory {
	private static final List<PayrollParser> parsers = new ArrayList<PayrollParser>();

	static {
		parsers.add(new JSRParser());
		parsers.add(new AmaxParser());
		parsers.add(new FortressParser());
		parsers.add(new GenericParser());
		parsers.add(new CscParser());
		parsers.add(new TbcParser());
		parsers.add(new ZkTecoIno1AParser());
		parsers.add(new RealandParser());
		parsers.add(new ZkTeco628CAParser());
		parsers.add(new IronValleyParser());
		parsers.add(new EulapV2Parser());
		parsers.add(new SSCParser());
		parsers.add(new G5544Parser());
		parsers.add(new GRVFCParser());
	}

	/**
	 * Get the biometric parser.
	 * @param biometricModelId
	 * @return The biometric parser.
	 */
	public static PayrollParser getParser(int biometricModelId) {
		for (PayrollParser parser : parsers) {
			if (parser.getBiometricModelId() == biometricModelId) {
				return parser; 
			}
		}
		throw new RuntimeException("Unkown biometric model : " + biometricModelId);
	}
}
