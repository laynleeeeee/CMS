<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
	<c:when test="${success}">
saved;<c:out value="${formNumber}"/>;<c:out value="${formId}"/>
	</c:when>
	<c:otherwise>
error
	</c:otherwise>
</c:choose>