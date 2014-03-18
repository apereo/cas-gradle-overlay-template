<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<html>
<head>
    <meta name="decorator" content="modal"/>
    <meta name="decorator" content="central"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/home.css" />"/>
    <script type="text/javascript" src="<c:url value="/js/home.js"/>"></script>
</head>
<body>


<c:url var="linkCustomerHubAccount" value="/app/central/linkCustomerHubAccount"/>
<c:url var="linkCommunityAccount" value="/app/central/linkCommunityAccount"/>
<c:url var="createCommunityAccount" value="/app/central/createCommunityAccount"/>
<c:url var="editCommunityAccount" value="/app/central/editCommunityAccount"/>
<c:url var="renameAccount" value="/app/central/renameAccount"/>

<c:if test="${!empty connectError}">
    <div class="alert alert-error" style="margin-top: 10px">
        <spring:message code="${connectError}"/>
    </div>
</c:if>

<div class="titlebar">
    <h2 class="apps" style="float: left">
        <spring:message code="central.home.your.accounts"/>
    </h2>

    <c:if test="${connectAccountCustomerHubEnabled || connectAccountCommunityEnabled}">
        <div class="btn-group" style="float: right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                <spring:message code="central.home.connect.account"/>
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <c:if test="${connectAccountCustomerHubEnabled}">
                    <li><a href="${linkCustomerHubAccount}"><spring:message code="central.home.connect.customerhub.account"/></a></li>
                </c:if>
                <c:if test="${connectAccountCommunityEnabled && !hasCommunityAccount}">
                    <li><a href="${linkCommunityAccount}"><spring:message code="central.home.connect.community.account"/></a></li>
                    <li class="divider"></li>
                    <li><a href="${createCommunityAccount}"><spring:message code="central.home.create.community.account"/></a></li>
                </c:if>
            </ul>
        </div>
    </c:if>
    <div style="clear: both"></div>
</div>

<div class="accounts">
    <c:forEach var="account" items="${accounts}">
        <c:choose>
            <c:when test="${account.appType == 'CRM'}">
                <div accountId="${account.id}" href="${crmProtocol}://${account.appName}.${crmDomain}:${crmPort}" class="account crm-account crm-account-${account.id}">
                    <div class="account-delete">&times;</div>
                    <div class="account-info">
                        <div id="account_${account.id}" class="account-title">
                            <span id="quick-editable-${account.id}" accountId="${account.id}" class="quick-editable">${fn:escapeXml(empty account.alias ? account.appName : account.alias)}</span>
                        </div>
                        <div class="account-detail account-url"><span>${account.appName}.${crmDomain}</span></div>
                        <div class="account-detail app-access hide" id="spinner-content-${account.id}">
                            <span id="manageAccounts-${account.id}" class="manageAccounts" accountId="${account.id}" userId="${user.id}">Manage App Access</span>
                        </div>
                    </div>
                </div>
                <div id="displayManageAccountsWrapper-${account.id}" class="action-body-left displayManageAccountsMarker" style="display:none;">
                    <div class="action-body-right">
                        <div  class="account-detail" >
                            <div>
                                <div id="displayManageAccountsContent-${account.id}" class="displayManageAccounts account-detail"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:when test="${account.appType == 'COMMUNITY'}">
                <div accountId="${account.id}" href="http://${communityDomain}/caslogin.php" class="account forum-account">
                    <div class="account-delete">&times;</div>
                    <div class="account-info">
                        <div class="account-title"><spring:message code="central.home.community.account"/></div>
                        <div class="account-detail">${communityDomain}</div>
                    </div>
                </div>
            </c:when>
            <c:when test="${account.appType == 'CUSTOMERHUB'}">
                <div accountId="${account.id}" href="https://${account.appName}.${customerHubDomain}/admin" class="account customerhub-account">
                    <div class="account-delete">&times;</div>
                    <div class="account-info">
                        <div id="account_${account.id}" class="account-title">
                            <span id="quick-editable-${account.id}" accountId="${account.id}" class="quick-editable">${fn:escapeXml(empty account.alias ? account.appName : account.alias)}</span>
                        </div>
                        <div class="account-detail"><spring:message code="central.home.customerhub.account"/></div>
                        <div class="account-detail">${account.appName}.${customerHubDomain}</div>
                    </div>
                </div>
            </c:when>
        </c:choose>
    </c:forEach>
    <div href="${marketplaceUrl}" class="account marketplace-account">
        <div class="account-delete">&times;</div>
        <div class="account-info">
            <div id="account_${account.id}" class="account-title">
                <span><spring:message code="central.home.marketplace.account"/></span>
            </div>
            <div class="account-detail">${marketplaceDomain}</div>
        </div>
    </div>
</div>

<div id="quick-editor">
    <form action="${renameAccount}" class="form-vertical" onsubmit="return centralHome.updateAlias()">
        <fieldset>
            <label for="alias" class="form-label"><spring:message code="central.home.account.alias"/></label>

            <div class="controls">
                <input type="hidden" name="account" id="account"/>
                <input type="text" name="alias" id="alias" style="width: 200px"/>
            </div>
            <div class="controls">
                <c:set var="saveLabel"><spring:message code="central.home.account.alias.save"/></c:set>
                <input type="submit" value="${saveLabel}" class="btn btn-primary"/>
                <a href="javascript:centralHome.hideQuickEditor()" class="btn"><spring:message code="central.home.account.alias.cancel"/></a>
            </div>
        </fieldset>
    </form>
</div>
</body>
</html>