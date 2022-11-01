package eulap.eb.service.bir;

import java.util.List;

/**
 * An object that represent the Alpha list schedule

 *
 */
public class AlphalistSchedule {
	
	private final List<AlphalistDetail> details;
	private final AlphaListControl control;
	
	private AlphalistSchedule (List<AlphalistDetail> details, AlphaListControl control) {
		this.details = details;
		this.control = control;
	}
	
	/**
	 * Get the instance of the Alphalist Schedule
	 * @param details The Alpahlist details. 
	 * @param control The control of the alphalist. Set null if you want to disable the writing of alphalist.
	 * @return The Alphalist schedule. 
	 */
	public static AlphalistSchedule getInstance (List<AlphalistDetail> details, AlphaListControl control) {
		return new AlphalistSchedule(details, control);
	}
	
	/**
	 * Get the details data.
	 * 
	 * @return the details data
	 */
	protected List<? extends AlphalistDetail> getDetails() {
		return details;
	}

	/**
	 * Get the control data. Null to disregard control. 
	 * 
	 * @return The control data.
	 */
	protected AlphaListControl getControl() {
		return control;
	}
}
