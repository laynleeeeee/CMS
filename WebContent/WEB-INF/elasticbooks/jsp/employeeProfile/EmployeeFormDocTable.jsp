<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Employee form documents table for GVCH
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<style type="text/css">
#tblDocForm tr.trHeader:hover {
	background-color:#F0F0F0;
}

.trHeader {
	border-bottom: 1px solid black;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	$("#btnEditfileDocuments").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var uri = contextPath + "/employeeProfile/" + employeeId + "/document?formTypeId="+formTypeId+"&pageNumber=1";
	var employeeId = "${employeeId}";
	var formTypeId = $("#selectFormType").val();
	var targetUrl = "/employeeProfile/" + employeeId + "/document?formTypeId="+formTypeId+"&pageNumber="+pageNumber;
	goToPage ("divEmployeeFormDocumentTable", targetUrl);
}

function loadFormDoc(formTypeId, formId, index) {
	var uri = contextPath;
	if (formTypeId == 1) { // Employee Document
		uri += "/employeeDocument";
	} else if (formTypeId == 2) { // Employee Leave Credit
		uri += "/employeeLeaveCredit";
	} else if (formTypeId == 3) { // Cash Bond
		uri += "/formDeduction";
	} else if (formTypeId == 4) { // Authority To Deduct
		uri += "/formDeduction";
	} else if (formTypeId == 5) { // Request For Leave
		uri += "/employeeRequest/1/view?pId="+formId;
	} else if (formTypeId == 6) { // Request For Overtime
		uri += "/employeeRequest/2/view?pId="+formId;
	} else if (formTypeId == 7) { // Personnel Action Notice
		uri += "/personnelActionNotice/view?pId="+formId;
	}
	if (formTypeId < 5) {
		uri += "/form/view?pId="+formId;
		if(formTypeId == 2) {
			uri += "&employeeId="+"${employeeId}";
		}
	}
	var $divForm = $("#divForm"+index);
	var $trForm = $("#trForm"+index);
	if ($($trForm).is(":visible")) {
		$($trForm).hide("slow");
	} else {
		$($trForm).show("slow");
		if ($($divForm).html() == "") {
			$($divForm).load(uri);
		}
	}
}
</script>
<title>Employee Profile - Form Documents</title>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table id="tblDocForm" class="dataTable">
		<tbody>
			<c:set var="index"
				value="${(fileDocuments.pageSetting.maxResult * (fileDocuments.currentPage - 1)) + 1}" />
			<c:forEach var="fd" items="${fileDocuments.data}" varStatus="status">
				<tr onclick="loadFormDoc('${fd.formTypeId}', '${fd.formId}', '${index}');" class="trHeader">
					<td style="font-weight: bold;" width="3%">${index}</td>
					<td style="font-weight: bold;" width="10%">
						<fmt:formatDate pattern="MM/dd/yyyy"  value="${fd.date}" />
					</td>
					<td style="font-weight: bold;">${fd.description}</td>
				</tr>
				<tr id="trForm${index}" style="display: none;">
					<td colspan="3">
						<div id="divForm${index}"></div>
						<hr/>
					</td>
				</tr>
				<c:set var="index" value="${index + 1}" />
			</c:forEach>
		</tbody>
		<tfoot class="nav">
			<tr>
				<td colspan="2">${fileDocuments.dataSize +
					((fileDocuments.currentPage - 1) *
					fileDocuments.pageSetting.maxResult)}/${fileDocuments.totalRecords}
				</td>
				<td colspan="1" style="text-align: right;"><c:if
						test="${fileDocuments.lastPage <= 5}">
						<c:forEach var="page" begin="1" end="${fileDocuments.lastPage }">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
					</c:if> <c:if test="${fileDocuments.lastPage > 5}">
						<c:if test="${fileDocuments.currentPage <= 5}">
							<c:forEach var="page" begin="1" end="5">
								<a href="#" id="page-${page}" onclick="loadPage (${page})"
									class="pageNumber">${page}</a>
							</c:forEach>
							<a href="#" onclick="loadPage (6)" class="pageNumber"> >> </a>
						</c:if>

						<c:if test="${fileDocuments.currentPage > 5}">
							<c:set var="modPage" value="${fileDocuments.currentPage % 5}" />
							<c:choose>
								<c:when
									test="${(fileDocuments.currentPage + (5 - modPage)) <= fileDocuments.lastPage && modPage != 0 }">
									<c:set var="maxPageSet"
										value="${fileDocuments.currentPage + (5 - modPage)}" />
									<c:set var="minPageSet" value="${maxPageSet - 4}" />
								</c:when>
								<c:when
									test="${(fileDocuments.currentPage + (5 - modPage)) >= fileDocuments.lastPage && modPage != 0 }">
									<c:set var="maxPageSet" value="${fileDocuments.lastPage}" />
									<c:set var="minPageSet"
										value="${maxPageSet + (1 - (maxPageSet % 5))}" />
								</c:when>
								<c:otherwise>
									<c:set var="maxPageSet" value="${fileDocuments.currentPage}" />
									<c:set var="minPageSet" value="${fileDocuments.currentPage - 4}" />
								</c:otherwise>
							</c:choose>
							<c:if test="${minPageSet > 5}">
								<a href="#" onclick="loadPage (${minPageSet - 1})"
									class="pageNumber"> << </a>
							</c:if>

							<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
								<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
							</c:forEach>

							<c:if test="${(maxPageSet) < fileDocuments.lastPage }">
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