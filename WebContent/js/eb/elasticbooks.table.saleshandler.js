/**
 * Object that handles the sales table. This is only applicable with table with
 * the following columns.
 * 
 * There should be a select object that contains the different company in the
 * header.
 * 
 * 1. Stock code (text) 2. Description 3. Existing Stocks 4. Warehouse (Select)
 * 5. Qty (Input) 6. UOM 7. SRP 8. Discount (Select) 9. Discount - Computed 10.
 * Amount
 */

var DT_PERCENTAGE = 1;
var DT_AMOUNT = 2;
var DT_QTY_DISCOUNT = 3;
SalesTableHandler = function(totalHandler) {
	this.totalHandler = totalHandler;

	this.showItems = function(setting, $table, $txtBox) {
		var stockCode = $.trim($txtBox.val());
		var isSerial = $($table).parent().attr("class").indexOf("tblSerial") !== -1;
		var uri = contextPath+"/getRetailSerialItem/getRetailItems?stockCode="+encodeURIComponent(stockCode)
				+"&isSerialized="+isSerial+"&isActive=true";
		var warehouseId = $("#warehouseId").val();
		if (warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		$($txtBox).autocomplete({
			source : uri,
			select : function(event, ui) {
				var sitem = ui.item;
				$(this).val(sitem.item.stockCode);
				$($txtBox).closest("tr").find(".itemId").val(sitem.item.id);
				return false;
			},
			minLength : 1,
		}).data("ui-autocomplete")._renderItem = function(ul, sitem) {
			return $("<li>").data("ui-autocomplete-item", sitem).append("<a style='font-size: small;'>"
					+sitem.item.stockCode+"-"+sitem.item.description+"</a>").appendTo(ul);
		};
	};

	// Auto complete function for service setting
	this.showServiceSettings = function(setting, $table, $txtBox) {
		var customerAcctId = $("#arCustomerAcctId").val();
		var uri = contextPath+"/getServices?name="+encodeURIComponent($.trim($txtBox.val()));
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		if (customerAcctId != undefined) {
			uri += "&arCustAcctId=" + customerAcctId;
		} else {
			// AR Customer Account is a required parameter
			return;
		}
		var currentALSetup = null;
		$($txtBox).autocomplete({
			source : uri,
			select : function(event, ui) {
				currentALSetup = ui.item;
				$(this).val(ui.item.name);
				$($txtBox).closest("tr").find(".serviceSettingId").val(ui.item.id);
				return false;
			}, minLength : 1
		}).data("ui-autocomplete")._renderItem = function(ul, alSetup) {
		return $("<li>").data("ui-autocomplete-item", alSetup)
			.append("<a style='font-size: small;'>" + alSetup.name + "</a>")
			.appendTo(ul);
		};
	};

	// Auto complete for UOM
	this.showUoms = function(setting, $table, $txtBox) {
		var uomName = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath + "/getUnitMeasurements?name=" + uomName;
		$($txtBox).autocomplete({
			source : uri,
			select : function(event, ui) {
				$(this).val(ui.item.name);
				$(this).closest("tr").find(".unitOfMeasurementId").val(ui.item.id);
				return false;
			}, minLength : 1
		}).data("ui-autocomplete")._renderItem = function(ul, uom) {
		return $("<li>").data("ui-autocomplete-item", uom).append(
				"<a style='font-size: small;'>" + uom.name + "</a>")
				.appendTo(ul);
		};
	};

	function emptyValues(setting, $table, $txtBox) {
		$($txtBox).closest("tr").find(".quantity").val("");
		$($txtBox).closest("tr").find(".discount").html("");
		$($txtBox).closest("tr").find(".amount").html("");
		computeTotal(setting, $table, this.totalHandler);
	}

	this.initRowData = function(setting, $table, $txtBox, isOtherCharges) {
		if (isOtherCharges) {
			populateTaxTypes($txtBox);
			computeAmountByCost(setting, $table, totalHandler, $txtBox, isOtherCharges, 0);
		} else {
			var doAfterProcessItem = {
				process : function() {
					processDiscountAndAmount(setting, this.totalHandler, $table,
							$($txtBox).closest("tr"), $txtBox);
				}
			};
			processItem(setting, $table, $txtBox, doAfterProcessItem);
		}
	};

	this.processSelectedItems = function(setting, $table, $txtBox) {
		var $tr = $($txtBox).closest("tr");
		var doAfterProcessItem = {
			process : function() {
				processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox);
			}
		};
		processItem(setting, $table, $txtBox, doAfterProcessItem);
	};

	this.processOtherCharges = function (setting, $table, $txtBox, isOtherCharges) {
		populateTaxTypes($txtBox);
		computeAmountByCost(setting, $table, totalHandler, $txtBox, isOtherCharges, 0);
	};

	this.computeRow = function(setting, $table, $tr, $txtBox, isOtherCharges) {
		if (isOtherCharges) {
			computeAmountByCost(setting, $table, totalHandler, $txtBox, isOtherCharges, 0);
		} else {
			var doAfterCompute = {
				process : function() {
					processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox);
				}
			};
			computeAddOn($txtBox, doAfterCompute);
		}
	};

	this.onDelete = function(setting, $table, isOtherCharges) {
		computeTotal(setting, $table, this.totalHandler);
		if (isOtherCharges) {
			computeTotalArLines(setting, $table, this.totalHandler);
		}
		if(typeof computeWtax == 'function') {
			computeWtax();
		}
		if(typeof computeTotalAdvances == 'function') {
			computeTotalAdvances();
		}
	};
};

function computeAmountByCost(setting, $table, totalHandler, $textBox, isOtherCharges, discount) {
	var taxTypeId = $($textBox).closest("tr").find(".taxTypeId").val();
	computeVatAmount($textBox, taxTypeId, isOtherCharges, discount);
	computeTotal(setting, $table, totalHandler);
}

function validateStockCode($table, $txtBox, currentItem, doAfterProcess) {
	var currentRow = Number($($txtBox).closest("tr").find(".rowNumber").text());
	var currentItemId = currentItem.id;
	var itemIdPerRow = null;
	var isDuplicateStockCode = false;
	$($table).find(" tbody tr ").each(function(row) {
		if (Number(row + 1) != currentRow) {
			itemIdPerRow = $(this).find(".itemId").val();
			if (currentItemId != "") {
				if (currentItemId == itemIdPerRow) {
					isDuplicateStockCode = true;
				} else {
					processStockCode($table, $txtBox, currentItem,
							doAfterProcess);
				}
				return false;
			}
		} else {
			itemIdPerRow = $($txtBox).closest("tr").find(".itemId")
					.val();
			var desc = $($txtBox).closest("tr").find(".description")
					.val();
			if (itemIdPerRow > 0 && desc == "") {
				processStockCode($table, $txtBox, currentItem,
						doAfterProcess);
				return false;
			}
		}
	});
	var errMessage = "";
	if (isDuplicateStockCode) {
		errMessage = "Duplicate stock code";
		$($txtBox).focus();
	}
	$("#" + setting.itemTableMessage).text(errMessage);
}

function processStockCode($table, $txtBox, item, doAfterProcess) {
	if (item == undefined) {
		return;
	}

	$($txtBox).closest("tr").find(".unitCost").val(item.unitCost);
	$($txtBox).closest("tr").find(".description").html(item.description);
	$($txtBox).closest("tr").find(".uom").html(item.unitMeasurement.name);
	$($txtBox).closest("tr").find(".itemId").val(item.id);

	// Empty Qty, Discount (Computed) and Amount
	var $quantity = $($txtBox).closest("tr").find(".quantity");
	if ($quantity == undefined) {
		$quantity.val("");
	}

	var $discount = $($txtBox).closest("tr").find(".discount");
	if ($discount == undefined) {
		$discount.html("");
	}

	var $amount = $($txtBox).closest("tr").find(".amount");
	if ($amount == undefined) {
		$amount.html("");
	}

	// Loading data
	if ($.trim(item.stockCode) != "") {
		var companyId = $("#companyId").val();
		if (typeof companyId == "undefined") {
			companyId = $("#hdnCompanyId").val();
		}
		var $warehouse = $($txtBox).closest("tr").find(".warehouse");
		if (typeof $warehouse != "undefined" && item.id > 0) {
			var uri = contextPath + "/getWarehouse/withES?companyId="
					+ companyId + "&itemId=" + item.id;
			var warehouseId = $($txtBox).closest("tr").find(".warehouseId")
					.val();
			populateSelect(uri, warehouseId, $warehouse, doAfterProcess);
		}

		var $discountType = $($txtBox).closest("tr").find(".discountType");
		if ($discountType != undefined) {
			var uri = contextPath + "/getItemDiscount/filter?itemId=" + item.id
					+ "&companyId=" + companyId;
			var sellingTypeId = $("#hdnSellingTypeId").val();
			if (sellingTypeId == 2 || sellingTypeId == 8 || sellingTypeId == 9) {
				// Set the type to 2 regardless if the type is 2 or 8
				uri += "&sellingTypeId=2";
			}
			var itemDiscountId = $($txtBox).closest("tr").find(
					".itemDiscountId").val();
			populateSelect(uri, itemDiscountId, $discountType, doAfterProcess);
		}

		populateSrp(companyId, item.id, $txtBox);

		var $addOn = $($txtBox).closest("tr").find(".addOn");
		if (typeof $addOn != "undefined") {
			var uri = contextPath + "/getItemAddOn?itemId=" + item.id
					+ "&companyId=" + companyId;
			var itemAddOnId = $($txtBox).closest("tr").find(".itemAddOnId")
					.val();
			populateSelect(uri, itemAddOnId, $addOn, doAfterProcess);
		}

		populateTaxTypes($txtBox, doAfterProcess)
	}
}

function populateTaxTypes($txtBox, doAfterProcess) {
	var $taxType = $($txtBox).closest("tr").find(".taxType");
	if (typeof $taxType != "undefined") {
		var taxTypeId = $($txtBox).closest("tr").find(".taxTypeId").val();
		var uri = contextPath + "/getTaxTypes/new?isReceivable=true";
		if (typeof taxTypeId != "undefined" && taxTypeId != null && taxTypeId != "") {
			uri += "&taxTypeId=" + taxTypeId;
		}
		populateSelect(uri, taxTypeId, $taxType, doAfterProcess)
	}
}

function processItem(setting, $table, $txtBox, doAfterProcess) {
	var stockCode = $.trim($($txtBox).val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getRetailSerialItem?isActiveOnly=" + (itemId != 0 ? "false" : "true")
			+ "&stockCode=" + encodeURIComponent(stockCode);
	var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
	if (warehouseId != undefined && warehouseId != "") {
		uri += "&warehouseId=" + warehouseId;
	}
	var serialNumber = $($txtBox).closest("tr").find(".serialNumber").val();
	if (serialNumber != undefined) {
		uri += "&isSerialized=true";
	}
	if (stockCode != "") {
		$.ajax({
			url : uri,
			async : false,
			success : function(item) {
				if (item != null) {
					if (setting.disableDuplicateStockCode) {
						validateStockCode($table, $txtBox, item.item,
								doAfterProcess);
					} else {
						processStockCode($table, $txtBox, item.item,
								doAfterProcess);
					}
				}
			},
			error : function(error) {
				console.log(error);
				$("#" + setting.itemTableMessage).css("color", "red")
						.text('Invalid Stock Code');
				$($txtBox).focus();
			},
			dataType : "json"
		});
	}
}

function populateSelect(uri, selectedValue, $select, doAfterProcess) {
	var classAttrib = $select.attr("class");
	$($select).empty();
	var optionParser = {
		getValue : function(rowObject) {
			if (rowObject != null)
				return rowObject["id"];
		},

		getLabel : function(rowObject) {
			if (rowObject != null) {
				if (classAttrib == "warehouse tblSelectClass") {
					return rowObject["name"] + " - "
							+ rowObject["existingStocks"];
				} else {
					return rowObject["name"];
				}
			}
		}
	};

	var postHandler = {
		doPost : function(data) {
			if (classAttrib == "addOn tblSelectClass") {
				computeAddOn($select, doAfterProcess);
			} else if (classAttrib == "warehouse tblSelectClass") {
				assignESByWarehouse($select);
			}

			// This is to remove any duplication.
			var found = [];
			$($select).each(function() {
				if ($.inArray(this.value, found) != -1)
					$(this).remove();
				found.push(this.value);
			});
			if (typeof selectedValue != "undefined" && selectedValue != null) {
				$($select).val(selectedValue);
			}
		}
	};
	// Prepend empty value for Discount and Add ons
	var prependEmpty = (classAttrib == "warehouse tblSelectClass") ? false : true;
	loadPopulateObject(uri, false, selectedValue, $select, optionParser, postHandler, false, prependEmpty);
}

function computeVatAmount($select, taxTypeId, isOtherCharges, discount) {
	var $tr = $($select).closest("tr");
	if (typeof discount == "undefined") {
		discount = 0.0;
	}
	// Process discount and add on
	var origQuantity = accounting.unformat($tr.find(".quantity").val());
	var quantity = Math.abs(origQuantity);
	var qtyDiscount = Math.abs(quantity != 0 ? (discount / quantity) : 0);
	var unitPrice = $tr.find(".srp").html();
	if (isOtherCharges) {
		unitPrice = $tr.find(".upAmount").val();
		quantity = quantity != 0 ? quantity : 1;
	}
	unitPrice = accounting.unformat(unitPrice);
	var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
	var grossAmount = quantity * unitPrice;
	var netOfVAT = isVatable ? formatDecimalPlaces(grossAmount / 1.12) : grossAmount;
	var vat = formatDecimalPlaces(grossAmount - accounting.unformat(netOfVAT));
	$tr.find(".vatAmount").html(vat);
	var netAmount = parseFloat((grossAmount - accounting.unformat(vat)).toFixed(6));
	if (!isOtherCharges) {
		var salesReferenceId = $tr.find(".salesRefId").val();
		if (salesReferenceId != 0 && salesReferenceId != null && salesReferenceId != undefined) {
			if (origQuantity < 0) {
				amount = amount * -1;
			}
		}
		$tr.find(".amount").html(formatDecimalPlaces(netAmount));
	} else {
		$tr.find(".amount").val(formatDecimalPlaces(netAmount));
	}
}

function assignESByWarehouse($select) {
	// Disable the drop down list on edit.
	var lineId = $($select).closest("tr").find(".id").val();
	if ($("#hdnFormId").val() > 0 && lineId > 0) {
		$($select).attr("disabled", "disabled");
	}

	if ($select.has("option").length > 0) {
		var $existingStocks = $($select).closest("tr").find(".existingStocks");
		var text = $select.find(":selected").text().split(" - ");
		var formatES = text[text.length - 1];
		$existingStocks.html(formatDecimalPlaces(formatES));
	}
}

function populateSrp(companyId, itemId, $txtBox) {
	var $salesReferenceId = $($txtBox).closest("tr").find(".salesRefId");
	var salesReferenceId = $salesReferenceId.val();
	if (salesReferenceId == 0 || salesReferenceId == "" || salesReferenceId == undefined) {
		var uri = "/getItemSrp?companyId=" + companyId + "&itemId=" + itemId;
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url : contextPath + uri,
			async : false,
			success : function(item) {
				if (item != null) {
					var $origSrp = $($txtBox).closest("tr").find(".origSrp");
					$origSrp.val(item.sellingPrice);

					var $srp = $($txtBox).closest("tr").find(".srp");
					$srp.html(formatDecimalPlaces(item.sellingPrice));

					var $itemSrpId = $($txtBox).closest("tr").find(".itemSrpId");
					$itemSrpId.val(item.id);
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType : "json"
		});
	} else {
		var $origSrp = $($txtBox).closest("tr").find(".origSrp");
		var $srp = $($txtBox).closest("tr").find(".srp");
		$srp.html($origSrp.val());
	}
}

function computeDiscount($input, itemDiscount) {
	var discount = 0.0;
	var grossAmount = getGrossAmount($input);
	var itemDiscountTypeId = itemDiscount.itemDiscountTypeId;
	var $quantity = $($input).closest("tr").find(".quantity");
	var quantity = $($quantity).val();
	var $serialNumber = $($input).closest("tr").find(".serialNumber");
	if (typeof $serialNumber.val() != "undefined"){
		quantity = $($quantity).html();
	}
	if (itemDiscountTypeId == DT_PERCENTAGE) {
		discount = Math.abs(grossAmount * (itemDiscount.value / 100));
	} else if (itemDiscountTypeId == DT_AMOUNT) {
		discount = quantity < 0 ? (itemDiscount.value * -1)
				: itemDiscount.value;
	} else if (itemDiscountTypeId == DT_QTY_DISCOUNT) {
		discount = quantity * itemDiscount.value;
	}
	return accounting.unformat(formatDecimalPlaces(discount));
}

function processDiscountAndAmount(setting, totalHandler, $table, $tr, $input) {
	var $discountType = $($tr).find(".discountType");
	var $amount = $($tr).find(".amount");
	var $discount = $($discountType).closest("tr").find(".discount");
	if ($discountType == undefined && $amount != undefined) {
		computeAmountByCost(setting, $table, totalHandler, $input, false, 0.0);
		return;
	}
	var itemDiscountId = $($discountType).val();
	if (itemDiscountId == null || itemDiscountId == "") {
		computeAmountByCost(setting, $table, totalHandler, $input, false, 0.0);
		var $amount = $($discountType).closest("tr").find(".amount");
		if ($amount == undefined) {
			return;
		}
	} else {
		var uri = contextPath+"/getItemDiscount?itemDiscountId="+itemDiscountId;
		$.ajax({
			url : uri,
			async : false,
			success : function(itemDiscount) {
				var discount = computeDiscount($discountType, itemDiscount);
				var $discount = $($discountType).closest("tr").find(".discount");
				$discount.html(formatDecimalPlaces(discount));
				computeAmountByCost(setting, $table, totalHandler, $input, false, discount)
			},
			error : function(error) {
				console.log(error);
			},
			dataType : "json"
		});
	}
}

function computeAddOn($textBox, doAfterCompute) {
	var $srp = $($textBox).closest("tr").find(".srp");
	var $origSrp = $($textBox).closest("tr").find(".origSrp");
	var origSrp = accounting.unformat($origSrp.val());
	var $quantity = $($textBox).closest("tr").find(".quantity");
	var addOn = $($textBox).closest("tr").find(".addOn").val();
	var taxTypeId = $($textBox).closest("tr").find(".taxTypeId").val();
	var quantity = accounting.unformat($quantity.val());
	if (addOn != null && $.trim(addOn) != "" && typeof addOn != "undefined") {
		var uri = contextPath + "/getItemAddOn/computeAddOn?itemAddOnId="+addOn
				+"&srp="+origSrp+"&quantity="+quantity+"&taxTypeId="+taxTypeId;
		$.ajax({
			url : uri,
			async : false,
			success : function(addOn) {
				var computedAddOn = addOn.computedAddOn;
				var srp = accounting.unformat($origSrp.val());
				srp = parseFloat(srp) + parseFloat(computedAddOn);
				$srp.html(formatDecimalPlaces(srp));
				if (doAfterCompute != undefined) {
					doAfterCompute.process();
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType : "json"
		});
	} else {
		$srp.text(formatDecimalPlaces($origSrp.val()));
		doAfterCompute.process();
	}
}

function computeTotal(setting, $table, totalHandler) {
	if (setting.footer != null) {
		for (var index = 0; index < setting.footer.length; index++) {
			var cls = setting.footer[index].cls;
			var total = computeTotalPerColumn($table, setting.footer[index].cls);
			var $footerSpan = $table.find("tfoot tr ." + cls);
			if (totalHandler != undefined) {
				totalHandler.handleTotal(total);
			}
			$footerSpan.html(formatDecimalPlaces(total, 2));
		}
	}

	// Computing the grand total in the form.
	// Will only work if there is a computeGrandTotal function declared in the form.
	if (typeof computeGrandTotal == 'function') {
		computeGrandTotal();
	}
}

function computeTotalArLines(setting, $table, totalHandler) {
	var total = 0;
	$($table).find("tbody tr").each(function() {
		var amount = accounting.unformat($(this).find("input.amount").val());
		total += amount;
	});

	$($table).find("tfoot tr .amount").text(formatDecimalPlaces(total, 2));
	// Computing the grand total in the form.
	// Will only work if there is a computeGrandTotal function declared in the
	// form.
	if (typeof computeGrandTotal == 'function') {
		computeGrandTotal();
	}
}

function computeTotalPerColumn($table, column) {
	var cls = column;
	var total = 0;
	$($table).find("tbody tr ." + cls).each(function() {
		var $span = $(this);
		var value = accounting.unformat($span.html());
		total += value;
	});
	return total;
}

function getGrossAmount($input) {
	var grossAmount = 0;
	var $amount = $($input).closest("tr").find(".amount");
	var $srp = $($input).closest("tr").find(".srp");
	if ($amount == undefined || $srp == undefined) {
		return grossAmount;
	}
	var $quantity = $($input).closest("tr").find(".quantity");
	var quantity = accounting.unformat($($quantity).val());
	var srp = accounting.unformat($srp.html());
	var taxTypeId = $($input).closest("tr").find(".taxTypeId").val();
	var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
	grossAmount = isVatable ? ((quantity * srp) / 1.12) : (quantity * srp);
	return grossAmount;
}

function updateOChargesAmt() {
	var totalAmount = 0;
	var updateTotalAmt = false;
	var $footerSpan = $("#otherChargesTable").find("tfoot tr .amount");
	$("#otherChargesTable tbody tr").each(function(row) {
		var netAmount = 0;
		var qty = accounting.unformat($(this).find(".quantity").val());
		qty = qty != 0 ? qty : 1;
		var upAmt = accounting.unformat($(this).find(".upAmount").val());
		if (qty != 0 || upAmt != 0) {
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
}

function loadItemSerialNumbers(elem) {
	var companyId = $("#companyId").val();
	var warehouseId = $(elem).closest("tr").find(".warehouse").val();
	var $serialNumber = $(elem).closest("tr").find(".serialNumber");
	var serialNumber = encodeURIComponent($serialNumber.val());
	var hdnRefObjectId = $("#hdnRefObjectId").val();
	var stockCode = $.trim($(elem).closest("tr").find(".stockCode").val());
	var uri = contextPath+"/getRetailSerialItem/getSerialNumber?serialNumber="+serialNumber
			+"&companyId="+companyId+"&warehouseId="+warehouseId
			+"&stockCode="+processSearchName(stockCode)+"&isExact=false&isActive=true";
	if (typeof hdnRefObjectId != "undefined") {
		uri += "&referenceObjectId=" + hdnRefObjectId;
	}
	var divisionId = $("#divisionId").val();
	if (divisionId != undefined) {
		uri += "&divisionId="+divisionId;
	}
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
						$(elem).closest("tr").find(".refenceObjectId").val(ui.item.ebObjectId);
						$(elem).closest("tr").find(".unitCost").val(ui.item.unitCost);
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

function getSerialItem(elem) {
	$("#serializedItemErrors").html("");
	var companyId = $("#companyId").val();
	var warehouseId = $(elem).closest("tr").find(".warehouse").val();
	var $serialNumber = $(elem).closest("tr").find(".serialNumber");
	var serialNumber = $serialNumber.val();
	var hdnRefObjectId = $("#hdnRefObjectId").val();
	var stockCode = $.trim($(elem).closest("tr").find(".stockCode").val());
	if (($.trim(serialNumber) != "" && typeof serialNumber != "undefined")
			&& (stockCode != "" && typeof stockCode != "undefined" )) {
		var uri = contextPath + "/getRetailSerialItem/getSerialNumber?serialNumber="+$.trim(serialNumber)
				+"&companyId="+companyId+"&warehouseId="+warehouseId
				+"&stockCode="+processSearchName(stockCode)+"&isExact=true";
		if (typeof hdnRefObjectId != "undefined") {
			uri += "&referenceObjectId="+hdnRefObjectId;
		}
		var divisionId = $("#divisionId").val();
		if (divisionId != undefined) {
			uri += "&divisionId="+divisionId;
		}
		$.ajax({
			url: uri,
			success : function(item) {
				if ((serialNumber != "" && typeof serialNumber != "undefined")
						&& (stockCode != "" && typeof stockCode != "undefined" )) {
					if (item != "" && typeof item != "undefined") {
						$(elem).closest("tr").find(".refenceObjectId").val(item[0].ebObjectId);
						$serialNumber.val(item[0].serialNumber);
					} else {
						$serialNumber.focus();
						$("#serializedItemErrors").html("Invalid serial number.");
					}
				}
			},
			error : function(error) {
				$("#serializedItemErrors").html("Invalid serial number.");
			},
			dataType: "json"
		});
	}
}