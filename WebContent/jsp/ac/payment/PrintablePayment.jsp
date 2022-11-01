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
	border: 1px solid black;
	font-size: 12px;
}

#accountListTable th{
	background-color: #eee;
}

#nameAndDate {
	width: 100%; 
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
<script type="text/javascript" src="../../../../js/datetimepicker.js"></script>
<script type="text/javascript" src="../../../../js/javascripts.js"></script>
<script type="text/javascript" src="../../../../js/jquery/jquery1.7.2min.js"></script>
<script type="text/javascript">

</script>
</head>
<body>

<div>
	<table id="nameAndDate">
		<tr>
			<td style="text-align: left;">
				Name: ${customerName}
			</td>
			<td style="text-align: right;">
				<form:form method="POST" commandName="payment" id="payment">
					Date: 
					<img src="../../../../images/cal.gif" onclick="javascript:NewCssCal('date')" style="cursor:pointer" style="float: right;"/>
					<form:input path="date" size="17" onblur="evalDate('date')" />
				</form:form>
			</td>
		</tr>
	</table>
</div>
<table id="accountListTable">
			<thead>
				<tr>
					<th width="2%">#</th>
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
						<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${accountReceivable.amount}" /> </td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="font-weight: bold; ">Total </td>
					<c:forEach var="accountReceivable" items="${accountReceivables.data}" begin="0" end="${accountReceivables.pageSetting.maxResult}" varStatus="status">
						<c:set var="totalAmount" value="${totalAmount + accountReceivable.amount}" />
					</c:forEach>
					<td colspan="1" style="text-align: right;  font-weight: bold; "><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmount}" /> </td>
				</tr>
			
			</tfoot>
</table>
<div style="text-align: right; margin-top: 10px;">
	<input type="button" value="Add Receivable" />
</div>
<div>
	<input type="hidden" id="receivableCount" value="${receivableCount}" />
</div>
<div class="controls">
	<input type="submit" id="btnPrint" name="btnPrint" value="Print" onclick="window.print(); window.close();" />	
	<input type="button" id="btnCancel" name="btnCancel" value="Close" onclick="window.close();" />	
</div>
</body>
</html>