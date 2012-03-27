<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<jsp:directive.include file="../includes/top.jsp" />

<script type="text/javascript">

    function showAssociateForm(type) {
        $("#associateDialog").load("associateForm", { type: type }, function(response) {
            $("#associateDialog").dialog({
                autoOpen: true,
                title: "Associate an app",
                resizable: false,
                draggable: false,
                minWidth: 500,
                minHeight: 200,
                modal: true
            });
        });
    }

    function associate(opts) {
        $("#associateDialog").html("Validating your credentials...");
        $.ajax({
            url: "associate",
            data: opts,
            type: "POST",
            success: function(data) {
                alert("valid? " + data);
            }
        });
    }

</script>

<div id="main">
    <h2>Your Infusionsoft applications</h2>
    <c:if test="${fn:length(user.accounts) == 0}">
        <p>
            No apps are associated with your account!
        </p>
    </c:if>
    <c:if test="${fn:length(user.accounts) > 0}">
        <c:forEach var="account" items="${user.accounts}">
            <p>${account.appUsername} at ${account.appName}</p>
        </c:forEach>
    </c:if>

    <p>
      Note: The below don't completely work yet. We're waiting on mockups from
      Clint before we burn time on these.
    </p>
    <ul>
        <li><a href="javascript:showAssociateForm('CRMAccount')">Associate an Infusionsoft application</a></li>
        <li><a href="javascript:showAssociateForm('ForumAccount')">Associate an Infusionsoft Community Forum account</a></li>
        <li><a href="javascript:showAssociateForm('CustomerHubAccount')">Associate a CustomerHub account</a></li>
    </ul>


    <div id="associateDialog" style="display: none">
    </div>
</div>

<jsp:directive.include file="../includes/bottom.jsp"/>
