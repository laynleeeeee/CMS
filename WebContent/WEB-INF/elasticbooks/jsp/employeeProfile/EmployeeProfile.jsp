<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Employee Profile main jsp page for GVCH
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
#divPPMain {
    width: 80%;
    top:0;
    bottom: 0;
    left: 0;
    right: 0;
    margin: auto;
}

.hrSeparator {
	border: 1px solid black;
	padding-top: 0px;
	margin-top: 0px;
}

.tblDetails {
	width: 90%;
	table-layout:fixed;
	border-color: black;
	border-collapse: collapse;
	border-right: 1px solid #F5F2EF;
	font-size: 13px;
}
.tblDetails thead, .tblDetails th {
	background: #000000;
	color: #FFFFFF;
}

.tblDetails thead, .tblDetails th, .tblDetails tbody, .tblDetails td {
	padding: 2px;
	border : 1px solid #000;
}

.tblDetails td {
	height: 25px;
}

.tblDetails td table, .tblDetails tfoot, .tblDetails tfoot td, .tblDetails td table tbody, .tblDetails td table td {
    border:none;
}

.tblDetails tr.highlight td {
	background-color: #F0F0F0;
}

.tblDetails td.timeClass{
	text-align: right;
}

.tblDetails td.numberClass {
	text-align: right;
}

#divViewPhotoContainer {
	border: 1px solid green;
	height: 300px;
	width: 320px;
	float: right;
}

.content {
	padding-left: 20px;
}

.patient_labels{
	text-align: right;
	width: 160px;
	font-size: 14;
	font-weight: bold;
}

.address_labels {
	font-size: 14;
	font-weight: bold;
}

.tdLabels {
	white-space: nowrap;
	font-size: 14;
	font-weight: bold;
}

.tdContent {
	white-space: nowrap;
	padding-left: 20px;
	padding-right: 20px;
}

.trSpacing {
   height:7px; 
   visibility:hidden;
}

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
.tabcontent {
    display: none;
    padding: 6px 12px;
    margin: 0 auto;
    width: 70%
}

.tablinks{
	font-weight: bold;
}

.eePhoto{
	height: 300px; 
	width: 320px;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	loadSavedPhoto();
	if ("${reload}" == "1") {
		$("#liHistory").trigger("click");
		$("#liHistory").trigger("click");
	}
	if("${employeeProfile.employeeId}" > 0) {
		$("#tabEmployeeProfileId").trigger('click');
	}
});

function loadSavedPhoto() {
	var photo = "${employeeProfile.referenceDocument.file}";
	$("#divViewPhotoContainer").html('<img src="' + photo + '" class="eePhoto"/>');
}

function initCamera() {
	Webcam.set({
			width: 320,
			height: 300,
			image_format: 'jpeg',
			jpeg_quality: 400,
			force_flash: true
	});
	Webcam.attach( '#divCamera' );
}

function takePhoto (button) {
	var btnVal = $.trim($(button).val());
	if (btnVal == "Take") {
		initCamera();
		$(button).val("Capture");
	} else if (btnVal == "Capture") {
		Webcam.snap( function(data_uri) {
			// display results in page
			document.getElementById('divCamera').innerHTML = 
				'<img src="'+data_uri+'" class="eePhoto"/>';
			$("#photo").val(data_uri);
			$("#fileName").val("WebcamCapture_" + "${employeeId}");
			$("#description").val("Webcam Capture " + "${employeeId}");
		});
		$(button).val("Take");
	}
}

function uploadPhoto () {
	var $btnPhoto = $("#btnSnapshot");
	$($btnPhoto).val("Take");
	$("#divCamera").html("");
	$("#fileData").trigger("click");
}

function loadPhotoToDiv(fileObj) {
	var value = $.trim($(fileObj).val());
	if (value != "") {
		value = value.replace(/C:\\fakepath\\/i, '');
		$("#fileName").val(value);
		$("#fileSize").val($(fileObj)[0].size);
		convertImgToBase64($(fileObj)[0], $("#divCamera"));
	}
}

function convertImgToBase64(input, div) {
	if ( input.files && input.files[0] ) {
        var FR= new FileReader();
        FR.onload = function(e) {
			$(div).html('<img src="'+e.target.result+'" class="eePhoto"/>');
			$("#photo").val(e.target.result);
        };
        $("#fileSize").val(input.files[0].size);
        FR.readAsDataURL( input.files[0] );
    }
}

var isSaving = false;
function saveEmployeeProf(formPage) {
	$("#hdnFormPage").val(formPage);
	if(isSaving == false) {
		isSaving = true;
		$("#btnSaveEmployeeProfile").attr("disabled", "disabled");
		if(formPage == 1) {
			$("#employeeChildrenJson").val($childrenTable.getData());
		} else if(formPage == 3) {
		} else if(formPage == 4) {
			$("#eeLicenseCertJson").val($licencesCertTbl.getData());
			$("#eeSeminarAttendedJson").val($seminarAttendedTbl.getData());
			$("#eeNationalCompetencyJson").val($nationalCompetencyTbl.getData());
			$("#eeEmerContactJson").val($eeEmerContactTbl.getData());
		}
		doPostWithCallBack ("employeeProfileForm", "divEmployeeProfileForm", function(data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				var employeeProfileId = parsedData[1];
				var employeeId = parsedData[2];
				var createdBy = parsedData[3];
				var ebObjectId = parsedData[4];
				$("#hdnId").val(employeeProfileId);
				$("#hdnEPEmployeeId").val(employeeId);
				$("#hdnEmployeeId").val(employeeId);
				$("#hdnEPCreatedBy").val(createdBy);
				$("#hdnEECreatedBy").val(createdBy);
				$("#ebObjectId").val(ebObjectId);
				isSaving = false;
				if (formPage == 1) {
					$("#divEPBasicInfoForm").hide("fast");
					$("#divEPEducForm").show("fast");
				} else if(formPage == 3) {
					$("#divEPEducForm").hide("fast");
					$("#divEPLastForm").show("fast");
				} else {
					$("#divEmployeeProfileForm").html("");
					$("#divEmployeeProfileForm").trigger('close');
					showSuccess(employeeId);
				}
			} else {
				$("#divEmployeeProfileForm").html(data);
				var photo = $("#photo").val();
				$("#divCamera").html('<img src="'+photo+'" class="eePhoto"/>');
				if($("#hdnPositionId").val() == "" || typeof $("#hdnPositionId").val() == undefined) {
					$("#txtPosition").val("");
				}
				isSaving = false;
			}
		});
		$("#btnSaveEmployeeProfile").removeAttr("disabled");
	}
}

function changeFocus(elemId) {
	$("#" + elemId).focus();
}

function loadPRecordForm(employeeId) {
	var path = contextPath + "/employeeProfile/form";
	if (employeeId != "") {
		path = contextPath + "/employeeProfile/" + employeeId + "/form";
	}
	$("#divEmployeeProfileForm").load(path, function (data) {
		$("#divEmployeeProfileForm").lightbox_me({
			closeSelector: "#btnClose, #btnCancelPatientProfile",
			centered: true,
			onClose: function() {
				$("#divEmployeeProfileForm").html("");
			}
		});
		//Set background color of the popup
		$("#divEmployeeProfileForm").css("background-color", "#FFF");
		updatePopupCss();
		//For initial loading of pop-up form.
		$("#btnClose").css("cursor","pointer");
		$("#btnClose").css("float","right");
		if (employeeId != "") {
			var photo = "${employeeProfile.referenceDocument.file}";
			$("#divCamera").html('<img src="' + photo + '" class="eePhoto" />');
		}
	});
}

function showEmployees () {
	var uri = contextPath + "/getEmployees/byName?name="+$.trim($("#txtSearchEmployee").val());
	$("#txtSearchEmployee").autocomplete({
		source: uri,
		select: function( event, ui ) {
			window.location.replace(contextPath + "/employeeProfile/" + (ui.item.id));
			var employeeName = ui.item.fullName;
			$(this).val(employeeName);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					$("#spanEmployeeError").text("");
					if (ui.item != null) {
						var employeeName = ui.item.fullName
						$(this).val(employeeName);
					}
				},
				error : function(error) {
					$("#txtSearchEmployee").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		var employeeName = item.fullName;
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" + employeeName + "</a>" )
			.appendTo( ul );
	};
}

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

function showSuccess(employeeId) {
	showClosingDialog();
	$("#successDialog").dialog({
		modal: true,
		buttons: {
			Close: function() {
				$(this).dialog("close");
				$(this).dialog("destroy");
				var uri = contextPath + "/employeeProfile/";
				if (employeeId != "0") {
					uri += employeeId;
				}
				window.location.href = uri; 
			}
		}
	});
}

function printProfile(employeeId) {
	var url = contextPath + "/employeeProfile/" + employeeId + "/print";
	window.open(url, "_blank");
}

function clickTab(evt, divId) {
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
}

</script>
<title>Employee's Profile</title>
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
		<input type="button" id="btnNewEmployee" value="+" onclick="loadPRecordForm('');" style="font-weight: bold; font-size: 14px;"/>
		<input id="txtSearchEmployee" onkeydown="showEmployees();" style="width: 300px;"/>
	</div>
	<br/>
	<div style="padding-top: 10px;">
		<table class="formTable" style="width: 90%; margin: 0 auto;" >
			<tr>
				<td style="width: 10%;" valign="top">
					<img alt="" src="" />
				</td>
				<td style="width: 65%;" valign="top">
					<table class="formTable" style="width: 85%;" >
						<tr>
							<td class="patient_labels">Company</td>
							<td class="content">${employeeProfile.employee.company.name} </td>
						</tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Department</td>
							<td class="content">${employeeProfile.employee.division.name} </td>
						</tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Name</td>
							<td class="content">${employeeProfile.employee.firstName} ${employeeProfile.employee.middleName} 
								${employeeProfile.employee.lastName}</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Employee ID</td>
							<td class="content">${employeeProfile.employee.employeeNo} </td>
						</tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Biometric ID</td>
							<td class="content">${employeeProfile.employee.biometricId} </td>
						</tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">RFID</td>
							<td class="content">${employeeProfile.rfid}</td>
						</tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Position</td>
							<td class="content">${employeeProfile.employee.position.name}</td>
						</tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Address</td>
							<td class="tdContent">${employeeProfile.permanentAddress}</td>
					 	</tr>
					 	<tr class="trSpacing"></tr>
					 	<tr>
							<td class="patient_labels">Contact Number</td>
							<td class="tdContent">${employeeProfile.employee.contactNo}</td>
					 	</tr>
					 	<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Blood Type</td>
							<td class="content">${employeeProfile.bloodType}</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">TIN No.</td>
							<td class="content">${employeeProfile.tin}</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Philhealth No.</td>
							<td class="content">${employeeProfile.philhealthNo}</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">SSS No.</td>
							<td class="content">${employeeProfile.sssNo}</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">HDMF No.</td>
							<td class="content">${employeeProfile.hdmfNo}</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Date Hired</td>
							<td class="content">
								<fmt:formatDate pattern="MM/dd/yyyy"  value="${employeeProfile.hiredDate}" />
							</td>
						 </tr>
						<tr class="trSpacing"></tr>
						
						<tr>
							<td class="patient_labels">Employment Period</td>
							<td class="content">
								<fmt:formatDate pattern="MM/dd/yyyy"  value="${employeeProfile.employmentPeriodFrom}" />
								To
								<fmt:formatDate pattern="MM/dd/yyyy"  value="${employeeProfile.employmentPeriodTo}" />
							</td>
						 </tr>
						<tr class="trSpacing"></tr>
						<tr>
							<td class="patient_labels">Employee Status</td>
							<td class="content">${employeeProfile.employee.employeeStatus.name}</td>
						</tr>
						<tr class="trSpacing"></tr>
					</table>
				</td>
				<td width="1px;"> </td>
				<td valign="top" align="center">
					<div id="divViewPhotoContainer"></div>
				</td>
			</tr>
		</table>
	</div>
	<br>
	<div id="divPPTabs" style="margin: 0 auto; width: 70%" class="tab">
		<button class="tablinks" onclick="clickTab(event, 'divTabEmployeeProfile');" id="tabEmployeeProfileId">Employee Profile</button>
		<button class="tablinks" onclick="clickTab(event, 'divTabFileDoc');">Documents/Filed Forms</button>
		<button class="tablinks" onclick="clickTab(event, 'divTabDTR')">DTR</button>
	</div>
	<div id="divTabEmployeeProfile" class="tabcontent">
		<%@ include file="EmployeeBasicInfo.jsp"%>
	</div>
	<div id="divTabFileDoc" class="tabcontent">
		<%@ include file="EmployeeFormDocument.jsp"%>
	</div>
	<div id="divTabDTR" class="tabcontent">
		<%@ include file="EmployeeDTR.jsp"%>
	</div>
	<div id="divEmployeeProfileForm"></div>
</div>
</body>
</html>