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
</script>
<span id="messageSpan" class="message"> ${message}
</span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table class="dataTable" id="dataTable">
	<tbody>
		<c:forEach var="division" items="${divisions}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='${trClass}'" class="${trClass}">
				<td width="3%" ></td>
				<td width="1%">
					<input type="checkbox" id="${division.id}" name="cbDivision"
					onclick="enableEditAndDeleteButton('cbDivision','btnEditDivision', null);"/>
				</td>
				<td width="15%">
					<c:if test="${division.lastLevel == false}">
						[<a href="#" class="plus" id="a${division.id}" onclick="showOrHideInfo(this, 'merge${division.id}', ${division.id});"> + </a>] 
					</c:if>
					${division.number}
				</td>
				<td width="20%">${division.name}</td>
				<td width="30%">${division.description}</td>
				<td width="20%">${division.pdName}</td>
				<td width="10%">
					<c:choose>
						<c:when test="${division.active == true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr class="merge${division.id} merge">
				<td colspan="20" id="addChildDivision${division.id}"></td>
			</tr>
		</c:forEach>
	</tbody>
</table>