package eulap.eb.service.bir;

/**
 * Object that represent the BIR Alphalist control
 * 

 *
 */
public interface AlphaListControl {
	/**
	 * Convert this header Control to CSV compliant based on the specification of
	 * BIR.
	 * 
	 * @return the converted String in CSV format.
	 */
	String convertControlToCSV();
}
