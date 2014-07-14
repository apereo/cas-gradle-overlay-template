<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html>
<head>
    <title><decorator:title/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="/css/bootstrap.min.css" />"/>
    <spring:theme code="standard.custom.css.file" var="customCssFile"/>
    <link type="text/css" rel="stylesheet" href="<c:url value="${customCssFile}" />"/>
    <script type="text/javascript" src="<c:url value="/js/jquery-1.10.0/jquery-1.10.0.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/bootstrap-2.3.1/bootstrap.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery-plugins/jeditable-1.7.1/jquery.jeditable.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery-plugins/placeholder-2.0.7/jquery.placeholder.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery-plugins/qtip-1.0.0-rc3/jquery.qtip.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/global.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/manageAppAccess.js"/>"></script>
    <script src="https://www.infusionsoft.com/sites/all/modules/contrib/analytics/marketo.js"></script>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800" type="text/css">
    <decorator:head/>
</head>
<body>
<div id="headerbg">
    <div id="header">
        <a id="logo" href="<c:url value="/"/>"></a>

        <div id="userinfo">
            <c:url var="logoutUrl" value="/j_spring_security_logout"/>
            <strong>
                <sec:authentication property="principal.firstName"/> <sec:authentication property="principal.lastName"/>
            </strong>
            (<sec:authentication property="principal.username"/>)
            |
            <a href="${logoutUrl}">Sign Out</a>
        </div>
        <span id="title">Account Central</span>
    </div>
</div>
<div id="navbg">
    <div id="nav">
        <ul>
            <c:url var="homeLink" value="/app/central/home"/>
            <c:url var="editProfileLink" value="/app/profile/editProfile"/>
            <c:url var="serviceLink" value="/services/manage.html"/>
            <c:url var="masheryLink" value="/app/oauth/userApplicationSearch"/>
            <c:url var="marketingOptionsLink" value="/app/marketingoptions/show"/>
            <li><a href="${homeLink}" class="${homeLinkSelected}">YOUR ACCOUNTS</a></li>
            <li><a href="${editProfileLink}" class="${editProfileLinkSelected}">EDIT YOUR PROFILE</a></li>
            <sec:authorize access="hasRole('ROLE_CAS_ADMIN')">
                <li><a href="${serviceLink}" class="${serviceLinkSelected}">SERVICES</a></li>
                <li><a href="${masheryLink}" class="${masheryLinkSelected}">OAUTH 2.0</a></li>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_CAS_MARKETING_ADMIN')">
                <li><a href="${marketingOptionsLink}" class="${marketingOptionsLinkSelected}">MARKETING</a></li>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_CAS_ADMIN') or hasRole('ROLE_CAS_SUPPORT_TIER_1')">
                <c:url var="userSearchUrl" value="/app/support/userSearch"/>
                <form id="userSearch" class="navbar-search pull-right" action="${userSearchUrl}">
                    <c:set var="searchLabel">
                        <spring:message code="search.infusionsoft.id.label"/>
                    </c:set>
                    <input type="text" class="search-query" name="searchUsername" placeholder="${searchLabel}" value="${fn:escapeXml(searchUsername)}"/>
                </form>
            </sec:authorize>
        </ul>
    </div>
</div>
<div class="wrapper">
    <div id="content">
        <div id="main">
            <c:if test="${not empty error}">
                <div class="alert alert-error"><spring:message code="${error}" text="${error}" htmlEscape="true" javaScriptEscape="true"/></div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success"><spring:message code="${success}" text="${success}" htmlEscape="true" javaScriptEscape="true"/></div>
            </c:if>
            <c:if test="${not empty info}">
                <div class="alert alert-info"><spring:message code="${info}" text="${info}" htmlEscape="true" javaScriptEscape="true"/></div>
            </c:if>
            <decorator:body/>
        </div>
    </div>
</div>
<!-- Bootstrap Modal -->
<div id="myModal" class="modal hide fade confirmation-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="myModalLabel">Are You Sure?</h3>
    </div>
    <div id="modal-body-id" class="modal-body">
        <p></p>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
        <button class="btn btn-primary" onclick="manageAppAccess.revokeAccess();">Revoke Access</button>
    </div>
</div>
<script type="text/javascript">(function () {
    var walkme = document.createElement('script');
    walkme.type = 'text/javascript';
    walkme.async = true;
    walkme.src = 'https://d3b3ehuo35wzeh.cloudfront.net/users/6543/walkme_6543_https.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(walkme, s);
})();</script>
</body>
</html>
