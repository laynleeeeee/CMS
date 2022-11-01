<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Stock Adjustment Register search page.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebReport.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#dateFrom").val("${currentDate}");
	$("#dateTo").val("${currentDate}");
	filterWarehouses();
});

function filterWarehouses() {
	var companyId = $("#companyId").val();

	if(companyId != "") {
		var uri = contextPath+"/getWarehouse?companyId="+companyId;
		$("#warehouseId").empty();
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
					filterAdjustmentTypes();
				}
		};
		loadPopulate (uri, true, null, "warehouseId", optionParser, postHandler);
	}
}

function filterAdjustmentTypes() {
	var companyId = $("#companyId").val();
	var divisionId = $("#divisionId").val();
	if(companyId != "") {
		companyId = $("#companyId").val();
	}
	if(divisionId != "") {
		divisionId = $("#divisionId").val();
	}

	var uri = contextPath+"/getSAdjustmentTypes?companyId="+companyId+"&divisionId="+divisionId;
	$("#adjustmentTypeId").empty();
	var optionParser = {
		getValue: function (rowObject) {
			return rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["name"];
		}
	};

	loadPopulate (uri, true, null, "adjustmentTypeId", optionParser, null);
}

function searchReport() {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var divisionId = $("#divisionId").val();
	var adjustmentTypeId = $("#adjustmentTypeId").val();
	var dateFrom = $("#dateFrom").val();
	var dateTo = $("#dateTo").val();
	var formatType = $("#formatType").val();
	var formStatusId = $("#formWorkflowStatusId").val();
	var url = "";
	if (dateFrom != "" && dateTo != "") {
		evalDate("dateFrom", true);
		evalDate("dateTo", true);
		$("#spanErrorDate").text("");
		url = "stockAdjRegisterPDF?companyId="+companyId+"&warehouseId="+warehouseId
				+"&divisionId="+divisionId+"&adjustmentTypeId="+adjustmentTypeId+"&dateFrom="+dateFrom
				+"&dateTo="+dateTo+"&formatType="+formatType+"&formStatusId="+formStatusId;
	} else {
		$("#spanErrorDate").text("Date from and to are required fields.");
	}
	$("#iFrame").attr('src', url);
	$("#iFrame").load();
}

</script>
</head>
<body>
<table>
	<tr>
		<td>
			<br/>
		</td>
	</tr>
	<tr>
		<td class="title2">Company </td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="filterWarehouses();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Division </td>
		<td>
			<select id="divisionId" class="frmSelectClass" onchange="filterAdjustmentTypes();">
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Warehouse</td>
		<td>
			<select id="warehouseId" class="frmSelectClass" ></select>
		</td>
	</tr>
	<tr>
		<td class="title2">Stock Adjustment Type </td>
		<td>
			<select id="adjustmentTypeId" class="frmSelectClass" ></select>
		</td>
	</tr>
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="spanErrorDate" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td>
			<select id="formWorkflowStatusId" class="frmSelectClass">
				<option value="-1">ALL</option>
				<c:forEach var="fs" items="${workflowStatuses}">
					<option value="${fs.id}">${fs.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Format</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchReport();"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>