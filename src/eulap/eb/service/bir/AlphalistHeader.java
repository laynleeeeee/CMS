package eulap.eb.service.bir;

import java.util.List;

/**
 * Object that represent the BIR Alphalist header data
 * 

 *
 */
public interface AlphalistHeader {
	
	/**
	 * Get Filename;
	 * @return The filename of the header
	 */
	String getFileName();
	/**
	 * Convert this header object to CSV compliant based on the specification of
	 * BIR.
	 * 
	 * @return the converted String in CSV format.
	 */
	String convertHeaderToCSV();

	/**
	 * Get the alphalist schedule.
	 * @return the alphalist Schedule
	 */
	List<AlphalistSchedule> getSchedules ();
}
