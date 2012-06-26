<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<script type="text/javascript">

    function showAssociateForm(type, title) {
        $("#associateDialog").load('<c:url value="/registration/associateForm"/>', { type: type }, function(response) {
            $("#associateDialog").dialog({
                title: title,
                resizable: false,
                draggable: false,
                minWidth: 500,
                minHeight: 200,
                modal: true
            });
            $("#associateDialog").dialog("open");
        });
    }

    function associate(opts) {
        $("#associateDialog").html("Validating your credentials...");
        $.ajax({
            url: '<c:url value="/registration/associate"/>',
            data: opts,
            type: "POST",
            success: function(data) {
                if ($.trim(data) == "OK") {
                    // TODO - change to Ajax
                    location.reload();
                }
            },
            error: function() {
                alert("error!");
            }
        });
    }

    function associateForum(opts) {
        $("#associateDialog").html("Validating your credentials...");
        $.ajax({
            url: '<c:url value="/registration/associateForum"/>',
            data: opts,
            type: "POST",
            success: function(data) {
                //alert("MESSAGE: " + $.trim(data));
                if ($.trim(data) == "OK") {
                    // TODO - change to Ajax
                    location.reload();
                }
            },
            error: function() {
                alert("error!");
            }
        });
    }

    function createForum(opts) {
        $("#associateDialog").html("Validating your credentials...");
        $.ajax({
            url: '<c:url value="/registration/createForum"/>',
            data: opts,
            type: "POST",
            success: function(data) {
                //alert("MESSAGE: " + $.trim(data));
                if ($.trim(data) == "OK") {
                    // TODO - change to Ajax
                    location.reload();
                }
            },
            error: function() {
                alert("error!");
            }
        });
    }



</script>

<div id="main">
    <h2 class="apps">Your Apps</h2>
    <p>
        Hello there, ${user.username}!
    </p>
    <c:set var="needsForumAccount" value="true"/>
    <c:if test="${fn:length(user.accounts) == 0}">
        <p>
            No apps are associated with your account!
        </p>
    </c:if>
    <c:if test="${fn:length(user.accounts) > 0}">
        <c:forEach var="account" items="${user.accounts}">
            <c:choose>
                <c:when test="${account.appType == 'CRM'}">
                    <p><a href="https://${account.appName}.infusiontest.com:8443">${account.appName}</a></p>
                </c:when>
                <c:otherwise>
                    <c:if test="${account.appType == 'forum'}">
                        <c:set var="needsForumAccount" value="false"/>
                    </c:if>
                    <p>${account.appUsername} at ${account.appName}</p>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </c:if>

    <p>
      Note: The below don't completely work yet. We're waiting on mockups from
      Clint before we burn time on these.
    </p>
    <ul>
        <li><a href="javascript:showAssociateForm('CRMAccount', 'Associate an app account')">Associate an Infusionsoft application</a></li>
        <c:if test="${needsForumAccount}">
            <li>
                <a href="javascript:showAssociateForm('ForumAccount', 'Associate a community login')">Associate an Infusionsoft Community Forum account</a> or
                <a href="javascript:showAssociateForm('CreateForumAccount', 'Create a community account')">Create an Infusionsoft Community Forum account</a>
            </li>
        </c:if>
        <li><a href="javascript:showAssociateForm('CustomerHubAccount', 'Associate a Customer Hub account')">Associate a CustomerHub account</a></li>
    </ul>


    <div id="associateDialog" style="display: none">
    </div>
</div>
