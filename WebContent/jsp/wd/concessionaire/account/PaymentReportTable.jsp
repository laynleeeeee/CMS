<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!--

	Description:Displays the payment of Concessionaire in table format.
 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js"> </script>
<script type="text/javascript">

$(document).ready (function () {
 	$("#btnDeletePayment").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var targetUrl = baseUrl +"?pageNumber=" +pageNumber;
	goToPage ("paymentReportTable", targetUrl);
}

$(function () {
	$("#btnPrintPayment").click(function () {
		var targetUrl = contextPath + baseUrl + "/printPayment";
		window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
		"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	});
	
	$("#btnDeletePayment").click(function() {
		var cbs = document.getElementsByName("cbPaymentReport");
		var ids = null;
		var ctr = 0;
		for (var index =0; index < cbs.length; index++) {
			var cb = cbs[index];
			if (cb.checked == true){
				if (ids == null){
					ids = "" + cb.id;
				} else {
					ids = ids + ";" + cb.id;
				}
				ctr++;
			}
		}
		var confirmation = "Are you sure you want to delete " + ctr + " payment";
		if (ctr == 1)

			confirmation = confirmation + "?";
		if (ctr > 1)

			confirmation = confirmation + "s?";

		var confirmDelete = confirm(confirmation);
		if (confirmDelete == true) {
			var url = contextPath + baseUrl + "/deletePayment?paymentAcctIds="+ids;
			$("#paymentReportTable").load(url);
			$("html, body").animate({scrollTop: $("#paymentReportTable").offset().top}, 0050);
			searchConcessionaireAcct();
		} else {
			searchPaymentReport();
		}
	});

	$("#btnCancelPaymentReport").live("click", function() {
		$("#paymentReportTable").html("");
		searchPaymentReport();
	});
});

</script>
<title> Payment report table</title>
</head>
<body>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}"/>
<div>
<table class="dataTable">
	<thead>
		<tr>
			<th width= "1%"><input type="checkbox" id="checkAll" style="text-align:center;
				vertical-align: middle;" onclick="checkUncheckedAll
				('cbPaymentReport', this, 'btnDeletePayment')"></th>
			<th width ="10%">
				Date/Payment Date
			</th>
			<th width ="15%">
				Water Bill Number
			</th>
			<th width ="50%">
				Item/Description
			</th>
			<th width ="15%">
				Amount
			</th>
			<th>Payment</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="totalAmount" value="0"></c:set>
		<c:set var="totalPayment" value="0"></c:set>
		<c:forEach var="payment" items="${paymentReports.data}" varStatus="status">
			<c:forEach var="paidAccount" items="${payment.paidAccounts}">
				<c:set var="account" value="${paidAccount.account}"></c:set>
				<c:if test="${paidAccount.paidMeteredSale > 0}">
					<tr>
						<td>
						<input type="checkbox" id="${payment.id}" name="cbPaymentReport"
							onclick="enableDisableButton ('cbPaymentReport','btnDeletePayment');" />
						</td>
						<td>
							<fmt:formatDate pattern="MM/dd/yyyy" value="${account.date}"/>
						</td>
						<td>
							${account.wbNumber}
						</td>
						<td>
							${account.cubicMeter} cubic meters
						</td>
						<td class="numberClass">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${paidAccount.paidMeteredSale}" />
							<c:set var="totalAmount" value="${totalAmount+ paidAccount.paidMeteredSale}"></c:set>
						</td>
						<td></td>
					</tr>
				</c:if>
				<c:if test="${paidAccount.paidRental > 0}">
					<tr>
						<td colspan="3"></td>
						<td>
							Meter Rental
						</td>
						<td class="numberClass">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${paidAccount.paidRental}" />
							<c:set var="totalAmount" value="${totalAmount+ paidAccount.paidRental}"></c:set>
						</td>
						<td></td>
					</tr>
				</c:if>
				<c:if test="${paidAccount.paidPenalty > 0}">
					<tr>
						<td colspan="3"></td>
						<td>
							Penalty (Due date: <fmt:formatDate pattern="MM/dd/yyyy" value="${account.dueDate}"/>)
						</td>
						<td class="numberClass">
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${paidAccount.paidPenalty}" />
							<c:set var="totalAmount" value="${totalAmount + paidAccount.paidPenalty}"></c:set>
						</td>
						<td></td>
					</tr>
				</c:if>
			</c:forEach>
			<!-- Payment data -->
			<tr>
				<td></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${payment.date}"/></td>
				<td></td>
				<td>Payment</td>
				<td></td>
				<td class="numberClass" ><fmt:formatNumber type="number" minFractionDigits="2" 
 					maxFractionDigits="2" value="${payment.payment}" />
 					<c:set var="totalPayment" value="${totalPayment + payment.payment }"></c:set>
 					</td>
 					
			</tr>
		</c:forEach>
			<tr class="summaryTr">
				<td colspan="4">Total</td>
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" 
 					maxFractionDigits="2" value="${totalAmount}" /></td>
 				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" 
 					maxFractionDigits="2" value="${totalPayment}" /></td>
			</tr>
	</tbody>
	<tfoot>
		<tr>
				<td colspan="5">${paymentReports.dataSize +
					((paymentReports.currentPage - 1) *
					paymentReports.pageSetting.maxResult)}/${paymentReports.totalRecords}
				</td>
					<td style="text-align: right;"><c:if
						test="${paymentReports.lastPage <= 5}">
						<c:forEach var="page" begin="1"
							end="${paymentReports.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${paymentReports.lastPage > 5}">
						<c:if test="${paymentReports.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${paymentReports.currentPage > 5}">
							<c:set var="modPage"
								value="${paymentReports.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(paymentReports.currentPage + (5 - modPage)) <= paymentReports.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${paymentReports.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(paymentReports.currentPage + (5 - modPage)) >= paymentReports.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${paymentReports.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet"
										value="${paymentReports.currentPage}" />
									<c:set var="minPageSet"
										value="${paymentReports.currentPage - 4}" />
								</c:otherwise>
							</c:choose>

							<c:if test="${minPageSet > 5}">
								<a href="#" onclick="loadPage (${minPageSet - 1})"
									class="pageNumber"> << </a>
							</c:if>

							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < paymentReports.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
				</c:if></td>
			</tr>
		</tfoot>
</table>
</div>
<br>
<div id="controlls" align="right">
	<input type="button" id="btnDeletePayment" value="Delete"></input>
    <input type="button" id="btnPrintPayment" value="Print"></input>
</div>
<br>
</body>
</html>