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
<script>
function computeGrossAndNetPay(rowIndex) {
	var $row = $("tr", $("#tblESalary tbody")).eq(rowIndex);

	var grossPay = 0;
	var totalDeduction = 0;
	var netPay = 0;

	var basicPay = accounting.unformat($($row).find(".tdBasicPay").text());
	var paidLeave = accounting.unformat($($row).find(".tdPaidLeave").text());
	var overtime = accounting.unformat($($row).find(".tdOvertime").text());
	var sundayHoliday =  accounting.unformat($($row).find(".tdSundayHoliday").text());
	var cola = accounting.unformat($($row).find(".tdCola").text());
	var bonus = accounting.unformat($($row).find(".tdBonus").find("input").val());

	var taxableIncome = basicPay + sundayHoliday + overtime + paidLeave + cola + bonus;
	grossPay = taxableIncome;
	$($row).find(".tdGrossPay").text(accounting.formatMoney(grossPay));

	var lateAbsent = accounting.unformat($($row).find(".tdLateAbsent").text());
	var deduction = accounting.unformat($($row).find(".tdDeduction").find("input").val());
	var sss = accounting.unformat($($row).find(".tdSss").text());
	var philHealth = accounting.unformat($($row).find(".tdPhilHealth").text());
	var pagIbig = accounting.unformat($($row).find(".tdPagIbig").text());
	var sssLoan =  accounting.unformat($($row).find(".tdSssLoan").find("input").val());
	var pagIbigLoan = accounting.unformat($($row).find(".tdPagibigLoan").find("input").val());
	var wTax = accounting.unformat($($row).find(".tdWTax").find("input").val());
	totalDeduction = lateAbsent + deduction + sss + philHealth + pagIbig + sssLoan + pagIbigLoan + wTax;
	netPay = grossPay - totalDeduction;
	$($row).find(".tdNetpay").text(accounting.formatMoney(netPay));
}

</script>
<div id="dvEmployeeSalary" class="innerTbl">
	<table id="tblESalary" class="dataTable" border=1>
		<thead>
			<tr class="alignLeft">
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">#</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">Employee<br>No</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">Employee<br>Name</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">Status</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Basic Pay
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Paid Leave
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					COLA
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Bonus/<br>Allowance/<br>Incentive
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Holiday<br>Pay
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Overtime<br>Pay
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Gross Pay
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Loans<br>And<br>Other<br>Deductions
				</th>
				<th style="height: 40px; font-size: 14px; display: none;" rowspan="2">
					Previous<br>Balance
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Lates
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Withholding<br>Tax
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" colspan="3">
					Contributions
				</th>
				<th style="height: 40px; font-size: 14px;  display: none;" colspan="2">
					Loans
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;" rowspan="2">
					Net Pay
				</th>
			</tr>
			<tr>
				<th style="height: 40px; font-size: 14px; text-align: center;">
					SSS
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;">
					PHIC
				</th>
				<th style="height: 40px; font-size: 14px; text-align: center;">
					HDMF
				</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="pes" items="${salaryDtos}" varStatus="status">
				<c:set var="rowIndex" value="${status.index}" />
				<tr onMouseOver="this.className='highlight'"
					style="border-top: 1px solid black; border-bottom: 1px solid black; background-color: #F0F0F0">
					<td class="tdStatus">${status.index+1}</td>
					<td class="tdEmployeeNo">${pes.employeeNo}</td>
					<td class="tdEmployeeName">${pes.employeeName}</td>
					<td class="tdEmployeeStatus">${pes.employeeStatus}</td>
					<td class="tdBasicPay" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value='${pes.basicPay}' />
					</td>
					<td class="tdPaidLeave" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.paidLeave}'/>
					</td>
					<td class="tdCola" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value='${pes.cola}' />
					</td>
					<td class="tdBonus" style="text-align: right;">
						<input id="txtBonus" style='width: 100%; text-align: right;' onblur="formatMoney(this); computeGrossAndNetPay(${rowIndex});"
							value="<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.bonus}'/>" />
					</td>
					<td class="tdSundayHoliday" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value='${pes.sundayHolidayPay}' />
					</td>
					<td class="tdOvertime" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value='${pes.overtime}' />
					</td>
					<td class="tdGrossPay" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value='${pes.grossPay}' />
					</td>
					<td class="tdDeduction" style="text-align: right;">
						<input type="hidden"
						value="<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.deduction}'/>" />
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.deduction}'/>
					</td>
					<td class="tdPrevBalance" style="text-align: right; display: none;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.prevBalance}'/>
					</td>
					<td class="tdLateAbsent" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.lateAbsent}'/>
					</td>
					<td class="tdWTax" style="text-align: right;">
						<input id="txtWTax" style='width: 100%; text-align: right;' onblur="formatMoney(this); computeGrossAndNetPay(${rowIndex});" 
							value="<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.withholdingTax}'/>" />
					</td>
					<td class="tdSss" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.sss}'/>
						<input type="hidden" id="hdnSssEr" value = '${pes.sssEr}'/>
						<input type="hidden" id="hdnSssEc" value = '${pes.sssEc}'/>
					</td>
					<td class="tdPhilHealth" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.philHealth}'/>
						<input type="hidden" id="hdnPhicEr" value = '${pes.philHealthEr}'/>
					</td>
					<td class="tdPagIbig" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value = '${pes.pagibig}'/>
						<input type="hidden" id="hdnPagibigEr" value = '${pes.pagibigEr}'/>
					</td>
					<td class="tdNetpay" style="text-align: right;">
						<fmt:formatNumber type="number" minFractionDigits='2' maxFractionDigits='2' value='${pes.netPay}' />
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
<table>
	<tr>
		<td></td>
		<td class="value"><form:errors path="payroll.payrollEmployeeSalaries" cssClass="error"/></td>
	</tr>
</table>