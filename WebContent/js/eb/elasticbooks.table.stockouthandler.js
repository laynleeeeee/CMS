/**
 * Object that handles the sales table. 
 * This is only applicable with table with the following columns.
 * 1. Stock code (text)
 * 2. Description
 * 5. Qty (Input)
 * 6. UOM
 * 7. Unit Cost
 * 8. SRP
 * 9. Amount
 * 
 */

var DT_PERCENTAGE = 1;
var DT_AMOUNT = 2;
var DT_QTY_DISCOUNT = 3;
StockOutTableHandler = function (totalHandler) {
	
	this.totalHandler = totalHandler;
	
	this.showItems = function (setting, $table, $txtBox) {
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getRItems/filter?stockCode="+stockCode;
		var warehouseId = $("#warehouseId").val();
		if (warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(this).val (ui.item.stockCode);
				return false;
			}, minLength: 1,
			change: function(event, ui){
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>"+ item.stockCode + "-" +item.description + "</a>" )
				.appendTo( ul );
		};
	};

	//Auto complete function for AP Line Setup
	var currentALSetup = null;
	this.showApLineSetups = function (setting, $table, $txtBox) {
		var companyId = $("#companyId").val();
		var arLineSetupName = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getApLineSetups?companyId="+companyId+"&name="+arLineSetupName;
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				currentALSetup = ui.item;
				$(this).val (ui.item.name);
				//No validation for duplicate AR Line Setup
				var $alSetupId = $($txtBox).closest("tr").find(".apLineSetupId");
				$alSetupId.val(ui.item.id);
				return false;
			}, minLength: 1,
			change: function(event, ui){
				var setupName = $(this).val();
				if (currentALSetup != null && setupName == currentALSetup.name){
					return false;
				}
				var $alSetupId = $($txtBox).closest("tr").find(".apLineSetupId");
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

	this.initRowData = function (setting, $table, $txtBox) {
		processItem(setting, $table, $txtBox);
		computeTotal (setting, $table, this.totalHandler);
	};
	
	this.processSelectedItems = function (setting, $table, $txtBox) {
		processItem(setting, $table, $txtBox);
	};

	this.computeRow = function (setting, $table, $tr, $input) {
		var $quantity = $($tr).find(".quantity");
		computeAmountByCost(setting, $table, this.totalHandler, $quantity);
	};

	this.onDelete = function (setting, $table) {
		if (typeof computeGrandTotal != "undefined") {
			computeGrandTotal();
		}
		computeTotal (setting, $table, this.totalHandler);
	};
};

function validateStockCode(setting, $table, $txtBox, currentItem) {
	var currentRow = Number($($txtBox).closest("tr").find(".rowNumber").text());
	var currentItemId = currentItem.id;
	var itemIdPerRow = null;
	var isDuplicateStockCode = false;
	$($table).find(" tbody tr ").each(function(row) {
		if(Number(row+1) != currentRow) {
			itemIdPerRow = $(this).find(".itemId").val();
			if(currentItemId != "") {
				if(currentItemId == itemIdPerRow) {
					isDuplicateStockCode = true;
					console.log("duplicate stock code!");
					return false;
				} else {
					processStockCode($table, $txtBox, currentItem);
					return false;
				}
			}
		} else {
			itemIdPerRow = $($txtBox).closest("tr").find(".itemId").val();
			var desc = $($txtBox).closest("tr").find(".description").val();
			if(itemIdPerRow > 0 && desc == "") {
				processStockCode(currentItem, $txtBox, currentItem);
				return false;
			}
		}
	});

	if(isDuplicateStockCode) {
		$("#"+setting.itemTableMessage).text("Duplicate stock code");
		$($txtBox).focus();
	}
}

function processStockCode ($table, $txtBox, item) {
	if (item == undefined)
		return;
	var $existingStocks = $($txtBox).closest("tr").find(".existingStock");
	var formatES = formatDecimalPlaces(item.existingStocks);
	$existingStocks.html(formatES);

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
		var date = typeof $("#trDate").val() != "undefined" ? $("#trDate").val() : $("#saDate").val();
		date = typeof date != "undefined" ? date : $("#date").val();
		populateSrp(companyId, item.id, $txtBox);
	}
}

function processItem(setting, $table, $txtBox) {
	var stockCode = $.trim($($txtBox).val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "")
		+ "?stockCode="+encodeURIComponent(stockCode); 
	var warehouseId = $("#warehouseId").val() != null ? $("#warehouseId").val() : $("#hdnWarehouseId").val();
	if (warehouseId != undefined) {
		uri += "&warehouseId="+warehouseId;
	}
	if (stockCode != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null) {
					if(setting.disableDuplicateStockCode) {
						validateStockCode(setting, $table, $txtBox, item);
					} else {
						processStockCode($table, $txtBox, item);
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

function populateSrp (companyId, itemId, $txtBox) {
	var uri = contextPath+"/getItemSrp?companyId="+companyId+"&itemId="+itemId;
	var divisionId = $("#divisionId").val();
	if (divisionId != undefined) {
		uri += "&divisionId="+divisionId;
	}
	$.ajax({
		url : uri,
		success : function(item) {
			if(item != null) {
				var $srp = $($txtBox).closest("tr").find(".srp");
				$srp.html(formatDecimalPlaces(item.srp));

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

function computeTotalAmount ($input) {
	var $discount = $($input).closet("tr").find(".discount");
	var discount = 0.0;
	if ($discount != undefined)
		discount = accounting.unformat($(discount).html());
	var grossAmount = getGrossAmount($input);
	return grossAmount - discount;
}

function computeTotal (setting, $table, totalHandler) {
	if (setting.footer != null) {
		for (var index = 0; index < setting.footer.length; index++) {
			var cls = setting.footer[index].cls;
			var total = computeTotalPerColumn($table, setting.footer[index].cls);
			var $footerSpan = $table.find("tfoot tr ."+cls);
			if (totalHandler != undefined)
				totalHandler.handleTotal (total);
			$footerSpan.html(formatDecimalPlaces(total));
		}
		updateOChargesAmt();
		//Computing the grand total in the form.
		//Will only work if there is a computeGrandTotal function declared in the form.
		if(typeof computeGrandTotal == 'function') {
			computeGrandTotal();
		}
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
	var quantity = $($quantity).val();
	
	var srp = accounting.unformat($srp.html()); // Unform this
	return quantity * srp;
}

function computeAmountByCost (setting, $table, totalHandler, $initCostOrQuantity) {
	var $unitCost =$($initCostOrQuantity).closest("tr").find(".unitCost");
	var $quantity = $($initCostOrQuantity).closest("tr").find(".quantity");
	var $amount = $($initCostOrQuantity).closest("tr").find(".amount");
	var quantity = accounting.unformat($quantity.val());
	var unitCost = accounting.unformat($unitCost.html());
	if(unitCost == 0) {
		unitCost = accounting.unformat($unitCost.val());
	}
	var formattedAmount = formatDecimalPlaces(Number(quantity*unitCost));
	$($amount).html(formattedAmount);
	computeTotal(setting, $table, totalHandler);
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

function computeGrandTotal(tdAmount) {
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
