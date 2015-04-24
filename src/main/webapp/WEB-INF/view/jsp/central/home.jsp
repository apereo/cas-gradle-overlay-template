<%--@elvariable id="accounts" type="java.util.Map"--%>
<%--@elvariable id="crmProtocol" type="java.lang.String"--%>
<%--@elvariable id="crmDomain" type="java.lang.String"--%>
<%--@elvariable id="crmPort" type="java.lang.Integer"--%>
<%--@elvariable id="customerHubDomain" type="java.lang.String"--%>
<%--@elvariable id="communityDomain" type="java.lang.String"--%>
<%--@elvariable id="marketplaceUrl" type="java.lang.String"--%>
<%--@elvariable id="marketplaceDomain" type="java.lang.String"--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<c:set var="linkCustomerHubAccount" value="${pageContext.request.contextPath}/app/central/linkCustomerHubAccount"/>
<c:set var="linkCommunityAccount" value="${pageContext.request.contextPath}/app/central/linkCommunityAccount"/>
<c:set var="createCommunityAccount" value="${pageContext.request.contextPath}/app/central/createCommunityAccount"/>
<c:set var="editCommunityAccount" value="${pageContext.request.contextPath}/app/central/editCommunityAccount"/>
<c:set var="renameAccount" value="${pageContext.request.contextPath}/app/central/renameAccount"/>

<c:set var="homeJs" value="${pageContext.request.contextPath}/js/home.js"/>

<c:set var="arrowImage" value="${pageContext.request.contextPath}/img/ic-arrow-circle.svg"/>
<c:set var="cogImage" value="${pageContext.request.contextPath}/img/ic-cog.svg"/>

<html>
<head>
    <title><spring:message code="central.home.your.accounts"/></title>
    <meta name="decorator" content="central"/>
</head>

<body>

<c:forEach var="accountEntry" items="${accounts}">
    <c:set var="accountType" value="${accountEntry.key}"/>
    <c:set var="accountList" value="${accountEntry.value}"/>

    <div class="list-group accounts">
        <c:set var="aliasable" value="${accountType.aliasable}"/>
        <c:set var="accessTokensAllowed" value="${accountType.accessTokensAllowed}"/>

        <c:forEach var="account" items="${accountList}">
            <c:choose>
                <c:when test="${accountType == 'CRM'}">
                    <c:set var="accountTitle" value="${fn:escapeXml(empty account.alias ? account.appName : account.alias)}"/>
                    <c:set var="accountLink" value="${crmProtocol}://${account.appName}.${crmDomain}:${crmPort}"/>
                    <c:set var="accountDomain" value="${account.appName}.${crmDomain}"/>
                </c:when>
                <c:when test="${accountType == 'CUSTOMERHUB'}">
                    <c:set var="accountTitle" value="${fn:escapeXml(empty account.alias ? account.appName : account.alias)}"/>
                    <c:set var="accountLink" value="https://${account.appName}.${customerHubDomain}/admin"/>
                    <c:set var="accountDomain" value="${account.appName}.${customerHubDomain}"/>
                </c:when>
                <c:when test="${accountType == 'COMMUNITY'}">
                    <c:set var="accountTitle"><spring:message code="central.home.community.account"/></c:set>
                    <c:set var="accountLink" value="http://${communityDomain}/caslogin.php"/>
                    <c:set var="accountDomain" value="${communityDomain}"/>
                </c:when>
                <c:when test="${accountType == 'MARKETPLACE'}">
                    <c:set var="accountTitle"><spring:message code="central.home.marketplace.account"/></c:set>
                    <c:set var="accountLink" value="${marketplaceUrl}"/>
                    <c:set var="accountDomain" value="${marketplaceDomain}"/>
                </c:when>
            </c:choose>

            <a class="list-group-item" href="${accountLink}" data-account-link="${accountLink}">
                <div class="row row-xs-height">
                    <div id="divApplicationImage" class="col-xs-1 col-xs-height col-middle">
                        <img class="hidden-xs" src="../../images/app-central-${fn:toLowerCase(accountType)}.png"/>
                        <img class="visible-xs" src="../../images/app-central-${fn:toLowerCase(accountType)}.png" width="30px" height="30px"/>
                    </div>

                    <div class="col-xs-10 col-xs-height col-middle">
                        <h4 class="list-group-item-heading">
                            <span id="account-name-${account.id}" class="accountName">
                                    ${accountTitle}
                            </span>

                            <c:if test="${aliasable || accessTokensAllowed}">
                                <button class="configure btn btn-sm btn-link" data-account-id="${account.id}" tabindex="-1">
                                    <img src="${cogImage}"/>
                                </button>
                            </c:if>
                        </h4>

                        <span class="list-group-item-text hidden-xs">
                                ${accountDomain}
                        </span>
                    </div>

                    <div id="divChevron" class="col-xs-1 col-xs-height col-middle">
                        <img src="${arrowImage}"/>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
</c:forEach>

<c:forEach var="accountEntry" items="${accounts}">
    <c:set var="accountType" value="${accountEntry.key}"/>
    <c:set var="accountList" value="${accountEntry.value}"/>

    <c:set var="aliasable" value="${accountType.aliasable}"/>
    <c:set var="accessTokensAllowed" value="${accountType.accessTokensAllowed}"/>

    <c:forEach var="account" items="${accountList}">
        <c:set var="accountTitle" value="${fn:escapeXml(empty account.alias ? account.appName : account.alias)}"/>

        <c:if test="${aliasable || accessTokensAllowed}">
            <div class="modal fade" id="configure-modal-${account.id}">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">
                                <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                            </button>
                            <h4>Configure Account</h4>
                        </div>
                        <div class="modal-body">
                            <form class="updateAlias" role="form" action="${renameAccount}" data-account-id="${account.id}">
                                <input type="hidden" name="accountId" value="${account.id}">

                                <c:if test="${aliasable}">
                                    <div class="form-group">
                                        <label for="alias" class="control-label">Account Name</label>

                                        <div class="input-group">
                                            <input type="text" id="alias" name="alias" class="form-control" value="${accountTitle}" data-original-title="${accountTitle}">
                                            <span class="input-group-btn">
                                                <button type="submit" class="btn btn-primary">Save</button>
                                            </span>
                                        </div>
                                    </div>
                                </c:if>
                            </form>

                            <c:if test="${accessTokensAllowed}">
                                <div id="user-applications-${account.id}" class="user-applications">
                                    <!-- placeholder div for the ajax fill of user applications -->
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </c:forEach>
</c:forEach>

<content tag="local_script">
    <script type="text/javascript" src="${homeJs}"></script>
</content>

</body>
</html>