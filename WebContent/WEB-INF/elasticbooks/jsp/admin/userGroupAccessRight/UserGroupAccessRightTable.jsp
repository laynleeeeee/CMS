<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<!-- 

	Description: User Group Access Right form table. 
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<style>
#tblUGARForm tbody td, #tblUGARReport tbody td, #tblUGARWorkflow tbody td{
	border-bottom: 1px solid #000000;
}

.tdCenterCB {
	text-align: center;
}

</style>
<script>
$(document).ready (function () {
	var totalForms = parseInt("${fn:length(uGAccessRightDto.uGARWorkflowDtos)}");
	for (var i=0; i<totalForms; i++) {
		$(".cbWorkFlow" + i).each(function (j) {
			compModKeyWorkflow(true, this, i, $(this).val());
		});
	} 
});
</script>
<table id="tblUGARReport" class="dataTable">
	<thead>
		<tr>
			<th>Admin Module Name</th>
			<th>Allow Access</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="uGAR" items="${uGAccessRightDto.uGarAdminDtos}" varStatus="status">
			<tr>
				<td>
					<input type="hidden" id="hdnAId${status.index}" 
						name="uGarAdminDtos[${status.index}].userGroupAccessRight.id" 
						value="${uGAR.userGroupAccessRight.id}" />
					<input type="hidden" id="hdnACreatedBy${status.index}" 
						name="uGarAdminDtos[${status.index}].userGroupAccessRight.createdBy" 
						value="${uGAR.userGroupAccessRight.createdBy}" />
					<input type="hidden" id="hdnAPK${status.index}" 
						name="uGarAdminDtos[${status.index}].userGroupAccessRight.productKey" 
						value="${uGAR.userGroupAccessRight.productKey }" />
					<input type="hidden" id="hdnAMC${status.index}" 
						name="uGarAdminDtos[${status.index}].moduleCode" 
						value="${uGAR.moduleCode}" />
					${uGAR.reportName}
				</td>
				<td class="tdCenterCB">
					<input type="checkbox" id="cbAllowAccessA${status.index}" 
						name='uGarAdminDtos[${status.index}].allowAccess' 
						<c:if test="${uGAR.allowAccess}">checked="checked"</c:if>		
					/>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br>
<table id="tblUGARForm" class="dataTable">
	<thead>
		<tr>
			<th>Product</th>
			<th>Add</th>
			<th>Edit/Cancel</th>
			<th>Search</th>
			<th>Approval</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="uGAR" items="${uGAccessRightDto.uGARFormDtos}" varStatus="status">
			<tr>
				<td>
					<input type="hidden" id="hdnFId${status.index}" 
						name="uGARFormDtos[${status.index}].userGroupAccessRight.id" 
						value="${uGAR.userGroupAccessRight.id}" />
					<input type="hidden" id="hdnFCreatedBy${status.index}" 
						name="uGARFormDtos[${status.index}].userGroupAccessRight.createdBy" 
						value="${uGAR.userGroupAccessRight.createdBy}" />
					<input type="hidden" id="hdnFPK${status.index}" 
						name="uGARFormDtos[${status.index}].userGroupAccessRight.productKey" 
						value="${uGAR.userGroupAccessRight.productKey}" />
					${uGAR.formName}
				</td>
				<td class="tdCenterCB">
					<input type="checkbox" id="cbAdd${status.index}" 
						name='uGARFormDtos[${status.index}].add' 
						<c:if test="${uGAR.add}">checked="checked"</c:if>
					/>
				</td>
				<td class="tdCenterCB">
					<input type="checkbox" id="cbEdit${status.index}" 
						name='uGARFormDtos[${status.index}].edit' 
						<c:if test="${uGAR.edit}">checked="checked"</c:if>
					/>
				</td>
				<td class="tdCenterCB">
					<input type="checkbox" id="cbSearch${status.index}" 
						name='uGARFormDtos[${status.index}].search' 
						<c:if test="${uGAR.search}">checked="checked"</c:if>
					/>
				</td>
				<td class="tdCenterCB">
					<input type="checkbox" id="cbSearch${status.index}" 
						name='uGARFormDtos[${status.index}].approval' 
						<c:if test="${uGAR.approval}">checked="checked"</c:if>
					/>
				</td>

			</tr>
		</c:forEach>
	</tbody>
</table>
<br>
<table id="tblUGARReport" class="dataTable">
	<thead>
		<tr>
			<th>Report Name</th>
			<th>Allow Access</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="uGAR" items="${uGAccessRightDto.uGARReportDtos}" varStatus="status">
			<tr>
				<td>
					<input type="hidden" id="hdnRId${status.index}" 
						name="uGARReportDtos[${status.index}].userGroupAccessRight.id" 
						value="${uGAR.userGroupAccessRight.id}" />
					<input type="hidden" id="hdnRCreatedBy${status.index}" 
						name="uGARReportDtos[${status.index}].userGroupAccessRight.createdBy" 
						value="${uGAR.userGroupAccessRight.createdBy}" />
					<input type="hidden" id="hdnRPK${status.index}" 
						name="uGARReportDtos[${status.index}].userGroupAccessRight.productKey" 
						value="${uGAR.userGroupAccessRight.productKey }" />
					<input type="hidden" id="hdnRMC${status.index}" 
						name="uGARReportDtos[${status.index}].moduleCode" 
						value="${uGAR.moduleCode}" />
					${uGAR.reportName}
				</td>
				<td class="tdCenterCB">
					<input type="checkbox" id="cbAllowAccessR${status.index}" 
						name='uGARReportDtos[${status.index}].allowAccess' 
						<c:if test="${uGAR.allowAccess}">checked="checked"</c:if>		
					/>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br>
<table id="tblUGARWorkflow" class="dataTable">
	<thead>
		<tr>
			<th>Form Name</th>
			<th>Access</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="uGAR" items="${uGAccessRightDto.uGARWorkflowDtos}" varStatus="status">
			<tr>
				<td>
					<input type="hidden" id="hdnWId${status.index}" 
						name="uGARWorkflowDtos[${status.index}].userGroupAccessRight.id" 
						value="${uGAR.userGroupAccessRight.id}" />
					<input type="hidden" id="hdnWCreatedBy${status.index}" 
						name="uGARWorkflowDtos[${status.index}].userGroupAccessRight.createdBy" 
						value="${uGAR.userGroupAccessRight.createdBy}" />
					<input type="hidden" id="hdnWPK${status.index}" 
						name="uGARWorkflowDtos[${status.index}].userGroupAccessRight.productKey" 
						value="${uGAR.userGroupAccessRight.productKey }" />
					<input type="hidden" id="hdnWMC${status.index}" 
						name="uGARWorkflowDtos[${status.index}].moduleCode" 
						value="${uGAR.moduleCode}" />
					<input type="hidden" id="hdnWStatuses${status.index}"
						name="uGARWorkflowDtos[${status.index}].statuses"
						value="${uGAR.statuses}" />
					${uGAR.workflowName}
				</td>
				<td>
					<c:forEach var="fdto" items="${uGAR.statuses}" varStatus="sStatus">
						<span style="white-space: nowrap">
							<c:if test="${sStatus.index gt 0}">
								<input type="checkbox" id="cbWorkFlow${status.index}${sStatus.index}" 
									name='statuses[${sStatus.index}].allowAccess'
									class='cbWorkFlow${status.index}'
									value='${fdto.moduleKey}'
									<c:if test="${fdto.allowAccess}">checked="checked"</c:if>		
									onclick="compModKeyWorkflow(false,this, ${status.index}, ${fdto.moduleKey});"
								/>
							</c:if>
							<span class="spanStatusDescription${status.index}">${fdto.formStatus.description}</span>
							<c:if test="${sStatus.index < (fn:length(uGAR.statuses) - 1)}">
								<span class="spanArrow">--></span>
							</c:if>
						</span> 
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>