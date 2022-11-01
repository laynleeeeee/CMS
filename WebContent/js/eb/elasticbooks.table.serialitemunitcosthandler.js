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
SerialItemUnitCostTblHandler = function (totalHandler) {

	this.totalHandler = totalHandler;

	this.showItems = function (setting, $table, $txtBox) {
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var serialNumber = $($txtBox).closest("tr").find(".serialNumber").val();
		var isSerialized = typeof serialNumber != "undefined";
		var uri = contextPath + "/getRetailSerialItem/getRetailItems?stockCode="+stockCode
			+"&isActive=true&isSerialized="+isSerialized;
		var warehouseId = $("#warehouseId").val();
		if (warehouseId != undefined) {
			uri += "&warehouseId=" + warehouseId;
		}
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		$($txtBox).autocomplete({
			source: uri,
			select: function( event, ui ) {
				var row = ui.item;
				$(this).val(row.item.stockCode);
				return false;
			}, minLength: 1
		}).data( "ui-autocomplete" )._renderItem = function( ul, row ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", row)
				.append( "<a style='font-size: small;'>" + row.item.stockCode + "-" + row.item.description + "</a>" )
				.appendTo( ul );
		};
	};

	this.initRowData = function (setting, $table, $txtBox, isOtherCharges) {
		var $tr = $($txtBox).closest("tr");
		if (isOtherCharges) {
			loadTaxTypes($txtBox, true);
			populateDiscountTypes($txtBox, true);
		} else {
			processItem(setting, $table, $txtBox);
		}
		processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, isOtherCharges);
	};
	
	this.processSelectedItems = function (setting, $table, $txtBox) {
		processItem(setting, $table, $txtBox);
		processDiscountAndAmount(setting, this.totalHandler, $table, $($txtBox).closest("tr"), $txtBox, false);
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

function processStockCode ($table, $txtBox, item) {
	if (item == undefined) {
		return;
	}
	var formatES = formatDecimalPlaces(item.existingStocks);
	var stockCode = item.stockCode;
	$($txtBox).closest("tr").find(".stockCode").val(stockCode);
	$($txtBox).closest("tr").find(".existingStock").html(formatES);
	$($txtBox).closest("tr").find(".description").html(item.description);
	$($txtBox).closest("tr").find(".uom").html(item.unitMeasurement.name);
	$($txtBox).closest("tr").find(".itemId").val(item.id);
	// Empty Qty, Discount (Computed) and Amount
	var $quantity = $($txtBox).closest("tr").find(".quantity");
	if ($quantity == undefined)
		$quantity.val("");

	var $amount = $($txtBox).closest("tr").find(".amount");
	if ($amount == undefined) {
		$amount.html("");
	}
	// Loading data
	var companyId = $("#companyId").val();
	if ($.trim(stockCode) != "") {
		// populateSrp(companyId, item.id, $txtBox);
		getLatestUC($txtBox, item.id);
		populateDiscountTypes($txtBox, false);
		loadTaxTypes($txtBox, false);
	}
}

function populateSrp (companyId, itemId, $txtBox) {
	var uri = contextPath+"/getItemSrp?companyId="+companyId+"&itemId="+itemId;
	var divisionId = $("#divisionId").val();
	if (divisionId != undefined) {
		uri += "&divisionId="+divisionId;
	}
	$.ajax({
		url: uri,
		success : function(item) {
			if(item != null) {
				var $srp = $($txtBox).closest("tr").find(".srp");
				$srp.html(accounting.formatMoney(item.srp));

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

function getLatestUC($txtBox, itemId) {
	var isSerial = $($txtBox).closest("tr").find(".serialNumber").val() != undefined; // True if serialNumber element exist, otherwise false.
	var $prevUC = $($txtBox).closest("tr").find(".prevUC");
	var supplierAccountId = $("#supplierAcctId").val();
	var warehouseId = $("#warehouseId").val();
	if($prevUC.length != 0 && supplierAccountId != null) {
		var uri = contextPath + "/retailReceivingReport/getLatestUc?itemId="+itemId
				+"&warehouseId="+warehouseId+"&supplierAcctId="+supplierAccountId+"&isSerial="+isSerial;
		$.ajax({
			url: uri,
			success : function(unitCost) {
				$prevUC.html(unitCost);
			},
			error : function(error) {
				console.log(error);
			}
		});
	}
}

function processItem(setting, $table, $txtBox) {
	var stockCode = $.trim($($txtBox).val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getRetailSerialItem?isActiveOnly="+(itemId != 0 ? false : true)
		+ "&stockCode=" + encodeURIComponent(stockCode);
	var warehouseId = $("#warehouseId").val() != null ? $("#warehouseId").val() : $("#hdnWarehouseId").val();
	if (warehouseId != undefined){
		uri += "&warehouseId="+warehouseId;
	}
	var serialNumber = $($txtBox).closest("tr").find(".serialNumber").val();
	if (serialNumber != undefined){
		uri += "&isSerialized="+true;
	}
	$("#"+setting.itemTableMessage).text("");
	if (stockCode != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null) {
					processStockCode($table, $txtBox, item.item);
				}
			},
			error : function(error) {
				console.log(error);
				$("#"+setting.itemTableMessage).css("color", "red").text('Invalid Stock Code');
				$($txtBox).focus();
			},
			dataType: "json"
		});
	}
}

function computeTotal (setting, $table, totalHandler) {
	for (var index = 0; index < setting.footer.length; index++) {
		var cls = setting.footer[index].cls;
		var total = computeTotalPerColumn($table, setting.footer[index].cls);
		var $footerSpan = $table.find("tfoot tr ."+cls);
		if (totalHandler != undefined) {
			totalHandler.handleTotal (total);
		}
		$footerSpan.html(formatDecimal(total));
	}
	updateOChargesAmt();
	// Computing the grand total in the form.
	// Will only work if there is a computeGrandTotal function declared in the form.
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

function computeAmountByCost (setting, $table, totalHandler, $textBox, isOtherCharges, discount) {
	var $row = $($textBox).closest("tr");
	computeVatAmount($textBox, $row.find(".taxTypeId").val(), isOtherCharges, discount);
	computeTotal(setting, $table, totalHandler);
}

function updateOChargesAmt() {
	var totalAmount = 0;
	var updateTotalAmt = false;
	var $footerSpan = $("#otherChargesTable").find("tfoot tr .amount");
	$("#otherChargesTable tbody tr").each(function(row) {
		var netAmount = 0;
		var itemDiscountTypeId = $(this).find(true ? ".discountTypeId" : ".itemDiscountTypeId").val();
		var discount = computeDiscount(this, itemDiscountTypeId, true);
		var qty = accounting.unformat($(this).find(".quantity").val());
		qty = qty != 0 ? qty : 1;
		var upAmt = accounting.unformat($(this).find(".upAmount").val());
		if (qty != 0 || upAmt != 0) {
			var qtyDiscount = Math.abs(qty != 0 ? (discount / qty) : 0);
			upAmt = upAmt - qtyDiscount;
			var taxTypeId = $(this).find(".taxTypeId").val();
			var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
			var grossAmount = qty * upAmt;
			var netOfVAT = isVatable ? formatDecimalPlaces(grossAmount / 1.12) : grossAmount;
			var vat = formatDecimalPlaces(grossAmount - accounting.unformat(netOfVAT));
			$(this).find(".vatAmount").html(vat);
			netAmount = parseFloat((grossAmount - accounting.unformat(vat)).toFixed(6));
			totalAmount += netAmount;
		}
		$(this).find(".amount").val(formatDecimalPlaces(netAmount));
	});
	$footerSpan.html(formatDecimalPlaces(totalAmount));
	$("#hdnOChargesAmt").val(totalAmount);
}

function loadItemSerialNumbers($txtBox) {
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var hdnRefObjectId = $("#hdnRefObjectId").val();
	var $serialNumber = $($txtBox).closest("tr").find(".serialNumber");
	var serialNumber = $serialNumber.val();
	var stockCode = $($txtBox).closest("tr").find(".stockCode").val();
	var uri = contextPath + "/getRetailSerialItem/getSerialNumber?serialNumber="+ encodeURIComponent($.trim(serialNumber))
			+ "&warehouseId=" + warehouseId
			+ "&stockCode=" +  encodeURIComponent($.trim(stockCode)) + "&isExact=false";
	if (typeof hdnRefObjectId != "undefined") {
		uri += "&referenceObjectId=" + hdnRefObjectId;
	}
//	var divisionId = $("#divisionId").val();
//	if (divisionId != undefined) {
//		uri += "&divisionId="+divisionId;
//	}
	$($serialNumber).autocomplete({
		source: uri,
		select: function(event, ui) {
			$(this).val(ui.item.serialNumber);
			return false;
		}, minLength: 2,
		change: function(event, ui) {
			$.ajax({
				url: uri,
				success : function(item) {
					if (ui.item != null) {
						$(this).val(ui.item.serialNumber);
					} else {
						$(this).val("");
					}
				},
				dataType: "json"
			});
		}
	}).data("ui-autocomplete")._renderItem = function(ul, item) {
		return $("<li>")
			.data("ui-autocomplete-item", item)
			.append("<a style='font-size: small;'>" + item.serialNumber + "</a>")
			.appendTo(ul);
	};
}

function getSerialItem($txtBox, $table) {
	$("#serializedItemErrors").html("");
	var companyId = $("#companyId").val();
	var warehouseId = $("#warehouseId").val();
	var hdnRefObjectId = $("#hdnRefObjectId").val();
	var $serialNumber = $($txtBox).closest("tr").find(".serialNumber");
	var serialNumber = $serialNumber.val();
	var stockCode = $($txtBox).closest("tr").find(".stockCode").val();
	var $unitCost = $($txtBox).closest("tr").find(".unitCost");
	if (($.trim(serialNumber) != "" && typeof serialNumber != "undefined")
			&& ($.trim(stockCode) != "" && typeof stockCode != "undefined" )) {
		var uri = contextPath + "/getRetailSerialItem/getSerialNumber?serialNumber="+encodeURIComponent($.trim(serialNumber))
				+ "&companyId=" + companyId + "&warehouseId=" + warehouseId
				+ "&stockCode=" +  encodeURIComponent($.trim(stockCode)) + "&isExact=true";
		if (typeof hdnRefObjectId != "undefined") {
			uri += "&referenceObjectId=" + hdnRefObjectId;
		}
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != "" && typeof item != "undefined") {
					$serialNumber.val(item[0].serialNumber);
					var unitCost = item[0].unitCost;
					var quantity = $($txtBox).closest("tr").find(".quantity").val();
					if (quantity > 0) {
						$unitCost.val(unitCost);
					} else {
						$unitCost.text(formatDecimalPlaces(unitCost));
						$($txtBox).closest("tr").find(".amount").html(formatDecimalPlaces(Math.abs(quantity) * unitCost));
						computeTotalStockAdjAmount("serializedAdjustmentItemTable");
					}
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function formatDecimal(value) {
	if (typeof formatNumber == 'function') {
		return formatNumber(value, 4);
	}
	return accounting.formatMoney(value);
}

function computeVatAmount($select, taxTypeId, isOtherCharges, discount) {
	var $tr = $($select).closest("tr");
	if (typeof discount == "undefined") {
		discount = 0.0;
	}
	var quantity =  accounting.unformat($tr.find(".quantity").val());
	var qtyDiscount = Math.abs(quantity != 0 ? (discount / quantity) : 0);
	var unitPriceClsName = isOtherCharges ? "upAmount" : "unitCost";
	var unitCost = accounting.unformat($tr.find("."+unitPriceClsName).val());
	if (isOtherCharges) {
		quantity = quantity != 0 ? quantity : 1;
	}
	unitCost = unitCost - qtyDiscount; //Subtract discount before computing VAT.
	var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
	var grossAmount = quantity * unitCost;
	var netOfVAT = isVatable ? formatDecimalPlaces(grossAmount / 1.12) : grossAmount;
	var vat = formatDecimalPlaces(grossAmount - accounting.unformat(netOfVAT));
	$tr.find(".vatAmount").html(vat);
	var netAmount = parseFloat((grossAmount - accounting.unformat(vat)).toFixed(6));
	$tr.find(".amount").html(formatDecimalPlaces(netAmount));
}

function loadTaxTypes($txtBox, isOtherCharges) {
	var $taxType = $($txtBox).closest("tr").find(".taxType");
	if (typeof $taxType != "undefined") {
		var taxTypeId = $($txtBox).closest("tr").find(".taxTypeId").val();
		var uri = contextPath + "/getTaxTypes/new?isReceivable=false";
		if (typeof taxTypeId != "undefined" && taxTypeId != null && taxTypeId != "") {
			uri += "&taxTypeId="+taxTypeId;
		}
		var discount = accounting.unformat($($txtBox).closest("tr").find(".discount").html());
		populateSelect(uri, taxTypeId, $taxType, isOtherCharges, discount)
	}
}

function populateSelect (uri, selectedValue, $select, isOtherCharges, discount) {
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
			computeVatAmount($select, selectedValue, isOtherCharges, discount);
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
		if (itemDiscountTypeId != null && itemDiscountTypeId != "" && itemDiscountTypeId != undefined) {
			var discount = computeDiscount($discountType, itemDiscountTypeId, isOtherCharges);
			$($discountType).closest("tr").find(".discount").html(formatDecimalPlaces(discount));
			computeAmountByCost(setting, $table, totalHandler, $input, isOtherCharges, discount);
		}
	}
}

function computeDiscount($input, itemDiscountTypeId, isOtherCharges) {
	var discount = 0.0;
	var grossAmount = getGrossAmount($input, isOtherCharges);
	var quantity = accounting.unformat($($input).closest("tr").find(".quantity").val());
	var discountValue = accounting.unformat($($input).closest("tr").find(".discountValue").val());
	if (itemDiscountTypeId == DT_PERCENTAGE) {
		discount = Math.abs(grossAmount * (discountValue / 100));
	} else if (itemDiscountTypeId == DT_AMOUNT) {
		discount = quantity < 0 ? -(discountValue) : discountValue;
	} else if (itemDiscountTypeId == DT_QTY_DISCOUNT) {
		discount = quantity * discountValue;
	}
	return accounting.unformat(formatDecimalPlaces(discount));
}

function getGrossAmount($input, isOtherCharges) {
	var $amount = $($input).closest("tr").find(".amount");
	var srp = accounting.unformat($($input).closest("tr").find(isOtherCharges ? ".upAmount" : ".unitCost").val());
	if ($amount == undefined || srp == 0) {
		return 0.0;
	}
	var quantity = accounting.unformat($($input).closest("tr").find(".quantity").val());
	if (isOtherCharges) {
		quantity = quantity != 0 ? quantity : 1;
	}
	var grossAmount = quantity * srp;
	return grossAmount;
}