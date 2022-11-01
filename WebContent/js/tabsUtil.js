/**
 * Utility class for tab processes.

 */

function loadDivContents (divId, uri) {
	var $div = $("#" + divId);
	if ($.trim($($div).html()) == "") {
		var url = contextPath + uri;
		$("#" + divId).load(url, function () {
		});
	}
	processTabOnClick();
}

function processTabOnClick() {
	$('ul.tabs li').click(function(){
		var tab_id = $(this).attr('data-tab');

		$('ul.tabs li').removeClass('current');
		$('.tab-content').removeClass('current');

		$(this).addClass('current');
		$("#"+tab_id).addClass('current');
	});
}

