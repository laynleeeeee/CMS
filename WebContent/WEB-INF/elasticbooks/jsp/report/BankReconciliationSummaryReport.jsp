<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Bank Reconciliation Summary Report.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<title>Insert title here</title>
<style type="text/css">
input.numeric {
	border: 1px solid gray;
	text-align: right;
	padding: 3px;
	width: 170px;
}

.bankBalances {
	text-align: right;
	width: 165px;
}
</style>
<script type="text/javascript">
var bankAccountAndBalances = "";

$(function () {
	$("#bankBalance").bind("keyup keydown", function() {
		checkAndSetDecimal("bankBalance");
	});
});

function generateReport() {
	$("#dateError").text("");
	setBankAccountIdAndBalances();
	var bankAcctIdAndBalances = bankAccountAndBalances;
	var asOfDate = $("#asOfDate").val();
	var bankDate = $("#bankDate").val();
	var amount = $("#bankBalance").val();
	var bankAcctName = processSearchName($("#bankAcctId option:selected").text());
	evalDate("asOfDate", false);
	evalDate("bankDate", false);
	if(asOfDate == "" || bankDate == "") {
		$("#dateError").text("As of date and/or bank date are required fields.");
	} else if ($("#bankAcctError").text() == "" && $("#bankBalanceError").text() == "") {
		var uri = contextPath+"/bankReconSummary/generateReport?bankAcctIdAndBalances="+bankAcctIdAndBalances+"&asOfDate="+asOfDate+
			"&bankDate="+bankDate+"&bankAcctName="+bankAcctName;
		$("#iFrame").attr('src', uri);
		$("#iFrame").load();
	}
}

function addBankAcctLine() {
	var tr = document.createElement('tr');
	tr.innerHTML = "<tr><td></td><td><select id='bankAcctId' class='frmSelectClass'><c:forEach var='bankAccount' items='${bankAccounts}'>" +
	"<option value='${bankAccount.id}'>${bankAccount.name}</option></c:forEach></select></td>" +
	"<td><input type='text' class='bankBalances' onkeydown='processBankBalance(this);' onkeyup='processBankBalance(this);'class='numeric'><a onclick='addBankAcctLine()'>[+]</a>"+
	"<a onclick='deleteBankAcctLine(this.parentNode.parentNode.rowIndex)'>[-]</a></td></tr>";
	document.getElementById("bankAcctBal").appendChild(tr);
}

function deleteBankAcctLine(rowIndex) {
	document.getElementById("bankAcctBal").deleteRow(rowIndex);
}

function setBankAccountIdAndBalances() {
	bankAccountAndBalances = "";
	$("#bankAcctError").text("");
	$("#bankBalanceError").text("");
	$(document.getElementById("bankAcctBal").rows).each(function(i) {
		var bankAccId = $(this).find("#bankAcctId").val();
		var balance = $(this).find(".bankBalances").val();
		bankAccountAndBalances += bankAccId+","+balance+";";
		if(balance == null || balance == ""){
			$("#bankBalanceError").text("Bank Balance is a required field.");
		} else if (bankAccId == null){
			$("#bankAcctError").text("Bank Account is a required field.");
		}
		$(document.getElementById("bankAcctBal").rows).each(function(index) {
			var bankAccId1 = $(this).find("#bankAcctId").val();
			if(i != index && bankAccId == bankAccId1){
				$("#bankAcctError").text("Bank Account should be unique.");
			}
		});
	});
}

function processBankBalance(field) {
	if(field != null){
		if(isNaN($(field).val())){
			$(field).val(0);
		} else{
			limitText(field);
		}
	}
}
</script>
</head>
<body>
<table id = "bankAcctBal">
	<tr>
		<td class="title2">Bank Account/Bank Balance</td>
		<td><select id="bankAcctId" class="frmSelectClass">
			<c:forEach var="bankAccount" items="${bankAccounts}">
				<option value="${bankAccount.id}">${bankAccount.name}</option>
			</c:forEach>
			</select>
		</td>
		<td><input type="text" class="bankBalances" onkeydown="processBankBalance(this);" onkeyup="processBankBalance(this);"><a onclick="addBankAcctLine()">[+]</a></td>
	</tr>
</table>
<table>
	<tr>
		<td></td>
		<td><span id="bankAcctError" class="error"></span></td>
	</tr>
	<tr>
		<td></td>
		<td><span id="bankBalanceError" class="error"></span></td>
	</tr>
	<tr>
		<td class="title2">As of Date </td>
		<td class="tdDateFilter">
			<input type="text" id="asOfDate" maxlength="10" class="dateClass2" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('asOfDate')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td class="title2">Bank Date </td>
		<td class="tdDateFilter">
			<input type="text" id="bankDate" maxlength="10" class="dateClass2" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('bankDate')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><span id="dateError" class="error"></span></td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Search" onclick="generateReport()"/></td>
	</tr>
</table>
<div>
	<iframe id="iFrame"></iframe>
</div>
</body>
</html>