/**
 * Utility class that handles formatting. 

 */

/**
 * Evaluate existing stocks if has decimal value.
 * @param existingStocks The existing stock value to be evaluated.
 * @returns formattedES The formatted existing stocks value.
 */
function formatExistingStocks(existingStocks) {
	var parsedExistingStock = parseFloat(existingStocks);
	if(parsedExistingStock % 1 != 0){
		// Return Existing Stock with two decimal places.
		var formattedES = Number(existingStocks).toFixed(2);
		return formattedES;
	} else {
		// Return Existing Stock as a whole number.
		var formattedES = Number(existingStocks).toFixed();
		return formattedES;
	}
}

/**
 * Add commas to number value.
 * @param spanId The span id where the number is.
 * @param decimalNumber number of decimal place, set to null if will not display decimal.
 */
function addCommaToExistingStocksInSpanElement(spanId, decimalNumber) {
	var value = $(spanId).text();
	$(spanId).text(addCommaToElement(value, decimalNumber));
}

/**
 * Add commas to number value.
 * @param txboxId The textbox id where the number is.
 * @param decimalNumber number of decimal place, set to null if will not display decimal.
 */
function addCommaToTxtBoxElement(txboxId, decimalNumber) {
	var value = $(txboxId).val();
	value = removeComma(value);
	$(txboxId).val(addCommaToElement(value, decimalNumber));
}

function addCommaToElement(value, decimalNumber) {
	if(decimalNumber != null) {
		value = Number(value).toFixed(decimalNumber);
		console.log("formatted value: "+value);
	}
	var splitVal = new Array();
	splitVal = value.split(".");
	var valBeforeSplit = splitVal[0];
	var valAfterSplit = splitVal[1] == null ? "" : "."+splitVal[1];
	while (/(\d+)(\d{3})/.test(valBeforeSplit.toString())){
		valBeforeSplit = valBeforeSplit.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
	}
	return valBeforeSplit+valAfterSplit;
}

/**
 * Remove comma/s from number value.
 * @param decimalNumber The number that will have comma removed.
 */
function removeComma (decimalNumber) {
	return decimalNumber.replace(/\,/g,"");
}
