<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="view.mashery.access.token.label"/></title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div>
    <form class="form-horizontal">
        <div class="control-group">
            <label class="control-label" for="token"><spring:message code="mashery.access_token.label"/></label>
            <div class="controls">
                <span id="token" class="uneditable-input">${masheryAccessToken.token}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="client_id"><spring:message code="mashery.client_id.label"/></label>
            <div class="controls">
                <span id="client_id" class="uneditable-input">${masheryAccessToken.client_id}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="token_type"><spring:message code="mashery.token_type.label"/></label>
            <div class="controls">
                <span id="token_type" class="uneditable-input">${masheryAccessToken.token_type}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="grant_type"><spring:message code="mashery.grant_type.label"/></label>
            <div class="controls">
                <span id="grant_type" class="uneditable-input">${masheryAccessToken.grant_type}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="expires"><spring:message code="mashery.expires.label"/></label>
            <div class="controls">
                <span id="expires" class="uneditable-input">${masheryAccessToken.expires}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="scope"><spring:message code="mashery.scope.label"/></label>
            <div class="controls">
                <span id="scope" class="input-xlarge uneditable-input">${masheryAccessToken.scope}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="user_context"><spring:message code="mashery.user_context.label"/></label>
            <div class="controls">
                <span id="user_context" class="input-xlarge uneditable-input">${masheryAccessToken.user_context}</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="extended"><spring:message code="mashery.extended.label"/></label>
            <div class="controls">
                <span id="extended" class="input-xlarge uneditable-input">${masheryAccessToken.extended}</span>
            </div>
        </div>
    </form>
</div>
</body>
</html>