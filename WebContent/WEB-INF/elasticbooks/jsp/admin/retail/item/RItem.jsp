<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp" %>
<!--

	Description: Retail Item main page.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Items</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/inputUtil.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript">
	$(function () {
		$("#txtStockCode, #txtDescription").bind("keypress", function (e) {
			if (e.which == 13) {
				searchItem();
				e.preventDefault();
			}
		});

		$("#btnSearchItem").click(function () {
			searchItem();
		});

		$("#btnAddItem").click(function (){
			$("#divItemForm").load(contextPath + "/admin/rItems/form", function () {
				$("html, body").animate({scrollTop: $("#divItemForm").offset().top}, 0050);
			});
		});

		$("#btnEditItem").click(function (){
			var id = getCheckedId ("cbItem");
			$("#divItemForm").load(contextPath + "/admin/rItems/form?itemId="+id, function () {
				$("html, body").animate({scrollTop: $("#divItemForm").offset().top}, 0050);
			});
		});

		$(document).on("click", "#btnCancelSave", function() {
			cancelSave ();
		});
	});

	function saveRItem() {
		parseDoubles();
		setCbValues();
		$("#btnSaveRItem").attr("disabled", "disabled");
		doPostWithCallBack ("rItemForm", "form", function (data) {
			if (data.substring(0,5) == "saved") {
				$("#spanItemMessage").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "item with Stock No. " + $("#stockCode").val() + ".");
				$("#divItemForm").html("");
				searchItem();
			} else {
				$("#divItemForm").html(data);
				parseDoubles();
				formatMonetaryVal();
			}
			$("#btnSaveRItem").removeAttr("disabled");
		});
	}

	function getCommonParam() {
		var stockCode = processSearchName($("#txtStockCode").val());
		var description = processSearchName($("#txtDescription").val());
		var unitMeasurementId = $("#selectUnitMeasurement").val();
		var itemCategoryId = $("#selectItemCategory").val();
		var status = $("#selectStatus").val();
		return "stockCode="+stockCode+"&description="+description+"&unitMeasurementId="+unitMeasurementId+
			"&itemCategoryId="+itemCategoryId+"&status="+status;
	}

	function searchItem () {
		$("#divRItemTable").load(contextPath+"/admin/rItems?"+getCommonParam()+"&pageNumber=1");
	}

	function cancelSave () {
		$("#divItemForm").html("");
		searchItem();
	}

	function parseDoubles() {
		$(".txtSrp").each(function(i) {
			var srp = $(this).val();
			$(this).val(removeComma(srp));
		});

		$(".txtValue").each(function(i) {
			var value = $(this).val();
			var noPercentage = value.replace('%', '');
			$(this).val(removeComma(noPercentage));
		});

		$(".txtWp").each(function(i) {
			var srp = $(this).val();
			$(this).val(removeComma(srp));
		});
	}

	function setCbValues () {
		$(".cbStatus").each (function () {
			$(this).val($(this).is(":checked"));
		});
	}

	function formatMoney (textboxId) {
		var money = accounting.formatMoney($(textboxId).val());
		$(textboxId).val(money.replace(/[$]/gi, ''));
	}

	function formatPercentage (textboxId) {
		formatMoney(textboxId);
		$(textboxId).val($(textboxId).val() + "%");
	}

	function formatValue (textboxId) {
		var typeId = $(textboxId).parent("td").parent("tr").
			closest("tr").find("select").val();
		if (typeId == 1) {
			formatPercentage(textboxId);
		} else {
			formatMoney(textboxId);
		}
	}

	function formatMonetaryVal() {
		$(".txtSrp").each(function(i) {
			formatMoney($(this));
		});

		$(".txtValue").each(function(i) {
			formatValue($(this));
		});

		$(".txtWp").each(function(i) {
			formatValue($(this));
		});
	}
</script>
</head>
<body>
	<div id="divSearch">
		<table class="formTable">
			<tr>
				<td class="title">Stock Code</td>
				<td><input type="text" id="txtStockCode" maxLength="100" size="20" class="inputSmall" /></td>
			</tr>
			<tr>
				<td class="title">Description</td>
				<td><input type="text" id="txtDescription" maxLength="100" size="20" class="inputSmall"/></td>
			</tr>
			<tr>
				<td class="title">Unit of Measure</td>
				<td>
					<select id="selectUnitMeasurement" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="um" items="${unitMeasurements}">
							<option value="${um.id}">${um.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Item Category</td>
				<td>
					<select id="selectItemCategory" class="frmSmallSelectClass">
						<option value="-1">All</option>
						<c:forEach var="ic" items="${itemCategories}">
							<option value="${ic.id}">${ic.name}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="title">Status</td>
				<td>
					<select id="selectStatus" class="frmSmallSelectClass">
						<option value=-1>All</option>
						<option value=1>Active</option>
						<option value=0>Inactive</option>
					</select>
					<input type="button" id="btnSearchItem" value="Search" />
				</td>
			</tr>
		</table>
	</div>
	<span id="spanItemMessage" class="message"></span>
	<div id="divRItemTable">
		<%@ include file="RItemTable.jsp" %>
	</div>
	<div class="controls">
		<input type="button" id="btnAddItem" value="Add"/>
		<input type="button" id="btnEditItem" value="Edit" />
	</div>
	<div id="divItemForm" style="margin-top: 50px;"></div>
</body>
</html>