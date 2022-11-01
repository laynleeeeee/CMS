<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ include file="../../../include.jsp" %>
 <!--

	Description: Concessionaire classification table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditConcessionaireClassification").attr("disabled", "disabled");
	$("#btnDeleteConcessionaireClassification").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/concessionaireClassification"+getCommonParameter()+"&pageNumber="+pageNumber;
	goToPage ("concessionaireClassificationTable", targetUrl);
}
</script>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<input type="hidden" id="hdnConcessionaireCompanyId" />
<table class="dataTable">
		<thead>
			<tr>
				<th width="2%">#</th>
				<th width="1%"><input type="checkbox" id="checkAll"
				onclick="checkUncheckedAll ('cbConcessionaireClassification', this, 'btnEditConcessionaireClassification', 'btnDeleteConcessionaireClassification')"></th>
				<th>Name</th>
				<th>Description</th>
			</tr>
		</thead>
				<c:forEach var="concessionaireClassification" items="${concessionaireClassifications.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
					<td>${status.index + 1}</td>
					<td><input type="checkbox" id="${concessionaireClassification.id}" name="cbConcessionaireClassification"
						onclick="enableEditAndDeleteButton('cbConcessionaireClassification','btnEditConcessionaireClassification','btnDeleteConcessionaireClassification'); ">
					</td>
					<td>${concessionaireClassification.name}</td>
					<td>${concessionaireClassification.description}</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="3">
				${concessionaireClassifications.dataSize + ((concessionaireClassifications.currentPage - 1) * concessionaireClassifications.pageSetting.maxResult)}/${concessionaireClassifications.totalRecords}
			</td>
			<td style="text-align: right;">
				<c:if test="${concessionaireClassifications.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${concessionaireClassifications.lastPage }" >
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				
				<c:if test="${concessionaireClassifications.lastPage > 5}">
					<c:if test="${concessionaireClassifications.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >>  </a>
					</c:if>
					
					<c:if test="${concessionaireClassifications.currentPage > 5}">
						<c:set var="modPage" value="${concessionaireClassifications.currentPage % 5}"/>
						<c:choose>
							<c:when test="${(concessionaireClassifications.currentPage + (5 - modPage)) <= concessionaireClassifications.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${concessionaireClassifications.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when test="${(concessionaireClassification.currentPage + (5 - modPage)) >= concessionaireClassifications.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${concessionaireClassifications.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${concessionaireClassifications.currentPage}" />
								<c:set var="minPageSet" value="${concessionaireClassifications.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						
						<c:if test="${minPageSet > 5}" >
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						
						<c:if test="${(maxPageSet) < concessionaireClassifications.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
	</table>