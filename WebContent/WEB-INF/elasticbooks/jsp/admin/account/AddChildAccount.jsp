<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Add Child Account table
 -->
<!DOCTYPE>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
</script>
<span id="messageSpan" class="message"> ${message} </span>
<input type="hidden" id="hdnPageNumber" value="${pageNumber}" />
<table id="dataTable" class="dataTable">
	<tbody>
		<c:forEach var="acct" items="${accounts}" varStatus="status">
			<tr class="noBorder" id="tr${acct.id}">
				<td width="3%" ></td>
				<td width="1%">
					<input type="checkbox" id="${acct.id}" name="cbAccount" 
						onclick="enableEditAndDeleteButton('cbAccount','btnEditAccount', null);"/>
				</td>
				<td width="10%">
					<c:if test="${acct.lastLevel == false}">
						[<a href="#" class="plus" id="a${acct.id}" onclick="showOrHideInfo(this, 'merge${acct.id}', ${acct.id});"> + </a>] 
					</c:if>
					${acct.number}
				</td>
				<td width="15%">${acct.accountName}</td>
				<td width="36%">${acct.description}</td>
				<td width="15%">${acct.atName}</td>
				<td width="15%">${acct.paName}</td>
				<td width="5%">
					<c:choose>
						<c:when test="${acct.active eq true}">Active</c:when>
						<c:otherwise>Inactive</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr class="merge${acct.id} merge">
				<td colspan="8" id="addChildAccount${acct.id}" style="padding-left: 15px;"></td>
			</tr>
		</c:forEach>
	</tbody>
</table>