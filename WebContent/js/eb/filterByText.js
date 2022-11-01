/**
* Description: A common jQuery function for filtering a select menu.

*/

/**
 * Filters options from the select tag.
 * source: http://dragonfruitdevelopment.com/filter-a-select-menu-with-jquery/
 * <br>Implementation:
 * $(selectId).filterByText($(textBoxId), sort);
 * 
 * @param selectId The Id of the select menu.
 * @param textBoxId The Id of the text box for searching.
 * @param sort (boolean) set to true for sort (ascending order), false for no sorting.
 */
jQuery.fn.filterByText = function(textbox) {
		return this.each(function() {
			var select = $(this);
			var options = [], defaultOption = '';

			select.find('option').each(function() {
				var elm = $(this);
				if (elm.data('default') != true) {
					options.push({value : elm.val(), text : elm.text()});
				} else {
					defaultOption = elm.text();
				}
			});

			$(textbox).bind('change keyup',function() {
				select.empty();
				var search = $.trim($(this).val());
				var regex = new RegExp(search, "gi");

				if (search == '') {
					select.append($('<option>').text(
							defaultOption).prop('selected',
							true));
				}
				$.each(options, function(i) {
					var option = options[i];
					if (option.text.match(regex) !== null) {
						select.append($('<option>').text(
								option.text).val(
								option.value));
					}
				});
			});
		});
	};