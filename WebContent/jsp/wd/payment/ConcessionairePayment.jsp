<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="../../include.jsp" %>
<!-- 

	Description: Displays concessionaire accounts
 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js"> </script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery1.7.2min.js"> </script>
<script type="text/javascript">
var baseUrl = contextPath + "/concessionare/payment/"+${concessionaire.id};
$(document).ready (function () {
	$("#btnSelectConcessionaire").click(function() {
		var ids = getCheckBoxIds ("cbConcessionaireAcct", true);
		var uri = baseUrl +"?toBePaid=" + ids + "";
		var unPaidIds = getUnpaidIds();
		if (unPaidIds != null)
			uri = uri + ";"+unPaidIds;
		var date = $("#date").val();
		var amount = $("#payment").val();
		uri = uri + "&dateRP="+date + "&amount="+amount;
		$("#concessionareForm").load(uri, function () {
			document.getElementById('btnSave').disabled = false;
		});
	});

	$("#btnRemoveConcessionaire").click(function() {
		var ids = getUncheckedCheckBoxIds ("cbToBePaidAccounts", false);
		var uri = baseUrl;
		document.getElementById('btnSave').disabled = true;
		if (ids != null) {
			uri = uri + "?toBePaid=" + ids;
			document.getElementById('btnSave').disabled = false;
		}
		var date = $("#date").val();
		var amount = $("#payment").val();
		if (ids != null)
			uri = uri + "&";
		else
			uri = uri + "?";
		uri = uri + "dateRP="+date + "&amount="+amount;
		$("#concessionareForm").load(uri, function () {
			var ids = getUncheckedCheckBoxIds ("cbToBePaidAccounts", false);
			if (ids != null) {
				document.getElementById('btnSave').disabled = false;
			};
		});
	});
	
	$("#btnSave").click(function() {
		$(this).attr("disabled", "disabled");
		var showPopup = function () {
			var targetUrl = baseUrl + "/print";
			window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
			"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
		};
		var callbackFunction  = function (data) {
			if (data == "saved") {
				
				$("#concessionareForm").load(baseUrl, showPopup);
				$("html, body").animate({scrollTop: $("#concessionareForm").offset().top}, 1000);
			} else {
				dojo.byId("concessionairePaymentForm").innerHTML = data;
				document.getElementById('btnSave').disabled = false;
			}
		};
		doPostWithCallBack ("concessionairePayment", "concessionairePaymentForm", callbackFunction);
	});
});

function getCheckBoxIds (checkBoxName, isCheckedOnly) {
	var cbs = document.getElementsByName(checkBoxName);
	var ids = null;
	var ctr = 0;
	for (var index =0; index < cbs.length; index++) {
		var cb = cbs[index];
		if ((isCheckedOnly && cb.checked == true) || (!isCheckedOnly)){
			if (ids == null){
				ids = "" + cb.id;
			} else {
				ids = ids + ";" + cb.id;
			}
			ctr++;
		};
	}
	return ids;
}

function getUncheckedCheckBoxIds (checkBoxName) {
	var cbs = document.getElementsByName(checkBoxName);
	var ids = null;
	var ctr = 0;
	for (var index = 0; index < cbs.length; index++) {
		var cb = cbs[index];
		if (cb.checked == false){
			if (ids == null){
				ids = "" + cb.id;
			} else {
				ids = ids + ";" + cb.id;
			}
			ctr++;
		};
	}
	return ids;
}

function getUnpaidIds () {
	return getCheckBoxIds("cbToBePaidAccounts", false);
}
</script>
<title>Concesionaire Payment</title>
</head>
<body>
<div class="modFormLabel">Concessionaire Payment Form</div>
	<c:if test="${concessionairePaymentId != null }">
		<input type="hidden" value="${concessionairePaymentId}" id="concessionairePaymentId">
	</c:if>
	<div class="modFormUnderline"> </div>
	<div id="concessionaireProfile">
		<%@ include file="../concessionaire/account/ConcessionaireAcctProfile.jsp" %>
	</div>
	<div id="concessionaireTable">
		<table id="unPaidTable" class="dataTable">
		<tr>
			<th width="1%"><input type="checkbox" id="checkAll" 
			onclick="checkUncheckedAll ('cbConcessionaireAcct', this, '', 'btnSelectConcessionaire')"></th>
			<th>Date</th>
			<th>Due Date</th>
			<th>Water Bill Number</th>
			<th>Cubic Meters</th>
			<th>Metered Sale</th>
			<th>Penalty</th>
			<th>Meter Rental</th>
			<th>Total</th>
		</tr>
		<c:forEach var="unpaidAccount" items="${unpaidAccounts}">
			<tr onMouseOver="this.className='highlight'" onMouseOut="this.className='normal'">
				<td><input type="checkbox" id="${unpaidAccount.id}" name="cbConcessionaireAcct"
				onclick="enableEditAndDeleteButton('cbConcessionaireAcct','','btnSelectConcessionaire');"></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${unpaidAccount.date}"/></td>
				<td><fmt:formatDate pattern="MM/dd/yyyy" value="${unpaidAccount.dueDate}"/></td>
				<td>${unpaidAccount.wbNumber}</td>
				<td class="numberClass">${unpaidAccount.cubicMeter}</td>
				
				<td class="numberClass">
					<c:if test="${unpaidAccount.paidAmount > 0 && unpaidAccount.paid == false}">
							<b>
								Initial payment (<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${unpaidAccount.paidAmount}" />)
							</b>
					</c:if>
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${unpaidAccount.unpaidAmount}" />
				</td>
				<c:set var="penalty" value="0"></c:set>
				<td class="numberClass">
					<c:choose>
						<c:when test="${unpaidAccount.penalty == null}">
							<c:set var="penalty" value="0"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="penalty" value="${unpaidAccount.penalty.unpaidAmount}"></c:set>
						</c:otherwise>
					</c:choose>
					<c:if test="${penalty > 0}">
						<b>
						<c:if test="${unpaidAccount.penalty.paidAmount > 0 && unpaidAccount.penalty.paid != true}">
							Initial payment (<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${unpaidAccount.penalty.paidAmount}" />)
						</c:if>
						</b>
					</c:if>
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${penalty}" />
				</td>
				<td class="numberClass">
					<b>
					<c:if test="${unpaidAccount.rental.paidAmount > 0 && unpaidAccount.rental.paid != true}">
						Initial payment (<fmt:formatNumber type="number" minFractionDigits="2"
											maxFractionDigits="2" value="${unpaidAccount.rental.paidAmount}" />)
					</c:if>
					</b>	
					<fmt:formatNumber type="number" minFractionDigits="2"
						maxFractionDigits="2" value="${unpaidAccount.rental.unpaidAmount}" />
				</td>
				<td class="numberClass">
					<fmt:formatNumber type="number"
						minFractionDigits="2"
						maxFractionDigits="2"
						value="${unpaidAccount.unpaidAmount + unpaidAccount.rental.unpaidAmount + penalty}" />
				</td>
			</tr>
		</c:forEach>
	</table>
	<div class="controls">
		<input type="button" id="btnSelectConcessionaire" disabled="disabled"
			value="Select"/>
	</div>
	<br/><br/>
	
	<fieldset >
		<legend>Payment Form</legend>
		<div id="concessionairePaymentForm">
			<form:form method="POST" commandName="concessionairePayment" >
				<div class="information">* Required field</div>
				<table class="formTable">
					<tr>
						<td>* Date</td>
						<td class="tdDateFilter">
							<form:input path="date" maxlength="10" cssClass="dateClass" />
							<img src="${pageContext.request.contextPath}/images/cal.gif"
									onclick="javascript:NewCssCal('date')" style="cursor:pointer"
										style="float: right;"/>
						</td>
					</tr>
					<tr>
						<td colspan="2"><form:errors path="date" cssClass="error"/></td>
					</tr>
					<tr>
						<td>* Amount</td>
						<td>
							<form:input path="payment" cssClass="numberClass"/>
						</td>
					<tr>
					<tr>
						<td colspan="2"><form:errors path="payment" cssClass="error"/></td>
					</tr>
				</table>
			</form:form>
		</div>
		<table id="toBePaidtbl" class="dataTable">
			<thead>
				<tr>
					<th width="1%">
					<input type="checkbox" class="checkbox" id="checkAll" 
						onclick="checkUncheckedAll ('cbToBePaidAccounts',this, '', 'btnRemoveConcessionaire' )"></th>
					<th width="10%">Date</th>
					<th width="10%">Bill Number</th>
					<th width="60%">Description</th>
					<th width="19%">Amount</th>
		 		</tr>
	 		</thead>
	 		<c:set var="total" value="0"></c:set>
	 		<tbody>
		 		<c:forEach var="toBePaidAccount" items="${toBePaidAccounts}">
		 			<tr>
		 				<td><input type="checkbox" id="${toBePaidAccount.id}" name="cbToBePaidAccounts"
							onclick="enableEditAndDeleteButton('cbToBePaidAccounts','','btnRemoveConcessionaire')"></td>
						<td>
							<fmt:formatDate pattern="MM/dd/yyyy" value="${toBePaidAccount.date}"/>
						</td>
						<td>
							${toBePaidAccount.wbNumber}
						</td>
						<td>
							${toBePaidAccount.cubicMeter} cubic meters
						</td>
						<td class="numberClass">
							<c:set var="total" value="${total + toBePaidAccount.unpaidAmount}"></c:set>
							<fmt:formatNumber type="number" minFractionDigits="2"
								maxFractionDigits="2" value="${toBePaidAccount.unpaidAmount}"/>
							
						</td>
		 			</tr>
		 			<c:if test="${toBePaidAccount.rental.unpaidAmount > 0}">
			 			<tr>
			 				<td colspan="3"></td>
							<td>
								Meter rental
							</td>
							<td class="numberClass">
								<c:set var="unpaidRental" value="${toBePaidAccount.rental.unpaidAmount}" />
								<c:set var="total" value="${total + unpaidRental}"></c:set>
								<fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${unpaidRental}" />
							</td>
			 			</tr>
		 			</c:if>
					<c:if test="${toBePaidAccount.penalty != null}">
						<tr>
							<td colspan="3"></td>
							<td>Penalty (due date : <fmt:formatDate pattern="MM/dd/yyyy" value="${toBePaidAccount.dueDate}"/>)</td>
							<td class="numberClass">
								<c:set var="total" value="${total + toBePaidAccount.penalty.unpaidAmount}"></c:set>
								<fmt:formatNumber type="number" minFractionDigits="2" 
								maxFractionDigits="2" value="${toBePaidAccount.penalty.unpaidAmount}" />
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="summaryTr">
					<td colspan="4" align="left"> Total</td>
					<td class="numberClass"><fmt:formatNumber type="number" minFractionDigits="2" 
							maxFractionDigits="2" value="${total}" /></td>
				</tr>
			</tfoot>
 		</table>
  		<div class="controls">
			<input type="button" id="btnRemoveConcessionaire" disabled="disabled" value="Remove"/>
			<input type="button" id="btnSave" value="Save" disabled="disabled"/>
		</div>
	</fieldset>	
	</div>
</body>
</html>