/**
 * Javascript input utility for YBL Forms.

 */

/**
 * Checks the unit cost.
 * @param textboxId The text box.
 */
function checkUnitCost(textboxId) {
	var amount = $("#" + textboxId).val();
    var amountLength = amount.length;
    var cntDec = 0;
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
    $("#" + textboxId).val(finalAmount);
}

/**
 * Append zeros to the number.
 * @param value The number to which the zeros will be appended.
 * @param maxDigits The maximum digits.
 */
function appendZero (value, maxDigits) {
	var numberOfZeros = maxDigits - $.trim(value).length;
	var zeros = "";
	for (var i=0; i<numberOfZeros; i++)
		zeros += "0";
	return value + zeros;
}

/**
 * Insert comma separator to every number that has more than 3 digits.
 * @param value
 * @returns The comma separated number.
 */
function insertCommaSeparator (value) {
	while (/(\d+)(\d{3})/.test(value.toString()))
		value = value.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
	return value;
}

/**
 * Format the unit cost into 6-digit decimal with comma separator.
 * @param value The value.
 */
function formatUnitCost (value) {
	value = value.replace(/\,/g,"");
	value = parseFloat(value).toFixed(6);
	var splittedValue = value.split(".");
	var wholeDigit = isNaN(splittedValue[0]) ? "0" : 
		insertCommaSeparator(splittedValue[0]);
	var decimalDigit = isNaN(splittedValue[1]) ? appendZero ("0", 6) : (splittedValue[1]);
	return wholeDigit + "." + decimalDigit;
}

/**
 * Sets the value of the textbox = the unit cost. 
 * @param textBoxId The uniqued id of the textbox element.
 */
function setUnitCost (textBoxId) {
	var value = formatUnitCost($.trim($("#" + textBoxId).val()));
	$("#" + textBoxId).val(value);
}