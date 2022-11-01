<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../include.jsp" %>
    <!-- 

	Description: Classification Sizes.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Classification Sizes</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type ="text/javascript">
$(function() {
	$("#btnSearchClassificationSize").click(function () {
		searchClassificationSize();
	});

    $("#btnAddClassificationSize").click(function(){
            $("#classificationSizeForm").load(contextPath + "/classificationSize/form");
            $("html, body").animate({scrollTop: $("#classificationSizeForm").offset().top}, 0050);
    });

    $("#btnEditClassificationSize").click(function(){
    	   	var cbs = document.getElementsByName("cbClassificationSize");
    	   	var id = 0;
			for (var index =0; index < cbs.length; index++) {
   				var cb = cbs[index];
   				if (cb.checked == true){
   					id = cb.id;
   					break;
   				}
   			}
   			$("#classificationSizeForm").load(contextPath + "/classificationSize/form?classificationSizeId="+id);
   			$("html, body").animate({scrollTop: $("#classificationSizeForm").offset().top}, 0050);
	});

	$("#btnDeleteClassificationSize").click(function(){
			var cbs = document.getElementsByName("cbClassificationSize");
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
			if (ctr == 0){
				alert("No item to delete. Please select.");
				searchClassificationSize ();
			}
			else{
				var confirmation = "Are you sure you want to delete " + ctr + " classification size";
				if (ctr == 1)
					confirmation = confirmation + "?";
				if (ctr > 1)
					confirmation = confirmation + "s?";
				var confirmDelete = confirm(confirmation);
				var classificationId = ($("#searchClassificationSize").val() == "all") ? -1 : 
					$("#searchClassificationSize").val();
				if (confirmDelete == true) {
					$("#classificationSizeTable").load(contextPath + "/classificationSize/delete?classificationSizeIds="+ids
							+"&classificationId="+classificationId+"&status="+$("input[@name='searchStatus']:checked").val());
					$("html, body").animate({scrollTop: $("#classificationSizeTable").offset().top}, 0050);
				} else {
	 				searchClassificationSize();
				}
			}
    });

    $("#btnSaveClassificationSize").live("click", function () {
           $(this).attr("disabled", "disabled");
           doPost ("classificationSize", "classificationSizeForm", "classificationSizeTable", 
                           "/classificationSize"+getCommonParameter()+"&pageNumber=1");
           $("html, body").animate({scrollTop: $("#classificationSizeTable").offset().top}, 0050);
   	});

   	$("#btnCancelClassificationSize").live("click", function() {
   			searchClassificationSize();
			$("#classificationSizeForm").html("");
   	});

	$("#btnPrintClassificationSize").click(function() {
		var targetUrl = contextPath + "/classificationSize/print"+getCommonParameter();
		window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
		"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	});
	
});

function getCommonParameter () {
	var classificationId = ($("#searchConcessionaireClassification").val() == "all") ? -1 : 
		$("#searchConcessionaireClassification").val();
	var size = processSearchName($("#size").val());
	return "?classificationId="+classificationId+"&size="+size;
}

function searchClassificationSize () {
	doSearch ("classificationSizeTable", "/classificationSize"+getCommonParameter()+"&pageNumber=1");
	$("html, body").animate({scrollTop: $("#classificationSizeTable").offset().top}, 0050);
}

</script>
</head>
<body>
<!-- Table for filters -->
	<div id="searchDiv">
		<table class="tableInfo">
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
				<td width="15%" class="title">Size</td>
				<td><input type="text" id="size" size="15" maxlength="5"></td>
			</tr>
		</table>
		<div align="right">
			<input type="button" value="Search" id="btnSearchClassificationSize">
		</div>
	</div>
<div id="classificationSizeTable" style="margin-top: 20px;">
<%@include file="ClassificationSizeTable.jsp" %>
</div>
<div class="controls">
		<input type="button" id="btnAddClassificationSize" value="Add" />
		<input type="button" id="btnEditClassificationSize" value="Edit" />
		<input type="button" id="btnDeleteClassificationSize" value="Delete" />
		<input type="button" id="btnPrintClassificationSize" value="Print" />
</div>
<div id ="classificationSizeForm" style="margin-top: 20px;">
</div>
</body>
</html>