<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: AR Miscellaneous form for viewing only.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
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
	formObjectId = parseInt("${arMiscellaneous.ebObjectId}");
	if("${arMiscellaneous.id}" != 0)  {
		$("#imgEdit").hide();
	}
	if ("${arMiscellaneous.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Other Receipt</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table class="formTable">
			<tr>
				<td class="label">Sequence Number</td>
				<td class="label-value">${arMiscellaneous.sequenceNo}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${arMiscellaneous.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AR Miscellaneous Header</legend>
		<table>
			<tr>
				<td class="label">* Type</td>
				<td class="label-value">${arMiscellaneous.arMiscellaneousType.name}</td>
			</tr>
			
			<tr>
				<td class="label">* Check Number</td>
				<td class="label-value">${arMiscellaneous.refNumber}</td>
			</tr>
			
			<tr>
				<td class="label">* Receipt Method</td>
				<td class="label-value">${arMiscellaneous.receiptMethod.name}</td>
			</tr>
			
			<tr>
				<td class="label">* Customer</td>
				<td class="label-value">${arMiscellaneous.arCustomer.name}</td>
			</tr>
				
			<tr>
				<td class="label">* Customer Account</td>
				<td class="label-value">${arMiscellaneous.arCustomerAccount.name}</td>
			</tr>
						
			<tr>
				<td class="label">* Receipt No.</td>
				<td class="label-value">${arMiscellaneous.receiptNumber}</td>
			</tr>
		
			<tr>
				<td class="label">* Receipt Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${arMiscellaneous.receiptDate}"/>
				</td>
			</tr>
			
			<tr>
				<td class="label">* Maturity Date</td>
				<td class="label-value">
					<fmt:formatDate pattern="MM/dd/yyyy" value="${arMiscellaneous.maturityDate}"/>
				</td>
			</tr>
			
			<tr>
				<td class="label">* Description</td>
				<td class="label-value">${arMiscellaneous.description}</td>
			</tr>
			
			<tr>
				<td class="label">* Amount</td>
				<td class="label-value">
					<fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${arMiscellaneous.amount}'/>
				</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AR Lines</legend>
		<table class="dataTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm">#</th>
					<th width="28%" class="th-td-norm">AR Line</th>
					<th width="10%" class="th-td-norm">Qty</th>
					<th width="10%" class="th-td-norm">UOM</th>
					<th width="10%" class="th-td-norm">UP</th>
					<th width="15%" class="th-td-norm">Tax Type</th>
					<th width="10%" class="th-td-norm">VAT Amount</th>
					<th width="15%" class="th-td-edge">Amount</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${arMiscellaneous.arMiscellaneousLines}" var="arLine" varStatus="status">
					<tr>
						<!-- Row number -->
						<td class="td-numeric v-align-top">${status.index + 1}</td>
						<!-- AR Line Setup -->
						<td class="th-td-norm v-align-top">${arLine.arLineSetup.name}</td>
						<!-- Quantity -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${arLine.quantity}" /></td>
						<!-- UOM -->
						<td class="th-td-norm v-align-top">${arLine.unitMeasurement.name}</td>
						<!-- UP Amount -->
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="6" value="${arLine.upAmount}" /></td>
						<td class="th-td-norm v-align-top">${arLine.taxType.name}</td>
						<td class="td-numeric v-align-top">
							<fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${arLine.vatAmount}" />
						</td>
						<!-- Amount -->
						<td class="td-numeric v-align-top" style="border-right: none;">
							<fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${arLine.amount}" /></td>
					</tr>
					<c:set var="totalOcVat" value="${totalOcVat + arLine.vatAmount}" />
					<c:set var="otherCharges" value="${otherCharges + arLine.amount}" />
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="7" style="font-weight:bold;">Total</td>
					<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${otherCharges}" /></td>
				</tr>
			</tfoot>
		</table>
		<br>
	</fieldset>
	<table class="frmField_set">
		<tr>
			<td class="footerViewCls" colspan="3">Sub Total</td>
			<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${otherCharges}" /></td>
		</tr>
		<tr>
			<td class="footerViewCls" colspan="3">Total VAT</td>
			<td class="footerViewCls" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${totalOcVat}" /></td>
		</tr>
		<tr>
			<td class="footerViewCls" colspan="3">Withholding Tax</td>
			<td class="footerViewCls">${arMiscellaneous.wtAcctSetting.name}</td>
			<td class="footerViewCls">
				<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
					value="${arMiscellaneous.wtAmount}"/>
			</td>
		</tr>
		<tr>
			<td class="footerViewCls" colspan="3">Total Amount Due</td>
			<td class="footerViewCls" align="right" colspan="2"><fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${otherCharges + totalOcVat - arMiscellaneous.wtAmount}" /></td>
		</tr>
	</table>
</div>
</body>
</html>