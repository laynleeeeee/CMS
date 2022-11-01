package eulap.common.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;

import eulap.common.domain.Domain;
import eulap.eb.validator.ValidatorMessages;

/**
 * Utility class for date format and computation.

 *
 */
public class DateUtil {
	public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	public static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
	public static final String JR_FN_DATE_FORMAT = "mm_dd_yyyy_HH_mm_ss";
	public static final String DATE_FORMAT_REGEX = "(0[1-9]|1[012])[/](0[1-9]|[12][0-9]|3[01])[/]([0-9]{4})";
	public static final String MONTH_DAY_FORMAT = "MMM dd";
	public static final String MEDIUM_DATE_FORMAT = "MMM dd, yyyy";
	public static final String FULL_DATE_FORMAT = "MMMMM dd, yyyy";
	public static final int MIN_YEAR_SUB = 5;
	public static final int MAX_YEAR_ADD = 5;

	private static final String TIME_FORMAT = "hh:mm a";
	/**
	 *  The non working days. TODO: This should be moved to the database.
	 */
	public static List<Integer> NON_WORKING_DAYS = new ArrayList<Integer>();

	static {
		NON_WORKING_DAYS.add(Calendar.SATURDAY);
		NON_WORKING_DAYS.add(Calendar.SUNDAY);
	}
	/**
	 * Get the day difference. 
	 * @param from date from
	 * @param to date to
	 * @return
	 */
	public static int getDayDifference (Date from, Date to) {
		long diff = to.getTime() - from.getTime();
		// millisec / sec/ min/ day
		return (int) (diff/ (1000 * 60 * 60 * 24));
	}

	/**
	 * Formats the date to sql standard format.
	 * @param date The date to be formatted.
	 * @return The formatted date.
	 */
	public static String formatToSqlDate(Date date) {
		return formatDate(SQL_DATE_FORMAT, date);
	}
		
	/**
	 * Removes the time from date.
	 * @param date The date to which its time will be removed.
	 * @return date that time is 0.
	 */
	public static Date removeTimeFromDate (Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		date = gc.getTime();
		return gc.getTime();
	}
	
	/**
	 * Formats the sql date to java date.
	 * @param sqlDate The sql date that to be formatted to java date.
	 * @return javaDate The date in java format.
	 * @throws ParseException 
	 */
	public static Date sqlDateToJavaDate (Date sqlDate) {
		Date javaDate = removeTimeFromDate(new Date());
		SimpleDateFormat sqlFormat = new SimpleDateFormat(SQL_DATE_FORMAT);
		try {
			javaDate = (Date) sqlFormat.parse(formatToSqlDate(sqlDate));
		} catch (ParseException pe) {
			// Do nothing.
		}
		return javaDate;
	}
		
	/**
	 * Register the acceptable date format
	 * in the {@link WebDataBinder#registerCustomEditor(Class, java.beans.PropertyEditor)}.
	 * This will allow the field to be empty string in the view.
	 * <br>
	 * Date Format: {@value #DATE_FORMAT}
	 * 
	 * @param binder 
	 */
	public static void regesterDateFormat (WebDataBinder binder){
		 SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
         dateFormat.setLenient(false);
         binder.registerCustomEditor(Date.class, new CBSCustomDateEditor(dateFormat));
	}

	/**
	 * Register the acceptable date format with time
	 * in the {@link WebDataBinder#registerCustomEditor(Class, java.beans.PropertyEditor)}.
	 * This will retain the time when formatting the date.
	 * <br>
	 * Date Format: {@value #DATE_TIME_FORMAT}
	 * 
	 * @param binder 
	 */
	public static void registerDateAndTimeFormat (WebDataBinder binder){
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CBSCustomDateEditor(dateFormat));
	}

	private static class CBSCustomDateEditor extends CustomDateEditor {
		public CBSCustomDateEditor(DateFormat dateFormat) {
			super(dateFormat, true);
		}
		
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (!DateUtil.isvalidDateFormat(text))
				text = null;
			super.setAsText(text);
		}
	}

	/**
	 * Check if the string has a valid format.
	 * @param date The string to be validated.
	 * @return true if valid, otherwise false.
	 */
	public static boolean isvalidDateFormat (String date) {
		if (date == null || date.isEmpty())
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        try {
			dateFormat.parse(date);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Parse the date using {@value #DATE_FORMAT}. 
	 * 
	 * @param date The string date to be parsed
	 * @return The parsed date. Null for invalid format.
	 */
	public static Date parseDate (String date) {
		return parseDate(date, DATE_FORMAT);
	}

	/**
	 * Parse the date using {@value #DATE_FORMAT}. 
	 * 
	 * @param date The string date to be parsed
	 * @return The parsed date. Null for invalid format.
	 */
	public static Date parseDate (String date, String format) {
		if (!isvalidDateFormat(date))
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Format the date to MM/dd/yyyy
	 * @param date The date to be formatted. 
	 * @return The string formatted date. 
	 */
	public static String formatDate (Date date) {
		return formatDate(DATE_FORMAT, date);
	}

	/**
	 * Format the date to MM/dd/yyyy h:mm a
	 * @param date The date to be formatted.
	 * @return The string formatted date.
	 */
	public static String formatDateWithTime (Date date) {
		String dateWithTimeFmt = DATE_FORMAT + " " + TIME_FORMAT;
		return formatDate(dateWithTimeFmt, date);
	}

	private static String formatDate (String format, Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date).toString();
	}
	
	/**
	 * Convert the integer month to its equivalent name.
	 * 1 = January
	 * 2 = February
	 * 3 = March
	 * 4 = April
	 * 5 = May
	 * 6 = June
	 * 7 = July
	 * 8 = August
	 * 9 = September
	 * 10 = October
	 * 11 = November
	 * 12 = December 
	 * @param month The number equivalent of month.
	 * @return The equivalent name of the month.
	 */
	public static String convertToStringMonth (int month) {
		switch (month) {
			case 1: return "January";
			case 2: return "February";
			case 3: return "March";
			case 4: return "April";
			case 5: return "May";
			case 6: return "June";
			case 7: return "July";
			case 8: return "August";
			case 9: return "September";
			case 10: return "October";
			case 11: return "November";
			case 12: return "December";
			default: return "";
		}
	}
		
	/**
	 * Add days to date.
	 * @param date The date that will be incremented.
	 * @param numOfDays The number of days desired to be added to the date.
	 * @return The date with increment.
	 */
	public static Date addDaysToDate (Date date, int numOfDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, numOfDays);
		date = cal.getTime();
		return date;
	}

	/**
	 * Subtract days to date.
	 * @param date The date that will be subtracted. 
	 * @param numOfDays The number of days to be subtracted to the date.
	 * @return The subtracted date.
	 */
	public static Date deductDaysToDate(Date date, int numOfDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - numOfDays);
		return cal.getTime();
	}

	/**
	 * Get the time from date "7:22 AM".
	 * @param date The date.
	 * @return The time.
	 */
	public static String getTimeFromDate (Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		return sdf.format(date);
	}
	
	/**
	 * Get the minutes of the date.
	 * @param date The date.
	 * @return The minutes of the date.
	 */
	public static int getMinute (Date date) {
		int minute = 0;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return  cal.get(Calendar.MINUTE);
		}
		return minute;
	}
	
	/**
	 * Get the hour of the date in military time format.
	 * @param date
	 * @return The hours of the date.
	 */
	public static int getHourOfDay (Date date) {
		int hourOfDay = 0;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return  cal.get(Calendar.HOUR_OF_DAY);
		}
		return hourOfDay;
	}

	/**
	 * Get the day of the month of the date.
	 */
	public static int getDay (Date date) {
		if (date == null)
			return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return  cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get the year of the date.
	 * @param date The date, optional field.
	 * @return Returns the year of the date, otherwise the current year.
	 */
	public static int getYear (Date date) {
		Calendar cal = Calendar.getInstance();
		if(date != null) {
			cal.setTime(date);
		}
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Get the month of the date.
	 * @param date The date, optional field.
	 * @return Returns the month of the date, otherwise the current month.
	 */
	public static int getMonth (Date date) {
		Calendar cal = Calendar.getInstance();
		if(date != null) {
			cal.setTime(date);
		}
		return cal.get(Calendar.MONTH);
	}

	/**
	 * Convert ms(milliseconds) to hour.
	 * @param milliSeconds the ms to be converted. 
	 * @return The converted ms to hour.
	 */
	public static double convertToHour (double milliSeconds) {
		return (milliSeconds / (1000*60*60));
	}
	
	/**
	 * Validates if the date is working day.
	 * @param date The date to be evaluated.
	 * @param nonWorkingDaysOfWeek The non working day of the week.
	 * @return true if working day otherwise false.
	 */
	public static boolean isWorkingDay (Date date, List<Integer> nonWorkingDaysOfWeek ) {
		Calendar tmpCal = Calendar.getInstance();
		tmpCal.setTime(date);
		int dayOfTheWeek = tmpCal.get(Calendar.DAY_OF_WEEK);
		return !nonWorkingDaysOfWeek.contains(dayOfTheWeek);
	}
	
	/**
	 * Append the time to date.
	 * The default time of date is removed to avoid adding it
	 * with desired time.
	 * @param strTime The time string format.
	 * @param date The date.
	 * @return The date with appended time.
	 */
	public static Date appendTimeToDate (String strTime, Date date) {
		String[] timeArr = strTime.trim().split(":");
		String period = timeArr[1].replaceAll("\\d", "").trim();
		Integer hour = Integer.valueOf(timeArr[0].trim().replaceAll("[^\\d.]", ""));
		if(hour == 12){
			// Check if time is midnight or noon.
			// hour = 0 if midnight, otherwise hour = 12.
			hour = period.equalsIgnoreCase("AM") ? 0 : 12;
		} else
			hour = period.equalsIgnoreCase("PM") ? hour + 12 : hour;
		Integer minute = Integer.valueOf(timeArr[1].trim().replaceAll("[^\\d.]", ""));
		Calendar cal = Calendar.getInstance();
		cal.setTime(removeTimeFromDate(date));
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	/**
	 * Set the date range.
	 * <br>Format if valid start and end date: MM/dd/yyyy to MM/dd/yyyy
	 * @param startDate start date of the date range.
	 * @param endDate end date of the date range.
	 */
	public static String setUpDate(String startDate, String endDate) {
		boolean isValidStartDate = false;
		if (isvalidDateFormat(startDate))
			isValidStartDate = true;
		boolean isValidEndDate = false;
		if (isvalidDateFormat(endDate))
			isValidEndDate = true;
		String strDate = "";
		if (isValidStartDate && isValidEndDate)
			strDate = startDate + " to " + endDate;
		else if(isValidStartDate || isValidEndDate)
			strDate = isValidStartDate ? startDate : endDate;
		return strDate;
	}

	/**
	 * Formats the date to default format for jasper report filename.
	 * @param date The date to be formatted.
	 * @return The formatted date.
	 */
	public static String formatToJrFileNameDate(Date date){
		return formatDate(JR_FN_DATE_FORMAT, date);
	}

	/**
	 * Format the time of the date.
	 * @param date The date to be formatted.
	 * @param hour The hour (24 hour format).
	 * @param minute The minute.
	 * @param second The second.
	 * @return Formatted time of date.
	 */
	public static Date setTimeOfDate (Date date, int hour, int minute, int second) {
		if(date == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}

	/**
	 * Extract the list of dates in MM/dd/yyyy format from a string.
	 * @param strDate The string to be checked.
	 * @return List of dates.
	 */
	public static List<String> extractDates(String strDate) {
		List<String> extractedDates = new ArrayList<String>();
		if (strDate != null) {
			Matcher m = Pattern.compile(DATE_FORMAT_REGEX).matcher(strDate);
			while(m.find()) {
				extractedDates.add(m.group());
			}
		}
		return extractedDates;
	}

	/**
	 * Format the date to MMMMM dd, yyyy format.
	 * @param date The date to be formatted.
	 * @param isUpperCase true if month is in upper case, otherwise false.
	 * @return The formatted date.
	 */
	public static String formatToText(Date date, boolean isUpperCase) {
		String formattedMonth = formatDate("MMMMM", date);
		if(isUpperCase)
			formattedMonth = formattedMonth.toUpperCase();
		return formattedMonth+" "+formatDate("dd, yyyy", date);
	}

	/**
	 * Set the created date of the domain.
	 * @param domain The domain object.
	 * @param createdDate The created date.
	 */
	public static void setCreatedDate(Domain domain, Date createdDate) {
		domain.setCreatedDate(createdDate);
	}
	
	/**
	 * Format the date to mmm dd. Ex. Jan 05
	 * @param date The date to be formatted.
	 * @return The formatted date.
	 */
	public static String formatToMonthDay (Date date) {
		return formatDate(MONTH_DAY_FORMAT, date).toString();
	}

	/**
	 * Format the date to medium format (MMM dd, yyyy). Example: Jun 29, 2014
	 * @param date The date to be formatted.
	 * @return The formatted date.
	 */
	public static String formatToMediumDate (Date date) {
		return formatDate(MEDIUM_DATE_FORMAT, date).toString();
	}

	/**
	 * Format the date to medium format (MM/dd/yyyy). Example: 06/29/2014
	 * @param date The date to be formatted.
	 * @return The formatted date.
	 */
	public static String formatToSimpleDate (Date date) {
		return formatDate(DATE_FORMAT, date).toString();
	}

	/**
	 * Format the date to full format (MMMMM dd, yyyy). Example: October 20, 2021
	 * @param date The date to be formatted.
	 * @return The formatted date.
	 */
	public static String formatToFullDate (Date date) {
		return formatDate(FULL_DATE_FORMAT, date).toString();
	}

	/**
	 * Removes the year from date.
	 * @param date The date to which its year will be removed.
	 * @return date that year is 0
	 */
	public static Date removeYearFromDate (Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.set(Calendar.YEAR, 0);
		return gc.getTime();
	}

	/**
	 * Removes the year and time from date.
	 * @param date The date to which its year and year will be removed.
	 * @return date that year and time is 0
	 */
	public static Date removeYearAndTimeFromDate (Date date) {
		Date dateWithoutTime = removeTimeFromDate(date);
		return removeYearFromDate(dateWithoutTime);
	}

	/**
	 * Compute the age based from the date of birth.
	 * @param birthDate The date of birth.
	 * @return the computed age.
	 */
	public static int computeAge(Date birthDate) {
		Calendar dateOfBirth = Calendar.getInstance();
		dateOfBirth.setTime(birthDate);
		Calendar currentDate = Calendar.getInstance();
		//Subtract the current year to the date of birth.
		int age = currentDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
		//Performing adjustments of the age based on the month and day.
		if((currentDate.get(Calendar.MONTH) < dateOfBirth.get(Calendar.MONTH))
				|| (currentDate.get(Calendar.MONTH) == dateOfBirth.get(Calendar.MONTH)
						&& currentDate.get(Calendar.DAY_OF_MONTH) < dateOfBirth.get(Calendar.DAY_OF_MONTH))) {
			//Subtract 1 from age if the current month and day is less than the month and day of the date of birth.
			age--;
		}
		return age;
	}

	/**
	 * Process the year of the date base from the reference date.
	 * <br>Two scenarios to process the date:
	 * <br> 1. If month and day of the date is greater than or equal
	 *  to the reference date, set year from the reference.
	 * <br> 2. Otherwise, add 1 to the year. <br>
	 * <br>Example: refDate = June 12, 2014
	 * <br> 1. date = Aug 1, 2013 returns: Aug 1, 2014
	 * <br> 2. date = Feb 7, 2014 returns: Feb 4, 2015
	 * @param date The date to be processed.
	 * @param refDate The reference date.
	 * @return The processed date.
	 */
	public static Date processDateFromReference(Date date, Date refDate) {
		if(date == null) {
			return date;
		}
		Calendar referenceDate = Calendar.getInstance();
		referenceDate.setTime(refDate);

		int year = referenceDate.get(Calendar.YEAR);
		int refDayOfMonth = referenceDate.get(Calendar.DAY_OF_MONTH);

		Calendar processedDate = Calendar.getInstance();
		processedDate.setTime(date);

		//Compare the months
		if(processedDate.get(Calendar.MONTH) >= referenceDate.get(Calendar.MONTH)) {
			//Date has the same month from the reference.
			if(processedDate.get(Calendar.MONTH) == referenceDate.get(Calendar.MONTH)) {
				//Day of month is lesser than the day from the reference.
				if(processedDate.get(Calendar.DAY_OF_MONTH) < refDayOfMonth) {
					//Add 1 year.
					year++;
				}
			}
		} else {
			//Month of date is lesser than the reference
			year++;
		}

		//Set the year of the date.
		processedDate.set(Calendar.YEAR, year);
		return processedDate.getTime();
	}
	
	/**
	 * Remove month/s from date.
	 * @param date The date to which month/s will be deducted from
	 * @param numOfMonths The number of months to be deducted.
	 * @return The date removed with month/s
	 */
	public static Date deductMonthsToDate (Date date, int numOfMonths) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(DateUtil.removeTimeFromDate(date)); 
		c.add(Calendar.MONTH, -numOfMonths);
		return c.getTime();
	}

	/**
	 * Remove year/s from date.
	 * @param date The date to which year/s will be deducted from
	 * @param numOfMonths The number of months to be deducted.
	 * @return The date removed with year/s
	 */
	public static Date deductYearsToDate (Date date, int numOfMonths) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(DateUtil.removeTimeFromDate(date)); 
		c.add(Calendar.YEAR, -numOfMonths);
		return c.getTime();
	}

	/**
	 * Add month/s to date.
	 * @param date The date to which month/s will be added to.
	 * @param numOfMonths The number of months to be added.
	 * @return The date with additional month/s
	 */
	public static Date addMonthsToDate (Date date, int numOfMonths) {
		Calendar c = Calendar.getInstance();
		c.setTime(DateUtil.removeTimeFromDate(date));
		c.add(Calendar.MONTH, numOfMonths);
		return c.getTime();
	}

	/**
	 * Checks if the number of days is valid compared to the month of the date.
	 * If  number of days is negative, return true.
	 * @param numberOfDays The number of days to be checked.
	 * @param date The date to which the month will be compared.
	 * @return True if contains number of days > days in month, otherwise false.
	 */
	public static boolean isInvNumOfDaysPerMonth (int numberOfDays, Date date) {
		if (numberOfDays < 0) {
			return true;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (numberOfDays > daysInMonth) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the year and month of d1 is the same with after d2.
	 * @return True if d1.get(Calendar.YEAR) <= d2.get(Calendar.YEAR)
	 * and d1.get(Calendar.MONTH) <= d2.get(Calendar.MONTH).
	 */
	public static boolean isD1BeforeOrEqualD2 (Date d1, Date d2) {
		if(d1.before(d2) || d1.equals(d2)){
			return true;
		}
		return false;
	}

	/**
	 * Set hour to date.
	 * @param date The date to which hour will be set to
	 * @param hour The hour to be set.
	 * @return The date with set hour.
	 */
	public static Date setHourToDate(Date date, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(date));
		cal.set(Calendar.HOUR, hour);
		return cal.getTime();
	}

	/**
	 * Add hour/s to date.
	 * @param date The date to which hour/s will be added to.
	 * @param numOfHours The number of hours to be added.
	 * @return The date with additional hour/s
	 */
	public static Date addHourToDate (Date date, int numOfHours) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, numOfHours);
		return c.getTime();
	}

	/**
	 * Get the first day of the month given a date.
	 * @param date The date basis.
	 * @return The date.
	 */
	public static Date getFirstDayOfMonth (Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(date));
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * Get the last day of the month given a date.
	 * @param date The date basis.
	 * @return The date.
	 */
	public static Date getEndDayOfMonth (Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(date));
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setTimeToEndofDay(cal);
		return cal.getTime();
	}

	private static void setTimeToEndofDay(Calendar calendar) {
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	}

	/**
	 * Get the date given month and year.
	 * @param year The year.
	 * @param month The month.
	 * @return The date.
	 */
	public static Date getDateByYearAndMonth (int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * Get the hours difference.
	 * @param startTime The start time.
	 * @param endTime The ending time.
	 * @param breakBetween The break time in hours between the start time and end time.
	 * @return Difference of start and end date.
	 * @throws ParseException
	 */
	public static Double getHoursDiff(String startTime, String endTime, Double breakBetween) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date1 = format.parse(startTime);
		Date date2 = format.parse(endTime);
		long diff = date2.getTime() - date1.getTime();
		double hoursDiff = Double.valueOf(diff) / (60 * 60 * 1000) % 24;
		if (breakBetween != null) {
			hoursDiff = hoursDiff - breakBetween;
		}
		return hoursDiff;
	}

	/**
	 * Get the list of payroll dates range.
	 * @param startdate The start date.
	 * @param enddate The end date.
	 * @return The list of payroll dates.
	 */
	public static List<Date> getDatesFromRange(Date startdate, Date enddate) {
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);
		while (calendar.getTime().before(enddate)) {
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DATE, 1);
		}
		dates.add(calendar.getTime());
		return dates;
	}

	/**
	 * Get the length of dates. SAMPLE(1 year(s) 2 months(s) 3 week(s) 1 day(s)).
	 * @param startDate The start date.
	 * @param endDate The end date.
	 * @return The string format for length of dates.
	 */
	public static String getLengthOfDates(Date startDate, Date endDate){
		Calendar calStart = new GregorianCalendar();
		calStart.setTime(startDate);
		Calendar calEnd = new GregorianCalendar();
		calEnd.setTime(endDate);
		int years = calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR);
		int months = calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);
		int weeks = calEnd.get(Calendar.WEEK_OF_MONTH) - calStart.get(Calendar.WEEK_OF_MONTH);
		int days = calEnd.get(Calendar.DAY_OF_WEEK) - calStart.get(Calendar.DAY_OF_WEEK);
		if((calEnd.get(Calendar.MONTH) < calStart.get(Calendar.MONTH))
				|| (calEnd.get(Calendar.MONTH) == calStart.get(Calendar.MONTH)
						&& calEnd.get(Calendar.DAY_OF_MONTH) < calStart.get(Calendar.DAY_OF_MONTH))) {
			years--;
		}
		if(months < 0){
			months = months + 12;
		}
		if(weeks < 0){
			weeks = weeks + calStart.getActualMaximum(Calendar.WEEK_OF_MONTH);
		}
		if(days < 0){
			days = days + calStart.getActualMaximum(Calendar.DAY_OF_WEEK);
		}
		String lengthOfDates = "";
		if(years > 0){
			lengthOfDates += years +ValidatorMessages.getString("DateUtil.0");
		}
		if(months > 0){
			lengthOfDates += months +ValidatorMessages.getString("DateUtil.1");
		}
		if(weeks > 0){
			lengthOfDates += weeks +ValidatorMessages.getString("DateUtil.2");
		}
		if(days > 0){
			lengthOfDates += days +ValidatorMessages.getString("DateUtil.3");
		}
		return lengthOfDates;
	}

	/**
	 * Add minute/s to date.
	 * @param date The date to which minute/s will be added to.
	 * @param numOfMinutes The number of minute to be added.
	 * @return The date with additional minute/s
	 */
	public static Date addMinuteToDate (Date date, int numOfMinutes) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, numOfMinutes);
		return c.getTime();
	}

	/**
	 * Create an instance of date given the parameters year, month, and day.
	 * @param year The year to be set.
	 * @param month The month to be set.
	 * @param day The day to be set.
	 * @return The date instance.
	 */
	public static Date createDate(int year, int month, int day) {
		return new GregorianCalendar(year, month, day).getTime();
	}

	/**
	 * Get the List of days in a month given the year and month.
	 * @param year The year
	 * @param month The month
	 * @return The days in a month
	 */
	public static List<Integer> getDaysInMonth(int year, int month) {
		List<Integer> days = new ArrayList<>();
		int maxDays = getMaxDaysInMonth(year, month);
		for (int i=1; i<=maxDays; i++) {
			days.add(i);
		}
		return days;
	}

	/**
	 * Get the maximum number of days in a month given the year and month.
	 * @param year The year
	 * @param month The month
	 * @return The maximum number of days in a month.
	 */
	public static int getMaxDaysInMonth(int year, int month) {
		return new GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get the maximum number of weeks in a month given the year and month.
	 * @param year The year
	 * @param month The month
	 * @return The maximum number of weeks in a month.
	 */
	public static int getMaxWeeksInMonth(int year, int month) {
		return new GregorianCalendar(year, month, 1).getActualMaximum(Calendar.WEEK_OF_MONTH);
	}

	/**
	 * Initialize the list of years
	 * @return The list of years
	 */
	public static List<Integer> initYears() {
		return initYears(MIN_YEAR_SUB, MAX_YEAR_ADD);
	}

	/**
	 * Initialize the list of years
	 * @param minYear The minimum number of years to be subtracted to the current year.
	 * @param maxYear The maximum number of years to be added to the current year.
	 * @return The list of years.
	 */
	public static List<Integer> initYears(int minYear, int maxYear) {
		if (maxYear < minYear) {
			throw new RuntimeException("Invalid parameters, maxYear must not be less than minYear");
		}
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		List<Integer> years = new ArrayList<>();
		for (int i=currentYear-minYear; i<=currentYear+maxYear; i++) {
			years.add(i);
		}
		return years;
	}

	/**
	 * Initialize the list of years
	 * @param startYear The start year of the year list to the current year + MAX_YEAR_ADD.
	 * @return The list of years.
	 */
	public static List<Integer> initYears(int startYear) {
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		List<Integer> years = new ArrayList<>();
		for (int i=startYear; i<=currentYear+MAX_YEAR_ADD; i++) {
			years.add(i);
		}
		return years;
	}

	/**
	 * Initialize the list of years
	 * @param fromYear The starting year to add in the list.
	 * @param toYear The last year to add in the list.
	 * @return The list of years.
	 */
	public static List<Integer> rangeYears(int fromYear, int toYear) {
	    List<Integer> list = new ArrayList<Integer>();
	    for (int i = fromYear; i <= toYear; ++i)
	        list.add(i);
	    return list;
	}

	/**
	 * Convert local date to date util.
	 * @param localDate The local date.
	 * @return The converted date.
	 */
	public static Date convertLocalDateToDate(LocalDate localDate) {
		if(localDate != null) {
			return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}

	/**
	 * Get the minutes difference two dates.
	 * @param startDate The start date.
	 * @param endDate The end date.
	 * @return The difference in minutes.
	 */
	public static long getMinutesDiff(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();
		long minutes = TimeUnit.MILLISECONDS.toMinutes(diff); 
		return minutes;
	}

	/**
	 * Set the time of date by string format HH:mm
	 * @param time The time in string.
	 * @param date The date.
	 */
	public static Date setTime(String time, Date date) {
		String tArr[] = time.split(":");
		int hour = Integer.valueOf(tArr[0]);
		int min = Integer.valueOf(tArr[1]);
		Calendar cal = Calendar.getInstance();
		cal.setTime(removeTimeFromDate(date));
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, min);
		return cal.getTime();
	}

	/**
	 * Get the name of the month given the month number.
	 * @param monthNum The month number.
	 * @return The month name.
	 */
	public static String getMonthName(int monthNum) {
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		if (monthNum >= 0 && monthNum <= 11 ) {
			return months[monthNum];
		}
		throw new RuntimeException("Invalid month.");
	}

	/**
	 * Get the name of the month given the date.
	 * @param date The date.
	 * @return The month name.
	 */
	public static String getMonthName(Date date) {
		return getMonthName(getMonth(date));
	}

	/**
	 * Get the name of the quarter given the quarter number.
	 * @param quarterNum The quarter number.
	 * @return The quarter name.
	 */
	public static String getQuarterName(int quarterNum) {
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getShortMonths();
		switch(quarterNum) {
		case 0:
			return months[0] + " - " + months[2];
		case 1:
			return months[3] + " - " + months[5];
		case 2:
			return months[6] + " - " + months[8];
		case 3:
			return months[9] + " - " + months[11];
		}
		throw new RuntimeException("Invalid quarter.");
	}

	/**
	 * Get the payroll date range.
	 * @param startDate The start date.
	 * @param endDate The end date.
	 * @return The format payroll date range.
	 */
	public static String formatPayrollDateRange(Date startDate, Date endDate) {
		int startYear = getYear(startDate);
		int endYear = getYear(endDate);
		boolean isSameYear = startYear == endYear;

		int startMonth = getMonth(startDate);
		int endMonth = getMonth(endDate);
		boolean isSameMonth = startMonth == endMonth;

		String startMonthAndDay = getMonthName(startDate) + " " + getDay(startDate);
		if (isSameYear && isSameMonth) {
			return startMonthAndDay + " - " + getDay(endDate) + ", " + startYear;
		} else if (isSameYear) {
			return startMonthAndDay + " - " + getMonthName(endDate) + " " + getDay(endDate) + ", " + startYear;
		} else {
			return startMonthAndDay + ", " + startYear + " - " + getMonthName(endDate) + " " + getDay(endDate) + ", " + endYear;
		}
	}

	/**
	 * Get the number of months.
	 * @param dateFrom the start date.
	 * @param dateTo the end date.
	 * @return the number of months.
	 */
	public static int getNumberOfMonths (Date dateFrom, Date dateTo) {
		Calendar startDate = new GregorianCalendar();
		Calendar endDate = new GregorianCalendar();
		startDate.setTime(dateFrom);
		endDate.setTime(dateTo);
		int noMonths = (endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH));
		return noMonths + 1;
	}
}
