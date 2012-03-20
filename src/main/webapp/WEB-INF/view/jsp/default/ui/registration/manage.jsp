
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
    <c:if test="${user.getAccounts().size() == 0}">
        <p>
            No apps are associated with your account!
        </p>
    </c:if>
    <c:if test="${user.getAccounts().size() > 0}">
        <c:forEach var="account" items="${user.getAccounts()}">
            <p>${account.getAppUsername()} at ${account.getAppName()}</p>
        </c:forEach>
    </c:if>

    <ul>
        <li><a href="javascript:showAssociateForm('CRMAccount')">Associate an Infusionsoft application</a></li>
        <li><a href="javascript:showAssociateForm('ForumAccount')">Associate an Infusionsoft Community Forum account</a></li>
        <li><a href="javascript:showAssociateForm('CustomerHubAccount')">Associate a CustomerHub account</a></li>
    </ul>


    <div id="associateDialog" style="display: none">
    </div>
</div>

<jsp:directive.include file="../includes/bottom.jsp"/>