/**
 * Utility class that handles data input checking and processing. 

 */

/**
 * Sets the textbox amount value to default. 
 * 
 * @param textboxId The id of the textbox that holds the amount.
 * @param defaultAmount The default amount.
 */
function setDefaultAmount(textboxId, defaultAmount) {
	if ($("#" + textboxId).val().length == 0) 
		$("#" + textboxId).val(defaultAmount);
}

/**
 * Rounds off a valid decimal input to 2 decimal places.
 * @param value The value to be rounded off.
 * @returns {Number} The rounded off value.
 */
function roundTo2Decimal(value) {
    return Math.floor(100 * value) / 100.0;
}

/**
 * Checks if the input is in correct number format otherwise, 
 * remove the invalid character/s. 
 * @param textboxId The id of the textbox that holds the amount.
 */
function checkAndSetDecimal(textboxId) {
	var amount = $("#" + textboxId).val();
    var amountLength = amount.length;
    var cntDec = 0;
    var lengthFirstDec = 0;
    var finalAmount = "";
    
    for (var i=0; i<amountLength; i++) {
        var char = amount.charAt(i);
        if (!isNaN(char) || (char == ".")) {
            if (char == ".")
                cntDec++;
            if ((char == ".") && (cntDec == 1)) {
                lengthFirstDec = i + 1;
                finalAmount += char;
            }
            if (!isNaN(char))
                finalAmount += char;
        }
    }
    if ((cntDec == 1) && (amountLength > (lengthFirstDec + 2)))
        finalAmount = roundTo2Decimal(finalAmount);
    $("#" + textboxId).val(finalAmount);
}

/**
 * Checks if the input is in correct number format otherwise, 
 * remove the invalid character/s. It allows negative values.
 * @param textboxId The id of the textbox that holds the amount.
 */
function checkAndSetDecimalWithNeg(txtObj) {
	var amount = $(txtObj).val();
    var amountLength = amount.length;
    var cntDec = 0;
    var lengthFirstDec = 0;
    var finalAmount = "";

    for (var i=0; i<amountLength; i++) {
        var char = amount.charAt(i);
        if (!isNaN(char) || (char == ".") || (char == "-")) {
			if (char == "-" && i==0)
				finalAmount += char;
            if (char == ".")
                cntDec++;
            if ((char == ".") && (cntDec == 1)) {
                lengthFirstDec = i + 1;
                finalAmount += char;
            }
            if (!isNaN(char))
                finalAmount += char;
        }
    }
    if ((cntDec == 1) && (amountLength > (lengthFirstDec + 2)))
        finalAmount = roundTo2Decimal(finalAmount);
    $(txtObj).val(finalAmount);
}

/**
 * Function that allows only numeric.
 * @param textboxId The id of the textbox that holds the value.
 */
function inputOnlyNumeric(textboxId) {
	var amount = $("#" + textboxId).val();
	var amountLength = amount.length;
	var finalAmount = "";

	 for (var i=0; i<amountLength; i++) {
		 var char = amount.charAt(i);
		 if (!isNaN(char)) {
            finalAmount += char;
		 }
	 }
	 $("#" + textboxId).val(finalAmount);
}

/**
 * Function that allows only numeric with dot.
 * @param textboxId The textbox id.
 */
function onlyNumbericWithDot(textboxId) {
	$("#" + textboxId).keyup(function(){
	    var val = $(this).val();
	    if(isNaN(val)){
         val = val.replace(/[^0-9\.]/g,'');
         if(val.split('.').length>2)
             val = val.replace(/\.+$/,"");
	    }
	    $(this).val(val);
	});
}

/**
 * Sets the text box string length based on the desired max length.
 * @param textboxId The id of the textbox that holds the amount.
 * @param desiredLength The desired length;
 */
function setTextboxMaxChar (textboxId, desiredLength) {
	var text = $("#" + textboxId).val();
	var textLength = text.length;

	if (textLength > desiredLength)
		$("#" + textboxId).val(text.substring(0, textLength - 1));
}

/**
 * Remove white spaces in string.
 * @param string The value to be evaluated.
 */
function removeSpace(string) {
	 return string.split(' ').join('');
}

/**
 * Add comma to the number.
 * @param textboxId The id of the text box.
 */
function commaSeparateNumber(textboxId){
	var val = $(textboxId).val();
	while (/(\d+)(\d{3})/.test(val.toString())){
		val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
	}
	$(textboxId).val(val);
}

/**
 * Format the number value
 * @param value The number value
 * @param precision The number of decimal places to be shown
 * @returns The formatted value
 */
function formatDecimalPlaces(value, precision) {
	if (typeof precision == "undefined") {
		precision = 2;
	}
	value = accounting.unformat(value);
	var tmp = value.toString();
	var dec = tmp.split(".")[1];
	if (typeof dec != "undefined" && dec.length > 2) {
		var lastDigit = parseInt(dec[dec.length - 1]);
		var secondLastDigit = parseInt(dec[dec.length - 2]);
		if (lastDigit == 5 && secondLastDigit >= 5) {
			var power = Math.pow(10, precision);
			value = (Math.ceil(accounting.unformat(value) * power) / power).toFixed(precision);
		}
	}
	return accounting.formatNumber(value, precision);
}
