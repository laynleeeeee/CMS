<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Profile Form
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<style>

#btnSaveFleetDriver, #btnCancelFleetDriver {
	font-weight: bold;
}

.tdFacLabel {
	font-size: 14px;
}
</style>
<script type="text/javascript">
var fpDivisionId = Number("${fpDivisionId}");
var url = contextPath + "/fleetAttributableCost";

function generateAttrCost(){
	genJO(1);
	genIC(1);
}

function genJO(pageNumber) {
	$("#divFacJobOrder").load(getJOCommonParam() + "&pageNumber=" + pageNumber);
}

function genIC(pageNumber) {
	$("#divFacItemsConsumed").load(getICCommonParam() + "&pageNumber=" + pageNumber);
}

function getJOCommonParam() {
	return url + "/jobOrders?" + getAttribCostCommonParam();
}

function getICCommonParam() {
	var stockCode = encodeURIComponent($("#txtStockCode").val());
	var itemDescription = encodeURIComponent($("#txtItemDescription").val());
	return url + "/itemsConsumed?"+getAttribCostCommonParam()+"&stockCode="+stockCode+"&itemDescription="+itemDescription;
}

function getAttribCostCommonParam() {
	var dateFrom = $("#txtDateFrom").val();
	var dateTo = $("#txtDateTo").val();
	return "divisionId="+fpDivisionId+"&dateFrom="+dateFrom+"&dateTo="+dateTo;
}

function printAttCost() {
	var fleetProfileId = Number("${fp.id}");
	var divisionId = $("#")
	var dateFrom = $("#txtDateFrom").val();
	var dateTo = $("#txtDateTo").val();
	var stockCode = $("#txtStockCode").val();
	var description = $("#txtItemDescription").val();
	var url = contextPath + "/fleetAttributableCost/print?fleetProfileId="+fleetProfileId
			+"&divisionId="+fpDivisionId+"&dateFrom="+dateFrom+"&dateTo="+dateTo+"&stockCode="+stockCode
			+"&description="+description;
	window.open(url, "_blank");
}
</script>
<title>Fleet Attributable</title>
</head>
<body>
	<br>
	<div style="width: 90%; margin: 0 auto;" >
		<div id="divFacHeader">
			<table>
				<tr>
					<td class="tdFacLabel">Date From</td>
					<td class="tdDateFilter">
						<input id="txtDateFrom" style="width: 120px;" class="dateClass2" onblur="evalDate('txtDateFrom')"/> 
						<img id="imgDateFrom"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('txtDateFrom')"
							style="cursor: pointer" style="float: right;" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td class="tdFacLabel">Date To</td>
					<td class="tdDateFilter">
						<input id="txtDateTo" style="width: 120px;" class="dateClass2" onblur="evalDate('txtDateTo')"/> 
						<img id="imgDateTo"
							src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('txtDateTo')"
							style="cursor: pointer" style="float: right;" />
					</td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td>
						<input type="button" id="btnGenerateAttrCost" value="Generate" onclick="generateAttrCost();">
					</td>
				</tr>
			</table>
		</div>
		<br />
		<fieldset>
			<legend style="font-weight: bold;">Job Order Costs</legend>
			<div id="divFacJobOrder" >
				<%@ include file="FleetACJobOrder.jsp"%>
			</div>
		</fieldset>
		<br />
		<div id="divFacItemSearch">
			<table>
				<tr>
					<td class="tdFacLabel">Stock Code</td>
					<td><input id="txtStockCode" /></td>
				</tr>
				<tr>
					<td class="tdFacLabel">Item Description</td>
					<td><input id="txtItemDescription" /></td>
						<td>
						<input type="button" id="btnGenerateAttrItems" value="Search" onclick="genIC(1);">
					</td>
				</tr>
			</table>
		</div>
		<br />
		<fieldset>
			<legend style="font-weight: bold;">Items Consumed</legend>
			<div id="divFacItemsConsumed" >
				<%@ include file="FleetACItemsConsumed.jsp"%>
			</div>
		</fieldset>
		<div>
			<table style="width: 100%; margin: auto;">
				<tr><td ><input type="button" id="btnSaveReceivingReport" value="Print" onclick="printAttCost();" style="float: right;"/> </td></tr>
			</table>
		</div>
	</div>
</body>
</html>