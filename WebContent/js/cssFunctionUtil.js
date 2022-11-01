/**
 * A class that will handle CSS function. 

 */


/*
 * Replace the current css class with a new class.
 */
function updatePopupCss() {
	removeClass("span#btnClose","btnClose");
	addClass("span#btnClose", "showCloseBtn");
}

/*
 * Revert back to previous css class.
 */
function revertPopupCss() {
	removeClass("span#btnClose","showCloseBtn");
	addClass("span#btnClose", "btnClose");
}

/*
 * Remove a class using element, id or class it self.
 */
function removeClass(element,classToBeRemove) {
	$(element).removeClass(classToBeRemove);
}

/*
 * Add a class using element, id or class it self.
 */
function addClass(element,classToBeAdded) {
	$(element).addClass(classToBeAdded);
}
