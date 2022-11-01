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
 List of users.
</div>
<div>
<table>
			<thead>
				<tr>
					<th width="2%">#</th>
					<th width="11%">User Name</th>
					<th width="18%">Name</th>
					<th width="5%">Birthdate</th>
					<th width="10%">Email Address</th>
					<th width="12%">Contact Number</th>
					<th width="22%">Address</th>
					<th width="10%">Company</th>
					<th width="10%">User Group</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${users.data}" varStatus="status">
					<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
						<td>${status.index + 1} </td>
						<td>${user.username}</td>
						<td>${user.firstName} ${user.middleName} ${user.lastName}</td>
						<td><fmt:formatDate pattern="MM/dd/yyyy"  value="${user.birthDate}" /></td>
						<td>${user.emailAddress}</td>
						<td>${user.contactNumber}</td>
						<td>${user.address}</td>						
						<td>${user.company.name}</td>
						<td>${user.userGroup.name}</td>
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