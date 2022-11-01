<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: timeSheet view table form.
	TODO : Enhance the UI. Add scroll to time sheet and timeSheet record.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/ebDataTable.css" media="all">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CMS/css/formPreview.css" media="all">
<style type="text/css">
.column1 {
  width: 30px;
  left: 0px;
}

.column2 {
  width: 100px;
  left: 30px;
}

.column3 {
  width: 190px;
  left: 130px;
}


.outer {
  position: relative;
  padding: 0px;
  width: 100%;
}

.inner {
  overflow-x: scroll;
  margin-left: 327px;
  width: calc(100% - 327px);
}

.column1, .column2, .column3 {
  position: absolute;
  background-color: #F0F0F0;
  height: 25px;
  float: bottom;
}
</style>
</head>
<body>
<fieldset class="frmField_set" style="min-width: 89%;">
<legend>Timesheet and Adjustments</legend>
<div>
	<span style="font-weight: bold">A - Absent</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span style="font-weight: bold">L - Leave</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</div>
<div class="outer">
	<div class="inner">
		<table class="dataTable" border="1" id="tblTSViewTable">
			<thead>
				<tr>
					<th width="2%" class="th-td-norm column1">#</th>
					<th width="5%" class="th-td-norm column2">No.</th>
					<th width="10%" class="th-td-norm column3">Name</th>
					<c:forEach items="${timeSheetHeader.timeSheet.tSDateTitles}" var="dt" varStatus="status">
					<th width="8%" class="th-td-norm" colspan="4">
						<fmt:formatDate pattern="MM/dd/yyyy" value="${dt}"/>
					</th>
				</c:forEach>
				<th class="th-td-norm" colspan="3">TOTAL</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${timeSheetHeader.timeSheet.timeSheetViewDtos}" var="ts" varStatus="status">
				<tr>
					<!-- Row number -->
					<td class="v-align-top column1">${status.index + 1}</td>
					<!-- Employee No -->
					<td class="th-td-norm v-align-top column2">
						<span>${ts.employeeNo}</span>
					</td>
					<!-- Employee Name -->
					<td class="th-td-norm v-align-top column3">${ts.employeeName}</td>
					<c:set var="totalRowAdjs" value="0"/>
					<c:set var="totalRowHrsWorked" value="0"/>
					<c:set var="totalRowOT" value="0"/>
					<c:set var="n" value="0" />
					<c:forEach items="${ts.hoursWorked}" var="hw" varStatus="hwStatus">
						<c:if test="${hwStatus.count mod 4 eq 1 and hwStatus.count gt 4}">
							<c:set var="n" value="${n + 1}" />
						</c:if>
						<c:set var="firstCol"  value="${((4 * n) + 1) eq hwStatus.count}"/>
						<c:set var="secondCol"  value="${((4 * n) + 2) eq hwStatus.count}"/>
						<c:set var="thirdCol"  value="${((4 * n) + 3) eq hwStatus.count}"/>
						<c:set var="fourthCol"  value="${((4 * n) + 4) eq hwStatus.count}"/>
						<c:choose>
							<c:when test="${firstCol}">
								<td class="td-numeric v-align-top">
									<fmt:formatNumber type="number" minFractionDigits="2" 
											maxFractionDigits="2" value="${hw}" />
									</td>
								<c:set var="totalRowHrsWorked" value="${totalRowHrsWorked + hw}" />
								<c:set var="totalColHrsWorked" value="${totalColHrsWorked + hw}" />
							</c:when>
							<c:when test="${secondCol}">
								<c:choose>
									<c:when test="${hw ne 0}">
										<td class="td-numeric v-align-top" style="color: green;">
											<fmt:formatNumber type="number" minFractionDigits="2"
													maxFractionDigits="2" value="${hw}" />
										</td>
										<c:set var="totalRowOT" value="${totalRowOT + hw}" />
										<c:set var="totalColOT" value="${totalColOT + hw}" />
									</c:when>
									<c:otherwise>
										<td width="27px" class="td-numeric v-align-top"></td>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${thirdCol}">
								<c:choose>
									<c:when test="${hw ne 0}">
										<td class="td-numeric v-align-top" style="color: red;">
											<fmt:formatNumber type="number" minFractionDigits="2"
													maxFractionDigits="2" value="${hw}" />
										</td>
										<c:set var="totalRowAdjs" value="${totalRowAdjs + hw}" />
										<c:set var="totalColAdjs" value="${totalColAdjs + hw}" />
									</c:when>
									<c:otherwise>
										<td width="27px" class="td-numeric v-align-top"></td>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:when test="${fourthCol}">
								<td  width="27px" style="text-align: center; font-weight: bold; 
									border-top: 1px solid #000000; border-right: 1px solid #000000;">
									<c:choose>
										<c:when test="${hw eq 1}">A</c:when>
										<c:when test="${hw eq 2}">L</c:when>
									</c:choose>
								</td>
							 </c:when>
						</c:choose>
						<c:set var="colspanLength" value="${hwStatus.count}"></c:set>
					</c:forEach>
					<td class="th-td-norm v-align-top" align="right">
						<fmt:formatNumber type="number" minFractionDigits="2"
							maxFractionDigits="2" value="${totalRowHrsWorked}" /></td>
					<td class="th-td-norm v-align-top" align="right" style="color: green;">
						<fmt:formatNumber type="number" minFractionDigits="2"
							maxFractionDigits="2" value="${totalRowOT}" /></td>
					<c:choose>
						<c:when test="${totalRowAdjs ne 0}">
							<td class="th-td-norm v-align-top" style="color: red;">
								<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${totalRowAdjs}" />
							</td>
						</c:when>
						<c:otherwise>
							<td width="27px" class="td-numeric v-align-top"></td>
						</c:otherwise>
					</c:choose>
				</tr>
				</c:forEach>
			</tbody>
			<tfoot>
			<tr>
				<td colspan="${colspanLength + 1}" style="font-weight:bold;">Total</td>
				<td style="font-weight:bold;" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${totalColHrsWorked}" /></td>
				<td style="font-weight:bold; color: green;" align="right">
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${totalColOT}" /></td>
				<c:choose>
					<c:when test="${totalColAdjs ne null}">
						<td style="font-weight:bold; color: red;">
							<fmt:formatNumber type="number" minFractionDigits="2"
							maxFractionDigits="2" value="${totalColAdjs}" /></td>
					</c:when>
					<c:otherwise>
						<td style="font-weight:bold; color: red;">0.00</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</table>
	</div>
</div>
</fieldset>
</body>
</html>