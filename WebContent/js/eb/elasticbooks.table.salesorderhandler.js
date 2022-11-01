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
		var uri = contextPath+"/getRItems/filter?stockCode="+encodeURIComponent(stockCode);
		var warehouseId = $("#warehouseId").val();
		if (warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		$($txtBox).autocomplete({
			source : uri,
			select : function(event, ui) {
				$($txtBox).closest("tr").find(".itemId").val(ui.item.id);
				$(this).val(ui.item.stockCode);
				return false;
			},
			minLength : 1,
		}).data("ui-autocomplete")._renderItem = function(ul, item) {
			return $("<li>").data("ui-autocomplete-item", item).append(
					"<a style='font-size: small;'>" + item.stockCode + "-"
						+ item.description + "</a>").appendTo(ul);
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
		$($txtBox).autocomplete({
			source : uri,
			select : function(event, ui) {
				$(this).val(ui.item.name);
				$($txtBox).closest("tr").find(".serviceSettingId").val(ui.item.id);
				$($txtBox).closest("tr").find(".upAmount").val(ui.item.amount);
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
		var $tr = $($txtBox).closest("tr");
		if (isOtherCharges) {
			populateTaxTypes($txtBox, true);
			populateDiscountTypes($txtBox, true);
			processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, true);
		} else {
			var doAfterProcessItem = {
				process : function() {
					processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, false);
				}
			};
			processItem(setting, $table, $txtBox, doAfterProcessItem);
		}
	};

	this.processSelectedItems = function(setting, $table, $txtBox) {
		var $tr = $($txtBox).closest("tr");
		var doAfterProcessItem = {
			process : function() {
				processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, false);
			}
		};
		processItem(setting, $table, $txtBox, doAfterProcessItem);
	};

	this.processOtherCharges = function (setting, $table, $txtBox, isOtherCharges) {
		populateTaxTypes($txtBox);
		populateDiscountTypes($txtBox, isOtherCharges);
		processDiscountAndAmount(setting, this.totalHandler, $table,
				$($txtBox).closest("tr"), $txtBox, isOtherCharges);
	};

	this.computeRow = function(setting, $table, $tr, $txtBox, isOtherCharges) {
		processDiscountAndAmount(setting, this.totalHandler, $table, $tr, $txtBox, isOtherCharges);
	};

	this.onDelete = function(setting, $table, isOtherCharges) {
		computeTotal(setting, $table, this.totalHandler);
		if (isOtherCharges) {
			computeTotalArLines(setting, $table, this.totalHandler);
		}
		if(typeof computeWtax == 'function') {
			computeWtax();
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
					processStockCode($table, $txtBox, currentItem, doAfterProcess);
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
	$($txtBox).closest("tr").find(".itemId").val(item.id);
	$($txtBox).closest("tr").find(".description").html(item.description);
	$($txtBox).closest("tr").find(".uom").html(item.unitMeasurement.name);

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
		if (typeof $warehouse != undefined && item.id > 0) {
			var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
			var uri = contextPath+"/getWarehouse/withES?companyId="+companyId+"&itemId="+item.id;
			if (warehouseId != undefined) {
				uri += "&warehouseId="+warehouseId;
			}
			populateSelect(uri, warehouseId, $warehouse, doAfterProcess);
		}

		populateDiscountTypes($txtBox, false);
		populateTaxTypes($txtBox, doAfterProcess);
		populateItemExistingStocks($txtBox);
	}
}

function populateItemExistingStocks($txtBox) {
	var companyId = $("#companyId").val();
	var stockCode = $.trim($($txtBox).closest("tr").find(".stockCode").val());
	var $existingStocks = $($txtBox).closest("tr").find(".existingStocks");
	var uri = contextPath + "/getItem/existingStockBySC?stockCode="+stockCode+"&companyId="+companyId;
	$.ajax({
		url: uri,
		success : function(existingStocks) {
			var formatES = formatDecimalPlaces(existingStocks);
			$existingStocks.html(formatES);
		},
		error : function(error) {
			console.log(error);
		}
	});
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

function processItem(setting, $table, $txtBox, doAfterProcess) {
	var stockCode = $.trim($($txtBox).val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "")
			+ "?stockCode=" + encodeURIComponent(stockCode);
	var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
	if (warehouseId != undefined && warehouseId != "") {
		uri += "&warehouseId=" + warehouseId;
	}
	if (stockCode != "") {
		$.ajax({
			url : uri,
			async : false,
			success : function(item) {
				if (item != null) {
					if (setting.disableDuplicateStockCode) {
						validateStockCode($table, $txtBox, item, doAfterProcess);
					} else {
						processStockCode($table, $txtBox, item, doAfterProcess);
					}
				}
			},
			error : function(error) {
				console.log(error);
				$("#" + setting.itemTableMessage).css("color", "red").text('Invalid Stock Code');
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
			if (classAttrib == "warehouse tblSelectClass") {
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
	var unitPrice = $tr.find(".grossAmount").val();
	var quantity = Math.abs(origQuantity);
	var qtyDiscount = Math.abs(quantity != 0 ? (discount / quantity) : discount);
	if (isOtherCharges) {
		unitPrice = $tr.find(".upAmount").val();
		quantity = quantity != 0 ? quantity : 1;
	}
	unitPrice = accounting.unformat(unitPrice) - qtyDiscount; // Discount should be deducted before computing VAT.
	var isVatable = taxTypeId == 1 || taxTypeId == 4 || taxTypeId == 5 || taxTypeId == 6 || taxTypeId == 8 || taxTypeId == 9 || taxTypeId == 10;
	var grossAmount = quantity * unitPrice;
	var netOfVAT = isVatable ? formatDecimalPlaces(grossAmount / 1.12) : grossAmount;
	var vat = formatDecimalPlaces(grossAmount - accounting.unformat(netOfVAT));
	$tr.find(".vatAmount").html(vat);
	var netAmount = parseFloat((grossAmount - accounting.unformat(vat)).toFixed(6));

	if (isOtherCharges) {
		$tr.find(".amount").val(formatDecimalPlaces(netAmount));
	} else {
		$tr.find(".amount").html(formatDecimalPlaces(netAmount));
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

function computeTotal(setting, $table, totalHandler) {
	if (setting.footer != null) {
		for (var index = 0; index < setting.footer.length; index++) {
			var cls = setting.footer[index].cls;
			var total = computeTotalPerColumn($table, setting.footer[index].cls);
			var $footerSpan = $table.find("tfoot tr ." + cls);
			if (totalHandler != undefined) {
				totalHandler.handleTotal(total);
			}
			$footerSpan.html(formatDecimalPlaces(total));
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

	$($table).find("tfoot tr .amount").text(formatDecimalPlaces(total));
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

function getGrossAmount($input, isOtherCharges) {
	var $amount = $($input).closest("tr").find(".amount");
	var srp = 0;
	if (isOtherCharges) {
		srp = $($input).closest("tr").find(".upAmount").val();
	} else {
		srp = $($input).closest("tr").find(".grossAmount").val();
	}
	srp = accounting.unformat(srp);
	if ($amount == undefined || srp == 0) {
		return 0.0;
	}
	var $quantity = $($input).closest("tr").find(".quantity");
	var quantity = accounting.unformat($($quantity).val());
	if (isOtherCharges) {
		quantity = quantity != 0 ? quantity : 1;
	}
	var grossAmount = quantity * srp;
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
