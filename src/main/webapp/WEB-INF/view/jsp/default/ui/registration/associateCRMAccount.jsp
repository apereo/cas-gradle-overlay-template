<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<form id="associateForm" action="associate" method="post">
    <input name="user" type="hidden" value="${user.getId()}"/>

    <p>
        Enter your application URL, username, and password to link it to your Single Sign-on.
    </p>

    <p>
        <div class="label">Infusionsoft application</div>
        <input name="appName" value="${fn:escapeXml(appName)}"/>.infusionsoft.com

        <div class="label">Username</div>
        <input name="appUsername" value="${fn:escapeXml(appUsername)}"/>

        <div class="label">Password</div>
        <input name="appPassword" type="password" value=""/>
    </p>

    <input type="button" value="Associate" onclick="associate($('#associateForm').serialize())"/>
</form>