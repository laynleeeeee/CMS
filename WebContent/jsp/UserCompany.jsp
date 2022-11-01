<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="include.jsp" %>
    <!-- 
	Common jsp file that list the company of logged user.
     -->
<c:choose>
	<c:when test="${fn:length(user.userCompanies) > 1}">
		<table class="tableInfo">
			<tr>
				<td style="width: 5%;">Company:</td>
				<td> 
					<select id="selectUserCompanies">
						<option id="dbAll" value="all">All</option>
						<c:forEach var="userCompany" items="${userCompanies}">
							<option id="db${userCompany.company.id}"
								value="${userCompany.company.id}">${userCompany.company.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><hr></td>
			</tr>
		</table>
	</c:when>
	<c:otherwise>
		<input type="hidden" id="hdnUserCompanyId" value="${userCompanyId}" />
	</c:otherwise>
</c:choose>


