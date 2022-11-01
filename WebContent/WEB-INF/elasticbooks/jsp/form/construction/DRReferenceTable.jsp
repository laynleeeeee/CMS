<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description	: Authority to withdraw reference table form for delivery receipt form
-->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblDRReference thead tr").find("th:last").css("border-right", "none");
	$("#tblDRReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblDRReference tbody").find("td").addClass("tdProperties");
	markCurrentPageNumber($("#hdnPageNumber").val());

	$("#tblDRReference tbody tr").find(".cbDr").each(function(i) {
		var drId = Number($(this).attr("id").split("dr-")[1]);
		var isUndefined = typeof slctdDrRefIds[drId] == "undefined";
		console.log(!isUndefined);
		console.log(typeof slctdDrRefIds[drId] != "undefined");
		if (!isUndefined) {
			$(this).prop("checked", true);
		}
	});
});

function loadPage(pageNumber) {
	$("#divDRRefTable").load(getCommonParam()+"&pageNumber="+pageNumber);
}

function populateDrRefId($checkbox, vals) {
	var valArr = vals.split("-");
	var drId = parseInt(valArr[0]);
	var hasDiff = false;
	if ($($checkbox).is(":checked")) {
		var dr = new DRReference(parseInt(valArr[1]), parseInt(valArr[2]), parseInt(valArr[3]), parseInt(valArr[4]), parseInt(valArr[5]));
		for (var i in slctdDrRefIds) {
			var obj = slctdDrRefIds[i];
			if (obj.companyId != dr.companyId || obj.arCustomerId != dr.arCustomerId || obj.arCustomerAccountId != dr.arCustomerAccountId
					|| obj.wtAcctSettingId != dr.wtAcctSettingId || obj.currencyId != dr.currencyId) {
				hasDiff = true;
				break;
			}
		}
		if (hasDiff) {
			alert("Selected DR reference/s must have the same company, customer, customer account, currency, and withholding tax percentage.");
			$($checkbox).prop("checked", false);
		} else {
			if (typeof slctdDrRefIds[drId] == "undefined") {
				slctdDrRefIds[drId] = dr;
			}
		}
	} else {
		delete slctdDrRefIds[drId];
	}
}

function DRReference(companyId, arCustomerId, arCustomerAccountId, wtAcctSettingId, currencyId) {
	this.companyId = companyId;
	this.arCustomerId = arCustomerId;
	this.arCustomerAccountId = arCustomerAccountId;
	this.wtAcctSettingId = wtAcctSettingId;
	this.currencyId = currencyId;
}
</script>
<style type="text/css">
html {
	overflow-x: hidden;
	overflow-y: auto;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}"/>
<table style="margin-top: 20px;" id="tblDRReference" class="dataTable">
	<thead>
		<tr>
			<th width="5%">#</th>
			<th width="10%">Date</th>
			<th width="10%">DR No.</th>
			<th width="10%">DR Reference No.</th>
			<th width="15%">Customer</th>
			<th width="15%">Customer Account</th>
			<th width="25%">Ship To</th>
			<th width="25%">Currency</th>
			<th width="10%" style="border-right: none;">WTax Percentage</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="dr" items="${deliveryReceipts.data}">
			<tr>
				<td style="border-left: none;">
					<input type="checkbox"  id="dr-${dr.id}" name="cbDr" class="cbDr"
						onclick="populateDrRefId(this, '${dr.id}-${dr.companyId}-${dr.arCustomerId}-${dr.arCustomerAccountId}-${dr.wtAcctSettingId}-${dr.currencyId}');"/>
				</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${dr.date}"/></td>
				<td>${dr.sequenceNo}</td>
				<td>${dr.drRefNumber}</td>
				<td>${dr.customerName}</td>
				<td>${dr.customerAcctName}</td>
				<td>${dr.remarks}</td>
				<td>${dr.currency.name}</td>
				<td>${dr.wtAcctPercentage}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${deliveryReceipts.dataSize + ((deliveryReceipts.currentPage - 1)
				* deliveryReceipts.pageSetting.maxResult)}/${deliveryReceipts.totalRecords}
			</td>
			<td colspan="4" style="text-align: right;">
			<c:if test="${deliveryReceipts.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${deliveryReceipts.lastPage }">
					<a href="#container" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${deliveryReceipts.lastPage > 5}">
				<c:if test="${deliveryReceipts.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#container" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${deliveryReceipts.currentPage > 5}">
					<c:set var="modPage" value="${deliveryReceipts.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(deliveryReceipts.currentPage + (5 - modPage)) <= deliveryReceipts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${deliveryReceipts.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(deliveryReceipts.currentPage + (5 - modPage)) >= deliveryReceipts.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${deliveryReceipts.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${deliveryReceipts.currentPage}" />
							<c:set var="minPageSet" value="${deliveryReceipts.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#container" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#container" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < deliveryReceipts.lastPage }">
						<a href="#container" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
		</tr>
	</tfoot>
</table>
</body>
</html>