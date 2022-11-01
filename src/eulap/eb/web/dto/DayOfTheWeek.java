package eulap.eb.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class that will handles Day of the week.

 *
 */
public class DayOfTheWeek {
	private final int day;
	private final String name;

	public int getDay() {
		return day;
	}

	public String getName() {
		return name;
	}

	private DayOfTheWeek (int day, String name) {
		this.day = day;
		this.name = name;
	}

	public static List<DayOfTheWeek> getDaysOfTheWeek() {
		List<DayOfTheWeek> dayOfTheWeeks = new ArrayList<DayOfTheWeek>();
		dayOfTheWeeks.add(new DayOfTheWeek(0, ""));
		dayOfTheWeeks.add(new DayOfTheWeek(1, "Sunday"));
		dayOfTheWeeks.add(new DayOfTheWeek(2, "Monday"));
		dayOfTheWeeks.add(new DayOfTheWeek(3, "Tuesday"));
		dayOfTheWeeks.add(new DayOfTheWeek(4, "Wednesday"));
		dayOfTheWeeks.add(new DayOfTheWeek(5, "Thursday"));
		dayOfTheWeeks.add(new DayOfTheWeek(6, "Friday"));
		dayOfTheWeeks.add(new DayOfTheWeek(7, "Saturday"));
		return dayOfTheWeeks;
	}

}
