<%--@elvariable id="homeLinkSelected" type="java.lang.String"--%>
<%--@elvariable id="editProfileLinkSelected" type="java.lang.String"--%>
<%--@elvariable id="oauthLinkSelected" type="java.lang.String"--%>
<%--@elvariable id="marketingOptionsLinkSelected" type="java.lang.String"--%>
<%--@elvariable id="securityQuestionLinkSelected" type="java.lang.String"--%>
<%--@elvariable id="alertTitle" type="java.lang.String"--%>
<%--@elvariable id="error" type="java.lang.String"--%>
<%--@elvariable id="info" type="java.lang.String"--%>
<%--@elvariable id="success" type="java.lang.String"--%>
<%--@elvariable id="warning" type="java.lang.String"--%>
<%--@elvariable id="errors" type="java.util.List"--%>
<%--@elvariable id="searchUsername" type="java.lang.String"--%>

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
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>

<c:url var="searchImage" value="/img/ic-magnifying-glass.svg"/>
<c:url var="xImage" value="/img/x.png"/>

<c:url var="homeLink" value="/app/central/home"/>
<c:url var="editProfileLink" value="/app/profile/editProfile"/>
<c:url var="serviceLink" value="/services/manage.html"/>
<c:url var="securityQuestionLink" value="/app/securityquestion/list"/>
<c:url var="masheryLink" value="/app/mashery/userApplicationSearch"/>
<c:url var="marketingOptionsLink" value="/app/marketingoptions/show"/>
<c:url var="userSearchUrl" value="/app/support/userSearch"/>

<c:set var="searchLabel">
    <spring:message code="search.infusionsoft.id.label"/>
</c:set>

<c:set var="pageHeader">
    <decorator:title/>
</c:set>

<c:choose>
    <c:when test="${not empty errors || not empty error}">
        <c:set var="alertAvailable" value="${true}"/>
        <c:set var="alertTitle" value="${not empty alertTitle ? alertTitle : 'Error'}"/>
        <c:set var="alertClass" value="alert-danger"/>
        <c:url var="alertImage" value="/img/ic-message-danger.png"/>
        <c:choose>
            <c:when test="${not empty errors}">
                <c:set var="alertMessages" value="${errors}"/>
            </c:when>
            <c:otherwise>
                <c:set var="alertMessage" value="${error}"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${not empty success}">
        <c:set var="alertAvailable" value="${true}"/>
        <c:set var="alertTitle" value="${not empty alertTitle ? alertTitle : 'Success'}"/>
        <c:set var="alertClass" value="alert-success"/>
        <c:url var="alertImage" value="/img/ic-message-success.png"/>
        <c:set var="alertMessage" value="${success}"/>
    </c:when>
    <c:when test="${not empty info}">
        <c:set var="alertAvailable" value="${true}"/>
        <c:set var="alertTitle" value="${not empty alertTitle ? alertTitle : 'Info'}"/>
        <c:set var="alertClass" value="alert-info"/>
        <c:url var="alertImage" value="/img/ic-message-info.png"/>
        <c:set var="alertMessage" value="${info}"/>
    </c:when>
    <c:when test="${not empty warning}">
        <c:set var="alertAvailable" value="${true}"/>
        <c:set var="alertTitle" value="${not empty alertTitle ? alertTitle : 'Warning'}"/>
        <c:set var="alertClass" value="alert-warning"/>
        <c:url var="alertImage" value="/img/ic-message-warning.png"/>
        <c:set var="alertMessage" value="${warning}"/>
    </c:when>
</c:choose>

<page:applyDecorator name="black-header-minimal">
    <!DOCTYPE html>

    <html lang="en">
    <head>
        <title><decorator:title/></title>
        <%--<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">--%>

        <decorator:head/>
    </head>
    <body>

    <div class="collapse navbar-collapse" id="central-navbar-collapse">
        <nav class="navbar navbar-default" role="navigation">
            <div class="container">
                <ul class="nav navbar-nav">
                    <li class="${!empty homeLinkSelected ? 'active' : ''}"><a href="${homeLink}">Your Accounts</a></li>
                    <li class="${!empty editProfileLinkSelected ? 'active' : ''}"><a href="${editProfileLink}">Edit Your Profile</a></li>
                    <sec:authorize url="${serviceLink}">
                        <li><a href="${serviceLink}">Services</a></li>
                        <li class="${!empty securityQuestionLinkSelected ? 'active' : ''}"><a href="${securityQuestionLink}">Security Questions</a></li>
                    </sec:authorize>
                    <sec:authorize url="${masheryLink}">
                        <li class="${!empty oauthLinkSelected ? 'active' : ''}"><a href="${masheryLink}">OAuth 2.0</a></li>
                    </sec:authorize>
                    <sec:authorize url="${marketingOptionsLink}">
                        <li class="${!empty marketingOptionsLinkSelected ? 'active' : ''}"><a href="${marketingOptionsLink}">Marketing</a></li>
                    </sec:authorize>
                </ul>
                <sec:authorize url="${userSearchUrl}">
                    <form class="navbar-form navbar-right" action="${userSearchUrl}" role="search">
                        <div class="form-group">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <object width="12" height="12" data="${searchImage}" type="image/svg+xml"></object>
                                </div>
                                <input type="search" class="form-control" name="searchUsername" placeholder="${searchLabel}" value="${fn:escapeXml(searchUsername)}"/>
                            </div>
                        </div>
                    </form>
                </sec:authorize>
            </div>
        </nav>
    </div>

    <div class="container">
        <div class="page-header">
            <c:if test="${alertAvailable}">
                <div class="alert ${alertClass} alert-dismissable fade in" role="alert">
                    <button data-dismiss="alert" aria-hidden="true" class="close" type="button">
                        <img src="${xImage}">
                    </button>

                    <div class="icon-holder">
                        <div class="tiny-icon">
                            <object width="18" height="18" data="${alertImage}" type="image/svg+xml"></object>
                        </div>
                    </div>
                    <div class="alert-text">
                        <c:if test="${not empty alertTitle}">
                            <strong>
                                <spring:message code="${alertTitle}" text="${alertTitle}" htmlEscape="true" javaScriptEscape="true"/>
                            </strong>
                            <br>
                        </c:if>

                        <c:if test="${not empty alertMessages}">
                            <ul>
                                <c:forEach var="alert" items="${alertMessages}">
                                    <li>
                                        <spring:message code="${alert}" text="${alert}" htmlEscape="true" javaScriptEscape="true"/>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>

                        <c:if test="${not empty alertMessage}">
                            <spring:message code="${alertMessage}" text="${alertMessage}" htmlEscape="true" javaScriptEscape="true"/>
                        </c:if>
                    </div>
                </div>
            </c:if>

            <h3>
                <spring:message code="${pageHeader}" text="${pageHeader}"/>
            </h3>


        </div>

        <decorator:body/>
    </div>

    <script type="text/javascript">
        (function () {
            var walkme = document.createElement('script');
            walkme.type = 'text/javascript';
            walkme.async = true;
            walkme.src = 'https://d3b3ehuo35wzeh.cloudfront.net/users/6543/walkme_6543_https.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(walkme, s);
        })();
    </script>

    <content tag="local_script">
        <decorator:getProperty property="page.local_script"/>
    </content>

    </body>
    </html>
</page:applyDecorator>