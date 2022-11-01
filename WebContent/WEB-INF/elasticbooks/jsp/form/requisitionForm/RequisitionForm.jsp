<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Requesition form.
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/popupModal.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.requisitionitemhandler.js"></script>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var $documentsTable = null;
var $requisitionFormItemsTable = null;
var REQ_TYPE_ID = $("#hdnRequisitionTypeId").val();
var RT_FUEL = 2;
var projectName = "${requisitionFormDto.requisitionForm.arCustomer.name}";
var requsitionFormId = "${requisitionFormDto.requisitionForm.id}";
var fleetProfileId = "${requisitionFormDto.requisitionForm.fleetProfileId}";
var codeVesselName = "${requisitionFormDto.requisitionForm.fleetProfile.codeVesselName}";
var arCustomerId = "${requisitionFormDto.requisitionForm.arCustomerId}";
var savedWarehouseId = "${requisitionFormDto.requisitionForm.warehouseId}";
$(document).ready(function() {
	filterWarehouse(savedWarehouseId);
	initializeDocumentsTbl();
	initItemTble();
	if (requsitionFormId > 0) {
		if (fleetProfileId != null && fleetProfileId != "") {
			$("#fleetProfileId").val(codeVesselName);
		}
	}

	// Disable fields of the status of the form is complete or cancelled
	if ("${requisitionFormDto.requisitionForm.formWorkflow.complete}" == "true"
			|| "${requisitionFormDto.requisitionForm.formWorkflow.currentStatusId}" == 4) {
		$("#requisitionForm :input").attr("disabled","disabled");
	}
});
function initItemTble() {
	var requisitionFormItemsJson = JSON.parse($("#requisitionFormItemsJson").val());
	setupItemTbl(requisitionFormItemsJson);
}

function setupItemTbl(rfisJson) {
	$("#requisitionFormItemsTable").html("");
	var cPath = "${pageContext.request.contextPath}";
	$requisitionFormItemsTable = $("#requisitionFormItemsTable").editableItem({
		data: rfisJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "origQty", "varType" : "double"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "quantity", "varType" : "int"}
		],
		contextPath: cPath,
		header : [
			{"title" : "id",
				"cls" : "id",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "itemId",
				"cls" : "itemId",
				"editor" : "hidden",
				"visible" : false},
			{"title" : "Stock Code",
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible": true,
				"width" : "10%",
				"handler" : new RQItemTableHandler(new function() {
					this.handleTotal = function(total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible": true,
				"width" : "15%"},
			{"title" : "Existing <br> Stocks",
				"cls" : "existingStock tblLabelNumeric",
				"editor" : "label",
				"visible" : true,
				"width" : "7%"},
			{"title" : "Qty", 
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM", 
				"cls" : "uom txtUOM",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" },
			{"title" : "origQty",
				"cls" : "origQty",
				"editor" : "hidden",
				"visible" : false}
			],
			"disableDuplicateStockCode" : false,
			"itemTableMessage": "errItemsMsg"
	});

	$("#requisitionFormItemsTable").on("blur", ".stockCode", function() {
		if ($.trim($(this).val()) != "") {
			var row = $(this).closest("tr");
			populateExistingStocks(row);
		}
	});
}

function populateExistingStocks(row) {
	var stockCode = $.trim($(row).find(".stockCode").val());
	if (stockCode != "") {
		var companyId = $("#companyId").val();
		var warehouseId = $("#warehouseId option:selected").val();
		var uri = contextPath + "/getItem/existingStockBySC?stockCode="+encodeURIComponent(stockCode)
				+"&companyId="+companyId;
		if (warehouseId != null && warehouseId > 0) {
			uri += "&warehouseId="+warehouseId;
		}
		$.ajax({
			url: uri,
			success : function(existingStocks) {
				var formatES = accounting.formatMoney(existingStocks);
				$(row).find(".existingStock").html(formatES);
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "text"
		});
	}
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentJson").val());
	var cPath = "${pageContext.request.contextPath}";
	$documentsTable = $("#documentsTable").editableItem({
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
			{"title" : "fileName",
				"cls" : "fileName",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Name",
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
			{"title" : "File",
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

	$("#documentsTable").on("change", ".fileInput", function(){
		var $fileSize = $(this).closest("tr").find(".fileSize");
		var fileSize = this.files[0].size;
		$($fileSize).val(fileSize);
		convertDocToBase64($(this), fileSize, $("#referenceDocsMgs"), $("#documentsTable"));
	});

	$("#documentsTable tbody tr").each(function(){
		var fileName = $.trim($(this).find(".fileName").val());
		if (fileName != "0") {
			$(this).find(".docName").html("");
			var $td = $(this).find(".docName").parent("td");
			$($td).append("<a href='#' class='fileLink'>" + fileName + "</a>");
		}
	});

	$("#documentsTable").on("click", ".fileLink", function(){
		var $document = $(this).closest("tr").find(".file");
		var fileName = $.trim($($document).closest("tr").find(".fileName").val());
		convBase64ToFile($($document).val(), fileName);
	});
}

var isSaving = false;
function saveRequisitionForm() {
	$("#companyId").removeAttr("disabled");
	var refDocErrorMsg = $("#referenceDocsMgs").text();
	var hasExceededFileSize = checkExceededFileSize($("#documentsTable"));
	if(isSaving == false && refDocErrorMsg == "" && !hasExceededFileSize) {
		isSaving = true;
		$("#requisitionFormItemsJson").val($requisitionFormItemsTable.getData());
		$("#referenceDocumentJson").val($documentsTable.getData());
		doPostWithCallBack ("requisitionForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var companyId = $("#companyId").val();
				var fleetProfileCode = $.trim($("#fleetProfileId").val());
				var projectName = $.trim($("#projectId").val());
				var status = $("#txtReqFormStatus").val();
				var hdnArCustomerId = $("#hdnArCustomerId").val();
				var hdnFleetProfileId = $("#hdnFleetProfileId").val();
				var warehouseId = $("#warehouseId").val();
				if (requsitionFormId == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
					$("#fleetProfileId").val(fleetProfileCode);
					$("#projectId").val(projectName);
				} else {
					dojo.byId("editForm").innerHTML = data;
					updatePopupCss();
				}
				$("#tdErrRfFleet").show();
				$("#projectId").val(projectName);
				filterWarehouse(warehouseId);
				$("#txtReqFormStatus").val(status);
				initItemTble();
				warehouseOnChange();
				initializeDocumentsTbl();
				clearValues(hdnArCustomerId, hdnFleetProfileId);
				isSaving = false;
			}
			isSaving = false;
			$("#btnSaveRequisitionForm").removeAttr("disabled");
		});
	} else if(hasExceededFileSize) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function clearValues(hdnArCustomerId, hdnFleetProfileId) {
	if(hdnArCustomerId == "") {
		$("#projectId").val("");
	}
	if(hdnFleetProfileId == ""){
		$("#fleetProfileId").val("");
	}
}

// For now, as was discussed with the team this will be the input for the project
function getCustomers() {
	var companyId = $("#companyId").val();
	var projectId = $.trim($("#projectId").val());
	var uri = contextPath + "/getArCustomers?name="+encodeURIComponent(projectId)+"&companyId="+companyId;
	$("#projectId").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#hdnArCustomerId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function setCustomerId() {
	$("#spanProjectErrorMsg").text("");
	var companyId = $("#companyId").val();
	var customerName = $.trim($("#projectId").val());
	var hdnCustomerId = $("#hdnArCustomerId").val();
	var uri = "/getArCustomers?name="+encodeURIComponent(customerName)
			+"&companyId="+companyId+"&isExact=true";
	if (hdnCustomerId != "" && hdnCustomerId != null) {
		uri += "&customerId="+hdnCustomerId;
	}
	$.ajax({
		url: contextPath + uri,
		success : function(customer) {
			if (customer != null && customer[0] != undefined) {
				$("#hdnArCustomerId").val(customer[0].id);
				$("#projectId").val(customer[0].name);
			} else {
				$("#hdnArCustomerId").val("");
				if (customerName != "") {
					$("#errRfProjId").text("");
					$("#spanProjectErrorMsg").text("Invalid project id.");
				}
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function formatMoney($input) {
	$($input).val(accounting.formatMoney($($input).val()));
}

function computeLiters() {
	var distance = accounting.unformat($("#txtDistance").val());
	var ratio = accounting.unformat($("#selRatioId").val());
	var liters = distance / ratio;

	$("#hdnLiters").val(liters);
	$("#txtDistance").val(accounting.formatMoney(distance));
	$("#tdLiters").text(accounting.formatMoney(liters));
}

function setRatioId() {
	$("#hdnRatioId").val($("#selRatioId option:selected").attr("id"));
}

function warehouseOnChange() {
	$("#requisitionFormItemsTable tbody tr").each(function() {
		populateExistingStocks(this);
	});
}

function clearFieldsOnCompanyChange() {
	$("#fleetProfileId").val("");
	$("#projectId").val("");
	$("#requestedBy").val("");
	$("#requisitionFormItemsTable").html("");
	filterWarehouse();
	initItemTble();
}

function filterWarehouse(warehouseId) {
	var selectedCompanyId = $("#companyId").val();
	if (selectedCompanyId > 0) {
		$("#warehouseId").empty();
		var uri = contextPath+"/getWarehouse/withInactive?companyId="+selectedCompanyId+"&itemId=0";
		if (warehouseId != null && typeof warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		var optionParser = {
			getValue: function (rowObject) {
				return rowObject["id"];
			},

			getLabel: function (rowObject) {
				return rowObject["name"];
			}
		};

		postHandler = {
				doPost: function() {
					// This is to remove any duplication.
					var found = [];
					$("#warehouseId option").each(function() {
						if($.inArray(this.value, found) != -1) 
							$(this).remove();
					  	found.push(this.value);
					});
					if (warehouseId != null && typeof warehouseId != undefined) {
						$("#warehouseId").val(warehouseId);
					}
				}
		};
		loadPopulate (uri, true, warehouseId, "warehouseId", optionParser, postHandler);
	}
}

function getFleetProfiles() {
	var companyId = $("#companyId").val();
	var codeVesselName = encodeURIComponent($.trim($("#fleetProfileId").val()));
	var uri = "/getFleetProfile?companyId=&codeVesselName="+codeVesselName+"&isExact=false&isActive=true";
	$("#fleetProfileId").autocomplete({
		source: contextPath + uri,
		select: function( event, ui ) {
			$("#hdnFleetProfileId").val(ui.item.id);
			$(this).val(ui.item.codeVesselName);
			return false;
		}, minLength: 2
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.codeVesselName + "</a>" )
			.appendTo( ul );
	};
}

function setFleetProfile() {
	var companyId = $("#companyId").val();
	var codeVesselName = encodeURIComponent($.trim($("#fleetProfileId").val()));
	$("#spanFleetProfile").text("");
	if(!$("#fleetProfileId").prop('readonly')){
		if (codeVesselName != "" && typeof codeVesselName != "undefined") {
			var uri = "/getFleetProfile?companyId=&codeVesselName="+codeVesselName+"&isExact=true";
			$.ajax({
				url: contextPath + uri,
				success : function(fleetProfile) {
					if (fleetProfile != null && fleetProfile != undefined) {
						$("#hdnFleetProfileId").val(fleetProfile.id);
						$("#fleetProfileId").val(fleetProfile.codeVesselName);
					}
				},
				error : function(error) {
					console.log(error);
					$("#tdErrRfFleet").hide();
					$("#hdnFleetProfileId").val("");
					$("#spanFleetProfile").text("Invalid fleet code.");
				},
				dataType: "json"
			});
		} else {
			$("#hdnFleetProfileId").val("");
			$("#fleetProfileId").val("");
		}
	}
}

function showWorkOrderRefForm() {
	$("#divWOSOReferenceId").html("");
	$("#divWOSOReferenceId").load(contextPath+"/requisitionForm/showWorkOrderRefForm");
}


function loadRefWorkOrder(refWorkOrderId) {
	$.ajax({
		url: contextPath + "/requisitionForm/loadRefWorkOrder?refWorkOrderId="+refWorkOrderId,
		success : function(rf) {
			$("#hdnWorkOrderId").val(rf.workOrderId);
			$("#txtWORefNumber").val(rf.woNumber);
			setupItemTbl(rf.requisitionFormItems);
			$("#aClose")[0].click();
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}
</script>
</head>
<body>
	<div id="container" class="popupModal">
		<div id="divWOSOReferenceContainer" class="reveal-modal">
			<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
			<div id="divWOSOReferenceId"></div>
		</div>
	</div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="requisitionFormDto" id="requisitionForm">
			<div class="modFormLabel">
				Material Requisition<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="requisitionForm.id" id="hdnFormId"/>
			<form:hidden path="requisitionForm.requisitionTypeId" id="hdnRequisitionTypeId"/>
			<form:hidden path="requisitionForm.createdBy"/>
			<form:hidden path="requisitionForm.formWorkflowId"/>
			<form:hidden path="requisitionForm.sequenceNumber"/>
			<form:hidden path="requisitionFormItemsJson" id="requisitionFormItemsJson"/>
			<form:hidden path="otherChargesLineJson" id="otherChargesLineJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentJson"/>
			<form:hidden path="requisitionForm.ebObjectId"/>
			<form:hidden path="itemCategoryId" id="itemCategoryId"/>
			<form:hidden path="requisitionForm.arCustomerId" id="hdnArCustomerId" />
			<form:hidden path="requisitionForm.fleetProfileId" id="hdnFleetProfileId" />
			<form:hidden path="requisitionForm.liters" id="hdnLiters" />
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">
								Sequence Number: 
							</td>
							<td class="value">
								<input type="text" id="txtSequenceNumber" class="textBoxLabel" readonly="readonly" 
									value="${requisitionFormDto.requisitionForm.sequenceNumber}"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Status:</td>
							<c:set var="status" value="${requisitionFormDto.requisitionForm.formWorkflow.currentFormStatus.description}"/>
							<c:if test="${status eq null}">
								<c:set var="status" value="NEW"/>
							</c:if>
							<td class="value">
								<input type="text" id="txtReqFormStatus" class="textBoxLabel" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Material Requisition Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="requisitionForm.companyId" onchange="clearFieldsOnCompanyChange();"
									id="companyId" cssClass="frmSelectClass">
										<form:options items="${companies}" itemLabel="name" itemValue="id" />
								</form:select>
							</td>
						</tr>
						<tr>
							<td class="labels">WO Reference</td>
							<td class="value">
								<form:hidden path="requisitionForm.workOrderId" id="hdnWorkOrderId"/>
								<input id="txtWORefNumber" readonly="readonly"/>
								<a href="#container" id="waOpen" data-reveal-id="divWOSOReferenceId" class="link_button"
									onclick="showWorkOrderRefForm();">Browse WO</a>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="requisitionForm.companyId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">* Warehouse</td>
							<td class="value">
								<form:select path="requisitionForm.warehouseId" id="warehouseId" class="frmSelectClass"
									onchange="warehouseOnChange();"></form:select>
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="requisitionForm.warehouseId" cssClass="error"/>
							</td>
						</tr>
						<tr>
							<td class="labels">* Date</td>
							<td class="value">
								<form:input path="requisitionForm.date" id="date" onblur="evalDate('date')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate1" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="requisitionForm.date" cssClass="error" style="margin-left: 12px;"/>
							</td>
						</tr>

						<tr>
							<td class="labels">Fleet</td>
							<td class="value">
							<form:input path="requisitionForm.fleetProfile.codeVesselName" id="fleetProfileId" class="input"
											onkeydown="getFleetProfiles();" onblur="setFleetProfile();" />
							</td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanFleetProfile" class="error"></span>
							</td>
						</tr>
						<tr id="tdErrRfFleet">
							<td class="labels"></td>
							<td class="value">
								<form:errors path="requisitionForm.fleetProfileId" id="errRfFleetId" cssClass="error" />
							</td>
						</tr>

						<tr>
							<td class="labels">Project</td>
							<td class="value">
								<form:input path="requisitionForm.arCustomer.name" id="projectId" class="input"
											onkeydown="getCustomers();" onblur="setCustomerId();" />
							</td>
						</tr>

						<tr>
							<td class="labels"></td>
							<td class="value">
								<span id="spanProjectErrorMsg" class="error"></span>
								<form:errors path="requisitionForm.arCustomerId" id="errRfProjId" cssClass="error" />
							</td>
						</tr>

					<c:if test="${requisitionFormDto.requisitionForm.requisitionTypeId eq 2}">
							<tr>
								<td class="labels">Distance</td>
								<td class="value">
									<form:input path="requisitionForm.distance" id="txtDistance" class="input" 
										onblur="formatMoney(this); computeLiters();" maxLength="13"/>
								</td>
							</tr>

							<tr>
								<td class="labels">Ratio</td>
								<td class="value">
									<select id="selRatioId" class="frmSelectClass" onchange="computeLiters();">
										<c:forEach items="${ratios}" var="r">
											<option value="${r.value}" id="${r.id}">${r.name}</option>
										</c:forEach>
									</select>
									<form:hidden path="requisitionForm.ratioId" id="hdnRatioId"/>
								</td>
							</tr>

							<tr>
								<td class="labels">Liters</td>
								<td class="value" id="tdLiters">
									<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${requisitionFormDto.requisitionForm.liters}"/>
								</td>
							</tr>
						</c:if>

						<tr>
							<td class="labels">Requested By</td>
							<td class="value">
								<form:input path="requisitionForm.requestedBy" id="requestedBy"  class="input"/>
							</td>
						</tr>

						<tr>
							<td class="labels">Requested Date</td>
							<td class="value">
								<form:input path="requisitionForm.requestedDate" id="requestedDate" onblur="evalDate('requestedDate')"
									style="width: 120px;" class="dateClass2" />
								<img id="imgDate2" src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('requestedDate')" 
									style="cursor: pointer" style="float: right;" /></td>
						</tr>

						<tr>
							<td class="labels">Remarks</td>
							<td class="value">
								<form:textarea path="requisitionForm.remarks" id="txtRemarks" class="input" />
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
				<legend>Material Requisition Items</legend>
				<div id="requisitionFormItemsTable"></div>
				<table>
					<tr>
						<td colspan="12">
							<span id="errItemsMsg" class="error"></span>
						</td>
					</tr>
					<tr>
						<td colspan="12">
							<form:errors path="errItemsMsg" cssClass="error"/>
						</td>
					</tr>
				</table>
				</fieldset>

				<fieldset class="frmField_set">
				<legend>Document</legend>
					<div id="documentsTable"></div>
				<table id="tblReferenceDocuments">
					<tr>
						<td colspan="12">
							<form:errors path="requisitionForm.referenceDocuments" cssClass="error"
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

				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSaveRequisitionForm" value="Save" onclick="saveRequisitionForm();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>