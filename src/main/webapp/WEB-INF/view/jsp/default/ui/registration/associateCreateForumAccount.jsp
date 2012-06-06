<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<form id="associateForm" action="associateForum" method="post">
    <input name="user" type="hidden" value="${user.getId()}"/>

    <p>
        Enter your desired community username and email you would like associated with the it to create a community account and link it to your Infusionsoft ID.
    </p>

    <p>

        <div class="label">Username</div>
        <input name="forumUsername" value=""/>

        <div class="label">Email</div>
        <input name="forumEmail" value=""/>
    </p>

    <input type="button" value="Associate" onclick="createForum($('#associateForm').serialize())"/>
</form>