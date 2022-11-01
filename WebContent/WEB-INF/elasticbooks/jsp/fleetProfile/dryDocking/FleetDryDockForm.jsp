<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Dry Docking Form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<style type="text/css">
.inputDryDocking {
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

.imgDryDockCalendar {
	cursor: pointer;
	float: right;
	padding-right: 20%;
}
</style>
<script type="text/javascript">
var $fleetDryDockingRefDocsTbl = null;

$(document).ready(function(){
	initDryDockingTbl();
	initializeDocumentsTbl();
});

function buildDryDockingJson() {
	var json = "[";
	var maxTrs = $("#tblDryDocking tbody tr").length - 1;
	$("#tblDryDocking tbody tr").each(function(row) {
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
			} else if (tdCls == "tdBorder date"){
				var date = $(this).find(".txtDate").val();
				if(date != ""){
					json += '"date"' + ":" + '"' + date + '"' + ",";
				} else {
					json += '"date"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder contractor"){
				var contractor = $(this).find(".contractor").val();
				if(contractor != ""){
					json += '"contractor"' + ":" + '"' + contractor + '"' + ",";
				} else {
					json += '"contractor"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder actual"){
				var actual = $(this).find(".actual").is(":checked");
				json += '"isActual"' + ":" + '"' + actual + '"';
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

function pickDate(elem) {
	var $txtDate = $(elem).closest("tr").find(".txtDate");
	NewCssCal($($txtDate).attr("id"));
}

function enableDryDockRow($tr){
	$($tr).find(".inputDryDocking").removeAttr("readonly");
	$($tr).find(".inputDryDocking").removeAttr("disabled");
	$($tr).find(".imgDryDockCalendar").attr("onclick", "pickDate(this)");
}

function initDryDockingTbl(){
	var dryDockingTbl = "<table width='63%' id='tblDryDocking'>";
	var url = contextPath + "/dryDock/getFleetDryDocking?refObjectId=" + fpRefObjectId;
	// Header
	dryDockingTbl += "<thead>";
	dryDockingTbl += "<tr>";
	dryDockingTbl += "<th class='header' width='5%'></th>";
	dryDockingTbl += "<th class='header' width='18%'>Date</th>";
	dryDockingTbl += "<th class='header' width='62%'>Contractor</th>";
	dryDockingTbl += "<th class='header' width='15%'>Actual</th>";

	// Body
	dryDockingTbl += "<tbody>";
	$.ajax({
		url : url,
		async : false,
		success: function(dryDockings){
			dryDockings = $.parseJSON(dryDockings);
			if(dryDockings.length > 0){
				$.each(dryDockings, function(i, d) {
					dryDockingTbl += "<tr>";
					dryDockingTbl += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableDryDockRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png'  height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
					dryDockingTbl += "<input type='hidden' class='hdnDryDockingId' value="+d.id+">";
					dryDockingTbl += "<input type='hidden' class='ebObjectId' value='"+d.ebObjectId+"'></td>";
					dryDockingTbl += "</td>";
					var dateId = "date"+i;
					dryDockingTbl += "<td class='tdBorder date'><input type='text' id="+dateId+" readonly='readonly' style='width: 120px;' class='txtDate inputDryDocking' value='"+d.strDate+"' onblur='evalDate(&quot;"+dateId+"&quot;)'>";
					dryDockingTbl += "<img class='imgDryDockCalendar' id='imgCalendar"+i+"' readonly='readonly' src='${pageContext.request.contextPath}/images/cal.gif' onclick='return false'/>";
					dryDockingTbl += "</td>";
					dryDockingTbl += "<td class='tdBorder contractor'><input type='text' id='contractor"+i+"' readonly='readonly' class='contractor inputDryDocking' value='"+d.contractor+"'>";
					dryDockingTbl += "</td>";
					var checked = "";
					if(d.isActual){
						checked = "checked";
					}
					dryDockingTbl += "<td class='tdBorder actual'><input type='checkbox' id='actual"+i+"' disabled='disabled' class='actual inputDryDocking' "+checked+">";
					dryDockingTbl += "</td>";
					dryDockingTbl += "</tr>";
				});
			} else {
				dryDockingTbl += "<tr>";
				dryDockingTbl += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableDryDockRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png'  height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
				dryDockingTbl += "<input type='hidden' class='hdnDryDockingId'>";
				dryDockingTbl += "<input type='hidden' class='ebObjectId'></td>";
				dryDockingTbl += "</td>";
				dryDockingTbl += "<td class='tdBorder date'><input type='text' id='date' style='width: 120px;' class='txtDate inputDryDocking' onblur='evalDate(&quot;date&quot;)'>";
				dryDockingTbl += "<img class='imgDryDockCalendar' id='imgCalendar' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickDate(this)'/>";
				dryDockingTbl += "</td>";
				dryDockingTbl += "<td class='tdBorder contractor'><input type='text' id='contractor' class='contractor inputDryDocking'>";
				dryDockingTbl += "</td>";
				dryDockingTbl += "<td class='tdBorder actual'><input type='checkbox' id='actual' class='actual inputDryDocking'>";
				dryDockingTbl += "</td>";
				dryDockingTbl += "</tr>";
			}
		}
	});
	dryDockingTbl += "</tbody>";
	dryDockingTbl += "</table>";
	$("#dryDockingTable").append(dryDockingTbl);

	// Add plus [+] in the 1st row.
	var addRow = "<td onclick='appendDryDockRow();' style='cursor:pointer' class='addDryDockRow'>[+]</td>";
	var $firstTr = $("#tblDryDocking").find('tbody tr:first');
	$($firstTr).append(addRow);
}

function appendDryDockRow(){
	$(".addDryDockRow").remove();
	var $firstTr = $("#tblDryDocking").find('tbody tr:first');
	var $lastTr = $("#tblDryDocking").find('tbody tr:last');
	var nextIndex = Number($($lastTr).index()) + 1;
	var tr = "<tr>";
	tr += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableDryDockRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png'  height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
	tr += "<input type='hidden' class='hdnDryDockingId'>";
	tr += "<input type='hidden' class='ebObjectId'></td>";
	tr += "</td>";
	var dateId = "date"+nextIndex;
	tr += "<td class='tdBorder date'><input type='text' id="+dateId+" style='width: 120px;' class='txtDate inputDryDocking' onblur='evalDate(&quot;"+dateId+"&quot;)'>";
	tr += "<img class='imgDryDockCalendar' id='imgCalendar' src='${pageContext.request.contextPath}/images/cal.gif' onclick='pickDate(this)'/>";
	tr += "</td>";
	tr += "<td class='tdBorder contractor'><input type='text' id='contractor' class='contractor inputDryDocking'>";
	tr += "</td>";
	tr += "<td class='tdBorder actual'><input type='checkbox' id='actual"+nextIndex+"' class='actual inputDryDocking'>";
	tr += "</td>";
	tr += "<td onclick='appendDryDockRow();' style='cursor:pointer' class='addDryDockRow'>[+]</td>";
	tr += "</tr>";
	$(tr).insertBefore($firstTr);
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJsonDryDocking").val());
	var cPath = "${pageContext.request.contextPath}";
	$fleetDryDockingRefDocsTbl = $("#fleetDryDockingRefDocsTbl").editableItem({
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

	$("#fleetDryDockingRefDocsTbl").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsMgs"), $("#fleetDryDockingRefDocsTbl"));
	});

	$("#fleetDryDockingRefDocsTbl tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fleetDryDockingRefDocsTbl").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveDryDockingForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#fleetDryDockingRefDocsTbl"))) {
		isSaving = true;
		$("#hdnDryDockRefObjectId").val(fpRefObjectId);
		$("#referenceDocumentsJsonDryDocking").val($fleetDryDockingRefDocsTbl.getData());
		$("#dryDockingsJson").val(buildDryDockingJson());
		$("#btnSaveDryDocking").attr("disabled", "disabled");
		$("#tblDryDocking tbody input").each(function(row) {
			$(this).attr('value',$(this).val());
		});
		var tblDryDocking = $("#tblDryDocking").html();
		doPostWithCallBack ("dryDockingForm", "divDryDocking", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/dryDock/?refObjectId="+fpRefObjectId;
							$("#divDryDocking").load(url);
						}
					}
				});
			} else {
				$("#dryDockingFormId").html(data);
				$("#tblDryDocking").html(tblDryDocking);
			}
			isSaving = false;
			$("#btnSaveDryDocking").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#fleetDryDockingRefDocsTbl"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

</script>
</head>
<body>
	<div class="formDivBigForms" id="dryDockingFormId" >
		<form:form method="POST" commandName="dryDock" id="dryDockingForm">
			<form:hidden path="fpEbObjectId" id="hdnDryDockRefObjectId"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJsonDryDocking" />
			<form:hidden path="dryDockingsJson" id="dryDockingsJson" />
			<fieldset class="frmField_set">
				<legend>Dry Docking</legend>
				<div id="dryDockingTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="dryDockingErrMsg" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<div id="fleetDryDockingRefDocsTbl"></div>
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
						<td align="right" colspan="2"><input type="button" id="btnSaveDryDocking"
							value="Save" onclick="saveDryDockingForm();" />
						</td>
					</tr>
				</table>
		</form:form>
	</div>
</body>
</html>