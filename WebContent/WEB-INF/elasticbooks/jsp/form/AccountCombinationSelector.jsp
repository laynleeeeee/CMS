<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Account combination selector
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
function selectCombination () {
	var companyNumber = $("#companyNumber").val();
	var divisionNumber = $("#divisionNumber").val();
	var accountNumber = $("#accountNumber").val();
	doAfterSelection (companyNumber, divisionNumber, accountNumber);
}

function filterDivisions(companyTextBox) {
	// Get the selectedId
	var companyId = $(companyTextBox).find(":selected").attr("id");
	var uri = contextPath+"/getDivisions?companyId="+companyId;
	$("#divisionNumber").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			//We Need to update the common function to allow the setting of id. 
			//implemented the work around for the id. 
			return rowObject["number"] + "'" + "id='" + rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["numberAndName"];
		}
	};
	postHandler = {
			doPost: function(data) {
				//Set selected
				filterAccounts($("#divisionNumber"));
			}
	};
	loadPopulate (uri, false, null, "divisionNumber", optionParser, postHandler);
}

function filterAccounts(divisionTextBox) {
	var companyId = $("#companyNumber").find(":selected").attr("id");
	var divisionId = $(divisionTextBox).find(":selected").attr("id");
	
	var uri = contextPath+"/getAccounts?companyId="+companyId+"&divisionId="+divisionId;
	$("#accountNumber").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			//We Need to update the common function to allow the setting of id. 
			//implemented the work around for the id. 
			return rowObject["number"] + "'" + "id='" + rowObject["id"];
		},

		getLabel: function (rowObject) {
			return rowObject["number"] + " - "+rowObject["accountName"];
		}
	};
	loadPopulate (uri, false, null, "accountNumber", optionParser, null);
}

</script>
<title>Account Combination selector</title>
</head>
<body>
<div >
<img id="imgCloseCList" src="${pageContext.request.contextPath}/images/cal_close.gif"
		class="imgCloseComboList"/>
<table class="formTable">
	<tr>
		<td>Company</td>
		<td><select id="companyNumber" class="frmSelectClass" onchange="filterDivisions(this);">
				<c:forEach var="company" items="${companies}">
					<option value="${company.companyNumber}" id="${company.id}">${company.numberAndName}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td>Division</td>
		<td><select id="divisionNumber" class="frmSelectClass" onchange="filterAccounts(this);">
				<c:forEach var="division" items="${divisions}">
					<option value="${division.number}" id="${division.id}">${division.numberAndName}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td>Account</td>
		<td><select id="accountNumber" class="frmSelectClass">
				<c:forEach var="account" items="${accounts}">
					<option value="${account.number}" id="${account.id}">${account.number} - ${account.accountName}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="3" align="right"><input type="button" value="select" onclick="selectCombination()"> </td>
	</tr>
</table>
</div>
</body>
</html>