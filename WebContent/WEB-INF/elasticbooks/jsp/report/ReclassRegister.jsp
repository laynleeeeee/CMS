<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Reclass Register page
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CMS/css/ebReport.css"
	media="all">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css"
	media="all">
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/autocompleteUtil.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/ajaxValidation.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#dateFrom").val("${currentDate}");
		$("#dateTo").val("${currentDate}");
		filterWarehouses();
	});

	function filterWarehouses() {
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var uri = contextPath + "/getWarehouse/new?";
		if (companyId != -1) {
			uri += "companyId=" + companyId;
		}
		if (divisionId != -1) {
			uri += "&divisionId=" + divisionId;
		}
		$("#warehouseId").empty();
		var optionParser = {
			getValue : function(rowObject) {
				return rowObject["id"];
			},

			getLabel : function(rowObject) {
				return rowObject["name"];
			}
		};
		loadPopulate(uri, true, null, "warehouseId", optionParser, null);
	}

	function searchReport() {
		var companyId = $("#companyId").val();
		var divisionId = $("#divisionId").val();
		var warehouseId = $("#warehouseId").val();
		var dateFrom = document.getElementById("dateFrom").value;
		var dateTo = document.getElementById("dateTo").value;
		var formatType = $("#formatType").val();
		var statusId = $("#statusId").val();
		if (companyId == -1) {
			$("#spanCompanyError").text("Company is required");
			$("#iFrame").attr('src', "");
		}
		if (dateFrom == "") {
			$("#spanErrorDate").text("Select an starting date.");
			$("#iFrame").attr('src', "");
		}
		if (dateTo == "") {
			$("#spanErrorDate").text("Select an ending date.");
			$("#iFrame").attr('src', "");
		}
		if (companyId != -1 && divisionId != null && dateFrom != ""
				&& dateTo != "" && warehouseId != null) {
			var url = "reclassRegister?companyId=" + companyId + "&divisionId="
					+ divisionId + "&warehouseId=" + warehouseId + "&dateFrom="
					+ dateFrom + "&dateTo=" + dateTo + "&formatType="
					+ formatType + "&statusId=" + statusId;
			$("#iFrame").attr('src', url);
			$("#iFrame").load();
			clearValues();
		} else {
			$("#iFrame").attr('src', "");
		}
	}

	function clearValues() {
		$("#spanCompanyError").text("");
		$("#spanErrorDate").text("");
		$("#spanCategoryError").text("");
	}
</script>
</head>
<body>
	<table>
		<tr>
			<td><br /></td>
		</tr>
		<tr>
			<td class="title2">Company</td>
			<td><select id="companyId" class="frmSelectClass">
					<option selected='selected' value=-1>Please select a
						company</option>
					<c:forEach var="company" items="${companies}">
						<option value="${company.id}">${company.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2"><span id="spanCompanyError" class="error"></span>
			</td>
		</tr>
		<tr>
			<td class="title2">Division</td>
			<td class="value"><select id="divisionId" class="frmSelectClass"
				onchange="filterWarehouses();">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="division" items="${divisions}">
						<option value="${division.id}">${division.name}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Warehouse</td>
			<td><select id="warehouseId" class="frmSelectClass">
					<option value="-1">ALL</option>
			</select></td>
		</tr>
		<tr>
			<td class="title2">Date</td>
			<td class="tdDateFilter"><input type="text" id="dateFrom"
				maxlength="10" class="dateClass2" onblur="evalDate(this.id, false) value=${currentDate};">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('dateFrom')" style="cursor: pointer"
				style="float: right;" /> <span style="font-size: small;">To</span> <input
				type="text" id="dateTo" maxlength="10" class="dateClass2"
				onblur="evalDate(this.id, false);"> <img
				src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('dateTo')" style="cursor: pointer"
				style="float: right;" /></td>
		</tr>
		<tr>
			<td></td>
			<td><span id="spanErrorDate" class="error"></span></td>
		</tr>
		<tr>
			<td class="title2">Workflow Status</td>
			<td class="value">
				<select id="statusId" class="frmSelectClass">
					<option selected='selected' value=-1>ALL</option>
					<c:forEach var="status" items="${statuses}">
						<option value="${status.id}">${status.description}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td class="title2">Format</td>
			<td class="value"><select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select></td>
		</tr>
		<tr align="right">
			<td colspan="3"><input type="button" value="Generate"
				onclick="searchReport();" /></td>
		</tr>
	</table>
	<div>
		<iframe id="iFrame">ReclassRegister</iframe>
	</div>
</body>
</html>