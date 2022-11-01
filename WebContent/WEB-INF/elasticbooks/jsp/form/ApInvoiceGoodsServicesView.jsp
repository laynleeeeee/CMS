<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

<!-- Description : AP invoice goods/services form view jsp page -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${apInvoiceGs.ebObjectId}");
	if ("${apInvoiceGs.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	var supplierAcctId = "${apInvoiceGs.supplierAccountId}";
	var warehouseId = "${apInvoiceGs.warehouseId}";
	$(".prevUC").each (function (i, e) {
		var $prevUC = $(this);
		var itemId = $(this).closest("tr").find(".hdnItemId").val();
		var isSerial = $(this).closest("tr").find(".serialNumber").val() != undefined;
		var uri = contextPath + "/retailReceivingReport/getLatestUc?itemId="+itemId
				+"&supplierAcctId="+supplierAcctId+"&warehouseId="+warehouseId+"&isSerial="+isSerial;
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
.disabled-link {
	pointer-events: none;
	cursor: default;
}

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
	<div class="modFormLabel">AP Invoice Goods/Services - ${apInvoiceGs.division.name}</div>
	<br>
	<fieldset class="frmField_set">
		<legend>System Generated</legend>
		<table>
			<tr>
				<td class="label">API-G/S No.</td>
				<td class="label-value">${apInvoiceGs.sequenceNumber}</td>
			</tr>
			<tr>
				<td class="label">Status</td>
				<td class="label-value">${apInvoiceGs.formWorkflow.currentFormStatus.description}</td>
			</tr>
		</table>
	</fieldset>
	<fieldset class="frmField_set">
		<legend>AP Invoice Header</legend>
		<table>
			<tr>
				<td class="label">Company</td>
				<td class="label-value">${apInvoiceGs.company.name}</td>
			</tr>
			<tr>
				<td class="label">Division</td>
				<td class="label-value">${apInvoiceGs.division.name}</td>
			</tr>
			<tr>
				<td class="label">RR Reference</td>
				<td class="label-value">${apInvoiceGs.rrNumber}</td>
			</tr>
			<tr>
				<td class="label">SI/SOA No.</td>
				<td class="label-value">${apInvoiceGs.invoiceNumber}</td>
			</tr>
			<tr>
				<td class="label">BMS No.</td>
				<td class="label-value">${apInvoiceGs.bmsNumber}</td>
			</tr>
			<tr>
				<td class="label">PO No.</td>
				<td class="label-value">${apInvoiceGs.poNumber}</td>
			</tr>
			<tr>
				<td class="label">Supplier</td>
				<td class="label-value">${apInvoiceGs.supplier.name}</td>
			</tr>
			<tr>
				<td class="label">Supplier Account</td>
				<td class="label-value">${apInvoiceGs.supplierAccount.name}</td>
			</tr>
			<tr>
				<td class="label">Term</td>
				<td class="label-value">${apInvoiceGs.term.name}</td>
			</tr>
			<tr>
				<td class="label">SI/SOA Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoiceGs.invoiceDate}"/></td>
			</tr>
			<tr>
				<td class="label">GL Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoiceGs.glDate}"/></td>
			</tr>
			<tr>
				<td class="label">Due Date</td>
				<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${apInvoiceGs.dueDate}"/></td>
			</tr>
			<tr>
				<td class="label">Description</td>
				<td class="label-value">${apInvoiceGs.description}</td>
			</tr>
			<tr>
				<td class="label">Currency</td>
				<td class="label-value">${apInvoiceGs.currency.name}</td>
			</tr>
			<tr>
				<td class="label">Amount</td>
				<td class="label-value"><fmt:formatNumber type='number' minFractionDigits='2'
					maxFractionDigits='2' value='${apInvoiceGs.amount}'/></td>
			</tr>
		</table>
	</fieldset>
	<c:if test="${fn:length(apInvoiceGs.serialItems) gt 0}">
		<fieldset class="frmField_set">
			<legend>Serialized Good/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="17%" class="th-td-norm">Stock Code</th>
						<th width="22%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="7%" class="th-td-norm">QTY</th>
						<th width="7%" class="th-td-norm">Serial Number</th>
						<th width="7%" class="th-td-norm">UOM</th>
						<th width="7%" class="th-td-norm">Previous<br>Unit Cost</th>
						<th width="7%" class="th-td-norm">Gross Price</th>
						<th width="5%" class="th-td-norm">Discount<br>Type</th>
						<th width="7%" class="th-td-norm">Discount<br>Value</th>
						<th width="7%" class="th-td-norm">Computed<br>Discount</th>
						<th width="8%" class="th-td-norm">Tax Type</th>
						<th width="7%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge">Amount</th>
					</tr>
					<tr>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="si" items="${apInvoiceGs.serialItems}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${si.item.stockCode}
								<input type="hidden" class="hdnItemId" value="${si.itemId}"/>
							</td>
							<td class="th-td-norm v-align-top">${si.item.description}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${si.existingStocks}" /></td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${si.quantity}" /></td>
							<td class="th-td-norm v-align-top">${si.serialNumber}
								<input type="hidden" class="serialNumber" value="${si.serialNumber}"/>
							</td>
							<td class="th-td-norm v-align-top">${si.item.unitMeasurement.name}</td>
							<td class="td-numeric v-align-top"><span class="prevUC"></span></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='6' value='${si.unitCost}'/></td>
							<td class="th-td-norm v-align-top">${si.itemDiscountType.name}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${si.discountValue}'/></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${si.discount}'/></td>
							<td class="th-td-norm v-align-top">${si.taxType.name}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='1' value='${si.vatAmount}'/></td>
							<td class="th-td-edge txt-align-right v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${si.amount}'/></td>
							<c:set var="siTotalVat" value="${siTotalVat + si.vatAmount}"/>
							<c:set var="siTotal" value="${siTotal + si.amount}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td>Total</td>
						<td colspan="14" align="right">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${siTotal}' />
						</td>
					 </tr>
				</tfoot>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${fn:length(apInvoiceGs.apInvoiceGoods) gt 0}">
		<fieldset class="frmField_set">
			<legend>Non-Serialized Good/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="1%" class="th-td-norm">#</th>
						<th width="17%" class="th-td-norm">Stock Code</th>
						<th width="22%" class="th-td-norm">Description</th>
						<th width="7%" class="th-td-norm">Existing<br>Stocks</th>
						<th width="7%" class="th-td-norm">QTY</th>
						<th width="7%" class="th-td-norm">UOM</th>
						<th width="7%" class="th-td-norm">Previous<br>Unit Cost</th>
						<th width="7%" class="th-td-norm">Gross Price</th>
						<th width="5%" class="th-td-norm">Discount<br>Type</th>
						<th width="7%" class="th-td-norm">Discount<br>Value</th>
						<th width="7%" class="th-td-norm">Computed<br>Discount</th>
						<th width="8%" class="th-td-norm">Tax Type</th>
						<th width="7%" class="th-td-norm">VAT Amount</th>
						<th width="10%" class="th-td-edge">Amount</th>
					</tr>
					<tr>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="apig" items="${apInvoiceGs.apInvoiceGoods}" varStatus="status">
						<tr>
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<td class="th-td-norm v-align-top">${apig.item.stockCode}
								<input type="hidden" class="hdnItemId" value="${apig.itemId}"/>
							</td>
							<td class="th-td-norm v-align-top">${apig.item.description}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${apig.existingStocks}" /></td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="6" value="${apig.quantity}" /></td>
							<td class="th-td-norm v-align-top">${apig.item.unitMeasurement.name}</td>
							<td class="td-numeric v-align-top"><span class="prevUC"></span></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='6' value='${apig.unitCost}'/></td>
							<td class="th-td-norm v-align-top">${apig.itemDiscountType.name}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${apig.discountValue}'/></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${apig.discount}'/></td>
							<td class="th-td-norm v-align-top">${apig.taxType.name}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${apig.vatAmount}'/></td>
							<td class="th-td-edge txt-align-right v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${apig.amount}'/></td>
							<c:set var="apigTotalVat" value="${apigTotalVat + apig.vatAmount}"/>
							<c:set var="apigTotal" value="${apigTotal + apig.amount}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td>Total</td>
						<td colspan="13" align="right">
							<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${apigTotal}' />
						</td>
					 </tr>
				</tfoot>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${fn:length(apInvoiceGs.apInvoiceLines) gt 0}">
		<fieldset class="frmField_set">
			<legend>Service/s</legend>
			<table class="dataTable">
				<thead>
					<tr>
						<th width="2%" class="th-td-norm">#</th>
						<th width="25%" class="th-td-norm">AP Line</th>
						<th width="10%" class="th-td-norm">Qty</th>
						<th width="10%" class="th-td-norm">UOM</th>
						<th width="10%" class="th-td-norm">Gross Price</th>
						<th width="7%" class="th-td-norm">Discount<br>Type</th>
						<th width="7%" class="th-td-norm">Discount<br>Value</th>
						<th width="7%" class="th-td-norm">Computed<br>Discount</th>
						<th width="20%" class="th-td-norm">Tax Type</th>
						<th width="10%" class="th-td-norm">VAT Amount</th>
						<th width="13%" class="th-td-edge">Amount</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${apInvoiceGs.apInvoiceLines}" var="apl" varStatus="status">
						<tr>
							<!-- Row number -->
							<td class="td-numeric v-align-top">${status.index + 1}</td>
							<!-- AR Line Setup -->
							<td class="th-td-norm v-align-top">${apl.apLineSetup.name}</td>
							<!-- Quantity -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="4" value="${apl.quantity}" />
							</td>
							<!-- UOM -->
							<td class="th-td-norm v-align-top">${apl.unitMeasurement.name}</td>
							<!-- Gross Price Amount -->
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="6" value="${apl.upAmount}"/>
							</td>
							<td class="th-td-norm v-align-top">${apl.itemDiscountType.name}</td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${apl.discountValue}'/></td>
							<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
								maxFractionDigits='2' value='${apl.discount}'/></td>
							<td class="th-td-norm v-align-top">${apl.taxType.name}</td>
							<td class="td-numeric v-align-top">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${apl.vatAmount}"/>
							</td>
							<!-- Amount -->
							<td class="td-numeric v-align-top" style="border-right: none;">
								<fmt:formatNumber type="number" minFractionDigits="2" 
									maxFractionDigits="2" value="${apl.amount}"/>
							</td>
							<c:set var="totalApLineVat" value="${totalApLineVat + apl.vatAmount}"/>
							<c:set var="otherCharges" value="${otherCharges + apl.amount}" />
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="9">Total</td>
						<td colspan="2" align="right"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${otherCharges}" /></td>
					</tr>
				</tfoot>
			</table>
		</fieldset>
	</c:if>
	<c:if test="${fn:length(apInvoiceGs.referenceDocuments) gt 0}">
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
					<c:forEach items="${apInvoiceGs.referenceDocuments}" var="refDoc" varStatus="status">
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
					<c:set var="totalNetOfVAT" value="${siTotal + apigTotal + otherCharges}"/>
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${totalNetOfVAT}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total VAT</td>
				<td class="footerViewCls" colspan="2">
					<c:set var="totalVAT" value="${siTotalVat + apigTotalVat + totalApLineVat}"/>
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${totalVAT}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Withholding Tax</td>
				<td class="footerViewCls">${apInvoiceGs.wtAcctSetting.name}</td>
				<td class="footerViewCls">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${apInvoiceGs.wtAmount}"/>
				</td>
			</tr>
			<tr>
				<td class="footerViewCls" colspan="3">Total Amount Due</td>
				<td class="footerViewCls" colspan="2">
					<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${(totalNetOfVAT + totalVAT) - apInvoiceGs.wtAmount}"/>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
</body>
</html>