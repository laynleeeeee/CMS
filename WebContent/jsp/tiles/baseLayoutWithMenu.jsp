<%@ include file="../include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
		<script src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js" type="text/javascript"></script>
    	<script type="text/javascript"> 
    	$(document).ready(function() {
			$("#menuSeparator").css("border-right", "1px solid #000");
 			$("#menuSeparator").css("height", $("#mainContent").height());
    	});
    	
    	$(function() {		
    		var $element = $("#mainContent");
    		var lastHeight = $("#mainContent").css('height');

    		function checkForChanges(){
    			if ($element.css('height') != lastHeight) {
    	        	$("#menuSeparator").css('height', $("#mainContent").height());
    	        	lastHeight = $element.css('height'); 
    	    	}

    	    	setTimeout(checkForChanges, 'fast');
    		}

    		$(checkForChanges);
    	});
    	</script>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
            <title><tiles:insertAttribute name="title" ignore="true" /></title>
    </head>
    <body>
        <table align="center" width="100%" height="100%">
            <tr>
                <td colspan="2" >
                    <tiles:insertAttribute name="header" />
                </td>
            </tr>
            <tr>
				<td style="width: 100%; height: 100%"  valign="top">
					<table  id="mainContent" style="width: 100%; height: 100%;" >
						<tr>
							<td valign="top" style="width: 19%;">
            					<tiles:insertAttribute name="menu" />
            				</td>
            				<td valign="top" style="width: 1%; height: 100%; text-align: center;">
            					<div id="menuSeparator"></div>
            				</td>
                			<td valign="top" style="width: 80%;">
                					<tiles:insertAttribute name="body" />
                			</td> 
						</tr>
					</table>
				</td>
            </tr>
            <tr>
                <td colspan="2">
                    <tiles:insertAttribute name="footer" />
                </td>
            </tr>
        </table>
    </body>
</html>
