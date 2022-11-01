<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Product Line Form.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebForm.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ybl/inputUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/select-common.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript">
var $productLineObj = null;
var initProductLineId = 0;
$(document).ready (function () {
	initializeTable();
	initMainItemId = "${productLine.mainItemId}";
});

function loadItems ($txtbox, isMainItem) {
	var stockCode = $.trim($($txtbox).val());
	var uri = contextPath+"/getRItems/filter?stockCode="+processSearchName(stockCode);
	var $description = $($txtbox).closest("tr").find(".description");
	var $uom = $($txtbox).closest("tr").find(".uom");
	var $itemId = $($txtbox).closest("tr").find(".itemId");
	$($txtbox).autocomplete({
		source: uri,
		select: function( event, ui ) {
			$(this).val(ui.item.stockCode);
			$($description).text(ui.item.description);
			$($uom).text(ui.item.unitMeasurement.name);
			if(isMainItem){
				initProductLineId = ui.item.id;
			} else {
				$($itemId).val(ui.item.id);
			}
			return false;
		}, minLength: 2,
		change: function(event, ui){
			var item = ui.item;
			$(this).val(item.stockCode);
			if(isMainItem){
				initProductLineId = item.id;
			} else {
				$($itemId).val(item.id);
			}
		}
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.data( "ui-autocomplete-item", item )
			.append( "<a style='font-size: small;'>" +item.stockCode+"-"+ item.description+"</a>" )
			.appendTo( ul );
	};
}

function getItem ($txtbox, isMainItem) {
	var companyId = $("#selectCompanyId").val();
	var stockCode = $.trim($($txtbox).val());
	var $description = $($txtbox).closest("tr").find(".description");
	var $uom = $($txtbox).closest("tr").find(".uom");
	var $itemId = $($txtbox).closest("tr").find(".itemId");
	$("#spanMainItemName").text("");
	$("#errorMessage").text("");
	if (stockCode != "") {
		$.ajax({
			url: contextPath+"/getItem?stockCode="+processSearchName(stockCode),
			success: function (item) {
				if (item != null) {
					$(this).val(item.stockCode);
					$($description).text(item.description);
					$($uom).text(item.unitMeasurement.name);
					if(isMainItem){
						initMainItemId = item.id;
					} else {
						$($itemId).val(item.id);
					}
				} else {
					if(isMainItem){
						$("#spanMainItemName").text("Invalid stock code.");
					} else {
						$("#errorMessage").text("Invalid stock code.");
					}
					$($txtbox).focus();
				}
			},
			error : function(error) {
				$($description).text("");
				$($uom).text("");
				if(isMainItem){
					initMainItemId = null;
					$("#spanMainItemName").text("Invalid stock code.");
				} else {
					$($itemId).val(null);
					$("#errorMessage").text("Invalid stock code.");
				}
				$($txtbox).focus();
			},
			dataType: "json"
		});
	} else {
		$($description).text("");
		$($uom).text("");
		if(isMainItem){
			initMainItemId = null;
		} else {
			$($itemId).val(null);
		}
	}
}

function initializeTable () {
	var productLine = JSON.parse($("#productLineItemsJsonId").val());
	var cPath = "${pageContext.request.contextPath}";
	$productLineObj = $("#productLineItemTable").editableItem({
		data: productLine,
		jsonProperties: [
						{"name" : "id", "varType" : "int"},
						{"name" : "productLineId", "varType" : "int"},
						{"name" : "itemId", "varType" : "int"},
						{"name" : "itemStockCode", "varType" : "string"},
						{"name" : "description", "varType" : "string"},
						{"name" : "quantity", "varType" : "double"},
						{"name" : "uom", "varType" : "string"},
		],
		contextPath: cPath,
		header:[
				{"title" : "id",
					"cls" : "id",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "productLineId",
					"cls" : "productLineId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "itemId",
					"cls" : "itemId",
					"editor" : "hidden",
					"visible" : false },
				{"title" : "Stock Code",
					"cls" : "itemStockCode tblInputText",
					"editor" : "text",
					"visible" : true ,
					"width" : "25%"},
				{"title" : "Description", 
					"cls" : "description tblLabelNumeric", 
					"editor" : "label",
					"visible" : true, 
					"width" : "35%"},
				{"title" : "Qty", 
					"cls" : "quantity tblInputNumeric", 
					"editor" : "text",
					"visible" : true,
					"width" : "20%" },
				{"title" : "UOM", 
					"cls" : "uom tblLabelNumeric", 
					"editor" : "label",
					"visible" : true, 
					"width" : "10%"},
				],
	"disableDuplicateStockCode" : false
	});

	$("#productLineItemTable").on("keydown keyup", ".itemStockCode", function(){
		loadItems(this, false);
	});

	$("#productLineItemTable").on("blur", ".quantity", function(){
		formatMoney(this);
	});

	$("#productLineItemTable").on("blur", ".itemStockCode", function(){
		getItem(this, false);
	});
}

function cancelForm() {
	$("#divProductLineForm").html("");
}

function formatMoney($text){
	$($text).val(accounting.formatMoney($($text).val()));
}


function saveProductLine() {
	$("#mainItemId").val(initMainItemId);
	$("#productLineItemsJsonId").val($productLineObj.getData());
	var mainItemId = initMainItemId;
	$("#btnSaveProductLine").attr("disabled", "disabled");
		doPostWithCallBack ("productLine", "divProductLineForm", function (data) {
			if (data.substring(0,5) == "saved") {
				$("#spanProcductLineMsg").html("Successfully " + ($("#id").val() != 0 ? "updated " : "added ") + "Product line of " 
						+ $("#txtProductLineItem").val() + ".");
				searchProductLine();
				dojo.byId("divProductLineForm").innerHTML = "";
				isSaving = false;
			} else {
				$("#divProductLineForm").html(data);
				initMainItemId = mainItemId;
				isSaving = false;
			}
		});
		$("#btnSaveProductLine").removeAttr("disabled");
}

</script>
<title>Product Line Form</title>
</head>
<body>
<div class="formDiv">
		<form:form method="POST" commandName="productLine">
			<form:hidden path="id" id="id"/>
			<form:hidden path="mainItemId" id="mainItemId"/>
			<form:hidden path="createdBy"/>
			<form:hidden path="createdDate"/>
			<form:hidden path="productLineItemsJson" id="productLineItemsJsonId"/>
			<div class="modFormLabel">Product Line</div>
			<br>
			<div class="modForm">
				<fieldset class="frmField_set">
					<legend>* Basic Information</legend>
					<table class="formTable">
						<tr>
							<td class="labels">* Product Line</td>
							<td class="value">
								<form:input path="mainItemName" cssClass="input" id="txtProductLineItem" onkeydown="loadItems(this, true);" 
									onkeyup="loadItems(this, true);" onblur="getItem(this, true)"/>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2" style="text-indent: 15px;">
								<span id="spanMainItemName" class="error"></span>
							</td>
						</tr>
						<tr>
							<td></td>
							<td colspan="2" style="text-indent: 15px;">
								<form:errors path="mainItemName" cssClass="error" id="userMainItemNameError"/>
							</td>
						</tr>
						<tr>
							<td class="labels">Active:</td>
							<td class="value">
								<form:checkbox path="active"/>
							</td>
						</tr>
					</table>
					<br>
				</fieldset>
				<fieldset class="frmField_set">
					<legend>* Raw Materials</legend>
					<div id="productLineItemTable"></div>
						<form:errors path="errorMessage" cssClass="error" />
						<br>
						<span id="errorMessage" class ="error"></span>
				</fieldset>
				<table class="formDiv">
					<tr>
						<td></td>
						<td align="right">
							<input type="button" id="btnSaveProductLine" value="${productLine.id eq 0 ? 'Save' : 'Update'}" onclick="saveProductLine();" />
							<input type="button" id="btnCancelProductLine" value="Cancel" onclick="cancelForm();" /></td>
					</tr>
				</table>
			</div>
		</form:form>
	</div>
	<hr class="thin" />
</body>
</html>