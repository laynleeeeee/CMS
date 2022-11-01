<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Diagnosis Type Table page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditActionNotice").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var name = processSearchName($("#txtName").val());
	var status = $("#selectStatus").val();
	var targetUrl = "/admin/actionNotice?name="+name+"&status="+status+"&pageNumber="+pageNumber;
	goToPage ("divActionNoticeTable", targetUrl);
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table id="dataTable" class="dataTable">
		<thead>
			<tr class="alignLeft">
				<th width=2%">#</th>
				<th width=3%"></th>
				<th width="55%">Name</th>
				<th width="40%">Status</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="index"
				value="${(actionNotice.pageSetting.maxResult * (actionNotice.currentPage - 1)) + 1}" />
			<c:forEach var="actionNotice" items="${actionNotice.data}"
				varStatus="status">
				<tr onMouseOver="this.className='highlight'"
					onMouseOut="this.className='${trClass}'" class="${trClass}">
					<td>${index}</td>
					<td><input type="checkbox" id="${actionNotice.id}"
						name="cbActionNotice"
						onclick="enableEditAndDeleteButton('cbActionNotice','btnEditActionNotice', null);" />
					</td>
					<td>${actionNotice.name}</td>
					<td><c:choose>
							<c:when test="${actionNotice.active eq true}">Active</c:when>
							<c:otherwise>Inactive</c:otherwise>
						</c:choose></td>
				</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="3">${actionNotice.dataSize +
					((actionNotice.currentPage - 1) *
					actionNotice.pageSetting.maxResult)}/${actionNotice.totalRecords}
				</td>
				<td colspan="1" style="text-align: right;"><c:if
						test="${actionNotice.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${actionNotice.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${actionNotice.lastPage > 5}">
						<c:if test="${actionNotice.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${actionNotice.currentPage > 5}">
							<c:set var="modPage" value="${actionNotice.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(actionNotice.currentPage + (5 - modPage)) <= actionNotice.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${actionNotice.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(actionNotice.currentPage + (5 - modPage)) >= actionNotice.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${actionNotice.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${actionNotice.currentPage}" />
									<c:set var="minPageSet" value="${actionNotice.currentPage - 4}" />
								</c:otherwise>
							</c:choose>
							<c:if test="${minPageSet > 5}">
								<a href="#" onclick="loadPage (${minPageSet - 1})"
									class="pageNumber"> << </a>
							</c:if>

							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
								<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < actionNotice.lastPage }">
								<a href="#" onclick="loadPage (${maxPageSet + 1})"
									class="pageNumber"> >> </a>
							</c:if>
						</c:if>
					</c:if>
				</td>
			</tr>
		</tfoot>
	</table>
</body>
</html>