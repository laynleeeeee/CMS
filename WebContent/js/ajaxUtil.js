/**
 * Utility class for ajax process. 

 */

/**
 * Handle the ajax post process using dojo. This will post the form data to the server. 
 * This method will consider a successful transaction if the return data is text "saved", otherwise unsuccessful or error. 
 * 
 * @param formId The form id to be posted. 
 * @param divFormId The division id that holds the html value of the form.
 * This will be emptied after a successful posting. 
 * @param divTableId The division id that holds the html table data.
 * @param targetUrl The target url that will be invoked after a successful post. Exclude the context path. 
 * posting. The html result will be shown in the "divTableId" 
 */
function doPost (formId, divFormId, divTableId,  targetUrl) {
	doPostWithCallBack(formId, divFormId, function (data) {
		if (data == "saved") {
			dojo.byId(divFormId).innerHTML = "";
			$("#"+divTableId).load(contextPath + targetUrl);
			$("html, body").animate({scrollTop: $("#"+divTableId).offset().top}, 1000);
		} else {
			dojo.byId(divFormId).innerHTML = data;
		}
	});
}

/**
 * Handle the ajax post process using dojo. This will post the form data to the server. 
 * This method accepts a call back function as parameter.
 * 
 * @param formId  The form id to be posted. 
 * @param divFormId The division id that holds the html value of the form.
 * @param callBackFunction The function that will be executed after a successful post.
 */
function doPostWithCallBack (formId, divFormId, callBackFunction) {
	$("#spinner").show();
	var xhrArgs = {
		form : dojo.byId(formId),
		handleAs : "text",
		load : function (data) {
			$("#spinner").hide();
			callBackFunction (data);
		},
		contentType:"application/x-www-form-urlencoded; charset=utf-8",
		error : function(error) {
			$("#spinner").hide();
			dojo.byId(divFormId).innerHTML = error;
		}
	};
	// Call the asynchronous xhrPost
	dojo.xhrPost(xhrArgs);
}

/**
 * Set the text of the span from the reference object short name
 * @param $span The span that will be used in this operation
 * @param ebObjectId the object id
 * @param orTypeId the relationship of this object to the other object.
 * @param contextPath the context path of this application
 */
function setSpanShortDesc ($span, ebObjectId, orTypeId, contextPath) {
	var url = contextPath + "/getRefObject/shortDesc?ebObjectId="+ebObjectId+"&orTypeId="+orTypeId;
	$($span).load(url);
}

/**
 * Set the text of the span from the reference object short name
 * @param $span The span that will be used in this operation
 * @param companyId The company Id
 * @param itemId The item id.
 * @param warehouseId The warehouseId.
 * @param orTypeId the relationship of this object to the other object.
 * @param sourceObjectId the object id
 * @param contextPath the context path of this application
 */
function setRefShortDesc ($span, orTypeId, sourceObjectId, contextPath) {
	var url = contextPath + "/getAvailableBags/origRef?orTypeId="+orTypeId+"&sourceObjectId="+sourceObjectId;
	$($span).load(url);
}

function doPostWithCallBackObjParam (url, frm, divForm, isAsync, callBackFunction) {
	$("#spinner").show();
	$.ajax({
		url: url,
		async : isAsync,
		type: 'POST',
		dataType: 'text',
		data: $(frm).serialize(),
		contentType:"application/x-www-form-urlencoded; charset=utf-8",
		success: callBackFunction,
		complete : function (data) {
			$("#spinner").hide();
		},
		error : function(error) {
			$("#spinner").hide();
			$(divForm).html(error);
		}
	});
}