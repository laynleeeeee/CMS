/**
 * Object that handles the table for Raw Materials.
 * This is only applicable with table with the following columns.
 * 1. Stock code (text)
 * 2. Description (label)
 * 5. Existing Stocks(label)
 * 6. Qty (Input)
 * 7. UOM (label)
 * 8. Add On (select)
 * 9. Buying Price (label)
 * 9. Discount (select)
 * 10.Discount - computed (label)
 * 11.Amount (label)
 */

RawMaterialsHandler = function (totalHandler) {

	this.totalHandler = totalHandler;

	this.showItems = function (setting, $table, $txtBox) {
//		console.log("inside sales table handler");
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getBuyingDetails/items?stockCode="+stockCode;
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
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

	//Auto complete function for AP Line Setup
	var currentALSetup = null;
	this.showApLineSetups = function (setting, $table, $txtBox) {
		console.log("Showing the list of AP Line Setups.");
		var companyId = $("#companyId").val();
		var arLineSetupName = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getApLineSetups?companyId="+companyId+"&name="+arLineSetupName;
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

	this.initRowData = function (setting, $table, $txtBox) {
		processItem(setting, $table, this.totalHandler, $txtBox);
		computeTotal (setting, $table, this.totalHandler);
	};

	this.processSelectedItems = function (setting, $table, $txtBox) {
		processItem(setting, $table, this.totalHandler, $txtBox);
	};

	this.computeRow = function (setting, $table, $tr, $input) {
		var $quantity = $($tr).find(".quantity");
		updateBuyingPrice(setting, $table, this.totalHandler, $quantity, true);
	};

	this.onDelete = function (setting, $table, isOtherCharge) {
		computeTotal (setting, $table, this.totalHandler);
		if (isOtherCharge) {
			computeTotalAPLines(setting, $table, this.totalHandler);
		}
		computeGrandTotal();
	};
};

function validateStockCode(setting, $table, totalHandler, $txtBox, currentItem) {
	var currentRow = Number($($txtBox).closest("tr").find(".rowNumber").text());
	var currentItemId = currentItem.id;
	var itemIdPerRow = null;
	var isDuplicateStockCode = false;
	console.log("current item id: "+currentItemId+" at row "+currentRow);
	$($table).find(" tbody tr ").each(function(row) {
		if(Number(row+1) != currentRow) {
			itemIdPerRow = $(this).find(".itemId").val();
			if(currentItemId != "") {
				if(currentItemId == itemIdPerRow) {
					isDuplicateStockCode = true;
					console.log("duplicate stock code!");
					return false;
				} else {
					console.log("calling processStockCode func. "+currentItem.id);
					processStockCode(setting, $table, totalHandler, $txtBox, currentItem);
					return false;
				}
			}
		} else {
			itemIdPerRow = $($txtBox).closest("tr").find(".itemId").val();
			var desc = $($txtBox).closest("tr").find(".description").val();
			if(itemIdPerRow > 0 && desc == "") {
				processStockCode(setting, $table, totalHandler, $txtBox, currentItem);
				return false;
			}
		}
	});

	if(isDuplicateStockCode) {
		$("#"+setting.itemTableMessage).text("Duplicate stock code");
		$($txtBox).focus();
	}
}

function processStockCode (setting, $table, totalHandler, $txtBox, item) {
	console.log("processStockCode");
	if (item == undefined) {
		return;
	}
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
	getBuyingPrice(setting, $table, totalHandler, companyId, item.id, $txtBox);
}

function processItem(setting, $table, totalHandler, $txtBox) {
//	console.log("processing items");
	var stockCode = $.trim($($txtBox).val());
//	console.log("stock code ============================= " + stockCode);
	var uri = contextPath + "/getBuyingDetails/item?stockCode="+encodeURIComponent(stockCode);
	var warehouseId = $("#warehouseId").val();
	if (warehouseId != undefined) {
		uri += "&warehouseId="+warehouseId;
	}
	if (stockCode != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null) {
					if(setting.disableDuplicateStockCode) {
						validateStockCode(setting, $table, totalHandler, $txtBox, item);
					} else {
						processStockCode(setting, $table, totalHandler, $txtBox, item);
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

function getBuyingPrice (setting, $table, totalHandler, companyId, itemId, $txtBox) {
	console.log("get the latest buying price.");
	$.ajax({
		url: contextPath + "/getBuyingDetails/price?companyId="+companyId+"&itemId="+itemId,
		success : function(bp) {
			if(bp != null) {
				var $unitCost = $($txtBox).closest("tr").find(".unitCost");
				$unitCost.val(bp.buyingPrice);
				var $buyingPrice = $($txtBox).closest("tr").find(".buyingPrice");
				$buyingPrice.html(formatDecimalPlaces(bp.buyingPrice));
				getBuyingAddOns(setting, $table, totalHandler, companyId, itemId, $txtBox);
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function getBuyingDiscs(setting, $table, totalHandler, companyId, itemId, $txtBox) {
//	console.log("get buying discounts. >>> "+$($txtBox).closest("tr").find(".description").html());
	var $discountType = $($txtBox).closest("tr").find(".slctDiscount");
	var uri = contextPath + "/getBuyingDetails/discounts?itemId="+itemId+"&companyId="+companyId;
	var itemDiscountId = $($txtBox).closest("tr").find(".buyingDiscountId").val();
	var postHandler = {
			doPost: function(data) {
				computeDiscount(setting, $table, totalHandler, $txtBox);
			}
	};
	populateSelect(uri, itemDiscountId, $discountType, postHandler);
}

function getBuyingAddOns(setting, $table, totalHandler, companyId, itemId, $txtBox) {
//	console.log("get buying add ons. >>> "+$($txtBox).closest("tr").find(".description").html());
	var $slctAddOn = $($txtBox).closest("tr").find(".slctAddOn");
	var uri = contextPath + "/getBuyingDetails/addOns?itemId="+itemId+"&companyId="+companyId;
	var addOnId = $($txtBox).closest("tr").find(".buyingAddOnId").val();
	var postHandler = {
			doPost: function(data) {
				updateBuyingPrice(setting, $table, totalHandler, $txtBox, false);
				getBuyingDiscs(setting, $table, totalHandler, companyId, itemId, $txtBox);
			}
	};
	populateSelect(uri, addOnId, $slctAddOn, postHandler);
}

function populateSelect (uri, selectedValue, $select, postHandler) {
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
	loadPopulateObject (uri, false, selectedValue, $select, optionParser, postHandler, true, true);
}

function updateBuyingPrice(setting, $table, totalHandler, $elem, isComputeDisc) {
	var $addOn = $($elem).closest("tr").find(".slctAddOn");
	var $buyingPrice = $($elem).closest("tr").find(".buyingPrice");
	var $unitCost = $($elem).closest("tr").find(".unitCost");
	if ($addOn.val() != null && $.trim($addOn.val()) != "" && 
			typeof $addOn.val() != "undefined") {
//		console.log ("Updating the buying price of the item.");
		$.ajax({
			url: contextPath + "/getBuyingDetails/addOn?id="+$addOn.val(),
			success : function(addOn) {
				$buyingPrice.text(""); // Remove value from buying price column
				var updatedPrice = parseFloat(addOn.value) + parseFloat($unitCost.val());
				$buyingPrice.text(formatDecimalPlaces(updatedPrice));
//				console.log("buying price: "+$buyingPrice.text());
				if(isComputeDisc) {
					//Compute discount and update amount after sucessfully loaded the buying price.
					computeDiscount(setting, $table, totalHandler, $elem);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$buyingPrice.text(formatDecimalPlaces($unitCost.val()));
		if(isComputeDisc) {
			computeDiscount(setting, $table, totalHandler, $elem);
		}
	}
}

function computeDiscount(setting, $table, totalHandler, $elem) {
	var $slctDiscount = $($elem).closest("tr").find(".slctDiscount");
	var $discount = $($elem).closest("tr").find(".discount");
	if ($slctDiscount.val() != null && $.trim($slctDiscount.val()) != "" && 
			typeof $slctDiscount.val() != "undefined") {
		var $quantity = $($elem).closest("tr").find(".quantity");
		var $buyingPrice = $($elem).closest("tr").find(".buyingPrice");
		$.ajax({
			url: contextPath + "/getBuyingDetails/discount?id="+$slctDiscount.val()
					+"&quantity="+accounting.unformat($quantity.val())+"&price="+accounting.unformat($buyingPrice.text()),
			success : function(discount) {
				$discount.text(formatDecimalPlaces(discount));
				//re-compute amount after successfully loaded the discounts.
				computeAmountByCost(setting, $table, totalHandler, $elem);
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$discount.text("");
//		console.log("no discount.");
		computeAmountByCost(setting, $table, totalHandler, $elem);
	}
}

function computeTotalAPLines (setting, $table, totalHandler) {
	console.log("computing total ar lines++++++++++++");
	var total = 0;
	$($table).find("tbody tr").each(function () {
		var amount = accounting.unformat($(this).find("input.amount").val());
		total += amount;
	});

	$($table).find("tfoot tr .amount").text(formatDecimalPlaces(total));
	callComputeGrandTotal();
}

function computeAmountByCost (setting, $table, totalHandler, $elem) {
//	console.log("compute amount by cost function!");
	var $quantity = $($elem).closest("tr").find(".quantity");
	var $amount = $($elem).closest("tr").find(".amount");
	var quantity = accounting.unformat($quantity.val());
	var $buyingPrice = $($elem).closest("tr").find(".buyingPrice");
	var $discount = $($elem).closest("tr").find(".discount");
	var buyingPrice = accounting.unformat($buyingPrice.html());
//	console.log(" === bp: "+$buyingPrice.html()
//			+" === discount: "+$discount.html()+" === amount: "+$amount.html());
	var amount = Number(quantity*buyingPrice) - Number(accounting.unformat($discount.html()));
	var formattedAmount = formatDecimalPlaces(amount);
	$($amount).html(formattedAmount);
	computeTotal(setting, $table, totalHandler);
}

function computeTotal (setting, $table, totalHandler) {
//	console.log("computing total ++++++++++++");
	for (var index = 0; index < setting.footer.length; index++) {
		var cls = setting.footer[index].cls;
		var total = computeTotalPerColumn($table, setting.footer[index].cls);
		var $footerSpan = $table.find("tfoot tr ."+cls);
		if (totalHandler != undefined) {
			totalHandler.handleTotal (total);
		}
		$footerSpan.html(formatDecimalPlaces(total));
	}
	callComputeGrandTotal();
}

function callComputeGrandTotal() {
	//Computing the grand total in the form.
	//Will only work if there is a computeGrandTotal function declared in the form.
	if(typeof computeGrandTotal == 'function') {
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