<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Daily cash collection main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#date").val(newDate);

	filterUsers();
});

function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var userId = $("#userId option:selected").val();
	var strDate = $("#date").val();
	var orderType = $("#selOrderType").val();
	var status = $("#slctStatus").val();
	return "?companyId="+companyId+"&userId="+userId+"&date="+strDate+"&orderType="+orderType
			+"&status="+status+"&formatType=pdf";
}

function filterUsers(){
	$("#userId").empty();
	var companyId = $("#companyId").val();
	var positionId = $("#positionId").val();
	var uri = contextPath + "/getUsers/position?positionId="+positionId
			+"&companyId="+companyId;

	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null)
					return rowObject["id"];
			},

			getLabel: function (rowObject){
				if (rowObject != null)
					return rowObject["lastName"] + ", " + rowObject["firstName"] + " " 
					+ rowObject["middleName"];
			}
	};

	postHandler = {
			doPost: function(data) {
				// This is to remove any duplication.
				var found = [];
				$("#userId option").each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
				  	found.push(this.value);
				});
			}
	};
	loadPopulate (uri, false, null, "userId", optionParser, postHandler);
}

function searchDailyCashCollection() {
	if($.trim($("#date").val()) == "") {
		$("#spanDateError").text("Date is required.");
	}else {
		var url = contextPath + "/dailyCashCollection/generate"+getCommonParam();
		$('#reportDailyCashCollection').attr('src',url);
		$('#reportDailyCashCollection').load();
	}

}
</script>
</head>
<body>
<table>
	<tr>
		<td class="title2">Company</td>
		<td>
			<select id="companyId" class="frmSelectClass" onchange="filterUsers();">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	
	<tr>
		<td class="title2">Position</td>
		<td>
			<select id="positionId" class="frmSelectClass" onchange="filterUsers();">
				<c:forEach var="position" items="${positions}">
					<option value="${position.id}">${position.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	
	<tr>
		<td class="title2">User</td>
		<td>
			<select id="userId" class="frmSelectClass">
			</select>
		</td>
	</tr>

	<tr>
		<td class="title2">Date </td>
		<td class="tdDateFilter">
			<input type="text" id="date" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('date')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>

	<tr>
		<td class="title2">Order Type </td>
		<td class="tdDateFilter">
			<select id="selOrderType" class="frmSelectClass">
				<option value=REFERENCE_NO>Reference No.</option>
				<option value=INVOICE_NO>Invoice No.</option>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Status</td>
		<td class="tdDateFilter">
			<select id="slctStatus" class="frmSelectClass">
				<option value=1>COMPLETED</option>
				<option value=-1>CANCELLED AND COMPLETED</option>
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchDailyCashCollection();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportDailyCashCollection"></iframe>
</div>
<hr class="thin2">
</body>
</html>