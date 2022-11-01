ProjectRetentionHandler = function(totalHandler) {
	this.totalHandler = totalHandler;

	this.showItems = function(setting, $table, $txtBox) {
		//Do Nothing
	};

	// Auto complete function for service setting
	this.showServiceSettings = function(setting, $table, $txtBox) {
		//Do Nothing
	};

	// Auto complete for UOM
	var currentUom = null;
	this.showUoms = function(setting, $table, $txtBox) {
		//Do Nothing
	};

	function emptyValues(setting, $table, $txtBox) {
		$($txtBox).closest("tr").find(".quantity").val("");
		$($txtBox).closest("tr").find(".discount").html("");
		$($txtBox).closest("tr").find(".amount").html("");
		computeTotal(setting, $table, this.totalHandler);
	}

	this.initRowData = function(setting, $table, $txtBox, isOtherCharges) {
		populateTaxTypes($txtBox);
		if(isOtherCharges) {
			computeAmountByCost(setting, $table, totalHandler, $txtBox, isOtherCharges, 0);
		}
	};

	this.processSelectedItems = function(setting, $table, $txtBox) {
		//Do Nothing
	};

	this.processOtherCharges = function (setting, $table, $txtBox, isOtherCharges) {
		//Do Nothing
	};

	this.computeRow = function(setting, $table, $tr, $txtBox, isOtherCharges) {
		if(isOtherCharges) {
			computeAmountByCost(setting, $table, totalHandler, $txtBox, isOtherCharges, 0);
		}
	};

	this.onDelete = function(setting, $table, isOtherCharges) {
		computeTotal(setting, $table, this.totalHandler);
	};
};

function computeAmountByCost(setting, $table, totalHandler, $textBox, isOtherCharges, discount) {
	var taxTypeId = $($textBox).closest("tr").find(".taxTypeId").val();
	computeVatAmount($textBox, taxTypeId, isOtherCharges, discount);
	computeTotal(setting, $table, totalHandler);
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
	var netOfVat = isVatable ? (unitPrice / 1.12).toFixed(6) : unitPrice;
	var vat = unitPrice - netOfVat;
	var totalItemVat = (vat * quantity).toFixed(6);
	totalItemVat = formatDecimalPlaces(totalItemVat);
	$tr.find(".vatAmount").html(totalItemVat);
	var computedUnitPrice = (unitPrice - vat) - qtyDiscount;
	var amount = computedUnitPrice * Math.abs(quantity);
	if (!isOtherCharges) {
		var salesReferenceId = $tr.find(".salesRefId").val();
		if (salesReferenceId != 0 && salesReferenceId != null && salesReferenceId != undefined) {
			if (origQuantity < 0) {
				amount = amount * -1;
			}
		}
		$tr.find(".amount").html(formatDecimalPlaces(amount));
	} else {
		$tr.find(".amount").html(formatDecimalPlaces(amount));
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
