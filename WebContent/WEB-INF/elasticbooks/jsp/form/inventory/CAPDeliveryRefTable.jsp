<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Shows the list of Customer Advance Payment in table format.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	$("#capDeliveryRefDiv").load(getCommonParams()+"&pageNumber=" + pageNumber);
}

function getReferenceId(checkedElem) {
	var checkedLength = $("input[type=checkbox]:checked").length;
	if($("input[type=checkbox]:checked").length == 0) {
		currentReference = null;
	} else {
		if(checkedLength > 1)
			$(currentReference).prop("checked", false);
		currentReference = $(checkedElem);
	}
}

</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="capDeliveryRefTable" class="dataTable">
		<thead>
			<tr>
				<th width="2%"></th>
				<th width="8%">CAP<c:if test="${typeId == 3}"> - IS</c:if> No.</th>
				<th width="10%">Date</th>
				<th width="20%">Invoice No.</th>
				<th width="30%">Customer</th>
				<th width="30%" style="border-right: none;">Customer Account</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="payment" items="${advancePayments.data}">
				<tr>
					<td><input id="${payment.id}" name="cbCAP" class="cbCAP" type="checkbox" onclick="getReferenceId(this);"/></td>
					<td>${payment.formattedCSNumber}</td>
					<td><fmt:formatDate pattern="MM/dd/yyyy" value="${payment.receiptDate}"/></td>
					<td>${payment.salesInvoiceNo}</td>
					<td>${payment.arCustomer.name}</td>
					<td>${payment.arCustomerAccount.name}</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">${advancePayments.dataSize + ((advancePayments.currentPage - 1) *
					advancePayments.pageSetting.maxResult)}/${advancePayments.totalRecords}
				</td><td colspan="4" style="text-align: right;">
				<c:if test="${advancePayments.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${advancePayments.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${advancePayments.lastPage > 5}">
					<c:if test="${advancePayments.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${advancePayments.currentPage > 5}">
						<c:set var="modPage" value="${advancePayments.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(advancePayments.currentPage + (5 - modPage)) <= advancePayments.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${advancePayments.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(advancePayments.currentPage + (5 - modPage)) >= advancePayments.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${advancePayments.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${advancePayments.currentPage}" />
								<c:set var="minPageSet" value="${advancePayments.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < advancePayments.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>