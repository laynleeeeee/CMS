<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: Concessionaire listing. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Concessionaire</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
$(function() {	
	$("#selectUserCompanies").change(function() {
		searchConcessionaire();
	});
	
	$("#btnSearchConcessionaire").click(function () {
		searchConcessionaire();
	});
	
	$("#btnAddConcessionaire").click(function() {
		var companyId = $("#selectUserCompanies").length > 0 ? 
				$("#selectUserCompanies").val() : $("#hdnUserCompanyId").val();  
		if (companyId == "all") {
			alert("Select a company from the list.");
		} else {
			$("#concessionaireForm").load(contextPath + "/concessionaireList/form");
			$("html, body").animate({scrollTop: $("#concessionaireForm").offset().top}, 0050);
		}
	});
	
	$("#btnEditConcessionaire").click(function() {
		var cbs = document.getElementsByName("cbConcessionaire");
		var id = 0;
		for (var index =0; index < cbs.length; index++) {
			var cb = cbs[index];
			if (cb.checked == true){
				id = cb.id;
				break;
			}
		}
		$("#companyId").val($("hdnConcessionaireCompanyId").val());
		$("#concessionaireForm").load(contextPath + "/concessionaireList/form?concessionaireId="+id);
		$("html, body").animate({scrollTop: $("#concessionaireForm").offset().top}, 0050);
	});
	
	$("#btnDeleteConcessionaire").click(function() {
		var cbs = document.getElementsByName("cbConcessionaire");
		var ids = null;
		var ctr = 0;
		for (var index =0; index < cbs.length; index++) {
			var cb = cbs[index];
			if (cb.checked == true){
				if (ids == null){ 
					ids = "" + cb.id;
				} else {
					ids = ids + ";" + cb.id;				
				} 
				ctr++;
			}
		}
		var confirmation = "Are you sure you want to delete " + ctr + " concessionaire";
		if (ctr == 1) 
			confirmation = confirmation + "?";
		if (ctr > 1)
			confirmation = confirmation + "s?";
		
		var confirmDelete = confirm(confirmation);
		var classificationId = ($("#searchConcessionaireClassification").val() == "all") ? -1 : 
			$("#searchConcessionaireClassification").val();
		if (confirmDelete == true) {
			var companyId = $("#selectUserCompanies").length > 0 ? 
					$("#selectUserCompanies").val() : $("#hdnUserCompanyId").val();
			if (companyId == "all")
				companyId = -1;
			$("#concessionaireTable").load(contextPath + "/concessionaireList/delete?concessionaireIds="+ids
					+"&classificationId="+classificationId+"&status="+$("input[@name='searchStatus']:checked").val()
					+"&companyIdParam="+companyId);
			$("html, body").animate({scrollTop: $("#concessionaireTable").offset().top}, 0050);
		} else {
			searchConcessionaire();
		}
	});
	
	$("#btnPrintConcessionaire").click(function() {
		var targetUrl = contextPath + "/concessionaireList/print"+getCommonParameter();
		window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
		"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	});
	
	$("#btnSaveConcessionaire").live("click", function () {
		$("#btnSaveConcessionaire").attr("disabled", "disabled");
		doPost ("concessionaire", "concessionaireForm", "concessionaireTable", "/concessionaireList"+
				getCommonParameter()+"&pageNumber=1");
 		$("html, body").animate({scrollTop: $("#concessionaireTable").offset().top}, 0050);
	});
	
	$("#btnCancelConcessionaire").live("click", function() {
		$("#concessionaireForm").html("");
		searchConcessionaire();
	});
});

function getCommonParameter () {
	var name = processSearchName($("#name").val());
	var address = processSearchName($("#address").val());
	var classificationId = ($("#searchConcessionaireClassification").val() == "all") ? -1 : 
		$("#searchConcessionaireClassification").val();
	var searchStatus = $("input[@name='searchStatus']:checked").val();
	var companyId = $("#selectUserCompanies").length > 0 ? 
			$("#selectUserCompanies").val() : $("#hdnUserCompanyId").val();
	if (companyId == "all") 
		companyId = -1;
	else 
		$("#companyId").val(companyId);
	
	return "?name="+name+"&address="+address+"&classificationId="+classificationId+
			"&status="+searchStatus+"&companyIdParam="+companyId;
}


function searchConcessionaire () {
	doSearch ("concessionaireTable", "/concessionaireList"+getCommonParameter()+"&pageNumber=1");
	$("html, body").animate({scrollTop: $("#concessionaireTable").offset().top}, 0050);
}
</script>
</head>
<body>
	<div id="userCompany">
		<%@ include file="../../UserCompany.jsp" %>
	</div>
	<!-- Table for filters -->
	<div id="searchDiv">
		<table class="tableInfo">
			<tr>
				<td width="15%" class="title">Name</td>
				<td><input type="text" id="name"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Address</td>
				<td><input type="text" id="address"></td>
			</tr>
			<tr>
				<td width="15%" class="title">Classification</td>
				<td>		
			 		<select id="searchConcessionaireClassification" >
						<option value="all" >All</option>
						<c:forEach var="classification" items="${concessionaireClassifications}">
							<option value="${classification.id}">${classification.name}</option>
						</c:forEach>
					</select>	
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td>
					<div class="divRadioBorder">
					<c:forEach items="${searchConcessionaireStatus}" var="status">
						<c:choose>
							<c:when test="${status == 'Active'}">
								<input type="radio" name="searchStatus" value="${status}" checked="checked"/>${status}
							</c:when>
							<c:otherwise>
								<input type="radio" name="searchStatus" value="${status}"/>${status}
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</div>
				</td>
			</tr>
		</table>
		<div align="right">
			<input type="button" value="Search" id="btnSearchConcessionaire">
		</div>
	</div>
	
	<!--Table for the actual concessionaire data -->
	
	<div id="concessionaireTable">
		<%@ include file="ConcessionaireTable.jsp" %>
	</div>
	
	<div class="controls">
		<input type="button" id="btnAddConcessionaire" value="Add" />
		<input type="button" id="btnEditConcessionaire" value="Edit" />
		<input type="button" id="btnDeleteConcessionaire" value="Delete" />
		<input type="button" id="btnPrintConcessionaire" value="Print" />
	</div>
	
	<div id="concessionaireForm" style="margin-top: 20px;">
	</div>
</body>
</html>