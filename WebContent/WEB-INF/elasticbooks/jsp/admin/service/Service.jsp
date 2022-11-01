<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Service main jsp page.
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
	$("#btnEdit").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
	searchService();
});

function loadDivisions(companyId, divisionId, accountId, discAccId) {
	if(companyId == null)
		companyId = $("#companyId").val();
	if(companyId != undefined){
		var uri = contextPath+"/getDivisions?companyId="+companyId
				+ ((divisionId != "") ? "&divisionId="+divisionId : "");
		$("#divisionId").empty();
		var optionParser =  {
			getValue: function (rowObject) {
				return rowObject["id"];
			},
			getLabel: function (rowObject) {
				return rowObject["numberAndName"];
			}
		};
		postHandler = {
				doPost: function(data) {
					if(divisionId != 0) {
						$("#divisionId").val(divisionId).attr('selected',true);
					}
					loadAccounts(companyId, divisionId, accountId);
				}
		};
		loadPopulate (uri, false, null, "divisionId", optionParser, postHandler);
	}
}

function loadAccounts(companyId, divisionId, accountId) {
	if(companyId == null)
		companyId = $("#companyId").val();
	if(divisionId == null || divisionId == 0)
		divisionId = $("#divisionId").val();
	if(companyId != undefined && divisionId != undefined){
		var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId
				+ ((typeof accountId != "undefined" || accountId != null) ? "&accountId="+accountId : "");
		$("#accountId").empty();
		var optionParser =  {
				getValue: function (rowObject) {
					return rowObject["id"];
				},
				getLabel: function (rowObject) {
					return rowObject["number"] + " - "+rowObject["accountName"];
				}
			};
			postHandler = {
					doPost: function(data) {
						if(accountId != 0)
							$("#accountId").val(accountId).attr('selected',true);
					}
			};
		loadPopulate (uri, false, null, "accountId", optionParser, postHandler);
	}
}

function save() {
	$("#btnSave").attr("disabled", "disabled");
	$("#amount").val(accounting.unformat($("#amount").val()));
	doPostWithCallBack ("serviceFormId", "serviceForm", function(data) {
		if (data.substring(0,5) == "saved") {
			$("#spanSaveMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") 
					+ "Service " + $("#txtName").val() + ".");
			$("#serviceForm").html("");
			searchService();
		} else {
			companyId = $("#companyId").val();
			$("#serviceForm").html(data);
			$("#companyId").val(companyId);
		}
	});
}

$(function () {
	$("#txtName").bind("keypress", function (e) {
		if (e.which == 13) {
			searchService();
			e.preventDefault();
		}
	});
});

function addService() {
	$("#serviceForm").load(contextPath + "/admin/service/form");
	$("html, body").animate({scrollTop: $("#serviceForm").offset().top}, 0050);
}

function getCommonParam() {
	var name = processSearchName($("#txtName").val());
	var companyId = $("#selectCompnay").val();
	var divisionId = $("#selectDivisionId").val();
	var status = $("#selectStatus").val();
	return "?name="+name+"&companyId="+companyId+"&status="+status+"&divisionId="+divisionId;
}

function searchService() {
	doSearch ("serviceTable", "/admin/service/search"+getCommonParam()+"&pageNumber=1");
}

function editService() {
	var id = getCheckedId("cbService");
	$("#serviceForm").load(contextPath + "/admin/service/form?pId="+id);
	$("html, body").animate({scrollTop: $("#serviceForm").offset().top}, 0050);
}

function cancelForm() {
	$("#serviceForm").html("");
	$("html, body").animate({scrollTop: $("#serviceTable").offset().top}, 0050);
	searchService();
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
				<td width="25%" class="title">Division</td>
				<td><select id="selectDivisionId" class="frmSmallSelectClass">
					<option value="-1">All</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td width="25%" class="title">Name</td>
				<td><input type="text" id="txtName" class="inputSmall"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmSmallSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="searchService();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanSaveMessage" class="message"></span>
	<div id="serviceTable">
		<%@ include file="ServiceTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAdd" value="Add" onclick="addService();"></input>
		<input type="button" id="btnEdit" value="Edit" onclick="editService();"></input>
	</div>
	<br>
	<br>
	<div id="serviceForm" style="margin-top: 20px;">
	</div>
</body>
</html>