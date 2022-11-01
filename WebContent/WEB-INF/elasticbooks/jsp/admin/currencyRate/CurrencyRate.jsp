<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ include file="../../../../../jsp/include.jsp" %>
	<!--

		Description	: Currency Rate main page
	 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<style>
.number {
	text-align: right;
	padding-right: 10px;
}
</style>
<script type="text/javascript">
$(function () {
	$("#slctCurrency, #startDate, #endDate, #startTime, #endTime, #selectStatus").bind("keypress", function (e) {
		if (e.which == 13) {
			search();
			e.preventDefault();
		}
	});
});

function addCurrencyRate() {
	$("#divCurrencyRateForm").load(contextPath + "/admin/currencyRate/form");
	$("html, body").animate({scrollTop: $("#divCurrencyRateForm").offset().top}, 0050);
}

function editCurrencyRate() {
	var id = getCheckedId("cbCurrencyRate");
	$("#divCurrencyRateForm").load(contextPath + "/admin/currencyRate/form?pId="+id);
	$("html, body").animate({scrollTop: $("#divCurrencyRateForm").offset().top}, 0050);
}

var isSaving = false;
function saveCurrencyRate() {
	if(isSaving == false) {
		isSaving = true;
		unformatRate();//Remove formatting from rate.
		$("#btnSave").attr("disabled", "disabled");
		$("#date").removeAttr("disabled");
		doPostWithCallBack ("currencyRateFromId", "divCurrencyRateForm", function(data) {
			if(data.startsWith("saved")) {
				$("#spanMessage").html("Successfully "
					+ ($("#id").val() != 0 ? "updated " : "added ") + "currency rate.");
				$("#divCurrencyRateForm").html("");
				search();
			} else {
				$("#divCurrencyRateForm").html(data);
			}
			isSaving = false;
		});
	}
}

function isValidSearchCriteria() {
	$("#scTimeErr").text("");
	$("#scDateErr").text("");
	var dateFrom = $("#startDate").val();
	var dateTo = $("#endDate").val();
	var timeFrom = $("#startTime").val();
	var timeTo = $("#endTime").val();
	var isValid = true;
	console.log(isValid);
	if((timeFrom.trim() !== "" && dateFrom.trim() === "")
			|| (timeTo.trim() !== "" && dateTo.trim() === "")) {
		isValid = false;
		$("#scTimeErr").text("Time should be accompanied by its date search criteria.");
	}

	if((dateFrom.trim() !== "" || dateTo.trim() !== "") 
			&& (dateFrom.trim() === "" || dateTo.trim() === "")) {
		$("#scDateErr").text("Invalid date range.");
		isValid = false;
	} else if(new Date(dateFrom) > new Date(dateTo)) {
		$("#scDateErr").text("Invalid date range.");
		isValid = false;
	}
	return isValid;
}

function cancelCurrency() {
	$("#divCurrencyRateForm").html("");
	$("html, body").animate({scrollTop: $("#divCurrencyRateTbl").offset().top}, 0050);
}

function getCommonParam() {
	var currencyId = $("#slctCurrency").val();
	var dateFrom = $("#startDate").val();
	var dateTo = $("#endDate").val();
	var timeFrom = $("#startTime").val();
	var timeTo = $("#endTime").val();
	var status = $("#selectStatus").val();
	return "?currencyId="+currencyId+"&dateFrom="+dateFrom+"&dateTo="+dateTo
			+"&timeFrom="+timeFrom+"&timeTo="+timeTo+"&status="+status;
}

function search() {
	if(isValidSearchCriteria()) {
		doSearch ("divCurrencyRateTbl", "/admin/currencyRate/search"+getCommonParam()+"&pageNumber=1");
	}
}

function validateTime(elem) {
	var time = $(elem).val();
	if(time.trim() !== "") {
		var isValid = true;
		time = time.split(":");
		//Check time validity.
		if(time.length === 2) {
			var hour = time[0];
			var min = time[1];
			if(isNaN(Number(hour)) || isNaN(Number(min)) //Should be a valid number
					|| hour.length !== 2 || min.length !== 2 //Should only contain 2 characters
					|| hour >= 24 || hour < 0 
					|| min >= 60 || min < 0) {
				isValid = false;
			}
		} else {
			isValid = false;
		}
		//Display validation error if invalid, else do nothing.
		if(!isValid) {
			alert("Invalid time format. Time should be in military time format.");
			$(elem).val("");//Clear data.
		}
	}
}


</script>
</head>
<body>
	<div id="searchDiv">
		<table class="formTable">
			<tr>
				<td width="15%" class="title">Currency</td>
				<td><select id="slctCurrency" class="frmMediumSelectClass">
					<option value="-1" selected>ALL</option>
					<c:forEach var="currency" items="${currencies}">
							<option value="${currency.id}">${currency.name}</option>
					</c:forEach>
				</select>
			</tr>
			<tr>
				<td class="title">Date </td>
				<td class="tdDateFilter">
					<input type="text" id="startDate" maxlength="10" class="dateClass2" onblur="evalDate('startDate')">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('startDate')" style="cursor:pointer"
								style="float: right;"/>
					<span class="title">To</span>
					<input type="text" id="endDate" maxlength="10" class="dateClass2" onblur="evalDate('startDate')">
						<img src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('endDate')" style="cursor:pointer"
								style="float: right;"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span class="error" id="scDateErr"></span>
				</td>
			</tr>
			<tr>
				<td class="title">Time </td>
				<td class="tdDateFilter">
					<input type="text" id="startTime" maxlength="10" class="dateClass2" onblur="validateTime(this)">
					<span class="title" style="padding-left: 20px;">To</span>
					<input type="text" id="endTime" maxlength="10" class="dateClass2" onblur="validateTime(this)">
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					<span class="error" id="scTimeErr"></span>
				</td>
			</tr>
			<tr>
				<td width="15%" class="title">Status</td>
				<td><select id="selectStatus" class="frmMediumSelectClass">
					<c:forEach var="status" items="${status}">
							<option>${status}</option>
					</c:forEach>
				</select>
				<input type="button" value="Search" onclick="search();"/></td>
			</tr>
		</table>
	</div>
	<span id="spanMessage" class="message"></span>
	<div id="divCurrencyRateTbl">
		<%@ include file="CurrencyRateTable.jsp" %>
	</div>
	<div class="controls" >
		<input type="button" id="btnAddCurrencyRate" value="Add" onclick="addCurrencyRate();"></input>
	</div>
	<br>
	<br>
	<div id="divCurrencyRateForm" style="margin-top: 20px;">
	</div>
</body>
</html>