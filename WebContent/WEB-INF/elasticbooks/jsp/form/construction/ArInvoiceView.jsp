<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description	: AR Invoice View
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${arInvoice.ebObjectId}");
	if("${arInvoice.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
<title>AR Invoice View</title>
</head>
<body>
<div class="formDivBigForms" id="divForm">
		<div class="modFormLabel">AR Invoice - Goods</div>
		<br>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table>
				<tr>
					<td class="label">ARI No.</td>
					<td class="label-value">${arInvoice.sequenceNo}</td>
				</tr>
				<tr>
					<td class="label">Status </td>
					<td class="label-value">${arInvoice.formWorkflow.currentFormStatus.description}</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>AR Invoice - Goods Header</legend>
			<table>
				<tr>
					<td class="label">DR-I Reference</td>
					<td class="label-value">${arInvoice.drNumber}</td>
				</tr>
				<tr>
					<td class="label">Company</td>
					<td class="label-value">${companyName}</td>
				</tr>
				<tr>
					<td class="label">Customer </td>
					<td class="label-value">${arInvoice.arCustomer.name}</td>
				</tr>
				<tr>
					<td class="label">Customer Account</td>
					<td class="label-value">${arInvoice.arCustomerAccount.name}</td>
				</tr>
				<tr>
					<td class="label">Term</td>
					<td class="label-value">${arInvoice.term.name}</td>
				</tr>
				<tr>
					<td class="label">Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arInvoice.date}"/></td>
				</tr>
				<tr>
					<td class="label">Due Date</td>
					<td class="label-value"><fmt:formatDate pattern="MM/dd/yyyy" value="${arInvoice.dueDate}"/></td>
				</tr>
				<tr>
					<td class="label">Ship To</td>
					<td class="label-value">${arInvoice.remarks}</td>
				</tr>
			</table>
		</fieldset>
		<c:if test="${fn:length(arInvoice.serialArItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Serialized Goods</legend>
					<table class="dataTable" id="serialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="16%" class="th-td-norm">Stock Code</th>
								<th width="16%" class="th-td-norm">Description</th>
								<th width="9%" class="th-td-norm">Warehouse</th>
								<th width="6%" class="th-td-norm">Serial Number</th>
								<th width="6%" class="th-td-norm">Available <br> Stocks</th>
								<th width="6%" class="th-td-norm">Qty</th>
								<th width="6%" class="th-td-norm">UOM</th>
								<th width="6%" class="th-td-norm">Gross Price</th>
								<th width="6%" class="th-td-norm">Discount</th>
								<th width="8%" class="th-td-norm">Tax Type</th>
								<th width="6%" class="th-td-norm">VAT Amount</th>
								<th width="6%" class="th-td-norm">Amount</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${arInvoice.serialArItems}" var="ariSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.warehouse.name}</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.serialNumber}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.item.unitMeasurement.name}</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariSerialItem.discount}"/>
									</td>
									<td class="th-td-norm v-align-top">${ariSerialItem.taxType.name}</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariSerialItem.vatAmount}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariSerialItem.amount}"/>
									</td>
								</tr>
								<c:set var="totalSerialQty" value="${totalSerialQty + ariSerialItem.quantity}"/>
								<c:set var="totalSerialAmount" value="${totalSerialAmount + ariSerialItem.amount}"/>
								<c:set var="totalSerialVat" value="${totalSerialVat + ariSerialItem.vatAmount}"/>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="6" align="left" style="font-weight:bold;">Total</td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalSerialQty}" />
								</td>
								<td colspan="5"></td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalSerialAmount}"/>
								</td>
							</tr>
						</tfoot>
					</table>
			</fieldset>
		</c:if>
		<c:if test="${fn:length(arInvoice.nonSerialArItems) gt 0}">
			<fieldset class="frmField_set">
				<legend>Non-Serialized Goods</legend>
					<table class="dataTable" id="nonSerialItems" style="width: 100%;">
						<thead>
							<tr>
								<th width="3%" class="th-td-norm" style="border-left: 1px solid black;">#</th>
								<th width="17%" class="th-td-norm">Stock Code</th>
								<th width="20%" class="th-td-norm">Description</th>
								<th width="10%" class="th-td-norm">Warehouse</th>
								<th width="7%" class="th-td-norm">Existing <br> Stocks</th>
								<th width="7%" class="th-td-norm">Qty</th>
								<th width="7%" class="th-td-norm">UOM</th>
								<th width="7%" class="th-td-norm">Gross Price</th>
								<th width="7%" class="th-td-norm">Discount</th>
								<th width="7%" class="th-td-norm">Tax Type</th>
								<th width="8%" class="th-td-norm">VAT Amount</th>
								<th width="8%" class="th-td-norm">Amount</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${arInvoice.nonSerialArItems}" var="ariNonSerialItem" varStatus="status">
								<tr style=" border-bottom: 1px solid black;"> 
									<td class="td-numeric v-align-top" style="border-bottom: 1px solid black; border-left: 1px solid black;">${status.index + 1}</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.item.stockCode}</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.item.description}</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.warehouse.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariNonSerialItem.existingStocks}" />
									</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${ariNonSerialItem.quantity}" />
									</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.item.unitMeasurement.name}</td>
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.srp}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.discount}"/>
									</td>
									<td class="th-td-norm v-align-top">${ariNonSerialItem.taxType.name}</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.vatAmount}"/>
									</td>
									<td class="td-numeric th-td-norm v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${ariNonSerialItem.amount}"/>
									</td>
								</tr>
								<c:set var="totalNonSerialQty" value="${totalNonSerialQty + ariNonSerialItem.quantity}"/>
								<c:set var="totalNonSerialGross" value="${totalNonSerialGross + ariNonSerialItem.srp}"/>
								<c:set var="totalNonSerialAmount" value="${totalNonSerialAmount + ariNonSerialItem.amount}"/>
								<c:set var="totalNonSerialVat" value="${totalNonSerialVat + ariNonSerialItem.vatAmount}"/>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="5" align="left" style="font-weight:bold;">Total</td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalNonSerialQty}" />
								</td>
								<td></td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalNonSerialGross}" />
								</td>
								<td colspan=3"></td>
								<td align="right">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${totalNonSerialAmount}"/>
								</td>
							</tr>
						</tfoot>
					</table>
			</fieldset>
		</c:if>
		<br>
		<c:set var="totalVat" value="${totalSerialVat + totalNonSerialVat}"/>
		<table class="frmField_set footerTotalAmountTbl">
			<tr>
				<td style="width: 22%;"></td>
				<td style="width: 22%;"></td>
				<td style="width: 22%; display: none;">Sub Total</td>
				<td style="width: 22%;"></td>
				<td style="width: 12%; display: none;"><span id="subTotal"></span></td>
			</tr>
			<tr>
				<td colspan="3">Total VAT</td>
				<td></td>
				<td>
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${totalVat}" />
				</td>
			</tr>
			<tr>
				<td colspan="3">Withholding Tax</td>
				<td>${arInvoice.wtAcctSetting.name}</td>
				<td colspan="7" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${arInvoice.wtAmount}" />
				</td>
			</tr>
			<tr>
				<td colspan="3">Total Amount Due</td>
				<td></td>
				<td>
					<fmt:formatNumber type="number" minFractionDigits="2" 
						maxFractionDigits="2" value="${arInvoice.amount}" />
				</td>
			</tr>
		</table>
		<br>
	</div>
</body>
</html>