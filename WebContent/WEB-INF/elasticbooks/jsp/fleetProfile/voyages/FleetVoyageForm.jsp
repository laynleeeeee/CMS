<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Voyages Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<style type="text/css">
.inputVoyage {
	border: none;
	width: 100%;
}

.header {
	font-style: normal;
	font-weight:bold;
	font-family: sans-serif;
	font-size: x-small;
	background-color: #C0C0C0;
	border: 1px solid black;
	padding: 5px;
}

.subHeader {
	font-style: normal;
	font-weight:bold;
	font-family: sans-serif;
	font-size: x-small;
	border: 1px solid black;
	padding: 5px;
}

.tdBorder {
	border: 1px solid black;
}

input:read-only{
  background-color: #F0F0F0;
}

.imgCalendar {
	cursor: pointer;
	float: right;
	padding-right: 20%;
}
</style>
<script type="text/javascript">
var $fleetVoyagesRefDocsTbl = null;

$(document).ready(function(){
	initVoyagesTbl();
	initializeDocumentsTbl();
});

function pickVoyageDate(elem, cssClass) {
	var $txtDate = $(elem).closest("tr").find("."+cssClass);
	NewCssCal($($txtDate).attr("id"));
}

function enableVoyageRow($tr){
	$($tr).find(".inputVoyage").removeAttr("readonly");
	$($tr).find(".imgCalendar").attr("onclick", "pickVoyageDate(this, 'txtDateOfDeparture')");
	$($tr).find(".imgCalendarUnloading").attr("onclick", "pickVoyageDate(this, 'txtDateOfUnloading')");
}

function initVoyagesTbl(){
	var voyagesTbl = "<table width='63%' id='tblVoyages'>";
	var url = contextPath + "/voyage/getFleetVoyages?refObjectId=" + fpRefObjectId;
	// Header
	voyagesTbl += "<thead>";
	voyagesTbl += "<tr>";
	voyagesTbl += "<th class='header' width='5%'></th>";
	voyagesTbl += "<th class='header' width='18%'>Date of Departure</th>";
	voyagesTbl += "<th class='header' width='18%'>Date of Unloading</th>";
	voyagesTbl += "<th class='header' width='44%'>Catcher</th>";
	voyagesTbl += "<th class='header' width='15%'>Volume</th>";

	// Body
	voyagesTbl += "<tbody>";
	$.ajax({
		url : url,
		async : false,
		success: function(voyages){
			voyages = $.parseJSON(voyages);
			if(voyages.length > 0){
				$.each(voyages, function(i, v) {
					voyagesTbl += "<tr>";
					voyagesTbl += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableVoyageRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
					voyagesTbl += "<input type='hidden' class='hdnDryDockingId' value="+v.id+">";
					voyagesTbl += "<input type='hidden' class='ebObjectId' value='"+v.ebObjectId+"'></td>";
					voyagesTbl += "</td>";
					var dateDepartureId = "dateOfDeparture"+i;
					voyagesTbl += "<td class='tdBorder dateOfDeparture'><input type='text' id="+dateDepartureId+" readonly='readonly' style='width: 120px;' class='txtDateOfDeparture inputVoyage' value='"+v.strDateOfDeparture+"' onblur='evalDate(&quot;"+dateDepartureId+"&quot;)'>";
					voyagesTbl += "<img class='imgCalendar' id='imgCalendar"+i+"' disabled='disabled' src='${pageContext.request.contextPath}/images/cal.gif' onclick='return false'/>";
					voyagesTbl += "</td>";
					var dateOfUnloadingId = "dateOfUnloading"+i;
					voyagesTbl += "<td class='tdBorder dateOfUnloading'><input type='text' id="+dateOfUnloadingId+" readonly='readonly' style='width: 120px;' class='txtDateOfUnloading inputVoyage' value='"+v.strDateOfUnloading+"' onblur='evalDate(&quot;"+dateOfUnloadingId+"&quot;)'>";
					voyagesTbl += "<img class='imgCalendarUnloading' id='imgCalendarUnloading"+i+"' disabled='disabled' src='${pageContext.request.contextPath}/images/cal.gif' onclick='return false'/>";
					voyagesTbl += "</td>";
					voyagesTbl += "<td class='tdBorder catcher'><input type='text' id='catcher"+i+"' readonly='readonly' class='catcher inputVoyage' value='"+v.catcher+"'>";
					voyagesTbl += "</td>";
					voyagesTbl += "<td class='tdBorder volume'><input type='text' id='volume"+i+"' readonly='readonly' class='volume inputVoyage' value='"+v.volume+"'>";
					voyagesTbl += "</td>";
					voyagesTbl += "</tr>";
				});
			} else {
				voyagesTbl += "<tr>";
				voyagesTbl += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableVoyageRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
				voyagesTbl += "<input type='hidden' class='hdnDryDockingId'>";
				voyagesTbl += "<input type='hidden' class='ebObjectId'></td>";
				voyagesTbl += "</td>";
				voyagesTbl += "<td class='tdBorder dateOfDeparture'><input type='text' id='dateOfDeparture' style='width: 120px;' class='txtDateOfDeparture inputVoyage' onblur='evalDate(&quot;dateOfDeparture&quot;)'>";
				voyagesTbl += "<img class='imgCalendar' id='imgCalendar' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickVoyageDate(this, &quot;txtDateOfDeparture&quot;)'/>";
				voyagesTbl += "</td>";
				voyagesTbl += "<td class='tdBorder dateOfUnloading'><input type='text' id='dateOfUnloading' style='width: 120px;' class='txtDateOfUnloading inputVoyage' onblur='evalDate(&quot;dateOfUnloading&quot;)'>";
				voyagesTbl += "<img class='imgCalendarUnloading' id='imgCalendarUnloading' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickVoyageDate(this, &quot;txtDateOfUnloading&quot;)'/>";
				voyagesTbl += "</td>";
				voyagesTbl += "<td class='tdBorder catcher'><input type='text' id='catcher' class='catcher inputVoyage'>";
				voyagesTbl += "</td>";
				voyagesTbl += "<td class='tdBorder volume'><input type='text' id='volume' class='volume inputVoyage'>";
				voyagesTbl += "</td>";
				voyagesTbl += "</tr>";
			}
		}
	});
	voyagesTbl += "</tbody>";
	voyagesTbl += "</table>";
	$("#voyagesTable").append(voyagesTbl);

	// Add plus [+] in the 1st row.
	var addRow = "<td onclick='appendVoyageRow();' style='cursor:pointer' class='addVoyageRow'>[+]</td>";
	var $firstTr = $("#tblVoyages").find('tbody tr:first');
	$($firstTr).append(addRow);
}

function appendVoyageRow(){
	$(".addVoyageRow").remove();
	var $firstTr = $("#tblVoyages").find('tbody tr:first');
	var $lastTr = $("#tblVoyages").find('tbody tr:last');
	var nextIndex = Number($($lastTr).index()) + 1;
	var tr = "<tr>";
	tr += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableVoyageRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
	tr += "<input type='hidden' class='hdnDryDockingId'>";
	tr += "<input type='hidden' class='ebObjectId'></td>";
	tr += "</td>";
	var dateOfDepartureId = "dateOfDeparture"+nextIndex;
	tr += "<td class='tdBorder dateOfDeparture'><input type='text' id="+dateOfDepartureId+" style='width: 120px;' class='txtDateOfDeparture inputVoyage' onblur='evalDate(&quot;"+dateOfDepartureId+"&quot;)'>";
	tr += "<img class='imgCalendar' id='imgCalendar' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickVoyageDate(this, &quot;txtDateOfDeparture&quot;)'/>";
	tr += "</td>";
	var dateOfUnloadingId = "dateOfUnloading"+nextIndex;
	tr += "<td class='tdBorder dateOfUnloading'><input type='text' id="+dateOfUnloadingId+" style='width: 120px;' class='txtDateOfUnloading inputVoyage' onblur='evalDate(&quot;"+dateOfUnloadingId+"&quot;)'>";
	tr += "<img class='imgCalendarUnloading' id='imgCalendarUnloading' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickVoyageDate(this, &quot;txtDateOfUnloading&quot;)'/>";
	tr += "</td>";
	tr += "<td class='tdBorder catcher'><input type='text' id='catcher"+nextIndex+"' class='catcher inputVoyage'>";
	tr += "</td>";
	tr += "<td class='tdBorder volume'><input type='text' id='volume"+nextIndex+"' class='volume inputVoyage'>";
	tr += "</td>";
	tr += "<td onclick='appendVoyageRow();' style='cursor:pointer' class='addVoyageRow'>[+]</td>";
	$(tr).insertBefore($firstTr);
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJsonVoyages").val());
	var cPath = "${pageContext.request.contextPath}";
	$fleetVoyagesRefDocsTbl = $("#fleetVoyagesRefDocsTbl").editableItem({
		data: refDocsJson,
		jsonProperties: [
				{"name" : "id", "varType" : "int"},
				{"name" : "ebObjectId", "varType" : "int"},
				{"name" : "referenceObjectId", "varType" : "int"},
				{"name" : "fileName", "varType" : "string"},
				{"name" : "description", "varType" : "string"},
				{"name" : "file", "varType" : "string"},
				{"name" : "fileInput", "varType" : "string"},
				{"name" : "fileSize", "varType" : "double"},
		],
		contextPath: cPath,
		header: [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "ebObjectId",
				"cls" : "ebObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "fileName",
				"cls" : "fileName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "File Name",
				"cls" : "docName tblInputText",
				"editor" : "label",
				"visible" : true,
				"width" : "25%" },
			{"title" : "Description",
				"cls" : "description tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "45%" },
			{"title" : "file",
				"cls" : "file",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Upload Image",
				"cls" : "fileInput tblInputFile",
				"editor" : "file",
				"visible" : true,
				"width" : "25%" },
			{"title" : "fileSize",
				"cls" : "fileSize",
				"editor" : "hidden",
				"visible" : false}
		],
		"disableDuplicateStockCode" : false,
		"itemTableMessage": ""
	});

	$("#fleetVoyagesRefDocsTbl").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsMgs"), $("#fleetVoyagesRefDocsTbl"));
	});

	$("#fleetVoyagesRefDocsTbl tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fleetVoyagesRefDocsTbl").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

function builVoyagesJSON(){
	var json = "[";
	var maxTrs = $("#tblVoyages tbody tr").length - 1;
	$("#tblVoyages tbody tr").each(function(row) {
		json += "{";
		$(this).find("td").each(function (col) {
			var tdCls = $(this).attr("class");
			if(tdCls == "tdBorder") {
				var dryDockingId = $(this).find(".hdnDryDockingId").val();
				var ebObjectId = $(this).find(".ebObjectId").val();
				if(dryDockingId != "") {
					json += '"id"' + ":" + '"' + dryDockingId + '"' + ",";
				} else {
					json += '"id"' + ":" + 0 + ",";
				}
				if(ebObjectId != "") {
					json += '"ebObjectId"' + ":" + '"' + ebObjectId + '"' + ",";
				} else {
					json += '"ebObjectId"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder dateOfDeparture"){
				var dateOfDeparture = $(this).find(".txtDateOfDeparture").val();
				if(dateOfDeparture != ""){
					json += '"dateOfDeparture"' + ":" + '"' + dateOfDeparture + '"' + ",";
				} else {
					json += '"dateOfDeparture"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder dateOfUnloading"){
				var dateOfUnloading = $(this).find(".txtDateOfUnloading").val();
				if(dateOfUnloading != ""){
					json += '"dateOfUnloading"' + ":" + '"' + dateOfUnloading + '"' + ",";
				} else {
					json += '"dateOfUnloading"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder catcher"){
				var catcher = $(this).find(".catcher").val();
				if(catcher != ""){
					json += '"catcher"' + ":" + '"' + catcher + '"' + ",";
				} else {
					json += '"catcher"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder volume"){
				var volume = $(this).find(".volume").val();
				if(volume != ""){
					json += '"volume"' + ":" + '"' + volume + '"';
				} else {
					json += '"volume"' + ":" + null;
				}
			}
		});
		json += "}";
		if (row < maxTrs) {
			json += ",";
		}
	});
	json += "]";
	return json;
}

var isSaving = false;
function saveVoyagesForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#fleetVoyagesRefDocsTbl"))) {
		isSaving = true;
		$("#hdnVoyageRefObjectId").val(fpRefObjectId);
		$("#referenceDocumentsJsonVoyages").val($fleetVoyagesRefDocsTbl.getData());
		$("#voyagessJson").val(builVoyagesJSON());
		$("#btnSaveVoyages").attr("disabled", "disabled");
		$("#tblVoyages tbody input").each(function(row) {
			$(this).attr('value',$(this).val());
		});
		var tblVoyages = $("#tblVoyages").html();
		doPostWithCallBack ("voyagesForm", "divVoyages", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/voyage/?refObjectId="+fpRefObjectId;
							$("#divVoyages").load(url);
						}
					}
				});
			} else {
				$("#voyagesFormId").html(data);
				$("#tblVoyages").html(tblVoyages);
			}
			isSaving = false;
			$("#btnSaveVoyages").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#fleetVoyagesRefDocsTbl"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
</head>
<body>
<div class="formDivBigForms" id="voyagesFormId" >
		<form:form method="POST" commandName="voyage" id="voyagesForm">
			<form:hidden path="fpEbObjectId" id="hdnVoyageRefObjectId"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJsonVoyages" />
			<form:hidden path="fleetVoyagesJson" id="voyagessJson" />
			<fieldset class="frmField_set">
				<legend>Voyages</legend>
				<div id="voyagesTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="fleetVoyagesErrMsg" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<div id="fleetVoyagesRefDocsTbl"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="referenceDocsMessage" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="spDocSizeMsg"></span></td>
					</tr>
					<tr>
						<td colspan="12"><span class="error" id="referenceDocsMgs" style="margin-top: 12px;"></span></td>
					</tr>
				</table>
				</fieldset>
				<br>
				<table class="frmField_set">
					<tr>
						<td align="right" colspan="2"><input type="button" id="btnSaveVoyages"
							value="Save" onclick="saveVoyagesForm();" />
						</td>
					</tr>
				</table>
		</form:form>
	</div>
</body>
</html>