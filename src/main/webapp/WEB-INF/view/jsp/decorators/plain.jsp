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
        <title><decorator:title/></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link type="text/css" rel="stylesheet" href="<c:url value="/css/bootstrap.min.css" />" />
        <spring:theme code="standard.custom.css.file" var="customCssFile" />
        <link type="text/css" rel="stylesheet" href="<c:url value="${customCssFile}" />" />
        <script type="text/javascript" src="<c:url value="/js/jquery-1.7.2.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery.jeditable.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/cas.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery.flipCounter.1.2.pack.js" />"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery.placeholder.js"/>"></script>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700,800" type="text/css">
        <decorator:head/>
    </head>
    <body>
            <decorator:body/>
            <script type="text/javascript">(function() {var walkme = document.createElement('script'); walkme.type = 'text/javascript'; walkme.async = true; walkme.src = 'https://d3b3ehuo35wzeh.cloudfront.net/users/6543/walkme_6543_https.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(walkme, s);})();</script>
    </body>
</html>
