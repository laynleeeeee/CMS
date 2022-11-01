<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!-- 

	Description: Receivable report table for water district.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/receivableReportWd?pageNumber="+pageNumber;
	goToPage ("receivableReportTableWd", targetUrl);
}
</script>
<body>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}"/>
<table class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="18%">Name</th>
			<th width="18%">Address</th>
			<th width="10%">Contact Number</th>
			<th width="8%">Date</th>
			<th width="8%">Due Date</th>
			<th width="9%">Metered Sale</th>
			<th width="9%">Penalty</th>
			<th width="9%">Meter Rental</th>
			<th width="9%">Total Amount</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="rr" items="${receivableReportWds.data}" varStatus="status">
			<tr>
				<td>${status.index + 1}</td>
				<td>${rr.concessionaire.firstName} ${rr.concessionaire.lastName}</td>
				<td>${rr.concessionaire.address}</td>
				<td>${rr.concessionaire.contactNumber}</td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${rr.date}"/></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${rr.dueDate}"/></td>
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
				value="${rr.unpaidAmount}" /></td>
				<c:set var="totalMeteredSale" value="${totalMeteredSale + rr.unpaidAmount}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
				value="${rr.penalty.unpaidAmount}"/></td>
				<c:set var="totalPenalty" value="${totalPenalty + rr.penalty.unpaidAmount}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
				value="${rr.rental.unpaidAmount}"/></td>
				<c:set var="totalMeterRental" value="${totalMeterRental + rr.rental.unpaidAmount}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
					value="${rr.unpaidAmount + rr.penalty.unpaidAmount + rr.rental.unpaidAmount}"/></td>
				<c:set var="grandTotal" value="${grandTotal + rr.unpaidAmount + rr.penalty.unpaidAmount + rr.rental.unpaidAmount}" />
			</tr>
		</c:forEach>
		<tr class="summaryTr">
			<td colspan="6">Total</td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalMeteredSale}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalPenalty}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalMeterRental}" /></td>
			<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${grandTotal}" /></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
				<td colspan="9">${receivableReportWds.dataSize +
					((receivableReportWds.currentPage - 1) *
					receivableReportWds.pageSetting.maxResult)}/${receivableReportWds.totalRecords}
				</td>
					<td style="text-align: right;"><c:if
						test="${receivableReportWds.lastPage <= 5}">
						<c:forEach var="page" begin="1"
							end="${receivableReportWds.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${receivableReportWds.lastPage > 5}">
						<c:if test="${receivableReportWds.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${receivableReportWds.currentPage > 5}">
							<c:set var="modPage"
								value="${receivableReportWds.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(receivableReportWds.currentPage + (5 - modPage)) <= receivableReportWds.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${receivableReportWds.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(receivableReportWds.currentPage + (5 - modPage)) >= receivableReportWds.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${receivableReportWds.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet"
										value="${receivableReportWds.currentPage}" />
									<c:set var="minPageSet"
										value="${receivableReportWds.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < receivableReportWds.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>	
</body>
</html>