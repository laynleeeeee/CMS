<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../../jsp/include.jsp"%>

    <!-- Description: Delivery receipt receiving form -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"href="${pageContext.request.contextPath}/CMS/css/ebForm.css"media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<style type="text/css">
.btnValue {
	text-align: right;
	padding-right: 100px;
	font-size: small;
}
#popupTbl {
	margin-top: 20px;
}
#popupTbl tr td:last-child {
	padding-right: 80px;
}
/* Override edit form styles */
#editForm {
	width: auto;
	margin-left:0;
}
</style>
<script type="text/javascript">
var isSaving = false;
var formId = "${formId}";
$(document).ready(function() {
	$("#hdnFormId").val(formId);
});
</script>
</head>
<body>
<div>
	<form:form method="POST" commandName="apPayment" id="apPaymentId">
		<form:hidden path="id" id="hdnFormId"/>
		<div class="modFormLabel">Clearing Details</div>
		<table id="popupTbl">
			<tr>
				<td class="labels">* Clearing Date</td>
				<td class="value">
					<form:input path="dateCleared" id="dateCleared" onblur="evalDate('dateCleared')"
						style="width: 120px;" class="dateClass2" /> <img id="imgDate"
						src="${pageContext.request.contextPath}/images/cal.gif"
						onclick="javascript:NewCssCal('dateCleared')" style="cursor: pointer"
						style="float: right;" />
				</td>
			</tr>
			<tr>
				<td class="labels"></td>
				<td class="value">
					<form:errors path="dateCleared" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td class="labels"></td>
				<td class="btnValue">
					<input type="button" id="btnSave" value="Save" onclick="saveStatusLogs();" />
				</td>
			</tr>
		</table>
	</form:form>
</div>
</body>
</html>