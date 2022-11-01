package eulap.eb.service.bir;

/**
 * Object that represent the BIR Alphalist details data.
 * 

 *
 */
public interface AlphalistDetail {
	/**
	 * Convert this detail object to CSV compliant based on the specification of
	 * BIR.
	 * 
	 * @return the converted String in CSV format.
	 */
	String convertDetailToCSV();
}
