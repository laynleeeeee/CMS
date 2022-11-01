<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
 <%@ include file="../../../include.jsp" %>
<!-- 

       Description: Classification of Concessionaire.
 -->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Concessioniare Classification</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/checkboxUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pageUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/searchUtil.js"></script>
<script type ="text/javascript">
$(function() { 
       $("#btnSearchConcessionaireClassification").click(function () {
               searchConcessionaireClassification();
       });
       
       $("#btnAddConcessionaireClassification").click(function(){
               $("#concessionaireClassificationForm").load(contextPath + "/concessionaireClassification/form");
               $("html, body").animate({scrollTop: $("#concessionaireClassificationForm").offset().top}, 0050);
       });
       
       $("#btnEditConcessionaireClassification").click(function() {
               var cbs = document.getElementsByName("cbConcessionaireClassification");
               var id = 0;
               for (var index =0; index < cbs.length; index++) {
                       var cb = cbs[index];
                       if (cb.checked == true){
                               id = cb.id;
                               break;
                       }
               }
               $("#concessionaireClassificationForm").load(contextPath + "/concessionaireClassification/form?concessionaireClassificationId="+id);
               $("html, body").animate({scrollTop: $("#concessionaireClassificationForm").offset().top}, 0050);
       });
       $("#btnDeleteConcessionaireClassification").click(function() {
               var cbs = document.getElementsByName("cbConcessionaireClassification");
               var ids = null;
               var ctr = 0;
               for (var index =0; index < cbs.length; index++) {
                       var cb = cbs[index];
                       if (cb.checked == true){
                               if (ids == null){ 
                                       ids = "" + cb.id;
                               } else {
                                       ids = ids + ";" + cb.id;                                
                               } 
                               ctr++;
                       }
               }
               var confirmation = "Are you sure you want to delete " + ctr + " concessionaire classification";
               if (ctr == 1) 
                       confirmation = confirmation + "?";
               if (ctr > 1)
                       confirmation = confirmation + "s?";
               
               var confirmDelete = confirm(confirmation);
               if (confirmDelete == true) {
                       $("#concessionaireClassificationTable").load(contextPath + "/concessionaireClassification/delete?"+
                                       "concessionaireClassificationIds="+ids);
                       $("html, body").animate({scrollTop: $("#concessionaireClassificationTable").offset().top}, 0050);
               } else {
                       doSearch ("concessionaireClassificationTable", "/concessionaireClassification?name="+searchStatus+"&pageNumber=1");
                       $("html, body").animate({scrollTop: $("#concessionaireclassificationTable").offset().top}, 0050);
               }
       });
       $("#btnPrintConcessionaireClassification").click(function() {
               var targetUrl = contextPath + "/concessionaireClassification/print?name=&description=";
               window.open(targetUrl,"popup","width=1000,height=600,scrollbars=yes,resizable=yes," +
               "toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
       });

       $("#btnSaveConcessionaireClassification").live("click", function () {
               $(this).attr("disabled", "disabled");
               doPost ("concessionaireClassification", "concessionaireClassificationForm", "concessionaireClassificationTable", 
                               "/concessionaireClassification"+getCommonParameter()+"&pageNumber=1");
               $("html, body").animate({scrollTop: $("#concessionaireClassificationTable").offset().top}, 0050);
       });

       $("#btnCancelConcessionaireClassification").live("click", function() {
               $("#concessionaireClassificationForm").html("");
               searchConcessionaireClassification();
       });
});

function getCommonParameter () {
       var name = processSearchName($("#name").val());
       var description = processSearchName($("#description").val());
       return "?name="+name+"&description="+description;
}


function searchConcessionaireClassification () {
       doSearch ("concessionaireClassificationTable", "/concessionaireClassification"+getCommonParameter()+"&pageNumber=1");
       $("html, body").animate({scrollTop: $("#concessionaireClassificationTable").offset().top}, 0050);
}

</script>
</head>
<body>
       <div id="searchDiv">
               <table class="tableInfo">
                       <tr>
                               <td width="8%" class="title">Name</td>
                               <td><input type ="text" id="name"> </td>
                       </tr>
                       <tr>
                               <td width="8%" class="title">Description</td>
                               <td><input type ="text" id="description"> </td>
                       </tr>
               </table>
               <div align="right">
                       <input type="button" id="btnSearchConcessionaireClassification" value="Search">
               </div>
       </div>

       <!-- Table for Concessionaire Classification -->
       <div id="concessionaireClassificationTable">
               <%@ include file="ConcessionaireClassificationTable.jsp" %>
       </div>
       <div class="controls">
               <input type ="button" id="btnAddConcessionaireClassification" value="Add">
               <input type ="button" id="btnEditConcessionaireClassification" value="Edit">
               <input type ="button" id="btnDeleteConcessionaireClassification" value="Delete">
               <input type ="button" id="btnPrintConcessionaireClassification" value="Print">
       </div>
       <div id ="concessionaireClassificationForm" style="margin-top: 20px;">
       </div>
</body>
</html>