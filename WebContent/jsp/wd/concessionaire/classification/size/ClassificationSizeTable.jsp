<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../include.jsp" %>
    <!-- 

	Description: Clissification Size Table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#btnEditClassificationSize").attr("disabled", "disabled");
	$("#btnDeleteClassificationSize").attr("disabled", "disabled");
	markCurrentPageNumber ($("#hdnPageNumber").val());
});

function loadPage (pageNumber) {
	var targetUrl = "/classificationSize"+getCommonParameter()+"&pageNumber="+pageNumber;
	goToPage ("classificationSizeTable", targetUrl);
}
</script>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<!--Table for detailed classification, sizes and charges -->
<table class="dataTable">
	<thead>
		<tr>
			<th rowspan=2 width="2%">#</th>
			<th rowspan=2 width="1%"><input type="checkbox" id="checkAll"
				onclick="checkUncheckedAll ('cbClassificationSize', this, 'btnEditClassificationSize', 'btnDeleteClassificationSize')"></th>
			<th rowspan=2>Classification</th>
			<th rowspan=2>Size</th>
			<th rowspan=2>Minimum Charge</th>
			<th colspan=4>Commodity Charges</th>
		</tr>
		<tr>
			<td> 11-20</td>
			<td> 21-30</td>
			<td> 31-40</td>
			<td> 41-up</td>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="classificationSize" items="${classificationSizes.data}" varStatus="status">
				<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
					<td>${status.index + 1}</td>
					<td><input type="checkbox" id="${classificationSize.id}" name="cbClassificationSize"
						onclick="enableEditAndDeleteButton('cbClassificationSize','btnEditClassificationSize','btnDeleteClassificationSize'); ">
					</td>
					<td>${classificationSize.concessionaireClassification.name}</td>
					<td>${classificationSize.size}</td>
					<td></td>
					<td></td>
					<td></td>	
					<td></td>
					<td></td>
				</tr>
	</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="8">
				${classificationSizes.dataSize + ((classificationSizes.currentPage - 1) * classificationSizes.pageSetting.maxResult)}/${classificationSizes.totalRecords}
			</td>
			<td style="text-align: right;">
				<c:if test="${classificationSizes.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${classificationSizes.lastPage }" >
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				
				<c:if test="${classificationSizes.lastPage > 5}">
					<c:if test="${classificationSizes.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >>  </a>
					</c:if>
					
					<c:if test="${classificationSizes.currentPage > 5}">
						<c:set var="modPage" value="${classificationSizes.currentPage % 5}"/>
						<c:choose>
							<c:when test="${(classificationSizes.currentPage + (5 - modPage)) <= classificationSizes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${classificationSizes.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when test="${(classificationSizes.currentPage + (5 - modPage)) >= classificationSizes.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${classificationSizes.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${classificationSizes.currentPage}" />
								<c:set var="minPageSet" value="${classificationSizes.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						
						<c:if test="${minPageSet > 5}" >
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						
						<c:if test="${(maxPageSet) < classificationSizes.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>	
</table>