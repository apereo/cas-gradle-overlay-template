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
        <link type="text/css" rel="stylesheet" href="<c:url value="/css/custom-theme/jquery-ui-1.8.18.custom.css"/>"/>
        <script type="text/javascript" src="<c:url value="/js/jquery-1.7.1.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery-ui-1.8.18.custom.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/cas.js" />"></script>
    </head>
    <body>
        <div id="modalheaderbg"><div id="modalheader">&nbsp;</div></div>
        <div class="wrapper">
            <div>
                <decorator:body/>
            </div>
        </div>
    </body>
</html>
