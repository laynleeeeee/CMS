<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description: Purchase order view form JSP page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${rPurchaseOrder.ebObjectId}");
	if ("${rPurchaseOrder.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	var supplierAcctId = "${rPurchaseOrder.supplierAccountId}";
	$(".prevUC").each (function (i, e) {
		var $prevUC = $(this);
		var itemId = $(this).closest("tr").find(".hdnItemId").val();
		var uri = contextPath + "/retailPurchaseOrder/getLatestUC?itemId="+itemId
				+"&supplierAcctId="+supplierAcctId;
		$.ajax({
			url: uri,
			success : function(unitCost) {
				$prevUC.text(formatDecimalPlaces(unitCost));
			}
		});
	});
});
</script>
<style type="text/css">
.footerViewCls {
	font-size: 12;
	font-weight: bold;
	text-align: right;
	width: 300px;
}
</style>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Purchase Order - ${rPurchaseOrder.division.name}</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">PO No. </td>
					<td class="label-value">${rPurchaseOrder.poNumber}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${rPurchaseOrder.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Purchase Order Header</legend>
			<table>
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${rPurchaseOrder.company.name}</td>
				</tr>
				<tr>
					<td class="label">Division</td>
					<td class="label-value">${rPurchaseOrder.division.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${rPurchaseOrder.poDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Estimated Delivery Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${rPurchaseOrder.estDeliveryDate}"/>
					</td>
				</tr>
				<tr>
					<td class="label">BMS No.</td>
					<td class="label-value">${rPurchaseOrder.bmsNumber}</td>
				</tr>
				<tr>
					<td class="label">Supplier</td>
					<td class="label-value">${rPurchaseOrder.supplier.name}</td>
				</tr>
				<tr>
					<td class="label">Supplier Account</td>
					<td class="label-value">${rPurchaseOrder.supplierAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Contact Person</td>
					<td class="label-value">${rPurchaseOrder.requesterName}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${rPurchaseOrder.term.name}</td>
				</tr>
				<tr>
					<td class="label">Remarks</td>
					<td class="label-value">${rPurchaseOrder.remarks}</td>
				</tr>
				<tr>
					<td class="label">Currency</td>
					<td class="label-value">${rPurchaseOrder.currency.name}</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${fn:length(rPurchaseOrder.rPoItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Good/s</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="1%" class="th-td-norm">#</th>
							<th width="12%" class="th-td-norm">Stock Code</th>
							<th width="20%" class="th-td-norm">Description</th>
							<th width="15%" class="th-td-norm">Qty</th>
							<th width="10%" class="th-td-norm">UOM</th>
							<th width="8%" class="th-td-norm">Previous<br>Unit Cost<br>(PHP)</th>
							<th width="8%" class="th-td-norm">Gross Price</th>
							<th width="8%" class="th-td-norm">Tax Type</th>
							<th width="7%" class="th-td-norm">VAT Amount</th>
							<th width="15%" class="th-td-edge">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="poi" items="${rPurchaseOrder.rPoItems}" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">
									<input type="hidden" class="hdnItemId" value="${poi.itemId}">
									${poi.item.stockCode}
								</td>
								<td class="th-td-norm v-align-top">${poi.item.description}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='4' value="${poi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${poi.item.unitMeasurement.name}</td>
								<td class="td-numeric v-align-top"><span class="prevUC"></span></td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='6' value="${poi.unitCost}"/>
								</td>
								<td class="th-td-norm v-align-top">${poi.taxType.name}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value='${poi.vatAmount}'/>
								</td>
								<!-- Hidden rounded off vat and amount values. This is to avoid incorrect computation. -->
								<td style="display:none;">
									<!-- VAT -->
									<fmt:formatNumber var="hdnVat" pattern="#" type='number' minFractionDigits='2' maxFractionDigits='2' value="${poi.vatAmount}"/>
									<c:set var="totalItemVAT" value="${totalItemVAT + hdnVat}"/>
									<!-- AMOUNT -->
									<c:set var="amount" value="${Math.round(((poi.quantity * poi.unitCost)+ Number.EPSILON) * 100) / 100 - poi.vatAmount}"/>
									<fmt:formatNumber var="hdnAmount" pattern="#" type='number' minFractionDigits='2' maxFractionDigits='2' value="${amount}"/>
									<c:set var="totalItem" value="${totalItem + hdnAmount}" />
								</td>
								<td class="th-td-edge txt-align-right v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value="${amount}"/>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
					<tr>
						<td colspan="8">Total</td>
						<td colspan="2" class="txt-align-right">
							<fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='4' value='${totalItem}'/>
						</td>
					 </tr>
				</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(rPurchaseOrder.poLines) gt 0}">
			<fieldset class="frmField_set">
				<legend>Service/s</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="2%" class="th-td-norm">#</th>
							<th width="25%" class="th-td-norm">AP Line</th>
							<th width="15%" class="th-td-norm">Qty</th>
							<th width="15%" class="th-td-norm">UOM</th>
							<th width="15%" class="th-td-norm">UP</th>
							<th width="8%" class="th-td-norm">Tax Type</th>
							<th width="7%" class="th-td-norm">VAT Amount</th>
							<th width="20%" class="th-td-edge">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="pol" items="${rPurchaseOrder.poLines}" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${pol.apLineSetup.name}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="4" value="${pol.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${pol.unitMeasurement.name}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="6" value="${pol.upAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${pol.taxType.name}</td>
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type='number' minFractionDigits='2'
										maxFractionDigits='2' value='${pol.vatAmount}'/>
								</td>
								<!-- Hidden rounded off vat and amount values. This is to avoid incorrect computation. -->
								<td style="display:none;">
									<!-- VAT -->
									<fmt:formatNumber var="hdnLineVat" pattern="#" type='number' minFractionDigits='2' maxFractionDigits='2' value="${pol.vatAmount}"/>
									<c:set var="totalLineVAT" value="${totalLineVAT + hdnLineVat}"/>
									<!-- AMOUNT -->
									<fmt:formatNumber var="hdnAmount" pattern="#" type='number' minFractionDigits='2' maxFractionDigits='2' value="${pol.amount}"/>
									<c:set var="totalLine" value="${totalLine + hdnAmount}"/>
								</td>
								<td class="td-numeric v-align-top" style="border-right: none;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${pol.amount}"/>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="6">Total</td>
							<td colspan="2" align="right">
								<fmt:formatNumber type="number" minFractionDigits="2"
									maxFractionDigits="2" value="${totalLine}"/>
							</td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(rPurchaseOrder.referenceDocuments) gt 0}">
			<fieldset class="frmField_set">
				<legend>Documents</legend>
				<table class="dataTable" id="referenceDocuments">
					<thead>
						<tr>
							<th width="3%" class="th-td-norm">#</th>
							<th width="18%" class="th-td-norm">Name</th>
							<th width="18%" class="th-td-norm">Description</th>
							<th width="18%" class="th-td-edge">file</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${rPurchaseOrder.referenceDocuments}" var="refDoc" varStatus="status">
							<tr> 
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${refDoc.fileName}</td>
								<td class="th-td-norm v-align-top">${refDoc.description}</td>
								<td class="th-td-edge v-align-top" id="file">
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
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${totalItem + totalLine}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total VAT</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${totalItemVAT + totalLineVAT}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total Amount Due</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalItem + totalLine + totalItemVAT + totalLineVAT}"/>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>