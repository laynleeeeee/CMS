<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!--

	Description: AP Invoice selector
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
var balance = 0;
var invoiceAmount = 0;
var APInvoice = new Array();
$(document).ready(function() {
	<c:forEach items="${approvedInvoices}" var="invoice">
		var apInvoice = new ApInvoice ("${invoice.id}", 
				"<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${invoice.amount}' />", 
				"<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${invoice.balance}' />");
		APInvoice.push(apInvoice);
	</c:forEach>
});

function ApInvoice (id, amount, balance) {
	this.id = id;
	this.amount = amount;
	this.balance = balance;
}

function getAmount(apInvoiceId) {
	for (var i = 0; i < APInvoice.length; i++) {
		if(apInvoiceId == APInvoice[i].id) {
			invoiceAmount = APInvoice[i].amount;
			balance = APInvoice[i].balance;
		}
	}
}

function selectInvoice() {
	var invoiceNumber = $("#invoiceNumber").val();
	var apInvoiceId = $("#invoiceNumber").find("option:selected").attr("id");
	getAmount(apInvoiceId);
	doAfterSelection(invoiceNumber, invoiceAmount, balance, apInvoiceId);
}
</script>
</head>
<body>
<div>
<img id="imgCloseCList" src="${pageContext.request.contextPath}/images/cal_close.gif"
		class="imgCloseComboList"/>
	<table class="formTable">
		<tr>
			<td>Invoice Number <br>
				<select id="invoiceNumber" class="frmSelectClass">
					<c:forEach items="${approvedInvoices}" var="invoice">
						<option value="${invoice.referenceNo}" id="${invoice.id}">${invoice.referenceNo}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td align="right"><input type="button" value="select" onclick="selectInvoice();"> </td>
		</tr>
	</table>
</div>
</body>
</html>