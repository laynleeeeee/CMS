<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Sales order view form jsp page
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable2.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview2.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${salesOrder.ebObjectId}");
	if ("${salesOrder.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	//disable reference table link
	disableRefTblLink("${hasEditAccess}", "referenceDocuments", "file");
});
</script>
<style type="text/css">
.monetary {
	text-align: right;
}

.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 22%;
}

.td-txt-align-top {
	vertical-align: top;
	padding-top: 8px;
}
</style>
</head>
<body>
<div id="divForm" class="formDivBigForms">
	<div class="modFormLabel">Sales Order - ${salesOrder.division.name}</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">SO No.</td>
					<td class="label-value">${salesOrder.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${salesOrder.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Sales Order Header</legend>
			<table class="formTable">
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${salesOrder.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division</td>
					<td class="label-value">${salesOrder.division.name}</td>
				</tr>
				<tr class="hidden">
					<td class="label">Sales Quotation Reference </td>
					<td class="label-value">${salesOrder.refSQNumber}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${salesOrder.date}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Estimated Delivery Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${salesOrder.deliveryDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">SO Type</td>
					<td class="label-value">${salesOrder.soType.name}</td>
				</tr>
				<tr>
					<td class="label">PO/PCR No.</td>
					<td class="label-value">${salesOrder.poNumber}</td>
				</tr>
				<tr>
					<td class="label">Customer</td>
					<td class="label-value">${salesOrder.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${salesOrder.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${salesOrder.term.name}</td>
				</tr>
				<tr class="hidden">
					<td class="label">Cluster</td>
					<td class="label-value">${salesOrder.customerType.name}</td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${salesOrder.shipTo}</td>
				</tr>
				<tr>
					<td class="label">Remarks</td>
					<td class="label-value">${salesOrder.remarks}</td>
				</tr>
				<tr>
					<td class="label">Currency</td>
					<td class="label-value">${salesOrder.currency.name}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Deposit</legend>
			<table class="formTable">
				<tr>
					<td class="label">Deposit Required</td>
					<td class="label-value">${salesOrder.deposit ? 'Yes' : 'No'}</td>
				</tr>
				<tr>
					<td class="label">Advance Payment Amount</td>
					<td class="label-value">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${salesOrder.advancePayment}"/>
					</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${not empty salesOrder.soItems}">
			<fieldset class="frmField_set">
				<legend>Good/s</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="2%" class="th-td-norm">#</th>
							<th width="12%" class="th-td-norm">Stock Code</th>
							<th width="13%" class="th-td-norm">Description</th>
							<th width="6%" class="th-td-norm">Qty</th>
							<th width="6%" class="th-td-norm">UOM</th>
							<th width="7%" class="th-td-norm">Gross Price</th>
							<th width="7%" class="th-td-norm">Discount<br>Type</th>
							<th width="6%" class="th-td-norm">Discount<br>Value</th>
							<th width="6%" class="th-td-norm">Computed<br>Discount</th>
							<th width="7%" class="th-td-norm">Tax Type</th>
							<th width="6%" class="th-td-norm">VAT Amount</th>
							<th width="10%" class="th-td-norm">Amount</th>
							<th width="12%" class="th-td-norm" style="display: none;">Memo</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesOrder.soItems}" var="soi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${soi.item.stockCode}</td>
								<td class="th-td-norm v-align-top">${soi.item.description}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${soi.item.unitMeasurement.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="4"
										maxFractionDigits="6" value="${soi.grossAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${soi.itemDiscountType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.discountValue}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.discount}"/>
								</td>
								<td class="th-td-norm v-align-top">${soi.taxType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.vatAmount}"/>
								</td>
								<td class="th-td-norm v-align-top monetary" style="">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.amount}"/>
								</td>
								<td class="th-td-norm v-align-top" style="display: none;">${soi.memo}</td>
							</tr>
							<c:set var="totalItemDiscount" value="${totalItemDiscount + soi.discount}"/>
							<c:set var="totalItemVAT" value="${totalItemVAT + soi.vatAmount}"/>
							<c:set var="totalItemAmount" value="${totalItemAmount + soi.amount}"/>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="10" style="font-weight:bold;">Total</td>
							<td colspan="2" class="monetary">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalItemAmount}"/>
							</td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty salesOrder.soLines}">
			<fieldset class="frmField_set">
				<legend>Service/s</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="2%" class="th-td-norm">#</th>
							<th width="12%" class="th-td-norm">Service</th>
							<th width="13%" class="th-td-norm">Description</th>
							<th width="6%" class="th-td-norm">Qty</th>
							<th width="7%" class="th-td-norm">UOM</th>
							<th width="10%" class="th-td-norm">Gross Price</th>
							<th width="10%" class="th-td-norm">Discount<br>Type</th>
							<th width="8%" class="th-td-norm">Discount<br>Value</th>
							<th width="8%" class="th-td-norm">Computed<br>Discount</th>
							<th width="10%" class="th-td-norm">Tax Type</th>
							<th width="8%" class="th-td-norm">VAT Amount</th>
							<th width="10%" class="th-td-norm">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesOrder.soLines}" var="soi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${soi.serviceSettingName}</td>
								<td class="th-td-norm v-align-top">${soi.description}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${soi.unitMeasurement.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="4"
										maxFractionDigits="6" value="${soi.upAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${soi.itemDiscountType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.discountValue}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.discount}"/>
								</td>
								<td class="th-td-norm v-align-top">${soi.taxType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.vatAmount}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${soi.amount}"/>
								</td>
							</tr>
							<c:set var="totalOcVAT" value="${totalOcVAT + soi.vatAmount}"/>
							<c:set var="totalOcAmount" value="${totalOcAmount + soi.amount}"/>
							<c:set var="totalDiscount" value="${totalDiscount + soi.discount}"/>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="10" style="font-weight:bold;">Total</td>
							<td colspan="2" class="monetary">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalOcAmount}"/>
							</td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty salesOrder.referenceDocuments}">
			<fieldset class="frmField_set">
				<legend>Document/s</legend>
				<table class="dataTable" id="referenceDocuments">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm">#</th>
							<th width="18%" class="th-td-norm">Name</th>
							<th width="18%" class="th-td-norm">Description</th>
							<th width="18%" class="th-td-norm">file</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesOrder.referenceDocuments}" var="refDoc" varStatus="status">
							<tr> 
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
								<td class="th-td-norm v-align-top">${refDoc.description}</td>
								<td class="th-td-norm v-align-top" id="file">
									<a href="${refDoc.file}" download="${refDoc.fileName}" class="fileLink" id="file">${refDoc.fileName}</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
		</c:if>
		<br>
		<fieldset class="frmField_set" style="border: 0;">
			<table>
				<tr>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Sub Total</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalItemAmount + totalOcAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total VAT</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalItemVAT + totalOcVAT}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Withholding VAT</td>
					<td class="footerViewCls" colspan="2">
						(<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${salesOrder.wtVatAmount}"/>)
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Withholding Tax</td>
					<td class="footerViewCls">${salesOrder.wtAcctSetting.name}</td>
					<td class="footerViewCls">
						(<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${salesOrder.wtAmount}"/>)
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total Amount Due</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${(totalItemAmount + totalOcAmount +totalItemVAT + totalOcVAT)-salesOrder.wtVatAmount-salesOrder.wtAmount}"/>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</div>
</body>
</html>