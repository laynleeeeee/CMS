<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp"%>

           Description: Employee Sheet -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript">
function buidlJsonEmployeeSchedule () {
	var json = "[";
	var maxTs = $("#tblETimeSheet tbody tr").length - 1;
	$("#tblETimeSheet tbody tr").each(function (i) {
		json += "{";
		var maxTsd = Number($(this).find("td").length) - 1;
		$(this).find("td").each(function (j) {
			var tdCls = $(this).attr("class");
			if (tdCls == "tdEmployeeId") {
				json +=  '"employeeId"' + ":" +  $(this).find(".hdnEmployeeId").val() + ",";
			} else if (tdCls == "tdEmployeeName") {
				json +=  '"employeeName"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
				json += '"dssLineDtos"' + ":[";
			} else if (tdCls == "timeDetails") {
				var date = $(this).find(".hdnDate").val();
				json += "{";
				var hndShiftName =  $(this).find(".hndShiftName").val();
				json += '"shiftName"' + ":"+'"' + hndShiftName + '",';
				var hdnShiftId =  $(this).find(".hdnShiftId").val();
				json += '"employeeShiftId"' + ":" + hdnShiftId;
				json += "}";
				if (j < maxTsd) {
					json += ",";
				} else {
					json += "]";
				}
			}
		});

		json += "}";
		if (i < maxTs) {
			json += ",";
		}
	});
	json += "]";
	return json;
}

function buildHeader () {
	var header = "";
	$("#tblETimeSheet thead th").each(function () {
		header += $.trim($(this).text()) + ",";
	});
	return header;
}

function buildBody () {
	var body = "";
	$("#tblETimeSheet tbody tr").each(function () {
	});
	return body;
}
</script>
<fieldset class="frmField_set" id="empShiftId">
<legend>Employee Sheets</legend>
<div id="dvEmployeeTimeSheet" class="inner">
	<table class="dataTable" border=1 id="tblETimeSheet">
		<thead>
			<tr class="alignLeft">
				<th style="height: 30px; font-size: 14px; width: 5%">#</th>
				<th style="height: 30px; font-size: 14px; width: 40%">Employee Name</th>
				<th style="height: 30px; font-size: 14px; width: 40%" colspan="2">Employee Shift</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${monthlyShiftSchedule.id ge 0}">
				<c:set var="monthlyShiftScheduleDtos" value="${monthlyShiftSchedule.monthlyShiftScheduleDtos}" />
			</c:if>
			<c:forEach var="ts" items="${monthlyShiftScheduleDtos}" varStatus="status">
				<tr onMouseOver="this.className='highlight'"
					style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
					<td class="tdIndex">
						<c:set var="rowIndex" value="${status.index+1}" />
						${rowIndex}
					</td>
					<td class="tdEmployeeId" style="display: none;">
						<input type='hidden' class='hdnEmployeeId' value="${ts.employee.id}"/>
					</td>
					<td class="tdEmployeeName">
						${ts.employee.fullName}
					</td>
					<td class="timeDetails" style="white-space: nowrap;">
						<select class="hdnShiftId" style="width: 100%; padding: 1px;" id="slctEs{es.id}">
							<c:forEach var="es" items="${ts.employeeShifts}">
								<option value="${es.id}" ${es.id == timeSheetDetail.employeeShiftId ? 'selected' : ''}>${es.name}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</fieldset>