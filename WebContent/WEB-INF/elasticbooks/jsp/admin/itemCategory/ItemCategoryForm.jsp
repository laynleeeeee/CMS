<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>

<!-- Description: The Form for Item Category -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript">
var $accountSetupsTableObj = null;
var itemCategoryId = 0;
$(document).ready (function () {
	initializeTable();
	itemCategoryId = "${itemCategory.id}";
});

function initializeTable () {
	var accountSetupsJson = JSON.parse($("#accountSetupsJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
	$accountSetupsTableObj = $("#accountSetupsTable").editableItem({
		data: accountSetupsJson,
		jsonProperties: [
						{"name" : "id", "varType" : "int"},
						{"name" : "companyId", "varType" : "int"},
						{"name" : "companyName", "varType" : "string"},
						{"name" : "divisionId", "varType" : "int"},
						{"name" : "divisionName", "varType" : "string"},
						{"name" : "costAccount", "varType" : "int"},
						{"name" : "costAccountName", "varType" : "string"},
						{"name" : "inventoryAccount", "varType" : "int"},
						{"name" : "inventoryAccountName", "varType" : "string"},
						{"name" : "salesAccount", "varType" : "int"},
						{"name" : "salesAccountName", "varType" : "string"},
						{"name" : "salesDiscountAccount", "varType" : "int"},
						{"name" : "salesDiscountAccountName", "varType" : "string"},
						{"name" : "salesReturnAccount", "varType" : "int"},
						{"name" : "salesReturnAccountName", "varType" : "string"},
						{"name" : "active", "varType" : "boolean"},
		],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "companyId",
					"cls" : "companyId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Company",
					"cls" : "companyName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "divisionId",
					"cls" : "divisionId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Division",
					"cls" : "divisionName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "costAccount",
					"cls" : "costAccount",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Cost Account",
					"cls" : "costAccountName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "inventoryAccount",
					"cls" : "inventoryAccount",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Inventory Account",
					"cls" : "inventoryAccountName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "salesAccount",
					"cls" : "salesAccount",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Sales Account",
					"cls" : "salesAccountName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "salesDiscountAccount",
					"cls" : "salesDiscountAccount",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Sales Discount Account",
					"cls" : "salesDiscountAccountName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "salesReturnAccount",
					"cls" : "salesReturnAccount",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Sales Return Account",
					"cls" : "salesReturnAccountName tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "10%"},
				{"title" : "Active",
					"cls" : "active tblInputText",
					"editor" : "checkbox",
					"visible" : true ,
					"width" : "2%"}
				],
	"disableDuplicateStockCode" : false
	});

	$("#accountSetupsTable").on("keydown keyup", ".companyName", function(){
		showCompanies(this);
	});

	$("#accountSetupsTable").on("blur", ".companyName", function(){
		getCompany(this);
	});

	$("#accountSetupsTable").on("keydown keyup", ".divisionName", function(){
		showDivisions(this);
	});

	$("#accountSetupsTable").on("blur", ".divisionName", function(){
		getDivision(this);
	});

	$("#accountSetupsTable").on("keydown keyup", ".costAccountName", function(){
		var $accountId = $(this).closest("tr").find(".costAccount");
		showAccts(this, $accountId);
	});

	$("#accountSetupsTable").on("keydown keyup", ".inventoryAccountName", function(){
		var $accountId = $(this).closest("tr").find(".inventoryAccount");
		showAccts(this, $accountId);
	});

	$("#accountSetupsTable").on("keydown keyup", ".salesAccountName", function(){
		var $accountId = $(this).closest("tr").find(".salesAccount");
		showAccts(this, $accountId);
	});

	$("#accountSetupsTable").on("keydown keyup", ".salesDiscountAccountName", function(){
		var $accountId = $(this).closest("tr").find(".salesDiscountAccount");
		showAccts(this, $accountId);
	});

	$("#accountSetupsTable").on("keydown keyup", ".salesReturnAccountName", function(){
		var $accountId = $(this).closest("tr").find(".salesReturnAccount");
		showAccts(this, $accountId);
	});

	$("#accountSetupsTable").on("blur", ".costAccountName", function(){
		var $accountId = $(this).closest("tr").find(".costAccount");
		getAccountCombi(this, $accountId);
	});

	$("#accountSetupsTable").on("blur", ".inventoryAccountName", function(){
		var $accountId = $(this).closest("tr").find(".inventoryAccount");
		getAccountCombi(this, $accountId);
	});

	$("#accountSetupsTable").on("blur", ".salesAccountName", function(){
		var $accountId = $(this).closest("tr").find(".salesAccount");
		getAccountCombi(this, $accountId);
	});

	$("#accountSetupsTable").on("blur", ".salesDiscountAccountName", function(){
		var $accountId = $(this).closest("tr").find(".salesDiscountAccount");
		getAccountCombi(this, $accountId);
	});

	$("#accountSetupsTable").on("blur", ".salesReturnAccountName", function(){
		var $accountId = $(this).closest("tr").find(".salesReturnAccount");
		getAccountCombi(this, $accountId);
	});
}

function showCompanies ($txtBox) {
	var uri = contextPath + "/getCompany/byName?companyName="+$.trim($($txtBox).val()
			+ "&isActiveOnly=true&limit=10");
	var $companyId = $($txtBox).closest("tr").find(".companyId");
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			var company = ui.item;
			$(this).val(ui.item.name);
			processcompanyName($txtBox, company);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					var compId = $($companyId).val();
					var comp = $($txtBox).val();
					if (ui.item != null && compId == ui.item.id) {
						$($txtBox).closest("tr").find(".divisionId").val("");
						$($txtBox).closest("tr").find(".divisionName").val("");
						clearFields($txtBox);
						if(comp != "") {
							processcompanyName($txtBox, ui.item);
						}
					}
				},
				dataType: "json",
			});
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.name +"</a>" )
			.appendTo( ul );
	};
}

function processcompanyName($txtBox, company) {
	var $companyId = $($txtBox).closest("tr").find(".companyId");
	var $companyName = $($txtBox).closest("tr").find(".companyName");
	if (company == undefined) {
		$companyId.val("");
		return;
	}
	$companyId.val(company.id);
	$($txtBox).val(company.name);
	$companyName.html(company.companyName);
}

function getCompany($txtBox) {
	$($txtBox).closest("tr").find(".companyId").val("");
	var $companyId = $($txtBox).closest("tr").find(".companyId");
	var uri = contextPath + "/getCompany/byName?companyName="+$.trim($($txtBox).val())
			+ "&isActiveOnly=true&limit=1";

	if($($txtBox).val() != null && $.trim($($txtBox).val()) != ""
		&& $.trim($($txtBox).val()) != "0"){
		$.ajax({
			url: uri,
			success : function(company) {
				if (company != null && company[0] != undefined) {
					$("#setupErrorMessage").text("");
					processcompanyName($txtBox, company[0]);
					getAcctCombinations($txtBox);
				} else {
					$companyId.val("");
					$($txtBox).focus();
					$("#setupErrorMessage").text("Invalid Company.");
				}
			},
			dataType: "json"
		});
	} else {
		$("#setupErrorMessage").text("");
		$($txtBox).closest("tr").find(".divisionId").val("");
		$($txtBox).closest("tr").find(".divisionName").val("");
		$companyId.val("");
		$($txtBox).val("");
		clearFields($txtBox);
	}
}

function clearFields($txtBox) {
	$($txtBox).closest("tr").find(".costAccount").val("");
	$($txtBox).closest("tr").find(".costAccountName").val("");
	$($txtBox).closest("tr").find(".inventoryAccount").val("");
	$($txtBox).closest("tr").find(".inventoryAccountName").val("");
	$($txtBox).closest("tr").find(".salesAccount").val("");
	$($txtBox).closest("tr").find(".salesAccountName").val("");
	$($txtBox).closest("tr").find(".salesDiscountAccount").val("");
	$($txtBox).closest("tr").find(".salesDiscountAccountName").val("");
	$($txtBox).closest("tr").find(".salesReturnAccount").val("");
	$($txtBox).closest("tr").find(".salesReturnAccountName").val("");
}

function showDivisions($txtBox) {
	var $companyId = $($txtBox).closest("tr").find(".companyId");
	var division = $($txtBox).val();
	var $divisionId = $($txtBox).closest("tr").find(".divisionId");
	var uri = contextPath + "/getDivisions/new?companyId="+$companyId.val()+"&divisionNumber="+division+"&isActive=true&limit=10";
	$($txtBox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			if($($divisionId).val() != ui.item.id){
				clearFields($txtBox);
			}
			$($divisionId).val(ui.item.id);
			$($txtBox).val(ui.item.name);
			return false;
		}, minLength: 2,
		change: function(event, ui){
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						if($($divisionId).val() != ui.item.id){
							clearFields($txtBox);
						}
						$($divisionId).val(ui.item.id);
						$($txtBox).val(ui.item.name);
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

function getDivision($txtBox) {
	var companyId = $($txtBox).closest("tr").find(".companyId").val();
	var division = $($txtBox).val();
	var $divisionId = $($txtBox).closest("tr").find(".divisionId");

	if(companyId != null && companyId != "" && $.trim($($txtBox).val()) != ""
		&& $.trim($($txtBox).val()) != "0"){
		var uri = contextPath + "/getDivisions/new?companyId="+companyId
				+"&divisionNumber="+division+"&isActive=true&limit=1";
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null && item[0] != undefined) {
					if($($divisionId).val() != item[0].id){
						clearFields($txtBox);
					}
					$("#setupErrorMessage").text("");
					$divisionId.val(item[0].id);
					$($txtBox).val(item[0].name);
					getAcctCombinations($txtBox);
				} else {
					if($.trim($($txtBox).val()) != ""){
						$($divisionId).val("");
						clearFields($txtBox);
						$($txtBox).focus();
						$("#setupErrorMessage").text("Invalid Division.");
					}
				}
			},
			dataType: "json"
		});
	} else {
		$divisionId.val("");
		clearFields($txtBox);
		if($.trim($($txtBox).val()) != ""){
			$($txtBox).focus();
			$("#setupErrorMessage").text("Invalid Division.");
		} else {
			$("#setupErrorMessage").text("");
			$($txtBox).val("");
		}
	}
}

function showAccts($txtBox, $accountId) {
	var accountName = $.trim($($txtBox).val());
	var companyId = $($txtBox).closest("tr").find(".companyId").val();
	var divisionId = $($txtBox).closest("tr").find(".divisionId").val();
	if(companyId != null && companyId != "" && divisionId != null && divisionId != ""){
		var uri = contextPath+"/getAccntCombination?companyId="+companyId+"&divisionId="
				+divisionId+"&accountName="+accountName+"&limit=10";
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$($txtBox).val(ui.item.account.accountName);
				$($accountId).val(ui.item.id);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null) {
							$($txtBox).val(ui.item.account.accountName);
							$($accountId).val(ui.item.id);
						}
					},
					dataType: "json",
				});
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.account.number+"-"+item.account.accountName+ "</a>" )
				.appendTo( ul );
		};
	}
}

function getAccountCombi($txtBox, $accountId) {
	var accountName = $.trim($($txtBox).val());
	var companyId = $($txtBox).closest("tr").find(".companyId").val();
	var divisionId = $($txtBox).closest("tr").find(".divisionId").val();
	if(companyId != null && companyId != "" && divisionId != null && divisionId != "" && accountName != ""
		&& $.trim($($txtBox).val()) != "0"){
		var uri = contextPath+"/getAccntCombination?companyId="+companyId+"&divisionId="
				+divisionId+"&accountName="+accountName+"&limit=1";
		$.ajax({
			url: uri,
			success : function(actCombi) {
				if (actCombi != null && actCombi[0] != undefined) {
					$("#setupErrorMessage").text("");
					$($txtBox).val(actCombi[0].account.accountName);
					$($accountId).val(actCombi[0].id);
				} else {
					if($.trim($($txtBox).val()) != ""){
						$accountId.val("");
						$($txtBox).focus();
						$("#setupErrorMessage").text("Invalid Account.");
					}
				}
			},
			dataType: "json"
		});
	} else {
		$accountId.val("");
		if($.trim($($txtBox).val()) != ""){
			$($txtBox).focus();
			$("#setupErrorMessage").text("Invalid Account.");
		} else {
			$("#setupErrorMessage").text("");
			$($txtBox).val("");
		}
	}
}

function saveCategory() {
	$("#btnSaveItemCategory").attr("disabled", "disabled");
	//console.log($accountSetupsTableObj.getData());
	$("#accountSetupsJsonId").val($accountSetupsTableObj.getData());
	doPostWithCallBack ("itemCategory", "divItemCategoryForm", function (data) {
		if (data.substring(0,5) == "saved") {
			$("#spanCategoryMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") +  $("#name").val() + " item category.");
			searchItemCategory();
			$("#divItemCategoryForm").html("");
		} else {
			//console.log("data from server with error : " + data);
			$("#divItemCategoryForm").html(data);
		}
	});
}

function getAcctCombinations($txtBox) {
	var companyId = $($txtBox).closest("tr").find(".companyId").val();
	var divisionId = $($txtBox).closest("tr").find(".divisionId").val();
	var isEmpty = isEmptyAcctCombis($txtBox);
	if(isEmpty && companyId != "" && divisionId != "") {
		$.ajax({
			url: contextPath + "/admin/itemCategories/getAcctCombis?companyId=" + companyId
					+ "&divisionId=" + divisionId,
			success : function(ItemAccCatSetUp) {
				processAccounts(ItemAccCatSetUp, $txtBox)
			},
			dataType: "json"
		});
	}
}

function isEmptyAcctCombis($txtBox) {
	return $($txtBox).closest("tr").find(".costAccountName").val() == "" 
			&& $($txtBox).closest("tr").find(".inventoryAccountName").val() == "" 
			&& $($txtBox).closest("tr").find(".salesAccountName").val() == "" 
			&& $($txtBox).closest("tr").find(".salesDiscountAccountName").val() == "" 
			&& $($txtBox).closest("tr").find(".salesReturnAccountName").val() == "";
}

function processAccounts(ItemAccCatSetUp, $txtBox) {
	$($txtBox).closest("tr").find(".costAccount").val(ItemAccCatSetUp.costAccount);
	$($txtBox).closest("tr").find(".costAccountName").val(ItemAccCatSetUp.costAccountName);
	$($txtBox).closest("tr").find(".inventoryAccount").val(ItemAccCatSetUp.inventoryAccount);
	$($txtBox).closest("tr").find(".inventoryAccountName").val(ItemAccCatSetUp.inventoryAccountName);
	$($txtBox).closest("tr").find(".salesAccount").val(ItemAccCatSetUp.salesAccount);
	$($txtBox).closest("tr").find(".salesAccountName").val(ItemAccCatSetUp.salesAccountName);
	$($txtBox).closest("tr").find(".salesDiscountAccount").val(ItemAccCatSetUp.salesDiscountAccount);
	$($txtBox).closest("tr").find(".salesDiscountAccountName").val(ItemAccCatSetUp.salesDiscountAccountName);
	$($txtBox).closest("tr").find(".salesReturnAccount").val(ItemAccCatSetUp.salesReturnAccount);
	$($txtBox).closest("tr").find(".salesReturnAccountName").val(ItemAccCatSetUp.salesReturnAccountName);
}
</script>
<title>Item Category</title>
</head>
<body>
<form:form method="POST" commandName = "itemCategory">
	<div class="formDivBigForms">
		<div class="modFormLabel">Item Category</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
			<legend>* Category Information</legend>
			<table class="formTable">
				<form:hidden path="id"/>
				<form:hidden path="accountSetupsJson" id="accountSetupsJsonId"/>
				<tr>
					<td class="labels">Name:</td>
					<td class="value">
						<form:input path="name" class="inputSmall"/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td class="value" colspan="2">
						<form:errors path="name" cssClass="error"/>
					</td>
				</tr>
				<tr>
					<td class="labels">Active: </td>
					<td class="value">
						<form:checkbox path="active"/>
					</td>
				</tr>
			</table>
			<br>
			<div id="accountSetupsTable"></div>
			<form:errors path="errorMessage" cssClass="error" id="errorMessage" />
			<br>
			<span id="setupErrorMessage" class="error"></span>
		</fieldset>
		<table class="frmField_set">
			<tr>
				<td align="right">
					<input id="btnSaveItemCategory" align="right" type="button" value="${itemCategory.id eq 0 ? 'Save' : 'Update'}" onclick="saveCategory();"/>
					<input id="btnCancelItemCategory" align="right" type="button" value="Cancel" onclick="cancelForm();"/>
				</td>
			</tr>
		</table>
		</div>
	</div>
</form:form>
</body>
</html>