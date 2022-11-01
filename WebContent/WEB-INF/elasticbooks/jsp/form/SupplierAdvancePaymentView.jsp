<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Supplier advance payment view form JSP page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	formObjectId = parseInt("${supplierAdvPayment.ebObjectId}");
	if ("${supplierAdvPayment.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Supplier Advance Payment - ${supplierAdvPayment.division.name}</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">SAP No.</td>
					<td class="label-value">${supplierAdvPayment.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${supplierAdvPayment.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Supplier Advance Payment Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${supplierAdvPayment.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division</td>
					<td class="label-value">${supplierAdvPayment.division.name}</td>
				</tr>
				<tr>
					<td class="label">PO Reference</td>
					<td class="label-value">${supplierAdvPayment.poNumber}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${supplierAdvPayment.date}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Supplier</td>
					<td class="label-value">${supplierAdvPayment.supplier.name}</td>
				</tr>
				<tr>
					<td class="label">Supplier Account</td>
					<td class="label-value">${supplierAdvPayment.supplierAccount.name}</td>
				</tr>
				<tr>
					<td class="label">BMS No.</td>
					<td class="label-value">${supplierAdvPayment.bmsNumber}</td>
				</tr>
				<tr>
					<td class="label">Invoice Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${supplierAdvPayment.invoiceDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">GL Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${supplierAdvPayment.glDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${supplierAdvPayment.dueDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Reference No.</td>
					<td class="label-value">${supplierAdvPayment.referenceNo}</td>
				</tr>
				<tr>
					<td class="label">Requestor</td>
					<td class="label-value">${supplierAdvPayment.requestor}</td>
				</tr>
				<tr>
					<td class="label">Remarks</td>
					<td class="label-value">${supplierAdvPayment.remarks}</td>
				</tr>
				<tr>
					<td class="label">Currency</td>
					<td class="label-value">${supplierAdvPayment.currency.name}</td>
				</tr>
				<tr>
					<td class="label">Advance Payment</td>
					<td class="label-value">
						<fmt:formatNumber type='number' minFractionDigits='2'
							maxFractionDigits='2' value='${supplierAdvPayment.amount}'/>
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Purchase Order Table</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="58%" class="th-td-norm">Particulars</th>
						<th width="40%" class="th-td-norm">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="sapl" items="${supplierAdvPayment.advPaymentLines}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${sapl.referenceNo}</td>
							<td class="th-td-edge txt-align-right v-align-top">
							<c:choose>
								<c:when test="${sapl.amount lt 0}">
									(<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
										value="${sapl.amount * -1}"/>)
								</c:when>
								<c:otherwise>
									<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
										value="${sapl.amount}"/>
								</c:otherwise>
							</c:choose>
								<c:set var="totalAmount" value="${totalAmount + sapl.amount}"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td class="th-td-norm txt-align-right v-align-top" colspan="2">Advance Payment</td>
						<td class="th-td-edge txt-align-right v-align-top">
							(<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
									value="${supplierAdvPayment.amount}"/>)
						</td>
					</tr>
					<tr>
						<td class="th-td-norm txt-align-right v-align-top" colspan="2">Total Unpaid PO Costs</td>
						<td class="th-td-edge txt-align-right v-align-top">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2'
								value="${totalAmount - supplierAdvPayment.amount}"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
		<c:if test="${not empty supplierAdvPayment.referenceDocuments}">
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
						<c:forEach items="${supplierAdvPayment.referenceDocuments}" var="doc" varStatus="status">
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
	</div>
</body>
</html>