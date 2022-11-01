<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
<table class="dataTable">
	<tr class="tdInfo${dailyTimeSheetSchedule.id} tdInfo">
				<td colspan="2"></td>
				<td style="font-weight: bold;">Name</td>
				<td style="font-weight: bold;">Date</td>
				<td style="font-weight: bold;">Shift</td>
	</tr>
	<c:forEach var="ssdl" items="${lines}" varStatus="status">
			<tr class="tdInfo${dailyTimeSheetSchedule.id} tdInfo">
				<td colspan="2"></td>
				<td class="tdRowLines">${ssdl.employeeName}</td>
				<td class="tdRowLines"><fmt:formatDate pattern="MM/dd/yyyy" value="${ssdl.date}" /></td>
				<td class="tdRowLines">${ssdl.shiftName}</td>
	</tr>
	</c:forEach>
</table>
</body>
</html>