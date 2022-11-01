<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
table {
	border-collapse:collapse;
	width: 100%;
}

th, td {
	border: 1px solid black;
	font-size: 12px;
}

th{
	background-color: #eee;
}

#main {
  	margin-left: auto;
  	margin-right: auto;
}

@media print {
	#controls {
		display: none;
	}
	
	th, td {
		border: 1px solid black;
		font-size: 12px;
	}

	th{
		background-color: #eee;
	}
}
</style>
</head>
<body>
<div id="main">
<div>
 List of companies.
</div>
<div>
<table>
	<thead>
		<tr>
			<th width="2%"># </th>
			<th width="30%">Name</th>
			<th width="33%">Address</th>
			<th width="12%">Contact Number</th>
			<th width="15%">Email</th>
			<th width="3%">Active</th>		
		</tr>
	</thead>
	<tbody>
		<c:forEach var="company" items="${companies.data}" varStatus="status">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
				<td>${status.index + 1}</td>
				<td>${company.name}</td>
				<td>${company.address}</td>
				<td>${company.contactNumber}</td>
				<td>${company.emailAddress}</td>
				<td>${company.active}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div>
</div>
<div id="controls" style="text-align: right; margin-top: 5px;">
	<input type="button" value="Print" onclick="window.print(); window.close();"  />
	<input type="button" value="Close" onclick="window.close();" />
</div>
</body>
</html>