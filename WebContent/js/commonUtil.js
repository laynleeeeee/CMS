/**
* All Common javascript common functionality. 

*/

function highlightMenu (menuName, requestedUri, cssClass) {
	var menus = $("a[name="+menuName+"]");
	for (var index=0; index < menus.length; index++) {
		var aMenu = menus[index];
		if (requestedUri.indexOf(aMenu.id) != -1) {
			$("#" +aMenu.id).attr("class", cssClass);
			break;
		}
	}
}

/**
 * Common function for indexing in data table. 
 * @param oSettings The dataTables settings object
 */
function addTableRowNumber(oSettings) {
	for ( var i=0, iLen=oSettings.aiDisplay.length ; i<iLen ; i++ ) {
		$('td:eq(0)', oSettings.aoData[ oSettings.aiDisplay[i] ].nTr ).html( i+1 );
	}
}

/**
 * Common function for indexing in data table with beginning balance. 
 * @param oSettings The dataTables settings object
 */
function addTableRowNumberWithBegBalance(oSettings) {
	for ( var i=0, iLen=oSettings.aiDisplay.length ; i<iLen ; i++ ) {
		$('td:eq(0)', oSettings.aoData[ oSettings.aiDisplay[i] ].nTr ).html( i+0 );
	}
}

/**
 * Computes the total amount of the specified column and add the value to the footer.  
 * @param nFoot the footer object.
 * @param aaData The table data. 
 * @param columnProps An arryay of columnProperty objects that hold the information of the column to be
 * totaled and the name of the column. Please see ColumnPropery for the attribute of this object. 
 * 
 * Sample implementation:
 * var columnProperties = new Array();
 * var columnProp = new ColumnPropert ("columnName", columnNumber);
 * columnProperties.push(columnProp).
 */
function addTotalInFooter (nFoot, aaData, columnProps) {
	var totalHolder = new Object ();
	//Initialize total holder
	for (var i = 0; i<columnProps.length; i++) {
		totalHolder[columnProps[i].columnName] = 0;
	}
	
	for ( var i=0 ; i<aaData.length ; i++ ) {
     	var row = aaData[i];
     	for (var iColumn = 0; iColumn<columnProps.length; iColumn++) {
     		var columnName = columnProps[iColumn].columnName;
     		var strValue = row[columnName];
     		strValue = strValue.toString();
     		var value = Number(strValue.replace(/[^\-0-9\.]+/g,""));
     		totalHolder[columnName] += value;
     	}
    }
	
	 var nCells = nFoot.getElementsByTagName('th');
	 for (var iColumn = 0; iColumn<columnProps.length; iColumn++) {
		 var columnName = columnProps[iColumn].columnName;
		 var columnNumber = columnProps[iColumn].columnNumber;
		 var formattedAmount = accounting.formatMoney(totalHolder[columnName]);
		 nCells[columnNumber].innerHTML = accounting.formatMoney(formattedAmount);
	 }
}

/**
 * Column Property
 * @param columnName The name of the column
 * @param columnNumber The column number
 * @returns {ColumnProperty}
 */
function ColumnProperty (columnName, columnNumber) {
	this.columnName = columnName;
	this.columnNumber = columnNumber;
}

/**
 * String concatenation
 * @param str The string to be concatenate
 * @param maxLength The desire length
 * @returns The concatenated string
 */
function concatString(str, maxLength){
	var formattedStr = str;
	if (str.length > maxLength)
		formattedStr = str.substring(0, maxLength) + "....";
	return formattedStr;
}