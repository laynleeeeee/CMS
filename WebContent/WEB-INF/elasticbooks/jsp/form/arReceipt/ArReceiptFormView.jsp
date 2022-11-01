<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: AR Receipt form for viewing only.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	// Disable this function; this is only applicable to AP payment
	doFormPreUpdateStatus = null;

	formObjectId = parseInt("${arReceipt.ebObjectId}");
	var statusId = "${arReceipt.formWorkflow.currentStatusId}";
	if (statusId == "16") {
		$("#imgEdit").show();
	}
	if (statusId == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Account Collection - ${arReceipt.division.name}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">Sequence Number</td>
				<td class="label-value">${arReceipt.sequenceNo}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${arReceipt.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>Account Collection Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${arReceipt.company.name}</td>
			</tr>
			<tr>
				<td class="label">Division</td>
				<td class="label-value">${arReceipt.division.name}</td>
			</tr>
			<tr>
				<td class="label">Type</td>
				<td class="label-value">${arReceipt.receiptType.name}</td>
			</tr><tr>
				<td class="label">Check/Online Reference No.</td>
				<td class="label-value">${arReceipt.refNumber}</td>
			</tr>
			<tr>
				<td class="label">Receipt Method</td>
				<td class="label-value">${arReceipt.receiptMethod.name}</td>
			</tr>
			<tr>
				<td class="label">Customer</td>
				<td class="label-value">${arReceipt.arCustomer.name}</td>
			</tr>
			<tr>
				<td class="label">Customer Account</td>
				<td class="label-value">${arReceipt.arCustomerAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Receipt No.</td>
				<td class="label-value">${arReceipt.receiptNumber}</td>
			</tr>
			<tr>
				<td class="label">Receipt Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arReceipt.receiptDate}"/></td>
			</tr>
			<tr>
				<td class="label">Maturity Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arReceipt.maturityDate}"/></td>
			</tr>
			<tr>
				<td class="label">Currency</td>
				<td class="label-value">${arReceipt.currency.name}</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${arReceipt.amount}'/></td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AR Transactions</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="3%" class="th-td-norm">#</th>
					<th width="60%" class="th-td-norm">AR Transaction No</th>
					<th width="30%" class="th-td-edge">Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${arReceipt.arReceiptLines}" var="arReceiptLine" varStatus="status">
					<tr>
						<td class="td-numeric">${status.index + 1}</td>
						<td class="th-td-norm">${arReceiptLine.referenceNo}</td>
						<td class="th-td-edge txt-align-right"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${arReceiptLine.amount}'/></td>
						<c:set var="total" value="${total + arReceiptLine.amount}" />
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</fieldset>
	<br>
	<c:if test="${not empty arReceipt.referenceDocuments}">
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
					<c:forEach items="${arReceipt.referenceDocuments}" var="doc" varStatus="status">
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
	<c:set var="totalSales" value="${otherCharges + total}" />
	<table class="frmField_set footerTotalAmountTbl">
		<tr>
			<td style="width: 22%;"></td>
			<td style="width: 22%;"></td>
			<td style="width: 22%;">Sub Total</td>
			<td style="width: 22%;"></td>
			<td style="width: 12%;">
				<fmt:formatNumber type="number" minFractionDigits="2"
					maxFractionDigits="2" value="${totalSales}" />
			</td>
		</tr>
		<tr>
			<td colspan="3">Recoupment</td>
			<td></td>
			<td>
				<span>(
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${arReceipt.recoupment}" />
				</span>)
			</td>
		</tr>
		<tr>
			<td colspan="3">Retention</td>
			<td></td>
			<td>
				<span>(
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${arReceipt.retention}" />
				</span>)
			</td>
		</tr>
		<tr>
			<td colspan="3">Amount Collected</td>
			<td></td>
			<td align="right">
				<fmt:formatNumber type="number" minFractionDigits="2"
					maxFractionDigits="2" value="${arReceipt.amount}" />
			</td>
		</tr>
	</table>
</div>
</body>
</html>