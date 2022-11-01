<%@ include file="../../include.jsp" %>
<!-- 

	Description: Concessionaire table.
 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
	$(document).ready (function () {
		$("#btnEditConcessionaire").attr("disabled", "disabled");
		$("#btnDeleteConcessionaire").attr("disabled", "disabled");
		markCurrentPageNumber ($("#hdnPageNumber").val());
	});
	
	function loadPage (pageNumber) {
		var targetUrl = "/concessionaireList"+getCommonParameter()+"&pageNumber="+pageNumber;
		goToPage ("concessionaireTable", targetUrl);
	}

	
	function redirectToConcessionaireAcct (concessionaireId) {
		window.location.replace(contextPath+"/concessionaire/"+concessionaireId+"/account");
	}	
	
	function assignCompanyId(companyId) {
		$("#hdnConcessionaireCompanyId").val(companyId);
	}
	
</script>
<span id="messageSpan" class="message">
	${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable">
	<thead>
		<tr>
			<th width="2%">#</th>
			<th width="1%"><input type="checkbox" id="checkAll"
				onclick="checkUncheckedAll ('cbConcessionaire', this, 'btnEditConcessionaire', 'btnDeleteConcessionaire')"></th>
			<th width="20%">Name</th>                     
			<th width="22%">Address</th>
			<th width="10%">Contact Number</th>
			<th width="8%">Classification</th>
			<th width="8%">Metered Sale</th>
			<th width="8%">Penalty</th>
			<th width="8%">Meter Rental</th>
			<th width="13%">Current Balance</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="concessionaire" items="${concessionaires.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});">${status.index + 1} </td>
				<td>
					<input type="checkbox" id="${concessionaire.id}" name="cbConcessionaire"
						onclick="enableEditAndDeleteButton('cbConcessionaire','btnEditConcessionaire','btnDeleteConcessionaire'); 
							assignCompanyId(${concessionaire.companyId});">
				</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});">${concessionaire.firstName} ${concessionaire.middleName} ${concessionaire.lastName}</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});">${concessionaire.address}</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});">${concessionaire.contactNumber}</td>
				<c:set var="classification" value="${concessionaire.concessionaireClassification.name}"></c:set>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});">${classification}</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.meteredSale}" />
				</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.penalty}" />
				</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.meterRental}" />
				</td>
				<td onclick="redirectToConcessionaireAcct (${concessionaire.id});" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${concessionaire.meteredSale + concessionaire.penalty + concessionaire.meterRental}" />
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="9">
				${concessionaires.dataSize + ((concessionaires.currentPage - 1) * concessionaires.pageSetting.maxResult)}/${concessionaires.totalRecords}
			</td>
			<td style="text-align: right;">
				<c:if test="${concessionaires.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${concessionaires.lastPage }" >
						<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
					</c:forEach>
				</c:if>
				
				<c:if test="${concessionaires.lastPage > 5}">
					<c:if test="${concessionaires.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> >>  </a>
					</c:if>
					
					<c:if test="${concessionaires.currentPage > 5}">
						<c:set var="modPage" value="${concessionaires.currentPage % 5}"/>
						<c:choose>
							<c:when test="${(concessionaires.currentPage + (5 - modPage)) <= concessionaires.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${concessionaires.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when test="${(concessionaires.currentPage + (5 - modPage)) >= concessionaires.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${concessionaires.lastPage}" />
								<c:set var="minPageSet" value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${concessionaires.currentPage}" />
								<c:set var="minPageSet" value="${concessionaires.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						
						<c:if test="${minPageSet > 5}" >
							<a href="#" onclick="loadPage (${minPageSet - 1})" class="pageNumber"> << </a>
						</c:if>
						
						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}" >
							<a href="#" id="page-${page}" onclick="loadPage (${page})" class="pageNumber">${page}</a>
						</c:forEach>
						
						<c:if test="${(maxPageSet) < concessionaires.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})" class="pageNumber"> >> </a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>
