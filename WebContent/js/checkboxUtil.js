/**
 * Utility class for check boxes. 

 */

/**
 * Enable/Disable the edit and delete button.
 * @param checkBoxesName the check boxes name.
 * @param editButtonId The edit button id.
 * @param deleteButtonId the delete button id.
 */
function enableEditAndDeleteButton (checkBoxesName, editButtonId, deleteButtonId) {
	var cbs = document.getElementsByName(checkBoxesName);
	var checkedCounter = 0;
	enableEdit = false;
	enableDelete = false;
	for (var index =0; index < cbs.length; index++) {
		var cb = cbs[index];
		if(cb.checked == true) {
			if (checkedCounter > 0) {
				enableEdit = false;
			} else {
				enableEdit = true;
			}
			enableDelete = true;
			checkedCounter++;
		}
	}
	$("#"+editButtonId).attr("disabled", "disabled"); 
	if (enableEdit){
		$("#"+editButtonId).removeAttr("disabled");
	}
	$("#"+deleteButtonId).attr("disabled", "disabled");
	if (enableDelete) {
		$("#"+deleteButtonId).removeAttr("disabled");
	}
}

/**
 * Enable/Disable the button(s).
 * @param checkBoxesName The checkboxes name.
 * @param buttonId The id of the button to be enable or disable.
 */
function enableDisableButton (checkBoxesName, buttonId) {
	var cbs = document.getElementsByName(checkBoxesName);
	enableButtons = false;
	for (var index =0; index < cbs.length; index++) {
		var cb = cbs[index];
		if(cb.checked == true) {
			enableButtons = true;
			break;
		}
	}
	$("#"+buttonId).attr("disabled", "disabled");
	if (enableButtons) {
		$("#"+buttonId).removeAttr("disabled");
	}
}
/**
 * Check/unchecked the check boxes depending on the 'checkAllCheckbox' status.
 * @param checkBoxesName The check boxes name
 * @param checkAllCheckbox checkbox object that holds the status to check
 * or unchecked the checkboxes.
 * @param editButtonId The id of the edit button.
 * @param deleteButtonId The id of the delete button. 
 */
function checkUncheckedAll (checkBoxesName, checkAllCheckbox, editButtonId, deleteButtonId) {
	var cbs = document.getElementsByName(checkBoxesName);
	var isChecked = checkAllCheckbox.checked;
	for (var index =0; index < cbs.length; index++) {
		var cb = cbs[index];
		$("#"+cb.id).attr("checked", isChecked);
	}
	$("#"+editButtonId).attr('disabled', 'disabled');
	if (isChecked) {
		$("#"+deleteButtonId).removeAttr('disabled');
	} else {
		$("#"+deleteButtonId).attr("disabled", "disabled");
	}
}

/**
 * Append all of the checked checkboxes in one string with the delimeter of ";";  
 * @param checkBoxesName The checkboxes name.
 * @returns String that contains the appended ids of the checkboxes. 
 * The delimeter is ";"
 */
function appendCheckedCheckBoxes(checkBoxesName) {
	var cbs = document.getElementsByName(checkBoxesName);
	var ids = null;
	for (var index =0; index < cbs.length; index++) {
		var cb = cbs[index];
		if (cb.checked == true){
			if (ids == null){ 
				ids = "" + cb.id;
			} else {
				ids = ids + ";"+ cb.id;				
			}
		}
	}
	return ids;
}

/**
 * Get the checked id from the list of checkboxes.
 * @param checkBoxesName The checkboxes name.
 * @returns {Number} The checkbox id.
 */
function getCheckedId(checkBoxesName) {
	var cbs = document.getElementsByName(checkBoxesName);
	var id = 0;
	for (var index =0; index < cbs.length; index++) {
		var cb = cbs[index];
		if (cb.checked == true) {
			id = cb.id;
			break;
		}
	}
	return id;
}





