<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: JSP page for List of Supplier Accounts.
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
$(document).ready (function () {
	$("#btnEditSupplierAcct").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchSupplierAcct();
});

function filterDivisions () {
	var companyId = $("#companyId option:selected").val();
	if (companyId == null){
		companyId = $("#companyId").val();
	}

	var uri = contextPath+"/getDivisions?companyId="+companyId;
	var selectIds = new Array ();
	selectIds.push ("defaultDebitDivisionId");
	selectIds.push ("defaultCreditDivisionId");
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		clearAndAddEmpty (selectId);
	}
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
				addSavedInactiveDiv($("#defaultDebitDivisionId"), selectedDebitDivisionValue);
				addSavedInactiveDiv($("#defaultCreditDivisionId"), selectedCreditDivisionValue);
				filterAccountByDebitDivision ();
				filterAccountByCreditDivision();
			}
	};
	loadPopulateMultiSelect (uri, false, null, selectIds, optionParser, postHandler);
}

function addSavedInactiveDiv ($select, selectedValue) {
	var uri = contextPath+"/getDivisions/specific?divisionId="+selectedValue;
	$.ajax({
		url: uri,
		async: false,
		success : function (division) {
			if (division != null) {
				$($select).append("<option value="+selectedValue+">"+division.name+"</option>");
				$($select).val(selectedValue).attr('selected',true);
			}
		},
		error : function (error) {
			console.log(error);
		},
		dataType: "json",
	});
}

function clearAndAddEmpty (selectId) {
	$("#"+selectId).empty();
	var option = "<option selected='selected' value='0'></option>";
	$("#"+selectId).append(option);
}

function filterAccountByDebitDivision () {
	var selectAccountIds = new Array (); 
	selectAccountIds.push ("defaultDebitAccountId");
	var divisionId = $("#defaultDebitDivisionId option:selected").val();
	filterAccount (selectAccountIds, divisionId, selectedDebitAccountValue);
}

function filterAccountByCreditDivision () {
	var selectAccountIds = new Array (); 
	selectAccountIds.push ("defaultCreditAccountId");
	var divisionId = $("#defaultCreditDivisionId option:selected").val();
	filterAccount (selectAccountIds, divisionId, selectedCreditAccountValue);
}

function filterAccount (selectIds, divisionId, selectedValue) {
	if (divisionId == null)
		return;
	for (var i = 0; i < selectIds.length; i++ ){
		var selectId = selectIds[i];
		clearAndAddEmpty (selectId);
	}
	var companyId = $("#companyId option:selected").val();
	if (companyId == null){
		companyId = $("#companyId").val();
	}
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

function saveSupplierAcct() {
	$("#btnSaveSupplierAcct").attr("disabled", "disabled");
	selectedDebitDivisionValue =  $("#defaultDebitDivisionId option:selected").val();
	selectedDebitAccountValue =  $("#defaultDebitAccountId option:selected").val();
	selectedCreditDivisionValue =  $("#defaultCreditDivisionId option:selected").val();
	selectedCreditAccountValue =  $("#defaultCreditAccountId option:selected").val();
	doPostWithCallBack ("supplierAcctFormId", "supplierAcctForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanSupplierAcctMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "Supplier Account " + $("#name").val() + ".");
			$("#supplierAcctForm").html("");
			searchSupplierAcct();
		} else {
 			$("#supplierAcctForm").html(data);
		}
	});
}

$(function () {
	$("#txtSupplierAcctName, #txtSupplierName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchSupplierAcct();
			e.preventDefault();
		}
	});
});

function addSupplierAcct() {
	$("#supplierAcctForm").load(contextPath + "/admin/supplierAccount/form");
	$("html, body").animate({scrollTop: $("#supplierAcctForm").offset().top}, 0050);
}

function getCommonParam() {
	var supplierName = processSearchName($("#txtSupplierName").val());
	var supplierAcctName = processSearchName($("#txtSupplierAcctName").val());
	var companyId = $("#selectCompnay").val();
	var termId = $("#selectTerm").val();
	var status = $("#selectStatus").val();
	return "?supplierName="+supplierName+"&supplierAcctName="+supplierAcctName+"&companyId="+companyId+"&termId="+termId+"&status="+status;
}

function searchSupplierAcct() {
	doSearch ("supplierAcctTable", "/admin/supplierAccount/search"+getCommonParam()+"&pageNumber=1");
}

function editSupplierAcct() {
	var id = getCheckedId("cbSupplierAcct");
	$("#supplierAcctForm").load(contextPath + "/admin/supplierAccount/form?pId="+id);
	$("html, body").animate({scrollTop: $("#supplierAcctForm").offset().top}, 0050);
}

function cancelSupplierAcct() {
	$("#supplierAcctForm").html("");
	$("html, body").animate({scrollTop: $("#supplierAcctTable").offset().top}, 0050);
	searchSupplierAcct();
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="25%" class="title">Supplier Name</td>
				<td><input type="text" id="txtSupplierName" class="inputSmall"></td>
			</tr>
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
				<td width="25%" class="title">Account Name</td>
				<td><input type="text" id="txtSupplierAcctName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="25%" class="title">Terms</td>
				<td><select id="selectTerm" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="term" items="${terms}">
						<option value="${term.id}">${term.name}</option>
					</c:forEach>
				</select></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchSupplierAcct();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanSupplierAcctMessage" class="message"></span>
	<div id="supplierAcctTable">
		<%@ include file="SupplierAccountTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddSupplierAcct" value="Add" onclick="addSupplierAcct();"></input>
		<input type="button" id="btnEditSupplierAcct" value="Edit" onclick="editSupplierAcct();"></input>
	</div>
	<br>
	<br>
	<div id="supplierAcctForm" style="margin-top: 20px;">
	</div>
</body>
</html>