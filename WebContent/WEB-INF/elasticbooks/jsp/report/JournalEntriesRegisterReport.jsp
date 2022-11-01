<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Journal Entries Register search page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
function getCommonParam () {
	var companyId = $("#companyId option:selected").val();
	var glDateFrom = $("#glDateFrom").val();
	var glDateTo = $("#glDateTo").val();
	var status = $("#statusId").val();
	var source = processSearchName($("#source").val());
	var refNo = processSearchName($("#refNo").val());
	var formatType = $("#formatType").val();

	return "?companyId="+companyId+"&strFromDate="+glDateFrom+
			"&strToDate="+glDateTo+"&statusId="+status+
			"&source="+source+"&refNo="+refNo+"&formatType="+formatType;
}

function searchJournalRegister(){
	var companyId = $("#companyId option:selected").val();
	var glDateFrom = $("#glDateFrom").val();
	var glDateTo = $("#glDateTo").val();
	if (companyId != -1 && glDateFrom != "" && glDateTo != "") {
		$("#spanCompanyError").text("");
		$("#spanDateError").text("");
		$("#spanStatusError").text("");
		var url = contextPath + "/journalEntriesRegisterReport/generate" + getCommonParam();
		$('#reportJournalEntry').attr('src',url);
		$('#reportJournalEntry').load();
	} else {
		if (companyId == -1) 
			$("#spanCompanyError").text("Company is required.");
		if (glDateFrom == "" && glDateTo == "") 
			$("#spanDateError").text("Date range is required.");
	} 
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
			<select id="companyId" class="frmSelectClass">
				<option selected='selected' value=-1>Please select a company</option>
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	
	<tr>
		<td class="title2">GL Date </td>
		<td class="tdDateFilter">
			<input type="text" id="glDateFrom" maxlength="10" class="dateClass2" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="glDateTo" maxlength="10" class="dateClass2" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Reference No.</td>
		<td>
			<input type="text" id="refNo" class="input"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Sources </td>
		<td>
			<select id="source" class="frmSelectClass">
				<option selected='selected' value='ALL'>ALL</option>
				<c:forEach var="src" items="${sources}">
					<option value="${src}">${src}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Status </td>
		<td><select id="statusId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
			<option value=0>UNPOSTED</option>
			<option value=1>POSTED</option>
		</select></td>
	</tr>
	<tr>
		<td></td>
		<td>
			<span id="spanStatusError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="searchJournalRegister()"/></td>
	</tr>
</table>
<div>
	<iframe id="reportJournalEntry"></iframe>
</div>
</body>
</html>