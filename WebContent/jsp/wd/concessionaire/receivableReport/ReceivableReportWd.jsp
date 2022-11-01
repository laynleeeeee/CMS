<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ include file="../../../include.jsp"%>
<!--

	Description: Receivable report for water district.
 -->
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		if ($.browser.mozilla)
			$(".dateClass").css("width", "90px");
		if ($.browser.webkit)
			$(".tdDateFilter").css("padding-left", "4px");
	});

$(function() {
		$("#btnSearchReport").click(function(){
			searchReceivableReportWd();
		});

		$("#btnPrintReport").click(function () {
			var targetUrl = contextPath + "/receivableReportWd/print" + getCommonParameter();
			window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
			"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
		});
});

	function getCommonParameter(){
		var name = processSearchName($("#name").val());
		var address = processSearchName($("#address").val());
		var startDate = $("#dateFrom").val();
		var endDate = $("#dateTo").val();
		var startDueDate = $("#dueDateFrom").val();
		var endDueDate = $("#dueDateTo").val();

		return "?name="+name+"&address="+address+"&strStartDate="+startDate+
		"&strEndDate="+endDate+"&strStartDueDate="+startDueDate+"&strEndDueDate="+endDueDate;
	}

	function searchReceivableReportWd() {
		var param = getCommonParameter() + "&pageNumber=1";
		var url = "/receivableReportWd"+ param;
		doSearch ("receivableReportTableWd", url);
		$("html, body").animate({scrollTop: $("#receivableReportTableWd").offset().top}, 0050);
}
</script>
</head>
<body>
	<div id="searchDiv">
		<table class="tableInfo">
			<tr >
				<td width="15%" class="title">Concessionaire Name</td>
				<td><input type="text" id="name"></td>
			</tr>
			<tr>
				<td class="title">Address</td>
				<td><input type="text" id="address"></td>
			</tr>
			<tr>
				<td class="title">Date</td>
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
				<td class="title">Due Date</td>
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
		</table>
		<div align="right">
			<input type="button" value="Search" id="btnSearchReport">
		</div>
	</div>

 <div id="receivableReportTableWd">
	<%@ include file="ReceivableReportTableWd.jsp" %>
</div>

<div class="controls">
	<input type="button" id="btnPrintReport" value="Print" />
	</div>
</body>
</html>