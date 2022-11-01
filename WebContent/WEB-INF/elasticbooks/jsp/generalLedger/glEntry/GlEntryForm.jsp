<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: General ledger entry form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js" type="text/javascript"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/cssFunctionUtil.js"></script>
<title>General Ledger Entry Form</title>
<style type="text/css">
.textboxToLabel, .txtGlEntries, .txtCompanyNumber, .txtDivisionNumber,
.txtAccountNumber, .txtDebits, .txtCredits, .comments{
	border: none;
	background-color: #FFFFFF;
	background: transparent;
}

.btnsBrowseCompanies, .btnsBrowseDivisions, .btnsBrowseAccounts {
	height : 20px;
}

.divVeil {
    background-color: #F2F1F0;
}

.tdNumber {
	text-align: right;
}

#glTable {
	cellspacing: 0;
	border: none;
}

#glTable thead th {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}

#glTable tbody td {
	border-top: 1px solid #000000;
	border-right: 1px solid #000000;
}
</style>

<script type="text/javascript">
var glStatusId = "${glStatusId}";
var glEntrySourceId  = "${glEntrySourceId}";
var minimumRows = "${glEntryRows}" > 4 ? "${glEntryRows}" : 4;
var isEdit = "${isEdit}";
var rowObj = null;
var currentFormStatus = "${generalLedgerDto.generalLedger.formWorkflow.currentStatusId}";
var currentGLIndex = 0;

if ("${pId}" > 0) {
	$.getScript ("${pageContext.request.contextPath}/js/dojo/dojo.js");
	$.getScript ("${pageContext.request.contextPath}/js/ajaxUtil.js");
	$("head").append("<link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/css/status.css'>");
	if ("${flag}" == "edit")
		$.noConflict();
	//Form status 6 for posted and 4 for cancelled.
	if (currentFormStatus == "6" || currentFormStatus == "4")
		$("#generalLedgerDto :input").attr("disabled", "disabled");
} else {
	$.noConflict();
}

$(document).ready (function () {
	$("tr .imgDelete").live("click", function () {
		var row = $(this).closest("tr");
		var index = $(row).index();
		if (!$(row).hasClass("divVeil") && index != 0) {
			$(row).remove();
			if ($("#glTable > tbody > tr").length < minimumRows) {
				addGlRows(1);
				disableEntries ($("#glTable tbody tr:last"));
			}
			updateTotalValue ();
			addEntryNumbers();
		}
	});

	$("#txtEntrySource").val(glEntrySourceId == 1 ? "General Ledger" : "");
	$("#glEntrySourceId").val(glEntrySourceId);
	$("#cbDebit").attr("checked", true);

	"${pId}" > 0 ? $("#jvNumber").val("${sequenceNo}") : $("#jvNumber").val("${jvNumber}");
	addGlRows(minimumRows);
	disableRows();

	if ("${pId}" > 0) {
		$("#txtSequenceNo").val("${sequenceNo}");
		repopulateTable();
		updateTotalValue();

		formatMonetaryVal();
	}

	$("#glTable thead tr").find("th:last").css("border-right", "none");
	$("#glTable tbody tr").each(function (i) {
		$(this).find("td:last").css("border-right", "none");
	});
});

$(function () {
	$("#txtComment").blur(function () {
		$("#glTable tbody tr").each(function (i) {
			if (!$(this).hasClass("divVeil") && $(this).find(".comments").val() == "") {
				$(this).find(".comments").val($("#txtComment").val());
			}
		});
	});

	$("#txtSearchCompanies").live("keypress", function (e) {
		filterComboList(e, $(this), "tblCompanyList", ".tdCompanyNumber", ".tdCompanyName");
	});

	$("#txtSearchDivisions").live("keypress", function (e) {
		filterComboList(e, $(this), "tblDivisionList", ".tdDivisionNumber", ".tdDivisionName");
	});

	$("#txtSearchAccounts").live("keypress", function (e) {
		filterComboList(e, $(this), "tblAccountList", ".tdAccountNumber", ".tdAccountName");
	});

	$(".imgCloseComboList").live("click", function() {
		$(this).parent("div").hide().find("table tbody").empty();
	});

	$("#trCompanyList").live("click", function() {
		selectNumber ($(this), ".hdnCompanyIds", ".hdnCompanyId", ".txtCompanyNumber", ".tdCompanyNumber",
				".spanCompanyNames", ".tdCompanyName", $("#tblCompanyList tbody"), $("#divCompanyList"), function() {
			$(rowObj).closest("tr").find(".txtDivisionNumber").focus();
			$($(rowObj).find(".hdnDivisionIds").val(""));
			$($(rowObj).find(".txtDivisionNumber").val(""));
			$(rowObj).closest("tr").find(".spanDivisionNames").html("");
			$($(rowObj).find(".hdnAccountIds").val(""));
			$($(rowObj).find(".txtAccountNumber").val(""));
			$(rowObj).closest("tr").find(".spanAccountNames").html("");
		});
	});

	$("#trDivisionList").live("click", function() {
		selectNumber ($(this), ".hdnDivisionIds", ".hdnDivisionId", ".txtDivisionNumber", ".tdDivisionNumber",
				".spanDivisionNames", ".tdDivisionName", $("#tblDivisionList tbody"), $("#divDivisionList"), function() {
			$(rowObj).closest("tr").find(".txtAccountNumber").focus();
			$($(rowObj).find(".hdnAccountIds").val(""));
			$($(rowObj).find(".txtAccountNumber").val(""));
			$(rowObj).closest("tr").find(".spanAccountNames").html("");
		});
	});

	$("#trAccountList").live("click", function() {
		selectNumber ($(this), ".hdnAccountIds", ".hdnAccountId", ".txtAccountNumber", ".tdAccountNumber",
				".spanAccountNames", ".tdAccountName", $("#tblAccountList tbody"), $("#divAccountList"), function() {
			$(rowObj).closest("tr").find(".txtDebits").focus();
		});
	});

	$(".txtDivisionNumber").live("focus", function (e) {
		var companyName = $.trim($(this).closest("tr").find(".spanCompanyNames").html());
		if (companyName == "Invalid company")
			$(this).closest("tr").find(".txtCompanyNumber").focus();
	});

	$(".txtAccountNumber").live("focus", function (e) {
		var companyName = $.trim($(this).closest("tr").find(".spanCompanyNames").html());
		if (companyName == "Invalid company")
			$(this).closest("tr").find(".txtCompanyNumber").focus();
		var divisionName = $.trim($(this).closest("tr").find(".spanDivisionNames").html());
		if (divisionName == "Invalid division")
			$(this).closest("tr").find(".txtDivisionNumber").focus();
	});

	$(".txtCompanyNumber").live("keydown blur", function (e) {
		if (e.which == 9 || e.which == 13 || e.which == 38 || e.which == 40) {
			var companyNumber = $(this).closest("tr").find(".txtCompanyNumber").val();
			assignCombo ($(this), ".hdnCompanyIds" , ".spanCompanyNames", e,
				"generateCompanyName?companyNumber="+companyNumber, "name", "company", ".txtCompanyNumber");
		}
	});

	$(".txtDivisionNumber").live("keydown blur", function (e) {
		if (e.which == 9 || e.which == 13 || e.which == 38 || e.which == 40) {
			var companyNumber = $(this).closest("tr").find(".txtCompanyNumber").val();
			var divisionNumber = $(this).closest("tr").find(".txtDivisionNumber").val();
			if (companyNumber != "") {
				assignCombo ($(this), ".hdnDivisionIds" , ".spanDivisionNames", e,
					"generateDivisionName?companyNumber="+companyNumber+"&divisionNumber="+divisionNumber,
						"name", "division", ".txtDivisionNumber");
			} else {
					alert("Select a company.");
			}
		}
	});

	$(".txtAccountNumber").live("keydown", function (e) {
		if (e.which == 9 || e.which == 13 || e.which == 38 || e.which == 40) {
			generateAcctNumber (this, e);
		}
	});

	function generateAcctNumber (elem, e) {
		var companyNumber = $(elem).closest("tr").find(".txtCompanyNumber").val();
		var divisionNumber = $(elem).closest("tr").find(".txtDivisionNumber").val();
		var accountNumber = $(elem).closest("tr").find(".txtAccountNumber").val();
		if (companyNumber != "" && divisionNumber != "") {
			assignCombo ($(elem), ".hdnAccountIds" , ".spanAccountNames", e,
					"generateAccountName?companyNumber="+companyNumber+"&divisionNumber="+divisionNumber+
							"&accountNumber="+accountNumber, "accountName", "account", ".txtAccountNumber");
		} else if (companyNumber == "" && divisionNumber == "") {
			alert("Select a company and a division.");
		} else if (companyNumber == "") {
			alert("Select a company.");
		}  else if (divisionNumber == "") {
			alert("Select a division.");
		}
	}

	$(".txtDebits").live("blur", function () {
		$(this).val(accounting.formatMoney($(this).val()));
	});

	$(".txtCredits").live("blur", function () {
		$(this).val(accounting.formatMoney($(this).val()));
	});

	$(".txtDebits").live("keydown", function (e) {
		if (e.which == 38 || e.which == 40)
			arrowUpAndDown($(this), e, ".txtDebits");
	});

	$(".txtCredits").live("keydown", function (e) {
		if (e.which == 38 || e.which == 40)
			arrowUpAndDown($(this), e, ".txtCredits");
	});

	$(".txtDebits, .txtCredits").live("blur", function () {
		updateTotalValue ();
	});

	$(".comments").live("keydown", function (e) {
		var currentRow = $(this).closest("tr");
		if (e.which == 9 && e.shiftKey == false) {
			if (validGlEntries($(currentRow)) || hasValidEntry($(currentRow))) {
				if ($(currentRow).is(":last-child")) {
					addGlRows(1);
					$(currentRow).next().find("td").find(".imgSearchCompanies").focus();
				} else {
					enableEntries ($(currentRow).next());
				}
				$(currentRow).next().find(".comments").val($("#txtComment").val());
			}
		} else {
			arrowUpAndDown($(this), e, ".comments");
			if (validGlEntries($(currentRow)) || hasValidEntry($(currentRow))) {
				if (e.which == 40)
					$(currentRow).next().find(".comments").val($("#txtComment").val());
			} else {
				if (e.which == 38) {
					clearGlRow($(currentRow));
					disableEntries ($(currentRow));
				}
			}
		}
	});
});

function searchCompanies (img) {
	searchCombo ($(img), $("#divCompanyList"), ".imgSearchCompanies", "loadCompanies", $("#tblCompanyList tbody"),
			"trCompanyList", "tdCompanyNumber", "companyNumber", "hdnCompanyId", "tdCompanyName", "name", function () {
		if ($("#divDivisionList").is(":visible"))
			$("#divDivisionList").hide().find("#tblDivisionList tbody").empty();
		if ($("#divAccountList").is(":visible"))
			$("#divAccountList").hide().find("#tblAccountList tbody").empty();
	});
}

function searchDivisions (img) {
	var companyId =  $(img).closest("tr").find(".hdnCompanyIds").val();
	if (companyId != "") {
		searchCombo ($(img), $("#divDivisionList"), ".imgSearchDivisions", "loadDivisions?companyId="+companyId,
				$("#tblDivisionList tbody"), "trDivisionList", "tdDivisionNumber", "number", "hdnDivisionId",
				"tdDivisionName", "name", function () {
			if ($("#divCompanyList").is(":visible"))
				$("#divCompanyList").hide().find("#tblCompanyList tbody").empty();
			if ($("#divAccountList").is(":visible"))
				$("#divAccountList").hide().find("#tblAccountList tbody").empty();
		});
	} else {
		alert("Select a company.");
	}
}

function searchAccounts (img) {
	var companyId =  $(img).closest("tr").find(".hdnCompanyIds").val();
	var divisionId = $(img).closest("tr").find(".hdnDivisionIds").val();
	if (companyId != "" && divisionId != "") {
		searchCombo ($(img), $("#divAccountList"), ".imgSearchAccounts", "loadAccounts?companyId="+companyId+
				"&divisionId="+divisionId, $("#tblAccountList tbody"), "trAccountList", "tdAccountNumber",
				"number", "hdnAccountId", "tdAccountName", "accountName", function () {
			if ($("#divCompanyList").is(":visible"))
				$("#divCompanyList").hide().find("#tblCompanyList tbody").empty();
			if ($("#divDivisionList").is(":visible"))
				$("#divDivisionList").hide().find("#tblDivisionList tbody").empty();
		});
	} else if (companyId == "" && divisionId == "") {
		alert("Select a company and a division.");
	} else if (companyId == "") {
		alert("Select a company.");
	}  else if (divisionId == "") {
		alert("Select a division.");
	}
}

function saveGeneralLedger () {
	parseDebitandCredit();
	$("#btnSaveGlEntry").attr("disabled", "disabled");
	var glEntries = '[';
	var ctr = 0;

	$("#glTable tbody tr").each(function(i) {
		var glEntryId = $.trim($(this).find("td").eq(0).find(".hdnGlEntryIds").val());
		var companyNumber = $.trim($(this).find("td").eq(2).find(".txtCompanyNumber").val());
		var companyId = $.trim($(this).find("td").eq(2).find(".hdnCompanyIds").val());
		var divisionNumber = $.trim($(this).find("td").eq(3).find(".txtDivisionNumber").val());
		var divisionId = $.trim($(this).find("td").eq(3).find(".hdnDivisionIds").val());
		var accountNumber = $.trim($(this).find("td").eq(4).find(".txtAccountNumber").val());
		var accountId = $.trim($(this).find("td").eq(4).find(".hdnAccountIds").val());
		var companyName = $.trim($(this).find("td").eq(5).find(".spanCompanyNames").html());
		var divisionName = $.trim($(this).find("td").eq(5).find(".spanDivisionNames").html());
		var accountName =  $.trim($(this).find("td").eq(5).find(".spanAccountNames").html());
		var debit = $.trim($(this).find("td").eq(6).find(".txtDebits").val());
		var credit = $.trim($(this).find("td").eq(7).find(".txtCredits").val());
		var description = $.trim($(this).find("td").eq(8).find(".comments").val());

		if (validGlEntries($(this))) {
			if (ctr > 0)
				glEntries += ', ';
			glEntries += '[{"glEntryId":"'+glEntryId+'"}, ';
			glEntries += '{"companyNumber":"'+companyNumber+'"}, ';
			glEntries += '{"companyId":"'+companyId+'"}, ';
			glEntries += '{"companyName":"'+companyName+'"}, ';
			glEntries += '{"divisionNumber":"'+divisionNumber+'"}, ';
			glEntries += '{"divisionId":"'+divisionId+'"}, ';
			glEntries += '{"divisionName":"'+divisionName+'"}, ';
			glEntries += '{"accountNumber":"'+accountNumber+'"}, ';
			glEntries += '{"accountId":"'+accountId+'"}, ';
			glEntries += '{"accountName":"'+accountName+'"}, ';
			glEntries += '{"debit":"'+(debit == "" ? 0.0 : debit)+'"}, ';
			glEntries += '{"credit":"'+(credit == "" ? 0.0 : credit)+'"}, ';
			glEntries += '{"description":"'+description+'"}]';
			ctr++;
		}
	});
	glEntries += ']';

	var difference = updateTotal(6, ".txtDebits") - updateTotal(7, ".txtCredits");
	$("#difference").val(difference);
	$("#jsonData").val(glEntries);
	if ($("#glStatusId").val() == 0)
		$("#glStatusId").val(1);
	var jvNumber = $("#jvNumber").val();
	doPostWithCallBack ("generalLedgerDto", "form", function (data) {
		if (data.substring(0,5) == "saved") {
			var objectId = data.split(";")[1];
			var formStatus = new Object();
			formStatus.objectId = objectId;
			updateTable (formStatus);
			if("${pId}" == 0)
				dojo.byId("form").innerHTML = "";
			$("#glTable tbody tr").remove();
			glEntries = '';
			$("#btnSaveGlEntry").removeAttr("disabled");
		} else {
			parseDebitandCredit();
			if("${pId}" == 0) {
				dojo.byId("form").innerHTML = data;
			} else {
				dojo.byId("editForm").innerHTML = data;
			}
			repopulateTable();
			formatMonetaryVal();
			updateTotalValue();
			$("#txtSequenceNo").val("${sequenceNo}");
			$("#jvNumber").val(jvNumber);
			disableRows();
		}
		$("#txtEntrySource").val("General Ledger");
	});
}

function selectNumber (elem, hdnIds, hdnId, txtNumber, tdNumber, spanName, tdName,
		tblBodyObj, divObj, callbackFunction) {
	if (rowObj != null) {
		$(rowObj).find(hdnIds).val($.trim($(elem).closest("tr").find(hdnId).val()));
		$(rowObj).find(txtNumber).val($.trim($(elem).closest("tr").find(tdNumber).text()));
		$(rowObj).closest("tr").find(spanName).html(checkNameLength($.trim($(elem).closest("tr").find(tdName).html()), spanName));
		$(rowObj).closest("tr").find(spanName).css("color", "#000000");
		callbackFunction();
		rowObj = null;
	}
	$(tblBodyObj).empty();
	$(divObj).hide();
}

function searchCombo (elem, divObj, img, param, tblBodyObj, trObj, tdNumber, number, hdnId,
		tdName, name, callbackFunction) {
	if (!$(elem).closest("tr").hasClass("divVeil")) {
		if (!$(divObj).is(":visible")) {
			callbackFunction();
			filterComboFields(param, img, divObj, tblBodyObj, trObj, tdNumber, number,
					hdnId, tdName, name);
			rowObj = $(elem).parent("td").parent("tr").closest("tr");
		}
	}
}

function arrowUpAndDown (elem, e, txtObj) {
	var currentRow = $(elem).closest("tr");
	if (e.which == 38) {
		$(currentRow).prev().find(txtObj).focus();
		if ($(currentRow).prevAll().length > 0) {
			if (!(validGlEntries($(currentRow)))) {
				if (!hasValidEntry($(currentRow)))
					clearGlRow($(currentRow));
				disableEntries ($(currentRow));
			}
		}
	} else if (e.which == 40) {
		if (validGlEntries($(currentRow)) || hasValidEntry($(currentRow))) {
			if ($(currentRow).is(":last-child"))
				addGlRows(1);
			else {
				enableEntries ($(currentRow).next());
				$(currentRow).next().find(txtObj).focus();
			}
		}
	}
}

function assignCombo (elem, hdnIds, spanNames, e, param, name, component, txtNumber) {
	var currentRow = $(elem).closest("tr");
	var elemId =  $(currentRow).find(hdnIds);
	var elemName = $(currentRow).find(spanNames);
	if (e.which == 9) {
		generateCombo (param, 9, $(elem), elemId, elemName, name, component);
	} else if (e.which == 13) {
		generateCombo (param, 13, $(elem), elemId, elemName, name, component);
	} else if (e.which == 38) {
		generateCombo (param, 38, $(elem), elemId, elemName, name, component, function () {
			if (validGlEntries(currentRow) || hasValidEntry(currentRow)) {
				$(currentRow).prev().find(txtNumber).focus();
				if ($(currentRow).prevAll().length > 0) {
					if (!(validGlEntries(currentRow))) {
						if (!hasValidEntry(currentRow))
							clearGlRow($(currentRow));
						disableEntries ($(currentRow));
					}
				}
			}
		});
	} else if (e.which == 40) {
		generateCombo (param, 40, $(elem), elemId, elemName, name, component, function () {
			if (validGlEntries(currentRow) || hasValidEntry(currentRow)) {
				if ($(currentRow).is(":last-child"))
					addGlRows(1);
				else {
					enableEntries ($(currentRow).next());
					$(currentRow).next().find(txtNumber).focus();
				}
			}
		});
	} else {
		if (txtNumber == ".txtAccountNumber") {
			generateCombo (param, 9, $(elem), elemId, elemName, name, component);
		}
	}
}

function generateCombo (param, keycode, elem, elemId, elemName, lblName, objectName, handler) {
	loadAjaxData(param, "json", function (responseText) {
		$(elemId).val(responseText["id"]);
		$(elemName).css("color", "#000000");
		$(elemName).html(checkNameLength(responseText[lblName], "." + $(elemName).attr("class")));
		if (keycode == 9) {
			if ($(elem).attr("class") == "txtCompanyNumber")
				$(elem).closest("tr").find(".txtDivisionNumber").focus();
			else if ($(elem).attr("class") == "txtDivisionNumber")
				$(elem).closest("tr").find(".txtAccountNumber").focus();
			else if ($(elem).attr("class") == "txtAccountNumber")
				$(elem).closest("tr").find(".txtDebits").focus();
		}
		if (handler != null)
			handler();
	}, function(error) {
		if($(elem).val() != "") {
			$(elemId).val("");
			$(elemName).css("color", "red");
			$(elemName).html("Invalid "+objectName);
			console.log(error);
		}
	});
}

function checkNameLength (name, spanNames) {
	var formattedName = name;
	if (spanNames == ".spanDivisionNames" || spanNames == ".spanAccountNames")
		formattedName = "-" + formattedName;
	return formattedName;
}

function formatMoney (textbox) {
	var money = accounting.formatMoney($(textbox).val());
	$(textbox).val(money.replace(/[$]/gi, ''));
}

function formatMonetaryVal() {
	formatMoney($("#amount"));
	$("#glTable tbody tr").each(function(i) {
		formatMoney($(this).find(".txtDebits"));
		formatMoney($(this).find(".txtCredits"));
	});
}

function updateTotalValue (){
	var totalDebit = updateTotal (6, ".txtDebits");
	$("#glTable tfoot td#tdDebitTotal").html(accounting.formatMoney(totalDebit));
	var totalCredit = updateTotal (7, ".txtCredits");
	$("#glTable tfoot td#tdCreditTotal").html(accounting.formatMoney(totalCredit));
}

function updateTotal (column, elem) {
	var total = 0;
	$("#glTable tbody tr").each(function(i) {
		var value = $.trim($(this).find("td").eq(column).find(elem).val());
		value = value.replace(/\,/g,"");
		total += value != "" ? parseFloat(value) : 0;
	});
	return total;
}

function addGlRows (numOfRows) {
	for (var i=0; i<numOfRows; i++) {
		var newRow = "<tr id='trReadOnly'>";
		newRow += "<td align='center'>";
		newRow += "<img class='imgDelete' id='imgDelete' src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRows(this);'/>";
		newRow += "<input type='hidden' class='hdnGlEntryIds'>";
		newRow += "</td>";

		newRow += "<td class='tdNumber'></td>";

		newRow += "<td valign='top' style='white-space: nowrap;'>";
		newRow += "<input style='width: 40px;' class='txtCompanyNumber' id='companyId' onkeyup='showCompanies(this);' maxLength='5'/>";
		newRow += "&nbsp;";
		newRow += "<img class='imgSearchCompanies' src='${pageContext.request.contextPath}/images/search_active.png' onclick='searchCompanies(this);'/>";
		newRow += "&nbsp;";
		newRow += "<input type='hidden' class='hdnCompanyIds'></span>";
		newRow +="</td>";

		newRow += "<td valign='top' style='white-space: nowrap;'>";
		newRow += "<input style='width: 40px;' class='txtDivisionNumber' id='divisionId' onkeyup='showDivisions(this);' maxLength='5'/>";
		newRow += "&nbsp;";
		newRow += "<img class='imgSearchDivisions' src='${pageContext.request.contextPath}/images/search_active.png' onclick='searchDivisions(this);'/>";
		newRow += "&nbsp;";
		newRow += "<input type='hidden' class='hdnDivisionIds'></span>";
		newRow +="</td>";

		newRow += "<td valign='top' style='white-space: nowrap;'>";
		newRow += "<input style='width: 80px;' class='txtAccountNumber' id='accountId' maxLength='10' onkeyup='showAccts(this);'/>";
		newRow += "&nbsp;";
		newRow += "<img class='imgSearchAccounts' src='${pageContext.request.contextPath}/images/search_active.png'  onclick='searchAccounts(this);'/>";
		newRow += "&nbsp;";
		newRow += "<input type='hidden' class='hdnAccountIds'></span>";
		newRow +="</td>";

		newRow += "<td>";
		newRow += "<span class='spanCompanyNames'></span>";
		newRow += "<span class='spanDivisionNames'></span>";
		newRow += "<span class='spanAccountNames'></span>";
		newRow +="</td>";

		newRow += "<td valign='top' class='tdDebits'>";
		newRow += "<input style='width: 100%; text-align: right;' class='txtDebits' maxLength='12'/>";
		newRow += "</td>";
		newRow += "<td valign='top' class='tdCredits'>";
		newRow += "<input style='width: 100%; text-align: right;' class='txtCredits' maxLength='12'/>";
		newRow += "</td>";
		newRow += "<td valign='top'>";
		newRow += "<input style='width: 100%;' class='comments' maxLength='200'/>";
		newRow += "</td>";
		newRow += "</tr>";
		$("#glTable tbody").append(newRow);
		$("#glTable tbody tr:last").find("td:last").css("border-right", "none");
	}
	addEntryNumbers();
}

function deleteRows(textBox) {
	enableEntries ($(textBox).closest("tr").next());
	$($(textBox).closest("tr")).remove();
	if($("#glTable tbody tr").length < 4){
		addGlRows (1);
	}
	disableRows ();
}
function addEntryNumbers () {
	$("#glTable tbody tr").each(function(i) {
		$.trim($(this).find("td").eq(1).html(i+1));
	});
}

function disableRows () {
	$("#glTable tbody tr:not(:first)").each(function(i) {
		var cNumber = $.trim($(this).find(".txtCompanyNumber").val());
		var dNumber = $.trim($(this).find(".txtDivisionNumber").val());
		var aNumber = $.trim($(this).find(".txtAccountNumber").val());
		var isDEmpty = $.trim($(this).find(".txtDebits").val()) == "0.00"
			|| $.trim($(this).find(".txtDebits").val()) == "";
		var isCEmpty = $.trim($(this).find(".txtCredits").val()) == "0.00"
			|| $.trim($(this).find(".txtCredits").val()) == "";
		if (cNumber == "" && dNumber == "" && aNumber == "" && isDEmpty && isCEmpty) {
			disableEntries($(this));
		}
	});
}

function disableEntries (elem) {
	$(elem).closest("tr").find(".txtCompanyNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtDivisionNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtAccountNumber").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtDebits").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtDebits").css("background-color", "#F2F1F0");
	$(elem).closest("tr").find(".txtCredits").attr("disabled", "disabled");
	$(elem).closest("tr").find(".txtCredits").css("background-color", "#F2F1F0");
	$(elem).closest("tr").find(".comments").attr("disabled", "disabled");
	$(elem).addClass("divVeil");
}

function enableEntries (elem) {
	$(elem).closest("tr").find(".txtCompanyNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtDivisionNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtAccountNumber").removeAttr("disabled");
	$(elem).closest("tr").find(".txtDebits").removeAttr("disabled");
	$(elem).closest("tr").find(".txtDebits").css("background-color", "#FFFFFF");
	$(elem).closest("tr").find(".txtCredits").removeAttr("disabled");
	$(elem).closest("tr").find(".txtCredits").css("background-color", "#FFFFFF");
	$(elem).closest("tr").find(".comments").removeAttr("disabled");
	$(elem).removeClass("divVeil");
	var comments = $(elem).closest("tr").find(".comments");
	if ($.trim($(comments).val()) == "")
		$(comments).val($("#txtComment").val());
}

function checkAndSetDecimal(txtObj) {
	var amount = $(txtObj).val();
    var amountLength = amount.length;
    var cntDec = 0;
    var lengthFirstDec = 0;
    var finalAmount = "";

    for (var i=0; i<amountLength; i++) {
        var char = amount.charAt(i);
        if (!isNaN(char) || (char == ".") || (char == "-")) {
			if (char == "-" && i==0)
				finalAmount += char;
            if (char == ".")
                cntDec++;
            if ((char == ".") && (cntDec == 1)) {
                lengthFirstDec = i + 1;
                finalAmount += char;
            }
            if (!isNaN(char))
                finalAmount += char;
        }
    }
    if ((cntDec == 1) && (amountLength > (lengthFirstDec + 2)))
        finalAmount = roundTo2Decimal(finalAmount);
    $(txtObj).val(finalAmount);
}

function repopulateTable () {
	var jsonObj = $.parseJSON($("#jsonData").val());
	if ($("#glTable tbody tr").length == 0) {
		var length = minimumRows < jsonObj.length ? jsonObj.length : minimumRows;
		addGlRows(length);
	}
	for (var i=0; i<jsonObj.length; i++) {
		for (var j=0; j<jsonObj[i].length; j++) {
			var obj = jsonObj[i][j];
			if (typeof obj["glEntryId"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(0).
					find(".hdnGlEntryIds").val(obj["glEntryId"]);
			} else if (typeof obj["companyNumber"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(2).
					find(".txtCompanyNumber").val(obj["companyNumber"]);
			} else if (typeof obj["companyId"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(2).
					find(".hdnCompanyIds").val(obj["companyId"]);
			} else if (typeof obj["divisionNumber"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(3).
					find(".txtDivisionNumber").val(obj["divisionNumber"]);
			} else if (typeof obj["divisionId"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(3).
					find(".hdnDivisionIds").val(obj["divisionId"]);
			} else if (typeof obj["accountNumber"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(4).
					find(".txtAccountNumber").val(obj["accountNumber"]);
			} else if (typeof obj["accountId"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(4).
					find(".hdnAccountIds").val(obj["accountId"]);
			} else if (typeof obj["companyName"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(5).
					find(".spanCompanyNames").html(obj["companyName"]);
			} else if (typeof obj["divisionName"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(5).
					find(".spanDivisionNames").html(obj["divisionName"]);
			} else if (typeof obj["accountName"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(5).
					find(".spanAccountNames").html(obj["accountName"]);
			} else if (typeof obj["debit"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(6).
					find(".txtDebits").val(obj["debit"] != "0" ? Number(obj["debit"]) : "");
			} else if (typeof obj["credit"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(7).
					find(".txtCredits").val(obj["credit"] != "0" ? Number(obj["credit"]) : "");
			} else if (typeof obj["description"] != "undefined") {
				$("#glTable tbody tr:eq("+i+")").find("td").eq(8).
				find(".comments").val(obj["description"]);
			}
		}
	}

	// Function that will check if the rows are to be disabled
	// or not
	$("#glTable tbody tr:not(:first)").each(function(i) {
		if (validGlEntries($(this) || hasValidEntry($(this))))
			enableEntries ($(this));
		else
			disableEntries ($(this));
	});
}

function validGlEntries (elem) {
	var companyId = $.trim($(elem).find("td").eq(2).find(".hdnCompanyIds").val());
	var divisionId = $.trim($(elem).find("td").eq(3).find(".hdnDivisionIds").val());
	var accountId = $.trim($(elem).find("td").eq(4).find(".hdnAccountIds").val());
	var debit = $.trim($(elem).find("td").eq(6).find(".txtDebits").val());
	var credit = $.trim($(elem).find("td").eq(7).find(".txtCredits").val());
	var description = $.trim($(elem).find("td").eq(8).find(".comments").val());

	return !(companyId == "" && divisionId == "" && accountId == "" &&
			(debit == "" || debit == "0.00")  &&
			(credit == "" || credit == "0.00")  && description == "");
}

function hasValidEntry (elem) {
	var companyId = $.trim($(elem).find("td").eq(2).find(".hdnCompanyIds").val());
	var divisionId = $.trim($(elem).find("td").eq(3).find(".hdnDivisionIds").val());
	var accountId = $.trim($(elem).find("td").eq(4).find(".hdnAccountIds").val());
	var debit = $.trim($(elem).find("td").eq(6).find(".txtDebits").val());
	var credit = $.trim($(elem).find("td").eq(7).find(".txtCredits").val());
	var description = $.trim($(elem).find("td").eq(8).find(".comments").val());

	return (companyId != "" || divisionId != "" || accountId != "" ||
			debit != "" && credit != "" && description != "");
}

function clearGlRow (row) {
	$(row).find("td").eq(2).find(".hdnCompanyIds").val("");
	$(row).find("td").eq(2).find(".txtCompanyNumber").val("");

	$(row).find("td").eq(3).find(".hdnDivisionIds").val("");
	$(row).find("td").eq(3).find(".txtDivisionNumber").val("");

	$(row).find("td").eq(4).find(".hdnAccountIds").val("");
	$(row).find("td").eq(4).find(".txtAccountNumber").val("");

	$(row).find("td").eq(5).find(".spanCompanyNames").html("");
	$(row).find("td").eq(5).find(".spanDivisionNames").html("");
	$(row).find("td").eq(5).find(".spanAccountNames").html("");

	$(row).find("td").eq(6).find(".txtDebits").val("");
	$(row).find("td").eq(7).find(".txtCredits").val("");
	$(row).find("td").eq(8).find(".comments").val("");
}

function filterComboFields (uri, searchImage, divObj, tblDivTbody, trList, tdNumber, number, hdnId, tdName, name) {
	loadAjaxData (uri, "json", function(responseText) {
		var position = $(searchImage).closest("tr").find(searchImage).position();
		var offset = $(searchImage).closest("tr").find(searchImage).offset();
		$(divObj).css("background-color", "#FFFFFF")
			.css("border", "1px solid #999999")
			.css("cursor", "default")
			.css("position","absolute")
			.css("left", position.left+"px")
			.css("top", position.top+"px")
			.css("padding", "25px 25px 20px")
			.show();
		$(tblDivTbody).empty();
		for (var i=0; i<responseText.length; i++) {
			var newRow = "<tr id='"+trList+"'>";
			newRow += "<td class='"+tdNumber+"'>"+responseText[i][number];
			newRow += "<input type='hidden' class='"+hdnId+"' value='"+responseText[i]["id"]+"' />";
			newRow += "</td>";
			newRow += "<td class='"+tdName+"'>"+responseText[i][name]+"</td>";
			newRow += "</tr>";
			$(tblDivTbody).append(newRow);
		}
	}, function(error) {
		alert("Error in retrieving data.");
		console.log(error);
	});
}

function loadAjaxData (uri, type, callbackFunction, errorFunction) {
	$.ajax({
		url: contextPath+"/generalLedger/journalEntries/"+uri,
		success : callbackFunction,
		error : errorFunction,
		dataType: type,
	});
}

function filterComboList (e, elem, tblElem, numberClass, nameClass) {
	if (e.which == 13) {
		var value = $.trim($(elem).val().toLowerCase());
		$("#"+tblElem+" tbody tr").each(function (i) {
			var number = $.trim($(this).find(numberClass).text().toLowerCase());
			var name = $.trim($(this).find(nameClass).text().toLowerCase());
			if (number.indexOf(value) == -1 && name.indexOf(value) == -1)
				$(this).closest("tr").hide();
			else
				$(this).closest("tr").show();
		});
	}
}

function parseDebitandCredit() {
	$(".txtDebits").each(function(i) {
		var debit = $(this).val();
		$(this).val(debit.replace(/\,/g,""));
	});

	$(".txtCredits").each(function(i) {
		var credit = $(this).val();
		$(this).val(credit.replace(/\,/g,""));
	});
}

function showCompanies(textBox){
	var companyName = $.trim($(textBox).closest("tr").find(".txtCompanyNumber").val());
	var uri = contextPath + "/generalLedger/journalEntries/getCompaniesByNameAndNumber?companyName="
			+companyName+"&isActive=true&limit=10";
	$(textBox).closest("tr").find(".txtCompanyNumber").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(textBox).closest("tr").find(".hdnCompanyIds").val(ui.item.id);
			$(this).val(ui.item.companyNumber);
			$(textBox).closest("tr").find(".spanCompanyNames").html(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(textBox).closest("tr").find(".hdnCompanyIds").val();
						$(this).val(ui.item.companyNumber);
					} else {
						if(companyName == ""){
							$(textBox).closest("tr").find(".hdnCompanyIds").val("");
							$(textBox).closest("tr").find(".spanCompanyNames").html("");
							$(textBox).closest("tr").find(".hdnDivisionIds").val();
							$(textBox).closest("tr").find(".txtDivisionNumber").val("")
							$(textBox).closest("tr").find(".spanDivisionNames").html("");
							$(textBox).closest("tr").find(".hdnAccountIds").val();
							$(textBox).closest("tr").find(".txtAccountNumber").val("");
							$(textBox).closest("tr").find(".spanAccountNames").html("");
						}
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.companyNumber+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}

function showDivisions(textBox) {
	var division = $.trim($(textBox).closest("tr").find(".txtDivisionNumber").val());
	var companyId =  $(textBox).closest("tr").find(".hdnCompanyIds").val();
	var uri = contextPath + "/getDivisions/new?companyId="+companyId+"&divisionNumber="+division+"&isActive=true&limit=10";
	$(textBox).closest("tr").find(".txtDivisionNumber").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(textBox).closest("tr").find(".hdnDivisionIds").val(ui.item.id);
			$(this).val(ui.item.number);
			$(textBox).closest("tr").find(".spanDivisionNames").html("-"+ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(textBox).closest("tr").find(".hdnDivisionIds").val(ui.item.id);
						$(this).val(ui.item.number);
					} else {
						if(division == ""){
							$(textBox).closest("tr").find(".hdnDivisionIds").val("");
							$(textBox).closest("tr").find(".spanDivisionNames").html("");
							$(textBox).closest("tr").find(".hdnAccountIds").val();
							$(textBox).closest("tr").find(".txtAccountNumber").val("");
							$(textBox).closest("tr").find(".spanAccountNames").html("");
						}
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.name+ "</a>" )
			.appendTo( ul );
	};
}

function showAccts(textBox) {
	var companyId =  $(textBox).closest("tr").find(".hdnCompanyIds").val();
	var divisionId = $(textBox).closest("tr").find(".hdnDivisionIds").val();
	var accountName = $(textBox).closest("tr").find(".txtAccountNumber").val();
	var uri = contextPath + "/getAccounts/byName?accountName="+accountName
			+"&companyId="+companyId+"&divisionId="+divisionId+"&limit=10";
	$(textBox).closest("tr").find(".txtAccountNumber").autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.number);
			$(textBox).closest("tr").find(".spanAccountNames").html("-"+ui.item.accountName);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.number);
						$(textBox).closest("tr").find(".spanAccountNames").html("-"+ui.item.accountName);
					} else {
						if(accountName == ""){
							$(textBox).closest("tr").find(".hdnAccountIds").val();
							$(textBox).closest("tr").find(".spanAccountNames").html("");
						}
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.number+"-"+item.accountName+ "</a>" )
			.appendTo( ul );
	};
}
</script>
</head>

<div id="divCompanyList" style="display: none">
	<img id="imgCloseCList" src="${pageContext.request.contextPath}/images/cal_close.gif"
		class="imgCloseComboList"/>
	<div>
		<input type="text" id="txtSearchCompanies"/>
	</div>
	<table id="tblCompanyList" class="dataTable">
		<thead>
			<tr>
				<th width="30%">Number</th>
				<th width="70%">Name</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<div id="divDivisionList" style="display: none">
	<img id="imgCloseDList" src="${pageContext.request.contextPath}/images/cal_close.gif"
		class="imgCloseComboList"/>
	<div>
		<input type="text" id="txtSearchDivisions"/>
	</div>
	<table id="tblDivisionList" class="dataTable">
		<thead>
			<tr>
				<th width="30%">Number</th>
				<th width="70%">Name</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<div id="divAccountList" style="display: none">
	<img id="imgCloseAList" src="${pageContext.request.contextPath}/images/cal_close.gif"
		class="imgCloseComboList"/>
	<div>
		<input type="text" id="txtSearchAccounts"/>
	</div>
	<table id="tblAccountList" class="dataTable">
		<thead>
			<tr>
				<th width="30%">Number</th>
				<th width="70%">Name</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<div id="divForm" class="formDivBigForms">
	<form:form method="POST" commandName="generalLedgerDto">
		<div class="modFormLabel">General Journal<span class="btnClose" id="btnClose">[X]</span></div>
		<br>
		<div class="modForm">
		<form:hidden path="generalLedger.id"/>
		<form:hidden path="generalLedger.ebObjectId" id="hdnEbObjectId"/>
		<form:hidden path="generalLedger.sequenceNo"/>
		<form:hidden path="generalLedger.glStatusId" id="glStatusId"/>
		<form:hidden path="generalLedger.glEntrySourceId" id="glEntrySourceId"/>
		<form:hidden path="generalLedger.createdBy"/>
		<form:hidden path="generalLedger.formWorkflowId"/>
		<form:hidden path="difference"/>
		<form:hidden path="jsonData"/>
		<fieldset class="frmField_set">
			<legend>System Generated</legend>
			<table id="tblHeader" class="formTable" width="100%">
				<tr>
					<td class="labels">Journal Voucher No.</td>
					<td class="value">
						<input type="text" id="txtSequenceNo" class="textBoxToLabel" readonly="readonly"/>
						<input type="hidden" id="jvNumber"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Status</td>
					<td class="value">
						<c:choose>
							<c:when test="${generalLedgerDto.generalLedger.formWorkflow != null}">
								${generalLedgerDto.generalLedger.formWorkflow.currentFormStatus.description}
							</c:when>
							<c:otherwise>
								NEW
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td class="labels">Entry Source</td>
					<td class="value">
						<input type="text" id="txtEntrySource" class="textBoxToLabel"  readonly="readonly" />
					</td>
				</tr>
			</table>
		</fieldset>
		<fieldset class="frmField_set">
			<legend>GL Header</legend>
			<table id="tblHeader" class="formTable" width="100%">
				<tr>
					<td class="labels">* Date</td>
					<td class="value">
						<form:input path="generalLedger.glDate" id="date" onblur="evalDate('date')" style="width: 120px;" class="dateClass2"/>
						<img id="imgDate" src="${pageContext.request.contextPath}/images/cal.gif"
							onclick="javascript:NewCssCal('date')" style="cursor:pointer" style="float: right;"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2">
						<form:errors path="generalLedger.glDate"
							cssClass="error" style="margin-left: 12px;"/>
					</td>
				</tr>
				<tr>
					<td class="descriptionLabel">* Description</td>
					<td class="value">
						<form:textarea path="generalLedger.comment" id="txtComment" cols="70" rows="5" class="addressClass"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2">
						<form:errors path="generalLedger.comment"
							cssClass="error" style="margin-left: 12px;"/>
					</td>
				</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
			<legend>GL Lines</legend>
			<table id="tblGlForm" class="formTable" width="100%">
				<tr>
					<td colspan="2">
						<br>
						<div class="gleTable" >
							<br>
							<table id="glTable" class="dataTable">
								<thead>
									<tr>
										<th width="1%"></th>
										<th width="3%">#</th>
										<th width="7%">Company</th>
										<th width="7%">Division</th>
										<th width="7%">Account</th>
										<th width="20%">Combination</th>
										<th width="15%">Debit</th>
										<th width="15%">Credit</th>
										<th width="25%">Description</th>
									</tr>
								</thead>
								<tbody>
								</tbody>
								<tfoot>
									<tr>
										<td colspan="6"> </td>
										<td id="tdDebitTotal" style="text-align: right;">0.00</td>
										<td id="tdCreditTotal" style="text-align: right;">0.00</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="difference" cssClass="error"/>
										</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="glEntry.accountCombinationId" cssClass="error"/>
										</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="glEntry.amount" cssClass="error"/>
										</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="glEntrySize" cssClass="error"/>
										</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="glEntry.companyId" cssClass="error"/>
										</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="generalLedger.glStatusId" cssClass="error"/>
										</td>
									</tr>
									<tr>
										<td colspan="8">
											<form:errors path="gLlineMessage" cssClass="error" />
										</td>
									</tr>
								</tfoot>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</fieldset>
		<table class="frmField_set">
			<tr>
				<td><form:errors path="generalLedger.formWorkflowId" cssClass="error"/></td>
			</tr>
		</table>
		<br>
		<div align="right" style="margin-right: 15px;" class="buttonClss">
			<input type="button" id="btnSaveGlEntry" value="Save" onclick="saveGeneralLedger();"/>
		</div>
		</div>
	</form:form>
</div>
</body>
</html>