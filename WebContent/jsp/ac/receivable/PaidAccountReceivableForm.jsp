<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
.currency, .total {
	text-align: right;
}

.total{
	font-weight: bold;
}

#customerInfo {
	display: none; 
}

 #amount {
 	text-align: right;
 }
</style>
<script type="text/javascript">
$(document).ready(function() {	
	$('#errorDate').hide();
	$('#errorReferenceId').hide();
	$('#errorDescription').hide();
	$('#errorAmount').hide();
	$('#errorDueDate').hide();
	
	$('#amount').val($('#principal').val());
	
			
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
	
	if ($("#dueDate").val().length == 0) {
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
		
		$("#dueDate").val(month + "/" + subString[2] + "/" + subString[3]);
	} 
	
	if ($("#dueDate").val().length > 10) {
		var subString = $("#dueDate").val().split(" ");
		var tmpDate = subString[0].split("-");
		$("#dueDate").val(tmpDate[1] + "/" + tmpDate[2] + "/" + tmpDate[0]);
		
		setSavedDate($("#dueDate").val());
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
<table style="width: 100%;" class="dataTable">	
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="5%">Date</th>
			<th width="5%">Due Date</th>
			<th width="5%">Paid Date</th>
			<th width="5%">Reference ID</th>
			<th width="20%">Description</th>
			<th width="12.4%">Principal</th>
			<th width="12.4%">Earned Interest</th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="ar" items="${customerAccountRecords}" varStatus="status">
			<c:if test="${ar.type == 1}">
				<c:set var="trClass" value="paidAccountRow"/>
			</c:if>
			<c:if test="${ar.type == 2}">
				<c:set var="trClass" value="paymentRow"/>
			</c:if>
			<c:if test="${ar.type == 3}">
				<c:set var="trClass" value=""/>
			</c:if>
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td >${status.index + 1} </td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${ar.date}" /> </td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${ar.dueDate}" /> </td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${ar.paidDate}" /> </td>
				<td>${ar.referenceId}</td>
				<td>${ar.description}</td>
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ar.principalAmount}" /> </td>
				<td style="text-align: right;"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ar.earnedInterest}" /> </td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
	</tfoot>
</table>

<form:form method="POST" commandName="accountReceivable" id="accountReceivable">
	<div class="modFormLabel">Account Receivable</div>
	<div class="modFormUnderline"> </div>
	
	
	<div style="width: 900px;"  >
		<table class="formTable" >
			<form:hidden path="id"/>
			<form:hidden path="customerId"/>
			<form:hidden path="principal" />
			<tr>
				<td>Date: </td>
				<td>
					<form:input path="date" onblur="evalDate('date')" />
					<img src="${pageContext.request.contextPath}/images/cal.gif" onclick="javascript:NewCssCal('date')" style="cursor:pointer" style="float: right;"/>
				</td>
			</tr>
			<tr id="errorDate">
				<td colspan="2">
					<font color="red">
						<form:errors path="date" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Due Date: </td>
				<td>
					<form:input path="dueDate" onblur="evalDate('dueDate')" />
					<img src="${pageContext.request.contextPath}/images/cal.gif"  onclick="javascript:NewCssCal('dueDate')" style="cursor:pointer" style="float: right;"/>
				</td>
			</tr>
			<tr id="errorDueDate">
				<td colspan="2">
					<font color="red">
						<form:errors path="dueDate" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Reference Id: </td>
				<td>
					<form:input path="referenceId"/>
				</td>
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
				<td>
					<form:input path="description" size="70"/>
				</td>
			</tr>
			<tr id="errorDescription">
				<td colspan="2">
					<font color="red">
						<form:errors path="description" cssClass="error"/>
					</font>
				</td>
			</tr>
			
			<tr>
				<td>Amount: </td>
				<td>
					<form:input path="amount" onblur="setDefaultAmount();"  />
				</td>
			</tr>
			<tr id="errorAmount">
				<td colspan="2">
					<font color="red">
						<form:errors path="amount" cssClass="error"/>
					</font>
				</td>
			</tr>
		
		</table>
		<div id="hiddenCompanyId"></div>
		<div class="controls">
			<input type="button" id="nextAccountReceivableBtn" name="nextAccountReceivableBtn" value="Next" onclick="saveAccountReceivable ();"/>	
			<input type="button" id="cancelAccountReceivableBtn" name="cancelAccountReceivableBtn" value="Cancel" onclick="cancelSave ();"/>	
		</div>
	</div>
</form:form>
</body>
</html>