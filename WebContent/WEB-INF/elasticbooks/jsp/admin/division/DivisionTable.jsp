<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of divisions in table format.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditDivision").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "/admin/division"+getCommonParam()+"&pageNumber="+pageNumber;
	goToPage("divisionTable", url);
}

function showOrHideInfo (link, elemClass, divisionId) {
	var label = $(link).text();
	var $addChildDivision = $("#addChildDivision" + divisionId);
	var $mainTr = $("#tr" + divisionId);
	if (label == " + ") {
		$("." + elemClass).show();
		$(link).text(" - ");
		var color = $($mainTr).css("background-color");
		if($($addChildDivision).html() == ""){
			var uri = contextPath + "/admin/division/showChildren?divisionId="+divisionId;
			$($addChildDivision).load(uri);
		}
		$($addChildDivision).css("background-color", color);
	} else {
		$("." + elemClass).hide();
		$(link).text(" + ");
	}
}
</script>
<span id="messageSpan" class="message"> ${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable" id="dataTable">
	<thead>
		<tr class="alignLeft">
			<th width="1%">#</th>
			<th width="2%"></th>
			<th width="15%">Number</th>
			<th width="20%">Name</th>
			<th width="30%">Description</th>
			<th width="20%">Main Division</th>
			<th width="10%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="division" items="${divisions.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index +1}</td>
				<td>
					<input type="checkbox" id="${division.id}" name="cbDivision"
					onclick="enableEditAndDeleteButton('cbDivision','btnEditDivision', null);"/>
				</td>
				<td>
					<c:if test="${division.lastLevel == false}">
						[<a href="#" class="plus" id="a${division.id}" onclick="showOrHideInfo(this, 'merge${division.id}', ${division.id});"> + </a>] 
					</c:if>
					${division.number}
				</td>
				<td>${division.name}</td>
				<td>${division.description}</td>
				<td>${division.pdName}</td>
				<c:choose>
					<c:when test="${division.active == true}"><td>Active</td></c:when>
					<c:otherwise><td>Inactive</td></c:otherwise>
				</c:choose>
			</tr>

			<tr class="merge${division.id} merge">
				<td colspan="20" id="addChildDivision${division.id}"></td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">${divisions.dataSize + ((divisions.currentPage - 1) *
				divisions.pageSetting.maxResult)}/${divisions.totalRecords}
			</td>
			<td colspan="3" style="text-align: right;">
			<c:if test="${divisions.lastPage <= 5}">
				<c:forEach var="page" begin="1" end="${divisions.lastPage }">
					<a href="#" id="page-${page}" onclick="loadPage (${page})"
						class="pageNumber">${page}</a>
				</c:forEach>
			</c:if>
			<c:if test="${divisions.lastPage > 5}">
				<c:if test="${divisions.currentPage <= 5}">
					<c:forEach var="page" begin="1" end="5">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
					<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
				</c:if>
				<c:if test="${divisions.currentPage > 5}">
					<c:set var="modPage" value="${divisions.currentPage % 5}" />
					<c:choose>
						<c:when
							test="${(divisions.currentPage + (5 - modPage)) <= divisions.lastPage && modPage != 0 }">
							<c:set var="maxPageSet"
								value="${divisions.currentPage + (5 - modPage)}" />
							<c:set var="minPageSet" value="${maxPageSet - 4}" />
						</c:when>
						<c:when
							test="${(divisions.currentPage + (5 - modPage)) >= divisions.lastPage && modPage != 0 }">
							<c:set var="maxPageSet" value="${divisions.lastPage}" />
							<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
						</c:when>
						<c:otherwise>
							<c:set var="maxPageSet" value="${divisions.currentPage}" />
							<c:set var="minPageSet" value="${divisions.currentPage - 4}" />
						</c:otherwise>
					</c:choose>
					<c:if test="${minPageSet > 5}">
						<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
					</c:if>
					<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
					<c:if test="${(maxPageSet) < divisions.lastPage }">
						<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
					</c:if>
				</c:if>
			</c:if></td>
			</tr>
	</tfoot>
</table>