<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Payroll employee deduction table form
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.monetary {
	text-align: right;
}
</style>
<script type="text/javascript">
var deductionTypes = [];
function DeductionType(id, name) {
	this.id = id;
	this.name = name;
};

$(document).ready(function () {
	<c:forEach items="${deductionTypes}" var="dt" varStatus="status">
		var dt = new DeductionType("${dt.id}", "${dt.name}");
		deductionTypes.push(dt);
	</c:forEach>
});

function addDeductionType(employeeId) {
	var content = initDeductionTypes(employeeId);
	if ($(content).html() != "") {
		var $tblDeduction = $("#tblDeduction-" + employeeId);
		var $newRow = "<tr>";
		$newRow += "<td><img src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this, "+employeeId+");'/> </td>";
		$newRow += "<td>" + "<input type='hidden' class='hdnEmployeeIds"+employeeId+"' value='"+ employeeId +"'/>" +
					content + "<input type='hidden' class='hdnFromDeductionForms' value='false'/>"
					+ "</td>";
		$newRow += "<td><input class='txtAmounts' style='text-align:right; width: 100%;' onblur='formatMoney(this); checkDeduction(this, "+employeeId+");'/></td>";
		$newRow += "</tr>";
		$($tblDeduction).append($newRow);
	} else {
		alert("There is no available deduction type.");
	}
}

function checkDeduction($txtbox, employeeId) {
	var amount = $($txtbox).val();
	var deductionType = $($txtbox).closest("tr").find(".selDeductionTypes"+employeeId).val();
	var $spError = $("#spAmount" + employeeId);
	$($spError).text("");
	if(amount <= 0 && typeof deductionType != "undefined") {
		$("#btnComputePayroll").attr("disabled", "disabled");
		$($spError).text("Deduction amount must not be less than or equal to zero.");
		$($txtbox).focus();
	} else {
		$("#btnComputePayroll").removeAttr("disabled");
	}
}

function formatMoney($textbox) {
	$($textbox).val(accounting.formatMoney($($textbox).val()));
}

function deleteRow(deleteImg, employeeId) {
	var $toBeDeletedRow = $(deleteImg).closest("tr");
	$($toBeDeletedRow).empty();
	$("#btnComputePayroll").removeAttr("disabled");
	$("#spAmount" + employeeId).text("");
	computeTotalDeduction();
}

function initDeductionTypes(employeeId) {
	var existingIds = [];
	var allIds = [];
	deductionTypes.forEach(function(dt) {
		allIds.push(dt.id);
	});
	$(".hdnDeductionTypeIds"+employeeId).each(function(i) {
		existingIds.push($(this).val());
	});
	var uniqueIds = arr_diff(existingIds, allIds);

	var select = "<select style='width: 250px;' class='selDeductionTypes"+employeeId+"'>";
	uniqueIds.forEach(function(id) {
		deductionTypes.forEach(function(dt) {
			if (id === dt.id) {
				select += "<option value='"+ id +"'>"+ dt.name +" </option>";
			}
		});
	});
	select += "</select>";
	return select;
}

function arr_diff (a1, a2) {
	var a = [], diff = [];
	for (var i = 0; i < a1.length; i++) {
		a[a1[i]] = true;
	}
	for (var i = 0; i < a2.length; i++) {
		if (a[a2[i]]) {
			delete a[a2[i]];
		} else {
			a[a2[i]] = true;
		}
	}
	for (var k in a) {
		diff.push(k);
	}
	return diff;
};

function computeEmployeeDeduction(employeeId) {
	var totalDeduction = 0;
	var $table = $("#tblDeduction-" + employeeId);

	$($table).find("tbody tr").find(".spAmounts").each(function () {
		var value = accounting.unformat($(this).text());
		totalDeduction += value;
	});

	$($table).find("tbody tr").each(function () {
		var $txtAmount = $(this).find(".txtAmounts");
		var value = accounting.unformat($($txtAmount).val());
		if (value >= 0) {
			totalDeduction += value;
		}
	});
	return totalDeduction;
}

function computeTotalDeduction() {
	var totalDeductions = '';
	$(".tblDeductions").each(function (i) {
		var employeeId = $(this).attr("id").split("-")[1];
		var totalDeduction = 0;

		$(this).find("tbody tr").find(".spAmounts").each(function () {
			var value = accounting.unformat($(this).text());
			totalDeduction += value;
		});

		$(this).find("tbody tr").each(function () {
			var $txtAmount = $(this).find(".txtAmounts");
			var value = accounting.unformat($($txtAmount).val());
			if (value >= 0) {
				totalDeduction += value;
			}
		});
		totalDeductions += employeeId + ";" + totalDeduction + (i < $(".tblDeductions").length - 1 ?  '-' : '');
	});
	return totalDeductions;
}

function checkDuplicateDeduction() {
	var dIds = [];
	var notUnique = false;
	$(".tblDeductions").each(function (i) {
		var employeeId = $(this).attr("id").split("-")[1];
		var totalDeduction = 0;

		$(this).find("tbody tr").find(".selDeductionTypes" + employeeId).each(function () {
			dIds.push($(this).val());
		});

		if (hasDuplicate(dIds)) {
			notUnique = true;
			return false;
		}
		dIds = [];
	});
	return notUnique;
}

function hasDuplicate(array) {
	var valuesSoFar = Object.create(null);
	for (var i = 0; i < array.length; ++i) {
		var value = array[i];
		if (value in valuesSoFar) {
			return true;
		}
		valuesSoFar[value] = true;
	}
	return false;
}

function computePayroll() {
	var hasDuplicate = checkDuplicateDeduction();
	if (hasDuplicate) {
		alert("Duplicate deductions are not allowed.");
	} else {
		var totalDeductions = computeTotalDeduction();
		var timeSheetId = $("#hdnTimeSheetId").val();
		var payrollId = $("#hdnPayrollId").val();
		var payrollTimePeriodId = $("#hdnPayrollTimePeriodId").val();
		var payrollTimePeriodScheduleId = $("#hdnPayrollTimePeriodScheduleId").val();
		var companyId = $("#hdnCompanyId").val();
		var uri = contextPath + "/payroll/parseSalary?timeSheetId="+timeSheetId+"&payrollId="+payrollId
				+"&payrollTimePeriodId="+payrollTimePeriodId+"&payrollTimePeriodScheduleId="+payrollTimePeriodScheduleId
				+"&companyId="+companyId+"&totalDeductions="+totalDeductions;
		$("#divPayrollESalary").load(uri, 
				 function (responseText, textStatus, XMLHttpRequest) {
			if (textStatus == "error") {
				$(this).html(XMLHttpRequest.responseText);
			}
		});
	}
}
</script>
</head>
<body>
	<table class="dataTable" style="width: 100%">
		<thead>
			<tr>
				<th width="5%" class="th-td-norm">#</th>
				<th width="15%" class="th-td-norm">Employee No</th>
				<th width="50%" class="th-td-norm">Employee Name</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${employeeDeductionDtos}" var="edtos" varStatus="status">
				<tr>
					<td>${status.count}</td>
					<td>${edtos.employeeNo}</td>
					<td>${edtos.employeeName}</td>
				</tr>
				<tr><td colspan="3"></td></tr>
					<tr>
						<td colspan="2"></td>
						<td> 
							<table width="100%" id="tblDeduction-${edtos.employeeId}" class="tblDeductions">
								<thead>
									<tr>
										<th></th>
										<th>Deduction Type</th>
										<th>Amount</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="ed" items="${edtos.employeeDeductions}">
										<tr>
											<c:choose>
												<c:when test="${ed.fromDeductionForm}">
													<td></td>
													<td>
														<input type="hidden" class="hdnEmployeeIds${ed.employeeId}" value="${ed.employeeId}" />
														<input type="hidden" class="hdnFromDeductionForms" value="${ed.fromDeductionForm}" />
														<input type="hidden" class="hdnDeductionTypeIds${ed.employeeId}" value="${ed.deductionTypeId}" />
														<input type="hidden" class="hdnFdlEbObjectIds" value="${ed.fdlEbObjectId}" />
														<span class="spDeductionTypeNames">
															${ed.deductionTypeName}
														</span>
													</td>
													<td style="text-align: right;">
														<input type="hidden" class="hdnAmounts" value="<fmt:formatNumber type='number' 
																minFractionDigits='2' maxFractionDigits='2' value='${ed.amount}' />" />
														<span class="spAmounts">
															<fmt:formatNumber type="number" minFractionDigits="2"
																maxFractionDigits="2" value="${ed.amount}" />
														</span>
													</td>
												</c:when>
												<c:otherwise>
													<td>
														<img src='${pageContext.request.contextPath}/images/delete_active.png' onclick='deleteRow(this, ${edtos.employeeId});'/>
													</td>
													<td>
														<input type="hidden" class="hdnEmployeeIds${ed.employeeId}" value="${ed.employeeId}" />
														<input type="hidden" class="hdnFromDeductionForms" value="${ed.fromDeductionForm}" />
														<input type="hidden" class="hdnFdlEbObjectIds" value="${ed.fdlEbObjectId}" />
														<select style='width: 250px;' class="selDeductionTypes${ed.employeeId}">
															<c:forEach items="${edtos.deductionTypes}" var="dt" varStatus="status">
																<c:choose>
																	<c:when test="${ed.deductionTypeId == dt.id}">
																		<option value="${dt.id}" selected="selected">${dt.name}</option>
																	</c:when>
																	<c:otherwise>
																		<option value="${dt.id}">${dt.name}</option>
																	</c:otherwise>
																</c:choose>
															</c:forEach>
														</select>
													</td>
													<td>
														<input class='txtAmounts' style='text-align:right; width: 100%;'
															onblur='formatMoney(this); onblur=checkDeduction(this, ${edtos.employeeId});'
															value='<fmt:formatNumber type="number" minFractionDigits="2" 
																maxFractionDigits="2" value="${ed.amount}" />' />
													</td>
												</c:otherwise>
											</c:choose>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td style="float: right;">
							<span id="spAmount${edtos.employeeId}" class="error"></span>
						</td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<td style="float: right;">
							<input type="button" id="btnAdd${edtos.employeeId}"
							 value="ADD" onclick="addDeductionType('${edtos.employeeId}');"/>
						</td>
					</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td colspan="2"><form:errors path="employeeDeductions"
						cssClass="error" style="margin-left: 12px;" /></td>
			</tr>
		</tfoot>
	</table>
	<div>
	<table class="frmField_set">
		<tr>
			<td align="right"><input type="button" id="btnComputePayroll"
				onclick="computePayroll();" value="Compute Payroll" /></td>
		</tr>
	</table>
</div>
</body>
</html>