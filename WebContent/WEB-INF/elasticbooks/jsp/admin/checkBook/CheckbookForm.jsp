<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
 <!--

	Description: Checkbook form main jsp page.
  -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	numericInputOnly();
	setTemplateImage();
});

function imageExists(url, callback) {
	var templateImg = document.getElementById('checkLayout');
	templateImg.onload = function() { callback(true); };
	templateImg.onerror = function() { callback(false); };
	templateImg.src = url;
}

function setTemplateImage() {
	var el = document.getElementById('checkbookTemplateId');
	var templateName = el.options[el.selectedIndex].innerHTML;
	var imageUrl = "${pageContext.request.contextPath}/images/checkTemplate/"
			+templateName+".jpg";
	imageExists(imageUrl, function(isExists) {
		if(isExists == false){
			$("#checkLayout").attr("src", "${pageContext.request.contextPath}/images/NotAvailable.jpg");
		}
	});

	if(templateName == ""){
		$("#checkLayout").hide();
	} else{
		$("#checkLayout").show();
	}
}

</script>
</head>
<body>
<div class="formDiv">
	<form:form method="POST" commandName="checkbook" id="checkbookFormId">
		<div class="modFormLabel">Checkbook Account</div>
		<br>
		<div class="modForm">
		<fieldset class="frmField_set">
				<legend>Checkbook Info</legend>
				<table class="formTable" >
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<tr>
						<td class="labels">Active: </td>
						<td class="value">
							<form:checkbox path="active"/>
						</td>
					</tr>
					<tr >
						<td colspan="2">
							<font color="red">
								<form:errors path="active" cssClass="error"/>
							</font>
						</td>
					</tr>
					<tr>
						<td class="labels">* Bank Account Name</td>
						<td class="value">
							<form:select path="bankAccountId" id="bankAccountId" class="frmSelectClass">
								<form:option value="0" label="Select bank account"/>
								<form:options items="${bankAccounts}" itemLabel="numberAndName" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr >
						<td></td>
						<td colspan="2">
							<font color="red">
								<form:errors path="bankAccountId" cssClass="error"/>
							</font>
						</td>
					</tr>
					<tr>
					<tr>
						<td class="labels" valign="top">* Checkbook Name</td>
						<td class="value"><form:input path="name" maxlength="100" id="checkbookName" class="input"/></td>
					</tr>
					<tr >
						<td></td>
						<td >
							<font color="red">
								<form:errors path="name" cssClass="error"/>
							</font>
						</td>
					</tr>
					<tr>
						<td class="labels" valign="top">* Check No. From</td>
						<td class="value"><form:input path="checkbookNoFrom" id="checkbookNoFrom" maxlength="20" class="input"/></td>
					</tr>
					<tr >
						<td></td>
						<td >
							<font color="red">
								<form:errors path="checkbookNoFrom" cssClass="error"/>
							</font>
						</td>
					</tr>
					<tr>
						<td class="labels" valign="top">* Check No. To</td>
						<td class="value"><form:input path="checkbookNoTo" id="checkbookNoTo" maxlength="20" class="input"/></td>
					</tr>
					<tr >
						<td></td>
						<td >
							<font color="red">
								<form:errors path="checkbookNoTo" cssClass="error"/>
							</font>
						</td>
					</tr>
					<tr>
						<td class="labels">Checkbook Template</td>
						<td class="value">
							<form:select path="checkbookTemplateId" id="checkbookTemplateId" class="frmSelectClass"
							onchange="setTemplateImage();">
								<form:option value=""/>
								<form:options items="${checkbookTemplates}" itemLabel="name" itemValue="id"/>
							</form:select>
						</td>
					</tr>
					<tr >
						<td></td>
						<td >
							<font color="red">
								<form:errors path="checkbookTemplateId" cssClass="error"/>
							</font>
						</td>
					</tr>
					<tr>
						<td></td>
						<td class="value">
							<img id="checkLayout" style="height: 130px;">
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<table class="buttonClss">
				<tr>
					<td align="right" >
						<input type="button" id="btnSaveCheckbook" value="${checkbook.id eq 0 ? 'Save' : 'Update'}" onclick="saveCheckbook();"/>
						<input type="button" id="btnCancelCheckbook" value="Cancel" onclick="cancelCheckbook();"/>
					</td>
				</tr>
			</table>
		</div>
		<hr class="thin"/>
	</form:form>
</div>
</body>
</html>