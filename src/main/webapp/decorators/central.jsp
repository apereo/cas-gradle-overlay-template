<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>

<!DOCTYPE html>

<html>
    <head>
        <decorator:head/>
        <spring:theme code="standard.custom.css.file" var="customCssFile" />
        <link type="text/css" rel="stylesheet" href="<c:url value="${customCssFile}" />" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link type="text/css" rel="stylesheet" href="<c:url value="/css/custom-theme/jquery-ui-1.8.21.custom.css"/>"/>
        <link type="text/css" rel="stylesheet" href="<c:url value="/css/ui.selectmenu.css"/>"/>
        <script type="text/javascript" src="<c:url value="/js/jquery-1.7.2.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery-ui-1.8.21.custom.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/ui.selectmenu.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/cas.js" />"></script>
    </head>
    <body>
        <div id="headerbg">
            <div id="header">
                <a id="logo" href="<c:url value="/"/>"></a>
                <div id="userinfo">
                    <c:choose>
                        <c:when test="${not empty sessionScope.username}">
                            Signed in as: <strong>${session.getAttribute("username")}</strong> | Sign Out
                        </c:when>
                        <c:otherwise>
                            Not signed in
                        </c:otherwise>
                    </c:choose>
                </div>
                <span id="title">App Central</span>
            </div>
        </div>
        <div id="navbg">
            <div id="nav">
              <ul>
                <li class="selected">YOUR APPS</li>
                <li>EDIT YOUR ID</li>
              </ul>
            </div>
        </div>
        <div class="wrapper">
            <div id="content">
                <decorator:body/>
            </div>
        </div>
    </body>
</html>
