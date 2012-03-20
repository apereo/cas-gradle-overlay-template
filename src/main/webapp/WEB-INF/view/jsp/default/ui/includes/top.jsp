<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:theme code="mobile.custom.css.file" var="mobileCss" text="" />

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
	<head>
	    <title>Welcome to Infusionsoft SSO</title>
        <c:choose>
           <c:when test="${not empty requestScope['isMobile'] and not empty mobileCss}">
                <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
                <meta name="apple-mobile-web-app-capable" content="yes" />
                <meta name="apple-mobile-web-app-status-bar-style" content="black" />
                <link type="text/css" rel="stylesheet" media="screen" href="<c:url value="/css/fss-framework-1.1.2.css" />" />
                <link type="text/css" rel="stylesheet" href="<c:url value="/css/fss-mobile-${requestScope['browserType']}-layout.css" />" />
                <link type="text/css" rel="stylesheet" href="${mobileCss}" />
           </c:when>
           <c:otherwise>
                <spring:theme code="standard.custom.css.file" var="customCssFile" />
                <link type="text/css" rel="stylesheet" href="<c:url value="${customCssFile}" />" />
           </c:otherwise>
        </c:choose>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	    <link rel="icon" href="<c:url value="/favicon.ico" />" type="image/x-icon" />
        <link type="text/css" rel="stylesheet" href="<c:url value="/css/custom-theme/jquery-ui-1.8.18.custom.css"/>"/>
        <script type="text/javascript" src="<c:url value="/js/jquery-1.7.1.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/jquery-ui-1.8.18.custom.min.js"/>"></script>
    </head>
	<body id="cas" class="fl-theme-iphone">
    <div class="flc-screenNavigator-view-container">
        <div class="fl-screenNavigator-view">
            <div id="header" class="flc-screenNavigator-navbar fl-navbar fl-table">
                <h1>Welcome to Infusionsoft SSO</h1>
            </div>		
            <div id="content" class="fl-screenNavigator-scroll-container">
