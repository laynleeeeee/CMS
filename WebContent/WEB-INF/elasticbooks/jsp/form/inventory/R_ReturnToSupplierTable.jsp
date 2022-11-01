<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Retail Return to Supplier Table
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
$(document).ready(function() {
	if("${company}" != "") {
		$("#companyId").append("<option selected='selected' value="+"${company.id}"+">"+"${company.name}"+"</option>");
		filterWarehouse ();
	}
	if("${supplier}" != "") {
		$("#aSupplierId").val("${supplier.id}");
		$("#supplierId").val("${supplier.name}");
		filterSupplierAccts();
	}
	$("#rrNumber").val("${rrNumber}");
	$("#supplierInvoiceNo").val("${supplierInvNo}");
	if(isReference){
		hideDelIcon();
		disableStockcode();
	}
});
</script>
</head>
<body>
<table id="tblReturnToSupplier" class="dataTable">
	<thead>
		<tr>
			<th width="1%"></th>
			<th width="2%">#</th>
			<th width="10%">Stock Code</th>
			<th width="20%">Description</th>
			<th width="8%">Existing Stocks</th>
			<th width="8%">QTY</th>
			<th width="8%" style="border-right: none;">UOM</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="rts" items="${rReturnToSupplier.rtsItems}" varStatus="status">
		<tr>
			<!-- Delete Image -->
			<td class="tdProperties" align="left">
				<img id="imgDelete${status.index}" class='imgDelete' src='${pageContext.request.contextPath}/images/delete_active.png' onclick="deleteRow(this);"/>
				<input name="rtsItems[${status.index}].rReceivingReportItemId" id="rrId${status.index}" value="${rts.rReceivingReportItemId}" class="txtrrId" style="display: none;"/>
			</td>
			<td class="tdProperties" align="right">
				<span id="spanRow${status.index}" class="spanRow">${status.index+1}</span></td>
			<!-- Stock Code -->
			<td class="tdProperties"><input id="stockCode${status.index}" onblur="validateStockCode(this);" onblur="validateStockCode(this);"
				onkeydown="loadItems(this);" class="txtStockCode" value="${rts.item.stockCode}"/><input name="rtsItems[${status.index}].itemId"
					id="itemId${status.index}" value="${rts.itemId}" class="txtItemId" style="display: none;"/></td>
			<!-- Description -->
			<td class="tdProperties"><span id="description${status.index}" class="txtDescription">${rts.item.description}</span></td>
			<td class="tdProperties" align="right">
				<span id="existingStocks${status.index}" class="spanExistingStocks">${rts.item.existingStocks}</span></td>
			<!-- Quantity: No. and Unit -->
			<td class="tdProperties"><input type="text" id="quantity${status.index}" name='rtsItems[${status.index}].quantity' class="txtQuantity"
				onkeydown="processNextRow(this, event);" value="<fmt:formatNumber type='number' minFractionDigits='0' value='${rts.quantity}'/>" /></td>
			<td><span id="unit${status.index}" class="txtUnit">${rts.item.unitMeasurement.name}</span></td>
		</tr>
	</c:forEach>
	</tbody>
</table>
</body>
</html>