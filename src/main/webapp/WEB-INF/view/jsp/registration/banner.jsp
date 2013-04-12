<!DOCTYPE html>

<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%-- This freestanding banner can be loaded in an iframe on the Infusionsoft app login page --%>

<html>
    <head>
        <meta name="decorator" content="plain"/>
        <title>Introducing Your Infusionsoft ID!</title>
    </head>
    <body>
        <c:set var="hittingCasDirectly" value="${false}"/>
        <%@include file="_banner.jsp"%>
    </body>
</html>


