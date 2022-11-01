<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!--

	Description: Displays the accounts of the Concessionaire in table format
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnAddConcessionaireAcct").removeAttr("disabled");
	$("#btnEditConcessionaireAcct").attr("disabled", "disabled");
	$("#btnDeleteConcessionaireAcct").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = baseUrl +getCommonParam()+"&pageNumber="+pageNumber;
	goToPage ("concessionaireAcctTable", targetUrl);
}
</script>
</head>
<body>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}"/>
	<!--Table for detailed accounts of the concessionaire -->
	<table class="dataTable">
		<thead>
			<tr>
				<th width="1%"><input type="checkbox" id="checkAll" onclick="checkUncheckedAll
				('cbConcessionaireAcct', this, 'btnEditConcessionaireAcct', 'btnDeleteConcessionaireAcct')"></th>
				<th>Date</th>
				<th>Due Date</th>
				<th>Paid Date</th>
				<th>Water Bill Number</th>
				<th>Cubic Meters</th>
				<th>Discount</th>
				<th>Metered Sale</th>
				<th>Penalty</th>
				<th>Meter Rental</th>
				<th>Total</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="ca" items="${concessionaireAccts.data}">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
					<td><input type="checkbox" id="${ca.id}" name="cbConcessionaireAcct"
					onclick="enableEditAndDeleteButton ('cbConcessionaireAcct','btnEditConcessionaireAcct',
							'btnDeleteConcessionaireAcct');"></td>
					<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ca.date}"/></td>
					<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ca.dueDate}"/></td>
					<td><fmt:formatDate pattern="MM/dd/yyyy" value="${ca.concessionairePayment.date}"/></td>
					<td>${ca.wbNumber}</td>
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.cubicMeter}" /></td>
					<td class="numberClass">
						<c:if test="${ca.seniorCitizen == true }">
							<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.discount}"/>
						</c:if>
					</td>
					<td class="numberClass">
						<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.unpaidAmount}" />
					</td>
					<c:set var="totalSale" value="${totalSale + ca.unpaidAmount}" />
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.penalty.unpaidAmount}" /></td>
					<c:set var="totalPenalty" value="${totalPenalty + ca.penalty.unpaidAmount}" />
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${ca.rental.unpaidAmount}"/></td>
					<c:set var="totalRental" value="${totalRental + ca.rental.unpaidAmount}" />
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2"
						value="${ca.unpaidAmount + ca.penalty.unpaidAmount + ca.rental.unpaidAmount}"/></td>
					<c:set var="totalAmt" value="${totalAmt + ca.unpaidAmount + ca.penalty.unpaidAmount + ca.rental.unpaidAmount}" />
				</tr>
			</c:forEach>
			<tr class="summaryTr">
				<td colspan="7">Total</td>
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalSale}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalPenalty}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalRental}" />
				<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmt}" />
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="9">${concessionaireAccts.dataSize +
					((concessionaireAccts.currentPage - 1) *
					concessionaireAccts.pageSetting.maxResult)}/${concessionaireAccts.totalRecords}
				</td>
				<td style="text-align: right;"><c:if
						test="${concessionaireAccts.lastPage <= 5}">
						<c:forEach var="page" begin="1"
							end="${concessionaireAccts.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${concessionaireAccts.lastPage > 5}">
						<c:if test="${concessionaireAccts.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${concessionaireAccts.currentPage > 5}">
							<c:set var="modPage"
								value="${concessionaireAccts.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(concessionaireAccts.currentPage + (5 - modPage)) <= concessionaireAccts.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${concessionaireAccts.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(concessionaireAccts.currentPage + (5 - modPage)) >= concessionaireAccts.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${concessionaireAccts.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet"
										value="${concessionaireAccts.currentPage}" />
									<c:set var="minPageSet"
										value="${concessionaireAccts.currentPage - 4}" />
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

							<c:if test="${(maxPageSet) < concessionaireAccts.lastPage }">
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