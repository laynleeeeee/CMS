<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Employee profile - educational attainment jsp page for GVCH
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
.btnPhoto {
	width: 90px;
}

#btnSaveEmployeeProfile, #btnCancelPatientProfile {
	font-weight: bold;
}

.inputName{
	border: 1px solid gray;
	width: 100%;
}

.inputYear{
	border: 1px solid gray;
	width: 100%;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	if ($("#divEPEducForm").is(":visible")) {
		$("#hdnFormPage").val(3);
	}
});

function inputYear(event, object){
	if (event.shiftKey == true) {
		event.preventDefault();
	}

	if (!((event.keyCode >= 48 && event.keyCode <= 57) || (event.keyCode >= 96 && event.keyCode <= 105)
			|| event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37 || event.keyCode == 39
			|| event.keyCode == 46 || event.keyCode == 190)) {
		event.preventDefault();
	}

	if($(object).val().indexOf('.') != -1 && event.keyCode == 190) {
		event.preventDefault();
	}
}
</script>
<title>Employee Education Record Form</title>
</head>
<body>
	<table class="formTable" style="margin: 12px;">
		<tr>
			<td class="tblLabels2">Elementary</td>
			<td class="tblLabels2" colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.elementarySchool" cssClass="inputSmall" />
			</td>
			<td class="tblLabels2">Year Graduated</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.elementaryYear"
						cssClass="inputSmall" onkeydown="inputYear(event, this)" maxlength="4"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.elementarySchool" cssClass="error"/>
			</td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.elementaryYear" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School Address</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.elementaryAddress" cssClass="inputSmall"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.elementaryAddress" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td class="tblLabels2">Secondary</td>
			<td class="tblLabels2" colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.hsSchool" cssClass="inputSmall" />
			</td>
			<td class="tblLabels2">Year Graduated</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.hsYear"
						cssClass="inputSmall" onkeydown="inputYear(event, this)" maxlength="4"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.hsSchool" cssClass="error"/>
			</td>
			<td>
				<form:errors path="employeeEducationalAttainment.hsYear" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School Address</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.hsAddress" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.hsAddress" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td class="tblLabels2">Tertiary</td>
			<td class="tblLabels2" colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">Course</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.collegeCourse" cssClass="inputSmall" />
			</td>
			<td class="tblLabels2">Year Graduated</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.collegeYear"
						cssClass="inputSmall" onkeydown="inputYear(event, this)" maxlength="4"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.collegeCourse" cssClass="error"/>
			</td>
			<td></td>
			<td>
				<form:errors path="employeeEducationalAttainment.collegeYear" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.collegeSchool" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.collegeSchool" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School Address</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.collegeAddress" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.collegeAddress" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td class="tblLabels2">Post-Graduate</td>
			<td class="tblLabels2" colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">Course</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.postGradCourse" cssClass="inputSmall" />
			</td>
			<td class="tblLabels2">Year Graduated</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.postGradYear"
						cssClass="inputSmall" onkeydown="inputYear(event, this)" maxlength="4"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.postGradCourse" cssClass="error"/>
			</td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.postGradYear" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.postGradSchool" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.postGradSchool" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School Address</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.postGradAddress" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.postGradAddress" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td class="tblLabels2">Vocational</td>
			<td class="tblLabels2" colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">Course</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.vocationalCourse" cssClass="inputSmall" />
			</td>
			<td class="tblLabels2">Year Graduated</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.vocationalYear"
						cssClass="inputSmall" onkeydown="inputYear(event, this)" maxlength="4"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.vocationalCourse" cssClass="error"/>
			</td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.vocationalYear" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.vocationalSchool" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.vocationalSchool" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2">School Address</td>
			<td class="tblLabels2">
				<form:input path="employeeEducationalAttainment.vocationalAddress" cssClass="inputSmall" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels2"></td>
			<td class="tblLabels2">
				<form:errors path="employeeEducationalAttainment.vocationalAddress" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td class="tblLabels2">Educational Attainment</td>
			<td class="tblLabels">
				<form:select path="employeeEducationalAttainment.employeeEducationalAttainmentTypeId"
						id="educationalAttainmentTypeId" cssClass="frmSmallSelectClass"
						items="${educAttTypes}" itemLabel="name" itemValue="id">
				</form:select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tblLabels">
				<form:errors path="employeeEducationalAttainment.employeeEducationalAttainmentTypeId" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td colspan="5">
				<fieldset class="frmField_set" style="margin-left: 14px;">
				<legend>Skills</legend>
					<table>
						<tr>
							<td>
								<form:textarea path="employeeEducationalAttainment.employeeSkills" cssClass="addressClass" style="width: 720px; border: none;" />
							</td>
						</tr>
						<tr>
							<td>
								<form:errors path="employeeEducationalAttainment.employeeSkills" cssClass="error"/>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
	<table class="frmField_set">
		<tr>
			<td style="padding-top: 5px; text-align: right;" colspan="4">
				<input type="button" id="btnLoadProfile" value="Previous"
					onclick="loadEPBasicInfoForm();" />
				<input type="button" id="btnSaveEmployeeEduc" value="Next" 
					onclick="saveEmployeeProf(3);" />
			</td>
		</tr>
	</table>
</body>
</html>