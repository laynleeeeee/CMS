<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Journal Voucher Register Search page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript">
$(document).ready (function () {
	loadDivisions();
});

function generateJournalVouchers() {
	var companyId = $("#companyId").val();
	var glDateFrom = new Date($("#glDateFrom").val());
	var glDateTo = new Date($("#glDateTo").val());
	var status = $("#statusId").val();
	var createdById = null;
	var updatedById = null;
	var formatType = $("#formatType").val();
	var hasFilterError = false;

	if($("#createdById").val() != ""){
		createdById = $("#createdById").val();
	} else {
		createdById = -1;
	}
	if($("#updatedById").val() != ""){
		updatedById = $("#updatedById").val();
	} else {
		updatedById = -1;
	}

	if (companyId == -1) {
		$("#spanCompanyError").text("Company is required.");
		hasFilterError = true;
		$('#reportJournalVoucher').attr('src',"");
	}

	if ($.trim($("#glDateFrom").val()) == "" || $.trim($("#glDateTo").val()) == "") {
		$("#spanDateError").text("Date from and to are required fields.");
		hasFilterError = true;
		$('#reportJournalVoucher').attr('src',"");
	} else if (glDateFrom > glDateTo) {
		$("#spanDateError").text("Invalid date range.");
		$('#reportJournalVoucher').attr('src',"");
		hasFilterError = true;
	} else if (glDateFrom == 'Invalid Date' || glDateTo == 'Invalid Date') {
		$('#reportJournalVoucher').attr('src',"");
		$("#spanDateError").text("Invalid date.");
		hasFilterError = true;
	}

	if (!hasFilterError) {
		var url = contextPath + "/journalVoucherRegister/generate?companyId="+companyId+
					"&divisionId="+$("#slctDivisionId").val()+"&fromGLDate="+$("#glDateFrom").val()+
					"&toGLDate="+$("#glDateTo").val()+"&status="+status+"&createdBy="+createdById+
					"&updatedBy="+updatedById+"&formatType="+formatType;
		$('#reportJournalVoucher').attr('src',url);
		$('#reportJournalVoucher').load();
		$("#spanDateError").text("");
		$("#spanCompanyError").text("");
	}
}

function loadDivisions() {
	var companyId = $("#companyId").val();
	var uri = contextPath+"/getDivisions/byCompany?companyId="
			+companyId+"&isMainLevelOnly=true";
	$("#slctDivisionId").empty();
	var optionParser =  {
		getValue: function (rowObject) {
			return rowObject["id"];
		},
		getLabel: function (rowObject) {
			return rowObject["number"] + " " + rowObject["name"];
		}
	};
	postHandler = {
			doPost: function(data) {
				clearValidationDP();
			}
	};
	loadPopulate (uri, true, null, "slctDivisionId", optionParser, postHandler);
}

function showCreatedBy($txtbox) {
	$("#spanCreateErrMsg").text("");
	var uri = contextPath + "/getUsers";
	$($txtbox).autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#createdById").val(ui.item.id);
			$(this).val(ui.item.firstName + " " +ui.item.lastName);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#createdById").val(ui.item.id);
						$(this).val(ui.item.name);
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.firstName + " " +item.lastName+"</a>" )
			.appendTo( ul );
	};
}

function getCreatedBy() {
	var createdBy = $.trim($("#createdBy").val());
	if (createdBy != "") {
		$.ajax ({
			url: contextPath + "/getUsers/byExactName?name="+processSearchName(createdBy),
			success : function(create) {
				if (create != null) {
					$("#createdById").val(create.id);
					$("#createdBy").val(create.firstName + " " + create.lastName);
					$("#spanCreateErrMsg").text("");
				} else {
					$("#spanCreateErrMsg").text("Invalid name created by.");
					$("#createdById").val("");
				}
			},
			error : function(error) {
				$("#spanCreateErrMsg").text("Invalid name created by.");
				$("#createdById").val("");
				
			},
			dataType: "json"
		});
	} else {
		$("#createdById").val("");
	}
}

function showUpdatedBy($txtbox) {
	$("#spanUpdateErrMsg").text("");
	var uri = contextPath + "/getUsers";
	$($txtbox).autocomplete({
		source: uri,
		select: function(event, ui) {
			$("#updatedById").val(ui.item.id);
			$(this).val(ui.item.firstName + " " +ui.item.lastName);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$("#updatedById").val(ui.item.id);
						$(this).val(ui.item.firstName + " " +ui.item.lastName);
					}
				},
				dataType: "json"
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.firstName + " " +item.lastName+"</a>" )
			.appendTo( ul );
	};
}

function getUpdatedBy() {
	var updatedBy = $.trim($("#updatedBy").val());
	if (updatedBy != "") {
		$.ajax ({
			url: contextPath + "/getUsers/byExactName?name="+processSearchName(updatedBy),
			success : function(upt) {
				if (upt != null) {
					$("#updatedById").val(upt.id);
					$("#updatedBy").val(upt.firstName + " " + upt.lastName);
					$("#spanPreparedErrMsg").text("");
				} else {
					$("#spanUpdateErrMsg").text("Invalid name updated by.");
					$("#updatedById").val("");
				}
			},
			error : function(error) {
				$("#spanUpdateErrMsg").text("Invalid name updated by.");
				$("#updatedById").val("");
			},
			dataType: "json"
		});
	} else {
		$("#updatedById").val("");
	}
}

function clearValidationDP() {
	var companyId = $("#companyId").val();
	if (companyId != -1) {
		$("#spanCompanyError").text("");
	}
}
</script>
</head>
<body>
<table>
	<tr>
		<td>
			<br/>
		</td>
	</tr>
	<tr>
		<td class="title2">Company </td>
		<td><select id="companyId" class="frmSelectClass" onchange="loadDivisions();">
			<option selected='selected' value=-1>Please select a company</option>
			<c:forEach var="company" items="${companies}">
				<option value="${company.id}">${company.name}</option>
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<span id="spanCompanyError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Division </td>
		<td class="value">
			<select id="slctDivisionId" class="frmSelectClass"></select>
		</td>
	</tr>
	<tr>
		<td class="title2"></td>
		<td class="value">
			<span id="spanDivisionError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">GL Date </td>
		<td class="tdDateFilter">
			<input type="text" id="glDateFrom" maxlength="10" class="dateClass2" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateFrom')" style="cursor:pointer"
						style="float: right;"/>
			<span style="font-size: small;">To</span>
			<input type="text" id="glDateTo" maxlength="10" class="dateClass2" value="${currentDate}">
				<img src="${pageContext.request.contextPath}/images/cal.gif"
					onclick="javascript:NewCssCal('glDateTo')" style="cursor:pointer"
						style="float: right;"/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td>
			<span id="spanDateError" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Status </td>
		<td><select id="statusId" class="frmSelectClass">
				<option selected='selected' value=-1>ALL</option>
				<c:forEach var="status" items="${formStatuses}">
					<option value="${status.id}">${status.description}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="title2">Created By</td>
		<td>
			<input id="createdBy" class="input" onkeydown="showCreatedBy(this);" 
				onkeyup="showCreatedBy(this);" onblur="getCreatedBy();"/>
			<input type="hidden" id="createdById">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanCreateErrMsg" class="error"></span>
		</td>
	</tr>
	<tr>
		<td class="title2">Updated By</td>
		<td>
			<input id="updatedBy" class="input" onkeydown="showUpdatedBy(this);" 
				onkeyup="showUpdatedBy(this);"  onblur="getUpdatedBy();"/>
			<input type="hidden" id="updatedById">
		</td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2">
			<span id="spanUpdateErrMsg" class="error"></span>
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
	<tr>
	<tr align="right">
		<td colspan="3"><input type="button" value="Generate" onclick="generateJournalVouchers()"/></td>
	</tr>
</table>
<div>
	<iframe id="reportJournalVoucher"></iframe>
</div>
</body>
</html>