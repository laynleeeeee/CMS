/**
 * Object that handles the item table for requisition related forms.
 * The filtering of items is based on the pre-configured Item Category:
 *  1. Tire
 *  2. Fuel
 *  3. PMS
 *  4. Electrical
 *  5. Construction Material
 *  6. Admin
 *  7. Motorpool
 *  8. Oil
 *  9. Subcon Settlement
 *  10. Pakyawan
 * 
 * This is only applicable with table with the following columns.
 *  1. Stock code (text)
 *  2. Description
 *  3. Existing Stocks
 *  5. Qty (Input)
 *  6. UOM
 *

 */
RQItemTableHandler = function (totalHandler, requisitionTypeId) {

	this.totalHandler = totalHandler;

	this.showItems = function (setting, $table, $txtBox) {
		var stockCode = encodeURIComponent($.trim($txtBox.val()));
		var uri = contextPath+"/getRItems/filter?stockCode="+stockCode;
		var warehouseId = $("#warehouseId").val();
		if (warehouseId != undefined) {
			uri += "&warehouseId="+warehouseId;
		}
		var currentItem;
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
		processItem(setting, $table, $txtBox);
	};
	
	this.processSelectedItems = function (setting, $table, $txtBox) {
		processItem(setting, $table, $txtBox);
		getExistingStocks($txtBox)
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
	$($table).find(" tbody tr ").each(function(row) {
		if(Number(row+1) != currentRow) {
			itemIdPerRow = $(this).find(".itemId").val();
			if(currentItemId != "") {
				if(currentItemId == itemIdPerRow) {
					isDuplicateStockCode = true;
					return false;
				} else {
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

function processStockCode (item, $txtBox) {
	if (item == undefined)
		return;
	var $existingStocks = $($txtBox).closest("tr").find(".existingStock");
	var formatES = accounting.formatMoney(item.existingStocks);
	$existingStocks.html(formatES);

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
}

function getExistingStocks($txtBox) {
	var companyId = $("#companyId").val();
	var stockCode = $.trim($($txtBox).closest("tr").find(".stockCode").val());
	var $existingStocks = $($txtBox).closest("tr").find(".existingStock");
	var uri = contextPath + "/getItem/getExistingStocksByItemCategory?stockCode="+stockCode
			+"&companyId="+companyId;
	var warehouseId = $("#warehouseId").val();
	if (typeof warehouseId != undefined && warehouseId != null && warehouseId != "") {
		uri += "&warehouseId="+warehouseId;
	}
	$.ajax({
		url: uri,
		success : function(existingStocks) {
			var formatES = accounting.formatMoney(existingStocks);
			$existingStocks.html(formatES);
		},
		error : function(error) {
			console.log(error);
		}
	});
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
				var formattedPrevUC = accounting.formatMoney(prevUC);
				$prevUC.html(formattedPrevUC);
			},
			error : function(error) {
				console.log(error);
			}
		});
	} else {
		$prevUC.html(accounting.formatMoney(0.0));
	}
}

function processItem(setting, $table, $txtBox) {
	var stockCode = $.trim($($txtBox).val());
	var itemId = $($txtBox).closest("tr").find(".itemId").val();
	var uri = contextPath + "/getItem" + (itemId != 0 ? "/withInactive" : "")
		+ "?stockCode=" + encodeURIComponent(stockCode); 
	var warehouseId = $("#warehouseId").val();
	if (typeof warehouseId != undefined && warehouseId != null && warehouseId != "") {
		uri += "&warehouseId="+warehouseId;
	}
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
	var quantity = accounting.unformat($quantity.val());
	var unitCost = accounting.unformat($unitCost.html());
	if(unitCost == 0) {
		unitCost = accounting.unformat($unitCost.val());
	}
	var formattedAmount = accounting.formatMoney(Number(quantity*unitCost));

	$($amount).html(formattedAmount);
	computeTotal(setting, $table, totalHandler);
}

function computeTotal (setting, $table, totalHandler) {
	for (var index = 0; index < setting.footer.length; index++) {
		var cls = setting.footer[index].cls;
		var total = computeTotalPerColumn($table, setting.footer[index].cls);
		var $footerSpan = $table.find("tfoot tr ."+cls);
		if (totalHandler != undefined)
			totalHandler.handleTotal (total);
		$footerSpan.html(accounting.formatMoney(total));
	}

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