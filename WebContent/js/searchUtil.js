/**
 * Utility class for search process. 

 */

/**
 * Function that handles basic search.
 * 
 * @param divTableId The div that contains the table to which the search result will be loaded.
 * @param targetUrl The target url that will be invoked after a successful search. Exclude the context path.
 */
function doSearch (divTableId, targetUrl) {
	$("#" + divTableId).load(contextPath + targetUrl);
}

/**
 * Function that handles the space between search key words by placing "+" between them.
 * Example : Key word = FC Bayern Munich , the result after the process is FC+Bayern+Munich.
 * 
 * @param keyword The search keyword.
 * @returns keyword The processed keyword.
 */
function processSearchName(keyword) {
	// Return encoded string parameter".	
	return encodeURIComponent(keyword);
}



