<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Purhcase requisition form
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
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.requisitionitemhandler.js"></script>
<style type="text/css">
.textBoxLabel {
	border: none;
}
</style>
<script type="text/javascript">
var $documentsTable = null;
var REQ_TYPE_ID = $("#hdnRequisitionTypeId").val();
var RT_FUEL = 2;
var IS_INITIAL_LOAD = true;
var savedWarehouseId = "${requisitionFormDto.requisitionForm.warehouseId}";
var PR_ID = "${requisitionFormDto.requisitionForm.id}";
var isNew = "${requisitionFormDto.requisitionForm.id}" == 0;
$(document).ready(function(){
	filterWarehouse(savedWarehouseId);
	if (PR_ID != 0) {
		var reqTypeId = "${requisitionFormDto.requisitionForm.reqFormRef.requisitionType.id}";
		var reqTypeName = getRequisitionTypeName(reqTypeId);
		$("#txtRfRef").val(reqTypeName + " " +
				"${requisitionFormDto.requisitionForm.reqFormRef.sequenceNumber}");
		$("#tdFleet").text("${requisitionFormDto.requisitionForm.reqFormRef.fleetProfile.codeVesselName}");
		$("#tdProject").text("${requisitionFormDto.requisitionForm.reqFormRef.arCustomer.name}");
		$("#aOpen").hide();
		$("#companyId").attr("disabled", "disabled");
	}
	initItemTable();
	initializeDocumentsTbl();
});

function getRequisitionTypeName(requisitionTypeId) {
	var reqName;
	var reqTypeId = Number(requisitionTypeId);
	switch (reqTypeId) {
		case 1:
			reqName = "Tire";
			break;
		case 2:
			reqName = "Fuel";
			break;
		case 3:
			reqName = "PMS";
			break;
		case 4:
			reqName = "Electrical";
			break;
		case 5:
			reqName = "CM";
			break;
		case 6:
			reqName = "Admin";
			break;
		case 7:
			reqName = "Motorpool";
			break;
		case 8:
			reqName = "Oil";
			break;
		case 9:
			reqName = "SUBCON" ;
			break;
		case 10:
			reqName = "Pakyawan";
			break;
	}
	return reqName;
}

function initializeDocumentsTbl() {
	var refDocsJson = JSON.parse($("#referenceDocumentsJson").val());
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
function savePRForm() {
	$("#companyId").removeAttr("disabled");
	var refDocErrorMsg = $("#referenceDocsMgs").text();
	var hasExceededFileSize = checkExceededFileSize($("#documentsTable"));
	if(isSaving == false && refDocErrorMsg == "" && !hasExceededFileSize) {
		isSaving = true;
		$("#referenceDocumentsJson").val($documentsTable.getData());
		$("#prItemsJson").val($prItemTable.getData());
		$("#btnSaveRequisitionForm").attr("disabled", "disabled");
		doPostWithCallBack ("requisitionForm", "form", function (data) {
			if (data.startsWith("saved")) {
				var objectId = data.split(";")[1];
				var formStatus = new Object();
				formStatus.objectId = objectId;
				updateTable (formStatus);
				dojo.byId("form").innerHTML = "";
			} else {
				var companyId = $("#companyId").val();
				var rfRef = $("#txtRfRef").val();
				var fleetProfileCode = $("#tdFleet").text();
				var projectName = $("#tdProject").text();
				var requestedBy = $("#tdRequestedBy").text();
				var status = $("#txtReqFormStatus").val();
				var hdnRfId = $("#hdnReqFormRefId").val();
				var warehouseId = $("#warehouseId").val();
				if (PR_ID == 0) {
					dojo.byId("form").innerHTML = data;
					$("#companyId").val(companyId);
					$("#tdRequestedBy").text(requestedBy);
				} else {
					dojo.byId("editForm").innerHTML = data;
					updatePopupCss();
					$("#aOpen").hide();
				}
				$("#txtRfRef").val(rfRef);
				$("#tdFleet").text(fleetProfileCode);
				$("#tdProject").text(projectName);
				$("#txtReqFormStatus").val(status);
				filterWarehouse(warehouseId);
				initItemTable();
				initializeDocumentsTbl();
				isSaving = false;
			}
			isSaving = false;
			$("#btnSaveRequisitionForm").removeAttr("disabled");
		});
	} else if(hasExceededFileSize) {
		$("#spDocSizeMsg").text("Total size for documents upload must not exceed 10 MB.");
	}
}

function showRFReference() {
	var companyId = $("#companyId").val();
	var url = contextPath + "/purchaseRequisition/" + REQ_TYPE_ID + "/rfReference?companyId="+companyId;
	$("#divRFReference").load(url);
}

function loadRFReference(rfId) {
	$.ajax({
		url: contextPath + "/purchaseRequisition/loadRequisitionForm?rfId="+rfId,
		success : function(pr) {
			clearRfRef();
			$("#aClose")[0].click();
			updateHeader(pr);
			setupItemTableData(pr.prItems);
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function initItemTable() {
	var prItemsJson = JSON.parse($("#prItemsJson").val());
	setupItemTableData(prItemsJson);
}

function setupItemTableData(prItemsJson) {
	$("#divPrItemsTbl").html("");
	var cPath = "${pageContext.request.contextPath}";
	$prItemTable = $("#divPrItemsTbl").editableItem({
		data: prItemsJson,
		jsonProperties: [
			{"name" : "id", "varType" : "int"},
			{"name" : "itemId", "varType" : "int"},
			{"name" : "ebObjectId", "varType" : "int"},
			{"name" : "refenceObjectId", "varType" : "int"},
			{"name" : "origRefObjectId", "varType" : "int"},
			{"name" : "stockCode", "varType" : "string"},
			{"name" : "existingStocks", "varType" : "double"},
			{"name" : "description", "varType" : "string"},
			{"name" : "uom", "varType" : "string"},
			{"name" : "quantity", "varType" : "double"},
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
			{"title" : "refenceObjectId",
				"cls" : "refenceObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "origRefObjectId",
				"cls" : "origRefObjectId",
				"editor" : "hidden",
				"visible" : false },
			{"title" : "itemId", 
				"cls" : "itemId", 
				"editor" : "hidden",
				"visible" : false },
			{"title" : "Stock Code", 
				"cls" : "stockCode tblInputText",
				"editor" : "text",
				"visible" : true,
				"width" : "15%",
				"handler" : new RQItemTableHandler (new function () {
					this.handleTotal = function (total) {
						// Do nothing
					};
				})},
			{"title" : "Description",
				"cls" : "description tblLabelText",
				"editor" : "label",
				"visible" : true, 
				"width" : "24%"},
			{"title" : "Existing <br> Stock",
				"cls" : "existingStocks tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%"},
			{"title" : "Qty",
				"cls" : "quantity tblInputNumeric",
				"editor" : "text",
				"visible" : true,
				"width" : "8%" },
			{"title" : "UOM",
				"cls" : "uom tblLabelText",
				"editor" : "label",
				"visible" : true,
				"width" : "8%" }
		],
			"disableDuplicateStockCode" : false,
		});

	$("#divPrItemsTbl tbody tr").each(function() {
		populateExistingStocks(this);
		$(this).find(".stockCode").attr("disabled", "disabled");
		$(this).find(".quantity").attr("disabled", "disabled");
	});
}

function populateExistingStocks(tbl) {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var stockCode = $(tbl).find(".stockCode").val();
	var $existingStocks = $(tbl).find(".existingStocks");
	var uri = contextPath + "/getItem/existingStockBySC?stockCode="+$.trim(stockCode)
			+"&companyId="+companyId;
	if (warehouseId != null && warehouseId > 0) {
		uri += "&warehouseId="+warehouseId;
	}
	$.ajax({
		url: uri,
		success : function(existingStocks) {
			var formattedExisitingStocks = accounting.formatMoney(existingStocks);
			$existingStocks.val(formattedExisitingStocks);
			$existingStocks.attr("disabled", "disabled");
		},
		error : function(error) {
			console.log(error);
		}
	});
}

function updateHeader(pr) {
	$("#hdnReqFormRefId").val(pr.rfId);
	$("#txtRfRef").val(pr.rfSeqNo);
	var arCustomerId = pr.arCustomerId != 0 ? pr.arCustomerId : "";
	$("#hdnArCustomerId").val(arCustomerId);
	var fleetProfileId = pr.fleetProfileId != 0 ? pr.fleetProfileId : "";
	$("#hdnFleetProfileId").val(fleetProfileId);
	$("#tdFleet").text(pr.fleetProfileCode);
	$("#tdProject").text(pr.projectName);
	$("#txtRemarks").val(pr.remarks);
	$("#tdRequestedBy").text(pr.requestedBy);
	$("#requestedDate").val(pr.strRequestedDate);
	if (pr.joSeqNo != 0) {
		$("#joReferenceId").val(pr.joSeqNo);
	}
	$("#warehouseId").val(pr.warehouseId);
}

function formatMoney($input) {
	$($input).val(accounting.formatMoney($($input).val()));
}

function clearRfRef() {
	$("#divRfReference").html("");
}

function clearFieldsOnCompanyChange() {
	$("#txtRfRef").val("");
	$("#joReferenceId").val("");
	$("#tdFleet").html("");
	$("#tdProject").html("");
	$("#txtRemarks").val("");
	$("#hdnReqFormRefId").val("");
	$("#referenceDocumentsJson").val("null");
	$("#documentsTable").empty();
	$("#divPrItems").empty();
	filterWarehouse();
	initializeDocumentsTbl();
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

function warehouseOnChange() {
	// Repopulate item existing stocks
	$("#divPrItemsTbl tbody tr").each(function() {
		populateExistingStocks(this);
	});
}
</script>
</head>
<body>
<div id="container" class="popupModal">
<div id="divRFReferenceContainer" class="reveal-modal">
	<a href="#" id="aClose" class="close-reveal-modal link_button">Close</a>
	<div id="divRFReference"></div>
</div>
</div>
	<div id="divForm" class="formDivBigForms">
		<form:form method="POST" commandName="requisitionFormDto" id="requisitionForm">
			<div class="modFormLabel">${requisitionFormDto.requisitionTypeName}
				<span class="btnClose" id="btnClose">[X]</span>
			</div>
			<form:hidden path="requisitionForm.id" id="hdnFormId"/>
			<form:hidden path="requisitionForm.requisitionTypeId" id="hdnRequisitionTypeId"/>
			<form:hidden path="requisitionForm.createdBy"/>
			<form:hidden path="requisitionForm.formWorkflowId"/>
			<form:hidden path="requisitionForm.sequenceNumber"/>
			<form:hidden path="requisitionForm.ebObjectId"/>
			<form:hidden path="requisitionForm.reqFormRefId" id="hdnReqFormRefId" />
			<form:hidden path="requisitionForm.requisitionClassificationId" id="requisitionClassificationId" />
			<form:hidden path="requisitionForm.requestedDate" id="requestedDate"/>
			<form:hidden path="otherChargesLineJson" id="otherChargesLineJson"/>
			<form:hidden path="referenceDocumentsJson" id="referenceDocumentsJson"/>
			<form:hidden path="prItemsJson" id="prItemsJson"/>
			<form:hidden path="requisitionForm.arCustomerId" id="hdnArCustomerId" />
			<form:hidden path="requisitionForm.fleetProfileId" id="hdnFleetProfileId" />
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>System Generated</legend>
					<table class="formTable">
						<tr>
							<td class="labels">Sequence Number:</td>
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
								<input type="text" id="txtReqFormStatus" class="textBoxLabel" 
									readonly="readonly" value='${status}'/>
							</td>
						</tr>
					</table>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>Requisition Form Header</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Company</td>
							<td class="value">
								<form:select path="requisitionForm.companyId" id="companyId"
									onchange="clearFieldsOnCompanyChange();" cssClass="frmSelectClass">
									<form:options items="${companies}" itemLabel="name" itemValue="id" />
								</form:select>
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
							<td class="labels">Requisition Form Reference</td>
							<td class="value">
								<input id="txtRfRef"  readonly="readonly"/>
								<a href="#container" id="aOpen" data-reveal-id="divRFReference" class="link_button" onclick="showRFReference();">Browse MR</a>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2">
								<form:errors path="requisitionForm.reqFormRefId" cssClass="error" style="margin-left: 12px;" />
							</td>
						</tr>
						<tr>
							<td class="labels">Fleet</td>
							<td class="value" id="tdFleet"></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="requisitionForm.fleetProfileId" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td class="labels">Project</td>
							<td class="value" id="tdProject"></td>
						</tr>
						<tr>
							<td class="labels"></td>
							<td class="value">
								<form:errors path="requisitionForm.arCustomerId" cssClass="error" />
							</td>
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
				<legend>Purchase Requisition Items</legend>
				<div id="divPrItemsTbl"></div>
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

				<table class="frmField_set">
					<tr>
						<td align="right"><input type="button" id="btnSavePRForm" value="Save" onclick="savePRForm();" /></td>
					</tr>
				</table>
			</div>
			<hr class="thin" />
		</form:form>
	</div>
</body>
</html>