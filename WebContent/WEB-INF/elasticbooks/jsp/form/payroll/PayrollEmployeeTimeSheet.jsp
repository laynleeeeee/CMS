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
<script type="text/javascript">
var employeeDtrsJson = JSON.stringify(${employeeDtrsJson});
$(document).ready(function() {
	if (typeof employeeDtrsJson !== "undefined") {
		$("#hdnEmployeeDtrsJson").val(employeeDtrsJson);
	}
	$("#fileName").val("${fileName}");
	$("#hdnFile").val("${file}");
	$("#hdnFileSize").val("${fileSize}");
	$("#description").val("${description}");
	$("#lblFile").text("${fileName}");
	$("#fileData").val("${fileData}");
});


function buidlJsonPayroll() {
	var json = "[";
	var maxTs = $("#tblETimeSheet tbody tr").length - 1;
	$("#tblETimeSheet tbody tr").each(function (i) {
		json += "{";
		var maxTsd = Number($(this).find("td").length) - 4;
		$(this).find("td").each(function (j) {
			var tdCls = $(this).attr("class");
			if (tdCls == "tdEmployeeNo column2 border") {
				json +=  '"employeeNo"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
			} else if (tdCls == "tdEmployeeName column3 border") {
				json +=  '"employeeName"' + ":" + '"' + $.trim($(this).text()) + '"' + ",";
				json += '"timeSheetDetailsDtos"' + ":[";
			} else if (tdCls == "tdHoursWork border") {
				var date = $(this).find(".hdnDate").val();
				json += "{";
				json += '"date"' + ":" + '"' + date + '"' + ",";
				var hoursWork =  $(this).find(".hoursWork").val();
				json += '"hoursWork"' + ":" + accounting.unformat(hoursWork) + ",";
				var adjustment =  $(this).find(".adjustment").val();
				json += '"adjustment"' + ":" + accounting.unformat(adjustment) + ",";
				var late =  $(this).find(".late").val();
				json += '"late"' + ":" + accounting.unformat(late) + ",";
				var undertime =  $(this).find(".undertime").val();
				json += '"undertime"' + ":" + accounting.unformat(undertime) + ",";
				var overtime =  $(this).find(".overtime").val();
				json += '"overtime"' + ":" + accounting.unformat(overtime) + ",";
				var status =  $(this).find(".status").val();
				json += '"status"' + ":" + accounting.unformat(status);
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

function computePayroll() {
	var payrollJson = buidlJsonPayroll ();
	$("#timeSheetDtoJson").val(payrollJson);
	var salaryJson = buildJsonSalary();
	$("#employeeSalaryDTOJson").val(salaryJson);
	payrollJson = $("#hdnFile").val() + "Base64" + payrollJson; 
	var uri = contextPath + "/payroll/parseSalary?date="+$("#date").val()+"&dateTo="+dateTo
		+"&dateFrom="+dateFrom+"&companyName="+$("#companyId option:selected").text()
		+"&employeeTypeId="+$("#employeeTypeId").val()+"&fileName="+$("#fileName").val()+"&fileSize="+$("#hdnFileSize").val()
		+"&biometricModelId="+$("#biometricModelId").val()
		+"&payrollTimePeriodId="+$("#payrollTimePeriodId").val()
		+"&payrollTimePeriodScheduleId="+$("#payrollTimePeriodScheduleId").val()
		+"&month="+$("#slctMonth").val()+"&year="+$("#slctYear").val()
		+"&selectTPSched="+selectTPSched
		+"&divisionId="+$("#divisionId").val();
	$.ajax({
		url: uri,
		type: "POST",
		data: payrollJson,
		dataType: "html",
		processData: false,
		contentType: "application/json;charset=utf-8",
		success: function(data) {
			$("#form").html(data);
			document.getElementById("payrollForm").action = "${pageContext.request.contextPath}/a/payroll/form";
			if ("${fileName}" != "") {
				$("#fileName").val("${fileName}");
				$("#hdnFile").val("${file}");
				$("#hdnFileSize").val("${fileSize}");
				$("#description").val("${description}");
				$("#lblFile").text("${fileName}");
			}
		},
		error: function(jqXHR, textStatus, errorMessage) {
			console.log(errorMessage); // Optional
		}
	});
}

function updateTotalAdjustment(rowIndex, elem) {
	var $row = $(elem).parent("td").parent("tr").closest("tr");
	var totalAdjustment = 0;
	$($row).find(".adjustment").each(function () {
		var adjustment = accounting.unformat($(this).val());
		totalAdjustment += adjustment;
	});
	$("#tdTotalAHours"+rowIndex).text(accounting.formatMoney(totalAdjustment));
}

$(window).resize(function() {
	resize();
});

function resize(){
	var screenWidth = screen.width;
	var origWidth = 0;
	var pxDeductTS = 330;

	console.log("Expected Width: " + $("#sysGenId").width());
	console.log("  Window Width: " + $(window).width());
	console.log("  Screen Width: " + origWidth);

	if (screenWidth >= 1920) { // 1920 Screen Resolution
		origWidth = 1255;
	} else if (screenWidth >= 1680){ // 1680 Screen Resolution
		origWidth = 1095;
	} else if (screenWidth >= 1440){ // 1440 Screen Resolution
		origWidth = 940;
	} else if (screenWidth >= 1280) { // 1280 Screen Resolution
		origWidth = 830;
	} else if (screenWidth >= 1024) { // 1024 Screen Resolution
		origWidth = 663;
	}
	console.log("origWidth:" + origWidth);
	$(".resize").css("width", origWidth);
	$(".resizeTS").css("width", origWidth-Number(pxDeductTS));

}

$(document).ajaxStop(function(){
	resize();
});
</script>
<style type="text/css">
.column1 {
  width: 30px;
  left:0px;
}

.column2 {
  width: 100px;
  left: 30px;
}

.column3 {
  width: 200px;
  left: 130px;
}

.column4 {
  width: 50px;
  left: 275px;
}

.outer {
  position:relative;
  padding: 0px;
}

.inner {
  overflow-x:scroll;
  width: 950px;
  margin-left:336px;
}

.column1, .column2, .column3, .column4 {
  position: absolute;
  background-color: #F0F0F0;
  height: 25px;
  float: bottom;
}

.border {
  border-top: 1px solid black;
}
</style>
<div>
	<span style="font-weight: bold">A - Absent</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span style="font-weight: bold">L - Leave</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span style="font-weight: bold">H - Holiday</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span style="font-weight: bold">D - Day Off</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span style="font-weight: bold">Green - Overtime</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span style="font-weight: bold">Red - Adjustment</span>
</div>
<div id="dvEmployeeTimeSheet" class="outer">
	<div class="inner resizeTS">
		<table class="dataTable" id="tblETimeSheet">
			<thead>
				<tr>
					<th class="alignLeft column1 border" style="height: 28px; min-height: 28px; font-size: 14px;">#</th>
					<th class="alignLeft column2 border" style="height: 28px; min-height: 28px; font-size: 14px;">Employee No</th>
					<th class="alignLeft column3 border" style="height: 28px; min-height: 28px; font-size: 14px;">Employee Name</th>
					<c:forEach var="date" items="${dates}" varStatus="status">
						<th class="alignLeft border" style="height: 28px; min-height: 28px; margin-bottom: 8px; min-width: 150px; font-size: 14px;">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${date}"/>
						</th>
					</c:forEach>
					<th class="border" style="height: 28px; min-height: 28px; font-size: 14px; width: 30px; text-align: center;" colspan="3">
						Total
					</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="ts" items="${timeSheetDtos}" varStatus="status">
					<tr onMouseOver="this.className='highlight'"
						style="background-color: #F0F0F0;">
						<td class="tdIndex column1 border">
							<c:set var="rowIndex" value="${status.index+1}" />
							${rowIndex}
						</td>
						<td class="tdEmployeeNo column2 border">
							${ts.employeeNo}
						</td>
						<td class="tdEmployeeName column3 border">
							${ts.employeeName}
						</td>
						<c:set var="totalTSHours" value="0"/>
						<c:set var="totalTSOverTime" value="0"/>
						<c:set var="totalAHours" value="0"/>
						<c:forEach var="timeSheetDetail" items="${ts.timeSheetDetailsDtos}" varStatus="status">
							<td class="tdHoursWork border" style="white-space: nowrap; height: 25px;">
								<input type='hidden' class='hdnDate'
									value='<fmt:formatDate pattern="MM/dd/yyyy" value="${timeSheetDetail.date}"/>'  /> 
								<input type='hidden' class='hoursWork' style='text-align: right;' onblur="formatMoney(this);"
									name='ts.timeSheetDetailsDtos[${status.index}].hoursWork' readonly="readonly"
									value="<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${timeSheetDetail.hoursWork}'/>" />
								<fmt:formatNumber type="number" minFractionDigits='2'
								maxFractionDigits='2' value = '${timeSheetDetail.hoursWork}'/>
								<span style='text-align: right; color: green;'>
									<fmt:formatNumber type="number" minFractionDigits='2'
										maxFractionDigits='2' value = '${timeSheetDetail.overtime}'/>
								</span>
								<input class='adjustment' style='width: 40%; text-align: right; color: red;'
									name='ts.timeSheetDetailsDtos[${status.index}].adjustment'
									onblur="updateTotalAdjustment('${rowIndex}', this);"
									value="<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${timeSheetDetail.adjustment}'/>"/>
								<input type='hidden' class='late' 
									name='ts.timeSheetDetailsDtos[${status.index}].late' 
									value="<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${timeSheetDetail.late}'/>"/>
								<input type='hidden' class='undertime' 
									name='ts.timeSheetDetailsDtos[${status.index}].undertime' 
									value="<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${timeSheetDetail.undertime}'/>"/>
								<input type='hidden' class='overtime'  class='hdnOvertime' name='ts.timeSheetDetailsDtos[${status.index}].overtime'  
									value="<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${timeSheetDetail.overtime}'/>"/> 
								<input type='hidden' class='status'  class='hdnStatus' name='ts.timeSheetDetailsDtos[${status.index}].status'  
									value="${timeSheetDetail.status}" /> 
								<c:set var="totalTSHours" value="${totalTSHours + timeSheetDetail.hoursWork}"/>
								<c:set var="totalTSOverTime" value="${totalTSOverTime + timeSheetDetail.overtime}"/>
								<c:set var="totalAHours" value="${totalAHours + timeSheetDetail.adjustment}"/>
								<c:choose>
									<c:when test="${timeSheetDetail.status eq 1}">A</c:when>
									<c:when test="${timeSheetDetail.status eq 2}">L</c:when>
									<c:when test="${timeSheetDetail.status eq 3}">H</c:when>
									<c:when test="${timeSheetDetail.status eq 4}">D</c:when>
								</c:choose>
							</td>
						</c:forEach>
						<td id="tdTotalTSHours${rowIndex}" class="border">
							<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${totalTSHours}'/>
						</td>
						<td id="totalTSOverTime{rowIndex}" class="border">
							<span style='text-align: right; color: green;'>
								<fmt:formatNumber type="number" minFractionDigits='2'
										maxFractionDigits='2' value = '${totalTSOverTime}'/>
							</span>
						</td>
						<td id="tdTotalAHours${rowIndex}" class="border" style='text-align: right; color: red;'>
							<fmt:formatNumber type="number" minFractionDigits='2'
									maxFractionDigits='2' value = '${totalAHours}'/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
<table>
	<tr>
		<td></td>
		<td><form:errors path="timeSheet.employeeTimeSheets" cssClass="error"/></td>
	</tr>
</table>