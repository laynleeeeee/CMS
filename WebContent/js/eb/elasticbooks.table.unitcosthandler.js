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
UnitCostTableHandler = function (totalHandler) {
	this.totalHandler = totalHandler;
	this.showItems = function (setting, $table, $txtBox) {
		var stockCode = $.trim($txtBox.val());
		var uri = contextPath+"/getRetailSerialItem/getRetailItems?stockCode="+encodeURIComponent(stockCode)
			+"&isSerialized=false&isActive=true";
		var warehouseId = $("#warehouseId").val() != null ? $("#warehouseId").val() : $("#hdnWarehouseId").val();
		if (warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$(this).val (ui.item.item.stockCode);
				return false;
			}, minLength: 1,
			change: function(event, ui){
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, row ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", row )
				.append( "<a style='font-size: small;'>"+ row.item.stockCode + "-" +row.item.description + "</a>" )
				.appendTo( ul );
		};
	};

	// Auto complete function for AP Line Setup
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
				// No validation for duplicate AR Line Setup
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

	// Auto complete for UOM
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
				} else if(uom == ""){
					$(this).closest("tr").find(".unitOfMeasurementId").val("");
				} else {
					$(this).closest("tr").find(".unitOfMeasurementId").val(ui.item.id);
				}
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, uom ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", uom )
				.append( "<a style='font-size: small;'>"+ uom.name + "</a>" )
				.appendTo( ul );
		};
	};

	this.initRowData = function (setting, $table, $txtBox, isOtherCharges) {
		var $tr = $($txtBox).closest("tr");
		if (isOtherCharges) {
			loadTaxTypes($txtBox, true);
			populateDiscountTypes($txtBox, true);
			processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, true);
		} else {
			var doAfterProcessItem = {
				process : function() {
					processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, false);
				}
			};
			processItem(setting, $table, $txtBox);
		}
		computeTotal(setting, $table, this.totalHandler);
	};

	this.processSelectedItems = function (setting, $table, $txtBox) {
		var doAfterProcessItem = {
			process : function() {
				processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, false);
			}
		};
		processItem(setting, $table, $txtBox);
	};

	this.processOtherCharges = function (setting, $table, $txtBox, isOtherCharges) {
		loadTaxTypes($txtBox, isOtherCharges);
		populateDiscountTypes($txtBox, isOtherCharges);
		processDiscountAndAmount(setting, this.totalHandler, $table,
				$($txtBox).closest("tr"), $txtBox, isOtherCharges);
	};

	this.computeRow = function (setting, $table, $tr, $input, isOtherCharges) {
		processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $input, isOtherCharges);
	};

	this.onDelete = function (setting, $table) {
		if (typeof computeGrandTotal != "undefined") {
			computeGrandTotal();
		}
		computeTotal(setting, $table, this.totalHandler);
		if (typeof computeWtax == 'function') {
			computeWtax();
		}
	};
};

function loadTaxTypes($txtBox, isOtherCharges) {
	var $taxType = $($txtBox).closest("tr").find(".taxType");
	if (typeof $taxType != "undefined") {
		var taxTypeId = $($txtBox).closest("tr").find(".taxTypeId").val();
		var uri = contextPath + "/getTaxTypes/new?isReceivable=false";
		if (typeof taxTypeId != "undefined" && taxTypeId != null && taxTypeId != "") {
			uri += "&taxTypeId="+taxTypeId;
		}
		populateSelect(uri, taxTypeId, $taxType, isOtherCharges)
	}
}

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
	if (item == undefined) {
		return;
	}
	var formatES = formatDecimalPlaces(item.existingStocks);
	$($txtBox).closest("tr").find(".existingStock").html(formatES);
	$($txtBox).closest("tr").find(".description").html(item.description);
	$($txtBox).closest("tr").find(".uom").html(item.unitMeasurement.name);
	$($txtBox).closest("tr").find(".itemId").val(item.id);
	// Empty Qty, Discount (Computed) and Amount
	var $quantity = $($txtBox).closest("tr").find(".quantity");
	if ($quantity == undefined) {
		$quantity.val("");
	}
	var $amount = $($txtBox).closest("tr").find(".amount");
	if ($amount == undefined) {
		$amount.html("");
	}
	// Loading data
	var companyId = $("#companyId").val();
	var stockCode = item.stockCode;
	if ($.trim(stockCode) != "") {
		// populateSrp(companyId, item.id, $txtBox);
		loadTaxTypes($txtBox, false);
		populateDiscountTypes($txtBox, false);
	}
}

function populateSelect (uri, selectedValue, $select, isOtherCharges) {
	$($select).empty();
	var optionParser = {
		getValue: function (rowObject){
			if (rowObject != null) {
				return rowObject["id"];
			}
		},
		getLabel: function (rowObject){
			if (rowObject != null) {
				return rowObject["name"];
			}
		}
	};
	var postHandler = {
		doPost: function(data) {
			// Add computation of VAT amount here
			computeVatAmount($select, selectedValue, isOtherCharges);
			// This is to remove any duplication.
 			var found = [];
			$($select).each(function() {
				if($.inArray(this.value, found) != -1) {
					$(this).remove();
				}
				found.push(this.value);	
			});
			if (typeof selectedValue != "undefined" && selectedValue != null && selectedValue != "") {
				$($select).val(selectedValue);
			}
		}
	};
	loadPopulateObject (uri, false, selectedValue, $select, optionParser, postHandler, false, true);
}

function computeVatAmount($select, taxTypeId, isOtherCharges) {
	var $tr = $($select).closest("tr");
	if (typeof discount == "undefined") {
		discount = 0.0;
	}
	var quantity =  accounting.unformat($tr.find(".quantity").val());
	var unitPriceClsName = isOtherCharges ? "upAmount" : "unitCost";
	var unitCost = accounting.unformat($tr.find("."+unitPriceClsName).val());
	if (isOtherCharges) {
		quantity = quantity != 0 ? quantity : 1;
	}
	var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
	var netOfVat = isVatable ? (unitCost / 1.12).toFixed(6) : unitCost;
	var vat = unitCost - netOfVat;
	var totalItemVat = formatDecimalPlaces((vat * quantity).toFixed(6));
	$tr.find(".vatAmount").html(totalItemVat);
	var computedUnitPrice = (unitCost - vat);
	var amount = computedUnitPrice * Math.abs(quantity);
	if (isOtherCharges) {
		$tr.find(".amount").val(formatDecimalPlaces(amount));
	} else {
		$tr.find(".amount").html(formatDecimalPlaces(amount));
	}
}

function processItem(setting, $table, $txtBox) {
	var stockCode = $.trim($($txtBox).val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "")
		+"?stockCode="+encodeURIComponent(stockCode);
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
				getLatestUC($txtBox, itemId);
			}
		},
		error : function(error) {
			console.log(error);
		},
		dataType: "json"
	});
}

function getLatestUC($txtBox, itemId) {
	var $prevUC = $($txtBox).closest("tr").find(".prevUC");
	var supplierAccountId = $("#supplierAcctId").val();
	var warehouseId = $("#warehouseId").val();
	if($prevUC.length != 0 && (supplierAccountId != null && supplierAccountId !== "undefined")) {
		var uri = contextPath + "/retailReceivingReport/getLatestUc?itemId="+itemId
				+"&warehouseId="+warehouseId;
		if (typeof supplierAccountId != "undefined") {
			uri += "&supplierAcctId="+supplierAccountId;
		}
		$.ajax({
			url: uri,
			success : function(unitCost) {
				$prevUC.html(formatDecimalPlaces(unitCost));
			},
			error : function(error) {
				console.log(error);
			}
		});
	} else {
		$prevUC.html(formatDecimalPlaces(0.0));
	}
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
	for (var index = 0; index < setting.footer.length; index++) {
		var cls = setting.footer[index].cls;
		var total = computeTotalPerColumn($table, setting.footer[index].cls);
		var $footerSpan = $table.find("tfoot tr ."+cls);
		if (totalHandler != undefined)
			totalHandler.handleTotal (total);
		$footerSpan.html(formatDecimalPlaces(total));
	}

	updateOChargesAmt();
	// Computing the grand total in the form.
	// Will only work if there is a computeGrandTotal function declared in the form.
	if (typeof computeGrandTotal == 'function') {
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
	var quantity = $($quantity).val();
	var srp = accounting.unformat($srp.html());
	return quantity * srp;
}

function computeAmountByCost(setting, $table, totalHandler, $textBox, isOtherCharges) {
	var $row = $($textBox).closest("tr");
	computeVatAmount($textBox, $row.find(".taxTypeId").val(), isOtherCharges);
	computeTotal(setting, $table, totalHandler);
}

function updateOChargesAmt(tdAmount) {
	var totalAmount = 0;
	var updateTotalAmt = false;
	var $footerSpan = $("#otherChargesTable").find("tfoot tr .amount");
	$("#otherChargesTable tbody tr").each(function(row) {
		var total = 0;
		var qty = accounting.unformat($(this).find(".quantity").val());
		qty = qty != 0 ? qty : 1;
		var upAmt = accounting.unformat($(this).find(".upAmount").val());
		if (qty != 0 || upAmt != 0) {
			var taxTypeId = $(this).find(".taxTypeId").val();
			var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
			var netOfVat = isVatable ? (upAmt / 1.12).toFixed(6) : upAmt;
			var vat = upAmt - netOfVat;
			var totalItemVat = (vat * qty).toFixed(6);
			$(this).find(".vatAmount").html(formatDecimalPlaces(totalItemVat));
			total = netOfVat * qty;
			totalAmount += total;
		}
		$(this).find(".amount").val(formatDecimalPlaces(total));
	});
	$footerSpan.html(formatDecimalPlaces(totalAmount));
	$("#hdnOChargesAmt").val(totalAmount);
}

function populateDiscountTypes($txtBox, isOtherCharges) {
	var $discountType = $($txtBox).closest("tr").find(".discountType");
	var itemDiscountTypeId = $($txtBox).closest("tr").find(isOtherCharges ? ".discountTypeId" : ".itemDiscountTypeId").val();
	if ($discountType != undefined) {
		var uri = contextPath+"/getItemDiscountType/all";
		$($discountType).empty();
		var optionParser = {
			getValue : function(rowObject) {
				if (rowObject != null)
					return rowObject["id"];
			},

			getLabel : function(rowObject) {
				if (rowObject != null) {
					return rowObject["name"];
				}
			}
		};

		var postHandler = {
			doPost : function(data) {
				// This is to remove any duplication.
				var found = [];
				$($discountType).each(function() {
					if ($.inArray(this.value, found) != -1)
						$(this).remove();
					found.push(this.value);
				});
				if (typeof itemDiscountTypeId != "undefined" && itemDiscountTypeId != null) {
					$($discountType).val(itemDiscountTypeId);
				}
			}
		};
		loadPopulateObject(uri, false, itemDiscountTypeId, $discountType, optionParser, postHandler, false, true);
	}
}

function processDiscountAndAmount(setting, totalHandler, $table, $tr, $input, isOtherCharges) {
	var $discountType = $($tr).find(".discountType");
	var $amount = $($tr).closest("tr").find(".amount");
	if ($discountType == undefined && $amount != undefined) {
		computeAmountByCost(setting, $table, totalHandler, $input, isOtherCharges, 0.0);
		return;
	} else {
		var itemDiscountTypeId = $($tr).find(isOtherCharges ? ".discountTypeId" : ".itemDiscountTypeId").val();
		var discount = computeDiscount($discountType, itemDiscountTypeId, isOtherCharges);
		$($discountType).closest("tr").find(".discount").html(formatDecimalPlaces(discount));
		computeAmountByCost(setting, $table, totalHandler, $input, isOtherCharges, discount);
	}
}

function computeDiscount($input, itemDiscountTypeId, isOtherCharges) {
	var discount = 0.0;
	var grossAmount = getGrossAmount($input, isOtherCharges);
	var quantity = accounting.unformat($($input).closest("tr").find(".quantity").val());
	var discountValue = accounting.unformat($($input).closest("tr").find(".discountValue").val());
	var $quantity = $($input).closest("tr").find(".quantity");
	var quantity = $($quantity).val();
	if (itemDiscountTypeId == DT_PERCENTAGE) {
		discount = Math.abs(grossAmount * (discountValue / 100));
	} else if (itemDiscountTypeId == DT_AMOUNT) {
		discount = quantity < 0 ? -(discountValue) : discountValue;
	} else if (itemDiscountTypeId == DT_QTY_DISCOUNT) {
		discount = quantity * discountValue;
	}
	return accounting.unformat(formatDecimalPlaces(discount));
}