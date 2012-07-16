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


</style>

<c:set var="needsForumAccount" value="true"/>

<div id="main">
    <div class="titlebar">
        <h2 class="apps" style="float: left">
            Your Apps
        </h2>
        <div class="btn-group" style="float: right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                Link an App
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="${linkInfusionsoftAppAccount}">Link an Infusionsoft App</a></li>
                <li><a href="${linkCustomerHubAccount}">Link a CustomerHub App</a></li>
                <c:if test="${!hasCommunityAccount}">
                    <li><a href="${linkCommunityAccount}">Link a Community Profile</a></li>
                    <li class="divider"></li>
                    <li><a href="${createCommunityAccount}">Create a Community Profile</a></li>
                </c:if>
            </ul>
        </div>
        <div style="clear: both"></div>
    </div>

    <c:if test="${fn:length(user.accounts) > 0}">
        <div class="accounts">
            <c:forEach var="account" items="${user.accounts}">
                <c:choose>
                    <c:when test="${account.appType == 'crm'}">
                        <a href="https://${account.appName}.infusiontest.com:8443" class="account crm-account">
                            <div class="account-info">
                                <div class="account-title">${account.appName}</div>
                                <div class="account-detail">Infusionsoft App</div>
                                <div class="account-detail">${account.appName}.infusionsoft.com</div>
                            </div>
                        </a>
                    </c:when>
                    <c:when test="${account.appType == 'community'}">
                        <a href="http://community.infusionsoft.com" class="account forum-account">
                            <div class="account-info">
                                <div class="account-title">Infusionsoft Community</div>
                                <div class="account-detail">Display Name: ${account.appUsername}</div>
                                <div class="account-detail">community.infusionsoft.com</div>
                            </div>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="https://${account.appName}.infusiontest.com:8443" class="account customerhub-account">
                            <div class="account-info">
                                <div class="account-title">${account.appName}</div>
                                <div class="account-detail">CustomerHub App</div>
                                <div class="account-detail">${account.appName}.customerhub.net</div>
                            </div>
                        </a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </c:if>

    <div id="associateDialog" style="display: none">
    </div>
</div>
