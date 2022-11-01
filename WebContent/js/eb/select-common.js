/**
* Description: A common javascript function for select object.
*/

/**
 * Populates the select object from the controller which will return a JSON object.
 * @param uri the URI of the controller which will return a JSON format object.
 * @param appendAll include "ALL" in the select object.
 * @param selectedOptionValue the current selected value from the list of options. Null if there is no selected value.
 * @param selectId the "select" DOM id.
 * @param optionParser a function that implement the parsing of value and label of the option.
 * The optionParser should implement the following methods:
 * function OptionParser = new function () {
 * 		getValue: function (objectRowData) {
 * 			parse the value that will be use in the option.
 * 		}
 * 	    getLabel: function (ObjectRowData) {
 * 			parse the label of the option.
 * 		}
 * };
 * @param postHandler Post process handler. The caller must implement the doPost method to handle the post operation after
 * populating the select object.
 *  function PostHandler = new function () {
 *  	doPost: function () {
 *  		handle the post operation in this method.
 *  	}
 *  }
 */
function loadPopulate(uri, appendAll, selectedOptionValue, selectId, optionParser, postHandler) {
	var selectIds = new Array();
	selectIds.push(selectId);
	loadPopulateMultiSelect (uri, appendAll, selectedOptionValue, selectIds, optionParser, postHandler);
}

function loadPopulateObject(uri, appendAll, selectedOptionValue, $selectObj,
		optionParser, postHandler, isExecutePostHandlerOnError, prependEmpty) {
	var selectObjs = new Array ();
	selectObjs.push ($selectObj);
	loadAndPopulateMultiSelect (uri, appendAll, selectedOptionValue, selectObjs,
			optionParser, postHandler, isExecutePostHandlerOnError, prependEmpty);
}

/**
 * Similar loadPopulate but with multi-select option.
 * @param selectIds arrays list of select ids. 
 */
function loadPopulateMultiSelect(uri, appendAll, selectedOptionValue, selectIds, optionParser, postHandler) {
	var selectObjs = new Array();
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		selectObjs.push ($("#"+selectId));
	}
	loadAndPopulateMultiSelect(uri, appendAll, selectedOptionValue, selectObjs, optionParser, postHandler, false, false);
}

function loadAndPopulateMultiSelect(uri, appendAll, selectedOptionValue, selectObjs, optionParser,
		postHandler, isExecutePostHandlerOnError, prependEmpty) {
	$.ajax({
		url: uri,
		async: false,
		success: function(responseText){
			if (appendAll == true) {
				for (var i = 0; i < selectObjs.length; i++ ){
					var selectObj = selectObjs[i];
					var option = "<option selected='selected' value=-1>ALL</option>";
					$(selectObj).append(option);
				}
			} else if(prependEmpty == true) {
				for (var i = 0; i < selectObjs.length; i++ ){
					var selectObj = selectObjs[i];
					var option = "<option selected='selected' value=''></option>";
					$(selectObj).append(option);
				}
			}
			for (var index = 0; index < responseText.length; index++){
				var rowObject =  responseText[index];
				var option = "<option";
				var value = optionParser.getValue (rowObject);
				var isSetSelected  = false;
				if (isSetSelected == false &&
						selectedOptionValue != null
						&& selectedOptionValue == value
						&& appendAll == false) {
					option += " selected='selected'";
					isSetSelected = true;
				}
				option += " value='" + value + "'>";
				var label = optionParser.getLabel (rowObject);
				option += label + "</option>";
				for (var i = 0; i < selectObjs.length; i++ ){
					var selectObj = selectObjs[i];
					$(selectObj).append(option);
				}
			}
			if(postHandler != null)
				postHandler.doPost(responseText);
		},
		error: function(error) {
			if(isExecutePostHandlerOnError && postHandler != null) {
				postHandler.doPost(error);
			}
		},
		dataType: "json"
	});
}

