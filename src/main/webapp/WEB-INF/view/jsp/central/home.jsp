<%--@elvariable id="accounts" type="java.util.List"--%>
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

<c:url var="linkCustomerHubAccount" value="/app/central/linkCustomerHubAccount"/>
<c:url var="linkCommunityAccount" value="/app/central/linkCommunityAccount"/>
<c:url var="createCommunityAccount" value="/app/central/createCommunityAccount"/>
<c:url var="editCommunityAccount" value="/app/central/editCommunityAccount"/>
<c:url var="renameAccount" value="/app/central/renameAccount"/>

<c:url var="homeJs" value="/js/home.js"/>

<c:url var="chevronImage" value="/img/ic-chevron-slim-right.svg"/>

<html>
<head>
    <title><spring:message code="central.home.your.accounts"/></title>
    <meta name="decorator" content="central"/>
</head>

<body>

<div class="list-group accounts">

    <c:forEach var="account" items="${accounts}">
        <c:set var="aliasable" value="${account.appType.aliasable}"/>
        <c:set var="accessTokensAllowed" value="${account.appType.accessTokensAllowed}"/>
        <c:choose>
            <c:when test="${account.appType == 'CRM'}">
                <c:set var="accountTitle" value="${fn:escapeXml(empty account.alias ? account.appName : account.alias)}"/>
                <%--@elvariable id="crmProtocol" type="String"--%>
                <c:set var="accountLink" value="${crmProtocol}://${account.appName}.${crmDomain}:${crmPort}"/>
                <c:set var="accountDomain" value="${account.appName}.${crmDomain}"/>
            </c:when>
            <c:when test="${account.appType == 'CUSTOMERHUB'}">
                <c:set var="accountTitle" value="${fn:escapeXml(empty account.alias ? account.appName : account.alias)}"/>
                <c:set var="accountLink" value="https://${account.appName}.${customerHubDomain}/admin"/>
                <c:set var="accountDomain" value="${account.appName}.${customerHubDomain}"/>
            </c:when>
            <c:when test="${account.appType == 'COMMUNITY'}">
                <c:set var="accountTitle"><spring:message code="central.home.community.account"/></c:set>
                <c:set var="accountLink" value="http://${communityDomain}/caslogin.php"/>
                <c:set var="accountDomain" value="${communityDomain}"/>
            </c:when>
            <c:when test="${account.appType == 'MARKETPLACE'}">
                <c:set var="accountTitle"><spring:message code="central.home.marketplace.account"/></c:set>
                <c:set var="accountLink" value="${marketplaceUrl}"/>
                <c:set var="accountDomain" value="${marketplaceDomain}"/>
            </c:when>
        </c:choose>

        <a class="list-group-item" data-url="${accountLink}">
            <div class="row row-xs-height">
                <div id="divApplicationImage" class="col-xs-1 col-xs-height col-middle">
                    <img class="hidden-xs" src="../../images/app-central-${fn:toLowerCase(account.appType)}.png"/>
                    <img class="visible-xs" src="../../images/app-central-${fn:toLowerCase(account.appType)}.png" width="30px" height="30px"/>
                </div>

                <div class="col-xs-10 col-xs-height col-middle">
                    <h4 class="list-group-item-heading">
                        <c:if test="${aliasable}">
                            <span id="alias-${account.id}" class="aliasable hidden-xs"
                                  data-type="text"
                                  data-pk="${account.id}"
                                  data-url="/app/central/renameAccount"
                                  data-emptytext="${accountTitle}"
                                  data-emptyclass=""
                                  data-title="title">${accountTitle}
                            </span>
                        </c:if>

                        <span class="${aliasable ? 'visible-xs' : ''}">${accountTitle}</span>
                    </h4>

                    <span class="list-group-item-text hidden-xs">
                            ${accountDomain}
                    </span>

                    <c:if test="${accessTokensAllowed}">
                        <%--<div id="displayManageAccountsWrapper-${account.id}">--%>
                        <%--<div class="action-body-right">--%>
                        <%--<div class="account-detail">--%>
                        <%--<div>--%>
                        <%--<div id="displayManageAccountsContent-${account.id}" class="displayManageAccounts account-detail"></div>--%>
                        <%--</div>--%>
                        <%--</div>--%>
                        <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="account-detail app-access hide" id="spinner-content-${account.id}">--%>
                        <%--<li>--%>
                        <%--<i class="fa-li fa fa-key"></i>--%>
                        <%--<%--<span id="manageAccounts-${account.id}" accountId="${account.id}" userId="${user.id}"></span>--%>
                        <%--<%--<a class="navbar-link">--%>
                        <%--<span data-toggle="collapse" data-parent="#accordion" class="accessTokensAllowed" href="#collapseOne">--%>
                        <%--Manage App Access--%>
                        <%--</span>--%>

                        <%--<div id="collapseOne" class="collapse">--%>
                        <%--Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.--%>
                        <%--</div>--%>
                        <%--</li>--%>
                    </c:if>

                </div>

                <div id="divChevron" class="col-xs-1 col-xs-height col-middle">
                    <object class="pull-right" width="32" height="32" data="${chevronImage}" type="image/svg+xml"></object>
                </div>
            </div>
        </a>


    </c:forEach>
</div>


<%--<!-- Bootstrap Modal -->--%>
<%--<div id="myModal" class="modal hide fade confirmation-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">--%>
<%--<div class="modal-header">--%>
<%--<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>--%>
<%--<h3 id="myModalLabel">Are You Sure?</h3>--%>
<%--</div>--%>
<%--<div id="modal-body-id" class="modal-body">--%>
<%--<p></p>--%>
<%--</div>--%>
<%--<div class="modal-footer">--%>
<%--<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>--%>
<%--<button class="btn btn-primary" onclick="manageAppAccess.revokeAccess();">Revoke Access</button>--%>
<%--</div>--%>
<%--</div>--%>

<content tag="local_script">
    <script type="text/javascript" src="${homeJs}"></script>
</content>

</body>
</html>