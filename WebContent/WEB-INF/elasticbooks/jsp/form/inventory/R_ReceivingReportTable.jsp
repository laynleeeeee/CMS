<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Retail Receiving Report Table
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<table id="tblReceivingReport" class="dataTable">
	<thead>
		<tr>
			<th width="1%"></th>
			<th width="3%">#</th>
			<th width="10%">Stock Code</th>
			<th width="20%">Description</th>
			<th width="8%">QTY</th>
			<th width="8%">UOM</th>
			<th width="12%">Unit Cost</th>
			<th width="12%">SRP</th>
			<th width="15%" style="border-right: none;">Amount</th>
		</tr>
		<tr>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="rr" items="${receivingReport.rrItems}" varStatus="status">
		<tr>
			<!-- Delete Image -->
			<td class="tdProperties" align="left">
				<img id="imgDelete${status.index}" class='imgDelete' src='${pageContext.request.contextPath}/images/delete_active.png' onclick="deleteRow(this);"/>
				<input type="hidden" name='rrItems[${status.index}].id' value="${rr.id}">
			</td>
			<td class="tdProperties">${status.index + 1}</td>
			<!-- Stock Code -->
			<td class="tdProperties"><input id="stockCode${status.index}" onblur="validateStockCode(this);"
				onkeydown="loadItems(this);" class="txtStockCode" value="${rr.item.stockCode}"/><input name="rrItems[${status.index}].itemId"
					id="itemId${status.index}" value="${rr.item.id}" class="txtItemId" style="display: none;"/></td>
			<!-- Description -->
			<td class="tdProperties"><span id="description${status.index}" class="txtDescription">${rr.item.description}</span></td>
			<!-- Quantity: No. and Unit -->
			<td class="tdProperties"><input type="text" id="quantity${status.index}" name='rrItems[${status.index}].quantity' class="txtQuantity"
				onblur='computeAmount(this);' value="<fmt:formatNumber type='number' minFractionDigits='0' value='${rr.quantity}'/>" /></td>
			<td class="tdProperties"><span id="unit${status.index}" class="txtUnit">${rr.item.unitMeasurement.name}</span></td>
			<!-- Unit Cost -->
			<td class="tdProperties"><input type="text" id="unitCost${status.index}" name='rrItems[${status.index}].unitCost' class="txtUnitCost" 
						onblur='computeAmount(this);' value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${rr.unitCost}'/>" /></td>
			<!-- SRP -->
			<td class="tdProperties" style="text-align: right;"><input  name="rrItems[${status.index}].item.itemSrp" id="itemSrp${status.index}"
				class="txtItemSrp" value="<fmt:formatNumber type='number' minFractionDigits='2' maxFractionDigits='2' value='${rr.item.itemSrp}'/>"/></td>
			<!-- Amount -->
			<td><span class="spanAmount" style="float: right;"></span></td>
		</tr>
	</c:forEach>
	</tbody>
	<tfoot>
	<tr>
		<td colspan="9">
			<span style="font-weight: bold">Total</span> 
			<span id="spanTotalAmount" class="amount" style="float: right;">0.0</span>
		</td>
	</tr>
	</tfoot>
</table>
</body>
</html>