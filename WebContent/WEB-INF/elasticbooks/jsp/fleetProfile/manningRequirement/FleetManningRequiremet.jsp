<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Main Fleet Manning Requirements
 -->
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
var refObjectId = "${refObjectId}";
var FORM_URL = contextPath+"/manningRequirements/form?refObjectId="+refObjectId;
var SEARCH_URL = "/manningRequirements/search?refObjectId="+refObjectId;

$(function () {
	$("#licenseId, #positionId, #numberId, #remarksId, #departmentId, #selectStatusId").bind("keypress", function (e) {
		if (e.which == 13) {
			searchManningRequirements();
			e.preventDefault();
		}
	});
});

function searchManningRequirements() {
	var param = getCommonParam()+"&pageNumber=1";
	doSearch("divManningRequirementTable", SEARCH_URL+param);
}

function getCommonParam() {
	var license = processSearchName($.trim($("#licenseId").val()));
	var position = processSearchName($.trim($("#positionId").val()));
	var number = processSearchName($.trim($("#numberId").val()));
	var remarks = processSearchName($.trim($("#remarksId").val()));
	var department = processSearchName($.trim($("#departmentId").val()));
	var status = $("#selectStatusId").val();
	return "&license="+license+"&position="+position+"&number="+number+"&remarks="+remarks+
			"&department="+department+"&status="+status;
}

function addManningRequirement() {
	$("#divManningRequirementForm").load(FORM_URL, function (){
		$("html, body").animate({
			scrollTop: $("#divManningRequirementForm").offset().top}, 0050);
	});
}

function editManningRequirement() {
	var id = getCheckedId("cbManningRequirement");
	$("#divManningRequirementForm").load(FORM_URL+"&pId="+id, function (){
		$("html, body").animate({
			scrollTop: $("#divManningRequirementForm").offset().top}, 0050);
	});
}
</script>
<title>Main Fleet Manning Requirements</title>
</head>
<body>
	<div style="width: 90%; margin: 0 auto;" >
		<div id="divSearch">
			<table class="formTable">
				<tr>
					<td width="25%" class="title">Department</td>
					<td><select id="departmentId" class="frmSelectClass">
						<option value="All">All</option>
						<c:forEach var="department" items="${departments}">
							<option value="${department}">${department}</option>
						</c:forEach>
					</select>
				</tr>
				<tr>
					<td class="title">Position</td>
					<td>
						<input type="text" id="positionId" maxLength="100" size="20" class="input"/>
					</td>
				</tr>
				<tr>
					<td class="title">License</td>
					<td>
						<input type="text" id="licenseId" maxLength="100" size="20" class="input"/>
					</td>
				</tr>
				<tr>
					<td class="title">Number</td>
					<td>
						<input type="text" id="numberId" maxLength="100" size="20" class="input"/>
					</td>
				</tr>
				<tr>
					<td class="title">Remarks</td>
					<td>
						<input type="text" id="remarksId" maxLength="100" size="20" class="input"/>
					</td>
				</tr>
				<tr>
					<td class="title">Status</td>
					<td><select id="selectStatusId" class="frmSelectClass">
						<c:forEach var="status" items="${status}">
								<option>${status}</option>
						</c:forEach>
					</select>
					<input type="button" id="btnSearchManningRequirement" value="Search" onclick="searchManningRequirements();"/>
				</tr>
			</table>
		</div>
		<span id="spanMessage" class="message"></span>
		<div id="divManningRequirementTable">
			<%@ include file="FleetManningRequiremetTable.jsp" %>
		</div>
		<div class="controls">
			<input type="button" id="btnAddManningRequirement" value="Add" onclick="addManningRequirement();"/>
			<input type="button" id="btnEditManningRequirement" value="Edit" onclick="editManningRequirement();"/>
		</div>
		<div id="divManningRequirementForm" style="margin-top: 50px;">
		</div>
	</div>
</body>
</html>