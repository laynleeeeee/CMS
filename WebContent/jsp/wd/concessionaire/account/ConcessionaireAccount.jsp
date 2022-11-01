<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp" %>
<!-- 

	Description: Displays the detailed accounts and profile for a certain concessionaire
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script type="text/javascript">
var baseUrl = "/concessionaire/${concessionaireId}/account";

$(document).ready(function() {
	$("#cbShowHistory").attr("checked", false);
});

$(function() {
	$("#cubicMeter").live("keyup", function(e) {
		checkAndSetDecimal("cubicMeter");
		if(e.which == 32){ //32 is the ascii code of space
			$("#cubicMeter").val("0.0");
		}
	});

	$("#cubicMeter").live("keydown", function(e) {
		checkAndSetDecimal("cubicMeter");
		if(e.which == 32){ //32 is the ascii code of space
			$("#cubicMeter").val("0.0");
		}
	});

	$("#btnSearchConcessionaireAcct").click(function () {
		searchConcessionaireAcct();
	});

	$("#btnAddConcessionaireAcct").click(function(){
		$("#concessionaireAcctForm").load(contextPath +baseUrl+"/form");
		$("html, body").animate({scrollTop: $("#concessionaireAcctForm").offset().top}, 0050);
	});

	$("#btnEditConcessionaireAcct").click(function() {
		var cbs = document.getElementsByName("cbConcessionaireAcct");
		var id = 0;
		for (var index = 0; index <cbs.length; index++) {
			var cb = cbs[index];
			if (cb.checked == true) {
				id = cb.id;
				break;
			}
		}
		var url = contextPath + baseUrl + "/form?concessionaireAcctId="+id;
		$("#concessionaireAcctForm").load(url);
		$("html, body").animate({scrollTop: $("#concessionaireAcctForm").offset().top}, 0050);
	});

	$("#btnDeleteConcessionaireAcct").click(function() {
		var cbs = document.getElementsByName("cbConcessionaireAcct");
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
		var confirmation = "Are you sure you want to delete " + ctr + " account";
		if (ctr == 1) 

			confirmation = confirmation + "?";
		if (ctr > 1)

			confirmation = confirmation + "s?";

		var confirmDelete = confirm(confirmation);
		if (confirmDelete == true) {
			var url = contextPath + baseUrl + "/delete?concessionaireAcctIds="+ids;
			$("#concessionaireAcctTable").load(url);
			$("html, body").animate({scrollTop: $("#concessionaireAcctTable").offset().top}, 0050);
		} else {
			searchConcessionaireAcct();
		}
	});

	$("#btnPrintConcessionaireAcct").click(function () {
		var targetUrl = contextPath + baseUrl + "/print"+getCommonParam();
		window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
		"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
	});
	
	

	$("#btnSaveConcessionaireAcct").live("click", function () {
		$(this).attr("disabled", "disabled");
		if ($("#cubicMeter").val()==""){
			$("#cubicMeter").val("0.0");
		}else if ($("#cubicMeter").val()=="."){
			$("#cubicMeter").val("0.0");
		}
		var url = contextPath + baseUrl + getCommonParam() + "&pageNumber=1";
		doPostWithCallBack ("concessionaireAcct", "concessionaireAcctForm", function (data) {
			if (data == "saved") {
				$("#concessionaireAcctForm").html("");
				$("#concessionaireAcctTable").load(url);
			} else {
				$("#concessionaireAcctForm").html(data);
				if ($("#cbOverride").is(":checked")) 
					$("#trAmount").show();
				else
					$("#trAmount").hide();
			}
		});
		$("html, body").animate({scrollTop: $("#concessionaireAcctTable").offset().top}, 0050);
	});

	$("#btnCancelConcessionaireAcct").live("click", function() {
		$("#concessionaireAcctForm").html("");
		searchConcessionaireAcct();
	});
});

function getCommonParam() {
	var wbNumber = processSearchName($("#wbNumber").val());
	var startDate = $("#dateFrom").val();
	var endDate = $("#dateTo").val();
	var startDueDate = $("#dueDateFrom").val();
	var endDueDate = $("#dueDateTo").val();

	return "?wbNumber="+wbNumber+"&strStartDate="+startDate+"&strEndDate="+
		endDate+"&strStartDueDate="+startDueDate+"&strEndDueDate="+endDueDate;
}

function searchConcessionaireAcct() {
	var param = getCommonParam() + "&pageNumber=1";
	doSearch ("concessionaireAcctTable", baseUrl + param);
	$("html, body").animate({scrollTop: $("#concessionaireAcctTable").offset().top}, 0050);
}

function searchPaymentReport() {
	doSearch ("paymentReportTable", baseUrl + "/paymentTable?pageNumber=1");
	$("html, body").animate({scrollTop: $("#paymentReportTable").offset().top}, 0050);
}

function reloadPaymentReportTable(checkbox) {
	var url = contextPath + baseUrl + "/paymentTable?pageNumber=1";
	if (checkbox.checked)
		$("#paymentReportTable").load(url);
	else
		$("#paymentReportTable").html("");
}
</script>
</head>
<body>
	<div>
	<%@ include file ="ConcessionaireAcctProfile.jsp" %>
	</div>
	<div id="searchDiv">
		<table class="tableInfo">
			<tr>
				<td width="13%" class="title">Water bill number</td>
				<td><input type="text" id="wbNumber" size="10"></td>
			</tr>
			<tr>
				<td class="title">Date </td>
				<td class="tdDateFilter">
					<input type="text" id="dateFrom" maxlength="10" class="dateClass">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
								style="float: right;"/>
					<span>To</span>
					<input type="text" id="dateTo" maxlength="10" class="dateClass">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
								style="float: right;"/>
				</td>
			</tr>
			<tr>
				<td class="title">Due Date </td>
				<td class="tdDateFilter">
					<input type="text" id="dueDateFrom" maxlength="10" class="dateClass">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDateFrom')" style="cursor:pointer"
								style="float: right;"/>
					<span>To</span>
					<input type="text" id="dueDateTo" maxlength="10" class="dateClass">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('dueDateTo')" style="cursor:pointer"
								style="float: right;"/>	
				</td>
			</tr>
			<tr>
				<td colspan="2" align="right">
				<input type="button" id="btnSearchConcessionaireAcct" value="Search"></td>
			</tr>
			<tr>
				<td colspan="2"><hr></td>
			</tr>
		</table>
	</div>
	<div id="paymentReportControl">
		<input type="checkbox" checked="checked" id="cbShowHistory" onclick="reloadPaymentReportTable(this);"/>
		<span class="labelCheckbox">Show Payment History </span>
	</div>
	<div id="paymentReportTable">
	</div>
	<div id="concessionaireAcctTable">
		<%@ include file ="ConcessionaireAcctTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddConcessionaireAcct" value="Add"></input>
		<input type="button" id="btnEditConcessionaireAcct" value="Edit"></input>
		<input type="button" id="btnDeleteConcessionaireAcct" value="Delete"></input>
		<input type="button" id="btnPrintConcessionaireAcct" value="Print"></input>
	</div>
	<div id="concessionaireAcctForm" style="margin-top: 20px;">
	</div>
</body>
</html>