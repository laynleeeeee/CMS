<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>

  	 Description: Fleet Tool
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/base64Util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/eb/CMS.table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dateUtil.js"></script>
<script type="text/javascript">
var $fleetToolsTbl = null;
var $fTRefDocsTable = null;
var fpDivisionId = Number("${fpDivisionId}");

function buildJson() {
	var json = "[";
	var maxLength = $("#tblFleetToolCondition tbody tr").length - 1;
	$("#tblFleetToolCondition tbody tr").each(function(i) {
		json += "{";
		$(this).find("td").each(function () {
			var tdCls = $(this).attr("class");
			if (tdCls == "tdProperties refObjectIds") {
				json +=  '"refObjectId"' + ":" + '"' + $.trim($(this).find(".hdnEbObjects").val()) + '"' + ",";
				json +=  '"itemId"' + ":" + '"' + $.trim($(this).find(".hdnItemId").val()) + '"' + ",";
			} else if (tdCls == "tdProperties stockCode") {
				json +=  '"stockCode"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
			} else if (tdCls == "tdProperties conditions") {
				json +=  '"toolCondition"' + ":" + '"' + $.trim($(this).find(".tblInputText").val()) + '"' + ",";
			}  else if (tdCls == "tdProperties statuses") {
				var isChecked = $(this).find(".cbStatuses").is(":checked");
				json +=  '"status"' + ":" + isChecked;
			}
		});
		json += "}";
		if (i < maxLength) {
			json += ",";
		}
	});
	json += "]";
	return json;
}

function saveFleetTools() {
	var fleetToolConditionsJson = buildJson();
	$("#fleetToolConditionsJson").val(buildJson());
}

var isSaving = false;
function saveFleetTools() {
	$("#hdnToolPageNumber").val($("#hdnPageNumber").val());
	$("#hdnToolRefObjectId").val(fpRefObjectId);
	$("#referenceDocumentsToolsJson").val($fTRefDocsTable.getData());
	var fleetJson = buildJson();
	$("#fleetToolConditionsJson").val(fleetJson);
	if(isSaving == false) {
		isSaving = true;
		doPostWithCallBack ("fleetToolForm", "divToolForm", function(data) {
			var parsedData = data.split(";");
			var isSuccessfullySaved = $.trim(parsedData[0]) == "saved";
			if(isSuccessfullySaved) {
				$("#divToolForm").html("");
				$("#successDialog").dialog({
					modal: true,
					buttons: {
						Close: function() {
							$(this).dialog("close");
							$(this).dialog("destroy");
							generateTools($("#hdnPageNumber").val());
							$("#fleetToolsErrMsg").text("");
						}
					}
				});
				isSaving = false;
			} else {0
				var toolHtml = $("#fleetToolsTbl").html();
				$("#dvFleetToolForm").html(data);
				$("#fleetToolsTbl").html(toolHtml);
				var fleetToolsJson = JSON.parse($("#fleetToolConditionsJson").val());
				$("#tblFleetToolCondition tbody tr").find(".cbStatuses").each(function (i) {
					$(this).prop("checked", fleetToolsJson[i].status);
				});
				isSaving = false;
			}
		});
		isSaving = false;
	}
}

function generateTools(pageNumber) {
	$("#fleetToolsTbl").load(contextPath + "/fleetTool/tools/" + getToolCommonParam() + "&pageNumber=" + pageNumber);
}

function getToolCommonParam() {
	var asOfDate = $("#asOfDate").val();
	return "?divisionId="+fpDivisionId+"&asOfDate="+asOfDate;
}
</script>
</head>
<body>
<br>
<table class="formTable">
	<tr>
		<td class="labels">As of Date</td>
		<td class="value">
			<input id="asOfDate" style="width: 120px;" class="dateClass2" onblur="evalDate('asOfDate')"/> 
			<img id="imgAsOfDate"
				src="${pageContext.request.contextPath}/images/cal.gif"
				onclick="javascript:NewCssCal('asOfDate')"
				style="cursor: pointer" style="float: right;" />
		</td>
		<td align="right">
			<input type="button" id="btnGenerateTools" value="Generate" onclick="generateTools(1);">
		</td>
	</tr>
</table>
<div>
	<%@ include file="FleetToolForm.jsp"%>
</div>
</body>
</html>