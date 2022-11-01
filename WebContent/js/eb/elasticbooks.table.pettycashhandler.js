/**
 * Object that handles the petty cash table. 
 * This is only applicable with table with the following columns.
 * 
 */

PettyCashHandler = function (totalHandler) {
	this.totalHandler = totalHandler;

	this.onDelete = function (setting, $table) {
		if (typeof computeGrandTotal != "undefined") {
			computeGrandTotal();
		}
	};
};

