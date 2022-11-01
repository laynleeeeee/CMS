<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ include file="../../../include.jsp" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Concessionaire Classification Printable</title>

</head>
<body>
<table class="printStyle">

	<thead>
		<tr>
			<th width="3%">#</th>
			<th>Name</th>
			<th>Description</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="concessionaireClassification" items="${concessionaireClassifications.data}" varStatus="status">
				<tr>
					<td>${status.index + 1} </td>
					<td>${concessionaireClassification.name}</td>
					<td>${concessionaireClassification.description}</td>
				</tr>
			</c:forEach>
		</tbody>
	<tfoot></tfoot>
</table>
</body>
</html>