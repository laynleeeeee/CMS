<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Sales quotation view form jsp page
-->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${salesQuotation.ebObjectId}");
	if ("${salesQuotation.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
	loadGeneralConditions("${salesQuotation.generalConditions}");
});

function loadGeneralConditions(genCon) {
	var splitGenCon = genCon.split(';');
	for (var i = 0; i < splitGenCon.length; i++) {
		if (splitGenCon[i] == "") {
			continue;
		}
		$('#genConditionViewTbl').append('<tr><td>'+splitGenCon[i]+'</td></tr>');
	}
}
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
	<div class="modFormLabel">Sales Quotation</div>
	<br>
	<div class="modForm">
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table class="formTable">
				<tr>
					<td class="label">Sequence Number</td>
					<td class="label-value">${salesQuotation.sequenceNumber}</td>
				</tr>
				<tr>
					<td class="label">Status</td>
					<td class="label-value">${salesQuotation.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>Sales Quotation Header</legend>
			<table class="formTable" border="0">
				<tr>
					<td class="label">Company </td>
					<td class="label-value">${salesQuotation.company.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${salesQuotation.date}"/>
					</td>
				</tr>
				<tr>
					<td class="label">Customer</td>
					<td class="label-value">${salesQuotation.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${salesQuotation.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Cluster</td>
					<td class="label-value">${salesQuotation.customerType.name}</td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${salesQuotation.shipTo}</td>
				</tr>
				<tr>
					<td class="label">Subject</td>
					<td class="label-value">${salesQuotation.subject}</td>
				</tr>
				<tr>
					<td class="label td-txt-align-top">General Conditions</td>
					<td class="label-value">
						<table id="genConditionViewTbl" class="dataTable"></table>
					</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${not empty salesQuotation.sqItems}">
			<fieldset class="frmField_set">
				<legend>Goods Table</legend>
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
							<th width="12%" class="th-td-edge">Memo</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesQuotation.sqItems}" var="sqi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${sqi.item.stockCode}</td>
								<td class="th-td-norm v-align-top">${sqi.item.description}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.item.unitMeasurement.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.grossAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.itemDiscountType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discountValue}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.taxType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.vatAmount}"/>
								</td>
								<td class="th-td-norm v-align-top monetary" style="">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.amount}"/>
								</td>
								<td class="th-td-edge v-align-top">${sqi.memo}</td>
							</tr>
							<c:set var="totalItemVAT" value="${totalItemVAT + sqi.vatAmount}"/>
							<c:set var="totalItemAmount" value="${totalItemAmount + sqi.amount}"/>
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
		<c:if test="${not empty salesQuotation.sqLines}">
			<fieldset class="frmField_set">
				<legend>Service Table</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="2%" class="th-td-norm">#</th>
							<th width="16%" class="th-td-norm">Service</th>
							<th width="10%" class="th-td-norm">Qty</th>
							<th width="8%" class="th-td-norm">UOM</th>
							<th width="10%" class="th-td-norm">Gross Price</th>
							<th width="10%" class="th-td-norm">Discount<br>Type</th>
							<th width="8%" class="th-td-norm">Discount<br>Value</th>
							<th width="8%" class="th-td-norm">Computed<br>Discount</th>
							<th width="10%" class="th-td-norm">Tax Type</th>
							<th width="8%" class="th-td-norm">VAT Amount</th>
							<th width="10%" class="th-td-edge">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesQuotation.sqLines}" var="sqi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${sqi.arLineSetupName}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.unitMeasurement.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.upAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.itemDiscountType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discountValue}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.taxType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.vatAmount}"/>
								</td>
								<td class="th-td-edge v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.amount}"/>
								</td>
							</tr>
							<c:set var="totalOcVAT" value="${totalOcVAT + sqi.vatAmount}"/>
							<c:set var="totalOcAmount" value="${totalOcAmount + sqi.amount}"/>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="9" style="font-weight:bold;">Total</td>
							<td colspan="2" class="monetary">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalOcAmount}"/>
							</td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty salesQuotation.sqtLines}">
			<fieldset class="frmField_set">
				<legend>Trucking Service Table</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="2%" class="th-td-norm">#</th>
							<th width="16%" class="th-td-norm">Service</th>
							<th width="10%" class="th-td-norm">Qty</th>
							<th width="8%" class="th-td-norm">UOM</th>
							<th width="10%" class="th-td-norm">Gross Price</th>
							<th width="10%" class="th-td-norm">Discount<br>Type</th>
							<th width="8%" class="th-td-norm">Discount<br>Value</th>
							<th width="8%" class="th-td-norm">Computed<br>Discount</th>
							<th width="10%" class="th-td-norm">Tax Type</th>
							<th width="8%" class="th-td-norm">VAT Amount</th>
							<th width="10%" class="th-td-edge">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesQuotation.sqtLines}" var="sqi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${sqi.arLineSetupName}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.unitMeasurement.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.upAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.itemDiscountType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discountValue}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.taxType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.vatAmount}"/>
								</td>
								<td class="th-td-edge v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.amount}"/>
								</td>
							</tr>
							<c:set var="totalTruckVAT" value="${totalTruckVAT + sqi.vatAmount}"/>
							<c:set var="totalTruckAmount" value="${totalTruckAmount + sqi.amount}"/>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="9" style="font-weight:bold;">Total</td>
							<td colspan="2" class="monetary">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalTruckAmount}"/>
							</td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<c:if test="${not empty salesQuotation.sqeLines}">
			<fieldset class="frmField_set">
				<legend>Equipment Service Table</legend>
				<table class="dataTable">
					<thead>
						<tr>
							<th width="2%" class="th-td-norm">#</th>
							<th width="16%" class="th-td-norm">Service</th>
							<th width="10%" class="th-td-norm">Qty</th>
							<th width="8%" class="th-td-norm">UOM</th>
							<th width="10%" class="th-td-norm">Gross Price</th>
							<th width="10%" class="th-td-norm">Discount<br>Type</th>
							<th width="8%" class="th-td-norm">Discount<br>Value</th>
							<th width="8%" class="th-td-norm">Computed<br>Discount</th>
							<th width="10%" class="th-td-norm">Tax Type</th>
							<th width="8%" class="th-td-norm">VAT Amount</th>
							<th width="10%" class="th-td-edge">Amount</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${salesQuotation.sqeLines}" var="sqi" varStatus="status">
							<tr>
								<td class="td-numeric v-align-top">${status.index + 1}</td>
								<td class="th-td-norm v-align-top">${sqi.arLineSetupName}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.quantity}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.unitMeasurement.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.upAmount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.itemDiscountType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discountValue}"/>
								</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.discount}"/>
								</td>
								<td class="th-td-norm v-align-top">${sqi.taxType.name}</td>
								<td class="th-td-norm v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.vatAmount}"/>
								</td>
								<td class="th-td-edge v-align-top monetary">
									<fmt:formatNumber type="number" minFractionDigits="2"
										maxFractionDigits="2" value="${sqi.amount}"/>
								</td>
							</tr>
							<c:set var="totalEquipVAT" value="${totalEquipVAT + sqi.vatAmount}"/>
							<c:set var="totalEquipAmount" value="${totalEquipAmount + sqi.amount}"/>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="9" style="font-weight:bold;">Total</td>
							<td colspan="2" class="monetary">
								<fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalEquipAmount}"/>
							</td>
						</tr>
					</tfoot>
				</table>
			</fieldset>
		</c:if>
		<fieldset class="frmField_set" style="border: 0;">
			<table>
				<tr>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
					<td class="footerViewCls"></td>
				</tr>
				<tr style="display: none;">
					<td class="footerViewCls" colspan="3">Sub Total</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalItemAmount + totalOcAmount + totalTruckAmount + totalEquipAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total VAT</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${totalItemVAT + totalOcVAT + totalTruckVAT + totalEquipVAT}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Withholding Tax</td>
					<td class="footerViewCls">${arTransaction.wtAcctSetting.name}</td>
					<td class="footerViewCls">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${salesQuotation.wtAmount}"/>
					</td>
				</tr>
				<tr>
					<td class="footerViewCls" colspan="3">Total Amount Due</td>
					<td class="footerViewCls" colspan="2">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
							value="${(totalItemAmount + totalOcAmount + totalTruckAmount + totalEquipAmount) - salesQuotation.wtAmount}"/>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</div>
</body>
</html>