<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Transaction form for viewing only
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<title>AR Transaction Form</title>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${arTransaction.ebObjectId}");
	if ("${arTransaction.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">AR Transaction</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">Sequence Number</td>
					<td class="label-value">${arTransaction.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${arTransaction.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Transaction Header</legend>
			<table class="formTable" border=0>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${arTransaction.company.name}</td>
				</tr>
				<tr>
					<td class="label">Transaction Type</td>
					<td class="label-value">${arTransaction.arTransactionType.name}</td>
				</tr>
				<tr>
					<td class="label">Transaction No</td>
					<td class="label-value">${arTransaction.transactionNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer Name</td>
					<td class="label-value">${arTransaction.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${arTransaction.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${arTransaction.term.name}</td>
				</tr>
				<tr>
					<td class="label">Transaction Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${arTransaction.transactionDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">GL Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${arTransaction.glDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${arTransaction.dueDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Description</td>
					<td class="label-value">${arTransaction.description}</td>
				</tr>
				<tr>
					<td class="label">Amount</td>
					<td class="label-value">
						<fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${arTransaction.amount}'/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>AR Lines</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="3%" class="th-td-norm">#</th>
						<th width="30%" class="th-td-norm">AR LINE</th>
						<th width="15%" class="th-td-norm">QTY</th>
						<th width="19%" class="th-td-norm">UOM</th>
						<th width="16%" class="th-td-norm">UP</th>
						<th width="21%" class="th-td-edge">AMOUNT</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${arTransaction.arLines}" var="arLine" varStatus="status">
						<tr>
							<td class="td-numeric">${status.index + 1}</td>
							<td class="th-td-norm">${arLine.arLineSetup.name}</td>
							<td class="td-numeric">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${arLine.quantity}' />
							</td>
							<td class="th-td-norm">${arLine.unitMeasurement.name}</td>
							<td class="td-numeric">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${arLine.upAmount}' />
							</td>
							<td class="th-td-edge txt-align-right">
								<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value='${arLine.amount}' />
							</td>
							<c:set var="total" value="${total + arLine.amount}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5"></td>
						<td class="txt-align-right">
							<fmt:formatNumber type='number' minFractionDigits='2'
									maxFractionDigits='2' value='${total}' />
						</td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</div>
</div>
</body>
</html>