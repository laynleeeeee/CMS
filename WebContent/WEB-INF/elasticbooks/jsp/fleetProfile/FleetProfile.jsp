<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Profile
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${pageContext.request.contextPath}/js/jquery/jquery.js" type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shCore.css" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shThemeDefault.css" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery/sdd/sh/shThemeDefault.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/tabs.css" media="all">
<link href="${pageContext.request.contextPath}/css/jquery-ui-1.10.3.css" rel="stylesheet" type="text/css"  />
<link href="${pageContext.request.contextPath}/css/status.css" rel="stylesheet" type="text/css"  />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.employeehandler.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/webcam.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tabsUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.lightbox_me.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js" type="text/javascript" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js" type="text/javascript" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.3.js" type="text/javascript"></script>
<style type="text/css">
/* Style the tab */
div.tab {
    overflow: hidden;
    border: 1px solid #ccc;
    background-color: #f1f1f1;
}

/* Style the buttons inside the tab */
div.tab button {
    background-color: inherit;
    float: left;
    border: none;
    outline: none;
    cursor: pointer;
    padding: 14px 16px;
    transition: 0.3s;
}

/* Change background color of buttons on hover */
div.tab button:hover {
    background-color: #ddd;
}

/* Create an active/current tablink class */
div.tab button.active {
    background-color: #ccc;
}

/* Style the tab content */

.tablinks{
	font-weight: bold;
}

.tabcontent {
	display: none;
	padding: 1px 1px;
	border-top: none;
	width: 98%;
	margin: 0 auto;
}
</style>
<script type="text/javascript">
var FT_CONSTRUCTION = 1;
var FT_FISHING = 2;
var fpRefObjectId = Number("${fpRefObjectId}");
var ftRefObjectId = Number("${ftRefObjectId}");

$(document).ready(function () {
	if (fpRefObjectId == 0 || fpRefObjectId == "") {
		$("#divFRBody").hide();
	} else {
		$("#divFRBody").show();
		$("#btnDriver").trigger("click");
	}
});

function showClosingDialog() {
	$("#successDialog").dialog({
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog("close");
				$(this).dialog("destroy");
			}
		}
	});
}

function saveFleetProf() {
	setFleetTypeId();
	doPostWithCallBack ("fleetProfileForm", "divFleetProfileForm", function(data) {
		var parsedData = data.split(";");
		var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
		if(isSuccessfullySaved) {
			$("#divFleetProfileForm").html("");
			$("#divFleetProfileForm").trigger('close');
			$("#successDialog").dialog({
				modal: true,
				buttons: {
					Close: function() {
						$(this).dialog("close");
						$(this).dialog("destroy");
						window.location.href = contextPath + "/fleetProfile/" + parsedData[2];
					}
				}
			});
		} else {
			var selectedRef = $("#refObjectId").val();
			var driverName = $("#txtDriver").val();
			$("#divFleetProfileForm").html(data);
			$("#refObjectId").val(selectedRef);
			$("#txtDriver").val(driverName);
			changeFormMode($("#refObjectId"));
		}
		isSaving = false;
	});
}

function showFleets () {
	var uri = contextPath + "/fleetProfile/searchFleet?codeVesselName="+$.trim($("#txtSearchFleet").val());
	$("#txtSearchFleet").autocomplete({
		source: uri,
		select: function( event, ui ) {
			var codeVesselName = ui.item.codeVesselName;
			window.location.replace(contextPath + "/fleetProfile/" + (ui.item.id));
			$(this).val(codeVesselName);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanFleetError").text("");
					if (ui.item != null) {
						var codeVesselName = ui.item.codeVesselName;
						$(this).val(codeVesselName);
					}
				},
				error : function(error) {
					$("#txtSearchFleet").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var codeVesselName = item.codeVesselName;
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + codeVesselName + "</a>" )
			.appendTo( ul );
	};
}

function setFleetTypeId() {
	var fleetTypeId = Number($("#refObjectId").val().split("-")[0]);
	$("#hdnFleetTypeId").val(fleetTypeId);
}

function loadForm(fleetProfileId) {
	var path = contextPath + "/fleetProfile/form";
	if (fleetProfileId != "") {
		path = contextPath + "/fleetProfile/" + fleetProfileId + "/form";
	}
	$("#divFleetProfileForm").load(path, function (data) {
		$("#divFleetProfileForm").lightbox_me({
			closeSelector: "#btnClose, #btnCancelFleetProfile",
			centered: true,
			onClose: function() {
				$("#divFleetProfileForm").html("");
			}
		});
		//Set background color of the popup
		$("#divFleetProfileForm").css("background-color", "#FFF");
		updatePopupCss();
		//For initial loading of pop-up form.
		$("#btnClose").css("cursor","pointer");
		$("#btnClose").css("float","right");
	});
}

function clickTab(evt, divId) {
	if (typeof evt.currentTarget == "undefined") {
		evt.currentTarget =  document.getElementById("btnDriver");
	}
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(divId).style.display = "block";
    evt.currentTarget.className += " active";
}

function loadTab(divId, uri, evt) {
	clickTab(evt, divId);
	if ($.trim($("#" + divId).html()) == "") {
		var url = contextPath + "/" + uri + (fpRefObjectId != "" ? "?refObjectId="+fpRefObjectId : "");
		$("#" + divId).load(url);
	}
}

function fleetTypeOnChange($elem) {
	var fleetCategoryId = Number($($elem).val().split("-")[1]);
	if (fleetCategoryId == 1) {
		$("#codeVesselErrMsgId").html("");
		$("#dateErrMsgId").html("");
		$("#officialNoErrMsgId").html("");
		$("#callSignErrMsgId").html("");
		$("#tonnageErrMsgId").html("");
		$("#engineNoErrMsgId").html("");
		$("#descErrMsgId").html("");
		$("#vmsErrMsgId").html("");
		$("#propellerErrMsgId").html("");
		$("#winchErrMsgId").html("");
		$("#captErrMsgId").html("");
	} else {
		$("#codeVesselErrMsgId").html("");
		$("#dateErrMsgId").html("");
		$("#makeErrMsgId").html("");
		$("#modelErrMsgId").html("");
		$("#chassisNoErrMsgId").html("");
		$("#plateNoErrMsgId").html("");
		$("#engineNoErrMsgId").html("");
		$("#descErrMsgId").html("");
		$("#driverErrMsgId").html("");
		$("#supplierErrMsgId").html("");
	}
}

function changeFormMode($combobox) {
	//This will be the default configuration for the client.
	//Only the construction fleet type will be used.
	var fleetCategoryId = 1;
	if (fleetCategoryId == 1) {
		$(".trFishing").hide();
		$(".trConstruction").show();
		$("#tdCode").text("* Code");
	} else {
		$(".trConstruction").hide();
		$(".trFishing").show();
		$("#tdCode").text("* Vehicle Name");
	}
}
</script>
<title>Fleet Profile</title>
</head>
<body>
<br />
<div id="divPRMain">
	<div id="successDialog" style="display: none;">
		<p align="center" style="font-size: small; font-style: italic;">
				Successfully updated
		</p>
	</div>
	<div id="divPRHeader" style="margin: 0 auto; width: 90%; 
		text-align: center;">
		<input type="button" id="btnNewFleet" value="+" onclick="loadForm('');" style="font-weight: bold; font-size: 14px;"/>
		<input id="txtSearchFleet" onkeydown="showFleets();" onkeyup="showFleets();" onclick="clear();" style="width: 300px;" placeholder="Search"/>
	</div>
	<br />
	<div>
	<div id="divFRBody">
		<table style="margin: 0 auto; width: 90%; ">
			<tr>
				<td>
					<div id="divFleetProfileRecord" style=" padding-top: 10px;">
						<%@ include file="FleetProfileRecord.jsp"%>
					</div>
				</td>
			</tr>
			<tr>
				<td style="padding-left: 7px;">
					<input type="button" id="btnEdit" onclick="loadForm('${fleetProfileId}');" value="Edit" />
				</td>
			</tr>
		</table>
		<br />
		<div id="divPPTabs" style="margin: 0 auto; width: 90%" class="tab">
			<button class="tablinks" onclick="loadTab('divDriver', 'fleetDriver', event);" id="btnDriver">Driver</button>
			<c:if test="${fleetProfile.fleetType.fleetCategoryId == 2}">
				<button class="tablinks" onclick="loadTab('divCaptain', 'captainMdm', event);">BC/MDM</button>
				<button class="tablinks" onclick="loadTab('divManningRequirements', 'manningRequirements', event);">Manning Requirements</button>
			</c:if>
			<button class="tablinks" onclick="loadTab('divInsurance', 'insurancePermitRenewal', event);">Insurance, Permits, and Renewals</button>
			<c:if test="${fleetProfile.fleetType.fleetCategoryId == 2}">
				<button class="tablinks" onclick="loadTab('divVoyages', 'voyage', event);">Voyages</button>
				<button class="tablinks" onclick="loadTab('divDryDocking', 'dryDock', event);">Dry Docking</button>
			</c:if>
			<button class="tablinks" onclick="loadTab('divPMS', 'fleetPms', event);">PMS</button>
			<button class="tablinks" onclick="loadTab('divAttributableCost', 'fleetAttributableCost', event);" id="btnAttributableCost">Attributable Cost</button>
			<button class="tablinks" onclick="loadTab('divTools', 'fleetTool', event)">Tools</button>
			<button class="tablinks" onclick="loadTab('divIncident', 'fleetIncident', event);">Incident</button>
		</div>

		<div id="divAttributableCost" class="tabcontent"></div>
		<div id="divDriver" class="tabcontent"></div>
		<div id="divCaptain" class="tabcontent"></div>
		<div id="divInsurance" class="tabcontent"></div>
		<div id="divTools" class="tabcontent"></div>
		<div id="divIncident" class="tabcontent"></div>
		<div id="divPMS" class="tabcontent"></div>
		<div id="divManningRequirements" class="tabcontent"></div>
		<div id="divDryDocking" class="tabcontent"></div>
		<div id="divVoyages" class="tabcontent"></div>

		<div id="divFleetProfileForm"></div>
	</div>

</div>
</div>
</body>
</html>