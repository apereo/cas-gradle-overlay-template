<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="linkInfusionsoftAppAccount" value="/central/linkInfusionsoftAppAccount"/>
<c:url var="linkCustomerHubAccount" value="/central/linkCustomerHubAccount"/>
<c:url var="linkCommunityAccount" value="/central/linkCommunityAccount"/>
<c:url var="createCommunityAccount" value="/central/createCommunityAccount"/>

<script type="text/javascript">

    function goto(url) {
        document.location.href = url;
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

<style type="text/css">

    .accounts {
        border: 1px solid #DDD;
    }

    .accounts .account {
        border-top: 1px solid #DDD;
    }

    .accounts .account:first-child {
        border-top: 1px solid #DDD;
    }

</style>

<c:set var="needsForumAccount" value="true"/>

<div id="main">
    <div class="titlebar">
        <h2 class="apps" style="float: left">
            Your Apps
        </h2>
        <div style="float: right; width: 100px">
            <div class="btn-group">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    Link an App
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="${linkInfusionsoftAppAccount}">Infusionsoft App</a></li>
                    <li><a onclick="${linkCustomerHubAccount}">CustomerHub App</a></li>
                    <li><a onclick="${linkCommunityAccount}">Community Profile</a></li>
                </ul>
            </div>
        </div>
        <div style="clear: both"></div>
    </div>

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

    <div id="associateDialog" style="display: none">
    </div>
</div>
