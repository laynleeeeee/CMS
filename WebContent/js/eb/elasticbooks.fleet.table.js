/**
 * All of the function related for elasticbook chl tables.
 * 

 */
(function ($, window, i) {
	'use strict'; // ???
	// Item edit table for items.  
	$.fn.editableItem = function (otherParams) {
				
		
		var rowNumber = 1; // Row number
		var setting = $.extend({
            data: [['']],
            footer: [['']],
            disableDuplicateStockCode: true,
            addEmptyRow: true,
            itemTableMessage: [['']],
		}, otherParams);
		
		var $divObj = $(this);
		var $table = $("<table/>", {
			html: "<thead><tr></tr></thead><tbody></tbody><tfoot><tr></tr></tfoot>"
		});
		
		// Set up CSS
		$divObj.addClass("ebDataTable");
		$divObj.append($table);

		$table.find("thead tr").append("<th width='3%'></th>");
		$table.find("thead tr").append("<th width='2%'>#</th>");
		
		//$table.attr("border", 1);
		for (var i = 0; i < setting.header.length; i++) {
			var headerCell = setting.header[i];
			var isVisible = headerCell.visible;
			
			if (isVisible) {
				var title = headerCell.title;
				var width = headerCell.width;
				$table.find("thead tr").append("<th width='"+width+"'>" + title + "</th>");
			}
		}
		populateTable();
		populateFooter();
		reloadAndCompute();
		
		/**
		 * Clear the body and footer data.
		 */
		function clearTable() {
			console.log("clearing table body");
			// cleaning tbody table
			$table.find("tbody").html("");
			createEmptyRow().appendTo($table.find("tbody"));
			updateRowNumber();
			// clean footer.
			$table.find("tfoot tr").html("");
			populateFooter();
		}
		
		// Functions
		function populateTable() {
			console.log("settings data : " + setting.data);
			if (setting.data == null || setting.data == "null" || setting.data == "") {
				createEmptyRow().appendTo($table.find("tbody"));
			} else {
				//console.log(setting.data.length);
				//console.log(setting.data);
				for (var i = 0; i < setting.data.length; i++) {
					var rowData = setting.data[i];
					
					//console.log("rowdata" + rowData);
					buildRow(rowData).appendTo($table.find("tbody"));
				}
			}
		}

		function populateFooter() {
			var rowContent = null;
			for (var index = 0; index < setting.footer.length; index++){
				var cls = setting.footer[index].cls;
				if (typeof cls == "undefined")
					return;
				var $th = $("table tbody tr:first-child").find("."+cls).parent();
				var col = parseInt($th.parent().children().index($th));
				//console.log("col == " + col);
				if (rowContent == null)
					rowContent = "<td colspan='"+(col)+"'></td>";
				rowContent += "<td><span class='"+cls+"'>0.0</span></td>";
			}
			//console.log("rowContent == " + rowContent);
			$table.find("tfoot tr").append(rowContent);
		}
		
		function reloadAndCompute() {
			$($table).find("tbody tr").each(function (i) {
				var $row = $(this).closest("tr");
				var $stockCode = $row.find(".stockCode");
				computeRow($stockCode);
				for (var i = 0; i < setting.header.length; i++) {
					var headerCell = setting.header[i];
					var className = headerCell.cls;
					if (className.indexOf("stockCode ") == 0) {
						//console.log("processing stock code.");
						headerCell.handler.initRowData(setting, $table, $stockCode);
						return;
					}
				}
			});
		}

		function createEmptyFooterRow(column) {
			var rowContent = "";
			for (var i = 0; i < column; i++){
				rowContent += "<td></td>";
			}
			return rowContent;
		}
		
		function createEmptyRow () {
			var tds = createEmptyTds();
			return $('<tr/>', {
				html: tds
			});
		}
		
		function createEmptyTds () {
			console.log("creating empty row..");
			var defaultRowsAndHidden = getDefaultRowContent ();
			var rowContent = "";
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var isHidden = !headerCell.visible;
				var cssClass = headerCell.cls;
				if (isHidden) {
					defaultRowsAndHidden += "<input type='hidden' class='"+cssClass+"' name='' value='' />";
					continue;
				}
				rowContent += buildCell("", cssClass, headerCell);
			}
			return defaultRowsAndHidden + "</td>" + rowContent;
		}
		
		function getDefaultRowContent () {
			var rowContent = "<td class='tdProperties' align='center'><a class='delrow'><img  src='"+setting.contextPath+"/images/delete_active.png'/></a></td>";
			rowContent += "<td class='rowNumber tdProperties'><span>"+(rowNumber++)+"</span>";// add all hidden text here.
			return rowContent;
		}

		function getJsonPropertyName (headerCell) {
			for (var i = 0; i < setting.jsonProperties.length; i++) {
				var prop = setting.jsonProperties[i];
				var headerClsName = headerCell.cls;
				var name = prop.name;
				if ((headerClsName.indexOf(name + " ") == 0)|| headerClsName == name){
					return prop;
				}
			}
			return null;
		}

		function buildRow (rowData) {
			var defaultRowsAndHidden = getDefaultRowContent ();
			var rowContent = "";
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var value = "0";
				var $property = getJsonPropertyName(headerCell);
				if ($property != null) {
					//console.log("property : " + $property.name);
					if (rowData.hasOwnProperty($property.name)) {
						value = rowData[$property.name];
					}
					
					if ($property.varType == "double") {
						if($property.name != "unitCost") {
							value = accounting.formatMoney(value);
						} else {
							value = accounting.toFixed(value, 2);
						}
					}

				}
				//console.log("value : " + value);
				var isHidden = !headerCell.visible;
				var cssClass = headerCell.cls;
				if (isHidden) {
					defaultRowsAndHidden += "<input type='hidden' class='"+cssClass+"' name='' value='"+value+"' />";
					continue;
				}
				rowContent += buildCell(value, cssClass, headerCell);
			}
			defaultRowsAndHidden += "</td>";
			return $('<tr/>', {
				html: defaultRowsAndHidden + rowContent
			});
		}

		function buildCell (cellData, cssClass, headerCell) {
			var editorType = headerCell.editor;
			var cellContent = "";
			if (editorType == "label") {
				cellContent = "<span class='"+cssClass+"'>"+cellData+"</span>";
			} else if (editorType == "select") {
				cellContent = "<select class='"+cssClass+"'></select>";
			} else if (editorType == "checkbox") {
				var checkAttrib = "";
				if (cellData == true || (typeof cellData == "string")) {
					checkAttrib = "checked='checked'";
				}
				cellContent = "<input type='checkbox' class='"+cssClass +"' name='' "+checkAttrib+" />";
			} else if (editorType == "file") {
				cellContent = "<input type='file' class='"+cssClass +"' name='' "+checkAttrib+" />";
			} else if (editorType.indexOf("datePicker") >= 0) {
				if (cellData.trim() != "" && cellData.trim() != "0") {
					var date = new Date(cellData);
					cellData = (date.getMonth() + 1) + '/' + date.getDate() + '/' +  date.getFullYear();
				} else {
					cellData = "";
				}
				cellContent = "<nobr><input type='text' class='"+cssClass + "' style='width: 85%' value='" + cellData + "' />";
				cellContent += "<img src='"+setting.contextPath+"/images/cal.gif' class='clickDate' /></nobr>";
			} else if (editorType == "radioButton") {
				var checkAttrib = "";
				if (cellData == true || (typeof cellData == "boolean")) {
					checkAttrib = "checked='checked'";
				}
				var value = cellData == true;
				cellContent = "<input type='radio' class='"+cssClass +"' name='"+cssClass +"' "+checkAttrib+" value='" + value + "'/>";
			} else {
				cellContent = "<input type='text' class='"+cssClass +"' name='' value='" + cellData + "' />";
			}

			var tdClass = headerCell.tdCls;
			var cls = "class='tdProperties";
			if (tdClass != undefined) {
				cls +=" " + tdClass;
			}
			cls += "'"; // Close the class attribute

			return "<td "+cls+">"+cellContent+"</td>";
			//console.log(cellContent);
		}

		function updateRowNumber () {
			rowNumber = 1; 
			$table.find('.rowNumber').each(function () {
				var $span = $(this).find("span");
				$span.html(rowNumber++);
			});
		}
		
		function isEmpty($tr) {
			var isEmpty = true;
	        for (var i = 0; i < setting.jsonProperties.length; i++) {
				var property = setting.jsonProperties[i];
				var propertyName = property.name;
				//console.log(propertyName);
				var $value = $tr.find("."+propertyName);
				var value = "0";
				if ($($value).is("span")){
					value = $($value).html();
				} else if ($($value).is("input")){
					value = $($value).val();
				}
				
				if (value == "")
					value ="0";
				if (property.varType == "double" || property.varType == "int") {
					value = accounting.unformat(value); 
				}
				//console.log("value====" +   value);
				if (value != "0" && value != "" && value != "0.00") {
					isEmpty = false;
				}
	    	}
	        return isEmpty;
		}

		// Events
		$table.on('click', '.delrow', function (){
			var rowCount = $($table).find("tbody tr").length;
			if (rowCount <= 1)
				return;
			var $parent = $(this).closest("tr"); // Closest TR only
			$parent.remove();
			updateRowNumber();
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var className = headerCell.cls;
				if (className.indexOf("stockCode ") == 0
						|| className.indexOf("arLineSetupName") == 0
						|| className.indexOf("apLineSetupName") == 0
						|| className.indexOf("description") == 0
						|| className.indexOf("transactionNumber") == 0) {
					var isOtherCharge = className.indexOf("arLineSetupName") == 0 
						|| className.indexOf("apLineSetupName") == 0;
					var isItemReservation = className.indexOf("description") == 0
						|| className.indexOf("transactionNumber") == 0;
					headerCell.handler.onDelete(setting, $table, isOtherCharge ? isOtherCharge : isItemReservation);
					return;
				}
			}
			return false;
		});
		
		// Stock code keydown function
		$table.on('keydown keyup', '.stockCode', function (){
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var className = headerCell.cls;
				if (className.indexOf("stockCode ") == 0) {
					//console.log("processing stock code.");
					headerCell.handler.showItems(setting, $table, $(this));
					return;
				}
			}
		});

		//AP Line setup keyup, keydown function
		$table.on('keydown keyup', '.apLineSetupName', function (){
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var className = headerCell.cls;
				if (className.indexOf("apLineSetupName") == 0) {
					headerCell.handler.showApLineSetups(setting, $table, $(this));
					return;
				}
			}
		});

		//AR Line setup keyup, keydown function
		$table.on('keydown keyup', '.arLineSetupName', function (){
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var className = headerCell.cls;
				if (className.indexOf("arLineSetupName") == 0) {
					headerCell.handler.showArLineSetups(setting, $table, $(this));
					return;
				}
			}
		});

		//UOM keyup, keydown function
		$table.on('keydown keyup', '.unitMeasurementName', function (){
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var className = headerCell.cls;
				if (className.indexOf("unitMeasurementName ") == 0) {
					headerCell.handler.showUoms(setting, $table, $(this));
					return;
				}
			}
		});

		$table.on("focusout", ".stockCode", function(e) {
			//var code = e.keyCode || e.which;
			//console.log("focusout ..." + code);
			var $parent = $(this).closest("tr"); // Closest TR only
			if (($.trim($(this).val()) == "") && !isEmpty($parent)) {
				$parent.html(createEmptyTds ());
				$parent.find(".stockCode").focus();
				updateRowNumber();
				//computeTotal();
			} else {
				for (var i = 0; i < setting.header.length; i++) {
					var headerCell = setting.header[i];
					var className = headerCell.cls;
					if (className.indexOf("stockCode ") == 0) {
						//console.log("processing stock code.");
						headerCell.handler.processSelectedItems(setting, $table, $(this));
						return;
					}
				}
			}
			
		});

		function loadPositionTypes($tr){
			var positionType = "<option value='37'>Captain</option>";
			positionType += "<option value='38'>MDM</option>";
			var $select = $($tr).find(".captainMdm");
			if($($select).html() == ""){
				$($select).html("");
				$($select).append(positionType);
				var val = $($tr).closest("tr").find(".orTypeId").val();
				console.log("VAL :"+val);
				if(val == ""){
					$($tr).closest("tr").find(".orTypeId").val($($select).val());
				}
			}
		}

		function computeRow ($input) {
			for (var i = 0; i < setting.header.length; i++) {
				var headerCell = setting.header[i];
				var className = headerCell.cls;
				if (className.indexOf("stockCode ") == 0) {
					//console.log("processing stock code.");
					var $tr = $($input).closest("tr");
					headerCell.handler.computeRow(setting, $table, $tr, $input);
					return;
				}
			}
		}

		$table.on("change", ".warehouse", function (){
			$(this).closest("tr").find(".warehouseId").val($(this).val());
		});

		$table.on("change", ".discountType", function (){
			$(this).closest("tr").find(".itemDiscountId").val($(this).val());
			computeRow($(this));
		});

		$table.on("change", ".addOn", function (){
			$(this).closest("tr").find(".itemAddOnId").val($(this).val());
			computeRow($(this));
		});

		$table.on("focusout", ".quantity", function(){
			computeRow($(this));
		});

		$table.on("focusout", ".unitCost", function(){
			computeRow($(this));
		});

		$table.on("focusout", ".discountType", function (){
			computeRow($(this));
		});

		$table.on("focusout", ".addOn", function (){
			computeRow($(this));
		});

		$table.on("change", ".slctDiscount", function (){
			computeRow($(this));
		});

		$table.on("change", ".slctAddOn", function (){
			computeRow($(this));
		});

		$table.on("focus", "tr",function () {
			var $nextTr = $($(this)).next("tr");
			//computeTotal();
			if ($nextTr.html() == undefined) {
				if(setting.addEmptyRow) {
					createEmptyRow().appendTo($table.find("tbody"));
				}
			}
			loadPositionTypes($(this));
		});

		$table.on('click', '.clickDate', function (){
			var $row = $(this).closest("tr");
			var $col = $(this).closest("td");
			var $target = $($col).find("input");
			var id = "input" + $($row).find(".rowNumber").text() + $col[0].cellIndex;
			$($target).attr("id", id);
			javascript:NewCssCal(id);
		});

		function exportData () {
			//var table = $divObj.find ("table");
			var row = 0, data = [];

		    $table.find('tbody tr').each(function () {
		    	var $tr = $(this);
		    	// warehouse
		    	var $warehouse = $tr.find (".warehouse");
		    	var $warehouseId = $tr.find (".warehouseId");
		    	
		    	if ($warehouse != undefined) {
		    		$warehouseId.val($warehouse.val());
		    	}
		    	
		    	var $discountType = $tr.find (".discountType");
		    	var $itemDiscountId = $tr.find (".itemDiscountId");
		    	if ($discountType != undefined) {
		    		$itemDiscountId.val($discountType.val());
		    	}
		    	
		    	var $addOn = $tr.find (".addOn");
		    	var $itemAddOnId = $tr.find (".itemAddOnId");
		    	if ($addOn != undefined) {
		    		$itemAddOnId.val($addOn.val());
		    	}

				//Buying Details
				var $discount = $tr.find (".slctDiscount");
				if ($discount != undefined) {
					var $itemDiscountId = $tr.find (".buyingDiscountId");
					$itemDiscountId.val($discount.val());
				}

				var $addOn = $tr.find (".slctAddOn");
				if ($addOn != undefined) {
					var $itemAddOnId = $tr.find (".buyingAddOnId");
					$itemAddOnId.val($addOn.val());
				}

				var $arLineSetup = $tr.find (".arLineSetupName");
				if($arLineSetup != undefined) {
					$arLineSetup.val($.trim($arLineSetup.val()));
				}

				var $uom = $tr.find(".unitMeasurementName");
				if($uom != undefined) {
					$uom.val($.trim($uom.val()));
				}

		    	row += 1;
		       //var keys = Object.keys(setting.data[0]);
		        //var keyIndex = 0;
		        // setting the data.
		    	var isEmpty = true;
		        var obj = new Object();
		        for (var i = 0; i < setting.jsonProperties.length; i++) {
					var property = setting.jsonProperties[i];
					var propertyName = property.name;
					//console.log(propertyName);
					var $value = $(this).find("."+propertyName);
					var value = "0";
					if ($($value).is("span")){
						value = $($value).html();
					} else if ($($value).is("input")){
						var $input = $($value);
						if ($input.is(":checkbox")) {
							value = $($value).is(":checked");
						} else {
							value = $($value).val();
						}
					}

					if (value == "")
						value ="0";
					if (property.varType == "double" || property.varType == "int") {
						value = accounting.unformat(value); 
					}
					// console.log("value ==== " +   value);
					if (value != "0" && value != "" && value != "0.00") {
						isEmpty = false;
						obj[property.name] =  value;
					}
		    	}
		        if (isEmpty) {
		        	row-=1;
		        	return;
		        } else {
		        	data[row] = obj;
		        }
		        
		    });

		    // Remove undefined
		    data.splice(0, 1);

		    return JSON.stringify(data);
		}
		
		// Return functions
		return {
			getData: function () {
				return exportData();
			},
			/**
			 * Compute the amount by unit cost. 
			 * Unit cost * quantity.
			 */
			computeAmountByUnitCost: function ($unitCostOrQty) {
				//computeAmountByCost ($unitCostOrQty);
			},
			emptyTable: function () {
				clearTable();
			},
			getTotal : function (cssColumnName) {
				return computeTotalPerColumn(cssColumnName);
			}
		};
	};
})(jQuery, this, 0);
