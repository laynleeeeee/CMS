// Note: Some of the functions or this file will be deleted after all of the 
// modules using this will be using the server side date handler utility.

/**
 * Utility class for date. 

 */

// Global variable is set when the date is taken from the database. 
// Will be removed after the other files that uses this is fixed.
var savedDate = '';

/**
 * set the saved date format to standard.
 * @param savedDate The date from the database.
 * @returns {setSavedDate} The formatted date.
 */
function setSavedDate(savedDate) {
	this.savedDate = savedDate;
}

/**
 * Sets the date to MM/dd/yyyy.
 * @param textboxId The id of the textbox that holds the date value. 
 */
function setDefaultDate(textboxId, serverDate) {
	if (savedDate.length == 10) {
		document.getElementById(textboxId).value = savedDate;
	} else {
		document.getElementById(textboxId).value = parseServerDate (serverDate);
	}
}

/**
 * Parses the server date to the standard date which is mm/dd/yyyy.
 * 
 * @param serverDate The current date of the server.
 */
function parseServerDate (serverDate) {
	var monthString = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	var monthNumber = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
	var month = "01";
	var subString = serverDate.toString().split(" ");
		
	for (var i=0; i<monthString.length; i++) {
		if (monthString[i] == subString[1]) {
			month = monthNumber[i];
			break;
		}
	}
	return month + "/" + subString[2] + "/" + subString[3];
}

/**
 * Checks individually the format of the month, day, and year. 
 * If any or all of the three is invalid it will return false.
 * @param mn The month in integer format
 * @param day The day in integer format
 * @param yr The year in integer format
 * @returns {Boolean} True if all the three parameters are valid, otherwise false.
 */
function IsValidDate(mn, day, yr){
	var validMonth = false;
	var validDay = false;			
	var validYear = false;

	if (!isNaN(mn)) {
		if ((mn > 0 && mn <= 12)) validMonth = true;
	} 
	if (!isNaN(day)) {
		if((day > 0 && day <= 31)) validDay = true;
	} 
	if (!isNaN(yr)) {
		if (yr > 1000 && yr <= 9999) validYear = true; 
	} 
	if (validMonth == true && validDay == true && validYear == true) {
		return true;
	}
}

/**
 * Function that evaluates the date is in standard format
 * @param textboxId The id of the textbox that holds the date value. 
 * @param isSetDefault True or false, if true set the default date otherwise, clears the textbox.
 */
function evalDate(textboxId, isSetDefault) {
	var input = document.getElementById(textboxId).value;
	var datesplit = input.split("/");
	if (input.length <= 10) {
		if(datesplit.length == 3) {	
			if (!IsValidDate(datesplit[0], datesplit[1], datesplit[2])) {
				handleDate(textboxId, isSetDefault);
			}
		} else {
			handleDate(textboxId, isSetDefault);
		}
	} else {
		handleDate(textboxId, isSetDefault);
	}
}

/**
 * handles the textbox that holds the date. If the isSetDefault is true it will set the 
 * date on MM/dd/yyyy format otherwise, clears the textbox.  
 * @param textboxId The id of the textbox that holds the date value. 
 * @param isSetDefault If true set the default date otherwise, clears the date.
 */
// NOTE: This is just a sub function. Don't use it directly!!!
function handleDate(textboxId, isSetDefault) {
	if (document.getElementById(textboxId).value != "") alert("Date should be in mm/dd/yyyy format.");
	if (isSetDefault) setDefaultDate(textboxId);	
	else document.getElementById(textboxId).value = "";
}

/**
 * Format the date to MM/dd/yyyy.
 * @param date The date to be formatted (Date object)
 * @returns {String}
 */
function getFormattedDate(date) {
	  var year = date.getFullYear();
	  var month = (1 + date.getMonth()).toString();
	  month = month.length > 1 ? month : '0' + month;
	  var day = date.getDate().toString();
	  day = day.length > 1 ? day : '0' + day;
	  return month + '/' + day + '/' + year;
}
// end of date functions
