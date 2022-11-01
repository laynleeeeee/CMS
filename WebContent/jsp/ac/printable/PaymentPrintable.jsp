<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">

#accountListTable  {
 	margin-top: 10px; 
 	border-collapse:collapse; 
 	width: 100%; 
}

#accountListTable th, #accountListTable td{
	font-size: 12px;
	padding: 5px;
}

#accountListTable th{
	border: 1px solid #FFF;
	background-color: #000;
	color: #FFF;
}

#accountListTable td {
	border: 1px solid #EEE;
}

#nameAndDate {
	width: 100%; 
}
#paymentFormTable {
	border: none;
	border-collapse:none;
}

#selectedReceivables {
	display: none;
}

#selectedAccounttListTable  {
 	margin-top: 10px; 
 	border-collapse:collapse; 
 	width: 100%; 
}

#selectedAccounttListTable th, #selectedAccounttListTable td{
	font-size: 12px;
	padding: 5px;
}

#selectedAccounttListTable th{
	border: 1px solid #FFF;
	background-color: #000;
	color: #FFF;
}

#selectedAccounttListTable td {
	border: 1px solid #EEE;
}

#cash, #paymentChange {
	text-align: right;
}
 
 .controls {
 	text-align: right;
 	margin-top: 10px;
 }
 
 @media print {
	.controls, #frmPayment {
		display: none;
	}
}
</style>
<script type="text/javascript">
function convertToStandardDate(date) {
	var subString = date.split("-");
	return subString[1] + "/" + subString[2] + "/" + subString[0];
}

function initializeValues() {
	var date = convertToStandardDate(document.getElementById("hiddenPaymentDate").value);
	document.getElementById("paymentDate").innerHTML = date;
}
</script>
</head>
<body onload="initializeValues(); ">
<input type="hidden" id="customerId" value="${customerId}">
<input type="hidden" id="hiddenPaymentDate" value="${paymentDate}" />
<input type="hidden" id="hiddenCash" value="${cash}" />
<input type="hidden" id="hiddenPaymentChange" value="${paymentChange}" />

<div>
	<table id="nameAndDate">
		<tr>
			<td style="text-align: left;">
				Name: ${customerName}
			</td>
			<td style="text-align: right; width: 10%;">
				<table>
					<tr>
						<td>Date: </td>
						<td><div id="paymentDate"></div></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<div id="receivableListTable">
<table id="selectedAccounttListTable">
			<thead>
				<tr>
					<th width="3%">#</th>
					<th width="18%">Reference ID</th>
					<th width="42%">Description</th>
					<th width="17%">Amount (Php)</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="accountReceivable" items="${accountReceivables.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>
							${status.index + 1} 
							<input type="hidden" id="hidden${status.index + 1}" value="${accountReceivable.id}"  />
						</td>
						<td>${accountReceivable.referenceId}</td>
						<td>${accountReceivable.description}</td>
						<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${accountReceivable.withInterest}" /> </td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="font-weight: bold; ">Total </td>
					<td colspan="1" style="text-align: right;  font-weight: bold; ">
						<input type="hidden" id="totalSelectedAcctReceivableAmt" value="${totalSelectedAcctReceivableAmt}" />
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalSelectedAcctReceivableAmt}" />
					</td>
				</tr>
			</tfoot>
</table>
<center>
<table style="margin-top: 10px; padding: 100px;" >
	<tr>
		<td style="font-weight: bold; ">Cash Tender: </td>
		<td style="font-weight: bold; " >
			<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${cash}" />
		</td>
	</tr>
	<tr>
		<td style="font-weight: bold; ">Change: </td>
		<td style="font-weight: bold; ">
			<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentChange}" />
		</td>
	</tr>
</table>
</center>
</div>
<div>
	<input type="hidden" id="receivableCount" value="${receivableCount}" />
</div>
<div class="controls">
	<input type="submit" id="btnPrint" name="btnPrint" value="Print" onclick="window.print(); window.close();" />	
	<input type="button" id="btnClose" name="btnClose" value="Close" onclick="window.close();" />	
</div>
</body>
</html>