package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class that will handles time period month

 *
 */
public class TimePeriodMonth {
	private final int month;
	private final String name;

	public int getMonth() {
		return month;
	}

	public String getName() {
		return name;
	}

	public TimePeriodMonth (int month, String name) {
		this.month = month;
		this.name = name;
	}

	public static List<TimePeriodMonth> getMonths() {
		List<TimePeriodMonth> timePeriodMonths = new ArrayList<TimePeriodMonth>();
		timePeriodMonths.add(new TimePeriodMonth(1, "January"));
		timePeriodMonths.add(new TimePeriodMonth(2, "February"));
		timePeriodMonths.add(new TimePeriodMonth(3, "March"));
		timePeriodMonths.add(new TimePeriodMonth(4, "April"));
		timePeriodMonths.add(new TimePeriodMonth(5, "May"));
		timePeriodMonths.add(new TimePeriodMonth(6, "June"));
		timePeriodMonths.add(new TimePeriodMonth(7, "July"));
		timePeriodMonths.add(new TimePeriodMonth(8, "August"));
		timePeriodMonths.add(new TimePeriodMonth(9, "September"));
		timePeriodMonths.add(new TimePeriodMonth(10, "October"));
		timePeriodMonths.add(new TimePeriodMonth(11, "November"));
		timePeriodMonths.add(new TimePeriodMonth(12, "December"));
		return timePeriodMonths;
	}

}
