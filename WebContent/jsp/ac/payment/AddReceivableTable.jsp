<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">

#toBeAddedAccounttListTable  {
 	margin-top: 10px; 
 	border-collapse:collapse; 
 	width: 100%; 
}

#toBeAddedAccounttListTable th, #toBeAddedAccounttListTable td{
	font-size: 12px;
	padding: 5px;
}

#toBeAddedAccounttListTable th{
	border: 1px solid #FFF;
	background-color: #000;
	color: #FFF;
}

#toBeAddedAccounttListTable td {
	border: 1px solid #EEE;
}

</style>
</head>
<body>
<input type="hidden" id="hiddenRemainingIds" value="${remainingReceivableIds}" /> 
<input type="hidden" id="hiddenCurrentIds" value="${currentReceivableIds}" /> 
<table id="toBeAddedAccounttListTable">
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="1%"> </th>
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
						<td><input type="checkbox" id="${accountReceivable.id}" onclick="populateToBeAddedRecord(${accountReceivable.id});"/></td> 
						<td>${accountReceivable.referenceId}</td>
						<td>${accountReceivable.description}</td>
						<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${accountReceivable.withInterest}" /> </td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" style="font-weight: bold; ">Total </td>
					<td colspan="1" style="text-align: right;  font-weight: bold; ">
						<input type="hidden" id="totalSelectedAcctReceivableAmt" value="${totalSelectedAcctReceivableAmt}" />
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalSelectedAcctReceivableAmt}" />
					</td>
				</tr>
			</tfoot>
</table>
</body>
</html>