<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../../../../jsp/include.jsp" %>
<!--

	Description: Production report view form.
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

.rawMatsTbl {
	width: 80%;
	align-content: center;
	margin: 10px 10px 20px 10px;
	font-size: 13px;
}

.rawMatHdr {
	font-weight: bold;
	font-size: 13px;
	border-top: 1px solid #B8B8B8;
}

.rawMatItem {
	font-size: 13px;
	border-top: 1px solid #B8B8B8;
}

.mainProd {
	font-size: 13px;
	border-top: 1px solid #000000;
}

.mainRecipeTbl {
	width: 100%;
	font-size: 13px;
}

.mainRecipeTbl thead, .mainRecipeTbl th {
	background: #F0F0F0;
	color: #000000;
	padding: 2px;
	border-top: none;
	border-bottom: none;
	border-left: none;
	border-right: none;
}

.mainRecipeTbl td {
	padding: 3px;
}

.algnRight {
	text-align: right;
}
</style>
<script type="text/javascript">
$(document).ready(function () {
	// This variable is being used by MainFormWorkflow.jsp.
	formObjectId = parseInt("${processingReport.ebObjectId}");

	if ("${processingReport.formWorkflow.currentStatusId}" == 4) {
		$("#btnPrint").hide();
	}
});
</script>
</head>
<body>
	<div class="formDivBigForms">
		<div class="modFormLabel">
			${processingType}
		</div>
		<br>
		<div class="modForm">
			<fieldset class="frmField_set">
				<legend>System Generated</legend>
				<table>
					<tr>
						<td class="label">
							Sequence No.
						</td>
						<td class="label-value">${processingReport.formattedPRNumber}</td>
					</tr>
					<tr>
						<td class="label">Status </td>
						<td class="label-value">${processingReport.formWorkflow.currentFormStatus.description}</td>
					</tr>
				</table>
			</fieldset>
			<fieldset class="frmField_set">
				<legend>Header</legend>
				<table class="formTable">
					<tr>
						<td class="label">Company</td>
						<td class="label-value">${processingReport.company.numberAndName}</td>
					</tr>
					<tr>
						<td class="label">Date</td>
						<td class="label-value">
							<fmt:formatDate pattern="MM/dd/yyyy" value="${processingReport.date}"/>
						</td>
					</tr>
					<tr>
						<td class="label">Ref No.</td>
						<td class="label-value">${processingReport.refNumber}</td>
					</tr>
					<tr>
						<td class="label">Remarks</td>
						<td class="label-value">${processingReport.remarks}</td>
					</tr>
				</table>
			</fieldset>
			<c:if test="${processingReport.processingReportTypeId ne 6}">
				<fieldset class="frmField_set">
					<legend>Main Recipe</legend>
					<table class="dataTable">
						<thead>
							<tr>
								<th width="5%" class="th-td-norm">#</th>
								<th width="10%" class="th-td-norm">Stock Code</th>
								<th width="15%" class="th-td-norm">Description</th>
								<th width="10%" class="th-td-norm">Warehouse</th>
								<th width="10%" class="th-td-norm">Existing<br>Stocks</th>
								<th width="8%" class="th-td-norm">Qty</th>
								<th width="8%" class="th-td-norm">UOM</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${processingReport.prRawMaterialsItems}" var="rmItem" varStatus="status">
								<tr>
									<!-- Row number -->
									<td class="td-numeric v-align-top">${status.index + 1}</td>
									<!-- Stock code -->
									<td class="th-td-norm v-align-top">${rmItem.item.stockCode}</td>
									<!-- Description -->
									<td class="th-td-norm v-align-top">${rmItem.item.description}</td>
									<!-- Warehouse list -->
									<td class="th-td-norm v-align-top">${rmItem.warehouse.name}</td>
									<!-- Existing stocks -->
									<td class="td-numeric v-align-top"><fmt:formatNumber type='number' minFractionDigits='2'
											maxFractionDigits='2' value="${rmItem.availableStocks}"/>
									</td>
									<!-- Quantity -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${rmItem.quantity}" />
									</td>
									<!-- UOM -->
									<td class="th-td-norm v-align-top">${rmItem.item.unitMeasurement.name}</td>
								</tr>
								<c:set var="totalRMIQuantity" value="${totalRMIQuantity + rmItem.quantity}" />
							</c:forEach>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4" style="font-weight:bold;">Total</td>
								<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${totalRMIQuantity}" /></td>
							</tr>
						</tfoot>
					</table>
				</fieldset>
			</c:if>

			<fieldset class="frmField_set">
			<legend>Main Recipe Table</legend>
			<table class="${processingReport.processingReportTypeId ne 6 ? 'dataTable' : 'mainRecipeTbl'}">
				<thead>
					<tr>
						<th width="5%" class="th-td-norm">#</th>
						<th width="10%" class="th-td-norm">Stock Code</th>
						<th width="15%" class="th-td-norm">Description</th>
						<th width="10%" class="th-td-norm">Warehouse</th>
						<th width="8%" class="th-td-norm">Qty</th>
						<th width="8%" class="th-td-norm">UOM</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${processingReport.processingReportTypeId ne 6}">
							<c:forEach items="${processingReport.prMainProducts}" var="mp" varStatus="status">
								<tr>
									<!-- Row number -->
									<td class="td-numeric v-align-top">${status.index + 1}</td>
									<!-- Stock code -->
									<td class="th-td-norm v-align-top">${mp.item.stockCode}</td>
									<!-- Description -->
									<td class="th-td-norm v-align-top">${mp.item.description}</td>
									<!-- Warehouse list -->
									<td class="th-td-norm v-align-top">${mp.warehouse.name}</td>
									<!-- Quantity -->
									<td class="td-numeric v-align-top">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${mp.quantity}" />
									</td>
									<!-- UOM -->
									<td class="th-td-norm v-align-top">${mp.item.unitMeasurement.name}</td>
								</tr>
								<c:set var="totalMPQuantity" value="${totalMPQuantity + mp.quantity}" />
							</c:forEach>
						</c:when>
						<c:otherwise>
							<c:forEach items="${prItems}" var="prItem" varStatus="status">
								<tr>
									<!-- Row number -->
									<td class="mainProd algnRight">${status.index + 1}</td>
									<!-- Stock code -->
									<td class="mainProd">${prItem.stockCode}</td>
									<!-- Description -->
									<td class="mainProd">${prItem.description}</td>
									<!-- Warehouse list -->
									<td class="mainProd">${prItem.warehouse}</td>
									<!-- Quantity -->
									<td class="mainProd algnRight">
										<fmt:formatNumber type="number" minFractionDigits="2" 
												maxFractionDigits="2" value="${prItem.quantity}" />
									</td>
									<!-- UOM -->
									<td class="mainProd">${prItem.uom}</td>
								</tr>
								<tr>
									<td colspan="6" align="center">
										<table id="rawMats${status.index}" class="rawMatsTbl">
											<tr>
												<td colspan="4" style="font-weight: bold;">Raw Materials</td>
											</tr>
											<tr>
												<th width="30%" class="rawMatHdr">Stock Code</th>
												<th width="30%" class="rawMatHdr">Description</th>
												<th width="20%" class="rawMatHdr">Qty</th>
												<th width="20%" class="rawMatHdr">UOM</th>
											</tr>
											<c:forEach items="${prItem.prRawMatDtos}" var="rawMat" varStatus="status">
												<tr>
													<td class="rawMatItem">${rawMat.stockCode}</td>
													<td class="rawMatItem">${rawMat.description}</td>
													<td class="rawMatItem algnRight">
														<fmt:formatNumber type="number" minFractionDigits="2" 
															maxFractionDigits="2" value="${rawMat.quantity}" />
													</td>
													<td class="rawMatItem">${rawMat.uom}</td>
												</tr>
											</c:forEach>
										</table>
									</td>
								</tr>
								<c:set var="totalMPQuantity" value="${totalMPQuantity + prItem.quantity}" />
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="3" style="font-weight:bold;">Total</td>
						<td colspan="2" class="monetary"><fmt:formatNumber type="number" minFractionDigits="2" 
										maxFractionDigits="2" value="${totalMPQuantity}" /></td>
					</tr>
				</tfoot>
			</table>
			</fieldset>
		</div>
		<hr class="thin" />
	</div>
</body>
</html>