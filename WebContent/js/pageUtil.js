/**
 * Utility class for search process. 

 */

/**
 * Function that handles the redirecting of a page to a specific page number.
 * 
 * @param divTableId The div that contains the table to which the search result will be loaded.
 * @param targetUrl The target url that will be invoked after a successful search. Exclude the context path.
 */
function goToPage ( divTableId, targetUrl) {
	$("#" + divTableId).load(contextPath + targetUrl);
}

/**
 * Function that handles the marking of the current page.
 * 
 * @param pageNumber The current page number.
 */
function markCurrentPageNumber (pageNumber) {
	$("a#page-" + pageNumber).css("font-size", "16px");
	$("a#page-" + pageNumber).css("color", "red");
}