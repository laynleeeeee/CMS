/**
 * Object that handles the sales table. 
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
 * 8. Discount (Select)
 * 9. Discount - Computed
 * 10. Amount
 */

var DT_PERCENTAGE = 1;
var DT_AMOUNT = 2;
var DT_QTY_DISCOUNT = 3;
SalesTableHandler = function (totalHandler) {

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

	function emptyValues (setting, $table, $txtBox) {
		$($txtBox).closest("tr").find(".quantity").val("");
		$($txtBox).closest("tr").find(".discount").html("");
		$($txtBox).closest("tr").find(".amount").html("");
		computeTotal (setting, $table, this.totalHandler);
	}

	this.initRowData = function (setting, $table, $txtBox) {
		console.log ("$txtBox : " + $txtBox.val());
		var totalHandler = this.totalHandler;
		var $tr = $($txtBox).closest("tr");
		var doAfterProcessItem = {
				process : function () {
					processDiscountAndAmount (setting, totalHandler, $table, $tr, $txtBox);
				}
		};
		processItem(setting, $table, $txtBox, doAfterProcessItem);
	};

	this.processSelectedItems = function (setting, $table, $txtBox) {
		console.log ("$txtBox : " + $txtBox.val());
		var totalHandler = this.totalHandler;
		var $tr = $($txtBox).closest("tr");
		var doAfterProcessItem = {
				process : function () {
					processDiscountAndAmount (setting, totalHandler, $table, $tr, $txtBox);
				}
		};
		processItem(setting, $table, $txtBox, doAfterProcessItem);
	};

	this.computeRow = function (setting, $table, $tr, $input) {
		var totalHandler = this.totalHandler;
		var doAfterProcessItem = {
				process : function () {
					processDiscountAndAmount (setting, totalHandler, $table, $tr, $input);
				}
		};
		computeAddOn($input, doAfterProcessItem);
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
		var $discountType = $($txtBox).closest("tr").find(".discountType");
		if (typeof $discountType != undefined) {
			var uri = contextPath + "/getItemDiscount/filter?itemId="+item.id+"&companyId="+companyId;
			var itemDiscountId = $($txtBox).closest("tr").find(".itemDiscountId").val();
			populateSelect(uri, itemDiscountId, $discountType, item, doAfterProcessItem);
		}

		var $warehouse = $($txtBox).closest("tr").find(".warehouse");
		if (typeof $warehouse != "undefined") {
			var uri = contextPath + "/getWarehouse?companyId="+companyId;
			var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
			populateSelect(uri, warehouseId, $warehouse, item, doAfterProcessItem);
		}

		var $srp = $($txtBox).closest("tr").find(".srp");
		if (typeof $($srp) != "undefined") {
			populateSrp(companyId, item.id, $txtBox);
		}

		var $addOn = $($txtBox).closest("tr").find(".addOn");
		if (typeof $addOn != "undefined") {
			var uri = contextPath + "/getItemAddOn?itemId="+item.id;
			var itemAddOnId = $($txtBox).closest("tr").find(".itemAddOnId").val();
			console.log ("ITEM ADD ID : " + itemAddOnId);
			populateSelect(uri, itemAddOnId, $addOn, item, doAfterProcessItem);
		}
	}
}

function processItem(setting, $table, $txtBox, doAfterProcessItem) {
	var stockCode = $.trim($($txtBox).closest("tr").find(".stockCode").val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "")
		+ "?stockCode="+encodeURIComponent(stockCode);
	var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
	if (warehouseId != undefined && warehouseId != "") {
		uri += "&warehouseId="+warehouseId;
	}
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

function populateSelect (uri, selectedValue, $select, item, doAfterProcessItem) {
	$($select).empty();
	var classAttrib = $select.attr("class");
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
				if (classAttrib == "addOn tblSelectClass") {
					computeAddOn($select, doAfterProcessItem);
				}

				// This is to remove any duplication.
	 			var found = [];
				$($select).each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
					found.push(this.value);

					$(this).find("option").each(function() {
						if ($select.attr("class") == "warehouse tblSelectClass") {
							if ($(this).is(':selected')) {
								var $availableStock = $($select).closest("tr").find(".availableStock");
								if($($availableStock).length > 0) {
									console.log("set values for stock code: "+item.stockCode);
									var formId = $("#hdnFormId").val();
									var orTypeId =  $("#hdnOrTypeId").val();
									// Populate only available stocks when available stocks drop down list is present.
									setValues ($availableStock, $(this).val(), item.id, formId, orTypeId);
								}

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
	// Prepend empty value for Discount and Add ons
	var prependEmpty = (classAttrib == "warehouse tblSelectClass") == true ? false : true;
	loadPopulateObject (uri, false, selectedValue, $select, optionParser, postHandler, false, prependEmpty);
}

function setValues($warehouse, warehouseId, itemId, formId, orTypeId) {
	var ebObjectId = $($warehouse).closest("tr").find(".ebObjectId").val();
	var $refObjectId = $($warehouse).closest("tr").find(".referenceObjectId");
	var $orig = $($warehouse).closest("tr").find(".origRefObjectId");
//	console.log("set values. form id: "+formId+" or type id: "+orTypeId+" eb object id "
//			+ebObjectId+" :: ref object id "+$($refObjectId).val());
	if(ebObjectId != "" && ebObjectId != 0 && typeof ebObjectId != "undefined"
			&& ($($orig).length > 0 && $($orig).val() == "")) {
				populateAvailableStock($warehouse, warehouseId, itemId, $($refObjectId).val(), formId, ebObjectId);
	} else {
		populateAvailableStock($warehouse, warehouseId, itemId, null, formId, ebObjectId);
	}
}

function populateAvailableStock ($warehouse, warehouseId, itemId, refObjectId, formId, ebObjectId) {
	console.log("populateAvailableStock");
	if(refObjectId == null) {
		refObjectId = $($warehouse).closest("tr").find(".referenceObjectId").val();
		var orTypeId = $("#hdnOrTypeId").val();
		var $reference = $($warehouse).closest("tr").find(".referenceId");
		if((orTypeId == 11 || orTypeId == 12) && $($reference).val() != undefined) {
			// For Paid in Advance OR Type only.
			refObjectId = $($reference).val();
		}
		console.log("refObjectId: " + refObjectId);
	}
	var $availableStock = $($warehouse).closest("tr").find(".availableStock");
	itemId = encodeURIComponent(itemId);
	if (typeof $availableStock != "undefined") {
		if (warehouseId == null) {
			warehouseId = $($warehouse).val();
		}
		if (itemId == null) {
			itemId = $($warehouse).closest("tr").find(".itemId").val();
		}
		$($availableStock).empty();
		var refObjectParam = (formId > 0 && typeof refObjectId != "undefined") ? "&referenceObjectId="+refObjectId : "";
		var companyId = $("#companyId").val();
		console.log("ebObjectId: " + ebObjectId);
		$.ajax({
			url: contextPath + "/getAvailableBags?companyId="+companyId
					+"&itemId="+itemId+"&warehouseId="+warehouseId+"&ebObjectId="+ebObjectId,
			success : function(availableStocks) {
				for ( var index = 0; index < availableStocks.length; index++) {
					var availableStock = availableStocks[index];
					var option = "<option ";
					if(refObjectId != null) {
						if(refObjectId == availableStock.sourceObjId) {
							option += " selected='selected'";
						}
					}
					option += " id='" + availableStock.sourceObjId + "'";
					option += " value='" + availableStock.unitCost + "'>";
					var label = availableStock.source + " - " + availableStock.totalBags + " - " + availableStock.totalStocks;
					option += label + "</option>";
					$($availableStock).append(option);
				}
				setReferenceDetails($($availableStock));
			},
			dataType: "json"
		});
	}
}

function setReferenceDetails($txtBox) {
	console.log("set reference details.");
	var $availableStock = $($txtBox).closest("tr").find(".availableStock");
	var $referenceObjectId = null;
	if($($availableStock).val() != undefined) {
//		console.log("available stock is not underfined.");
		//Set the reference object id
		var ebObjectId = $($availableStock).children(":selected").attr("id");
		$referenceObjectId = $($txtBox).closest("tr").find(".referenceObjectId");
		$($referenceObjectId).val(ebObjectId);

		//Set the unit cost
		var unitCost = $($availableStock).val();
		var $unitCost = $($txtBox).closest("tr").find(".unitCost");
		$($unitCost).val(unitCost);
	} else {
		$referenceObjectId = $($txtBox).closest("tr").find(".referenceObjectId");
		$($referenceObjectId).val("");
	}
}

function computeDiscount ($input, itemDiscount) {
	var discount = 0.0;
	var grossAmount = getGrossAmount($input);
	console.log("gross amount ==== " + grossAmount);
	console.log("itemDiscount ====" + itemDiscount);
	console.log("itemDiscount ====" + itemDiscount.value);
	var itemDiscountTypeId = itemDiscount.itemDiscountTypeId;
	console.log("itemDiscountTypeId ===" + itemDiscountTypeId);
	var $quantity = $($input).closest("tr").find(".quantity");
	var quantity = $($quantity).val();
	if (itemDiscountTypeId == DT_PERCENTAGE)
		discount = grossAmount * (itemDiscount.value / 100); 
	else if (itemDiscountTypeId == DT_AMOUNT) {
		discount = quantity < 0 ? (itemDiscount.value * -1) : itemDiscount.value;
	} else if (itemDiscountTypeId == DT_QTY_DISCOUNT) {
		discount = quantity * itemDiscount.value;
	}
	console.log("discount ====" + discount);
	return accounting.unformat(formatDecimalPlaces(discount));
}

// This should be called after computeDiscount, otherwise this will not work as
// expected.
function computeTotalAmount ($input) {
	var $discount = $($input).closet("tr").find(".discount");
	var discount = 0.0;
	if ($discount != undefined)
		discount = accounting.unformat($(discount).html());
	var grossAmount = getGrossAmount($input);
	return grossAmount - discount;
}

function processDiscountAndAmount (setting, totalHandler, $table, $tr, $input) {
	var $discountType = $($tr).find(".discountType");
	var $amount = $($tr).find(".amount");
	if ($discountType == undefined && $amount != undefined) {
		var grossAmount = computeTotalAmount($input);
		$amount.val(account.formatMoney(grossAmount));
		return;
	}

	var itemDiscountId = $($discountType).val();
	console.log("itemDiscountId == null || itemDiscountId ==" + itemDiscountId == null || itemDiscountId == "");
	if (itemDiscountId == null || itemDiscountId == "") {
		var $discount = $($discountType).closest("tr").find(".discount");
		$discount.html("0.00");
		var discount = 0;
		var $amount = $($discountType).closest("tr").find(".amount");
		if ($amount == undefined)
			return;
		var grossAmount = getGrossAmount($input);
		$amount.html(formatDecimalPlaces(grossAmount - discount));

		computeTotal (setting, $table, totalHandler);// compute total
	} else {
		var uri = contextPath + "/getItemDiscount?itemDiscountId="+itemDiscountId;
		$.ajax({
			url: uri,
			success : function(itemDiscount) {
				var discount = computeDiscount($discountType, itemDiscount);
				var $discount = $($discountType).closest("tr").find(".discount");
				$discount.html(formatDecimalPlaces (discount)); // Discount

				var $amount = $($discountType).closest("tr").find(".amount");

				var grossAmount = getGrossAmount($discountType);
				$amount.html(formatDecimalPlaces(grossAmount - discount));

				computeTotal (setting, $table, totalHandler);// compute total
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	}
}

function computeAddOn ($textBox, doAfterProcessItem) {
	var $srp = $($textBox).closest("tr").find(".srp");
	var $origSrp = $($textBox).closest("tr").find(".origSrp");
	var $addOn = $($textBox).closest("tr").find(".addOn");
	if ($addOn.val() != null && $.trim($addOn.val()) != "" && 
			typeof $addOn.val() != "undefined" && !isNaN($addOn.val())) {
		$.ajax({
			url: contextPath + "/getItemAddOn/byId?itemAddOnId="+accounting.unformat($addOn.val()),
			success : function(addOn) {
				var srp = accounting.unformat($origSrp.val());
				srp = parseFloat(srp) + parseFloat(addOn.value);
				$srp.html(formatDecimalPlaces(srp));
				if (doAfterProcessItem != undefined) {
					doAfterProcessItem.process();
				}
			},
			error : function(error) {
				console.log(error);
			},
			dataType: "json"
		});
	} else {
		$srp.html(formatDecimalPlaces($origSrp.val()));
		doAfterProcessItem.process();
	}
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
	var salesReferenceId = $salesReferenceId.val()
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

function getExistingStocks($txtBox, stockCode, warehouseId){
	stockCode = encodeURIComponent($.trim(stockCode));
	$.ajax({
		url: contextPath + "/getAvailableStocks/total?warehouseId="
				+warehouseId+"&stockCode="+stockCode,
		success : function(existingStocks) {
			$td = $($txtBox).closest("tr").find("td").eq(5);
			$($td).css("text-align", "right");
			var $existingStocks = $($td).find(".existingStocks");
			var formatES = formatDecimalPlaces(existingStocks);
			$existingStocks.text(formatES);
		}, error : function(error) {
		},
		dataType: "json"
	});
}

function calculateQty($tr, companyId) {
	var $qty = $($tr).find(".quantity");
	if($($qty).val() == 0 || $($qty).val() == "") {
		var refObjectId = $($tr).find(".referenceObjectId").val();
		var itemId = $($tr).find(".itemId").val();
		var warehouseId = $($tr).find(".warehouseId").val();
		var bagsToWithraw = $($tr).find(".itemBagQuantity").val();
		var origRefObjectId = $($tr).find(".origRefObjectId").val();
		console.log("refObjectId: "+refObjectId);
		$.ajax({
			url: contextPath + "/getAvailableBags/proportionQty?companyId="+companyId+"&itemId="+itemId+
				"&warehouseId="+warehouseId+"&refObjectId="+refObjectId+"&bagsToWithdraw="+bagsToWithraw+"&origRefObjectId="+origRefObjectId,
			success : function(qty) {
				$($qty).val(formatDecimalPlaces(qty));
				$($qty).trigger("blur");
			}, error : function(error) {
			},
			dataType: "text"
		});	
	}
}