<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!-- 


	Description: Warehouse table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	$("#btnEditWarehouse").attr("disabled", "disabled");
	markCurrentPageNumber ("${pageNumber}");
});

function loadPage(pageNumber) {
	goToPage("divWarehouseTable", SEARCH_URL+getCommonParam()+"&page="+pageNumber);
}

function showOrHideInfo(link, elemClass, warehouseId) {
	var label = $(link).text();
	var $addChildWarehouse = $("#addChildWarehouse" + warehouseId);
	var status = $("#selectStatusId").val();
	var $mainTr = $("#tr" + warehouseId);
	if (label == " + ") {
		$("." + elemClass).show();
		$(link).text(" - ");
		var color = $($mainTr).css("background-color");
		if ($($addChildWarehouse).html() == ""){
			var uri = contextPath + "/admin/warehouses/showChildren?warehouseId="+warehouseId+"&status="+status;
			$($addChildWarehouse).load(uri);
		}
		$($addChildWarehouse).css("background-color", color);
	} else {
		$("." + elemClass).hide();
		$(link).text(" + ");
	}
}
</script>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="tblWarehouse" class="dataTable" border=0>
	<thead>
		<tr class="alignLeft">
			<th width="2%">#</th>
			<th width="1%"></th>
			<th width="20%">Company</th>
			<th width="15">Division</th>
			<th width="15%">Warehouse</th>
			<th width="20%">Address</th>
			<th width="15%">Main Storage</th>
			<th width="12%">Status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="warehouse" items="${warehouses.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td>${status.index +1}</td>
				<td>
					<input type="checkbox" id="${warehouse.id}" name="cbWarehouse"
						onclick="enableEditAndDeleteButton('cbWarehouse','btnEditWarehouse', null);"/>
				</td>
				<td>
					<c:if test="${warehouse.lastLevel == false}">
						[<a href="#" class="plus" id="a${warehouse.id}" onclick="showOrHideInfo(this, 'merge${warehouse.id}', ${warehouse.id});"> + </a>] 
					</c:if>
					${warehouse.companyName}
				</td>
				<td style="white-space: pre-wrap;">${warehouse.divisionName}</td>
				<td style="white-space: pre-wrap;">${warehouse.name}</td>
				<td style="white-space: pre-wrap;">${warehouse.address}</td>
				<td style="white-space: pre-wrap;">${warehouse.parentWarehouseName}</td>
				<td>
					<c:choose>
						<c:when test="${warehouse.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr class="merge${warehouse.id} merge">
				<td colspan="20" id="addChildWarehouse${warehouse.id}"></td>
			</tr>
			<c:set var="index" value="${index + 1}" />
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7">${warehouses.dataSize + ((warehouses.currentPage - 1)
				* warehouses.pageSetting.maxResult)}/${warehouses.totalRecords}
			</td>
			<td colspan="1" style="text-align: right;"><c:if
					test="${warehouses.lastPage <= 5}">
					<c:forEach var="page" begin="1" end="${warehouses.lastPage }">
						<a href="#" id="page-${page}" onclick="loadPage (${page})"
							class="pageNumber">${page}</a>
					</c:forEach>
				</c:if> <c:if test="${warehouses.lastPage > 5}">
					<c:if test="${warehouses.currentPage <= 5}">
						<c:forEach var="page" begin="1" end="5">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>
						<a href="#" onclick="loadPage (6)" class="pageNumber"> </a>
					</c:if>

					<c:if test="${warehouses.currentPage > 5}">
						<c:set var="modPage" value="${warehouses.currentPage % 5}" />
						<c:choose>
							<c:when
								test="${(warehouses.currentPage + (5 - modPage)) <= warehouses.lastPage && modPage != 0 }">
								<c:set var="maxPageSet"
									value="${warehouses.currentPage + (5 - modPage)}" />
								<c:set var="minPageSet" value="${maxPageSet - 4}" />
							</c:when>
							<c:when
								test="${(warehouses.currentPage + (5 - modPage)) >= warehouses.lastPage && modPage != 0 }">
								<c:set var="maxPageSet" value="${warehouses.lastPage}" />
								<c:set var="minPageSet"
									value="${maxPageSet + (1 - (maxPageSet % 5))}" />
							</c:when>
							<c:otherwise>
								<c:set var="maxPageSet" value="${warehouses.currentPage}" />
								<c:set var="minPageSet" value="${warehouses.currentPage - 4}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${minPageSet > 5}">
							<a href="#" onclick="loadPage (${minPageSet - 1})"
								class="pageNumber"> </a>
						</c:if>

						<c:forEach var="page" begin="${minPageSet}" end="${maxPageSet}">
							<a href="#" id="page-${page}" onclick="loadPage (${page})"
								class="pageNumber">${page}</a>
						</c:forEach>

						<c:if test="${(maxPageSet) < warehouses.lastPage }">
							<a href="#" onclick="loadPage (${maxPageSet + 1})"
								class="pageNumber"> </a>
						</c:if>
					</c:if>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>
