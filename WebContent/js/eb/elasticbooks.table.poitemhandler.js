/**
 * Object that handles the sales table. 
 * This is only applicable with table with the following columns.
 * 1. Stock code (text)
 * 2. Description
 * 3. Existing Stocks
 * 4. Warehouse (Select)
 * 5. Qty (Input)
 * 6. UOM

 */
ItemTableHandler = function (totalHandler) {
	
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
			}, minLength: 1,
			change: function(event, ui){
				var stockCode = $(this).val();
				if (currentItem != null && stockCode == currentItem.stockCode){
					return false;
				}
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>"+ item.stockCode + "-" +item.description + "</a>" )
				.appendTo( ul );
		};
	};
	var prevSelected = null;
	
	function emptyValues (setting, $table, $txtBox) {
		$($txtBox).closest("tr").find(".quantity").val("");
	}

	this.initRowData = function (setting, $table, $txtBox) {
//		console.log ("$txtBox : " + $txtBox.val());
		processItem(setting, $table, $txtBox);
	};
	
	this.processSelectedItems = function (setting, $table, $txtBox) {
//		console.log ("$txtBox : " + $txtBox.val());
		processItem(setting, $table, $txtBox);
	};
	
	this.computeRow = function (setting, $table, $tr, $input) {
		var $quantity = $($tr).find(".quantity");
		computeAmountByCost(setting, $table, this.totalHandler, $quantity);
	};
	
	this.onDelete = function (setting, $table) {
		// Do nothing
	};
};

function validateStockCode(setting, $table, $txtBox, currentItem) {
	var currentRow = Number($($txtBox).closest("tr").find(".rowNumber").text());
	var currentItemId = currentItem.id;
	var itemIdPerRow = null;
	var isDuplicateStockCode = false;
//	console.log("current item id: "+currentItemId+" at row "+currentRow);
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
					processStockCode(currentItem, $txtBox);
					return false;
				}
			}
		} else {
			itemIdPerRow = $($txtBox).closest("tr").find(".itemId").val();
			var desc = $($txtBox).closest("tr").find(".description").val();
			if(itemIdPerRow > 0 && desc == "") {
				processStockCode(currentItem, $txtBox);
				return false;
			}
		}
	});

	if(isDuplicateStockCode) {
		$("#"+setting.itemTableMessage).text("Duplicate stock code");
		$($txtBox).focus();
	}
}
function assignESByWarehouse($select) {
	// Disable the drop down list on edit.
	var lineId = $($select).closest("tr").find(".id").val();
	if ($("#hdnFormId").val() > 0 && lineId > 0) {
		$($select).attr("disabled", "disabled");
	}

	if ($select.has("option").length > 0) {
		var $existingStocks = $($select).closest("tr").find(".existingStock");
		var text = $select.find(":selected").text().split(" - ");
		var formatES = Number(text[text.length - 1]).toFixed(8).replace(/\.?0+$/,"");
		$existingStocks.html(formatDecimalPlaces(formatES));
	}
}

function processStockCode (item, $txtBox) {
	if (item == undefined)
		return;

	var $description = $($txtBox).closest("tr").find(".description");
	$description.html(item.description);

	var $uom = $($txtBox).closest("tr").find(".uom");
	$uom.html(item.unitMeasurement.name);

	var $itemId = $($txtBox).closest("tr").find(".itemId");
	$itemId.val(item.id);

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
		stockCode = encodeURIComponent($.trim(stockCode));

		var $warehouse = $($txtBox).closest("tr").find(".warehouse");
		if (typeof $warehouse != "undefined") {
			var uri = contextPath + "/getWarehouse/withES?companyId="
			+ companyId + "&itemId=" + item.id;
			var warehouseId = $($txtBox).closest("tr").find(".warehouseId").val();
			populateSelect(uri, warehouseId, $warehouse, item);
		}
	}
}

function populateSelect (uri, selectedValue, $select, item) {
	$($select).empty();
	var classAttrib = $select.attr("class");
	var optionParser = {
			getValue: function (rowObject){
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
			doPost: function(data) {
				// This is to remove any duplication.
	 			var found = [];
				$($select).each(function() {
					if($.inArray(this.value, found) != -1) 
						$(this).remove();
					found.push(this.value);

					$(this).find("option").each(function() {
						if ($select.attr("class") == "warehouse tblSelectClass") {
							assignESByWarehouse($select);
						}
					});
				});
				if (typeof selectedValue != "undefined" && selectedValue != null ) {
					$($select).val(selectedValue);
				}
			}
	};
	// Prepend empty value for Discount and Add ons
	var prependEmpty = (classAttrib == "warehouse tblSelectClass") ? false : true;
	loadPopulateObject (uri, false, selectedValue, $select, optionParser, postHandler, false, prependEmpty);
}

function getLatestUC($txtBox, itemId) {
	var $prevUC = $($txtBox).closest("tr").find(".prevUC");
	var supplierAccountId = $("#selectSupplierAcctId").val();
	if($prevUC.length != 0 && (supplierAccountId != null && supplierAccountId.length != 0)) {
		var uri = contextPath + "/retailPurchaseOrder/getLatestUC?itemId="+itemId
				+"&supplierAcctId="+supplierAccountId;
		$.ajax({
			url: uri,
			success : function(prevUC) {
				$prevUC.html(formatDecimalPlaces(prevUC, 4));
			},
			error : function(error) {
				console.log(error);
			}
		});
	} else {
		$prevUC.html(formatDecimalPlaces(0.0));
	}
}

function processItem(setting, $table, $txtBox) {
	var stockCode = $.trim($($txtBox).val());
//	console.log("============================= " + stockCode);
	var companyId = $("#companyId").val();
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "")
		+ "?stockCode="+encodeURIComponent(stockCode); 
	if(companyId != undefined)
		uri += "&companyId="+companyId;
	if(stockCode != "") {
		$.ajax({
			url: uri,
			success : function(item) {
				if (item != null) {
					if(setting.disableDuplicateStockCode) {
						validateStockCode(setting, $table, $txtBox, item);
						getLatestUC($txtBox, item.id);
					} else {
						processStockCode(item, $txtBox);
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

function computeAmountByCost (setting, $table, totalHandler, $initCostOrQuantity) {
	var $unitCost =$($initCostOrQuantity).closest("tr").find(".unitCost");
	var $quantity = $($initCostOrQuantity).closest("tr").find(".quantity");
	var $amount = $($initCostOrQuantity).closest("tr").find(".amount");
//	console.log($unitCost + "=======" + $quantity + "========" + $($amount).val());
	var quantity = accounting.unformat($quantity.val());
	var unitCost = accounting.unformat($unitCost.html());
	if(unitCost == 0) {
		unitCost = accounting.unformat($unitCost.val());
	}
	var formattedAmount = formatDecimalPlaces(Number(quantity*unitCost));

	$($amount).html(formattedAmount);
	computeTotal(setting, $table, totalHandler);
}

function computeTotal (setting, $table, totalHandler) {
//	console.log("computing total ++++++++++++");
	for (var index = 0; index < setting.footer.length; index++) {
		var cls = setting.footer[index].cls;
		var total = computeTotalPerColumn($table, setting.footer[index].cls);
		var $footerSpan = $table.find("tfoot tr ."+cls);
//		console.log("$footerSpan === " + $footerSpan);
//		console.log("total === " + formatDecimalPlaces(total));
		if (totalHandler != undefined)
			totalHandler.handleTotal (total);
		$footerSpan.html(formatDecimalPlaces(total));
	}

//	updateOChargesAmt();
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