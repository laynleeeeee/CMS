<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Profile Record
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Fleet Profile</title>
<style type="text/css">
.fleet_labels{
	text-align: left;
	width: 160px;
	font-size: 16;
	font-weight: bold;
	padding: 7px;
}

.content {
	padding-left: 30px;
}

</style>
</head>
<body>
<c:if test="${fleetProfile.id ne null}">
	<table >
		<tr>
			<td class="fleet_labels">Company</td>
			<td class="content">${fleetProfile.company.name} </td>
		</tr>
		<tr>
			<td class="fleet_labels">Fleet Type</td>
			<td class="content">${fleetProfile.fleetType.name} </td>
		</tr>
		<tr>
			<td class="fleet_labels">
				<c:choose>
					<c:when test="${fleetProfile.fleetType.fleetCategoryId eq 1}">Code</c:when>
					<c:otherwise>Vehicle Name</c:otherwise>
				</c:choose>
			</td>
			<td class="content">${fleetProfile.codeVesselName} </td>
		</tr>
		<tr>
			<td class="fleet_labels">Acquisition Date</td>
			<td class="content">
				<fmt:formatDate pattern="MM/dd/yyyy"  value="${fleetProfile.acquisitionDate}" />
			</td>
		</tr>
		<c:choose>
			<c:when test="${fleetProfile.fleetType.fleetCategoryId eq 1}">
				<tr>
					<td class="fleet_labels">Make</td>
					<td class="content">${fleetProfile.make}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Model</td>
					<td class="content">${fleetProfile.model}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Chassis No.</td>
					<td class="content">${fleetProfile.chassisNo}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Plate No.</td>
					<td class="content">${fleetProfile.plateNo}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Engine No.</td>
					<td class="content">${fleetProfile.engineNo}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Description</td>
					<td class="content">${fleetProfile.description}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Driver</td>
					<td class="content">
						${fleetProfile.driver.lastName}, ${fleetProfile.driver.firstName} ${fleetProfile.driver.middleName}
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td class="fleet_labels">Official No.</td>
					<td class="content">${fleetProfile.officialNo}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Call Sign</td>
					<td class="content">${fleetProfile.callSign}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Tonnage Weight</td>
					<td class="content">${fleetProfile.tonnageWeight}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Engine No.</td>
					<td class="content">${fleetProfile.engineNo}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Description</td>
					<td class="content">${fleetProfile.description}</td>
				</tr>
				<tr>
					<td class="fleet_labels">VMS</td>
					<td class="content">${fleetProfile.vms}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Propeller</td>
					<td class="content">${fleetProfile.propeller}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Winch</td>
					<td class="content">${fleetProfile.winch}</td>
				</tr>
				<tr>
					<td class="fleet_labels">Captain</td>
					<td class="content">${fleetProfile.captain}</td>
				</tr>
			</c:otherwise>
		</c:choose>
		<tr>
			<td class="fleet_labels">Supplier</td>
			<td class="content">${fleetProfile.supplier}</td>
		</tr>
	</table>
</c:if>
</body>
</html>