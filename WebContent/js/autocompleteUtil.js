/**
 * Auto complete utility function for elastic books.

 */

function loadACItems(textboxAC, itemId, description, sourceURI, changeURI, itemLabel, 
		selectFunction, changeFunction, successFunction, errorFunction){
	loadACList(textboxAC, itemId, sourceURI, changeURI, itemLabel, itemLabel,
			function(object) {
				//Select function
				if(selectFunction != null)
					selectFunction();
				if (description != null)
					$("#" + description).text(object.description);
			}, function(object) {
				//Change function
				if(changeFunction != null)
					changeFunction();
			}, function() {
				//Success function
				if(successFunction != null)
					successFunction();
			}, function() {
				//Error function
				if(errorFunction != null)
					errorFunction();
			}
	);
}

function loadACList(textboxAC, textBoxtId, sourceURI, changeURI, label, setLabel,
		selectFunction, changeFunction, successFunction, errorFunction) {
	$("#"+textboxAC).autocomplete({
		source: sourceURI,
		select: function(event, ui) {
			$("#" + textBoxtId).val(ui.item.id);
			$(this).val(ui.item[setLabel]);
			if (selectFunction != null)
				selectFunction(ui.item);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			if (changeFunction != null)
				changeFunction(ui.item);
			return false;
			$.ajax({
				url: changeURI,
				success : function(object) {
					if (object != null) {
						$("#" + textBoxtId).val(object.id);
						if (successFunction != null)
							successFunction(object);
					} else {
						if (errorFunction != null)
							errorFunction();
					}
				},
				error : function(error) {
					console.log(error);
					$("#"+textboxAC).focus();
					if (errorFunction != null)
						errorFunction();
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, object ) {
		return $( "<li>" )
		.data( "ui-autocomplete-item", object )
		.append( "<a style='font-size: small;'>"+ object[label]+ "</a>" )
		.appendTo( ul );
	};
}
