<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
  <!-- 

	Description: Reference Requisition Form table..
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<style>
html {
  overflow-x: hidden;
  overflow-y: auto;
}

a.plus {
	text-decoration: none;
	font-weight: bold;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	$("#tblRRReference thead tr").find("th:last").css("border-right", "none");
	$("#tblRRReference tbody tr").find("td:last").css("border-right", "none");
	$("#tblRRReference tbody").find("td").addClass("tdProperties");

	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage(pageNumber) {
	var url = "";
	if(!IS_INIT){
		url = getCommonParam()+"&pageNumber="+pageNumber;
	} else {
		var companyId = $("#companyId").val();
		var status = $("#selectPOStatus").val();
		url = contextPath+"/getRequisitionForm/"+typeId+"/loadRfs?companyId="
		+companyId+"&pageNumber="+pageNumber+"&status="+status;
	}
	$("#divRFRefTable").load(url);
}

function populatePO(checkbox) {
	if(poId == null){
		poId = checkbox;
	}else{
		if ($("input[type=checkbox]").length > 1) 
			$(poId).prop("checked", false);
		poId = $(checkbox);
	}
	if ($("input[type=checkbox]:checked").length == 0)
		poId = null;
}
</script>
</head>
<body>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
	<table style="margin-top : 20px;" id="tblRRReference" class="dataTable">
		<thead>
			<tr>
				<th width="2%"></th>
				<th width="13%">Date</th>
				<th width="10%">Reference No.</th>
				<th width="20%">Fleet</th>
				<th width="20%">Project</th>
				<th width="35%" style="border-right: none;">Remarks</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="rf" items="${requisitionForms.data}">
				<tr>
					<td width="3%" style="border-left: none;">
						<input id="${rf.id}" name="cbPO"
								class="cbPO" type="checkbox" onclick="populatePO(this);"/>
						<input type="hidden" class="hdnCompanyId" value="${rf.companyId}">
						<input type="hidden" class="hdnSupplierId" value="${rf.fleetProfileId}">
						<input type="hidden" class="hdnSupplierId" value="${rf.projectId}">
					</td>
					<td width="10%"><fmt:formatDate pattern="MM/dd/yyyy" value="${rf.date}" /></td>
					<td width="10%">RF-${rf.sequenceNumber}</td>
					<td>${rf.fleetName}</td>
					<td>${rf.projectName}</td>
					<td>${rf.remarks}</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">${requisitionForms.dataSize + ((requisitionForms.currentPage - 1) *
					requisitionForms.pageSetting.maxResult)}/${requisitionForms.totalRecords}
				</td>
				<td colspan="3" style="text-align: right;">
				<c:if test="${requisitionForms.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${requisitionForms.lastPage }">
						<a href="#rfContainer" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				<c:if test="${requisitionForms.lastPage > 5}">
					<c:if test="${requisitionForms.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#rfContainer" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#rfContainer" onclick="loadPage (6)" class="pageNumber"> >> </a>
					</c:if>
					<c:if test="${requisitionForms.currentPage > 5}">
						<c:set var="modPage" value="${requisitionForms.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(requisitionForms.currentPage + (5 - modPage)) <= requisitionForms.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${requisitionForms.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(requisitionForms.currentPage + (5 - modPage)) >= requisitionForms.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${requisitionForms.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${requisitionForms.currentPage}" />
								<c:set var="minPageSet" value="${requisitionForms.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#rfContainer" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#rfContainer" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<c:if test="${(maxPageSet) < requisitionForms.lastPage }">
							<a href="#rfContainer" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>