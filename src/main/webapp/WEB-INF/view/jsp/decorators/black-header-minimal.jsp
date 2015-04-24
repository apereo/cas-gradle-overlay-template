<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="cornerstoneImage" value="${pageContext.request.contextPath}/img/is_cornerstone.svg"/>
<c:set var="homeLink" value="${pageContext.request.contextPath}/app/central/home"/>
<c:set var="logoutUrl" value="${pageContext.request.contextPath}/j_spring_security_logout"/>

<page:applyDecorator name="base-bootstrap3">
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title><decorator:title/></title>
        <decorator:head/>
    </head>
    <body>
    <nav class="navbar navbar-inverse" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#central-navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="${homeLink}">
                    <img src="${cornerstoneImage}" width="18px" height="30px"/>
                    <span class="vertical-bar">|</span>
                    Account Central
                </a>
            </div>
            <sec:authorize access="!isAnonymous()">
                <div class="hidden-xs">
                    <p class="navbar-text navbar-right">
                        <strong><sec:authentication property="principal.firstName"/> <sec:authentication property="principal.lastName"/></strong>
                        (<sec:authentication property="principal.username"/>)
                        <span class="vertical-bar">|</span>
                        <a class="navbar-link" href="${logoutUrl}">Log Out</a>
                    </p>
                </div>
            </sec:authorize>

        </div>
    </nav>

    <decorator:body/>

    <content tag="local_script">
        <decorator:getProperty property="page.local_script"/>
    </content>

    </body>
    </html>
</page:applyDecorator>