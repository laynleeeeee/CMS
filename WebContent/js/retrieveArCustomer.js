/**
 * Customer auto Complete.

 */

function showCustomers (name, arCustomerId, companyId, errCustomerMsg, errCompMsg, isExact) {
	var $companyId = $("#" + companyId);
	var $customerId = $("#" + arCustomerId);
	var $name = $("#" + name);
	var $errCustomerMsg = $("#" + errCustomerMsg);
	var $errCompMsg = $("#" + errCompMsg);
	if($($companyId).val() == -1) {
		$($errCompMsg).text("Company is required.");
	} else {
		var customerName = $.trim($($name).val());
		$($errCustomerMsg).text("");
		var uri = contextPath + "/getArCustomers/new?name="+encodeURIComponent(customerName)
				+"&companyId="+$($companyId).val()+"&isExact="+isExact;
		$($name).autocomplete({
			source: uri,
			select: function( event, ui ) {
				$($customerId).val(ui.item.id);
				$(this).val(ui.item.name);
				return false;
			}, minLength: 2,
			change: function(event, ui){
				$.ajax({
					url: uri,
					success : function(item) {
						if (ui.item != null) {
							$(this).val(ui.item.name);
						}
					},
					error : function(error) {
						$($errCustomerMsg).text("Please select customer.");
						$($name).val("");
					},
					dataType: "json"
				});
			}
		}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
			return $( "<li>" )
				.data( "ui-autocomplete-item", item )
				.append( "<a style='font-size: small;'>" +item.name + "</a>" )
				.appendTo( ul );
		};
	}
}

function getCustomer(name, arCustomerId, companyId, errCustomerMsg, errCompMsg, isExact) {
	var $companyId = $("#" + companyId);
	var $customerId = $("#" + arCustomerId);
	var $name = $("#" + name);
	var $errCustomerMsg = $("#" + errCustomerMsg);
	var $errCompMsg = $("#" + errCompMsg);
	if($($companyId).val() == -1) {
		$($errCompMsg).text("Company is required.");
	} else {
		var customerName = $.trim($($name).val());
		var uri = contextPath + "/getArCustomers/new?name="+encodeURIComponent(customerName)
				+"&companyId="+$($companyId).val()+"&isExact="+isExact;
		$.ajax({
			url: uri,
			success : function(customer) {
				if (customer != null && customer[0] != undefined) {
					$($customerId).val(customer[0].id);
					$($name).val(customer[0].name);
				} else {
					if(customerName != "") {
						$($errCustomerMsg).text("Invalid customer");
					}
					$($customerId).val("");
				}
			},
			dataType: "json"
		});
	}
}