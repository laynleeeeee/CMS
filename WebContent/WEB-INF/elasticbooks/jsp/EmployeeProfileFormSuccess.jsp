<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
	<c:when test="${success}">
		saved;<c:out value="${employeeProfileId}"/>;<c:out value="${employeeId}"/>;<c:out value="${createdBy}"/>;
		<c:out value="${ebObjectId}"/>
	</c:when>
	<c:otherwise>
error
	</c:otherwise>
</c:choose>