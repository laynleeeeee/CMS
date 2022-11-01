<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../../../../jsp/include.jsp"%>
<!--

	Description: Payroll Employee Time Sheet.
     -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<style>
#tblEmployee tr {
	min-height: 28px;
	max-height: 28px;
	height: 28px;
}

#tblDates tr {
	min-height: 28px;
	max-height: 28px;
	height: 28px;
}

.fixedHorizontalScroll {
	position: sticky;
	background-color: #F0F0F0;
	float: left;
}

.fixedVerticalScroll {
	position: sticky;
	top: 0;
}

.borderRight {
	border-right: 1px solid black;
}

.borderTopBottom {
	border-bottom: 1px solid black;
	border-top: 1px solid black;
}

</style>
<script type="text/javascript">
function buidlJsonTimeSchedule() {
	var json = "[";
	var maxTs = $("#tblDates tbody tr").length - 1;
	$("#tblDates tbody tr").each(function (i) {
		json += "{";
		var maxTsd = Number($(this).find("td").length) - 1;
		$(this).find("td").each(function (j) {
			var tdCls = $(this).attr("class");
			if (tdCls == "tdEmployeeId") {
				json +=  '"employeeId"' + ":" +  $(this).find(".hdnEmployeeId").val() + ",";
			} else if (tdCls == "tdEmployeeName") {
				json +=  '"employeeName"' + ":" + '"' + $.trim($(this).find(".hdnEmployeeName").val()) + '"' + ",";
				json += '"dssLineDtos"' + ":[";
			} else if (tdCls == "timeDetails borderTopBottom") {
				json += "{";
				var date = $(this).find(".hdnDate").val();
				json += '"date"' + ":" + '"' + date + '"' + ",";
				var origEmployeeShiftId = $.trim($(this).find(".hdnOrigEmployeeShiftId").val());
				if (origEmployeeShiftId != "") {
					json += '"origEmployeeShiftId"' + ":" + '"' + origEmployeeShiftId + '"' + ",";
				}
				var hndShiftName =  $(this).find(".hndShiftName").val();
				json += '"shiftName"' + ":"+'"' + hndShiftName + '",';
				var hdnShiftId =  $(this).find(".hdnShiftId").val();
				json += '"employeeShiftId"' + ":" + hdnShiftId + ',';
				var hdnIsActive =  $(this).find(".hdnIsActive").val();
				json += '"active"' + ":" + hdnIsActive;
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
	console.log(json);
	return json;
}

$(".scroll").scroll(function() {
	$("#tblEmployee th").addClass("fixedVerticalScroll");
	$("#tblDates th").addClass("fixedVerticalScroll");
	$("#tblEmployee").addClass("fixedHorizontalScroll");
	$('#tblETimeSheet tbody td:nth-child(1)').css("left", $("tbody").scrollLeft());
});

</script>
<fieldset class="frmField_set" >
	<legend>Employee Sheets</legend>
	<div id="dvEmployeeTimeSheet" class="scroll" style="overflow-y: auto; overflow-x: auto; 
		max-width: 800px; max-height: 605px; position: relative;">
		<span class="error" id="errorMessage">${errorMessage}</span>
		<table class="dataTable" id="tblETimeSheet">
			<tr>
				<td style="padding: 0; display: block; position: sticky; z-index: 2; ">
					<table id="tblEmployee" width="100%">
						<thead>
							<tr>
								<th style="font-size: 14px;">#</th>
								<th></th>
								<th class="borderRight" style="font-size: 14px;">Employee Name</th>
							</tr>
						</thead>
						<tbody class="scrollContent">
							<c:if test="${dailyShiftSchedule.id ge 0}">
								<c:set var="scheduleSheetDtos" value="${dailyShiftSchedule.scheduleSheetDtos}" />
							</c:if>
							<c:forEach var="ts" items="${scheduleSheetDtos}" varStatus="status">
								<tr class="borderTopBottom" style="background-color: #F0F0F0">
									<td class="tdIndex" align="right">
										<c:set var="rowIndex" value="${status.index+1}" />
										${rowIndex}
									</td>
									<td class="tdEmployeeId">
										<input type='hidden' class='hdnEmployeeId' value="${ts.employee.id}"/>
									</td>
									<td class="tdEmployeeName borderRight" style="white-space: nowrap;">
										${ts.employee.fullName}
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
				<td style="padding: 0; left: 0;" valign="top">
					<table id="tblDates">
						<thead>
							<tr class="height">
								<c:forEach var="date" items="${dates}" varStatus="status">
									<th class="headerDate" style="font-size: 14px;" >
										<fmt:formatDate pattern="MM/dd/yyyy" value="${date}"/>
									</th>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
							<c:if test="${dailyShiftSchedule.id ge 0}">
								<c:set var="scheduleSheetDtos" value="${dailyShiftSchedule.scheduleSheetDtos}" />
							</c:if>
							<c:forEach var="ts" items="${scheduleSheetDtos}" varStatus="status">
								<tr class="borderTopBottom" style="background-color: #F0F0F0">
									<td class="tdEmployeeId" style="display: none;">
										<input type='hidden' class='hdnEmployeeId' value="${ts.employee.id}"/>
									</td>
									<td class="tdEmployeeName" style="display: none;">
										<input type='hidden' class='hdnEmployeeName' value="${ts.employee.fullName}"/>
									</td>
									<c:forEach var="timeSheetDetail" items="${ts.dssLineDtos}" varStatus="status">
										<td class="timeDetails borderTopBottom" style="white-space: nowrap;">
											<input type='hidden' class='hdnDate'
												value='<fmt:formatDate pattern="MM/dd/yyyy" value="${timeSheetDetail.date}"/>'/>
											<input type='hidden' class='hdnOrigEmployeeShiftId' value="${timeSheetDetail.origEmployeeShiftId}"/>
											<input type='hidden' style='text-align: right;' onblur="formatMoney(this);" class="hndShiftName"
												name='ts.timeSheetDetailsDtos[${status.index}].shiftName' readonly="readonly"
												value = '${timeSheetDetail.shiftName}'/>
											<input type='hidden' value="${timeSheetDetail.active}" class="hdnIsActive" />
											<select class="hdnShiftId" style="width: 100%; padding: 1px;" id="slctEs{es.id}">
												<c:if test="${timeSheetDetail.employeeShiftId ne null}">
													<c:if test="${timeSheetDetail.active ne true}">
														<option value="${timeSheetDetail.employeeShiftId}" selected="selected">
															${timeSheetDetail.shiftName}
														</option>
													</c:if>
												</c:if>
												<c:forEach var="es" items="${employeeShifts}">
													<option value="${es.id}" ${es.id == timeSheetDetail.employeeShiftId ? 'selected' : ''}>${es.name}</option>
												</c:forEach>
											</select>
										</td>
									</c:forEach>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<table class="formTable">
		<tr>
			<td>
				<form:errors path="scheduleSheetDtos" cssClass="error"/>
			</td>
		</tr>
	</table>
</fieldset>