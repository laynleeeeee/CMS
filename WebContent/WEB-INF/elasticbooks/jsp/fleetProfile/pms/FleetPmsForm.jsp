<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet PMS Form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<style>

#btnSaveFleetDriver, #btnCancelFleetDriver {
	font-weight: bold;
}

.inputPms {
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
/*   color: grey;  */
  background-color: #F0F0F0;
}

</style>
<script type="text/javascript">
var $pmsTable = null;
var $fleetPmsRefDocsTbl = null;
var fleetTypeId = null;
$(document).ready(function(){
	fleetTypeId = "${fleetPmsDto.fleetTypeId}";
	$("#fleetTypeId").val(fleetTypeId);
	initPmsTbl();
	initializeDocumentsTbl();
});

function buildPmsJson(){
	var json = "[";
	var maxTrs = $("#tblPms tbody tr").length - 1;
	$("#tblPms tbody tr").each(function(row) {
		json += "{";
		$(this).find("td").each(function (col) {
			var tdCls = $(this).attr("class");

			if(tdCls == "subHeader schedHdn") {
				var pmsId = $(this).find(".hdnPmsId").val();
				var isSchedule = $(this).find(".isSchedule").val();
				var ebObjectId = $(this).find(".ebObjectId").val();
				if(pmsId != ""){
					json += '"id"' + ":" + pmsId + ",";
				} else {
					json += '"id"' + ":" + 0 + ",";
				}
				if(isSchedule != ""){
					json += '"isSchedule"' + ":" + isSchedule + ",";
				}
				if(ebObjectId != "") {
					json += '"ebObjectId"' + ":" + ebObjectId + ",";
				} else {
					json += '"ebObjectId"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedEngine") {
				var schedEngine = $(this).find("#schedEngine").val();
				if(schedEngine != ""){
					json += '"changeOilEngine"' + ":" + '"' + schedEngine + '"' +",";
				} else {
					json += '"changeOilEngine"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedMain") {
				var schedMain = $(this).find("#schedMain").val();
				if(schedMain != ""){
					json += '"overhaulEngineMain"' + ":" + '"' + schedMain + '"' +",";
				} else {
					json += '"overhaulEngineMain"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedAuxiliary") {
				var schedAuxiliary = $(this).find("#schedAuxiliary").val();
				if(schedAuxiliary != ""){
					json += '"overhaulEngineAuxiliary"' + ":" + '"' + schedAuxiliary + '"' +",";
				} else {
					json += '"overhaulEngineAuxiliary"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedTransmission") {
				var schedTransmission = $(this).find("#schedTransmission").val();
				if(schedTransmission != "") {
					json += '"changeOilTransmission"' + ":" + '"' + schedTransmission + '"' + ",";
				} else {
					json += '"changeOilTransmission"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedGenset") {
				var schedGenset = $(this).find("#schedGenset").val();
				if(schedGenset != "") {
					json += '"changeOilGenest"' + ":" + '"' + schedGenset + '"' + ",";
				} else {
					json += '"changeOilGenest"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedHydraulics") {
				var schedHydraulics = $(this).find("#schedHydraulics").val();
				if(schedHydraulics != "") {
					json += '"changeOilHydraulic"' + ":" + '"' + schedHydraulics + '"' +",";
				} else {
					json += '"changeOilHydraulic"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedFuel") {
				var schedFuel = $(this).find("#schedFuel").val();
				if(schedFuel != ""){
					json += '"filterFuel"' + ":" + '"' + schedFuel + '"' + ",";
				} else {
					json += '"filterFuel"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedOil") {
				var schedOil = $(this).find("#schedOil").val();
				if(schedOil != ""){
					json += '"filterOil"' + ":" + '"' + schedOil + '"' + ",";
				} else {
					json += '"filterOil"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder schedAir") {
				var schedAir = $(this).find("#schedAir").val();
				if(schedAir != ""){
					json += '"filterAir"' + ":" + '"' + schedAir + '"';
					if(fleetTypeId == "2"){
						json += ",";
					}
				} else {
					json += '"filterAir"' + ":" + null;
					if(fleetTypeId == "2"){
						json += ",";
					}
				}
			} else if (tdCls == "tdBorder schedFanbelt") {
				var schedFanbelt = $(this).find("#schedFanbelt").val();
				if(schedFanbelt != "") {
					json += '"filterFanbelt"' + ":" + '"' + schedFanbelt + '"' + ",";
				} else {
					json += '"filterFanbelt"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder dryDocking") {
				var dryDocking = $(this).find("#dryDocking").val();
				if(dryDocking != ""){
					json += '"dryDocking"' + ":" + '"' + dryDocking + '"' + ",";
				} else {
					json += '"dryDocking"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder net") {
				var net = $(this).find("#net").val();
				if(net != ""){
					json += '"net"' + ":" + '"' + net +'"';
				} else {
					json += '"net"' + ":" + null;
				}
			}

			if(tdCls == "subHeader lDate") {
				var pmsId = $(this).find(".hdnPmsId").val();
				var isSchedule = $(this).find(".isSchedule").val();
				var ebObjectId = $(this).find(".ebObjectId").val();
				if(pmsId != ""){
					json += '"id"' + ":" + pmsId + ",";
				} else {
					json += '"id"' + ":" + 0 + ",";
				}
				if(isSchedule != ""){
					json += '"isSchedule"' + ":" + isSchedule + ",";
				}
				if(ebObjectId != "") {
					json += '"ebObjectId"' + ":" + ebObjectId + ",";
				} else {
					json += '"ebObjectId"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateEngine") {
				var lDateEngine = $(this).find("#lDateEngine").val();
				if(lDateEngine != ""){
					json += '"changeOilEngine"' + ":" + '"' + lDateEngine + '"' + ",";
				} else {
					json += '"changeOilEngine"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateMain") {
				var lDateMain = $(this).find("#lDateMain").val();
				if(lDateMain != ""){
					json += '"overhaulEngineMain"' + ":" + '"' + lDateMain + '"' + ",";
				} else {
					json += '"overhaulEngineMain"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateAuxiliary") {
				var lDateAuxiliary = $(this).find("#lDateAuxiliary").val();
				if(lDateAuxiliary != ""){
					json += '"overhaulEngineAuxiliary"' + ":" + '"' + lDateAuxiliary + '"' + ",";
				} else {
					json += '"overhaulEngineAuxiliary"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateTransmission") {
				var lDateTransmission = $(this).find("#lDateTransmission").val();
				if(lDateTransmission != ""){
					json += '"changeOilTransmission"' + ":" + '"' + lDateTransmission + '"' + ",";
				} else {
					json += '"changeOilTransmission"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateGenset") {
				var lDateGenset = $(this).find("#lDateGenset").val();
				if(lDateGenset != ""){
					json += '"changeOilGenest"' + ":" + '"' + lDateGenset + '"' + ",";
				} else {
					json += '"changeOilGenest"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateHydraulics") {
				var lDateHydraulics = $(this).find("#lDateHydraulics").val();
				if(lDateHydraulics != ""){
					json += '"changeOilHydraulic"' + ":" + '"' + lDateHydraulics + '"' + ",";
				} else {
					json += '"changeOilHydraulic"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateFuel") {
				var lDateFuel = $(this).find("#lDateFuel").val();
				if(lDateFuel != ""){
					json += '"filterFuel"' + ":" + '"' + lDateFuel + '"' + ",";
				} else {
					json += '"filterFuel"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateOil") {
				var lDateOil = $(this).find("#lDateOil").val();
				if(lDateOil != ""){
					json += '"filterOil"' + ":" + '"' + lDateOil + '"' + ",";
				} else {
					json += '"filterOil"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateAir") {
				var lDateAir = $(this).find("#lDateAir").val();
				if(lDateAir != ""){
					json += '"filterAir"' + ":" + '"' + lDateAir + '"';
					if(fleetTypeId == "2"){
						json += ",";
					}
				} else {
					json += '"filterAir"' + ":" + null;
					if(fleetTypeId == "2"){
						json += ",";
					}
				}
			} else if (tdCls == "tdBorder lDateFanbelt") {
				var lDateFanbelt = $(this).find("#lDateFanbelt").val();
				if(lDateFanbelt != ""){
					json += '"filterFanbelt"' + ":" + '"' + lDateFanbelt + '"' + ",";
				} else {
					json += '"filterFanbelt"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateDryDocking") {
				var dryDocking = $(this).find("#dryDocking").val();
				if(dryDocking != ""){
					json += '"dryDocking"' + ":" + '"' + dryDocking + '"' + ",";
				} else {
					json += '"dryDocking"' + ":" + null + ",";
				}
			} else if (tdCls == "tdBorder lDateNet") {
				var net = $(this).find("#net").val();
				if(net != ""){
					json += '"net"' + ":" + '"' + net + '"';
				} else {
					json += '"net"' + ":" + null;
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

function initPmsTbl(){
	var pmsTbl = "<table width='100%' id='tblPms'>";
	var url = contextPath + "/fleetPms/getFleetPms?refObjectId=" + fpRefObjectId;
	var hasSchedule = false;
	var hasLastDate = false;
	// Header
	pmsTbl += "<thead>";
	pmsTbl += "<tr>";
	pmsTbl += "<th class='header'></th>";
	pmsTbl += "<th class='header'></th>";

	if(fleetTypeId == "1"){
		pmsTbl += "<th colspan='2' class='header'>CHANGE OIL</th>";
		pmsTbl += "<th colspan='3' class='header'>FILTERS</th>";
	} else if (fleetTypeId == "2"){
		pmsTbl += "<th colspan='3' class='header'>CHANGE OIL</th>";
		pmsTbl += "<th colspan='4' class='header'>FILTERS</th>";
		pmsTbl += "<th rowspan='3' class='header'>NET REPAIR</th>";
	}
	pmsTbl += "</tr>";

	// Sub Header Level 1
	pmsTbl += "<tr>";
	if(fleetTypeId == "2") {
		pmsTbl += "<th class='tdBorder' rowspan='2' style='background-color: #C0C0C0;'></th>";
		pmsTbl += "<th class='tdBorder' rowspan='2' style='background-color: #C0C0C0;'></th>";
		pmsTbl += "<th align='center' colspan='2' style='background-color: #C0C0C0;' class='subHeader'>Overhaul Engine</th>";
		pmsTbl += "<th align='center' rowspan='2' style='background-color: #C0C0C0;' class='subHeader'>Overhaul Transmission</th>";
		pmsTbl += "<th align='center' rowspan='2' style='background-color: #C0C0C0;' class='subHeader'>Fuel</th>";
		pmsTbl += "<th align='center' rowspan='2' style='background-color: #C0C0C0;' class='subHeader'>Oil</th>";
		pmsTbl += "<th align='center' rowspan='2' style='background-color: #C0C0C0;' class='subHeader'>Air</th>";
		pmsTbl += "<th align='center' rowspan='2' style='background-color: #C0C0C0;' class='subHeader'>Fanbelt</th>";
	} else {
		pmsTbl += "<th class='tdBorder' style='background-color: #C0C0C0;'></th>";
		pmsTbl += "<th class='tdBorder' style='background-color: #C0C0C0;'></th>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Engine</th>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Transmission</th>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Fuel</th>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Oil</th>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Air</th>";
	}
	pmsTbl += "</tr>";

	// Sub Header Level 2
	if(fleetTypeId == "2") {
		pmsTbl += "<tr>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Main</th>";
		pmsTbl += "<th align='center' style='background-color: #C0C0C0;' class='subHeader'>Auxiliary</th>";
		pmsTbl += "</tr>";
	}
	pmsTbl += "</thead>";

	var schedRow = "";
	// Body
	pmsTbl += "<tbody>";

	$.ajax({
		url : url,
		async : false,
		success: function(pms){
			pms = $.parseJSON(pms);
			if(pms.length > 0){
				$.each(pms, function(i, p) {
					if(p.schedule){
						// Schedule
						schedRow += "<tr>";
						schedRow += "<td class='tdBorder' align='center' onclick='enableRow($(this).closest(&quot;tr&quot;));' style='cursor:pointer;'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle;' id='imgEditSched'/>";
						schedRow += "</td>";
						schedRow += "<td align='center' class='subHeader schedHdn'>Schedule";
						schedRow += "<input type='hidden' class='hdnPmsId' value="+p.id+">";
						schedRow += "<input type='hidden' class='isSchedule' value='true'>";
						schedRow += "<input type='hidden' class='ebObjectId' value="+p.ebObjectId+"></td>";
						if(fleetTypeId == "1") {
							schedRow += "<td class='tdBorder schedEngine'><input type='text' id='schedEngine' readonly='readonly' class='inputPms' value='"+p.changeOilEngine+"'></td>";
						} else if(fleetTypeId == "2") {
							schedRow += "<td class='tdBorder schedMain'><input type='text' id='schedMain' readonly='readonly' class='inputPms' value='"+p.overhaulEngineMain+"'></td>";
							schedRow += "<td class='tdBorder schedAuxiliary'><input type='text' id='schedAuxiliary' readonly='readonly' class='inputPms' value='"+p.overhaulEngineAuxiliary+"'></td>";
						}
						schedRow += "<td class='tdBorder schedTransmission'><input type='text' id='schedTransmission' readonly='readonly' class='inputPms' value='"+p.changeOilTransmission+"'></td>";
						schedRow += "<td class='tdBorder schedFuel'><input type='text' id='schedFuel' readonly='readonly' class='inputPms' value='"+p.filterFuel+"'></td>";
						schedRow += "<td class='tdBorder schedOil'><input type='text' id='schedOil' readonly='readonly' class='inputPms' value='"+p.filterOil+"'></td>";
						schedRow += "<td class='tdBorder schedAir'><input type='text' id='schedAir' readonly='readonly' class='inputPms' value='"+p.filterAir+"'></td>";
						if(fleetTypeId == "2") {
							schedRow += "<td class='tdBorder schedFanbelt'><input type='text' id='schedFanbelt' readonly='readonly' class='inputPms' value='"+p.filterFanbelt+"'></td>";
							schedRow += "<td class='tdBorder net'><input type='text' id='net' readonly='readonly' class='inputPms' value='"+p.net+"'></td>";
						}
						schedRow += "</tr>";
						// End of Schedule
						hasSchedule = true;
					} else {
						// Last Date
						pmsTbl += "<tr>";
						pmsTbl += "<td class='tdBorder' align='center' style='cursor:pointer;' onclick='enableRow($(this).closest(&quot;tr&quot;));'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
						pmsTbl += "</td>";
						pmsTbl += "<td align='center' class='subHeader lDate'>Date";
						pmsTbl += "<input type='hidden' class='hdnPmsId' value="+p.id+">";
						pmsTbl += "<input type='hidden' class='isSchedule' value='false'>";
						pmsTbl += "<input type='hidden' class='ebObjectId' value='"+p.ebObjectId+"'></td>"
						if(fleetTypeId == "1") {
							pmsTbl += "<td class='tdBorder lDateEngine'><input type='text' id='lDateEngine' readonly='readonly' class='inputPms' value='"+p.changeOilEngine+"'></td>";
						} else if(fleetTypeId == "2") {
							pmsTbl += "<td class='tdBorder lDateMain'><input type='text' id='lDateMain' readonly='readonly' class='inputPms' value='"+p.overhaulEngineMain+"'></td>";
							pmsTbl += "<td class='tdBorder lDateAuxiliary'><input type='text' id='lDateAuxiliary' readonly='readonly' class='inputPms' value='"+p.overhaulEngineAuxiliary+"'></td>";
						}
						pmsTbl += "<td class='tdBorder lDateTransmission'><input type='text' id='lDateTransmission' readonly='readonly' class='inputPms' value='"+p.changeOilTransmission+"'></td>";
						pmsTbl += "<td class='tdBorder lDateFuel'><input type='text' id='lDateFuel' readonly='readonly' class='inputPms' value='"+p.filterFuel+"'></td>";
						pmsTbl += "<td class='tdBorder lDateOil'><input type='text' id='lDateOil' readonly='readonly' class='inputPms' value='"+p.filterOil+"'></td>";
						pmsTbl += "<td class='tdBorder lDateAir'><input type='text' id='lDateAir' readonly='readonly' class='inputPms' value='"+p.filterAir+"'></td>";
						if(fleetTypeId == "2") {
							pmsTbl += "<td class='tdBorder lDateFanbelt'><input type='text' id='lDateFanbelt' readonly='readonly' class='inputPms' value='"+p.filterFanbelt+"'></td>";
							pmsTbl += "<td class='tdBorder lDateNet'><input type='text' id='net' readonly='readonly' class='inputPms' value='"+p.net+"'></td>";
						}
						pmsTbl += "</tr>";
						// End of Last Date
						hasLastDate = true;
					}
				});
			} 
			if(!hasLastDate){
				// Last Date
				pmsTbl += "<tr>";
				pmsTbl += "<td class='tdBorder' align='center' onclick='enableRow($(this).closest(&quot;tr&quot;));' style='cursor:pointer;'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
				pmsTbl += "</td>";
				pmsTbl += "<td align='center' class='subHeader lDate'>Date";
				pmsTbl += "<input type='hidden' class='hdnPmsId'>";
				pmsTbl += "<input type='hidden' class='isSchedule' value='false'>";
				pmsTbl += "<input type='hidden' class='ebObjectId'></td>";
				if(fleetTypeId == "1") {
					pmsTbl += "<td class='tdBorder lDateEngine'><input type='text' id='lDateEngine' class='inputPms'></td>";
				} else if(fleetTypeId = "2") {
					pmsTbl += "<td class='tdBorder lDateMain'><input type='text' id='lDateMain' class='inputPms'></td>";
					pmsTbl += "<td class='tdBorder lDateAuxiliary'><input type='text' id='lDateAuxiliary' class='inputPms'></td>";
				}
				pmsTbl += "<td class='tdBorder lDateTransmission'><input type='text' id='lDateTransmission' class='inputPms'></td>";
				pmsTbl += "<td class='tdBorder lDateFuel'><input type='text' id='lDateFuel' class='inputPms'></td>";
				pmsTbl += "<td class='tdBorder lDateOil'><input type='text' id='lDateOil' class='inputPms'></td>";
				pmsTbl += "<td class='tdBorder lDateAir'><input type='text' id='lDateAir' class='inputPms'></td>";
				if(fleetTypeId == "2") {
					pmsTbl += "<td class='tdBorder lDateFanbelt'><input type='text' id='lDateFanbelt' class='inputPms'></td>";
					pmsTbl += "<td class='tdBorder lDateNet'><input type='text' id='net' class='inputPms'></td>";
				}
				pmsTbl += "</tr>";
				// End of Last Date
			}
		}
	});
	pmsTbl += "</tbody>";
	// End of Body

	pmsTbl += "</table>";
	$("#pmsTable").append(pmsTbl);

	// Insert the schedule on top of the table.
	var $firstTr = $("#pmsTable").find('tbody tr:first');
	if(!hasSchedule){
		insertSchedule($firstTr);
	} else {
		$(schedRow).insertBefore($firstTr);
	}

	// Add plus [+] in the 1st row.
	var addRow = "<td onclick='appendLastDate();' style='cursor:pointer' class='addLastDate'>[+]</td>";
	var $firstLDateTr = $("#pmsTable").find('tbody tr:first').next('tr');
	$($firstLDateTr).append(addRow);
}

function insertSchedule($tr){
	// Schedule
	var schedTr = "<tr>";
	schedTr += "<td class='tdBorder' align='center' onclick='enableRow($(this).find().closest(&quot;tr&quot;));' style='cursor:pointer;'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle;' id='imgEditSched'/>";
	schedTr += "</td>";
	schedTr += "<td align='center' class='subHeader schedHdn'>Schedule";
	schedTr += "<input type='hidden' class='hdnPmsId'>";
	schedTr += "<input type='hidden' class='isSchedule' value='true'>";
	schedTr += "<input type='hidden' class='ebObjectId'></td>";
	if(fleetTypeId == "1") {
		schedTr += "<td class='tdBorder schedEngine'><input type='text' id='schedEngine' class='inputPms'></td>";
	} else if(fleetTypeId = "2") {
		schedTr += "<td class='tdBorder schedMain'><input type='text' id='schedMain' class='inputPms'></td>";
		schedTr += "<td class='tdBorder schedAuxiliary'><input type='text' id='schedAuxiliary' class='inputPms'></td>";
	}
	schedTr += "<td class='tdBorder schedTransmission'><input type='text' id='schedTransmission' class='inputPms'></td>";
	schedTr += "<td class='tdBorder schedFuel'><input type='text' id='schedFuel' class='inputPms'></td>";
	schedTr += "<td class='tdBorder schedOil'><input type='text' id='schedOil' class='inputPms'></td>";
	schedTr += "<td class='tdBorder schedAir'><input type='text' id='schedAir' class='inputPms'></td>";
	if(fleetTypeId == "2") {
		schedTr += "<td class='tdBorder schedFanbelt'><input type='text' id='schedFanbelt' class='inputPms'></td>";
		schedTr += "<td class='tdBorder net'><input type='text' id='net' class='inputPms'></td>";
	}
	schedTr += "</tr>";
	// End of Schedule
	$(schedTr).insertBefore($tr);
}

function enableRow($tr){
	$($tr).find(".inputPms").removeAttr("readonly");
}

function appendLastDate(){
	var $firstTr = $("#pmsTable").find('tbody tr:first');
	$(".addLastDate").remove();
	var trLastDate = "<tr>";
	trLastDate += "<td class='tdBorder' align='center' onclick='enableRow($(this).closest(&quot;tr&quot;));' style='cursor:pointer;'><img src='../../CMS/images/pen.png' height='15' width='15' style='vertical-align: middle; padding:5px;' id='imgEditLastDate'/>";
	trLastDate += "</td>";
	trLastDate += "<td align='center' class='subHeader lDate'>Date";
	trLastDate += "<input type='hidden' class='hdnPmsId'>";
	trLastDate += "<input type='hidden' class='isSchedule' value='false'>";
	trLastDate += "<input type='hidden' class='ebObjectId'></td>";
	if(fleetTypeId == "1") {
		trLastDate += "<td class='tdBorder lDateEngine'><input type='text' id='lDateEngine' class='inputPms'></td>";
	} else if(fleetTypeId == "2") {
		trLastDate += "<td class='tdBorder lDateMain'><input type='text' id='lDateMain' class='inputPms'></td>";
		trLastDate += "<td class='tdBorder lDateAuxiliary'><input type='text' id='lDateAuxiliary' class='inputPms'></td>";
	}
	trLastDate += "<td class='tdBorder lDateTransmission'><input type='text' id='lDateTransmission' class='inputPms'></td>";
	trLastDate += "<td class='tdBorder lDateFuel'><input type='text' id='lDateFuel' class='inputPms'></td>";
	trLastDate += "<td class='tdBorder lDateOil'><input type='text' id='lDateOil' class='inputPms'></td>";
	trLastDate += "<td class='tdBorder lDateAir'><input type='text' id='lDateAir' class='inputPms'></td>";
	if(fleetTypeId == "2") {
		trLastDate += "<td class='tdBorder lDateFanbelt'><input type='text' id='lDateFanbelt' class='inputPms'></td>";
		trLastDate += "<td class='tdBorder lDateNet'><input type='text' id='net' class='inputPms'></td>";
	}
	trLastDate += "<td onclick='appendLastDate();' style='cursor:pointer' class='addLastDate'>[+]</td>";
	trLastDate += "</tr>";
	$(trLastDate).insertAfter($($firstTr));
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJsonPms").val());
	var cPath = "${pageContext.request.contextPath}";
	$fleetPmsRefDocsTbl = $("#fleetPmsRefDocsTbl").editableItem({
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

	$("#fleetPmsRefDocsTbl").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize,$("#referenceDocsMgs"), $("#fleetPmsRefDocsTbl"));
	});

	$("#fleetPmsRefDocsTbl tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#fleetPmsRefDocsTbl").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function savePmsForm(){
	if(isSaving == false && $("#referenceDocsMgs").html() == "" && !checkExceededFileSize($("#fleetPmsRefDocsTbl"))) {
		isSaving = true;
		var tempFleetTypeId = fleetTypeId;
		$("#hdnFdRefObjectId").val(fpRefObjectId);
		$("#referenceDocumentsJsonPms").val($fleetPmsRefDocsTbl.getData());
		$("#tblPms tbody input").each(function(row) {
			$(this).attr('value',$(this).val());
		});
		var tblPms = $("#pmsTable").html();
		$("#fleetPmslJson").val(buildPmsJson());
		$("#btnSavePms").attr("disabled", "disabled");
		doPostWithCallBack ("pmsForm", "divPMS", function (data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							var url = contextPath + "/fleetPms/?refObjectId="+fpRefObjectId;
							$("#divPMS").load(url);
						}
					}
				});
			} else {
				$("#pmsFormId").html(data);
				$("#pmsTable").html(tblPms);
			}
			isSaving = false;
			$("#btnSavePms").removeAttr("disabled");
		});
	} else if (checkExceededFileSize($("#fleetPmsRefDocsTbl"))) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}
</script>
<title>Fleet PMS Form</title>
</head>
<body>
	<div class="formDivBigForms" id="pmsFormId" >
		<form:form method="POST" commandName="fleetPmsDto" id="pmsForm">
			<form:hidden path="fpEbObjectId" id="hdnFdRefObjectId"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJsonPms" />
			<form:hidden path="fleetPmslJson" id="fleetPmslJson" />
			<form:hidden path="fleetTypeId" id="fleetTypeId"/>
			<fieldset class="frmField_set">
				<legend>Preventive Maintenance Schedule</legend>
				<div id="pmsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<form:errors path="fleetPmsErrMsg" cssClass="error"
								style="margin-top: 12px;" />
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set">
				<legend>Documents Table</legend>
				<div id="fleetPmsRefDocsTbl"></div>
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
						<td align="right" colspan="2"><input type="button" id="btnSavePms"
							value="Save" onclick="savePmsForm();" />
						</td>
					</tr>
				</table>
		</form:form>
	</div>
</body>
</html>