<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp"%>
<!--

	Description: Employee DTR Details jsp page for GVCH
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
</head>
<body>
	<c:forEach var="sched" items="${schedules}">
		<h3>${sched.scheduleName}</h3>
		<c:set var="width" value="${100 / fn:length(sched.headerNames)}%"/>
		<table  class="dataTable" border="1">
			<thead>
				<tr>
					<c:forEach var="h" items="${sched.headerNames}">
						<th width="${width}">${h}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="log" items="${sched.employeeLogDtos}" varStatus="lIndex">
					<tr>
						<c:forEach var="lT" items="${log.logTimes}" varStatus="lTIndex">
							<td width="${width}" style="text-align: center;">${lT}</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:forEach>
</body>
</html>