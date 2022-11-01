/**
 * Validation utility using ajax.
 * @param sourceUrl The URL
 * @param inputValue The value of the textbox
 * @param hdnLocationId The location of the id of the object
 * @param descLocation The description of the object
 * @param successFunction The function to be executed if the ajax call is successful
 * @param errorFunction The function to be executed if the ajax call is unsuccessful

 */
function validateInput(sourceUrl, inputValue, hdnLocationId, descLocation, successFunction, errorFunction) {
	if($.trim(inputValue) == "") {
		$("#"+hdnLocationId).val(null);
	} else {
		validateInput(sourceUrl, inputValue,
				function(object) {
					//Success function
					if(successFunction != null)
						successFunction();
					$("#"+hdnLocationId).val(object.id);
					if(descLocation != null)
						$("#"+descLocation).text(object.description);
				}, function(object) {
					//Error function
					if(errorFunction != null)
						errorFunction();
				}
		);
	}
}

/**
 * Validation utility using ajax.
 * @param sourceUrl The URL
 * @param inputValue The value of the textbox
 * @param successFunction The function to be executed if the ajax call is successful
 * @param errorFunction The function to be executed if the ajax call is unsuccessful
 */
function validateInput(sourceUrl, successFunction, errorFunction) {
	console.log("Validating the user input");
	var uri = contextPath+sourceUrl;
	$.ajax({
		url: uri,
		success : function(object) {
			if(object == null) {
				if(errorFunction != null)
					errorFunction(object);
			} else {
				if(successFunction != null) {
					successFunction(object);
				}
			}
		}, error : function(error) {
			if(errorFunction != null)
				errorFunction(object);
			console.log(error);
		}, dataType: "json",
	});
}
