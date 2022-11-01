<%@ include file="../include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
            <title><tiles:insertAttribute name="title" ignore="true" /></title>
    </head>
    <body >
        <table align="center" width="100%" height="100%">
            <tr>
                <td valign="top" id="tdHeader">
                    <tiles:insertAttribute name="header" />
                </td>
            </tr>
            <tr style="width: 100%; height: 100%;"  valign="top">
                <td valign="top" id="tdBody" style="padding: 0px; " height="100%">
                    <tiles:insertAttribute name="body" />
                </td>
            </tr>
            <tr>
                <td valign="top" id="tdFooter">
                    <tiles:insertAttribute name="footer" />
                </td>
            </tr>
        </table>
    </body>
</html>
