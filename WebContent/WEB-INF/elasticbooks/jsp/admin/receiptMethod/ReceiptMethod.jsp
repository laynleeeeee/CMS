<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Receipt method main jsp page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditReceiptMethod").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchReceiptMethod();
});

$(function () {
	$("#txtName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchReceiptMethod();
			e.preventDefault();
		}
	});
});

var isSaving = false;
function saveReceipt() {
	if (!isSaving) {
		isSaving = true;
		$("#btnSaveReceipt").attr("disabled", "disabled");
		// For Debit Account Combination
		selectedDebitAcctDivisionValue = $("#dbACDivisionId option:selected").val();
		selectedDebitAcctAccountValue = $("#dbACAccountId option:selected").val();
		// For Credit Account Combination
		selectedCreditAcctDivisionValue = $("#crACDivisionId option:selected").val();
		selectedCreditAcctAccountValue = $("#crACAccountId option:selected").val();
		$("#btnSaveReceipt").attr("disabled", "disabled");
		doPostWithCallBack ("receiptMethodFormId", "receiptMethodForm", function(data) {
			if (data.substring(0,5) == "saved") {
				$("#spanReceiptMethodMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
						+ "receipt method " + $("#rmName").val() + ".");
				$("#receiptMethodForm").html("");
				searchReceiptMethod();
			} else {
				$("#receiptMethodForm").html(data);
			}
			isSaving = false;
			$("#btnSaveReceipt").removeAttr("disabled");
		});
	}
}

function addReceiptMethod() {
	$("#receiptMethodForm").load(contextPath + "/admin/receiptMethods/form");
	$("html, body").animate({scrollTop: $("#receiptMethodForm").offset().top}, 0050);
}

function getCommonParam() {
	var name = processSearchName($("#txtName").val());
	var companyId = $("#selectCompnay").val();
	var bankAccountId = $("#selectBankAccount").val();
	var status = $("#selectStatus").val();
	return "?name="+name+"&companyId="+companyId+"&bankAccountId="+bankAccountId+"&status="+status;
}

function searchReceiptMethod() {
	doSearch ("receiptMethodTable", "/admin/receiptMethods/search"+getCommonParam()+"&pageNumber=1");
}

function editReceiptMethod() {
	var id = getCheckedId("cbReceiptMethod");
	$("#receiptMethodForm").load(contextPath + "/admin/receiptMethods/form?pId="+id);
	$("html, body").animate({scrollTop: $("#receiptMethodForm").offset().top}, 0050);
}

function cancelReceiptMethod() {
	$("#receiptMethodForm").html("");
	$("html, body").animate({scrollTop: $("#receiptMethodTable").offset().top}, 0050);
	searchReceiptMethod();
}

function filterBankAccounts () {
	$("#bankAccountId").empty();
	var option = "<option selected='selected' value=-1>PLEASE SELECT</option>";
	$("#bankAccountId").append(option);
	var companyId = $("#companyId option:selected").val();
	var uri = contextPath+"/getBankAccounts?companyId="+companyId;

	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};

	postHandler = {
		doPost: function(data) {
			$("#bankAccountId").val(selectBankAccount).attr("selected", true);
		}
	};

	loadPopulate (uri, false, selectBankAccount, "bankAccountId", optionParser, postHandler);
}

function filterDivisions(divisionId, slctDivisionId, isDebitAcctCombi) {
	var companyId = $("#companyId option:selected").val();
	var uri = contextPath+"/getDivisions?companyId="+companyId
			+ (divisionId > 0 ? "&divisionId="+divisionId : "");

	clearAndAddEmpty (slctDivisionId);
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["number"] +" - " +rowObject["name"];
		}
	};
	var postHandler = {
		doPost: function(data) {
			if (isDebitAcctCombi) {
				$("#dbACDivisionId").val(selectedDebitAcctDivisionValue).attr('selected',true);
				filterAccountByDebitACDivision();
			} else {
				$("#crACDivisionId").val(selectedCreditAcctDivisionValue).attr('selected',true);
				filterAccountByCreditACDivision();
			}
		}
	};

	loadPopulate (uri, false, divisionId, slctDivisionId, optionParser, postHandler);
}

function clearAndAddEmpty (selectId) {
	$("#"+selectId).empty();
	var option = "<option selected='selected' value='0'></option>";
	$("#"+selectId).append(option);
}

function divisionOnChange(isDebitAcctChanged) {
	if(isDebitAcctChanged == true) {
		selectedDebitAcctAccountValue = 0;
		filterAccountByDebitACDivision();
	} else {
		selectedCreditAcctAccountValue = 0;
		filterAccountByCreditACDivision();
	}
}

function filterAccountByDebitACDivision() {
	var selectAccountIds = new Array (); 
	selectAccountIds.push ("dbACAccountId");
	var divisionId = $("#dbACDivisionId option:selected").val();
	filterAccount (selectAccountIds, divisionId, selectedDebitAcctAccountValue);
}

function filterAccountByCreditACDivision() {
	var selectAccountIds = new Array (); 
	selectAccountIds.push ("crACAccountId");
	var divisionId = $("#crACDivisionId option:selected").val();
	filterAccount (selectAccountIds, divisionId, selectedCreditAcctAccountValue);
}

function filterAccount (selectIds, divisionId, selectedValue) {
	if (divisionId == null)
		return;
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		clearAndAddEmpty (selectId);
	}
	var companyId = $("#companyId option:selected").val();
	var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId+
		(typeof selectedValue != "undefined" ? "&accountId="+selectedValue : "");
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["number"] +" - " +rowObject["accountName"];
		}
	};
	var postHandler = {
			doPost: function(data) {
				for (var i = 0; i < selectIds.length; i++ ){
					var selectId = selectIds[i];
					$("#"+selectId).val(selectedValue).attr('selected',true);
				}
			}
	};
	loadPopulateMultiSelect (uri, false, null, selectIds, optionParser, postHandler);
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="25%" class="title">Company</td>
				<td><select id="selectCompnay" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="comp" items="${companies}">
						<option value="${comp.id}">${comp.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="25%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="25%" class="title">Bank Account</td>
				<td><select id="selectBankAccount" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="ba" items="${bankAccounts}">
						<option value="${ba.id}">${ba.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchReceiptMethod();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanReceiptMethodMessage" class="message"></span>
	<div id="receiptMethodTable">
		<%@ include file="ReceiptMethodTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddReceiptMethod" value="Add" onclick="addReceiptMethod();"></input>
		<input type="button" id="btnEditReceiptMethod" value="Edit" onclick="editReceiptMethod();"></input>
	</div>
	<br>
	<br>
	<div id="receiptMethodForm" style="margin-top: 20px;">
	</div>
</body>
</html>