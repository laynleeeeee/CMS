<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Employee Basic info jsp page form for GVCH
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
.tblLabels {
	text-align: left;
	white-space: nowrap;
	padding-left: 15px;
	padding-bottom: 5px;
	font-weight: bold;
}

.tblValue {
	text-align: left;
	white-space: nowrap;
	padding-left: 15px;
	padding-bottom: 5px;
	width: 50%
}
</style>
<title>Employee Profile - Basic info</title>
</head>
<body>
<div>
<table class="formTable">
 	<tr>
		<td class="tblLabels">
			<c:if test="${employeeProfile.employeeId ne null}">
				<input type="button" value="Edit" onclick="loadPRecordForm('${employeeProfile.employeeId}');"/>
				<input type="button" value="Print" onclick="printProfile('${employeeProfile.employeeId}');"/>
			</c:if>
		</td>
	</tr>
	<tr class="tblLabels"><td>&nbsp;</td></tr>
	<tr>
		<td class="tblLabels">Permanent Address</td>
		<td class="tblValue">
			${employeeProfile.permanentAddress}
		</td>
		<td class="tblLabels">Sex</td>
		<td class="tblValue">
			${employeeProfile.gender.name}
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Birthdate</td>
		<td class="tblValue">
			<fmt:formatDate pattern="MM/dd/yyyy"
				value="${employeeProfile.employee.birthDate}" />
		</td>
		<td class="tblLabels">Birthplace</td>
		<td class="tblValue">
			${employeeProfile.birthPlace}
		</td>
	</tr>
	<tr>
	</tr>
	<tr>
		<td class="tblLabels">Civil Status</td>
		<td class="tblValue">
			${employeeProfile.civilStatus.name}
		</td>
		<td class="tblLabels">Email Address</td>
		<td class="tblValue">
			${employeeProfile.employee.emailAddress}
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Father's Name</td>
		<td class="tblValue">
			${employeeProfile.employeeFamily.fatherName}
		</td>
		<td class="tblLabels">Occupation</td>
		<td class="tblValue">
			${employeeProfile.employeeFamily.fatherOccupation}
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Mother's Name</td>
		<td class="tblValue">
			${employeeProfile.employeeFamily.motherName}
		</td>
		<td class="tblLabels">Occupation</td>
		<td class="tblValue">
			${employeeProfile.employeeFamily.motherOccupation}
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Name of Spouse</td>
		<td class="tblValue">
			${employeeProfile.employeeFamily.spouseName}
		</td>
		<td class="tblLabels">Occupation</td>
		<td class="tblValue">
			${employeeProfile.employeeFamily.spouseOccupation}
		</td>
	</tr>
	<tr>
		<td class="tblLabels">Educational Attainment</td>
	</tr>
	<tr>
		<td class="tblLabels">Elementary</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">School</td>
			<td class="tblValue">${employeeProfile.employeeEducationalAttainment.elementarySchool}</td>
		<td class="tblLabels">Year Graduated</td>
			<td class="tblValue">${employeeProfile.employeeEducationalAttainment.elementaryYear}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Address</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.elementaryAddress}</td>
	</tr>
	<tr>
		<td class="tblLabels">Secondary</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">School</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.hsSchool}</td>
		<td class="tblLabels">Year Graduated</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.hsYear}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Address</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.hsAddress}</td>
	</tr>
	<tr>
		<td class="tblLabels">Tertiary</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Course</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.collegeCourse}</td>
		<td class="tblLabels">Year Graduated</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.collegeYear}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">School</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.collegeSchool}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Address</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.collegeAddress}</td>
	</tr>
	<tr>
		<td class="tblLabels">Post Graduate</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Course</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.postGradCourse}</td>
		<td class="tblLabels">Year Graduated</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.postGradYear}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">School</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.postGradSchool}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Address</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.postGradAddress}</td>
	</tr>
	<tr>
		<td class="tblLabels">Vocational</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Course</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.vocationalCourse}</td>
		<td class="tblLabels">Year Graduated</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.vocationalYear}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">School</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.vocationalSchool}</td>
	</tr>
	<tr>
		<td class="tblLabels" style="padding-left: 60px;">Address</td>
		<td class="tblValue">${employeeProfile.employeeEducationalAttainment.vocationalAddress}</td>
	</tr>
	<tr>
		<td class="tblLabels" colspan="5">Licenses (PRC, Civil Service, etc)</td>
	</tr>
	<tr>
		<td class="tblValue" colspan="5">
			<c:choose>
				<c:when test="${fn:length(employeeProfile.licenseCertificates) gt 0}">
			 		<table class="dataTable">
			 			<thead>
							<tr>
								<th class="tdLabels">Type</th>
							</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="elc" items="${employeeProfile.licenseCertificates}">
			 					<tr>
			 						<td>${elc.accreditationType}</td>
			 					</tr>
			 				</c:forEach>
			 			</tbody>
					</table>
				</c:when>
		 		<c:otherwise>
		 			No Licenses/Certified Membership to any Trade/Profession enlisted.
		 		</c:otherwise>
	 		</c:choose>
		</td>
	</tr>
	<tr>
		<td class="tblLabels" colspan="5">National Competencies (NC I or NC II)/Trainer's Methodology (TM)</td>
	</tr>
	<tr>
		<td class="tblValue" colspan="5">
			<c:choose>
				<c:when test="${fn:length(employeeProfile.nationalCompetencies) gt 0}">
			 		<table class="dataTable">
			 			<thead>
							<tr>
								<th class="tdLabels">Description</th>
							</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="enc" items="${employeeProfile.nationalCompetencies}">
			 					<tr>
			 						<td>${enc.description}</td>
			 					</tr>
			 				</c:forEach>
			 			</tbody>
					</table>
				</c:when>
		 		<c:otherwise>
		 			No National Competencies enlisted.
		 		</c:otherwise>
	 		</c:choose>
		</td>
	</tr>
	<tr>
		<td class="tblLabels" colspan="5">Trainings and Seminars Attended</td>
	</tr>
	<tr>
		<td class="tblValue" colspan="5">
			<c:choose>
				<c:when test="${fn:length(employeeProfile.seminarAttendeds) gt 0}">
			 		<table class="dataTable">
			 			<thead>
							<tr>
								<th class="tdLabels">Title of Trainings/Seminars</th>
								<th class="tdLabels">Date Issued</th>
							</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="esa" items="${employeeProfile.seminarAttendeds}">
			 					<tr>
			 						<td>${esa.courseTitle}</td>
			 						<td>${esa.seminarDate}</td>
			 					</tr>
			 				</c:forEach>
			 			</tbody>
					</table>
				</c:when>
		 		<c:otherwise>
		 			No Trainings/Seminars enlisted.
		 		</c:otherwise>
	 		</c:choose>
		</td>
	</tr>
	<tr>
		<td class="tblLabels" colspan="5">In Case of Emergency</td>
	</tr>
	<tr>
		<td class="tblValue" colspan="5">
			<c:choose>
				<c:when test="${fn:length(employeeProfile.emergencyContacts) gt 0}">
			 		<table class="dataTable">
			 			<thead>
							<tr>
								<th class="tdLabels">Full Name</th>
								<th class="tdLabels">Address</th>
								<th class="tdLabels">Telephone No.</th>
							</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="eec" items="${employeeProfile.emergencyContacts}">
			 					<tr>
			 						<td>${eec.name}</td>
			 						<td>${eec.address}</td>
			 						<td>${eec.contactNo}</td>
			 					</tr>
			 				</c:forEach>
			 			</tbody>
					</table>
				</c:when>
		 		<c:otherwise>
		 			No Emergency Contact/s enlisted.
		 		</c:otherwise>
	 		</c:choose>
		</td>
	</tr>
</table>
</div>
</body>
</html>