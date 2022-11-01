<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="../js/jquery/jquery1.7.2min.js"></script>
<script src="../js/dojo/dojo.js" type="text/javascript"></script>
<script type="text/javascript">
function reloadTable() {
	var selectedYear = document.getElementById("yearList").options[document.getElementById("yearList").selectedIndex].value;
	var thisUrl = 'accountReceivableChart/annual/' + selectedYear;

	dojo.xhrGet({
      url: thisUrl,
      load: function (data) {   
    	  		dojo.byId("accountReceivableAnnualChartTable").innerHTML = data;
      		},
      error: function (data, ioArgs){
    	  		dojo.byId("accountReceivableAnnualChartTable").innerHTML = "unknown error";
      		}
	});
} 
</script>
</head>
<body>
	<table>
		<tr>
			<td>
				Year:
			</td>
			
			<td>
				<select id="yearList" onchange="reloadTable();">
					<c:forEach var="year" begin="1900" end="2100" >
						<option value="${year}" ${year == selectedYear ? 'selected' : ''}>${year}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
	</table>
	<div id="accountReceivableAnnualChartTable">
		<%@ include file="AccountReceivableAnnualChartTable.jsp" %>
	</div>
</body>
</html>