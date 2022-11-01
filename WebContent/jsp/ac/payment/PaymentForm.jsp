<!-- 

	Description: Payment form 
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
#amount {
	text-align: right;
}
</style>

<script type="text/javascript">
$(document).ready(function() {	
	if (!isEdit) $('#paymentDetailList').hide();
	$('#errorDate').hide();
	$('#errorReferenceId').hide();
	$('#errorDescription').hide();
	$('#errorAmount').hide();
	$('#errorDueDate').hide();
			
	if ($("#date").val().length == 0) {
		var dateNow = new Date();
		
		var monthString = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
		var monthNumber = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
		var month = "01";
		var subString = dateNow.toString().split(" ");
	
		for (var i=0; i<monthString.length; i++) {
			if (monthString[i] == subString[1]) {
				month = monthNumber[i];
				break;
			}
		}
		
		$("#date").val(month + "/" + subString[2] + "/" + subString[3]);
	} 
	
	if ($("#date").val().length > 10) {
		var subString = $("#date").val().split(" ");
		var tmpDate = subString[0].split("-");
		$("#date").val(tmpDate[1] + "/" + tmpDate[2] + "/" + tmpDate[0]);
		
		setSavedDate($("#date").val());
	} 
});

$(function(){
	$('input#amount').click(function () {
		if ($(this).val() == "0.0") $(this).val("");
	});
});

function setDefaultAmount() {
	if ($("#amount").val().length == 0) {
		$("#amount").val("0.0");
	}
}

</script>
</head>
<body>
	<table id="paymentDetailList" class="dataTable" border="0">
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="8%">Date</th>
					<th width="8%">Due Date</th>
					<th width="12%">Reference ID</th>
					<th width="40%">Description</th>
					<th width="10%">Paid Principal </th>
					<th width="10%">Earned Interest</th>
					<th width="10%">Total</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="paymentDetail" items="${paymentDetails}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${paymentDetail.date}" /> </td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${paymentDetail.dueDate}" /> </td>
						<td>${paymentDetail.referenceId}</td>
						<td>${paymentDetail.description}</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${paymentDetail.paidPrincipal}" /> 
						</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${paymentDetail.earnedInterest}" /> 
						</td>
						<td style="text-align: right;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" 
							value="${paymentDetail.paidPrincipal + paymentDetail.earnedInterest}" /> 
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="8"> 
						<span style="float: left; font-weight: bold;">Total</span>
						<span style="float: right; font-weight: bold;">							
							<c:forEach var="paymentDetail" items="${paymentDetails}" varStatus="status">
								<c:set var="total" value="${total + paymentDetail.paidPrincipal + paymentDetail.earnedInterest}" />
							</c:forEach>
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${total}" />
						</span> 
					</td>
				</tr>
				
				<tr>
					<td colspan="8"> 
						<span style="float: left; font-weight: bold;">Payment</span>
						<span style="float: right; font-weight: bold;">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentAmount}" />
						</span>
					</td>
				</tr>
			
			</tfoot>
		</table>


	<form:form method="POST" commandName="payment" id="payment">
	<div class="modFormLabel">Payment</div>
	<div class="modFormUnderline"> </div>
	<div style="width: 650px;" class="modForm">
		<table class="formTable" >
			<form:hidden path="id"/>
			<form:hidden path="customerId"/>
			<tr id="errorDate">
				<td colspan="2">
					<font color="red">
						<form:errors path="date" cssClass="error"/>
					</font>
				</td>
			</tr>
			<tr>
				<td>Date: </td>
				<td>
					<form:input path="date" size="6" onblur="evalDate('date')"/>
					<img src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" style="cursor:pointer" style="float: right;"/>
				</td>
			</tr>

		
			<tr>
				<td>Reference ID: </td>
				<td><form:input path="referenceId" size="7"/></td>
			</tr>
			<tr id="errorReferenceId">
				<td colspan="2">
					<font color="red">
						<form:errors path="referenceId" cssClass="error"/>
					</font>
				</td>
			</tr>

			<tr>
				<td>Description: </td>
				<td><form:input path="description"  size="70"/></td>
			</tr>
		
			<tr>
				<td>Amount: </td>
				<td><form:input path="amount"  size="7" onblur="setDefaultAmount();"/></td>
			</tr>
				<tr id="errorAmount">
				<td colspan="2">
					<font color="red">
						<form:errors path="amount" cssClass="error"/>
					</font>
				</td>
			</tr>
		</table>
		<div class="controls">
			<input type="button" id="nextPaymentBtn" value="Next" onclick="showPaymentDetails ();" />	
			<input type="button" id="cancelPaymentBtn" name="cancelPaymentBtn" value="Cancel" onclick="cancelPayment ();"/>	
		</div>
	</div>
	</form:form>
</body>
</html>