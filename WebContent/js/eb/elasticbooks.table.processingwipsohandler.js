/**
 * Object that handles the PIAD - Special Order table. 
 * This is only applicable with table with the following columns.
 * 
 * There should be a select object that contains the different company in the header. 
 * 
 * 1. Stock code (text)
 * 2. Description
 * 3. Existing Stocks
 * 4. Warehouse (Select)
 * 5. Qty (Input)
 * 6. UOM
 * 7. SRP
 * 8. Amount
 * 

 * 
 */

var DT_PERCENTAGE = 1;
var DT_AMOUNT = 2;
var DT_QTY_DISCOUNT = 3;
SalesTableHandler = function (totalHandler) {

	this.totalHandler = totalHandler;

	this.showItems = function (setting, $table, $txtBox) {
//		console.log("inside sales table handler");
		var warehouseId = $("#warehouseId").val();
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getRItems/filter?stockCode="+stockCode;
		if (warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
//		console.log("autocomplete. stock code: "+stockCode);
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentItem = ui.item;
				$(this).val (ui.item.stockCode);
				return false;
			}, minLength: 1
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>"+ item.stockCode + "-" +item.description + "</a>" )
				.appendTo( ul );
		};
	};

	//Auto complete function for AR Line Setup
	var currentALSetup = null;
	this.showArLineSetups = function (setting, $table, $txtBox) {
		console.log("Showing the list of AR Line Setups.");
		var containerId =  $table.parent("div").attr("id");
		var customerAcctId = $("#arCustomerAcctId").val();
		var arLineSetupName = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getArLineSetups?name="+arLineSetupName;
		if (containerId == "oFArLinesTbl" || containerId == "tuitionsTbl") {
			var psId = $("#paymentSchemeId").val();
			uri = contextPath+"/getArLineSetups/enrolment?paymentSchemeId="+psId+"&name="+arLineSetupName+
				"&tuition=" + (containerId == "tuitionsTbl" ? "true" : "false");
		}
		if(customerAcctId != undefined) {
			uri += "&arCustAcctId="+customerAcctId;
		} else {
			//AR Customer Account is a required parameter
			return;
		}

		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentALSetup = ui.item;
				$(this).val (ui.item.name);
				//No validation for duplicate AR Line Setup
				var $alSetupId = $($txtBox).closest("tr").find(".arLineSetupId");
				$alSetupId.val(ui.item.id);
				return false;
			}, minLength: 1,
			change: function(event, ui){
				var setupName = $(this).val();
				if (currentALSetup != null && setupName == currentALSetup.name){
					return false;
				}
				var $alSetupId = $($txtBox).closest("tr").find(".arLineSetupId");
				$alSetupId.val(ui.item.id);
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, alSetup ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", alSetup )
				.append( "<a style='font-size: small;'>"+ alSetup.name + "</a>" )
				.appendTo( ul );
		};
	};

	//Auto complete for UOM
	var currentUom = null;
	this.showUoms = function (setting, $table, $txtBox) {
		console.log("Showing the list of Unit of Measurements.");
		var uomName = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getUnitMeasurements?name="+uomName;
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentUom = ui.item;
				$(this).val (ui.item.name);
				$(this).closest("tr").find(".unitOfMeasurementId").val(ui.item.id);
				return false;
			}, minLength: 1,
			change: function(event, ui){
				var uom = $(this).val();
				if (currentUom != null && uom == currentUom.name){
					return false;
				}
				$(this).closest("tr").find(".unitOfMeasurementId").val(ui.item.id);
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, uom ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", uom )
				.append( "<a style='font-size: small;'>"+ uom.name + "</a>" )
				.appendTo( ul );
		};
	};

	function emptyValues (setting, $table, $txtBox) {
		$($txtBox).closest("tr").find(".quantity").val("");
		$($txtBox).closest("tr").find(".amount").html("");
		computeTotal (setting, $table, this.totalHandler);
	}

	this.initRowData = function (setting, $table, $txtBox) {
		var totalHandler = this.totalHandler;
		var doAfterProcessItem = {
			process : function () {
				processAmount (setting, $table, totalHandler, $txtBox);
			}
		};
		processItem(setting, $table, $txtBox, doAfterProcessItem);
	};

	this.processSelectedItems = function (setting, $table, $txtBox) {
		var totalHandler = this.totalHandler;
		var doAfterProcessItem = {
			process : function () {
				processAmount (setting, $table, totalHandler, $txtBox);
			}
		};
		processItem(setting, $table, $txtBox, doAfterProcessItem);
	};

	this.computeRow = function (setting, $table, $tr, $input) {
		processAmount (setting, $table, totalHandler, $input);
	};

	this.onDelete = function (setting, $table, isOtherCharge) {
		computeTotal (setting, $table, this.totalHandler);
	};
};

function validateStockCode($table, $txtBox, currentItem, doAfterProcessItem) {
//	console.log("validate Stock code ");
	var currentRow = Number($($txtBox).closest("tr").find(".rowNumber").text());
	var currentItemId = currentItem.id;
	var itemIdPerRow = null;
	var isDuplicateStockCode = false;
	console.log("current item id: "+currentItemId+" at row "+currentRow);
	$($table).find(" tbody tr ").each(function(row) {
		if(Number(row+1) != currentRow) {
			itemIdPerRow = $(this).find(".itemId").val();
//			console.log("item id: "+itemIdPerRow+" at row "+row);
			if(currentItemId != "") {
				if(currentItemId == itemIdPerRow) {
					isDuplicateStockCode = true;
//					console.log("duplicate stock code!");
					return false;
				} else {
//					console.log("calling processStockCode func. "+currentItem.id);
					processStockCode($table, $txtBox, currentItem, doAfterProcessItem);
					return false;
				}
			}
		} else {
			itemIdPerRow = $($txtBox).closest("tr").find(".itemId").val();
			var desc = $($txtBox).closest("tr").find(".description").val();
			if(itemIdPerRow > 0 && desc == "") {
				processStockCode($table, $txtBox, currentItem, doAfterProcessItem);
				return false;
			}
		}
	});

	if(isDuplicateStockCode) {
		$("#"+setting.itemTableMessage).text("Duplicate stock code");
		$($txtBox).focus();
	} else {
		$("#"+setting.itemTableMessage).text("");
	}
}

function processStockCode ($table, $txtBox, item, doAfterProcessItem) {
	if (item == null || item == undefined)
		return;
	console.log("current item _________ "+item.id);

	var $unitCost = $($txtBox).closest("tr").find(".unitCost");
	$unitCost.val(item.unitCost);

	var $description = $($txtBox).closest("tr").find(".description");
	$description.html(item.description);

	var $uom = $($txtBox).closest("tr").find(".uom");
	$uom.html(item.unitMeasurement.name);

	var $itemId = $($txtBox).closest("tr").find(".itemId");
	$itemId.val(item.id);

	// Empty Qty, Discount (Computed) and Amount
	var $quantity = $($txtBox).closest("tr").find(".quantity");
	if ($quantity == undefined)
		$quantity.val("");

	var $amount = $($txtBox).closest("tr").find(".amount");
	if ($amount == undefined)
		$amount.html("");
	// Loading data
	var companyId = $("#companyId").val();
	var stockCode = item.stockCode;
	if ($.trim(stockCode) != "") {
		stockCode = encodeURIComponent($.trim(stockCode));

		var $warehouse = $($txtBox).closest("tr").find(".warehouse");
		if (typeof $warehouse != "undefined") {
			var uri = contextPath + "/getWarehouse?companyId="+companyId;
			var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
			populateSelect(uri, warehouseId, $warehouse, item);
		}

		var referenceId = $($txtBox).closest("tr").find(".referenceId").val();
		var $srp = $($txtBox).closest("tr").find(".srp");
		if (typeof $($srp) != "undefined" && (referenceId == "" || referenceId == null
				|| typeof referenceId == "undefined")) {
			populateSrp(companyId, item.id, $txtBox);
		}
		doAfterProcessItem.process();
	}
}

function processItem(setting, $table, $txtBox, doAfterProcessItem) {
	var stockCode = $.trim($($txtBox).closest("tr").find(".stockCode").val());
//	console.log("============================= " + stockCode);
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "") + 
		"?stockCode="+encodeURIComponent(stockCode);
	var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
	if (warehouseId != undefined && warehouseId != "") {
		uri += "&warehouseId="+warehouseId;
	}
	var sellingTypeId = $("#hdnSellingTypeId").val();
	var isWholesale = false;
	if (sellingTypeId == 2 || sellingTypeId == 8 || sellingTypeId == 9) {
		// Filter items by retail or wholesale
		isWholesale = true;
	}
	uri += "&isWholesale="+isWholesale;
	if(stockCode != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null) {
					if(setting.disableDuplicateStockCode) {
						validateStockCode($table, $txtBox, item, doAfterProcessItem);
					} else {
						processStockCode($table, $txtBox, item, doAfterProcessItem);
					}
				}
			},error : function(error) {
				console.log(error);
				$("#"+setting.itemTableMessage).css("color", "red").text('Invalid Stock Code');
				$($txtBox).focus();
			},
			dataType: "json"
		});
	}
}

function populateSelect (uri, selectedValue, $select, item) {
	$($select).empty();
	var optionParser = {
			getValue: function (rowObject){
				if (rowObject != null)
					return rowObject["id"];
			},

			getLabel: function (rowObject){
				if (rowObject != null)
					return rowObject["name"];
			}
	};

	var postHandler = {
			doPost: function(data) {

				// This is to remove any duplication.
	 			var found = [];
				$($select).each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
					found.push(this.value);

					$(this).find("option").each(function() {
						if ($select.attr("class") == "warehouse tblSelectClass") {
							if ($(this).is(':selected')) {
								getExistingStocks($select, item.stockCode, $(this).val());
							}
						}
					});
				});
				if (typeof selectedValue != "undefined" && selectedValue != null ) {
					$($select).val(selectedValue);
				}
			}
	};
	loadPopulateObject (uri, false, selectedValue, $select, optionParser, postHandler);
}

function processAmount (setting, $table, totalHandler, $input) {
	var grossAmount = getGrossAmount($input);
	var $amount = $($input).closest("tr").find(".amount");
	$amount.html(formatDecimalPlaces(grossAmount));
	computeTotal (setting, $table, totalHandler);// compute total
}

function computeTotal (setting, $table, totalHandler) {
//	console.log("computing total ++++++++++++");
	if (setting.footer != null) {
		for (var index = 0; index < setting.footer.length; index++){
			var cls = setting.footer[index].cls;
			var total = computeTotalPerColumn($table, setting.footer[index].cls);
			var $footerSpan = $table.find("tfoot tr ."+cls);
//			console.log("$footerSpan === " + $footerSpan);
//			console.log("total === " + formatDecimalPlaces(total));
			if (totalHandler != undefined)
				totalHandler.handleTotal (total);
			$footerSpan.html(formatDecimalPlaces(total));
		}
	}

	//Computing the grand total in the form.
	//Will only work if there is a computeGrandTotal function declared in the form.
	updateOChargesAmt();
	if(typeof computeGrandTotal == 'function') {
//		console.log("sales handler. calling the grand total.");
		computeGrandTotal();
	}
}

function computeTotalPerColumn ($table, column) {
	var cls = column;
	var total = 0;
	$($table).find("tbody tr ."+cls).each(function () {
		var $span = $(this);
		var value = accounting.unformat($span.html());
		total += value;
	});
	return total;
}

function getGrossAmount ($input) {
	var $amount = $($input).closest("tr").find(".amount");
	var $srp = $($input).closest("tr").find(".srp");
	if ($amount == undefined || $srp == undefined)
		return 0;
	var $quantity = $($input).closest("tr").find(".quantity");
	var quantity = accounting.unformat($($quantity).val());
	
	var srp = accounting.unformat($srp.html()); // Unform this
	return quantity * srp;
}

function assignESByWarehouse ($select) {
	if ($select.attr("class") == "warehouse tblSelectClass") {
		if ($select.has("option").length > 0) {
			var $existingStocks = $($select).closest("tr").find(".existingStock");
			var text = $select.find(":selected").text().split(" - ");
			var formatES = text[text.length - 1];
			$existingStocks.html(formatDecimalPlaces(formatES));
		}
	}
}
function populateSrp (companyId, itemId, $txtBox) {
	var sellingTypeId = $("#hdnSellingTypeId").val();
	var $salesReferenceId = $($txtBox).closest("tr").find(".salesRefId");
	var salesReferenceId = $salesReferenceId.val();
	if(salesReferenceId < 1 || salesReferenceId == "" || salesReferenceId == undefined) {
		var uri = "/getItemSrp?companyId="+companyId+"&itemId="+itemId;
		if(sellingTypeId == 2 || sellingTypeId == 8 || sellingTypeId == 9) {
			uri += "&sellingTypeId=2";
		} 
		$.ajax({
			url: contextPath + uri,
			success : function(item) {
				if(item != null) {
					var $origSrp = $($txtBox).closest("tr").find(".origSrp");
					$origSrp.val(item.sellingPrice);

					var $srp = $($txtBox).closest("tr").find(".srp");
					$srp.html(formatDecimalPlaces(item.sellingPrice));

					var $itemSrpId =  $($txtBox).closest("tr").find(".itemSrpId");
					$itemSrpId.val(item.id);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function getExistingStocks($txtBox, stockCode, warehouseId){
	stockCode = encodeURIComponent($.trim(stockCode));
	$.ajax({
		url: contextPath + "/getAvailableStocks/total?warehouseId="
				+warehouseId+"&stockCode="+stockCode,
		success : function(availableStocks) {
			$td = $($txtBox).closest("tr").find("td").eq(5);
			$($td).css("text-align", "right");
			var $availableStocks = $($td).find(".availableStocks");
			var formatES = formatDecimalPlaces(availableStocks);
			$availableStocks.text(formatES);
		}, error : function(error) {
		},
		dataType: "json"
	});
}

function updateOChargesAmt(tdAmount) {
	var totalAmount = 0;
	var updateTotalAmt = false;
	var $footerSpan = $("#otherChargesTable").find("tfoot tr .amount");
	$("#otherChargesTable tbody tr").each(function(row) {
		var total = 0;
		var $amount = $(this).find(".amount");

		if(tdAmount > 0) {
			//Amount was updated.
			updateTotalAmt = true;
			total += accounting.unformat($amount.val());
		} else {
			//Qty and Up Amount was updated.
			updateTotalAmt = false;
			if($amount.val() == 0) {
				//Amount is zero. Amount will be computed from quantity and up amount.
				var qty = accounting.unformat($(this).find(".quantity").val());
				var upAmt = accounting.unformat($(this).find(".upAmount").val());
				total = qty * upAmt;

				updateTotalAmt = true;
				$amount.val(formatDecimalPlaces(total));
			} else {
				//Amount has a value. Should not be computed.
				updateTotalAmt = true;
				total += accounting.unformat($amount.val());
			}
		}
		totalAmount += total;
	});

	if(updateTotalAmt == true) {
		$footerSpan.html(formatDecimalPlaces(totalAmount));
		$("#hdnOChargesAmt").val(totalAmount);
	}
}