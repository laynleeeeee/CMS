<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Payroll view form.
	TODO : Enhance the UI. Add scroll to time sheet and payroll record.
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

.inner1 {
  overflow-x:scroll;
  width: 100%;
}
a#btnPrintPayslip{
	color: blue;
}

a#btnPrintPayslip:hover {
	color: blue;
	cursor: pointer;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	formObjectId = parseInt("${payrollDto.payroll.ebObjectId}");
	if ("${payrollDto.payroll.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});

function printPayslip () {
	window.open(contextPath + "/payroll/payslip/pdf?payrollId="+"${payrollDto.payroll.id}");
}

</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">Payroll</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">
							Sequence No.
						</td>
						<td class="label-value">${payrollDto.payroll.sequenceNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${payrollDto.payroll.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Payroll Header</legend>
				<table class="formTable" >
					<tr>
						<td class="label">Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${payrollDto.payroll.date}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Company/Branch</td>
						<td class="label-value">${payrollDto.payroll.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Division/Department</td>
						<td class="label-value">${payrollDto.payroll.division.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Month/Year</td>
						<td class="label-value">
						<c:choose>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 1}">JANUARY</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 2}">FEBRUARY</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 3}">MARCH</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 4}">APRIL</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 5}">MAY</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 6}">JUNE</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 7}">JULY</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 8}">AUGUST</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 9}">SEPTEMBER</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 10}">OCTOBER</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 11}">NOVEMBER</c:when>
							<c:when test="${payrollDto.payroll.payrollTimePeriod.month eq 12}">DECEMBER</c:when>
						</c:choose>
						${payrollDto.payroll.payrollTimePeriod.year}
						</td>
					</tr>
					<tr>
						<td class="label">Time Period</td>
						<td class="label-value">
							${payrollDto.payroll.payrollTimePeriodSchedule.name}
						</td>
					</tr>
					<tr>
						<td class="label">Compute Contribution</td>
						<td class="label-value">
							<c:choose>
								<c:when test="${payrollDto.payroll.payrollTimePeriodSchedule.computeContributions}">
									<input type="checkbox" checked="checked" onclick="return false;"/>
								</c:when>
								<c:otherwise>
									<input type="checkbox" onclick="return false;" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td></td>
						<td style="font-weight: bold;" class="label-value">
							<a id="btnPrintPayslip" onclick="printPayslip();">Print Payslip</a>
						</td>
					</tr>
				</table>
			</fieldset>
			<br>
			<div id="divTimeSheetViewTable">
				<%@ include file = "TimeSheetTableView.jsp" %>
			</div>
			<br>
			<fieldset class="frmField_set">
			<legend>Other Deduction</legend>
				<table class="dataTable" border="1" style="width: 100%">
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
										<table border=1 width="100%" >
											<thead>
												<tr>
													<th>Deduction Type</th>
													<th>Amount</th>
												</tr>
											</thead>
											<tbody>
												<c:set var="totalAmount" value="0" />
												<c:forEach var="ed" items="${edtos.employeeDeductions}">
													<tr>
														<td>
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
															<c:set var="totalAmount" value="${totalAmount + ed.amount}" />
														</td>
													</tr>
												</c:forEach>
												<tr>
													<td><b>Total</b></td>
													<td style="text-align: right;">
														<b><fmt:formatNumber type="number" minFractionDigits='2'
															maxFractionDigits='2' value='${totalAmount}' /></b>
													</td>
												</tr>
											</tbody>
										</table>
									</td>
								</tr>
						</c:forEach>
					</tbody>
				</table>
			</fieldset>
			<br>
			<fieldset class="frmField_set" >
			<legend>Payroll Record</legend>
				<div class = "inner1">
					<table class="dataTable" border=1>
						<thead>
							<tr>
								<th width="2%" class="th-td-norm" rowspan="2">#</th>
								<th width="4%" class="th-td-norm" rowspan="2">Employee<br>No</th>
								<th width="15%" class="th-td-norm" rowspan="2">Employee<br>Name</th>
								<th width="3%" class="th-td-norm" rowspan="2">Status</th>
								<th width="7%" class="th-td-norm" rowspan="2">Basic Pay</th>
								<th width="4%" class="th-td-norm" rowspan="2">Paid Leave</th>
								<th width="6%" class="th-td-norm" rowspan="2">COLA</th>
								<th width="6%" class="th-td-norm" rowspan="2">Bonus/<br>Allowance/<br>Incentive</th>
								<th width="6%" class="th-td-norm" rowspan="2">Holiday Pay</th>
								<th width="6%" class="th-td-norm" rowspan="2">Overtime Pay</th>
								<th width="7%" class="th-td-norm" rowspan="2">Gross Pay</th>
								<th width="6%" class="th-td-norm" rowspan="2">Loans<br>And<br>Other<br>Deductions</th>
								<th width="6%" class="th-td-norm" rowspan="2">Lates</th>
								<th width="6%" class="th-td-norm" rowspan="2">Withholding<br>Tax</th>
								<th class="th-td-norm" colspan="3">Contributions</th>
								<th width="7%" class="th-td-norm" rowspan="2">Net Pay</th>
							</tr>
							<tr>
								<th width="4%" class="th-td-norm">SSS</th>
								<th width="4%" class="th-td-norm">PHIC</th>
								<th width="4%" class="th-td-norm">HDMF</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${payrollDto.employeeSalaryDtos}" var="pes" varStatus="status">
								<tr>
									<!-- Row number -->
									<td class="td-numeric v-align-top">${status.index + 1}</td>
									<!-- Employee No -->
									<td class="th-td-norm v-align-top">${pes.employeeNo}</td>
									<!-- Employee Name -->
									<td class="th-td-norm v-align-top">${pes.employeeName}</td>
									<!-- Employee Status -->
									<td class="th-td-norm v-align-top">${pes.employeeStatus}</td>
									<!-- Basic Pay -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.basicPay}" />
									</td>
									<!-- PAID LEAVE -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.paidLeave}" />
									</td>
									<!-- COLA -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.cola}" />
									</td>
									<!-- Bonus -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.bonus}" />
									</td>
									<!-- Holiday Pay -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.sundayHolidayPay}" />
									</td>
									<!-- Overtime -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.overtime}" />
									</td>
									<!-- Gross Pay -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.grossPay}" />
									</td>
									<!-- Deduction -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.deduction}" />
									</td>
									<!-- Late / Absences -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.lateAbsent}" />
									</td>
									<!-- W Tax -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.withholdingTax}" />
									</td>
									<!-- SSS -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.sss}" />
									</td>
									<!-- PHIC -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.philHealth}" />
									</td>
									<!-- Pag-Ibig -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.pagibig}" />
									</td>
									<!-- Net Pay -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${pes.netPay}" />
									</td>
								</tr>
								<c:set var="totalBasicPay" value="${totalBasicPay + pes.basicPay}" />
								<c:set var="totalPaidLeave" value="${totalPaidLeave + pes.paidLeave}" />
								<c:set var="totalCola" value="${totalCola + pes.cola}"/>
								<c:set var="totalBonus" value="${totalOverTime + pes.bonus}" />
								<c:set var="totalRegHoliday" value="${totalRegHoliday + pes.sundayHolidayPay}" />
								<c:set var="totalOvertime" value="${totalOverTime + pes.overtime}" />
								<c:set var="totalGrossPay" value="${totalGrossPay + pes.grossPay}" />
								<c:set var="totalDeductions" value="${totalDeductions + pes.deduction}" />
								<c:set var="totalLateAbsent" value="${totalLateAbsent + pes.lateAbsent}" />
								<c:set var="totalWithhTax" value="${totalWithhTax + pes.withholdingTax}" />
								<c:set var="totalSss" value="${totalSss + pes.sss}" />
								<c:set var="totalPHIC" value="${totalPHIC + pes.philHealth}" />
								<c:set var="totalPagibig" value="${totalPagibig + pes.pagibig}" />
								<c:set var="totalNetpay" value="${totalNetpay + pes.netPay}" />
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4" style="font-weight:bold;">Total </td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalBasicPay}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalPaidLeave}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalCola}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalBonus}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalRegHoliday}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalOvertime}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalGrossPay}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalDeductions}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalLateAbsent}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalWithhTax}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalSss}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalPHIC}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalPagibig}" />
								</td>
								<td  class="monetary" style="font-weight:bold;">
									<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalNetpay}" />
								</td>
							</tr>
						</tfoot>
					</table>
				</div>
			</fieldset>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>