<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description: Customer Advance Payment view.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<style type="text/css">
.monetary {
	text-align: right;
}

.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 300px;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${customerAdvancePayment.ebObjectId}");
	if ("${customerAdvancePayment.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Customer Advance Payment - ${customerAdvancePayment.division.name}</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table class="formTable">
					<tr>
						<td class="label">CAP
							<c:if test="${customerAdvancePayment.customerAdvancePaymentTypeId == 3}"> - IS</c:if> No.
						</td>
						<td class="label-value">
							${customerAdvancePayment.capNumber}
						</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${customerAdvancePayment.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Customer Advance Payment Header</legend>
				<table class="formTable">
					<tr>
						<td class="label">Company</td>
						<td class="label-value">${customerAdvancePayment.company.name}</td>
					</tr>
					<tr>
						<td class="label">Division</td>
						<td class="label-value">${customerAdvancePayment.division.name}</td>
					</tr>
					<tr>
						<td class="label">Sales Order Reference</td>
						<td class="label-value">${customerAdvancePayment.salesOrder.sequenceNumber}</td>
					</tr>
					<tr>
						<td class="label">PO/PCR No.</td>
						<td class="label-value">${customerAdvancePayment.poNumber}</td>
					</tr>
					<tr>
						<td class="label">Customer</td>
						<td class="label-value">${customerAdvancePayment.arCustomer.name }</td>
					</tr>
					<tr>
						<td class="label">Customer Account</td>
						<td class="label-value">${customerAdvancePayment.arCustomerAccount.name}</td>
					</tr>
					<tr>
						<td class="label">Reference No.</td>
						<td class="label-value">${customerAdvancePayment.refNumber}</td>
					</tr>
					<tr>
						<td class="label">Receipt Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${customerAdvancePayment.receiptDate}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Maturity Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${customerAdvancePayment.maturityDate}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Currency</td>
						<td class="label-value">${customerAdvancePayment.currency.name}</td>
					</tr>
					<tr>
						<td class="label">Advance Payment</td>
						<td class="label-value">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${customerAdvancePayment.amount}" /> 
						</td>
					</tr>
					
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Sales Order Table</legend>
				<table class="dataTable">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="50%" class="th-td-norm">Particulars</th>
						<th width="45%" class="th-td-norm">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${customerAdvancePayment.capLines}" var="capLine" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${capLine.referenceNo}</td>
							<td class="th-td-edge txt-align-right v-align-top">
								<c:choose>
									<c:when test="${capLine.amount lt 0}">
										(<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${capLine.amount * -1}" />)
									</c:when>
									<c:otherwise>
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${capLine.amount}" />
									</c:otherwise>
								</c:choose>
							</td>
							<c:set var="totalAmount" value="${totalAmount + capLine.amount}" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td class="th-td-norm txt-align-right v-align-top" colspan="2">Advance Payment</td>
						<td class="th-td-edge txt-align-right v-align-top">
							(<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value="${customerAdvancePayment.amount}"/>)
						</td>
					</tr>
					<tr>
						<td class="th-td-norm txt-align-right v-align-top" colspan="2">Remaining Balance</td>
						<td class="th-td-edge txt-align-right v-align-top">
							<span id="spanTotalAmount">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${totalAmount - customerAdvancePayment.amount}" />
							</span>
						</td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
			<c:if test="${not empty customerAdvancePayment.referenceDocuments}">
			<fieldset class="frmField_set">
				<legend>Documents</legend>
				<table class="dataTable" id="referenceDocuments">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm">#</th>
							<th width="18%" class="th-td-norm">Name</th>
							<th width="18%" class="th-td-norm">Description</th>
							<th width="18%" class="th-td-edge">File</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${customerAdvancePayment.referenceDocuments}" var="doc" varStatus="status">
							<tr> 
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${doc.fileName}</td>
								<td class="th-td-norm v-align-top">${doc.description}</td>
								<td class="th-td-edge v-align-top" id="file">
									<a href="${doc.file}" download="${doc.fileName}" class="fileLink" id="file">${doc.fileName}</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
			<br>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>