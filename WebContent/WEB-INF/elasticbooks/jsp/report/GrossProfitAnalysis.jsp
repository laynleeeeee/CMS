 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!-- 

	Description: Gross profit analysis main page
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var newDate = parseServerDate(new Date);
	$("#dateFrom").val(newDate);
	$("#dateTo").val(newDate);
});

function generateGPAnalysis() {
	var companyId = $("#companyId option:selected").val();
	var divisionId = $("#divisionId").val();
	var itemCategoryId = $("#itemCategoryId").val();
	var itemCategory = $("#itemCategory").val();
	var strDateFrom =  new Date($("#dateFrom").val());
	var strDateTo = new Date($("#dateTo").val());
	var formatType = $("#formatType").val();
	var hasFilterError = false;

	if($.trim($("#dateFrom").val()) == "" || $.trim($("#dateTo").val()) == "") {
		$('#reportGPAnalysis').attr('src',"");
		$("#spanDateError").text("Date from and to are required fields.");
		hasFilterError = true;
	} else if (strDateFrom > strDateTo) {
		$('#reportGPAnalysis').attr('src',"");
		$("#spanDateError").text("Invalid date range.");
		hasFilterError = true;
	}else if (strDateFrom == 'Invalid Date' || strDateTo == 'Invalid Date') {
		$('#strDateTo').attr('src',"");
		$("#spanDateError").text("Invalid date.");
		hasFilterError = true;
	}

	if (itemCategoryId == "" && itemCategory !="") {
		$('#reportGPAnalysis').attr('src',"");
		$("#spanGPAMsg").text("Invalid item category.");
		hasFilterError = true;
	}
	if (!hasFilterError) {
		var url = contextPath + "/grossProfitAnalysis/generate?companyId="+companyId+"&divisionId="+divisionId
				+"&itemCategoryId="+itemCategoryId+"&dateFrom="+$("#dateFrom").val()+"&dateTo="+$("#dateTo").val()+"&formatType="+formatType;
		$('#reportGPAnalysis').attr('src',url);
		$('#reportGPAnalysis').load();
		$("#spanDateError").text("");
	}
}

function loadItemCategories() {
	$("#spanGPAMsg").text("");
	var divisionId = $("#divisionId").val();
	var name = processSearchName($.trim($("#itemCategory").val()));
	var uri = contextPath+"/getItemCategories?name="+name+"&companyId=-1&divisionId="+divisionId;
	$("#itemCategory").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$("#itemCategoryId").val(ui.item.id);
			$(this).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.name);
						$("#itemCategoryId").val(ui.item.id);
					}
				},
				error : function(error) {
					$("#itemCategoryId").val("");
					$("#itemCategory").val("");
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name + "</a>" )
			.appendTo( ul );
	};
}

function getItemCategory (elem) {
	var name = $.trim($("#itemCategory").val());
	var companyId = $("#companyId").val();
	if (name != "") {
		$.ajax({
			url: contextPath+"/getItemCategories/perCategory?term="+processSearchName(name)+ 
					"&companyId=-1",
			success: function (item) {
				if (item != null) {
					$(elem).val(item.name);
					$("#itemCategoryId").val(item.id);
					$("#itemCategory").val(item.name);
					$("#spanGPAMsg").text("");
				} else {
					$("#spanGPAMsg").text("Invalid item category.");
					$("#itemCategoryId").val("");
				}
			},
			error : function(error) {
				$("#spanGPAMsg").text("Invalid item category.");
				$("#itemCategory").val("");
				$("#itemCategoryId").val("");
			},
			dataType: "json"
		});
	} else {
		$("#itemCategoryId").val("");
	}
}
</script>
</head>
<body>
<table border=0>
	<tr>
		<td class="title2">Company</td>
		<td>
			<select id="companyId" class="frmSelectClass">
				<c:forEach var="company" items="${companies}">
					<option value="${company.id}">${company.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
	<tr>
		<td class="title2">Division</td>
		<td>
			<select id="divisionId" class="frmSelectClass">
			<option selected='selected' value=-1>ALL</option>
				<c:forEach var="division" items="${divisions}">
					<option value="${division.id}">${division.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Item Category</td>
		<td>
			<input id="itemCategory" class="input" onkeydown="loadItemCategories();" 
			onkeyup="loadItemCategories();" onblur ="getItemCategory(this);"/>
			<input type="hidden" id="itemCategoryId">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanGPAMsg" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Date</td>
		<td class="tdDateFilter">
			<input type="text" id="dateFrom" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateFrom')" style="cursor:pointer"
						style="float: right;"/>
			
			<span class="title2">To</span>
			<input type="text" id="dateTo" maxlength="10" class="dateClass2" onblur="evalDate(this.id, false);">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('dateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanDateError" class="error" ></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Format:</td>
		<td class="value">
			<select id="formatType" class="frmSelectClass">
					<option value="pdf">PDF</option>
					<option value="xls">EXCEL</option>
			</select>
		</td>
	</tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateGPAnalysis();"/></td>
	</tr>
</table>
<div>
	<iframe id="reportGPAnalysis"></iframe>
</div>
<hr class="thin2">
</body>
</html>