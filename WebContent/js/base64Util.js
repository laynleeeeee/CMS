/**
 * Utility class for base64 processing.

 */

function convBase64ToFile(strBase64, filename) {
	var tmp = strBase64.split(",");
	var prefix = tmp[0];
	var contentType = prefix.split(/[:;]+/)[1];
	var byteCharacters = atob(tmp[1]);

	var byteNumbers = new Array(byteCharacters.length);
	for (var i = 0; i < byteCharacters.length; i++) {
		byteNumbers[i] = byteCharacters.charCodeAt(i);
	}
	var byteArray = new Uint8Array(byteNumbers);
	var blob = new Blob([byteArray], {type: contentType});
	var blobUrl = URL.createObjectURL(blob);

	var link = document.createElement("a");
	link.href = blobUrl;
	link.download = filename;
	link.style.display = "none";
	document.body.appendChild(link);
	link.click();
	delete link;
}

function convertDocToBase64($fileObj, sizeinbytes, $spanErrorMsg, $divDocTable) {
	$($spanErrorMsg).html("");
	var $fileName = $($fileObj).closest("tr").find(".fileName");
	$($fileName).val("");
	$($fileObj).closest("tr").find(".fileLink").html("");
	var value = $.trim($($fileObj).val());
	var fileNames = value.split("\\");
	var isDuplicate = false;
	var name = null;
	var names = null;
	$($divDocTable).find("tbody tr").each(function(){
		name = $.trim($(this).find(".fileName").val());
		if(fileNames.slice(-1)[0] == name){
			isDuplicate = true;
		}
	});
	if(isDuplicate){
		$($spanErrorMsg).html("Duplicate file.");
	} else {
		if (value != "") {
			var file = $($fileObj)[0].files[0];
			var $file = $($fileObj).closest("tr").find(".file");
			var $docName = $($fileObj).closest("tr").find(".docName");
			var FR= new FileReader();
			FR.onload = function(e) {
				$($file).val(e.target.result);
			};
			FR.onprogress = function (e){
				if (e.lengthComputable) {
					var percentLoaded = Math.round((e.loaded / e.total) * 100);
					if (percentLoaded < 100) {
						$($docName).html(percentLoaded + '%' + " loading...");
					}
				}
			}
			FR.onloadend = function (e) {
				var $td = $($fileObj).closest("tr").find(".docName").parent("td");
				$($docName).html("");
				$($fileName).val($.trim(fileNames.slice(-1)[0]));
				$($td).append("<a href='#' class='fileLink'>" + fileNames.slice(-1)[0] + "</a>");
			}
			FR.readAsDataURL( file );
		}
	}
}

function checkExceededFileSize ($divDocTable) {
	var totalFileSize = 0;
	var FILE_INCREASE = 0.40;
	$($divDocTable).find("tbody tr").find(".fileSize").each(function(){
		if ($.trim($(this).val()) != "") {
			var fileSize = parseFloat(accounting.unformat($(this).val()));
			totalFileSize += (fileSize + (fileSize * FILE_INCREASE));
		}
	});
	// 14680064 = 10485760(10 MB) + (10485760 × .4)
	// Included the file increase
	if (totalFileSize > 14680064) {
		return true;
	}
	return false;
}

//Disable reference table attachment link.
function disableRefTblLink(hasEditAccess, tableId, fileId) {
	if(hasEditAccess != "true"){
		$("#"+tableId + " tbody tr").find("#"+fileId).each(function(row) {
			$(this).on('click', false);
			$(this).addClass('disabled-link');
			$(this).attr("disabled", "disabled");
		});
	}
}
