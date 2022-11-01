<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Displays the list of warehouses in table format.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<span id="messageSpan" class="message"> ${message}</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable" id="dataTable">
	<tbody>
		<c:forEach var="warehouse" items="${warehouses}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td width="4%"></td>
				<td width="1%">
					<input type="checkbox" id="${warehouse.id}" name=cbWarehouse
					onclick="enableEditAndDeleteButton('cbWarehouse','btnEditWarehouse', null);"/>
				</td>
				<td width="20%">
					<c:if test="${warehouse.lastLevel == false}">
						[<a href="#" class="plus" id="a${warehouse.id}" onclick="showOrHideInfo(this, 'merge${warehouse.id}', ${warehouse.id});"> + </a>] 
					</c:if>
					${warehouse.companyName}
				</td>
				<td width="20%">${warehouse.name}</td>
				<td width="25%">${warehouse.address}</td>
				<td width="20%">${warehouse.parentWarehouseName}</td>
				<td width="10%">
					<c:choose>
						<c:when test="${warehouse.active == true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr class="merge${warehouse.id} merge">
				<td colspan="20" id="addChildWarehouse${warehouse.id}"></td>
			</tr>
		</c:forEach>
	</tbody>
</table>